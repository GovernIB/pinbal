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
					<jmesa:htmlColumn property="procedimentCodi" titleKey="procediment.serveis.taula.columna.procediment.codi.adicional">
						<div class="input-append">
						  <input class="span9" id="procedimentCodi_${registre.codi}" type="text" value="${registre.procedimentCodi}" disabled>
						  <button class="btn edit-codi-procediment" type="button" data-codi-servei="${registre.codi}"><i class="icon-pencil"></i></button>
						</div>
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
$(document).ready(function() {
	$('button.edit-codi-procediment').click(function() {
		var servei_codi = $(this).data('codi-servei');
		var $input = $('#procedimentCodi_' + servei_codi);
		if ($input.prop('disabled')) {
			$input.prop('disabled',false);
			$input.next().html('<i class="icon-ok"></i>');
			$input.focus();
			$input.select();
		} else {
			actualitzaCodiProcediment(servei_codi,$input.val());
			$input.prop('disabled',true);
			$input.next().html('<i class="icon-pencil"></i>');
		}
	});
});

$('#confirm-remove').click(function() {
	  return confirm("<spring:message code="procediment.serveis.confirmacio.desactivacio.servei.procediment"/>");
});

function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}

function actualitzaCodiProcediment(servei_codi, codi_procediment) {
	$.ajax({
	    url:'<c:url value="servei/' + servei_codi + '/procedimentCodi"/>',
	    type:'GET',
	    dataType: 'json',
	    data: {procedimentCodi: codi_procediment},
	    success: function(result) {
		    if (result)
	    		console.log(result);
	    }
	});	
}

</script>
	<div>
		<a href="<c:url value="/procediment"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
