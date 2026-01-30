# Global News Management & Service Platform (NewsPulse)

<p align="center">
  🌐 English | <a href="./README_CN.md">CN中文</a>
</p>

This project is an **enterprise-grade news ecosystem** with advanced technical architecture covering all terminal scenarios. It seamlessly integrates **Spring Cloud microservices** and a **native Java crawler engine**, while providing three independent interactive terminals: **PC Web + Mobile H5 + Admin Console**, achieving a complete closed-loop from content production to full-domain distribution.

---

## 1. Three Core Terminals Overview

### 🌍 1.1 PC Web Client (newsweb)
A standard portal for traditional internet users, built with the classic **HTML + jQuery + CSS3** architecture.
- **🏛️ Portal-Level Interaction**:
  - Real-time "News Scrolling Announcement" bar with automatic scrolling display of latest system announcements.
  - Classic three-column layout (Domestic/International/Entertainment sections), meeting users' deep reading habits.
  - Paginated news list browsing with category filtering support.
- **🔐 Complete User System**:
  - **Captcha Login/Registration**: Secure and convenient phone number authentication process.
  - **Immersive Reading**: News detail page displaying complete content with author, source, and publication time metadata.
- **💼 Personal Asset Management**:
  - **My Collections**: Independent collection list management page with keyword search support.
  - **Browsing History**: Complete historical browsing record tracking feature.
- **💬 Social Interaction**:
  - **Like Feature**: One-click like for favorite news articles.
  - **Comment System**: Users can comment on news, with anonymous commenting support.

### 📱 1.2 Mobile App (newsmobile)
A **responsive H5 application** for mobile internet users, built with **Vue 2 + Vant UI**.
- **✨ Modern Interactive Design**:
  - **Waterfall Feed**: Millisecond-response gesture sliding experience with infinite scroll loading.
  - **Bottom Navigation Bar**: Quick switching between four functional modules: Home, Life, Traffic, and Profile.
  - **Homepage Carousel**: Hot news image push with dynamic featured content display.
- **📰 Smart Content Presentation**:
  - **Category Tab Filtering**: Quick news content filtering by topic.
  - **News Detail**: Immersive reading experience with complete news content and metadata.
- **💬 Social Features**:
  - **Nested Comment System**: Multi-level "thread-style" commenting with reply-to-reply support.
  - **Like/Collect**: One-click operations with synchronized status indicators.
  - **Auto Browsing History**: News visits are automatically recorded in browsing history.
- **👤 Personal Center**:
  - **Login/Register Popup**: Smooth form validation and user authentication process.
  - **My Collections**: Collection list display with keyword search and swipe-to-delete.
  - **My Footprints**: Browsing history records for reviewing previously read content.
  - **Statistics Display**: Collection count and browsing count at a glance.

### 📊 1.3 Admin Console (news_vue)
A **SPA single-page management system** built for operations teams, based on **Vue 2 + Element UI**.
- **📈 Visual Data Dashboard**:
  - **Core Statistics Cards**: Real-time display of registered users, news topics, and total news count.
  - **amCharts Pie Chart**: News topic distribution visualization for intuitive content structure presentation.
- **📂 Content Management**:
  - **Topic Management**: Add/delete news category topics.
  - **News Management**: Complete news list with pagination, sorting, and status filtering.
    - News review (approve/revoke)
    - News deletion
    - Batch deletion by date (super admin privilege)
- **💬 Comment Governance**:
  - **Comment List Management**: Paginated display with time-based sorting.
  - **Comment Review**: Approve/revoke/delete comments.
  - **🤖 AI Smart Review**: Integrated LLM for one-click batch analysis of comment content, intelligently identifying violations/normal comments and providing review suggestions (pass/block/manual review needed), with one-click AI suggestion adoption.
- **📢 Announcement System**:
  - **Announcement CRUD**: Complete create/read/update/delete operations.
  - **Display Control**: Set display target (users/admins/all), priority (high/medium/low), and display status toggle.
- **🕷️ News Pulse (Crawler Console)**:
  - **Visual Crawler Task Configuration**: Select collection categories and set per-category limits.
  - **Real-time Log Terminal**: Hacker-style terminal interface with real-time scrolling collection logs.
  - **Async Collection**: Background execution without blocking page operations.
- **👮 Permission Management**:
  - **RBAC Hierarchical System**: Super admin/regular admin permission separation.
  - **Admin Management**: Approve/revoke/delete regular admin accounts.
  - **User Management**: View/delete registered users.

---

## 2. Technology Stack Details

### 🔧 Backend Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Build Tool** | Maven | 3.6.3 |
| **JDK** | JDK | 17 |
| **Core Framework** | Spring Boot | 3.5.9 |
| **ORM** | MyBatis + MyBatis-Plus | 3.5.7 |
| **Database** | MySQL | 9.4.0 |
| **Cache** | Redis | 3.2.1 |
| **Microservices** | Spring Cloud | 2025.0.1 |
| **Service Registry** | Netflix Eureka | 3-node cluster |
| **API Gateway** | Spring Cloud Gateway | |
| **API Documentation** | Swagger | 2.6.0 |
| **Entity Simplification** | Lombok | 1.18.30 |
| **Utilities** | Apache Commons Lang3 | 3.18.0 |
| **Architecture Spec** | COLA Component DTO | 4.3.2 |
| **HTML Parsing** | JSoup | 1.18.1 |

