<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>


<html>
<head>
	<title><spring:message code="procediment.serveis.nou"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>

<c:url value="/procediment/servei" var="urlConsultaProcedimentServei"/>
<c:url value="/procediment/serveis" var="urlConsultaProcedimentServeis"/>
<c:set var="formAction"><c:url value="/modal/avis/save"/></c:set>

<!--  TODO per desar -->
<c:url var="formAction" value="/modal/procediment/${procedimentId}/servei/save"/>
<!--  TODO per desar -->
<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentServeiCommand" role="form">
<%-- 	<form:hidden path="serveiCodi"/> --%>
	<pbl:inputSuggest
			name="serveiCodi"
			urlConsultaInicial="${urlConsultaProcedimentServei}"
			urlConsultaLlistat="${urlConsultaProcedimentServeis}"
			textKey="admin.consulta.list.filtre.servei"
			suggestValue="codi"
			suggestText="codiNom"
			inline="false"
			labelSize="2"/>
	<div style="height: 160px;"></div>

	<div id="modal-botons">
		<button type="submit" class="btn btn-primary"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
		<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</form:form>

<script>
	///
	function getServeiData(codi) {
		$.ajax({
			url: '${urlConsultaProcedimentServei}/' + codi,
			type: 'GET',
			success: function (resposta) {
				console.log("He rebut del servidor: " + resposta.descripcio);
				$('#serveiNom').val(resposta.descripcio);
			},
			error: function (error) {
				console.error(error);
			}
		});
	}

	$(document).ready(function() {

		$('#serveiCodi').change(function() {
			console.log("Valor canviat a: " + $(this).val());
			const codi = $(this).val();
			if (codi) {
				getServeiData(codi);
			} else {
				$('#nom').val();
			}
		});	
	});
</script>
</body>
</html>
