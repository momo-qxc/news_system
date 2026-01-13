<template>
  <div class="life-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar title="生活地图" class="nav-header">
      <template #right>
        <van-icon name="search" size="18" @click="showSearch = true" />
      </template>
    </van-nav-bar>

    <!-- 地图容器 -->
    <div id="map-container" ref="mapContainer"></div>

    <!-- 定位按钮 -->
    <div class="location-btn" @click="locateMe">
      <van-icon name="aim" />
    </div>

    <!-- 当前位置信息卡片 -->
    <div class="location-card" v-if="currentAddress">
      <div class="card-header">
        <van-icon name="location-o" class="location-icon" />
        <span class="card-title">当前位置</span>
      </div>
      <div class="card-content">{{ currentAddress }}</div>
      <div class="card-actions">
        <van-button size="small" round type="primary" @click="searchNearby('餐饮')">
          <van-icon name="shop-o" /> 美食
        </van-button>
        <van-button size="small" round type="info" @click="searchNearby('酒店')">
          <van-icon name="hotel-o" /> 酒店
        </van-button>
        <van-button size="small" round type="success" @click="searchNearby('景点')">
          <van-icon name="photo-o" /> 景点
        </van-button>
      </div>
    </div>

    <!-- 搜索弹窗 -->
    <van-popup v-model="showSearch" position="top" :style="{ height: '100%' }">
      <div class="search-panel">
        <van-search
          v-model="searchKeyword"
          show-action
          placeholder="搜索地点"
          @search="onSearch"
          @cancel="showSearch = false"
        />
        <div class="search-results" v-if="searchResults.length > 0">
          <van-cell 
            v-for="(item, index) in searchResults" 
            :key="index"
            :title="item.title"
            :label="item.address"
            is-link
            @click="goToLocation(item)"
          />
        </div>
        <van-empty v-else-if="searchKeyword && !searching" description="暂无搜索结果" />
      </div>
    </van-popup>

    <!-- 周边结果弹窗 -->
    <van-action-sheet v-model="showNearby" :title="nearbyTitle">
      <div class="nearby-list">
        <van-cell 
          v-for="(item, index) in nearbyResults" 
          :key="index"
          :title="item.title"
          :label="item.address"
          is-link
          @click="goToLocation(item)"
        >
          <template #right-icon>
            <span class="distance">{{ item.distance }}</span>
          </template>
        </van-cell>
        <van-empty v-if="nearbyResults.length === 0" description="附近暂无相关地点" />
      </div>
    </van-action-sheet>

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
/* global BMapGL */
export default {
  name: 'LifeView',
  data() {
    return {
      active: 1,
      map: null,
      currentAddress: '',
      currentPoint: null,
      showSearch: false,
      searchKeyword: '',
      searchResults: [],
      searching: false,
      showNearby: false,
      nearbyTitle: '',
      nearbyResults: [],
      geolocation: null,
      localSearch: null
    }
  },
  mounted() {
    this.initMap()
  },
  beforeDestroy() {
    // 清理地图实例
    if (this.map) {
      this.map = null
    }
  },
  methods: {
    // 初始化地图
    initMap() {
      // 检查百度地图 API 是否加载
      if (typeof BMapGL === 'undefined') {
        console.error('百度地图 API 未加载')
        this.$toast.fail('地图加载失败')
        return
      }

      // 创建地图实例
      this.map = new BMapGL.Map(this.$refs.mapContainer)
      
      // 设置初始中心点（北京）
      const point = new BMapGL.Point(116.404, 39.915)
      this.map.centerAndZoom(point, 15)
      
      // 开启滚轮缩放
      this.map.enableScrollWheelZoom(true)
      
      // 添加比例尺控件
      const scaleCtrl = new BMapGL.ScaleControl()
      this.map.addControl(scaleCtrl)
      
      // 初始化定位服务
      this.geolocation = new BMapGL.Geolocation()
      
      // 初始化搜索服务
      this.localSearch = new BMapGL.LocalSearch(this.map, {
        renderOptions: { map: this.map }
      })
      
      // 自动定位
      this.locateMe()
    },

    // 定位当前位置
    locateMe() {
      if (!this.geolocation) {
        this.$toast.fail('定位服务未初始化')
        return
      }

      this.$toast.loading({ message: '定位中...', forbidClick: true })

      this.geolocation.getCurrentPosition((result) => {
        this.$toast.clear()
        
        if (this.geolocation.getStatus() === 0) {
          // 定位成功
          this.currentPoint = result.point
          this.currentAddress = result.address.province + result.address.city + result.address.district + result.address.street + result.address.streetNumber
          
          // 移动地图到当前位置
          this.map.centerAndZoom(result.point, 16)
          
          // 添加当前位置标记
          this.map.clearOverlays()
          const marker = new BMapGL.Marker(result.point)
          this.map.addOverlay(marker)
          
          // 添加定位圆圈
          const circle = new BMapGL.Circle(result.point, result.accuracy, {
            strokeColor: '#e60012',
            strokeWeight: 2,
            strokeOpacity: 0.5,
            fillColor: '#e60012',
            fillOpacity: 0.1
          })
          this.map.addOverlay(circle)
          
          this.$toast.success('定位成功')
        } else {
          this.$toast.fail('定位失败，请检查权限')
        }
      }, {
        enableHighAccuracy: true,
        timeout: 10000
      })
    },

    // 搜索地点
    onSearch() {
      if (!this.searchKeyword.trim()) {
        return
      }

      this.searching = true
      this.searchResults = []

      this.localSearch.setSearchCompleteCallback((results) => {
        this.searching = false
        if (results && results.getCurrentNumPois() > 0) {
          const pois = []
          for (let i = 0; i < results.getCurrentNumPois(); i++) {
            const poi = results.getPoi(i)
            pois.push({
              title: poi.title,
              address: poi.address,
              point: poi.point
            })
          }
          this.searchResults = pois
        }
      })

      this.localSearch.search(this.searchKeyword)
    },

    // 搜索周边
    searchNearby(keyword) {
      if (!this.currentPoint) {
        this.$toast('请先定位')
        return
      }

      this.nearbyTitle = `附近的${keyword}`
      this.nearbyResults = []
      this.showNearby = true

      const nearbySearch = new BMapGL.LocalSearch(this.map, {
        renderOptions: { map: this.map },
        onSearchComplete: (results) => {
          if (results && results.getCurrentNumPois() > 0) {
            const pois = []
            for (let i = 0; i < results.getCurrentNumPois(); i++) {
              const poi = results.getPoi(i)
              // 计算距离
              const distance = this.map.getDistance(this.currentPoint, poi.point)
              pois.push({
                title: poi.title,
                address: poi.address,
                point: poi.point,
                distance: distance < 1000 ? `${Math.round(distance)}m` : `${(distance / 1000).toFixed(1)}km`
              })
            }
            this.nearbyResults = pois
          }
        }
      })

      nearbySearch.searchNearby(keyword, this.currentPoint, 2000)
    },

    // 跳转到指定位置
    goToLocation(item) {
      this.showSearch = false
      this.showNearby = false
      
      this.map.clearOverlays()
      this.map.centerAndZoom(item.point, 17)
      
      const marker = new BMapGL.Marker(item.point)
      this.map.addOverlay(marker)
      
      // 添加信息窗口
      const infoWindow = new BMapGL.InfoWindow(item.title, {
        width: 200,
        title: item.title
      })
      marker.addEventListener('click', () => {
        this.map.openInfoWindow(infoWindow, item.point)
      })
      
      // 自动打开信息窗口
      setTimeout(() => {
        this.map.openInfoWindow(infoWindow, item.point)
      }, 300)
    }
  }
}
</script>

