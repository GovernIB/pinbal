<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
<script>
$(document).ready(function() {
	$('.confirm-remove').click(function() {
		  return confirm("<spring:message code="entitat.serveis.confirmacio.desactivar.servei"/>");
	});
});
</script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="entitat.miques.entitat" arguments="${entitat.nom}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="entitat.miques.serveis"/></li>
	</ul>

	<form>
		<div>
			<jmesa:tableModel
		                id="serveis"
		                items="${serveisActius}"
		                view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
		                var="registre"
		                maxRows="${fn:length(serveisActius)}">
				<jmesa:htmlTable>
					<jmesa:htmlRow>
						<c:set var="trobat" value="${false}"/>
						<c:forEach var="entitatServei" items="${entitat.serveis}">
							<c:if test="${registre.codi == entitatServei}"><c:set var="trobat" value="${true}"/></c:if>
						</c:forEach>
						<jmesa:htmlColumn property="codi"/>
						<jmesa:htmlColumn property="descripcio"/>
						<jmesa:htmlColumn property="CALCUL_actiu" titleKey="entitat.serveis.actiu">
							<c:if test="${trobat}"><i class="icon-ok"></i></c:if>
						</jmesa:htmlColumn>
						<jmesa:htmlColumn property="ACCIO_activar" title="&nbsp;" sortable="false" style="white-space:nowrap;">
							<c:choose>
								<c:when test="${not trobat}"><a href="servei/${registre.codi}/add" class="btn"><i class="icon-ok"></i>&nbsp;<spring:message code="comu.boto.activar"/></a></c:when>
								<c:otherwise><a href="servei/${registre.codi}/remove" class="btn confirm-remove"><i class="icon-remove"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a></c:otherwise>
							</c:choose>
						</jmesa:htmlColumn>
		            </jmesa:htmlRow>
		        </jmesa:htmlTable>
			</jmesa:tableModel>
		</div>
	</form>
	<div>
		<a href="<c:url value="/entitat"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
