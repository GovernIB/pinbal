<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty procedimentCommand.id}"><spring:message code="procediment.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="procediment.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
</head>
<body>

	<c:url value="/procediment/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<fieldset class="well">
			<c:set var="campPath" value="codi"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.codi"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			<c:set var="campPath" value="nom"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.nom"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			<c:set var="campPath" value="departament"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.departament"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</fieldset>
		<div class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/procediment"/>" class="btn"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

</body>
</html>
