<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<html>
<head>
	<title><spring:message code="usuari.codi.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
</head>
<body>
	<c:url value="/usuari/username" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariCodiCommand" role="form">
		<div class="row">
			<div class="col-md-6">
				<pbl:inputText name="codiAntic" textKey="usuari.form.camp.antic"/>
			</div>
			<div class="col-md-6">
				<pbl:inputText name="codiNou" textKey="usuari.form.camp.nou"/>
			</div>
		</div>
		<div id="modal-botons" class="pull-right">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-save"></span>
				<spring:message code="comu.boto.modificar"/>
			</button>
			<a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>
