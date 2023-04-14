<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%
	request.setAttribute(
			"serveiCampTipus",
			es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoTipus.values());
	request.setAttribute(
			"serveiCampValidacioOperacio",
			es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoValidacioOperacio.values());
	request.setAttribute(
			"serveiCampValidacioDataTipus",
			es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoValidacioDataTipus.values());
	request.setAttribute(
			"llistatDadesEspecifiques",
			((es.caib.pinbal.core.dto.ArbreDto<?>)request.getAttribute("arbreDadesEspecifiques")).toList());
%>

<html>
<head>
	<title><spring:message code="servei.camp.titol"/>: ${servei.descripcio}</title>
	<c:choose>
		<c:when test="${not empty servei.descripcio}"><c:set var="serveiPerTitol" value="${servei.descripcio}"/></c:when>
		<c:otherwise><c:set var="serveiPerTitol" value="${servei.codi}"/></c:otherwise>
	</c:choose>
	<meta name="subtitle" content="${serveiPerTitol}"/>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

	<link href="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/css/jasny-bootstrap.min.css"/>" rel="stylesheet"> 
	<script src="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/js/jasny-bootstrap.min.js"/>"></script> 

<script>
const inicialitzarDadesEspecifiques = ${servei.pinbalIniDadesExpecifiques};

