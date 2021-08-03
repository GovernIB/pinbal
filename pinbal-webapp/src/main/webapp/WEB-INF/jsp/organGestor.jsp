<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl"%>
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
	

	<c:url value="/organgestor" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-filtre-table" commandName="organGestorFiltreCommand">

		<div class="row">		
			<div class="col-md-3" >
				<pbl:inputText name="codi" inline="true" placeholderKey="organgestor.list.filtre.camp.codi"/>
			</div>						
			<div class="col-md-4">
				<pbl:inputText name="nom" inline="true" placeholderKey="organgestor.list.filtre.camp.nom"/>
			</div>
			<div class="col-md-3">
				<pbl:inputSelect name="estat"  inline="true" placeholderKey="organgestor.list.filtre.camp.estat" optionEnum="OrganGestorEstatEnumDto"  emptyOption="true"/>
			</div>	

			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>				
			</div>	
		</div>	
	</form:form>



	<table id="table-organs" 
			class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-data="actiu"><spring:message code="organgestor.list.columna.codi"/></th>
				<th data-data="codi" width="80px"><spring:message code="organgestor.list.columna.codi"/></th>
				<th data-data="nom"><spring:message code="organgestor.list.columna.nom"/></th>
			</tr>
		</thead>
	</table>
	
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
			columnDefs: [
				{ 
		            targets: [0],
					orderable: false,
					visible: false
		        },
				{
					targets: [1],
					render: function (data, type, row, meta) {
							var template = $('#template-activa').html();
							return Mustache.render(template, row);
					}
				}				
			]
		});

		$('#netejar-filtre').click(function() {
			$(':input', $('#form-filtre')).each (function() {
				var type = this.type, tag = this.tagName.toLowerCase();
				if (type == 'text' || type == 'password' || tag == 'textarea')
					this.value = '';
				else if (type == 'checkbox' || type == 'radio')
					this.checked = false;
				else if (tag == 'select')
					this.selectedIndex = 0;
			});
		    $('#estat').val('VIGENT').change();
			$('#form-filtre').submit();
		});

		
	});
	</script>
	
<script id="template-activa" type="x-tmpl-mustache">
	{{codi}}
	{{^actiu}}
		<span class="fa fa-exclamation-triangle text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="organgestor.list.extingit"/>"></span>
	{{/actiu}}
</script>
	
</body>
</html>