### 🌐 Frontend Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Basics** | HTML5 + CSS3 | - |
| **Core Framework** | Vue.js | 2.6.14 |
| **Routing** | Vue Router | 3.5.1 |
| **HTTP Requests** | Axios | 1.13.2 |
| **Mobile UI** | Vant UI | 2.13.9 |
| **Admin UI** | Element UI | 2.15.14 |
| **Charts** | amCharts 5 | 5.15.1 |
| **Map Visualization** | amCharts Geodata | 5.1.5 |
| **Maps** | Baidu Maps (BMAP) | - |
| **Cross-platform** | UniApp | - |
| **DOM Manipulation** | jQuery | 1.12.4 |
| **Build Tool** | Vue CLI | 5.0.0 |
| **Code Transpiling** | Babel | 7.12.16 |
| **Code Standards** | ESLint | 7.32.0 |

---

## 3. Core Technical Highlights

1. **Full Terminal Coverage**: Whether desktop browsing, mobile fragmented time, or heavy backend management, each has dedicated professional client support.
2. **Native Java Crawler**: Abandoning Python scripts, crawler logic is deeply integrated as `@Service` within Java services, directly reusing Spring context and transaction control.
3. **Microservice Governance**: Complete Eureka + Gateway system ensuring service stability under high concurrency.
4. **Data Visualization**: Professional charting library amCharts integrated in admin backend for at-a-glance operational data.
5. **AI Content Moderation**: Integrated AI LLM for intelligent analysis of user comments, automatically identifying violations and improving operational efficiency.
6. **Sensitive Word Filtering**: Built-in multi-dimensional sensitive word libraries (advertising, weapons/explosives, URLs, profanity, etc.) ensuring content safety.
7. **Nested Comment System**: Multi-level reply comment system enhancing user interaction experience.

---

## 4. Directory Structure

```text
news/
├── newsweb/                # [Core] PC Web Portal (HTML/jQuery)
│   └── newsweb/
│       ├── index.html      # Homepage
│       ├── newsinfo.html   # News Detail Page
│       ├── collection.html # My Collections
│       ├── history.html    # Browsing History
│       └── regist.html     # Registration Page
├── newsmobile/             # [Core] Mobile H5 App (Vue/Vant)
│   └── src/views/
│       ├── HomeView.vue    # Homepage (Feed + Carousel)
│       ├── NewsDetail.vue  # News Detail (with Comments)
│       ├── MyView.vue      # Personal Center
│       ├── LifeView.vue    # Life Channel
│       └── TrafficView.vue # Traffic Channel
├── news_vue/               # [Core] Admin Management System (Vue/Element)
│   └── src/views/
│       ├── HomeView.vue    # Login Page
│       ├── AdminView.vue   # Management Center (all admin features)
│       └── CustomerView.vue# User-side Preview
├── newsmanager202512/      # Java Backend Microservice Cluster
│   ├── newsmanager-eureka/ # Service Registry (7001/7002/7003)
│   ├── newsmanager-gateway/# API Gateway Routing
│   ├── newsmanger-core/    # Core Business Service
│   │   └── controller/
│   │       ├── NewsController.java      # News Management
│   │       ├── CommentController.java   # Comment Management
│   │       ├── CollectionController.java# Collection Management
│   │       ├── LikeController.java      # Like Management
│   │       ├── HistoryController.java   # Browsing History
│   │       ├── NoticeController.java    # Announcement Management
│   │       ├── ThemeController.java     # Topic Management
│   │       ├── AdminController.java     # Admin Management
│   │       ├── UserController.java      # User Management
│   │       ├── AIController.java        # AI Review Interface
│   │       └── TotalController.java     # Statistics Interface
│   └── newsmanager-customer/# User Interaction Service + Crawler Engine
│       └── controller/
│           ├── NewsCrawlerController.java # Crawler Control
│           ├── NoticeController.java      # Notification Push
│           └── ...                        # Other Interaction APIs
├── sensitive-stop-words/   # Sensitive Word Libraries
│   ├── 广告.txt            # Advertising keywords
│   ├── 涉枪涉爆违法信息关键词.txt # Weapons/explosives keywords
│   ├── 网址.txt            # URL patterns
│   ├── 骂人词库.txt        # Profanity dictionary
│   └── stopword.dic
└── news_system.sql         # Database Initialization Script
```

---

## 5. Quick Start

### 5.1 Environment Requirements
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 3.2+
- Node.js 25+

### 5.2 Backend Startup
1. Import `news_system.sql` to initialize the database
2. Start Eureka cluster sequentially (7001/7002/7003)
3. Start Gateway service
4. Start Core and Customer business services

### 5.3 Frontend Startup
```bash
# PC Web - Open newsweb/newsweb/index.html directly in browser

# Mobile App
cd newsmobile
npm install
npm run serve

# Admin Console
cd news_vue
npm install
npm run serve
```

---

## 6. Screenshots

### 6.1 PC Web Homepage

<div align="center">
  <img src="./images/web-home.png" alt="Web Homepage" width="80%"/>
  <p><em>Figure 6-1 Web Homepage - Three-column layout with news categorization</em></p>
</div>

### 6.2 Mobile App

<div align="center">
  <img src="./images/mobile-home.png" alt="Mobile App" width="40%"/>
  <p><em>Figure 6-2 Mobile H5 App - Feed + Bottom Navigation</em></p>
</div>

### 6.3 Admin Console - Super Administrator

<div align="center">
  <img src="./images/admin-super.png" alt="Super Admin Console" width="80%"/>
  <p><em>Figure 6-3 Super Admin View - Full Feature Privileges</em></p>
</div>

### 6.4 Admin Console - Regular Administrator

<div align="center">
  <img src="./images/admin-normal.png" alt="Regular Admin Console" width="80%"/>
  <p><em>Figure 6-4 Regular Admin View - Limited Feature Privileges</em></p>
</div>

---

## 7. License

MIT License © 2024-2026
