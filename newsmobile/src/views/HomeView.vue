<template>
  <div class="home-container">
    <!-- 顶部红色导航栏 -->
    <van-nav-bar title="新闻聚合" class="nav-header" />
    
    <!-- 主题栏 -->
    <div class="theme-bar">
      <b 
        v-for="item in themelist" 
        :key="item.tid" 
        class="theme-item"
        :class="{ active: currentTid === item.tid }"
        @click="selectTheme(item.tid)"
      >{{item.tname}}</b>
    </div>

    <!-- 轮播图功能 -->
    <div class="swipe-container">
      <van-swipe ref="swipeRef" class="my-swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item v-for="(img, index) in images" :key="index">
          <img :src="img" class="swipe-img" />
        </van-swipe-item>
      </van-swipe>
      <!-- 左侧导航按钮 -->
      <div class="swipe-nav swipe-nav-left" @click="prevSlide">
        <van-icon name="arrow-left" />
      </div>
      <!-- 右侧导航按钮 -->
      <div class="swipe-nav swipe-nav-right" @click="nextSlide">
        <van-icon name="arrow" />
      </div>
    </div>

    <!-- 新闻列表区域 -->
    <div class="content">
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-cell 
          v-for="item in newsList" 
          :key="item.nid"
          :title="item.ntitle"
          :label="item.createdate"
          is-link
          @click="goToDetail(item.nid)"
        />
      </van-list>
      <van-empty v-if="!loading && newsList.length === 0" description="暂无新闻数据" />
    </div>

    <!-- 底部 Tabbar -->
    <van-tabbar v-model="active" active-color="#e60012" route>
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/life">生活</van-tabbar-item>
      <van-tabbar-item icon="friends-o" to="/traffic">交通</van-tabbar-item>
      <van-tabbar-item icon="setting-o" to="/my">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'HomeView',
  data() {
    return {
      active: 0,
      themelist: [],
      images: [
        require('@/assets/picture/trump.jpg'),
        require('@/assets/picture/lzm.png'),
        require('@/assets/picture/war.jpg'),
        require('@/assets/picture/afd.jpg')
      ],
      // 新闻列表相关
      newsList: [],
      loading: false,
      finished: false,
      pageno: 1,
      pagesize: 10,
      currentTid: -1  // -1 表示全部
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      // 获取主题列表
      axios.get('http://localhost:6060/news/theme/get')
        .then(response => {
          let data = response.data;
          if (typeof data === "string") {
            try {
              data = JSON.parse(data);
            } catch (e) {
              console.error("Theme data parse error:", e);
              data = [];
            }
          }
          this.themelist = data;
        })
        .catch(error => {
          console.error("Fetch themes error:", error);
        });
    },
    // 加载新闻列表（无限滚动触发）
    onLoad() {
      let url = '';
      if (this.currentTid === -1) {
        url = `http://localhost:6060/news/newsinfo/get?pageno=${this.pageno}&pagesize=${this.pagesize}`;
      } else {
        url = `http://localhost:6060/news/newsinfo/getbytid?pageno=${this.pageno}&pagesize=${this.pagesize}&tid=${this.currentTid}`;
      }
      
      axios.get(url)
        .then(response => {
          let data = response.data;
          if (typeof data === "string") {
            try {
              data = JSON.parse(data);
            } catch (e) {
              console.error("News data parse error:", e);
              data = { list: [], totalpage: 1 };
            }
          }
          
          if (data.list && data.list.length > 0) {
            this.newsList = this.newsList.concat(data.list);
            this.pageno++;
          }
          
          // 判断是否加载完毕
          if (this.pageno > data.totalpage || !data.list || data.list.length === 0) {
            this.finished = true;
          }
          
          this.loading = false;
        })
        .catch(error => {
          console.error("Fetch news error:", error);
          this.loading = false;
          this.finished = true;
        });
    },
    // 选择主题分类
    selectTheme(tid) {
      if (this.currentTid === tid) return;
      this.currentTid = tid;
      // 重置列表
      this.newsList = [];
      this.pageno = 1;
      this.finished = false;
      this.loading = true;
      this.onLoad();
    },
    // 跳转到新闻详情
    goToDetail(nid) {
      this.$router.push({ name: 'newsDetail', params: { nid } });
    },
    // 切换到上一张图片
    prevSlide() {
      this.$refs.swipeRef.prev();
    },
    // 切换到下一张图片
    nextSlide() {
      this.$refs.swipeRef.next();
    }
  }
}
</script>

<style scoped>
/* 顶部导航红色背景 */
.nav-header {
  background-color: #e60012 !important;
}
/* 标题白色文字 */
.nav-header ::v-deep .van-nav-bar__title {
  color: #ffffff !important;
  font-weight: bold;
}
/* 主题栏样式 */
.theme-bar {
  line-height: 35px;
  height: 35px;
  padding: 0 10px;
  background: #fff;
  white-space: nowrap;
  overflow-x: auto;
}
.theme-item {
  font-size: 14px;
  margin-right: 12px;
  color: #323233;
  cursor: pointer;
  padding: 0 8px;
  border-radius: 4px;
  transition: all 0.2s;
}
.theme-item:hover {
  color: #e60012;
}
.theme-item.active {
  color: #fff;
  background-color: #e60012;
}
/* 轮播图样式 */
.swipe-container {
  position: relative;
}
.my-swipe {
  height: 200px;
}
.swipe-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
/* 轮播图导航按钮 */
.swipe-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}
.swipe-nav:hover {
  background: rgba(0, 0, 0, 0.5);
}
.swipe-nav-left {
  left: 0;
  border-radius: 0 4px 4px 0;
}
.swipe-nav-right {
  right: 0;
  border-radius: 4px 0 0 4px;
}
/* 鼠标悬停在轮播容器时显示导航按钮 */
.swipe-container:hover .swipe-nav {
  opacity: 1;
}
.home-container {
  background-color: #f7f8fa;
  min-height: 100vh;
}
.content {
  padding-bottom: 50px; /* 为底部 tabbar 留位 */
}
</style>