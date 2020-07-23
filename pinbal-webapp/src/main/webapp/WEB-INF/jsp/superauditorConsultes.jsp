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
	<title><spring:message code="superauditor.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script> 
 	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script> 
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	
	

	
	
	
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
	$('#select-procediment').change(function() {
		var targetUrl;
		if ($(this).val())
			targetUrl = '<c:url value="superauditor/serveisPerProcediment"/>/' + $(this).val();
		else
			targetUrl = '<c:url value="superauditor/serveisPerProcediment"/>';
		$.ajax({
		    url:targetUrl,
		    type:'GET',
		    dataType: 'json',
		    success: function(json) {
		    	$('#select-servei').empty();
	        	$('#select-servei').append($('<option>').text('<spring:message code="auditor.list.filtre.servei"/>:'));
		        $.each(json, function(i, value) {
		            $('#select-servei').append($('<option>').text(value.descripcio).attr('value', value.codi));
		        });
		    }
		});
	});
	

    $('#table-consultes').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/superauditor/datatable/"/>',
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
				targets: [6],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-estat').html();
						row['icon-status'] = '';
						if (row.estat=='Error'){
							row['icon-status'] = '<i class="fas fa-exclamation-triangle"></i>';

						}else if(row.estat=='Pendent'){
							row['icon-status'] = '<i class="fas fa-bookmark"></i>';

						}else if(row.estat=='Processant'){
							row['icon-status'] = '<i class="fas fa-hourglass-half"></i>';

						}else{
							row['icon-status'] = '<i class="fa fa-check"></i>';
						}
						return Mustache.render(template, row);
				}
			}
	   ],
	   initComplete: function( settings, json ) {

		}
	});
});
</script>
</head>
<body>

	<c:choose>
		<c:when test="${empty entitatActual}">
			<c:url value="/superauditor/entitat/seleccionar" var="formAction"/>
			<form action="<c:url value="/superauditor/entitat/seleccionar"/>" method="post" class="well">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-2">
<%-- 						<pbl:inputSelect name="entitatId" inline="true" placeholderKey="superauditor.list.select.entitat.seleccioni" optionItems="${entitats}" emptyOption="true"/> --%>
							<select name="entitatId" class="form-control">
								<option value=""><spring:message code="superauditor.list.select.entitat.seleccioni"/></option>
								<c:forEach var="entitat" items="${entitats}">
								<option value="${entitat.id}">${entitat.nom}</option>
							</c:forEach>
							</select>
					</div>
					<div class="col-md-2">
							
						<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.seleccionar"/></button>
					
					</div>
				</div>
			</div>		
			</form>
		</c:when>
		<c:otherwise>
			<form class="well">
				<input type="text" class="input-sm" value="${entitatActual.nom}" disabled="disabled"/>
				<a href="<c:url value="/superauditor/entitat/deseleccionar"/>" class="btn btn-default"><spring:message code="comu.boto.canviar"/></a>
			</form>
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty entitatActual}">
		<form:form id="form-filtre" action="" method="post" cssClass="well" commandName="filtreCommand">
			<div class="page-header"><spring:message code="auditor.list.filtre.titol"/></div>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-3">
					<pbl:inputText name="scspPeticionId" inline="true" placeholderKey="auditor.list.filtre.peticion.id"/>
<%-- 						<c:set var="campPath" value="scspPeticionId"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<spring:bind path="${campPath}"> --%>
<%-- 								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.peticion.id"/>"> --%>
<%-- 							</spring:bind> --%>
<!-- 						</div> -->
					</div>
					<div class="col-md-3">
					
						<pbl:inputSelect name="procediment" inline="true" placeholderKey="auditor.list.filtre.procediment"
							optionItems="${procediments}"
							optionValueAttribute="id"
							optionTextAttribute="nom"
							emptyOption="true"/>
<%-- 						<c:set var="campPath" value="procediment"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<form:select id="select-procediment" path="${campPath}" cssClass="form-control"> --%>
<%-- 								<option value=""><spring:message code="auditor.list.filtre.procediment"/>:</option> --%>
<%-- 								<form:options items="${procediments}" itemLabel="nom" itemValue="id"/> --%>
<%-- 							</form:select> --%>
<!-- 						</div> -->
					</div>
					<div class="col-md-3">
						<pbl:inputSelect name="servei" inline="true" placeholderKey="auditor.list.filtre.servei"
							optionItems="${serveis}"
							optionValueAttribute="codi"
							optionTextAttribute="descripcio"
							emptyOption="true"/>
<%-- 						<c:set var="campPath" value="servei"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<form:select id="select-servei" path="${campPath}" cssClass="form-control"> --%>
<%-- 								<option value=""><spring:message code="auditor.list.filtre.servei"/>:</option> --%>
<%-- 								<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/> --%>
<%-- 							</form:select> --%>
<!-- 						</div> -->
					</div>
					<div class="col-md-3">
						<pbl:inputSelect name="estat" inline="true" placeholderKey="auditor.list.filtre.estat"
							optionItems="${consultaEstats}"
							emptyOption="true"/>
