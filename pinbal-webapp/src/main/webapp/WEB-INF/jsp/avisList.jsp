<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="avis.list.titol"/></title>
	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>


	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	
<script>
$(document).ready(function() {

	let selectedIds = new Set();
	const MSG_NO_SELECTION = '<spring:message code="avis.bulk.no.selection" javaScriptEscape="true"/>';
	const MSG_RES_ENABLE = '<spring:message code="avis.bulk.result.enable" javaScriptEscape="true"/>';
	const MSG_RES_DISABLE = '<spring:message code="avis.bulk.result.disable" javaScriptEscape="true"/>';
	const MSG_RES_DELETE = '<spring:message code="avis.bulk.result.delete" javaScriptEscape="true"/>';
	const showAlert = function(type, message){
		var cls = type === 'success' ? 'alert-success' : (type === 'warning' ? 'alert-warning' : 'alert-info');
		var $alert = $('<div class="alert '+cls+' alert-dismissible" role="alert" style="margin-top:10px;">\
			<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>'+message+'</div>');
		$('#avis-alerts').prepend($alert);
		setTimeout(function(){ $alert.fadeOut(500, function(){ $(this).remove(); }); }, 5000);
	};
	const $table = $('#table-avisos');
	const loadSelection = function(cb){
		$.get('avis/selection/list', function(data){
			selectedIds = new Set(data || []);
			if (cb) cb();
		});
	};
	const addSelection = function(ids){
		if (!ids || ids.length===0) return;
		$.post('avis/selection/add', {ids: ids.join(',')});
		ids.forEach(id => selectedIds.add(id));
		updateHeaderSelectorState();
	};
	const removeSelection = function(ids){
		if (!ids || ids.length===0) return;
		$.post('avis/selection/remove', {ids: ids.join(',')});
		ids.forEach(id => selectedIds.delete(id));
		updateHeaderSelectorState();
	};
	const clearSelection = function(){
		$.post('avis/selection/clear');
		selectedIds.clear();
		updateHeaderSelectorState();
		repaintVisibleSelectors();
	};
	const selectAll = function(){
		$.post('avis/selection/selectAll', function(){ loadSelection(repaintVisibleSelectors); });
	};
	const getVisibleRowIds = function(){
		let ids = [];
		$table.DataTable().rows({page:'current'}).every(function(){
			let data = this.data();
			if (data && data.id != null) ids.push(Number(data.id));
		});
		return ids;
	};
	const repaintVisibleSelectors = function(){
		$table.find('tbody tr').each(function(){
			let $row = $(this);
			let id = Number($row.find('td .row-selector').data('id'));
			let $icon = $row.find('.row-selector i, .row-selector [data-fa-i2svg]');
			if (!$icon.length) return;
			if (selectedIds.has(id)) {
				$icon.removeClass('fa-square').addClass('fa-check-square');
				$row.addClass('selected');
			} else {
				$icon.removeClass('fa-check-square').addClass('fa-square');
				$row.removeClass('selected');
			}
		});
		updateHeaderSelectorState();
	};
	const updateHeaderSelectorState = function(){
		let visible = getVisibleRowIds();
		let selectedVisible = visible.filter(id => selectedIds.has(id)).length;
		let $icon = $('#selectallAdd').find('i, [data-fa-i2svg]');
		if (selectedVisible === 0) {
			$icon.removeClass('fa-check-square fa-minus-square').addClass('fa-square');
		} else if (selectedVisible < visible.length) {
			$icon.removeClass('fa-check-square fa-square').addClass('fa-minus-square');
		} else {
			$icon.removeClass('fa-square fa-minus-square').addClass('fa-check-square');
		}
		$('#seleccioCountAdd').text(selectedIds.size);
		// Enable/disable dropdown actions
		if (selectedIds.size === 0) {
			$('.bulk-action').addClass('disabled');
		} else {
			$('.bulk-action').removeClass('disabled');
		}
	};

	// Initialize header selector icon
	$('#selectallAdd').html('<i class="far fa-square selector" style="cursor:pointer;"></i>');
	$('#selectallAdd').on('click', function(){
		let visible = getVisibleRowIds();
		let selectedVisible = visible.filter(id => selectedIds.has(id)).length;
		if (selectedVisible < visible.length) {
			addSelection(visible);
		} else {
			removeSelection(visible);
		}
		repaintVisibleSelectors();
	});

	$table.on('click', '.row-selector', function(){
		let id = Number($(this).data('id'));
		let $icon = $(this).find('i, [data-fa-i2svg]');
		if (selectedIds.has(id)) {
			removeSelection([id]);
			$icon.removeClass('fa-check-square').addClass('fa-square');
		} else {
			addSelection([id]);
			$icon.removeClass('fa-square').addClass('fa-check-square');
		}
	});

	$('#table-avisos').on('draw.dt', function () {
		$('.confirm-esborrar').click(function() {
			  return confirm("<spring:message code="avis.list.confirmacio.esborrar" javaScriptEscape="true"/>");
			});
		repaintVisibleSelectors();
	});

	var dt = $table.DataTable({
    	autoWidth: false,
		processing: true,
		serverSide: true,
		language: {
            "url": '<c:url value="/js/datatable-language.json"/>',
        },
		ajax: "avis/datatable",
		columns: [
			{ data: 'id' },
			{ data: 'assumpte' },
			{ data: 'dataInici' },
			{ data: 'dataFinal' },
			{ data: 'actiu' },
			{ data: 'info' },
			{ data: 'warning' },
			{ data: 'error' },
			{ data: 'avisNivell' },
			{ data: 'id' }
		],
		columnDefs: [
			{
				targets: 0,
                width: "1%",
                orderable: false,
                render: function (data, type, row, meta) {
                    return '<span class="row-selector" data-id="'+row.id+'" style="cursor:pointer;"><i class="far fa-square"></i></span>';
                }
			},
			{
				targets: [2, 3],
				width: "10%",
				render: $.fn.dataTable.render.moment('DD/MM/YYYY')
			},	
			{
				targets: [4],
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-activa').html();
						return Mustache.render(template, row);
				}
			},
			{ 
		            targets: [5, 6, 7],
				orderable: false,
				visible: false
		        },			
			{
				targets: [8],
				width: "10%",
				render: function (data, type, row, meta) {
						var template = $('#template-nivell').html();
						return Mustache.render(template, row);
				}
			},
			{
				targets: [9],
				orderable: false,
				width: "1%",
				render: function (data, type, row, meta) {
						var template = $('#template-accions').html();
						return Mustache.render(template, row);
				}
			}	

	   ]
	});

	// Toolbar actions
	$('#btn-select-all').on('click', function(){ selectAll(); });
	$('#btn-clear-all').on('click', function(){ clearSelection(); });
	$('#bulk-enable').on('click', function(e){ e.preventDefault(); if (selectedIds.size===0) { showAlert('warning', MSG_NO_SELECTION); return; } $.post('avis/selected/enable', function(data){ dt.ajax.reload(null, false); if (typeof webutilRefreshAvisos === 'function') { webutilRefreshAvisos(); } var n=(data&&data.processed)||0; showAlert('success', MSG_RES_ENABLE.replace('{0}', n)); }); });
	$('#bulk-disable').on('click', function(e){ e.preventDefault(); if (selectedIds.size===0) { showAlert('warning', MSG_NO_SELECTION); return; } $.post('avis/selected/disable', function(data){ dt.ajax.reload(null, false); if (typeof webutilRefreshAvisos === 'function') { webutilRefreshAvisos(); } var n=(data&&data.processed)||0; showAlert('success', MSG_RES_DISABLE.replace('{0}', n)); }); });
	$('#bulk-delete').on('click', function(e){ e.preventDefault(); if (selectedIds.size===0) { showAlert('warning', MSG_NO_SELECTION); return; } if (!confirm('<spring:message code="avis.list.confirmacio.esborrar" javaScriptEscape="true"/>')) return; $.post('avis/selected/delete', function(data){ dt.ajax.reload(null, false); if (typeof webutilRefreshAvisos === 'function') { webutilRefreshAvisos(); } loadSelection(updateHeaderSelectorState); var n=(data&&data.processed)||0; showAlert('success', MSG_RES_DELETE.replace('{0}', n)); }); });

	// Initial load of selection
	loadSelection(function(){ updateHeaderSelectorState(); });
	
});
	
