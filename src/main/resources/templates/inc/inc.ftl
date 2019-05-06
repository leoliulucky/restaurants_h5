<link href="/css/amazeui.min.css" type="text/css" rel="stylesheet" />
<link href="/css/style.css" type="text/css" rel="stylesheet" />
<script src="/js/jquery.min.js" type="text/javascript"></script>
<script src="/js/amazeui.min.js" type="text/javascript"></script>
<script type="text/javascript">
function refresh(imgId){$("#"+imgId).attr("src", "/global/captcha?r=" + new Date().getTime());}
</script>

<#if result??>
    <#assign code = result.getCode() />
    <#assign msg = result.getMsg() />
    <#assign data = result.data! />
    <#-- 业务错误时，页面给出提醒 -->
    <#if (code < 0)>
        <script type="text/javascript">
            $(function () {
                alert('${msg}');
            });
        </script>
    </#if>
</#if>