<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<c:if test="${empty dadesEspecifiquesDisabled}"><c:set var="dadesEspecifiquesDisabled" value="${false}"/></c:if>
<c:set var="numColumnes" value="${2}"/>
<c:set var="indexCamp" value="${0}"/>
<c:forEach var="camp" items="${campsPerMostrar}" varStatus="status">
	<c:set var="campId" value="camp_${camp.id}"/>
	<c:set var="campCommandPath" value="dadesEspecifiques[${camp.path}]"/>
	<c:set var="campError"><form:errors path="${campCommandPath}"/></c:set>
	<c:set var="ruta" value="/${camp.path}"></c:set>
	<c:set var="valorDadaEspecifica" value="${dadesEspecifiquesValors[ruta]}"/>
	<c:if test="${not dadesEspecifiquesDisabled}"><c:set var="campValorDefecte">${camp.valorPerDefecte}</c:set></c:if>
	<c:if test="${dadesEspecifiquesDisabled}"><c:set var="campValorDefecte">${valorDadaEspecifica}</c:set></c:if>
	<c:if test="${pageContext.request.method == 'POST'}"><c:set var="campValorDefecte">${param[campId]}</c:set></c:if>
	<c:set var="dadaEspecifica" value="${null}"/>
	<c:forEach var="nodeDadaEspecifica" items="${llistaArbreDadesEspecifiques}">
		<c:if test="${nodeDadaEspecifica.dades.pathAmbSeparadorDefault == camp.path}">
			<c:set var="dadaEspecifica" value="${nodeDadaEspecifica.dades}"/>
		</c:if>
	</c:forEach>
	<c:choose>
		<c:when test="${camp.visible}">
			<c:if test="${indexCamp == 0 or (indexCamp % numColumnes) == 0}">
				<div class="row">
			</c:if>
			<div class="col-md-<fmt:formatNumber value="${12 / numColumnes}" maxFractionDigits="0"/>">
				<div class="form-group<c:if test="${not empty campError}"> has-error</c:if><c:if test='${camp.campRegla}'> camp-regla</c:if>" data-path="${camp.path}">
					<label class="control-label" for="${campId}">
						<c:choose>
							<c:when test="${camp.tipus == 'ETIQUETA'}">&nbsp;</c:when>
							<c:when test="${not empty camp.etiqueta}">${camp.etiqueta}<c:if test="${not dadesEspecifiquesDisabled and camp.obligatori}"> *</c:if></c:when>
							<c:otherwise>${camp.campNom}<c:if test="${not dadesEspecifiquesDisabled and camp.obligatori}"> *</c:if></c:otherwise>
						</c:choose>
					</label>
					<c:choose>
						<c:when test="${camp.modificable}">
							<c:choose>
								<c:when test="${empty dadaEspecifica.enumeracioValors}">
									<c:choose>
										<c:when test="${camp.tipus == 'TEXT' or camp.tipus == 'DOC_IDENT'}">
											<input type="text" id="${campId}" name="${campId}"<c:if test="${not empty campValorDefecte}"> value="${campValorDefecte}"</c:if><c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if><c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if> class="form-control"/>
										</c:when>
										<c:when test="${camp.tipus == 'NUMERIC'}">
											<input type="number" id="${campId}" name="${campId}"<c:if test="${not empty campValorDefecte}"> value="${campValorDefecte}"</c:if><c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if><c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if> class="form-control"/>
										</c:when>
										<c:when test="${camp.tipus == 'DATA'}">
											<div class="input-group" style="width: 100%">
												<input type="text" id="${campId}" name="${campId}"<c:if test="${not empty campValorDefecte}"> value="${campValorDefecte}"</c:if><c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if><c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if> class="form-control"/>
												<span class="input-group-addon" style="width: 1%"><span class="far fa-calendar-alt"></span></span>
											</div>
<script>
$(document).ready(function() {
	$('#${campId}').mask('99/99/9999');
	$('#${campId}').datepicker({
		orientation: "bottom auto",
		format: 'dd/mm/yyyy',
		weekStart: 1,
		autoclose: true,
		language: 'es'
	});
});
</script>
										</c:when>
										<c:when test="${camp.tipus == 'BOOLEA'}">
											<c:set var="campChecked" value="${false}"/>
											<c:if test="${not empty valorDadaEspecifica}"><c:set var="campChecked" value="${valorDadaEspecifica == 'on'}"/></c:if>
											<input type="checkbox" id="${campId}" name="${campId}"<c:if test="${campChecked}"> checked="checked"</c:if><c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if> class="form-control"/>
											<input type="hidden" name="${campId}" value="off"/>
										</c:when>
										<c:when test="${camp.tipus == 'MUNICIPI_3' or camp.tipus == 'MUNICIPI_5'}">
											<c:set var="codiMunicipiSenseCodiProvincia" value="${camp.tipus == 'MUNICIPI_3'}"/>
											<select id="${campId}" name="${campId}"<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if> class="form-control">
												<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
											</select>
											<c:choose>
												<c:when test="${not empty camp.campPare}">
													<c:set var="campPareId" value="camp_${camp.campPare.id}"/>
