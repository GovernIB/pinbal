<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code="representant.usuaris.afegir.titol" arguments="${usuari.descripcio}"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script>
		// Mètodes per a la selecció de permisos
		let numRows = ${fn:length(permisos)};
		let $selectProcediment = $('#select-procediment');
		let $permisosBody = $('#permisosBody');

		let isSelectedAll = () => {
			return $("#selectallAdd").hasClass("selected");
		}
		let selectedRows = () => {
			return $(".selectorpare.selected", $permisosBody).length;
		}
		let isSelected = (elem) => {
			return elem.hasClass("selected");
		}
		let clickSelectAll = () => {
			if(isSelectedAll()) {
				$("#selectallAdd").find('[data-fa-i2svg]').removeClass("fa-check-square").addClass("fa-square");
				$(".selectorpare", $permisosBody).find('[data-fa-i2svg]').removeClass("fa-check-square").addClass("fa-square");
				$(".selectorpare", $permisosBody).removeClass("selected");
			} else {
				$("#selectallAdd").find('[data-fa-i2svg]').removeClass("fa-square").addClass("fa-check-square");
				$(".selectorpare", $permisosBody).find('[data-fa-i2svg]').removeClass("fa-square").addClass("fa-check-square");
				$(".selectorpare", $permisosBody).addClass("selected");
			}
			$("#selectallAdd").toggleClass("selected");
			updateEsborrarSelectedLink();
		}
		let clickSelectOne = (elem) => {
			// let procedimentCodi = elem.data("procediment");
			// let serveiCodi = elem.data("servei");
			if (isSelected(elem)) {
				elem.find('[data-fa-i2svg]').removeClass("fa-check-square").addClass("fa-square");
			} else {
				elem.find('[data-fa-i2svg]').removeClass("fa-square").addClass("fa-check-square");
			}
			elem.toggleClass("selected");
			updateSelectordAll();
		}
		let updateSelectordAll = () => {
			let numRowsSelected = selectedRows();
			let $selectall = $("#selectallAdd");
			if (numRowsSelected == 0) {
				$selectall.find('[data-fa-i2svg]').removeClass("fa-check-square").addClass("fa-square");
			} else if (numRowsSelected < numRows) {
				$selectall.find('[data-fa-i2svg]').removeClass("fa-check-square").removeClass("fa-square").addClass("fa-minus-square");
			} else {
				$selectall.find('[data-fa-i2svg]').removeClass("fa-square").addClass("fa-check-square");
			}
			updateEsborrarSelectedLink();
		}
		let updateEsborrarSelectedLink = () => {
			let numRowsSelected = selectedRows();
			$("#seleccioCountAdd", parent.document).text(numRowsSelected);
			if (numRowsSelected == 0) {
				$(".confirm-afegir-selected", parent.document).addClass("disabled");
			} else {
				$(".confirm-afegir-selected", parent.document).removeClass("disabled");
			}
		}
		let selectedNumberClick = () => {
			debugger
			if (selectedRows() == 0) {
				return false;
			}
			omplePermisosSeleccionats();
			return true;
			// $("#addSelectedForm").submit();
		}
		let omplePermisosSeleccionats = () => {
			let permisosSeleccionats = [];
			$(".selectorpare.selected", $permisosBody).each((index, element) => {
				let permis = {
					procedimentCodi: $(element).data("procediment"),
					serveiCodi: $(element).data("servei")
				};
				permisosSeleccionats.push(permis);
			});
			$("#persisosSeleccionats").val(JSON.stringify(permisosSeleccionats));
		}
		let setTaulaBuida = () => {
			numRows = 0;
			$permisosBody.empty();
			$permisosBody.append('<tr><td colspan="5"><spring:message code="representant.usuaris.permisos.afegir.no.serveis"/></td></tr>');
		}
		let getFilaServei = (procedimentServei) => {
			let $newRow = 	$('<tr></tr>');
			let $selector = $('<td class="selectorpare" data-procediment="' + procedimentServei.procedimentCodi + '" data-servei="' + procedimentServei.serveiCodi + '"><span class="far fa-square selector"></span></td>');
			let $procCodi = $('<td>' + procedimentServei.procedimentCodi + '</td>');
			let $procNom = 	$('<td>' + procedimentServei.procedimentNom + '</td>');
			let $servCodi = $('<td>' + procedimentServei.serveiCodi + '</td>');
			let $servDesc = $('<td>' + procedimentServei.serveiDescripcio + '</td>');
			$newRow.append($selector);
			$newRow.append($procCodi);
			$newRow.append($procNom);
			$newRow.append($servCodi);
			$newRow.append($servDesc);
			return $newRow;
		}

		$(document).ready(function() {
			$selectProcediment = $('#select-procediment');
			$permisosBody = $('#permisosBody');
			$selectProcediment.change(function() {
				if ($(this).val()) {
					$selectProcediment.prop("disabled", true);
					var targetUrl = '<c:url value="/representant/usuari/${usuari.codi}/permis"/>/' + $(this).val() + '/serveis/disponibles';
					$.ajax({
						url: targetUrl,
						type:'GET',
						dataType: 'json',
						success: function(procedimentServeis) {
							$selectProcediment.prop("disabled", false);
							$permisosBody.empty();
							$('#auxAlt').remove();

							numRows = procedimentServeis.length;
							if (numRows == 0) {
								setTaulaBuida();
								return;
							}

							$.each(procedimentServeis, function (index, procedimentServei) {
								$permisosBody.append(getFilaServei(procedimentServei));
							});
						},
						error: function (jqXHR, textStatus, errorThrown) {
							setTaulaBuida();
						}
					});
				} else {
					setTaulaBuida();
				}
			});

			$selectProcediment.select2({
				placeholder: '<spring:message code="representant.usuaris.filtre.camp.procediment"/>',
				theme: "bootstrap",
				allowClear: true,
				minimumResultsForSearch: 3
			});

			$('#selectallAdd').click(() => { clickSelectAll(); });
			$('#addSelectedForm').submit(() => { return selectedNumberClick(); });
			$permisosBody.on('click', '.selectorpare', (event) => { clickSelectOne($(event.currentTarget)); });
		});
	</script>
	<style>
		.form-inline { display: inline; }
	</style>
