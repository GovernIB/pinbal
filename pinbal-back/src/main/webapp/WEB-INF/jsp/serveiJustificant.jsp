<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="servei.justificant.titol"/></title>
	<c:choose>
		<c:when test="${not empty servei.descripcio}"><c:set var="serveiPerTitol" value="${servei.descripcio}"/></c:when>
		<c:otherwise><c:set var="serveiPerTitol" value="${servei.codi}"/></c:otherwise>
	</c:choose>
	<meta name="subtitle" content="${serveiPerTitol}"/>
<script>
$(document).ready(function() {
	$('.justificant-modificar').click(function() {
		$('#modal-justificant-hidden-xpath').val($(this).data('xpath'));
		$('#modal-justificant-input-traduccio').val($(this).data('traduccio'));
		$('#modal-justificant-input-document').prop('checked', $(this).data('document'));
		$('#modal-justificant').modal('toggle');
	});
	$('#modal-justificant-boto-submit').click(function(event) {
		event.preventDefault();
		if ($('#modal-justificant-input-traduccio').val() === '') {
			$('#div-traduccio').addClass("has-error");
			$('.help-block').removeClass("ocult");
		} else {
			$('#modal-justificant-form').submit();
		}
	});
});
</script>
</head>
<body>
	<table id="table-servei-justificant" class="table table-striped table-bordered" style="width: 100%">
	<thead>
		<tr>
		<th><spring:message code="servei.justificant.taula.columna.path" /></th>
		<th><spring:message code="servei.justificant.taula.columna.traduccio" /></th>
		<th><spring:message code="servei.justificant.taula.columna.document" /></th>
		<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${llistaDadesEspecifiques}" var="registre">
			<c:if test="${registre.dades.pathAmbSeparadorDefault != 'DatosEspecificos'}">
				<c:set var="traduccioCoincident"/>
				<c:forEach var="traduccio" items="${traduccions}">
					<c:if test="${traduccio.xpath == registre.dades.pathAmbSeparadorDefault}">
						<c:set var="traduccioCoincident" value="${traduccio}"/>
					</c:if>
				</c:forEach>
	   			<tr>
					<td>
						${registre.dades.pathAmbSeparadorDefault}
					</td>
					<td>
						<c:choose>
							<c:when test="${not empty traduccioCoincident}">${traduccioCoincident.traduccio}</c:when>
							<c:otherwise><span class="muted">${registre.dades.nom}</span></c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:if test="${not empty traduccioCoincident and traduccioCoincident.document}"><span class="fa fa-check"></span></c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${not empty traduccioCoincident}">
								<button title="<spring:message code="comu.boto.modificar"/>" class="btn btn-primary justificant-modificar" data-xpath="${registre.dades.pathAmbSeparadorDefault}" data-traduccio="${traduccioCoincident.traduccio}" data-document="${traduccioCoincident.document}"><i class="icon-pencil"></i> <spring:message code="comu.boto.modificar"/></button>
							</c:when>
							<c:otherwise>
								<button title="<spring:message code="comu.boto.modificar"/>" class="btn btn-primary justificant-modificar" data-xpath="${registre.dades.pathAmbSeparadorDefault}"><i class="icon-pencil"></i> <spring:message code="comu.boto.modificar"/></button>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:if>
		</c:forEach>
	</tbody>
	</table>

	<div>
		<a href="<c:url value="/servei"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

<div id="modal-justificant" class="modal fade" role="dialog">
	<div class="modal-dialog">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3 id="modal-justificant-titol"><spring:message code="servei.justificant.modal.titol"/></h3>
			</div>
			<div class="modal-body">
				<form id="modal-justificant-form" action="" method="post">
					<input type="hidden" id="modal-justificant-hidden-id" name="id"/>
					<input type="hidden" id="modal-justificant-hidden-servei" name="servei" value="${servei.codi}"/>
					<input type="hidden" id="modal-justificant-hidden-xpath" name="xpath"/>
					<div id="div-traduccio" class="form-group">
						<label for="modal-justificant-input-traduccio"><spring:message code="servei.justificant.modal.camp.traduccio"/> *</label>
						<input type="text" id="modal-justificant-input-traduccio" name="traduccio" class="form-control"/>
						<p class="help-block ocult"><span class="fa fa-exclamation-triangle"></span>&nbsp;<spring:message code="NotEmpty"/></p>
					</div>
					<div class="form-group">
						<label for="modal-justificant-input-document"><spring:message code="servei.justificant.modal.camp.document"/>
							<input type="checkbox" id="modal-justificant-input-document" name="document" class="form-control"/>
						</label>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></button>
				<button id="modal-justificant-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			</div>
		</div>
	</div>
</div>

</body>
</html>
