<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<style>
    .caret-up {
        display: inline-block;
        transform: rotate(180deg);
    }
</style>

<script type="text/javascript">
    function toggleDocumentPreview(button) {
        var $button = $(button);
        var $preview = $button.closest('dd').find('> .document-preview').first();
        var $caret = $button.find('.document-preview-caret');
        var visible = $preview.is(':visible');

        $preview.toggle(!visible);
        $caret.attr('class', visible ? 'caret document-preview-caret' : 'caret caret-up document-preview-caret');
    }
</script>

<c:forEach var="item" items="${fills}">
    <c:if test="${not empty item}">
        <c:if test="${not empty item}">
            <c:set var="margin" value="${param.margin}" />
            <div style="margin-left: ${margin}px;">
                <dt style="display: inline; float: left; margin-right: 12px;">- ${item.titol}<c:if test="${not empty item.descripcio}">:</c:if></dt>
                <dd>
                    <c:choose>
                        <c:when test="${item.document and not empty item.documentContingutBase64}">
                            <a href="data:${item.documentMimeType};base64,${item.documentContingutBase64}" download="${item.documentNom}" class="btn btn-default btn-xs">
                                <i class="fas fa-download"></i> <spring:message code="comu.boto.descarregar"/>
                            </a>
                            <c:if test="${item.documentPdf}">
                                <button type="button" class="btn btn-default btn-xs" onclick="toggleDocumentPreview(this); return false;">
                                    <i class="fas fa-eye"></i> Previsualitzar
                                    <span class="caret document-preview-caret"></span>
                                </button>
                                <div class="document-preview" style="margin-top: 8px; display: none;">
                                    <object data="data:application/pdf;base64,${item.documentContingutBase64}" type="application/pdf" width="100%" height="480">
                                        <a href="data:${item.documentMimeType};base64,${item.documentContingutBase64}" download="${item.documentNom}" class="btn btn-default btn-xs">
                                            <i class="fas fa-download"></i> <spring:message code="comu.boto.descarregar"/>
                                        </a>
                                    </object>
                                </div>
                            </c:if>
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
