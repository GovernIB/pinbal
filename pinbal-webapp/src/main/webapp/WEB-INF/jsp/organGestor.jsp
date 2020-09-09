<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="organgestor.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
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
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a id="organgestor-boto-nou" class="btn btn-default" href="organgestor/sync/dir3">
			<i class="fas fa-sync"></i> &nbsp; <spring:message code="organgestor.list.boto.actualitzar"/>
		</a>
	</div>
	<table id="table-organs" 
			class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-data="codi">
					<spring:message code="organgestor.list.columna.codi"/>
				</th>
				<th data-data="nom">
					<spring:message code="organgestor.list.columna.nom"/>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			
		</p>
	</script>
	
	<script type="text/javascript">
	$(document).ready(function() {
	    $('#table-organs').DataTable({
	    	autoWidth: false,
			processing: true,
			serverSide: true,
			"order": [[ 1, "asc" ]],
			language: {
	            "url": "js/datatable-language.json"
	        },
			ajax: "organgestor/datatable",
			columnDefs: []
		});
	});
	</script>
</body>
</html>
