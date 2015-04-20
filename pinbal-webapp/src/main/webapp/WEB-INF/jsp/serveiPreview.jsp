<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<%
	java.util.Map<?,?> map = (java.util.Map<?,?>)request.getAttribute("campsDadesEspecifiquesAgrupats");
	if (map != null)
		pageContext.setAttribute("campsSenseAgrupar", map.get(null));
%>

<html>
<head>
<script>
$(document).ready(function() {
	$('.datepicker').datepicker({
		format: 'dd/mm/yy',
		weekStart: 1
	});
});
</script>
</head>
<body>

	<form class="form-horizontal">
		<c:set var="campsPerMostrar" value="${campsSenseAgrupar}" scope="request"/>
		<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
		<c:forEach var="grup" items="${grups}">
			<fieldset>
			 	<legend>${grup.nom}</legend>
			 	<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[grup.id]}" scope="request"/>
				<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
   			</fieldset>
		</c:forEach>
	</form>

</body>
</html>
