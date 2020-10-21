<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	pageContext.setAttribute(
			"consentimentValors",
			es.caib.pinbal.core.dto.ConsultaDto.Consentiment.values());
%>

<c:set var="serveiMultiple" value="${servei.consultaMultiplePermesa}"/>
<c:set var="tabSimpleActiu" value="${not consultaCommand.multiple}"/>

<html>
<head>
	<title><spring:message code="consulta.form.titol" arguments="${servei.descripcio}"/></title>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

	<script src="<c:url value="/js/bootstrap.file-input.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>

	<link href="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/css/jasny-bootstrap.min.css"/>" rel="stylesheet"> 
	<script src="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/js/jasny-bootstrap.min.js"/>"></script> 

</head>
<body>

	<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/Excel" var="downloadPlantillaExcelUrl"/>
	<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/CSV" var="downloadPlantillaCsvUrl"/>
	<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/ODS" var="downloadPlantillaOdsUrl"/>
	<c:url value="/consulta/${servei.codiUrlEncoded}/new" var="formAction"/>
	<div class="container-fluid">
	<form:form action="${formAction}" method="post" cssClass="" commandName="consultaCommand" enctype="multipart/form-data">
		<form:hidden path="serveiCodi"/>
		<form:hidden path="multiple" />
		<br/>
		<c:set var="campPath" value="procedimentId"/>
		<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
		<div class="row">
			<div class="col-md-6">
				 <pbl:inputSelect name="${campPath}" inline="true"
			 					  optionItems="${procediments}" 
								  optionValueAttribute="id"
								  optionTextAttribute="nom"
							      emptyOption="false"/>
			</div>
		</div>
			
		<fieldset>
			<legend><spring:message code="consulta.form.dades.generiques"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nom"/> *</label>
					<pbl:inputText name="funcionariNom" inline="true"/>
					
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.nom"/></label> 
					<pbl:inputText name="entitatNom" inline="true" disabled="true"/>
					
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.consentiment"/> *</label>
					<pbl:inputSelect name="consentiment" inline="true" optionItems="${consentimentValors}" emptyOption="false"/>
				</div>

				<div class="col-md-6">			
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nif"/></label>
					<pbl:inputText name="funcionariNif" inline="true"/>
					
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.cif"/></label> 
					<pbl:inputText name="entitatCif" inline="true"/>
					
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.departament"/> *</label>
					<pbl:inputText name="departamentNom" inline="true"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<c:set var="campPath" value="finalitat"/>
					<pbl:inputTextarea name="${campPath}" required="true" inline="true" textKey="consulta.form.camp.finalitat"/>
				</div>
			</div>
		</fieldset>

		<c:if test="${serveiMultiple}">
			<ul id="tabs-simple-multiple" class="nav nav-tabs">
				<li<c:if test="${tabSimpleActiu}"> class="active"</c:if>><a href="#tab-simple" data-toggle="tab"><spring:message code="consulta.form.tipus.simple"/></a></li>
	 			<li<c:if test="${tabMultipleActiu}"> class="active"</c:if>><a href="#tab-multiple" data-toggle="tab"><spring:message code="consulta.form.tipus.multiple"/></a></li>
	 		</ul>
	 		<div class="tab-content" style="margin-top: 15px;">
	 			<div class="tab-pane<c:if test="${tabSimpleActiu}"> active</c:if>" id="tab-simple">
	 				<jsp:include page="import/consultaSimpleForm.jsp"/>
	 			</div>
				<div class="tab-pane"<c:if test="${tabMultipleActiu}"> active</c:if>" id="tab-multiple">
					<div class="container-fluid">
					<c:if test="${not empty consultaCommand.multipleErrorsValidacio}">
						<div id="errorsFitxer" class="errorsFitxer well alert-danger">
							<h4><spring:message code="consulta.form.camp.multiple.errors.fitxer"/></h4>
						<c:forEach items="${consultaCommand.multipleErrorsValidacio}" var="error">
							<p style="margin-left:20px">${error}</p>
						</c:forEach>
						</div>
					</c:if>
					
					<div class="row">
						<div class="col-md-12">
						<br />
							<p>	<spring:message code="consulta.form.multiple.info.titular"/><br />
							<spring:message code="consulta.form.multiple.info.plantilla"/>
							</p>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for="plantilla"><spring:message code="consulta.form.camp.multiple.plantilla"/></label>
								<div class="controls">
									<a href="${downloadPlantillaExcelUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.excel" />">
										<i class="far fa-file-excel"></i> <spring:message code="consulta.form.camp.multiple.fitxer.excel" />
									</a>
									<a href="${downloadPlantillaCsvUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.csv" />">
										<i class="fas fa-file-csv"></i> <spring:message code="consulta.form.camp.multiple.fitxer.csv" />
									</a>
									<a href="${downloadPlantillaOdsUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.ods" />">
										<i class="fas fa-file-word"></i> <spring:message code="consulta.form.camp.multiple.fitxer.ods" />
									</a>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<c:set var="campPath" value="multipleFitxer"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
								<pbl:inputFile inline="false" name="${campPath}" textKey="consulta.form.camp.multiple.fitxer" required="true"/>
								<form:errors path="${campPath}" cssClass="help-block"/>
							</div>
						</div>
					</div>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${not serveiMultiple}">
			<jsp:include page="import/consultaSimpleForm.jsp"/>
		</c:if>
		<div class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/consulta"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
			<c:if test="${not empty servei.ajuda or not empty servei.fitxerAjudaNom}">
				<a href="#modalAjuda" class="btn btn-default" data-toggle="modal"><spring:message code="comu.boto.ajuda"/></a>
			</c:if>
		</div>
	</form:form>
	</div>
	<!-- Modal ajuda-->
	<div id="modalAjuda" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  		<div class="modal-dialog" role="document">
    		<div class="modal-content">
				<div class="modal-header">
			    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			    	<h3 id="myModalLabel"><spring:message code="comu.boto.ajuda"/></h3>
			  	</div>
			  	<div class="modal-body">
			  		${servei.ajuda}
			  	</div>
			  	<div class="modal-footer">
			  		<c:if test="${not empty servei.fitxerAjudaNom}">
			  			<a href="<c:url value='/consulta/${servei.codiUrlEncoded}/downloadAjuda'/>" class="btn btn-primary pull-left"><i class="fas fa-file-download"></i> <spring:message code="comu.boto.document.ajuda"/></a>
			  		</c:if>
			    	<button class="btn btn-default" data-dismiss="modal" aria-hidden="true"><spring:message code="comu.boto.tancar"/></button>
			  	</div>
			</div>
	  	</div>
	</div>
	<!-- Fi modal -->

</body>
</html>
