<%@page import="es.caib.pinbal.core.dto.EstadistiquesFiltreDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

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
				<form:form action="${formAction}" method="post" cssClass="well well-sm form-block" commandName="estadistiquesFiltreCommand">
					<c:set var="campPath" value="entitatId"/>
					<form:select id="entitat_entitatId" path="${campPath}" cssClass="input-sm">
						<option value="-1">${opcioEntitatTotes}</option>
						<form:options items="${entitats}" itemLabel="nom" itemValue="id"/>
					</form:select>
					<button type="submit" class="btn"><spring:message code="comu.boto.seleccionar"/></button>
				</form:form>
			</c:when>
			<c:otherwise>
				<form class="well-sm">
					<c:choose>
						<c:when test="${empty entitatSeleccionada}"><input type="text" class="input-lg" value="${opcioEntitatTotes}" disabled="disabled"/></c:when>
						<c:otherwise><input type="text" class="input-sm" value="${entitatSeleccionada.nom}" disabled="disabled"/></c:otherwise>
					</c:choose>
					<a href="<c:url value="/estadistiques/canviEntitat"/>" class="btn"><spring:message code="comu.boto.canviar"/></a>
				</form>
			</c:otherwise>
		</c:choose>
	</c:if>
	<c:if test="${not empty estadistiquesFiltreCommand.entitatId}">
		<c:choose>
			<c:when test="${empty entitatSeleccionada}"><c:set var="spanClass" value="col-md-6"/></c:when>
			<c:otherwise><c:set var="spanClass" value="col-md-4"/></c:otherwise>
		</c:choose>
		<form:form id="form-filtre" action="" method="post" cssClass="well-lg formbox" commandName="estadistiquesFiltreCommand">
			<form:hidden path="entitatId"/>
			<div class="page-header"><spring:message code="estadistiques.list.filtre.titol"/></div>
			<div class="container-fluid">
				<div class="row">
					<c:if test="${not empty entitatSeleccionada}">
						<div class="${spanClass}">
							<c:set var="campPath" value="procediment"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
								<form:select id="select-procediment" path="${campPath}" cssClass="form-control">
									<option value=""><spring:message code="estadistiques.list.filtre.procediment"/>:</option>
									<form:options items="${procediments}" itemLabel="nom" itemValue="id"/>
								</form:select>
							</div>
						</div>
					</c:if>
					<div class="${spanClass}">
						<c:set var="campPath" value="servei"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select id="select-servei" path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="estadistiques.list.filtre.servei"/>:</option>
								<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/>
							</form:select>
						</div>
					</div>
					<div class="${spanClass}">
						<c:set var="campPath" value="estat"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="estadistiques.list.filtre.estat"/>:</option>
								<c:forEach var="estat" items="${consultaEstats}">
									<c:if test="${not fn:startsWith(estat, 'P')}">
										<form:option value="${estat}">${estat}</form:option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
				</div>
				
					<div class="col-md-3">
						<c:set var="campErrors"><form:errors path="dataInici"/></c:set>
						<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<label><spring:message code="estadistiques.list.filtre.data"/></label>
							<div class="container-fluid">
								<div class="col-md-6">
									<c:set var="campPath" value="dataInici"/>
									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
									<spring:bind path="${campPath}">
										<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="estadistiques.list.filtre.data.inici"/>">
										<script>$("#${campPath}").mask("99/99/9999");</script>
									</spring:bind>
								</div>
								<div class="col-md-6">
									<c:set var="campPath" value="dataFi"/>
									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
									<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
										<spring:bind path="${campPath}">
											<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="estadistiques.list.filtre.data.fi"/>">
											<script>$("#${campPath}").mask("99/99/9999");</script>
										</spring:bind>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-4"> 
						<c:set var="campPath" value="usuariCodi"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<label>&nbsp;</label>
							<form:select path="${campPath}" cssClass="col-md-12">
								<option value=""><spring:message code="estadistiques.list.filtre.usuari"/>:</option>
								<c:forEach var="entitatUsuari" items="${entitatSeleccionada.usuaris}">
									<form:option value="${entitatUsuari.usuari.codi}">${entitatUsuari.usuari.descripcio}</form:option>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="agrupacio"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<label>Agrupar per</label>
							<form:select path="${campPath}" cssClass="col-md-12">
								<c:forEach var="agrupacio" items="${agrupacioValors}">
									<form:option value="${agrupacio}"><spring:message code="estadistiques.list.filtre.agrupacio.${agrupacio}"/></form:option>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="col-md-2" style="text-align:right">
						<label>&nbsp;</label>
						<button id="netejar-filtre" class="btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
						<button class="btn-default btn-default-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>
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
		<a href="estadistiques/excel" class="btn-default"><i class="glyphicon-download-alt"></i>&nbsp;<spring:message code="estadistiques.list.exportar.excel"/></a>
	</c:if>

</body>
</html>
