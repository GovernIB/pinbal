<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%
	pageContext.setAttribute(
			"sessionEntitats",
			es.caib.pinbal.webapp.common.EntitatHelper.getEntitats(request));
	pageContext.setAttribute(
			"sessionEntitatActualIndex",
			es.caib.pinbal.webapp.common.EntitatHelper.getEntitatActualIndex(request));
	pageContext.setAttribute(
			"isRepresentantEntitatActual",
			es.caib.pinbal.webapp.common.EntitatHelper.isRepresentantEntitatActual(request));
	pageContext.setAttribute(
			"isDelegatEntitatActual",
			es.caib.pinbal.webapp.common.EntitatHelper.isDelegatEntitatActual(request));
	pageContext.setAttribute(
			"isAuditorEntitatActual",
			es.caib.pinbal.webapp.common.EntitatHelper.isAuditorEntitatActual(request));
	pageContext.setAttribute(
			"requestParameterCanviEntitat",
			es.caib.pinbal.webapp.common.EntitatHelper.getRequestParameterCanviEntitat());
	if (es.caib.pinbal.webapp.common.RolHelper.isRolActualDelegat(request)) {
		es.caib.pinbal.core.dto.EntitatDto entitat = es.caib.pinbal.webapp.common.EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			pageContext.setAttribute(
					"sessionServeis",
					es.caib.pinbal.webapp.common.ServeiHelper.getServeis(
							request,
							entitat.getId()));
		}
	}
	pageContext.setAttribute(
			"dadesUsuariActual",
			es.caib.pinbal.webapp.common.UsuariHelper.getDadesUsuariActual(request));
	pageContext.setAttribute(
			"rolActual",
			es.caib.pinbal.webapp.common.RolHelper.getRolActual(request));
	pageContext.setAttribute(
			"rolsUsuariActual",
			es.caib.pinbal.webapp.common.RolHelper.getRolsUsuariActual(request));
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.pinbal.webapp.common.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualRepresentant",
			es.caib.pinbal.webapp.common.RolHelper.isRolActualRepresentant(request));
	pageContext.setAttribute(
			"isRolActualDelegat",
			es.caib.pinbal.webapp.common.RolHelper.isRolActualDelegat(request));
	pageContext.setAttribute(
			"isRolActualAuditor",
			es.caib.pinbal.webapp.common.RolHelper.isRolActualAuditor(request));
	pageContext.setAttribute(
			"isRolActualSuperauditor",
			es.caib.pinbal.webapp.common.RolHelper.isRolActualSuperauditor(request));
	pageContext.setAttribute(
			"countConsultesMultiplesPendents",
			es.caib.pinbal.webapp.common.PeticionsMultiplesPendentsHelper.countPendents(request));
%>
<c:set var="hiHaEntitats" value="${fn:length(sessionEntitats) > 0}"/>
<c:set var="hiHaMesEntitats" value="${fn:length(sessionEntitats) > 1}"/>

<!DOCTYPE html>
<html lang="en">
<head>

	<meta charset="utf-8">
	<title>Pinbal - <decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>

	<!-- Els estils -->
	<link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet">
    <link href="<c:url value="/css/bootstrap-responsive.css"/>" rel="stylesheet">
    <link href="<c:url value="/css/default.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/orange-header.css"/>" rel="stylesheet">

	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Els fav i touch icons -->
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />

	<script src="<c:url value="/js/jquery.min.js"/>"></script>
	<!-- Resize de la capçalera -->
	<![if (gt IE 8) | !(IE)]>
		<script src="<c:url value="/js/resize.js"/>"></script>
 	<![endif]>
	<decorator:head />
