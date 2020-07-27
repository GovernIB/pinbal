<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script>
$(document).ready(function() {
	$('.confirm-remove').click(function() {
		  return confirm("<spring:message code="entitat.serveis.confirmacio.desactivar.servei"/>");
	});
	
	
	
    $('#table-serveis').DataTable({
    	autoWidth: false,
		processing: true,
		paging: false,
		serverSide: true,
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/entitat/${entitat.id}/servei/datatable"/>',
		columnDefs: [
			{
				targets: [2],
				orderable: false,
				render: function (data, type, row, meta) {
					var template = $('#template-actiu').html();
					return Mustache.render(template, row);
				}
			}, 
			{
				targets: [3],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
					var template = $('#template-swap-actiu').html();
					return Mustache.render(template, row);
				}
			}, 
	   ]
	});
});
</script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="entitat.miques.entitat" arguments="${entitat.nom}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="entitat.miques.serveis"/></li>
	</ul>
	<table id="table-serveis" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="codi"><spring:message code="servei.list.taula.columna.codi" /></th>
				<th data-data="descripcio"><spring:message code="servei.list.taula.columna.descripcio" /></th>
				<th data-data="actiu"><spring:message code="entitat.serveis.actiu" /></th>
				<th></th>
			</tr>
		</thead>
	</table>
<script id="template-actiu" type="x-tmpl-mustache">
{{#actiu}}
<i class="fa fa-check"></i>
{{/actiu}}
</script>
<script id="template-swap-actiu" type="x-tmpl-mustache">
{{#actiu}}
	<a href="servei/{{ codi }}/remove" class="btn btn-default confirm-remove"><i class="fas fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
{{/actiu}}
{{^actiu}}
	<a href="servei/{{ codi }}/add" class="btn btn-default"><i class="fa fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
{{/actiu}}
</script>
	<div>
		<a href="<c:url value="/entitat"/>" class="btn btn-primary pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
