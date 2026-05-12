<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<c:set var="codiMunicipiSenseCodiProvincia" value="${camp.tipus == 'MUNICIPI_3'}"/>
<select id="${campId}" name="${campId}"<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if> 
		data-minimumresults="-1" data-toggle="select2"
		class="form-control">
<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
</select>
<c:choose>
	<c:when test="${not empty camp.campPare}">
		<c:set var="campPareId" value="camp_${camp.campPare.id}"/>
<script>
$("#${campPareId}").change(function(event, valor) {
	$('#${campId}').empty();
	if ($('#${campPareId}').val().length > 0) {
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.carregant"/>'));
		$.ajax({
		    url:'<c:url value="/dades/municipis"/>/' + $('#${campPareId}').val(),
		    type:'GET',
		    dataType: 'json',
		    success: function(json) {
		    	$('#${campId}').empty();
	        	$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
		        $.each(json, function(i, value) {
		        	var valueCodi = value.codi;
		        	<c:if test="${codiMunicipiSenseCodiProvincia}">valueCodi = value.codi.substring(2);</c:if>
		        	if ( $('#${campId}').data('defecte-processat')) {
		            	$('#${campId}').append($('<option>').text(value.nom).attr('value', valueCodi));
		        	} else {
		        		var valorPerDefecte = '${campValorDefecte}';
			        	if (value.codi == valorPerDefecte) {
			        		$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', valueCodi));
			        	} else {
			        		$('#${campId}').append($('<option>').text(value.nom).attr('value', valueCodi));
			        	}
		        	}
		        });
		        $('#${campId}').data('defecte-processat', true);
		    }
		});
	} else {
		$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
	}
});
</script>
	</c:when>
	<c:when test="${not empty camp.valorPare}">
<script>
$.ajax({
    url:'<c:url value="/dades/municipis"/>/${camp.valorPare}',
    type:'GET',
    dataType: 'json',
    success: function(json) {
    	$('#${campId}').empty();
    	$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
        $.each(json, function(i, value) {
        	var valorPerDefecte = '${campValorDefecte}';
        	if (value.codi == valorPerDefecte) {
        		$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', value.codi));
        	} else {
        		$('#${campId}').append($('<option>').text(value.nom).attr('value', value.codi));
        	}
            
        });
    }
});
</script>
	</c:when>
</c:choose>