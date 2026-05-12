<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code="representant.usuaris.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script>
		// Mètodes per a la selecció de permisos
		const numRows = ${fn:length(permisos)};
		let isSelectedAll = () => {
			return $("#selectall").hasClass("selected");
		}
		let selectedRows = () => {
			return $(".selectorpare.selected").length;
		}
		let isSelected = (elem) => {
			return elem.hasClass("selected");
		}
		let clickSelectAll = () => {
			if(isSelectedAll()) {
				$("#selectall").find('[data-fa-i2svg]').removeClass("fa-check-square").addClass("fa-square");
				$(".selectorpare").find('[data-fa-i2svg]').removeClass("fa-check-square").addClass("fa-square");
				$(".selectorpare").removeClass("selected");
			} else {
				$("#selectall").find('[data-fa-i2svg]').removeClass("fa-square").addClass("fa-check-square");
				$(".selectorpare").find('[data-fa-i2svg]').removeClass("fa-square").addClass("fa-check-square");
				$(".selectorpare").addClass("selected");
			}
			$("#selectall").toggleClass("selected");
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
			let $selectall = $("#selectall");
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
			$("#seleccioCount").text(numRowsSelected);
			if (numRowsSelected == 0) {
				$(".confirm-esborrar-selected").addClass("disabled");
			} else {
				$(".confirm-esborrar-selected").removeClass("disabled");
			}
		}
		let selectedNumberClick = () => {
			if (selectedRows() == 0) {
				return false;
			}
			if (confirm("<spring:message code="procediment.serveis.permisos.esborrar.seleccionats.segur"/>")) {
				omplePermisosSeleccionats();
				$("#denySelectedForm").submit();
			}
		}
		let omplePermisosSeleccionats = () => {
			let permisosSeleccionats = [];
			$(".selectorpare.selected").each((index, element) => {
				let permis = {
					procedimentCodi: $(element).data("procediment"),
					serveiCodi: $(element).data("servei")
				};
				permisosSeleccionats.push(permis);
			});
			$("#persisosSeleccionats").val(JSON.stringify(permisosSeleccionats));
		}


		$(document).ready(function() {
			$('.confirm-esborrar').click(function() {
				  return confirm("<spring:message code="procediment.serveis.permisos.esborrar.tots.segur"/>");
			});
			$('.confirm-esborrar-selected').click(() => { return selectedNumberClick($(this)); });

			$('#selectall').click(() => { clickSelectAll(); });
			$('.selectorpare').click((event) => { clickSelectOne($(event.currentTarget)); });
		});
	</script>
	<style>
		.form-inline { display: inline; }
	</style>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="representant.usuaris.permisos.miques.usuari" arguments="${usuari.descripcio}"/></li>
		<li class="active"><spring:message code="representant.usuaris.permisos.miques.permisos"/></li>
	</ul>

	<div class="pull-right" style="margin-bottom: 10px;">
		<a href="<c:url value="/representant/usuari/${usuari.codi}/permis/afegir"/>" class="btn btn-primary" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-plus"></span>
			<spring:message code="representant.usuaris.permisos.afegir"/>
		</a>
	</div>

	<table id="permisos" border="0" cellpadding="0" class="table table-bordered table-striped">
	<thead>
	<tr class="header">
		<th id="selectall"><span class="far fa-square selector"></span></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.procediment.codi"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.procediment.nom"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.servei.codi"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.servei.descripcio"/></div></th>
		<th style="width: 100px;"><div>&nbsp;</div></th>
	</tr>
	</thead>
	<tbody class="tbody">
		<c:forEach var="permis" items="${permisos}">
		<tr>
			<td class="selectorpare" data-procediment="${permis.procediment.codi}" data-servei="${permis.servei.codi}"><span class="far fa-square selector"></span></td>
			<td>
				${permis.procediment.codi} 
			</td>
			<td>
				${permis.procediment.nom} 
			</td>
			<td>
				${permis.servei.codi} 
			</td>
			<td>
				 ${permis.servei.descripcio}
			</td>
			<td>
				<c:url value="/representant/usuari/${usuari.codi}/permis/deny" var="formAction"/>
				<form action="${formAction}" method="post" class="form-inline">
					<input type="hidden" name="procedimentId" value="${permis.procediment.id}"/>
					<input type="hidden" name="serveiCodi" value="${permis.servei.codi}"/>
					<button type="submit" class="btn btn-primary"><i class="icon-remove"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.denegar.acces"/></button>
				</form>
			</td>
		</tr>
		</c:forEach>		
	</tbody>
	</table>

	<div>
		<a href="<c:url value="/representant/usuari/${usuari.codi}/permis/deny/all"/>" class="btn btn-primary confirm-esborrar"><spring:message code="procediment.serveis.permisos.esborrar.tots"/></a>
		<form id="denySelectedForm" action="<c:url value="/representant/usuari/${usuari.codi}/permis/deny/selected"/>" method="post" class="form-inline">
			<input type="hidden" id="persisosSeleccionats" name="persisosSeleccionats"/>
			<button type="button" class="btn btn-primary confirm-esborrar-selected disabled">
				<span id="seleccioCount" class="badge">0</span>
				<spring:message code="procediment.serveis.permisos.esborrar.seleccionats"/>
			</button>
		</form>
		<a href="<c:url value="/representant/usuari"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
