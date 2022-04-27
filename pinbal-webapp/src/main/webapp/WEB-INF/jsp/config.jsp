<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title><spring:message code="config.titol"/></title>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
<%--    <script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>--%>
<%--    <link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>--%>
    <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
    <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
<script>
    $(document).ready(function() {
        $("#btn-sync").on("click", function () {
            $.get('<c:url value="/config/sync"/>', function( data ) {
                $('#syncModal-body').html(
                    '<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
                    '	<span class="fa fa-circle-notch fa-spin fa-3x"></span> <br>' +
                    '   Sincronitzant propietats de l\'aplicació ' +
                    '</div>');
                if (data.status) {
                    let message = "S'han actualitzat satisfactoriament les següents propietats: ";
                    data.editedProperties.forEach( element => message += element + ", ");
                    alert(message);
                    document.location.reload();
                } else {
                    alert("Error actualitzant les propietats desde JBoss.");
                }
            });
        });

        $("#btn-reiniciar").on("click", () => {
            $.get('<c:url value="/config/reiniciarTasques"/>', () => {alert("Tasques reiniciades!")});
        })

        <c:url var="urlEdit" value="/config/update"/>
        $(".form-update-config").submit(function(e) {

            //prevent Default functionality
            e.preventDefault();

            let self = this;
            let formData = new FormData(this);
            $('#syncModal-body').html(
                '<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
                '	<span class="fa fa-circle-o-notch fa-spin fa-3x"></span> <br>' +
                '   Sincronitzant la propietat: ' + formData.get('key') +
                '</div>');
            $("#syncModal").modal("show");

            //do your own request an handle the results
            $.ajax({
                url: '${urlEdit}',
                type: 'post',
                processData: false,
                contentType: false,
                enctype: 'multipart/form-data',
                data: formData,
                success: function(data) {
                    $("#syncModal").modal("hide");
                    if (data.status === 1) {
                        alert("La propietat " + formData.get('key') + " s'ha editat satisfactoriament");
                    } else {
                        alert("Hi ha hagut un error editant la propietat");
                        document.location.reload();
                    }
                }
            });

        });

        $('.a-config-group:first').tab('show');
    });
</script>
<div class="text-right" data-toggle="botons-titol">
    <button data-toggle="dropdown" class="btn btn-primary dropdown-toggle"><spring:message code="comu.accions"/>&nbsp;<span class="caret caret-white"></span></button>
    <ul class="dropdown-menu dropdown-menu-right">
        <li><a id="btn-sync" data-toggle="modal" data-target="#syncModal"><span class="fa fa-sync-alt"></span>&nbsp;Sincronitzar amb JBoss</a></li>
        <li><a id="btn-reiniciar"><span class="fa fa-redo-alt"></span>&nbsp;Reiniciar tasques en segon pla</a></li>
    </ul>
<%--    <a id="btn-reiniciar" class="btn btn-default"><span class="fa fa-redo-alt"></span>&nbsp;Reiniciar tasques en segon pla</a>--%>
<%--    <a id="btn-sync" class="btn btn-default" data-toggle="modal" data-target="#syncModal"><span class="fa fa-sync-alt"></span>&nbsp;Sincronitzar amb JBoss</a>--%>
</div>
<div id="syncModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Sincronitzant propietats</h4>
            </div>
            <div id="syncModal-body" class="modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Tanca</button>
            </div>
        </div>

    </div>
</div>
    <div class="row">
        <div class="col-md-3">
            <ul id="tab-list" class="nav nav-pills nav-stacked">
                <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                    <li role="presentation">
                        <a class="a-config-group" data-toggle="tab" href="#group-${group.key}"><spring:message code="${group.descriptionKey}"/></a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="col-md-9">
            <div class="tab-content">
            <c:forEach items="${config_groups}" var="group" varStatus="status_group">
                <c:set var="group" value="${group}" scope="request"/>
                <c:set var="level" value="0" scope="request"/>
                <div id="group-${group.key}" class="tab-pane fade">
                    <jsp:include page="import/configGroup.jsp"/>
                </div>
            </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>
