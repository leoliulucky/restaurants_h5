<header data-am-widget="header" class="am-header am-header-default sq-head" style="background: #39b867">
    <div class="am-header-left am-header-nav">
        <#if backUrl??>
            <a href="${backUrl}" class="" style="border: 0; color:white">
                <i class="am-icon-chevron-left"></i>返回
            </a>
        </#if>
    </div>

    <div class="am-header-right am-header-nav">
        <#--<button type="button" class="am-btn am-btn-warning" &lt;#&ndash;onfirm-toggle"&ndash;&gt; style="background: none; border: 0; font-size: 24px;">-->
        <#--<i class="am-header-icon am-icon-trash"></i>-->
            <#if userObj??>
                <a href="javascript:;" style="color:white;font-size: 16px;" onclick="return logout();">${userObj.nickName}</a>
            <#else>
                <a href="/user/register" style="color:white;font-size: 16px;">注册</a>
				<a href="/user/login" style="color:white;font-size: 16px;">登录</a>
            </#if>
        <#--</button>-->
    </div>
</header>

<div class="am-modal am-modal-confirm" tabindex="-1" id="logout-confirm">
    <div class="am-modal-dialog">
        <div class="am-modal-bd" style="height: 80px; line-height: 80px;">  您确定要退出登录吗？</div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-cancel>取消</span>
            <span class="am-modal-btn" data-am-modal-confirm>确定</span>
        </div>
    </div>
</div>

<script type="text/javascript">
function logout() {
    $('#logout-confirm').modal({
        relatedTarget: this,
        onConfirm: function(options) {
            // alert('确定了');
            window.location.href = "/user/logout";
        },
        onCancel: function() {
            // alert('不删除');
        }
    });
}
</script>