<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<%
	request.setAttribute(
			"entitatTipusLlista",
			es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto.values());
%>

<html>
<head>
	<title><spring:message code="entitat.list.titol"/></title>
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
		  return confirm("<spring:message code="entitat.list.confirmacio.esborrar"/>");
	});
});
</script>
</head>
<body>

	<c:url value="/entitat" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well form-inline" commandName="entitatFiltreCommand">
		<c:set var="campPath" value="codi"/>
		<spring:message var="placeholderCodi" code="entitat.list.filtre.camp.codi"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderCodi}"/>
		
		<c:set var="campPath" value="nom"/>
		<spring:message var="placeholderNom" code="entitat.list.filtre.camp.nom"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderNom}"/>
		
		<c:set var="campPath" value="cif"/>
		<spring:message var="placeholderCif" code="entitat.list.filtre.camp.cif"/>
		<form:input path="${campPath}" cssClass="input-medium" id="${campPath}" placeholder="${placeholderCif}"/>
		
		<c:set var="campPath" value="activa"/>
		<spring:message var="trueValue" code="entitat.list.filtre.camp.activa.yes"/>
		<spring:message var="falseValue" code="entitat.list.filtre.camp.activa.no"/>
		<form:select path="${campPath}">
			<option value=""><spring:message code="entitat.list.filtre.camp.activa"/></option>>
			<form:option value="true">${trueValue}</form:option>>
			<form:option value="false">${falseValue}</form:option>>
		</form:select>

		<c:set var="campPath" value="tipus"/>
		<form:select path="${campPath}">
			<option value=""><spring:message code="entitat.list.filtre.camp.tipus"/></option>>
			<c:forEach var="estat" items="${entitatTipusLlista}">
					<form:option value="${estat}">${estat}</form:option>
			</c:forEach>
		</form:select>
				
		<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
	</form:form>

	<div class="row-fluid">
		<div class="span12">
			<a class="btn pull-right" href="<c:url value="/entitat/new"/>"><i class="icon-plus"></i>&nbsp;<spring:message code="entitat.list.boto.nova.entitat"/></a>
		</div>
		<div class="clearfix"></div>
	</div>

	<form>
		<jmesa:tableModel
				id="entitats" 
				items="${entitats}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="codi" titleKey="entitat.list.taula.columna.codi"/>
					<jmesa:htmlColumn property="nom" titleKey="entitat.list.taula.columna.nom">
						<c:if test="${registre.tipus != 'ALTRES'}">
							<c:set var="tipusDescripcio" value="entitat.list.entitat.tipus.${registre.tipus}"/>
							<span class="label" title="<spring:message code="${tipusDescripcio}"/>">${fn:substring(registre.tipus, 0, 3)}</span>
						</c:if>
						${registre.nom}
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="cif" titleKey="entitat.list.taula.columna.cif"/>
					<jmesa:htmlColumn property="tipus" titleKey="entitat.list.taula.columna.tipus"/>
					<jmesa:htmlColumn property="activa" titleKey="entitat.list.taula.columna.activa">
						<c:if test="${registre.activa}"><i class="icon-ok"></i></c:if>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_usuaris" title="&nbsp;" sortable="false" style="white-space:nowrap;">
						<a href="entitat/${registre.id}/usuari" class="btn">
							<i class="icon-user"></i>&nbsp;<spring:message code="entitat.list.taula.boto.usuaris"/>&nbsp;<span class="badge">${fn:length(registre.usuaris)}</span>
						</a>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_serveis" title="&nbsp;" sortable="false" style="white-space:nowrap;">
						<a href="entitat/${registre.id}/servei" class="btn">
							<i class="icon-briefcase"></i>&nbsp;<spring:message code="entitat.list.taula.boto.serveis"/>&nbsp;<span class="badge">${fn:length(registre.serveis)}</span>
						</a>
					</jmesa:htmlColumn>
					<%--jmesa:htmlColumn property="ACCIO_enable_disable" title="&nbsp;" style="white-space:nowrap;">
						<c:choose>
							<c:when test="${not registre.activa}">
								<a href="entitat/${registre.id}/enable" class="btn"><i class="icon-ok"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
							</c:when>
							<c:otherwise>
								<a href="entitat/${registre.id}/disable" class="btn"><i class="icon-remove"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
							</c:otherwise>
						</c:choose>
					</jmesa:htmlColumn>
					<jmesa:htmlColumn property="ACCIO_edit" title="&nbsp;" style="white-space:nowrap;">
						<a href="entitat/${registre.id}" class="btn"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
					</jmesa:htmlColumn>
					<c:if test="${propertyEsborrar}">
						<jmesa:htmlColumn property="ACCIO_delete" title="&nbsp;" style="white-space:nowrap;">
							<a href="entitat/${registre.id}/delete" class="btn confirm-esborrar"><i class="icon-trash"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a>
						</jmesa:htmlColumn>
					</c:if--%>
					<jmesa:htmlColumn property="ACCIO_accions" title="&nbsp;" sortable="false" style="white-space:nowrap;">
						<div class="btn-group">
							<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li>
									<c:choose>
										<c:when test="${not registre.activa}">
											<a href="entitat/${registre.id}/enable"><i class="icon-ok"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
										</c:when>
										<c:otherwise>
											<a href="entitat/${registre.id}/disable"><i class="icon-remove"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
										</c:otherwise>
									</c:choose>
								</li>
								<li><a href="entitat/${registre.id}"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<c:if test="${propertyEsborrar}">
									<li><a href="entitat/${registre.id}/delete" class="confirm-esborrar"><i class="icon-trash"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</c:if>
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
