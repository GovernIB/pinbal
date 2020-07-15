<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%
	request.setAttribute(
			"consultaEstats",
			es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
%>

<html>
<head>
	<title><spring:message code="superauditor.list.titol"/></title>
	<script type="text/javascript"src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
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
	$('#select-procediment').change(function() {
		var targetUrl;
		if ($(this).val())
			targetUrl = '<c:url value="superauditor/serveisPerProcediment"/>/' + $(this).val();
		else
			targetUrl = '<c:url value="superauditor/serveisPerProcediment"/>';
		$.ajax({
		    url:targetUrl,
		    type:'GET',
		    dataType: 'json',
		    success: function(json) {
		    	$('#select-servei').empty();
	        	$('#select-servei').append($('<option>').text('<spring:message code="auditor.list.filtre.servei"/>:'));
		        $.each(json, function(i, value) {
		            $('#select-servei').append($('<option>').text(value.descripcio).attr('value', value.codi));
		        });
		    }
		});
	});
});
</script>
</head>
<body>

	<c:choose>
		<c:when test="${empty entitatActual}">
			<c:url value="/superauditor/entitat/seleccionar" var="formAction"/>
			<form action="<c:url value="/superauditor/entitat/seleccionar"/>" method="post" class="well formbox">
				<select name="entitatId" class="input-sm">
					<option value=""><spring:message code="superauditor.list.select.entitat.seleccioni"/></option>
					<c:forEach var="entitat" items="${entitats}">
						<option value="${entitat.id}">${entitat.nom}</option>
					</c:forEach>
				</select>
				<button type="submit" class="btn-default"><spring:message code="comu.boto.seleccionar"/></button>
			</form>
		</c:when>
		<c:otherwise>
			<form class="well-sm form-inline">
				<input type="text" class="input-sm" value="${entitatActual.nom}" disabled="disabled"/>
				<a href="<c:url value="/superauditor/entitat/deseleccionar"/>" class="btn-default"><spring:message code="comu.boto.canviar"/></a>
			</form>
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty entitatActual}">
		<form:form id="form-filtre" action="" method="post" cssClass="well-sm" commandName="filtreCommand">
			<div class="page-header"><spring:message code="auditor.list.filtre.titol"/></div>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-3">
						<c:set var="campPath" value="scspPeticionId"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.peticion.id"/>">
							</spring:bind>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="procediment"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select id="select-procediment" path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="auditor.list.filtre.procediment"/>:</option>
								<form:options items="${procediments}" itemLabel="nom" itemValue="id"/>
							</form:select>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="servei"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select id="select-servei" path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="auditor.list.filtre.servei"/>:</option>
								<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/>
							</form:select>
						</div>
					</div>
					<div class="col-md-3">
						<c:set var="campPath" value="estat"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<form:select path="${campPath}" cssClass="form-control">
								<option value=""><spring:message code="auditor.list.filtre.estat"/>:</option>
								<c:forEach var="estat" items="${consultaEstats}">
									<c:if test="${not fn:startsWith(estat, 'P')}">
										<form:option value="${estat}">${estat}</form:option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
				</div>
			</div>
			<div class="container-fluid">	
				<div class="row">
					<div class="col-md-4">
						<c:set var="campErrors"><form:errors path="dataInici"/></c:set>
						<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if>
					
						<div class="form-gruop<c:if test="${not empty campErrors}"> error</c:if>">
							<label><spring:message code="auditor.list.filtre.data"/></label>
							<div class="row">
								<div class="col-md-6">
									<c:set var="campPath" value="dataInici"/>
									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
									<spring:bind path="${campPath}">
										<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.data.inici"/>">
										<script>$("#${campPath}").mask("99/99/9999");</script>
									</spring:bind>
								</div>
								<div class="col-md-6">
									<c:set var="campPath" value="dataFi"/>
									<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
									<div class="form-gruop<c:if test="${not empty campErrors}"> error</c:if>">
										<spring:bind path="${campPath}">
											<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.data.fi"/>">
											<script>$("#${campPath}").mask("99/99/9999");</script>
										</spring:bind>
									</div>
								</div>
							</div>
						</div>
				</div>
					<div class="col-md-4">
					<c:set var="campErrors"><form:errors path="funcionariNom"/></c:set>
					<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="funcionariDocument"/></c:set></c:if>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label>&nbsp;</label>
						<div class="row">
							<div class="col-md-6">
								<c:set var="campPath" value="funcionariNom"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<spring:bind path="${campPath}">
									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.funcionari.nom"/>">
								</spring:bind>
							</div>
							<div class="col-md-6">
								<c:set var="campPath" value="funcionariDocument"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<spring:bind path="${campPath}">
									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="form-control" placeholder="<spring:message code="auditor.list.filtre.funcionari.document"/>">
								</spring:bind>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-4" style="text-align:right">
					<label>&nbsp;</label>
					<button id="netejar-filtre" class="btn" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button class=" btn btn-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</form:form>

		<form>
			<jmesa:tableModel
					id="consultes"
					items="${consultes}"
					toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
					view="es.caib.pinbal.webapp.jmesa.BootstrapView"
					var="registre">
				<jmesa:htmlTable>
					<jmesa:htmlRow>
						<jmesa:htmlColumn property="scspPeticionSolicitudId" titleKey="consulta.list.taula.peticion.id">
							${registre.scspPeticionSolicitudId}<c:if test="${registre.recobriment}"> <span class="badge">R</span></c:if>
						</jmesa:htmlColumn>
						<jmesa:htmlColumn property="creacioData" titleKey="auditor.list.taula.data" cellEditor="org.jmesa.view.editor.DateCellEditor" pattern="dd/MM/yyyy HH:mm:ss" />
						<jmesa:htmlColumn property="creacioUsuari.nom" titleKey="auditor.list.taula.usuari"/>
						<jmesa:htmlColumn property="funcionariNomAmbDocument" titleKey="auditor.list.taula.funcionari"/>
						<jmesa:htmlColumn property="procedimentNom" titleKey="auditor.list.taula.procediment"/>
						<jmesa:htmlColumn property="serveiDescripcio" titleKey="auditor.list.taula.servei"/>
						<jmesa:htmlColumn property="estat" titleKey="auditor.list.taula.estat" style="white-space:nowrap;">
							<span<c:if test="${registre.estatError}"> title="${registre.error}"</c:if>>
								<c:choose>
									<c:when test="${registre.estatPendent}"><i class="icon-bookmark"></i></c:when>
									<c:when test="${registre.estatProcessant}"><i class="icon-time"></i></c:when>
									<c:when test="${registre.estatError}"><i class="icon-warning-sign"></i></c:when>
									<c:otherwise><i class="icon-ok"></i></c:otherwise>
								</c:choose>
								${registre.estat}
							</span>
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
	</c:if>

</body>
</html>
