<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="servei.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
		
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
				  return confirm("<spring:message code="servei.list.confirmacio.esborrar"/>");
			});
			
			
			
		    $('#table-serveis').DataTable({
		    	autoWidth: false,
				processing: true,
				serverSide: true,
				dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
				language: {
		            "url": "js/datatable-language.json"
		        },
				ajax: "servei/datatable",
				columnDefs: [
					{
						targets: [3],
						orderable: false,
						width: "10%",
						render: function (data, type, row, meta) {
								var template = $('#template-btn-procediments').html();
								return Mustache.render(template, row);
						}
					}, 
					{
						targets: [4],
						orderable: false,
						width: "10%",
						render: function (data, type, row, meta) {
								var template = $('#template-accions').html();
								return Mustache.render(template, row);
						}
					}, 
			   ]
			});		    
		});
		function showModalProcediments(element) {
			var ample = Math.min(980, (window.innerWidth - 40));
			var alt = Math.round(window.innerHeight * 0.8) - 110;
			
			$('#modal-procediment-list').find('.modal-header h3').html($(element).data("titol"));
// 			$('#modal-procediment-list').css({	"width": ample + 'px',
// 												"margin-left": '-' + (ample/2) + 'px'});
			$('#modal-procediment-list').find('.modal-body').css({"max-height": alt + "px"});
			$('#modal-procediment-list').find('.modal-body').load(element.href);
			$('#modal-procediment-list').modal('toggle');
		}
	</script>
	<style type="text/css">
		#modal-procediment-list .modal-body .well {
			display: none;
		}
	</style>
</head>
<body>
	<c:url value="/servei" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-inline" commandName="serveiFiltreCommand">
		<div class="row">
			<div class="form-group col-md-3">	
				<c:set var="campPath" value="codi"/>
				<spring:message var="placeholderCodi" code="servei.list.filtre.camp.codi"/>
				<form:input path="${campPath}" cssClass="col-md-12 input-medium" id="${campPath}" placeholder="${placeholderCodi}"/>
			</div>
			<div class="form-group col-md-3">	
				<c:set var="campPath" value="descripcio"/>
				<spring:message var="placeholderDescripcio" code="servei.list.filtre.camp.descripcio"/>
				<form:input path="${campPath}" cssClass="col-md-12 input-medium" id="${campPath}" placeholder="${placeholderDescripcio}"/>
			</div>
			<div class="form-group col-md-3">	
				<c:set var="campPath" value="emissor"/>
				<form:select path="${campPath}" id="${campPath}" class="col-md-12">
					<option value=""><spring:message code="servei.list.filtre.camp.emissor"/></option>
					<form:options items="${emisors}" itemLabel="nom" itemValue="id"/>
				</form:select>
			</div>
			<div class="form-group col-md-3">	
				<c:set var="campPath" value="activa"/>
				<spring:message var="trueValue" code="entitat.list.filtre.camp.activa.yes"/>
				<spring:message var="falseValue" code="entitat.list.filtre.camp.activa.no"/>
				<form:select path="${campPath}" class="col-md-12">
					<option value=""><spring:message code="entitat.list.filtre.camp.activa"/></option>>
					<form:option value="true">${trueValue}</form:option>>
					<form:option value="false">${falseValue}</form:option>>
				</form:select>
			</div>
		</div>
		<div class="row">
			<div class="pull-right">
				<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>

	</form:form>

	<div class="row">
		<div class="col-md-12">
			<a class="btn pull-right" href="<c:url value="/servei/new"/>"><i class="glyphicon-plus"></i>&nbsp;<spring:message code="servei.list.boto.nou.servei"/></a>
		</div>
		<div class="clearfix"></div>
	</div>
	
	<div class="clearfix"></div>
	<div style="position: relative; top: -40px; z-index:0">
	<table id="table-serveis" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="codi"><spring:message code="servei.list.taula.columna.codi" /></th>
				<th data-data="descripcio"><spring:message code="servei.list.taula.columna.descripcio" /></th>
				<th data-data="scspEmisor.nom"><spring:message code="servei.list.taula.columna.emissor" /></th>
				<th data-data="numeroProcedimentsAssociats"></th>
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>
	</div>

<div id="modal-procediment-list" class="modal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn btn-primary" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
		</div>
	</div>
  </div>
</div>
<script id="template-btn-procediments" type="x-tmpl-mustache">
<a class="btn btn-default" href="<c:url value="/modal/servei/{{ codi }}/procediments"/>" 
		onclick="showModalProcediments(this);return false" data-titol="<spring:message code="servei.procediment.list.titol" arguments="{{ descripcio }}"/>">
	<i class="fas fa-briefcase"></i>&nbsp;<spring:message code="entitat.list.taula.boto.procediments"/>&nbsp;<span class="badge">{{ numeroProcedimentsAssociats }}</span>
</a>
</script>
<script id="template-accions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu">
			<li><a href="servei/{{ codi }}" title="<spring:message code="comu.boto.modificar"/>"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
			<li><a href="servei/{{ codi }}/camp" title="<spring:message code="servei.list.taula.boto.formulari"/>"><i class="fas fa-th-list"></i>&nbsp;<spring:message code="servei.list.taula.boto.formulari"/></a></li>
			<li><a href="servei/{{ codi }}/justificant" title="<spring:message code="servei.list.taula.boto.justificant"/>"><i class="fas fa-file"></i></i>&nbsp;<spring:message code="servei.list.taula.boto.justificant"/></a></li>
			<li><a href="servei/{{ codi }}/delete" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
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
