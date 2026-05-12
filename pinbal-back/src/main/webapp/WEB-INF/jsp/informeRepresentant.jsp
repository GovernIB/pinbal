<%@page import="es.caib.pinbal.core.dto.InformeRepresentantFiltreDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>


<html>
<head>
	<title><spring:message code="informe.representant.list.titol"/></title>
	
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

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
			$('#organGestorId').val(null);
			$('#procedimentId').val(null);
			$('#serveiCodi').val(null);
			$('#form-filtre').submit();
		});
		
// 		$('#procediment').change(function() {
// 			$('#serveiCodi').attr('urlParamAddicional', $(this).val());
// 			$('#serveiCodi').val(null).trigger('change.select2');
// 		});

// 		$('#procediment').on('select2:clear', function (e) {
// 			$('#serveiCodi').attr('urlParamAddicional', '');
// 			$('#serveiCodi').val(null).trigger('change.select2');
// 		});
		
	    $('#table-informe-representant').DataTable({
	    	autoWidth: false,
	    	paging: false,
			processing: true,
			serverSide: true,
			order: [],
			columns: [ { orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false },
				{ orderable: false } ],
			columnDefs: [{
				targets: [0],
				visible: false
			}, {
				targets: [1],
				render: function (data, type, row, meta) {
					var template = $('#template-activa').html();
					return Mustache.render(template, row);
				}
			}],
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>',
	        },
			ajax: '<c:url value="/informeRepresentant/datatable"/>'
		});
	});
</script>
</head>
<body>
	<c:url value="/informeRepresentant" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-filtre-table" commandName="informeRepresentantFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicialOrgans"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistatOrgans"/>
				<pbl:inputSuggest 
 					name="organGestorId"  
 					urlConsultaInicial="${urlConsultaInicialOrgans}"
 					urlConsultaLlistat="${urlConsultaInicialOrgans}"
 					placeholderKey="informe.representant.list.filtre.organ"
 					suggestValue="id"
 					suggestText="codiINom"
 					inline="true"/>	
			</div>	
			<div class="col-md-3">
				<c:url value="/procedimentajax/procediment" var="urlConsultaInicialProcediments"/>
				<c:url value="/procedimentajax/procediment" var="urlConsultaLlistatProcediments"/>
				<pbl:inputSuggest 
 					name="procedimentId"  
 					urlConsultaInicial="${urlConsultaInicialProcediments}"
 					urlConsultaLlistat="${urlConsultaLlistatProcediments}"
 					placeholderKey="informe.representant.list.filtre.procediment"
 					suggestValue="id"
 					suggestText="nomAmbCodi"
 					inline="true"/>	
			</div>
			<div class="col-md-3">
					<c:url value="/serveiajax/servei" var="urlConsultaInicialServeis"/>
				<c:url value="/serveiajax/servei" var="urlConsultaLlistatServeis"/>
				<pbl:inputSuggest 
					name="serveiCodi"  
					urlConsultaInicial="${urlConsultaInicialServeis}"
					urlConsultaLlistat="${urlConsultaLlistatServeis}"
					placeholderKey="informe.representant.list.filtre.servei"
					suggestValue="codi"
					suggestText="descripcioAmbCodi"
					inline="true"/> 
<!-- 					urlParamAddicional=""/>	 -->
			</div>			
			<div class="col-md-3 pull-right"> 
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button class="btn btn-primary" type="submit"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>		
		</div>
	</form:form>

	<table id="table-informe-representant" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="organGestorActiu"></th>
				<th data-data="organGestorCodi"><spring:message code="informe.representant.list.taula.organ.codi" /></th>
				<th data-data="organGestorNom"><spring:message code="informe.representant.list.taula.organ.nom" /></th>
				<th data-data="procedimentCodi"><spring:message code="informe.representant.list.taula.procediment.codi" /></th>
				<th data-data="procedimentNom"><spring:message code="informe.representant.list.taula.procediment.nom" /></th>
				<th data-data="serveiCodi"><spring:message code="informe.representant.list.taula.servei.codi" /></th>
				<th data-data="serveiNom"><spring:message code="informe.representant.list.taula.servei.nom" /></th>
				<th data-data="usuariCodi"><spring:message code="informe.representant.list.taula.usuari.codi" /></th>
				<th data-data="usuariNom"><spring:message code="informe.representant.list.taula.usuari.nom" /></th>
				<th data-data="usuariNif"><spring:message code="informe.representant.list.taula.usuari.nif" /></th>
			</tr>
		</thead>
	</table>

	<br/>
	<a href="informeRepresentant/excelInformePerRepresentant" class="btn btn-default">
		<i class="far fa-file-excel"></i>&nbsp;<spring:message code="estadistiques.list.exportar.excel"/>
	</a>

<script id="template-activa" type="x-tmpl-mustache">
	{{organGestorCodi}}
	{{^organGestorActiu}}
		<span class="fa fa-exclamation-triangle text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="organgestor.list.extingit"/>"></span>
	{{/organGestorActiu}}
</script>
</body>
</html>
