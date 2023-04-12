<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="nomNode" value="${nodeArbreActual.dades.nom}"/>
<li class="arbre-node" id="li-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}">
	<c:if test="${nodeArbreActual.dades.complexa}">
		<i class="fas fa-chevron-down"
			id="icon-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}"
			data-toggle="collapse"
			data-target="#ul-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}"></i>&nbsp;
		<c:choose>
			<c:when test="${nomNode == '__ALL'}"><spring:message code='servei.camp.complex.ALL' var="nomNode"/><c:set var="campComplexSenseNom" value="${true}"/></c:when>
			<c:when test="${nomNode == '__SEQUENCE'}"><spring:message code='servei.camp.complex.SEQUENCE' var="nomNode"/><c:set var="campComplexSenseNom" value="${true}"/></c:when>
			<c:when test="${nomNode == '__CHOICE'}"><spring:message code='servei.camp.complex.CHOICE' var="nomNode"/><c:set var="campComplexSenseNom" value="${true}"/></c:when>
		</c:choose>
	</c:if>
	<c:set var="campTrobat" value="${false}"/>
	<c:forEach var="camp" items="${camps}">
		<c:if test="${camp.path == nodeArbreActual.dades.pathAmbSeparadorDefault}"><c:set var="campTrobat" value="${true}"/></c:if>
	</c:forEach>
	<c:if test="${not campTrobat and not nodeArbreActual.dades.complexa}">
		<a href="#modal-element-afegir" onclick="serveiCampAfegir('${servei.codi}', '${nodeArbreActual.dades.pathAmbSeparadorDefault}')">
	</c:if>
	<c:if test="${nodeArbreActual.dades.complexa}">
		<c:choose>
			<c:when test="${nodeArbreActual.dades.tipusDadaComplexa == 'ALL'}"><span class="fas fa-expand" title="ALL: Es poden emplenar tots els camps, i l'ordre no importa"></span></c:when>
			<c:when test="${nodeArbreActual.dades.tipusDadaComplexa == 'CHOICE'}"><span class="fas fa-sitemap fa-rotate-270 fa-xs" title="CHOICE: Només s'ha d'emplenar un dels camps"></span></c:when>
			<c:when test="${nodeArbreActual.dades.tipusDadaComplexa == 'SEQUENCE'}"><span class="fas fa-ellipsis-h" title="SEQUENCE: Es poden emplenar tots els camps, i han d'aparèixer ordenats"></span></c:when>
			<c:otherwise><span style="width: 9px;"></span></c:otherwise>
		</c:choose>
	</c:if>
	<c:choose>
		<c:when test="${campTrobat}"><strong>${nomNode}</strong></c:when>
		<c:when test="${campComplexSenseNom}"><i>${nomNode}</i></c:when>
		<c:otherwise>${nomNode}</c:otherwise>
	</c:choose>
<%--	<c:if test="${nodeArbreActual.dades.complexa}">--%>
<%--		<c:choose>--%>
<%--			<c:when test="${nodeArbreActual.dades.tipusDadaComplexa == 'ALL'}"><span class="label label-info" style="position: relative;top: -2px;" title="ALL: Es poden emplenar tots els camps, i l'ordre no importa">A</span></c:when>--%>
<%--			<c:when test="${nodeArbreActual.dades.tipusDadaComplexa == 'CHOICE'}"><span class="label label-info" style="position: relative;top: -2px;" title="CHOICE: Només s'ha d'emplenar un dels camps">C</span></c:when>--%>
<%--			<c:when test="${nodeArbreActual.dades.tipusDadaComplexa == 'SEQUENCE'}"><span class="label label-info" style="position: relative;top: -2px;" title="SEQUENCE: Es poden emplenar tots els camps, i han d'aparèixer ordenats">S</span></c:when>--%>
<%--		</c:choose>--%>
<%--	</c:if>--%>
	<c:if test="${nodeArbreActual.dades.enumeracio}"><a id="enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" href="#" rel="tooltip" title="[<c:forEach var="valor" items="${nodeArbreActual.dades.enumeracioValors}" varStatus="status">${valor}<c:if test="${not status.last}">, </c:if></c:forEach>]"><i class="icon-list-alt"></i></a><script>$('#enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}').tooltip({placement:'right'});</script></c:if>
	<c:if test="${campTrobat}">&nbsp;<i class="fa fa-check"></i></c:if>
	<c:if test="${not campTrobat and not nodeArbreActual.dades.complexa}">
		</a>
	</c:if>
	<c:if test="${fn:length(nodeArbreActual.fills) > 0}">
		<ul id="ul-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" class="arbre-ul collapse in" style="list-style:none">
			<c:forEach var="node" items="${nodeArbreActual.fills}">
				<c:set var="nodeArbreActual" value="${node}" scope="request"/>
				<jsp:include page="dadesEspecifiquesArbreNode.jsp"/>
			</c:forEach>
		</ul>
	</c:if>
</li>