<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${not reloadPage}">
		<form:form id="modal-form" commandName="serveiBusCommand">
			<c:if test="${not empty serveiBusCommand.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="servei"/>
			<c:set var="campPath" value="entitatId"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
		 		<label for="modal-input-defecte"><spring:message code="servei.bus.form.camp.entitat"/></label>
				<form:select path="${campPath}" cssClass="form-control" id="${campPath}">
					<option value=""><spring:message code="comu.opcio.sense.definir"/></option>
					<form:options items="${entitats}" itemLabel="nom" itemValue="id"/>
				</form:select>
				<form:errors path="${campPath}" cssClass="help-block"/>
			</div>
			<c:set var="campPath" value="urlDesti"/>
			<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
			<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
		 		<label for="modal-input-etiqueta"><spring:message code="servei.bus.form.camp.url"/></label>
				<form:input path="${campPath}" cssClass="form-control" id="${campPath}"/>
				<form:errors path="${campPath}" cssClass="help-block"/>
			</div>
		</form:form>
	</c:when>
	<c:otherwise><script>location.reload();</script></c:otherwise>
</c:choose>
