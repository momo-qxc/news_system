# -*- coding: utf-8 -*-
"""
新闻爬虫配置文件
"""

# 数据库配置
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "database": "news_push",
    "user": "root",
    "password": "root",
    "charset": "utf8mb4"
}

# 新闻分类 RSS 源
CATEGORY_FEEDS = {
    1: {
        "name": "国内",
        "rss": "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSkwyMHZNR1F3TlhjekVnVjZhQzFEVGlnQVAB?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"
    },
    2: {
        "name": "国际",
        "rss": "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx1YlY4U0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"
    },
    3: {
        "name": "商业",
        "rss": "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRGx6TVdZU0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"
    },
    4: {
        "name": "娱乐",
        "rss": "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNREpxYW5RU0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"
    },
    5: {
        "name": "体育",
        "rss": "https://news.google.com/rss/topics/CAAqKggKIiRDQkFTRlFvSUwyMHZNRFp1ZEdvU0JYcG9MVU5PR2dKRFRpZ0FQAQ?hl=zh-CN&gl=CN&ceid=CN:zh-Hans"
    }
}

# 爬虫配置
CRAWLER_CONFIG = {
    "max_articles_per_category": 10,  # 每个分类抓取数量
}

# 作者/媒体名称映射 (处理网址形式的作者名)
AUTHOR_MAPPING = {
    "chinanews.com.cn": "中国新闻网",
    "cma.gov.cn": "中国气象局",
    "court.gov.cn": "最高人民法院",
    "henan100.com": "河南一百度",
    "rmzxw.com.cn": "人民政协网",
    "sport.gov.cn": "国家体育总局",
    "thepaper.cn": "澎湃新闻",
    "stcn.com": "证券时报网",
    "wenxuecity": "文学城",
    "hengyang.gov.cn": "衡阳政务门户网",
    "nanjing.gov.cn": "南京政务门户网",
    "nfnews.com": "南方新闻网",
    "court.gov.cn": "最高人民法院"
}
