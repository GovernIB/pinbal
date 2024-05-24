<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="emissorcert.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.ca.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script>
	$(document).ready(function() {
		/* $('#netejar-filtre').click(function() {
			$(':input', $('#form-filtre')).each (function() {
				var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea')
					this.value = '';
				else if (type == 'checkbox' || type == 'radio')
					this.checked = false;
				else if (tag == 'select')
					this.selectedIndex = 0;
			});
			$('#form-filtre').submit();
		}); */
		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="emissorcert.list.confirmacio.esborrar"/>");
		});
		
	    $('#table-emisors').DataTable({
	    	autoWidth: false,
			processing: true,
			serverSide: true,
			dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>',
	        },
			ajax: '<c:url value="/scsp/emissorcert/datatable"/>',
			columnDefs: [
				{ 
		            targets: 0,
		            width: "50%"
		        },
		        {
		            targets: 1,
		            width: "20%"
		        },
		        {
		            targets: [2],
		            width: "20%",
		            render: function (data, type, row, meta) {
		            	
		            	return data==null ? "" : moment(data).format('YYYY-MM-DD', 'DD/MM/YYYY', 'es' )
		            }		            	
		        },
				{
					targets: [3],
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
			<a class="btn btn-primary " href="<c:url value="/scsp/emissorcert/new"/>" data-toggle="modal" data-refresh-pagina="true">
				<i class="fas fa-plus"></i>&nbsp;<spring:message code="emissorcert.list.boto.nou.registre"/>
			</a>
		</div>
	
		<table id="table-emisors" class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
					<th data-data="nom"><spring:message code="emissorcert.list.taula.columna.nom" /></th>
					<th data-data="cif"><spring:message code="emissorcert.list.taula.columna.cif" /></th>
					<th data-data="dataBaixa"><spring:message code="emissorcert.list.taula.columna.databaixa" /></th>
					<th data-data="id"></th>
				</tr>
			</thead>
		</table>
	</div>
<script id="template-actions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu dropdown-menu-right">
			<li><a href="<c:url value="/scsp/emissorcert/{{ id }}"/>" data-toggle="modal" data-refresh-pagina="true"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
			<li><a href="<c:url value="/scsp/emissorcert/{{ id }}/delete"/>" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
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
