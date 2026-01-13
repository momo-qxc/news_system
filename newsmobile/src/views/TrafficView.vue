<template>
  <div class="traffic-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <van-nav-bar title="地铁线路图" class="nav-header">
        <template #right>
          <div class="city-selector" @click="showCityPicker = true">
            <span>{{ currentCity }}</span>
            <van-icon name="arrow-down" size="12" />
          </div>
        </template>
      </van-nav-bar>
    </div>

    <!-- 地铁图容器 -->
    <div id="subway-container" class="subway-container"></div>

    <!-- 城市选择器 -->
    <van-popup v-model="showCityPicker" position="bottom" round>
      <van-picker
        show-toolbar
        title="选择城市"
        :columns="subwayCities"
        value-key="name"
        @confirm="onCityConfirm"
        @cancel="showCityPicker = false"
      />
    </van-popup>

    <!-- 底部导航 -->
    <van-tabbar v-model="active" active-color="#e60012">
      <van-tabbar-item icon="wap-home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="location-o" to="/life">生活</van-tabbar-item>
      <van-tabbar-item icon="guide-o" to="/traffic">交通</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/my">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
/* global BMapSub */

export default {
  name: 'TrafficView',
  data() {
    return {
      active: 2,
      currentCity: '厦门',
      currentCityCode: '',
      showCityPicker: false,
      subway: null,
      subwayCities: []
    }
  },
  mounted() {
    this.initSubwayCities()
    this.$nextTick(() => {
      this.initSubway()
    })
  },
  methods: {
    // 初始化支持地铁的城市列表
    initSubwayCities() {
      if (typeof BMapSub !== 'undefined' && BMapSub.SubwayCitiesList) {
        this.subwayCities = BMapSub.SubwayCitiesList
        // 找到默认城市
        const defaultCity = this.subwayCities.find(c => c.name === this.currentCity)
        if (defaultCity) {
          this.currentCityCode = defaultCity.citycode
        }
      }
    },

    // 初始化地铁图
    initSubway() {
      if (!this.currentCityCode) {
        const city = this.subwayCities.find(c => c.name === this.currentCity)
        if (city) {
          this.currentCityCode = city.citycode
        } else if (this.subwayCities.length > 0) {
          this.currentCity = this.subwayCities[0].name
          this.currentCityCode = this.subwayCities[0].citycode
        }
      }

      if (this.currentCityCode) {
        this.subway = new BMapSub.Subway('subway-container', this.currentCityCode)
        this.subway.setZoom(0.4)
      }
    },

    // 选择城市
    onCityConfirm(value) {
      this.currentCity = value.name
      this.currentCityCode = value.citycode
      this.showCityPicker = false
      
      // 重新初始化地铁图
      this.$nextTick(() => {
        this.initSubway()
      })
    }
  }
}
</script>

<style scoped>
.traffic-container {
  min-height: 100vh;
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  padding-bottom: 50px;
}

/* 导航栏 */
.nav-header {
  background: linear-gradient(135deg, #e60012 0%, #ff4d4f 100%) !important;
}
.nav-header ::v-deep .van-nav-bar__title {
  color: #fff !important;
  font-weight: bold;
}

/* 城市选择器 */
.city-selector {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 14px;
  color: #fff;
  white-space: nowrap;
}

.city-selector:active {
  background: rgba(255, 255, 255, 0.3);
}

/* 地铁图容器 */
.subway-container {
  flex: 1;
  min-height: calc(100vh - 100px);
  background: #fff;
}
</style>