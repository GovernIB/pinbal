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
	<title><spring:message code="consulta.multiple.info.titol"/></title>
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
		<div class="alert alert-danger fade in">
		  	<button class="close" data-dismiss="alert" aria-label="close">&times;</button>
			<h4 class="alert-heading"><spring:message code="consulta.controller.recepcio.error"/>:</h4>
			<p>${consulta.error}</p>
		</div>
	</c:if>

	<div class="well">
		<h3>
			<spring:message code="consulta.info.consulta.dades"/>
			<button class="btn btn-link pull-right" data-toggle="collapse" data-target="#dadesPeticio" style="color:black">
				<i id="dadesPeticioIcon" class="pull-right fas fa-chevron-down"></i>
			</button>
		</h3>
		<div id="dadesPeticio" class="collapse">
			<c:if test="${consulta.hiHaPeticio}">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn btn-default pull-right" href="<c:url value="/modal/consulta/${consulta.id}/xmlPeticio"/>" onclick="${initModalXml}">
					<i class="fas fa-info-circle"></i> <spring:message code="consulta.info.veure.xml"/>
				</a>
			</c:if>
			<p>
				<spring:message code="consulta.info.consulta.enviada.dia"/>
				<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.creacioData}"/>
				<spring:message code="consulta.info.consulta.enviada.ales"/>
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
							<input type="text" value="${consulta.scspSolicitudId}" class="form-control"  disabled="disabled"/>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.procediment"/></label>
							<div class="controls">
								<input type="text" value="${consulta.procedimentNom}" class="form-control" disabled="disabled"/>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<label class="control-label" for=""><spring:message code="consulta.form.camp.servei"/></label>
							<div class="controls">
								<input type="text" value="${servei.descripcio}" class="form-control" disabled="disabled"/>
							</div>
						</div>
					</div>
				</div>
				<fieldset>
					<legend><spring:message code="consulta.form.dades.generiques"/></legend>
					<br/>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.funcionari.nom"/></label>
								<div class="controls">
									<input type="text" value="${consulta.funcionariNom}" class="form-control" disabled="disabled"/>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.funcionari.nif"/></label>
								<div class="controls">
									<input type="text" value="${consulta.funcionariNif}" class="form-control" disabled="disabled"/>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.entitat.nom"/></label>
								<input type="text" value="${consulta.entitatNom}" class="form-control" disabled="disabled"/>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.entitat.cif"/></label>
								<input type="text" value="${consulta.entitatCif}" class="form-control" disabled="disabled"/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.consentiment"/></label>
								<input type="text" value="${consulta.consentiment}" class="form-control" disabled="disabled"/>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.departament"/></label>
								<input type="text" value="${consulta.departamentNom}" class="form-control" disabled="disabled"/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="form-group">
								<label class="control-label" for=""><spring:message code="consulta.form.camp.finalitat"/></label>
								<textarea rows="8" class="form-control" id="" disabled="disabled">${consulta.finalitat}</textarea>
							</div>
						</div>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
<script type="text/javascript">
$('#dadesPeticio').on('hidden', function () {
	$('#dadesPeticioIcon').attr('class', 'pull-right icon-chevron-down');
});
$('#dadesPeticio').on('shown', function () {
	$('#dadesPeticioIcon').attr('class', 'pull-right icon-chevron-up');
});
</script>
	<c:if test="${not empty filles}">
		<div class="well">
			<h3>
				<spring:message code="consulta.multiple.info.solicituds"/>
				<a href="#dadesSolicitud" class="pull-right" data-toggle="collapse" data-target="#dadesSolicitud">
					<i id="dadesSolicitudIcon" class="fas fa-chevron-up"></i>
				</a>
			</h3>
			<div id="dadesSolicitud" class="collapse in">
				<table id="solicituds" class="table table-bordered table-striped">
					<thead>
						<tr>
							<th><spring:message code="consulta.multiple.info.num.solicitud"/></th>
							<th><spring:message code="consulta.multiple.info.titular.nom"/></th>
							<th><spring:message code="consulta.multiple.info.titular.document"/></th>
							<c:if test="${consulta.hiHaPeticio}">
								<th><spring:message code="consulta.multiple.info.peticio"/></th>
								<th><spring:message code="consulta.multiple.info.resposta"/></th>
							</c:if>
							<th></th>
							<c:if test="${consulta.estatTramitada}">
								<th></th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="filla" items="${filles}">
							<tr>
								<td>${filla.scspSolicitudId}</td>
								<td>${filla.titularNomSencer}</td>
								<td>${filla.titularDocumentAmbTipus}</td>
								<c:if test="${consulta.hiHaPeticio}">
									<td>
										<a class="btn btn-default pull-right" href="<c:url value="/modal/consulta/${filla.id}/xmlPeticio"/>" onclick="${initModalXml}">
											<i class="fas fa-info-circle"></i>
											<spring:message code="consulta.info.veure.xml"/>
										</a>
									</td>
									<td>
										<a class="btn btn-default pull-right" href="<c:url value="/modal/consulta/${filla.id}/xmlResposta"/>" onclick="${initModalXml}">
											<i class="fas fa-info-circle"></i>
											<spring:message code="consulta.info.veure.xml"/>
										</a>
									</td>
								</c:if>
								<td>
									<a href="../${filla.id}" class="btn btn-default"><i class="fas fa-search-plus"></i>&nbsp;<spring:message code="consulta.list.taula.detalls"/></a>
								</td>
								<c:if test="${consulta.estatTramitada}">
									<td>
										<a class="btn btn-default" href="../${filla.id}/justificant">
											<i class="far fa-file-pdf"></i>
										</a>
									</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if> 
