let registobj = {
    init: function () {
        $("#registerBtn").click(function () {
            registobj.adduser();
        });
    },
    adduser: function () {
        // 获取表单数据（使用 id 选择器）
        let username = $("#username").val();
        let phone = $("#phone").val();
        let sex = $("#sex").val();
        let age = $("#age").val();

        // 简单验证
        if (username.length == 0) {
            alert("请输入用户名");
            return;
        }
        if (phone.length != 11) {
            alert("请输入正确的手机号码");
            return;
        }
        if (age.length == 0 || parseInt(age) < 1) {
            alert("请输入有效的年龄");
            return;
        }

        $.post(service_path + "/news/users/add", {
            "username": username,
            "phone": phone,
            "sex": sex,
            "age": age,
            "pic": ""
        }, function (data) {
            console.log("Register response:", data);
            if (data == "true" || data === true || data == "success") {
                alert("注册成功！请返回登录");
                location.href = "index.html";
            } else if (data == "exists") {
                alert("该手机号已被注册，请使用其他手机号");
            } else {
                alert("注册失败，请重试");
            }
        }).fail(function (jqXHR, textStatus, errorThrown) {
            console.log("Register failed:", textStatus, errorThrown);
            alert("注册请求失败: " + textStatus);
        });
    }
};
$(registobj.init);