</script>	
</head>
<body>
	<div id="avis-alerts"></div>
	<div class="pull-right" style="margin-bottom:10px;">
		<div class="btn-group" role="group" aria-label="seleccio-avisos" style="margin-right:10px;">
            <button id="btn-select-all" type="button" class="btn btn-default" title="Seleccionar tots els avisos"><i class="far fa-check-square"></i></button>
			<button id="btn-clear-all" type="button" class="btn btn-default" title="Netejar selecció"><i class="far fa-square"></i></button>
			<div class="btn-group" role="group">
				<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><span class="badge" id="seleccioCountAdd" style="margin-left:8px;">0</span>&nbsp;<spring:message code="comu.accions.massives"/>&nbsp;<span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-right">
					<li><a href="#" id="bulk-enable" class="bulk-action"><i class="fas fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a></li>
					<li><a href="#" id="bulk-disable" class="bulk-action"><i class="fas fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
					<li><a href="#" id="bulk-delete" class="bulk-action"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
				</ul>
			</div>
		</div>
		<a class="btn btn-default" href="<c:url value="/avis/new"/>" data-toggle="modal" data-refresh-pagina="true"><i class="fa fa-plus"></i>&nbsp;<spring:message code="avis.list.boto.nova.avis"/></a>
	</div>
	<div class="clearfix"></div>
	<table id="table-avisos" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
                <th data-data="id" id="selectallAdd"></th>
				<th data-data="assumpte"><spring:message code="avis.list.columna.assumpte"/></th>
				<th data-data="dataInici"><spring:message code="avis.list.columna.dataInici"/></th>
				<th data-data="dataFinal"><spring:message code="avis.list.columna.dataFinal"/></th>
				<th data-data="actiu"><spring:message code="avis.list.columna.activa"/></th>
				<th data-data="info"></th>
				<th data-data="warning"></th>
				<th data-data="error"></th>
				<th data-data="avisNivell"><spring:message code="avis.list.columna.avisNivell"/></th>			
				<th data-data="id"></th>
			</tr>
		</thead>
	</table>


