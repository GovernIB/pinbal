<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="titol"><spring:message code="contingut.arxiu.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
<%--	<rip:modalHead/>--%>
</head>
<body>
	<c:if test="${documentInvalid}">
		<div class="alert alert-danger alert-dismissible">
			<button type="button" class="close close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
			<spring:message code="contingut.icona.estat.invalid.origen.form" arguments="${contingut.validacioFirmaErrorMsg}"/>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${not empty arxiuDetall}">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active"><a href="#info" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="contingut.arxiu.tab.info"/></a></li>
				<c:if test="${not empty arxiuDetall.fills}">
					<li role="presentation"><a href="#fills" aria-controls="fills" role="tab" data-toggle="tab"><spring:message code="contingut.arxiu.tab.fills"/> <span class="badge badge-default">${fn:length(arxiuDetall.fills)}</span></a></li>
				</c:if>
				<c:if test="${not empty arxiuDetall.firmes}">
					<li role="presentation"><a href="#firmes" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="contingut.arxiu.tab.firmes"/> <span class="badge badge-default">${fn:length(arxiuDetall.firmes)}</span></a></li>
				</c:if>
				<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
					<li role="presentation"><a href="#metadades" aria-controls="metadades" role="tab" data-toggle="tab"><spring:message code="contingut.arxiu.tab.metadades"/> <span class="badge badge-default" id="metadadesSpan"></span></a></li>
				</c:if>
			</ul>
			<br/>
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane active" id="info">
					<table class="table table-striped table-bordered">
						<tbody>
							<tr>
								<td><strong><spring:message code="contingut.arxiu.camp.identificador"/></strong></td>
								<td>${arxiuDetall.identificador}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="contingut.arxiu.camp.nom"/></strong></td>
								<td>${arxiuDetall.nom}</td>
							</tr>
							<c:if test="${not empty arxiuDetall.serieDocumental}">
								<tr>
									<td><strong><spring:message code="contingut.arxiu.camp.serie.doc"/></strong></td>
									<td>${arxiuDetall.serieDocumental}</td>
								</tr>
							</c:if>
							<c:if test="${not empty arxiuDetall.arxiuEstat}">
								<tr>
									<td><strong><spring:message code="contingut.arxiu.camp.arxiu.estat"/></strong></td>
									<td>${arxiuDetall.arxiuEstat}</td>
								</tr>
							</c:if>
						</tbody>
					</table>
					<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
						<div class="panel panel-default">
							<div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="contingut.arxiu.grup.contingut"/></strong></h4></div>
							<table class="table table-striped table-bordered">
							<tbody>
								<c:if test="${not empty arxiuDetall.contingutTipusMime}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.contingut.tipus.mime"/></strong></td>
										<td>${arxiuDetall.contingutTipusMime}</td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.contingut.arxiu.nom"/></strong></td>
										<td>${arxiuDetall.contingutArxiuNom}</td>
									</tr>
								</c:if>
							</tbody>
							</table>
						</div>
					</c:if>
					<c:if test="${not empty arxiuDetall.eniIdentificador}">
						<div class="panel panel-default">
							<div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="contingut.arxiu.grup.metadades"/></strong></h4></div>
							<table class="table table-striped table-bordered">
							<tbody>
								<tr>
									<td><strong><spring:message code="contingut.arxiu.camp.eni.versio"/></strong></td>
									<td>${arxiuDetall.eniVersio}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="contingut.arxiu.camp.eni.identificador"/></strong></td>
									<td>${arxiuDetall.eniIdentificador}</td>
								</tr>
								<c:if test="${not empty arxiuDetall.eniOrgans}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.organs"/></strong></td>
										<td>
											<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
												${organ}<c:if test="${not status.last}">,</c:if>
											</c:forEach>
										</td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniDataObertura}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.data.obertura"/></strong></td>
										<td><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniClassificacio}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.classificacio"/></strong></td>
										<td>${arxiuDetall.eniClassificacio}</td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniEstat}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.estat"/></strong></td>
										<td><spring:message code="expedient.estat.enum.${arxiuDetall.eniEstat}"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniDataCaptura}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.data.captura"/></strong></td>
										<td><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniOrigen}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.origen"/></strong></td>
										<td><spring:message code="document.nti.origen.enum.${arxiuDetall.eniOrigen}"/></td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.estat.elab"/></strong></td>
										<td><spring:message code="document.nti.estela.enum.${arxiuDetall.eniEstatElaboracio}"/></td>
									</tr>
								</c:if>
								<c:choose>
									<c:when  test="${not empty arxiuDetall.eniTipusDocumental}">
										<tr>
											<td><strong><spring:message code="contingut.arxiu.camp.eni.tipus.doc"/></strong></td>
											<td>${arxiuDetall.eniTipusDocumental}</td>
										</tr>
									</c:when>
									<c:when  test="${not empty arxiuDetall.eniTipusDocumentalAddicional}">
											<td><strong><spring:message code="contingut.arxiu.camp.eni.tipus.doc"/></strong></td>
											<td>${arxiuDetall.eniTipusDocumentalAddicional}</td>
									</c:when>
								</c:choose>
								<c:if test="${not empty arxiuDetall.eniFormat}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.format.nom"/></strong></td>
										<td>${arxiuDetall.eniFormat}</td>
									</tr>
								</c:if>

								<c:if test="${not empty arxiuDetall.firmes}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.tipus.firma"/></strong></td>
										<td>
											<c:forEach var="firma" items="${arxiuDetall.firmes}" varStatus="status">
												${firma.tipus}<c:if test="${not status.last}">,</c:if>
											</c:forEach>
										</td>
									</tr>
								</c:if>

								<c:if test="${not empty arxiuDetall.eniInteressats}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.interessats"/></strong></td>
										<td>
											<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
												${interessat}<c:if test="${not status.last}">,</c:if>
											</c:forEach>
										</td>
									</tr>
								</c:if>
								<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
									<tr>
										<td><strong><spring:message code="contingut.arxiu.camp.eni.doc.orig.id"/></strong></td>
										<td>${arxiuDetall.eniDocumentOrigenId}</td>
									</tr>
								</c:if>
							</tbody>
							</table>
						</div>
					</c:if>
				</div>
				<c:if test="${not empty arxiuDetall.fills}">
					<div role="tabpanel" class="tab-pane" id="fills">
						<table class="table table-striped table-bordered">
							<c:forEach var="fill" items="${arxiuDetall.fills}" varStatus="status">
								<tr>
									<td width="10%">${fill.tipus}</td>
									<td>${fill.nom}</td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</c:if>
				<c:if test="${not empty arxiuDetall.firmes}">
					<div role="tabpanel" class="tab-pane" id="firmes">
						<c:forEach var="firma" items="${arxiuDetall.firmes}" varStatus="status">
							<div class="panel panel-default">
								<div class="panel-heading"><h4 style="margin:0"><strong><spring:message code="contingut.arxiu.grup.firma"/> ${firma.tipus}</strong></h4></div>
								<table class="table table-striped table-bordered">
								<tbody>
									<c:if test="${not empty firma.perfil}">
										<tr>
											<td><strong><spring:message code="contingut.arxiu.camp.firma.perfil"/></strong></td>
											<td>${firma.perfil}</td>
										</tr>
									</c:if>
									<c:if test="${not empty firma.fitxerNom}">
										<tr>
											<td><strong><spring:message code="contingut.arxiu.camp.firma.arxiu"/></strong></td>
											<td>${firma.fitxerNom}</td>
										</tr>
									</c:if>
									<c:if test="${not empty firma.tipusMime}">
										<tr>
											<td><strong><spring:message code="contingut.arxiu.camp.firma.tipus.mime"/></strong></td>
											<td>${firma.tipusMime}</td>
										</tr>
									</c:if>
									<c:if test="${firma.tipus == 'CSV'}">
										<tr>
											<td><strong><spring:message code="contingut.arxiu.camp.firma.csv"/></strong></td>
											<td>${firma.contingutComString}</td>
										</tr>
									</c:if>
									<c:if test="${not empty firma.csvRegulacio}">
										<tr>
											<td><strong><spring:message code="contingut.arxiu.camp.firma.csv.reg"/></strong></td>
											<td>${firma.csvRegulacio}</td>
										</tr>
									</c:if>
								</tbody>
								</table>
							</div>
						</c:forEach>
					</div>
				</c:if>
				<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
					<div role="tabpanel" class="tab-pane" id="metadades">
						<table class="table table-striped table-bordered">
							<c:set var="contMetadades" value="0" />
							<c:forEach var="metadada" items="${arxiuDetall.metadadesAddicionals}" varStatus="status">
								<tr>
									<c:choose>
										<c:when test="${contingut.document}">
											<spring:message code="contingut.arxiu.camp.metadades.docu.enum.${metadada.key}" var="message" text=""/>
											<c:if test="${not empty message}">
												<c:set var="contMetadades" value="${contMetadades + 1}" />
												<td width="20%"><strong><spring:message code="contingut.arxiu.camp.metadades.docu.enum.${metadada.key}"/></strong></td>
												<td>${metadada.value}</td>
											</c:if>			
										</c:when>
										<c:when test="${contingut.expedient}">
											<spring:message code="contingut.arxiu.camp.metadades.exp.enum.${metadada.key}" var="message" text=""/>
											<c:if test="${not empty message}">
												<c:set var="contMetadades" value="${contMetadades + 1}" />
												<td width="20%"><strong><spring:message code="contingut.arxiu.camp.metadades.exp.enum.${metadada.key}"/></strong></td>
												<td>${metadada.value}</td>
											</c:if>			
										</c:when>
										<c:otherwise>
											<c:set var="contMetadades" value="${contMetadades + 1}" />
											<td width="20%"><strong>${metadada.key}</strong></td>
											<td>${metadada.value}</td>
										</c:otherwise>	
									</c:choose>
								</tr>
							</c:forEach>
						</table>
						<script type="text/javascript">
							$("#metadadesSpan").text(${contMetadades});
						</script>
					</div>
				</c:if>
			</div>
		</c:when>
		<c:otherwise>
			<div class="alert alert-warning well-sm" role="alert">
				<span class="fa fa-info-circle"></span>
				<spring:message code="contingut.arxiu.no.alta"/>
			</div>
			<br/>
			<%--div style="text-align:center">
				<a href="#" class="btn btn-success">
					<span class="fa fa-download"></span>
					Emmagatzemar a dins l'arxiu digital
				</a>
			</div--%>
			<br/>
		</c:otherwise>
	</c:choose>
</body>
</html>
