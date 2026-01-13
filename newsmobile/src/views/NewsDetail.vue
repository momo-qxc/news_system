<template>
  <div class="detail-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar 
      title="新闻详情" 
      left-arrow 
      @click-left="goBack"
      class="nav-header"
    />

    <!-- 加载状态 -->
    <van-loading v-if="loading" class="loading-center" size="36px" vertical>
      加载中...
    </van-loading>

    <!-- 新闻内容 -->
    <div v-else class="news-content-wrapper">
      <!-- 新闻标题 -->
      <h1 class="news-title">{{ news.ntitle }}</h1>
      
      <!-- 新闻元信息 -->
      <div class="news-meta">
        <span class="meta-author">
          <van-icon name="user-o" /> {{ news.author || '未知来源' }}
        </span>
        <span class="meta-date">
          <van-icon name="clock-o" /> {{ news.createdate }}
        </span>
      </div>

      <!-- 新闻正文 -->
      <div class="news-body" v-html="news.content || '暂无内容'"></div>

      <!-- 操作栏：点赞、收藏 -->
      <div class="action-bar">
        <div 
          class="action-item" 
          :class="{ active: isLiked }"
          @click="handleLike"
        >
          <van-icon :name="isLiked ? 'good-job' : 'good-job-o'" />
          <span>{{ isLiked ? '已点赞' : '点赞' }}</span>
        </div>
        <div 
          class="action-item" 
          :class="{ active: isCollected }"
          @click="handleCollect"
        >
          <van-icon :name="isCollected ? 'star' : 'star-o'" />
          <span>{{ isCollected ? '已收藏' : '收藏' }}</span>
        </div>
        <div class="action-item" @click="scrollToComment">
          <van-icon name="comment-o" />
          <span>评论</span>
        </div>
      </div>

      <!-- 分割线 -->
      <van-divider>评论区</van-divider>

      <!-- 评论输入 -->
      <div class="comment-input-section">
        <van-field
          v-model="commentContent"
          rows="3"
          autosize
          type="textarea"
          placeholder="说点什么吧..."
          class="comment-input"
        />
        <div class="comment-options">
          <van-checkbox v-model="isAnonymous" shape="square">匿名评论</van-checkbox>
          <van-button 
            type="primary" 
            size="small" 
            round
            @click="submitComment"
          >
            发表评论
          </van-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list" ref="commentSection">
        <div v-if="comments.length === 0" class="no-comment">
          <van-empty image="search" description="暂无评论，快来抢沙发吧~" />
        </div>
        
        <!-- 顶级评论循环 -->
        <div 
          v-for="comment in comments" 
          :key="comment.cid" 
          class="comment-item"
        >
          <div class="comment-main">
            <div class="comment-header">
              <span class="comment-user">{{ comment.username || '匿名用户' }}</span>
              <span v-if="comment.status === 0" class="pending-tag">待审核</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-footer">
              <div class="footer-left">
                <span class="comment-time">{{ comment.createdate }}</span>
                <span 
                  v-if="comment.uid === currentUid" 
                  class="comment-delete"
                  @click.stop="deleteComment(comment.cid)"
                >
                  删除
                </span>
              </div>
              <div class="footer-right">
                <div class="interaction-item" @click.stop="toggleCommentLike(comment)">
                  <van-icon :name="comment.isLiked ? 'like' : 'like-o'" :color="comment.isLiked ? '#e60012' : '#969799'" />
                  <span>{{ comment.likeCount || 0 }}</span>
                </div>
                <div class="interaction-item" @click.stop="prepareReply(comment)">
                  <van-icon name="comment-o" />
                  <span>回复</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 子回复列表 (嵌套显示) -->
          <div v-if="comment.replyList && comment.replyList.length > 0" class="reply-list">
            <div v-for="reply in comment.replyList" :key="reply.cid" class="reply-item">
              <div class="reply-header">
                <span class="reply-user">{{ reply.username || '匿名用户' }}</span>
                <span v-if="reply.status === 0" class="pending-tag">待审核</span>
              </div>
              <div class="reply-text">{{ reply.content }}</div>
              <div class="reply-footer">
                <span class="comment-time">{{ reply.createdate }}</span>
                <div class="interaction-item" @click.stop="toggleCommentLike(reply)">
                  <van-icon :name="reply.isLiked ? 'like' : 'like-o'" :color="reply.isLiked ? '#e60012' : '#969799'" />
                  <span>{{ reply.likeCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部安全区 -->
      <div class="safe-area-bottom"></div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import API_BASE_URL from '@/config/api'

export default {
  name: 'NewsDetail',
  data() {
    return {
      loading: true,
      news: {},
      isLiked: false,
      isCollected: false,
      currentColid: null,
      currentUid: null,
      commentContent: '',
      isAnonymous: false,
      replyTo: null, // 正在回复的评论对象
      comments: []
    }
  },
  computed: {
    nid() {
      return this.$route.params.nid
    },
    phone() {
      return sessionStorage.getItem('cuser')
    }
  },
  mounted() {
    this.loadNews()
    if (this.phone) {
      this.getCurrentUser()
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    
    // 获取当前用户信息
    async getCurrentUser() {
      try {
        const res = await axios.get(`${API_BASE_URL}/news/users/getbyphone?phone=${this.phone}`)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)
        if (data && data.uid) {
          this.currentUid = data.uid
        }
      } catch (e) {
        console.error('Get user error:', e)
      }
    },

    // 加载新闻详情
    async loadNews() {
      this.loading = true
      try {
        const res = await axios.get(`${API_BASE_URL}/news/newsinfo/getone?nid=${this.nid}`)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)
        
        if (data && data.ntitle) {
          this.news = data
          // 记录浏览历史
          this.saveHistory()
          // 检查点赞和收藏状态
          if (this.phone) {
            this.checkIfLiked()
            this.checkIfCollected()
          }
          // 加载评论
          this.loadComments()
        }
      } catch (e) {
        console.error('Load news error:', e)
        this.$toast.fail('加载失败')
      } finally {
        this.loading = false
      }
    },

    // 保存浏览历史
    async saveHistory() {
      if (!this.phone) return
      try {
        const createdate = encodeURIComponent(new Date().toLocaleString())
        await axios.post(`${API_BASE_URL}/news/history/save?phone=${this.phone}&nid=${this.nid}&createdate=${createdate}`)
      } catch (e) {
        console.error('Save history error:', e)
      }
    },

    // 检查是否已点赞
    async checkIfLiked() {
      try {
        const res = await axios.get(`${API_BASE_URL}/news/like/check?phone=${this.phone}&nid=${this.nid}`)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)
        this.isLiked = data.liked
      } catch (e) {
        console.error('Check like error:', e)
      }
    },

    // 检查是否已收藏
    async checkIfCollected() {
      try {
        const res = await axios.get(`${API_BASE_URL}/news/collection/getdetail?phone=${this.phone}`)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)
        if (data && data.length > 0) {
          for (let item of data) {
            if (item.nid == this.nid) {
              this.isCollected = true
              this.currentColid = item.colid
              break
            }
          }
        }
      } catch (e) {
        console.error('Check collect error:', e)
      }
    },

    // 点赞操作
    async handleLike() {
      if (!this.phone) {
        this.$toast('请先登录')
        return
      }
      try {
        const res = await axios.post(`${API_BASE_URL}/news/like/toggle?phone=${this.phone}&nid=${this.nid}`)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)
        this.isLiked = data.liked
        this.$toast(data.liked ? '点赞成功' : '已取消点赞')
      } catch (e) {
        console.error('Like error:', e)
        this.$toast.fail('操作失败')
      }
    },

    // 收藏操作
    async handleCollect() {
      if (!this.phone) {
        this.$toast('请先登录')
        return
      }
      
      if (this.isCollected) {
        // 取消收藏
        try {
          await axios.delete(`${API_BASE_URL}/news/collection/del/${this.currentColid}`)
          this.isCollected = false
          this.currentColid = null
          this.$toast('已取消收藏')
        } catch (e) {
          console.error('Cancel collect error:', e)
          this.$toast.fail('取消失败')
        }
      } else {
        // 添加收藏
        try {
          const params = new URLSearchParams()
          params.append('phone', this.phone)
          params.append('nid', this.nid)
          params.append('createdate', new Date().toLocaleString())
          await axios.post(`${API_BASE_URL}/news/collection/save`, params)
          this.isCollected = true
          this.$toast.success('收藏成功')
          // 重新获取 colid
          this.checkIfCollected()
        } catch (e) {
          console.error('Collect error:', e)
          this.$toast.fail('收藏失败')
        }
      }
    },

    // 滚动到评论区
    scrollToComment() {
      this.$refs.commentSection.scrollIntoView({ behavior: 'smooth' })
    },

    // 加载评论列表
    async loadComments() {
      try {
        const res = await axios.get(`${API_BASE_URL}/news/comment/getbynid?nid=${this.nid}&phone=${this.phone || ''}`)
        let data = res.data
        if (typeof data === 'string') data = JSON.parse(data)
        this.comments = data || []
      } catch (e) {
        console.error('Load comments error:', e)
      }
    },

    // 提交评论
    async submitComment() {
      if (!this.phone) {
        this.$toast('请先登录')
        return
      }
      if (!this.commentContent.trim()) {
        this.$toast('请输入评论内容')
        return
      }

      try {
        const params = new URLSearchParams()
        params.append('phone', this.phone)
        params.append('nid', this.nid)
        params.append('content', this.commentContent)
        params.append('createdate', new Date().toLocaleString())
        params.append('anonymous', this.isAnonymous)
        if (this.replyTo) {
          params.append('pid', this.replyTo.cid)
        }
        
        await axios.post(`${API_BASE_URL}/news/comment/save`, params)
        this.$toast.success(this.replyTo ? '回复成功' : '评论成功')
        
        this.commentContent = ''
        this.isAnonymous = false
        this.replyTo = null
        this.loadComments()
      } catch (e) {
        console.error('Submit comment error:', e)
        this.$toast.fail('操作失败')
      }
    },

    // 准备回复某人
    prepareReply(comment) {
      if (!this.phone) {
        this.$toast('请先登录')
        return
      }
      this.replyTo = comment
      this.commentContent = `@${comment.username || '匿名用户'} `
      this.scrollToComment()
    },

    // 切换评论点赞状态
    async toggleCommentLike(comment) {
      if (!this.phone) {
        this.$toast('请先登录')
        return
      }
      try {
        const url = comment.isLiked ? '/news/comment/unlike' : '/news/comment/like'
        await axios.post(`${API_BASE_URL}${url}?cid=${comment.cid}&phone=${this.phone}`)
        
        // 本地状态更新，提升 UX
        comment.isLiked = !comment.isLiked
        comment.likeCount = (comment.likeCount || 0) + (comment.isLiked ? 1 : -1)
      } catch (e) {
        console.error('Toggle comment like error:', e)
      }
    },

    // 删除评论
    async deleteComment(cid) {
      try {
        await this.$dialog.confirm({
          title: '提示',
          message: '确定要删除这条评论吗？'
        })
        await axios.delete(`${API_BASE_URL}/news/comment/del/${cid}`)
        this.$toast.success('删除成功')
        this.loadComments()
      } catch (e) {
        if (e !== 'cancel') {
          console.error('Delete comment error:', e)
          this.$toast.fail('删除失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f7f8fa;
}

/* 导航栏 */
.nav-header {
  background-color: #e60012 !important;
}
.nav-header ::v-deep .van-nav-bar__title {
  color: #fff !important;
  font-weight: bold;
}
.nav-header ::v-deep .van-nav-bar__arrow {
  color: #fff !important;
}

/* 加载状态 */
.loading-center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
}

/* 新闻内容区 */
.news-content-wrapper {
  padding: 16px;
  padding-bottom: 60px;
}

/* 标题 */
.news-title {
  font-size: 22px;
  font-weight: bold;
  color: #323233;
  line-height: 1.4;
  margin-bottom: 12px;
}

/* 元信息 */
.news-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #969799;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebedf0;
}
.meta-author, .meta-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 正文 */
.news-body {
  font-size: 16px;
  line-height: 1.8;
  color: #323233;
  text-align: justify;
  margin-bottom: 24px;
}
.news-body ::v-deep p {
  text-indent: 2em;
  margin-bottom: 12px;
}
.news-body ::v-deep img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 12px 0;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-around;
  padding: 16px 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #646566;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.action-item .van-icon {
  font-size: 24px;
}
.action-item.active {
  color: #e60012;
}
.action-item:active {
  transform: scale(0.95);
}

