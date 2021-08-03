<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty avisCommand.id}"><c:set var="titol"><spring:message code="avis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="avis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
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
	<c:set var="formAction"><c:url value="/avis/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="avisCommand" role="form">
		<form:hidden path="id"/>
		<pbl:inputText name="assumpte" textKey="avis.form.camp.assumpte" required="true"/>
		<pbl:inputTextarea name="missatge" textKey="avis.form.camp.missatge" required="true"/>
		<pbl:inputDate name="dataInici" textKey="avis.form.camp.dataInici" required="true"/>
		<pbl:inputDate name="dataFinal" textKey="avis.form.camp.dataFinal" required="true"/>
		<pbl:inputSelect name="avisNivell" textKey="avis.form.camp.avisNivell" optionEnum="AvisNivellEnumDto" required="true"/>
		
		<<div class="pull-right">
			<button type="submit" class="btn btn-primary"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/avis"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
