<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://code.google.com/p/jmesa" prefix="jmesa" %>

<html>
<head>
	<title><spring:message code="procediment.list.titol"/></title>
	<script type="text/javascript" src="<c:url value="/js/jquery.jmesa.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jmesa.min.js"/>"></script>
</head>
<body>

	<ul class="breadcrumb">
		<li><spring:message code="procediment.serveis.miques.procediment" arguments="${procediment.nom}"/> <span class="divider">/</span></li>
		<li><spring:message code="procediment.serveis.miques.servei" arguments="${servei.descripcio}"/> <span class="divider">/</span></li>
		<li class="active"><spring:message code="procediment.serveis.miques.permisos"/></li>
	</ul>

	<jmesa:tableModel
                id="permisos" 
                items="${entitat.usuarisRepresentant}"
                view="es.caib.pinbal.webapp.jmesa.BootstrapNoToolbarView"
                var="registre"
				maxRows="${fn:length(entitat.usuarisRepresentant)}">
		<jmesa:htmlTable>
			<jmesa:htmlRow>
				<c:set var="trobat" value="${false}"/>
				<c:forEach var="usuari" items="${usuarisAmbPermis}">
					<c:if test="${usuari == registre.usuari.codi}"><c:set var="trobat" value="${true}"/></c:if>
				</c:forEach>
				<jmesa:htmlColumn property="usuari.descripcio" titleKey="procediment.serveis.permisos.taula.columna.usuari"/>
				<jmesa:htmlColumn property="acces" titleKey="procediment.serveis.permisos.taula.columna.acces.permes">
					<c:if test="${trobat}"><i class="icon-ok"></i></c:if>
				</jmesa:htmlColumn>
				<jmesa:htmlColumn property="ACCIO_canvi_acces" title="&nbsp;" sortable="false">
					<c:choose>
						<c:when test="${trobat}">
							<a href="permis/${registre.usuari.codi}/deny" class="btn">
								<i class="icon-remove"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.denegar.acces"/>
							</a>
						</c:when>
						<c:otherwise>
							<a href="permis/${registre.usuari.codi}/allow" class="btn">
								<i class="icon-ok"></i>&nbsp;<spring:message code="procediment.serveis.permisos.taula.boto.permetre.acces"/>
							</a>
						</c:otherwise>
					</c:choose>
				</jmesa:htmlColumn>
            </jmesa:htmlRow>
        </jmesa:htmlTable>
	</jmesa:tableModel>
	<div>
		<a href="<c:url value="../../servei"/>" class="btn pull-right"><spring:message code="comu.boto.tornar"/></a>
		<div class="clearfix"></div>
	</div>

</body>
</html>
