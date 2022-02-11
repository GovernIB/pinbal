<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	request.setAttribute(
			"consultaEstats",
			es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
%>

<html>
<head>
	<title><spring:message code="consulta.multiple.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	

	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
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
		var accioInput = $("<input>")
		.attr("type", "hidden")
		.attr("name", "accio").val("netejar");
		$('#form-filtre').append(accioInput);
		$('#form-filtre').submit();
	});
	$('#select-procediment').change(function() {
		var targetUrl;
		if ($(this).val())
			targetUrl = '<c:url value="consulta/serveisPermesosPerProcediment"/>/' + $(this).val();
		else
			targetUrl = '<c:url value="consulta/serveisPermesosPerProcediment"/>';
		$.ajax({
		    url:targetUrl,
		    type:'GET',
		    dataType: 'json',
		    success: function(json) {
		    	$('#select-servei').empty();
	        	$('#select-servei').append($('<option>').text('<spring:message code="consulta.list.filtre.servei"/>:'));
		        $.each(json, function(i, value) {
		            $('#select-servei').append($('<option>').text(value.descripcio).attr('value', value.codi));
		        });
				<c:set var="campPath" value="codi"/>
		        
		        
		        
		    }
		});
	});
});
</script>
</head>
<body>
	<form:form id="form-filtre" action="multiple" method="post" cssClass="well form-filtre-table" commandName="filtreCommand">
		<div class="row">
			<div class="col-md-3">
				<pbl:inputText name="scspPeticionId" inline="true" placeholderKey="consulta.list.filtre.peticion.id"/>
			</div>
			<div class="col-md-3">
				<pbl:inputSelect name="procediment" inline="true" 
								 placeholderKey="consulta.list.filtre.procediment" 
								 optionItems="${procediments}"  
								 optionValueAttribute="id"
								 optionTextAttribute="nom"
								 emptyOption="true"/>
			</div>
			<div class="col-md-3">
				<pbl:inputSelect 
						name="servei" 
						inline="true" 
						placeholderKey="consulta.list.filtre.servei"
 						optionItems="${serveis}" 
	 					optionValueAttribute="codi" 
	 					optionTextAttribute="descripcio" 
	 					emptyOption="true"/> 
			</div>
			<div class="col-md-3">
				<pbl:inputSelect 
					name="estat"  
					inline="true" 
					placeholderKey="consulta.list.filtre.estat" 
					optionItems="${consultaEstats}"
				 	emptyOption="true"/>
			</div>	
		</div>
		<div class="row">	
			<div class="col-md-3" >
				<div class="row">
					<div class="col-md-6" >
						<pbl:inputDate name="dataInici" inline="true" placeholderKey="consulta.list.filtre.data.inici"/>
					</div>		
					<div class="col-md-6" >
						<pbl:inputDate name="dataFi" inline="true" placeholderKey="consulta.list.filtre.data.fi"/>
					</div>		
				</div>		
			</div>
			<div class="col-md-9">
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
			 		<button class="btn btn-primary" type="submit"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
		 		</div>
	 		</div>			
		</div>
	</form:form>
	<table id="table-consultes" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="scspPeticionId"><spring:message code="consulta.list.taula.peticion.id" /></th>
				<th data-data="creacioData"><spring:message code="consulta.list.taula.data" /></th>
				<th data-data="procedimentNom"><spring:message code="consulta.list.taula.procediment" /></th>
				<th data-data="serveiDescripcio"><spring:message code="consulta.list.taula.servei" /></th>
				<th data-data="titularNomSencer"><spring:message code="consulta.list.taula.titular.nom" /></th>		
				<th data-data="estat"><spring:message code="consulta.list.taula.estat" /></th>
				<th data-data="error" data-visible="false"></th>
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>
<script>
$(document).ready(function() {
	$('#table-consultes').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		"order": [[ 1, "desc" ]],
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/consulta/multiple/datatable/"/>',
		columnDefs: [
			{
				targets: [0],
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-id-peticion').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [1],
				width: "10%",
				render: $.fn.dataTable.render.moment('x', 'DD/MM/YYYY HH:mm:ss', 'es' )
			},	
			{
				targets: [3, 4],
				orderable: false,
			},	
			{
				targets: [5],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-estat').html();
						row['icon-status'] = '';
						if (row.estat=='Error'){
							row['icon-status'] = '<i class="fas fa-exclamation-triangle" title="' + row.error + '"></i>';

						}else if(row.estat=='Pendent'){
							row['icon-status'] = '<i class="fas fa-bookmark"></i>';

						}else if(row.estat=='Processant'){
							row['icon-status'] = '<i class="fas fa-hourglass-half"></i>';

						}else{
							row['icon-status'] = '<i class="fa fa-check"></i>';
						}
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [7],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-details').html();
						return Mustache.render(template, row);
				}
			},
	   ],
	   initComplete: function( settings, json ) {

		}
	});
});
</script>
<script id="template-id-peticion" type="x-tmpl-mustache">
{{scspPeticionId}}
{{#recobriment}}
	<span class="badge">R</span>
{{/recobriment}}
</script>
<script id="template-estat" type="x-tmpl-mustache">
	{{{ icon-status }}} {{ estat }}
</script>
<script id="template-details" type="x-tmpl-mustache">
<a href="multiple/{{ id }}" class="btn btn-default"><i class="fas fa-search-plus"></i>&nbsp;<spring:message code="admin.consulta.list.taula.detalls"/></a>
</script>
	<script type="text/javascript">
		function onInvokeAction(id) {
			setExportToLimit(id, '');
			createHiddenInputFieldsForLimitAndSubmit(id);
		}
	</script>

</body>
</html>
