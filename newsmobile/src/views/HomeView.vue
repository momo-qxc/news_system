<template>
  <div class="home-container">
    <!-- 顶部红色导航栏 -->
    <van-nav-bar title="新闻聚合" class="nav-header" />
    
    <!-- 搜索栏 -->
    <van-search
      v-model="searchKeyword"
      placeholder="请输入新闻关键词"
      @search="onSearch"
      @clear="onClearSearch"
      background="#fff"
    />

    <!-- 系统公告 -->
    <van-notice-bar
      v-if="notices.length > 0"
      left-icon="volume-o"
      :text="noticeText"
      mode="closeable"
      scrollable
      class="home-notice-bar"
    />

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
    <div class="swipe-container" v-show="!isSearchMode">
      <van-swipe ref="swipeRef" class="my-swipe" :autoplay="5000" indicator-color="white">
        <van-swipe-item v-for="(slide, index) in slides" :key="index">
          <div class="slide-wrapper">
            <img :src="slide.image" class="swipe-img" />
            <div class="slide-caption">
              <span>{{ slide.title }}</span>
            </div>
          </div>
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

    <!-- 新闻列表区域（支持下拉刷新） -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
      <div class="content">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          loading-text="加载中..."
          @load="onLoad"
          :immediate-check="false"
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
        <van-empty v-if="!loading && !refreshing && newsList.length === 0" description="暂无新闻数据" />
      </div>
    </van-pull-refresh>

    <!-- 底部 Tabbar -->
    <van-tabbar v-model="active" active-color="#e60012" route>
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="location-o" to="/life">生活</van-tabbar-item>
      <van-tabbar-item icon="guide-o" to="/traffic">交通</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/my">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import axios from 'axios'
import API_BASE_URL from '@/config/api'

