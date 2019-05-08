<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>注册</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<#include "../inc/inc.ftl">
<#include "../inc/rsa.ftl">
</head>
<body>
<header data-am-widget="header" class="am-header am-header-default sq-head ">
    <div class="am-header-left am-header-nav">
        <a href="javascript:history.back()" class="" style="border: 0;">
            <i class="am-icon-chevron-left"></i>
        </a>
    </div>
    <h1 class="am-header-title" >
        <a href="" class="" style="color: #333;">注册</a>
    </h1>
</header>
<div style="height: 49px;"></div>
<ul class="contact">
    <li><input type="text" id="account" placeholder="请输入字母、数字或中文" maxlength="32" /></li>
    <li><input type="password" id="password" placeholder="请输入密码" maxlength="16" /></li>
    <li><input type="password" id="rePassword" class="input" placeholder="请确认密码" maxlength="16" /></li>
    <li id="captchaDiv">
        <input type="text" id="captcha" class="input" maxlength="4" placeholder="请输入验证码" style="width: 50%;" />
        <img id="captchaImgId" style="height: 40px;padding-left: 5px;" src="/global/captcha" onclick="return refresh('captchaImgId');" />
    </li>
    <li id="errorTip" style="color:red;padding-left:10px;display:none;">错误提示</li>
</ul>
<button class="paybtn" type="button" onclick="return register();">注册</button>
<div style="padding:30px;">
    已有账号，<a href="/user/inputMobile4ResetPwd.do">登录</a>
</div>
</body>
<script type="text/javascript">
function register() {
    var $account = $('#account');
    var $password = $('#password');
    var $rePassword = $('#rePassword');
    var $captcha = $('#captcha');
    var $errorTip = $('#errorTip');

    if ($account.val() == null || $account.val() == '') {
        $errorTip.html('请输入账号').show();
        return false;
    }
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
    if ($captcha.val() == null || $captcha.val() == '') {
        $errorTip.html('请输入正确的验证码').show();
        return false;
    }

    $.ajax({
        type: 'POST',
        url: "/user/doRegister",
        data: {
            "account": $account.val(),
            "password": encodeData("${data.exponent}", "${data.module}", "${data.random}", $password.val()),
            "captcha": $captcha.val()
        },
        dataType: 'json',
        success: function(data) {
            if(data.code < 0){
                $errorTip.html(data.msg).show();
                return false;
            }
            window.location.href = "/user/inputMobile4Register";
        }
    });
}
</script>
</html>