</head>
<body>

	<div class="container-fluid well" style="margin-bottom: 10px; padding: 15px;">
		<select id="select-procediment" name="procedimentId" class="form-control" style="width: 100%;">
			<option value=""><spring:message code="representant.usuaris.permisos.filtre.procediment"/>:</option>
				<c:forEach var="procediment" items="${procediments}">
				<option value="${procediment.id}">(${procediment.codi}) ${procediment.nom}</option>
			</c:forEach>
		</select>
	</div>

	<table id="permisos" border="0" cellpadding="0" class="table table-bordered table-striped">
	<thead>
	<tr class="header">
		<th id="selectallAdd"><span class="far fa-square selector"></span></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.procediment.codi"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.procediment.nom"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.servei.codi"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.servei.descripcio"/></div></th>
	</tr>
	</thead>
	<tbody id="permisosBody" class="tbody">
		<tr><td colspan="5"><spring:message code="representant.usuaris.permisos.afegir.no.serveis"/></td></tr>
	</tbody>
	</table>

	<div id="auxAlt" style="min-height: 500px;"></div>

	<form id="addSelectedForm" action="<c:url value="/modal/representant/usuari/${usuari.codi}/permis/afegir/selected"/>" method="post" class="form-inline">
		<input type="hidden" id="persisosSeleccionats" name="persisosSeleccionats"/>
		<div id="modal-botons">
			<button type="submit" class="btn btn-primary confirm-afegir-selected disabled">
				<span id="seleccioCountAdd" class="badge">0</span>
				<spring:message code="procediment.serveis.permisos.afegir.seleccionats"/>
			</button>
			<a href="<c:url value="/representant/usuari/${usuari.codi}/permis"/>" class="btn btn-default" data-modal-cancel="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.cancelar"/></a>
			<div class="clearfix"></div>
		</div>
	</form>

</body>
</html>
