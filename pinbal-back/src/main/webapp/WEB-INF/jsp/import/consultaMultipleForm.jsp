<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/Excel" var="downloadPlantillaExcelUrl"/>
<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/CSV" var="downloadPlantillaCsvUrl"/>
<c:url value="/consulta/${servei.codiUrlEncoded}/plantilla/ODS" var="downloadPlantillaOdsUrl"/>

<div class="container-fluid">
    <c:if test="${not empty consultaCommand.multipleErrorsValidacio}">
        <div id="errorsFitxer" class="errorsFitxer well alert-danger">
            <div id="errorsFitxerBody">
                <h4><spring:message code="consulta.form.camp.multiple.errors.fitxer"/></h4>
                <c:forEach items="${consultaCommand.multipleErrorsValidacio}" var="error">
                    <p style="margin-left:20px">${error}</p>
                </c:forEach>
            </div>
            <button type="button" id="cbcopy" class="btn btn-default pull-right"><span class="fa fa-clipboard"></span> <spring:message code="comu.clipboard.copy"/></button>
        </div>
    </c:if>
    <div class="row">
        <div class="col-md-12">
            <br />
            <p>	<spring:message code="consulta.form.multiple.info.titular"/><br />
                <spring:message code="consulta.form.multiple.info.plantilla"/>
            </p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-6">
            <div class="form-group">
                <label class="control-label" for="plantilla"><spring:message code="consulta.form.camp.multiple.plantilla"/></label>
                <div class="controls">
                    <a href="${downloadPlantillaExcelUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.excel" />">
                        <i class="far fa-file-excel"></i> <spring:message code="consulta.form.camp.multiple.fitxer.excel" />
                    </a>
                    <a href="${downloadPlantillaOdsUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.ods" />">
                        <i class="fas fa-file-excel"></i> <spring:message code="consulta.form.camp.multiple.fitxer.ods" />
                    </a>
                    <a href="${downloadPlantillaCsvUrl}" class="btn btn-default btn-editar" title="<spring:message code="consulta.form.camp.multiple.plantilla.csv" />">
                        <i class="fas fa-file-csv"></i> <spring:message code="consulta.form.camp.multiple.fitxer.csv" />
                    </a>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <c:set var="campPath" value="multipleFitxer"/>
            <pbl:inputFile inline="false" name="${campPath}" textKey="consulta.form.camp.multiple.fitxer" required="true"/>
        </div>
    </div>
</div>
