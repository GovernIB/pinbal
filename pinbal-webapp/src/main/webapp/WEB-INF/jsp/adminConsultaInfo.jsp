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

	$("#justificantInfo").click(() => {
		console.log("clicked");
		$.ajax({
			url:'<c:url value="${consulta.id}/justificant/arxiu/detall"/>',
			type:'GET',
			success: jsp => {
				$("#arxiuDetall").html(jsp);
			}
		});
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
	
	<!--  CAPÇALERA DEL TAB PANEL -->
	<div class="tab-header">		
		<div class="well">
			<h3>
				<spring:message code="admin.consulta.info.consulta.dades"/>
				<button class="btn btn-link pull-right" data-toggle="collapse" data-target="#dadesPeticio" style="color:black">
					<i id="dadesPeticioIcon" class="pull-right fas fa-chevron-down"></i>
				</button>
			</h3>
			<div id="dadesPeticio" class="collapse in">				
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
								<input type="text" value="${consulta.procedimentCodi} - ${consulta.procedimentNom}" class="form-control" disabled="disabled"/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.servei"/></label>
								<input type="text" value="${servei.codi} - ${servei.descripcio}" class="form-control" disabled="disabled"/>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>		
		
		<!-- DEFINICIO DE LES ENTRADES DE CADA TAB -->
		<ul class="nav nav-tabs" role="tablist">
			<li class="active" role="presentation">
				<a href="#dadesGeneriquesTab" aria-controls="dadesGeneriques" role="tab" data-toggle="tab"><spring:message code="consulta.info.descarregar.peticio.etiqueta"/></a>
			</li>
			<c:if test="${consulta.hiHaResposta}">	
				<li role="presentation">
					<a href="#dadesRespostaTab" aria-controls="dadesResposta" role="tab" data-toggle="tab"><spring:message code="admin.consulta.info.resposta.dades"/></a>
				</li>
			</c:if>
			<c:if test="${consulta.justificantEstatOk or (consulta.estatTramitada && consulta.justificantEstatPendent) or consulta.justificantEstatError}">
				<li role="presentation">
					<a href="#descarregaJustificantsTab" aria-controls="descarregaJustificants" role="tab" data-toggle="tab"><spring:message code="consulta.info.descarregar.justificant.etiqueta"/></a>
				</li>
			</c:if>		
		</ul>
		<!-- FI DEFINICIO DE LES ENTRADES DE CADA TAB -->
				
	</div>
	<!--  FI CAPÇALERA DEL TAB PANEL -->
	
	<!-- DEFINICIO DEL CONTINGUT DE LES TABS -->
	<div class="tab-content">	
	
		<!-- DADES GENERIQUES TAB -->
		<div class="tab-pane active in" id="dadesGeneriquesTab" role="tabpanel">
			<div class="well well-sm">				
				<form>		
					<fieldset>
						<legend><spring:message code="consulta.form.dades.generiques"/>						
							<c:if test="${consulta.hiHaPeticio}">
								<c:set var="initModalXml">initModalXml(this);return false</c:set>
								<a class="btn btn-default pull-right" style="top: -4px; position: relative;" href="<c:url value="/modal/admin/consulta/${consulta.id}/xmlPeticio"/>" onclick="${initModalXml}">
									<i class="fas fa-info-circle"></i> <spring:message code="admin.consulta.info.veure.xml"/>
									<c:if test="${consulta.peticioGenerada}"><spring:message code="admin.consulta.info.veure.xml.autogenerat"/></c:if>
								</a>
							</c:if>
						</legend>
						<div class="clearfix legend-margin-bottom"></div>
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
								</div>							
								<div class="form-group">
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
							<c:set var="dadesEspecifiquesValors" value="${consulta.dadesEspecifiquesMap}" scope="request"/>
							<c:set var="campsPerMostrar" value="${campsSenseAgrupar}" scope="request"/>
							<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
							<c:forEach var="grup" items="${grups}">
								<fieldset id="grup_${grup.id}" class="fs-grup<c:if test='${grup.grupRegla}'> grup-regla</c:if> editable" data-nom="${grup.nom}">
									<legend><span class="fs-grup-nom">${grup.nom}</span></legend>
									<div class="clearfix legend-margin-bottom"></div>
									<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[grup.id]}" scope="request"/>
									<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
										<%-- Subgrups --%>
									<c:if test="${not empty grup.fills}">
										<c:forEach var="subgrup" items="${grup.fills}">
											<div id="grup_${subgrup.id}" class="panel panel-default fs-subgrup<c:if test='${subgrup.grupRegla}'> grup-regla</c:if> editable" data-nom="${subgrup.nom}">
												<div class="panel-heading"><h3 class="panel-title">${subgrup.nom}</h3></div>
												<div class="panel-body">
													<c:set var="campsPerMostrar" value="${campsDadesEspecifiquesAgrupats[subgrup.id]}" scope="request"/>
													<jsp:include page="import/dadesEspecifiquesForm.jsp"/>
												</div>
											</div>
										</c:forEach>
									</c:if>
								</fieldset>
							</c:forEach>
						</fieldset>
					</c:if>
				</form>			
			</div>
		</div>
		<!-- FI DADES GENERIQUES TAB -->
		
		<!-- DADES RESPOSTA TAB -->
		<div class="tab-pane" id="dadesRespostaTab" role="tabpanel">
			<c:if test="${consulta.hiHaResposta}">
			<div class="well well-sm">
				<fieldset>
					<legend><spring:message code="admin.consulta.info.resposta.dades"/>
						<c:set var="initModalXml">initModalXml(this);return false</c:set>
						<a class="btn btn-default pull-right" style="top: -4px; position: relative;" href="<c:url value="/modal/admin/consulta/${consulta.id}/xmlResposta"/>" onclick="${initModalXml}">
							<i class="fas fa-info-circle"></i> <spring:message code="admin.consulta.info.veure.xml"/>
						</a>
					</legend>
					<div class="clearfix legend-margin-bottom"></div>
					<c:if test="${not empty consulta.respostaData}">
					<div id="dadesResposta">
						<p>
							<spring:message code="admin.consulta.info.resposta.rebuda.dia"/>
							<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.respostaData}"/>
							<spring:message code="admin.consulta.info.resposta.rebuda.ales"/>
							<fmt:formatDate pattern="HH:mm:ss" value="${consulta.respostaData}"/>
						</p>
					</div>
					</c:if>
					<c:if test="${not empty dadesResposta}">
						<div id="arbreDadesResposta">
							<c:if test="${not empty dadesResposta.fills}">
								<c:set var="fills" value="${dadesResposta.fills}" scope="request"/>
								<jsp:include page="import/renderFills.jsp" >
									<jsp:param name="margin" value="20" />
								</jsp:include>
							</c:if>
						</div>
					</c:if>
				</fieldset>
			</div>
			</c:if>
		</div>
		<!-- FI DADES RESPOSTA TAB -->		
		
		<!-- JUSTIFICANTS TAB -->
		<div class="tab-pane" id="descarregaJustificantsTab" role="tabpanel">
			<div class="well well-sm">
				<fieldset>
					<legend><spring:message code="consulta.info.descarregar.justificant.etiqueta"/></legend>

					<c:if test="${consulta.justificantEstatError}">
						<div class="row">
							<div class="col-md-12">
								<strong><spring:message code="consulta.info.descarregar.justificant"/></strong>
								<div class="dropdown pull-right">
									<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-"><img src="<c:url value="/img/error_icon.png"/>" title="<spring:message code="consulta.list.taula.justif.error"/>" alt="<spring:message code="consulta.list.taula.justif.error"/>"/></i>&nbsp;&nbsp;<span class="caret"></span></a>
									<ul class="dropdown-menu">
										<li>
											<a href="#" data-toggle="modal" data-target="#modal-justificant-error">
												<i class="fas fa-exclamation-triangle"></i>&nbsp;<spring:message code="consulta.list.taula.justif.error.veure"/>
											</a>
										</li>
										<li>
											<a href="../admin/consulta/${consulta.id}/justificantReintentar?info=true" class="justificant-reintentar">
												<i class="fas fa-redo-alt"></i>&nbsp;<spring:message code="consulta.list.taula.justif.error.reintentar"/>
											</a>
										</li>
									</ul>
								</div>
							</div>
							<div class="col-md-12">
								<span style="color:#a94442; font-size: smaller;"><i class="fas fa-exclamation-triangle"></i>&nbsp;<spring:message code="consulta.info.errors.justificant"/></span>
							</div>
						</div>
					</c:if>

					<c:if test="${consulta.justificantEstatOk or (consulta.estatTramitada && consulta.justificantEstatPendent)}">
						<div class="row">
							<div class="col-md-12">
								<strong><spring:message code="consulta.info.descarregar.justificant"/></strong>
								<a href="${consulta.id}/justificant" class="btn btn-default pull-right" style="width: 120px;">
									<i class="far fa-file-pdf"></i> <spring:message code="comu.boto.descarregar"/>
								</a>
							</div>
						</div>
						<c:if test="${not empty consulta.arxiuDocumentUuid}">
							<div class="row">
								<div class="col-md-12">
									<strong><spring:message code="consulta.info.descarregar.justificant.consultaArxiuDigital"/></strong>
									<a id="justificantInfo" class="btn btn-default pull-right" data-target="#modal-justificant-arxiu-info" data-toggle="modal" style="cursor:pointer; padding-right: 10px; text-decoration: none; width: 120px;">
										<i class="fas fa-info-circle"></i> Info ARXIU
									</a>
								</div>
							</div>
						</c:if>

						<div class="row">
							<div class="col-md-12">
								<legend>
									<spring:message code="consulta.info.descarregar.justificant.vistaPrevia"/>
								</legend>
								<div id="pdf-container" class="well">
									<object id="pdfObject"
											data="${consulta.id}/justificant/inline"
											type="application/pdf"
											width="100%" height="500px">
									</object>
								</div>
								<div id="error-container" style="display: none; color: #a94442; font-size: smaller;">
									<i class="fas fa-exclamation-triangle"></i> Error: No s'ha pogut carregar el document PDF.
								</div>
							</div>
						</div>

						<script>
							document.addEventListener('DOMContentLoaded', function () {
								const pdfObject = document.getElementById('pdfObject');
								const errorContainer = document.getElementById('error-container');
								const pdfContainer = document.getElementById('pdf-container');

								// Verificar si el PDF es carrega
								pdfObject.onerror = function () {
									// Mostrar missatge d'error
									errorContainer.style.display = 'block';
									pdfContainer.style.display = 'none';
								};
							});
						</script>
					</c:if>
				</fieldset>
			</div>
		</div>
		<!-- FI JUSTIFICANTS TAB -->
	
	</div>
	<!-- FI DEFINICIO DEL CONTINGUT DE LES TABS -->
	<div id="modal-botons" class="well">
		<c:choose>
			<c:when test="${multiple and not empty consulta.pareId}">
				<a href="<c:url value="/modal/admin/consulta/${consulta.pareId}"/>" class="btn btn-default"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
			</c:when>
			<c:otherwise>
				<a href="<c:url value="/admin/consulta"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
			</c:otherwise>
		</c:choose>
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
	<div id="modal-justificant-arxiu-info" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h3><spring:message code="consulta.info.justificant"/></h3>
				</div>
				<div class="modal-body" id="arxiuDetall">
				</div>
				<div class="modal-footer">
				</div>
			</div>
		</div>
	</div>
</body>
</html>
