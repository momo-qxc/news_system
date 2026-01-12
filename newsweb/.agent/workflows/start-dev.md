---
description: 启动新闻系统开发环境
---

# 新闻系统启动步骤

## 1. 启动后端服务（在 IDEA 中）

按以下顺序启动 Spring Boot 服务：

1. **启动 Eureka 注册中心**（至少一个）
   - 右键 `newsmanagereureka7002` → Run
   - 等待启动完成

2. **启动 newsmanagercore**
   - 右键 `newsmanagercore` → Run

3. **启动 newsmanagergateway**（端口 6060）
   - 右键 `newsmanagergateway` → Run

验证后端：访问 http://127.0.0.1:6060/news/theme/get 应返回 JSON 数据

---

## 2. 启动前端静态服务器

打开 **PowerShell** 或 **CMD**，运行：

```powershell
cd E:\news_system\newsweb\newsweb
python -m http.server 8848
```

// turbo

---

## 3. 访问网站

打开浏览器访问：**http://127.0.0.1:8848/index.html**

---

## 常见问题

| 问题 | 解决方案 |
|------|----------|
| 端口被占用 | 换个端口，如 `python -m http.server 8080` |
| API 无法访问 | 检查 IDEA 中后端服务是否都启动了 |
| 页面空白 | 按 F12 查看控制台错误 |
