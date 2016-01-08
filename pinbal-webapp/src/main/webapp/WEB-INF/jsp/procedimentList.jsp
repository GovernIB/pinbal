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
	$('.confirm-esborrar').click(function() {
		  return confirm('<spring:message code="procediment.list.confirmacio.esborrar"/>');
	});
});
</script>
</head>
<body>

	<c:url value="/procediment" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-inline" commandName="procedimentFiltreCommand">
		<c:set var="campPath" value="codi"/>
		<spring:message var="placeholderCodi" code="procediment.list.filtre.camp.codi"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderCodi}"/>
		<c:set var="campPath" value="nom"/>
		<spring:message var="placeholderNom" code="procediment.list.filtre.camp.nom"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderNom}"/>
		<c:set var="campPath" value="departament"/>
		<spring:message var="placeholderDepartament" code="procediment.list.filtre.camp.departament"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderDepartament}"/>
		<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
	</form:form>

	<div class="row-fluid">
		<div class="span12">
			<a class="btn pull-right" href="<c:url value="/procediment/new"/>"><i class="icon-plus"></i>&nbsp;<spring:message code="procediment.list.boto.nou.procediment"/></a>
		</div>
		<div class="clearfix"></div>
	</div>

	<form>
		<jmesa:tableModel
				id="procediments" 
				items="${procediments}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="codi" titleKey="procediment.list.taula.columna.codi"/>
					<jmesa:htmlColumn property="nom" titleKey="procediment.list.taula.columna.nom"/>
					<jmesa:htmlColumn property="departament" titleKey="procediment.list.taula.columna.departament"/>
					<jmesa:htmlColumn property="actiu" titleKey="procediment.list.taula.columna.actiu">
						<c:if test="${registre.actiu}"><i class="icon-ok"></i></c:if>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn sortable="false">
						<c:choose>
							<c:when test="${not registre.actiu}">
								<a href="procediment/${registre.id}/enable" class="btn"><i class="icon-ok"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
							</c:when>
							<c:otherwise>
								<a href="procediment/${registre.id}/disable" class="btn"><i class="icon-remove"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
							</c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_serveis" title="&nbsp;" sortable="false">
						<a href="procediment/${registre.id}/servei" class="btn">
							<i class="icon-briefcase"></i>&nbsp;Serveis <span class="badge">${fn:length(registre.serveisActius)}</span>
						</a>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_edit" title="&nbsp;" sortable="false">
						<a href="procediment/${registre.id}" class="btn"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
					</jmesa:htmlColumn>
					<c:if test="${propertyEsborrar}">
						<jmesa:htmlColumn property="ACCIO_delete" title="&nbsp;" sortable="false">
							<a href="procediment/${registre.id}/delete" class="btn confirm-esborrar"><i class="icon-trash"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a>
						</jmesa:htmlColumn>
					</c:if>
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
