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
			"caracterRolAuditor",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_AUDITOR);
%>

<html>
<head>
	<title><spring:message code="auditor.usuaris.titol"/></title>
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
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="auditor.usuaris.titol.crear"/>");
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
	$('#modal-input-auditor').prop('checked', true);
	$('#modal-form-usuari').modal('toggle');
}
function showModalEditar(
	inicialitzat,
	noInicialitzatNif,
	noInicialitzatCodi,
	codi,
	nif,
	departament,
	auditor) {
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="auditor.usuaris.titol.modificar"/>");
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
	$('#modal-input-auditor').prop('checked', auditor);
	$('#modal-form-usuari').modal('toggle');
}
</script>
</head>
<body>

	<c:url value="/auditor/usuari" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well-sm form-block" commandName="usuariFiltreCommand">
		<c:set var="campPath" value="codi"/>
		<spring:message var="placeholderCodi" code="auditor.usuaris.filtre.camp.codi"/>
		<form:input path="${campPath}" cssClass="input-sm" id="${campPath}" placeholder="${placeholderCodi}"/>
		<c:set var="campPath" value="nif"/>
		<spring:message var="placeholderNif" code="auditor.usuaris.filtre.camp.nif"/>
		<form:input path="${campPath}" cssClass="input-sm" id="${campPath}" placeholder="${placeholderNif}"/>
		<c:set var="campPath" value="nom"/>
		<spring:message var="placeholderNom" code="auditor.usuaris.filtre.camp.nom"/>
		<form:input path="${campPath}" cssClass="input-sm" id="${campPath}" placeholder="${placeholderNom}"/>
		<button id="netejar-filtre" class="btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
	</form:form>

	<div class="container-fluid">
		<div class="col-md-12">
			<a class="btn col-md-pull-right" href="#modal-form-usuari" onclick="showModalCrear()"><i class="glyphicons-plus"></i>&nbsp;<spring:message code="entitat.usuaris.boto.nou.usuari"/></a>
		</div>
		<div class="clearfix"></div>
	</div>
	
	<div style="margin-top:8px;padding:4px 0">
		<jmesa:tableModel
				id="usuaris" 
				items="${entitat.usuarisAuditor}"
				view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
				var="registre"
				maxRows="${fn:length(entitat.usuarisAuditor)}">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="usuari.nom" titleKey="auditor.usuaris.camp.usuari" sortable="false">
						${registre.usuari.descripcio}
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="rols" titleKey="auditor.usuaris.camp.rols" sortable="false">
						<c:if test="${registre.auditor}"><span class="label"><spring:message code="auditor.usuaris.rol.audit"/></span></c:if>
					</jmesa:htmlColumn>
					<%--jmesa:htmlColumn property="principal" titleKey="auditor.usuaris.camp.principal">
						<c:if test="${registre.principal}"><i class="icon-certificate"></i></c:if>
					</jmesa:htmlColumn--%>
					<jmesa:htmlColumn property="ACCIO_update" title="&nbsp;" sortable="false">
						<c:choose>
							<c:when test="${registre.principal}">
								<a class="btn disabled" href="#"><i class="glyphicons-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
							</c:when>
							<c:otherwise>
								<c:set var="onclickShowModal">showModalEditar(${registre.usuari.inicialitzat}, ${registre.usuari.noInicialitzatNif}, ${registre.usuari.noInicialitzatCodi}, '${registre.usuari.codi}', '${registre.usuari.nif}', '${fn:replace(registre.departament, "'", "\\'")}', ${registre.auditor})</c:set>
								<a class="btn-default" href="#modal-editar-departament" onclick="${onclickShowModal}"><i class="glyphicons-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
							</c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
	            </jmesa:htmlRow>
	        </jmesa:htmlTable>
		</jmesa:tableModel>
	</div>

	<div id="modal-content-usuari" class="modal hidden fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="auditor.usuaris.titol.modificar"/></h3>
		</div>
		<div class="modal-content">
			<c:url value="/auditor/usuari/save" var="formAction"/>
			<form id="modal-form" action="${formAction}" method="post" class="form-horizontal">
				<input type="hidden" id="modal-hidden-id" name="id" value="${entitat.id}"/>
				<input type="hidden" id="modal-hidden-codi" name="codi"/>
				<input type="hidden" id="modal-hidden-nif" name="nif"/>
				<div id="modal-group-tipus" class="form-group">
    				<label class="control-label" for="modal-select-tipus"><spring:message code="auditor.usuaris.camp.tipus"/></label>
					<div class="controls">
						<select id="modal-select-tipus" name="tipus" class="input-sm">
							<option value="${caracterTipusNif}"><spring:message code="auditor.usuaris.tipus.nif"/></option>
							<option value="${caracterTipusCodi}"><spring:message code="auditor.usuaris.tipus.codi"/></option>
						</select>
					</div>
				</div>
				<div id="modal-group-codi" class="from-group">
    				<label class="control-label" for="modal-input-codi"><spring:message code="auditor.usuaris.camp.codi"/></label>
					<div class="controls">
						<input type="text" id="modal-input-codi" name="codi" disabled="disabled"/>
					</div>
				</div>
				<div id="modal-group-nif" class="form-group">
    				<label class="control-label" for="modal-input-nif"><spring:message code="auditor.usuaris.camp.nif"/></label>
					<div class="controls">
						<input type="text" id="modal-input-nif" name="nif" disabled="disabled"/>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label" for="modal-input-auditor"><spring:message code="auditor.usuaris.camp.rols"/></label>
					<div class="controls">
	    				<label class="checkbox" for="modal-input-auditor">
	    					<input type="checkbox" id="modal-input-auditor" name="rolAuditor">
	    					<spring:message code="auditor.usuaris.rol.audit"/>
	    				</label>
	    			</div>
    			</div>
			</form>
		</div>
		<div class="modal-content">
			<a href="#" class="btn-default" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" class="btn btn-primary" onclick="$('#modal-form').submit()"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>

</body>
</html>
