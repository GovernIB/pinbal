<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${param.esSubgrup}">
    <c:set var="grup" value="${subgrup}"/>
    <c:set var="grupStatus" value="${subgrupStatus}"/>
</c:if>
<div class="well well-sm" <c:if test="${param.esSubgrup}">style="background-color: #FFFFFF;"</c:if>>
    <fieldset>
        <legend>
            ${grup.nom}
            <c:if test="${not empty grup.ajuda}">
                <button type="button" class="btn btn-sm btn-info btn-ppv" data-toggle="popover" title="Ajuda" data-html="true" data-content='${grup.ajuda}'><span class="fa fa-info"></span></button>
            </c:if>
            <c:if test="${!param.esSubgrup}"><a id="boto-nou-grup" class="btn btn-primary pull-right boto-grup-nou" data-pare="${grup.id}" style="margin-bottom: 10px;"><i class="fas fa-plus"></i>&nbsp;Nou subgrup</a></c:if>
        </legend>
    </fieldset>
    <c:if test="${not empty campsAgrupats[grup.id]}">
        <table id="table-camps-grup-${grup.id}" class="table table-striped table-bordered taula-camps" style="width: 100%">
            <thead>
            <tr>
                <th data-data="campNom"><spring:message code="servei.camp.taula.columna.nom" /></th>
                <th data-data="tipus"><spring:message code="servei.camp.taula.columna.tipus" /></th>
                <th data-data="etiqueta"><spring:message code="servei.camp.taula.columna.etiqueta" /></th>
                <c:if test="${servei.pinbalIniDadesExpecifiques}">
                    <th data-data="inicialitzar" style="width: 1%"><spring:message code="servei.camp.taula.columna.i" /></th>
                </c:if>
                <th data-data="obligatori" style="width: 1%"><spring:message code="servei.camp.taula.columna.o" /></th>
                <th data-data="modificable" style="width: 1%"><spring:message code="servei.camp.taula.columna.m" /></th>
                <th data-data="visible" style="width: 1%"><spring:message code="servei.camp.taula.columna.v" /></th>
                <th data-data="path" style="width: 1%"></th>
                <th data-data="id" style="width: 1%"></th>
                <th data-data="campPare" style="width: 1%"></th>
                <th data-data="validacioRegexp" style="width: 1%"></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${campsAgrupats[grup.id]}" var="camp" varStatus="loopcamps">
                <c:set var="camp" value="${camp}" scope="request"/>
                <c:set var="loopcamps" value="${loopcamps}" scope="request"/>
<%--                    <c:set var="id" value="${id}" scope="request"/>--%>
<%--                    <c:set var="grups" value="${grups}" scope="request"/>--%>
                <jsp:include page="serveiCampGrupFila.jsp"/>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${not empty grup.fills}">
        <c:forEach var="subgrup" items="${grup.fills}" varStatus="subgrupStatus">
            <c:set var="subgrup" value="${subgrup}" scope="request"/>
            <c:set var="subgrupStatus" value="${subgrupStatus}" scope="request"/>
            <jsp:include page="serveiCampGrup.jsp">
                <jsp:param name="esSubgrup" value="${true}"/>
            </jsp:include>
        </c:forEach>
    </c:if>
    <c:if test="${empty campsAgrupats[grup.id] and empty grup.fills}">
        <p style="text-align:center"><spring:message code="servei.camp.grup.buit"/></p>
    </c:if>
    <div style="text-align: right">
        <a href="campGrup/${grup.id}/up" title="<spring:message code="comu.boto.pujar"/>" class="btn btn-default <c:if test="${grupStatus.first}"> disabled</c:if>"><i class="fas fa-arrow-up"></i></a>
        <a href="campGrup/${grup.id}/down" title="<spring:message code="comu.boto.baixar"/>" class="btn btn-default <c:if test="${grupStatus.last}"> disabled</c:if>"><i class="fas fa-arrow-down"></i></a>
        <a href="#modal-grup-form" title="<spring:message code="comu.boto.modificar"/>" class="btn btn-default boto-grup-editar" data-id="${grup.id}" data-nom="${grup.nom}" data-pare="${grup.pareId}" data-ajuda="${fn:escapeXml(grup.ajudaHtml)}"><i class="fas fa-pen"></i></a>
        <a href="campGrup/${grup.id}/delete" title="<spring:message code="comu.boto.esborrar"/>" class="btn btn-default confirm-esborrar-grup"><i class="fas fa-trash-alt"></i></a>
    </div>
</div>