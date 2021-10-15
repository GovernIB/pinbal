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
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
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
				<pbl:inputSelect name="organGestorId" textKey="procediment.form.camp.organgestor" 
								labelSize="1" inline="false" 
								emptyOption="true" emptyOptionTextKey="organgestor.form.camp.organ.opcio.cap"
								optionItems="${ organsGestors }" optionValueAttribute="id" optionTextAttribute="nom"
								required="true" optionMinimumResultsForSearch="5"/>
				<pbl:inputText name="codiSia" labelSize="1" inline="false" textKey="procediment.form.camp.codisia"/>
				<%--c:set var="campPath" value="valorCampAutomatizado"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="form-group">
					<label class="control-label col-md-1" for="${campPath}"></label>
					<div class="col-md-11">
						<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="procediment.form.camp.actiuCampAuto"/>
						<form:errors path="${campPath}" cssClass="help-block"/>
					</div>
				</div--%>
				<pbl:inputSelect name="valorCampAutomatizado" textKey="procediment.form.camp.actiuCampAuto" 
								emptyOption="true" emptyOptionTextKey="procediment.form.camp.actiuCampAuto.buit"
								optionItems="${ procedimentAutomatizadoOptions }" optionValueAttribute="value" optionTextKeyAttribute="text"
								required="false" labelSize="1" />
				<pbl:inputSelect name="valorCampClaseTramite" textKey="procediment.form.camp.claseTramite" 
								emptyOption="true" emptyOptionTextKey="procediment.form.camp.claseTramite.buit"
								optionItems="${ procedimentClaseTramiteOptions }" optionValueAttribute="value" optionTextKeyAttribute="text"
								required="false" labelSize="1" />
				<div class="pull-right">
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
					<a href="<c:url value="/procediment"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>
		</div>
	</form:form>
</body>
</html>
