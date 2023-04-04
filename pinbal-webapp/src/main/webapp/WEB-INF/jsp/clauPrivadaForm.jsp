<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<link href="<c:url value="/css/clau-privada.css"/>" rel="stylesheet" type="text/css"/>

	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<script>
		var eyeEnabled = true;
		
		$(document).ready(function() {
			$('#btn-eye').click(function() {
				if(eyeEnabled) {
					eyeEnabled = false;
					$('#password').get(0).type = 'text';
					$("#icon-eye").attr('class', 'icon-eye-close');
				} else {
					eyeEnabled = true;
					$('#password').get(0).type = 'password';
					$("#icon-eye").attr('class', 'icon-eye-open');
				}
			});
			
			$('#btn-calendar-alta').click(function() {
				$('#dataAlta').focus();
			});
			
			$('#btn-calendar-baixa').click(function() {
				$('#dataBaixa').focus();
			});
		});
	</script>
	
	<title>
		<c:choose>
			<c:when test="${empty clauPrivadaCommand.id}"><spring:message code="clau.privada.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="clau.privada.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	
</head>
<body>

	<c:url value="/modal/scsp/clauprivada/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="clauPrivadaCommand">
	
		<form:hidden path="id"/>	
		<fieldset>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-12">
					<pbl:inputText name="alies" required="true" labelSize="1" inline="false" textKey="clau.privada.form.camp.alies"/>
					<pbl:inputText name="nom" required="true" labelSize="1" inline="false" textKey="clau.privada.form.camp.nom"/>
					<pbl:inputText name="password" required="true" labelSize="1" inline="false" textKey="clau.privada.form.camp.password"/>
					<pbl:inputText name="numSerie" required="true" labelSize="1" inline="false" textKey="clau.privada.form.camp.numeroserie"/>
					<pbl:inputDate name="dataAlta" required="true" labelSize="1" inline="false" textKey="clau.privada.form.camp.data.alta"/>
					<pbl:inputDate name="dataBaixa" required="false" labelSize="1" inline="false" textKey="clau.privada.form.camp.data.baixa"/>

					<c:set var="campPath" value="interoperabilitat"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label col-md-1" for="${campPath}"><spring:message code="servei.form.camp.pinbal.document.obligatori"/></label>
						<div class="col-md-11">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
					<pbl:inputSelect name="organisme" required="true" labelSize="1" inline="false" textKey="clau.privada.form.camp.organisme"
								optionItems="${organismes}"
								optionValueAttribute="id"
								optionTextAttribute="cadenaIdentificadora"
								emptyOption="true"
								emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
			</div>
		</div>
			<%--					<div class="pull-right">--%>
		<div id="modal-botons">
			<button type="submit" class="btn btn-primary" ><spring:message code="comu.boto.guardar" /></button>
				<%--						<a href="<c:url value="/scsp/clauprivada"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>--%>
			<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
		</fieldset>
	</form:form>

</body>
</html>
