<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
			"caracterRolDelegat",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_DELEGAT);
	request.setAttribute(
			"caracterRolRepresentant",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_REPRESENTANT);
	request.setAttribute(
			"caracterRolAuditor",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_AUDITOR);
	request.setAttribute(
			"caracterRolAplicacio",
			es.caib.pinbal.webapp.command.EntitatUsuariCommand.CARACTER_APLICACIO);
%>

<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
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
	$('select[name="tipus"]', $('form#modal-form')).val('${caracterTipusNif}');
	$('select[name="tipus"]').trigger('change');
	


	
	
    $('#table-users').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>',
        },
		ajax: '<c:url value="/entitat/${entitat.id}/usuari/datatable"/>',
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
				targets: [1],
				orderable: false,
				visible: true
			}, 
	        {
	            targets: 2,
	            orderable: false,
	            width: "20%",
				render: function (data, type, row, meta) {
					var template = $('#template-rols').html();
					return Mustache.render(template, row);
				}
	        },
	        {
	            targets: [3],
	            orderable: false,
	            width: "20%",
	            render: function (data, type, row, meta) {
					var template = $('#template-principal').html();
					return Mustache.render(template, row);
	            }
	        },
			{
				targets: [4],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
					var template = $('#template-swap-principal').html();
					return Mustache.render(template, row);
				}
			}, 
			{
				targets: [5],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
					var template = $('#template-actions').html();
					row['nrow'] = meta['row'];
					return Mustache.render(template, row);
				}
			}, 
			{
				targets: [6, 7],
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
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="entitat.usuaris.titol.crear"/>");
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
	$('#modal-input-auditor').prop('checked', false);
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
		auditor,
		aplicacio) {
	
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="entitat.usuaris.titol.modificar"/>");
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
	$('#modal-input-auditor').prop('checked', auditor);
	$('#modal-input-aplicacio').prop('checked', aplicacio);
	$('#modal-form-usuari').modal('toggle');
}
</script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="entitat.miques.entitat" arguments="${entitat.nom}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="entitat.miques.usuaris"/></li>
	</ul>

	<c:url value="/entitat/${entitat.id}/usuari" var="formAction"/>
	<form:form id="form-filtre"action="${formAction}" method="post" cssClass="well-lg" commandName="usuariFiltreCommand">
		<div class="container-fluid">
			<div class="col-md-3">
				<c:set var="campPath" value="codi"/>
				<pbl:inputText name="${campPath}" textKey="entitat.usuaris.filtre.camp.codi"
							   placeholder="entitat.usuaris.filtre.camp.codi" hideLabel="true"/>
			</div>
			<div class="col-md-3">
				<c:set var="campPath" value="nif"/>
				<pbl:inputText name="${campPath}" textKey="entitat.usuaris.filtre.camp.nif"
							   placeholder="entitat.usuaris.filtre.camp.nif" hideLabel="true"/>
			</div>
			<div class="col-md-3">			
				<c:set var="campPath" value="nom"/>
				<pbl:inputText name="${campPath}" textKey="entitat.usuaris.filtre.camp.nom"
							   placeholder="entitat.usuaris.filtre.camp.nom" hideLabel="true"/>
			</div>
			<div class="col-md-3">			
				<c:set var="campPath" value="departament"/>
				<pbl:inputText name="${campPath}" textKey="entitat.usuaris.filtre.camp.departament"
							   placeholder="entitat.usuaris.filtre.camp.departament" hideLabel="true"/>
			</div>
		</div>
		<div class="container-fluid">
<%-- 		<label class="control-label" for="modal-input-representant"><spring:message code="entitat.usuaris.camp.rols"/>: </label> --%>
			<c:set var="campPath" value="isRepresentant"/>
			<label class="checkbox col-md-1" for="modal-input-representant">
				<form:checkbox  path="${campPath}" id="${campPath}"/>
				<spring:message code="entitat.usuaris.rol.repres"/>
			</label>
			
			<c:set var="campPath" value="isDelegat"/>
			<label class="checkbox col-md-1" for="modal-input-delegat">
				<form:checkbox  path="${campPath}" id="${campPath}"/>
				<spring:message code="entitat.usuaris.rol.deleg"/>
			</label>
			
			<c:set var="campPath" value="isAuditor"/>
			<label class="checkbox col-md-1" for="modal-input-auditor">
				<form:checkbox  path="${campPath}" id="${campPath}"/>
				<spring:message code="entitat.usuaris.rol.audit"/>
			</label>
	
			<c:set var="campPath" value="isAplicacio"/>
			<label class="checkbox col-md-1" for="modal-input-aplicacio">
				<form:checkbox  path="${campPath}" id="${campPath}"/>
				<spring:message code="entitat.usuaris.rol.aplic"/>
			</label>
			<div class="col-md-pull-right">
				<button id="netejar-filtre" class="btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>		
	</form:form>

	<div class="container-fluid">
		<div class="col-md-12">
			<a class="btn col-md-pull-right" href="#modal-form-usuari" onclick="showModalCrear()"><i class="glyphicon-plus"></i>&nbsp;<spring:message code="entitat.usuaris.boto.nou.usuari"/></a>
		</div>
		<div class="clearfix"></div>
	</div>
	<table id="table-users" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
			<th data-data="usuari.nom"><spring:message code="entitat.usuaris.camp.usuari" /></th>
			<th data-data="departament"><spring:message code="entitat.usuaris.camp.departament" /></th>
			<th data-data="representant"><spring:message code="entitat.usuaris.camp.rols" /></th>
			<th data-data="principal"></th>
			<th data-data="aplicacio"></th>
			<th data-data="auditor"></th>
			<th data-data="delegat"></th>
			<th data-data="usuari.codi"></th>
			</tr>
		</thead>
	</table>

