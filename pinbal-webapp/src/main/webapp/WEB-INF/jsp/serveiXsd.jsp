<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<c:choose>
	<c:when test="${not reloadPage}">
	<form:form id="xsd-form" method="post" action="/servei/${serveiCodi}/xsd/save" commandName="serveiXsdCommand" role="form" enctype="multipart/form-data">	
		<c:set var="campPath" value="fitxerXsd"/>
		<form:input path="codi" type="hidden" value="${serveiCodi}"/>
		
		<pbl:inputSelect name="tipus"  inline="true" 
						 placeholderKey="servei.xsd.camp.tipus"
						 srLabel="false"
						 textKey="servei.xsd.camp.tipus" 
						 required="true"
						 optionItems="${xsdTipusEnumOptions}" 
						 optionValueAttribute="value"
						 optionTextAttribute="text"
						 emptyOption="true"/>
		<pbl:inputFile name="nomArxiu" textKey="servei.form.camp.fitxer.xsd"/>
	</form:form>
	</c:when>
	<c:otherwise><script>location.reload();</script></c:otherwise>
</c:choose>

