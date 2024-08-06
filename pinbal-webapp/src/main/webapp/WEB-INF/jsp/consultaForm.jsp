<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	pageContext.setAttribute(
			"consentimentValors",
			es.caib.pinbal.core.dto.ConsultaDto.Consentiment.values());
%>

<c:set var="serveiMultiple" value="${servei.consultaMultiplePermesa}"/>
<c:set var="serveiSimple" value="${servei.consultaSimplePermesa}"/>
<c:set var="tabSimpleActiu" value="${not consultaCommand.multiple}"/>
<c:set var="tabMultipleActiu" value="${not tabSimpleActiu}"/>
<c:url var="urlFitxerErrors" value="/consulta/errors/${fitxerAmbErrors}/download" />

<html>
<head>
	<title><spring:message code="consulta.form.titol" arguments="${servei.descripcio}"/></title>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

	<script src="<c:url value="/js/bootstrap.file-input.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>

	<link href="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/js/jasny-bootstrap.min.js"/>"></script>
	<script>

		<c:if test="${not empty consultaCommand.multipleErrorsValidacio}">
		let errorMsgs = '<spring:message code="consulta.form.camp.multiple.errors.fitxer"/>:\n';
		<c:forEach items="${consultaCommand.multipleErrorsValidacioScaped}" var="err">errorMsgs += '\t - ${err}\n';
		</c:forEach>
		let fitxerErrorUrl = "${urlFitxerErrors}";
		</c:if>

		$(document).ready(function() {
			<c:if test="${serveiMultiple && serveiSimple}">
				$('#tabs-simple-multiple a:first').click(function (e) {
					$("#multiple").val('false');
				});
				$('#tabs-simple-multiple a:last').click(function (e) {
					$("#multiple").val('true');
				});
			</c:if>
			$('.btn-ppv').popover();

			$("#cbcopy").click((e) => {
				e.stopPropagation();
				navigator.clipboard.writeText(errorMsgs);
			});

			$(".grup-regla input, .grup-regla select").change( (event) => {
				updateGrupsRegles();
			});
			$(".camp-regla input, .camp-regla select").change( function() {
				updateCampsRegles();
			});
			updateCampsRegles();

			<c:if test="${not empty fitxerAmbErrors}">loadFitxerErrors();</c:if>
		});

		const loadFitxerErrors = () => {
			$.ajax({
				type: 'GET',
				url: fitxerErrorUrl,
				responseType: 'arraybuffer',
				success: function(json) {
					if (json.error) {
						console.log("Error al descarregar el fitxer de consultes amb els errors: " + json.errorMsg);
					} else {
						const response = json.data;
						const blob = base64toBlob(response.contingut, response.contentType);
						const file = new File([blob], response.contentType, {type: response.contentType});
						const url = URL.createObjectURL(file);

						// Create a new anchor element
						const a = document.createElement('a');
						a.href = url;
						a.download = response.nom || 'download';

						// Click handler that releases the object URL after the element has been clicked
						const clickHandler = () => {
							setTimeout(() => {
								URL.revokeObjectURL(url);
								removeEventListener('click', clickHandler);
							}, 150);
						};

						// Add the click event listener on the anchor element
						a.addEventListener('click', clickHandler, false);

						// Programmatically trigger a click on the anchor element
						// Useful if you want the download to happen automatically without attaching the anchor element to the DOM
						a.click();
					}
				},
				error: function(xhr, ajaxOptions, thrownError) {
					console.log("Error al descarregar el fitxer de consultes amb els errors");
				}
			});
		}

		const base64toBlob = (b64Data, contentType) => {
			var contentType = contentType || '';
			var sliceSize = 512;
			var byteCharacters = atob(b64Data);
			var byteArrays = [];
			for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
				var slice = byteCharacters.slice(offset, offset + sliceSize);
				var byteNumbers = new Array(slice.length);
				for (var i=0; i<slice.length; i++) {
					byteNumbers[i] = slice.charCodeAt(i);
				}
				var byteArray = new Uint8Array(byteNumbers);
				byteArrays.push(byteArray);
			}
			var blob = new Blob(byteArrays, {type: contentType});
			return blob;
		}

		const campsModificats = () => {
			let campsModiicats = $("#dades-especifiques-grup").find("input:not([type=hidden]), select").filter(function () {
				if ($(this).is(':checkbox')) {
					return $(this).is(":checked");
				}
				return $.trim($(this).val()).length > 0
			}).map(function() {
				return $(this).closest(".form-group").data('path')
			}).get();
			return campsModiicats;
		}
		const grupModificats = () => {
			let grupsModificats = $("#dades-especifiques-grup").find("input:not([type=hidden]), select").filter(function () {
				if ($(this).is(':checkbox')) {
					return $(this).is(":checked");
				}
				return $.trim($(this).val()).length > 0
			}).map(function() {
				return $(this).parents(".grup-regla").get()
			}).map(function() {
				return $(this).data('nom')
			}).get();
			return grupsModificats;
		}

		const updateCampsRegles = () => {
			const params = campsModificats();
			$.ajax({
				type: "post",
				dataType: 'json',
				data: {campsModificats: params},
				url: '<c:url value="/consulta/${servei.codiUrlEncoded}/camps/regles"/>',
				async: false,
				success: (response) => {
					updateCamps(response);
				}
			});
		}
		const updateCamps = (campsRegles) => {
			console.log("Regles de camps: ", campsRegles);
			if (campsRegles) {
				campsRegles.forEach( function(campRegla) {
					if ($("#camp_" + campRegla.varId).length) {
						let camp = $("#camp_" + campRegla.varId);
						const grup = camp.closest(".fs-grup");
						const subgrup = camp.closest(".fs-subgrup");
						const editable = grup.length ? (grup.hasClass('editable') ? (subgrup.length ? subgrup.hasClass('editable') : true) : false) : true;
						camp.prop("disabled", !(editable && campRegla.editable));
						camp.toggleClass("bloquejat", !campRegla.editable);
						camp.toggleClass("ocult", !campRegla.visible);
						// Netejar camp
						if (!campRegla.editable || !campRegla.visible) {
							clearCamp(camp.attr('id'));
						}
						let etiqueta = camp.closest(".form-group").find('label');
						let etiquetaText = etiqueta.text().trim();
						if (etiquetaText.endsWith("*"))
							etiquetaText = etiquetaText.substring(0, etiquetaText.length - 2);
						if (campRegla.obligatori)
							etiquetaText = etiquetaText + " *";
						etiqueta.text(etiquetaText);
					}
				});
			}
		}
		const clearCamp = (campId) => {
			let camp = $("#" + campId);
			if (camp.is(':checkbox')) {
				camp.prop("checked", false);
			} else {
				camp.val('');
			}
		}
		const updateGrupsRegles = () => {
			const params = grupModificats();
			$.ajax({
				type: "post",
				dataType: 'json',
				data: {grupsModificats: params},
				url: '<c:url value="/consulta/${servei.codiUrlEncoded}/grups/regles"/>',
				async: false,
				success: (response) => {
					updateGrups(response);
				}
			});
		}
		const updateGrups = (grupsRegles) => {
			console.log("Regles de grups: ", grupsRegles);
			if (grupsRegles) {
				grupsRegles.forEach( function(grupRegla) {
					if ($("#grup_" + grupRegla.varId).length) {
						let grup = $("#grup_" + grupRegla.varId);
						const isGrup = grup.is("fieldset");

						// Editable
						grup.toggleClass("editable", grupRegla.editable);
						$(grup).find("input, select").each((index, element) => {
							$(element).prop("disabled", (!grupRegla.editable || $(element).hasClass('bloquejat')))
						});

						// Visible
						grup.toggleClass("ocult", !grupRegla.visible);

						// Netejar camps
						if (!grupRegla.editable || !grupRegla.visible) {
							clearGrup(grup.attr('id'));
						}

						// Obligatori
						let etiqueta = isGrup ? grup.find(".fs-grup-nom") : grup.find(".panel-title");
						let etiquetaText = etiqueta.text().trim();
						if (etiquetaText.endsWith("*"))
							etiquetaText = etiquetaText.substring(0, etiquetaText.size() - 2);
						if (grupRegla.obligatori)
							etiquetaText = etiquetaText + " *";
						etiqueta.text(etiquetaText);
					}
				});
			}
		}
		const clearGrup = (grupId) => {
			$("#" + grupId).find("input, select").each((index, element) => {clearCamp($(element).attr('id'))})
		}
	</script>
	<style>
		legend {font-weight: bold;}
		.btn-ppv {border-radius: 16px; padding: 2px 7px 0px 7px; margin-left: 8px; position: relative; font-size: 10px;}
		.popover {max-width: 1000px !important;}
		.popover-content {min-width: 600px !important;}
		.fs-grup {margin-top: 20px;}
		.fs-grup .fs-grup-nom {color: darkorange;}
		.fs-grup .btn-ppv {top: 4px;}
		.fs-grup legend {font-weight: normal;}
		.fs-subgrup {margin-top: 15px;}
		.fs-subgrup .panel-title {font-weight: bold;}
		.fs-subgrup .btn-ppv {top: -18px;}
	</style>
