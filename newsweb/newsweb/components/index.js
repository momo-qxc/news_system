let pageobj = {
	pageno: 1,
	pagesize: 25,
	totalPages: 1,
	currentTid: -1, // 当前选中的分类ID，-1表示全部
	isCollectionMode: false, // 是否为"我的收藏"模式
	cnt: 60,
	myinterl: null,
	init: function () {
		pageobj.loadthemes();
		pageobj.loadnews(-1);
		pageobj.loadinternal(1, 0)
		pageobj.loadinternal(2, 1);
		pageobj.loadinternal(4, 2);
		loadpage();
		pageobj.loadNotices();
		$(".login_sub").click(function () {
			pageobj.login();
		})

		$("#getcode").click(function () {

			pageobj.getcode();
		})

		$("#exit").click(function () {
			sessionStorage.removeItem("cuser");
			location.reload();
		});

		// 我的收藏点击事件
		$("#myCollection").click(function () {
			let phone = sessionStorage.getItem("cuser");
			if (phone == null) {
				alert("请先登录");
				return false;
			}
			pageobj.isCollectionMode = true;
			pageobj.pageno = 1;
			pageobj.currentTid = -1;
			pageobj.loadnews(-1);
			return false;
		});

		// 分页按钮点击事件
		$("#firstPage").click(function () {
			if (pageobj.pageno > 1) {
				pageobj.pageno = 1;
				pageobj.loadnews(pageobj.currentTid);
			}
			return false;
		});

		$("#prevPage").click(function () {
			if (pageobj.pageno > 1) {
				pageobj.pageno--;
				pageobj.loadnews(pageobj.currentTid);
			}
			return false;
		});

		$("#nextPage").click(function () {
			if (pageobj.pageno < pageobj.totalPages) {
				pageobj.pageno++;
				pageobj.loadnews(pageobj.currentTid);
			}
			return false;
		});

		$("#lastPage").click(function () {
			if (pageobj.pageno < pageobj.totalPages) {
				pageobj.pageno = pageobj.totalPages;
				pageobj.loadnews(pageobj.currentTid);
			}
			return false;
		});
	},

	loadthemes: function () {
		$.get(service_path + "/news/theme/get", function (data) {
			let obj = $.parseJSON(data);
			let result = "";
			for (let s of obj) {
				result += "<a href='#' onclick='pageobj.selectCategory(" + s.tid + ");return false;'><b>" + s.tname + "</b></a>";
			}
			// 添加"全部"选项
			result += "<a href='#' onclick='pageobj.selectCategory(-1);return false;'><b>全部</b></a>";
			$("#class_month").html(result);
		})
	},

	// 选择分类（可能在普通模式或收藏模式下）
	selectCategory: function (tid) {
		// 如果不是收藏模式，切换回普通模式
		if (!pageobj.isCollectionMode) {
			// 切换分类时，保留搜索框内容的显示，loadnews会根据是否有字自动决定是"该分类全部"还是"该分类搜索"
			pageobj.loadnews(tid);
		} else {
			// 收藏模式下切换分类
			if (tid !== pageobj.currentTid) {
				pageobj.pageno = 1;
				pageobj.currentTid = tid;
			}
			pageobj.loadnews(tid);
		}
	},

	loadnews: function (tid) {
		// 如果切换了分类，重置页码为1
		if (tid !== pageobj.currentTid) {
			pageobj.pageno = 1;
			pageobj.currentTid = tid;
		}

		let url = "";
		let phone = sessionStorage.getItem("cuser");

		if (pageobj.isCollectionMode && phone) {
			// 收藏模式：调用收藏相关的API
			if (tid == -1) {
				url = service_path + "/news/collection/getbyphone?phone=" + phone + "&pageno=" + pageobj.pageno + "&pagesize=" + pageobj.pagesize;
			} else {
				url = service_path + "/news/collection/getbyphoneandtid?phone=" + phone + "&tid=" + tid + "&pageno=" + pageobj.pageno + "&pagesize=" + pageobj.pagesize;
			}
		} else {
			// 普通模式：调用新闻列表API
			pageobj.isCollectionMode = false; // 确保退出收藏模式

			// 检查是否有搜索关键词
			let searchInputEl = $("#searchInput");
			let keyword = searchInputEl.length > 0 ? searchInputEl.val().trim() : "";

			if (keyword) {
				// 有搜索词：执行搜索逻辑
				if (tid == -1) {
					// 全局搜索
					url = service_path + "/news/newsinfo/getnewsbykeyword?pageno=" + pageobj.pageno + "&pagesize=" + pageobj.pagesize + "&keyword=" + keyword;
				} else {
					// 分类内搜索
					url = service_path + "/news/newsinfo/getnewsbytidandkeyword?pageno=" + pageobj.pageno + "&pagesize=" + pageobj.pagesize + "&tid=" + tid + "&keyword=" + keyword;
				}
			} else {
				// 无搜索词：执行普通列表逻辑
				if (tid == -1) {
					url = service_path + "/news/newsinfo/get?pageno=" + pageobj.pageno + "&pagesize=" + pageobj.pagesize;
				} else {
					url = service_path + "/news/newsinfo/getbytid?pageno=" + pageobj.pageno + "&pagesize=" + pageobj.pagesize + "&tid=" + tid;
				}
			}
		}

		$.get(url, function (data) {
			let obj = $.parseJSON(data);
			let result = "";
			if (obj.list && obj.list.length > 0) {
				for (let i = 0; i < obj.list.length; i++) {
					result += "<li><a href='newsinfo.html?nid=" + obj.list[i].nid + "'>" + obj.list[i].ntitle + "</a><span>" +
						obj.list[i].createdate + "</span></li>";
				}
			} else {
				result = "<li style='color:#999; padding:20px; text-align:center;'>暂无数据</li>";
			}
			$(".classlist").html(result);

			// 更新分页信息
			pageobj.totalPages = obj.totalpage || 1;
			$("#currentPage").text(pageobj.pageno);
			$("#totalPages").text(pageobj.totalPages);
			$("#pagination").show(); // 确保分页栏显示
		})
	},
	// ... loadinternal omitted ...

	// ... (omitting middle functions which I am not editing to avoid context issues but I must be careful with replace_file_content logic if I skip them. Wait, I should not skip huge blocks if I can help it, or I should use MULTIPLE replacements if I want to skip the middle.
	// Actually, I can just target the specific blocks. The "loadinternal" and others are in the middle.
	// I will split this into multiple replacement chunks using multi_replace_file_content ideally, or just do one replace if the range is contiguous.
	// But here I'm modifying selectCategory (lines 93-105) and loadnews (lines 107-160). These are contiguous!
	// AND I am modifying initSearch (260+) and searchNews (281+). These are separated by loadinternal etc.
	// So distinct tools calls or multi_replace is needed.
	// I will use multi_replace for safety and clarity.
	// ... loadinternal omitted in edit but kept in file via context ...

	loadinternal: function (tid, cnt) {
		$.get(service_path + "/news/newsinfo/getbytid?pageno=1&pagesize=5&tid=" + tid, function (data) {
			let obj = $.parseJSON(data);
			let result = "<marquee direction='up' scrollamount='2' height='240'><ul>";
			for (let i = 0; i < obj.list.length; i++) {
				if (i != 0 && i % 5 == 0) {
					result += "<li class='space'></li>";
				}
				result += " <li> <a href='newsinfo.html?nid=" + obj.list[i].nid + "'><b> " + obj.list[i].ntitle + " </b></a> </li>";
			}
			result += "</ul></marquee>";
			$(".side_list").eq(cnt).html(result);
		})
	},

	login: function () {
		console.log("Login called");
		$.post(service_path + "/news/users/login", {
			"phone": $("[name=phone]").val(),
			"code": $("[name=code]").val()
		}).done(function (data) {
			console.log("Login response:", data, typeof data);
			if (data == "true" || data === true) {
				sessionStorage.setItem("cuser", $("[name=phone]").val());
				location.reload();
			} else {
				alert("登录验证失败，请重试");
			}
		}).fail(function (jqXHR, textStatus, errorThrown) {
			console.log("Login failed:", textStatus, errorThrown);
			alert("登录请求失败: " + textStatus);
		});
	},
	getcode: function () {
		// 倒计时进行中，禁止重复发送
		if (pageobj.cnt < 60) {
			alert("验证码已发送，请勿频繁操作");
			return;
		}

		let val = $("[name=phone]").val();
		if (val.length == 0) {
			alert('请输入手机号码');
		} else {
			pageobj.myinterl = window.setInterval("pageobj.setcnt()", 1000);
			$.get(service_path + "/news/users/getcode?phone=" + val, function (data) {
				console.log('test code');
			});
		}
	},
	setcnt: function () {

		$("#getcode").text("重新发送(" + pageobj.cnt + ")");
		pageobj.cnt--;
		if (pageobj.cnt == 0) {
			window.clearInterval(pageobj.myinterl);
			$("#getcode").text("发送验证码");
			pageobj.cnt = 60;
		}

	},
	// 加载公告（target=0为用户端公告，target=2为所有人公告）
	loadNotices: function () {
		$.ajax({
			url: service_path + "/news/notice/getbytarget",
			type: "GET",
			data: { target: 0 },
			dataType: "json",
			success: function (result) {
				if (result && result.length > 0) {
					let noticeTexts = result.map(n => n.content).join("　　　　");
					let $content = $("#notice-content");
					let $bar = $("#notice-bar");

					$content.html(noticeTexts);
					$bar.show();

					// 检查内容是否超出容器宽度
					setTimeout(function () {
						let contentWidth = $content[0].scrollWidth;
						let wrapperWidth = $("#notice-wrapper").width();

						if (contentWidth > wrapperWidth) {
							// 内容超长，添加滚动动画
							let duration = contentWidth / 50; // 速度
							$content.css({
								"animation": "scrollNotice " + duration + "s linear infinite"
							});
						}
					}, 100);
				}
			}
		});
	},


	// 搜索功能
	initSearch: function () {
		$("#searchBtn").click(function () {
			pageobj.searchNews();
		});

		// 支持回车搜索
		$("#searchInput").keypress(function (e) {
			if (e.which == 13) {
				pageobj.searchNews();
			}
		});

		// 监听输入框变空时恢复列表
		$("#searchInput").on("input", function () {
			if ($(this).val().trim() === "") {
				$("#pagination").show(); // 恢复分页显示
				// 清空时，重新加载当前分类的全部新闻（即取消搜索过滤）
				pageobj.loadnews(pageobj.currentTid);
			}
		});
	},

	searchNews: function () {
		let keyword = $("#searchInput").val().trim();
		if (!keyword) {
			// 如果搜索框为空，显示当前分类的全部新闻
			pageobj.pageno = 1;
			pageobj.loadnews(pageobj.currentTid);
			return;
		}
		// 在当前分类下搜索（如果 currentTid 为 -1 则是全局搜索）
		pageobj.pageno = 1;
		pageobj.loadnews(pageobj.currentTid);
	}
}
$(function () {
	pageobj.init();
	pageobj.initSearch();
});

