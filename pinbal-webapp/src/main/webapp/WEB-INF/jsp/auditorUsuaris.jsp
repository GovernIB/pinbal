<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

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
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
<script>
$(document).ready(function() {

	$("option[value='REPRESENTANT']").remove();
	$("option[value='DELEGAT']").remove();
	$("option[value='APLICACIO']").remove();

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


	$('input[type=radio][name=tipus]').on('change', function() {
		if ($(this).val() == '${caracterTipusNif}') {
			$('#modal-group-codi').addClass('hide');
			$('#modal-group-nif').removeClass('hide');
		} else if ($(this).val() == '${caracterTipusCodi}') {
			$('#modal-group-codi').removeClass('hide');
			$('#modal-group-nif').addClass('hide');
		}
	});


    $('#table-users').DataTable({
    	autoWidth: false,
    	paging: false,
		processing: true,
		serverSide: true,
		order: [],
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
				width: "1%",
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
				console.log(row);
		 		showModalEditar(
						usuari.inicialitzat,
						usuari.noInicialitzatNif,
		 				usuari.noInicialitzatCodi,
						usuari.descripcio,
		 				usuari.codi,
						usuari.nif,
		 				row.departament, 
		 				row.auditor);
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
	nom,
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
		$('#modal-group-nom').removeClass('hide');
		$('#modal-input-nom').val(nom);
		$('#modal-group-nif').removeClass('hide');
		$('#modal-input-nif').val(nif);
	} else {
		if (noInicialitzatNif) {
			$('#modal-group-codi').addClass('hide');
			$('#modal-group-nif').removeClass('hide');
			$('#modal-input-nif').val(nif);
		} else if (noInicialitzatCodi) {
			$('#modal-group-codi').removeClass('hide');
			$('#modal-group-nom').removeClass('hide');
			$('#modal-group-nif').addClass('hide');
			$('#modal-input-codi').val(codi);
			$('#modal-input-nom').val(nom);
		}
	}
	$('#modal-input-auditor').prop('checked', auditor);
	$('#modal-form-usuari').modal('toggle');
}
</script>
</head>
<body>

	<c:url value="/auditor/usuari" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-filtre-table" commandName="usuariFiltreCommand">
		<div class="row">
			<div class="col-md-3">			
				<pbl:inputText name="codi" inline="true"  placeholderKey="auditor.usuaris.filtre.camp.codi"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="nif" inline="true"  placeholderKey="auditor.usuaris.filtre.camp.nif"/>
				
			</div>
			<div class="col-md-2">
				<pbl:inputText name="nom" inline="true"  placeholderKey="auditor.usuaris.filtre.camp.nom"/>
			</div>
			
			<div class="col-md-2">
				<pbl:inputSelect 
					name="rol" 
					emptyOption="true" 
					inline="true"
					optionEnum="RolEnumDto"
					placeholderKey="representant.usuaris.filtre.camp.rol"/>
			</div>
			
			<div class="col-md-2">
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>	
	</form:form>

	<a class="btn btn-primary pull-right" onclick="showModalCrear()"><i class="fas fa-plus"></i>&nbsp;<spring:message code="entitat.usuaris.boto.nou.usuari"/></a>
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
 	<a class="btn btn-primary disabled"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
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
		<div class="modal-body">
			<c:url value="/auditor/usuari/save" var="formAction"/>
			<form id="modal-form" action="${formAction}" method="post" class="form-horizontal">
				<input type="hidden" id="modal-hidden-id" name="id" value="${entitat.id}"/>
				<input type="hidden" id="modal-hidden-codi" name="codi"/>
				<input type="hidden" id="modal-hidden-nif" name="nif"/>
				
				<div id="modal-group-tipus" class="form-group">
					<label class="control-label col-md-2" for="modal-select-tipus"><spring:message code="auditor.usuaris.camp.tipus"/></label>
					<div class="btn-group" data-toggle="buttons" id="modal-select-tipus" style="padding-left: 15px;">
						<label class="btn btn-default active">
							<input id="tipus1" name="tipus" type="radio" value="${caracterTipusNif}" checked="checked"> <spring:message code="auditor.usuaris.tipus.nif"/>
						</label>
						<label class="btn btn-default">
							<input id="tipus2" name="tipus" type="radio" value="${caracterTipusCodi}"> <spring:message code="auditor.usuaris.tipus.codi"/>
						</label>
					</div>
				</div>
				
				<div id="modal-group-codi" class="form-group">
    				<label class="control-label col-md-2" for="modal-input-codi"><spring:message code="auditor.usuaris.camp.codi"/></label>
					<div class="col-md-10">
						<input type="text" class="form-control" id="modal-input-codi" name="codi" disabled="disabled"/>
					</div>
				</div>
				<div id="modal-group-nif" class="form-group">
    				<label class="control-label col-md-2" for="modal-input-nif"><spring:message code="auditor.usuaris.camp.nif"/></label>
					<div class="col-md-10">
						<input type="text" class="form-control" id="modal-input-nif" name="nif" disabled="disabled"/>
					</div>
				</div>
				<div id="modal-group-nom" class="form-group">
					<label class="control-label col-md-2" for="modal-input-nom"><spring:message code="representant.usuaris.camp.nom"/></label>
					<div class="col-md-10">
						<input class="form-control" type="text" id="modal-input-nom" name="codi" disabled="disabled"/>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-md-2" for="modal-input-auditor"><spring:message code="auditor.usuaris.camp.rols"/></label>
					<div class="controls col-md-10">
	    				<label class="checkbox" for="modal-input-auditor">
	    					<input type="checkbox" id="modal-input-auditor" name="rolAuditor">
	    					<spring:message code="auditor.usuaris.rol.audit"/>
	    				</label>
	    			</div>
    			</div>
			</form>
		</div>
		<div class="modal-footer">
			<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></button>
			<button class="btn btn-primary" onclick="$('#modal-form').submit()"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
		</div>
	</div>
	</div>
	</div>

</body>
</html>
