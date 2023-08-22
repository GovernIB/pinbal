<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:forEach var="attributeName" items="${pageContext.request.attributeNames}">
	<c:if test="${not fn:contains(attributeName, '.') && fn:contains(attributeName, 'ommand')}">
		<spring:hasBindErrors name="${attributeName}">
			<c:if test="${not empty errors.globalErrors}">
				<c:forEach var="error" items="${errors.globalErrors}">
					<div class="alert alert-danger">
						<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
						<spring:message message="${error}"/>
					</div>
				</c:forEach>
			</c:if>
		</spring:hasBindErrors>
	</c:if>
</c:forEach>
<%
	request.setAttribute(
		"sessionErrors",
		session.getAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_ERROR));
%>
<c:forEach var="text" items="${sessionErrors}">
	<c:set var="msg" value="${text}" />
	<c:if test="${fn:contains(text, '|||')}">
		<c:set var="splitted" value="${fn:split(text, '|||')}" />
		<c:set var="msg" value="${splitted[0]}" />
		<c:set var="trace" value="${splitted[1]}" />
	</c:if>
	<div class="alert alert-danger">
		<button class="close" data-dismiss="alert" aria-label="close">&times;</button>
		${msg}
		<c:if test="${not empty trace}">
			<div id="trace-container">
				<a class="detall-trace-titol" data-toggle="collapse" href="#errtrace" aria-expanded="false" aria-controls="errtrace">
					<spring:message code="comu.error.detall" />
				</a>
				<div id=errtrace class="collapse">
					<div class="well">${trace}</div>
				</div>
			</div>
		</c:if>
	</div>
</c:forEach>
<%
	session.removeAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_ERROR);
%>

<%
	request.setAttribute(
		"sessionWarnings",
		session.getAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_WARNING));
%>
<c:forEach var="text" items="${sessionWarnings}">
	<div class="alert alert-warning">
		<button class="close" data-dismiss="alert">&times;</button>
		${text}
	</div>
</c:forEach>
<%
	session.removeAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_WARNING);
%>

<%
	request.setAttribute(
		"sessionSuccesses",
		session.getAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_SUCCESS));
%>
<c:forEach var="text" items="${sessionSuccesses}">
	<div class="alert alert-success">
		<button class="close" data-dismiss="alert">&times;</button>
		${text}
	</div>
</c:forEach>
<%
	session.removeAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_SUCCESS);
%>

<%
	request.setAttribute(
		"sessionInfos",
		session.getAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_INFO));
%>
<c:forEach var="text" items="${sessionInfos}">
	<div class="alert alert-info">
		<button class="close" data-dismiss="alert">&times;</button>
		${text}
	</div>
</c:forEach>
<%
session.getAttribute(es.caib.pinbal.webapp.common.AlertHelper.SESSION_ATTRIBUTE_INFO);
%>
