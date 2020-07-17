<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code="auditor.generar.titol"/></title>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
</head>
<body>

	<c:url value="/auditor/generar" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="well-lg form-inline" commandName="auditoriaGenerarCommand">
		<form:hidden path="numEntitats"/>
		<c:set var="campPath" value="dataInici"/>
		<spring:message var="placeholderDataInici" code="auditor.generar.filtre.camp.data.inici"/>
		<form:input path="${campPath}" cssClass="input-lg" id="${campPath}" placeholder="${placeholderDataInici}"/>
		<script>$("#${campPath}").mask("99/99/9999");</script>
		<c:set var="campPath" value="dataFi"/>
		<spring:message var="placeholderDataFi" code="auditor.generar.filtre.camp.data.fi"/>
		<form:input path="${campPath}" cssClass="input-lg" id="${campPath}" placeholder="${placeholderDataFi}"/>
		<script>$("#${campPath}").mask("99/99/9999");</script>
		<c:set var="campPath" value="numConsultes"/>
		<spring:message var="placeholderNumConsultes" code="auditor.generar.filtre.camp.num.consultes"/>
		<form:input path="${campPath}" cssClass="input-lg" id="${campPath}" placeholder="${placeholderNumConsultes}"/>
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.generar"/></button>
	</form:form>

	<c:if test="${not empty consultes}">
			<table id="table-users" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
			<th><spring:message code="consulta.list.taula.peticion.id" /></th>
			<th><spring:message code="auditor.list.taula.data" /></th>
			<th><spring:message code="auditor.list.taula.usuari" /></th>
			<th><spring:message code="auditor.list.taula.funcionari" /></th>
			<th><spring:message code="auditor.list.taula.procediment" /></th>
			<th><spring:message code="auditor.list.taula.servei" /></th>
			<th><spring:message code="auditor.list.taula.estat" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${consultes}" var="consulta">
	   			<tr>
					<td>
					${consulta.scspPeticionSolicitudId}<c:if test="${consulta.recobriment}"> <span class="badge">R</span></c:if>
					</td>
					<td>
					${ consulta.creacioData }
					</td>
					<td>
					${ consulta.creacioUsuari.nom }
					</td>
					<td>
					${ consulta.funcionariNomAmbDocument }
					</td>
					<td>
					${ consulta.procedimentNom }
					</td>
					<td>
					${ consulta.serveiDescripcio }
					</td>
					<td>
<%-- 						<span<c:if test="${consulta.estatError}"> title="${consulta.error}"</c:if>> --%>
							<c:choose>
								<c:when test="${consulta.estatPendent}"><i class="fas fa-bookmark"></i></i></c:when>
								<c:when test="${consulta.estatProcessant}"><i class="fas fa-times"></i></c:when>
								<c:when test="${consulta.estatError}"><i class="fas fa-exclamation-triangle"></i></c:when>
								<c:otherwise><i class="fas fa-check"></i></c:otherwise>
							</c:choose>
							${consulta.estat}
<!-- 						</span> -->
					</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
		<a href="generarExcel" class="btn-default"><i class="glyphicon-download-alt"></i>&nbsp;<spring:message code="auditor.list.exportar.excel"/></a>
	</form>
	<script type="text/javascript">
		function onInvokeAction(id) {
			setExportToLimit(id, '');
			createHiddenInputFieldsForLimitAndSubmit(id);
		}
	</script>
	</c:if>

</body>
</html>
