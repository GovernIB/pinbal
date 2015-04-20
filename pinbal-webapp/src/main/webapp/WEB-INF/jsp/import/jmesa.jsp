<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
<form name="personesJMesaForm" action="${param.actionUrl}">
	<div id="jmesa-ajax-div-${param.gridId}"><%=request.getAttribute(request.getParameter("gridId") + "Html")%></div>
</form>
<script type="text/javascript">
function onInvokeAction(id) {
	setExportToLimit(id, '');
<c:choose>
	<c:when test="${empty param.ajax}">createHiddenInputFieldsForLimitAndSubmit(id);</c:when>
	<c:otherwise>
	$("#jmesa-ajax-div-${param.gridId}").load("${param.actionUrl}?ajax=true&" + createParameterStringForLimit(id));
	</c:otherwise>
</c:choose>
}
</script>