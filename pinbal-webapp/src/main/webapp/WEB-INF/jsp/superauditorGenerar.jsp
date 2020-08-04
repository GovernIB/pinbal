<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%	request.setAttribute(
			"consultaEstats",
			es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
%>
<html>
<head>
	<title><spring:message code="auditor.generar.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script> 
 	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
 	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script> 	
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
</head>
<body>

	<c:url value="/superauditor/generar" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="well" commandName="auditoriaGenerarCommand">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-3">
					<pbl:inputDate name="dataInici" inline="true" placeholderKey="auditor.generar.filtre.camp.data.inici"/>
				</div>
				<div class="col-md-3">	
					<pbl:inputDate name="dataFi" inline="true" placeholderKey="auditor.generar.filtre.camp.data.fi"/>
				</div>
				<div class="col-md-3">
					<pbl:inputText name="numEntitats" inline="true" placeholderKey="auditor.generar.filtre.camp.num.entitats"/>
				</div>
				<div class="col-md-3">
					<c:set var="campPath" value="numConsultes"/>
					<spring:message var="placeholderNumConsultes" code="auditor.generar.filtre.camp.num.consultes"/>
					<pbl:inputText name="${campPath}" inline="true"  placeholderKey="auditor.generar.filtre.camp.num.consultes"/>
				</div>
			</div>	
			<div class="row">
				<div class="col-md-12">
					<div class="pull-right">
						<button type="submit" class="btn btn-primary "><spring:message code="comu.boto.generar"/></button>
					</div>
				</div>
			</div>			
		</div>	
	</form:form>

	<c:if test="${not empty consultes}">
		<c:forEach var="consultesEntitat" items="${consultes}">
			<h3>${consultesEntitat.key.nom}</h3>	
			<c:set var="taulaId" value="consultes_${consultesEntitat.key.id}"/>
			<table id="${taulaId}" class="table table-striped table-bordered" style="width: 100%">
			<thead>
				<tr>
				<th><spring:message code="consulta.list.taula.peticion.id" /></th>
				<th><spring:message code="auditor.list.taula.data" /></th>
				<th><spring:message code="auditor.list.taula.usuari" /></th>
				<th><spring:message code="auditor.list.taula.funcionari.nom" /></th>
				<th><spring:message code="auditor.list.taula.funcionari.nif" /></th>
				<th><spring:message code="auditor.list.taula.procediment" /></th>
				<th><spring:message code="auditor.list.taula.servei" /></th>
				<th><spring:message code="auditor.list.taula.estat" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${consultesEntitat.value}" var="consulta">
		   			<tr>
						<td>
						${consulta.scspPeticionId}<c:if test="${consulta.recobriment}"> <span class="badge">R</span></c:if>
						</td>
						<td>
						${ consulta.creacioData }
						</td>
						<td>
						${ consulta.creacioUsuari.nom }
						</td>
						<td>
						${ consulta.funcionariNom }
						</td>
						<td>
						${ consulta.funcionariNif }
						</td>
						<td>
						${ consulta.procedimentNom }
						</td>
						<td>
						${ consulta.serveiDescripcio }
						</td>
						<td>
							<span<c:if test="${consulta.estatError}"> title="${consulta.error}"</c:if>>
								<c:choose>
									<c:when test="${consulta.estatPendent}"><i class="fas fa-bookmark"></i></c:when>
									<c:when test="${consulta.estatProcessant}"><i class="far fa-clock"></i></c:when>
									<c:when test="${consulta.estatError}"><i class="fas fa-exclamation-triangle"></i></c:when>
									<c:otherwise><i class="fas fa-check"></i></c:otherwise>
								</c:choose>
								${consulta.estat}
							</span>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			</table>
			<script type="text/javascript">
				function onInvokeAction(id) {
					setExportToLimit(id, '');
					createHiddenInputFieldsForLimitAndSubmit(id);
				}
			</script>
		</c:forEach>
		<a href="generarExcel" class="btn btn-primary"><i class="fas fa-file-download"></i>&nbsp;<spring:message code="auditor.list.exportar.excel"/></a>		
	</c:if>

</body>
</html>
