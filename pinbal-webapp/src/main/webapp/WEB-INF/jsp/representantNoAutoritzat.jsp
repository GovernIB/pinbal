<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title><spring:message code="representant.no.autoritzat.titol"/></title>
</head>
<body>

	<div class="alert alert-error">
		<spring:message code="representant.no.autoritzat.alert.error"/>
	</div>

</body>
</html>
