<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>找回密码</title>
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
        <a href="" class="" style="color: #333;">找回密码</a>
    </h1>
</header>
<div style="height: 49px;"></div>
<ul class="contact">
    <li><input type="text" id="mobile" placeholder="请输入手机号" value="${data.mobile!}" maxlength="11" /></li>
    <li id="captchaDiv">
        <input type="text" id="captcha" class="input" maxlength="4" placeholder="请输入验证码" style="width: 50%;" />
        <img id="captchaImgId" style="height: 40px;padding-left: 5px;" src="/global/captcha" onclick="return refresh('captchaImgId');" />
    </li>
    <li id="errorTip" style="color:red;padding-left:10px;display:none;">错误提示</li>
</ul>
<button class="paybtn" type="button" onclick="return getMobileCode();">获取短信验证码</button>
</body>
<script type="text/javascript">
function getMobileCode() {
    var $mobile = $('#mobile');
    var $captcha = $('#captcha');
    var $errorTip = $('#errorTip');
    if ($mobile.val() == null || $mobile.val() == '') {
        $errorTip.html('请输入手机号').show();
        return false;
    }
    var regMobile = /^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\d{8}$/;
    if(!regMobile.test($mobile.val())){
        $errorTip.html('手机号格式不正确，请检查').show();
        return false;
    }
    if ($captcha.val() == null || $captcha.val() == '') {
        $errorTip.html('请输入验证码').show();
        return false;
    }
    $.ajax({
        type: 'POST',
        url: "/user/getMobileCode4ResetPwd",
        data: {"mobile": $mobile.val(), "captcha": $captcha.val()},
        dataType: 'json',
        success: function(data) {
            if(data.code < 0){
                $errorTip.html(data.msg).show();
                return false;
            }
            window.location.href = "/user/preVerifyMobile4ResetPwd?mobile=" + data.data.mobile + "&mai=" + data.data.mobileAuthenId + "&verifyCode=" + data.data.verifyCode;
        }
    });
}
</script>
</html>
