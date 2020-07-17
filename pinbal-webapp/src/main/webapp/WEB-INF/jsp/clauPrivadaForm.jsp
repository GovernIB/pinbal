<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<link href="<c:url value="/css/clau-privada.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/my-datepicker.css"/>" rel="stylesheet" type="text/css"/>
	
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
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

	<c:url value="/scsp/clauprivada/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="clauPrivadaCommand">
	
		<form:hidden path="id"/>
		
		<fieldset class="well">
		
		<div class="col-md-12"></div>
		
		<div class="col-md-10">
			<c:set var="campPath" value="alies"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.alies"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="col-md-12 input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</div>
		
		<div class="col-md-10">
			<c:set var="campPath" value="nom"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.nom"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="col-md-12 input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</div>
		
		<div class="col-md-10">
			<c:set var="campPath" value="password"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.password"/> *</label>
				<div class="controls">
					<div class="input-append" style="width: 100%;">
						<form:input path="${campPath}" cssClass="col-md-12 input-xlarge" id="${campPath}" type="password" style="width: calc(100% - 40px);"/>
						<button id="btn-eye" class="btn" type="button"><i id="icon-eye" class="icon-eye-open"></i></button>
					</div>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</div>
		
		<div class="col-md-10">
			<c:set var="campPath" value="numSerie"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.numeroserie"/> *</label>
				<div class="controls">
					<form:input path="${campPath}" cssClass="col-md-12 input-xlarge" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-block"/>
				</div>
			</div>
		</div>
		
		<div class="col-md-5">
			<c:set var="campPath" value="dataAlta"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.data.alta"/> *</label>
				<div class="controls">
					<div class="input-append" style="width: 100%;">
						<form:input
							path="${campPath}"
							cssClass="form-control datepicker"
							id="${campPath}"
							disabled="false"
							data-toggle="datepicker"
							data-idioma="${idioma}"
							style="width: calc(100% - 40px);"/>
						<button id="btn-calendar-alta" class="btn" type="button"><i class="icon-calendar"></i></button>
					</div>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</div>
		
		<div class="col-md-5">
			<c:set var="campPath" value="interoperabilitat"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.interoperabilitat"/>  </label>
				<div class="controls">
					<form:checkbox path="${campPath}" cssClass="my-checkbox" id="${campPath}"/>
					<form:errors path="${campPath}" cssClass="help-inline"/>
				</div>
			</div>
		</div>
		
		<div class="col-md-5">
			<c:set var="campPath" value="dataBaixa"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.data.baixa"/>  </label>
				<div class="controls">
					<div class="input-append" style="width: 100%;">
						<form:input
							path="${campPath}"
							cssClass="form-control datepicker"
							id="${campPath}"
							disabled="false"
							data-toggle="datepicker"
							data-idioma="${idioma}"
							style="width: calc(100% - 40px);"/>
						<button id="btn-calendar-baixa" class="btn" type="button"><i class="glyphicon-calendar"></i></button>
					</div>
					<form:errors path="${campPath}" cssClass="help-block"/>
				</div>
			</div>
		</div>
			
		<div class="col-md-5">
			<c:set var="campPath" value="organisme"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
				<label class="control-label" for="${campPath}"><spring:message code="clau.privada.form.camp.organisme"/>  </label>
				<div class="controls">
					<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}">
						<form:options items="${organismes}" itemLabel="cadenaIdentificadora" itemValue="id"/>
					</form:select>
					<form:errors path="${campPath}" cssClass="help-block"/>
				</div>
			</div>
		</div>
						
		</fieldset>
		
		<div class="well">
			<button type="submit" class="btn btn-primary" ><spring:message code="comu.boto.guardar" /></button>
			<a href="<c:url value="/scsp/clauprivada"/>" class="btn"><spring:message code="comu.boto.cancelar"/></a>
		</div>
		
	</form:form>

</body>
</html>
