<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%
	request.setAttribute(
			"consultaEstats",
			es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
%>

<html>
<head>
	<title><spring:message code="superauditor.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
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
					<div class="col-md-2" width="">
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
			<form class="well-sm form-inline">
				<input type="text" class="input-sm" value="${entitatActual.nom}" disabled="disabled"/>
				<a href="<c:url value="/superauditor/entitat/deseleccionar"/>" class="btn-default"><spring:message code="comu.boto.canviar"/></a>
			</form>
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty entitatActual}">
		<form:form id="form-filtre" action="" method="post" cssClass="well" commandName="filtreCommand">
			<div class="page-header"><spring:message code="auditor.list.filtre.titol"/></div>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-3">
						<c:set var="campPath" value="scspPeticionId"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.peticion.id"/>">
							</spring:bind>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="procediment"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select id="select-procediment" path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="auditor.list.filtre.procediment"/>:</option>
								<form:options items="${procediments}" itemLabel="nom" itemValue="id"/>
							</form:select>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="servei"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select id="select-servei" path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="auditor.list.filtre.servei"/>:</option>
								<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/>
							</form:select>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="estat"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="auditor.list.filtre.estat"/>:</option>
								<c:forEach var="estat" items="${consultaEstats}">
									<c:if test="${not fn:startsWith(estat, 'P')}">
										<form:option value="${estat}">${estat}</form:option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
				</div>
			</div>
			<div class="container-fluid">	
				<div class="row">
					<div class="col-md-4">
						<c:set var="campErrors"><form:errors path="dataInici"/></c:set>
						<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if>
					
						<div class="form-gruop<c:if test="${not empty campErrors}"> error</c:if>">
							<label><spring:message code="auditor.list.filtre.data"/></label>
							<div class="row">
								<div class="col-md-6">
									<c:set var="campPath" value="dataInici"/>
									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
									<spring:bind path="${campPath}">
										<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.data.inici"/>">
										<script>$("#${campPath}").mask("99/99/9999");</script>
									</spring:bind>
								</div>
								<div class="col-md-6">
									<c:set var="campPath" value="dataFi"/>
									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
									<div class="form-gruop<c:if test="${not empty campErrors}"> error</c:if>">
										<spring:bind path="${campPath}">
											<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.data.fi"/>">
											<script>$("#${campPath}").mask("99/99/9999");</script>
										</spring:bind>
									</div>
								</div>
							</div>
						</div>
				</div>
					<div class="col-md-4">
					<c:set var="campErrors"><form:errors path="funcionariNom"/></c:set>
					<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="funcionariDocument"/></c:set></c:if>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label>&nbsp;</label>
						<div class="row">
							<div class="col-md-6">
								<c:set var="campPath" value="funcionariNom"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<spring:bind path="${campPath}">
									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.funcionari.nom"/>">
								</spring:bind>
							</div>
							<div class="col-md-6">
								<c:set var="campPath" value="funcionariDocument"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<spring:bind path="${campPath}">
									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.funcionari.document"/>">
								</spring:bind>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-4" style="text-align:right">
					<label>&nbsp;</label>
					<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button class=" btn btn-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>
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
