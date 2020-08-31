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
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<div class="row">
			<div class="col-md-12">
				<pbl:inputText name="codi" required="true" labelSize="1" inline="false" textKey="procediment.form.camp.codi"/>
				<pbl:inputText name="nom" required="true" labelSize="1" inline="false" textKey="procediment.form.camp.nom"/>
				<pbl:inputText name="departament" labelSize="1" inline="false" textKey="procediment.form.camp.departament"/>
		
				<pbl:inputText name="organGestor" required="true" labelSize="1" inline="false" textKey="procediment.form.camp.organgestor"/>
				<pbl:inputText name="codiSia" labelSize="1" inline="false" textKey="procediment.form.camp.codisia"/>
		  		<div class="pull-right">
					 <button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
					 <a href="<c:url value="/procediment"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
				</div>
		 	</div>
		</div> 	 		
	</form:form>
</body>
</html>