<%-- 						<c:set var="campPath" value="estat"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<form:select path="${campPath}" cssClass="form-control"> --%>
<%-- 								<option value=""><spring:message code="auditor.list.filtre.estat"/>:</option> --%>
<%-- 								<c:forEach var="estat" items="${consultaEstats}"> --%>
<%-- 									<c:if test="${not fn:startsWith(estat, 'P')}"> --%>
<%-- 										<form:option value="${estat}">${estat}</form:option> --%>
<%-- 									</c:if> --%>
<%-- 								</c:forEach> --%>
<%-- 							</form:select> --%>
					</div>
				</div>
			</div>
		
			<div class="container-fluid">	
				<div class="row">
					<div class="col-md-3">
						<label><spring:message code="auditor.list.filtre.data"/></label>
						<pbl:inputDate name="dataInici"  inline="true" placeholderKey="auditor.list.filtre.data.inici"/>
<%-- 							<c:set var="campErrors"><form:errors path="dataInici"/></c:set> --%>
<%-- 							<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if> --%>
					
<%-- 						<div class="form-gruop<c:if test="${not empty campErrors}"> error</c:if>"> --%>
							<label><spring:message code="auditor.list.filtre.data"/></label>
		
<%-- 									<c:set var="campPath" value="dataInici"/> --%>
<%-- 									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 									<spring:bind path="${campPath}"> --%>
<%-- 										<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.data.inici"/>"> --%>
<!-- 										<script>$("#${campPath}").mask("99/99/9999");</script> -->
<%-- 									</spring:bind> --%>
					</div>
					<div class="col-md-3">
						<label>&nbsp;</label>
						<pbl:inputDate name="dataFi"  inline="true" placeholderKey="auditor.list.filtre.data.fi"/>
<%-- 									<c:set var="campPath" value="dataFi"/> --%>
<%-- 									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 									<div class="form-gruop<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 										<spring:bind path="${campPath}"> --%>
<%-- 											<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.data.fi"/>"> --%>
<!-- 											<script>$("#${campPath}").mask("99/99/9999");</script> -->
<%-- 										</spring:bind> --%>
<!-- 									</div> -->
<!-- 								</div> -->
					</div>
				</div>
				<div class="row">
					<div class="col-md-3">
						<pbl:inputText name="funcionariNom" inline="true" placeholderKey="auditor.list.filtre.funcionari.nom"/>
<%-- 					<c:set var="campErrors"><form:errors path="funcionariNom"/></c:set> --%>
<%-- 					<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="funcionariDocument"/></c:set></c:if> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<!-- 						<label>&nbsp;</label> -->
<!-- 						<div class="row"> -->
<!-- 							<div class="col-md-6"> -->
<%-- 								<c:set var="campPath" value="funcionariNom"/> --%>
<%-- 								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 								<spring:bind path="${campPath}"> --%>
<%-- 									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.funcionari.nom"/>"> --%>
<%-- 								</spring:bind> --%>
<!-- 							</div> -->
					</div>
					<div class="col-md-3">
					<pbl:inputText name="funcionariDocument" inline="true" placeholderKey="auditor.list.filtre.funcionari.document"/>
<%-- 								<c:set var="campPath" value="funcionariDocument"/> --%>
<%-- 								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 								<spring:bind path="${campPath}"> --%>
<%-- 									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.funcionari.document"/>"> --%>
<%-- 								</spring:bind> --%>
					</div>
					
					<div class="col-md-6">
						<div class="pull-right">
						<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
						<button class=" btn btn-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>
					</div>
					
					
				</div>
		</div>
	</div>	
			
			
		</form:form>
		
		<table id="table-consultes" class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
					<th data-data="scspPeticionId"><spring:message code="consulta.list.taula.peticion.id" /></th>
					<th data-data="creacioData"><spring:message code="auditor.list.taula.data" /></th>
					<th data-data="creacioUsuari.nom"><spring:message code="auditor.list.taula.usuari" /></th>
					<th data-data="funcionariNomAmbDocument"><spring:message code="auditor.list.taula.funcionari" /></th>
					<th data-data="procedimentNom"><spring:message code="auditor.list.taula.procediment" /></th>
					<th data-data="serveiDescripcio"><spring:message code="auditor.list.taula.servei" /></th>
					<th data-data="estat"><spring:message code="auditor.list.taula.estat" /></th>
				</tr>
			</thead>
		</table>
<script id="template-id-peticion" type="x-tmpl-mustache">
{{scspPeticionId}}
{{#recobriment}}
	<span class="badge">R</span>
{{/recobriment}}
</script>
<script id="template-estat" type="x-tmpl-mustache">
	{{{ icon-status }}} {{ estat }}
</script>
		<script type="text/javascript">
			function onInvokeAction(id) {
				setExportToLimit(id, '');
				createHiddenInputFieldsForLimitAndSubmit(id);
			}
		</script>
	</c:if>

</body>
</html>
