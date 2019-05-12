<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>下单成功后支付</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<#include "../inc/inc.ftl">
</head>
<body>
<#assign backUrl = '/global/index'>
<#include "../inc/header.ftl">
<div style="height: 49px;"></div>
<ul class="contact">
    <li><h2>下单成功，请支付</h2></li>
    <li>订单编号：<input type="text" readonly value="${data.orderMap.orderVO.orderId}"/></li>
    <li>支付金额：<input type="text" readonly value="${data.orderMap.orderVO.realTotalAmout?string(',##0.00')}"/></li>
    <li><button class="paybtn" type="button" onclick="return payOrder();">立即支付</button></li>
    <br>
    <#list data.orderMap.orderItemVOList as orderItemVO>
        <li style="height: 110px">
            <a href="javascript:;"><img src="${orderItemVO.productIcon}" class="list-pic" /></a>
            <div class="shop-list-mid" style="float:none;">
                <div class="tit"><a href="javascript:;">&nbsp;&nbsp;${orderItemVO.productName}</a></div>
                <div class="am-gallery-desc">&nbsp;&nbsp;￥${orderItemVO.shopPrice?string(',##0.00')} x ${orderItemVO.productCount}</div>
            </div>
        </li>
    </#list>
</ul>
<input type="hidden" id="orderId" name="orderId" value="${data.orderMap.orderVO.orderId!}" />
</body>
<script type="text/javascript">
function payOrder() {
    var $orderId = $('#orderId');

    if ($orderId.val() == null || $orderId.val() == '') {
        alert("数据错误，请刷新页面重试");
        return false;
    }

    $.ajax({
        type: 'POST',
        url: "/order/pay",
        data: {"oid": $orderId.val()},
        dataType: 'json',
        success: function(json) {
            if(json.code < 0){
                alert(json.msg);
                return false;
            }
            window.location.href = "/order/paySuccess";
        }
    });
}
</script>
</html>
