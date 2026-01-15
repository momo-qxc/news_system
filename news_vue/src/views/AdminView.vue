<template>
	<el-container class="admin-container">
		<!-- 顶部 Header -->
		<el-header class="admin-header">
			<div class="header-title">新闻聚合综合管理服务平台</div>
			<div class="header-user">
				<span>欢迎您，{{ adminUser }}</span>
			</div>
		</el-header>
		
		<el-container>
			<!-- 左侧菜单 -->
			<el-aside width="180px" class="admin-aside">
				<el-menu
					:default-active="activeMenu"
					:default-openeds="['menu']"
					class="admin-menu"
					background-color="#304156"
					text-color="#bfcbd9"
					active-text-color="#409EFF"
					@select="handleMenuSelect">
					<el-submenu index="menu">
						<template slot="title">
							<i class="el-icon-menu"></i>
							<span>功能菜单</span>
						</template>
						<el-menu-item index="home">
							<i class="el-icon-s-home"></i>
							<span>系统主页</span>
						</el-menu-item>
						<el-menu-item index="theme">
							<i class="el-icon-menu"></i>
							<span>主题管理</span>
						</el-menu-item>
						<el-menu-item index="news">
							<i class="el-icon-document"></i>
							<span>新闻管理</span>
						</el-menu-item>
						<el-menu-item index="notice">
							<i class="el-icon-bell"></i>
							<span>系统公告</span>
						</el-menu-item>
						<el-menu-item index="comment">
							<i class="el-icon-s-comment"></i>
							<span>评论管理</span>
						</el-menu-item>
						<el-menu-item index="crawler">
							<i class="el-icon-cpu"></i>
							<span>新闻脉冲</span>
						</el-menu-item>
						<el-menu-item index="profile">
							<i class="el-icon-s-check"></i>
							<span>个人信息</span>
						</el-menu-item>
						<el-menu-item index="logout">
							<i class="el-icon-s-grid"></i>
							<span>安全退出</span>
						</el-menu-item>
					</el-submenu>
				</el-menu>
			</el-aside>
			
			<!-- 主要内容区域 -->
			<el-main class="admin-main">
				<!-- 统计卡片 -->
				<el-row :gutter="20" v-if="activeMenu === 'home'">
					<el-col :span="8">
						<div class="stat-card">
							<el-statistic title="注册用户数" :value="stats.users">
								<template slot="prefix">
									<i class="el-icon-user" style="color: blue;"></i>
								</template>
							</el-statistic>
						</div>
					</el-col>
					<el-col :span="8">
						<div class="stat-card">
							<el-statistic title="新闻主题" :value="stats.themes">
								<template slot="prefix">
									<i class="el-icon-s-flag" style="color: blue;"></i>
								</template>
							</el-statistic>
						</div>
					</el-col>
					<el-col :span="8">
						<div class="stat-card">
							<el-statistic title="新闻数量" :value="stats.news">
								<template slot="prefix">
									<i class="el-icon-s-flag" style="color: red;"></i>
								</template>
							</el-statistic>
						</div>
					</el-col>
				</el-row>
				
				<!-- 饼状图 -->
				<div v-if="activeMenu === 'home'" class="chart-container">
					<h3>新闻主题统计</h3>
					<div id="chartdiv" ref="chartdiv"></div>
				</div>
				
				<!-- 主题管理 -->
				<div v-if="activeMenu === 'theme'" class="content-card">
					<div class="card-header">
						<span>主题管理</span>
						<el-button type="primary" size="small" @click="showAddTheme = true">新增主题</el-button>
					</div>
					<el-table :data="themeList" style="width: 100%">
						<el-table-column prop="tid" label="主题ID" width="120"></el-table-column>
						<el-table-column prop="tname" label="主题名称"></el-table-column>
						<el-table-column label="操作" width="120">
							<template slot-scope="scope">
								<el-button type="danger" size="mini" @click="deleteTheme(scope.row.tid)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
				</div>
				
				<!-- 新增主题对话框 -->
				<el-dialog title="新增主题" :visible.sync="showAddTheme" width="400px">
					<el-form :model="newTheme">
						<el-form-item label="主题名称" label-width="80px">
							<el-input v-model="newTheme.tname" placeholder="请输入主题名称"></el-input>
						</el-form-item>
					</el-form>
					<span slot="footer" class="dialog-footer">
						<el-button @click="showAddTheme = false">取消</el-button>
						<el-button type="primary" @click="addTheme">确定</el-button>
					</span>
				</el-dialog>
				
				<!-- 新闻管理 -->
				<div v-if="activeMenu === 'news'" class="content-card">
					<div class="card-header">
						<span>新闻管理</span>
						<div v-if="isSuperAdmin" style="display: flex; align-items: center; gap: 10px;">
							<el-date-picker
								v-model="batchDeleteDate"
								type="date"
								placeholder="选择日期"
								size="small"
								value-format="yyyy-MM-dd"
								style="width: 150px;">
							</el-date-picker>
							<el-button 
								type="danger" 
								size="small" 
								icon="el-icon-delete"
								:disabled="!batchDeleteDate"
								@click="previewBatchDelete">批量删除</el-button>
						</div>
					</div>
					<el-table :data="newsList" style="width: 100%">
						<el-table-column prop="nid" label="新闻ID" width="120" show-overflow-tooltip></el-table-column>
						<el-table-column prop="ntitle" label="新闻标题" show-overflow-tooltip></el-table-column>
						<el-table-column prop="createdate" label="创建时间" width="160"></el-table-column>
						<el-table-column prop="author" label="作者" width="80"></el-table-column>
						<el-table-column prop="cnt" label="点赞" width="60"></el-table-column>
						<el-table-column label="状态" width="70">
							<template slot-scope="scope">
								<el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="mini">
									{{ scope.row.status === 1 ? '通过' : '待审' }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="操作" width="150">
							<template slot-scope="scope">
								<el-button 
									v-if="scope.row.status !== 1" 
									type="success" 
									size="mini" 
									@click="approveNews(scope.row.nid)">通过</el-button>
								<el-button 
									v-if="scope.row.status === 1" 
									type="warning" 
									size="mini" 
									@click="rejectNews(scope.row.nid)">撤回</el-button>
								<el-button 
									type="danger" 
									size="mini" 
									@click="deleteNews(scope.row.nid)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<el-pagination
						class="news-pagination"
						layout="prev, pager, next"
						:total="newsTotal"
						:page-size="newsPageSize"
						:current-page.sync="newsPageNo"
						@current-change="loadNews">
					</el-pagination>
					
					<!-- 批量删除确认弹窗 -->
					<el-dialog 
						title="确认批量删除" 
						:visible.sync="showBatchDeleteConfirm" 
						width="400px">
						<div style="text-align: center;">
							<i class="el-icon-warning" style="font-size: 48px; color: #E6A23C;"></i>
							<p style="margin: 20px 0; font-size: 16px;">
								您即将删除 <strong style="color: #F56C6C;">{{ batchDeleteDate }}</strong> 的所有新闻
							</p>
							<p style="font-size: 24px; font-weight: bold; color: #F56C6C;">
								共 {{ batchDeleteCount }} 条
							</p>
							<p style="color: #909399; font-size: 12px;">此操作不可恢复，请谨慎操作！</p>
						</div>
						<div slot="footer">
							<el-button @click="showBatchDeleteConfirm = false">取消</el-button>
							<el-button type="danger" @click="confirmBatchDelete">确认删除</el-button>
						</div>
					</el-dialog>
				</div>
				
				<!-- 评论管理 -->
				<div v-if="activeMenu === 'comment'" class="content-card">
					<div class="card-header">
						<span>评论管理</span>
					</div>
					<el-table :data="commentList" style="width: 100%">
						<el-table-column prop="cid" label="评论ID" width="80"></el-table-column>
						<el-table-column label="用户" width="100">
							<template slot-scope="scope">
								<span>{{ scope.row.uid === 0 ? '匿名用户' : getUserName(scope.row.uid) }}</span>
							</template>
						</el-table-column>
						<el-table-column prop="content" label="评论内容" show-overflow-tooltip></el-table-column>
						<el-table-column prop="createdate" label="评论时间" width="160"></el-table-column>
						<el-table-column prop="nid" label="新闻ID" width="100" show-overflow-tooltip></el-table-column>
						<el-table-column label="状态" width="80">
							<template slot-scope="scope">
								<el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" size="mini">
									{{ scope.row.status === 1 ? '已审核' : '待审核' }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="操作" width="160">
							<template slot-scope="scope">
								<el-button 
									v-if="scope.row.status !== 1" 
									type="success" 
									size="mini" 
									@click="approveComment(scope.row.cid)">通过</el-button>
								<el-button 
									v-if="scope.row.status === 1" 
									type="warning" 
									size="mini" 
									@click="rejectComment(scope.row.cid)">撤回</el-button>
								<el-button 
									type="danger" 
									size="mini" 
									@click="deleteComment(scope.row.cid)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<el-pagination
						class="comment-pagination"
						layout="prev, pager, next"
						:total="commentTotal"
						:page-size="commentPageSize"
						:current-page.sync="commentPageNo"
						@current-change="loadComments">
					</el-pagination>
				</div>
				
				<!-- 个人信息 -->
				<div v-if="activeMenu === 'profile'" class="content-card">
					<div class="card-header">
						<span>个人信息管理</span>
					</div>
					
					<!-- Tab 切换 -->
					<div class="profile-tabs">
						<div 
							class="profile-tab" 
							:class="{ active: profileTab === 'admin' }"
							@click="profileTab = 'admin'">
							<i class="el-icon-user-solid"></i> 管理员管理
						</div>
						<div 
							class="profile-tab" 
							:class="{ active: profileTab === 'user' }"
							@click="profileTab = 'user'">
							<i class="el-icon-s-custom"></i> 普通用户管理
						</div>
					</div>
					
					<!-- 管理员管理 -->
					<el-table v-if="profileTab === 'admin'" :data="adminList" style="width: 100%;">
						<el-table-column prop="aid" label="ID" width="60"></el-table-column>
						<el-table-column prop="adminame" label="用户名" width="120"></el-table-column>
						<el-table-column label="类型" width="100">
							<template slot-scope="scope">
								<el-tag :type="scope.row.isadmin == 1 ? 'danger' : 'primary'" size="small">
									{{ scope.row.isadmin == 1 ? '超级管理员' : '普通管理员' }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="审核状态" width="100">
							<template slot-scope="scope">
								<el-tag :type="scope.row.status == 1 ? 'success' : 'warning'" size="small">
									{{ scope.row.status == 1 ? '已通过' : '待审核' }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="操作" width="180">
							<template slot-scope="scope">
								<template v-if="scope.row.isadmin != 1">
									<el-button 
										v-if="scope.row.status != 1" 
										type="success" 
										size="mini" 
										@click="approveAdmin(scope.row.aid)">审核通过</el-button>
									<el-button 
										v-if="scope.row.status == 1" 
										type="warning" 
										size="mini" 
										@click="rejectAdmin(scope.row.aid)">撤回审核</el-button>
									<el-button 
										type="danger" 
										size="mini" 
										@click="deleteAdmin(scope.row.aid)">删除</el-button>
								</template>
								<span v-else style="color: #999;">-</span>
							</template>
						</el-table-column>
					</el-table>
					
					<!-- 普通用户管理 -->
					<el-table v-if="profileTab === 'user'" :data="userList" style="width: 100%;">
						<el-table-column prop="uid" label="ID" width="60"></el-table-column>
						<el-table-column prop="username" label="用户名" width="120"></el-table-column>
						<el-table-column prop="phone" label="手机号" width="140"></el-table-column>
						<el-table-column label="性别" width="80">
							<template slot-scope="scope">
								{{ scope.row.sex == 1 ? '男' : '女' }}
							</template>
						</el-table-column>
						<el-table-column prop="age" label="年龄" width="70"></el-table-column>
						<el-table-column label="操作" width="100">
							<template slot-scope="scope">
								<el-button 
									type="danger" 
									size="mini" 
									@click="deleteUser(scope.row.uid)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
				</div>
				
				<!-- 系统公告 -->
				<div v-if="activeMenu === 'notice'" class="content-card">
					<div class="card-header">
						<span>系统公告管理</span>
						<el-button type="primary" size="small" @click="showAddNotice = true">
							<i class="el-icon-plus"></i> 发布公告
						</el-button>
					</div>
					
					<!-- 公告列表 -->
					<el-table :data="noticeList" style="width: 100%;">
						<el-table-column prop="noid" label="ID" width="180"></el-table-column>
						<el-table-column prop="content" label="公告内容" show-overflow-tooltip></el-table-column>
						<el-table-column prop="createdate" label="发布时间" width="140"></el-table-column>
						<el-table-column label="展示对象" width="100">
							<template slot-scope="scope">
								<el-tag :type="scope.row.target == 0 ? 'success' : (scope.row.target == 1 ? 'warning' : 'primary')" size="small">
									{{ scope.row.target == 0 ? '用户端' : (scope.row.target == 1 ? '管理员' : '所有人') }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="优先级" width="80">
							<template slot-scope="scope">
								<el-tag :type="scope.row.priority == 1 ? 'danger' : (scope.row.priority == 2 ? 'warning' : 'info')" size="small">
									{{ scope.row.priority == 1 ? '高' : (scope.row.priority == 2 ? '中' : '低') }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column label="展示状态" width="90">
							<template slot-scope="scope">
								<el-switch v-model="scope.row.visible" :active-value="1" :inactive-value="0" @change="toggleNoticeVisible(scope.row)"></el-switch>
							</template>
						</el-table-column>
						<el-table-column label="操作" width="150">
							<template slot-scope="scope">
								<el-button type="primary" size="mini" @click="editNotice(scope.row)">编辑</el-button>
								<el-button type="danger" size="mini" @click="deleteNotice(scope.row.noid)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					
					<!-- 新增/编辑公告对话框 -->
					<el-dialog :title="editingNotice ? '编辑公告' : '发布公告'" :visible.sync="showAddNotice" width="500px">
						<el-form :model="noticeForm" label-width="80px">
							<el-form-item label="公告内容">
								<el-input type="textarea" :rows="4" v-model="noticeForm.content" placeholder="请输入公告内容"></el-input>
							</el-form-item>
							<el-form-item label="展示对象">
								<el-select v-model="noticeForm.target" placeholder="选择展示对象" style="width: 100%;">
									<el-option label="用户端(前端页面)" :value="0"></el-option>
									<el-option label="管理员(普通管理员)" :value="1"></el-option>
									<el-option label="所有人" :value="2"></el-option>
								</el-select>
							</el-form-item>
							<el-form-item label="优先级">
								<el-select v-model="noticeForm.priority" placeholder="选择优先级" style="width: 100%;">
									<el-option label="高" :value="1"></el-option>
									<el-option label="中" :value="2"></el-option>
									<el-option label="低" :value="3"></el-option>
								</el-select>
							</el-form-item>
							<el-form-item label="立即展示">
								<el-switch v-model="noticeForm.visible" :active-value="1" :inactive-value="0"></el-switch>
							</el-form-item>
						</el-form>
						<div slot="footer">
							<el-button @click="showAddNotice = false">取消</el-button>
							<el-button type="primary" @click="submitNotice">{{ editingNotice ? '保存' : '发布' }}</el-button>
						</div>
					</el-dialog>
				</div>
				
				<!-- 新闻脉冲 (爬虫管理) -->
				<div v-if="activeMenu === 'crawler'" class="content-card">
					<div class="card-header">
						<span>新闻脉冲 - 实时采集系统</span>
						<el-tag :type="crawlerStatus ? 'danger' : 'success'" effect="dark">
							{{ crawlerStatus ? '运行中' : '空闲' }}
						</el-tag>
					</div>
					
					<el-row :gutter="20">
						<el-col :span="8">
							<div class="crawler-controls">
								<h4 class="control-title">采集任务设置</h4>
								<el-form :model="crawlerForm" label-width="80px" size="small">
									<el-form-item label="采集分类">
										<el-select v-model="crawlerForm.tid" placeholder="全部分类" style="width: 100%;">
											<el-option label="全部分类" :value="0"></el-option>
											<el-option 
												v-for="item in themeList" 
												:key="item.tid" 
												:label="item.tname" 
												:value="item.tid"></el-option>
										</el-select>
									</el-form-item>
									<el-form-item label="单类上限">
										<el-slider v-model="crawlerForm.limit" :min="1" :max="50" show-input></el-slider>
									</el-form-item>
									<el-form-item>
										<el-button 
											type="primary" 
											icon="el-icon-video-play" 
											:loading="crawlerStatus"
											@click="startCrawl"
											style="width: 100%;">立即启动</el-button>
									</el-form-item>
									<el-form-item>
										<el-button 
											icon="el-icon-delete" 
											@click="clearCrawlerLogs"
											style="width: 100%;">清空日志</el-button>
									</el-form-item>
								</el-form>
								
								<div class="crawler-tips">
									<i class="el-icon-info"></i>
									<span>提示：采集任务将在后台异步执行，您可以切换到其他页面操作，日志会持续更新。</span>
								</div>
							</div>
						</el-col>
						<el-col :span="16">
							<div class="crawler-terminal">
								<div class="terminal-header">
									<span class="dot red"></span>
									<span class="dot yellow"></span>
									<span class="dot green"></span>
									<span class="terminal-title">Pulse Output Console</span>
								</div>
								<div class="terminal-body" ref="terminalBody">
									<div v-if="crawlerLogs.length === 0" class="log-empty">等待任务启动...</div>
									<div 
										v-for="(log, index) in crawlerLogs" 
										:key="index" 
										class="log-line"
										:class="{ 'log-error': log.includes('❌'), 'log-success': log.includes('✅') || log.includes('✓') }">
										{{ log }}
									</div>
								</div>
							</div>
						</el-col>
					</el-row>
				</div>
				
				<!-- 其他页面占位 -->
				<div v-if="activeMenu !== 'home' && activeMenu !== 'theme' && activeMenu !== 'news' && activeMenu !== 'comment' && activeMenu !== 'profile' && activeMenu !== 'notice' && activeMenu !== 'crawler'" class="content-placeholder">
					<h2>{{ getMenuTitle() }}</h2>
					<p>功能开发中...</p>
				</div>
			</el-main>
		</el-container>
	</el-container>
</template>

<script>
import axios from 'axios';
import * as am5 from '@amcharts/amcharts5';
import * as am5percent from '@amcharts/amcharts5/percent';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';

export default {
	name: 'AdminView',
	data() {
		return {
			adminUser: '',
			activeMenu: 'home',
			stats: {
				users: 0,
				themes: 0,
				news: 0
			},
			chartRoot: null,
			themeList: [],
			showAddTheme: false,
			newTheme: { tname: '' },
			// 新闻管理
			newsList: [],
			newsTotal: 0,
			newsPageNo: 1,
			newsPageSize: 10,
			// 评论管理
			commentList: [],
			commentTotal: 0,
			commentPageNo: 1,
			commentPageSize: 10,
			userMap: {}, // uid -> username 映射
			// 个人信息管理
			adminList: [],
			userList: [],
			profileTab: 'admin', // 'admin' 或 'user'
			// 公告管理
			noticeList: [],
			showAddNotice: false,
			editingNotice: null,
			noticeForm: { content: '', target: 0, visible: 1, priority: 2 },
			// 新闻脉冲
			crawlerForm: { tid: 0, limit: 10 },
			crawlerLogs: [],
			crawlerStatus: false,
			crawlerTimer: null,
			// 批量删除
			batchDeleteDate: null,
			batchDeleteCount: 0,
			showBatchDeleteConfirm: false
		};
	},
	computed: {
		isSuperAdmin() {
			return sessionStorage.getItem('adminType') === '1';
		}
	},
	created() {
		// 检查登录状态
		this.adminUser = sessionStorage.getItem('adminUser');
		if (!this.adminUser) {
			this.$router.push('/');
			return;
		}
		this.loadStats();
	},
	mounted() {
		this.loadPieChart();
	},
	beforeDestroy() {
		// 销毁图表实例
		if (this.chartRoot) {
			this.chartRoot.dispose();
		}
	},
	watch: {
		activeMenu(newVal, oldVal) {
			// 切换离开主页时销毁图表
			if (oldVal === 'home' && newVal !== 'home') {
				if (this.chartRoot) {
					this.chartRoot.dispose();
					this.chartRoot = null;
				}
			}
			// 切换到主页时重新加载饼状图
			if (newVal === 'home') {
				// 延迟加载，等待DOM渲染
				this.$nextTick(() => {
					this.loadPieChart();
				});
			}
			// 切换到主题管理时加载主题列表
			if (newVal === 'theme') {
				this.loadThemes();
			}
			// 切换到新闻管理时加载新闻列表
			if (newVal === 'news') {
				this.loadNews();
			}
			// 切换到评论管理时加载评论列表
			if (newVal === 'comment') {
				this.loadComments();
			}
			// 切换到个人信息时加载管理员和用户列表
			if (newVal === 'profile') {
				this.loadAdmins();
				this.loadUserList();
			}
			// 切换到公告管理时加载公告列表
			if (newVal === 'notice') {
				this.loadNotices();
			}
			// 切换到新闻脉冲时
			if (newVal === 'crawler') {
				this.loadThemes(); // 确保分类列表加载
				this.fetchCrawlerLogs();
				this.crawlerTimer = setInterval(this.fetchCrawlerLogs, 2000);
			} else {
				if (this.crawlerTimer) {
					clearInterval(this.crawlerTimer);
					this.crawlerTimer = null;
				}
			}
		},
		// 监听日期选择变化，按日期筛选新闻
		batchDeleteDate() {
			if (this.activeMenu === 'news') {
				this.newsPageNo = 1; // 重置页码
				this.loadNews();
			}
		}
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
		getMenuTitle() {
			const titles = {
				'theme': '主题管理',
				'news': '新闻管理',
				'notice': '系统公告',
				'comment': '评论管理',
				'profile': '个人信息',
				'crawler': '新闻脉冲'
			};
			return titles[this.activeMenu] || '';
		},
		loadStats() {
			// 加载统计数据 - 使用 /total/total 接口
			axios.get('http://localhost:6060/news/total/total')
				.then(res => {
					if (res.data) {
						this.stats.themes = res.data.themes || 0;
						this.stats.news = res.data.news || 0;
						this.stats.users = res.data.users || 0;
					}
				}).catch(err => console.log(err));
		},
		loadPieChart() {
			// 获取新闻主题分布数据
			axios.get('http://localhost:6060/news/total/newsByTheme')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						this.createChart(res.data);
					}
				}).catch(err => console.log(err));
		},
		createChart(data) {
			// 创建图表根元素
			let root = am5.Root.new(this.$refs.chartdiv);
			this.chartRoot = root;
			
			// 设置主题
			root.setThemes([am5themes_Animated.new(root)]);
			
			// 创建饼图
			let chart = root.container.children.push(am5percent.PieChart.new(root, {
				layout: root.verticalLayout
			}));
			
			// 创建数据系列
			let series = chart.series.push(am5percent.PieSeries.new(root, {
				valueField: "value",
				categoryField: "category"
			}));
			
			// 设置标签格式 - 显示名称和百分比
			series.labels.template.setAll({
				text: "{category}: {valuePercentTotal.formatNumber('0.00')}%",
				fontSize: 12
			});
			
			// 设置数据
			series.data.setAll(data);
			
			// 创建图例
			let legend = chart.children.push(am5.Legend.new(root, {
				centerX: am5.percent(50),
				x: am5.percent(50),
				marginTop: 15,
				marginBottom: 15
			}));
			legend.data.setAll(series.dataItems);
			
			// 播放动画
			series.appear(1000, 100);
		},
		// 加载主题列表
		loadThemes() {
			axios.get('http://localhost:6060/news/theme/get')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						this.themeList = res.data;
					}
				}).catch(err => console.log(err));
		},
		// 新增主题
		addTheme() {
			if (!this.newTheme.tname.trim()) {
				this.$message.warning('请输入主题名称');
				return;
			}
			axios.post('http://localhost:6060/news/theme/save', this.newTheme, {
				headers: { 'Content-Type': 'application/json' }
			}).then(() => {
				this.$message.success('新增成功');
				this.showAddTheme = false;
				this.newTheme.tname = '';
				this.loadThemes();
			}).catch(err => {
				console.log(err);
				this.$message.error('新增失败');
			});
		},
		// 删除主题
		deleteTheme(tid) {
			this.$confirm('确定要删除该主题吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				axios.delete('http://localhost:6060/news/theme/del/' + tid)
					.then(() => {
						this.$message.success('删除成功');
						this.loadThemes();
					}).catch(err => {
						console.log(err);
						this.$message.error('删除失败');
					});
			}).catch(() => {});
		},
		// 加载新闻列表（支持全量加载和按日期筛选）
		loadNews() {
			let url = 'http://localhost:6060/news/newsinfo/getAll';
			const params = {
				pageno: this.newsPageNo,
				pagesize: this.newsPageSize
			};

			if (this.batchDeleteDate) {
				url = 'http://localhost:6060/news/newsinfo/getByDate';
				params.date = this.batchDeleteDate;
			}

			axios.get(url, { params })
				.then(res => {
					if (res.data && res.data.list) {
						this.newsList = res.data.list;
						this.newsTotal = res.data.total || 0;
					}
				}).catch(err => console.log(err));
		},
		// 审核通过
		approveNews(nid) {
			axios.put('http://localhost:6060/news/newsinfo/checknews', null, {
				params: { nid: nid, status: 1 }
			}).then(() => {
				this.$message.success('审核通过');
				this.loadNews();
			}).catch(err => {
				console.log(err);
				this.$message.error('操作失败');
			});
		},
		// 撤回审核
		rejectNews(nid) {
			this.$confirm('确定要撤回该新闻的审核吗？撤回后将不在前端显示', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				axios.put('http://localhost:6060/news/newsinfo/checknews', null, {
					params: { nid: nid, status: 0 }
				}).then(() => {
					this.$message.success('已撤回');
					this.loadNews();
				}).catch(err => {
					console.log(err);
					this.$message.error('操作失败');
				});
			}).catch(() => {});
		},
		// 删除新闻
		deleteNews(nid) {
			this.$confirm('确定要删除该新闻吗？删除后无法恢复', '警告', {
				confirmButtonText: '确定删除',
				cancelButtonText: '取消',
				type: 'error'
			}).then(() => {
				axios.delete('http://localhost:6060/news/newsinfo/del/' + nid)
					.then(() => {
						this.$message.success('删除成功');
						this.loadNews();
					}).catch(err => {
						console.log(err);
						this.$message.error('删除失败');
					});
			}).catch(() => {});
		},
		// 预览批量删除 - 获取该日期新闻数量
		previewBatchDelete() {
			if (!this.batchDeleteDate) {
				this.$message.warning('请先选择日期');
				return;
			}
			axios.get('http://localhost:6060/news/newsinfo/countByDate', {
				params: { date: this.batchDeleteDate }
			}).then(res => {
				this.batchDeleteCount = res.data || 0;
				if (this.batchDeleteCount === 0) {
					this.$message.info('该日期没有新闻');
					return;
				}
				this.showBatchDeleteConfirm = true;
			}).catch(err => {
				console.log(err);
				this.$message.error('查询失败');
			});
		},
		// 确认批量删除
		confirmBatchDelete() {
			axios.delete('http://localhost:6060/news/newsinfo/deleteByDate', {
				params: { date: this.batchDeleteDate }
			}).then(res => {
				this.$message.success(`成功删除 ${res.data} 条新闻`);
				this.showBatchDeleteConfirm = false;
				this.batchDeleteDate = null;
				this.batchDeleteCount = 0;
				this.loadNews();
				this.loadStats(); // 刷新统计
			}).catch(err => {
				console.log(err);
				this.$message.error('删除失败');
			});
		},
		// 获取用户名
		getUserName(uid) {
			if (this.userMap[uid]) {
				return this.userMap[uid];
			}
			return 'UID:' + uid;
		},
		// 加载评论列表
		loadComments() {
			axios.get('http://localhost:6060/news/comment/get', {
				params: {
					pageno: this.commentPageNo,
					pagesize: this.commentPageSize
				}
			}).then(res => {
				if (res.data && res.data.list) {
					this.commentList = res.data.list;
					this.commentTotal = res.data.total || 0;
					// 加载用户信息
					this.loadUsers();
				}
			}).catch(err => console.log(err));
		},
		// 加载用户信息
		loadUsers() {
			axios.get('http://localhost:6060/news/users/get')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						let newUserMap = {};
						res.data.forEach(user => {
							newUserMap[user.uid] = user.username;
						});
						// 替换整个对象以触发响应式更新
						this.userMap = newUserMap;
					}
				}).catch(err => console.log(err));
		},
		// 审核通过评论
		approveComment(cid) {
			axios.put('http://localhost:6060/news/comment/check', null, {
				params: { cid: cid, status: 1 }
			}).then(() => {
				this.$message.success('审核通过');
				this.loadComments();
			}).catch(err => {
				console.log(err);
				this.$message.error('操作失败');
			});
		},
		// 撤回评论审核
		rejectComment(cid) {
			axios.put('http://localhost:6060/news/comment/check', null, {
				params: { cid: cid, status: 0 }
			}).then(() => {
				this.$message.success('已撤回');
				this.loadComments();
			}).catch(err => {
				console.log(err);
				this.$message.error('操作失败');
			});
		},
		// 删除评论
		deleteComment(cid) {
			this.$confirm('确定要删除该评论吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				axios.delete('http://localhost:6060/news/comment/del/' + cid)
					.then(() => {
						this.$message.success('删除成功');
						this.loadComments();
					}).catch(err => {
						console.log(err);
						this.$message.error('删除失败');
					});
			}).catch(() => {});
		},
		// 加载管理员列表
		loadAdmins() {
			axios.get('http://localhost:6060/news/admin/get')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						this.adminList = res.data;
					}
				}).catch(err => console.log(err));
		},
		// 审核通过管理员
		approveAdmin(aid) {
			axios.put('http://localhost:6060/news/admin/modifystatus', null, {
				params: { aid: aid, status: 1 }
			}).then(() => {
				this.$message.success('审核通过');
				this.loadAdmins();
			}).catch(err => {
				console.log(err);
				this.$message.error('操作失败');
			});
		},
		// 撤回管理员审核
		rejectAdmin(aid) {
			axios.put('http://localhost:6060/news/admin/modifystatus', null, {
				params: { aid: aid, status: 0 }
			}).then(() => {
				this.$message.success('已撤回审核');
				this.loadAdmins();
			}).catch(err => {
				console.log(err);
				this.$message.error('操作失败');
			});
		},
		// 删除管理员
		deleteAdmin(aid) {
			this.$confirm('确定要删除该管理员吗？', '警告', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				axios.delete('http://localhost:6060/news/admin/del/' + aid)
					.then(() => {
						this.$message.success('删除成功');
						this.loadAdmins();
					}).catch(err => {
						console.log(err);
						this.$message.error('删除失败');
					});
			}).catch(() => {});
		},
		// 加载用户列表
		loadUserList() {
			axios.get('http://localhost:6060/news/users/get')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						this.userList = res.data;
					}
				}).catch(err => console.log(err));
		},
		// 删除用户
		deleteUser(uid) {
			this.$confirm('确定要删除该用户吗？删除后该用户的评论和收藏也将受到影响', '警告', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				axios.delete('http://localhost:6060/news/users/del/' + uid)
					.then(() => {
						this.$message.success('删除成功');
						this.loadUserList();
					}).catch(err => {
						console.log(err);
						this.$message.error('删除失败');
					});
			}).catch(() => {});
		},
		// 加载公告列表
		loadNotices() {
			axios.get('http://localhost:6060/news/notice/get')
				.then(res => {
					if (res.data && Array.isArray(res.data)) {
						this.noticeList = res.data;
					}
				}).catch(err => console.log(err));
		},
		// 编辑公告
		editNotice(notice) {
			this.editingNotice = notice;
			this.noticeForm = {
				noid: notice.noid,
				content: notice.content,
				target: notice.target,
				visible: notice.visible,
				priority: notice.priority
			};
			this.showAddNotice = true;
		},
		// 提交公告（新增或编辑）
		submitNotice() {
			if (!this.noticeForm.content) {
				this.$message.warning('请输入公告内容');
				return;
			}
			if (this.editingNotice) {
				// 编辑
				axios.put('http://localhost:6060/news/notice/update', null, {
					params: {
						noid: this.noticeForm.noid,
						content: this.noticeForm.content,
						target: this.noticeForm.target,
						visible: this.noticeForm.visible,
						priority: this.noticeForm.priority
					}
				}).then(() => {
					this.$message.success('修改成功');
					this.showAddNotice = false;
					this.editingNotice = null;
					this.noticeForm = { content: '', target: 0, visible: 1, priority: 2 };
					this.loadNotices();
				}).catch(err => {
					console.log(err);
					this.$message.error('修改失败');
				});
			} else {
				// 新增
				axios.post('http://localhost:6060/news/notice/save', null, {
					params: {
						content: this.noticeForm.content,
						target: this.noticeForm.target,
						visible: this.noticeForm.visible,
						priority: this.noticeForm.priority
					}
				}).then(() => {
					this.$message.success('发布成功');
					this.showAddNotice = false;
					this.noticeForm = { content: '', target: 0, visible: 1, priority: 2 };
					this.loadNotices();
				}).catch(err => {
					console.log(err);
					this.$message.error('发布失败');
				});
			}
		},
		// 快速切换公告展示状态
		toggleNoticeVisible(notice) {
			axios.put('http://localhost:6060/news/notice/update', null, {
				params: {
					noid: notice.noid,
					content: notice.content,
					target: notice.target,
					visible: notice.visible,
					priority: notice.priority
				}
			}).then(() => {
				this.$message.success(notice.visible === 1 ? '已开启展示' : '已隐藏展示');
			}).catch(err => {
				console.log(err);
				this.$message.error('操作失败');
				// 回滚状态
				notice.visible = notice.visible === 1 ? 0 : 1;
			});
		},
		// 删除公告
		deleteNotice(noid) {
			this.$confirm('确定要删除该公告吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				axios.delete('http://localhost:6060/news/notice/del', {
					params: { noid: noid }
				}).then(() => {
					this.$message.success('删除成功');
					this.loadNotices();
				}).catch(err => {
					console.log(err);
					this.$message.error('删除失败');
				});
			}).catch(() => {});
		},
		// 新闻脉冲相关方法
		startCrawl() {
			this.$confirm('确定启动采集任务吗？', '提示', {
				confirmButtonText: '启动',
				cancelButtonText: '取消',
				type: 'primary'
			}).then(() => {
				axios.post('http://localhost:6060/news/crawler/run', {
					tid: this.crawlerForm.tid === 0 ? null : this.crawlerForm.tid,
					limit: this.crawlerForm.limit
				}).then(res => {
					this.$message.success(res.data);
					this.fetchCrawlerLogs();
				}).catch(err => {
					console.error(err);
					this.$message.error('启动失败');
				});
			}).catch(() => {});
		},
		fetchCrawlerLogs() {
			axios.get('http://localhost:6060/news/crawler/logs')
				.then(res => {
					this.crawlerLogs = res.data || [];
					this.$nextTick(() => {
						const container = this.$refs.terminalBody;
						if (container) {
							container.scrollTop = container.scrollHeight;
						}
					});
				}).catch(err => console.log(err));
			
			axios.get('http://localhost:6060/news/crawler/status')
				.then(res => {
					this.crawlerStatus = res.data;
				}).catch(err => console.log(err));
		},
		clearCrawlerLogs() {
			axios.post('http://localhost:6060/news/crawler/clear-logs')
				.then(() => {
					this.crawlerLogs = [];
					this.$message.success('日志已清空');
				}).catch(err => console.log(err));
		}
	}
}
</script>

