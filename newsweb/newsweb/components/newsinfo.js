let newsinfoobj = {
    currentNid: null,
    currentColid: null,
    currentUid: null,
    replyTo: null,
    cnt: 60,
    myinterl: null,

    // 分页相关状态
    allComments: [], // 存储所有评论
    currentPage: 1,
    pageSize: 5, // 每页显示 5 条根评论

    init: function () {
        // ... (Same as before: login check, buttons, etc.) ...
        // 检查登录状态
        let phone = sessionStorage.getItem("cuser");
        if (phone) {
            $("#currentuser").text(phone);
            $("#top_login").hide();
            $("#top_login2").show();

            // 获取当前用户的 uid
            $.get(service_path + "/news/users/getbyphone?phone=" + phone, function (data) {
                let user = typeof data === "string" ? JSON.parse(data) : data;
                if (user && user.uid) {
                    newsinfoobj.currentUid = user.uid;
                }
            });
        } else {
            $("#top_login").show();
            $("#top_login2").hide();
        }

        // 退出按钮
        $("#exit").click(function () {
            sessionStorage.clear();
            location.href = "index.html";
        });

        // 验证码按钮
        $("#getcode").click(function () {
            newsinfoobj.getcode();
        });

        // 登录按钮
        $(".login_sub").click(function () {
            newsinfoobj.login();
        });

        // 从 URL 获取 nid 参数
        let nid = newsinfoobj.getUrlParam("nid");
        newsinfoobj.currentNid = nid;

        if (nid) {
            newsinfoobj.loadNews(nid);
        } else {
            $("#newsTitle").text("未找到新闻");
            $("#newsContent").text("请从首页选择一条新闻查看");
        }

        // 绑定点赞按钮
        $("#likeBtn").click(function () {
            newsinfoobj.handleLike();
        });

        // 绑定收藏按钮
        $("#collectBtn").click(function () {
            newsinfoobj.handleCollect();
        });

        // 绑定评论提交
        $("#submitComment").click(function () {
            newsinfoobj.submitComment();
        });
    },

    // 获取 URL 参数
    getUrlParam: function (name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        let r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURIComponent(r[2]);
        return null;
    },

    // 加载新闻详情
    loadNews: function (nid) {
        $.get(service_path + "/news/newsinfo/getone", { nid: nid }, function (data) {
            // ... (Same as before) ...
            let news = typeof data === "string" ? JSON.parse(data) : data;

            if (news && news.ntitle) {
                $("#newsTitle").text(news.ntitle);
                $("#newsContent").html(news.content || "暂无内容");
                $("#newsAuthor").text(news.author || "");
                $("#newsDate").text(news.createdate || "");
                document.title = news.ntitle + " - 新闻中国";
                newsinfoobj.checkIfCollected(nid);
                newsinfoobj.checkIfLiked(nid);
                newsinfoobj.saveHistory(nid);
            } else {
                $("#newsTitle").text("新闻不存在");
            }
        });

        // 加载评论列表
        newsinfoobj.loadComments(nid);
    },

    saveHistory: function (nid) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) return;
        $.post(service_path + "/news/history/save", {
            phone: phone,
            nid: nid,
            createdate: new Date().toLocaleString()
        });
    },

    handleLike: function () {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) { alert("请先登录后再点赞"); return; }
        // ... (Same logic) ... 简化：假设逻辑不变
        let $btn = $("#likeBtn");
        let nid = newsinfoobj.currentNid;
        $.post(service_path + "/news/like/toggle", { phone: phone, nid: nid }, function (data) {
            let result = typeof data === "string" ? JSON.parse(data) : data;
            if (result.liked) {
                $btn.addClass("active");
            } else {
                $btn.removeClass("active");
            }
        });
    },

    checkIfLiked: function (nid) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) return;
        $.get(service_path + "/news/like/check", { phone: phone, nid: nid }, function (data) {
            let result = typeof data === "string" ? JSON.parse(data) : data;
            if (result.liked) {
                $("#likeBtn").addClass("active");
            } else {
                $("#likeBtn").removeClass("active");
            }
        });
    },

    handleCollect: function () {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) { alert("用户未登录，请登录"); return; }
        let $btn = $("#collectBtn");
        let nid = newsinfoobj.currentNid;

        if ($btn.hasClass("active")) {
            let colid = newsinfoobj.currentColid;
            if (colid) {
                $.ajax({
                    url: service_path + "/news/collection/del/" + colid,
                    type: "DELETE",
                    success: function () {
                        $btn.removeClass("active");
                        newsinfoobj.currentColid = null;
                        alert("已取消收藏");
                    }
                });
            }
        } else {
            $.post(service_path + "/news/collection/save", { phone: phone, nid: nid, createdate: new Date().toLocaleString() }, function (data) {
                $btn.addClass("active");
                alert("收藏成功！");
                newsinfoobj.checkIfCollected(nid);
            });
        }
    },

    checkIfCollected: function (nid) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) return;
        $.get(service_path + "/news/collection/getdetail", { phone: phone }, function (data) {
            let list = typeof data === "string" ? JSON.parse(data) : data;
            if (list && list.length > 0) {
                for (let i = 0; i < list.length; i++) {
                    if (list[i].nid == nid) {
                        $("#collectBtn").addClass("active");
                        newsinfoobj.currentColid = list[i].colid;
                        break;
                    }
                }
            }
        });
    },

    submitComment: function () {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) { alert("请先登录后再评论"); return; }
        let content = $("#commentInput").val().trim();
        if (!content) { alert("请输入评论内容"); return; }
        let nid = newsinfoobj.currentNid;
        let isAnonymous = $("#anonymousCheck").is(":checked");
        let postData = { phone: phone, nid: nid, content: content, createdate: new Date().toLocaleString(), anonymous: isAnonymous };
        if (newsinfoobj.replyTo) { postData.pid = newsinfoobj.replyTo.cid; }
        $.post(service_path + "/news/comment/save", postData, function (data) {
            let result = typeof data === "string" ? JSON.parse(data) : data;
            if (result && result.success === false) {
                // 敏感词检测失败
                alert(result.message || "评论包含违禁词，请修改后重新提交");
                return;
            }
            alert(newsinfoobj.replyTo ? "回复成功！" : "评论成功！");
            $("#commentInput").val("");
            $("#anonymousCheck").prop("checked", false);
            newsinfoobj.replyTo = null;
            $("#submitComment").text("提交");
            // 提交后重置到第一页并重新加载
            newsinfoobj.currentPage = 1;
            newsinfoobj.loadComments(nid);
        });
    },

    prepareReply: function (cid, username) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) { alert("请先登录后操作"); return; }
        newsinfoobj.replyTo = { cid: cid, username: username };
        $("#commentInput").val("@" + username + " ").focus();
        $("#submitComment").text("回复");
        // 滚动到输入框
        $('html, body').animate({
            scrollTop: $(".comment-input-wrapper").offset().top - 100
        }, 500);
    },

    flattenReplies: function (comment, targetList) {
        if (comment.replyList && comment.replyList.length > 0) {
            for (let i = 0; i < comment.replyList.length; i++) {
                let reply = comment.replyList[i];
                reply.replyToUsername = comment.username || "匿名用户";
                targetList.push(reply);
                newsinfoobj.flattenReplies(reply, targetList);
            }
        }
    },

    toggleCommentLike: function (cid, isLiked, $icon, $count) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) { alert("请先登录后点赞"); return; }
        let url = isLiked ? "/news/comment/unlike" : "/news/comment/like";
        $.post(service_path + url, { cid: cid, phone: phone }, function () {
            let newIsLiked = !isLiked;
            let currentCount = parseInt($count.text()) || 0;
            let newCount = newIsLiked ? currentCount + 1 : currentCount - 1;
            $count.text(newCount);
            if (newIsLiked) $icon.css("color", "#ff4d4f"); // Red for heart
            else $icon.css("color", "#ccc");
            $icon.parent().attr("onclick", "newsinfoobj.toggleCommentLike(" + cid + ", " + newIsLiked + ", $(this).find('span.heart-icon'), $(this).find('span.like-count'))");
        });
    },

    // ---------------------- 分页与加载逻辑优化 ----------------------

    // 加载评论数据（只获取数据，渲染交给 renderPage）
    loadComments: function (nid) {
        let phone = sessionStorage.getItem("cuser") || "";
        $.get(service_path + "/news/comment/getbynid", { nid: nid, phone: phone }, function (data) {
            let list = typeof data === "string" ? JSON.parse(data) : data;

            // 保存所有评论数据
            newsinfoobj.allComments = list || [];

            // 获取当前用户的 uid (如果未获取)
            if (phone && !newsinfoobj.currentUid) {
                $.get(service_path + "/news/users/getbyphone?phone=" + phone, function (userData) {
                    let user = typeof userData === "string" ? JSON.parse(userData) : userData;
                    if (user && user.uid) newsinfoobj.currentUid = user.uid;
                    // uid 获取后重新渲染一遍以显示删除按钮
                    newsinfoobj.renderPage(newsinfoobj.currentPage);
                });
            } else {
                newsinfoobj.renderPage(newsinfoobj.currentPage);
            }

        }).fail(function (err) {
            console.log("Load comments failed:", err);
            $("#commentList").html('<div style="text-align:center; padding:20px; color:#999;">加载评论失败</div>');
        });
    },

    // 渲染指定页码的评论
    renderPage: function (page) {
        let list = newsinfoobj.allComments;
        if (!list || list.length === 0) {
            $("#commentList").html('<div style="color:#969799; text-align:center; padding:40px; font-size:14px;">暂无评论，快来发表第一篇评论吧 ~</div>');
            $("#pagination").html("");
            return;
        }

        // 计算分页切片
        let startIndex = (page - 1) * newsinfoobj.pageSize;
        let endIndex = Math.min(startIndex + newsinfoobj.pageSize, list.length);
        let pageItems = list.slice(startIndex, endIndex);

        let html = "";
        for (let i = 0; i < pageItems.length; i++) {
            let rootComment = pageItems[i];
            html += '<div class="comment-thread">';
            // 1. 渲染根评论
            html += newsinfoobj.renderCommentItem(rootComment, false);

            // 2. 铺平所有子回复 (楼中楼) - 这里全部渲染，不分页，符合用户要求 "必须把它显示完"
            let flatReplies = [];
            newsinfoobj.flattenReplies(rootComment, flatReplies);

            if (flatReplies.length > 0) {
                html += '<div class="reply-list">';
                for (let reply of flatReplies) {
                    html += newsinfoobj.renderCommentItem(reply, true);
                }
                html += '</div>';
            }
            html += '</div>';
        }

        $("#commentList").html(html);

        // 渲染分页控件
        newsinfoobj.renderPaginationControls();

        // 滚动到评论区顶部 (可选体验优化)
        if (page > 1) { // 只有翻页时滚动
            $('html, body').animate({
                scrollTop: $(".comment-section").comments - 50
            }, 300);
        }
    },

    // 渲染分页控件按钮
    renderPaginationControls: function () {
        let total = newsinfoobj.allComments.length;
        if (total === 0) return;

        let totalPage = Math.ceil(total / newsinfoobj.pageSize);
        if (totalPage <= 1) {
            $("#pagination").html(""); // 只有一页时不显示分页
            return;
        }

        let current = newsinfoobj.currentPage;
        let html = "";

        // 上一页
        if (current > 1) {
            html += '<button class="page-btn" onclick="newsinfoobj.goToPage(' + (current - 1) + ')">上一页</button>';
        } else {
            html += '<button class="page-btn disabled">上一页</button>';
        }

        // 页码 (简单实现：显示 1..totalPage，如果页数太多可以优化显示逻辑)
        // 这里为了简单展示，只显示当前页和总页数信息，或者简单的几个数字
        // 优化：显示 1, ..., current-1, current, current+1, ..., total

        for (let i = 1; i <= totalPage; i++) {
            // 简单策略：显示所有页码，如果超过 10 页可能需要折叠，暂时假设不会太多
            if (i == current) {
                html += '<button class="page-btn active">' + i + '</button>';
            } else {
                html += '<button class="page-btn" onclick="newsinfoobj.goToPage(' + i + ')">' + i + '</button>';
            }
        }

        // 下一页
        if (current < totalPage) {
            html += '<button class="page-btn" onclick="newsinfoobj.goToPage(' + (current + 1) + ')">下一页</button>';
        } else {
            html += '<button class="page-btn disabled">下一页</button>';
        }

        $("#pagination").html(html);
    },

    goToPage: function (page) {
        newsinfoobj.currentPage = page;
        newsinfoobj.renderPage(page);
    },

    renderCommentItem: function (comment, isReply) {
        let statusTag = (comment.status === 0) ? ' <span style="color:#f90; font-weight:normal; font-size:12px;">(待审核)</span>' : '';
        let heartColor = comment.isLiked ? "#e60012" : "#a8abb2"; // Heart is always red when active
        let likeCount = comment.likeCount || 0;

        let userHtml = '<span class="comment-user">' + (comment.username || "匿名用户") + '</span>';
        if (isReply && comment.replyToUsername) {
            userHtml += '<span style="color:#999; margin:0 6px; font-size:12px;">回复</span>';
            userHtml += '<span class="comment-user">' + comment.replyToUsername + '</span>';
        }
        userHtml += statusTag;

        let html = '<div class="comment-item">';
        // ... (Same rendering layout) ...
        html += '  <div style="display: flex; justify-content: space-between; align-items: flex-start;">';
        html += '    <div>' + userHtml + '</div>';

        html += '    <div style="display: flex; align-items: center; gap: 16px;">';
        // 注意：点赞图标颜色逻辑：未点赞灰色，点赞红色 (#ff4d4f)
        html += '      <span style="cursor:pointer; display:flex; align-items:center; gap:4px; font-size: 13px; color: #606266;" onclick="newsinfoobj.toggleCommentLike(' + comment.cid + ', ' + comment.isLiked + ', $(this).find(\'.heart-icon\'), $(this).find(\'.like-count\'))">';
        html += '        <span class="heart-icon" style="color:' + heartColor + '; font-size: 18px;">❤</span>';
        html += '        <span class="like-count">' + likeCount + '</span>';
        html += '      </span>';
        html += '      <span style="cursor:pointer; font-size: 13px; color: #0066cc;" onclick="newsinfoobj.prepareReply(' + comment.cid + ', \'' + (comment.username || "匿名用户") + '\')">回复</span>';
        html += '    </div>';
        html += '  </div>';

        html += '  <div class="comment-content">' + comment.content + '</div>';
        html += '  <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 5px;">';
        html += '    <span style="color:#999; font-size:12px;">' + (comment.createdate || "") + '</span>';

        if (comment.uid && newsinfoobj.currentUid && comment.uid === newsinfoobj.currentUid) {
            html += '    <span class="comment-delete" onclick="newsinfoobj.deleteComment(' + comment.cid + ')" style="color:#999; font-size:12px; cursor:pointer;">删除</span>';
        }
        html += '  </div>';
        html += '</div>';
        return html;
    },

    deleteComment: function (cid) {
        if (!confirm("确定要删除这条评论吗？")) return;
        $.ajax({
            url: service_path + "/news/comment/del/" + cid,
            type: "DELETE",
            success: function () {
                alert("删除成功");
                // 重新加载所有数据，保持在当前页（如果当前页变空了一整页则需要减页码，这里暂简化）
                newsinfoobj.loadComments(newsinfoobj.currentNid);
            }
        });
    },

    getcode: function () {
        if (newsinfoobj.cnt < 60) { alert("验证码已发送，请勿频繁操作"); return; }
        let val = $("[name=phone]").val();
        if (val.length == 0) { alert('请输入手机号码'); } else {
            newsinfoobj.myinterl = window.setInterval("newsinfoobj.setcnt()", 1000);
            $.get(service_path + "/news/users/getcode?phone=" + val);
        }
    },

    setcnt: function () {
        $("#getcode").text("重新发送(" + newsinfoobj.cnt + ")");
        newsinfoobj.cnt--;
        if (newsinfoobj.cnt == 0) {
            window.clearInterval(newsinfoobj.myinterl);
            $("#getcode").text("发送验证码");
            newsinfoobj.cnt = 60;
        }
    },

    login: function () {
        $.post(service_path + "/news/users/login", { "phone": $("[name=phone]").val(), "code": $("[name=code]").val() })
            .done(function (data) {
                let result = typeof data === "string" ? (data === "true" || data === "TRUE") : data;
                if (result === true || result === "true" || result === "TRUE") {
                    sessionStorage.setItem("cuser", $("[name=phone]").val());
                    location.reload();
                } else { alert("登录失败，请检查手机号和验证码"); }
            });
    }
};

$(newsinfoobj.init);
