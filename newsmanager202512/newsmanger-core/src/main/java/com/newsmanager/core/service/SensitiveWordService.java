package com.newsmanager.core.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤服务
 * 启动时加载敏感词库，提供检查方法
 */
@Service
public class SensitiveWordService {

    private Set<String> sensitiveWords = new HashSet<>();

    // 敏感词库目录路径
    private static final String SENSITIVE_WORDS_DIR = "E:/news_system/news/sensitive-stop-words";

    @PostConstruct
    public void init() {
        loadSensitiveWords();
    }

    /**
     * 加载所有敏感词文件
     */
    private void loadSensitiveWords() {
        try {
            Path dir = Paths.get(SENSITIVE_WORDS_DIR);
            if (!Files.exists(dir)) {
                System.err.println("敏感词目录不存在: " + SENSITIVE_WORDS_DIR);
                return;
            }

            // 遍历目录下所有txt文件
            Files.list(dir)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(this::loadWordsFromFile);

            System.out.println("敏感词加载完成，共 " + sensitiveWords.size() + " 个词");
        } catch (IOException e) {
            System.err.println("加载敏感词失败: " + e.getMessage());
        }
    }

    /**
     * 从单个文件加载敏感词（尝试多种编码）
     */
    private void loadWordsFromFile(Path filePath) {
        // 尝试多种编码格式
        java.nio.charset.Charset[] charsets = {
                StandardCharsets.UTF_8,
                java.nio.charset.Charset.forName("UTF-16LE"),
                java.nio.charset.Charset.forName("UTF-16"),
                java.nio.charset.Charset.forName("GBK")
        };

        for (java.nio.charset.Charset charset : charsets) {
            try (BufferedReader reader = Files.newBufferedReader(filePath, charset)) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    // 去除BOM、逗号和空格
                    line = line.replace("\uFEFF", "")
                            .replace(",", "")
                            .replace("，", "")
                            .trim();
                    if (!line.isEmpty() && line.length() < 50) { // 过滤异常长的行
                        sensitiveWords.add(line.toLowerCase());
                        count++;
                    }
                }
                if (count > 0) {
                    System.out.println(
                            "成功加载敏感词文件: " + filePath.getFileName() + " (编码: " + charset.name() + ", " + count + " 个词)");
                    return; // 成功读取后退出
                }
            } catch (java.nio.charset.MalformedInputException e) {
                // 编码不匹配，尝试下一个
                continue;
            } catch (IOException e) {
                System.err.println("读取敏感词文件失败: " + filePath + ", " + e.getMessage());
                return;
            }
        }
        System.err.println("无法读取敏感词文件（编码不支持）: " + filePath);
    }

    /**
     * 检查文本是否包含敏感词
     * 
     * @param text 待检查的文本
     * @return 如果包含敏感词返回该敏感词，否则返回null
     */
    public String findSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        String lowerText = text.toLowerCase();
        for (String word : sensitiveWords) {
            if (lowerText.contains(word)) {
                return word;
            }
        }
        return null;
    }

    /**
     * 检查文本是否包含敏感词
     * 
     * @param text 待检查的文本
     * @return 是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        return findSensitiveWord(text) != null;
    }
}
