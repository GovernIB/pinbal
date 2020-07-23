<%@page import="es.caib.pinbal.core.dto.EstadistiquesFiltreDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	<title><spring:message code="admin.consulta.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	
	
	
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

</head>
<body>

	<c:choose>
		<c:when test="${empty entitatActual}">
			<form action="<c:url value="/admin/consulta/entitat/seleccionar"/>" method="post" class="well">
				<div class="container-fluid">
					<div class="row">
						<div class="col-md-3">
							<label for="id_label_multiple" class="col-md-12">
							
<%-- 							<pbl:inputSelect name="entitat.id" inline="true"  placeholderKey="admin.consulta.list.entitat.seleccio" --%>
<%-- 								optionItems="${entitats}" --%>
<%-- 								optionTextKeyAttribute="${entitat.id}" --%>
<%-- 								optionTextAttribute="${entitat.id}" --%>
<%-- 								emptyOption="true"/> --%>
							
			  					<select class="form-control" name="entitatId" id="select-entitat" data-toggle="select2"  data-netejar="${netejar}"  data-minimumresults="4" data-enum-value="entitatId">
									<option value=""><spring:message code="admin.consulta.list.entitat.seleccio"/></option>
								<c:choose>
									<c:when test="${not empty entitats}">
										<c:forEach var="entitat" items="${entitats}">
											<option value="${entitat.id}">${entitat.nom}</option>
										</c:forEach>
									</c:when>
									<c:otherwise><form:options/></c:otherwise>
								</c:choose>
								</select>
							</label>
						</div>
						<div class="col-md-3">
							<button type="submit" class="btn btn-primary" style="height: 36px;"><spring:message code="comu.boto.seleccionar"/></button>
						</div>
					</div>
				</div>				
			</form>
		</c:when>
		<c:otherwise>
			<form class="well">
				<input type="text" class="input-sm" value="${entitatActual.nom}" disabled="disabled"/>
				<a href="<c:url value="/admin/consulta/entitat/deseleccionar"/>" class="btn-default"><spring:message code="comu.boto.canviar"/></a>
			</form>
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty entitatActual}">
		<form:form id="form-filtre" action="" method="post" cssClass="well" commandName="filtreCommand">
<%-- 			<div class="page-header"><spring:message code="admin.consulta.list.filtre.titol"/></div> --%>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-3">
						<pbl:inputText name="scspPeticionId" inline="true" placeholderKey="admin.consulta.list.filtre.peticion.id"/>
<%-- 						<c:set var="campPath" value="scspPeticionId"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<spring:bind path="${campPath}"> --%>
<%-- 								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="admin.consulta.list.filtre.peticion.id"/>"> --%>
<%-- 							</spring:bind> --%>
					</div>
					
					<div class="col-md-3">
 						 <pbl:inputSelect name="procediment" inline="true" placeholderKey="admin.consulta.list.filtre.procediment" 
	 						 optionItems="${procediments}"   
		 						 optionValueAttribute="id"
		 						 optionTextAttribute="nom" 
								 emptyOption="true"/> 
						 
<%-- 						<c:set var="campPath" value="procediment"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group"<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<form:select id="select-procediment" path="${campPath}" cssClass="form-control col-md-12"> --%>
<%-- 								<option value=""><spring:message code="admin.consulta.list.filtre.procediment"/>:</option> --%>
<%-- 								<form:options items="${procediments}" itemLabel="nom" itemValue="id"/> --%>
<%-- 							</form:select> --%>
<%-- 						</div> --%>
					</div>
					<div class="col-md-3">
						<pbl:inputSelect name="servei" inline="true" placeholderKey="admin.consulta.list.filtre.servei"
						 optionItems="${serveis}"
						 optionValueAttribute="codi"
		 				 optionTextAttribute="descripcio" 
						 emptyOption="true"/>
<%-- 						<c:set var="campPath" value="servei"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group"<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<form:select id="select-servei" path="${campPath}" cssClass="form-control col-md-12"> --%>
<%-- 								<option value=""><spring:message code="admin.consulta.list.filtre.servei"/>:</option> --%>
<%-- 								<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/> --%>
<%-- 							</form:select> --%>
<!-- 						</div> -->
					</div>
					<div class="col-md-3">
					
						<pbl:inputSelect name="estat" inline="true" placeholderKey="admin.consulta.list.filtre.estat"
						optionItems="${consultaEstats}"
						emptyOption="true"/>
<%-- 						<c:set var="campPath" value="estat"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group"<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<form:select path="${campPath}" cssClass="form-control col-md-12"> --%>
<%-- 								<option value=""><spring:message code="admin.consulta.list.filtre.estat"/>:</option> --%>
<%-- 								<c:forEach var="estat" items="${consultaEstats}"> --%>
<%-- 									<c:if test="${not fn:startsWith(estat, 'P')}"> --%>
<%-- 										<form:option value="${estat}">${estat}</form:option> --%>
<%-- 									</c:if> --%>
<%-- 								</c:forEach> --%>
<%-- 							</form:select> --%>
						</div>
					</div>
				
				<div class="row">				
					<div class="col-md-2">
						<pbl:inputDate name="dataInici" inline="true" placeholderKey="admin.consulta.list.filtre.data"/>
