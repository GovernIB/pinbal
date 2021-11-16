<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<title><spring:message code="informe.list.titol"/></title>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12 well">
				<h4>
				<spring:message code="informe.list.informe.procediments"/>
				<a href="informe/procediments" class="btn btn-default pull-right"><i class="fas fa-file-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
				</h4>
			</div>
		</div>
		<div class="row">
			<div class="well">
					<h4>
						<spring:message code="informe.list.informe.usuaris"/>
						<a href="informe/usuaris" class="btn btn-default pull-right"><i class="fas fa-file-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
					</h4>
			</div>
		</div>
		<div class="row">
			<div class="well">
				<h4>
						<spring:message code="informe.list.informe.serveis"/>
						<a href="informe/serveis" class="btn btn-default pull-right"><i class="fas fa-file-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
				</h4>
			</div>
		</div>
		<div class="row">
			<div class="well">
				<h4>
					<spring:message code="informe.list.informe.generalEstat"/>
					<button onclick="showModalFiltrar()" class="btn btn-default pull-right">
						<i class="fas fa-file-download"></i>&nbsp;<spring:message code="informe.list.generar"/>
					</button>
				</h4>
			</div>
		</div>
		<div class="row">
			<div class="well">
				<h4>
						<spring:message code="informe.list.informe.serveis"/>
						<a href="informe/procedimentServei" class="btn btn-default pull-right"><i class="fas fa-file-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
				</h4>
			</div>
		</div>
	</div>
	<div id="modal-filtre-dates" class="modal fade">
	  <div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3 style="margin: 0px;"><spring:message code="informe.general.estat.filtre.dates.titol"/></h3>
			</div>
			<div class="modal-body">
				<c:url value="informe/generalEstat" var="formAction"/>
				<form id="modal-form-filtre" action="${formAction}" method="GET">
					<div class="input-group date" data-provide="datepicker" style="margin-bottom: 20px;">
						<spring:message var="placeholderDataInici" code="informe.general.estat.filtre.dates.inici"/>
					    <input class="form-control" type="text" name="dataInici" id="dataInici" placeholder="${placeholderDataInici}">
					    <div class="input-group-addon">
					        <span class="glyphicon glyphicon-th"></span>
					    </div>
					</div>
					<div class="input-group date" data-provide="datepicker">
						<spring:message var="placeholderDataFi" code="informe.general.estat.filtre.dates.fi"/>
					    <input class="form-control" type="text" name="dataFi" id="dataFi" placeholder="${placeholderDataFi}">
					    <div class="input-group-addon">
					        <span class="glyphicon glyphicon-th"></span>
					    </div>
					</div>
				</form>
			</div>	
			
			<div class="modal-footer">
				<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></button>
				<button class="btn btn-primary" onclick="$('#modal-form-filtre').submit()"><spring:message code="comu.boto.generar"/></button>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript">
$(document).ready(function() {
	$.fn.datepicker.defaults.format = 'dd/mm/yyyy';
	$('.datepicker').datepicker();
	
});
function showModalFiltrar() {
	$('#modal-filtre-dates').modal();
}
</script>
</body>
</html>
