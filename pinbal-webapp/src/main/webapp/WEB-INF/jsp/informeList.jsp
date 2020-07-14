<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<title><spring:message code="informe.list.titol"/></title>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
</head>
<body>

	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.procediments"/>
			<a href="informe/procediments" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>
	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.usuaris"/>
			<a href="informe/usuaris" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>
	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.serveis"/>
			<a href="informe/serveis" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>
	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.generalEstat"/>
			<a href="#modal-form-filtre" onclick="showModalFiltrar()" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>

	<div id="modal-filtre-dates" class="modal fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="informe.general.estat.filtre.dates.titol"/></h3>
		</div>
		<div class="modal-body">
			<c:url value="informe/generalEstat" var="formAction"/>
			<form id="modal-form-filtre" action="${formAction}" method="GET" class="form-horizontal">
				<div class="control-group">
					<spring:message var="placeholderDataInici" code="informe.general.estat.filtre.dates.inici"/>
					<label class="control-label" for="dataInici">${placeholderDataInici}</label>
					<div class="controls">
						<input class="dynamic-url" type="text" name="dataInici" id="dataInici" placeholder="${placeholderDataInici}"/>
						<script>$("#dataInici").mask("99/99/9999");</script>
					</div>
				</div>
				<div class="control-group">
					<spring:message var="placeholderDataFi" code="informe.general.estat.filtre.dates.fi"/>
					<label class="control-label" for="dataFi">${placeholderDataFi}</label>
					<div class="controls">
						<input class="dynamic-url" type="text" name="dataFi" id="dataFi" placeholder="${placeholderDataFi}"/>
						<script>$("#dataFi").mask("99/99/9999");</script>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" class="btn btn-primary" onclick="$('#modal-form-filtre').submit()"><spring:message code="comu.boto.generar"/></a>
		</div>
	</div>

<script type="text/javascript">
function showModalFiltrar() {
	$('#modal-filtre-dates').modal();
}
</script>
</body>
</html>
