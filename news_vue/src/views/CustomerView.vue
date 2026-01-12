<template>
	<el-container class="customer-container">
		<!-- 顶部 Header -->
		<el-header class="customer-header">
			<div class="header-title">新闻聚合综合管理服务平台</div>
			<div class="header-user">
				<span>欢迎您，{{ adminUser }}</span>
			</div>
		</el-header>
		
		<el-container>
			<!-- 左侧菜单 -->
			<el-aside width="180px" class="customer-aside">
				<el-menu
					:default-active="activeMenu"
					:default-openeds="['menu']"
					class="customer-menu"
					background-color="#304156"
					text-color="#bfcbd9"
					active-text-color="#409EFF"
					@select="handleMenuSelect">
					<el-submenu index="menu">
						<template slot="title">
							<i class="el-icon-menu"></i>
							<span>功能菜单</span>
						</template>
						<el-menu-item index="publish">
							<i class="el-icon-edit-outline"></i>
							<span>发布新闻</span>
						</el-menu-item>
						<el-menu-item index="profile">
							<i class="el-icon-user"></i>
							<span>个人信息</span>
						</el-menu-item>
						<el-menu-item index="logout">
							<i class="el-icon-switch-button"></i>
							<span>安全退出</span>
						</el-menu-item>
					</el-submenu>
				</el-menu>
			</el-aside>
			
			<!-- 主要内容区域 -->
			<el-main class="customer-main">
				<!-- 公告展示 -->
				<div v-if="noticeList.length > 0" class="notice-banner">
					<div class="notice-icon">
						<i class="el-icon-bell"></i>
					</div>
					<div class="notice-content">
						<el-carousel height="40px" direction="vertical" :autoplay="true" indicator-position="none">
							<el-carousel-item v-for="notice in noticeList" :key="notice.noid">
								<div class="notice-text">{{ notice.content }}</div>
							</el-carousel-item>
						</el-carousel>
					</div>
				</div>
				
				<!-- 发布新闻 -->
				<div v-if="activeMenu === 'publish'" class="content-card">
					<div class="card-header">
						<i class="el-icon-edit-outline"></i>
						<span>发布新闻</span>
					</div>
					
					<el-form :model="newsForm" label-width="100px" class="news-form">
						<el-form-item label="新闻主题">
							<el-select v-model="newsForm.tid" placeholder="请选择主题" style="width: 100%;">
								<el-option
									v-for="theme in themeList"
									:key="theme.tid"
									:label="theme.tname"
									:value="theme.tid">
								</el-option>
							</el-select>
						</el-form-item>
						
						<el-form-item label="新闻标题">
							<el-input v-model="newsForm.ntitle" placeholder="请输入新闻标题"></el-input>
						</el-form-item>
						
						<el-form-item label="作者">
							<el-input v-model="newsForm.author" placeholder="请输入作者名称"></el-input>
						</el-form-item>
						
						<el-form-item label="新闻内容">
							<el-input
								type="textarea"
								v-model="newsForm.content"
								:rows="10"
								placeholder="请输入新闻内容...">
							</el-input>
						</el-form-item>
						
						<el-form-item>
							<el-button type="primary" @click="publishNews" :loading="publishing">
								<i class="el-icon-upload2"></i> 立即发布
							</el-button>
							<el-button @click="resetForm">
								<i class="el-icon-refresh"></i> 重置
							</el-button>
						</el-form-item>
					</el-form>
				</div>
				
				<!-- 个人信息 -->
				<div v-if="activeMenu === 'profile'" class="content-card">
					<div class="card-header">
						<i class="el-icon-user"></i>
						<span>个人信息</span>
					</div>
					<div class="profile-info">
						<el-descriptions :column="1" border>
							<el-descriptions-item label="用户名">{{ adminUser }}</el-descriptions-item>
							<el-descriptions-item label="角色">普通管理员</el-descriptions-item>
							<el-descriptions-item label="权限">发布新闻</el-descriptions-item>
						</el-descriptions>
					</div>
				</div>
			</el-main>
		</el-container>
	</el-container>
</template>

<script>
import axios from 'axios';

