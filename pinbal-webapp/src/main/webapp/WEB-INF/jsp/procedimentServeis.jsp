<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="procediment.list.titol"/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
<script>
$(document).ready(function() {
	$('.confirm-remove').click(function() {
		  return confirm("<spring:message code="procediment.serveis.confirmacio.desactivacio.servei.entitat"/>");
	});
});
</script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="procediment.serveis.miques.procediment" arguments="${procediment.nom}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="procediment.serveis.miques.serveis"/></li>
	</ul>

	<form>
		<jmesa:tableModel
				id="serveis" 
				items="${serveisActius}"
				view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
				var="registre"
				maxRows="${fn:length(serveisActius)}">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="codi" titleKey="procediment.serveis.taula.columna.codi"/>
					<jmesa:htmlColumn property="descripcio" titleKey="procediment.serveis.taula.columna.descripcio"/>
					<jmesa:htmlColumn property="actiu" titleKey="procediment.serveis.taula.columna.actiu">
						<c:if test="${registre.actiu}"><i class="icon-ok"></i></c:if>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_activar" title="&nbsp;" sortable="false">
						<c:choose>
							<c:when test="${not registre.actiu}"><a href="servei/${registre.codi}/enable" class="btn"><i class="icon-ok"></i>&nbsp;<spring:message code="comu.boto.activar"/></a></c:when>
							<c:otherwise><a href="servei/${registre.codi}/disable" class="btn confirm-remove"><i class="icon-remove"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a></c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_permisos" title="&nbsp;" sortable="false">
						<c:choose>
							<c:when test="${registre.actiu}">
								<a href="servei/${registre.codi}/permis" class="btn"><i class="icon-lock"></i>&nbsp;<spring:message code="procediment.serveis.taula.boto.permisos"/></a>
							</c:when>
							<c:otherwise>
								<a href="#" class="btn disabled"><i class="icon-lock"></i>&nbsp;<spring:message code="procediment.serveis.taula.boto.permisos"/></a>
							</c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
	            </jmesa:htmlRow>
	        </jmesa:htmlTable>
		</jmesa:tableModel>
	</form>
<script>
$('#confirm-remove').click(function() {
	  return confirm("<spring:message code="procediment.serveis.confirmacio.desactivacio.servei.procediment"/>");
});
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
</script>
	<div>
		<a href="<c:url value="/procediment"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
