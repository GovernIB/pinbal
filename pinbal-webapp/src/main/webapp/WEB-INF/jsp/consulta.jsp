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

	<title><spring:message code="consulta.list.titol"/></title>	

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
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	
	
	
	
	
</head>
<body>
		<form:form id="form-filtre" action="" method="post" cssClass="well" commandName="filtreCommand" >
		<div class="container-fluid" >
			<div class ="row">	
				<div class="col-md-3">
				<pbl:inputText name="scspPeticionId"  inline="true" placeholderKey="consulta.list.filtre.peticion.id"/>
<%-- 					<c:set var="campPath" value="scspPeticionId"/>					 --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<spring:bind path="${campPath}"> --%>
<%-- 							<input class="form-control" type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> placeholder="<spring:message code="consulta.list.filtre.peticion.id"/>"> --%>
<%-- 						</spring:bind> --%>
<!-- 					</div> -->
				</div>
				<div class="col-md-3">
				<pbl:inputSelect name="procediment"  inline="true" placeholderKey="consulta.list.filtre.procediment" 
								 optionItems="${procediments}" 
								 optionValueAttribute="id"
								 optionTextAttribute="nom"
								 emptyOption="true"/>
<%-- 					<c:set var="campPath" value="procediment"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<form:select id="select-procediment" path="${campPath}" cssClass="form-control"> --%>
<%-- 							<option value=""><spring:message code="consulta.list.filtre.procediment"/>:</option> --%>
<%-- 							<form:options items="${procediments}" itemLabel="nom" itemValue="id"/> --%>
<%-- 						</form:select> --%>
				</div>
		
				<div class="col-md-3">
					<pbl:inputSelect name="servei"  inline="true" placeholderKey="consulta.list.filtre.servei" 
									 optionItems="${serveis}" 
									 optionValueAttribute="codi"
									 optionTextAttribute="descripcio"
									 emptyOption="true"/>
<%-- 					<c:set var="campPath" value="servei"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}">error</c:if>"> --%>
<%-- 						<form:select id="select-servei" path="${campPath}" cssClass="form-control"> --%>
<%-- 							<option value=""><spring:message code="consulta.list.filtre.servei"/>:</option> --%>
<%-- 							<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/> --%>
<%-- 						</form:select> --%>
<!-- 					</div> -->
				</div>
				<div class="col-md-3">
					<pbl:inputSelect name="estat"  inline="true" placeholderKey="consulta.list.filtre.estat" 
									 optionItems="${consultaEstats}" 
									 emptyOption="true"/>
<%-- 					<c:set var="campPath" value="estat"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}">error</c:if>"> --%>
<%-- 						<form:select path="${campPath}" cssClass="form-control"> --%>
<%-- 							<option value=""><spring:message code="consulta.list.filtre.estat"/>:</option> --%>
<%-- 							<c:forEach var="estat" items="${consultaEstats}"> --%>
<%-- 								<c:if test="${not fn:startsWith(estat, 'P')}"> --%>
<%-- 									<form:option value="${estat}">${estat}</form:option> --%>
<%-- 								</c:if> --%>
<%-- 							</c:forEach> --%>
<%-- 						</form:select> --%>
<!-- 					</div> -->
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-12">
						<c:set var="campErrors"><form:errors path="dataInici"/></c:set>
						<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<label><spring:message code="consulta.list.filtre.data"/></label>
							<c:set var="campErrors"><form:errors path="titularNom"/></c:set>
							<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="titularDocument"/></c:set></c:if>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
<!-- 							 <label>&nbsp;</label> -->
							</div>	
						</div>
				</div>							
				<div class="col-md-2" >
						<pbl:inputDate name="dataInici"  inline="true" placeholderKey="consulta.list.filtre.data.inici"/> 
<%--  									<spring:bind path="${campPath}">  --%>
<%--  										<input class="form-control" type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-6" placeholder="<spring:message code="consulta.list.filtre.data.inici"/>">  --%>
<!-- 										<script>$("#${campPath}").mask("99/99/9999");</script> -->
<%-- 									</spring:bind> --%>
				</div>
				<div class="col-md-2">
							<pbl:inputDate name="dataFi"  inline="true" placeholderKey="consulta.list.filtre.data.fi"/>
<%-- 									<c:set var="campPath" value="dataFi"/> --%>
<%-- 									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
									
