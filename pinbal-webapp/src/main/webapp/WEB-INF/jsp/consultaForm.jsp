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
	pageContext.setAttribute(
			"documentTipusValors",
			es.caib.pinbal.core.dto.ConsultaDto.getDocumentTipusValorsPerFormulari());
	java.util.Map<?,?> map = (java.util.Map<?,?>)request.getAttribute("campsDadesEspecifiquesAgrupats");
	if (map != null)
		pageContext.setAttribute("campsSenseAgrupar", map.get(null));
%>

<c:set var="serveiMultiple" value="${servei.consultaMultiplePermesa}"/>
<c:set var="tabSimpleActiu" value="${not consultaCommand.multiple}"/>

<html>
<head>
	<title><spring:message code="consulta.form.titol" arguments="${servei.descripcio}"/></title>
	<script src="<c:url value="/js/bootstrap.file-input.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>	
	
	
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
	

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>

	<c:if test="${serveiMultiple}">
	<script>
		$(document).ready(function() {
			$('#tabs-simple-multiple a:first').click(function (e) {
				$("#multiple").val('false');
			});
			$('#tabs-simple-multiple a:last').click(function (e) {
				$("#multiple").val('true');
			});
			$('#multipleFitxer').attr("title", "<i class='icon-file'></i> <spring:message code='consulta.form.camp.multiple.fitxer.examinar'/>");
			$('#multipleFitxer').bootstrapFileInput();
			$('#multipleFitxer').attr("title", "<spring:message code='consulta.form.camp.multiple.fitxer.examinar'/>");
		});
	</script>
	</c:if>
</head>
<body>

	<c:url value="/consulta/${servei.codi}/plantilla/Excel" var="downloadPlantillaExcelUrl"/>
	<c:url value="/consulta/${servei.codi}/plantilla/CSV" var="downloadPlantillaCsvUrl"/>
	<c:url value="/consulta/${servei.codi}/plantilla/ODS" var="downloadPlantillaOdsUrl"/>
	<c:url value="/consulta/${servei.codi}/new" var="formAction"/>
	
	<form:form action="${formAction}" method="post" cssClass="well" commandName="consultaCommand" enctype="multipart/form-data">
		<form:hidden path="serveiCodi"/>
		<form:hidden path="multiple" />
		<br/>
		<c:set var="campPath" value="procedimentId"/>
		<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-6">
					 <pbl:inputSelect name="${campPath}" inline="true" placeholderKey="consulta.form.camp.procediment"
				 					optionItems="${procediments}" 
									 optionValueAttribute="id"
									 optionTextAttribute="nom"
									 emptyOption="true"/>
<%-- 						<div class="form-group"<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.procediment"/> *</label> --%>
<!-- 							<div class="controls"> -->
<%-- 								<form:select path="${campPath}" cssClass="col-md-12" cssStyle="width:100%" id="${campPath}"> --%>
<%-- 									<form:options items="${procediments}" itemLabel="nom" itemValue="id"/> --%>
<%-- 								</form:select> --%>
<%-- 								<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 							</div> -->
					</div>
				</div>
			</div>
			
		<fieldset>
			<legend><spring:message code="consulta.form.dades.generiques"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row">
				<div class="col-md-6">
				<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nom"/> *</label>
					<pbl:inputText name="funcionariNom" inline="true" placeholderKey="consulta.form.camp.funcionari.nom"/>
<%-- 					<c:set var="campPath" value="funcionariNom"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="form-group" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
	
				<div class="col-md-6">
				
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nif"/></label>
						<pbl:inputText name="funcionariNif" inline="true" placeholderKey="consulta.form.camp.funcionari.nif"/>
					
<%-- 					<c:set var="campPath" value="funcionariNif"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nif"/> *</label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}" disabled="true"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
			</div>	
		
			<div class="container-fluid">
				<div class="row">
				<pbl>
					<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.nom"/></label> 
						<pbl:inputText name="entitatNom" inline="true" placeholderKey="consulta.form.camp.entitat.nom"/>
<%-- 					<c:set var="campPath" value="entitatNom"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.nom"/> *</label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}" disabled="true"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
						</div>
					</div>
				</div>
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.cif"/></label> 
					<pbl:inputText name="entitatCif" inline="true" placeholderKey="consulta.form.camp.entitat.cif"/>
<%-- 					<c:set var="campPath" value="entitatCif"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}" disabled="true"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
			</div>
		</div>	
			<div class="row">
				<div class="col-md-6">
				
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.consentiment"/> *</label>
					<pbl:inputSelect name="consentiment" inline="true" placeholderKey="consulta.form.camp.consentiment" 
								optionItems="${consentimentValors}"
								emptyOption="true"/>
				
