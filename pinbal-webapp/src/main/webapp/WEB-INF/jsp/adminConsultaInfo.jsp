<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%
	java.util.Map<?,?> map = (java.util.Map<?,?>)request.getAttribute("campsDadesEspecifiquesAgrupats");
	if (map != null)
		pageContext.setAttribute("campsSenseAgrupar", map.get(null));
%>

<html>
<head>
	<title><spring:message code="admin.consulta.info.titol"/></title>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

	<link href="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>

	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
<script>
function initModalXml(element) {
	$('#modal-missatge-xml .modal-body').load(element.href);
	$('#modal-missatge-xml').modal('toggle');
}

$(document).ready(function () {
	$("#cbcopy").click(() => {
		let xml = $('#modal-missatge-xml .modal-body').find('#missatgeXml').val();
		navigator.clipboard.writeText(xml);
	});
});
</script>
</head>
<body>
	<c:if test="${consulta.estatError}">
		<c:set var="msg" value="${consulta.error}" />
		<c:if test="${fn:contains(consulta.error, '|||')}">
			<c:set var="splitted" value="${fn:split(consulta.error, '|||')}" />
			<c:set var="msg" value="${splitted[0]}" />
			<c:set var="trace" value="${splitted[1]}" />
		</c:if>
		<div class="alert alert-danger fade in">
			<button class="close" data-dismiss="alert" aria-label="close">&times;</button>
			<h4 class="alert-heading"><spring:message code="admin.consulta.info.recepcio.error"/>:</h4>
			<p>${msg}</p>
			<c:if test="${not empty trace}">
				<div id="trace-container">
					<a class="detall-trace-titol" data-toggle="collapse" href="#errtrace" aria-expanded="false" aria-controls="errtrace">
						<spring:message code="comu.error.detall" />
					</a>
					<div id=errtrace class="collapse">
						<div class="well">${trace}</div>
					</div>
				</div>
			</c:if>
		</div>
	</c:if>
	<div class="well">
		<h3>
			<spring:message code="admin.consulta.info.consulta.dades"/>
			<button class="btn btn-link pull-right" data-toggle="collapse" data-target="#dadesPeticio" style="color:black">
				<i id="dadesPeticioIcon" class="pull-right fas fa-chevron-down"></i>
			</button>
		</h3>
		<div id="dadesPeticio" class="collapse in">
			<c:if test="${consulta.hiHaPeticio}">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn btn-default pull-right" href="<c:url value="/modal/admin/consulta/${consulta.id}/xmlPeticio"/>" onclick="${initModalXml}">
					<i class="fas fa-info-circle"></i> <spring:message code="admin.consulta.info.veure.xml"/>
				</a>
			</c:if>
			<p>
				<spring:message code="admin.consulta.info.consulta.enviada.dia"/>
				<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.creacioData}"/>
				<spring:message code="admin.consulta.info.consulta.enviada.ales"/>
				<fmt:formatDate pattern="HH:mm:ss" value="${consulta.creacioData}"/>
			</p>
			<form>
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.numpet"/></label>
							<input type="text" value="${consulta.scspPeticionId}" class="form-control" disabled="disabled"/>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.numsol"/></label>
							<input type="text" value="${consulta.scspSolicitudId}" class="form-control" disabled="disabled"/>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.procediment"/></label>
							<input type="text" value="${consulta.procedimentNom}" class="form-control" disabled="disabled"/>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.servei"/></label>
							<input type="text" value="${servei.descripcio}" class="form-control" disabled="disabled"/>
						</div>
					</div>
				</div>
				<fieldset>
					<legend><spring:message code="consulta.form.dades.generiques"/></legend>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.funcionari.nom"/></label>
								<input type="text" value="${consulta.funcionariNom}" class="form-control" id="" disabled="disabled"/>
							</div>

							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.entitat.nom"/></label>
								<input type="text" value="${consulta.entitatNom}" class="form-control" id="" disabled="disabled"/>
							</div>
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.consentiment"/></label>
								<input type="text" value="${consulta.consentiment}" class="form-control" id="" disabled="disabled"/>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.funcionari.nif"/></label>
								<input type="text" value="${consulta.funcionariNif}" class="form-control" id="" disabled="disabled"/>
							</div>
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.entitat.cif"/></label>
								<input type="text" value="${consulta.entitatCif}" class="form-control" id="" disabled="disabled"/>
							</div>							<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.departament"/></label>
							<div class="controls">
								<input type="text" value="${consulta.departamentNom}" class="form-control" id="" disabled="disabled"/>
							</div>
						</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.finalitat"/></label>
								<div class="controls">
									<textarea rows="8" class="form-control" id="" disabled="disabled">${consulta.finalitat}</textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.expedient"/></label>
								<div class="controls">
									<input type="text" value="${consulta.expedientId}" class="form-control" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
						<div class="col-md-6">
						</div>
					</div>
				</fieldset>
				<c:set var="mostrarDadesTitular" value="${servei.pinbalActiuCampNom or servei.pinbalActiuCampLlinatge1 or servei.pinbalActiuCampLlinatge2 or servei.pinbalActiuCampNomComplet or servei.pinbalActiuCampDocument}"/>
				<c:if test="${mostrarDadesTitular}">
					<fieldset>
						<legend><spring:message code="consulta.form.dades.titular"/></legend>
						<div class="clearfix legend-margin-bottom"></div>
						<c:if test="${servei.pinbalActiuCampDocument}">
							<div class="row">
								<div class="col-md-6">
									<div class="form-group">
										<label class="control-label" for=""><spring:message code="consulta.form.camp.document.tipus"/></label>
										<div class="controls">
											<input type="text" value="${consulta.titularDocumentTipus}" class="form-control" id="" disabled="disabled"/>
										</div>
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group">
										<label class="control-label" for=""><spring:message code="consulta.form.camp.document.num"/></label>
										<div class="controls">
											<input type="text" value="${consulta.titularDocumentNum}" class="form-control" id="" disabled="disabled"/>
										</div>
									</div>
								</div>
							</div>
						</c:if>
						<c:set var="numColumnes" value="${2}"/>
						<c:set var="indexCamp" value="${0}"/>
						<c:forEach var="index" begin="0" end="3" varStatus="status">
							<c:choose>
								<c:when test="${index == 0}">
									<c:set var="campPath" value="titularNom"/>
									<spring:message var="campLabel" code="consulta.form.camp.nom"/>
									<c:set var="campServeiTest" value="pinbalActiuCampNom"/>
								</c:when>
								<c:when test="${index == 1}">
									<c:set var="campPath" value="titularLlinatge1"/>
									<spring:message var="campLabel" code="consulta.form.camp.llinatge1"/>
									<c:set var="campServeiTest" value="pinbalActiuCampLlinatge1"/>
								</c:when>
								<c:when test="${index == 2}">
									<c:set var="campPath" value="titularLlinatge2"/>
									<spring:message var="campLabel" code="consulta.form.camp.llinatge2"/>
									<c:set var="campServeiTest" value="pinbalActiuCampLlinatge2"/>
								</c:when>
								<c:when test="${index == 3}">
									<c:set var="campPath" value="titularNomComplet"/>
									<spring:message var="campLabel" code="consulta.form.camp.nomcomplet"/>
									<c:set var="campServeiTest" value="pinbalActiuCampNomComplet"/>
								</c:when>
							</c:choose>
							<c:if test="${servei[campServeiTest]}">
								<c:if test="${indexCamp == 0 or (indexCamp % numColumnes) == 0}">
									<div class="row">
								</c:if>
								<div class="col-md-6">
									<div class="form-group">
										<label class="control-label" for="${campPath}">${campLabel}</label>
										<div class="controls">
											<input type="text" value="${consulta[campPath]}" class="form-control" id="campPath" disabled="disabled"/>
										</div>
									</div>
								</div>
								<c:if test="${status.last or (indexCamp % numColumnes) == (numColumnes - 1)}">
									</div>
								</c:if>
								<c:set var="indexCamp" value="${indexCamp + 1}"/>
							</c:if>
						</c:forEach>
					</fieldset>
				</c:if>
				<c:if test="${mostrarDadesEspecifiques}">
					<fieldset>
						<c:if test="${not empty campsSenseAgrupar}">
							<legend><spring:message code="consulta.form.dades.especifiques"/></legend>
							<div class="clearfix legend-margin-bottom"></div>
						</c:if>
						<c:set var="dadesEspecifiquesDisabled" value="${true}" scope="request"/>
						<c:set var="dadesEspecifiquesValors" value="${consulta.dadesEspecifiques}" scope="request"/>
						<c:set var="campsPerMostrar" value="${campsSenseAgrupar}" scope="request"/>
						<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
						<c:forEach var="grup" items="${grups}">
							<fieldset>
								<legend>${grup.nom}</legend>
								<div class="clearfix legend-margin-bottom"></div>
								<c:set var="dadesEspecifiquesDisabled" value="${true}" scope="request"/>
								<c:set var="dadesEspecifiquesValors" value="${consulta.dadesEspecifiques}" scope="request"/>
								<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[grup.id]}" scope="request"/>
								<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
							</fieldset>
						</c:forEach>
					</fieldset>
				</c:if>
			</form>
		</div>
	</div>
	<c:if test="${consulta.hiHaResposta}">
		<div class="well">
			<h3>
				<spring:message code="admin.consulta.info.resposta.dades"/>
			</h3>
			<div id="dadesResposta">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn btn-default pull-right" href="<c:url value="/modal/admin/consulta/${consulta.id}/xmlResposta"/>" onclick="${initModalXml}">
					<i class="fas fa-info-circle"></i> <spring:message code="admin.consulta.info.veure.xml"/>
				</a>
				<p>
					<spring:message code="admin.consulta.info.resposta.rebuda.dia"/>
					<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.respostaData}"/>
					<spring:message code="admin.consulta.info.resposta.rebuda.ales"/>
					<fmt:formatDate pattern="HH:mm:ss" value="${consulta.respostaData}"/>
				</p>
			</div>
		</div>
	</c:if>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/admin/consulta"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
	<div id="modal-missatge-xml" class="modal fade">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="admin.consulta.info.missatge.xml"/></h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<button id="cbcopy" class="btn btn-default"><span class="fa fa-clipboard"></span> <spring:message code="comu.clipboard.copy"/></button>
				<a href="#" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></a>
			</div>
		</div>
	</div>
	</div>
</body>
</html>