<%-- 										<spring:bind path="${campPath}"> --%>
<%-- 											<input class="form-control" type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-6" placeholder="<spring:message code="consulta.list.filtre.data.fi"/>"> --%>
<!-- 											<script>$("#${campPath}").mask("99/99/9999");</script> -->
<%-- 										</spring:bind> --%>
							</div>
				</div>	
							
				<div class="col-md-2">
							<c:set var="campPath" value="titularNom"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input class="form-control" type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="consulta.list.filtre.titular.nom"/>">
							</spring:bind>
				</div>
				<div class="col-md-2">
							<c:set var="campPath" value="titularDocument"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input class="form-control" type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="consulta.list.filtre.titular.document"/>">
								</spring:bind>
				</div>
							
			
				<div class="col-md-2" style="text-align:right">
						<label>&nbsp;</label>
						<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
						<button class="btn btn-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
				
		</div>

		</form:form>

	<div class="clearfix"></div>
	<table id="table-consultes" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="scspPeticionId"><spring:message code="consulta.list.taula.peticion.id" /></th>
				<th data-data="creacioData"><spring:message code="consulta.list.taula.data" /></th>
				<th data-data="procedimentNom"><spring:message code="consulta.list.taula.procediment" /></th>
				<th data-data="serveiDescripcio"><spring:message code="consulta.list.taula.servei" /></th>
				<th data-data="titularNomSencer"><spring:message code="consulta.list.taula.titular.nom" /></th>
				<th data-data="titularDocumentAmbTipus"><spring:message code="consulta.list.taula.titular.document" /></th>				
				<th data-data="estat"><spring:message code="consulta.list.taula.estat" /></th>
				<th data-data="id"></th>
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>
<script type="text/javascript">
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
			targetUrl = '<c:url value="consulta/serveisPermesosPerProcediment"/>/' + $(this).val();
		else
			targetUrl = '<c:url value="consulta/serveisPermesosPerProcediment"/>';
		$.ajax({
		    url:targetUrl,
		    type:'GET',
		    dataType: 'json',
		    success: function(json) {
		    	$('#select-servei').empty();
	        	$('#select-servei').append($('<option value="">').text('<spring:message code="consulta.list.filtre.servei"/>:'));
		        $.each(json, function(i, value) {
		            $('#select-servei').append($('<option>').text(value.descripcio).attr('value', value.codi));
		        });
		    }
		});
	});
	$('.justificant-reintentar').click(function() {
		var $link = $(this);
		setTimeout(function(){$link.attr('href', '#')}, 100);
	});
	

    $('#table-consultes').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/consulta/datatable/"/>',
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
				orderable: false,
				render: $.fn.dataTable.render.moment('x', 'DD/MM/YYYY HH:mm:ss', 'es' )
			},	
			{
				targets: [2, 3, 4, 5],
				width: "10%",
				orderable: false,
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
			}, 
			{
				targets: [7],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-justificant').html();
						row["estat-pendent"] = row['justificantEstat'] == 'pendent';
						row["estat-ok"] = row['justificantEstat'] == 'ok';
						row["estat-error"] = row['justificantEstat'] == 'error';
						row["estat-nodisponible"] = row['justificantEstat'] == 'no_disponible';
						row["estat-oknocustodia"] = row['justificantEstat'] == 'ok_no_custodia';
						return Mustache.render(template, row);
				}
			},
			{
				targets: [8],
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

<script id="template-justificant" type="x-tmpl-mustache">
{{#estat-pendent}}
<i class="far fa-clock" title="<spring:message code="consulta.list.taula.justif.pendent"/>"></i>
<i class="icon-time" title="<spring:message code="consulta.list.taula.justif.pendent"/>"></i>
{{/estat-pendent}}
{{#estat-ok}}
<a class="btn btn-small" href="consulta/{{ id }}/justificant">
<i class="far fa-file-pdf" title="<spring:message code="consulta.list.taula.descarregar.pdf"/>" 
			 alt="<spring:message code="consulta.list.taula.descarregar.pdf"/>"></i>
</a>
{{/estat-ok}}
{{#estat-error}}
<div class="btn-group">
	<a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="#">
		<i class="icon-"><img src="<c:url value="/img/error_icon.png"/>" width="16" height="15" title="<spring:message code="consulta.list.taula.justif.error"/>" alt="<spring:message code="consulta.list.taula.justif.error"/>"/></i>
		&nbsp;<span class="caret"></span>
	</a>
	<ul class="dropdown-menu">
		<li><a href="consulta/{{ id }}/justificantError" data-toggle="modal" data-target="#modal-justificant-error-{{ id }}"><i class="icon-info-sign"></i>&nbsp;<spring:message code="consulta.list.taula.justif.error.veure"/></a></li>
		<li><a href="consulta/{{ id }}/justificantReintentar" class="justificant-reintentar"><i class="icon-repeat"></i>&nbsp;<spring:message code="consulta.list.taula.justif.error.reintentar"/></a></li>
	</ul>
</div>
{{/estat-error}}
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
<a href="consulta/{{ id }}" class="btn btn-default"><i class="fas fa-search-plus"></i>&nbsp;<spring:message code="admin.consulta.list.taula.detalls"/></a>
</script>
<script type="text/javascript">
	function onInvokeAction(id) {
		setExportToLimit(id, '');
		createHiddenInputFieldsForLimitAndSubmit(id);
	}
</script>

<c:forEach var="consulta" items="${consultes}">
	<c:if test="${consulta.justificantEstatError}">
		<div class="modal hide fade" id="modal-justificant-error-${consulta.id}" style="width:900px;margin-left:-450px;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><spring:message code="consulta.list.taula.justif.error"/></h3>
			</div>
			<div class="modal-body">
				<textarea style="width:98%" rows="18">${consulta.justificantError}</textarea>
			</div>
			<div class="modal-footer">
			</div>
		</div>
	</c:if>
</c:forEach>

</body>
</html>