<style scoped>
.admin-container {
	height: 100vh;
}

.admin-header {
	background-color: #304156;
	color: white;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 0 20px;
	position: relative;
}

.header-title {
	font-size: 20px;
	font-weight: bold;
	text-align: center;
}

.header-user {
	font-size: 14px;
	position: absolute;
	right: 20px;
}

.admin-aside {
	background-color: #304156;
}

.admin-menu {
	border-right: none;
	height: 100%;
}

.admin-main {
	background-color: #e9eef3;
	padding: 20px;
}

.stat-card {
	background: white;
	padding: 20px;
	border-radius: 4px;
	text-align: center;
}

.chart-container {
	background: white;
	padding: 20px;
	border-radius: 4px;
	margin-top: 20px;
}

.chart-container h3 {
	margin: 0 0 15px 0;
	color: #333;
	text-align: left;
	font-size: 14px;
	font-weight: normal;
}

#chartdiv {
	width: 100%;
	height: 400px;
}

.content-card {
	background: white;
	padding: 20px;
	border-radius: 4px;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
	font-size: 14px;
	color: #333;
}

.content-placeholder {
	background: white;
	padding: 40px;
	border-radius: 4px;
	text-align: center;
	color: #666;
}

.news-pagination {
	margin-top: 15px;
	text-align: center;
}

