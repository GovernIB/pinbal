<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<%
	java.util.Map<?,?> map = (java.util.Map<?,?>)request.getAttribute("campsDadesEspecifiquesAgrupats");
	if (map != null)
		pageContext.setAttribute("campsSenseAgrupar", map.get(null));
%>

<html>
<head>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

<script>
$(document).ready(function() {
	$('.datepicker').datepicker({
		format: 'dd/mm/yy',
		weekStart: 1
	});
	$('.btn-ppv').popover();
});
</script>
	<style>
		.btn-ppv {border-radius: 16px; padding: 2px 7px 0px 7px; margin-left: 8px; position: relative; font-size: 10px;}
		.popover {max-width: 600px !important;}
		.popover-content {min-width: 400px !important;}
		.fs-grup {margin-top: 20px;}
		.fs-grup .fs-grup-nom {color: darkorange;}
		.fs-grup .btn-ppv {top: 4px;}
		.fs-subgrup {margin-top: 15px;}
		.fs-subgrup .panel-title {font-weight: bold;}
		.fs-subgrup .btn-ppv {top: -18px;}
		.control-label {color: #666666;}
	</style>
</head>
<body>

	<form class="well">
		<c:set var="campsPerMostrar" value="${campsSenseAgrupar}" scope="request"/>
		<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
		<c:forEach var="grup" items="${grups}">
			<fieldset class="fs-grup">
			 	<legend>
					<span class="fs-grup-nom">${grup.nom}</span>
					<c:if test="${not empty grup.ajuda}">
						<button type="button" class="btn btn-sm btn-info btn-ppv pull-right" data-toggle="popover" title="Ajuda" data-placement="left" data-html="true" data-content='${grup.ajuda}'><span class="fa fa-info"></span></button>
					</c:if>
				</legend>
			 	<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[grup.id]}" scope="request"/>
				<jsp:include page="import/dadesEspecifiquesForm.jsp"/>

				<%-- Subgrups --%>
				<c:if test="${not empty grup.fills}">
					<c:forEach var="subgrup" items="${grup.fills}">
						<div class="panel panel-default fs-subgrup">
							<div class="panel-heading">
								<h3 class="panel-title">${subgrup.nom}</h3>
								<c:if test="${not empty subgrup.ajuda}">
									<button type="button" class="btn btn-sm btn-info btn-ppv pull-right" data-toggle="popover" title="Ajuda" data-placement="left" data-html="true" data-content='${subgrup.ajuda}'><span class="fa fa-info"></span></button>
								</c:if>
							</div>
							<div class="panel-body">
								<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[subgrup.id]}" scope="request"/>
								<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
							</div>
						</div>
					</c:forEach>
				</c:if>
   			</fieldset>
		</c:forEach>
	</form>

</body>
</html>
