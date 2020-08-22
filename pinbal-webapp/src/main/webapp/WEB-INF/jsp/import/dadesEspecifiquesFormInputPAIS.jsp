<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<c:set var="campFillId" value=""/>
<c:forEach var="candidatFill" items="${campsPerMostrar}">
	<c:if test="${not empty candidatFill.campPare and candidatFill.campPare.id == camp.id}"><c:set var="campFillId" value="camp_${candidatFill.campPare.id}"/></c:if>
</c:forEach>
<select id="${campId}" name="${campId}"
		<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if>
		data-minimumresults="-1" data-toggle="select2"
		class="form-control">
	<option value=""><spring:message code="comu.opcio.carregant" /></option>
</select>
<script>
$.ajax({
    url:'<c:url value="/dades/paisos"/>',
    type:'GET',
    dataType: 'json',
    success: function(json) {
    	$('#${campId}').empty();
    	$('#${campId}').append($('<option value="">').text('<spring:message code="comu.opcio.sense.definir"/>'));
        $.each(json, function(i, value) {
        	var valorPerDefecte = '${campValorDefecte}';
        	if (value.alpha3 == valorPerDefecte) {
            	$('#${campId}').append($('<option selected="selected">').text(value.nom).attr('value', value.alpha3));
            	<c:if test="${not empty campFillId}">$('#${campFillId}').trigger('change', valorPerDefecte);</c:if>
        	} else {
        		$('#${campId}').append($('<option>').text(value.nom).attr('value', value.alpha3));
        	}
        });
    }
});
</script>