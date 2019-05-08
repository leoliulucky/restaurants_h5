<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<title>点餐</title>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<#include "../inc/inc.ftl">
</head>
<body>
<#assign backUrl = '/global/index'>
<#include "../inc/header.ftl">
<div>
    <ul class="list-pro" style="padding-bottom: 0">
        <li>
            <img src="${data.detail.restaurantVO.icon}" class="list-pic" style="width:40%;" />
            <div class="shop-list-mid" style="width:50%">
                <div class="tit" style="font-weight:700;font-size: 20px;color:#111;">${data.detail.restaurantVO.restaurantName}</div>
                <div class="tit" style="font-size: 14px;color:#909090;">位置：${data.detail.restaurantVO.address}</div>
                <div class="tit" style="font-size: 14px;color:#909090;">电话：${data.detail.restaurantVO.tel}</div>
                <div class="tit" style="font-size: 14px;color:#909090;">标签：${data.detail.restaurantVO.tags}</div>
            </div>
        </li>
    </ul>
</div>


<div class="content-list" id="outer">
    <div class="list-left" id="tab">
        <ul>
            <#list data.detail.productCategoryVOList as vo>
                <li><a>${vo.categoryName}</a></li>
            </#list>
        </ul>
    </div>
    <div class="list-right" id="content">
        <#list data.detail.productVOListList as productVOList>
            <ul class="list-pro" style="margin-top:0;">
                <#list productVOList as productVO>
                    <li>
                        <a href="javascript:;"><img src="${productVO.icon}" class="list-pic" /></a>
                        <div class="shop-list-mid">
                            <div class="tit"><a href="javascript:;">${productVO.productName}</a></div>
                            <div class="am-gallery-desc">￥${productVO.price?string(',##0.00')}</div>
                        </div>
                        <div class="list-cart">
                            <div class="d-stock ">
                                <a class="decrease" data="${productVO.productId}">-</a>
                                <input readonly="" class="text_box" name="num" type="text" value="0">
                                <a class="increase" data="${productVO.productId}">+</a>
                            </div>
                        </div>
                    </li>
                </#list>
            </ul>
        </#list>
    </div>
</div>
<!--底部-->
<div style="height: 100px;"></div>
<div class="fix-bot">
    <a href="javascript:;" class="list-js" id="showStat">合计：<i>0元</i><em>(0份)</em></a>
    <a href="javascript:;" class="list-jsk" onclick="return chooseDone();">选好了</a>
</div>

<div class="am-modal am-modal-confirm" tabindex="-1" id="login-confirm">
    <div class="am-modal-dialog">
        <div class="am-modal-bd" style="height: 80px; line-height: 80px;">  您还没有登录，请先登录后再操作：）</div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-cancel>取消</span>
            <span class="am-modal-btn" data-am-modal-confirm>确定</span>
        </div>
    </div>
</div>

<script type="text/javascript">
//购物数量加减
$(function(){
    $('.increase').click(function(){
        checkLogin();

        var self = $(this);
        var current_num = parseInt(self.siblings('input').val());
        current_num += 1;
        if(current_num > 0){
            self.siblings(".decrease").fadeIn();
            self.siblings(".text_box").fadeIn();
        }
        self.siblings('input').val(current_num);
        // update_item(self.siblings('input').data('item-id'));

        var pid = self.attr("data");
        updateCart(pid, current_num);
    });
    $('.decrease').click(function(){
        checkLogin();

        var self = $(this);
        var current_num = parseInt(self.siblings('input').val());
        if(current_num > 0){
            current_num -= 1;
            if(current_num < 1){
                self.fadeOut();
                self.siblings(".text_box").fadeOut();
            }
            self.siblings('input').val(current_num);
            // update_item(self.siblings('input').data('item-id'));

            var pid = self.attr("data");
            updateCart(pid, current_num);
        }
    });
});

//删除提示信息
$(function() {
    $('#doc-modal-list').find('.am-icon-close').add('#doc-confirm-toggle').
    on('click', function() {
        $('#my-confirm').modal({
            relatedTarget: this,
            onConfirm: function(options) {
                var $link = $(this.relatedTarget).prev('a');
                var msg = $link.length ? '你要删除的饮品 为 ' + $link.data('id') :
                        '确定了';
//        alert(msg);
            },
            onCancel: function() {
                alert('不删除');
            }
        });
    });
});

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

    _$showStat = $("#showStat");
});

/**
 * 检验是否登录
 */
function checkLogin(){
    var isLogin = false;
    <#if userObj??>
        isLogin = true;
    </#if>

    if(!isLogin){
        $('#login-confirm').modal({
            relatedTarget: this,
            onConfirm: function(options) {
                // alert('确定了');
                window.location.href = "/user/login";
            },
            onCancel: function() {
                // alert('不删除');
            }
        });
    }
}

var _productObj = {};
var _cartObj = {};
var _$showStat;

<#list data.detail.productVOListList as productVOList>
    <#list productVOList as productVO>
        _productObj["${productVO.productId}"] = ${productVO.price};
    </#list>
</#list>

/**
 * 更新购物车商品
 */
function updateCart(pid, num){
    if(num <= 0){
        num = 0;
    }
    _cartObj[pid] = num;

    var _totalAmount = 0;
    var _totalCount = 0;
    for(var key in _cartObj){
        _totalAmount += _cartObj[key] * _productObj[key];
        _totalCount += _cartObj[key];
    }
    _$showStat.html("合计：<i>" + numFormat(_totalAmount, 2) + "元</i><em>(" + _totalCount + "份)</em>");
}

function numFormat(s, n) {
    var t = '',
            r = '';
    var Str = function() {
        n = n > 0 && n <= 20 ? n: 2;
        s = parseFloat((s + '').replace(/[^\d\.-]/g, '')).toFixed(n) + '';
        var l = s.split('.')[0].split('').reverse();
        r = s.split('.')[1];
        for (var i = 0; i < l.length; i++) {
            t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? ',': '')
        }
    };
    if (n >= 0) {
        Str();
        return t.split('').reverse().join('') + '.' + r
    } else {
        Str();
        return t.split('').reverse().join('')
    }
}

/**
 * 选好了
 */
function chooseDone(){
    var _pid = "", _num = "";
    for(var key in _cartObj){
        if(_cartObj[key] > 0){
            _pid += key + ",";
            _num += _cartObj[key] + ",";
        }
    }
    _pid = _pid.substring(0, _pid.length - 1);
    _num = _pid.substring(0, _num.length - 1);
    if(_pid == ""){
        alert("您还没有选餐，请选择后再提交");
        return false;
    }
    window.location.href = "/order/goods?pid=" + _pid + "&num=" + _num;
}
</script>
</body>
</html>
