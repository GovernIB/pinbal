<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%
	request.setAttribute("consultaEstats", es.caib.pinbal.core.dto.ConsultaDto.EstatTipus.sortedValues());
	request.setAttribute("historicSession", es.caib.pinbal.webapp.controller.AuditorController.SESSION_CONSULTA_HISTORIC);
%>
<html>
<head>
	<title><spring:message code="auditor.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/js/ios-checkbox/iosCheckbox.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/js/ios-checkbox/iosCheckbox.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>

	<script type="application/javascript">
		function checkCallback() {
			// historicColor();
			$("#filtrar").click();
		}

		function historicColor() {
			let historic = $("#titolCheck").prop("checked");
			if (historic) {
				$(".container-caib > .panel-default > .panel-body").addClass("panel-historic");
				$(".dataTables_info").addClass("table-info-historic");
			} else {
				$(".container-caib > .panel-default > .panel-body").removeClass("panel-historic")
				$(".dataTables_info").removeClass("table-info-historic");
			}
		}

		function formatState(estat) {

			const msgError = '<spring:message code="consulta.list.estat.Error"/>';
			const msgPendent = '<spring:message code="consulta.list.estat.Pendent"/>';
			const msgProcessant = '<spring:message code="consulta.list.estat.Processant"/>';
			const msgTramitada = '<spring:message code="consulta.list.estat.Tramitada"/>';

			if (estat.id=='Error') {
				return $('<div><span class="fas fa-exclamation-triangle"></span> <span>' + msgError + '</span></div>');
			} else if(estat.id=='Pendent') {
				return $('<div><span class="far fa-clock"></span>  <span>' + msgPendent + '</span></div>');
			} else if(estat.id=='Processant') {
				return $('<div><span class="fa fa-cogs"></span>  <span>' + msgProcessant + '</span></div>');
			} else if(estat.id=='Tramitada') {
				return $('<div><span class="fa fa-check"></span>  <span>' + msgTramitada + '</span></div>');
			} else {
				return estat.text;
			}
		}

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
				$('#usuari').val('');
				$('#form-filtre').submit();
			});
			$('#select-procediment').change(function() {
				var targetUrl;
				if ($(this).val())
					targetUrl = '<c:url value="auditor/serveisPerProcediment"/>/' + $(this).val();
				else
					targetUrl = '<c:url value="auditor/serveisPerProcediment"/>';
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
			$('#table-consultes').DataTable({
				autoWidth: false,
				processing: true,
				serverSide: true,
				dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
				"order": [[ 1, "desc" ]],
				language: {
					"url": '<c:url value="/js/datatable-language.json"/>'
				},
				ajax: '<c:url value="/auditor/datatable/"/>',
				columnDefs: [
					{
						targets: [0],
						width: "10%",
						render: function (data, type, row, meta) {
								var template = $('#template-id-peticion').html();
								return Mustache.render(template, row);
						}
					}, {
						targets: [1],
						width: "10%",
						render: $.fn.dataTable.render.moment('x', 'DD/MM/YYYY HH:mm:ss', 'es' )
					// }, {
					// 	targets: [3, 5],
					// 	orderable: false,
					}, {
						targets: [6],
						orderable: false,
						width: "10%",
						render: function (data, type, row, meta) {
								var template = $('#template-estat').html();
								row['icon-status'] = '';
								if (row.estat=='Error'){
									row['icon-status'] = '<i class="fas fa-exclamation-triangle"></i>';
								} else if(row.estat=='Pendent'){
									row['icon-status'] = '<i class="far fa-clock"></i>';
								} else if(row.estat=='Processant'){
									row['icon-status'] = '<i class="fa fa-cogs"></i>';
								} else{
									row['icon-status'] = '<i class="fa fa-check"></i>';
								}
								return Mustache.render(template, row);
						}
					}, {
						targets: [7],
						orderable: false,
						width: "1%",
						render: function (data, type, row, meta) {
							var template = $('#template-details').html();
							return Mustache.render(template, row);
						}
					}
				],
				initComplete: function( settings, json ) {
				}
			});

			historicColor();
		});
