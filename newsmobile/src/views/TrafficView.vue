<template>
  <div class="traffic-container">
    <van-nav-bar title="交通" class="nav-header" />
    <van-empty description="交通栏目开发中..." />
    <van-tabbar v-model="active" active-color="#e60012" route>
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/life">生活</van-tabbar-item>
      <van-tabbar-item icon="friends-o" to="/traffic">交通</van-tabbar-item>
      <van-tabbar-item icon="setting-o" to="/my">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
export default {
  name: 'TrafficView',
  data() {
    return {
      active: 2
    }
  }
}
</script>

<style scoped>
.traffic-container {
  min-height: 100vh;
  background-color: #f7f8fa;
}
.nav-header {
  background-color: #e60012 !important;
}
.nav-header ::v-deep .van-nav-bar__title {
  color: #fff !important;
  font-weight: bold;
}
</style>