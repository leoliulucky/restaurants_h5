<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>登录</title>
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
        <a href="" class="" style="color: #333;">登录</a>
    </h1>
</header>
<div style="height: 49px;"></div>
<ul class="contact">
    <li><input type="text" id="account" placeholder="手机/用户名/邮箱"/></li>
    <li><input type="password" id="password" placeholder="请输入密码" maxlength="16"/></li>
    <li id="errorTipTop" style="color:red;padding-left:10px;display:none;">错误提示</li>

    <#assign displayStyle=''>
    <#if !(requireValidateCode??)>
        <#assign displayStyle=' style="display:none;"'>
    </#if>
    <li id="captchaDiv" ${displayStyle }>
        <input type="text" id="captcha" class="input" maxlength="4" placeholder="请输入验证码" style="width: 50%;" />
        <img id="captchaImgId" style="height: 40px;padding-left: 5px;" src="/global/captcha" onclick="return refresh('captchaImgId');" />
    </li>
    <li id="errorTipBottom" style="color:red;padding-left:10px;display:none;">错误提示</li>
</ul>
<button class="paybtn" type="button" onclick="return login();">登录</button>
<div style="padding:30px;">
    没有账号，<a href="/user/register">注册</a>
    <a href="/user/inputMobile4ResetPwd" style="float:right">忘记密码</a>
</div>
<input type="hidden" id="path" name="path" value="${data.path!}" />
</body>
<script type="text/javascript">
function login() {
    var $account = $('#account');
    var $password = $('#password');
    var $captcha = $('#captcha');
    var $path = $('#path');
    var $errorTipTop = $('#errorTipTop');
    var $errorTipBottom = $('#errorTipBottom');
    var $captchaDiv = $("#captchaDiv");

    if ($account.val() == null || $account.val() == '') {
        if($captchaDiv.is(":hidden")){
            $errorTipTop.html('请输入账号').show();
        }else{
            $errorTipTop.hide();
            $errorTipBottom.html('请输入账号').show();
        }
        return false;
    }
    if ($password.val() == null || $password.val() == '') {
        if($captchaDiv.is(":hidden")){
            $errorTipTop.html('请输入密码').show();
        }else{
            $errorTipTop.hide();
            $errorTipBottom.html('请输入密码').show();
        }
        return false;
    }
    if(!$captchaDiv.is(":hidden")){
        if ($captcha.val() == null || $captcha.val() == '') {
            $errorTipTop.hide();
            $errorTipBottom.html('请输入正确的验证码').show();
            return false;
        }
    }

    $.ajax({
        type: 'POST',
        url: "/user/doLogin",
        data: {"account": $account.val(), "password": encodeData("${data.exponent}", "${data.module}", "${data.random}", $password.val()), "captcha": $captcha.val()},
        dataType: 'json',
        success: function(json) {
            if(json.code < 0){
                if(json.code == -88 || (json.data && json.data.requireValidateCode)){
                    $captchaDiv.show();
                    refresh('captchaImgId');
                }
                if($captchaDiv.is(":hidden")){
                    $errorTipTop.html(json.msg).show();
                }else{
                    $errorTipTop.hide();
                    $errorTipBottom.html(json.msg).show();
                }
                return false;
            }
            if(json.code == 2){
                window.location.href = "/user/inputMobile4Register";
                return false;
            }
            var url = "/global/index";
            if($path.val() != null && $path.val() != ''){
                url = $path.val();
            }
            window.location.href = decodeURIComponent(url);
        }
    });
}
</script>
</html>