</script>
</head>
<body>
	<div class="text-right" data-toggle="titol-check" data-titol-check-value="${historic}" data-titol-check-session-name="${historicSession}" data-titol-check-callback="checkCallback" data-titol-check-label="<spring:message code="comu.historic"/>"></div>
	<form:form id="form-filtre" action="" method="post" cssClass="well form-filtre-table" commandName="filtreCommand">
		<div class="row">
			<div class="col-md-3">
			<pbl:inputText name="scspPeticionId"  inline="true" placeholderKey="consulta.list.filtre.peticion.id"/>
			</div>
			<div class="col-md-3">
				<pbl:inputSelect name="procediment"  inline="true" placeholderKey="consulta.list.filtre.procediment" 
 								optionItems="${procediments}"  
 								optionValueAttribute="id" 
 								optionTextAttribute="codiNom"
 								emptyOption="true"
								optionMinimumResultsForSearch="0"/> 
			</div>
			<div class="col-md-3">
				<pbl:inputSelect name="servei"  inline="true" placeholderKey="consulta.list.filtre.servei" 
								optionItems="${serveis}" 
								optionValueAttribute="codi"
								optionTextAttribute="codiNom"
								emptyOption="true"
								optionMinimumResultsForSearch="0"/>
			</div>
			<div class="col-md-3">
				<pbl:inputSelect name="estat"  inline="true" placeholderKey="consulta.list.filtre.estat" 
								 optionItems="${consultaEstats}" 
								 emptyOption="true"
								 formatResult="formatState"
								 formatSelection="formatState"/>
			</div>
		</div>
		<div class="row">	
			<div class="col-md-3" >
				<div class="row">
					<div class="col-md-6" >
						<pbl:inputDate name="dataInici" inline="true" placeholderKey="auditor.list.filtre.data.inici"/>
					</div>		
					<div class="col-md-6" >
						<pbl:inputDate name="dataFi" inline="true" placeholderKey="auditor.list.filtre.data.fi"/>
					</div>		
				</div>		
			</div>
			<div class="col-md-3">
				<pbl:inputText name="funcionari" inline="true" placeholderKey="auditor.list.filtre.funcionari"/>
			</div>
			<div class="col-md-3">
				<c:url value="/usuariajax/usuari" var="urlConsultaUsuaris"/>
				<pbl:inputSuggest
						name="usuari"
						urlConsultaInicial="${urlConsultaUsuaris}"
						urlConsultaLlistat="${urlConsultaUsuaris}"
						placeholderKey="admin.consulta.list.filtre.usuari"
						suggestValue="codi"
						suggestText="nom"
						inline="true"/>
			</div>
			<div class="col-md-3">
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button id="filtrar" class="btn btn-primary" type="submit"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>	
	</form:form>
	<div class="pull-right">		
		<a class="btn btn-default" href="auditor/excel"><i class="far fa-file-excel"></i>&nbsp;<spring:message code="estadistiques.list.exportar.excel"/></a>	
	</div>
	<table id="table-consultes" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="scspPeticionId"><spring:message code="consulta.list.taula.peticion.id" /></th>
				<th data-data="creacioData"><spring:message code="auditor.list.taula.data" /></th>
				<th data-data="creacioUsuari.nomCodi"><spring:message code="auditor.list.taula.usuari" /></th>
				<th data-data="funcionariNomAmbDocument"><spring:message code="auditor.list.taula.funcionari" /></th>
				<th data-data="procedimentCodiNom"><spring:message code="auditor.list.taula.procediment" /></th>
				<th data-data="serveiCodiNom"><spring:message code="auditor.list.taula.servei" /></th>
				<th data-data="estat"><spring:message code="auditor.list.taula.estat" /></th>
				<th data-data="id"></th><%-- 7 --%>
				<th data-data="recobriment" data-visible="false"></th>
				<th data-data="multiple" data-visible="false"></th>
			</tr>
		</thead>
	</table>

	<br/>
	<a href="auditor/excelConsultes" class="btn btn-default">
		<i class="far fa-file-excel"></i>&nbsp;<spring:message code="auditor.list.exportar.excel"/>
	</a>
	<a href="auditor/csvConsultes" class="btn btn-default">
		<i class="fa fa-file-csv"></i>&nbsp;<spring:message code="auditor.list.exportar.csv"/>
	</a>

<script id="template-id-peticion" type="x-tmpl-mustache">
{{scspPeticionId}}
{{#recobriment}}
	<span class="badge" title="<spring:message code="admin.consulta.list.recobriment"/>">R</span>
{{/recobriment}}
{{#multiple}}
	<span class="badge" title="<spring:message code="admin.consulta.list.multiple"/>">M</span>
{{/multiple}}
</script>
<script id="template-estat" type="x-tmpl-mustache">
	{{{ icon-status }}} {{ estat }}
</script>
<script type="text/javascript">
	function onInvokeAction(id) {
		setExportToLimit(id, '');
		createHiddenInputFieldsForLimitAndSubmit(id);
	}
</script>
	<script id="template-details" type="x-tmpl-mustache">
		<a href="consulta/{{ id }}" class="btn btn-default" data-toggle="modal"><i class="fas fa-search-plus"></i>&nbsp;<spring:message code="admin.consulta.list.taula.detalls"/></a>
	</script>
</body>
</html>
