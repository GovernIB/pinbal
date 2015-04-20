<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="informe.list.titol"/></title>
</head>
<body>

	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.procediments"/>
			<a href="informe/procediments" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>
	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.usuaris"/>
			<a href="informe/usuaris" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>
	<div class="well well-small">
		<h4>
			<spring:message code="informe.list.informe.serveis"/>
			<a href="informe/serveis" class="btn pull-right"><i class="icon-download"></i>&nbsp;<spring:message code="informe.list.generar"/></a>
		</h4>
	</div>

</body>
</html>
