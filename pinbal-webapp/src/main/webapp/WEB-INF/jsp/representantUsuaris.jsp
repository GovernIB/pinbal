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

	<style type="text/css">
		.error {
			border-color: #a94442;
			box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
		}
		.red {
			color: #a94442;
		}
	</style>
<script>
$(document).ready(function() {
	$("option[value='AUDITOR']").remove();
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

		}
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
		//paging: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
			"url": '<c:url value="/js/datatable-language.json"/>',
		},
		ajax: '<c:url value="/representant/usuari/datatable"/>',
		"order": [[ 1, "desc" ]],
		columnDefs: [{
			targets: [0, 1, 2],
			//orderable: false
		}, {
			targets: [3],
			//orderable: false
		}, {
			targets: 4,
			width: "20%",
			orderable: false,
			render: function (data, type, row, meta) {
				var template = $('#template-rols').html();
				return Mustache.render(template, row);
			}
		}, {
			targets: 5,
			width: "10%",
			orderable: false,
			render: function (data, type, row, meta) {
				var template = $('#template-actiu').html();
				return Mustache.render(template, row);
			}
		},
		{
			targets: 6,
			orderable: false,
			visible: false
		}, {
			targets: 7,
			width: "1%",
			orderable: false,
			render: function (data, type, row, meta) {
				if (row.usuari.inicialitzat || row.usuari.noInicialitzatCodi){
					var template = $('#template-permisos').html();
					return Mustache.render(template, row);
				} else {
					return "";
				}
			}
		}, {
			targets: 8,
			orderable: false,
			width: "1%",
			render: function (data, type, row, meta) {
				var template = $('#template-accions').html();
				row['nrow'] = meta['row'];
				return Mustache.render(template, row);
			}
		}],
		initComplete: function( settings, json ) {
			$('body').on("click", '.btn-open-modal-edit', function() {
				const usuariCodi = $(this).data("codi");
				$.get('<c:url value="/entitat/${entitat.id}/usuari/"/>' + usuariCodi, (entitatUsuari) => {
					var usuari = entitatUsuari.usuari;
					showModalEditar(
							usuari.inicialitzat,
							usuari.noInicialitzatNif,
							usuari.noInicialitzatCodi,
							usuari.descripcio,
							usuari.codi,
							usuari.nif,
							entitatUsuari.departament,
							entitatUsuari.representant,
							entitatUsuari.delegat,
							entitatUsuari.aplicacio,
							entitatUsuari.actiu);
				});
			});
		}
	});
});
function showModalCrear() {
	cleanUsuariErrors();
	$('#modal-form-usuari .modal-header h3').html("<spring:message code="representant.usuaris.titol.crear"/>");
	$('#modal-hidden-codi').attr('disabled', 'disabled');
	$('#modal-hidden-nif').attr('disabled', 'disabled');
	$('#modal-group-tipus').removeClass('hide');
	$('#modal-select-tipus').removeAttr('disabled');
	<%--$('#modal-select-tipus').val('${caracterTipusNif}');--%>
	$("#tipus1").prop("checked", true);
	$("#tipus1").parent().addClass("active");
	$("#tipus2").parent().removeClass("active");
	$('#modal-input-nom').val('');
	$('#modal-input-codi').removeAttr('disabled');
	$('#modal-input-codi').val('');
	$('#modal-input-nif').removeAttr('disabled');
	$('#modal-input-nif').val('');
	$('#modal-group-codi').addClass('hide');
	$('#modal-group-nif').removeClass('hide');
	$('#modal-input-departament').val('');
	$('#modal-input-actiu').prop('checked', false);
	$('#modal-input-representant').prop('checked', false);
	$('#modal-input-delegat').prop('checked', false);
	$('#modal-input-aplicacio').prop('checked', false);
	$('#modal-form-usuari').modal('toggle');
	$('#modal-accio-text').html('<spring:message code="comu.boto.crear"/>');
}
function showModalEditar(
		inicialitzat,
		noInicialitzatNif,
		noInicialitzatCodi,
		nom,
		codi,
		nif,
		departament,
		representant,
		delegat,
		aplicacio,
		actiu) {
	cleanUsuariErrors();
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
	$('#modal-input-departament').val(departament);
	$('#modal-input-representant').prop('checked', representant);
	$('#modal-input-delegat').prop('checked', delegat);
	$('#modal-input-aplicacio').prop('checked', aplicacio);
	$('#modal-input-actiu').prop('checked', actiu);
	$('#modal-form-usuari').modal('toggle');
	$('#modal-accio-text').html('<spring:message code="comu.boto.modificar"/>');
}
function sendUsuariForm() {

	let hasError = false;
	cleanUsuariErrors();

	// Validacions
	const inputCodi = $("#modal-input-codi");
	const inputNif = $("#modal-input-nif");
	const inputDep = $("#modal-input-departament");

	const isNif = $('#tipus1').is(':checked');

	if (!isNif) {
		if (inputCodi.val() == "") {
			inputCodi.addClass("error");
			$('<p class="help-block red" id="codiError"><spring:message code="NotEmpty"/></p>').insertAfter("#modal-input-codi");
			hasError = true;
		} else if (inputCodi.val().length > 64) {
			inputCodi.addClass("error");
			$('<p class="help-block red" id="codiError"><spring:message code="Size" arguments="1;64;1" htmlEscape="false" argumentSeparator=";"/></p>').insertAfter("#modal-input-codi");
			hasError = true;
		}
	} else {
		if (inputNif.val() == "") {
			inputNif.addClass("error");
			$('<p class="help-block red" id="nifError"><spring:message code="NotEmpty"/></p>').insertAfter("#modal-input-nif");
			hasError = true;
		} else if (inputNif.val().length > 64) {
			inputNif.addClass("error");
			$('<p class="help-block red" id="nifError"><spring:message code="Size" arguments="1;64;1" htmlEscape="false" argumentSeparator=";"/></p>').insertAfter("#modal-input-nif");
			hasError = true;
		}
	}
	if (inputDep.val().length > 64) {
		inputDep.addClass("error");
		$('<p class="help-block red" id="depError"><spring:message code="Size" arguments="0;64;0" htmlEscape="false" argumentSeparator=";"/></p>').insertAfter("#modal-input-departament");
		hasError = true;
	}

	if (hasError) {
		return;
	}

	// Enviament
	const params = $('#modal-form').serialize();
	$.ajax({
		type: "post",
		data: params,
		url: '<c:url value="/entitat/${entitat.id}/usuari/save"/>',
		async: false,
		success: (response) => {
			if (response === "NO_ENTITAT") {
				window.location('<c:url value="/index"/>');
			}

			$('#modal-form-usuari').modal('toggle');
			if (response === 'OK') {
				$("#table-users").DataTable().ajax.reload(null, false);
			}

			webutilRefreshMissatges();
		}
	});
}
function cleanUsuariErrors() {
	$("#codiError").remove();
	$("#nifError").remove();
	$("#depError").remove();

	$("#modal-input-codi").removeClass("error");
	$("#modal-input-nif").removeClass("error");
	$("#modal-input-departament").removeClass("error");
}
function canviActiu(usuariCodi) {
	$.ajax({
		type: "post",
		url: '<c:url value="/entitat/${entitat.id}/usuari/"/>' + usuariCodi + "/activar",
		async: false,
		success: () => {
			$("#table-users").DataTable().ajax.reload(null, false);
		}
	}).always(() => {webutilRefreshMissatges();});
}
</script>
</head>
<body>
	<c:url value="/representant/usuari" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-filtre-table" commandName="usuariFiltreCommand">
		<div class="row">
		 	<div class="col-md-2">
		 		<pbl:inputText name="codi" inline="true" placeholderKey="representant.usuaris.filtre.camp.codi" />
		 	</div>
			<div class="col-md-2">
				<pbl:inputText name="nom" inline="true" placeholderKey="representant.usuaris.filtre.camp.nom" />
			</div>	
		 	<div class="col-md-2">
		 		<pbl:inputText name="nif" inline="true" placeholderKey="representant.usuaris.filtre.camp.nif" />
			</div>
			<div class="col-md-2">
				<pbl:inputText name="departament" inline="true" placeholderKey="representant.usuaris.filtre.camp.departament"/>
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
	<div class="pull-right">
		<a class="btn btn-primary" href="#modal-form-usuari" onclick="showModalCrear()"><i class="fa fa-plus"></i>&nbsp;<spring:message code="entitat.usuaris.boto.nou.usuari"/></a>
	</div>
	<table id="table-users" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
			<th data-data="usuari.codi"><spring:message code="representant.usuaris.camp.codi" /></th>
			<th data-data="usuari.nom"><spring:message code="representant.usuaris.camp.nom" /></th>
			<th data-data="usuari.nif"><spring:message code="representant.usuaris.camp.nif" /></th>
			<th data-data="departament"><spring:message code="representant.usuaris.camp.departament" /></th>
			<th data-data="representant"><spring:message code="representant.usuaris.camp.rols" /></th>
			<th data-data="actiu"><spring:message code="representant.usuaris.camp.actiu" /></th>
			<th data-data="delegat"></th>
			<th data-data="aplicacio"></th>
			<th data-data="principal"></th>
			</tr>
		</thead>
	</table>
