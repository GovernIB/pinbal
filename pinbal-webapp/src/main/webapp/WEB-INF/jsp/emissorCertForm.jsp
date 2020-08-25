<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<title>
		<c:choose>
			<c:when test="${empty emissorCertCommand.id}"><spring:message code="emissorcert.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="emissorcert.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	
	<script>
		var eyeEnabled = true;
		
		$(document).ready(function() {
			
			$('#btn-calendar-baixa').click(function() {
				$('#dataBaixa').focus();
			});
			
		});
	</script>
	
</head>
<body>

	<c:url value="/scsp/emissorcert/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="emissorCertCommand">
		<form:hidden path="id"/>
		<div class="row">
			<div class="col-md-12">
				<pbl:inputText name="nom" required="true" labelSize="1" inline="false" textKey="emissorcert.form.camp.nom"/>
				<pbl:inputDate name="dataBaixa" required="false" labelSize="1" inline="false" textKey="emissorcert.form.camp.data.baixa"/>
				<pbl:inputText name="cif" required="true" labelSize="1" inline="false" textKey="emissorcert.form.camp.cif"/>
				<div class="pull-right">	
					<button type="submit" class="btn btn-primary" ><spring:message code="comu.boto.guardar" /></button>
					<a href="<c:url value="/scsp/emissorcert"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>	
		</div>
	</form:form>

</body>
</html>
