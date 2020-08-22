<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<div class="input-group" style="width: 100%">
	<input id="${campId}" name="${campId}"
		<c:if test="${not empty campValorDefecte}"> value="${campValorDefecte}"</c:if>
		<c:if test="${dadesEspecifiquesDisabled}"> disabled="disabled"</c:if>
		<c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if> 
		class="form-control datepicker" />
	<span class="input-group-addon" style="width: 1%"><span
		class="far fa-calendar-alt"></span></span>
</div>
<script>
$(document).ready(function() {
	$('.datepicker').datepicker({
	    orientation: "bottom auto",
		format: 'dd/mm/yyyy',
		weekStart: 1,
		autoclose: true,
		language: 'es'
	});
});
</script>