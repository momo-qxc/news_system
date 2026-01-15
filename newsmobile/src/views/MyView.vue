<template>
  <div class="my-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar title="我的" class="nav-header" />

    <!-- 未登录状态 -->
    <div v-if="!isLoggedIn" class="login-section">
      <div class="avatar-placeholder">
        <van-icon name="user-o" size="60" color="#ddd" />
      </div>
      <p class="login-hint">登录后享受更多功能</p>
      <div class="login-buttons">
        <van-button type="primary" round block @click="showLoginPopup = true">
          手机号登录
        </van-button>
        <van-button plain round block @click="showRegisterPopup = true" class="register-btn">
          立即注册
        </van-button>
      </div>
    </div>

    <!-- 已登录状态 -->
    <div v-else class="user-section">
      <div class="user-card">
        <div class="user-avatar">
          <van-icon name="user-circle-o" size="60" color="#e60012" />
        </div>
        <div class="user-info">
          <div class="user-phone">{{ phone }}</div>
          <div class="user-tag">普通用户</div>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <van-cell-group inset>
        <van-cell 
          title="我的收藏" 
          icon="star-o" 
          is-link 
          :value="isLoggedIn ? collectionCount + '篇' : ''"
          @click="goToList('collection')"
        />
        <van-cell 
          title="我的点赞" 
          icon="good-job-o" 
          is-link 
          :value="isLoggedIn ? likeCount + '篇' : ''"
          @click="goToList('like')"
        />
        <van-cell 
          title="浏览记录" 
          icon="clock-o" 
          is-link 
          :value="isLoggedIn ? historyCount + '篇' : ''"
          @click="goToList('history')"
        />
      </van-cell-group>

      <van-cell-group inset class="mt-16">

        <van-cell title="关于我们" icon="info-o" is-link @click="showAbout = true" />
      </van-cell-group>

      <van-cell-group v-if="isLoggedIn" inset class="mt-16">
        <van-cell>
          <van-button type="danger" plain block round @click="logout">
            退出登录
          </van-button>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 登录弹窗 -->
    <van-popup v-model="showLoginPopup" round position="bottom" :style="{ height: '50%' }">
      <div class="popup-content">
        <h3 class="popup-title">手机号登录</h3>
        <van-form @submit="onLogin">
          <van-field
            v-model="loginForm.phone"
            type="tel"
            label="手机号"
            placeholder="请输入手机号"
            maxlength="11"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
          <van-field
            v-model="loginForm.code"
            type="number"
            label="验证码"
            placeholder="请输入验证码"
            maxlength="6"
            :rules="[{ required: true, message: '请输入验证码' }]"
          >
            <template #button>
              <van-button 
                size="small" 
                type="primary" 
                :disabled="countdown > 0"
                @click.prevent="sendCode('login')"
              >
                {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
              </van-button>
            </template>
          </van-field>
          <div class="form-actions">
            <van-button type="primary" block round native-type="submit">
              登 录
            </van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 注册弹窗 -->
    <van-popup v-model="showRegisterPopup" round position="bottom" :style="{ height: '55%' }">
      <div class="popup-content">
        <h3 class="popup-title">用户注册</h3>
        <van-form @submit="onRegister">
          <van-field
            v-model="registerForm.username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
          />
          <van-field
            v-model="registerForm.phone"
            type="tel"
            label="手机号"
            placeholder="请输入手机号"
            maxlength="11"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
          <van-field
            v-model="registerForm.code"
            type="number"
            label="验证码"
            placeholder="请输入验证码"
            maxlength="6"
            :rules="[{ required: true, message: '请输入验证码' }]"
          >
            <template #button>
              <van-button 
                size="small" 
                type="primary" 
                :disabled="countdown > 0"
                @click.prevent="sendCode('register')"
              >
                {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
              </van-button>
            </template>
          </van-field>
          <van-field name="sex" label="性别">
            <template #input>
              <van-radio-group v-model="registerForm.sex" direction="horizontal">
                <van-radio :name="1">男</van-radio>
                <van-radio :name="0">女</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field
            v-model="registerForm.age"
            type="number"
            label="年龄"
            placeholder="请输入年龄"
          />
          <div class="form-actions">
            <van-button type="primary" block round native-type="submit">
              注 册
            </van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 列表弹窗 -->
    <van-popup v-model="showListPopup" position="right" :style="{ width: '100%', height: '100%' }">
      <div class="list-page">
        <van-nav-bar 
          :title="listTitle" 
          left-arrow 
          @click-left="showListPopup = false"
          class="nav-header"
        >
          <template #right v-if="listType === 'history'">
            <van-icon name="delete-o" size="18" @click="clearAllHistory" />
          </template>
        </van-nav-bar>
        <div class="list-content">
          <van-list
            v-model="listLoading"
            :finished="listFinished"
            finished-text="没有更多了"
            @load="loadListData"
          >
            <van-swipe-cell 
              v-for="item in listData" 
              :key="item.nid || item.hid || item.lid || item.colid"
            >
              <van-cell 
                :title="item.ntitle"
                :label="item.createdate"
                is-link
                @click="goToNews(item.nid)"
              />
              <template #right>
                <van-button 
                  square 
                  type="danger" 
                  text="删除"
                  class="delete-btn"
                  @click="deleteItem(item)"
                />
              </template>
            </van-swipe-cell>
          </van-list>
          <van-empty v-if="!listLoading && listData.length === 0" description="暂无数据" />
        </div>
      </div>
    </van-popup>

    <!-- 关于我们弹窗 -->
    <van-popup v-model="showAbout" round :style="{ padding: '24px', width: '80%' }">
      <div class="about-content">
        <h3>新闻聚合 App</h3>
        <p>版本：1.0.0</p>
        <p>一款简洁好用的新闻阅读应用</p>
        <van-button type="primary" block round size="small" @click="showAbout = false">
          知道了
        </van-button>
      </div>
    </van-popup>

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
  name: 'MyView',
  data() {
    return {
      active: 3,
      showLoginPopup: false,
      showRegisterPopup: false,
      showListPopup: false,
      showAbout: false,
      countdown: 0,
      countdownTimer: null,
      loginForm: {
        phone: '',
        code: ''
      },
      registerForm: {
        username: '',
        phone: '',
        code: '',
        sex: 1,
        age: ''
      },
      collectionCount: 0,
      likeCount: 0,
      historyCount: 0,
      listType: '',
      listTitle: '',
      listData: [],
      listLoading: false,
      listFinished: false,
      listPage: 1,
      currentPhone: sessionStorage.getItem('cuser') || ''
    }
  },
  computed: {
    isLoggedIn() {
      return !!this.currentPhone
    },
    phone() {
      return this.currentPhone
    }
  },
  mounted() {
    if (this.isLoggedIn) {
      this.loadCounts()
    }
  },
  methods: {
    // 加载数据统计
    async loadCounts() {
      try {
        // 获取收藏数量
        const colRes = await axios.get(`${API_BASE_URL}/news/collection/getdetail?phone=${this.phone}`)
        let colData = colRes.data
        if (typeof colData === 'string') colData = JSON.parse(colData)
        this.collectionCount = colData ? colData.length : 0

        // 获取点赞数量
        const likeRes = await axios.get(`${API_BASE_URL}/news/like/getbyphone?phone=${this.phone}`)
        let likeData = likeRes.data
        if (typeof likeData === 'string') likeData = JSON.parse(likeData)
        this.likeCount = likeData ? likeData.length : 0

        // 获取浏览记录数量
        const histRes = await axios.get(`${API_BASE_URL}/news/history/getdetail?phone=${this.phone}`)
        let histData = histRes.data
        if (typeof histData === 'string') histData = JSON.parse(histData)
        this.historyCount = histData ? histData.length : 0
      } catch (e) {
        console.error('Load counts error:', e)
      }
    },

    // 发送验证码
    async sendCode(type) {
      const phone = type === 'login' ? this.loginForm.phone : this.registerForm.phone
      if (!phone || phone.length !== 11) {
        this.$toast('请输入正确的手机号')
        return
      }

      try {
        await axios.get(`${API_BASE_URL}/news/users/getcode?phone=${phone}`)
        this.$toast.success('验证码已发送')
        this.startCountdown()
      } catch (e) {
        console.error('Send code error:', e)
        this.$toast.fail('发送失败')
      }
    },

    // 开始倒计时
    startCountdown() {
      this.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(this.countdownTimer)
        }
      }, 1000)
    },

    // 登录
    async onLogin() {
      try {
        // 使用 URLSearchParams 发送表单格式数据
        const params = new URLSearchParams()
        params.append('phone', this.loginForm.phone)
        params.append('code', this.loginForm.code)
        
        const res = await axios.post(`${API_BASE_URL}/news/users/login`, params)
        
        let result = res.data
        console.log('Login response:', result, typeof result)
        if (result === true || result === 'true' || result === 'TRUE') {
          sessionStorage.setItem('cuser', this.loginForm.phone)
          this.currentPhone = this.loginForm.phone
          this.$toast.success('登录成功')
          this.showLoginPopup = false
          this.loginForm = { phone: '', code: '' }
          this.loadCounts()
        } else {
          this.$toast.fail('登录失败，请检查验证码')
        }
      } catch (e) {
        console.error('Login error:', e)
        this.$toast.fail('登录失败')
      }
    },

    // 注册
    async onRegister() {
      try {
        // 使用 URLSearchParams 发送表单格式数据
        const params = new URLSearchParams()
        params.append('username', this.registerForm.username)
        params.append('phone', this.registerForm.phone)
        params.append('code', this.registerForm.code)
        params.append('sex', this.registerForm.sex)
        if (this.registerForm.age) {
          params.append('age', this.registerForm.age)
        }
        
        const res = await axios.post(`${API_BASE_URL}/news/users/add`, params)
        
        let result = res.data
        console.log('Register response:', result)
        if (result === true || result === 'true' || result === 'TRUE' || result === 'success' || result > 0) {
          this.$toast.success('注册成功，请登录')
          this.showRegisterPopup = false
          const phone = this.registerForm.phone
          this.registerForm = { username: '', phone: '', code: '', sex: 1, age: '' }
          this.showLoginPopup = true
          this.loginForm.phone = phone
        } else {
          this.$toast.fail('注册失败')
        }
      } catch (e) {
        console.error('Register error:', e)
        this.$toast.fail('注册失败')
      }
    },

    // 退出登录
    logout() {
      this.$dialog.confirm({
        title: '提示',
        message: '确定要退出登录吗？'
      }).then(() => {
        sessionStorage.removeItem('cuser')
        this.currentPhone = ''
        this.collectionCount = 0
        this.likeCount = 0
        this.historyCount = 0
        this.$toast.success('已退出登录')
      }).catch(() => {})
    },

    // 跳转到列表
    goToList(type) {
      if (!this.isLoggedIn) {
        this.$toast('请先登录')
        return
      }

      this.listType = type
      this.listTitle = type === 'collection' ? '我的收藏' : type === 'like' ? '我的点赞' : '浏览记录'
      this.listData = []
      this.listPage = 1
      this.listFinished = false
      this.showListPopup = true
    },

    // 加载列表数据
    async loadListData() {
      try {
        let url = ''
        if (this.listType === 'collection') {
          url = `${API_BASE_URL}/news/collection/getdetail?phone=${this.phone}`
        } else if (this.listType === 'like') {
          url = `${API_BASE_URL}/news/like/getbyphone?phone=${this.phone}`
        } else {
          url = `${API_BASE_URL}/news/history/getdetail?phone=${this.phone}`
        }

        const res = await axios.get(url)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)

        if (data && data.length > 0) {
          this.listData = data
        }
        this.listFinished = true
        this.listLoading = false
      } catch (e) {
        console.error('Load list error:', e)
        this.listLoading = false
        this.listFinished = true
      }
    },

    // 跳转到新闻详情
    goToNews(nid) {
      this.showListPopup = false
      this.$router.push({ name: 'newsDetail', params: { nid } })
    },

    // 删除单条记录
    async deleteItem(item) {
      try {
        await this.$dialog.confirm({
          title: '提示',
          message: '确定要删除这条记录吗？'
        })
        
        let url = ''
        if (this.listType === 'collection') {
          url = `${API_BASE_URL}/news/collection/del/${item.colid}`
        } else if (this.listType === 'like') {
          // 点赞使用 toggle 来取消
          url = `${API_BASE_URL}/news/like/toggle?phone=${this.phone}&nid=${item.nid}`
          await axios.post(url)
          this.$toast.success('已删除')
          this.listData = this.listData.filter(i => i.lid !== item.lid)
          this.likeCount = Math.max(0, this.likeCount - 1)
          return
        } else {
          url = `${API_BASE_URL}/news/history/del/${item.hid}`
        }
        
        await axios.delete(url)
        this.$toast.success('已删除')
        
        // 从列表中移除
        if (this.listType === 'collection') {
          this.listData = this.listData.filter(i => i.colid !== item.colid)
          this.collectionCount = Math.max(0, this.collectionCount - 1)
        } else {
          this.listData = this.listData.filter(i => i.hid !== item.hid)
          this.historyCount = Math.max(0, this.historyCount - 1)
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('Delete error:', e)
          this.$toast.fail('删除失败')
        }
      }
    },

    // 清空浏览记录
    async clearAllHistory() {
      try {
        await this.$dialog.confirm({
          title: '提示',
          message: '确定要清空所有浏览记录吗？'
        })
        
        await axios.delete(`${API_BASE_URL}/news/history/clear?phone=${this.phone}`)
        this.$toast.success('已清空')
        this.listData = []
        this.historyCount = 0
      } catch (e) {
        if (e !== 'cancel') {
          console.error('Clear history error:', e)
          this.$toast.fail('清空失败')
        }
      }
    },


  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  }
}
</script>

