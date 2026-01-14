# -*- coding: utf-8 -*-
"""
新闻爬虫脚本 (RSS 版本)
从 Google News 各分类 RSS 抓取新闻数据，并插入到 MySQL 数据库中

Usage:
    python news_crawler.py          # 正常运行，抓取并插入数据库
    python news_crawler.py --test   # 测试模式，只打印不插入数据库
"""

import argparse
import hashlib
import random
import re
import string
import time
from datetime import datetime
from typing import Dict, List

import feedparser
import pymysql

from config import (
    CATEGORY_FEEDS,
    CRAWLER_CONFIG,
    DB_CONFIG,
    AUTHOR_MAPPING,
)


class NewsDatabase:
    """数据库操作类"""
    
    def __init__(self):
        self.connection = None
    
    def connect(self) -> bool:
        try:
            self.connection = pymysql.connect(
                host=DB_CONFIG["host"],
                port=DB_CONFIG["port"],
                user=DB_CONFIG["user"],
                password=DB_CONFIG["password"],
                database=DB_CONFIG["database"],
                charset=DB_CONFIG["charset"],
                cursorclass=pymysql.cursors.DictCursor
            )
            print(f"✓ 数据库连接成功: {DB_CONFIG['database']}")
            return True
        except Exception as e:
            print(f"✗ 数据库连接失败: {e}")
            return False
    
    def close(self):
        if self.connection:
            self.connection.close()
            print("✓ 数据库连接已关闭")
    
    def check_duplicate(self, title: str) -> bool:
        try:
            with self.connection.cursor() as cursor:
                sql = "SELECT nid FROM tb_news WHERE ntitle = %s"
                cursor.execute(sql, (title,))
                return cursor.fetchone() is not None
        except Exception as e:
            print(f"✗ 检查重复时出错: {e}")
            return False
    
    def insert_news(self, news: Dict) -> bool:
        try:
            with self.connection.cursor() as cursor:
                sql = """
                    INSERT INTO tb_news (nid, ntitle, content, createdate, author, cnt, status, tid)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """
                cursor.execute(sql, (
                    news["nid"],
                    news["ntitle"],
                    news["content"],
                    news["createdate"],
                    news["author"],
                    news.get("cnt", 0),
                    news.get("status", 1),
                    news["tid"]
                ))
            self.connection.commit()
            return True
        except Exception as e:
            print(f"✗ 插入新闻失败: {e}")
            self.connection.rollback()
            return False


