<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true" %>
<%@ attribute name="text" required="false" rtexprvalue="true" %>
<%@ attribute name="textKey" required="false" rtexprvalue="true" %>
<%@ attribute name="optionsModelKey" required="true" rtexprvalue="true" %>
<%@ attribute name="emptyOptionText" required="false" rtexprvalue="true" %>
<%@ attribute name="emptyOptionTextKey" required="false" rtexprvalue="true" %>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
	<label class="control-label" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
	</label>
	<div class="controls">
		<form:select path="${campPath}" cssClass="span12" id="${campPath}">
			<c:choose>
				<c:when test="${not empty emptyOptionTextKey}"><option value=""><spring:message code="${emptyOptionTextKey}"/></option></c:when>
				<c:when test="${not empty emptyOptionText}"><option value="">${emptyOptionText}</option></c:when>
			</c:choose>
			<form:options items="${requestScope[optionsModelKey]}"/>
		</form:select>
		<form:errors path="${campPath}" cssClass="help-inline"/>
	</div>
</div>