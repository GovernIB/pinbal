<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	request.setAttribute(
			"entitatTipusLlista",
			es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto.values());
%>
<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty entitatCommand.id}"><spring:message code="entitat.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="entitat.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
</head>
<body>

	<c:url value="/modal/entitat/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="entitatCommand">
		<form:hidden path="id"/>
		<div class="row">					
			<div class="col-md-12">
				<pbl:inputText name="codi" required="true" labelSize="1" inline="false" textKey="entitat.form.camp.codi"/>
				<pbl:inputText name="nom" required="true" labelSize="1" inline="false" textKey="entitat.form.camp.nom"/>
				<pbl:inputText name="cif" required="true" labelSize="1" inline="false" textKey="entitat.form.camp.cif"/>
				<pbl:inputText name="unitatArrel" required="true" labelSize="1" inline="false" textKey="entitat.form.camp.dir3"/>
				<pbl:inputSelect name="tipus" 
						textKey="entitat.form.camp.tipus"
						required="true" labelSize="1" inline="false" 
						optionItems="${entitatTipusLlista}" 
						emptyOption="true"
						emptyOptionTextKey="comu.opcio.sense.definir"/>			
				<div id="modal-botons">
					<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
					<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>
		</div>
	</form:form>

</body>
</html>
