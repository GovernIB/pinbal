<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<link href="<c:url value="/css/my-datepicker.css"/>" rel="stylesheet" type="text/css"/>
	
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	
	
	
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script> 
 	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script> 
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	
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
	<form:form action="${formAction}" method="post" cssClass="well" commandName="emissorCertCommand">
	
		<form:hidden path="id"/>
		
		<fieldset>
			

			<div class="container-fluid">
				<div class="row">
					<div class="col-md-3">
						<label class="control-label" for="${campPath}"><spring:message code="emissorcert.form.camp.nom"/></label>
						<label>&nbsp;</label>
						<pbl:inputText name="nom" inline="true" placeholderKey="emissorcert.form.camp.nom"/>
<%-- 					<c:set var="campPath" value="nom"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="emissorcert.form.camp.nom"/> *</label> --%>
<!-- 					<div class="controls"> -->
<%-- 						<form:input path="${campPath}" cssClass="input-lg" id="${campPath}"/> --%>
<%-- 						<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 					</div> -->
<!-- 				</div> -->
					</div>
			
					<div class="col-md-3">
					
					<label class="control-label" for="${campPath}"><spring:message code="emissorcert.form.camp.data.baixa"/></label>
					<label>&nbsp;</label>
					<pbl:inputDate name="dataBaixa" inline="true" placeholderKey="emissorcert.form.camp.data.baixa"/>
				
<%-- 					<c:set var="campPath" value="dataBaixa"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<!-- 					<div class="controls"> -->
<!-- 						<div class="input-append" style="width: 100%;"> -->
<%-- 							<form:input --%>
<%-- 								path="${campPath}" --%>
<%-- 								cssClass="form-control datepicker" --%>
<%-- 								id="${campPath}" --%>
<%-- 								disabled="false" --%>
<%-- 								data-toggle="datepicker" --%>
<%-- 								data-idioma="${idioma}" --%>
<%-- 								style="width: calc(100% - 40px);"/> --%>
<!-- 							<button id="btn-calendar-baixa" class="btn" type="button"><i class="icon-calendar"></i></button> -->
<!-- 						</div> -->
<%-- 						<form:errors path="${campPath}" cssClass="help-block"/> --%>
						</div>
			
					
			
			
						<div class="col-md-3">
							<label class="control-label" for="${campPath}"><spring:message code="emissorcert.form.camp.cif"/> *</label>
							<pbl:inputText name="cif" inline="true" placeholderKey="emissorcert.form.camp.cif"/>
<%-- 							<c:set var="campPath" value="cif"/> --%>
<%-- 							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 								<div class="controls"> -->
<%-- 									<form:input path="${campPath}" cssClass="input-lg" id="${campPath}"/> --%>
<%-- 									<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 								</div> -->
<!-- 							</div> -->
<!-- 						</div> -->
						</div>
					
						<lablel>&nbsp;</lablel>
						
						<div class="col-md-3">
							<div class="pull-right">	
							<button type="submit" class="btn btn-primary" ><spring:message code="comu.boto.guardar" /></button>
							<a href="<c:url value="/scsp/emissorcert"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
							</div>
						</div>	
	
					</div>
			</div>
			
		</fieldset>
		
	
	</form:form>

</body>
</html>