<script>
$("#${campPareId}").change(function(event, valor) {
	$('#${campId}').empty();
	if ($('#${campPareId}').val().length > 0) {
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.carregant"/>'));
		$.ajax({
			url:'<c:url value="/dades/municipis"/>/' + $('#${campPareId}').val(),
			type:'GET',
			dataType: 'json',
			success: function(json) {
				$('#${campId}').empty();
				$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
				$.each(json, function(i, value) {
					var valueCodi = value.codi;
					<c:if test="${codiMunicipiSenseCodiProvincia}">valueCodi = value.codi.substring(2);</c:if>
					if ( $('#${campId}').data('defecte-processat')) {
						$('#${campId}').append($('<option>').text(value.nom).attr('value', valueCodi));
					} else {
						var valorPerDefecte = '${campValorDefecte}';
						if (value.codi == valorPerDefecte) {
							$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', valueCodi));
						} else {
							$('#${campId}').append($('<option>').text(value.nom).attr('value', valueCodi));
						}
					}
				});
				$('#${campId}').data('defecte-processat', true);
			}
		});
	} else {
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
	}
});
</script>
												</c:when>
												<c:when test="${not empty camp.valorPare}">
<script>
$.ajax({
	url:'<c:url value="/dades/municipis"/>/${camp.valorPare}',
	type:'GET',
	dataType: 'json',
	success: function(json) {
		$('#${campId}').empty();
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
		$.each(json, function(i, value) {
			var valorPerDefecte = '${campValorDefecte}';
			if (value.codi == valorPerDefecte) {
				$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', value.codi));
			} else {
				$('#${campId}').append($('<option>').text(value.nom).attr('value', value.codi));
			}
		});
	}
});
</script>
												</c:when>
											</c:choose>
										</c:when>
										<c:when test="${camp.tipus == 'PROVINCIA'}">
											<c:set var="campFillId" value=""/>
											<c:forEach var="candidatFill" items="${campsPerMostrar}">
												<c:if test="${not empty candidatFill.campPare and candidatFill.campPare.id == camp.id}"><c:set var="campFillId" value="camp_${candidatFill.campPare.id}"/></c:if>
											</c:forEach>
											<select id="${campId}" name="${campId}" <c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if> class="form-control">
												<option value=""><spring:message code="comu.opcio.carregant"/></option>
											</select>
<script>
$.ajax({
	url:'<c:url value="/dades/provincies"/>',
	type:'GET',
	dataType: 'json',
	success: function(json) {
		$('#${campId}').empty();
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
		$.each(json, function(i, value) {
			var valorPerDefecte = '${campValorDefecte}';
			if (value.codi == valorPerDefecte) {
				$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', value.codi));
				<c:if test="${not empty campFillId}">$('#${campFillId}').trigger('change', valorPerDefecte);</c:if>
			} else {
				$('#${campId}').append($('<option>').text(value.nom).attr('value', value.codi));
			}
		});
	}
});
</script>
										</c:when>
										<c:when test="${camp.tipus == 'PAIS'}">
											<c:set var="campFillId" value=""/>
											<c:forEach var="candidatFill" items="${campsPerMostrar}">
												<c:if test="${not empty candidatFill.campPare and candidatFill.campPare.id == camp.id}"><c:set var="campFillId" value="camp_${candidatFill.campPare.id}"/></c:if>
											</c:forEach>
											<select id="${campId}" name="${campId}" <c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if> class="form-control">
												<option value=""><spring:message code="comu.opcio.carregant"/></option>
											</select>