<script type="text/javascript">
$('#dadesSolicitud').on('hidden', function () {
	$('#dadesSolicitudIcon').attr('class', 'pull-right glyphicon-chevron-down');
});
$('#dadesSolicitud').on('shown', function () {
	$('#dadesSolicitudIcon').attr('class', 'pull-right glyphicon-chevron-up');
});
</script>
	<c:if test="${consulta.hiHaResposta}">
	<div class="well well-sm">
		<div class="row">
			<div class="col-xs-8">
				<h3><spring:message code="consulta.info.resposta.dades"/></h3>
			</div>
			<div class="col-xs-2 mt15 pull-right">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn btn-default pull-right" href="<c:url value="/modal/consulta/${consulta.id}/xmlResposta"/>" onclick="${initModalXml}"><i class="fas fa-info-circle"></i> <spring:message code="consulta.info.veure.xml"/></a>
			</div>
		</div>
		<c:if test="${consulta.respostaData}">
		<div id="dadesResposta">
			<p>
				<spring:message code="consulta.info.resposta.rebuda.dia"/>
				<fmt:formatDate pattern="dd/MM/yyyy" value="${consulta.respostaData}"/>
				<spring:message code="consulta.info.resposta.rebuda.ales"/>
				<fmt:formatDate pattern="HH:mm:ss" value="${consulta.respostaData}"/>
			</p>
		</div>
		</c:if>
	</div>
<script type="text/javascript">
$('#respostaXml').val(vkbeautify.xml($('#respostaXml').val()));
$('#dadesResposta').on('hidden', function () {
	$('#dadesRespostaIcon').attr('class', 'pull-right glyphicon-chevron-down');
});
$('#dadesResposta').on('shown', function () {
	$('#dadesRespostaIcon').attr('class', 'pull-right glyphicon-chevron-up');
});
</script>
	</c:if>
	<c:if test="${consulta.estatTramitada}">
		<div class="well well-lg">
			<h3>
				<spring:message code="consulta.multiple.info.generar.justificantpdf"/>
				<a href="${consulta.id}/justificantpdf" 
					aria-label="<spring:message code="consulta.info.descarregar.pdf"/>" class="pull-right">
					<i aria-hidden="true" class="fas fa-file-pdf"></i>
				</a>
			</h3>
		</div>
		<div class="well well-lg">
			<h3>
				<spring:message code="consulta.multiple.info.generar.justificantzip"/>
				<a href="${consulta.id}/justificantzip" 
					aria-label="<spring:message code="consulta.info.descarregar.zip"/>" class="pull-right">
					<i class="far fa-file-archive"></i>
				</a>
			</h3>
		</div>
	</c:if>
	<div class="well well-lg">
		<a href="<c:url value="/consulta/multiple"/>" class="btn btn-default"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
	</div>
	<div id="modal-missatge-xml" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
  		<div class="modal-dialog modal-dialog-centered" role="document">
	   		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3><spring:message code="consulta.info.missatge.xml"/></h3>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<button id="cbcopy" class="btn btn-default"><span class="fa fa-clipboard"></span> <spring:message code="comu.clipboard.copy"/></button>
				<button class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
			</div>
			</div>
		</div>
	</div>

</body>
</html>