.section-title {
	font-size: 16px;
	font-weight: 600;
	color: #303133;
	margin: 20px 0 15px 0;
	padding-bottom: 10px;
	border-bottom: 1px solid #ebeef5;
}

.section-title i {
	color: #409EFF;
	margin-right: 8px;
}

.profile-tabs {
	display: flex;
	gap: 0;
	margin-bottom: 20px;
	border-bottom: 2px solid #e4e7ed;
}

.profile-tab {
	padding: 12px 24px;
	cursor: pointer;
	font-size: 14px;
	color: #606266;
	border-bottom: 2px solid transparent;
	margin-bottom: -2px;
	transition: all 0.3s;
}

.profile-tab:hover {
	color: #409EFF;
}

.profile-tab.active {
	color: #409EFF;
	border-bottom-color: #409EFF;
	font-weight: 600;
}

.profile-tab i {
	margin-right: 6px;
}

/* 爬虫脉冲样式 */
.crawler-controls {
	background: #f8f9fb;
	padding: 20px;
	border-radius: 8px;
	border: 1px solid #ebeef5;
}

.control-title {
	margin: 0 0 20px 0;
	color: #303133;
	font-size: 15px;
	display: flex;
	align-items: center;
}

.control-title::before {
	content: '';
	width: 4px;
	height: 16px;
	background: #409EFF;
	margin-right: 10px;
	border-radius: 2px;
}