<style scoped>
.my-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 60px;
}

/* 导航栏 */
.nav-header {
  background: linear-gradient(135deg, #e60012 0%, #ff4d4f 100%) !important;
}
.nav-header ::v-deep .van-nav-bar__title {
  color: #fff !important;
  font-weight: bold;
}
.nav-header ::v-deep .van-nav-bar__arrow {
  color: #fff !important;
}

/* 未登录区域 */
.login-section {
  padding: 40px 24px;
  text-align: center;
  background: linear-gradient(180deg, #fff 0%, #f7f8fa 100%);
}
.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  border: 2px dashed #ddd;
}
.login-hint {
  color: #969799;
  font-size: 14px;
  margin-bottom: 24px;
}
.login-buttons {
  max-width: 280px;
  margin: 0 auto;
}
.register-btn {
  margin-top: 12px;
}

/* 已登录用户区域 */
.user-section {
  padding: 24px 16px;
  background: linear-gradient(135deg, #e60012 0%, #ff4d4f 100%);
}
.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.user-info {
  flex: 1;
}
.user-phone {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 4px;
}
.user-tag {
  display: inline-block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
}

/* 菜单区域 */
.menu-section {
  padding: 16px;
}
.mt-16 {
  margin-top: 16px;
}

/* 弹窗内容 */
.popup-content {
  padding: 24px;
}
.popup-title {
  text-align: center;
  font-size: 18px;
  color: #323233;
  margin-bottom: 24px;
}
.form-actions {
  margin-top: 24px;
}

/* 列表页面 */
.list-page {
  height: 100%;
  background: #f7f8fa;
}
.list-content {
  padding: 16px;
}
.list-content .van-swipe-cell {
  margin-bottom: 8px;
  border-radius: 8px;
  overflow: hidden;
}
.list-content .van-cell {
  background: #fff;
  border-radius: 8px;
}
.delete-btn {
  height: 100%;
  min-width: 65px;
}

/* 关于内容 */
.about-content {
  text-align: center;
}
.about-content h3 {
  margin-bottom: 12px;
  color: #323233;
}
.about-content p {
  color: #646566;
  font-size: 14px;
  margin-bottom: 8px;
}
.about-content .van-button {
  margin-top: 16px;
}
</style>