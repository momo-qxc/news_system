let newsinfoobj = {
    currentNid: null,
    currentColid: null,
    currentUid: null,
    replyTo: null,
    cnt: 60,
    myinterl: null,

    init: function () {
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
            console.log("News detail:", data);

            // 如果返回的是字符串，需要解析
            let news = typeof data === "string" ? JSON.parse(data) : data;

            if (news && news.ntitle) {
                $("#newsTitle").text(news.ntitle);
                $("#newsContent").html(news.content || "暂无内容");
                $("#newsAuthor").text(news.author || "");
                $("#newsDate").text(news.createdate || "");

                // 更新页面标题
                document.title = news.ntitle + " - 新闻中国";

                // 检查是否已收藏
                newsinfoobj.checkIfCollected(nid);
                // 检查是否已点赞
                newsinfoobj.checkIfLiked(nid);
                // 记录浏览历史
                newsinfoobj.saveHistory(nid);
            } else {
                $("#newsTitle").text("新闻不存在");
                $("#newsContent").text("该新闻可能已被删除");
            }
        }).fail(function (err) {
            console.log("Load news failed:", err);
            $("#newsTitle").text("加载失败");
            $("#newsContent").text("请稍后重试");
        });

        // 加载评论列表
        newsinfoobj.loadComments(nid);
    },

    // 保存浏览历史
    saveHistory: function (nid) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) return; // 未登录不记录

        $.post(service_path + "/news/history/save", {
            phone: phone,
            nid: nid,
            createdate: new Date().toLocaleString()
        }, function () {
            console.log("History saved");
        });
    },

    // 处理点赞
    handleLike: function () {
        let phone = sessionStorage.getItem("cuser");

        if (!phone) {
            alert("请先登录后再点赞");
            return;
        }

        let $btn = $("#likeBtn");
        let nid = newsinfoobj.currentNid;

        $.post(service_path + "/news/like/toggle", {
            phone: phone,
            nid: nid
        }, function (data) {
            let result = typeof data === "string" ? JSON.parse(data) : data;
            if (result.liked) {
                $btn.addClass("liked");
            } else {
                $btn.removeClass("liked");
            }
        }).fail(function (err) {
            console.log("Like toggle failed:", err);
            alert("操作失败，请重试");
        });
    },

    // 检查是否已点赞
    checkIfLiked: function (nid) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) return;

        $.get(service_path + "/news/like/check", { phone: phone, nid: nid }, function (data) {
            let result = typeof data === "string" ? JSON.parse(data) : data;
            if (result.liked) {
                $("#likeBtn").addClass("liked");
            }
        });
    },

    // 处理收藏
    handleCollect: function () {
        let phone = sessionStorage.getItem("cuser");

        // 检查是否登录
        if (!phone) {
            alert("用户未登录，请登录");
            return;
        }

        let $btn = $("#collectBtn");
        let nid = newsinfoobj.currentNid;

        if ($btn.hasClass("collected")) {
            // 已收藏，取消收藏
            let colid = newsinfoobj.currentColid;
            if (colid) {
                $.ajax({
                    url: service_path + "/news/collection/del/" + colid,
                    type: "DELETE",
                    success: function () {
                        $btn.removeClass("collected");
                        $btn.html("&#9734;"); // 空心星星
                        newsinfoobj.currentColid = null;
                        alert("已取消收藏");
                    },
                    error: function (err) {
                        console.log("Cancel collect failed:", err);
                        alert("取消收藏失败");
                    }
                });
            }
        } else {
            // 添加收藏
            $.post(service_path + "/news/collection/save", {
                phone: phone,
                nid: nid,
                createdate: new Date().toLocaleString()
            }, function (data) {
                $btn.addClass("collected");
                $btn.html("&#9733;"); // 实心星星
                alert("收藏成功！");
                // 重新检查获取 colid
                newsinfoobj.checkIfCollected(nid);
            }).fail(function (err) {
                console.log("Collect failed:", err);
                alert("收藏失败，请重试");
            });
        }
    },

    // 检查是否已收藏
    checkIfCollected: function (nid) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) return;

        $.get(service_path + "/news/collection/getdetail", { phone: phone }, function (data) {
            let list = typeof data === "string" ? JSON.parse(data) : data;
            if (list && list.length > 0) {
                for (let i = 0; i < list.length; i++) {
                    if (list[i].nid == nid) {
                        $("#collectBtn").addClass("collected");
                        $("#collectBtn").html("&#9733;"); // 实心星星
                        newsinfoobj.currentColid = list[i].colid; // 保存 colid 用于取消
                        break;
                    }
                }
            }
        });
    },

    // 提交评论
    submitComment: function () {
        let phone = sessionStorage.getItem("cuser");

        if (!phone) {
            alert("请先登录后再评论");
            return;
        }

        let content = $("#commentInput").val().trim();
        if (!content) {
            alert("请输入评论内容");
            return;
        }

        let nid = newsinfoobj.currentNid;

        // 检查是否匿名评论
        let isAnonymous = $("#anonymousCheck").is(":checked");

        let postData = {
            phone: phone,
            nid: nid,
            content: content,
            createdate: new Date().toLocaleString(),
            anonymous: isAnonymous
        };

        if (newsinfoobj.replyTo) {
            postData.pid = newsinfoobj.replyTo.cid;
        }

        $.post(service_path + "/news/comment/save", postData, function (data) {
            alert(newsinfoobj.replyTo ? "回复成功！" : "评论成功！");
            $("#commentInput").val("");
            $("#anonymousCheck").prop("checked", false);
            newsinfoobj.replyTo = null;
            $("#submitComment").text("提交");
            // 重新加载评论
            newsinfoobj.loadComments(nid);
        }).fail(function (err) {
            console.log("Comment failed:", err);
            alert("操作失败，请重试");
        });
    },

    // 准备回复某人
    prepareReply: function (cid, username) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) {
            alert("请先登录后操作");
            return;
        }
        newsinfoobj.replyTo = { cid: cid, username: username };
        $("#commentInput").val("@" + username + " ").focus();
        $("#submitComment").text("回复");
    },

    // 切换评论点赞状态
    toggleCommentLike: function (cid, isLiked, $icon, $count) {
        let phone = sessionStorage.getItem("cuser");
        if (!phone) {
            alert("请先登录后点赞");
            return;
        }

        let url = isLiked ? "/news/comment/unlike" : "/news/comment/like";
        $.post(service_path + url, { cid: cid, phone: phone }, function () {
            // 本地更新 UI
            let newIsLiked = !isLiked;
            let currentCount = parseInt($count.text()) || 0;
            let newCount = newIsLiked ? currentCount + 1 : currentCount - 1;

            $count.text(newCount);
            if (newIsLiked) {
                $icon.css("color", "#e60012");
            } else {
                $icon.css("color", "#969799");
            }
            // 更新 onclick 属性以反映新状态
            $icon.parent().attr("onclick", "newsinfoobj.toggleCommentLike(" + cid + ", " + newIsLiked + ", $(this).find('span.heart-icon'), $(this).find('span.like-count'))");
        });
    },

    // 加载评论列表
    loadComments: function (nid) {
        let phone = sessionStorage.getItem("cuser") || "";
        $.get(service_path + "/news/comment/getbynid", { nid: nid, phone: phone }, function (data) {
            let list = typeof data === "string" ? JSON.parse(data) : data;
            let html = "";

            // 获取当前用户的 uid
            if (phone && !newsinfoobj.currentUid) {
                $.get(service_path + "/news/users/getbyphone?phone=" + phone, function (userData) {
                    let user = typeof userData === "string" ? JSON.parse(userData) : userData;
                    if (user && user.uid) {
                        newsinfoobj.currentUid = user.uid;
                    }
                });
            }

            if (list && list.length > 0) {
                for (let i = 0; i < list.length; i++) {
                    html += newsinfoobj.renderCommentItem(list[i], false);
                }
            } else {
                html = '<div style="color:#969799; text-align:center; padding:40px; font-size:14px;">暂无评论，快来发表第一篇评论吧 ~</div>';
            }

            $("#commentList").html(html);
        }).fail(function (err) {
            console.log("Load comments failed:", err);
        });
    },

    // 渲染单条评论 HTML（支持无限级递归）
    renderCommentItem: function (comment, isReply) {
        let statusTag = (comment.status === 0) ? ' <span style="color:#f90; font-weight:normal; font-size:12px;">(待审核)</span>' : '';
        let heartColor = comment.isLiked ? "#e60012" : "#a8abb2";
        let likeCount = comment.likeCount || 0;

        let html = '<div class="comment-item">';
        html += '  <div style="display: flex; justify-content: space-between; align-items: flex-start;">';
        html += '    <span class="comment-user">' + (comment.username || "匿名用户") + statusTag + '</span>';

        // 操作区
        html += '    <div style="display: flex; align-items: center; gap: 16px;">';
        html += '      <span style="cursor:pointer; display:flex; align-items:center; gap:4px; font-size: 13px; color: #606266;" onclick="newsinfoobj.toggleCommentLike(' + comment.cid + ', ' + comment.isLiked + ', $(this).find(\'.heart-icon\'), $(this).find(\'.like-count\'))">';
        html += '        <span class="heart-icon" style="color:' + heartColor + '; font-size: 18px;">❤</span>';
        html += '        <span class="like-count">' + likeCount + '</span>';
        html += '      </span>';
        if (!isReply) {
            html += '      <span style="cursor:pointer; font-size: 13px; color: #0066cc;" onclick="newsinfoobj.prepareReply(' + comment.cid + ', \'' + (comment.username || "匿名用户") + '\')">回复</span>';
        }
        html += '    </div>';
        html += '  </div>';

        html += '  <div class="comment-content">' + comment.content + '</div>';
        html += '  <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 5px;">';
        html += '    <span style="color:#999; font-size:12px;">' + (comment.createdate || "") + '</span>';

        // 删除按钮
        if (comment.uid && comment.uid === newsinfoobj.currentUid) {
            html += '    <span class="comment-delete" onclick="newsinfoobj.deleteComment(' + comment.cid + ')" style="color:#f56c6c; font-size:12px; cursor:pointer;">删除</span>';
        }
        html += '  </div>';

        // 处理递归子评论
        if (comment.replyList && comment.replyList.length > 0) {
            html += '<div class="reply-list">';
            for (let j = 0; j < comment.replyList.length; j++) {
                html += newsinfoobj.renderCommentItem(comment.replyList[j], true);
            }
            html += '</div>';
        }

        html += '</div>';
        return html;
    },

    // 删除评论
    deleteComment: function (cid) {
        if (!confirm("确定要删除这条评论吗？")) {
            return;
        }

        $.ajax({
            url: service_path + "/news/comment/del/" + cid,
            type: "DELETE",
            success: function () {
                alert("删除成功");
                newsinfoobj.loadComments(newsinfoobj.currentNid);
            },
            error: function (err) {
                console.log("Delete comment failed:", err);
                alert("删除失败，请重试");
            }
        });
    },

    // 获取验证码
    getcode: function () {
        // 倒计时进行中，禁止重复发送
        if (newsinfoobj.cnt < 60) {
            alert("验证码已发送，请勿频繁操作");
            return;
        }

        let val = $("[name=phone]").val();
        if (val.length == 0) {
            alert('请输入手机号码');
        } else {
            newsinfoobj.myinterl = window.setInterval("newsinfoobj.setcnt()", 1000);
            $.get(service_path + "/news/users/getcode?phone=" + val, function (data) {
                console.log('验证码已发送');
            });
        }
    },

    // 倒计时
    setcnt: function () {
        $("#getcode").text("重新发送(" + newsinfoobj.cnt + ")");
        newsinfoobj.cnt--;
        if (newsinfoobj.cnt == 0) {
            window.clearInterval(newsinfoobj.myinterl);
            $("#getcode").text("发送验证码");
            newsinfoobj.cnt = 60;
        }
    },

    // 登录
    login: function () {
        $.post(service_path + "/news/users/login", {
            "phone": $("[name=phone]").val(),
            "code": $("[name=code]").val()
        }).done(function (data) {
            let result = typeof data === "string" ? (data === "true" || data === "TRUE") : data;
            if (result === true || result === "true" || result === "TRUE") {
                sessionStorage.setItem("cuser", $("[name=phone]").val());
                location.reload();
            } else {
                alert("登录失败，请检查手机号和验证码");
            }
        }).fail(function () {
            alert("登录请求失败");
        });
    }
};

$(newsinfoobj.init);
