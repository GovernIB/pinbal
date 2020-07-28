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
	
	
	
<%-- 		<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script> --%>
<%-- 	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script> --%>
<%-- 	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script> --%>
<%-- 	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script> --%>
<%-- 	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.ca.min.js"/>"></script> --%>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
</head>
<body>

	<c:url value="/entitat/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="well" commandName="entitatCommand">
		<form:hidden path="id"/>
		<fieldset>
			<div class="container-fluid">
				<div class="row">
				
					
					<div class="col-md-2">
						<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.codi"/> *</label>
						<pbl:inputText name="codi" inline="true" placeholderKey="entitat.form.camp.codi"/>
	<%-- 					<c:set var="campPath" value="codi"/ > --%>
	<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
	<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
				
<!-- 					<div class="controls"> -->
<%-- 					<form:input path="${campPath}" cssClass="input-lg" id="${campPath}"/> --%>
<%-- 					<form:errors path="${campPath}" cssClass="help-block"/> --%>
					</div>
					<div class="col-md-2">
						<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.nom"/> *</label>
						<pbl:inputText name="nom" inline="true" placeholderKey="entitat.form.camp.nom"/>
					
<%-- 					<c:set var="campPath" value="nom"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="input-lg" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 				</div> -->
					</div>
					<div class="col-md-2">
						<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.cif"/> *</label>
						<pbl:inputText name="cif" inline="true" placeholderKey="entitat.form.camp.cif"/>
<%-- 					<c:set var="campPath" value="cif"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
					
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
					</div>
		
				
					<div class="col-md-2">
						
						<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.tipus"/> *</label>
						<pbl:inputSelect name="tipus" inline="true" placeholderKey="entitat.form.camp.tipus"
	 						optionItems="${entitatTipusLlista}" 
	 						emptyOption="true"/>
							
<%-- 					<c:set var="campPath" value="tipus"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 					<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.tipus"/> *</label> --%>
<!-- 					<div class="controls"> -->
<%-- 					<form:select path="${campPath}"> --%>
<%-- 						<form:options items"${entitatTipusLlista}"/> --%>
<%-- 					</form:select> --%>
<%-- 					<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 				</div> -->
					</div>
				
				<label>&nbsp;</label>
				<div class="col-md-4">
					<label>&nbsp;</label>
					
					<div class="pull-right">
						<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
						<a href="<c:url value="/entitat"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
					</div>
				</div>	
			</div>
		</div>		
		</fieldset>

			
	</form:form>

</body>
</html>
