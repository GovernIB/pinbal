<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<title><spring:message code="paramconf.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.ca.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	
	<script>
	$(document).ready(function() {
		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="paramconf.list.confirmacio.esborrar"/>");
		});

		
	    $('#table-params').DataTable({
	    	autoWidth: false,
			processing: true,
			serverSide: true,
			dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>',
	        },
			ajax: '<c:url value="/scsp/paramconf/datatable"/>',
			columnDefs: [
				{ 
		            targets: 0,
		            width: "30%"
		        },
		        {
		            targets: 1,
		            width: "60%"
		        },
				{
					targets: [2],
					orderable: false,
					width: "1%",
					render: function (data, type, row, meta) {
							var template = $('#template-actions').html();
							return Mustache.render(template, row);
					}
				}, 
		   ]
		});
	});

	</script>

</head>
<body>
	
	<div class="container-fluid">
		<div class="pull-right">
			<a class="btn btn-primary" href="<c:url value="/scsp/paramconf/new"/>">
				<i class="fas fa-plus"></i>&nbsp;<spring:message code="paramconf.list.boto.nou.registre"/>
			</a>
		</div>
		
		<table id="table-params" class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
					<th data-data="nom"><spring:message code="paramconf.list.taula.columna.nom" /></th>
					<th data-data="valor"><spring:message code="paramconf.list.taula.columna.valor" /></th>
					<th data-data="id"></th>
				</tr>
			</thead>
		</table>
	</div>
<script id="template-actions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu">
			<li><a href="<c:url value="/scsp/paramconf/{{ nom }}"/>" ><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
			<li><a href="<c:url value="/scsp/paramconf/{{ nom }}/delete"/>" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
		</ul>
	</div>
</script>
<script type="text/javascript">
	function onInvokeAction(id) {
		setExportToLimit(id, '');
		createHiddenInputFieldsForLimitAndSubmit(id);
	}
</script>

</body>
</html>
