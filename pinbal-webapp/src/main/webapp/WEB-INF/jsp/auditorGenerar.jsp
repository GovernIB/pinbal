<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code="auditor.generar.titol"/></title>
	<script type="text/javascript"src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
</head>
<body>

	<c:url value="/auditor/generar" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="well form-inline" commandName="auditoriaGenerarCommand">
		<form:hidden path="numEntitats"/>
		<c:set var="campPath" value="dataInici"/>
		<spring:message var="placeholderDataInici" code="auditor.generar.filtre.camp.data.inici"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderDataInici}"/>
		<script>$("#${campPath}").mask("99/99/9999");</script>
		<c:set var="campPath" value="dataFi"/>
		<spring:message var="placeholderDataFi" code="auditor.generar.filtre.camp.data.fi"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderDataFi}"/>
		<script>$("#${campPath}").mask("99/99/9999");</script>
		<c:set var="campPath" value="numConsultes"/>
		<spring:message var="placeholderNumConsultes" code="auditor.generar.filtre.camp.num.consultes"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderNumConsultes}"/>
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.generar"/></button>
	</form:form>

	<c:if test="${not empty consultes}">
	<form>
		<jmesa:tableModel
				id="consultes"
				items="${consultes}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="scspPeticionId" titleKey="consulta.list.taula.peticion.id" sortable="false">
						${registre.scspPeticionSolicitudId}<c:if test="${registre.recobriment}"> <span class="badge">R</span></c:if>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="creacioData" titleKey="auditor.list.taula.data" cellEditor="org.jmesa.view.editor.DateCellEditor" pattern="dd/MM/yyyy HH:mm:ss" sortable="false"/>
					<jmesa:htmlColumn property="creacioUsuari.nom" titleKey="auditor.list.taula.usuari"/>
					<jmesa:htmlColumn property="funcionariNomAmbDocument" titleKey="auditor.list.taula.funcionari"/>
					<jmesa:htmlColumn property="procedimentNom" titleKey="auditor.list.taula.procediment" sortable="false"/>
					<jmesa:htmlColumn property="serveiDescripcio" titleKey="auditor.list.taula.servei" sortable="false"/>
					<jmesa:htmlColumn property="estat" titleKey="auditor.list.taula.estat" style="white-space:nowrap;" sortable="false">
						<span<c:if test="${registre.estatError}"> title="${registre.error}"</c:if>>
							<c:choose>
								<c:when test="${registre.estatPendent}"><i class="icon-bookmark"></i></c:when>
								<c:when test="${registre.estatProcessant}"><i class="icon-time"></i></c:when>
								<c:when test="${registre.estatError}"><i class="icon-warning-sign"></i></c:when>
								<c:otherwise><i class="icon-ok"></i></c:otherwise>
							</c:choose>
							${registre.estat}
						</span>
					</jmesa:htmlColumn>
				</jmesa:htmlRow>
			</jmesa:htmlTable>
		</jmesa:tableModel>
		<a href="generarExcel" class="btn"><i class="icon-download-alt"></i>&nbsp;<spring:message code="auditor.list.exportar.excel"/></a>
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
