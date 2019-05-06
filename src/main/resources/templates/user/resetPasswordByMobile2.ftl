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
    <li>${data.mobile}</li>
    <li>
        <input type="text" id="code" placeholder="请确认短信验证码" maxlength="6" style="width:50%"/>
        <input type="hidden" id="mobile" name="mobile" value="${data.mobile }" />
        <input type="hidden" id="mai" name="mai" value="${data.mobileAuthenId }" />

        <label id="countDownLabel" style="font-size:12px;">请<em style="font-size: 1.5em;color:red">60</em>秒后重新获取</label>
        <label id="btnMobileLabel" style="font-size:12px;display:none"><a href="javascript:;" onclick="return getMobileCode();">获取短信验证码</a></label>
    </li>
    <li id="errorTip" style="color:red;padding-left:10px;display:none;">错误提示</li>
</ul>
<button class="paybtn" type="button" onclick="return verifyMobileCode();">重置密码</button>
</body>
<script type="text/javascript">
$(function(){
    _interval = setInterval("smsSend()", 1000);
});

function verifyMobileCode() {
    var $mobile = $('#mobile');
    var $code = $('#code');
    var $mai = $('#mai');
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
    if ($code.val() == null || $code.val() == '') {
        $errorTip.html('请输入验证码').show();
        return false;
    }
    var regCode = /^\d{6}$/;
    if(!regCode.test($code.val())){
        $errorTip.html('验证码输入错误，请检查').show();
        return false;
    }
    if ($mai.val() == null || $mai.val() == '') {
        $errorTip.html('请刷新页面后重新验证').show();
        return false;
    }
    var regMai = /^\d+$/;
    if(!regMai.test($mai.val())){
        $errorTip.html('请刷新页面后重新验证').show();
        return false;
    }
    $.ajax({
        type: 'POST',
        url: "/user/verifyMobileCode4ResetPwd",
        data: {"mobile": $mobile.val(), "code": $code.val(), "mai": $mai.val()},
        dataType: 'json',
        success: function(data) {
            if(data.code < 0){
                $errorTip.html(data.msg).show();
                return false;
            }
            window.location.href = "/user/verifySuccess4ResetPwd";
        }
    });
}

function getMobileCode() {
    var $mobile = $('#mobile');
    var $mai = $('#mai');
    var $errorTip = $('#errorTip');

    if ($mobile.val() == null || $mobile.val() == '') {
        $errorTip.html('请输入手机号').show();
        return false;
    }
    var regMobile = /^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\d{8}$/;
    if(!regMobile.test($mobile.val())){
        $errorTip.html('手机格式不正确，请检查并重新输入').show();
        return false;
    }
    if ($mai.val() == null || $mai.val() == '') {
        alert('参数非法');
        return false;
    }
    var regMai = /^\d+$/;
    if(!regMai.test($mai.val())){
        alert("参数非法");
        return false;
    }
    $.ajax({
        type: 'POST',
        url: "/user/getMobileCode4ResetPwd",
        data: {"mobile": $mobile.val()},
        dataType: 'json',
        success: function(data) {
            if(data.code < 0){
                $errorTip.html(data.msg).show();
                return false;
            }
            $mai.val(data.data.mobileAuthenId);
            _interval = setInterval("smsSend()", 1000);
        }
    });
}
var n = 60;
var _interval;
function smsSend(){
    n--;
    $("#btnMobileLabel").hide();
    $("#countDownLabel").html('请<em style="font-size: 1.5em;color:red">' + n + '</em>秒后重新获取').show();
    if (n == 0) {
        $("#btnMobileLabel").show();
        $("#countDownLabel").html('请<em style="font-size: 1.5em;color:red">60</em>秒后重新获取').hide();
        clearInterval(_interval);
        n = 60;
    }
}
</script>
</html>
