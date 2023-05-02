<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
    <tr id="${camp.ordre}" data-campid="${camp.id}">
        <td><span title="${ camp.path }">${ camp.campNom }</span></td>
        <td>${ camp.tipus }</td>
        <td>${ camp.etiqueta }</td>
        <c:if test="${servei.pinbalIniDadesExpecifiques}">
            <td><c:if test="${ camp.inicialitzar }"><i class="fa fa-check"></i></c:if></td>
        </c:if>
        <td><c:if test="${ camp.obligatori }"><i class="fa fa-check"></i></c:if></td>
        <td><c:if test="${ camp.modificable }"><i class="fa fa-check"></i></c:if></td>
        <td><c:if test="${ camp.visible }"><i class="fa fa-check"></i></c:if></td>
        <c:if test="${not empty grups}">
            <td>
                <div class="btn-group">
                    <a href="#" title="<spring:message code="servei.camp.taula.accio.agrupar"/>" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                        <i class="far fa-arrow-alt-circle-down"></i>&nbsp;<span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <c:forEach var="grup" items="${grups}">
                            <li><a href="camp/${ camp.id }/agrupar/${grup.id}">${grup.nom}</a></li>
                            <c:if test="${not empty grup.fills}">
                            <ul class="dropdown-submenu">
                                <c:forEach var="subgrup" items="${grup.fills}">
                                    <li><a href="camp/${ camp.id }/agrupar/${subgrup.id}">${subgrup.nom}</a></li>
                                </c:forEach>
                            </ul>
                            </c:if>
                        </c:forEach>
                        <c:if test="${not empty camp.grup}">
                            <li role="separator" class="divider"></li>
                            <li><a href="camp/${ camp.id }/desagrupar/"><spring:message code="servei.camp.taula.accio.desagrupar"/></a></li>
                        </c:if>
                    </ul>
                </div>
            </td>
        </c:if>
        <td>
            <a href="#modal-editar-camp" data-nrow="${ loopcamps.index }"
                data-id="${ camp.id }"
                data-path="${ camp.path }"
                data-tipus="${ camp.tipus }"
                data-etiqueta="${ camp.etiqueta }"
                data-valorperdefecte="${ camp.valorPerDefecte }"
                data-comentari="${ camp.comentari }"
                data-dataformat="${ camp.dataFormat }"
                <c:if test="${ not empty camp.campPare }">
                    data-camppare="${ camp.campPare.id }"
                </c:if>
                <c:if test="${ empty camp.campPare }">
                    data-camppare="0"
                </c:if>
                data-valorpare="${ camp.valorPare }"
                data-inicialitzar="${ camp.inicialitzar }"
                data-obligatori="${ camp.obligatori }"
                data-modificable="${ camp.modificable }"
                data-visible="${ camp.visible }"
                data-validacio-regexp="${ camp.validacioRegexp }"
                data-validacio-min="${ camp.validacioMin }"
                data-validacio-max="${ camp.validacioMax }"
                data-validacio-data-cmp-operacio="${ camp.validacioDataCmpOperacio }"
                data-validacio-data-cmp-camp2="${ camp.validacioDataCmpCamp2.id }"
                data-validacio-data-cmp-nombre="${ camp.validacioDataCmpNombre }"
                data-validacio-data-cmp-tipus="${ camp.validacioDataCmpTipus }"
                class="btn btn-default btn-edit-camp" title="<spring:message code="servei.camp.taula.accio.modificar"/>">
                <i class="fas fa-pen"></i>
            </a>
        </td>
        <td>
            <a href="camp/${ camp.id }/delete" title="<spring:message code="servei.camp.taula.accio.esborrar"/>" class="btn btn-default confirm-esborrar">
                <i class="fas fa-trash-alt"></i>
            </a>
        </td>
    </tr>