<%--<script id="template-check" type="x-tmpl-mustache">--%>
<%--    <span class="far fa-square selector"></span>--%>
<%--</script>--%>
<script id="template-activa" type="x-tmpl-mustache">
    {{#actiu}}
        <span class="fa fa-check"></span>
    {{/actiu}}
</script>
<script id="template-nivell" type="x-tmpl-mustache">
    {{#info}}
        <spring:message code="avis.nivell.enum.INFO"/>
    {{/info}}
    {{#warning}}
        <spring:message code="avis.nivell.enum.WARNING"/>
    {{/warning}}
    {{#error}}
        <spring:message code="avis.nivell.enum.ERROR"/>
    {{/error}}
</script>


<script id="template-accions" type="x-tmpl-mustache">
	<div class="btn-group">
		<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><i class="fas fa-cog"></i>&nbsp;<spring:message code="comu.accions"/>&nbsp;<span class="caret"></span></a>
		<ul class="dropdown-menu dropdown-menu-right">
			<li>
				<a href="avis/{{ id }}" data-toggle="modal" data-refresh-pagina="true"><i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/></a>
			</li>
			<li>
				{{#actiu}}
				<a href="avis/{{ id }}/disable"><i class="fas fa-times"></i>&nbsp;<spring:message code="comu.boto.desactivar"/></a>
				{{/actiu}}
				{{^actiu}}
				<a href="avis/{{ id }}/enable"><i class="fas fa-check"></i>&nbsp;<spring:message code="comu.boto.activar"/></a>
				{{/actiu}}
			</li>
			<li><a href="avis/{{ id }}/delete" class="confirm-esborrar"><i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
		</ul>
	</div>
</script>	
	
	
</body>