<%-- 						<c:set var="campErrors"><form:errors path="dataInici"/></c:set> --%>
<%-- 						<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if> --%>
<%-- 						<div class="col-md-12"<c:if test="${not empty campErrors}">error</c:if>"> --%>
<%-- 							<label><spring:message code="admin.consulta.list.filtre.data"/></label> --%>
<!-- 						</div> -->
					</div>	
						
<!-- 				<div class="col-md-2"> -->
<%-- 						<c:set var="campPath" value="dataInici"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<spring:bind path="${campPath}">  --%>
<%-- 							<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="auditor.list.filtre.data.inici"/>">  --%>
<!-- 							<script>$("#${campPath}").mask("99/99/9999");</script> -->
<%-- 						</spring:bind> --%>
<!-- 				</div>																 -->
					<div class="col-md-2">
					
							<pbl:inputDate name="dataFi" inline="true" placeholderKey="auditor.list.filtre.data.fi"/>
<%-- 						<c:set var="campPath" value="dataFi"/> --%>
				
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<spring:bind path="${campPath}"> --%>
<%-- 								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="auditor.list.filtre.data.fi"/>"> --%>
<!-- 								<script>$("#${campPath}").mask("99/99/9999");</script> -->
<%-- 							</spring:bind> --%>
<!-- 						</div>	 -->
					</div>					
				</div>																			
				<div class=row>
<!--  					<div class="col-md-3">  -->
<%-- 						<c:set var="campErrors"><form:errors path="funcionariNom"/></c:set> --%>
<%-- 						<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="funcionariDocument"/></c:set></c:if> --%>
<%-- 						<div class="form-group"<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<!-- 							<label>&nbsp;</label> -->
<!-- 						</div> -->
<!-- 					</div> -->
				
					
				<div class="col-md-2">
					<pbl:inputText name="funcionariNom" inline="true" placeholderKey="admin.consulta.list.filtre.funcionari.nom"/>
<%-- 						<c:set var="campPath" value="funcionariNom"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<spring:bind path="${campPath}"> --%>
<%-- 							<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="admin.consulta.list.filtre.funcionari.nom"/>"> --%>
<%-- 						</spring:bind> --%>
				</div>
				<div class="col-md-2">	
					<pbl:inputText name="funcionariDocument" inline="true" placeholderKey="admin.consulta.list.filtre.funcionari.document"/>
<%-- 						<c:set var="campPath" value="funcionariDocument"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<spring:bind path="${campPath}"> --%>
<%-- 							<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control col-md-12" placeholder="<spring:message code="admin.consulta.list.filtre.funcionari.document"/>"> --%>
<%-- 						</spring:bind> --%>
				</div>
				<div class="col-md-2 pull-right">
					<div class="pull-right">
<!-- 							<label>&nbsp;</label> -->
							<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
							<button class="btn btn-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>		
					</div>
				</div>
		</div>	
	</div>		
		</form:form>

		<div class="clearfix"></div>
		<table id="table-consultes" class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
					<th data-data="scspPeticionId"><spring:message code="admin.consulta.list.taula.peticion.id" /></th>
					<th data-data="creacioData"><spring:message code="admin.consulta.list.taula.data" /></th>
					<th data-data="creacioUsuari.nom"><spring:message code="admin.consulta.list.taula.usuari" /></th>
					<th data-data="funcionariNomAmbDocument"><spring:message code="admin.consulta.list.taula.funcionari" /></th>
					<th data-data="procedimentNom"><spring:message code="admin.consulta.list.taula.procediment" /></th>
					<th data-data="serveiDescripcio"><spring:message code="admin.consulta.list.taula.servei" /></th>
					<th data-data="estat"><spring:message code="admin.consulta.list.taula.estat" /></th>
					<th data-data="id"></th>
				</tr>
			</thead>
		</table>
		<script type="text/javascript">
			function onInvokeAction(id) {
				setExportToLimit(id, '');
				createHiddenInputFieldsForLimitAndSubmit(id);
			}
		</script>
	</c:if>
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
		$("#select-entitat").select2();
		
	    $('#table-consultes').DataTable({
	    	autoWidth: false,
			processing: true,
			serverSide: true,
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>'
	        },
			ajax: '<c:url value="/admin/consulta/datatable/"/>',
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
					width: "6%",
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
					width: "1%",
					render: function (data, type, row, meta) {
							var template = $('#template-details').html();
							return Mustache.render(template, row);
					}
				},
				{
					targets: [3, 5],
					orderable: false,
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
<a href="consulta/{{ id }}" class="btn btn-default"><i class="fas fa-search-plus"></i>&nbsp;<spring:message code="admin.consulta.list.taula.detalls"/></a>
</script>

</body>
</html>
