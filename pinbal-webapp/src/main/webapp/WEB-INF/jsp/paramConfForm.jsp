<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

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
		
		<fieldset class="well">
		
			<div class="col-md-12"></div>
			
			<div class="col-md-5"> 
				<c:set var="campPath" value="nom"/>
				<c:set var="forcreate" value="${paramConfCommand.forcreate}"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
					<label class="control-label" for="${campPath}"><spring:message code="paramconf.form.camp.nom"/> *</label>
					<div class="controls">
						<form:input path="${campPath}" cssClass="input-lg" id="${campPath}" readonly="${!forcreate}"/>
						<form:errors path="${campPath}" cssClass="help-block"/>
					</div>
				</div>
			</div>
			
			<div class="col-md-10">
				<c:set var="campPath" value="valor"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
					<label class="control-label" for="${campPath}"><spring:message code="paramconf.form.camp.valor"/> *</label>
					<div class="controls">
						<form:input path="${campPath}" cssClass="input-lg" id="${campPath}" style="width: calc(100% - 40px);"/>
						<form:errors path="${campPath}" cssClass="help-block"/>
					</div>
				</div>
			</div>
			
			<div class="col-md-10">
				<c:set var="campPath" value="descripcio"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
					<label class="control-label" for="${campPath}"><spring:message code="paramconf.form.camp.descripcio"/>  </label>
					<div class="controls">
						<form:input path="${campPath}" cssClass="input-lg" id="${campPath}" style="width: calc(100% - 40px);"/>
						<form:errors path="${campPath}" cssClass="help-bloc"/>
					</div>
				</div>
			</div>
			
		</fieldset>
		
		<div class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/scsp/paramconf"/>" class="btn-default"><spring:message code="comu.boto.cancelar"/></a>
		</div>
		
	</form:form>

</body>
</html>
