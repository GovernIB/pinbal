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
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>

	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>

	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="procediment.serveis.miques.procediment" arguments="${procediment.nom}"/> </li>
		<li class="active"><spring:message code="procediment.serveis.miques.serveis"/></li>
	</ul>

	<c:url value="/procediment/${procediment.id}/servei" var="formAction"/>
	<form:form id="form-filtre" action="${formAction}" method="post" cssClass="well" commandName="serveiFiltreCommand">
		<div class="row">
			<div class="col-md-3">	
				<pbl:inputText name="codi" inline="true" placeholderKey="servei.list.filtre.camp.codi"/>
			</div>
			<div class="col-md-3">	
				<pbl:inputText name="descripcio" inline="true" placeholderKey="servei.list.filtre.camp.descripcio"/>
			</div>
			<div class="col-md-3">
				<c:set var="campPath" value="activa"/>
				<spring:message var="trueValue" code="entitat.list.filtre.camp.activa.yes"/>
				<spring:message var="falseValue" code="entitat.list.filtre.camp.activa.no"/>
				<form:select path="${campPath}" class="form-control" data-toggle="select2"
							 data-minimumresults="5">
					<option value=""><spring:message code="entitat.list.filtre.camp.activa"/></option>>
					<form:option value="true">${trueValue}</form:option>>
					<form:option value="false">${falseValue}</form:option>>
				</form:select>
			</div>
			<div class="col-md-3">
				<div class="pull-right">
					<button id="netejar-filtre" class="btn btn-default" type="button"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<table id="table-serveis" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
				<th data-data="codi"><spring:message code="procediment.serveis.taula.columna.codi" /></th>
				<th data-data="descripcio"><spring:message code="procediment.serveis.taula.columna.descripcio" /></th>
				<th data-data="actiu"><spring:message code="procediment.serveis.taula.columna.actiu" /></th>
				<th data-data="procedimentCodi"><spring:message code="procediment.serveis.taula.columna.procediment.codi.adicional" /></th>
				<th data-data="actiu"><spring:message code="entitat.list.taula.columna.activa" /></th>
				<th></th>
			</tr>
		</thead>
	</table>

	<div>
		<a href="<c:url value="/procediment"/>" class="btn btn-primary pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>
<script>
$(document).ready(function() {
	$('.confirm-remove').click(function() {
		  return confirm("<spring:message code="procediment.serveis.confirmacio.desactivacio.servei.entitat"/>");
	});
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

	
	$('#confirm-remove').click(function() {
		  return confirm("<spring:message code="procediment.serveis.confirmacio.desactivacio.servei.procediment"/>");
	});

    $('#table-serveis').DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>'
        },
		ajax: '<c:url value="/procediment/${procediment.id}/servei/datatable"/>',
		columnDefs: [
			{
				targets: [2],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-activa').html();
						console.log(row)
						return Mustache.render(template, row);
				}
			},
			{
				targets: [3],
				orderable: false,
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-procediment').html();
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [4],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-status').html();
						return Mustache.render(template, row);
				}
			}, 
			{
				targets: [5],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-permisos').html();
						return Mustache.render(template, row);
				}
			}, 
	   ],
	   initComplete: function( settings, json ) {
			$('button.edit-codi-procediment').click(function() {
				console.log("uep!");
				var servei_codi = $(this).data('codi-servei');
				var $input = $('#procedimentCodi_' + servei_codi);
				if ($input.prop('disabled')) {
					$input.prop('disabled',false);
					$(this).html('<i class="fa fa-check"></i>');
					$input.focus();
					$input.select();
				} else {
					console.log(servei_codi);
					console.log($input.val());
					actualitzaCodiProcediment(servei_codi, $input.val());
					$input.prop('disabled',true);
					$(this).html('<i class="fas fa-pencil-alt"></i>');
				}
			});
		}
	});
   
});

function onInvokeAction(id) {
	setExportToLimit(id, '');
	createHiddenInputFieldsForLimitAndSubmit(id);
}

function actualitzaCodiProcediment(servei_codi, codi_procediment) {
	$.ajax({
	    url:'<c:url value="servei/' + servei_codi + '/procedimentCodi"/>',
	    type:'GET',
	    dataType: 'json',
	    data: {procedimentCodi: codi_procediment},
	    success: function(result) {
    		console.log(result);
	    }
	});	
}
</script>
<script id="template-activa" type="x-tmpl-mustache">
{{#actiu}}
<i class="fa fa-check"></i>
{{/actiu}}
</script>
<script id="template-procediment" type="x-tmpl-mustache">
  <div class="input-group">
  <input class="form-control" id="procedimentCodi_{{ codi }}" type="text" value="{{ procedimentCodi }}" disabled>
  <div class="input-group-btn">
      <button class="btn btn-default edit-codi-procediment" type="button" data-codi-servei="{{ codi }}">
        <i class="fas fa-pencil-alt"></i>
      </button>
  </div>
  </div>
</script>
<script id="template-status" type="x-tmpl-mustache">
	{{#actiu}}
		<a href="servei/{{ codi }}/disable" class="btn btn-default confirm-remove"><i class="fa fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
	{{/actiu}}
	{{^actiu}}
		<a href="servei/{{ codi }}/enable" class="btn btn-default"><i class="fa fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
	{{/actiu}}
</script>
<script id="template-permisos" type="x-tmpl-mustache">
	{{#actiu}}
		<a href="servei/{{ codi }}/permis" class="btn btn-primary"><i class="icon-lock"></i>&nbsp;<spring:message code="procediment.serveis.taula.boto.permisos"/></a>
	{{/actiu}}
	{{^actiu}}
		<a href="#" class="btn btn-primary disabled"><i class="icon-lock"></i>&nbsp;<spring:message code="procediment.serveis.taula.boto.permisos"/></a>
	{{/actiu}}
</script>
</body>
</html>