export default {
  name: 'HomeView',
  data() {
    return {
      active: 0,
      themelist: [],
      // 轮播图数据（图片+标题）
      slides: [
        { image: require('@/assets/picture/trump.jpg'), title: '特朗普：委内瑞拉将向美国移交数千万桶石油' },
        { image: require('@/assets/picture/lzm.png'), title: '韩国总统李在明访华' },
        { image: require('@/assets/picture/war.jpg'), title: '沙特主导联军：也门南方过渡委员会领导人逃往不明地点' },
        { image: require('@/assets/picture/afd.jpg'), title: '《阿凡达：火与烬》加冕三连冠、全球票房破十亿' }
      ],
      // 新闻列表相关
      newsList: [],
      loading: false,
      finished: false,
      refreshing: false,  // 下拉刷新状态
      pageno: 1,
      pagesize: 10,
      currentTid: -1,  // -1 表示全部
      
      // 搜索相关
      searchKeyword: '',
      isSearchMode: false,
      
      // 公告相关
      notices: []
    }
  },
  computed: {
    noticeText() {
      return this.notices.map(n => n.content).join(' | ');
    }
  },
  mounted() {
    this.init();
    this.fetchNotices();
    // 初始加载新闻列表
    this.onLoad();
  },
  methods: {
    init() {
      // 获取主题列表
      axios.get(`${API_BASE_URL}/news/theme/get`)
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
          // 在列表开头添加"全部"选项
          this.themelist = [{ tid: -1, tname: '全部' }, ...data];
        })
        .catch(error => {
          console.error("Fetch themes error:", error);
        });
    },

    // 搜索触发
    onSearch(val) {
        if(!val) return;
        this.isSearchMode = true;
        this.searchKeyword = val;
        this.newsList = [];
        this.finished = false;
        this.loading = true;
        this.pageno = 1;

        // API 需要 pageno 和 pagesize 参数
        let url = `${API_BASE_URL}/news/newsinfo/getnewsbykeyword?pageno=1&pagesize=50&keyword=${encodeURIComponent(val)}`;
        axios.get(url).then(res => {
            let data = res.data;
            if (typeof data === "string") data = JSON.parse(data);
            
            // API 返回 { list: [...], totalpage: ... } 格式
            if(data && data.list && data.list.length > 0) {
                this.newsList = data.list;
            } else {
                this.$toast('未找到相关新闻');
            }
            this.loading = false;
            this.finished = true; // 搜索接口一次性加载完
        }).catch(err => {
            console.error("Search error:", err);
            this.$toast('搜索失败，请重试');
            this.loading = false;
            this.finished = true;
        });
    },

    // 清除搜索
    onClearSearch() {
        this.isSearchMode = false;
        this.searchKeyword = '';
        this.selectTheme(-1); // 恢复默认列表
    },

    // 加载新闻列表（无限滚动触发）
    onLoad() {
      if(this.isSearchMode) return; // 搜索模式下不触发滚动加载

      let url = '';
      if (this.currentTid === -1) {
        url = `${API_BASE_URL}/news/newsinfo/get?pageno=${this.pageno}&pagesize=${this.pagesize}`;
      } else {
        url = `${API_BASE_URL}/news/newsinfo/getbytid?pageno=${this.pageno}&pagesize=${this.pagesize}&tid=${this.currentTid}`;
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
      if (this.currentTid === tid && !this.isSearchMode) return;
      this.currentTid = tid;
      // 重置列表
      this.newsList = [];
      this.pageno = 1;
      this.finished = false;
      this.loading = true;
      
      // 如果有搜索关键词，则在该分类下搜索
      if (this.searchKeyword && this.searchKeyword.trim()) {
        this.searchInCategory(tid, this.searchKeyword.trim());
      } else {
        this.isSearchMode = false;
        this.onLoad();
      }
    },
    
    // 在指定分类下搜索
    searchInCategory(tid, keyword) {
      this.isSearchMode = true;
      let url = '';
      if (tid === -1) {
        // 全部分类
        url = `${API_BASE_URL}/news/newsinfo/getnewsbykeyword?pageno=1&pagesize=50&keyword=${encodeURIComponent(keyword)}`;
      } else {
        // 特定分类
        url = `${API_BASE_URL}/news/newsinfo/getnewsbytidandkeyword?pageno=1&pagesize=50&tid=${tid}&keyword=${encodeURIComponent(keyword)}`;
      }
      
      axios.get(url).then(res => {
        let data = res.data;
        if (typeof data === "string") data = JSON.parse(data);
        
        if(data && data.list && data.list.length > 0) {
          this.newsList = data.list;
        } else {
          this.newsList = [];
        }
        this.loading = false;
        this.finished = true;
      }).catch(err => {
        console.error("Search in category error:", err);
        this.loading = false;
        this.finished = true;
      });
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
    },
    // 获取通知公告
    fetchNotices() {
      axios.get(`${API_BASE_URL}/news/notice/getbytarget?target=0`)
        .then(res => {
          let data = res.data;
          if (typeof data === "string") {
            try {
              data = JSON.parse(data);
            } catch (e) {
              console.error("Notice data parse error:", e);
              data = [];
            }
          }
          this.notices = data || [];
        })
        .catch(err => {
          console.error("Fetch notices error:", err);
        });
    },
    // 下拉刷新
    onRefresh() {
      // 重置状态
      this.newsList = [];
      this.pageno = 1;
      this.finished = false;
      this.loading = true;
      
      // 重新加载数据
      let url = '';
      if (this.currentTid === -1) {
        url = `${API_BASE_URL}/news/newsinfo/get?pageno=${this.pageno}&pagesize=${this.pagesize}`;
      } else {
        url = `${API_BASE_URL}/news/newsinfo/getbytid?pageno=${this.pageno}&pagesize=${this.pagesize}&tid=${this.currentTid}`;
      }
      
      axios.get(url)
        .then(response => {
          let data = response.data;
          if (typeof data === "string") {
            try {
              data = JSON.parse(data);
            } catch (e) {
              data = { list: [], totalpage: 1 };
            }
          }
          
          if (data.list && data.list.length > 0) {
            this.newsList = data.list;
            this.pageno++;
          }
          
          if (this.pageno > data.totalpage || !data.list || data.list.length === 0) {
            this.finished = true;
          }
          
          this.loading = false;
          this.refreshing = false;
        })
        .catch(error => {
          console.error("Refresh error:", error);
          this.loading = false;
          this.refreshing = false;
          this.finished = true;
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
.home-notice-bar {
  margin-bottom: 5px;
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
/* 轮播图包装器 */
.slide-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}
/* 轮播图文字标题（渐变背景） */
.slide-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30px 15px 15px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: #fff;
  font-size: 14px;
  line-height: 1.4;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
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