<style scoped>
.life-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* 导航栏 */
.nav-header {
  background: linear-gradient(135deg, #e60012 0%, #ff4d4f 100%) !important;
}
.nav-header ::v-deep .van-nav-bar__title {
  color: #fff !important;
  font-weight: bold;
}
.nav-header ::v-deep .van-icon {
  color: #fff !important;
}

/* 地图容器 */
#map-container {
  width: 100%;
  height: calc(100vh - 46px - 50px);
  position: relative;
}

/* 定位按钮 */
.location-btn {
  position: absolute;
  right: 16px;
  bottom: 180px;
  width: 44px;
  height: 44px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
  cursor: pointer;
  transition: all 0.2s;
}
.location-btn:active {
  transform: scale(0.95);
  background: #f5f5f5;
}
.location-btn .van-icon {
  font-size: 22px;
  color: #e60012;
}

/* 当前位置卡片 */
.location-card {
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: 70px;
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  z-index: 100;
}
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.location-icon {
  font-size: 18px;
  color: #e60012;
  margin-right: 6px;
}
.card-title {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
}
.card-content {
  font-size: 13px;
  color: #646566;
  line-height: 1.5;
  margin-bottom: 12px;
}
.card-actions {
  display: flex;
  gap: 10px;
}
.card-actions .van-button {
  flex: 1;
  font-size: 12px;
}

/* 搜索面板 */
.search-panel {
  height: 100%;
  background: #f7f8fa;
}
.search-results {
  background: #fff;
  max-height: calc(100vh - 54px);
  overflow-y: auto;
}

/* 周边列表 */
.nearby-list {
  max-height: 50vh;
  overflow-y: auto;
}
.distance {
  font-size: 12px;
  color: #969799;
}
</style>