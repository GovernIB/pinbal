<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	request.setAttribute(
			"entitatTipusLlista",
			es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto.values());
%>

<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.ca.min.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
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
		  return confirm("<spring:message code="entitat.list.confirmacio.esborrar"/>");
	});
		
    $('#table-entitats').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": "js/datatable-language.json"
        },
		ajax: "entitat/datatable",
		columnDefs: [
			{ 
	            targets: 0,
	        }, { 
	            targets: [1],
				render: function (data, type, row, meta) {
					row['span-label'] = '';
					if (row.tipus=='GOVERN'){
						row['span-label'] = '<span class="label label-default" title="<spring:message code="entitat.list.entitat.tipus.GOVERN"/>">GOV</span>';
						
					}else if(row.tipus=='CONSELL'){
						row['span-label'] = '<span class="label label-default" title="<spring:message code="entitat.list.entitat.tipus.CONSELL"/>">CON</span>';
						
					}else if(row.tipus=='AJUNTAMENT'){
						row['span-label'] = '<span class="label label-default" title="<spring:message code="entitat.list.entitat.tipus.AJUNTAMENT"/>">AJU</span>';
					}
					var template = $('#template-nom').html();
					return Mustache.render(template, row);
				}
	        },
			{
				targets: [4],
				render: function (data, type, row, meta) {
						var template = $('#template-activa').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [5],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-btn-usuaris').html();
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [6],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-btn-serveis').html();
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [7],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-accions').html();
						row['propertyEsborrar'] = ${propertyEsborrar};
						return Mustache.render(template, row);
				}
			}, 
	   ]
	});
	
});
</script>
</head>
<body>

	<c:url value="/entitat" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well" commandName="entitatFiltreCommand">
		<div class="container-fluid">
			<div class="row">		
				<div class="col-md-2" >
					<pbl:inputText name="codi" inline="true" placeholderKey="entitat.list.filtre.camp.codi"/>
<%-- 					<c:set var="campPath" value="codi" /> --%>
<%-- 					<div class="form-group"<c:if test="${not empty campErrors}"> error</c:if>"> --%>
								
<%-- 							<spring:bind path="${campPath}"> --%>
<%-- 								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="entitat.list.filtre.camp.codi"/>"> --%>
<%-- 							</spring:bind> --%>
				</div>
						
					
						
				<div class="col-md-2">
				
					<pbl:inputText name="nom" inline="true" placeholderKey="entitat.list.filtre.camp.nom"/>
					
<%-- 					<c:set var="campPath" value="nom"/> --%>
					
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<spring:bind path="${campPath}"> --%>
<%-- 								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="entitat.list.filtre.camp.nom"/>"> --%>
<%-- 							</spring:bind> --%>
<!-- 					</div> -->

				</div>
				
				<div class="col-md-2">
					<pbl:inputText name="cif" inline="true" placeholderKey="entitat.list.filtre.camp.cif"/>
<%-- 					<c:set var="campPath" value="cif"/> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<spring:bind path="${campPath}"> --%>
<%-- 								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="entitat.list.filtre.camp.cif"/>"> --%>
<%-- 							</spring:bind> --%>
<!-- 					</div> -->

				</div>
			
				<div class="col-md-2">
					<pbl:inputSelect name="activa" inline="true"  placeholderKey="entitat.list.filtre.camp.activa.yes"	optionItems="${campPath}" emptyOption="true"/>
<%-- 					<c:set var="campPath" value="activa"/> --%>
<%-- 					<spring:message var="trueValue" code="entitat.list.filtre.camp.activa.yes"/> --%>
<%-- 					<spring:message var="falseValue" code="entitat.list.filtre.camp.activa.no"/> --%>
<%-- 					<form:select class="form-control" path="${campPath}"> --%>
<%-- 						<option value=""><spring:message code="entitat.list.filtre.camp.activa"/></option>> --%>
<%-- 						<form:option value="true">${trueValue}</form:option>> --%>
<%-- 						<form:option value="false">${falseValue}</form:option>> --%>
<%-- 					</form:select> --%>
				</div>	
				<div class="col-md-2">
					<pbl:inputSelect name="activa" inline="true"  placeholderKey="entitat.list.filtre.camp.tipus"
						optionItems="${entitatTipusLlista}"
						emptyOption="true"/>
<%-- 						<c:set var="campPath" value="tipus"/> --%>
<%-- 					<form:select class="form-control" path="${campPath}"> --%>
<%-- 						<option value=""><spring:message code="entitat.list.filtre.camp.tipus"/></option>> --%>
<%-- 						<c:forEach var="estat" items="${entitatTipusLlista}"> --%>
<%-- 							<form:option value="${estat}">${estat}</form:option> --%>
<%-- 						</c:forEach> --%>
<%-- 					</form:select> --%>
				</div>
				<div class="col-md-2">
					<div class="pull-right">
						<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
						<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
					</div>				
				</div>
		</div>
	</div>		
	
	</form:form>
	
		<div class ="container-fluid">
				<div class="row">
					<div class="pull-right">
						<a class="btn btn-primary " href="<c:url value="/entitat/new"/>"><i class="glyphicon-plus"></i>&nbsp;<spring:message code="entitat.list.boto.nova.entitat"/></a>
					</div>
							
				</div
		</div>		
		<div class="clearfix"></div>
		
	<div class="container-fluid">	
		<div style="position: relative; top: -40px; z-index:0">
		<table id="table-entitats" class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
					<th data-data="codi"><spring:message code="entitat.list.taula.columna.codi" /></th>
					<th data-data="nom"><spring:message code="entitat.list.taula.columna.nom" /></th>
					<th data-data="cif"><spring:message code="entitat.list.taula.columna.cif" /></th>
					<th data-data="tipus"><spring:message code="entitat.list.taula.columna.tipus" /></th>
					<th data-data="activa"><spring:message code="entitat.list.taula.columna.activa" /></th>
					<th data-data="usuaris"></th>
					<th data-data="serveis"></th>
					<th data-data="id"></th>
				</tr>
			</thead>
		</table>
		</div>
	</div>
<script id="template-nom" type="x-tmpl-mustache">
{{{ span-label }}}
{{ nom }}
</script>

<script id="template-activa" type="x-tmpl-mustache">
{{#activa}}
<i class="fa fa-check"></i>
{{/activa}}
</script>
<script id="template-btn-usuaris" type="x-tmpl-mustache">
	<a href="entitat/{{ id }}/usuari" class="btn btn-default">
		<i class="fas fa-users"></i>&nbsp;<spring:message code="entitat.list.taula.boto.usuaris"/>&nbsp;<span class="badge">{{ usuaris.length }}</span>
	</a>
</script>
<script id="template-btn-serveis" type="x-tmpl-mustache">
	<a href="entitat/{{ id }}/servei" class="btn btn-default">
		<i class="fas fa-briefcase"></i>&nbsp;<spring:message code="entitat.list.taula.boto.serveis"/>&nbsp;<span class="badge">{{ serveis.length }}</span>
	</a>
</script>
<script id="template-accions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu">
			<li>
				{{#activa}}
				<a href="entitat/{{ id }}/disable"><i class="fas fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
				{{/activa}}
				{{^activa}}
				<a href="entitat/{{ id }}/enable"><i class="fas fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
				{{/activa}}
			</li>
			<li>
				<a href="entitat/{{ id }}"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
			</li>
			{{#propertyEsborrar}}
				<li><a href="entitat/{{ id }}/delete" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
			{{/propertyEsborrar}}
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
