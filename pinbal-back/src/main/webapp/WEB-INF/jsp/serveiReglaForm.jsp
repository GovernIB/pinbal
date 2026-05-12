<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty serveiReglaCommand.id}"><c:set var="titol"><spring:message code="servei.regla.form.titol.nou"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="servei.regla.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<meta name="title" content="${titol}"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script>
		let tipusSeleccionatCamp = null;
		$(document).ready(function() {
			if ($('#modificat').val()) {
				tipusSeleccionatCamp = isCampTipus('${serveiReglaCommand.modificat}');
			}
			$("#modificat").change((e) => {
				updateValors($(e.currentTarget).val());
			});
		});

		const isCampTipus = (tipus) => { return (tipus === 'CAMPS' || tipus === 'ALGUN_CAMP')}
		const updateValors = (tipus) => {
			const isCamp = isCampTipus(tipus);
			if(tipusSeleccionatCamp != isCamp) {
				$("#modificatValor").val(null).trigger('change');
				$("#afectatValor").val(null).trigger('change');
				ompleVariables(isCamp);
			}
			tipusSeleccionatCamp = isCamp;
		}
		const ompleVariables = (isCamp) => {
			let modificatValorPare = $("#modificatValor").parent();
			let afectatValorPare = $("#afectatValor").parent();
			valorsLoadingStart(modificatValorPare, afectatValorPare);

			let getUrl = '';
			if (isCamp) {
				getUrl = '<c:url value="/servei/${servei.codiUrlEncoded}/regla/camp/select"/>';
			} else {
				getUrl = '<c:url value="/servei/${servei.codiUrlEncoded}/regla/grup/select"/>';
			}

			$.ajax({
				type: 'GET',
				url: getUrl,
				dataType: "json",
				async: true,
				success: function(data) {
					$("#modificatValor option").each(function(){
						$(this).remove();
					});
					$("#afectatValor option").each(function(){
						$(this).remove();
					});

					for (let i = 0; i < data.length; i++) {
						$("#modificatValor").append($("<option>" + (isCamp ? (data[i].codi + " | " + data[i].valor) : data[i].valor) + "</option>"));
						$("#afectatValor").append($("<option>" + (isCamp ? (data[i].codi + " | " + data[i].valor) : data[i].valor) + "</option>"));
					}
					$("#modificatValor").val('').change();
					$("#afectatValor").val('').change();
					valorsLoadingStop(modificatValorPare, afectatValorPare);
				},
				error: function(e) {
					console.log("Error obtenint dades: " + e);
					valorsLoadingStop(modificatValorPare, afectatValorPare);
				}
			});
		}
		const valorsLoadingStart = (el1, el2) => {
			$("<span class='fa fa-circle-o-notch fa-spin valors-load'></span>").appendTo($(".select2-container", el1));
			$("<span class='fa fa-circle-o-notch fa-spin valors-load'></span>").appendTo($(".select2-container", el2));
		}
		const valorsLoadingStop = (el1, el2) => {
			el1.find(".valors-load").remove();
			el2.find(".valors-load").remove();
		}
	</script>
	<style>
		.text-left { text-align: left !important; }
		.select2-container { width: 100% !important; }
		.valors-load { position: absolute; font-size: 20px; right: 10px; top: 8px; color: cornflowerblue; }
		div:has(> #modificatValor) {margin-bottom: 16px;}
	</style>
</head>
<body>
<c:choose>
	<c:when test="${empty serveiReglaCommand.id}"><c:set var="formAction"><c:url value="/modal/servei/camp/${servei.codiUrlEncoded}/regla/new"/></c:set></c:when>
	<c:otherwise><c:set var="formAction"><c:url value="/modal/servei/camp/${servei.codiUrlEncoded}/regla/${serveiReglaCommand.id}"/></c:set></c:otherwise>
</c:choose>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="serveiReglaCommand">
		<form:hidden path="id"/>
		<form:hidden path="serveiId"/>

		<div class="row">
			<div class="col-xs-12">
				<pbl:inputText name="nom" textKey="servei.regla.nom"  labelSize="2" required="true"/>
			</div>
		</div>
		<hr/>
		<div class="row">
			<div class="col-xs-4">
				<pbl:inputSelect name="modificat" textKey="servei.regla.modificat" emptyOption="true" optionItems="${modificatOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="6" required="true"/>
			</div>
			<div class="col-xs-8" style="padding-right: 32px;">
				<pbl:inputSelect name="modificatValor" emptyOption="true" optionItems="${valors}" optionValueAttribute="valor" optionTextAttribute="valor" inline="true" required="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<pbl:inputSelect name="afectatValor" textKey="servei.regla.afectat.valor" emptyOption="true" optionItems="${valors}" optionValueAttribute="valor" optionTextAttribute="valor" labelSize="2" required="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<pbl:inputSelect name="accio" textKey="servei.regla.accio" emptyOption="true" optionItems="${accioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2" required="true"/>
			</div>
		</div>
		<div style="min-height: 150px;"></div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