</head>
<body>
	<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/Excel" var="downloadPlantillaExcelUrl"/>
	<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/CSV" var="downloadPlantillaCsvUrl"/>
	<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/ODS" var="downloadPlantillaOdsUrl"/>
	<c:url value="/consulta/${servei.codiUrlEncoded}/new" var="formAction"/>
	<div class="container-fluid">
	<form:form id="consultaForm" action="${formAction}" method="post" cssClass="" commandName="consultaCommand" enctype="multipart/form-data">
		<form:hidden path="serveiCodi"/>
		<form:hidden path="multiple" />
		<br/>
		<c:set var="campPath" value="procedimentId"/>
		<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
		<div class="row">
			<div class="col-md-6">
				<pbl:inputSelect
						name="${campPath}"
						inline="true"
						optionItems="${procediments}"
						optionValueAttribute="id"
						optionTextAttribute="nom"
						emptyOption="false"/>
			</div>
		</div>
		<fieldset>
			<legend><spring:message code="consulta.form.dades.generiques"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nom"/> *</label>
					<pbl:inputText name="funcionariNom" inline="true"/>

					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.nom"/></label>
					<pbl:inputText name="entitatNom" inline="true" disabled="true"/>

					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.consentiment"/> *</label>
					<pbl:inputSelect name="consentiment" inline="true" optionItems="${consentimentValors}" emptyOption="false"/>
				</div>
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.funcionari.nif"/></label>
					<pbl:inputText name="funcionariNif" inline="true"/>

					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.entitat.cif"/></label>
					<pbl:inputText name="entitatCif" inline="true"/>

					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.departament"/> *</label>
					<pbl:inputText name="departamentNom" inline="true"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<c:set var="campPath" value="finalitat"/>
					<pbl:inputTextarea name="${campPath}" required="true" inline="true" textKey="consulta.form.camp.finalitat"/>
				</div>
			</div>
		</fieldset>
		<c:choose>
		<c:when test="${serveiMultiple && serveiSimple}">
			<ul id="tabs-simple-multiple" class="nav nav-tabs">
				<li<c:if test="${tabSimpleActiu}"> class="active"</c:if>><a href="#tab-simple" data-toggle="tab"><spring:message code="consulta.form.tipus.simple"/></a></li>
				<li<c:if test="${tabMultipleActiu}"> class="active"</c:if>><a href="#tab-multiple" data-toggle="tab"><spring:message code="consulta.form.tipus.multiple"/></a></li>
			</ul>
			<div class="tab-content" style="margin-top: 15px;">
				<div class="tab-pane<c:if test="${tabSimpleActiu}"> active</c:if>" id="tab-simple">
					<jsp:include page="import/consultaSimpleForm.jsp"/>
				</div>
				<div class="tab-pane<c:if test="${tabMultipleActiu}"> active</c:if>" id="tab-multiple">
					<jsp:include page="import/consultaMultipleForm.jsp"/>
				</div>
			</div>
		</c:when>
		<c:when test="${serveiMultiple && not serveiSimple}">
			<jsp:include page="import/consultaMultipleForm.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="import/consultaSimpleForm.jsp"/>
		</c:otherwise>
		</c:choose>
		<div class="pull-right">
			<c:if test="${not empty servei.ajuda or not empty servei.fitxerAjudaNom}">
				<button type="button" class="btn btn-default" data-toggle="modal" data-target="#modalAjuda"><spring:message code="comu.boto.ajuda"/></button>
			</c:if>
			<a href="<c:url value="/consulta"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
			<button type="submit" class="btn btn-primary"><c:choose><c:when test="${reintentar}"><spring:message code="comu.boto.reintentar"/></c:when><c:otherwise><spring:message code="comu.boto.enviar"/></c:otherwise></c:choose></button>
		</div>
	</form:form>
	</div>
	<!-- Modal ajuda-->
	<div id="modalAjuda" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
					<h3 id="myModalLabel"><spring:message code="comu.boto.ajuda"/></h3>
				</div>
				<div class="modal-body">${servei.ajuda}</div>
				<div class="modal-footer">
					<c:if test="${not empty servei.fitxerAjudaNom}">
						<a href="<c:url value='/consulta/${servei.codiUrlEncoded}/downloadAjuda'/>" class="btn btn-primary pull-left"><i class="fas fa-file-download"></i> <spring:message code="comu.boto.document.ajuda"/></a>
					</c:if>
					<button class="btn btn-default" data-dismiss="modal" aria-hidden="true"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>
	<!-- Fi modal -->
</body>
</html>
