<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%
	java.util.Map<?,?> map = (java.util.Map<?,?>)request.getAttribute("campsDadesEspecifiquesAgrupats");
	if (map != null)
		pageContext.setAttribute("campsSenseAgrupar", map.get(null));
%>

<html>
<head>
	<title><spring:message code="consulta.info.titol"/></title>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
<script>
function initModalXml(element) {
	$('#modal-missatge-xml .modal-body').load(element.href);
	$('#modal-missatge-xml').modal('toggle');
}
</script>
</head>
<body>

	<c:if test="${consulta.estatError}">
		<div class="alert alert-error">
			<h4 class="alert-heading"><spring:message code="consulta.controller.recepcio.error"/>:</h4>
			<p>${consulta.error}</p>
		</div>
	</c:if>

	<div class="well">
		<h3>
			<spring:message code="consulta.info.consulta.dades"/>
			<a href="#dadesPeticio" data-toggle="collapse" data-target="#dadesPeticio">
				<i id="dadesPeticioIcon" class="pull-right icon-chevron-down"></i>
			</a>
		</h3>
		<div id="dadesPeticio" class="collapse in">
			<c:if test="${consulta.hiHaPeticio}">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn pull-right" href="<c:url value="/modal/consulta/${consulta.id}/xmlPeticio"/>" onclick="${initModalXml}"><i class="icon-info-sign"></i> <spring:message code="consulta.info.veure.xml"/></a>
			</c:if>
			<p>
				<spring:message code="consulta.info.consulta.enviada.dia"/>
				<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.creacioData}"/>
				<spring:message code="consulta.info.consulta.enviada.ales"/>
				<fmt:formatDate pattern="HH:mm:ss" value="${consulta.creacioData}"/>
			</p>
			<form class="form-horizontal" style="margin:0">
				<br/>
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.numpet"/></label>
							<div class="controls">
								<input type="text" value="${consulta.scspPeticionId}" class="span12" style="width:100%" id="" disabled="disabled"/>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.numsol"/></label>
							<div class="controls">
								<input type="text" value="${consulta.scspSolicitudId}" class="span12" style="width:100%" id="" disabled="disabled"/>
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class="control-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.procediment"/></label>
							<div class="controls">
								<input type="text" value="${consulta.procedimentNom}" class="span12" style="width:100%" id="" disabled="disabled"/>
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class="control-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.servei"/></label>
							<div class="controls">
								<input type="text" value="${servei.descripcio}" class="span12" style="width:100%" id="" disabled="disabled"/>
							</div>
						</div>
					</div>
				</div>
				<fieldset>
					<legend><spring:message code="consulta.form.dades.generiques"/></legend>
					<br/>
					<div class="row-fluid">
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.funcionari.nom"/></label>
								<div class="controls">
									<input type="text" value="${consulta.funcionariNom}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.funcionari.nif"/></label>
								<div class="controls">
									<input type="text" value="${consulta.funcionariNif}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.entitat.nom"/></label>
								<div class="controls">
									<input type="text" value="${consulta.entitatNom}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.entitat.cif"/></label>
								<div class="controls">
									<input type="text" value="${consulta.entitatCif}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.consentiment"/></label>
								<div class="controls">
									<input type="text" value="${consulta.consentiment}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.departament"/></label>
								<div class="controls">
									<input type="text" value="${consulta.departamentNom}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span12">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.finalitat"/></label>
								<div class="controls">
									<textarea rows="8" class="span12" id="" disabled="disabled">${consulta.finalitat}</textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.expedient"/></label>
								<div class="controls">
									<input type="text" value="${consulta.expedientId}" class="span12" id="" disabled="disabled"/>
								</div>
							</div>
						</div>
						<div class="span6">
						</div>
					</div>
				</fieldset>
				<c:set var="mostrarDadesTitular" value="${servei.pinbalActiuCampNom or servei.pinbalActiuCampLlinatge1 or servei.pinbalActiuCampLlinatge2 or servei.pinbalActiuCampNomComplet or servei.pinbalActiuCampDocument}"/>
				<c:if test="${mostrarDadesTitular}">
					<fieldset>
						<legend><spring:message code="consulta.form.dades.titular"/></legend>
						<div class="clearfix legend-margin-bottom"></div>
						<c:if test="${servei.pinbalActiuCampDocument}">
							<div class="row-fluid">
								<div class="span6">
									<div class="control-group">
										<label class="control-label" for=""><spring:message code="consulta.form.camp.document.tipus"/></label>
										<div class="controls">
											<input type="text" value="${consulta.titularDocumentTipus}" class="span12" id="" disabled="disabled"/>
										</div>
									</div>
								</div>
								<div class="span6">
									<div class="control-group">
										<label class="control-label" for=""><spring:message code="consulta.form.camp.document.num"/></label>
										<div class="controls">
											<input type="text" value="${consulta.titularDocumentNum}" class="span12" id="" disabled="disabled"/>
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
									<div class="row-fluid">
								</c:if>
								<div class="span6">
									<div class="control-group">
										<label class="control-label" for="${campPath}">${campLabel}</label>
										<div class="controls">
											<input type="text" value="${consulta[campPath]}" class="span12" id="campPath" disabled="disabled"/>
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
				<c:set var="mostrarDadesEspecifiques" value="${false}"/>
				<c:forEach var="camp" items="${campsDadesEspecifiques}">
					<c:if test="${camp.visible}"><c:set var="mostrarDadesEspecifiques" value="${true}"/></c:if>
				</c:forEach>
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
<script type="text/javascript">
$('#dadesPeticio').on('hidden', function () {
	$('#dadesPeticioIcon').attr('class', 'pull-right icon-chevron-down');
});
$('#dadesPeticio').on('shown', function () {
	$('#dadesPeticioIcon').attr('class', 'pull-right icon-chevron-up');
});
</script>
	</div>
	<c:if test="${consulta.hiHaResposta}">
		<div class="well">
			<h3>
				<spring:message code="consulta.info.resposta.dades"/>
				<a href="#dadesResposta" data-toggle="collapse" data-target="#dadesResposta">
					<i id="dadesRespostaIcon" class="pull-right icon-chevron-down"></i>
				</a>
			</h3>
			<div id="dadesResposta" class="collapse out">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn pull-right" href="<c:url value="/modal/consulta/${consulta.id}/xmlResposta"/>" onclick="${initModalXml}"><i class="icon-info-sign"></i> <spring:message code="consulta.info.veure.xml"/></a>
				<p>
					<spring:message code="consulta.info.resposta.rebuda.dia"/>
					<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.respostaData}"/>
					<spring:message code="consulta.info.resposta.rebuda.ales"/>
					<fmt:formatDate pattern="HH:mm:ss" value="${consulta.respostaData}"/>
				</p>
			</div>