export default {
	name: 'CustomerView',
	data() {
		return {
			adminUser: '',
			activeMenu: 'publish',
			themeList: [],
			publishing: false,
			newsForm: {
				tid: '',
				ntitle: '',
				author: '',
				content: ''
			},
			noticeList: []
		};
	},
	created() {
		// 检查登录状态
		this.adminUser = sessionStorage.getItem('adminUser');
		if (!this.adminUser) {
			this.$router.push('/');
			return;
		}
		this.loadThemes();
		this.loadNotices();
	},
	methods: {
		handleMenuSelect(index) {
			if (index === 'logout') {
				this.logout();
			} else {
				this.activeMenu = index;
			}
		},
		logout() {
			sessionStorage.removeItem('adminUser');
			sessionStorage.removeItem('adminType');
			this.$message.success('已安全退出');
			this.$router.push('/');
		},
		loadThemes() {
			axios.get('http://localhost:6060/news/theme/get')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						this.themeList = res.data;
					}
				}).catch(err => console.log(err));
		},
		// 加载公告（target=1为管理员公告，target=2为所有人公告）
		loadNotices() {
			axios.get('http://localhost:6060/news/notice/getbytarget', {
				params: { target: 1 }
			}).then(res => {
				if (res.data && Array.isArray(res.data)) {
					this.noticeList = res.data;
				}
			}).catch(err => console.log(err));
		},
		publishNews() {
			// 验证
			if (!this.newsForm.tid) {
				this.$message.warning('请选择新闻主题');
				return;
			}
			if (!this.newsForm.ntitle.trim()) {
				this.$message.warning('请输入新闻标题');
				return;
			}
			if (!this.newsForm.author.trim()) {
				this.$message.warning('请输入作者');
				return;
			}
			if (!this.newsForm.content.trim()) {
				this.$message.warning('请输入新闻内容');
				return;
			}
			
			this.publishing = true;
			
			// 构建新闻数据
			const newsData = {
				tid: this.newsForm.tid,
				ntitle: this.newsForm.ntitle,
				author: this.newsForm.author,
				content: this.newsForm.content,
				status: 0, // 待审核
				cnt: 0,
				createdate: new Date().toISOString().slice(0, 19).replace('T', ' ')
			};
			
			axios.post('http://localhost:6060/news/newsinfo/save', newsData, {
				headers: { 'Content-Type': 'application/json' }
			}).then(() => {
				this.$message.success('新闻发布成功！等待超级管理员审核');
				this.resetForm();
			}).catch(err => {
				console.log(err);
				this.$message.error('发布失败，请重试');
			}).finally(() => {
				this.publishing = false;
			});
		},
		resetForm() {
			this.newsForm = {
				tid: '',
				ntitle: '',
				author: '',
				content: ''
			};
		}
	}
}
</script>

<style scoped>
.customer-container {
	height: 100vh;
}

.customer-header {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 0 20px;
	position: relative;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.header-title {
	font-size: 22px;
	font-weight: bold;
	letter-spacing: 2px;
}

.header-user {
	font-size: 14px;
	position: absolute;
	right: 20px;
}

.customer-aside {
	background-color: #304156;
}

.customer-menu {
	border-right: none;
	height: 100%;
}

.customer-main {
	background: linear-gradient(180deg, #f5f7fa 0%, #e9eef3 100%);
	padding: 25px;
}

.content-card {
	background: white;
	padding: 30px;
	border-radius: 8px;
	box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
	max-width: 800px;
}

.card-header {
	display: flex;
	align-items: center;
	margin-bottom: 25px;
	font-size: 18px;
	font-weight: 600;
	color: #303133;
	padding-bottom: 15px;
	border-bottom: 2px solid #409EFF;
}

.card-header i {
	font-size: 22px;
	margin-right: 10px;
	color: #409EFF;
}

.news-form {
	margin-top: 20px;
}

.news-form .el-form-item {
	margin-bottom: 22px;
}

.profile-info {
	margin-top: 20px;
}

.notice-banner {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 8px;
	padding: 12px 20px;
	margin-bottom: 20px;
	display: flex;
	align-items: center;
	box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.notice-icon {
	background: rgba(255, 255, 255, 0.2);
	border-radius: 50%;
	width: 36px;
	height: 36px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 15px;
}

.notice-icon i {
	color: white;
	font-size: 18px;
}

.notice-content {
	flex: 1;
	overflow: hidden;
}

.notice-text {
	color: white;
	font-size: 14px;
	line-height: 40px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
</style>