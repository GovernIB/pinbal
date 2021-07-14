<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="admin.consulta.info.titol"/></title>
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
		<div class="alert alert-danger fade in">
		  	<button class="close" data-dismiss="alert" aria-label="close">&times;</button>
			<h4 class="alert-heading"><spring:message code="admin.consulta.info.recepcio.error"/>:</h4>
			<p>${consulta.error}</p>
		</div>
	</c:if>

	<div class="well">
		<h3>
			<spring:message code="admin.consulta.info.consulta.dades"/>
		</h3>
		<div id="dadesPeticio">
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
		</div>
	</div>
	<c:if test="${consulta.hiHaResposta}">
		<div class="well">
			<h3>
				<spring:message code="admin.consulta.info.resposta.dades"/>
			</h3>
			<div id="dadesResposta">
				<c:set var="initModalXml">initModalXml(this);return false</c:set>
				<a class="btn Zcol-md-pull-right" href="<c:url value="/modal/admin/consulta/${consulta.id}/xmlResposta"/>" onclick="${initModalXml}">
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
	<div class="well">
		<a href="<c:url value="/admin/consulta"/>" class="btn btn-default"><spring:message code="comu.boto.tornar"/></a>
	</div>
	<div id="modal-missatge-xml" class="modal fade">
  	<div class="modal-dialog">
    	<div class="modal-content">
	      	<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="admin.consulta.info.missatge.xml"/></h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<a href="#" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></a>
			</div>
		</div>
	</div>
	</div>

</body>
</html>
