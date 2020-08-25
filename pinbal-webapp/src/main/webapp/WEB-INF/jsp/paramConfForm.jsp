<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${paramConfCommand.forcreate}"><spring:message code="paramconf.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="paramconf.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
</head>
<body>

	<c:url value="/scsp/paramconf/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="paramConfCommand">
	
	<form:hidden path="forcreate"/>
		<div class="row">
			<div class="col-md-12"> 
				<pbl:inputText name="nom" required="true" labelSize="1" inline="false" textKey="paramconf.form.camp.nom"/>
				<pbl:inputText name="valor" required="true" labelSize="1" inline="false" textKey="paramconf.form.camp.valor"/>
				<pbl:inputText name="descripcio" required="false" labelSize="1" inline="false" textKey="paramconf.form.camp.descripcio"/>
				<div class="pull-right">
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
					<a href="<c:url value="/scsp/paramconf"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>
		</div>		
		
	</form:form>

</body>
</html>
