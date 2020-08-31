<%@page import="es.caib.pinbal.core.dto.EstadistiquesFiltreDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
	pageContext.setAttribute(
			"consultaEstats",
			es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
	pageContext.setAttribute(
			"agrupacioValors",
			es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto.values());
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.pinbal.webapp.common.RolHelper.isRolActualAdministrador(request));
%>

<html>
<head>
	<title><spring:message code="estadistiques.list.titol"/></title>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
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
	$('#select-procediment').change(function() {
		var targetUrl;
		if ($(this).val())
			targetUrl = '<c:url value="estadistiques/serveisPerProcediment"/>/' + $(this).val();
		else
			targetUrl = '<c:url value="estadistiques/serveisPerProcediment"/>';
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
});
</script>
<style>
.table th {
	text-align: center;
	vertical-align: middle !important;
}
.table td.numeric, .table th.numeric, .table td.total, .table th.total {
	text-align: right;
}
</style>
</head>
<body>

	<c:if test="${isRolActualAdministrador}">
		<c:set var="opcioEntitatTotes">&lt;&lt;<spring:message code="estadistiques.list.entitat.seleccio"/>&gt;&gt;</c:set>
		<c:choose>
			<c:when test="${empty estadistiquesFiltreCommand.entitatId}">
				<c:url value="/estadistiques/canviEntitat" var="formAction"/>
				<form:form action="${formAction}" method="post" cssClass="well form-filtre-table" commandName="estadistiquesFiltreCommand">
					<div class="row">
						<div class="col-md-2">
							<pbl:inputSelect name="entitatId" inline="true" 
											placeholderKey="estadistiques.list.entitat.seleccio" 
											optionMinimumResultsForSearch="5"
											optionItems="${entitats}"
											optionValueAttribute="id" 
											optionTextAttribute="nom" 
											emptyOption="true"/>
						</div>	
						<div class="col-md-3">
							<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.seleccionar"/></button>
						</div>	
					</div>	
				</form:form>
			</c:when>
			<c:otherwise>
				<form class="well form-filtre-table">
					<div class="row">
						<div class="col-md-2">
							<div class="form-group">
								<c:choose>
									<c:when test="${empty entitatSeleccionada}">
										<input type="text" class="form-control" value="${opcioEntitatTotes}" disabled="disabled"/>
									</c:when>
									<c:otherwise>
										<input type="text" class="form-control" value="${entitatSeleccionada.nom}" disabled="disabled"/>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="col-md-3">
							<a href="<c:url value="/estadistiques/canviEntitat"/>" class="btn btn-default"><spring:message code="comu.boto.canviar"/></a>
						</div>	
					</div>	
				</form>
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:if test="${not empty estadistiquesFiltreCommand.entitatId}">
		<form:form id="form-filtre" action="" method="post" cssClass="well form-filtre-table" commandName="estadistiquesFiltreCommand">
			<form:hidden path="entitatId"/>
			<div class="row">
				<div class="col-md-3">
					<pbl:inputSelect name="procediment" inline="true" placeholderKey="estadistiques.list.filtre.procediment"
						optionItems="${procediments}"  
						optionValueAttribute="id"  
						optionTextAttribute="nom"  
						emptyOption="true"/>
				</div>
				<div class="col-md-3">
					<pbl:inputSelect name="servei"  inline="true" placeholderKey="estadistiques.list.filtre.servei" 
 								 optionItems="${serveis}"  
 								 optionValueAttribute="codi" 
 								 optionTextAttribute="descripcio" 
 								 emptyOption="true"/> 
				</div>			
				<div class="col-md-3">
					<pbl:inputSelect name="estat"  inline="true" placeholderKey="estadistiques.list.filtre.estat"
								optionItems="${consultaEstats}"  
 								 emptyOption="true"/> 
				</div>
				<div class="col-md-3">
 					<pbl:inputSelect name="agrupacio"  inline="true" placeholderKey="estadistiques.list.filtre.agrupacio.SERVEI_PROCEDIMENT" 
	 	  							optionItems="${agrupacioValors}"    
	 	 							emptyOption="true"/>
				</div>
			</div>
			<div class="row">	
				<div class="col-md-3" >
					<div class="row">
						<div class="col-md-6" >
							<pbl:inputDate name="dataInici" inline="true" placeholderKey="estadistiques.list.filtre.data.inici"/>
						</div>		
						<div class="col-md-6" >
							<pbl:inputDate name="dataFi" inline="true" placeholderKey="estadistiques.list.filtre.data.fi"/>
						</div>		
					</div>		
				</div>									
				<div class="col-md-3">
					<c:set var="campPath" value="usuariCodi"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<c:set var="campPlaceholder"><spring:message code="estadistiques.list.filtre.usuari"/></c:set>
						<form:select path="${campPath}" cssClass="form-control" data-toggle="select2" data-placeholder="${campPlaceholder}">
							<option value=""></option>
							<c:forEach var="entitatUsuari" items="${entitatSeleccionada.usuaris}">
								<form:option value="${entitatUsuari.usuari.codi}">${entitatUsuari.usuari.descripcio}</form:option>
							</c:forEach>
						</form:select>
					</div>
				</div>
				<div class="col-md-6"> 
					<div class="pull-right">
						<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
						<button class="btn btn-primary" type="submit"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
					</div>
				</div>		
			</div>	
		</form:form>
		<c:if test="${not empty estadistiques}">
			<c:set var="estadistiques" value="${estadistiques}" scope="request"/>
			<jsp:include page="import/estadistiquesTaula.jsp">
				<jsp:param name="agrupacio" value="${estadistiquesFiltreCommand.agrupacio}"/>
			</jsp:include>
		</c:if>
		<c:if test="${not empty estadistiquesPerEntitat}">
			<c:forEach var="estadistica" items="${estadistiquesPerEntitat}">
				<c:choose>
					<c:when test="${empty estadistica.key}"><c:set var="estadisticaGlobal" value="${estadistica.value}"/></c:when>
					<c:otherwise>
						<h3><spring:message code="estadistiques.list.entitat"/>: ${estadistica.key.nom}</h3>
						<c:set var="estadistiques" value="${estadistica.value}" scope="request"/>
						<jsp:include page="import/estadistiquesTaula.jsp">
							<jsp:param name="agrupacio" value="${estadistiquesFiltreCommand.agrupacio}"/>
						</jsp:include>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<h3><spring:message code="estadistiques.list.totals"/></h3>
			<c:set var="estadistiques" value="${estadisticaGlobal}" scope="request"/>
			<jsp:include page="import/estadistiquesTaula.jsp">
				<jsp:param name="agrupacio" value="${estadistiquesFiltreCommand.agrupacio}"/>
			</jsp:include>
		</c:if>
		<a href="estadistiques/excel" class="btn btn-default"><i class="far fa-file-excel"></i>&nbsp;<spring:message code="estadistiques.list.exportar.excel"/></a>
	</c:if>

</body>
</html>
