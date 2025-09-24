<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<% pageContext.setAttribute("dadesUsuariActual", es.caib.pinbal.webapp.common.UsuariHelper.getDadesUsuariActual(request)); %>

<html>
<head>
	<title><spring:message code="decorator.menu.consulta.nova"/></title>
<%--	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>--%>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
<%--	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>--%>
<%--	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>--%>
<%--	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>--%>
<%--	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>--%>
<%--	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>--%>
	
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
<%--	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>--%>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<%--	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>--%>
	
<script>
	$(document).ready(function() {
		// $('#netejar-filtre').click(function() {
		// 	$(':input', $('#form-filtre')).each (function() {
		// 		var type = this.type, tag = this.tagName.toLowerCase();
		// 		if (type == 'text' || type == 'password' || tag == 'textarea')
		// 			this.value = '';
		// 		else if (type == 'checkbox' || type == 'radio')
		// 			this.checked = false;
		// 		else if (tag == 'select')
		// 			this.selectedIndex = 0;
		// 	});
		// 	$('#form-filtre').submit();
		// });
		
	    <%--$('#table-serveis').DataTable({--%>
	    <%--	autoWidth: false,--%>
		<%--	processing: true,--%>
		<%--	serverSide: true,--%>
		<%--	dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",--%>
		<%--	language: {--%>
        <%--        "url": '<c:url value="/js/datatable-language.json"/>',--%>
	    <%--    },--%>
		<%--	ajax: "<c:url value="/usuari/${dadesUsuariActual.codi}/serveis/datatable"/>",--%>
		<%--	columnDefs: [--%>
		<%--		{--%>
		<%--			targets: [2],--%>
		<%--			orderable: false,--%>
		<%--			width: "1%",--%>
		<%--			render: function (data, type, row, meta) {--%>
		<%--					var template = $('#template-accions').html();--%>
		<%--					return Mustache.render(template, row);--%>
		<%--			}--%>
		<%--		}--%>
		<%--   ]--%>
		<%--});--%>
	});
</script>
</head>
<body>
	<c:url value="/modal/usuari/${dadesUsuariActual.codi}/servei" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="form-nova-consulta" commandName="consultaCommand">
        <div class ="row">
            <div class="col-md-12">
                <pbl:inputSelect name="procediment" inline="true" placeholderKey="consulta.list.filtre.procediment"
                                 optionItems="${procediments}"
                                 optionValueAttribute="id"
                                 optionTextAttribute="codiNom"
                                 emptyOption="true"
                                 optionMinimumResultsForSearch="0"/>
            </div>
            <div class="col-md-12">
                <pbl:inputSelect name="servei" inline="true" placeholderKey="consulta.list.filtre.servei"
                                 optionItems="${serveis}"
                                 optionValueAttribute="codi"
                                 optionTextAttribute="codiNom"
                                 emptyOption="true"
                                 optionMinimumResultsForSearch="0"/>
            </div>
        </div>
<%--		<div class="row">--%>
<%--			<div class="col-md-4">--%>
<%--				<pbl:inputText name="codi"  inline="true" placeholderKey="servei.list.filtre.camp.codi"/>	--%>
<%--			</div>--%>
<%--			<div class="col-md-4">--%>
<%--				<pbl:inputText name="descripcio"  inline="true" placeholderKey="servei.list.filtre.camp.descripcio"/>--%>
<%--			</div>--%>
<%--			<div class="col-md-4">--%>
<%--				<div class="pull-right">--%>
<%--					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>--%>
<%--					<button type="submit" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>--%>
<%--				</div>--%>
<%--			</div>--%>
<%--		</div>--%>

<%--    <table id="table-serveis" class="table table-striped table-bordered" style="width: 100%">--%>
<%--		<thead>--%>
<%--			<tr>--%>
<%--				<th data-data="codi"><spring:message code="servei.list.taula.columna.codi" /></th>--%>
<%--				<th data-data="descripcio"><spring:message code="servei.list.taula.columna.descripcio" /></th>--%>
<%--				<th data-data="codiUrlEncoded"></th>--%>
<%--			</tr>--%>
<%--		</thead>--%>
<%--	</table>--%>

        <div id="modal-botons">
            <button type="submit" class="btn btn-primary"><span class="fa fa-save"></span> <spring:message code="decorator.menu.consulta.nova"/></button>
            <a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></a>
        </div>

    </form:form>

<%--    <script id="template-accions" type="x-tmpl-mustache">--%>
<%--        <a href="/consulta/{{ codiUrlEncoded }}/new"/>" class="btn btn-primary" data-toggle="dropdown"><i class="fas fa-play"></i>&nbsp;<spring:message code="decorator.menu.consulta.nova"/></a>--%>
<%--    </script>--%>

</body>
</html>
