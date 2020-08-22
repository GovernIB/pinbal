<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<li class="arbre-node" id="li-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}">
	<c:if test="${nodeArbreActual.dades.complexa}">
		<i class="fas fa-chevron-down"
			id="icon-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}"
			data-toggle="collapse"
			data-target="#ul-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}"></i>&nbsp;
	</c:if>
	<c:set var="campTrobat" value="${false}"/>
	<c:forEach var="camp" items="${camps}">
		<c:if test="${camp.path == nodeArbreActual.dades.pathAmbSeparadorDefault}"><c:set var="campTrobat" value="${true}"/></c:if>
	</c:forEach>
	<c:if test="${not campTrobat}">
		<a href="#modal-element-afegir" onclick="serveiCampAfegir('${servei.codi}', '${nodeArbreActual.dades.pathAmbSeparadorDefault}')">
	</c:if>
	<c:choose>
		<c:when test="${campTrobat}">
			<strong>${nodeArbreActual.dades.nom}</strong>
		</c:when>
		<c:otherwise>
			${nodeArbreActual.dades.nom}
		</c:otherwise>
	</c:choose>
	<c:if test="${nodeArbreActual.dades.enumeracio}"><a id="enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" href="#" rel="tooltip" title="[<c:forEach var="valor" items="${nodeArbreActual.dades.enumeracioValors}" varStatus="status">${valor}<c:if test="${not status.last}">, </c:if></c:forEach>]"><i class="icon-list-alt"></i></a><script>$('#enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}').tooltip({placement:'right'});</script></c:if>
	<c:if test="${campTrobat}">&nbsp;<i class="fa fa-check"></i></c:if>
	<c:if test="${not campTrobat}">
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