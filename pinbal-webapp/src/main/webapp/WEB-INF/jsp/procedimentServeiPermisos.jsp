<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="procediment.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables/1.10.21/css/jquery.dataTables.min.css"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="procediment.serveis.miques.procediment" arguments="${procediment.nom}"/> <span class="divider">/</span></li>
		<li><spring:message code="procediment.serveis.miques.servei" arguments="${servei.descripcio}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="procediment.serveis.miques.permisos"/></li>
	</ul>
	<table id="table-serveis" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="usuari.descripcio"><spring:message code="procediment.serveis.permisos.taula.columna.usuari" /></th>
				<th data-data="acces"><spring:message code="procediment.serveis.permisos.taula.columna.acces.permes" /></th>
				<th data-data="usuari.codi"></th>
			</tr>
		</thead>
	</table>
	<div>
		<a href="<c:url value="../../servei"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

<script id="template-acces" type="x-tmpl-mustache">
{{#acces}}
<i class="fa fa-check"></i>
{{/acces}}
</script>
<script id="template-accions" type="x-tmpl-mustache">
{{#acces}}
	<a href="permis/{{ usuari.codi }}/deny" class="btn btn-primary">
		<i class="fa fa-times"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.denegar.acces"/>
	</a>
{{/acces}}
{{^acces}}
	<a href="permis/{{ usuari.codi }}/allow" class="btn btn-primary">
		<i class="fa fa-check"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.permetre.acces"/>
	</a>
{{/acces}}
</script>
<script>
	$(document).ready(function() {

	    $('#table-serveis').DataTable({
	    	autoWidth: false,
	    	paging: false,
			processing: true,
			serverSide: true,
			dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>'
	        },
			ajax: '<c:url value="/procediment/${procediment.id}/servei/${servei.codi}/permis/datatable"/>',
			columnDefs: [
				{
					targets: [0],
					orderable: false,
				},
				{
					targets: [1],
					orderable: false,
					width: "10%",
					render: function (data, type, row, meta) {
							var template = $('#template-acces').html();
							return Mustache.render(template, row);
					}
				},
				{
					targets: [2],
					orderable: false,
					width: "10%",
					render: function (data, type, row, meta) {
							var template = $('#template-accions').html();
							return Mustache.render(template, row);
					}
				}, 
		   ],
		});
	});
</script>
<script>
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
</script>
</body>
</html>
