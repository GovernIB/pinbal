<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="emissorcert.list.titol"/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	
	<script>
	$(document).ready(function() {
		/* $('#netejar-filtre').click(function() {
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
		}); */
		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="paramconf.list.confirmacio.esborrar"/>");
		});
	});
	</script>

</head>
<body>

	<div class="row-fluid">
		<div class="span12">
			<a class="btn pull-right" href="<c:url value="/scsp/emissorcert/new"/>"><i class="icon-plus"></i>&nbsp;<spring:message code="emissorcert.list.boto.nou.registre"/></a>
		</div>
		<div class="clearfix"></div>
	</div>

	<form>
		<jmesa:tableModel
				id="emissorcert" 
				items="${llistaEmissors}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
				
					<jmesa:htmlColumn property="nom" titleKey="emissorcert.list.taula.columna.nom" style="width:50%;"/>
					<jmesa:htmlColumn property="cif" titleKey="emissorcert.list.taula.columna.cif" style="width:20%;"/>
					<jmesa:htmlColumn property="dataBaixa" titleKey="emissorcert.list.taula.columna.databaixa" style="width:20%;"/>
					
					<jmesa:htmlColumn property="ACCIO_accions" title="&nbsp;" sortable="false" style="width:10%;white-space:nowrap;">
						<div class="btn-group">
							<a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/scsp/emissorcert/${registre.id}"/>" ><i class="icon-pencil"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="<c:url value="/scsp/emissorcert/${registre.id}/delete"/>" class="confirm-esborrar"><i class="icon-trash"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
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
