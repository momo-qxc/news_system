<template>
	<div class="home">
		<img src="@/assets/images/a_b01.gif" class="banner" />
		<h2 class="title">新闻聚合平台--{{ isRegister ? '管理员注册入口' : '管理员登录入口' }}</h2>

		<div class="login-card">
			<h1 class="login-title">{{ isRegister ? '管理员注册' : '管理员登录' }}</h1>
			<el-form label-position="right" label-width="80px" :model="formLabelAlign" class="login-form">
				<el-form-item label="账号">
					<el-input v-model="formLabelAlign.name" placeholder="请输入账号"></el-input>
				</el-form-item>
				<el-form-item label="密码">
					<el-input v-model="formLabelAlign.password" type="password" placeholder="请输入密码"></el-input>
				</el-form-item>
				<el-form-item label="用户类型">
					<el-radio-group v-model="formLabelAlign.userType">
						<el-radio label="1">超级管理员</el-radio>
						<el-radio label="0">普通管理员</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item>
					<el-button v-if="!isRegister" type="primary" class="login-btn" @click="login">立即登录</el-button>
					<el-button v-else type="success" class="login-btn" @click="register">立即注册</el-button>
				</el-form-item>
				<el-form-item>
					<div class="switch-mode">
						<span v-if="!isRegister">还没有账号？<el-link type="primary" @click="isRegister = true">立即注册</el-link></span>
						<span v-else>已有账号？<el-link type="primary" @click="isRegister = false">返回登录</el-link></span>
					</div>
				</el-form-item>
			</el-form>
		</div>
	</div>
</template>

<script>
	import axios from 'axios';

	export default {
		name: 'HomeView',
		data() {
			return {
				isRegister: false,
				formLabelAlign: {
					name: '',
					password: '',
					userType: '1'
				}
			};
		},
		methods: {
			login() {
				let $vue = this;
				if (!$vue.formLabelAlign.name || !$vue.formLabelAlign.password) {
					$vue.$message.warning('请输入账号和密码');
					return;
				}
				
				const params = new URLSearchParams();
				params.append('adminame', $vue.formLabelAlign.name);
				params.append('pwd', $vue.formLabelAlign.password);
				params.append('isadmin', $vue.formLabelAlign.userType);
				
				axios.post('http://localhost:6060/news/admin/login', params, {
						headers: {
							"Content-Type": "application/x-www-form-urlencoded"
						}
					})
					.then((response) => {
						console.log(response);
						if (response.data === 1) {
							$vue.$message.success('登录成功');
							// 保存登录状态
							sessionStorage.setItem('adminUser', $vue.formLabelAlign.name);
							sessionStorage.setItem('adminType', $vue.formLabelAlign.userType);
							// 跳转到管理页面
							if ($vue.formLabelAlign.userType == 1) {
								$vue.$router.push('/admin');
							} else {
								$vue.$router.push('/customer');
							}
						} else if (response.data === 2) {
							$vue.$message.warning('账号暂未通过审核，请等待超级管理员审核');
						} else {
							$vue.$message.error('登录失败，请检查账号和密码');
						}
					})
					.catch((error) => {
						console.log(error);
						$vue.$message.error('登录请求失败: ' + error);
					});
			},
			register() {
				let $vue = this;
				if (!$vue.formLabelAlign.name || !$vue.formLabelAlign.password) {
					$vue.$message.warning('请输入账号和密码');
					return;
				}
				
				const params = new URLSearchParams();
				params.append('adminame', $vue.formLabelAlign.name);
				params.append('pwd', $vue.formLabelAlign.password);
				params.append('isadmin', $vue.formLabelAlign.userType);
				
				axios.post('http://localhost:6060/news/admin/regist', params, {
						headers: {
							"Content-Type": "application/x-www-form-urlencoded"
						}
					})
					.then((response) => {
						console.log(response);
						if (response.data === true) {
							$vue.$message.success('注册成功，请登录');
							$vue.isRegister = false;
							$vue.formLabelAlign.password = '';
						} else {
							$vue.$message.error('用户名已存在，请更换用户名');
						}
					})
					.catch((error) => {
						console.log(error);
						$vue.$message.error('注册失败: ' + error);
					});
			}
		}
	}
</script>

<style scoped>
	.home {
		text-align: center;
		padding: 20px;
	}

	.banner {
		max-width: 100%;
		height: auto;
	}

	.title {
		color: #333;
		margin: 20px 0;
	}

	.login-card {
		max-width: 400px;
		margin: 30px auto;
		padding: 30px;
		background: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
	}

	.login-title {
		color: #409EFF;
		margin-bottom: 30px;
	}

	.login-form {
		text-align: left;
	}

	.login-btn {
		width: 100%;
	}

	.switch-mode {
		text-align: center;
		width: 100%;
	}
</style>