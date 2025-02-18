<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<html>
<head>
	<title><spring:message code="cache.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	
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
<script>
	var messages = {
		"cache.desc.serveiDescripcio": "<spring:message code='cache.desc.serveiDescripcio'/>",
		"cache.desc.emisorNombre": "<spring:message code='cache.desc.emisorNombre'/>",
		"cache.desc.clavePrivadaNombre": "<spring:message code='cache.desc.clavePrivadaNombre'/>",
		"cache.desc.clavePrivadaNumeroSerie": "<spring:message code='cache.desc.clavePrivadaNumeroSerie'/>",
		"cache.desc.clavePublicaNombre": "<spring:message code='cache.desc.clavePublicaNombre'/>",
		"cache.desc.clavePublicaNumeroSerie": "<spring:message code='cache.desc.clavePublicaNumeroSerie'/>",
		"cache.desc.procediments": "<spring:message code='cache.desc.procediments'/>",
		"cache.desc.serveis": "<spring:message code='cache.desc.serveis'/>",
		"cache.desc.serveisEntitat": "<spring:message code='cache.desc.serveisEntitat'/>",
		"cache.desc.serveisProcediment": "<spring:message code='cache.desc.serveisProcediment'/>",
		"cache.desc.dadesEspecifiques": "<spring:message code='cache.desc.dadesEspecifiques'/>",
		"cache.desc.enumerats": "<spring:message code='cache.desc.enumerats'/>",
		"cache.desc.paisos": "<spring:message code='cache.desc.paisos'/>",
		"cache.desc.provincies": "<spring:message code='cache.desc.provincies'/>",
		"cache.desc.municipis": "<spring:message code='cache.desc.municipis'/>",
		"cache.desc.usuariAmbCodi": "<spring:message code='cache.desc.usuariAmbCodi'/>",
		"cache.desc.usuariAmbNif": "<spring:message code='cache.desc.usuariAmbNif'/>"
	};

	$(document).ready(function() {
		$('#table-caches').DataTable({
			autoWidth: false,
			paging: false,
			processing: true,
			serverSide: true,
			order: [],
			columns: [
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false } ],
			columnDefs: [
				{
					targets: [0],
					width: '20%',
				}, {
					targets: [1],
					render: function (data, type, row, meta) {
						var template = $('#template-desc').html();
						// var renderedKey = 'cache.desc.' + row['codi'];
						// row['desc'] = messages[renderedKey] || renderedKey;
						var renderedKey = Mustache.render(template, row);
						return messages[renderedKey] || renderedKey;
						return Mustache.render(template, row);
					}
				}, {
					targets: [2],
					width: '10%',
				}, {
					targets: [3],
					width: '2%',
					render: function (data, type, row, meta) {
						var template = $('#template-accions').html();
						return Mustache.render(template, row);
					}
				}
			],
			language: {
				"url": '<c:url value="/js/datatable-language.json"/>',
			},
			ajax: '<c:url value="/cache/datatable"/>',
			drawCallback: function(settings) {
				$(document).webutilTogglesEval();
			}

		});
	});
</script>
</head>
<body>
	<div class="pull-right">
		<a class="btn btn-primary" href="<c:url value="/cache/all/buidar"/>" data-toggle="ajax" data-confirm="<spring:message code="cache.list.all.confirmacio.esborrar"/>"><i class="fa fa-plus"></i>&nbsp;<spring:message code="cache.all.boto.esborrar"/></a>
	</div>
	<table id="table-caches" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="codi"><spring:message code="cache.list.columna.codi" /></th>
				<th data-data="codi"><spring:message code="cache.list.columna.descripcio" /></th>
				<th data-data="localHeapSize"><spring:message code="cache.list.columna.mida" /></th>
				<th data-data="codi"></th>
			</tr>
		</thead>
	</table>

	<script id="template-desc" type="x-tmpl-mustache">cache.desc.{{codi}}</script>
	<script id="template-accions" type="x-tmpl-mustache">
		<a href="<c:url value="/cache/{{codi}}/buidar"/>" class="btn btn-warning" data-toggle="ajax" data-corfirm="<spring:message code="cache.list.confirmacio.esborrar"/>"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="cache.boto.esborrar"/></a></li>
	</script>
</body>
</html>
