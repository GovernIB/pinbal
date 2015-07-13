<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="servei.procediment.list.titol" arguments=""/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
</head>
<body>
	<c:forEach items="${procedimentsEntitat}" var="procedimentEntitat">
		<c:set var="entitat" value="${procedimentEntitat.key}"/>
		<c:set var="procediments" value="${procedimentEntitat.value}"/>
		
		<h3><spring:message code="entitat.miques.entitat" arguments="${entitat}"/></h3>
		<jmesa:tableModel
				id="procediments_${entitat}" 
				items="${procediments}"
				toolbar="es.caib.pinbal.webapp.jmesa.BootstrapToolbar"
				view="es.caib.pinbal.webapp.jmesa.BootstrapView"
				var="registre"
				maxRows="${fn:length(procediments)}">
			<jmesa:htmlTable>
				<jmesa:htmlRow>
					<jmesa:htmlColumn property="codi" titleKey="servei.procediment.list.taula.columna.codi" width="25%"/>
					<jmesa:htmlColumn property="nom" titleKey="servei.procediment.list.taula.columna.nom" width="50%"/>
					<jmesa:htmlColumn property="departament" titleKey="servei.procediment.list.taula.columna.departament" width="20%"/>
					<jmesa:htmlColumn property="actiu" titleKey="servei.procediment.list.taula.columna.actiu" width="5%">
						<c:if test="${registre.actiu}"><i class="icon-ok"></i></c:if>
					</jmesa:htmlColumn>
	            </jmesa:htmlRow>
	        </jmesa:htmlTable>
		</jmesa:tableModel>
	</c:forEach>
</body>
</html>
