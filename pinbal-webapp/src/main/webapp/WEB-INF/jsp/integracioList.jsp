<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%	request.setAttribute("integracioAccioEstats", es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto.sortedValues());
	request.setAttribute("integracioAccioTipus", es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto.sortedValues());
%>

<html>
<head>
	<title><spring:message code="integracio.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script>
$(document).ready(function() {
	
	  $('#btnDelete').click(function() {
	    	$('#btnDelete').addClass('disabled');
	    	$('#trash-btn-esborrar').hide();
	    	$('#spin-btn-esborrar').show();
	    	esborrarEntrades();
	    });
	
    $('#table-entitats').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/integracio/datatable/"/>',
		columnDefs: [
			{ 
	            targets: [0, 1],
				orderable: false,
				visible: false
	        },
			{
				targets: [2],
				orderable: false,
				render: $.fn.dataTable.render.moment('x', 'DD/MM/YYYY HH:mm:ss', 'es' )
			},
			{ 
	            targets: [3, 4, 5],
				orderable: false
	        },
			{
				targets: [6],
				orderable: false,
				render: function (data, type, row, meta) {
						var template = $('#template-temps-resposta').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [7],
				orderable: false,
				visible: false
				
			},
			{
				targets: [8],
				orderable: false,
				render: function (data, type, row, meta) {
						var template = $('#template-estat').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [9],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-accions').html();
						return Mustache.render(template, row);
				}
			}
	   ]
	});
	
});

function esborrarEntrades() {
	webutilClearMissatges();
	$.ajax({
		url: "<c:url value='/integracio'/>/${codiActual.codi}/esborrar"
	}).done(function(){
		refrescarInformacio()
    	$('#spin-btn-esborrar').hide();
    	$('#trash-btn-esborrar').show();
    	$('#btnDelete').removeClass('disabled');
	});
}

function refrescarInformacio() {	
		// Refresca la taula
		$('#table-entitats').dataTable().fnDraw();
}

function formatarEstat(estat) {

	const msgOk= '<spring:message code="integracio.list.estat.Ok"/>';
	const msgError= '<spring:message code="integracio.list.estat.Error"/>';
	
	if (estat.id=='OK') {
		return $('<div><span class="fa fa-check"></span> <span>' + msgOk + '</span></div>');
	} else if(estat.id=='ERROR') {
		return $('<div><span class="fas fa-exclamation-triangle"></span> <span>' + msgError + '</span></div>');	
	} else {
		return estat.text;
	}
}

function formatarTipus(tipus) {

	const msgEnviament= '<spring:message code="integracio.list.tipus.Enviament"/>';
	const msgRecepcio = '<spring:message code="integracio.list.tipus.Recepcio"/>';
	
	if (tipus.id=='ENVIAMENT') {
		return $('<div><span>' + msgEnviament + '</span></div>');
	} else if(tipus.id=='RECEPCIO') {
		return $('<div><span>' + msgRecepcio + '</span></div>');	
	} else {
		return tipus.text;
	}
}

</script>
</head>
<body>
	
	<c:set var="formAction"><c:url value='/integracio'></c:url></c:set>
	<form:form action="${formAction}/${integracioFiltreCommand.codi}" method="post" cssClass="well" modelAttribute="integracioFiltreCommand">
		
		<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
		
		<div class="row">
			<div class="col-md-3">
				<pbl:inputDate name="data" inline="true" placeholderKey="integracio.list.filtre.data"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="descripcio" inline="true" placeholderKey="integracio.list.filtre.descripcio"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="idPeticio" inline="true" placeholderKey="integracio.list.filtre.peticio"/>
			</div> 			
			<div class="col-md-3">				
				<pbl:inputSelect
					name="tipus"
					inline="true"
					placeholderKey="integracio.list.filtre.tipus"
					optionItems="${integracioAccioTipus}"
					emptyOption="true"
					formatResult="formatarTipus"
					formatSelection="formatarTipus"/>				
			</div>
			<div class="col-md-3">				
				<pbl:inputSelect
					name="estat"
					inline="true"
					placeholderKey="integracio.list.filtre.estat"
					optionItems="${integracioAccioEstats}"
					emptyOption="true"
					formatResult="formatarEstat"
					formatSelection="formatarEstat"/>				
			</div>
			<div class="col-md-4 pull-right">
				<div class="pull-right">					
					<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>	
		</div>
	</form:form>
	
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">
			<li<c:if test="${integracio.codi == codiActual}"> class="active"</c:if>>
				<a href="<c:url value="/integracio/${integracio.codi}"/>"><spring:message code="${integracio.nom}"/>
					<c:if test="${integracio.numErrors > 0}">
						<span id="bustia-pendent-count" class="badge small" style="background-color: #d9534f;">${integracio.numErrors}</span>
					</c:if>
				</a>
			</li>
		</c:forEach>
	</ul>
	<br/>

	<table 
		id="table-entitats"
		data-toggle="datatable"
		data-url="<c:url value="/integracio/datatable"/>" 
		data-search-enabled="false"
		data-info-type="search"
		data-default-order="2" 
		data-default-dir="desc"
		class="table table-striped table-bordered"
		style="width: 100%">
		<thead>
			<tr>
				<th data-data="excepcioMessage"></th>
				<th data-data="excepcioStacktrace"></th>
				<th data-data="data"><spring:message code="integracio.list.columna.data"/></th>
				<th data-data="descripcio"><spring:message code="integracio.list.columna.descripcio"/></th>
				<th data-data="tipus"><spring:message code="integracio.list.columna.tipus"/></th>
				<th data-data="idPeticio"><spring:message code="integracio.list.columna.peticio.id"/></th>
				<th data-data="tempsResposta"><spring:message code="integracio.list.columna.temps.resposta"/></th>
				<th data-data="estatOk"></th>
				<th data-data="estat"><spring:message code="integracio.list.columna.estat"/></th>
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>
	
	<table style="margin-top: 25px; margin-bottom: 20px; margin-right: 10px; width:100%;">
		<tr>
			<td>
				<button id="btnDelete" type="button" class="btn btn-danger pull-left"><span id="trash-btn-esborrar" class="fa fa-trash-o"></span><span id="spin-btn-esborrar" class="fa fa-cog fa-spin" style="display:none;"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></button>
			</td>
<!-- 			<td> -->
<%-- 				<button id="btnRefresh" type="button" class="btn btn-info pull-right"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="comu.boto.refrescar"/></button> --%>
<!-- 			</td> -->
		</tr>
	</table>


<script id="template-temps-resposta" type="x-tmpl-mustache">
	{{tempsResposta}} ms
</script>

<script id="template-estat" type="x-tmpl-mustache">
	{{#estatOk}}
		<span class="label label-success"><span class="fa fa-check"></span>&nbsp;{{estat}}</span>
	{{/estatOk}}
	{{^estatOk}}
		<span class="label label-danger"><span class="fa fa-exclamation-triangle"></span>&nbsp;{{estat}}</span>
	{{/estatOk}}
</script>

<script id="template-accions" type="x-tmpl-mustache">
	<a href="<c:url value="/modal/integracio/${codiActual}/{{id}}"/>" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
</script>



</body>
</html>
