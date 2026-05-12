<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<c:choose>
	<c:when test="${dadesEspecifiquesDisabled}">
		<input type="text" id="${campId}" name="${campId}" disabled="disabled"<c:if test="${not empty valorDadaEspecifica}"> value="${valorDadaEspecifica}"</c:if> class="form-control"/>
	</c:when>
	<c:otherwise>
   		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
			<div class="form-control" data-trigger="fileinput">
				<i class="glyphicon glyphicon-file fileinput-exists"></i> 
				<span class="fileinput-filename"></span>
			</div>
			<span class="input-group-addon btn btn-default btn-file">
				<span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span>
				<input type="file" id="${campId}" name="${campId}" value="${serveiCommand.fitxerAjudaNom}"></span>
			<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
		</div>
	</c:otherwise>
</c:choose>