<%-- 					<c:set var="campPath" value="consentiment"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<!-- 												<div class="controls"> -->
<%-- 							<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}"> --%>
<%-- 								<form:options items="${consentimentValors}"/> --%>
<%-- 							</form:select> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
					</div>
				</div>
				<div class="col-md-6">
					<c:set var="campPath" value="departamentNom"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.departament"/> *</label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<c:set var="campPath" value="finalitat"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.finalitat"/> *</label>
						<div class="controls">
							<form:textarea rows="8" path="${campPath}" cssClass="col-md-12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
				</div>
			</div>
		</fieldset>

		<c:if test="${serveiMultiple}">
		<ul id="tabs-simple-multiple" class="nav nav-tabs">
			<li<c:if test="${tabSimpleActiu}"> class="active"</c:if>><a href="#tab-simple" data-toggle="tab"><spring:message code="consulta.form.tipus.simple"/></a></li>
 			<li<c:if test="${tabMultipleActiu}"> class="active"</c:if>><a href="#tab-multiple" data-toggle="tab"><spring:message code="consulta.form.tipus.multiple"/></a></li>
 		</ul>
 		<div class="tab-content">
 			<div class="tab-pane<c:if test="${tabSimpleActiu}"> active</c:if>" id="tab-simple">
		</c:if>

		<div class="row">
			<div class="col-md-6">
				<c:set var="campPath" value="expedientId"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.expedient"/></label>
					<div class="controls">
						<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/>
						<form:errors path="${campPath}" cssClass="help-block"/>
					</div>
				</div>
			</div>
			<div class="col-md-12">
			</div>
		</div>
		<c:set var="mostrarDadesTitular" value="${servei.pinbalActiuCampNom or servei.pinbalActiuCampLlinatge1 or servei.pinbalActiuCampLlinatge2 or servei.pinbalActiuCampNomComplet or servei.pinbalActiuCampDocument}"/>
		<c:if test="${mostrarDadesTitular}">
			<fieldset>
				<legend><spring:message code="consulta.form.dades.titular"/></legend>
				<div class="clearfix legend-margin-bottom"></div>
				<c:if test="${servei.pinbalActiuCampDocument}">
					<div class="row">
						<div class="col-md-6">
							<c:set var="campPath" value="titularDocumentTipus"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.document.tipus"/> <c:if test="${servei.pinbalDocumentObligatori}">*</c:if></label>
								<div class="controls">
									<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}">
										<c:forEach var="documentTipusValor" items="${documentTipusValors}">
											<c:choose>
												<c:when test="${documentTipusValor == 'DNI'}"><c:set var="propietatActiu">pinbalPermesDocumentTipusDni</c:set></c:when>
												<c:when test="${documentTipusValor == 'NIF'}"><c:set var="propietatActiu">pinbalPermesDocumentTipusNif</c:set></c:when>
												<c:when test="${documentTipusValor == 'CIF'}"><c:set var="propietatActiu">pinbalPermesDocumentTipusCif</c:set></c:when>
												<c:when test="${documentTipusValor == 'NIE'}"><c:set var="propietatActiu">pinbalPermesDocumentTipusNie</c:set></c:when>
												<c:otherwise><c:set var="propietatActiu">pinbalPermesDocumentTipusPas</c:set></c:otherwise>
											</c:choose>
											<c:if test="${servei[propietatActiu]}">
												<form:option value="${documentTipusValor}">${documentTipusValor}</form:option>
											</c:if>
										</c:forEach>
									</form:select>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<c:set var="campPath" value="titularDocumentNum"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.document.num"/> <c:if test="${servei.pinbalDocumentObligatori}">*</c:if></label>
								<div class="controls">
									<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:set var="numColumnes" value="${2}"/>
				<c:set var="indexCamp" value="${0}"/>
				<c:forEach var="index" begin="0" end="3" varStatus="status">
					<c:choose>
						<c:when test="${index == 0}">
							<c:set var="campPath" value="titularNom"/>
							<spring:message var="campLabel" code="consulta.form.camp.nom"/>
							<c:set var="campServeiTest" value="pinbalActiuCampNom"/>
						</c:when>
						<c:when test="${index == 1}">
							<c:set var="campPath" value="titularLlinatge1"/>
							<spring:message var="campLabel" code="consulta.form.camp.llinatge1"/>
							<c:set var="campServeiTest" value="pinbalActiuCampLlinatge1"/>
						</c:when>
						<c:when test="${index == 2}">
							<c:set var="campPath" value="titularLlinatge2"/>
							<spring:message var="campLabel" code="consulta.form.camp.llinatge2"/>
							<c:set var="campServeiTest" value="pinbalActiuCampLlinatge2"/>
						</c:when>
						<c:when test="${index == 3}">
							<c:set var="campPath" value="titularNomComplet"/>
							<spring:message var="campLabel" code="consulta.form.camp.nomcomplet"/>
							<c:set var="campServeiTest" value="pinbalActiuCampNomComplet"/>
						</c:when>
					</c:choose>
					<c:if test="${servei[campServeiTest]}">
						<c:if test="${indexCamp == 0 or (indexCamp % numColumnes) == 0}">
							<div class="row">
						</c:if>
						<div class="col-md-6">
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label" for="${campPath}">${campLabel}</label>
								<div class="controls">
									<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>
						<c:if test="${status.last or (indexCamp % numColumnes) == (numColumnes - 1)}">
							</div>
						</c:if>
						<c:set var="indexCamp" value="${indexCamp + 1}"/>
					</c:if>
				</c:forEach>
			</fieldset>
		</c:if>
		<c:set var="mostrarDadesEspecifiques" value="${false}"/>
		<c:forEach var="camp" items="${campsDadesEspecifiques}">
			<c:if test="${camp.visible}"><c:set var="mostrarDadesEspecifiques" value="${true}"/></c:if>
		</c:forEach>
		<c:choose>
			<c:when test="${mostrarDadesEspecifiques}">
				<fieldset>
					<c:if test="${not empty campsSenseAgrupar}">
						<legend><spring:message code="consulta.form.dades.especifiques"/></legend>
						<div class="clearfix legend-margin-bottom"></div>
					</c:if>
					<c:set var="dadesEspecifiquesDisabled" value="${false}" scope="request"/>
					<c:set var="dadesEspecifiquesValors" value="${consultaCommand.dadesEspecifiques}" scope="request"/>
					<c:set var="campsPerMostrar" value="${campsSenseAgrupar}" scope="request"/>
					<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
					<c:forEach var="grup" items="${grups}">
						<fieldset>
						 	<legend>${grup.nom}</legend>
						 	<div class="clearfix legend-margin-bottom"></div>
						 	<c:set var="dadesEspecifiquesDisabled" value="${false}" scope="request"/>
						 	<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[grup.id]}" scope="request"/>
							<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
			   			</fieldset>
					</c:forEach>
				</fieldset>
			</c:when>
			<c:otherwise>
				<%-- No s'han de mostrar els camps de les dades específiques però s'han d'incloure com a camps ocults --%>
				<c:set var="dadesEspecifiquesValors" value="${consultaCommand.dadesEspecifiques}" scope="request"/>
				<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${serveiMultiple}">
			</div>
			<div class="tab-pane<c:if test="${tabMultipleActiu}"> active</c:if>" id="tab-multiple">
				<c:if test="${not empty consultaCommand.multipleErrorsValidacio}">
					<div id="errorsFitxer" class="errorsFitxer well well-small alert-danger">
						<h4><spring:message code="consulta.form.camp.multiple.errors.fitxer"/></h4>
					<c:forEach items="${consultaCommand.multipleErrorsValidacio}" var="error">
						<p style="margin-left:20px">${error}</p>
					</c:forEach>
					</div>
				</c:if>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group" style="padding-left:40px;">
							<spring:message code="consulta.form.multiple.info.titular"/><br />
							<spring:message code="consulta.form.multiple.info.plantilla"/>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label" for="plantilla"><spring:message code="consulta.form.camp.multiple.plantilla"/></label>
							<div class="controls">
								<a href="${downloadPlantillaExcelUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.excel" />">
									<i class="icon-download-alt"></i> <spring:message code="consulta.form.camp.multiple.fitxer.excel" />
								</a>
								<a href="${downloadPlantillaCsvUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.csv" />">
									<i class="icon-download-alt"></i> <spring:message code="consulta.form.camp.multiple.fitxer.csv" />
								</a>
								<a href="${downloadPlantillaOdsUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.ods" />">
									<i class="icon-download-alt"></i> <spring:message code="consulta.form.camp.multiple.fitxer.ods" />
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
							<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.multiple.fitxer"/> *</label>
							<div class="controls">
								<span class="input-group-btn">
									<form:input type="file" path="${campPath}" cssClass="" id="${campPath}" title="<i class='glyphicon-file'></i>" />
								</span>
								<form:errors path="${campPath}" cssClass="help-block"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		</c:if>
		<div class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/consulta"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
			<c:if test="${not empty servei.ajuda or not empty servei.fitxerAjudaNom}">
				<a href="#modalAjuda" class="btn btn-default col-lg-6" data-toggle="modal"><spring:message code="comu.boto.ajuda"/></a>
			</c:if>
		</div>
	</form:form>
	
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
		  			<a href="<c:url value='/consulta/${servei.codi}/downloadAjuda'/>" class="btn btn-primary pull-left"><i class='icon-download-alt icon-white'></i> <spring:message code="comu.boto.document.ajuda"/></a>
		  		</c:if>
		    	<button class="btn" data-dismiss="modal" aria-hidden="true"><spring:message code="comu.boto.tancar"/></button>
		  	</div>
			</div>
	  	</div>
	</div>
	<!-- Fi modal -->

</body>
</html>