class NewsCrawler:
    """新闻爬虫类"""
    
    def generate_nid(self, title: str) -> str:
        """生成唯一的新闻ID"""
        hash_part = hashlib.md5(title.encode()).hexdigest()[:8]
        random_part = ''.join(random.choices(string.ascii_lowercase + string.digits, k=4))
        return f"{hash_part}{random_part}"
    
    def _parse_date(self, published: str) -> str:
        """解析发布时间"""
        if published:
            try:
                parsed_time = time.strptime(published[:25], "%a, %d %b %Y %H:%M:%S")
                return time.strftime("%Y-%m-%d %H:%M:%S", parsed_time)
            except:
                pass
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    def _clean_text(self, text: str) -> str:
        """清理文本"""
        if not text:
            return ""
        # 移除 HTML 标签
        text = re.sub(r"<[^>]+>", "", text)
        # 替换 HTML 实体
        text = text.replace("&nbsp;", " ")
        text = text.replace("&amp;", "&")
        text = text.replace("&lt;", "<")
        text = text.replace("&gt;", ">")
        text = text.replace("&quot;", '"')
        text = text.replace("&#39;", "'")
        # 移除多余空白
        text = re.sub(r"\s+", " ", text)
        return text.strip()
    
    def _extract_content(self, description: str, title: str) -> str:
        """
        从 RSS description 中提取内容
        排除与标题重复的部分，提取相关新闻摘要
        """
        if not description:
            return title
        
        text = self._clean_text(description)
        
        # 移除无用的文本
        text = re.sub(r"在Google\s*新闻上查看完整报道", "", text)
        text = re.sub(r"查看完整报道", "", text)
        
        # 如果内容太短或和标题一样，返回标题
        if len(text) < 20 or text == title:
            return title
        
        return text
    
    def fetch_category(self, tid: int, category_name: str, feed_url: str) -> List[Dict]:
        """从指定分类的 RSS 源获取新闻"""
        news_list = []
        print(f"\n→ 正在获取【{category_name}】分类新闻...")
        
        try:
            feed = feedparser.parse(feed_url)
            
            if not feed.entries:
                print(f"  ⚠ 未获取到任何新闻条目")
                return news_list
            
            max_articles = CRAWLER_CONFIG["max_articles_per_category"]
            print(f"  ✓ 获取到 {len(feed.entries)} 条，取前 {max_articles} 条")
            
            for entry in feed.entries[:max_articles]:
                try:
                    # 获取标题 (移除来源后缀)
                    raw_title = entry.get("title", "")
                    title_parts = raw_title.rsplit(" - ", 1)
                    title = self._clean_text(title_parts[0])
                    
                    if not title:
                        continue
                    
                    # 获取发布时间
                    published = entry.get("published", "")
                    createdate = self._parse_date(published)
                    
                    # 获取来源/作者
                    author = entry.get("source", {}).get("title", "")
                    if not author and len(title_parts) > 1:
                        author = title_parts[1]
                    
                    author = self._clean_text(author)
                    # 应用作者名称映射
                    if author.lower() in AUTHOR_MAPPING:
                        author = AUTHOR_MAPPING[author.lower()]
                    
                    if not author:
                        author = "未知来源"
                    
                    # 获取内容
                    description = entry.get("summary", "") or entry.get("description", "")
                    content = self._extract_content(description, title)
                    
                    news = {
                        "nid": self.generate_nid(title),
                        "ntitle": title[:255],
                        "content": content,
                        "createdate": createdate,
                        "author": self._clean_text(author)[:255],
                        "cnt": 0,
                        "status": 1,
                        "tid": tid
                    }
                    news_list.append(news)
                    
                except Exception as e:
                    print(f"  ⚠ 处理条目时出错: {e}")
                    continue
                    
        except Exception as e:
            print(f"  ✗ 获取 RSS 失败: {e}")
        
        return news_list
    
    def crawl_all(self) -> List[Dict]:
        """从所有分类源抓取新闻"""
        all_news = []
        seen_titles = set()
        
        for tid, info in CATEGORY_FEEDS.items():
            news_list = self.fetch_category(tid, info["name"], info["rss"])
            for news in news_list:
                if news["ntitle"] not in seen_titles:
                    all_news.append(news)
                    seen_titles.add(news["ntitle"])
        
        return all_news


def print_news(news_list: List[Dict]):
    """打印新闻列表"""
    category_names = {tid: info["name"] for tid, info in CATEGORY_FEEDS.items()}
    
    print("\n" + "=" * 60)
    print(f"共获取到 {len(news_list)} 条新闻")
    print("=" * 60)
    
    for i, news in enumerate(news_list, 1):
        category = category_names.get(news["tid"], "其他")
        content_preview = news['content'][:60] + "..." if len(news['content']) > 60 else news['content']
        print(f"\n[{i}] 【{category}】{news['ntitle']}")
        print(f"    作者: {news['author']} | 时间: {news['createdate']}")
        print(f"    内容: {content_preview}")


def main():
    parser = argparse.ArgumentParser(description="新闻爬虫脚本")
    parser.add_argument("--test", action="store_true", help="测试模式，只打印不插入数据库")
    args = parser.parse_args()
    
    print("=" * 60)
    print("新闻爬虫脚本启动")
    print(f"时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"模式: {'测试模式' if args.test else '正常模式'}")
    print(f"分类: {', '.join(info['name'] for info in CATEGORY_FEEDS.values())}")
    print("=" * 60)
    
    crawler = NewsCrawler()
    
    print("\n[1] 开始抓取新闻...")
    news_list = crawler.crawl_all()
    
    if not news_list:
        print("\n✗ 未能获取到任何新闻，请检查网络连接")
        return
    
    print_news(news_list)
    
    if args.test:
        print("\n[测试模式] 已跳过数据库插入")
        return
    
    print("\n[2] 开始插入数据库...")
    db = NewsDatabase()
    
    if not db.connect():
        print("✗ 无法连接数据库，退出")
        return
    
    try:
        inserted = 0
        skipped = 0
        
        for news in news_list:
            if db.check_duplicate(news["ntitle"]):
                print(f"  ⚠ 跳过重复: {news['ntitle'][:30]}...")
                skipped += 1
            elif db.insert_news(news):
                print(f"  ✓ 已插入: {news['ntitle'][:30]}...")
                inserted += 1
            else:
                print(f"  ✗ 插入失败: {news['ntitle'][:30]}...")
        
        print("\n" + "=" * 60)
        print(f"完成! 插入: {inserted} 条, 跳过: {skipped} 条")
        print("=" * 60)
        
    finally:
        db.close()


if __name__ == "__main__":
    main()
