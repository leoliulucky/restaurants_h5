<#assign pageCount = data.pager.pageCount>
<#assign curPageNum = data.pager.pageNum>
<#if curPageNum?? || curPageNum <= 0>
	<#assign curPageNum = 1>
</#if>
<div class="page">
	<#if curPageNum == 1>
		<span style="color:#909090;font-size:1.4rem;"> 上一页 </span>
	<#else>
		<a href="javascript:;" onclick="return $pager.go(${curPageNum-1 });" style="font-size:1.4rem;"> 上一页 </a>
	</#if>

	<#if (curPageNum >= pageCount)>
		<span style="color:#909090;font-size:1.4rem;float:right;"> 下一页 </span>
	<#else>
		<a href="javascript:;" onclick="return $pager.go(${curPageNum+1 });" style="font-size:1.4rem;float:right;"> 下一页 </a>
	</#if>
</div>
<form id="pageSearchForm" name="pageSearchForm" action="" method="post"></form>
<script type="text/javascript">
    var $pager=(function(manager){var _params={};manager.go=function(page, params){if(params){manager.create(params);}var htmlStr='<input type="hidden" name="page" value="'+page+'" />';for(var key in _params){htmlStr += '<input type="hidden" name="' + key + '" value="' + _params[key] + '" />';}$("#pageSearchForm").html(htmlStr).submit();};manager.create=function(params){_params = params;};return manager;}($pager || {}));
</script>