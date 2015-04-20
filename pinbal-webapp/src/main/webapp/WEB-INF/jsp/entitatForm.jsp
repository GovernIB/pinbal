<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
	request.setAttribute(
			"entitatTipusLlista",
			es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto.values());
%>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty entitatCommand.id}"><spring:message code="entitat.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="entitat.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
</head>
<body>

	<c:url value="/entitat/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="entitatCommand">
		<form:hidden path="id"/>
		<fieldset class="well">
			<c:set var="campPath" value="codi"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.codi"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			<c:set var="campPath" value="nom"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.nom"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			<c:set var="campPath" value="cif"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.cif"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			<c:set var="campPath" value="tipus"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="entitat.form.camp.tipus"/> *</label>
				<div class="controls">
					<form:select path="${campPath}">
						<form:options items="${entitatTipusLlista}"/>
					</form:select>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</fieldset>
		<div class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat"/>" class="btn"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

</body>
</html>