/* 评论输入区 */
.comment-input-section {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 16px;
}
.comment-input {
  background: #f7f8fa;
  border-radius: 8px;
  margin-bottom: 12px;
}
.comment-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 评论列表 */
.comment-list {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
}
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #ebedf0;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.comment-user {
  font-size: 14px;
  font-weight: 500;
  color: #323233;
}
.pending-tag {
  font-size: 11px;
  color: #ff976a;
  background: #fff7e8;
  padding: 2px 6px;
  border-radius: 4px;
}
.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #323233;
  margin-bottom: 8px;
}
.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.interaction-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #969799;
  cursor: pointer;
}
.interaction-item .van-icon {
  font-size: 18px;
}
.comment-time {
  font-size: 12px;
  color: #969799;
}
.comment-delete {
  font-size: 12px;
  color: #ee0a24;
  cursor: pointer;
}

/* 子回复列表样式 */
.reply-list {
  margin-top: 12px;
  padding: 12px;
  background-color: #f7f8fa;
  border-radius: 8px;
}
.reply-item {
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ebedf0;
}
.reply-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}
.reply-header {
  font-size: 13px;
  font-weight: 500;
  color: #323233;
  margin-bottom: 4px;
}
.reply-user {
  color: #1989fa;
}
.reply-text {
  font-size: 13px;
  line-height: 1.5;
  color: #323233;
  margin-bottom: 6px;
}
.reply-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.no-comment {
  padding: 20px 0;
}

/* 安全区 */
.safe-area-bottom {
  height: 20px;
}
</style>
