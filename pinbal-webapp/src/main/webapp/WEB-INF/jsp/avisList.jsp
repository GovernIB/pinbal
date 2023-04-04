<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="avis.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>


	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	
<script>
$(document).ready(function() {


	$('#table-avisos').on('draw.dt', function () {
		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="avis.list.confirmacio.esborrar"/>");
		});
	});


    $('#table-avisos').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		language: {
            "url": "js/datatable-language.json"
        },
		ajax: "avis/datatable",
		columnDefs: [
			{
				targets: 0
			},
			{
				targets: [1, 2],
				width: "10%",
				render: $.fn.dataTable.render.moment('DD/MM/YYYY')
			},	
			{
				targets: [3],
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-activa').html();
						return Mustache.render(template, row);
				}
			},
			{ 
	            targets: [4, 5, 6],
				orderable: false,
				visible: false
	        },			
			{
				targets: [7],
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-nivell').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [8],
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
	
</script>	
</head>
<body>
	<div class="pull-right">
		<a class="btn btn-default" href="<c:url value="/avis/new"/>" data-toggle="modal" data-refresh-pagina="true"><i class="fa fa-plus"></i>&nbsp;<spring:message code="avis.list.boto.nova.avis"/></a>
	</div>
	<table id="table-avisos" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-data="assumpte"><spring:message code="avis.list.columna.assumpte"/></th>
				<th data-data="dataInici"><spring:message code="avis.list.columna.dataInici"/></th>
				<th data-data="dataFinal"><spring:message code="avis.list.columna.dataFinal"/></th>
				<th data-data="actiu"><spring:message code="avis.list.columna.activa"/></th>
				<th data-data="info"></th>
				<th data-data="warning"></th>
				<th data-data="error"></th>
				<th data-data="avisNivell"><spring:message code="avis.list.columna.avisNivell"/></th>			
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>
	
	
	
<script id="template-activa" type="x-tmpl-mustache">
{{#actiu}}
<span class="fa fa-check"></span>
{{/actiu}}
</script>
<script id="template-nivell" type="x-tmpl-mustache">
{{#info}}
<spring:message code="avis.nivell.enum.INFO"/>
{{/info}}
{{#warning}}
<spring:message code="avis.nivell.enum.WARNING"/>
{{/warning}}
{{#error}}
<spring:message code="avis.nivell.enum.ERROR"/>
{{/error}}
</script>
	
<script id="template-accions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu">
			<li>
				<a href="avis/{{ id }}" data-toggle="modal" data-refresh-pagina="true"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
			</li>
			<li>
				{{#actiu}}
				<a href="avis/{{ id }}/disable"><i class="fas fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
				{{/actiu}}
				{{^actiu}}
				<a href="avis/{{ id }}/enable"><i class="fas fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
				{{/actiu}}
			</li>
			<li><a href="avis/{{ id }}/delete" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
		</ul>
	</div>
</script>	
	
	
</body>