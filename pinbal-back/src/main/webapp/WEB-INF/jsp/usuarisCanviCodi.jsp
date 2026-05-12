<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<html>
<head>
	<title><spring:message code="usuari.codis.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="application/javascript">
		$(document).ready(function () {
			$('#modificarButton').click(function () {
				processCodis();
			});
		});

		function processCodis() {
			// Deshabilitar els botons mentre es processa
			toggleButtons(false);

			// Obtenir les línies del "textarea"
			const lines = $('#codis').val().split('\n').filter(line => line.trim() !== '');
			const processDiv = $('#change-process');
			processDiv.empty();

			// Processar línia a línia
			const processNextLine = (index) => {
				if (index >= lines.length) {
					toggleButtons(true); // Habilitar els botons un cop acabat el processament
					return; // Finalitzar si totes les línies ja s'han processat
				}

				const line = lines[index];
				const lineRow = $('<div class="col-md-12 row mb-2"></div>');
				const parseResult = /(.+)=(.+)/.exec(line);

				if (!parseResult) {
					// Format incorrecte
					lineRow.html(
							'<div class="col-md-4 processing-line">' +
							'<span>' + line + ' - <spring:message code="usuari.codi.processant"/> (' + line + ')</span>' +
							'</div>' +
							'<div class="col-md-8 result">' +
							'<i class="fa fa-times text-danger"></i> ' +
							'<span class="result-info text-danger"><spring:message code="usuari.codi.format.error"/></span>' +
							'</div>'
					);
					processDiv.append(lineRow);
					processNextLine(index + 1); // Saltar a la següent línia
					return;
				}


				const [_, codiAntic, codiNou] = parseResult;
				lineRow.html(
						'<div class="col-md-4 processing-line">' +
						'<span><spring:message code="usuari.codi.processant"/> <strong>' + codiAntic + ' -> ' + codiNou + '</strong></span> ' +
						'</div>' +
						'<div class="col-md-8 result">' +
						'<i class="fa fa-spinner fa-spin status-icon"></i> ' +
						'</div>' );
				processDiv.append(lineRow);

				// Validar els codis via AJAX
				$.post('<c:url value="/usuari/usernames/"/>' + codiAntic + '/validateTo/' + codiNou)
						.done(function (response) {
							if (response.usuariActual) {
								// Codi antic és el de l'usuari actual
								lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
								lineRow.find('.result').append('<span class="result-info text-danger"><spring:message code="usuari.antic.es.actual"/></span>');
								processNextLine(index + 1);
							} else if (!response.usuariAnticExists) {
								// Codi antic no existeix
								lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
								lineRow.find('.result').append('<span class="result-info text-danger"><spring:message code="usuari.antic.not.found"/></span>');
								processNextLine(index + 1);
							} else if (response.usuariNouExists) {
								// Codi nou ja existeix
								confirmAction('usuari.nou.exists', codiAntic, codiNou, function () {
									executeCanviCodi(lineRow, codiAntic, codiNou, () => processNextLine(index + 1));
								}, function () {
									lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
									lineRow.find('.result').append('<span class="result-info text-warning"><spring:message code="usuari.canvi.cancelat"/></span>');
									processNextLine(index + 1);
								});
							} else {
								// Validació correcta, realitzar el canvi
								executeCanviCodi(lineRow, codiAntic, codiNou, () => processNextLine(index + 1));
							}
						})
						.fail(function () {
							lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
							lineRow.find('.result').append('<span class="result-info text-danger"><spring:message code="usuari.codi.format.error"/></span>');
							processNextLine(index + 1);
						});
			};

			// Inicia el processament per la primera línia
			processNextLine(0);
		}

		function executeCanviCodi(lineRow, codiAntic, codiNou, callback) {
			$.post('<c:url value="/usuari/usernames/"/>' + codiAntic + '/changeTo/' + codiNou)
					.done(function (response) {
						lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass(response.estat === 'OK' ? 'fa-check text-success' : 'fa-times text-danger');
						lineRow.find('.result').append('<span class="badge">Durada: ' + response.duracio + 'ms</span>');

						if (response.estat === 'OK') {
							lineRow.find('.result').append('<span class="result-info text-success">' + response.registresModificats + ' <spring:message code="usuari.codi.processats"/></span>');
						} else {
							lineRow.find('.result').append('<span class="result-info text-danger">' + response.errorMessage + '</span>');
						}
						callback(); // Continuar amb la següent línia
					})
					.fail(function () {
						lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
						lineRow.find('.result').append(`<span class="result-info text-danger">Error al processar la línia</span>`);
						callback(); // Continuar amb la següent línia
					});
		}

		function confirmAction(messageCode, codiAntic, codiNou, onContinue, onCancel) {
			const modal = $('<div class="modal" tabindex="-1" role="dialog"></div>');
			modal.html(
					'<div class="modal-dialog" role="document">' +
					'<div class="modal-content">' +
					'<div class="modal-header">' +
					'<h5 class="modal-title"><spring:message code="usuari.nou.exists"/> (' + codiAntic + ' -> ' + codiNou + ')</h5>' +
					'</div>' +
					'<div class="modal-body">' +
					'<p><spring:message code="usuari.nou.exists.detall"/></p>' +
					'</div>' +
					'<div class="modal-footer">' +
					'<button type="button" class="btn btn-primary confirm-continue"><spring:message code="comu.boto.continuar"/></button>' +
					'<button type="button" class="btn btn-secondary confirm-cancel" data-dismiss="modal"><spring:message code="comu.boto.cancelar"/></button>' +
					'</div>' +
					'</div>' +
					'</div>'
			);

			$('body').append(modal);
			modal.modal('show');

			modal.find('.confirm-continue').click(function () {
				modal.modal('hide');
				modal.remove();
				if (onContinue) onContinue();
			});

			modal.find('.confirm-cancel').click(function () {
				modal.modal('hide');
				modal.remove();
				if (onCancel) onCancel();
			});
		}

		function toggleButtons(enable) {
			$('#modificarButton').prop('disabled', !enable);
			$('#modal-botons a').prop('disabled', !enable);
		}

	</script>
	<style>
		hr { margin-top: 20px; margin-bottom: 20px; }
		.processing-line { display: flex; align-items: center; white-space: nowrap; }
		.processing-line .status-icon { margin-left: auto; }
		.result-info { margin-left: 10px; }
	</style>
</head>
<body>
	<form action="#" method="post" class="form-horizontal" role="form" id="codisForm">
		<div class="row">
			<div class="col-md-12">
				<label class="control-label col-xs-2" for="codis">
					<spring:message code="usuari.form.camp.mapeig"/>
				</label>
				<div class="controls col-xs-10">
					<textarea class="form-control" id="codis" rows="6"></textarea>
					<p class="comentari col-xs-12"><spring:message code="usuari.form.camp.mapeig.comentari"/></p>
				</div>
			</div>
		</div>
		<hr/>
		<div id="change-process" class="row"></div>
		<div id="modal-botons" class="pull-right">
			<button type="button" class="btn btn-success" id="modificarButton">
				<span class="fa fa-save"></span>
				<spring:message code="comu.boto.modificar"/>
			</button>
			<a href="<c:url value="/"/>" class="btn btn-default" data-modal-cancel="true">
				<span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.tancar"/>
			</a>
		</div>
	</form>
</body>
</html>
