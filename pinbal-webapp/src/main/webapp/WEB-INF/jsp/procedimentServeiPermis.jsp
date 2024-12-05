<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>


<html>
<head>
	<title><spring:message code="procediment.serveis.permisos.permis.nou"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>

<c:url value="/usuariajax/entitat/${entitatId}/usuari" var="urlConsultaUsuariEntitat"/>
<c:url value="/usuariajax/entitat/${entitatId}/usuaris" var="urlConsultaUsuarisEntitat"/>
<c:set var="formAction"><c:url value="/modal/avis/save"/></c:set>
<c:url var="formAction" value="/modal/procediment/${procedimentId}/servei/${serveiCodi}/permis/save"/>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariPermisiCommand" role="form">
	<form:hidden path="usuariNom"/>
	<pbl:inputSuggest
			name="usuariCodi"
			urlConsultaInicial="${urlConsultaUsuariEntitat}"
			urlConsultaLlistat="${urlConsultaUsuarisEntitat}"
			textKey="admin.consulta.list.filtre.usuari"
			suggestValue="codi"
			suggestText="usuariCodiNomNif"
			inline="false"
			labelSize="2"/>
	<div style="height: 160px;"></div>

	<div id="modal-botons">
		<button type="submit" class="btn btn-primary"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
		<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</form:form>

<script>
	function getUserData(codi) {
		$.ajax({
			url: '${urlConsultaUsuariEntitat}/' + codi,
			type: 'GET',
			success: function (resposta) {
				$('#usuariNom').val(resposta.nom);
			},
			error: function (error) {
				console.error(error);
			}
		});
	}

	$(document).ready(function() {

		$('#usuariCodi').change(function() {
			const codi = $(this).val();
			if (codi) {
				getUserData(codi);
			} else {
				$('#nom').val();
			}
		});
		<%--if (${not empty usuariPermisiCommand.usuariCodi}) {--%>
		<%--	getUserData('${usuariPermisiCommand.usuariCodi}');--%>
		<%--}--%>
	});
</script>
</body>
</html>
