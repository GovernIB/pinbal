<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<html>
<head>
	<title><spring:message code="usuari.codi.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="application/javascript">
		$(document).ready(function() {
			// Capturar el canvi de valor a 'codiAntic'
			$('#codiAntic').on('change', function () {
				const codiAntic = $(this).val().trim(); // Obté el valor actual

				// Comprovar si el valor no està buit
				if (codiAntic) {
					// Crida AJAX al servei per obtenir UsuariDto
					$.ajax({
						url: '<c:url value="/usuariajax/usuari/item/"/>' + codiAntic,
						type: 'GET',
						contentType: 'application/json',
						success: function (usuariDto) {
							// Assignar els valors retornats als camps corresponents
							$('#nom').val(usuariDto.nom);
							$('#nif').val(usuariDto.nif);
							$('#email').val(usuariDto.email);
							$('#idioma').val(usuariDto.idioma).trigger('change');
						},
						error: function (xhr, status, error) {
							console.error("Error en obtenir UsuariDto: ", error);
							alert("Error al intentar obtenir la informació de l'usuari amb codi " + codiAntic);
						}
					});
				} else {
					// Si el valor està buit, buidar els camps relacionats
					$('#nom').val('');
					$('#nif').val('');
					$('#email').val('');
					$('#idioma').val('').trigger('change');
				}
			});

			$('#codiNou').on('change', function () {
				const codiNou = $(this).val().trim(); // Obté el valor actual

				// Comprovar si el valor no està buit
				if (codiNou) {
					$.ajax({
						url: '<c:url value="/usuariajax/usuari/item/"/>' + codiNou,
						type: 'GET',
						contentType: 'application/json',
						success: function (usuariDto) {
							if (usuariDto && Object.keys(usuariDto).length !== 0) {
								alert("El nou codi " + codiNou + " ja existeix. \n" +
										"Si utilitza aquest codi, tot el que està assignat dins l'aplicació a l'usuari a modificar quedarà assignat a aquest usuari ja existent.\n\n" +
										"Tengui present que en aquest cas, les dades de l'usuari amb codi " + codiNou + " no es modificaran.");
							}
						},
						error: function (xhr, status, error) {
							console.error("Error en obtenir UsuariDto: ", error);
						}
					});
				}
			});
			$('form').on('submit', function () {
				// Mostra el spinner
				$('#loading-overlay').show();
			});

		});
	</script>
	<style>
		/* Capa semitransparent */
		#loading-overlay {
			position: fixed;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-color: rgba(0, 0, 0, 0.5);
			z-index: 9999; /* Assegura que estigui per sobre de tot */
			display: flex;
			justify-content: center;
			align-items: center;
		}

		#loading-overlay .fa-spinner {
			color: white; /* Color blanc per al spinner */
		}
	</style>

</head>
<body>
	<c:url value="/usuari/username" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariCodiCommand" role="form">
		<div class="row">
			<div class="col-md-6">
				<pbl:inputText name="codiAntic" textKey="usuari.form.camp.antic"/>
			</div>
			<div class="col-md-6">
				<pbl:inputText name="codiNou" textKey="usuari.form.camp.nou"/>
			</div>
		</div>
		<fieldset style="margin-top: 20px;">
			<legend><spring:message code="usuari.form.valors.usuari"/></legend>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputText name="nom" textKey="usuari.form.camp.nom"/>
				</div>
				<div class="col-md-6">
					<pbl:inputText name="nif" textKey="usuari.form.camp.nif"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputText name="email" textKey="usuari.form.camp.email"/>
				</div>
				<div class="col-md-6">
					<pbl:inputSelect name="idioma" optionItems="${idiomaEnumOptions}" textKey="usuari.form.camp.idioma" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="true"/>
				</div>
			</div>
		</fieldset>
		<div id="modal-botons" class="pull-right">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-save"></span>
				<spring:message code="comu.boto.modificar"/>
			</button>
			<a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
	<div id="loading-overlay" style="display: none;">
		<i class="fas fa-spinner fa-spin fa-3x"></i>
	</div>
</body>
</html>