<script>
$(document).ready(function() {
	<%-- Per evitar que el menu surti de la finestra del navegador --%>
	$(".dropdown-menu").css("max-height", ($(window).height() - 110) + "px");
	$(".dropdown-menu").css("overflow", "auto");
});
</script>
</head>
<body>
	<div class="row-fluid container nav-container">
		<div id="govern-logo" class="govern-logo pull-left"><img id="govern-img" src="<c:url value="/img/govern-logo.png"/>" width="159" height="36" alt="<spring:message code="decorator.govern"/>" /></div>
		<div id="app-logo" class="aplication-logo pull-left"><img id="app-img" src="<c:url value="/img/logo-pinbal-w.png"/>" width="217" height="61" alt="<spring:message code="decorator.pinbal"/>" /></div>
		<div id="main-menu" class="pull-right main-menu">
			<ul class="user-nav pull-right">
				<c:if test="${hiHaEntitats}">
					<li class="dropdown">
						<c:if test="${hiHaMesEntitats}"><a href="#" class="dropdown-toggle" data-toggle="dropdown"></c:if>
         						<i class="icon-map-marker icon-white"></i> ${sessionEntitats[sessionEntitatActualIndex].nom}
         						<c:if test="${hiHaMesEntitats}"><b class="caret caret-white"></b></c:if>
						<c:if test="${hiHaMesEntitats}"></a></c:if>
						<c:if test="${hiHaMesEntitats}">
							<ul class="dropdown-menu">
								<c:forEach var="entitat" items="${sessionEntitats}" varStatus="status">
									<c:if test="${status.index != sessionEntitatActualIndex}">
										<c:url var="urlCanviEntitat" value="/index">
											<c:param name="${requestParameterCanviEntitat}" value="${entitat.id}"/>
										</c:url>
										<li><a href="${urlCanviEntitat}">${entitat.nom}</a></li>
									</c:if>
								</c:forEach>
							</ul>
						</c:if>
					</li>
				</c:if>
				<li class="dropdown">
					<c:choose>
						<c:when test="${fn:length(rolsUsuariActual) > 1}">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown">
								<i class="icon-lock icon-white"></i>
								<spring:message code="decorator.menu.rol.${rolActual}"/>
								<b class="caret caret-white"></b>
							</a>
							<ul class="dropdown-menu">
								<c:forEach var="rol" items="${rolsUsuariActual}">
									<c:if test="${rol != rolActual}">
										<li>
											<c:url value="/index" var="canviRolUrl">
												<c:param name="canviRol">${rol}</c:param>
											</c:url>
											<a href="${canviRolUrl}"><spring:message code="decorator.menu.rol.${rol}"/></a>
										</li>
									</c:if>
								</c:forEach>
							</ul>
						</c:when>
						<c:otherwise>
							<c:if test="${not empty rolActual}"><i class="icon-lock icon-white"></i>&nbsp;<spring:message code="decorator.menu.rol.${rolActual}"/></c:if>
						</c:otherwise>
					</c:choose>
				</li>
				<li>
					<i class="icon-user icon-white"></i>
					<c:choose>
						<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom}</c:when>
						<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
					</c:choose>
				</li>
			</ul>
			<div class="clearfix"></div>
			<div class="btn-group pull-right">
				<c:choose>
					<c:when test="${isRolActualAdministrador}">
						<a href="<c:url value="/entitat"/>" class="btn btn-primary"><spring:message code="decorator.menu.entitats"/></a>
						<a href="<c:url value="/servei"/>" class="btn btn-primary"><spring:message code="decorator.menu.serveis"/></a>
						<a href="<c:url value="/estadistiques"/>" class="btn btn-primary"><spring:message code="decorator.menu.estadistiques"/></a>
						<a href="<c:url value="/informe"/>" class="btn btn-primary"><spring:message code="decorator.menu.informes"/></a>
						<a href="<c:url value="/admin/consulta"/>" class="btn btn-primary"><spring:message code="decorator.menu.consultes"/></a>
						<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.scsp"/>&nbsp;<span class="caret caret-white"></span></button>
						<ul class="dropdown-menu">
							<li><a href="<c:url value="/scsp/paramconf"/>"><spring:message code="decorator.menu.scsp.parametres.configuracio"/></a></li>
						</ul>
					</c:when>
					<c:when test="${isRolActualRepresentant}">
						<c:if test="${isRepresentantEntitatActual}">
							<a href="<c:url value="/representant/usuari"/>" class="btn btn-primary"><spring:message code="decorator.menu.usuaris"/></a>
							<a href="<c:url value="/procediment"/>" class="btn btn-primary"><spring:message code="decorator.menu.procediments"/></a>
						<a href="<c:url value="/estadistiques"/>" class="btn btn-primary"><spring:message code="decorator.menu.estadistiques"/></a>
						</c:if>
					</c:when>
					<c:when test="${isRolActualDelegat}">
						<c:if test="${isDelegatEntitatActual}">
							<a href="<c:url value="/consulta"/>" class="btn btn-primary"><spring:message code="decorator.menu.consultes.simples"/></a>
							<a href="<c:url value="/consulta/multiple"/>" class="btn btn-primary">
								<spring:message code="decorator.menu.consultes.multiples"/>
								<c:if test="${countConsultesMultiplesPendents gt 0}"><span class="badge badge-warning">${countConsultesMultiplesPendents}</span></c:if>
							</a>
							<c:if test="${not empty sessionServeis}">
								<a href="#" data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.consulta.nova"/> <span class="caret"></span></a>
								<ul class="dropdown-menu">
									<c:forEach var="servei" items="${sessionServeis}">
										<li><a href="<c:url value="/consulta/${servei.codi}/new"/>">${servei.descripcio}</a></li>
									</c:forEach>
								</ul>
							</c:if>
						</c:if>
					</c:when>
					<c:when test="${isRolActualAuditor}">
						<c:if test="${isAuditorEntitatActual}">
							<a href="<c:url value="/auditor/generar"/>" class="btn btn-primary"><spring:message code="decorator.menu.auditoria"/></a>
							<a href="<c:url value="/auditor"/>" class="btn btn-primary"><spring:message code="decorator.menu.consultes"/></a>
							<a href="<c:url value="/auditor/usuari"/>" class="btn btn-primary"><spring:message code="decorator.menu.usuaris"/></a>
						</c:if>
					</c:when>
					<c:when test="${isRolActualSuperauditor}">
						<a href="<c:url value="/superauditor/generar"/>" class="btn btn-primary"><spring:message code="decorator.menu.auditoria"/></a>
						<a href="<c:url value="/superauditor"/>" class="btn btn-primary"><spring:message code="decorator.menu.consultes"/></a>
					</c:when>
				</c:choose>
			</div>
		</div>
	</div>
	<div class="row-fluid container main">
		<div class="well well-white">
			<jsp:include page="../import/alerts.jsp"/>
			<div class="page-header">
				<h2>
					<decorator:title />
					<small><decorator:getProperty property="meta.subtitle"/></small>
				</h2>
			</div>
			<decorator:body />
		</div>
	</div>
    <div class="container row-fluid">
    	<div class="pull-left colophon versio-footer" style="margin: 10px 0 0 40px"><p>Pinbal v${versioActual}</p></div>
        <div class="pull-right govern-footer" style="margin: 10px 40px 0 0"><p><img src="<c:url value="/img/govern-logo-neg.png"/>" width="129" height="30" alt="<spring:message code="decorator.govern"/>" /></p></div>
    </div>

    <script src="<c:url value="/js/bootstrap.min.js"/>"></script>

</body>
</html>
