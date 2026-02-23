<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="procediment.serveis.migrar.titol" arguments="${procediment.nom},${serveiCodi}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
</head>
<body>
	<c:set var="formAction"><c:url value="/modal/procediment/${procediment.id}/servei/${serveiCodi}/migrar"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="procedimentServeiMigrarCommand" role="form">
		<form:hidden path="procedimentId"/>
		<form:hidden path="serveiCodiOriginal"/>
		<pbl:inputSelect
                name="serveiCodiDesti"
                placeholderKey="admin.consulta.list.filtre.servei"
                textKey="procediment.serveis.migrar.servei"
                optionItems="${serveis}"
                optionTextKeyAttribute="codiNom"
                optionValueAttribute="codi"
                optionMinimumResultsForSearch="0"
                required="true"/>

        <div style="min-height: 300px;"></div>
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-primary"><span class="fa fa-suitcase"></span> <spring:message code="procediment.serveis.taula.boto.migrar"/></button>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>
