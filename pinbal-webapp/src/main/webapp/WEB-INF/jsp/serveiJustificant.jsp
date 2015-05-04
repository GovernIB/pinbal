<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="servei.justificant.titol"/></title>
	<c:choose>
		<c:when test="${not empty servei.descripcio}"><c:set var="serveiPerTitol" value="${servei.descripcio}"/></c:when>
		<c:otherwise><c:set var="serveiPerTitol" value="${servei.codi}"/></c:otherwise>
	</c:choose>
	<meta name="subtitle" content="${serveiPerTitol}"/>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
<script>
$(document).ready(function() {
	$('.justificant-modificar').click(function() {
		$('#modal-justificant-hidden-xpath').val($(this).data('xpath'));
		$('#modal-justificant-input-traduccio').val($(this).data('traduccio'));
		$('#modal-justificant').modal('toggle');
	});
	$('#modal-justificant-boto-submit').click(function() {
		$('#modal-justificant-form').submit();
	});
});
</script>
</head>
<body>

	<jmesa:tableModel
			id="jmesa-camps"
			items="${llistaDadesEspecifiques}"
			view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
			var="registre"
			maxRows="${fn:length(llistaDadesEspecifiques)}">
		<jmesa:htmlTable>
			<c:if test="${registre.dades.pathAmbSeparadorDefault != 'DatosEspecificos'}">
				<jmesa:htmlRow>
					<c:set var="traduccioCoincident"/>
					<c:forEach var="traduccio" items="${traduccions}">
						<c:if test="${traduccio.xpath == registre.dades.pathAmbSeparadorDefault}">
							<c:set var="traduccioCoincident" value="${traduccio}"/>
						</c:if>
					</c:forEach>
					<jmesa:htmlColumn property="CALCUL_path" titleKey="servei.justificant.taula.columna.path" sortable="false">
						${registre.dades.pathAmbSeparadorDefault}
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="CALCUL_traduccio" titleKey="servei.justificant.taula.columna.traduccio" sortable="false">
						<c:choose>
							<c:when test="${not empty traduccioCoincident}">${traduccioCoincident.traduccio}</c:when>
							<c:otherwise><span class="muted">${registre.dades.nom}</span></c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn sortable="false">
						<c:choose>
							<c:when test="${not empty traduccioCoincident}">
								<a href="#" title="<spring:message code="comu.boto.modificar"/>" class="btn justificant-modificar" data-xpath="${registre.dades.pathAmbSeparadorDefault}" data-traduccio="${traduccioCoincident.traduccio}"><i class="icon-pencil"></i> <spring:message code="comu.boto.modificar"/></a>
							</c:when>
							<c:otherwise>
								<a href="#" title="<spring:message code="comu.boto.modificar"/>" class="btn justificant-modificar" data-xpath="${registre.dades.pathAmbSeparadorDefault}"><i class="icon-pencil"></i> <spring:message code="comu.boto.modificar"/></a>
							</c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
				</jmesa:htmlRow>
			</c:if>
		</jmesa:htmlTable>
	</jmesa:tableModel>
	<div>
		<a href="<c:url value="/servei"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

	<div id="modal-justificant" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 id="modal-justificant-titol"><spring:message code="servei.justificant.modal.titol"/></h3>
		</div>
		<div class="modal-body">
			<form id="modal-justificant-form" action="" method="post" class="form-horizontal">
				<input type="hidden" id="modal-justificant-hidden-id" name="id"/>
				<input type="hidden" id="modal-justificant-hidden-servei" name="servei" value="${servei.codi}"/>
				<input type="hidden" id="modal-justificant-hidden-xpath" name="xpath"/>
				<div class="control-group">
    				<label class="control-label" for="modal-justificant-input-traduccio"><spring:message code="servei.justificant.modal.camp.traduccio"/></label>
					<div class="controls">
						<input type="text" id="modal-justificant-input-traduccio" name="traduccio" class="input-xlarge"/>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" id="modal-justificant-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>

</body>
</html>