<script type="text/javascript">
$('#respostaXml').val(vkbeautify.xml($('#respostaXml').val()));
$('#dadesResposta').on('hidden', function () {
	$('#dadesRespostaIcon').attr('class', 'pull-right icon-chevron-down');
});
$('#dadesResposta').on('shown', function () {
	$('#dadesRespostaIcon').attr('class', 'pull-right icon-chevron-up');
});
</script>
		</div>
	</c:if>
	<c:if test="${consulta.estatTramitada}">
		<div class="well">
			<h3>
				<spring:message code="consulta.info.generar.justificant"/>
				<a href="${consulta.id}/justificant" class="pull-right">
					<img src="<c:url value="/img/pdf-icon-big.png"/>" width="27" height="32" alt="<spring:message code="consulta.info.descarregar.pdf"/>" title="<spring:message code="consulta.info.descarregar.pdf"/>"/>
				</a>
			</h3>
		</div>
	</c:if>
	<div class="well">
		<c:choose>
			<c:when test="${not empty consulta.pareId}">
				<a href="<c:url value="/consulta/multiple/${consulta.pareId}"/>" class="btn"><spring:message code="comu.boto.tornar"/></a>
			</c:when>
			<c:otherwise>
				<a href="<c:url value="/consulta"/>" class="btn"><spring:message code="comu.boto.tornar"/></a>
			</c:otherwise>
		</c:choose>
	</div>
	<div id="modal-missatge-xml" class="modal hide fade" style="width:750px">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="consulta.info.missatge.xml"/></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
		</div>
	</div>

</body>
</html>
