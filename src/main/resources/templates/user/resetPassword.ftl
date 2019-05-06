<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>重置密码</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<#include "../inc/inc.ftl">
</head>
<body>
<header data-am-widget="header" class="am-header am-header-default sq-head ">
    <div class="am-header-left am-header-nav">
        <a href="/user/login" class="" style="border: 0;">
            <i class="am-icon-chevron-left"></i>
        </a>
    </div>
    <h1 class="am-header-title" >
        <a href="" class="" style="color: #333;">重置密码</a>
    </h1>
</header>
<div style="height: 49px;"></div>
<ul class="contact">
    <li><input type="password" id="password" placeholder="请输入新的密码" maxlength="16" /></li>
    <li><input type="password" id="rePassword" placeholder="请确认新的密码" maxlength="16" /></li>
    <li id="errorTip" style="color:red;padding-left:10px;display:none;">错误提示</li>
</ul>
<button class="paybtn" type="button" onclick="return resetPwd();">重置密码</button>
</body>
<script type="text/javascript">
function resetPwd() {
    var $password = $('#password');
    var $rePassword = $('#rePassword');
    var $errorTip = $('#errorTip');

    if ($password.val() == null || $password.val() == '') {
        $errorTip.html('请输入密码').show();
        return false;
    }
    if ($rePassword.val() == null || $rePassword.val() == '') {
        $errorTip.html('请输入确认密码').show();
        return false;
    }
    if ($rePassword.val() != $password.val()) {
        $errorTip.html('两次输入密码不一致').show();
        return false;
    }
    $.ajax({
        type: 'POST',
        url: "/user/resetPwd",
        data: {"pwd": $password.val()},
        dataType: 'json',
        success: function(data) {
            if(data.code < 0){
                $errorTip.html(data.msg).show();
                return false;
            }
            window.location.href = "/user/resetPwdSuccessByMobile";
        }
    });
}
</script>
</html>
