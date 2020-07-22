<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="claupublica.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	
	<script>
	$(document).ready(function() {

		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="claupublica.list.confirmacio.esborrar"/>");
		});
		
	    $('#table-claus').DataTable({
	    	autoWidth: false,
			processing: true,
			serverSide: true,
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>',
	        },
			ajax: '<c:url value="/scsp/claupublica/datatable"/>',
			columnDefs: [
		        {
		            targets: [3, 4],
		            render: function (data, type, row, meta) {
		            	console.log(data)
		            	return data==null ? "" : moment(data).format('DD-MM-YYYY' )
		            }
		        },
				{
					targets: [5],
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

	<div class="row-fluid">
		<div class="span12">
			<a class="btn pull-right" href="<c:url value="/scsp/claupublica/new"/>"><i class="icon-plus"></i>&nbsp;<spring:message code="claupublica.list.boto.nou.registre"/></a>
		</div>
		<div class="clearfix"></div>
	</div>
	<table id="table-claus" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
			<th data-data="alies"><spring:message code="claupublica.list.taula.columna.alies" /></th>
			<th data-data="nom"><spring:message code="claupublica.list.taula.columna.nom" /></th>
			<th data-data="numSerie"><spring:message code="claupublica.list.taula.columna.numserie" /></th>
			<th data-data="dataAlta"><spring:message code="claupublica.list.taula.columna.dataalta" /></th>
			<th data-data="dataBaixa"><spring:message code="claupublica.list.taula.columna.databaixa" /></th>
			<th data-data="id"></th>
			</tr>
		</thead>
	</table>

<script id="template-actions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu">
			<li><a href="<c:url value="/scsp/claupublica/{{ id }}"/>" ><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
			<li><a href="<c:url value="/scsp/claupublica/{{ id }}/delete"/>" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
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