<script id="template-usuari" type="x-tmpl-mustache">
	{{ usuari.descripcio }}
</script>
<script id="template-rols" type="x-tmpl-mustache">
	{{#representant}}
		<span class="badge"><spring:message code="representant.usuaris.rol.repres"/></span>
	{{/representant}}
	{{#delegat}}
		<span class="badge"><spring:message code="representant.usuaris.rol.deleg"/></span>
	{{/delegat}}
	{{#aplicacio}}
		<span class="badge"><spring:message code="representant.usuaris.rol.aplic"/></span>
	{{/aplicacio}}
</script>
<script id="template-actiu" type="x-tmpl-mustache">
{{#actiu}}
 	<i class="fas fa-check"></i>
{{/actiu}}
</script>
<script id="template-permisos" type="x-tmpl-mustache">
	<a class="btn btn-default" href="<c:url value="/representant/usuari/{{ usuari.codi }}/permis"/>">
		<i class="fas fa-lock"></i>&nbsp;<spring:message code="comu.boto.permisos"/>
	</a>
</script>
<script id="template-accions" type="x-tmpl-mustache">
	<div class="btn-group">
		<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></button>
		<ul class="dropdown-menu">
			{{#principal}}
				<li class="disabled"><a href="#"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
			{{/principal}}
			{{^principal}}
				<li><a href="#" data-nrow="{{ nrow }}" data-codi="{{usuari.codi}}" class="btn-open-modal-edit"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
			{{/principal}}
			{{#actiu}}
				<li><a href="#" onclick="canviActiu('{{usuari.codi}}');"><i class="fa fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
			{{/actiu}}
			{{^actiu}}
				<li><a href="#" onclick="canviActiu('{{usuari.codi}}');"><i class="fa fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a></li>
			{{/actiu}}
		</ul>
	</div>
</script>
	<div id="modal-form-usuari" class="modal fade" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h3 style="margin: 0;"></h3>
				</div>
				<div class="modal-body">
					<c:url value="/representant/usuari/save" var="formAction"/>
					<form id="modal-form" action="${formAction}" method="post">
						<input type="hidden" id="modal-hidden-id" name="id" value="${entitat.id}"/>
						<input type="hidden" id="modal-hidden-codi" name="codi"/>
						<input type="hidden" id="modal-hidden-nif" name="nif"/>
						<div id="modal-group-tipus" class="form-group">
							<label class="control-label" for="modal-select-tipus"><spring:message code="representant.usuaris.camp.tipus"/></label>
							<div class="btn-group" data-toggle="buttons" id="modal-select-tipus" style="display: block; width: 100%; margin-bottom: 50px;">
								<label class="btn btn-default active">
									<input id="tipus1" name="tipus" type="radio" value="${caracterTipusNif}" checked="checked"> <spring:message code="representant.usuaris.tipus.nif"/>
								</label>
								<label class="btn btn-default">
									<input id="tipus2" name="tipus" type="radio" value="${caracterTipusCodi}"> <spring:message code="representant.usuaris.tipus.codi"/>
								</label>
							</div>
						</div>
						<div id="modal-group-codi" class="form-group">
							<label class="control-label" for="modal-input-codi"><spring:message code="representant.usuaris.camp.codi"/></label>
							<input class="form-control" type="text" id="modal-input-codi" name="codi" disabled="disabled"/>
						</div>
						<div id="modal-group-nif" class="form-group">
							<label class="control-label" for="modal-input-nif"><spring:message code="representant.usuaris.camp.nif"/></label>
							<input class="form-control" type="text" id="modal-input-nif" name="nif" disabled="disabled"/>
						</div>
						<div id="modal-group-nom" class="form-group">
							<label class="control-label" for="modal-input-nom"><spring:message code="representant.usuaris.camp.nom"/></label>
							<input class="form-control" type="text" id="modal-input-nom" name="codi" disabled="disabled"/>
						</div>
						<div class="form-group">
							<label class="control-label" for="modal-input-departament"><spring:message code="representant.usuaris.camp.departament"/></label>
							<input class="form-control" type="text" id="modal-input-departament" name="departament"/>
						</div>
						<div class="form-group">
							<label for="modal-input-actiu">
								<input type="checkbox" id="modal-input-actiu" name="actiu"/>
								<spring:message code="representant.usuaris.camp.actiu"/>
							</label>
						</div>	
						<div class="form-group">
							<label for="modal-input-representant"><spring:message code="representant.usuaris.camp.rols"/></label>
							<div class="checkbox" style="margin-top: 0px;">
								<label for="modal-input-representant">
									<input type="checkbox" id="modal-input-representant" name="rolRepresentant">
									<spring:message code="representant.usuaris.rol.repres"/>
								</label>
							</div>
							<div class="checkbox">
								<label for="modal-input-delegat">
									<input type="checkbox" id="modal-input-delegat" name="rolDelegat">
									<spring:message code="representant.usuaris.rol.deleg"/>
								</label>
							</div>
							<div class="checkbox">
								<label for="modal-input-aplicacio">
									<input type="checkbox" id="modal-input-aplicacio" name="rolAplicacio">
									<spring:message code="representant.usuaris.rol.aplic"/>
								</label>
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></button>
					<button class="btn btn-primary" onclick="sendUsuariForm()"><span class="fa fa-save"></span>&nbsp;<span id="modal-accio-text"></span></button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
