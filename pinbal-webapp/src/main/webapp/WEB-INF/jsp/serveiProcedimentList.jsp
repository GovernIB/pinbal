<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<html>
<head>
	<title><spring:message code="servei.procediment.list.titol" arguments=""/></title>
</head>
<body>
	<c:forEach items="${procedimentsEntitat}" var="procedimentEntitat">
		<c:set var="entitat" value="${procedimentEntitat.key}"/>
		<c:set var="procediments" value="${procedimentEntitat.value}"/>
		
		<h3><spring:message code="entitat.miques.entitat" arguments="${entitat}"/></h3>
		
		<table id="procediments_${entitat}" class="table table-striped table-bordered" style="width: 100%">
		<thead>
			<tr>
			<th><spring:message code="servei.procediment.list.taula.columna.codi" /></th>
			<th><spring:message code="servei.procediment.list.taula.columna.nom" /></th>
			<th><spring:message code="servei.procediment.list.taula.columna.departament" /></th>
			<th><spring:message code="servei.procediment.list.taula.columna.actiu" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${procediments}" var="registre">
	   			<tr>
					<td>
						${registre.codi}
					</td>
					<td>
						${registre.nom}
					</td>
					<td>
						${registre.departament}
					</td>
					<td>
					<c:if test="${registre.actiu}"><i class="fas fa-check"></i></c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</c:forEach>
</body>
</html>
