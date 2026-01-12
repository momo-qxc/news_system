let service_path="http://127.0.0.1:6060"

let loadpage = function() {
    $("#top_login,#top_login2").hide();
    if (sessionStorage.getItem("cuser") == null) {
        $("#top_login").show();
    } else {
        $("#top_login2").show();
        $("#currentuser").text(sessionStorage.getItem("cuser"));
    }
};