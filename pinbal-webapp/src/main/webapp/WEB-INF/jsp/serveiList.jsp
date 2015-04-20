<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="servei.list.titol"/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
<script>
$(document).ready(function() {
	$('.confirm-esborrar').click(function() {
		  return confirm("<spring:message code="servei.list.confirmacio.esborrar"/>");
	});
});
</script>
</head>
<body>

	<div class="row-fluid">
		<div class="span12">
			<a class="btn pull-right" href="<c:url value="/servei/new"/>"><i class="icon-plus"></i>&nbsp;<spring:message code="servei.list.boto.nou.servei"/></a>
		</div>
		<div class="clearfix"></div>
	</div>
	<form>
		<jmesa:tableModel
				id="serveis" 
				items="${serveis}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre"
				maxRows="${fn:length(serveis)}">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="codi" titleKey="servei.list.taula.columna.codi"/>
					<jmesa:htmlColumn property="descripcio" titleKey="servei.list.taula.columna.descripcio"/>
					<jmesa:htmlColumn property="scspEmisor.nom" titleKey="servei.list.taula.columna.emisor"/>
					<jmesa:htmlColumn property="ACCIO_accions" title="&nbsp;" style="white-space:nowrap;" sortable="false">
						<div class="btn-group">
							<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="servei/${registre.codi}" title="<spring:message code="comu.boto.modificar"/>"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="servei/${registre.codi}/camp" title="<spring:message code="servei.list.taula.boto.formulari"/>"><i class="icon-th-list"></i>&nbsp;<spring:message code="servei.list.taula.boto.formulari"/></a></li>
								<li><a href="servei/${registre.codi}/justificant" title="<spring:message code="servei.list.taula.boto.justificant"/>"><i class="icon-file"></i>&nbsp;<spring:message code="servei.list.taula.boto.justificant"/></a></li>
								<li><a href="servei/${registre.codi}/delete" class="confirm-esborrar"><i class="icon-trash"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</jmesa:htmlColumn>
	            </jmesa:htmlRow>
	        </jmesa:htmlTable>
		</jmesa:tableModel>
	</form>
<script type="text/javascript">
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
</script>

</body>
</html>
