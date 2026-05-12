<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty procedimentCommand.id}"><spring:message code="procediment.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="procediment.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery/"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script>
	var organsGestorsActiu = [];
	<c:forEach items="${organsGestors}" var="organGestor">
	organsGestorsActiu["${organGestor.id}"] = ${organGestor.actiu};
	</c:forEach>
	function formatState(organ) {
		if (!organ.id) {
			return organ.text;
		} else {
			return organsGestorsActiu[organ.id] ? organ.text : $('<span title="<spring:message code="organgestor.list.extingit"/>">' + organ.text + ' <span class="fa fa-exclamation-triangle text-danger"></span></span>');
		}
	}

	const nouProcediment = ${empty procedimentCommand.id};
	<%--const DIALOG_CONFIG = {--%>
	<%--	title: "Permisos",--%>
	<%--	modal: true,--%>
	<%--	text: "<spring:message code='procediment.form.clonar.permisos.origen'/>"--%>
	<%--};--%>

	$(document).ready(function() {
		// Selecció dels elements pels noms dels "selects"
		const select1 = $('#codiSiaOrigen');
		const select2 = $('#codiSiaFills');

		select2options = {
			theme: "bootstrap",
			minimumResultsForSearch: 0
		};

		// Funció per deshabilitar/enhabilitar els selects basant-se en el valor de l'altre
		function toggleSelects() {
			if (select1.val()) {
				select2.prop("disabled", true).select2(select2options); // Si select1 té valor, deshabilita select2
			} else if (select2.val() && select2.val().length > 0) {
				select1.prop("disabled", true).select2(select2options); // Si select2 té valor, deshabilita select1
			} else {
				// Cap dels selects té valor, per tant, tots dos estan habilitats
				select1.prop("disabled", false).select2(select2options);
				select2.prop("disabled", false).select2(select2options);
			}
		}

		// Definir l'estat inicial en carregar la pàgina
		toggleSelects();

		// Detectar canvis al primer select
		select1.on("change", function() {
			toggleSelects();
		});

		// Detectar canvis al segon select
		select2.on("change", function() {
			toggleSelects();
		});

		$('#procedimentCommand').on('submit', function(e) {
			e.preventDefault();
			const $form = $(this);
			const codiOrigen = $('#codiSiaOrigen').val();

			function submitFormWithPermissions(clonarPermisos) {
				$('#clonarPermisosOrigen').val(clonarPermisos);
				$form.unbind('submit').submit();
			}

			if (!codiOrigen || !nouProcediment) {
				submitFormWithPermissions(false);
				return;
			}

			if (confirm("<spring:message code='procediment.form.clonar.permisos.origen'/>")) {
				submitFormWithPermissions(true);
			} else {
				submitFormWithPermissions(false);
			}
			// $("<div>").dialog({
			// 	...DIALOG_CONFIG,
			// 	buttons: {
			// 		"Sí": function() {
			// 			submitFormWithPermissions(true);
			// 			$(this).dialog("close");
			// 		},
			// 		"No": function() {
			// 			submitFormWithPermissions(false);
			// 			$(this).dialog("close");
			// 		}
			// 	}
			// });
		})
	});

</script>
</head>
<body>
	<c:url value="/modal/procediment/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand">
		<form:hidden path="clonarPermisosOrigen"/>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<div class="row">
			<div class="col-md-12">
				<pbl:inputText name="codi" required="true" labelSize="3" inline="false" textKey="procediment.form.camp.codi"/>
				<pbl:inputText name="nom" required="true" labelSize="3" inline="false" textKey="procediment.form.camp.nom"/>
				<pbl:inputText name="departament" labelSize="3" inline="false" textKey="procediment.form.camp.departament"/>
				<pbl:inputSelect
					name="organGestorId"
					textKey="procediment.form.camp.organgestor"
					labelSize="3"
					inline="false" 
					emptyOption="true"
					emptyOptionTextKey="organgestor.form.camp.organ.opcio.cap"
					optionItems="${ organsGestors }"
					optionValueAttribute="id"
					optionTextAttribute="codiINom"
					required="true"
					formatResult="formatState"
					optionMinimumResultsForSearch="2"/>
				<pbl:inputText name="codiSia" labelSize="3" inline="false" textKey="procediment.form.camp.codisia"/>
				<%--c:set var="campPath" value="valorCampAutomatizado"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="form-group">
					<label class="control-label col-md-1" for="${campPath}"></label>
					<div class="col-md-11">
						<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="procediment.form.camp.actiuCampAuto"/>
						<form:errors path="${campPath}" cssClass="help-block"/>
					</div>
				</div--%>
				<pbl:inputSelect
					name="valorCampAutomatizado"
					textKey="procediment.form.camp.actiuCampAuto" 
					emptyOption="true"
					emptyOptionTextKey="procediment.form.camp.actiuCampAuto.buit"
					optionItems="${ procedimentAutomatizadoOptions }"
					optionValueAttribute="value"
					optionTextKeyAttribute="text"
					required="false"
					labelSize="3"/>
				<pbl:inputSelect name="valorCampClaseTramite" textKey="procediment.form.camp.claseTramite" 
								emptyOption="true" emptyOptionTextKey="procediment.form.camp.claseTramite.buit"
								optionItems="${ procedimentClaseTramiteOptions }" optionValueAttribute="value" optionTextKeyAttribute="text"
								required="false" labelSize="3" />

				<hr/>
				<pbl:inputSelect
						name="codiSiaOrigen"
						labelSize="3"
						textKey="procediment.form.camp.codisia.origen"
						emptyOption="true"
						emptyOptionTextKey="procediment.form.camp.actiuCampAuto.buit"
						optionItems="${procedimentsOrigen}"
						optionTextAttribute="valor"
						optionValueAttribute="codi"
						optionMinimumResultsForSearch="0" />
				<pbl:inputSelect
						name="codiSiaFills"
						labelSize="3"
						multiple="true"
						textKey="procediment.form.camp.codisia.fills"
						emptyOption="true"
						emptyOptionTextKey="procediment.form.camp.actiuCampAuto.buit"
						optionItems="${procedimentsFills}"
						optionTextAttribute="valor"
						optionValueAttribute="codi"
						optionMinimumResultsForSearch="0" />
				<div id="modal-botons">
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
					<a href="<c:url value="/procediment"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>
