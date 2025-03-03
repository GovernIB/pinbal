<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set var="titol"><spring:message code="integracio.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
</head>
<body>

	<c:if test="${not empty integracio}">
		<dl class="dl-horizontal">
			<dt><spring:message code="integracio.detall.camp.data"/></dt>
			<dd><fmt:formatDate value="${integracio.data}" pattern="dd/MM/yyyy HH:mm:ss" timeZone="Europe/Madrid"/></dd>
			<dt><spring:message code="integracio.detall.camp.descripcio"/></dt>
			<dd>${integracio.descripcio}</dd>
			<dt><spring:message code="integracio.detall.camp.tipus"/></dt>
			<dd>${integracio.tipus}</dd>
			<dt><spring:message code="integracio.list.columna.peticio.id"/></dt>
			<dd>${integracio.idPeticio}</dd>
			<dt><spring:message code="integracio.detall.camp.estat"/></dt>
			<dd>${integracio.estat}</dd>
			<c:if test="${not empty integracio.parametres}">
				<dt><spring:message code="integracio.detall.camp.params"/></dt>
				<dd>
					<ul>
						<c:forEach var="parametre" items="${integracio.parametres}">
							<li><strong>${parametre.nom}:</strong> ${parametre.descripcio}</li>
						</c:forEach>
					</ul>
				</dd>
			</c:if>
			<c:if test="${integracio.estat == 'ERROR'}">
				<dt><spring:message code="integracio.detall.camp.error.desc"/></dt>
				<dd>${integracio.errorDescripcio}</dd>
				<dt><spring:message code="integracio.detall.camp.excepcio.missatge"/></dt>
				<dd>${integracio.excepcioMessage}</dd>
			</c:if>
		</dl>
		<c:if test="${integracio.estat == 'ERROR' && not empty integracio.excepcioMessage}">
			<pre style="height:300px">${integracio.excepcioStacktrace}</pre>
		</c:if>
	</c:if>
	<div id="modal-botons">
		<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>

</body>