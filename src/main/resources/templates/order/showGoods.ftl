<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>确认选餐</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<#include "../inc/inc.ftl">
</head>
<body>
<#assign backUrl = '/global/index'>
<#include "../inc/header.ftl">
<div style="height: 49px;"></div>
<ul class="contact">
    <#list data.productVOList as productVO>
        <li style="height: 110px">
            <a href="javascript:;"><img src="${productVO.icon}" class="list-pic" /></a>
            <div class="shop-list-mid" style="float:none;">
                <div class="tit"><a href="javascript:;">&nbsp;&nbsp;${productVO.productName}</a></div>
                <div class="am-gallery-desc">&nbsp;&nbsp;￥${productVO.price?string(',##0.00')} x ${productVO.productCount}</div>
            </div>
        </li>
    </#list>
</ul>
<br>
<br>
<input type="hidden" id="productIds" name="productIds" value="${data.productIds!}" />
<input type="hidden" id="productCounts" name="productCounts" value="${data.productCounts!}" />
<button class="paybtn" type="button" onclick="return createOrder();">确认下单</button>
</body>
<script type="text/javascript">
function createOrder() {
    var $productIds = $('#productIds');
    var $productCounts = $('#productCounts');

    if ($productIds.val() == null || $productIds.val() == '') {
        alert("数据错误，请刷新页面重试");
        return false;
    }
    if ($productCounts.val() == null || $productCounts.val() == '') {
        alert("数据错误，请刷新页面重试");
        return false;
    }

    $.ajax({
        type: 'POST',
        url: "/order/create",
        data: {"pid": $productIds.val(), "num": $productCounts.val()},
        dataType: 'json',
        success: function(json) {
            if(json.code < 0){
                alert(json.msg);
                return false;
            }
            window.location.href = "/order/money?oid=" + json.data.orderId;
        }
    });
}
</script>
</html>
