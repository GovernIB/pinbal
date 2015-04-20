<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<li class="arbre-node" id="li-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}">
	<c:if test="${nodeArbreActual.dades.complexa}">
		<i class="icon-chevron-down" id="icon-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" data-toggle="collapse" data-target="#ul-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}"></i>&nbsp;
	</c:if>
	<c:choose>
		<c:when test="${nodeArbreActual.dades.complexa}">
			<strong>${nodeArbreActual.dades.nom}</strong>
		</c:when>
		<c:otherwise>
			<c:set var="campTrobat" value="${false}"/>
			<c:forEach var="camp" items="${camps}">
				<c:if test="${camp.path == nodeArbreActual.dades.pathAmbSeparadorDefault}"><c:set var="campTrobat" value="${true}"/></c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${campTrobat}">
					${nodeArbreActual.dades.nom}<c:if test="${nodeArbreActual.dades.enumeracio}"><a id="enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" href="#" rel="tooltip" title="[<c:forEach var="valor" items="${nodeArbreActual.dades.enumeracioValors}" varStatus="status">${valor}<c:if test="${not status.last}">, </c:if></c:forEach>]"><i class="icon-list-alt"></i></a><script>$('#enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}').tooltip({placement:'right'});</script></c:if>&nbsp;<i class="icon-ok"></i>
				</c:when>
				<c:otherwise>
					<a href="#modal-element-afegir" onclick="serveiCampAfegir('${servei.codi}', '${nodeArbreActual.dades.pathAmbSeparadorDefault}')">
						${nodeArbreActual.dades.nom}<c:if test="${nodeArbreActual.dades.enumeracio}"><a id="enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" href="#" rel="tooltip" title="[<c:forEach var="valor" items="${nodeArbreActual.dades.enumeracioValors}" varStatus="status">${valor}<c:if test="${not status.last}">, </c:if></c:forEach>]"><i class="icon-list-alt"></i></a><script>$('#enum-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}').tooltip({placement:'right'});</script></c:if>
					</a>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	<c:if test="${fn:length(nodeArbreActual.fills) > 0}">
		<ul id="ul-${nodeArbreActual.dades.pathAmbSeparadorAlternatiu}" class="arbre-ul collapse in" style="list-style:none">
			<c:forEach var="node" items="${nodeArbreActual.fills}">
				<c:set var="nodeArbreActual" value="${node}" scope="request"/>
				<jsp:include page="dadesEspecifiquesArbreNode.jsp"/>
			</c:forEach>
		</ul>
	</c:if>
</li>