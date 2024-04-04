<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
	pageContext.setAttribute("isRolActualAdministrador", es.caib.pinbal.webapp.common.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute("isRolActualSuperauditor", es.caib.pinbal.webapp.common.RolHelper.isRolActualSuperauditor(request));
%>
<html>
<head>
	<title><spring:message code="usuari.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="application/javascript">
		$(document).ready(function() {
			$('#entitatId').change(function() {
				let procedimentsPerEntitatUrl = '<c:url value="/procedimentajax/entitat/"/>' + $('#entitatId').val() + '/procediment';
				if ($('#entitatId').val() == '') {
					procedimentsPerEntitatUrl = '<c:url value="/procedimentajax/procediment/"/>';
				}
				$.ajax({
					url:procedimentsPerEntitatUrl,
					type:'GET',
					dataType: 'json',
					success: function(json) {
						$('#procedimentId').empty();
						$('#procedimentId').append($('<option>'));
						$.each(json, function(i, value) {
							$('#procedimentId').append($('<option>').text(value.codi + " - " + value.nom).attr('value', value.id));
						});
						$('#procedimentId').trigger('change');
					}
				});
			});
			$('#procedimentId').change(function() {
				let serveisPerProcedimentsUrl = '<c:url value="/serveiajax/procediment/"/>' + $('#procedimentId').val() + '/servei';
				if ($('#procedimentId').val() == '') {
					serveisPerProcedimentsUrl = '<c:url value="/serveiajax/servei/"/>';
				}
				$.ajax({
					url:serveisPerProcedimentsUrl,
					type:'GET',
					dataType: 'json',
					success: function(json) {
						$('#serveiCodi').empty();
						$('#serveiCodi').append($('<option>'));
						$.each(json, function(i, value) {
							$('#serveiCodi').append($('<option>').text(value.codi + " - " + value.descripcio).attr('value', value.codi));
						});
					}
				});
			});
		});
	</script>
	<style>
		legend { color: #484848; font-weight: bold; padding-top: 20px; }
	</style>
</head>
<body>
	<c:url value="/modal/usuari/configuracio" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariCommand" role="form">
		<form:hidden path="codi"/>
		<pbl:inputText name="nom" textKey="usuari.form.camp.nom" labelSize="2" disabled="true"/>
		<pbl:inputText name="nif" textKey="usuari.form.camp.nif" labelSize="2" disabled="true"/>
		<pbl:inputText name="email" textKey="usuari.form.camp.email" labelSize="2" disabled="true"/>
		<pbl:inputSelect name="rols" textKey="usuari.form.camp.rols" optionItems="${usuariCommand.rols}" labelSize="2" disabled="true"/>
		<pbl:inputSelect name="idioma" optionItems="${idiomaEnumOptions}" textKey="usuari.form.camp.idioma" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2" disabled="false"/>
		<c:if test="${not isRolActualAdministrador and not isRolActualSuperauditor and usuariCommand.hasMultiplesEntitats}">
			<fieldset>
				<legend><spring:message code="usuari.form.valors.defecte"/>: <spring:message code="usuari.form.valors.defecte.entitat"/></legend>
				<pbl:inputSelect name="entitatId"
								 optionItems="${entitats}"
								 optionValueAttribute="id"
								 optionTextAttribute="nom"
								 textKey="usuari.form.camp.entitat"
								 emptyOption="true"
								 labelSize="2"
								 optionMinimumResultsForSearch="0"/>
			</fieldset>
		</c:if>
		<fieldset>
			<legend><spring:message code="usuari.form.valors.defecte"/>: <spring:message code="usuari.form.valors.defecte.consulta.filtre"/></legend>
			<c:if test="${isRolActualAdministrador or isRolActualSuperauditor}">
				<pbl:inputSelect name="entitatId"
								 optionItems="${entitats}"
								 optionValueAttribute="id"
								 optionTextAttribute="nom"
								 textKey="usuari.form.camp.entitat"
								 emptyOption="true"
								 labelSize="2"
								 optionMinimumResultsForSearch="0"/>
				</c:if>
			<pbl:inputSelect name="procedimentId"
							 optionItems="${procediments}"
							 optionValueAttribute="id"
							 optionTextAttribute="codiNom"
							 textKey="usuari.form.camp.procediment"
							 placeholderKey="consulta.list.filtre.procediment"
							 emptyOption="true"
							 labelSize="2"
							 optionMinimumResultsForSearch="0"/>
			<pbl:inputSelect name="serveiCodi"
							 optionItems="${serveis}"
							 optionValueAttribute="codi"
							 optionTextAttribute="codiNom"
							 textKey="usuari.form.camp.servei"
							 placeholderKey="consulta.list.filtre.servei"
							 emptyOption="true"
							 labelSize="2"
							 optionMinimumResultsForSearch="0"/>
		</fieldset>
	<fieldset>
		<legend><spring:message code="usuari.form.valors.defecte"/>: <spring:message code="usuari.form.valors.defecte.consulta.form"/></legend>
		<pbl:inputText name="departament" textKey="consulta.form.camp.departament" labelSize="2"/>
		<pbl:inputTextarea name="finalitat" textKey="consulta.form.camp.finalitat" labelSize="2"/>
	</fieldset>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-save"></span>
				<c:choose>
					<c:when test="${empty usuariCommand.codi}"><spring:message code="comu.boto.crear"/></c:when>
					<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
				</c:choose>
			</button>
			<a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>
