let collectionobj = {
    init: function () {
        // 检查登录状态 - 登录时存储的是 cuser
        let phone = sessionStorage.getItem("cuser");

        if (!phone) {
            alert("请先登录");
            location.href = "index.html";
            return;
        }

        // 显示用户名
        $("#currentuser").text(phone);
        $("#top_login").hide();
        $("#top_login2").show();

        // 加载收藏列表
        collectionobj.loadCollections(phone);

        // 退出按钮
        $("#exit").click(function () {
            sessionStorage.clear();
            location.href = "index.html";
        });
    },

    loadCollections: function (phone) {
        // 使用 /getdetail 获取包含新闻标题和收藏时间的数据
        $.get(service_path + "/news/collection/getdetail", { phone: phone }, function (data) {
            console.log("Collections:", data);
            let html = "";

            // 如果返回的是字符串，需要解析
            let list = typeof data === "string" ? JSON.parse(data) : data;

            if (list && list.length > 0) {
                for (let i = 0; i < list.length; i++) {
                    let item = list[i];
                    html += '<div class="collection-item" data-colid="' + item.colid + '">';
                    html += '  <a href="newsinfo.html?nid=' + item.nid + '" class="news-title">' + item.ntitle + '</a>';
                    html += '  <span class="news-date">' + (item.createdate || '') + '</span>';
                    html += '  <button class="btn-cancel" data-colid="' + item.colid + '">取消收藏</button>';
                    html += '</div>';
                }
            } else {
                html = '<div class="empty-msg">暂无收藏的新闻</div>';
            }

            $("#collectionList").html(html);

            // 绑定取消收藏事件 - 直接使用 colid
            $(".btn-cancel").click(function () {
                let colid = $(this).data("colid");
                collectionobj.cancelCollection(colid, phone);
            });
        }).fail(function (err) {
            console.log("Load collections failed:", err);
            $("#collectionList").html('<div class="empty-msg">加载失败，请重试</div>');
        });
    },

    cancelCollection: function (colid, phone) {
        // 直接使用 colid 删除
        $.ajax({
            url: service_path + "/news/collection/del/" + colid,
            type: "DELETE",
            success: function () {
                alert("已取消收藏");
                collectionobj.loadCollections(phone);
            },
            error: function (err) {
                console.log("Cancel failed:", err);
                alert("取消收藏失败");
            }
        });
    }
};

$(collectionobj.init);