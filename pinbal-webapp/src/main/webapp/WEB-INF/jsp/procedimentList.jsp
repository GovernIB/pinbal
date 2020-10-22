<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<title><spring:message code="procediment.list.titol"/></title>

	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
		<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
		
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
	
	
<script>
$(document).ready(function() {
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
		$('#form-filtre').submit();
	});
	$('.confirm-esborrar').click(function() {
		  return confirm('<spring:message code="procediment.list.confirmacio.esborrar"/>');
	});
	

    $('#table-procediments').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/procediment/datatable/"/>',
		columnDefs: [
			{
				targets: [0, 1, 2, 3],
				width: "20%"
			},
			{
				targets: [5],
				render: function (data, type, row, meta) {
						var template = $('#template-activa').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [6],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-chg-status').html();
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [7],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-serveis').html();
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [8],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-modificar').html();
						row['propertyEsborrar'] = ${propertyEsborrar};
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [9],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-eliminar').html();
						return Mustache.render(template, row);
				}
			}, 
	   ],
	   initComplete: function( settings, json ) {

		}
	});
});
</script>
</head>
<body>

	<c:url value="/procediment" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-filtre-table" commandName="procedimentFiltreCommand">
		<div class="row">
			 <div class="col-md-3">			 
				 <pbl:inputText name="codi" inline="true" placeholderKey="procediment.list.filtre.camp.codi"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="nom" inline="true" placeholderKey="procediment.list.filtre.camp.nom"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="departament" inline="true" placeholderKey="procediment.list.filtre.camp.departament"/>	
			</div>
		</div>
		<div class="row">
			<div class="col-md-3">
				<pbl:inputSelect 
					name="organGestorId" 
					placeholderKey="procediment.list.filtre.camp.organgestor" 
					inline="true" 
					emptyOption="true" 
					emptyOptionTextKey="procediment.list.filtre.camp.opcio.cap"
					optionItems="${organsGestors}" 
					optionValueAttribute="id" 
					optionTextAttribute="nom"
					required="true" 
					optionMinimumResultsForSearch="5"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="codiSia"  inline="true" placeholderKey="procediment.list.filtre.camp.codisia"/>
			</div>
			<div class="col-md-3">
				<pbl:inputSelect 
					name="actiu" 
					emptyOption="true" 
					inline="true"
					optionEnum="FiltreActiuEnumDto"
					placeholderKey="procediment.list.filtre.camp.actiuOInactiu"/>
			</div>

			<div class="col-md-3">
				<div class ="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
	       </div>
		</div>
	</form:form>
	<a class="btn btn-primary" href="<c:url value="/procediment/new"/>"><i class="fa fa-plus"></i>&nbsp;<spring:message code="procediment.list.boto.nou.procediment"/></a>
	<table id="table-procediments" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="codi"><spring:message code="procediment.list.taula.columna.codi" /></th>
				<th data-data="nom"><spring:message code="procediment.list.taula.columna.nom" /></th>
				<th data-data="departament"><spring:message code="procediment.list.taula.columna.departament" /></th>
				<th data-data="organGestorStr"><spring:message code="procediment.list.taula.columna.organgestor" /></th>
				<th data-data="codiSia"><spring:message code="procediment.list.taula.columna.codisia" /></th>
				<th data-data="actiu"><spring:message code="procediment.list.taula.columna.actiu" /></th>
				<th data-data="id"></th>
				<th data-data="serveisActius"></th>
				<th data-data="id"></th>
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>

<script id="template-activa" type="x-tmpl-mustache">
{{#actiu}}
<i class="fa fa-check"></i>
{{/actiu}}
</script>
<script id="template-chg-status" type="x-tmpl-mustache">
{{#actiu}}
<a href="procediment/{{ id }}/disable" class="btn btn-default"><i class="fas fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
{{/actiu}}
{{^actiu}}
<a href="procediment/{{ id }}/enable" class="btn btn-default"><i class="fa fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
{{/actiu}}
</script>
<script id="template-serveis" type="x-tmpl-mustache">
	<a href="procediment/{{ id }}/servei" class="btn btn-default">
		<i class="fas fa-briefcase"></i>&nbsp;<spring:message code="entitat.list.taula.boto.serveis"/>&nbsp;<span class="badge">{{ serveisActius.length }}</span>
 	</a>
</script>
<script id="template-modificar" type="x-tmpl-mustache">
	<a href="procediment/{{ id }}" class="btn btn-default"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
</script>
<script id="template-eliminar" type="x-tmpl-mustache">
	<a href="procediment/{{ id }}/delete" class="btn btn-default confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a>
</script>
<script type="text/javascript">
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
</script>

</body>
</html>
