<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>


<html>
<head>
	<title><spring:message code="procediment.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/css/jquery.dataTables.min.css"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>

	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="procediment.serveis.miques.procediment" arguments="${procediment.nom}"/> </li>
		<li><spring:message code="procediment.serveis.miques.servei" arguments="${servei.descripcio}"/> </li>
		<li class="active"><spring:message code="procediment.serveis.miques.permisos"/></li>
	</ul>

	<c:url var="formAction" value="/procediment/${procediment.id}/servei/${servei.codi}/permis"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well" commandName="procedimentServeiPermisFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<pbl:inputText name="codi" inline="true" placeholderKey="entitat.usuaris.filtre.camp.codi"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="nif" inline="true" placeholderKey="entitat.usuaris.filtre.camp.nif"/>
			</div>
			<div class="col-md-3">
				<pbl:inputText name="nom" inline="true" placeholderKey="entitat.usuaris.filtre.camp.nom"/>
			</div>
			<div class="col-md-3">
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<div class="pull-right">
		<a class="btn btn-default" href="<c:url value="/procediment/${procediment.id}/servei/${servei.codi}/permis/new"/>" data-toggle="modal" data-refresh-pagina="true"><i class="fa fa-plus"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.nou"/></a>
	</div>
	<table id="table-serveis-permis" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="usuari.codi"><spring:message code="procediment.serveis.permisos.taula.columna.usuari.codi" /></th>
				<th data-data="usuari.nif"><spring:message code="procediment.serveis.permisos.taula.columna.usuari.nif" /></th>
				<th data-data="usuari.nom"><spring:message code="procediment.serveis.permisos.taula.columna.usuari.nom" /></th>
<%--				<th data-data="acces"><spring:message code="procediment.serveis.permisos.taula.columna.acces.permes" /></th>--%>
				<th data-data="usuari.codi"></th>
			</tr>
		</thead>
	</table>
	<div>
		<a href="<c:url value="../../servei"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

<%--<script id="template-acces" type="x-tmpl-mustache">--%>
<%--{{#acces}}--%>
<%--<i class="fa fa-check"></i>--%>
<%--{{/acces}}--%>
<%--</script>--%>
<script id="template-accions" type="x-tmpl-mustache">
<%--{{#acces}}--%>
	<a href="permis/{{ usuari.codi }}/deny" class="btn btn-primary">
		<i class="fa fa-times"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.denegar.acces"/>
	</a>
<%--{{/acces}}--%>
<%--{{^acces}}--%>
<%--	<a href="permis/{{ usuari.codi }}/allow" class="btn btn-primary">--%>
<%--		<i class="fa fa-check"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.permetre.acces"/>--%>
<%--	</a>--%>
<%--{{/acces}}--%>
</script>
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

	    $('#table-serveis-permis').DataTable({
	    	autoWidth: false,
	    	// paging: false,
			processing: true,
			serverSide: true,
			// dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
			language: {
	            "url": '<c:url value="/js/datatable-language.json"/>'
	        },
			ajax: '<c:url value="/procediment/${procediment.id}/servei/${servei.codiUrlEncoded}/permis/datatable"/>',
			columnDefs: [
				{
					targets: [0, 1],
					orderable: true,
					width: "20%",
				},
				{
					targets: [2],
					orderable: true,
					// width: "10%",
					// render: function (data, type, row, meta) {
					// 		var template = $('#template-acces').html();
					// 		return Mustache.render(template, row);
					// }
				},
				{
					targets: [3],
					orderable: false,
					width: "1%",
					render: function (data, type, row, meta) {
							var template = $('#template-accions').html();
							return Mustache.render(template, row);
					}
				}, 
		   ],
		});
	});
</script>
<script>
function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}
</script>
</body>
</html>
