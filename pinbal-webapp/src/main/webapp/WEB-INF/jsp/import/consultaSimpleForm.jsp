<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
pageContext.setAttribute(
		"documentTipusValors",
		es.caib.pinbal.core.dto.ConsultaDto.getDocumentTipusValorsPerFormulari());
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
		<div class="row">	
			<div class="col-md-6">
				<label class="control-label" for="titularNom"><spring:message code="consulta.form.camp.nom"/></label>
				<pbl:inputText name="titularNom" inline="true"/>
			</div>		
			<div class="col-md-6">
				<label class="control-label" for="titularLlinatge1"><spring:message code="consulta.form.camp.llinatge1"/></label>
				<pbl:inputText name="titularLlinatge1" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">	
				<label class="control-label" for="titularLlinatge2"><spring:message code="consulta.form.camp.llinatge1"/></label>
				<pbl:inputText name="titularLlinatge2" inline="true"/>
			</div>
			<div class="col-md-6">	
				<label class="control-label" for="$titularNomComplet"><spring:message code="consulta.form.camp.nomcomplet"/></label>
				<pbl:inputText name="titularNomComplet" inline="true"/>
			</div>
		</div>			
		<c:set var="numColumnes" value="${2}"/>
		<c:set var="indexCamp" value="${0}"/>
		<c:forEach var="index" begin="0" end="3" varStatus="status">
			<c:choose>
				<c:when test="${index == 0}">
					
				</c:when>			
				<c:when test="${index == 1}">
											
				</c:when>
				<c:when test="${index == 2}">

				</c:when>						
				<c:when test="${index == 3}">

				</c:when>
			</c:choose>
			<c:if test="${servei[campServeiTest]}">
				<div class="col-md-6">
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}">${campLabel}</label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
				</div>
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
