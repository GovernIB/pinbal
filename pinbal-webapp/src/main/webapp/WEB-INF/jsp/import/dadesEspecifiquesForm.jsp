<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>


<c:if test="${empty dadesEspecifiquesDisabled}"><c:set var="dadesEspecifiquesDisabled" value="${false}"/></c:if>
<c:set var="numColumnes" value="${2}"/>
<c:set var="indexCamp" value="${0}"/>

<c:forEach var="camp" items="${campsPerMostrar}" varStatus="status">
	<c:set var="campId" value="camp_${camp.id}"/>
	<c:set var="campCommandPath" value="dadesEspecifiques[${camp.path}]"/>
	<c:set var="campError"><form:errors path="${campCommandPath}"/></c:set>
	
	<c:set var="ruta" value="/${camp.path}"></c:set>
	<c:set var="valorDadaEspecifica" value="${dadesEspecifiquesValors[ruta]}"/>
	
	<c:if test="${not dadesEspecifiquesDisabled}"><c:set var="campValorDefecte">${camp.valorPerDefecte}</c:set></c:if>
	<c:if test="${dadesEspecifiquesDisabled}"><c:set var="campValorDefecte">${valorDadaEspecifica}</c:set></c:if>
	<c:if test="${pageContext.request.method == 'POST'}"><c:set var="campValorDefecte">${param[campId]}</c:set></c:if>

	<c:set var="dadaEspecifica" value="${null}"/>
	<c:forEach var="nodeDadaEspecifica" items="${llistaArbreDadesEspecifiques}">
		<c:if test="${nodeDadaEspecifica.dades.pathAmbSeparadorDefault == camp.path}">
			<c:set var="dadaEspecifica" value="${nodeDadaEspecifica.dades}"/>
		</c:if>
	</c:forEach>
	
	<c:choose>
		<c:when test="${camp.visible}">
			<c:if test="${indexCamp == 0 or (indexCamp % numColumnes) == 0}">
				<div class="row">
			</c:if>
			<div class="col-md-<fmt:formatNumber value="${12 / numColumnes}" maxFractionDigits="0"/>">
				<div class="form-group<c:if test="${not empty campError}"> error</c:if>">
	   				<label class="control-label" for="${campId}">
	   					<c:choose>
	   						<c:when test="${camp.tipus == 'ETIQUETA'}">&nbsp;</c:when>
	   						<c:when test="${not empty camp.etiqueta}">${camp.etiqueta}<c:if test="${not dadesEspecifiquesDisabled and camp.obligatori}"> *</c:if></c:when>
	   						<c:otherwise>${camp.campNom}<c:if test="${not dadesEspecifiquesDisabled and camp.obligatori}"> *</c:if></c:otherwise>
	   					</c:choose>
	   				</label>
					<c:choose>
						<c:when test="${camp.modificable}">
							<c:choose>
								<c:when test="${empty dadaEspecifica.enumeracioValors}">
									<c:choose>
										<c:when test="${camp.tipus == 'TEXT' or camp.tipus == 'DOC_IDENT'}">
											<input type="text" id="${campId}" name="${campId}"
												<c:if test="${not empty campValorDefecte}"> value="${campValorDefecte}"</c:if>
												<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if>
												<c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if>
												class="form-control" />
										</c:when>
										<c:when test="${camp.tipus == 'NUMERIC'}">
											<input type="text" id="${campId}" name="${campId}"
												<c:if test="${not empty campValorDefecte}"> value="${campValorDefecte}"</c:if>
												<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if>
												<c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if>
												class="form-control" />
											<script>$("#${campId}").mask("999999999999");</script>
										</c:when>
										<c:when test="${camp.tipus == 'DATA'}">
											<jsp:include page="dadesEspecifiquesFormInputDATA.jsp"/>
										</c:when>
										<c:when test="${camp.tipus == 'BOOLEA'}">
											<c:set var="campChecked" value="${false}"/>
											<c:if test="${not empty valorDadaEspecifica}"><c:set var="campChecked" value="${valorDadaEspecifica == 'on'}"/></c:if>
											<div class="checkbox">
												<input style="margin: 0;" type="checkbox" id="${campId}" name="${campId}"<c:if test="${campChecked}"> checked="checked"</c:if><c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if>/>
											</div>
											<input type="hidden" name="${campId}" value="off"/>
										</c:when>
										<c:when test="${camp.tipus == 'MUNICIPI_3' or camp.tipus == 'MUNICIPI_5'}">
											<jsp:include page="dadesEspecifiquesFormInputMUNICIPI.jsp"/>
										</c:when>
										<c:when test="${camp.tipus == 'PROVINCIA'}">
											<jsp:include page="dadesEspecifiquesFormInputPROVINCIA.jsp"/>
										</c:when>
										<c:when test="${camp.tipus == 'PAIS'}">
											<jsp:include page="dadesEspecifiquesFormInputPAIS.jsp"/>
										</c:when>
										<c:when test="${camp.tipus == 'ADJUNT_BINARI' or camp.tipus == 'ADJUNT_XML'}">
											<jsp:include page="dadesEspecifiquesFormInputADJUNT.jsp"/>
										</c:when>
										<c:when test="${camp.tipus == 'ETIQUETA'}"></c:when>
									</c:choose>
								</c:when>
   								<c:otherwise>
   									<c:set var="selectValue" value="${valorDadaEspecifica}"/>
   									<c:if test="${empty selectValue}"><c:set var="selectValue" value="${camp.valorPerDefecte}"/></c:if>
									<select id="${campId}" name="${campId}" data-minimumresults="-1" data-toggle="select2"
											<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if>
											class="form-control" data-toggle="select2">
										<c:if
											test="${dadesEspecifiquesDisabled or not camp.obligatori}">
											<option value=""><spring:message
													code="comu.opcio.sense.definir" /></option>
										</c:if>
										<c:forEach var="enumeracioValor"
											items="${dadaEspecifica.enumeracioValors}" varStatus="status">
											<option value="${enumeracioValor}"
												<c:if test="${enumeracioValor == selectValue}"> selected="selected"</c:if>>${camp.enumDescripcions[status.index]}</option>
										</c:forEach>
									</select>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<input type="text" id="${campId}" name="${campId}"<c:if test="${not empty camp.valorPerDefecte}"> value="${camp.valorPerDefecte}"</c:if> class="form-control" disabled="disabled"/>
							<input type="hidden" id="${campId}_hidden" name="${campId}"<c:if test="${not empty camp.valorPerDefecte}"> value="${camp.valorPerDefecte}"</c:if>/>
						</c:otherwise>
					</c:choose>
					<form:errors path="${campCommandPath}" cssClass="help-block"/>
					<c:if test="${not dadesEspecifiquesDisabled and not empty camp.comentari}"><span class="help-block">${camp.comentari}</span></c:if>
				</div>
			</div>
			<c:if test="${status.last or (indexCamp % numColumnes) == (numColumnes - 1)}">
				</div>
			</c:if>
			<c:set var="indexCamp" value="${indexCamp + 1}"/>
		</c:when>
		<c:otherwise>
			<input type="hidden" id="${campId}" name="${campId}"<c:if test="${not empty camp.valorPerDefecte}"> value="${camp.valorPerDefecte}"</c:if>/>
		</c:otherwise>
	</c:choose>
</c:forEach>
