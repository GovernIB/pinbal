<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

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
	<form:form action="${formAction}" method="post" cssClass="well" commandName="procedimentCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<fieldset>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-3">
					<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.codi"/> *</label>
					<pbl:inputText name="codi" inline="true" placeholderKey="procediment.form.camp.codi"/>
<%-- 					<c:set var="campPath" value="codi"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
				
<!-- 				<div class="controls"> -->
<%-- 					<form:input path="${campPath}" cssClass="input-sm" id="${campPath}"/> --%>
<%-- 					<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 				</div> -->
				</div>
			</div>
			<div class="row">
				<div class="col-md-3">
				<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.nom"/> *</label>
					<pbl:inputText name="nom" inline="true" placeholderKey="procediment.form.camp.nom"/>
			
<%-- 			<c:set var="campPath" value="nom"/> --%>
<%-- 			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
				
<!-- 				<div class="controls"> -->
<%-- 					<form:input path="${campPath}" cssClass="form-control" id="${campPath}"/> --%>
<%-- 					<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 				</div> -->
				</div>
			</div>
			<div class="row">
				<div class="col-md-3">	
				<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.departament"/> *</label>
					<pbl:inputText name="departament" inline="true" placeholderKey="procediment.form.camp.departament"/>
<%-- 					<c:set var="campPath" value="departament"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="procediment.form.camp.departament"/> *</label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="input-sm" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
			  	</div>
			</div>
		</div>
		</fieldset>
		
		<div clas="container-fluid">
			<div class="row">
				<div class="col-md-12">			
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
					<a href="<c:url value="/procediment"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>
		</div>	
	</form:form>

</body>
</html>
