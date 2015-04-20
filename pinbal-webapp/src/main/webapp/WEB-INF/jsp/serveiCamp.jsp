<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%
	request.setAttribute(
			"serveiCampTipus",
			es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoTipus.values());
	request.setAttribute(
			"llistatDadesEspecifiques",
			((es.caib.pinbal.core.dto.ArbreDto<?>)request.getAttribute("arbreDadesEspecifiques")).toList());
%>

<html>
<head>
	<title><spring:message code="servei.camp.titol"/></title>
	<c:choose>
		<c:when test="${not empty servei.descripcio}"><c:set var="serveiPerTitol" value="${servei.descripcio}"/></c:when>
		<c:otherwise><c:set var="serveiPerTitol" value="${servei.codi}"/></c:otherwise>
	</c:choose>
	<meta name="subtitle" content="${serveiPerTitol}"/>
	<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.8.20.custom.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
<script>
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
function initModalCamp(id, path, tipus, etiqueta, defecte, comentari, dataFormat, campPareId, valorPare, obligatori, modificable, visible) {
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
	if ($('#modal-select-tipus').val() == 'MUNICIPI') {
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
	$('#modal-select-tipus').change(function() {
		if ($('#modal-select-tipus').val() == 'DATA') {
			$('#modal-input-data-format').removeAttr('disabled');
		} else {
			$('#modal-input-data-format').attr('disabled', 'disabled');
		}
		if ($('#modal-select-tipus').val() == 'MUNICIPI') {
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
	});
	$('#modal-editar-camp').modal('toggle');
}
// Modal per a previsualitzar el formulari
function initModalPreview(element) {
	$('#modal-form-preview .modal-body').load(element.href);
	$('#modal-form-preview').css('width', '700px');
	$('#modal-form-preview').modal('toggle');
}
// JMesa
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
$(function() {
	// Ordenació del llistat de camps
	$("#jmesa-camps tbody").sortable({
		cursor: 'move',
		start: function(e, ui) {
			//$(this).attr('data-previndex', ui.item.index());
			$(this).attr('data-campId', $('input[name="id"]', ui.item).val());
		},
		update: function(e, ui) {
			var newIndex = ui.item.index();
			//var oldIndex = $(this).attr('data-previndex');
			//$(this).removeAttr('data-previndex');
			var campId = $(this).attr('data-campId');
			$(this).removeAttr('data-campId');
			window.location = 'camp/move/' + campId + '/' + newIndex;
		}
	});
	$("#jmesa-camps tbody").disableSelection();
	// Canvi d'icones al ocultar/mostrar
	$('.arbre-node').on('hidden', function (event) {
		var iconId = '#icon-' + this.id.substring(3);
		$(iconId).attr('class', 'icon-chevron-right');
		event.stopPropagation();
	});
	$('.arbre-node').on('shown', function (event) {
		var iconId = '#icon-' + this.id.substring(3);
		$(iconId).attr('class', 'icon-chevron-down');
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
});
</script>
</head>
<body>

	<div class="row-fluid">
		<div class="well span4" style="overflow:auto">
			<div class="btn-group pull-right">
				<a href="#" class="btn" title="<spring:message code="servei.camp.contreure.tot"/>" id="accio-contreure-all"><i class="icon-chevron-right"></i></a>
				<a href="#" class="btn" title="<spring:message code="servei.camp.expandir.tot"/>" id="accio-expandir-all"><i class=icon-chevron-down></i></a>
			</div><br/>
			<ul style="list-style:none; margin:0">
				<c:set var="nodeArbreActual" value="${arbreDadesEspecifiques.arrel}" scope="request"/>
				<jsp:include page="import/dadesEspecifiquesArbreNode.jsp"/>
			</ul>
		</div>
		<div class="span8">
			<a id="boto-nou-grup" class="btn pull-right" href="#"><i class="icon-plus"></i>&nbsp;Nou grup</a><br/><br/>
			<c:set var="hiHaCampsSenseGrup" value="${false}"/>
			<c:forEach var="camp" items="${camps}">
				<c:if test="${empty camp.grup}"><c:set var="hiHaCampsSenseGrup" value="${true}"/></c:if>
			</c:forEach>
			<c:if test="${hiHaCampsSenseGrup}">
				<jmesa:tableModel
						id="jmesa-camps"
						items="${camps}"
						view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
						var="registre"
						maxRows="${fn:length(camps)}">
					<jmesa:htmlTable>
						<c:if test="${empty registre.grup}">
							<jmesa:htmlRow>
								<jmesa:htmlColumn property="CALCUL_campNom" titleKey="servei.camp.taula.columna.nom" sortable="false">
									<input type="hidden" name="id" value="${registre.id}"/>
									<span title="${registre.path}">${registre.campNom}</span>
								</jmesa:htmlColumn>
								<jmesa:htmlColumn property="tipus" titleKey="servei.camp.taula.columna.tipus" sortable="false"/>
								<jmesa:htmlColumn property="etiqueta" titleKey="servei.camp.taula.columna.etiqueta" sortable="false"/>
								<jmesa:htmlColumn property="CALCUL_obligatori" titleKey="servei.camp.taula.columna.o" sortable="false">
									<c:if test="${registre.obligatori}"><i class="icon-ok"></i></c:if>
								</jmesa:htmlColumn>
								<jmesa:htmlColumn property="CALCUL_modificable" titleKey="servei.camp.taula.columna.m" sortable="false">
									<c:if test="${registre.modificable}"><i class="icon-ok"></i></c:if>
								</jmesa:htmlColumn>
								<jmesa:htmlColumn property="CALCUL_visible" titleKey="servei.camp.taula.columna.v" sortable="false">
									<c:if test="${registre.visible}"><i class="icon-ok"></i></c:if>
								</jmesa:htmlColumn>
								<c:if test="${not empty grups}">
									<jmesa:htmlColumn property="ACCIO_grup" title="&nbsp;" style="white-space:nowrap;" sortable="false">
										<div class="btn-group">
											<a href="#" title="<spring:message code="servei.camp.taula.accio.agrupar"/>" class="btn dropdown-toggle" data-toggle="dropdown"><i class="icon-download"></i>&nbsp;<span class="caret"></span></a>
											<ul class="dropdown-menu">
												<c:forEach var="grup" items="${grups}">
													<li><a href="camp/${registre.id}/agrupar/${grup.id}">${grup.nom}</a></li>
												</c:forEach>
											</ul>
										</div>
									</jmesa:htmlColumn>
								</c:if>
								<jmesa:htmlColumn property="ACCIO_edit" title="&nbsp;" style="white-space:nowrap;" sortable="false">
									<c:set var="jsInitModalCamp">initModalCamp('${registre.id}', '${registre.path}', '${registre.tipus}', '${fn:replace(registre.etiqueta,"'","\\'")}', '${fn:replace(registre.valorPerDefecte,"'","\\'")}', '${fn:replace(registre.comentari,"'","\\'")}', '${registre.dataFormat}', '${registre.campPare.id}', '${registre.valorPare}', ${registre.obligatori}, ${registre.modificable}, ${registre.visible})</c:set>
									<a href="#modal-editar-camp" title="<spring:message code="servei.camp.taula.accio.modificar"/>" onclick="${jsInitModalCamp}" class="btn"><i class="icon-pencil"></i></a>
								</jmesa:htmlColumn>
								<jmesa:htmlColumn property="ACCIO_delete" title="&nbsp;" style="white-space:nowrap;" sortable="false">
									<a href="camp/${registre.id}/delete" title="<spring:message code="servei.camp.taula.accio.esborrar"/>" class="btn confirm-esborrar"><i class="icon-trash"></i></a>
								</jmesa:htmlColumn>
							</jmesa:htmlRow>
						</c:if>
					</jmesa:htmlTable>
				</jmesa:tableModel>
			</c:if>
			<c:forEach var="grup" items="${grups}" varStatus="grupStatus">
				<div class="well well-small">
					<fieldset>
					 	<legend>${grup.nom}</legend>
	    			</fieldset>
	    			<c:choose>
	    				<c:when test="${not empty campsAgrupats[grup.id]}">
			    			<jmesa:tableModel
									id="jmesa-camps"
									items="${campsAgrupats[grup.id]}"
									view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
									var="registre"
									maxRows="${fn:length(campsAgrupats[grup.id])}">
								<jmesa:htmlTable>
									<jmesa:htmlRow>
										<jmesa:htmlColumn property="CALCUL_campNom" titleKey="servei.camp.taula.columna.nom" sortable="false">
											<input type="hidden" name="id" value="${registre.id}"/>
											<span title="${registre.path}">${registre.campNom}</span>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="tipus" titleKey="servei.camp.taula.columna.tipus" sortable="false"/>
										<jmesa:htmlColumn property="etiqueta" titleKey="servei.camp.taula.columna.etiqueta" sortable="false"/>
										<jmesa:htmlColumn property="CALCUL_obligatori" titleKey="servei.camp.taula.columna.o" sortable="false">
											<c:if test="${registre.obligatori}"><i class="icon-ok"></i></c:if>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="CALCUL_modificable" titleKey="servei.camp.taula.columna.m" sortable="false">
											<c:if test="${registre.modificable}"><i class="icon-ok"></i></c:if>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="CALCUL_visible" titleKey="servei.camp.taula.columna.v" sortable="false">
											<c:if test="${registre.visible}"><i class="icon-ok"></i></c:if>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="ACCIO_grup" title="&nbsp;" style="white-space:nowrap;" sortable="false">
											<div class="btn-group">
												<a href="#" title="<spring:message code="servei.camp.taula.accio.agrupar"/>" class="btn dropdown-toggle" data-toggle="dropdown"><i class="icon-download"></i>&nbsp;<span class="caret"></span></a>
												<ul class="dropdown-menu">
													<li><a href="camp/${registre.id}/desagrupar">Sense grup</a></li>
													<c:forEach var="grupItem" items="${grups}">
														<c:if test="${grupItem.id != grup.id}">
															<li><a href="camp/${registre.id}/agrupar/${grupItem.id}">${grupItem.nom}</a></li>
														</c:if>
													</c:forEach>
												</ul>
											</div>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="ACCIO_edit" title="&nbsp;" style="white-space:nowrap;" sortable="false">
											<c:set var="jsInitModalCamp">initModalCamp('${registre.id}', '${registre.path}', '${registre.tipus}', '${fn:replace(registre.etiqueta,"'","\\'")}', '${fn:replace(registre.valorPerDefecte,"'","\\'")}', '${fn:replace(registre.comentari,"'","\\'")}', '${registre.dataFormat}', '${registre.campPare.id}', '${registre.valorPare}', ${registre.obligatori}, ${registre.modificable}, ${registre.visible})</c:set>
											<a href="#modal-editar-camp" title="<spring:message code="servei.camp.taula.accio.modificar"/>" onclick="${jsInitModalCamp}" class="btn"><i class="icon-pencil"></i></a>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="ACCIO_delete" title="&nbsp;" style="white-space:nowrap;" sortable="false">
											<a href="camp/${registre.id}/delete" title="<spring:message code="servei.camp.taula.accio.esborrar"/>" class="btn confirm-esborrar"><i class="icon-trash"></i></a>
										</jmesa:htmlColumn>
									</jmesa:htmlRow>
								</jmesa:htmlTable>
							</jmesa:tableModel>
						</c:when>
						<c:otherwise>
							<p style="text-align:center"><spring:message code="servei.camp.grup.buit"/></p>
						</c:otherwise>
					</c:choose>
					<div style="text-align: right">
						<a href="campGrup/${grup.id}/up" title="<spring:message code="comu.boto.pujar"/>" class="btn<c:if test="${grupStatus.first}"> disabled</c:if>"><i class="icon-arrow-up"></i></a>
						<a href="campGrup/${grup.id}/down" title="<spring:message code="comu.boto.baixar"/>" class="btn<c:if test="${grupStatus.last}"> disabled</c:if>"><i class="icon-arrow-down"></i></a>
						<a href="#modal-grup-form" title="<spring:message code="comu.boto.modificar"/>" class="btn boto-grup-editar" data-id="${grup.id}" data-nom="${grup.nom}"><i class="icon-pencil"></i></a>
						<a href="campGrup/${grup.id}/delete" title="<spring:message code="comu.boto.esborrar"/>" class="btn confirm-esborrar-grup"><i class="icon-trash"></i></a>
					</div>
				</div>
			</c:forEach>
			<c:if test="${not empty camps}">
				<p style="text-align:right"><spring:message code="servei.camp.opcions"/></p>
			</c:if>
		</div>
	</div>

	<form id="hidden-form" action="camp/add" method="post" class="form-horizontal">
		<input type="hidden" id="hidden-hidden-servei" name="servei"/>
		<input type="hidden" id="hidden-hidden-path" name="path"/>
	</form>

	<div id="modal-editar-camp" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="servei.camp.titol.modificar"/></h3>
		</div>
		<div class="modal-body">
			<form id="modal-form" action="camp/update" method="post" class="form-horizontal">
				<input type="hidden" id="modal-hidden-id" name="id"/>
				<input type="hidden" id="modal-hidden-servei" name="servei" value="${servei.codi}"/>
				<div class="control-group">
    				<label class="control-label" for="modal-input-path"><spring:message code="servei.camp.path"/> *</label>
					<div class="controls">
						<input type="hidden" id="modal-hidden-path" name="path"/>
						<input type="text" id="modal-input-path" name="path" class="input-xlarge" disabled="disabled"/>
					</div>
				</div>
				<div class="control-group">
    				<label class="control-label" for="modal-input-tipus"><spring:message code="servei.camp.tipus"/> *</label>
					<div class="controls">
						<select id="modal-select-tipus" name="tipus" class="input-xlarge">
							<c:forEach var="tip" items="${serveiCampTipus}">
								<option value="${tip}"<c:if test="${tip == 'ENUM'}"> id="modal-option-tipus-enum"</c:if>>${tip}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="control-group">
    				<label class="control-label" for="modal-input-etiqueta"><spring:message code="servei.camp.etiqueta"/></label>
					<div class="controls">
						<input type="text" id="modal-input-etiqueta" name="etiqueta" class="input-xlarge"/>
					</div>
				</div>
				<div class="control-group">
    				<label class="control-label" for="modal-input-defecte"><spring:message code="servei.camp.valor.defecte"/></label>
					<div class="controls">
						<input type="text" id="modal-input-defecte" name="valorPerDefecte" class="input-xlarge"/>
					</div>
				</div>
				<div class="control-group">
    				<label class="control-label" for="modal-input-comentari"><spring:message code="servei.camp.comentari"/></label>
					<div class="controls">
						<input type="text" id="modal-input-comentari" name="comentari" class="input-xlarge"/>
					</div>
				</div>
				<div class="control-group">
					<div class="controls">
						<label class="checkbox inline" for="modal-checkbox-obligatori">
							<input type="checkbox" id="modal-checkbox-obligatori" name="obligatori"/>
							<spring:message code="servei.camp.obligatori"/>
						</label>
						<label class="checkbox inline" for="modal-checkbox-modificable">
							<input type="checkbox" id="modal-checkbox-modificable" name="modificable"/>
							<spring:message code="servei.camp.modificable"/>
						</label>
						<label class="checkbox inline" for="modal-checkbox-visible">
							<input type="checkbox" id="modal-checkbox-visible" name="visible"/>
							<spring:message code="servei.camp.visible"/>
						</label>
					</div>
				</div>
				<div id="modal-grup-data" class="control-group">
    				<label class="control-label" for="modal-input-comentari"><spring:message code="servei.camp.data.format"/></label>
					<div class="controls">
						<input type="text" id="modal-input-data-format" name="dataFormat" class="input-xlarge"/>
						<span class="help-block"><small><spring:message code="servei.camp.data.format.comment"/></small></span>
					</div>
				</div>
				<div id="modal-grup-data" class="control-group">
    				<label class="control-label" for="modal-input-comentari"><spring:message code="servei.camp.data.camp.pare"/></label>
					<div class="controls">
						<select id="modal-select-camp-pare" name="campPareId" class="input-xlarge">
							<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
							<c:forEach var="camp" items="${camps}">
								<option value="${camp.id}">${camp.path}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div id="modal-grup-data" class="control-group">
    				<label class="control-label" for="modal-input-comentari"><spring:message code="servei.camp.data.valor.pare"/></label>
					<div class="controls">
						<input type="text" id="modal-input-valor-pare" name="valorPare" class="input-xlarge"/>
					</div>
				</div>
				<c:forEach var="camp" items="${camps}">
					<c:if test="${camp.tipus == 'ENUM'}">
						<c:forEach var="node" items="${llistatDadesEspecifiques}">
							<c:if test="${node.dades.pathAmbSeparadorDefault == camp.path}">
								<div id="modal-grup-descripcions-${camp.id}" class="control-group grup-descripcions">
			    					<label class="control-label" for="modal-input-descripcions"><spring:message code="servei.camp.descripcions"/></label>
			    					<c:forEach var="valor" items="${node.dades.enumeracioValors}" varStatus="status">
			    						<div class="controls">
											<div class="input-prepend">
												<span class="add-on">${valor}</span><input type="text" id="modal-input-descripcio-${status.index}" name="descripcio-${camp.id}"<c:if test="${status.index lt fn:length(camp.enumDescripcions)}"> value="${camp.enumDescripcions[status.index]}"</c:if> class="input-large"/>
											</div>
										</div><c:if test="${not status.last}"><br/></c:if>
			    					</c:forEach>
								</div>
							</c:if>
						</c:forEach>
					</c:if>
				</c:forEach>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" id="modal-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>

	<div id="modal-form-preview" class="modal hide fade" style="width: 750px;margin-left: -375px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="servei.camp.titol.previsualitzacio"/></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
		</div>
	</div>

	<div id="modal-grup-form" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 id="modal-grup-titol"></h3>
		</div>
		<div class="modal-body">
			<form id="modal-grup-formform" action="" method="post" class="form-horizontal">
				<input type="hidden" id="modal-grup-hidden-id" name="id"/>
				<input type="hidden" id="modal-grup-hidden-servei" name="servei" value="${servei.codi}"/>
				<div class="control-group">
    				<label class="control-label" for="modal-grup-input-nom"><spring:message code="servei.camp.grup.form.nom"/> *</label>
					<div class="controls">
						<input type="text" id="modal-grup-input-nom" name="nom" class="input-xlarge"/>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" id="modal-grup-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>

	<div class="well">
		<a href="<c:url value="/servei"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<c:set var="initModalPreview">initModalPreview(this);return false</c:set>
		<a href="<c:url value="/modal/servei/${servei.codi}/preview"/>" class="btn btn-info" onclick="${initModalPreview}"><i class="icon-eye-open icon-white"></i>&nbsp;<spring:message code="comu.boto.previsualitzar"/></a>
	</div>

</body>
</html>
