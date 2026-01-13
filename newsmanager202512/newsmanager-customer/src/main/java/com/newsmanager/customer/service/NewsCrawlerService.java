package com.newsmanager.customer.service;

import com.newsmanager.api.models.NewsModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@Service
public class NewsCrawlerService {

    @Autowired
    private RestTemplate restTemplate; // 默认负载均衡，用于内部调用

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("externalRestTemplate")
    private RestTemplate externalRestTemplate; // 普通 RestTemplate，用于外部调用

    private final List<String> logs = new CopyOnWriteArrayList<>();
    private boolean isRunning = false;

    // 分类 RSS 地址映射
    private static final Map<Integer, String> RSS_FEEDS = new HashMap<>();
    static {
        RSS_FEEDS.put(1,
                "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSkwyMHZNR1F3TlhjekVnVjZhQzFEVGlnQVAB?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"); // 国内
        RSS_FEEDS.put(2,
                "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx1YlY4U0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"); // 国际
        RSS_FEEDS.put(3,
                "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx6TVdZU0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"); // 商业
        RSS_FEEDS.put(4,
                "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNREpxYW5RU0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"); // 娱乐
        RSS_FEEDS.put(5,
                "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRFp1ZEdvU0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"); // 体育
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public void clearLogs() {
        logs.clear();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void runCrawler(Integer tid, int limit) {
        if (isRunning)
            return;
        isRunning = true;
        logs.clear();
        addLog("🚀 开始新闻采集任务...");

        new Thread(() -> {
            try {
                if (tid != null && tid > 0) {
                    processCategory(tid, limit);
                } else {
                    for (Integer id : RSS_FEEDS.keySet()) {
                        processCategory(id, limit);
                    }
                }
                addLog("✅ 采集任务全部完成！");
            } catch (Exception e) {
                addLog("❌ 任务异常终止: " + e.getMessage());
            } finally {
                isRunning = false;
            }
        }).start();
    }

    private void processCategory(int tid, int limit) {
        String url = RSS_FEEDS.get(tid);
        if (url == null)
            return;

        addLog("→ 正在获取分类 [" + tid + "] 的新闻...");
        try {
            String xml = externalRestTemplate.getForObject(url, String.class);
            Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
            Elements items = doc.select("item");

            int count = 0;
            for (Element item : items) {
                if (count >= limit)
                    break;

                String title = item.select("title").text();
                // 移除来源后缀 " - 某某媒体"
                if (title.contains(" - ")) {
                    title = title.substring(0, title.lastIndexOf(" - "));
                }

                if (checkDuplicate(title)) {
                    addLog("  ⚠ 跳过重复: " + abbreviate(title, 20));
                    continue;
                }

                String pubDate = item.select("pubDate").text();
                String author = item.select("source").text();
                if (author.isEmpty() && item.select("title").text().contains(" - ")) {
                    String fullTitle = item.select("title").text();
                    author = fullTitle.substring(fullTitle.lastIndexOf(" - ") + 3);
                }

                String description = item.select("description").text();
                String content = Jsoup.parse(description).text();
                if (content.isEmpty() || content.length() < 10)
                    content = title;

                NewsModel news = new NewsModel();
                news.setNid(generateNid(title));
                news.setNtitle(title);
                news.setContent(content);
                news.setAuthor(author.isEmpty() ? "未知来源" : author);
                news.setCreatedate(formatDate(pubDate));
                news.setTid(tid);
                news.setCnt(0);
                news.setStatus(1);

                saveNews(news);
                addLog("  ✓ 已入库: " + abbreviate(title, 20));
                count++;
            }
            addLog("✓ 分类 [" + tid + "] 采集完成，新增 " + count + " 条新闻");
        } catch (Exception e) {
            addLog("  ✗ 分类 [" + tid + "] 获取失败: " + e.getMessage());
        }
    }

    private boolean checkDuplicate(String title) {
        try {
            // 这里为了简单，假设 core 端有根据标题查询的接口，如果没有，我们可以通过获取列表后在内存过滤，或者直接尝试插入让数据库主键/唯一索引报错
            // 实际上为了效率，我们直接调用 core 的搜索接口
            String res = restTemplate.getForObject(
                    SERVICE_PATH1 + "/news/getbytitle?title=" + URLEncoder.encode(title, StandardCharsets.UTF_8),
                    String.class);
            return res != null && !res.equals("null") && res.length() > 10;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveNews(NewsModel news) {
        restTemplate.postForObject(SERVICE_PATH1 + "/news/save", news, String.class);
    }

    private String generateNid(String title) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(title.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            sb.append(System.currentTimeMillis() % 10000);
            return sb.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString().substring(0, 8);
        }
    }

    private String formatDate(String rawDate) {
        try {
            // Google News Date format: Tue, 13 Jan 2026 10:45:00 GMT
            SimpleDateFormat input = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            SimpleDateFormat output = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            return output.format(input.parse(rawDate));
        } catch (Exception e) {
            return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date());
        }
    }

    private void addLog(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        logs.add("[" + time + "] " + message);
        if (logs.size() > 500)
            logs.remove(0);
    }

    private String abbreviate(String str, int len) {
        if (str.length() <= len)
            return str;
        return str.substring(0, len) + "...";
    }
}
