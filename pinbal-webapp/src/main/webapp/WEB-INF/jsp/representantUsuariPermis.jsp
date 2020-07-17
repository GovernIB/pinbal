<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code="representant.usuaris.titol"/></title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script type="text/javascript"src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/select2.min.js"/>"></script>
	
<script>
$(document).ready(function() {
	$('#select-procediment').change(function() {
		if ($(this).val()) {
			var targetUrl = '<c:url value="/estadistiques/serveisPerProcediment"/>/' + $(this).val();
			$.ajax({
			    url: targetUrl,
			    type:'GET',
			    dataType: 'json',
			    success: function(json) {
			    	$('#serveiCodi').empty();
		        	$('#serveiCodi').append($('<option value="">').text('<spring:message code="consulta.list.filtre.servei"/>:'));
			        $.each(json, function(i, value) {
			            $('#serveiCodi').append($('<option>').text("(" + value.codi+ ") " + value.descripcio).attr('value', value.codi));
			        });
			    }
			});
		} else {
			$('#serveiCodi').empty();
			$('#serveiCodi').append($('<option value="">').text('<spring:message code="consulta.list.filtre.servei"/>:'));
		}
	});
	
	$('#select-procediment').select2();
	$('#serveiCodi').select2();
	$('.confirm-esborrar').click(function() {
		  return confirm('<spring:message code="procediment.serveis.permisos.esborrar.tots.segur"/>');
	});
});
</script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="representant.usuaris.permisos.miques.usuari" arguments="${usuari.descripcio}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="representant.usuaris.permisos.miques.permisos"/></li>
	</ul>

	<c:url value="/representant/usuari/${usuari.codi}/permis/allow" var="formAction"/>
	<form action="${formAction}" method="post" class="well">
		<select id="procedimentId" name="procedimentId" class="input-lg">
			<option value=""><spring:message code="representant.usuaris.permisos.filtre.procediment"/>:</option>
			<c:forEach var="procediment" items="${procediments}">
				<option value="${procediment.id}">(${procediment.codi}) ${procediment.nom}</option>
			</c:forEach>
		</select>
		<select id="serveiCodi" name="serveiCodi" class="input-lg">
			<option value=""><spring:message code="representant.usuaris.permisos.filtre.servei"/>:</option>
		</select>
		<div class="pull-right">
		<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.afegir"/></button>
		</div>
	</form>

	<table id="permisos" border="0" cellpadding="0" cellspacing="0" class="table table-bordered table-striped">
	<thead>
	<tr class="header">
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.procediment.codi"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.procediment.nom"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.servei.codi"/></div></th>
		<th><div><spring:message code="representant.usuaris.permisos.taula.columna.servei.descripcio"/></div></th>
		<th><div>&nbsp;</div></th>
	</tr>
	</thead>
	<tbody class="tbody">
		<c:forEach var="permis" items="${permisos}">
		<tr>
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
		<a href="<c:url value="/representant/usuari"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
