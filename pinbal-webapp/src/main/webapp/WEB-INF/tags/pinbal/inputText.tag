<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true" %>
<%@ attribute name="text" required="false" rtexprvalue="true" %>
<%@ attribute name="textKey" required="false" rtexprvalue="true" %>
<%@ attribute name="placeholder" required="false" rtexprvalue="true" %>
<%@ attribute name="hideLabel" required="false" rtexprvalue="true" %>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
	<c:if test="${empty hideLabel}">
		<label class="control-label" for="${campPath}">
			<c:choose>
				<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
				<c:when test="${not empty text}">${text}</c:when>
				<c:otherwise>${campPath}</c:otherwise>
			</c:choose>
		</label>
	</c:if>
	<div class="controls">
		<c:choose>
			<c:when test="${not empty placeholder}">
				<spring:message var="placeholder" code="${placeholder}"/>
				<form:input path="${campPath}" cssClass="span12" id="${campPath}" placeholder="${placeholder}"/>
			</c:when>
			<c:otherwise>
				<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
			</c:otherwise>
		</c:choose>
		
		<form:errors path="${campPath}" cssClass="help-inline"/>
	</div>
</div>
