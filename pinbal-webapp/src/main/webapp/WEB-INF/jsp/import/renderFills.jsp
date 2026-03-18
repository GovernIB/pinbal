<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:forEach var="item" items="${fills}">
    <c:if test="${not empty item}">
        <c:if test="${not empty item}">
            <c:set var="margin" value="${param.margin}" />
            <div style="margin-left: ${margin}px;">
                <dt style="display: inline; float: left; margin-right: 12px;">- ${item.titol}<c:if test="${not empty item.descripcio}">:</c:if></dt>
                <dd>
                    <c:choose>
                        <c:when test="${item.document and not empty item.descripcio}">
                            <a href="data:application/octet-stream;base64,${item.descripcio}" download="${item.titol}" class="btn btn-default btn-xs">
                                <i class="fas fa-download"></i> <spring:message code="comu.boto.descarregar"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            ${item.descripcio}<c:if test="${empty item.descripcio}"><br/></c:if>
                        </c:otherwise>
                    </c:choose>
                </dd>
            </div>
            <c:if test="${not empty item.fills}">
                <c:set var="fills" value="${item.fills}" scope="request"/>
                <jsp:include page="renderFills.jsp" >
                    <jsp:param name="margin" value="${margin  + 20}" />
                </jsp:include>
            </c:if>
        </c:if>
    </c:if>
</c:forEach>