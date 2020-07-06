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
	$('#netejar-filtre').click(function() {
		$(':input', $('#form-filtre')).each (function() {
			var type = this.type, tag = this.tagName.toLowerCase();
			if (type == 'text' || type == 'password' || tag == 'textarea')
				this.value = '';
			else if (type == 'checkbox' || type == 'radio')
				this.checked = false;
			else if (tag == 'select')
				this.selectedIndex = 0;
		});
		$('#form-filtre').submit();
	});
});
</script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="procediment.serveis.miques.procediment" arguments="${procediment.nom}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="procediment.serveis.miques.serveis"/></li>
	</ul>

	<c:url value="./servei" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-inline" commandName="serveiFiltreCommand">
		<div class="row-fluid">
			<div class="control-group span3">	
				<c:set var="campPath" value="codi"/>
				<spring:message var="placeholderCodi" code="servei.list.filtre.camp.codi"/>
				<form:input path="${campPath}" cssClass="span12 input-medium" id="${campPath}" placeholder="${placeholderCodi}"/>
			</div>
			<div class="control-group span3">	
				<c:set var="campPath" value="descripcio"/>
				<spring:message var="placeholderDescripcio" code="servei.list.filtre.camp.descripcio"/>
				<form:input path="${campPath}" cssClass="span12 input-medium" id="${campPath}" placeholder="${placeholderDescripcio}"/>
			</div>
<!-- 			<div class="control-group span3 hidden">	 -->
<%-- 				<c:set var="campPath" value="emissor"/> --%>
<%-- 				<form:select path="${campPath}" id="${campPath}" class="span12"> --%>
<%-- 					<option value=""><spring:message code="servei.list.filtre.camp.emissor"/></option> --%>
<%-- 					<form:options items="${emisors}" itemLabel="nom" itemValue="id"/> --%>
<%-- 				</form:select> --%>
<!-- 			</div> -->
			<div class="control-group span3">	
				<c:set var="campPath" value="activa"/>
				<spring:message var="trueValue" code="servei.list.filtre.camp.activa.yes"/>
				<spring:message var="falseValue" code="servei.list.filtre.camp.activa.no"/>
				<form:select path="${campPath}" class="span12">
					<option value=""><spring:message code="servei.list.filtre.camp.activa"/></option>>
					<form:option value="true">${trueValue}</form:option>>
					<form:option value="false">${falseValue}</form:option>>
				</form:select>
			</div>
		</div>
		<div class="row-fluid">
			<div class="pull-right">
				<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</form:form>
	
	<form>
		<jmesa:tableModel
				id="serveis" 
				items="${serveis}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre">
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
	
	$('#confirm-remove').click(function() {
		  return confirm("<spring:message code="procediment.serveis.confirmacio.desactivacio.servei.procediment"/>");
	});
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