<script>
$.ajax({
	url:'<c:url value="/dades/paisos"/>',
	type:'GET',
	dataType: 'json',
	success: function(json) {
		$('#${campId}').empty();
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
		$.each(json, function(i, value) {
			var valorPerDefecte = '${campValorDefecte}';
			if (value.alpha3 == valorPerDefecte) {
				$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', value.alpha3));
				<c:if test="${not empty campFillId}">$('#${campFillId}').trigger('change', valorPerDefecte);</c:if>
			} else {
				$('#${campId}').append($('<option>').text(value.nom).attr('value', value.alpha3));
			}
		});
	}
});
</script>
										</c:when>
										<c:when test="${camp.tipus == 'ADJUNT_BINARI' or camp.tipus == 'ADJUNT_XML'}">
											<c:choose>
												<c:when test="${dadesEspecifiquesDisabled}">
													<input type="text" id="${campId}" name="${campId}" disabled="disabled"<c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if> class="form-control"/>
												</c:when>
												<c:otherwise>
													<div class="input-append">
														<input type="text" id="${campId}-file-helper" value="${serveiCommand.fitxerAjudaNom}" style="width:110%"/>
														<span id="${campId}-file-helper-clean" class="btn btn-default"><i class='icon-trash'></i></span>
														<span id="${campId}-file-helper-file" class="btn btn-default"><i class='icon-file'></i></span>
													</div>
													<input type="file" id="${campId}" name="${campId}" class="hide"/>
<script>
$(document).ready(function() {
	var $fileHelper = $('#${campId}-file-helper');
	var $fileHelperClean = $('#${campId}-file-helper-clean');
	var $fileHelperFile = $('#${campId}-file-helper-file');
	var fileClick = function() {
		if (!$fileHelper.val()) {
			$('#${campId}').trigger('click');
		}
	}
	if (!$fileHelper.val()) {
		$fileHelperClean.attr('disabled', 'disabled');
	} else {
		var path = $fileHelper.val();
		$fileHelperClean.removeAttr('disabled');
		$fileHelperFile.attr('disabled', 'disabled');
		$fileHelper.val(path);
		$fileHelper.attr('disabled', 'disabled');
		$fileHelper.change();
	}
	$fileHelperFile.click(fileClick);
	$fileHelper.click(fileClick);
	$fileHelperClean.click(function() {
		$fileHelper.val('');
		$fileHelper.removeAttr('disabled');
		$fileHelperClean.attr('disabled', 'disabled');
		var $fileHelperFileIcon = $('i', $fileHelperFile);
		$fileHelperFile.removeAttr('disabled');
		$('#${campId}').val('');
	});
	$('#${campId}').change(function(e) {
		var path = $(this).val();
		var path = e.target.files[0].name;
		if (path) {
			$fileHelperClean.removeAttr('disabled');
			$fileHelperFile.attr('disabled', 'disabled');
			$fileHelper.val(path);
			$fileHelper.attr('disabled', 'disabled');
			$fileHelper.change();
		}
	});
});
</script>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:when test="${camp.tipus == 'ETIQUETA'}"></c:when>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:set var="selectValue" value="${valorDadaEspecifica}"/>
									<c:if test="${empty selectValue}"><c:set var="selectValue" value="${camp.valorPerDefecte}"/></c:if>
									<select id="${campId}" name="${campId}"<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if> class="form-control">
										<c:if test="${dadesEspecifiquesDisabled or not camp.obligatori}">
											<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
										</c:if>
										<c:forEach var="enumeracioValor" items="${dadaEspecifica.enumeracioValors}" varStatus="status">
											<option value="${enumeracioValor}"<c:if test="${enumeracioValor == selectValue}"> selected="selected"</c:if>>${camp.enumDescripcions[status.index]}</option>
										</c:forEach>
									</select>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<input type="text" id="${campId}" name="${campId}"<c:if test="${not empty camp.valorPerDefecte}"> value="${camp.valorPerDefecte}"</c:if> class="form-control" disabled="disabled"/>
							<input type="hidden" id="${campId}_hidden" name="${campId}"<c:if test="${not empty camp.valorPerDefecte}"> value="${camp.valorPerDefecte}"</c:if>/>
						</c:otherwise>
					</c:choose>
					<c:set var="campErrors"><form:errors path="${campCommandPath}"/></c:set>
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campCommandPath}"/></p></c:if>
					<c:if test="${not dadesEspecifiquesDisabled and not empty camp.comentari}"><span class="help-block">${camp.comentari}</span></c:if>
				</div>
			</div>
			<c:if test="${status.last or (indexCamp % numColumnes) == (numColumnes - 1)}">
				</div>
			</c:if>
			<c:set var="indexCamp" value="${indexCamp + 1}"/>
		</c:when>
		<c:otherwise>
			<input type="hidden" id="${campId}" name="${campId}"<c:if test="${not empty camp.valorPerDefecte}"> value="${camp.valorPerDefecte}"</c:if>/>
		</c:otherwise>
	</c:choose>
</c:forEach>
