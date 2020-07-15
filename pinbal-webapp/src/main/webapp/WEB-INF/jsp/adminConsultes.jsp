<%@page import="es.caib.pinbal.core.dto.EstadistiquesFiltreDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
	request.setAttribute(
			"consultaEstats",
			es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
%>

<html>
<head>
	<title><spring:message code="admin.consulta.list.titol"/></title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script type="text/javascript"src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/select2.min.js"/>"></script>
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
		$("#select-entitat").select2();
	});
</script>
</head>
<body>

	<c:choose>
		<c:when test="${empty entitatActual}">
			<form action="<c:url value="/admin/consulta/entitat/seleccionar"/>" method="post" class="well well-small form-block">
				<div class="row">
					<div class="col-md-10">
					<label for="id_label_multiple" class="col-md-12">
	  				<select class="form-control" name="entitatId" id="select-entitat" data-toggle="select2" data-netejar="${netejar}" 
								 data-minimumresults="4" data-enum-value="entitatId">
						<option value=""><spring:message code="admin.consulta.list.entitat.seleccio"/></option>
						<c:choose>
							<c:when test="${not empty entitats}">
								<c:forEach var="entitat" items="${entitats}">
									<option value="${entitat.id}">${entitat.nom}</option>
								</c:forEach>
							</c:when>
							<c:otherwise><form:options/></c:otherwise>
						</c:choose>
					</select>
					</label>
					</div>
					<div class="col-md-2">
						<div class="col-md-pull">
						<button type="submit" class="btn btn-primary" style="height: 36px;"><spring:message code="comu.boto.seleccionar"/></button>
						</div>
					</div>
				</div>				
			</form>
		</c:when>
		<c:otherwise>
			<form class="well well-small form-block">
				<input type="text" class="input-xlarge" value="${entitatActual.nom}" disabled="disabled"/>
				<a href="<c:url value="/admin/consulta/entitat/deseleccionar"/>" class="btn"><spring:message code="comu.boto.canviar"/></a>
			</form>
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty entitatActual}">
		<form:form id="form-filtre" action="" method="post" cssClass="well formbox" commandName="filtreCommand">
			<div class="page-header"><spring:message code="admin.consulta.list.filtre.titol"/></div>
			<div class="row">
				<div class="col-md-3">
					<c:set var="campPath" value="scspPeticionId"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="admin.consulta.list.filtre.peticion.id"/>">
						</spring:bind>
					</div>
				</div>
				<div class="col-md-3">
					<c:set var="campPath" value="procediment"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<form:select id="select-procediment" path="${campPath}" cssClass="col-md-12">
							<option value=""><spring:message code="admin.consulta.list.filtre.procediment"/>:</option>
							<form:options items="${procediments}" itemLabel="nom" itemValue="id"/>
						</form:select>
					</div>
				</div>
				<div class="col-md-3">
					<c:set var="campPath" value="servei"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<form:select id="select-servei" path="${campPath}" cssClass="col-md-12">
							<option value=""><spring:message code="admin.consulta.list.filtre.servei"/>:</option>
							<form:options items="${serveis}" itemLabel="descripcio" itemValue="codi"/>
						</form:select>
					</div>
				</div>
				<div class="col-md-3">
					<c:set var="campPath" value="estat"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<form:select path="${campPath}" cssClass="col-md-12">
							<option value=""><spring:message code="admin.consulta.list.filtre.estat"/>:</option>
							<c:forEach var="estat" items="${consultaEstats}">
								<c:if test="${not fn:startsWith(estat, 'P')}">
									<form:option value="${estat}">${estat}</form:option>
								</c:if>
							</c:forEach>
						</form:select>
					</div>
				</div>
			</div>
			<div class="row>
				<div class="col-md-4">
					<c:set var="campErrors"><form:errors path="dataInici"/></c:set>
					<c:if test="${empty campErrors}"><c:set var="campErrors"><form:errors path="dataFi"/></c:set></c:if>
					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label><spring:message code="admin.consulta.list.filtre.data"/></label>
						<div class="row">
							<div class="col-md-6">
								<c:set var="campPath" value="dataInici"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>12/12/1210
								<spring:bind path="${campPath}">
									<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="auditor.list.filtre.data.inici"/>
									<script>$("#${campPath}").mask("99/99/9999");</script>
								</spring:bind>
							</div>
							<div class="col-md-6">
								<c:set var="campPath" value="dataFi"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
									<spring:bind path="${campPath}">
										<input type="text" id="${campPath}" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="auditor.list.filtre.data.fi"/>">
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
									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="admin.consulta.list.filtre.funcionari.nom"/>">
								</spring:bind>
							</div>
							<div class="col-md-6">
								<c:set var="campPath" value="funcionariDocument"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<spring:bind path="${campPath}">
									<input type="text" name="${campPath}"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="col-md-12" placeholder="<spring:message code="admin.consulta.list.filtre.funcionari.document"/>">
								</spring:bind>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-4" style="text-align:right">
					<label>&nbsp;</label>
					<button id="netejar-filtre" class="btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button class="btn btn-primary" type="submit"><spring:message code="comu.boto.filtrar"/></button>
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
						<jmesa:htmlColumn property="scspPeticionSolicitudId" titleKey="admin.consulta.list.taula.peticion.id">
							${registre.scspPeticionSolicitudId}<c:if test="${registre.recobriment}"> <span class="badge">R</span></c:if>
						</jmesa:htmlColumn>
						<jmesa:htmlColumn property="creacioData" titleKey="admin.consulta.list.taula.data" cellEditor="org.jmesa.view.editor.DateCellEditor" pattern="dd/MM/yyyy HH:mm:ss" />
						<jmesa:htmlColumn property="creacioUsuari.nom" titleKey="admin.consulta.list.taula.usuari"/>
						<jmesa:htmlColumn property="funcionariNomAmbDocument" titleKey="admin.consulta.list.taula.funcionari"/>
						<jmesa:htmlColumn property="procedimentNom" titleKey="admin.consulta.list.taula.procediment"/>
						<jmesa:htmlColumn property="serveiDescripcio" titleKey="admin.consulta.list.taula.servei"/>
						<jmesa:htmlColumn property="estat" titleKey="admin.consulta.list.taula.estat" style="white-space:nowrap;">
							<span<c:if test="${registre.estatError}"> title="${registre.error}"</c:if>>
								<c:choose>
									<c:when test="${registre.estatPendent}"><i class="glyphicon-bookmark"></i></c:when>
									<c:when test="${registre.estatProcessant}"><i class="glyphicon-time"></i></c:when>
									<c:when test="${registre.estatError}"><i class="icon-warning-sign"></i></c:when>
									<c:otherwise><i class="icon-ok"></i></c:otherwise>
								</c:choose>
								${registre.estat}
							</span>
						</jmesa:htmlColumn>
						<jmesa:htmlColumn property="ACCIO_detalls" title="&nbsp;" style="white-space:nowrap;" sortable="false">
							<a href="consulta/${registre.id}" class="btn-default"><i class="glyphicon-zoom-in"></i>&nbsp;<spring:message code="admin.consulta.list.taula.detalls"/></a>
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
