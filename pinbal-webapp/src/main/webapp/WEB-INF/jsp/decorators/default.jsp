<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
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
					es.caib.pinbal.webapp.common.ServeiHelper.getServeis(request));
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
	pageContext.setAttribute(
			"avisos",
			es.caib.pinbal.webapp.helper.AvisHelper.getAvisos(request));
	
%>
<c:set var="hiHaEntitats" value="${fn:length(sessionEntitats) > 0}"/>
<c:set var="hiHaMesEntitats" value="${fn:length(sessionEntitats) > 1}"/>
<c:set var="entitatActual" value="${sessionEntitats[sessionEntitatActualIndex]}"/>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Pinbal - <decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
	<!-- Estils CSS -->
	<link href="<c:url value="/webjars/bootstrap/3.3.6/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/5.13.1/css/all.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/estils.css"/>" rel="stylesheet">
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon" />
	<script src="<c:url value="/webjars/jquery/1.12.4/jquery.min.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/webjars/bootstrap/3.3.6/js/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/font-awesome/5.13.1/js/all.min.js"/>"></script>
	<decorator:head />
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.png"/>);
	color:#666666;
	padding-top: 120px;
}
.navbar-app {
	background-color: #ff9523;
}
.datepicker {
	padding-left: 12px;
	padding-right: 12px;
}
</style>
</head>
<body>
	<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container container-caib">
			<div class="navbar-header">
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<img id="govern-img" src="<c:url value="/img/govern-logo.png"/>" height="65" alt="<spring:message code="decorator.govern"/>" />
					</div>
					<div id="app-logo" class="pull-left">
						<img id="app-img" src="<c:url value="/img/logo-pinbal-w.png"/>" height="65" alt="<spring:message code="decorator.pinbal"/>" />
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
					<ul class="list-inline pull-right">
						<c:if test="${hiHaEntitats}">
							<li class="dropdown">
								<c:if test="${hiHaMesEntitats}">
									<a href="#" class="dropdown-toggle" data-toggle="dropdown">
								</c:if>
								<i class="fas fa-university"></i> ${entitatActual.nom}
								<c:if test="${hiHaMesEntitats}"><b class="caret caret-white"></b></c:if>
								<c:if test="${hiHaMesEntitats}"></a></c:if>
								<c:if test="${hiHaMesEntitats}">
									<ul class="dropdown-menu">
										<c:forEach var="entitat" items="${sessionEntitats}" varStatus="status">
											<c:if test="${entitat.id != entitatActual.id}">
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
									<a href="#" data-toggle="dropdown">
										<i class="far fa-id-card"></i>
										<spring:message code="decorator.menu.rol.${rolActual}"/>
										<span class="caret caret-white"></span>
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
									<c:if test="${not empty rolActual}"><span class="far fa-id-card"></span>&nbsp;<spring:message code="decorator.menu.rol.${rolActual}"/></c:if>
								</c:otherwise>
							</c:choose>
						</li>
						<li class="dropdown">
							<a href="#" data-toggle="dropdown">
								<span class="fa fa-user"></span>
								<c:choose>
									<c:when test="${not empty dadesUsuariActual}">${dadesUsuariActual.nom}</c:when>
									<c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
								</c:choose>
								<span class="caret caret-white"></span>
							</a>
							<ul class="dropdown-menu">
								<li>
									<a href="<c:url value="/usuari/configuracio"/>">
										<span class="fa fa-download"></span> <spring:message code="decorator.menu.configuracio.user"/>
									</a>
								</li>
								<li>
									<a href="<c:url value="/usuari/logout"/>">
										<i class="fa fa-power-off"></i> <spring:message code="decorator.menu.accions.desconectar"/>
									</a>
								</li>
							</ul>
						</li>						
						
					</ul>
					<div class="clearfix"></div>
					<%------------------------ MENU BUTTONS ------------------------%>
					<div class="btn-group navbar-btn navbar-right">
						<c:choose>
						<c:when test="${isRolActualAdministrador}">
							<a href="<c:url value="/entitat"/>" class="btn btn-primary"><spring:message code="decorator.menu.entitats"/></a>
							<a href="<c:url value="/servei"/>" class="btn btn-primary"><spring:message code="decorator.menu.serveis"/></a>
							<a href="<c:url value="/estadistiques"/>" class="btn btn-primary"><spring:message code="decorator.menu.estadistiques"/></a>
							<a href="<c:url value="/informe"/>" class="btn btn-primary"><spring:message code="decorator.menu.informes"/></a>
							<a href="<c:url value="/organgestor"/>" class="btn btn-primary"><spring:message code="decorator.menu.organgestor"/></a>
							<a href="<c:url value="/admin/consulta"/>" class="btn btn-primary"><spring:message code="decorator.menu.consultes"/></a>
							<a href="<c:url value="/integracio"/>" class="btn btn-primary"><spring:message code="decorator.menu.integracions"/></a>
							<a href="<c:url value="/avis"/>" class="btn btn-primary"><spring:message code="decorator.menu.avisos"/></a>
							<div class="btn-group">
								<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.scsp"/>&nbsp;<span class="caret caret-white"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="/scsp/paramconf"/>"><spring:message code="decorator.menu.scsp.parametres.configuracio"/></a></li>
									<li><a href="<c:url value="/scsp/emissorcert"/>"><spring:message code="decorator.menu.scsp.emissor.certificat"/></a></li>
									<li><a href="<c:url value="/scsp/claupublica"/>"><spring:message code="decorator.menu.scsp.claus.publiques"/></a></li>
									<li><a href="<c:url value="/scsp/clauprivada"/>"><spring:message code="decorator.menu.scsp.clau.privada"/></a></li>
								</ul>
							</div>
						</c:when>
						<c:when test="${isRolActualRepresentant}">
							<c:if test="${isRepresentantEntitatActual}">
								<a href="<c:url value="/representant/usuari"/>" class="btn btn-primary"><spring:message code="decorator.menu.usuaris"/></a>
								<a href="<c:url value="/procediment"/>" class="btn btn-primary"><spring:message code="decorator.menu.procediments"/></a>
								<a href="<c:url value="/organgestor"/>" class="btn btn-primary"><spring:message code="decorator.menu.organgestor"/></a>
								<a href="<c:url value="/estadistiques"/>" class="btn btn-primary"><spring:message code="decorator.menu.estadistiques"/></a>
								<a href="<c:url value="/informeRepresentant"/>" class="btn btn-primary"><spring:message code="decorator.menu.informes"/></a>
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
								<div class="btn-group">
									<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="decorator.menu.consulta.nova"/> <span class="caret"></span></button>
									<ul class="dropdown-menu">
										<c:forEach var="servei" items="${sessionServeis}">
											<li><a href="<c:url value="/consulta/${servei.codiUrlEncoded}/new"/>">${servei.descripcio}</a></li>
										</c:forEach>
									</ul>
									</div>
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
		</div>
	</div>
	<div class="container container-main container-caib">
		<c:if test="${not empty avisos}">
			<div id="accordion">
				<c:forEach var="avis" items="${avisos}" varStatus="status">
						<div class="card avisCard ${avis.avisNivell == 'INFO' ? 'avisCardInfo':''} ${avis.avisNivell == 'WARNING' ? 'avisCardWarning':''} ${avis.avisNivell == 'ERROR' ? 'avisCardError':''}">
							<div data-toggle="collapse" data-target="#collapse${status.index}" class="card-header avisCardHeader">
								${avis.avisNivell == 'INFO' ? '<span class="fa fa-info-circle text-info"></span>':''} ${avis.avisNivell == 'WARNING' ? '<span class="fa fa-exclamation-triangle text-warning"></span>':''} ${avis.avisNivell == 'ERROR' ? '<span class="fa fa-exclamation-triangle text-danger"></span>':''} ${avis.assumpte}
							<button class="btn btn-default btn-xs pull-right"><span class="fa fa-chevron-down "></span></button>										
							</div>
							<div id="collapse${status.index}" class="collapse" data-parent="#accordion">
								<div class="card-body avisCardBody" >${avis.missatge}</div>
							</div>
						</div>
				</c:forEach>
			</div>
		</c:if>
		<div class="panel panel-default">
			<div class="panel-heading">
				<h2>
					<decorator:title />
				</h2>
			</div>
			<div class="panel-body">
				<jsp:include page="../import/alerts.jsp"/>
				<decorator:body />
			</div>
		</div>
	</div>
	<div class="container container-foot container-caib">
		<div class="pull-left app-version"><p>PINBAL <pbl:versio/></p></div>
		<div class="pull-right govern-footer">
			<p>
				<img src="<c:url value="/img/govern-logo-neg.png"/>" style="height:30px" alt="<spring:message code="decorator.govern"/>" />
				<img src="<c:url value="/img/una_manera.png"/>" hspace="5" style="height:30px" alt="<spring:message code='decorator.logo.manera'/>" />
				<img src="<c:url value="/img/feder7.png"/>" hspace="5" style="height:35px" alt="<spring:message code='decorator.logo.feder'/>" />
				<img src="<c:url value="/img/uenegroma.png"/>" hspace="5" style="height:50px" alt="<spring:message code='decorator.logo.ue'/>" />
			</p>
		</div>
	</div>
</body>
</html>