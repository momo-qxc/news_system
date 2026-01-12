<template>
  <div class="home-container">
    <!-- 顶部红色导航栏 -->
    <van-nav-bar title="新闻聚合" class="nav-header" />
    
    <!-- 主题栏 (还原老师风格) -->
    <div class="theme-bar">
      <b v-for="item in themelist" :key="item.tid" class="theme-item">{{item.tname}}</b>
    </div>

    <!-- 轮播图功能 -->
    <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
      <van-swipe-item v-for="(img, index) in images" :key="index">
        <img :src="img" class="swipe-img" />
      </van-swipe-item>
    </van-swipe>

    <!-- 主体内容区域 -->
    <div class="content">
      <van-empty v-if="themelist.length === 0" description="暂无分类数据" />
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
        require('@/assets/picture/“绝对决心行动”的执行影片.jpg'),
        require('@/assets/picture/李在明访华.png'),
        require('@/assets/picture/沙特轰炸也门.jpg'),
        require('@/assets/picture/阿凡达3.jpg')
      ]
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
          // 处理后端可能返回的 JSON 字符串
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
}
/* 轮播图样式 */
.my-swipe {
  height: 200px;
}
.swipe-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.home-container {
  background-color: #f7f8fa;
  min-height: 100vh;
}
.content {
  padding-bottom: 50px; /* 为底部 tabbar 留位 */
}
</style>