<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<table id="estadistiques" class="table table-striped table-bordered">
	<thead>
		<tr>
			<c:choose>
				<c:when test="${param.agrupacio == 'PROCEDIMENT_SERVEI'}">
					<th rowspan="2"><spring:message code="estadistiques.list.taula.procediment"/></th>
					<th rowspan="2"><spring:message code="estadistiques.list.taula.servei"/></th>
				</c:when>
				<c:otherwise>
					<th rowspan="2"><spring:message code="estadistiques.list.taula.servei"/></th>
					<th rowspan="2"><spring:message code="estadistiques.list.taula.procediment"/></th>
				</c:otherwise>
			</c:choose>
			<th colspan="2"><spring:message code="estadistiques.list.taula.tramitades"/></th>
			<th colspan="2"><spring:message code="estadistiques.list.taula.errors"/></th>
			<th rowspan="2"><spring:message code="estadistiques.list.taula.total"/></th>
		</tr>
		<tr>
			<th><spring:message code="estadistiques.list.taula.recobriment"/></th>
			<th><spring:message code="estadistiques.list.taula.web"/></th>
			<th><spring:message code="estadistiques.list.taula.recobriment"/></th>
			<th><spring:message code="estadistiques.list.taula.web"/></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="totalRecobrimentOk" value="${0}"/>
		<c:set var="totalRecobrimentError" value="${0}"/>
		<c:set var="totalWebUIOk" value="${0}"/>
		<c:set var="totalWebUIError" value="${0}"/>
		<c:forEach var="estadistica" items="${estadistiques}" varStatus="status">
			<c:if test="${estadistica.conteSumatori and not status.first}">${htmlTotal}</c:if>
			<c:if test="${estadistica.conteSumatori}">
				<c:set var="htmlTotal"><tr>
					<td class="total muted"><strong><spring:message code="estadistiques.list.taula.subtotal"/></strong></td>
					<td class="numeric text-info"><strong>${estadistica.sumatoriNumRecobrimentOk}</strong></td>
					<td class="numeric text-info"><strong>${estadistica.sumatoriNumWebUIOk}</strong></td>
					<td class="numeric text-info"><strong>${estadistica.sumatoriNumRecobrimentError}</strong></td>
					<td class="numeric text-info"><strong>${estadistica.sumatoriNumWebUIError}</strong></td>
					<td class="numeric text-info"><strong>${estadistica.sumatoriNumTotal}</strong></td>
				</tr></c:set>
			</c:if>
			<tr>
				<c:if test="${estadistica.conteSumatori}">
					<c:choose>
						<c:when test="${param.agrupacio == 'PROCEDIMENT_SERVEI'}"><td rowspan="${estadistica.sumatoriNumRegistres + 1}">${estadistica.procedimentNomAmbDepartament}</td></c:when>
						<c:otherwise><td rowspan="${estadistica.sumatoriNumRegistres + 1}">${estadistica.serveiNom}</td></c:otherwise>
					</c:choose>
				</c:if>
				<c:choose>
					<c:when test="${param.agrupacio == 'PROCEDIMENT_SERVEI'}"><td>${estadistica.serveiNom}</td></c:when>
					<c:otherwise><td>${estadistica.procedimentNomAmbDepartament}</td></c:otherwise>
				</c:choose>
				<td class="numeric">${estadistica.numRecobrimentOk}</td>
				<td class="numeric">${estadistica.numWebUIOk}</td>
				<td class="numeric">${estadistica.numRecobrimentError}</td>
				<td class="numeric">${estadistica.numWebUIError}</td>
				<td class="numeric">${estadistica.numTotal}</td>
			</tr>
			<c:if test="${status.last}">${htmlTotal}</c:if>
			<c:set var="totalRecobrimentOk" value="${totalRecobrimentOk + estadistica.numRecobrimentOk}"/>
			<c:set var="totalWebUIOk" value="${totalWebUIOk + estadistica.numWebUIOk}"/>
			<c:set var="totalRecobrimentError" value="${totalRecobrimentError + estadistica.numRecobrimentError}"/>
			<c:set var="totalWebUIError" value="${totalWebUIError + estadistica.numWebUIError}"/>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<th></th>
			<th class="total"><spring:message code="estadistiques.list.taula.total"/></th>
			<th class="numeric text-success">${totalRecobrimentOk}</th>
			<th class="numeric text-success">${totalWebUIOk}</th>
			<th class="numeric text-success">${totalRecobrimentError}</th>
			<th class="numeric text-success">${totalWebUIError}</th>
			<th class="numeric text-success">${totalRecobrimentOk + totalRecobrimentError + totalWebUIOk + totalWebUIError}</th>
		</tr>
	</tfoot>
</table>