<script id="template-usuari" type="x-tmpl-mustache">
	{{ usuari.descripcio }}
</script>
<script id="template-rols" type="x-tmpl-mustache">
	{{#representant}}
		<span class="badge"><spring:message code="entitat.usuaris.rol.repres"/></span>
	{{/representant}}
	{{#delegat}}
		<span class="badge"><spring:message code="entitat.usuaris.rol.deleg"/></span>
	{{/delegat}}
	{{#auditor}}
		<span class="badge"><spring:message code="entitat.usuaris.rol.audit"/></span>
	{{/auditor}}
	{{#aplicacio}}
		<span class="badge"><spring:message code="entitat.usuaris.rol.aplic"/></span>
	{{/aplicacio}}
</script>
<script id="template-permisos" type="x-tmpl-mustache">
	<a class="btn btn-primary" href="<c:url value="/representant/usuari/{{ usuari.codi }}/permis"/>">
		<i class="fas fa-lock"></i>&nbsp;<spring:message code="comu.boto.permisos"/>
	</a>
</script>
<script id="template-actions" type="x-tmpl-mustache">
{{#principal}}
 	<a class="btn btn-primary disabled" href="#"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
{{/principal}}
{{^principal}}
	<a data-nrow="{{ nrow }}" class="btn-open-modal-edit btn btn-primary"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
{{/principal}}
</script>
<script id="template-principal" type="x-tmpl-mustache">
{{#principal}}
 	<i class="fas fa-certificate"></i>
{{/principal}}
</script>
<script id="template-swap-principal" type="x-tmpl-mustache">
{{#principal}}
	<a href="usuari/{{ usuari.codi }}/principal" class="btn btn-primary"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="entitat.usuaris.accio.desfer.principal"/></a>
{{/principal}}
{{^principal}}
	<a href="usuari/{{ usuari.codi }}/principal" class="btn btn-primary"><i class="fas fa-certificate"></i>&nbsp;<spring:message code="entitat.usuaris.accio.fer.principal"/></a>
{{/principal}}
</script>
	<div>
		<a href="<c:url value="/entitat"/>" class="btn col-md-pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

<div id="modal-form-usuari" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">

		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="entitat.usuaris.titol.modificar"/></h3>
		</div>
		<div class="modal-body">
			<c:url value="/entitat/${entitat.id}/usuari/save" var="formAction"/>
			<form id="modal-form" action="${formAction}" method="post">
				<input type="hidden" id="modal-hidden-id" name="id" value="${entitat.id}"/>
				<input type="hidden" id="modal-hidden-codi" name="codi"/>
				<input type="hidden" id="modal-hidden-nif" name="nif"/>
				<div id="modal-group-tipus" class="form-group">
    				<label class="control-label" for="modal-select-tipus"><spring:message code="entitat.usuaris.camp.tipus"/></label>
					<select id="modal-select-tipus" name="tipus" class="form-control input-medium">
						<option value="${caracterTipusNif}"><spring:message code="entitat.usuaris.tipus.nif"/></option>
						<option value="${caracterTipusCodi}"><spring:message code="entitat.usuaris.tipus.codi"/></option>
					</select>
				</div>
				<div id="modal-group-codi" class="form-group">
    				<label for="modal-input-codi"><spring:message code="entitat.usuaris.camp.codi"/></label>
					<input class="form-control" type="text" id="modal-input-codi" name="codi" disabled="disabled"/>
				</div>
				<div id="modal-group-nif" class="form-group">
    				<labe for="modal-input-nif"><spring:message code="entitat.usuaris.camp.nif"/></label>
					<input class="form-control" type="text" id="modal-input-nif" name="nif" disabled="disabled"/>
				</div>
				<div class="form-group">
    				<label for="modal-input-departament"><spring:message code="entitat.usuaris.camp.departament"/></label>
					<input class="form-control" type="text" id="modal-input-departament" name="departament"/>
				</div>		
				<div class="form-group">
					<label for="modal-input-representant"><spring:message code="entitat.usuaris.camp.rols"/></label>
					<div class="checkbox" for="modal-input-representant" style="margin-top: 0px;">
						<label>
    					<input type="checkbox" id="modal-input-representant" name="rolRepresentant">
    					<spring:message code="entitat.usuaris.rol.repres"/>
    					</label>
    				</div>
    				<div class="checkbox" for="modal-input-delegat">
    					<label>
    					<input type="checkbox" id="modal-input-delegat" name="rolDelegat">
    					<spring:message code="entitat.usuaris.rol.deleg"/>
    					</label>
    				</div>
    				<div class="checkbox" for="modal-input-auditor">
    					<label>
    					<input type="checkbox" id="modal-input-auditor" name="rolAuditor">
    					<spring:message code="entitat.usuaris.rol.audit"/>
    					</label>
    				</div>
    				<div class="checkbox" for="modal-input-aplicacio">
	    				<label>
	    					<input type="checkbox" id="modal-input-aplicacio" name="rolAplicacio">
	    					<spring:message code="entitat.usuaris.rol.aplic"/>
	    				</label>
    				</div>
    			</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" class="btn btn-primary" onclick="$('#modal-form').submit()"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>
	</div>
	</div>
	<script type="text/javascript">
	function onInvokeAction(id) {
		setExportToLimit(id, '');
		createHiddenInputFieldsForLimitAndSubmit(id);
	}
	</script>
</body>
</html>