.crawler-tips {
	margin-top: 20px;
	padding: 12px;
	background: #ecf5ff;
	border-radius: 4px;
	color: #409EFF;
	font-size: 12px;
	line-height: 1.6;
	display: flex;
	gap: 8px;
}

.crawler-terminal {
	background: #1e1e1e;
	border-radius: 8px;
	box-shadow: 0 10px 30px rgba(0,0,0,0.3);
	overflow: hidden;
}

.terminal-header {
	background: #333;
	padding: 10px 15px;
	display: flex;
	align-items: center;
	gap: 8px;
}

.dot {
	width: 12px;
	height: 12px;
	border-radius: 50%;
}
.dot.red { background: #ff5f56; }
.dot.yellow { background: #ffbd2e; }
.dot.green { background: #27c93f; }

.terminal-title {
	color: #999;
	font-size: 12px;
	margin-left: 10px;
	font-family: 'Courier New', Courier, monospace;
}

.terminal-body {
	height: 400px;
	padding: 15px;
	overflow-y: auto;
	font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
	font-size: 13px;
	line-height: 1.5;
	color: #d4d4d4;
	background: #1e1e1e;
}

.log-line {
	margin-bottom: 4px;
	white-space: pre-wrap;
	word-break: break-all;
}

.log-empty {
	color: #555;
	text-align: center;
	margin-top: 150px;
	font-style: italic;
}

.log-success { color: #4ec9b0; }
.log-error { color: #f44336; }

/* 滚动条美化 */
.terminal-body::-webkit-scrollbar {
	width: 8px;
}
.terminal-body::-webkit-scrollbar-thumb {
	background: #444;
	border-radius: 4px;
}
.terminal-body::-webkit-scrollbar-track {
	background: #222;
}
</style>
