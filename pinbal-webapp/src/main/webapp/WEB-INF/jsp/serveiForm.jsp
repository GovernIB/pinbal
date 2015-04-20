<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	request.setAttribute(
			"entitatTipusLlista",
			es.caib.pinbal.core.dto.ServeiDto.EntitatTipusDto.values());
	request.setAttribute(
			"justificantTipusLlista",
			es.caib.pinbal.core.dto.ServeiDto.JustificantTipusDto.values());
%>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty serveiCommand.codi}"><spring:message code="servei.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="servei.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
<script>
$(document).ready(function() {
	$('#modal-boto-submit').click(function() {
		$('#modal-redir-form .modal-body').load(
			'<c:url value="/modal/servei/${serveiCommand.codi}/redir/save"/>',
			$('#modal-form').serializeArray());
	});
	$('.confirm-esborrar').click(function() {
		  return confirm("<spring:message code="servei.form.bus.confirmacio.esborrar"/>");
	});
});
function showModalRedir(element) {
<c:choose>
	<c:when test="${not empty serveiCommand.pinbalEntitatTipus}">
		$('#modal-redir-form .modal-body').load(element.href);
		$('#modal-redir-form').modal('toggle');
	</c:when>
	<c:otherwise>
		alert("<spring:message code="servei.form.alert.no.entitat.tipus"/>");
	</c:otherwise>
</c:choose>
}
</script>
</head>
<body>

	<c:url value="/servei" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="serveiCommand">
		<form:hidden path="creacio"/>
		<fieldset>
			<div class="row-fluid">
				<div class="span12">
					<c:set var="campPath" value="codi"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.codi"/> *</label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
					<c:set var="campPath" value="descripcio"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.descripcio"/> *</label>
						<div class="controls">
							<form:textarea rows="8" path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
					<c:set var="campPath" value="scspEmisor"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.emisor"/> *</label>
						<div class="controls">
							<form:select path="${campPath}" cssClass="span12" id="${campPath}">
								<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
								<form:options items="${emisors}" itemLabel="nom" itemValue="cif"/>
							</form:select>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.param.pinbal"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row-fluid">
				<div class="span6">
					<pbl:inputText name="pinbalCustodiaCodi" textKey="servei.form.camp.pinbal.custodia.codi"/>
				</div>
				<div class="span6">
					<pbl:inputText name="pinbalRoleName" textKey="servei.form.camp.pinbal.role.name"/>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<pbl:inputText name="pinbalCondicioBusClass" textKey="servei.form.camp.pinbal.condicio.bus.class"/>
				</div>
				<div class="span6">
					<pbl:inputSelect name="pinbalEntitatTipus" textKey="servei.form.camp.pinbal.entitat.tipus" optionsModelKey="entitatTipusLlista" emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<pbl:inputSelect name="pinbalJustificantTipus" textKey="servei.form.camp.pinbal.justificant.tipus" optionsModelKey="justificantTipusLlista"/>
				</div>
				<div class="span6">
					<pbl:inputText name="pinbalJustificantXpath" textKey="servei.form.camp.pinbal.justificant.xpath"/>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="control-group">
						<label class="control-label"><spring:message code="servei.form.camp.pinbal.camps.dadesgen"/></label>
						<div class="controls">
							<c:set var="campPath" value="pinbalActiuCampNom"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.nom"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampLlinatge1"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.llinatge1"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampLlinatge2"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.llinatge2"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampNomComplet"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.nomcomp"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampDocument"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.document"/>
							</label>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="control-group">
						<label class="control-label"><spring:message code="servei.form.camp.pinbal.document.tipus"/></label>
						<div class="controls">
							<c:set var="campPath" value="pinbalPermesDocumentTipusDni"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.DNI"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusNif"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.NIF"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusCif"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.CIF"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusNie"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.NIE"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusPas"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.Passaport"/>
							</label>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="pinbalDocumentObligatori"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.pinbal.document.obligatori"/></label>
						<div class="controls">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="pinbalComprovarDocument"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.pinbal.comprovar.document"/></label>
						<div class="controls">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<c:if test="${not empty serveiCommand.codi}">
			<fieldset>
				<legend><spring:message code="servei.form.legend.config.bus"/></legend>
				<div class="clearfix legend-margin-bottom"></div>
				<div class="row-fluid">
					<div class="span11 offset1">
						<a class="btn pull-right" href="<c:url value="/modal/servei/${serveiCommand.codi}/redir/new"/>" onclick="showModalRedir(this);return false"><i class="icon-plus"></i>&nbsp;<spring:message code="servei.form.boto.nova.redireccio"/></a>
					</div>
				</div>
				<c:if test="${not empty serveisBus}">
					<div class="row-fluid">
						<div class="span11 offset1">
							<br/>
							<jmesa:tableModel
									id="serveisBus"
									items="${serveisBus}"
									view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
									var="registre"
									maxRows="${fn:length(serveisBus)}">
								<jmesa:htmlTable>
									<jmesa:htmlRow>
										<jmesa:htmlColumn property="entitat.nom" titleKey="servei.form.bus.taula.columna.entitat"/>
										<jmesa:htmlColumn property="urlDesti" titleKey="servei.form.bus.taula.columna.url"/>
										<jmesa:htmlColumn property="ACCIO_edit" title="&nbsp;" style="white-space:nowrap;">
											<a href="../modal/servei/${registre.servei}/redir/${registre.id}" title="<spring:message code="comu.boto.modificar"/>" class="btn" onclick="showModalRedir(this);return false"><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
										</jmesa:htmlColumn>
										<jmesa:htmlColumn property="ACCIO_delete" title="&nbsp;" style="white-space:nowrap;">
											<a href="${registre.servei}/redir/${registre.id}/delete" class="btn confirm-esborrar"><i class="icon-trash"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a>
										</jmesa:htmlColumn>
						            </jmesa:htmlRow>
						        </jmesa:htmlTable>
							</jmesa:tableModel>
						</div>
					</div>
				</c:if>
			</fieldset>
		</c:if>
		<fieldset>
			<legend><spring:message code="servei.form.legend.validesa.servei"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row-fluid">
				<div class="span6">
					<pbl:inputText name="scspFechaAlta" textKey="servei.form.camp.scsp.fecha.alta"/>
					<script>$("#scspFechaAlta").mask("99/99/9999");</script>
				</div>
				<div class="span6">
					<pbl:inputText name="scspFechaBaja" textKey="servei.form.camp.scsp.fecha.baja"/>
					<script>$("#scspFechaBaja").mask("99/99/9999");</script>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<pbl:inputText name="scspCaducidad" textKey="servei.form.camp.scsp.caducidad"/>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.ubicacio.servei"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row-fluid">
				<div class="span12">
					<c:set var="campPath" value="scspUrlSincrona"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.url.sincrona"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}" cssStyle="width:100%"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<c:set var="campPath" value="scspUrlAsincrona"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.url.asincrona"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}" cssStyle="width:100%"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspActionSincrona"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.action.sincrona"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspActionAsincrona"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.action.asincrona"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspActionSolicitud"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.action.solicitud"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspVersionEsquema"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.version.esquema"/> *</label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspEsquemas"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.esquemas"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.xifrat.seguretat"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspTipoSeguridad"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.tipo.seguridad"/> *</label>
						<div class="controls">
							<form:select path="${campPath}" cssClass="span12" id="${campPath}">
								<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
								<form:option value="XMLSignature"/>
								<form:option value="WS-Security"/>
							</form:select>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspAlgoritmoCifrado"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.algoritmo.cifrado"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspValidacionFirma"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.validacion.firma"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspClaveFirma"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.clave.firma"/> *</label>
						<div class="controls">
							<form:select path="${campPath}" cssClass="span12" id="${campPath}">
								<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
								<form:options items="${clausPrivades}" itemLabel="nom" itemValue="alies"/>
							</form:select>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspClaveCifrado"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.clave.cifrado"/></label>
						<div class="controls">
							<form:select path="${campPath}" cssClass="span12" id="${campPath}">
								<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
								<form:options items="${clausPubliques}" itemLabel="nom" itemValue="alies"/>
							</form:select>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspXpathCifradoSincrono"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.cifrado.sincrono"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspXpathCifradoAsincrono"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.cifrado.asincrono"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.config.enviaments"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspPrefijoPeticion"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.prefijo.peticion"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspPrefijoIdTransmision"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.prefijo.id.transmision"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspNumeroMaximoReenvios"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.num.max.reenvios"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspMaxSolicitudesPeticion"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.max.solicitudes.peticion"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspXpathCodigoError"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.codigo.error"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
				<div class="span6">
					<c:set var="campPath" value="scspXpathLiteralError"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.literal.error"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<c:set var="campPath" value="scspTimeout"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.timeout"/></label>
						<div class="controls">
							<form:input path="${campPath}" cssClass="span12" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-inline"/>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<div class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/servei"/>" class="btn"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

	<div id="modal-redir-form" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="servei.form.modal.bus.titol"/></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" id="modal-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>

</body>
</html>
