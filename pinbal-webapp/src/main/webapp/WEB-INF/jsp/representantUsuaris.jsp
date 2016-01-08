<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%
	request.setAttribute(
			"caracterTipusNif",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_NIF);
	request.setAttribute(
			"caracterTipusCodi",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_CODI);
	request.setAttribute(
			"caracterRolDelegat",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_DELEGAT);
	request.setAttribute(
			"caracterRolRepresentant",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_REPRESENTANT);
	request.setAttribute(
			"caracterRolAplicacio",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_APLICACIO);
%>

<html>
<head>
	<title><spring:message code="representant.usuaris.titol"/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
<script>
$(document).ready(function() {
	$('#netejar-filtre').click(function() {
		$(':input', $('#form-filtre')).each (function() {
			var type = this.type, tag = this.tagName.toLowerCase();
			if (type == 'text' || type == 'password' || tag == 'textarea')
				this.value = '';
			else if (type == 'checkbox' || type == 'radio')
				this.checked = false;
			else if (tag == 'select')
				this.selectedIndex = 0;
		});
		$('#form-filtre').submit();
	});
	$('select[name="tipus"]', $('form#modal-form')).change(function() {
		if (this.value == '${caracterTipusNif}') {
			$('#modal-group-codi').addClass('hide');
			$('#modal-group-nif').removeClass('hide');
		} else if (this.value == '${caracterTipusCodi}') {
			$('#modal-group-codi').removeClass('hide');
			$('#modal-group-nif').addClass('hide');
		}
	});
});
function showModalCrear() {
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="representant.usuaris.titol.crear"/>");
	$('#modal-hidden-codi').attr('disabled', 'disabled');
	$('#modal-hidden-nif').attr('disabled', 'disabled');
	$('#modal-group-tipus').removeClass('hide');
	$('#modal-select-tipus').removeAttr('disabled');
	$('#modal-select-tipus').val('${caracterTipusNif}');
	$('#modal-input-codi').removeAttr('disabled');
	$('#modal-input-codi').val('');
	$('#modal-input-nif').removeAttr('disabled');
	$('#modal-input-nif').val('');
	$('#modal-group-codi').addClass('hide');
	$('#modal-group-nif').removeClass('hide');
	$('#modal-input-departament').val('');
	$('#modal-input-representant').prop('checked', false);
	$('#modal-input-delegat').prop('checked', false);
	$('#modal-input-aplicacio').prop('checked', false);
	$('#modal-form-usuari').modal('toggle');
}
function showModalEditar(
		inicialitzat,
		noInicialitzatNif,
		noInicialitzatCodi,
		codi,
		nif,
		departament,
		representant,
		delegat,
		aplicacio) {
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="representant.usuaris.titol.modificar"/>");
	$('#modal-hidden-codi').removeAttr('disabled');
	$('#modal-hidden-codi').val(codi);
	$('#modal-hidden-nif').removeAttr('disabled');
	$('#modal-hidden-nif').val(nif);
	$('#modal-select-tipus').attr('disabled', 'disabled');
	$('#modal-input-codi').attr('disabled', 'disabled');
	$('#modal-input-nif').attr('disabled', 'disabled');
	$('#modal-group-tipus').addClass('hide');
	if (inicialitzat) {
		$('#modal-group-codi').removeClass('hide');
		$('#modal-input-codi').val(codi);
		$('#modal-group-nif').removeClass('hide');
		$('#modal-input-nif').val(nif);
	} else {
		if (noInicialitzatNif) {
			$('#modal-group-codi').addClass('hide');
			$('#modal-group-nif').removeClass('hide');
			$('#modal-input-nif').val(nif);
		} else if (noInicialitzatCodi) {
			$('#modal-group-codi').removeClass('hide');
			$('#modal-group-nif').addClass('hide');
			$('#modal-input-codi').val(codi);
		}
	}
	$('#modal-input-departament').val(departament);
	$('#modal-input-representant').prop('checked', representant);
	$('#modal-input-delegat').prop('checked', delegat);
	$('#modal-input-aplicacio').prop('checked', aplicacio);
	$('#modal-form-usuari').modal('toggle');
}
</script>
</head>
<body>

	<c:url value="/representant/usuari" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-inline" commandName="usuariFiltreCommand">
		<c:set var="campPath" value="codi"/>
		<spring:message var="placeholderCodi" code="representant.usuaris.filtre.camp.codi"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderCodi}"/>
		<c:set var="campPath" value="nif"/>
		<spring:message var="placeholderNif" code="representant.usuaris.filtre.camp.nif"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderNif}"/>
		<c:set var="campPath" value="nom"/>
		<spring:message var="placeholderNom" code="representant.usuaris.filtre.camp.nom"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderNom}"/>
		<c:set var="campPath" value="departament"/>
		<spring:message var="placeholderDepartament" code="representant.usuaris.filtre.camp.departament"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderDepartament}"/>
		<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
	</form:form>

	<div class="row-fluid">
		<div class="span12">
			<a class="btn pull-right" href="#modal-form-usuari" onclick="showModalCrear()"><i class="icon-plus"></i>&nbsp;<spring:message code="entitat.usuaris.boto.nou.usuari"/></a>
		</div>
		<div class="clearfix"></div>
	</div>

	<div style="margin-top:8px;padding:4px 0">
		<jmesa:tableModel
				id="usuaris" 
				items="${entitat.usuarisRepresentant}"
				view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
				var="registre"
				maxRows="${fn:length(entitat.usuarisRepresentant)}">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="usuari.nom" titleKey="representant.usuaris.camp.usuari" sortable="false">
						${registre.usuari.descripcio}
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="departament" titleKey="representant.usuaris.camp.departament" sortable="false">
						${registre.departament}
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="rols" titleKey="representant.usuaris.camp.rols" sortable="false">
						<c:if test="${registre.representant}"><span class="label"><spring:message code="representant.usuaris.rol.repres"/></span></c:if>
						<c:if test="${registre.delegat}"><span class="label"><spring:message code="representant.usuaris.rol.deleg"/></span></c:if>
						<c:if test="${registre.aplicacio}"><span class="label"><spring:message code="representant.usuaris.rol.aplic"/></span></c:if>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="principal" title="&nbsp;">
						<c:if test="${registre.usuari.inicialitzat or registre.usuari.noInicialitzatCodi}">
							<a class="btn" href="<c:url value="/representant/usuari/${registre.usuari.codi}/permis"/>"><i class="icon-lock"></i>&nbsp;<spring:message code="comu.boto.permisos"/></a>
						</c:if>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_update" title="&nbsp;" sortable="false">
						<c:choose>
							<c:when test="${registre.principal}">
								<a class="btn disabled" href="#"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
							</c:when>
							<c:otherwise>
								<c:set var="onclickShowModal">showModalEditar(${registre.usuari.inicialitzat}, ${registre.usuari.noInicialitzatNif}, ${registre.usuari.noInicialitzatCodi}, '${registre.usuari.codi}', '${registre.usuari.nif}', '${fn:replace(registre.departament, "'", "\\'")}', ${registre.representant}, ${registre.delegat}, ${registre.aplicacio})</c:set>
								<a class="btn" href="#modal-form-usuari" onclick="${onclickShowModal}"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
							</c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
				</jmesa:htmlRow>
			</jmesa:htmlTable>
		</jmesa:tableModel>
	</div>

	<div id="modal-form-usuari" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="representant.usuaris.titol.modificar"/></h3>
		</div>
		<div class="modal-body">
			<c:url value="/representant/usuari/save" var="formAction"/>
			<form id="modal-form" action="${formAction}" method="post" class="form-horizontal">
				<input type="hidden" id="modal-hidden-id" name="id" value="${entitat.id}"/>
				<input type="hidden" id="modal-hidden-codi" name="codi"/>
				<input type="hidden" id="modal-hidden-nif" name="nif"/>
				<div id="modal-group-tipus" class="control-group">
    				<label class="control-label" for="modal-select-tipus"><spring:message code="representant.usuaris.camp.tipus"/></label>
					<div class="controls">
						<select id="modal-select-tipus" name="tipus" class="input-medium">
							<option value="${caracterTipusNif}"><spring:message code="representant.usuaris.tipus.nif"/></option>
							<option value="${caracterTipusCodi}"><spring:message code="representant.usuaris.tipus.codi"/></option>
						</select>
					</div>
				</div>
				<div id="modal-group-codi" class="control-group">
    				<label class="control-label" for="modal-input-codi"><spring:message code="representant.usuaris.camp.codi"/></label>
					<div class="controls">
						<input type="text" id="modal-input-codi" name="codi" disabled="disabled"/>
					</div>
				</div>
				<div id="modal-group-nif" class="control-group">
    				<label class="control-label" for="modal-input-nif"><spring:message code="representant.usuaris.camp.nif"/></label>
					<div class="controls">
						<input type="text" id="modal-input-nif" name="nif" disabled="disabled"/>
					</div>
				</div>
				<div class="control-group">
    				<label class="control-label" for="modal-input-departament"><spring:message code="representant.usuaris.camp.departament"/></label>
					<div class="controls">
						<input type="text" id="modal-input-departament" name="departament"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="modal-input-representant"><spring:message code="representant.usuaris.camp.rols"/></label>
					<div class="controls">
						<label class="checkbox" for="modal-input-representant">
	    					<input type="checkbox" id="modal-input-representant" name="rolRepresentant">
	    					<spring:message code="representant.usuaris.rol.repres"/>
	    				</label>
	    				<label class="checkbox" for="modal-input-delegat">
	    					<input type="checkbox" id="modal-input-delegat" name="rolDelegat">
	    					<spring:message code="representant.usuaris.rol.deleg"/>
	    				</label>
	    				<label class="checkbox" for="modal-input-aplicacio">
	    					<input type="checkbox" id="modal-input-aplicacio" name="rolAplicacio">
	    					<spring:message code="representant.usuaris.rol.aplic"/>
	    				</label>
	    			</div>
    			</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" class="btn btn-primary" onclick="$('#modal-form').submit()"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>

</body>
</html>
