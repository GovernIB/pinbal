<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
	pageContext.setAttribute("isRolActualAdministrador", es.caib.pinbal.webapp.common.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute("isRolActualSuperauditor", es.caib.pinbal.webapp.common.RolHelper.isRolActualSuperauditor(request));
%>
<c:url value="/usuariajax/usuari/extern/" var="urlConsultaUsuariExtern"/>
<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty entitatUsuariCommand.codi}"><spring:message code="representant.usuaris.titol.crear"/></c:when>
			<c:otherwise><spring:message code="representant.usuaris.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

	<script>
		function getUserData(codi) {
			$.ajax({
				url: '${urlConsultaUsuariExtern}' + codi,
				type: 'GET',
				success: function (resposta) {
					$('#codi_').val(resposta.codi);
					$('#nom').val(resposta.nom);
					$('#nif').val(resposta.nif);
					$('#email').val(resposta.email);
				},
				error: function (error) {
					console.error(error);
				}
			});
		}

		$(document).ready(function() {
			$('#codi').change(function() {
				const codi = $(this).val();
				if (codi) {
					getUserData(codi);
				} else {
					$('#codi_').val();
					$('#nom').val();
					$('#nif').val();
					$('#email').val();
				}
			});
			if (${not empty entitatUsuariCommand.codi}) {
				getUserData('${entitatUsuariCommand.codi}');
				$('#codi').prop('disabled', true);
				$('form#entitatUsuariCommand').submit(function(e) {
					e.preventDefault();
					$('#codi').prop('disabled', false);
					this.submit();
				});
			}
		});
	</script>
</head>
<body>
	<c:url value="/usuariajax/usuari" var="urlConsultaUsuari"/>
	<c:url value="/usuariajax/usuari/externs" var="urlConsultaUsuaris"/>
	<c:url value="/modal/auditor/usuari/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="entitatUsuariCommand" role="form">
		<pbl:inputSuggest
				name="codi"
				urlConsultaInicial="${urlConsultaUsuari}"
				urlConsultaLlistat="${urlConsultaUsuaris}"
				textKey="admin.consulta.list.filtre.usuari"
				suggestValue="codi"
				suggestText="nom"
				inline="false"
				labelSize="2"
				comment="usuari.form.camp.codi.comment"/>

<%--		<pbl:inputText name="codi" textKey="representant.usuaris.filtre.camp.codi" labelSize="2" readonly="true"/>--%>
		<div class="form-group">
			<label class="control-label col-md-2" for="codi_"><spring:message code="representant.usuaris.filtre.camp.codi"/></label>
			<div class="controls col-md-10"><input type="text" id="codi_" class="form-control" readonly="readonly" value="${entitatUsuariCommand.codi}"/></div>
		</div>
		<form:hidden path="id" />
		<form:hidden path="nom" />
		<pbl:inputText name="nif" textKey="usuari.form.camp.nif" labelSize="2" readonly="true"/>
		<pbl:inputText name="email" textKey="usuari.form.camp.email" labelSize="2" readonly="true"/>
		<pbl:inputText name="departament" textKey="usuari.form.camp.departament" labelSize="2"/>

		<div class="form-group">
			<label class="control-label col-md-2"><spring:message code="representant.usuaris.camp.rols"/></label>
			<div class="col-md-10" style="margin-top: 0px; padding-left: 35px;">
				<label class="checkbox" for="rolAuditor">
					<form:checkbox path="rolAuditor" id="rolAuditor" name="rolAuditor" />
					<spring:message code="representant.usuaris.rol.audit"/>
				</label>
			</div>
		</div>

		<div id="modal-botons">
			<a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></a>
			<button type="submit" class="btn btn-success">
				<span class="fa fa-save"></span>
				<c:choose>
					<c:when test="${empty entitatUsuariCommand.codi}"><spring:message code="comu.boto.guardar"/></c:when>
					<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
				</c:choose>
			</button>
		</div>
	</form:form>
</body>
</html>
