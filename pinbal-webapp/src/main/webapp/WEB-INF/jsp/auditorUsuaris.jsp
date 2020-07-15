<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
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

    $('#table-users').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>',
        },
		ajax: '<c:url value="/auditor/usuari/datatable"/>',
		columnDefs: [
			{ 
	            targets: 0,
	            orderable: false,
				render: function (data, type, row, meta) {
					var template = $('#template-usuari').html();
					return Mustache.render(template, row);
				}
	        },
	        {
	            targets: 1,
	            orderable: false,
	            width: "20%",
				render: function (data, type, row, meta) {
					var template = $('#template-rols').html();
					return Mustache.render(template, row);
				}
	        },
			{
				targets: 2,
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
					var template = $('#template-actions').html();
					row['nrow'] = meta['row'];
					return Mustache.render(template, row);
				}
			}, 
			{
				targets: [3, 4],
				orderable: false,
				visible:false
			}, 
	   ],
	   initComplete: function( settings, json ) {
		   console.log(settings)
		   console.log(json)
			$('.btn-open-modal-edit').click(function() {
				var nrow = $(this).data('nrow');
				var row = json.data[nrow];
				var usuari = row.usuari;
				console.log(usuari);
		 		showModalEditar(usuari.inicialitzat, usuari.noInicialitzatNif, 
		 				usuari.noInicialitzatCodi, usuari.descripcio, 
		 				usuari.codi, usuari.nif, 
		 				row.departament, 
		 				row.representant, 
		 				row.delegat, row.aplicacio);
			});
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
	<table id="table-users" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
			<th data-data="usuari.nom"><spring:message code="entitat.usuaris.camp.usuari" /></th>
			<th data-data="representant"><spring:message code="auditor.usuaris.camp.rols" /></th>
			<th data-data="principal"></th>
			<th data-data="aplicacio"></th>
			<th data-data="auditor"></th>
			</tr>
		</thead>
	</table>	
	
<script id="template-usuari" type="x-tmpl-mustache">
	{{ usuari.descripcio }}
</script>
<script id="template-rols" type="x-tmpl-mustache">

	{{#auditor}}
		<span class="badge"><spring:message code="auditor.usuaris.rol.audit"/></span>
	{{/auditor}}
</script>
<script id="template-actions" type="x-tmpl-mustache">
{{#principal}}
 	<a class="btn btn-primary disabled" href="#"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
{{/principal}}
{{^principal}}
	<a data-nrow="{{ nrow }}" class="btn-open-modal-edit btn btn-primary"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
{{/principal}}
</script>

<div id="modal-form-usuari" class="modal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
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
	</div>
	</div>

</body>
</html>
