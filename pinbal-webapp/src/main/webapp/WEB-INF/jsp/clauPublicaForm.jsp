<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<link href="<c:url value="/css/my-datepicker.css"/>" rel="stylesheet" type="text/css"/>
	
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	<title>
		<c:choose>
			<c:when test="${empty clauPublicaCommand.id}"><spring:message code="claupublica.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="claupublica.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	
</head>
<body>

	<c:url value="/scsp/claupublica/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="clauPublicaCommand">
	
		<form:hidden path="id"/>
		
		<fieldset class="well">
		
			<c:set var="campPath" value="alies"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="claupublica.form.camp.alies"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			
			<c:set var="campPath" value="nom"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="claupublica.form.camp.nom"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>

			<c:set var="campPath" value="numSerie"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="claupublica.form.camp.numserie"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			
						
			<c:set var="campPath" value="dataAlta"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="claupublica.form.camp.dataalta"/> *</label>
				<div class="controls">
					<form:input
						path="${campPath}"
						cssClass="form-control datepicker my-datepicker"
						id="${campPath}"
						disabled="false"
						data-toggle="datepicker"
						data-idioma="${idioma}"/>
					<span class="input-group-addon" style="width:100%"><i class="icon-calendar"></i></span>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>

			<c:set var="campPath" value="dataBaixa"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="claupublica.form.camp.databaixa"/>  </label>
				<div class="controls">
					<form:input
						path="${campPath}"
						cssClass="form-control datepicker my-datepicker"
						id="${campPath}"
						disabled="false"
						data-toggle="datepicker"
						data-idioma="${idioma}"/>
					<span class="input-group-addon" style="width:100%"><i class="icon-calendar"></i></span>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
			
		</fieldset>
		
		<div class="well">
			<button type="submit" class="btn btn-primary" ><spring:message code="comu.boto.guardar" /></button>
			<a href="<c:url value="/scsp/claupublica"/>" class="btn"><spring:message code="comu.boto.cancelar"/></a>
		</div>
		
	</form:form>

</body>
</html>
