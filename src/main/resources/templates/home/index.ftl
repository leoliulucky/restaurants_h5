<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>首页</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<#include "../inc/inc.ftl">
</head>
<body>
<#include "../inc/header.ftl">
<#--<header data-am-widget="header" class="am-header am-header-default sq-head" style="background: #39b867">-->
    <#--<div class="am-header-right am-header-nav">-->
        <#--<button type="button" class="am-btn am-btn-warning" id="doc-confirm-toggle" style="background: none; border: 0; font-size: 24px;">-->
            <#--&lt;#&ndash;<i class="am-header-icon am-icon-trash"></i>&ndash;&gt;-->
            <#--<#if userObj??>-->
                <#--${userObj.nickName}-->
            <#--<#else>-->
                <#--<a href="/user/register" style="color:white;font-size: 12px;">注册</a>-->
				<#--<a href="/user/login" style="color:white;font-size: 12px;">登录</a>-->
            <#--</#if>-->
        <#--</button>-->
    <#--</div>-->
<#--</header>-->
<div class="content-list" id="outer">
    <div class="list-left" id="tab" style="top:50px;">
        <ul>
            <li class="current"><a style="position: relative;">餐馆列表</li>
            <li><a>餐馆推荐</a></li>
        </ul>
    </div>
    <div class="list-right" id="content">
        <ul class="list-pro">

            <#list data.home.restaurantVOList as vo>
                <li>
                    <a href="detail.html"><img src="${vo.icon}" class="list-pic" /></a>
                    <div class="shop-list-mid">
                        <div class="tit"><a href="/restaurant/detail?i=${vo.restaurantId}" style="font-size:17px;color:#333;">${vo.restaurantName}</a></div>
                        <div class="tit"><a href="/restaurant/detail?i=${vo.restaurantId}">[${vo.address}] ${vo.tel}</a></div>
                        <div class="am-gallery-desc">${vo.tags}</div>
                    </div>
                    <div class="list-cart">
                        <div class="d-stock ">
                            <a class="increase" style="width:2em;" href="/restaurant/detail?i=${vo.restaurantId}">点餐</a>
                        </div>
                    </div>
                </li>
            </#list>

            <li>
                <#include "../inc/pager_params.ftl">
            </li>
        </ul>

        <ul class="list-pro">
            <li>
                <a href="detail.html"><img src="/images/1.png" class="list-pic" /></a>
                <div class="shop-list-mid">
                    <div class="tit"><a href="detail.html">法国加力果12个装 进口新鲜水果 嘎啦苹果 包邮</a></div>
                    <div class="am-gallery-desc">￥52</div>
                </div>
                <div class="list-cart">
                    <div class="d-stock ">
                        <a class="increase" style="width:2em;">点餐</a>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</div>

<script>
    //tab切换
    $(function(){
        window.onload = function()
        {
            var $li = $('#tab li');
            var $ul = $('#content ul');
            $li.click(function(){
                var $this = $(this);
                var $t = $li.index($(this)[0]);
                // alert($t);
                $li.removeClass();
                $this.addClass('current');
                $ul.css('display','none');
                $ul.eq($t).css('display','block');
            });
            $li.eq(0).click();
        }
    });
</script>
</body>
</html>
