<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
es.caib.pinbal.core.dto.ServeiDto servei = (es.caib.pinbal.core.dto.ServeiDto)request.getAttribute("servei");
java.util.List<es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus> documentTipusValors = new java.util.ArrayList<es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus>();
if (servei.isPinbalPermesDocumentTipusDni()) {
	documentTipusValors.add(es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus.DNI);
}
if (servei.isPinbalPermesDocumentTipusNif()) {
	documentTipusValors.add(es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus.NIF);
}
if (servei.isPinbalPermesDocumentTipusCif()) {
	documentTipusValors.add(es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus.CIF);
}
if (servei.isPinbalPermesDocumentTipusNie()) {
	documentTipusValors.add(es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus.NIE);
}
if (servei.isPinbalPermesDocumentTipusPas()) {
	documentTipusValors.add(es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus.Passaport);
}
pageContext.setAttribute("documentTipusValors", documentTipusValors);
java.util.Map<?,?> map = (java.util.Map<?,?>)request.getAttribute("campsDadesEspecifiquesAgrupats");
if (map != null) {
	pageContext.setAttribute("campsSenseAgrupar", map.get(null));
}
%>
<div class="row">
	<div class="col-md-6">
		<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.expedient"/></label>
		<pbl:inputText name="expedientId" inline="true"/>
	</div>
</div>

<c:set var="mostrarDadesTitular" value="${servei.pinbalActiuCampNom or servei.pinbalActiuCampLlinatge1 or servei.pinbalActiuCampLlinatge2 or servei.pinbalActiuCampNomComplet or servei.pinbalActiuCampDocument}"/>
<c:if test="${mostrarDadesTitular}">
	<fieldset>
		<legend><spring:message code="consulta.form.dades.titular"/></legend>
		<c:if test="${servei.pinbalActiuCampDocument}">
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.document.tipus"/> <c:if test="${servei.pinbalDocumentObligatori}">*</c:if></label>
					<pbl:inputSelect name="titularDocumentTipus" inline="true"
									 optionItems="${documentTipusValors}" 
									 emptyOption="false"/>
				</div>
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="consulta.form.camp.document.num"/> <c:if test="${servei.pinbalDocumentObligatori}">*</c:if></label>
					<pbl:inputText name="titularDocumentNum" inline="true"/>
				</div>
			</div>
		</c:if>
		<c:set var="numColumnes" value="${2}"/>
		<c:set var="indexCamp" value="${0}"/>
		<c:forEach var="index" begin="0" end="3" varStatus="status">
			<c:choose>
				<c:when test="${index == 0}">
					<c:set var="campPath" value="titularNom"/>
					<spring:message var="campLabel" code="consulta.form.camp.nom"/>
					<c:set var="campServeiTest" value="pinbalActiuCampNom"/>
				</c:when>
				<c:when test="${index == 1}">
					<c:set var="campPath" value="titularLlinatge1"/>
					<spring:message var="campLabel" code="consulta.form.camp.llinatge1"/>
					<c:set var="campServeiTest" value="pinbalActiuCampLlinatge1"/>
				</c:when>
				<c:when test="${index == 2}">
					<c:set var="campPath" value="titularLlinatge2"/>
					<spring:message var="campLabel" code="consulta.form.camp.llinatge2"/>
					<c:set var="campServeiTest" value="pinbalActiuCampLlinatge2"/>
				</c:when>
				<c:when test="${index == 3}">
					<c:set var="campPath" value="titularNomComplet"/>
					<spring:message var="campLabel" code="consulta.form.camp.nomcomplet"/>
					<c:set var="campServeiTest" value="pinbalActiuCampNomComplet"/>
				</c:when>
			</c:choose>
			<c:if test="${servei[campServeiTest]}">
				<c:if test="${indexCamp == 0 or (indexCamp % numColumnes) == 0}">
					<div class="row">
				</c:if>
				<div class="col-md-6">
					<label class="control-label" for="titularNom">${campLabel}</label>
					<pbl:inputText name="${campPath}" inline="true"/>
				</div>
				<c:if test="${status.last or (indexCamp % numColumnes) == (numColumnes - 1)}">
					</div>
				</c:if>
				<c:set var="indexCamp" value="${indexCamp + 1}"/>
			</c:if>
		</c:forEach>
	</fieldset>
</c:if>
<c:set var="mostrarDadesEspecifiques" value="${false}"/>
<c:forEach var="camp" items="${campsDadesEspecifiques}">
	<c:if test="${camp.visible}"><c:set var="mostrarDadesEspecifiques" value="${true}"/></c:if>
</c:forEach>
<c:choose>
	<c:when test="${mostrarDadesEspecifiques}">
		<fieldset>
			<c:if test="${not empty campsSenseAgrupar}">
				<legend><spring:message code="consulta.form.dades.especifiques"/></legend>
				<div class="clearfix legend-margin-bottom"></div>
			</c:if>
			<c:set var="dadesEspecifiquesDisabled" value="${false}" scope="request"/>
			<c:set var="dadesEspecifiquesValors" value="${consultaCommand.dadesEspecifiques}" scope="request"/>
			<c:set var="campsPerMostrar" value="${campsSenseAgrupar}" scope="request"/>
			<jsp:include page="dadesEspecifiquesForm.jsp"/>
			<c:forEach var="grup" items="${grups}">
				<fieldset>
					<legend>${grup.nom}</legend>
					<div class="clearfix legend-margin-bottom"></div>
					<c:set var="dadesEspecifiquesDisabled" value="${false}" scope="request"/>
					<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[grup.id]}" scope="request"/>
					<jsp:include page="dadesEspecifiquesForm.jsp"/>
				</fieldset>
			</c:forEach>
		</fieldset>
	</c:when>
	<c:otherwise>
		<%-- No s'han de mostrar els camps de les dades específiques però s'han d'incloure com a camps ocults --%>
		<c:set var="dadesEspecifiquesValors" value="${consultaCommand.dadesEspecifiques}" scope="request"/>
		<jsp:include page="dadesEspecifiquesForm.jsp"/>
	</c:otherwise>
</c:choose>
