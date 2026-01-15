let historyobj = {
    pageno: 1,
    pagesize: 10,
    totalPages: 1,

    init: function () {
        // 1. 检查登录状态
        let phone = sessionStorage.getItem("cuser");

        if (!phone) {
            historyobj.showMessage("请先登录查看记录", "error");
            setTimeout(function () { location.href = "index.html"; }, 1500);
            return;
        }

        // 2. 显示用户名
        $("#currentuser").text(phone);

        // 3. 加载历史列表
        historyobj.loadHistory(phone);

        // 4. 退出按钮
        $("#exit").off("click").on("click", function () {
            sessionStorage.clear();
            location.href = "index.html";
        });

        // 5. 清空所有记录按钮
        $(".btn-clear-all").off("click").on("click", function () {
            if (confirm("确定要清空您所有的浏览记录吗？此操作不可恢复。")) {
                $.ajax({
                    url: service_path + "/news/history/clear?phone=" + phone,
                    type: "DELETE",
                    success: function () {
                        historyobj.showMessage("已成功清空", "success");
                        historyobj.pageno = 1;
                        historyobj.loadHistory(phone);
                    },
                    error: function (err) {
                        console.log("Clear all failed:", err);
                        historyobj.showMessage("清空失败", "error");
                    }
                });
            }
        });

        // 6. 分页按钮点击事件
        $("#firstPage").click(function () {
            if (historyobj.pageno > 1) {
                historyobj.pageno = 1;
                historyobj.loadHistory(phone);
            }
            return false;
        });

        $("#prevPage").click(function () {
            if (historyobj.pageno > 1) {
                historyobj.pageno--;
                historyobj.loadHistory(phone);
            }
            return false;
        });

        $("#nextPage").click(function () {
            if (historyobj.pageno < historyobj.totalPages) {
                historyobj.pageno++;
                historyobj.loadHistory(phone);
            }
            return false;
        });

        $("#lastPage").click(function () {
            if (historyobj.pageno < historyobj.totalPages) {
                historyobj.pageno = historyobj.totalPages;
                historyobj.loadHistory(phone);
            }
            return false;
        });
    },

    // 消息提示函数 (Toast)
    showMessage: function (text, type) {
        let className = type === "success" ? "msg-success" : "msg-error";
        let $msg = $('<div class="msg-box ' + className + '">' + text + '</div>');
        $("#msgContainer").append($msg);

        // 2秒后自动淡出并移除
        setTimeout(function () {
            $msg.fadeOut(500, function () {
                $(this).remove();
            });
        }, 2000);
    },

    loadHistory: function (phone) {
        $.get(service_path + "/news/history/getdetail_pager", {
            phone: phone,
            pageno: historyobj.pageno,
            pagesize: historyobj.pagesize
        }, function (data) {
            let res = typeof data === "string" ? JSON.parse(data) : data;
            let list = res.list;
            let html = "";

            if (list && list.length > 0) {
                for (let i = 0; i < list.length; i++) {
                    let item = list[i];
                    html += '<div class="history-item">';
                    html += '  <div class="news-info">';
                    html += '    <a href="newsinfo.html?nid=' + item.nid + '" class="news-title">' + item.ntitle + '</a>';
                    html += '    <span class="view-time">看过时间：' + (item.createdate || '') + '</span>';
                    html += '  </div>';
                    html += '  <button class="btn-delete btn-item-delete" data-hid="' + item.hid + '">删除记录</button>';
                    html += '</div>';
                }

                // 更新分页信息
                historyobj.totalPages = res.totalpage || 1;
                $("#currentPage").text(historyobj.pageno);
                $("#totalPages").text(historyobj.totalPages);
                $("#pagination").show();
            } else {
                html = '<div class="empty-msg">您还没有浏览过任何新闻哦～</div>';
                $("#pagination").hide();
            }

            $("#historyList").html(html);

            // 绑定单条删除事件
            $(".btn-item-delete").off("click").on("click", function () {
                let hid = $(this).attr("data-hid");
                if (confirm("确定要删除这条记录吗？")) {
                    historyobj.deleteHistory(hid, phone);
                }
            });
        }).fail(function (err) {
            console.log("Load history failed:", err);
            $("#historyList").html('<div class="empty-msg">加载失败，请重试</div>');
            $("#pagination").hide();
        });
    },

    deleteHistory: function (hid, phone) {
        if (!hid) {
            historyobj.showMessage("记录ID无效", "error");
            return;
        }
        $.ajax({
            url: service_path + "/news/history/del/" + hid,
            type: "DELETE",
            success: function () {
                historyobj.showMessage("已删除该记录", "success");
                historyobj.loadHistory(phone);
            },
            error: function (err) {
                console.log("Delete failed:", err);
                historyobj.showMessage("删除失败", "error");
            }
        });
    }
};

$(function () {
    historyobj.init();
});
