<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="servei.list.titol"/></title>
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
				  return confirm("<spring:message code="servei.list.confirmacio.esborrar"/>");
			});
		});
		function showModalProcediments(element) {
			var ample = Math.min(980, (window.innerWidth - 40));
			var alt = Math.round(window.innerHeight * 0.8) - 110;
			
			$('#modal-procediment-list .modal-header h3').html($(element).data("titol"));
			$('#modal-procediment-list').css({	"width": ample + 'px',
												"margin-left": '-' + (ample/2) + 'px'});
			$('#modal-procediment-list .modal-body').css({"max-height": alt + "px"});
			$('#modal-procediment-list .modal-body').load(element.href);
			$('#modal-procediment-list').modal('toggle');
		}
	</script>
	<style type="text/css">
		#modal-procediment-list .modal-body .well {
			display: none;
		}
	</style>
</head>
<body>
	<c:url value="/servei" var="formAction"/>
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
			<div class="control-group span3">	
				<c:set var="campPath" value="emissor"/>
				<form:select path="${campPath}" id="${campPath}" class="span12">
					<option value=""><spring:message code="servei.list.filtre.camp.emissor"/></option>
					<form:options items="${emisors}" itemLabel="nom" itemValue="id"/>
				</form:select>
			</div>
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
				var="registre">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="codi" titleKey="servei.list.taula.columna.codi"/>
					<jmesa:htmlColumn property="descripcio" titleKey="servei.list.taula.columna.descripcio"/>
					<jmesa:htmlColumn property="scspEmisor.nom" titleKey="servei.list.taula.columna.emissor"/>
<%-- 					<jmesa:htmlColumn property="scspFechaBaja" titleKey="servei.list.taula.columna.emissor"/> --%>
					<jmesa:htmlColumn property="ACCIO_procediments" title="&nbsp;" sortable="false" style="white-space:nowrap;">
						<a class="btn" href="<c:url value="/modal/servei/${registre.codi}/procediments"/>" 
							onclick="showModalProcediments(this);return false" data-titol="<spring:message code="servei.procediment.list.titol" arguments="${registre.descripcio}"/>">
							<i class="icon-briefcase"></i>&nbsp;<spring:message code="entitat.list.taula.boto.procediments"/>&nbsp;<span class="badge">${registre.numeroProcedimentsAssociats}</span>
						</a>
					</jmesa:htmlColumn>
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
	
	<div id="modal-procediment-list" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
		</div>
	</div>
<script type="text/javascript">
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
</script>

</body>
</html>
