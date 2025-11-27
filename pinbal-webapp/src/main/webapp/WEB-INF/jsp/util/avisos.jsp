<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    // Asseguram que la llista d'avisos és present a la petició
    pageContext.setAttribute(
            "avisos",
            es.caib.pinbal.webapp.helper.AvisHelper.getAvisos(request));
%>
<c:if test="${not empty avisos}">
    <div id="accordion">
        <c:forEach var="avis" items="${avisos}" varStatus="status">
            <div class="card avisCard ${avis.avisNivell == 'INFO' ? 'avisCardInfo':''} ${avis.avisNivell == 'WARNING' ? 'avisCardWarning':''} ${avis.avisNivell == 'ERROR' ? 'avisCardError':''}">
                <div data-toggle="collapse" data-target="#collapse${status.index}" class="card-header avisCardHeader">
                    ${avis.avisNivell == 'INFO' ? '<span class="fa fa-info-circle text-info"></span>':''} ${avis.avisNivell == 'WARNING' ? '<span class="fa fa-exclamation-triangle text-warning"></span>':''} ${avis.avisNivell == 'ERROR' ? '<span class="fa fa-exclamation-triangle text-danger"></span>':''} ${avis.assumpte}
                    <button class="btn btn-default btn-xs pull-right"><span class="fa fa-chevron-down "></span></button>
                </div>
                <div id="collapse${status.index}" class="collapse" data-parent="#accordion">
                    <div class="card-body avisCardBody">${avis.missatge}</div>
                </div>
            </div>
                </c:forEach>
    </div>
</c:if>