$(document).ready(function() {
	// Confirmació al esborrar el camp
	$('.confirm-esborrar').click(function() {
		return confirm("<spring:message code="servei.camp.confirmacio.eliminacio.camp"/>");
	});
	// Confirmació al esborrar el grup
	$('.confirm-esborrar-grup').click(function() {
		return confirm("<spring:message code="servei.camp.confirmacio.eliminacio.grup"/>");
	});
	// Modal camp botó submit
	$('#modal-boto-submit').click(function() {
		$('#modal-select-tipus').removeAttr('disabled');
		$('#modal-form').submit();
	});
	// Modal grup botó submit
	$('#modal-grup-boto-submit').click(function() {
		$('#modal-grup-formform').submit();
	});
	// Contreure tot
	$('#accio-contreure-all').click(function() {
		$('.arbre-ul').collapse('hide');
	});
	// Expandir tot
	$('#accio-expandir-all').click(function() {
		$('.arbre-ul').collapse('show');
	});
});
// Afegir nou camp
function serveiCampAfegir(servei, path) {
	$('#hidden-hidden-servei').val(servei);
	$('#hidden-hidden-path').val(path);
	$('#hidden-form').submit();
}
// Modal per editar camps
function initModalCamp(
		id, path, tipus, etiqueta, defecte, comentari, dataFormat,
		campPareId, valorPare, inicialitzar, obligatori, modificable, visible,
		validacioRegexp, validacioMin, validacioMax, validacioDataCmpOperacio,
		validacioDataCmpCamp2, validacioDataCmpNombre, validacioDataCmpTipus) {
	$('#modal-hidden-id').val(id);
	$('#modal-input-path').val(path);
	$('#modal-hidden-path').val(path);
	$('#modal-select-tipus').val(tipus);
	$('.grup-descripcions').css('display', 'none');
	if (tipus != 'ENUM') {
		$('#modal-select-tipus').removeAttr('disabled');
		$('#modal-option-tipus-enum').css('display', 'none');
	} else {
		$('#modal-select-tipus').attr('disabled', 'disabled');
		$('#modal-option-tipus-enum').css('display', 'block');
		$('#modal-grup-descripcions-' + id).css('display', 'block');
	}
	if ($('#modal-select-tipus').val() == 'DATA') {
		$('#modal-input-data-format').removeAttr('disabled');
	} else {
		$('#modal-input-data-format').attr('disabled', 'disabled');
	}
	$('#modal-input-etiqueta').val(etiqueta);
	$('#modal-input-defecte').val(defecte);
	$('#modal-input-comentari').val(comentari);
	$('#modal-input-data-format').val(dataFormat);
	$('#modal-select-camp-pare').val(campPareId);
	$('#modal-input-valor-pare').val(valorPare);
	if ($('#modal-select-tipus').val().indexOf('MUNICIPI') == 0) {
		$('#modal-select-camp-pare').removeAttr('disabled');
		$('#modal-input-valor-pare').removeAttr('disabled');
	} else if ($('#modal-select-tipus').val() == 'DOC_IDENT') {
		$('#modal-select-camp-pare').removeAttr('disabled');
		$('#modal-input-valor-pare').removeAttr('disabled');
	} else {
		$('#modal-select-camp-pare').attr('disabled', 'disabled');
		$('#modal-input-valor-pare').attr('disabled', 'disabled');
	}
	if ($('#modal-select-tipus').val() == 'ETIQUETA') {
		$('#modal-input-etiqueta').attr('disabled', 'disabled');
		$('#modal-input-defecte').attr('disabled', 'disabled');
	} else {
		$('#modal-input-etiqueta').removeAttr('disabled');
		$('#modal-input-defecte').removeAttr('disabled');
	}
	if (inicialitzarDadesEspecifiques) {
		if (inicialitzar)
			$('#modal-checkbox-inicialitzar').attr('checked', 'checked');
		else
			$('#modal-checkbox-inicialitzar').removeAttr('checked');
	}
	if (obligatori)
		$('#modal-checkbox-obligatori').attr('checked', 'checked');
	else
		$('#modal-checkbox-obligatori').removeAttr('checked');
	if (modificable)
		$('#modal-checkbox-modificable').attr('checked', 'checked');
	else
		$('#modal-checkbox-modificable').removeAttr('checked');
	if (visible)
		$('#modal-checkbox-visible').attr('checked', 'checked');
	else
		$('#modal-checkbox-visible').removeAttr('checked');
	$('#modal-input-val-regexp').val(validacioRegexp);
	$('#modal-input-val-min').val(validacioMin);
	$('#modal-input-val-max').val(validacioMax);
	$('#modal-input-val-datop').val(validacioDataCmpOperacio);
	$('#modal-input-val-datcamp2').val(validacioDataCmpCamp2);
	$('#modal-input-val-datcamp3').val(validacioDataCmpCamp2);
	$('#modal-input-val-datop2').val(validacioDataCmpOperacio);
	$('#modal-input-val-datnum').val(validacioDataCmpNombre);
	$('#modal-input-val-dattip').val(validacioDataCmpTipus);
	$('#modal-select-tipus').change(function() {
		if ($('#modal-select-tipus').val() == 'DATA') {
			$('#modal-input-data-format').removeAttr('disabled');
		} else {
			$('#modal-input-data-format').attr('disabled', 'disabled');
		}
		if ($('#modal-select-tipus').val().indexOf('MUNICIPI') == 0) {
			$('#modal-select-camp-pare').removeAttr('disabled');
			$('#modal-input-valor-pare').removeAttr('disabled');
		} else if ($('#modal-select-tipus').val() == 'DOC_IDENT') {
			$('#modal-select-camp-pare').removeAttr('disabled');
			$('#modal-input-valor-pare').removeAttr('disabled');
		} else {
			$('#modal-select-camp-pare').attr('disabled', 'disabled');
			$('#modal-input-valor-pare').attr('disabled', 'disabled');
		}
		if ($('#modal-select-tipus').val() == 'ETIQUETA') {
			$('#modal-input-etiqueta').attr('disabled', 'disabled');
			$('#modal-input-defecte').attr('disabled', 'disabled');
		} else {
			$('#modal-input-etiqueta').removeAttr('disabled');
			$('#modal-input-defecte').removeAttr('disabled');
		}
		showHideValidation();
	});
	$('#modal-input-val-datvalcamp').change(function() {
		showHideValidationData();
	});
	$('#modal-input-val-datcamp3').change(function() {
		$('#modal-input-val-datcamp2').val($('#modal-input-val-datcamp3').val());
	});
	$('#modal-input-val-datop2').change(function() {
		$('#modal-input-val-datop').val($('#modal-input-val-datop2').val());
	});
	$('#modal-editar-camp').modal('toggle');
}
// Modal per a previsualitzar el formulari
function initModalPreview(element) {
	$('#modal-form-preview .modal-body').load(element.href);
// 	$('#modal-form-preview').css('width', '700px');
	$('#modal-form-preview').modal('toggle');
}
// Mostrar/ocultar camps validació segons tipus
function showHideValidation() {
	const tipus = $('#modal-select-tipus').val();
	$('#validacio').css('display', 'none');
	if (tipus == 'TEXT' || tipus == 'NUMERIC' || tipus == 'DATA') {
		$('#validacio').css('display', 'block');
	}
	$('#validacio_tipus_text').css('display', 'none');
	$('#validacio_tipus_numeric').css('display', 'none');
	$('#validacio_tipus_data').css('display', 'none');
	if (tipus == 'TEXT') {
		$('#validacio_tipus_text').css('display', 'block');
	} else if (tipus == 'NUMERIC') {
		$('#validacio_tipus_numeric').css('display', 'block');
	} else if (tipus == 'DATA') {
		$('#validacio_tipus_data').css('display', 'block');
	}
}
function showHideValidationData(initValue) {
	if (initValue) {
		const datnumVal = $('#modal-input-val-datnum').val();
		if (datnumVal) {
			$('#modal-input-val-datvalcamp').val('DIFERENCIA')
		} else {
			$('#modal-input-val-datvalcamp').val('VALOR')
		}
	}
	const dataTipus = $('#modal-input-val-datvalcamp').val();
	$('#validacio_tipus_data_valor').css('display', 'none');
	$('#validacio_tipus_data_diferencia').css('display', 'none');
	if (dataTipus == 'VALOR') {
		$('#validacio_tipus_data_valor').css('display', 'block');
		$('#modal-input-val-datnum').val('');
	} else {
		$('#validacio_tipus_data_diferencia').css('display', 'block');
	}
	$("#modal-input-val-datcamp2 > option").each(function() {
		const currentId = $('#modal-hidden-id').val();
		$(this).css('display', (currentId == this.value) ? 'none' : 'block');
	});
	$("#modal-input-val-datcamp3 > option").each(function() {
		const currentId = $('#modal-hidden-id').val();
		$(this).css('display', (currentId == this.value) ? 'none' : 'block');
	});
}
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
$(function() {
	// Canvi d'icones al ocultar/mostrar
	$('.arbre-node').on('hide.bs.collapse', function (event) {
		var iconId = '#icon-' + this.id.substring(3);
		$(iconId).attr('class', 'fas fa-chevron-right');
		event.stopPropagation();
	});
	$('.arbre-node').on('show.bs.collapse', function (event) {
		var iconId = '#icon-' + this.id.substring(3);
		$(iconId).attr('class', 'fas fa-chevron-down');
		event.stopPropagation();
	});
	$('#boto-nou-grup').click(function (event) {
		$('#modal-grup-titol').html('<spring:message code="servei.camp.grup.crear"/>');
		$('#modal-grup-hidden-id').val(null);
		$('#modal-grup-input-nom').val(null);
		$('#modal-grup-formform').attr('action', 'campGrup/add');
		$('#modal-grup-form').modal('toggle');
	});
	$('.boto-grup-editar').click(function (event) {
		$('#modal-grup-titol').html('<spring:message code="servei.camp.grup.modificar"/>');
		$('#modal-grup-hidden-id').val($(this).data('id'));
		$('#modal-grup-input-nom').val($(this).data('nom'));
		$('#modal-grup-formform').attr('action', 'campGrup/update');
		$('#modal-grup-form').modal('toggle');
	});
	$('.btn-edit-camp').click(function (event) {
		initModalCamp($(this).data('id'),
					  $(this).data('path'),
					  $(this).data('tipus'),
					  $(this).data('etiqueta'),
					  $(this).data('valorperdefecte'),
					  $(this).data('comentari'),
					  $(this).data('dataformat'),
					  $(this).data('camppare'),
					  $(this).data('valorPare'),
					  $(this).data('inicialitzar'),
					  $(this).data('obligatori'),
					  $(this).data('modificable'),
					  $(this).data('visible'),
					  $(this).data('validacioRegexp'),
					  $(this).data('validacioMin'),
					  $(this).data('validacioMax'),
					  $(this).data('validacioDataCmpOperacio'),
					  $(this).data('validacioDataCmpCamp2'),
					  $(this).data('validacioDataCmpNombre'),
					  $(this).data('validacioDataCmpTipus'));
		showHideValidation();
		showHideValidationData(true);
	});
});
</script>
</head>
<body>
	<div class="row">
		<div class="col-md-4">
			<div class="well">
				<div class="btn-group pull-right">
					<a href="#" class="btn btn-default"
						title="<spring:message code="servei.camp.contreure.tot"/>"
						id="accio-contreure-all"><i class="fas fa-chevron-right"></i></a> 
					<a href="#" class="btn btn-default"
						title="<spring:message code="servei.camp.expandir.tot"/>"
						id="accio-expandir-all"><i class="fas fa-chevron-down"></i></a>
				</div>
				<br />
				<ul style="list-style: none; margin: 0; padding: 0;">
					<c:set var="nodeArbreActual" value="${arbreDadesEspecifiques.arrel}"
						scope="request" />
					<jsp:include page="import/dadesEspecifiquesArbreNode.jsp" />
				</ul>
			</div>
		</div>
		<div class="col-md-8">
			<a id="boto-nou-grup" class="btn btn-primary pull-right"><i class="fas fa-plus"></i>&nbsp;Nou grup</a><br/><br/>
			<c:set var="hiHaCampsSenseGrup" value="${false}"/>
			<c:forEach var="camp" items="${camps}">
				<c:if test="${empty camp.grup}"><c:set var="hiHaCampsSenseGrup" value="${true}"/></c:if>
			</c:forEach>
			<c:if test="${hiHaCampsSenseGrup}">
				<table id="table-camps" class="table table-striped table-bordered" style="width: 100%">
					<thead>
						<tr>
						<th><spring:message code="servei.camp.taula.columna.nom" /></th>
						<th><spring:message code="servei.camp.taula.columna.tipus" /></th>
						<th><spring:message code="servei.camp.taula.columna.etiqueta" /></th>
						<c:if test="${servei.pinbalIniDadesExpecifiques}">
							<th style="width: 1%"><spring:message code="servei.camp.taula.columna.i" /></th>
						</c:if>
						<th style="width: 1%"><spring:message code="servei.camp.taula.columna.o" /></th>
						<th style="width: 1%"><spring:message code="servei.camp.taula.columna.m" /></th>
						<th style="width: 1%"><spring:message code="servei.camp.taula.columna.v" /></th>
						<c:if test="${not empty grups}">
						<th style="width: 1%"></th>
						</c:if>
						<th style="width: 1%"></th>
						<th style="width: 1%"></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${camps}" var="camp" varStatus="loopcamps">
						<tr>
							<td>
								<input type="hidden" name="id" value="${ id }"/>
								<span title="${ camp.path }">${ camp.campNom }</span>
							</td>
							<td>
								${ camp.tipus }
							</td>
							<td>
								${ camp.etiqueta }
							</td>
							<td>
								<c:if test="${ camp.inicialitzar }"><i class="fa fa-check"></i></c:if>
							</td>
							<td>
								<c:if test="${ camp.obligatori }"><i class="fa fa-check"></i></c:if>
							</td>
							<td>
								<c:if test="${ camp.modificable }"><i class="fa fa-check"></i></c:if>
							</td>
							<td>
								<c:if test="${ camp.visible }"><i class="fa fa-check"></i></c:if>
							</td>
							<c:if test="${not empty grups}">
							<td>
								<div class="btn-group">
									<a href="#" title="<spring:message code="servei.camp.taula.accio.agrupar"/>" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
										<i class="far fa-arrow-alt-circle-down"></i>&nbsp;<span class="caret"></span>
									</a>
									<ul class="dropdown-menu">
										<c:forEach var="grup" items="${grups}">
											<li><a href="camp/${ camp.id }/agrupar/${grup.id}">${grup.nom}</a></li>
										</c:forEach>
									</ul>
								</div>
							</td>
							</c:if>
							<td>
								<a href="#modal-editar-camp" data-nrow="${ loopcamps.index }" 
								   data-id="${ camp.id }" 
								   data-path="${ camp.path }" 
								   data-tipus="${ camp.tipus }" 
								   data-etiqueta="${ camp.etiqueta }" 
								   data-valorperdefecte="${ camp.valorPerDefecte }" 
								   data-comentari="${ camp.comentari }" 
								   data-dataformat="${ camp.dataFormat }" 
								   <c:if test="${ not empty camp.campPare }">
								   data-camppare="${ camp.campPare.id }"
								   </c:if>
								   <c:if test="${ empty camp.campPare }">
								   data-camppare="0"
								   </c:if>
								   data-valorpare="${ camp.valorPare }"
								   data-inicialitzar="${ camp.inicialitzar }"
								   data-obligatori="${ camp.obligatori }" 
								   data-modificable="${ camp.modificable }" 
								   data-visible="${ camp.visible }"
								   data-validacio-regexp="${ camp.validacioRegexp }"
								   data-validacio-min="${ camp.validacioMin }"
								   data-validacio-max="${ camp.validacioMax }"
								   data-validacio-data-cmp-operacio="${ camp.validacioDataCmpOperacio }"
								   data-validacio-data-cmp-camp2="${ camp.validacioDataCmpCamp2.id }"
								   data-validacio-data-cmp-nombre="${ camp.validacioDataCmpNombre }"
								   data-validacio-data-cmp-tipus="${ camp.validacioDataCmpTipus }"
								class="btn btn-default btn-edit-camp" title="<spring:message code="servei.camp.taula.accio.modificar"/>">
									<i class="fas fa-pen"></i>
								</a>
							</td>
							<td>
								<a href="camp/${ camp.id }/delete" title="<spring:message code="servei.camp.taula.accio.esborrar"/>" class="btn btn-default confirm-esborrar">
									<i class="fas fa-trash-alt"></i>
								</a>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</c:if>
			<c:forEach var="grup" items="${grups}" varStatus="grupStatus">
				<div class="well well-sm">
					<fieldset>
					 	<legend>${grup.nom}</legend>
					</fieldset>
					<c:choose>
						<c:when test="${not empty campsAgrupats[grup.id]}">
							<table id="table-camps" class="table table-striped table-bordered" style="width: 100%">
								<thead>
									<tr>
									<th data-data="campNom"><spring:message code="servei.camp.taula.columna.nom" /></th>
									<th data-data="tipus"><spring:message code="servei.camp.taula.columna.tipus" /></th>
									<th data-data="etiqueta"><spring:message code="servei.camp.taula.columna.etiqueta" /></th>
									<c:if test="${servei.pinbalIniDadesExpecifiques}">
										<th data-data="inicialitzar" style="width: 1%"><spring:message code="servei.camp.taula.columna.i" /></th>
									</c:if>
									<th data-data="obligatori" style="width: 1%"><spring:message code="servei.camp.taula.columna.o" /></th>
									<th data-data="modificable" style="width: 1%"><spring:message code="servei.camp.taula.columna.m" /></th>
									<th data-data="visible" style="width: 1%"><spring:message code="servei.camp.taula.columna.v" /></th>
									<th data-data="path" style="width: 1%"></th>
									<th data-data="id" style="width: 1%"></th>
									<th data-data="campPare" style="width: 1%"></th>
									<th data-data="validacioRegexp" style="width: 1%"></th>
									</tr>
								</thead>
								<tbody>
								<c:forEach items="${campsAgrupats[grup.id]}" var="camp" varStatus="loopcamps">
									<tr>
										<td>
											<input type="hidden" name="id" value="${ id }"/>
											<span title="${ camp.path }">${ camp.campNom }</span>
										</td>
										<td>
											${ camp.tipus }
										</td>
										<td>
											${ camp.etiqueta }
										</td>
										<td>
											<c:if test="${ camp.inicialitzar }"><i class="fa fa-check"></i></c:if>
										</td>
										<td>
											<c:if test="${ camp.obligatori }"><i class="fa fa-check"></i></c:if>
										</td>
										<td>
											<c:if test="${ camp.modificable }"><i class="fa fa-check"></i></c:if>
										</td>
										<td>
											<c:if test="${ camp.visible }"><i class="fa fa-check"></i></c:if>
										</td>
										<c:if test="${not empty grups}">
										<td>
											<div class="btn-group">
												<a href="#" title="<spring:message code="servei.camp.taula.accio.agrupar"/>" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
													<i class="far fa-arrow-alt-circle-down"></i>&nbsp;<span class="caret"></span>
												</a>
												<ul class="dropdown-menu">
													<c:forEach var="grup" items="${grups}">
														<li><a href="camp/${ camp.id }/agrupar/${grup.id}">${grup.nom}</a></li>
													</c:forEach>
												</ul>
											</div>
										</td>
										</c:if>
										<td>
											<a href="#modal-editar-camp" data-nrow="${ loopcamps.index }" 
												data-id="${ camp.id }" 
												data-path="${ camp.path }" 
												data-tipus="${ camp.tipus }" 
												data-etiqueta="${ camp.etiqueta }" 
												data-valorperdefecte="${ camp.valorPerDefecte }" 
												data-comentari="${ camp.comentari }" 
												data-dataformat="${ camp.dataFormat }" 
												<c:if test="${ not empty camp.campPare }">
												data-camppare="${ camp.campPare.id }"
												</c:if>
												<c:if test="${ empty camp.campPare }">
												data-camppare="0"
												</c:if>
												data-valorpare="${ camp.valorPare }"
												data-inicialitzar="${ camp.inicialitzar }"
												data-obligatori="${ camp.obligatori }"
												data-modificable="${ camp.modificable }"
												data-visible="${ camp.visible }"
												data-validacio-regexp="${ camp.validacioRegexp }"
												data-validacio-min="${ camp.validacioMin }"
												data-validacio-max="${ camp.validacioMax }"
												data-validacio-data-cmp-operacio="${ camp.validacioDataCmpOperacio }"
												data-validacio-data-cmp-camp2="${ camp.validacioDataCmpCamp2.id }"
												data-validacio-data-cmp-nombre="${ camp.validacioDataCmpNombre }"
												data-validacio-data-cmp-tipus="${ camp.validacioDataCmpTipus }"
												class="btn btn-default btn-edit-camp" title="<spring:message code="servei.camp.taula.accio.modificar"/>">
												<i class="fas fa-pen"></i>
											</a>
										</td>
										<td>
											<a href="camp/${ camp.id }/delete" title="<spring:message code="servei.camp.taula.accio.esborrar"/>" class="btn btn-default confirm-esborrar">
												<i class="fas fa-trash-alt"></i>
											</a>
										</td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
						</c:when>
						<c:otherwise>
							<p style="text-align:center"><spring:message code="servei.camp.grup.buit"/></p>
						</c:otherwise>
					</c:choose>
					<div style="text-align: right">
						<a href="campGrup/${grup.id}/up" title="<spring:message code="comu.boto.pujar"/>" class="btn btn-default <c:if test="${grupStatus.first}"> disabled</c:if>"><i class="fas fa-arrow-up"></i></a>
						<a href="campGrup/${grup.id}/down" title="<spring:message code="comu.boto.baixar"/>" class="btn btn-default <c:if test="${grupStatus.last}"> disabled</c:if>"><i class="fas fa-arrow-down"></i></a>
						<a href="#modal-grup-form" title="<spring:message code="comu.boto.modificar"/>" class="btn btn-default boto-grup-editar" data-id="${grup.id}" data-nom="${grup.nom}"><i class="fas fa-pen"></i></a>
						<a href="campGrup/${grup.id}/delete" title="<spring:message code="comu.boto.esborrar"/>" class="btn btn-default confirm-esborrar-grup"><i class="fas fa-trash-alt"></i></a>
					</div>
				</div>
			</c:forEach>
			<c:if test="${not empty camps}">
				<p style="text-align:right"><spring:message code="servei.camp.opcions"/></p>
			</c:if>
		</div>
	</div>
	
	<div class="well">
		<a href="<c:url value="/servei"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		<c:set var="initModalPreview">initModalPreview(this);return false</c:set>
		<a href="<c:url value="/modal/servei/${servei.codiUrlEncoded}/preview"/>" class="btn btn-info" onclick="${initModalPreview}"><i class="fas fa-eye"></i>&nbsp;<spring:message code="comu.boto.previsualitzar"/></a>
	</div>

	<form id="hidden-form" action="camp/add" method="post" class="form-horizontal">
		<input type="hidden" id="hidden-hidden-servei" name="servei"/>
		<input type="hidden" id="hidden-hidden-path" name="path"/>
	</form>

	<div id="modal-editar-camp" class="modal" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><spring:message code="servei.camp.titol.modificar"/></h3>
			</div>
			<div class="modal-body">
				<form id="modal-form" action="camp/update" method="post">
					<input type="hidden" id="modal-hidden-id" name="id"/>
					<input type="hidden" id="modal-hidden-servei" name="servei" value="${servei.codi}"/>
					<div class="form-group">
						<label for="modal-input-path"><spring:message code="servei.camp.path"/> *</label>
						<input type="hidden" id="modal-hidden-path" name="path"/>
						<input type="text" id="modal-input-path" name="path" class="form-control" disabled="disabled"/>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label for="modal-input-tipus"><spring:message code="servei.camp.tipus"/> *</label>
								<select id="modal-select-tipus" name="tipus" class="form-control">
									<c:forEach var="tip" items="${serveiCampTipus}">
										<option value="${tip}"<c:if test="${tip == 'ENUM'}"> id="modal-option-tipus-enum"</c:if>>${tip}</option>
									</c:forEach>
								</select>
							</div>
							<div class="form-group">
								<label for="modal-input-etiqueta"><spring:message code="servei.camp.etiqueta"/></label>
								<input type="text" id="modal-input-etiqueta" name="etiqueta" class="form-control"/>
							</div>
							<div class="form-group">
								<label class="control-label" for="modal-input-defecte"><spring:message code="servei.camp.valor.defecte"/></label>
								<input type="text" id="modal-input-defecte" name="valorPerDefecte" class="form-control"/>
							</div>
						</div>
						<div class="col-md-6">
							<div id="modal-grup-data" class="form-group">
								<label for="modal-input-comentari"><spring:message code="servei.camp.data.camp.pare"/></label>
								<select class="form-control" id="modal-select-camp-pare" name="campPareId" class="input-lg">
									<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
									<c:forEach var="camp" items="${camps}">
										<option value="${camp.id}">${camp.path}</option>
									</c:forEach>
								</select>
							</div>
							<div id="modal-grup-data" class="form-group">
								<label for="modal-input-comentari"><spring:message code="servei.camp.data.valor.pare"/></label>
								<input class="form-control" type="text" id="modal-input-valor-pare" name="valorPare" class="input-lg"/>
							</div>
							<div id="modal-grup-data" class="form-group">
								<label for="modal-input-comentari"><spring:message code="servei.camp.data.format"/></label>
								<input class="form-control" type="text" id="modal-input-data-format" name="dataFormat" class="input-lg"/>
								<span class="help-block"><small><spring:message code="servei.camp.data.format.comment"/></small></span>
							</div>
							
						</div>
					</div>
					<div class="form-group">
						<label for="modal-input-comentari"><spring:message code="servei.camp.comentari"/></label>
						<input type="text" id="modal-input-comentari" name="comentari" class="form-control"/>
					</div>
					<div class="form-group">
						<c:if test="${servei.pinbalIniDadesExpecifiques}">
							<div class="checkbox-inline">
								<label for="modal-checkbox-inicialitzar">
								<input type="checkbox" id="modal-checkbox-inicialitzar" name="inicialitzar"/>
								<spring:message code="servei.camp.inicialitzar"/>
								</label>
							</div>
						</c:if>
						<c:if test="${!servei.pinbalIniDadesExpecifiques}">
							<input type="hidden" name="inicialitzar" value="false"/>
						</c:if>
						<div class="checkbox-inline">
							<label for="modal-checkbox-obligatori">
							<input type="checkbox" id="modal-checkbox-obligatori" name="obligatori"/>
							<spring:message code="servei.camp.obligatori"/>
							</label>
						</div>
						<div class="checkbox-inline">
							<label for="modal-checkbox-modificable">
							<input type="checkbox" id="modal-checkbox-modificable" name="modificable"/>
							<spring:message code="servei.camp.modificable"/>
							</label>
						</div>
						<div class="checkbox-inline">
							<label for="modal-checkbox-visible">
							<input type="checkbox" id="modal-checkbox-visible" name="visible"/>
							<spring:message code="servei.camp.visible"/>
							</label>
						</div>
					</div>
					<c:forEach var="camp" items="${campsEnumList}" varStatus="status">
						<div id="modal-grup-descripcions-${camp.id}" class="form-group grup-descripcions">
							<label for="modal-input-descripcions"><spring:message code="servei.camp.descripcions"/></label>
							<c:forEach var="valor" items="${valorsEnumList[status.index]}" varStatus="statusValor">
								<div class="input-group">
									<span class="input-group-addon">${valor}</span>
									<input id="modal-input-descripcio-${statusValor.index}" class="form-control" type="text" name="descripcio-${camp.id}"<c:if test="${statusValor.index lt fn:length(camp.enumDescripcions)}"> value="${camp.enumDescripcions[statusValor.index]}"</c:if>/>
								</div>
								<c:if test="${not statusValor.last}"><br/></c:if>
							</c:forEach>
						</div>
					</c:forEach>
					<div id="validacio">
						<fieldset>
							<legend>Validació</legend>
							<div id="validacio_tipus_text">
								<div class="form-group">
									<label for="modal-input-val-regexp"><spring:message code="servei.camp.validacio.regexp"/></label>
									<input class="form-control" type="text" id="modal-input-val-regexp" name="validacioRegexp" class="input-lg"/>
								</div>
							</div>
							<div id="validacio_tipus_numeric">
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="modal-input-val-min"><spring:message code="servei.camp.validacio.min"/></label>
											<input class="form-control" type="number" id="modal-input-val-min" name="validacioMin" class="input-lg"/>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="modal-input-val-max"><spring:message code="servei.camp.validacio.max"/></label>
											<input class="form-control" type="number" id="modal-input-val-max" name="validacioMax" class="input-lg"/>
										</div>
									</div>
								</div>
							</div>
							<div id="validacio_tipus_data">
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="modal-input-val-datvalcamp"><spring:message code="servei.camp.validacio.data.valorcamp"/></label>
											<select id="modal-input-val-datvalcamp" name="validacioDataCmpValcamp" class="form-control">
												<option value="VALOR"><spring:message code="servei.camp.validacio.enum.tipval.VALOR"/></option>
												<option value="DIFERENCIA"><spring:message code="servei.camp.validacio.enum.tipval.DIFERENCIA"/></option>
											</select>
										</div>
									</div>
								</div>
								<div class="row">
									<div id="validacio_tipus_data_valor">
										<div class="col-md-4">
											<div class="form-group">
												<label for="modal-input-val-datop"><spring:message code="servei.camp.validacio.data.operacio"/></label>
												<select id="modal-input-val-datop" name="validacioDataCmpOperacio" class="form-control">
													<c:forEach var="op" items="${serveiCampValidacioOperacio}">
														<option value="${op}"><spring:message code="servei.camp.validacio.enum.operacio.${op}"/></option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="col-md-8">
											<div class="form-group">
												<label for="modal-input-val-datcamp2"><spring:message code="servei.camp.validacio.data.camp2"/></label>
												<select class="form-control" id="modal-input-val-datcamp2" name="validacioDataCmpCamp2Id" class="input-lg">
													<c:forEach var="camp" items="${camps}">
														<c:if test="${camp.tipus=='DATA'}">
															<option value="${camp.id}">${camp.path}</option>
														</c:if>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
									<div id="validacio_tipus_data_diferencia">
										<div class="col-md-6">
											<div class="form-group">
												<label for="modal-input-val-datcamp3"><spring:message code="servei.camp.validacio.data.camp2"/></label>
												<select class="form-control" id="modal-input-val-datcamp3" name="validacioDataCmpCamp3Id" class="input-lg">
													<c:forEach var="camp" items="${camps}">
														<c:if test="${camp.tipus=='DATA'}">
															<option value="${camp.id}">${camp.path}</option>
														</c:if>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="col-md-2">
											<div class="form-group">
												<label for="modal-input-val-datop2"><spring:message code="servei.camp.validacio.data.operacio"/></label>
												<select id="modal-input-val-datop2" name="validacioDataCmpOperacio2" class="form-control">
													<c:forEach var="op" items="${serveiCampValidacioOperacio}">
														<option value="${op}"><spring:message code="servei.camp.validacio.enum.operacio.${op}"/></option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="col-md-2">
											<div class="form-group">
												<label for="modal-input-val-datnum"><spring:message code="servei.camp.validacio.data.nombre"/></label>
												<input class="form-control" type="number" id="modal-input-val-datnum" name="validacioDataCmpNombre" class="input-lg"/>
											</div>
										</div>
										<div class="col-md-2">
											<div class="form-group">
												<label for="modal-input-val-dattip"><spring:message code="servei.camp.validacio.data.tipus"/></label>
												<select id="modal-input-val-dattip" name="validacioDataCmpTipus" class="form-control">
													<c:forEach var="tip" items="${serveiCampValidacioDataTipus}">
														<option value="${tip}"><spring:message code="servei.camp.validacio.enum.tipus.${tip}"/></option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
							</div>
						</fieldset>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></button>
				<button id="modal-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			</div>
		</div>
		</div>
	</div>
	<div id="modal-form-preview" class="modal fade" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h3><spring:message code="servei.camp.titol.previsualitzacio"/></h3>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></button>
				</div>
			</div>
		</div>
	</div>
	<div id="modal-grup-form" class="modal fade" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3 id="modal-grup-titol"></h3>
			</div>
			<div class="modal-body">
				<form id="modal-grup-formform" action="" method="post">
					<input type="hidden" id="modal-grup-hidden-id" name="id"/>
					<input type="hidden" id="modal-grup-hidden-servei" name="servei" value="${servei.codi}"/>
					<div class="form-group">
						<label for="modal-grup-input-nom"><spring:message code="servei.camp.grup.form.nom"/> *</label>
						<input class="form-control" type="text" id="modal-grup-input-nom" name="nom"/>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></button>
				<button id="modal-grup-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			</div>
			</div>
		</div>
	</div>
</body>
</html>
