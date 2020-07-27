<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>

<%
	request.setAttribute(
			"entitatTipusLlista",
			es.caib.pinbal.core.dto.ServeiDto.EntitatTipusDto.values());
	request.setAttribute(
			"justificantTipusLlista",
			es.caib.pinbal.core.dto.ServeiDto.JustificantTipusDto.values());
%>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${empty serveiCommand.codi}"><spring:message code="servei.form.titol.crear"/></c:when>
			<c:otherwise><spring:message code="servei.form.titol.modificar"/></c:otherwise>
		</c:choose>
	</title>
   
   	<link href="<c:url value="/webjars/datatables/1.10.21/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>

	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables/1.10.21/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables/1.10.21/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/mustache.js/3.0.1/mustache.min.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>" type="text/javascript"></script>
	<script src="<c:url value="/webjars/datatables-plugins/1.10.20/dataRender/datetime.js"/>"></script>
	<script src="<c:url value="/webjars/momentjs/2.24.0/min/moment.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>




<script>

$(document).ready(function() {
	$("#scspEsquemas").attr("disabled", $("#activaGestioXsd").attr("checked") ? true : false);
	$("#gestioXsdFieldSet").attr("hidden", $("#activaGestioXsd").attr("checked") ? false : true);
	
	$('#modal-boto-submit').click(function() {
		$('#modal-redir-form .modal-body').load(
			'<c:url value="/modal/servei/${serveiCommand.codi}/redir/save"/>',
			$('#modal-form').serializeArray());
	});

	$('#modal-boto-submit-xsd').click(function() {
		$('#nomArxiu').removeAttr('disabled');
		var data = $('#xsd-form').serializeArray();
		var contingut = {"name": "contingut", "value": $("#nomArxiu")[0].files[0]};
		var formData =  new FormData();
		data.forEach(function(row){
			formData.append(row.name, row.value);
		});
		data.push(contingut);
		formData.append('contingut', $("#nomArxiu")[0].files[0], data[2].name);
 		formData.append('nomArxiu', $("#nomArxiu")[0].files[0].name);
		data.forEach(function(row){
			console.log(row);
		});
		$.ajax({
			type: "POST",
			url: '<c:url value="/modal/servei/${serveiCommand.codi}/xsd/save"/>',
			data: formData,
			processData: false,  // tell jQuery not to process the data
			contentType: false,   // tell jQuery not to set contentType
			success: function(data, textStatus, jqXHR){
				console.log(data);
				var jsonData = JSON.parse(data)
				if (jsonData){
					if(jsonData.error){
						jsonData.errors.forEach(function(error){
							
							if(error.camp == "contingut"){
								error.camp = "nomArxiu";
							}
							$("#" + error.camp + "Control").addClass("error");
							if(error.errorMsg != undefined && error.errorMsg != ""){
								$("#" + error.camp + "Label").append(error.errorMsg);
								$("#" + error.camp + "Label").css("display", 'block');
							}
						});
					}else{
						location.reload();
					}
				}	
			}
		});
	});
	
	$('.confirm-esborrar').click(function() {
		  return confirm("<spring:message code="servei.form.bus.confirmacio.esborrar"/>");
	});
	$('#ajuda').htmlarea({
        toolbar: [
					["html"],
                   	["bold", "italic", "underline", "strikethrough", "|", "forecolor"],
                   	["subscript", "superscript", "|" , "increasefontsize", "decreasefontsize"],
                   	["orderedList", "unorderedList"],
                   	["indent", "outdent"],
                   	["justifyleft", "justifycenter", "justifyright"],
                   	["link", "unlink", "image", "horizontalrule"],
                   	[ "p", "h1", "h2", "h3", "h4", "h5", "h6"],
                   	["cut", "copy", "paste"]
				],
		toolbarText: $.extend({}, jHtmlArea.defaultOptions.toolbarText, {
					"html": "Mostra/Oculta el codi HTML",
			        "bold": "Negreta",
			        "italic": "Cursiva",
			        "underline": "Subrallat",
			        "strikethrough": "Ratllat",
			        "forecolor": "Color del text",
			        "subscript": "Subíndex", 
			        "superscript": "Superíndex", 
			        "increasefontsize": "Augmenta la mida de la font", 
			        "decreasefontsize": "Disminueix la mida de la font",
                   	"orderedlist": "Insereix una llista ordenada", 
                   	"unorderedlist": "Insereix una llista no ordenada",
                   	"indent": "Augmenta el sagnat", 
                   	"outdent": "Disminueix el sagnat",
                   	"justifyleft": "Alinea a l'esquerra", 
                   	"justifycenter": "Alinea al centre", 
                   	"justifyright": "Alinea a la dreta",
                   	"link": "Insereix un enllaç", 
                   	"unlink": "Elimina un enllaç", 
                   	"image": "Insereix una imatge", 
                   	"horizontalrule": "Insereix una separació horitzontal",
                   	"p": "Paràgraf", 
                   	"h1": "Capçalera 1", 
                   	"h2": "Capçalera 2", 
                   	"h3": "Capçalera 3", 
                   	"h4": "Capçalera 4", 
                   	"h5": "Capçalera 5", 
                   	"h6": "Capçalera 6",
                   	"cut": "Retalla", 
                   	"copy": "Còpia", 
                   	"paste": "Enganxa"
			    })
	});
	
	$("#activaGestioXsd").change(function(value){
		$("#scspEsquemas").attr("disabled", $(this).attr("checked") ? true : false);
		$("#gestioXsdFieldSet").attr("hidden", $(this).attr("checked") ? false : true);
	});
});
function showModalRedir(element) {
<c:choose>
	<c:when test="${not empty serveiCommand.pinbalEntitatTipus}">
		$('#modal-redir-form .modal-body').load(element.href);
		$('#modal-redir-form').modal('toggle');
	</c:when>
	<c:otherwise>
		alert("<spring:message code="servei.form.alert.no.entitat.tipus"/>");
	</c:otherwise>
</c:choose>
}

function esborraFitxerXsd(tipusFitxer) {
	
	if(confirm("<spring:message code="servei.form.xsd.confirmacio.esborrar"/>")){
		//var tipusFitxer = $("#tipusXsd").val();
		$.ajax({
			url: '<c:url value="/servei/${serveiCommand.codi}/xsd/'+ tipusFitxer +'/delete"/>',
			success: function(info){
				location.reload();
			}
		});
	}
};

function showModalXsd(element) {
		$('#modal-xsd-form .modal-body').load(element.href);
		$('#modal-xsd-form').modal('toggle');	
	}
</script>
</head>
<body>

	<c:url value="/servei/save" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="well" 
			   commandName="serveiCommand" enctype="multipart/form-data">
		<form:hidden path="creacio"/>
		<fieldset>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-12">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.codi"/> *</label>
						<pbl:inputText name="codi" inline="true" placeholderKey="servei.form.camp.codi"/>
<%-- 						<c:set var="campPath" value="codi"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
					</div>
				</div>	
				<div class="row">
					<div class="col-md-12">
					
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.descripcio"/> *</label>
						<pbl:inputText name="codi" inline="true" placeholderKey="servei.form.camp.descripcio"/>
					
<%-- 					<c:set var="campPath" value="descripcio"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.descripcio"/> *</label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:textarea rows="8" path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.emisor"/> *</label>
						<pbl:inputSelect name="scspEmisor" inline="true"  placeholderKey="servei.form.camp.scsp.emisor" 
						optionItems="${emisors}"
						optionValueAttribute="cif"
						optionTextAttribute="nom"
						emptyOption="true"/>
						
	<%-- 					<c:set var="campPath" value="scspEmisor"/> --%>
	<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
	<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
							
	<!-- 						<div class="controls"> -->
	<%-- 							<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}"> --%>
	<%-- 								<option value=""><spring:message code="comu.opcio.sense.definir"/></option> --%>
	<%-- 								<form:options items="${emisors}" itemLabel="nom" itemValue="cif"/> --%>
	<%-- 							</form:select> --%>
	<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
	<!-- 						</div> -->
	<!-- 					</div> -->
					</div>
				</div>
				
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.param.pinbal"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="container-fluid well">
				<div class="row">
					<div class="col-md-6">
						<pbl:inputText name="pinbalCustodiaCodi" textKey="servei.form.camp.pinbal.custodia.codi"/>
					</div>
					<div class="col-md-6">
						<pbl:inputText name="pinbalRoleName" textKey="servei.form.camp.pinbal.role.name"/>
					</div>
					</div>
				<div class="row">
					<div class="col-md-6">
					<pbl:inputText name="pinbalCondicioBusClass" textKey="servei.form.camp.pinbal.condicio.bus.class"/>
				</div>
					<div class="col-md-6">
					<pbl:inputSelect name="pinbalEntitatTipus" textKey="servei.form.camp.pinbal.entitat.tipus" emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
				</div>
				<div class="row">
				<div class="col-md-6">
					<pbl:inputSelect name="pinbalJustificantTipus" textKey="servei.form.camp.pinbal.justificant.tipus" />
				</div>
				<div class="col-md-6">
					<pbl:inputText name="pinbalJustificantXpath" textKey="servei.form.camp.pinbal.justificant.xpath"/>
				</div>
			</div>
			</div>
			<label>&nbsp;</label>
			<div class="container-fluid well">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label class="control-label"><spring:message code="servei.form.camp.pinbal.camps.dadesgen"/></label>
								<div class="controls">
								<c:set var="campPath" value="pinbalActiuCampNom"/>
								<label class="checkbox inline" for="${campPath}">
									<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.nom"/>
								</label>
								<c:set var="campPath" value="pinbalActiuCampLlinatge1"/>
								<label class="checkbox inline" for="${campPath}">
									<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.llinatge1"/>
								</label>
								<c:set var="campPath" value="pinbalActiuCampLlinatge2"/>
								<label class="checkbox inline" for="${campPath}">
									<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.llinatge2"/>
								</label>
								<c:set var="campPath" value="pinbalActiuCampNomComplet"/>
								<label class="checkbox inline" for="${campPath}">
									<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.nomcomp"/>
								</label>
								<c:set var="campPath" value="pinbalActiuCampDocument"/>
								<label class="checkbox inline" for="${campPath}">
									<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.document"/>
								</label>
							</div>
						</div>
				</div>	
			
				<div class="col-md-6">
					<div class="form-group">
						<label class="control-label"><spring:message code="servei.form.camp.pinbal.document.tipus"/></label>
						<div class="controls">
							<c:set var="campPath" value="pinbalPermesDocumentTipusDni"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.DNI"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusNif"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.NIF"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusCif"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.CIF"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusNie"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.NIE"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusPas"/>
							<label class="checkbox inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.Passaport"/>
							</label>
						</div>
					</div>
				</div>
			</div>
			</div>
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.pinbal.document.obligatori"/></label>
<%-- 					<pbl:inputText name="pinbalDocumentObligatori" inline="true" placeholderKey="servei.form.camp.pinbal.document.obligatori"/> --%>
						<c:set var="campPath" value="pinbalDocumentObligatori"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							
							<div class="controls">
								<form:checkbox path="${campPath}" id="${campPath}"/>
								<form:errors path="${campPath}" cssClass="help-block"/>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<c:set var="campPath" value="pinbalComprovarDocument"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>">
							<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.pinbal.comprovar.document"/></label>
								<div class="controls">
									<form:checkbox path="${campPath}" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
						</div>
					</div>
				</div>

				</div>	
				
			<div class="container-fluid">
					<div class="row">
						<div class="col-md-3">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.pinbal.dir3"/></label>
							<pbl:inputText name="pinbalUnitatDir3" inline="true" placeholderKey="servei.form.camp.pinbal.dir3"/>
						</div>
					</div>	
			</div>
				<div class="container-fluid">
					<div class="row">
						<div class="col-md-12">
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.ajuda"/></label> --%>
							
							<pbl:inputTextarea name="ajuda"  textKey="servei.form.camp.ajuda" />
								
							
	<!-- 				<c:set var="campPath" value="ajuda"/> -->
	<%-- 				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
	<%-- 				<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
					
<!-- 					<div class="controls"> -->
<%-- 						<form:textarea rows="8" path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 						<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 					</div> -->
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.fitxer.ajuda"/></label>
						<pbl:inputText name="fitxerAjuda" inline="true" placeholderKey="servei.form.camp.fitxer.ajuda"/>
						<c:set var="campPath" value="fitxerAjuda"/>
						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
							
							<div class="controls input-append" style="margin-left:20px;width:calc(100% - 180px);">
								<input type="text" id="${campPath}_txt" class="form-control ajuda_file" value="${serveiCommand.fitxerAjudaNom}" style="width:calc(100% - 92px);"/>
								<span id="ajuda_clean" class="btn btn-default btn-file-clean"><i class='icon-trash'></i></span>
								<span id="ajuda_file" class="btn btn-default btn-file ajuda_file"><i class='icon-file'></i></span>
							</div>
							<form:input type="file" path="${campPath}" cssClass="hide" id="${campPath}" title="" />
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
				</div>
			<script>
				$(document).ready(function() {
					var fileTextInput = $('#fitxerAjuda_txt');
					if (!$('#fitxerAjuda_txt').val()) {
						$('#ajuda_clean').attr('disabled', 'disabled');
						$("#ajuda_file").click(function() {
							if (!$('#fitxerAjuda_txt').val())
								$('#fitxerAjuda').trigger('click');
						});
					} else {
						$('#fitxerAjuda_txt').attr('disabled', 'disabled');
						$('#ajuda_file i').removeClass("icon-file").addClass("icon-download-alt");
						$('#ajuda_file').click(function() {
							location.href = "<c:url value='/servei/${serveiCommand.codi}/downloadAjuda'/>";
						});
					}
					$("#fitxerAjuda").change(
						function() {
							var input = $(this),
							numFiles = input.get(0).files ? input.get(0).files.length : 1,
							label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
							input.trigger('fileselect', [numFiles, label]);
						}
					);
					$("#fitxerAjuda_txt").click(
						function() {
							if (!$('#fitxerAjuda_txt').val())
								$('#fitxerAjuda').trigger('click');
						}	
					);
					$('label[for="fitxerAjuda"]').click(
						function(e) { 
					    	e.preventDefault();
						}	
					);
					$("#ajuda_clean").click(
						function() {
							// Nomes s'ha d'actualitzar l'input de tipus text!!!
							$('#fitxerAjuda_txt').val('');
							$('#fitxerAjuda_txt').removeAttr('disabled');
							$('#ajuda_clean').attr('disabled', 'disabled');
							if ($('#ajuda_file i').hasClass("icon-download-alt")) {
								$('#ajuda_file i').removeClass("icon-download-alt").addClass("icon-file");
								$("#ajuda_file").unbind("click");
								$("#ajuda_file").bind("click", function() {
									if (!$('#fitxerAjuda_txt').val())
										$('#fitxerAjuda').trigger('click');
								});
							} else {
								$('#ajuda_file').removeAttr('disabled');
								$('#fitxerAjuda').val('');
							}
						}
					);
					$('#fitxerAjuda').on('fileselect', function (event, numFiles, label) {
						if (label) {
							$('#ajuda_clean').removeAttr('disabled');
							$('#ajuda_file').attr('disabled', 'disabled');
							// Nomes s'ha d'actualitzar l'input de tipus text!!!
							$('#fitxerAjuda_txt').val(label);
							$('#fitxerAjuda_txt').attr('disabled', 'disabled');
							$('#fitxerAjuda_txt').change();
						}
				    });
				});
			</script>
		</fieldset>
		<c:if test="${not empty serveiCommand.codi}">
			<fieldset>
			
			<div class="container-fluid">
				<legend><spring:message code="servei.form.legend.config.bus"/></legend>
					<div class="clearfix legend-margin-bottom"></div>
				
					<div class="row">
						<div class="col-md-11">
							<a class="btn btn-primary pull-right" href="<c:url value="/modal/servei/${serveiCommand.codi}/redir/new"/>" 
							onclick="showModalRedir(this);return false"><i class="fas fa-plus"></i>&nbsp;<spring:message code="servei.form.boto.nova.redireccio"/></a>
						</div>
					</div>
					<c:if test="${not empty serveisBus}">
					<div class="row">
						<div class="col-md-11">
							<br/>
							<table id="serveisBus" class="table table-striped table-bordered" style="width: 100%">
							<thead>
								<tr>
								<th><spring:message code="servei.form.bus.taula.columna.entitat" /></th>
								<th><spring:message code="servei.form.bus.taula.columna.url" /></th>
								<th></th>
								<th></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${serveisBus}" var="registre">
						   			<tr>
										<td>
										${ registre.entitat.nom }
										</td>
										<td>
										${ registre.urlDesti }
										</td>
										<td>
										<a href="../modal/servei/${registre.servei}/redir/${registre.id}" title="<spring:message code="comu.boto.modificar"/>" 
										   class="btn btn-default" onclick="showModalRedir(this);return false">
										   <i class="fas fa-pen"></i>&nbsp;<spring:message code="comu.boto.modificar"/>
										</a>
										</td>
										<td>
										<a href="${registre.servei}/redir/${registre.id}/delete" class="btn btn-default confirm-esborrar">
											<i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/>
										</a>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							</table>
						</div>
					</div>
				</div>	
				</c:if>
			</fieldset>
		</c:if>
		<fieldset>
			<legend><spring:message code="servei.form.legend.validesa.servei"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputDate name="scspFechaAlta" textKey="servei.form.camp.scsp.fecha.alta"/>
<!-- 					<script>$("#scspFechaAlta").mask("99/99/9999");</script> -->
				</div>
				<div class="col-md-6">
					<pbl:inputDate name="scspFechaBaja"  textKey="servei.form.camp.scsp.fecha.baja"/>
<!-- 					<script>$("#scspFechaBaja").mask("99/99/9999");</script> -->
				</div>
			</div>
			
			<label>&nbsp;</label>
			<div class="row">
			
				<div class="col-md-6">
					<pbl:inputDate name="scspCaducidad" textKey="servei.form.camp.scsp.caducidad" placeholderKey="servei.form.legend.ubicacio.servei"/>
				</div>
			</div>
		</fieldset>
		
		<fieldset>
			<div class="container-fluid">
				<legend><spring:message code="servei.form.legend.ubicacio.servei"/></legend>
				<div class="clearfix legend-margin-bottom"></div>
				<div class="row">
					<div class="col-md-12">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.url.sincrona"/></label>
						<pbl:inputText name="scspUrlSincrona" inline="true" placeholderKey="servei.form.camp.scsp.url.sincrona"/>
<%-- 					<c:set var="campPath" value="scspUrlSincrona"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}" cssStyle="width:100%"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
					</div>
				</div>
			<div class="row">
				<div class="col-md-12">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.url.asincrona"/></label>
						<pbl:inputText name="scspUrlAsincrona" inline="true" placeholderKey="servei.form.camp.scsp.url.asincrona"/>
<%-- 					<c:set var="campPath" value="scspUrlAsincrona"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}" cssStyle="width:100%"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.action.sincrona"/></label>
					<pbl:inputText name="scspActionSincrona" inline="true" placeholderKey="servei.form.camp.scsp.action.sincrona"/>
<%-- 					<c:set var="campPath" value="scspActionSincrona"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
			
			<div class="col-md-6">
				<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.action.asincrona"/></label>
					<pbl:inputText name="scspActionAsincrona" inline="true" placeholderKey="servei.form.camp.scsp.action.asincrona"/>
<%-- 					<c:set var="campPath" value="scspActionAsincrona"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
			</div>
			</div>
		</div>	
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.action.solicitud"/></label>
					<pbl:inputText name="scspActionSolicitud" inline="true" placeholderKey="servei.form.camp.scsp.action.solicitud"/>
<%-- 					<c:set var="campPath" value="scspActionSolicitud"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
				
				
				
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.activa.gestio.xsd"/></label>
					<pbl:inputText name="activaGestioXsd" inline="true" placeholderKey="servei.form.camp.scsp.activa.gestio.xsd"/>
<%-- 					<c:set var="campPath" value="activaGestioXsd"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
					
<!-- 						<div class="controls"> -->
<%-- 							<form:checkbox path="${campPath}" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
			</div>
				
			
			<div class="row">
				<div class="col-md-6">
				<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.version.esquema"/> *</label>
					<pbl:inputText name="scspVersionEsquema" inline="true" placeholderKey="servei.form.camp.scsp.version.esquema"/>
<%-- 					<c:set var="campPath" value="scspVersionEsquema"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.version.esquema"/> *</label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
					</div>
	
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.esquemas"/></label>
					<pbl:inputText name="scspEsquemas" inline="true" placeholderKey="servei.form.camp.scsp.esquemas"/>
<%-- 					<c:set var="campPath" value="scspEsquemas"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.esquemas"/></label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}" disabled="${ activaGestioXsd ? 'true' : ''}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
			</div>
		</div>
			
		</fieldset>
		
<%-- 		<c:if test="${not empty serveiCommand.codi}"> --%>
			<fieldset id="gestioXsdFieldSet">
				<legend><spring:message code="servei.form.legend.gestio.xsd"/></legend>
				<div class="clearfix well"></div>
					
					<fieldset>
						<div class="container-fluid">
							<div class="row">
								<div class="col-md-6">
									<a class="btn btn-primary pull-right" href="<c:url value="/modal/servei/${serveiCommand.codi}/xsd/new"/>" onclick="showModalXsd(this);return false"><i class="fas fa-plus"></i>&nbsp;<spring:message code="servei.list.boto.nou.arxiuXsd"/></a>
								</div>
							</div>
						</div>	
					</fieldset>
				<c:if test="${not empty serveiCommand.fitxersXsd}">
					<div class="row">
						<div class="clearfix"></div>
					</div>
					
					<table id="arxiusXsd" class="table table-striped table-bordered" style="width: 100%">
					<thead>
						<tr>
						<th width="30%"><spring:message code="servei.xsd.camp.tipus" /></th>
						<th width="60%"><spring:message code="servei.xsd.camp.arxiu" /></th>
						<th width="10%"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${serveiCommand.fitxersXsd}" var="registre">
				   			<tr>
								<td>
								${ registre.tipus }
								</td>
								<td>
								<div style="width: 90%; display: inline-block;">${registre.nomArxiu}</div>
								<div style="width: 10%; display: inline;">
									<a class="btn btn-default btn-sm" href="<c:url value="/servei/${serveiCommand.codi}/xsd/${registre.tipus}/download"/>">
										<i class="fas fa-download"></i>&nbsp;<spring:message code="comu.boto.baixar"/>
									</a>
								</div>
								</td>
								<td>
								<button class="btn btn-default" onclick="esborraFitxerXsd('${registre.tipus}');" type="button">
									<i class="fas fa-trash-alt"></i>&nbsp;<spring:message code="comu.boto.esborrar"/>
								</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					</table>
				</c:if>
			</fieldset>
<%--		</c:if> --%>
		<fieldset>
			<legend><spring:message code="servei.form.legend.xifrat.seguretat"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="container-fluid">
				<div class="row">
						<div class="col-md-6">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.tipo.seguridad"/> *</label>
						<pbl:inputSelect name="scspTipoSeguridad" inline="true" placeholderKey="servei.form.camp.scsp.tipo.seguridad"
							optionItems=""
							optionValueAttribute="XMLSignature"
							optionTextAttribute="WS-Security"
							emptyOption="true"/>
					
<%-- 					<c:set var="campPath" value="scspTipoSeguridad"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}"> --%>
<%-- 								<option value=""><spring:message code="comu.opcio.sense.definir"/></option> --%>
<%-- 								<form:option value="XMLSignature"/> --%>
<%-- 								<form:option value="WS-Security"/> --%>
<%-- 							</form:select> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
						</div>
				</div>	
			    <div class="row">
					<div class="col-md-6">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.algoritmo.cifrado"/></label>
						<pbl:inputText name="scspAlgoritmoCifrado" inline="true" placeholderKey="servei.form.camp.scsp.algoritmo.cifrado"/>
<%-- 					<c:set var="campPath" value="scspAlgoritmoCifrado"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.algoritmo.cifrado"/></label> --%>
<%-- 						<div class="controls"> --%>
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<%-- 						</div> --%>
<%-- 					</div> --%>
					</div>
					<div class="col-md-6">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.validacion.firma"/></label>
						<pbl:inputText name="scspValidacionFirma" inline="true" placeholderKey="servei.form.camp.scsp.validacion.firma"/>
<%-- 					<c:set var="campPath" value="scspValidacionFirma"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 							<div class="controls"> -->
<%-- 								<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 								<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 							</div> -->
<!-- 						</div> -->
					</div>
			</div>
			<div class="row">
					
					<div class="col-md-6">
						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.clave.firma"/> *</label> 
						<pbl:inputText name="scspClaveFirma" inline="true" placeholderKey="servei.form.camp.scsp.clave.firma"/>
				
<%-- 					<c:set var="campPath" value="scspClaveFirma"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.clave.firma"/> *</label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}"> --%>
<%-- 								<option value=""><spring:message code="comu.opcio.sense.definir"/></option> --%>
<%-- 								<form:options items="${clausPrivades}" itemLabel="nom" itemValue="alies"/> --%>
<%-- 							</form:select> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
					</div>
					
					<div class="col-md-6">
						<label class="control-label" for="${campPath}"><spring:message code="comu.opcio.sense.definir"/></label>
						<pbl:inputText name="scspClaveCifrado" inline="true" placeholderKey="comu.opcio.sense.definir"/>
<%-- 						<c:set var="campPath" value="scspClaveCifrado"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:select path="${campPath}" cssClass="col-md-12" id="${campPath}"> --%>
<%-- 								<option value=""><spring:message code="comu.opcio.sense.definir"/></option> --%>
<%-- 								<form:options items="${clausPubliques}" itemLabel="nom" itemValue="alies"/> --%>
<%-- 							</form:select> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
					</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.cifrado.sincrono"/></label>
					<pbl:inputText name="scspXpathCifradoSincrono" inline="true" placeholderKey="servei.form.camp.scsp.xpath.cifrado.sincrono"/>
<%-- 						<c:set var="campPath" value="scspXpathCifradoSincrono"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							
<!-- 							<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
				</div>
			
				
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.cifrado.asincrono"/></label>
					<pbl:inputText name="scspXpathCifradoAsincrono" inline="true" placeholderKey="servei.form.camp.scsp.xpath.cifrado.asincrono"/>
<%-- 						<c:set var="campPath" value="scspXpathCifradoAsincrono"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.cifrado.asincrono"/></label> --%>
<!-- 						<div class="controls">form-control -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
			</div>
		</div>	
		</fieldset>
		<fieldset>
			<div class="container-fluid">
				<div class="row">
					<legend><spring:message code="servei.form.legend.config.enviaments"/></legend>
						<div class="clearfix legend-margin-bottom"></div>
						<div class="col-md-6">
							<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.prefijo.peticion"/></label>
							<pbl:inputText name="scspPrefijoPeticion" inline="true" placeholderKey="servei.form.camp.scsp.prefijo.peticion"/>
<%-- 						<c:set var="campPath" value="scspPrefijoPeticion"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 							<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.prefijo.peticion"/></label> --%>
<!-- 							<div class="controls"> -->
<%-- 								<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 								<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 							</div> -->
<!-- 						</div> -->
						</div>
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.prefijo.id.transmision"/></label>
					<pbl:inputText name="scspPrefijoIdTransmision" inline="true" placeholderKey="servei.form.camp.scsp.prefijo.id.transmision"/>
<%-- 						<c:set var="campPath" value="scspPrefijoIdTransmision"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
							
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.num.max.reenvios"/></label>
					<pbl:inputText name="scspPrefijoIdTransmision" inline="true" placeholderKey="servei.form.camp.scsp.prefijo.id.transmision"/>
<%-- 					<c:set var="campPath" value="scspNumeroMaximoReenvios"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
				<div class="col-md-6">
				<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.prefijo.id.transmision"/></label>
				<pbl:inputText name="scspMaxSolicitudesPeticion" inline="true" placeholderKey="servei.form.camp.scsp.prefijo.id.transmision"/>
<%-- 					<c:set var="campPath" value="scspMaxSolicitudesPeticion"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-6">
					<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.codigo.error"/></label>
					<pbl:inputText name="scspXpathCodigoError" inline="true" placeholderKey="servei.form.camp.scsp.xpath.codigo.error"/>
<%-- 						<c:set var="campPath" value="scspXpathCodigoError"/> --%>
<%-- 						<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 						<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.codigo.error"/></label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
				
				<div class="col-md-6">
				<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.literal.error"/></label>
				<pbl:inputText name="scspXpathCodigoError" inline="true" placeholderKey="servei.form.camp.scsp.xpath.codigo.error"/>
<%-- 					<c:set var="campPath" value="scspXpathLiteralError"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
<%-- 						<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.xpath.literal.error"/></label> --%>
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
				</div>
			</div>
		
			<div class="row">
				<div class="col-md-6">
<%-- 				<label class="control-label" for="${campPath}"><spring:message code="servei.form.camp.scsp.timeout"/></label> --%>
<%-- 				<pbl:inputText name="scspXpathCodigoError" inline="true" placeholderKey="servei.form.camp.scsp.xpath.codigo.error"/> --%>
<%-- 					<c:set var="campPath" value="scspTimeout"/> --%>
<%-- 					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set> --%>
<%-- 					<div class="form-group<c:if test="${not empty campErrors}"> error</c:if>"> --%>
						
<!-- 						<div class="controls"> -->
<%-- 							<form:input path="${campPath}" cssClass="col-md-12" id="${campPath}"/> --%>
<%-- 							<form:errors path="${campPath}" cssClass="help-block"/> --%>
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
			</div>
		</div>	
		</fieldset>
		<div class="container-fluid">
			<div class="row">
				<div class="pull-right">
					<button type="submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
					<a href="<c:url value="/servei"/>" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></a>
				</div>
			</div>
		</div>
	</form:form>

<div id="modal-redir-form" class="modal" tabindex="-1" role="dialog">
 	<div class="modal-dialog" role="document">
    	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="servei.form.modal.bus.titol"/></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" id="modal-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></a>
		</div>
		</div>
	</div>
</div>
	
	<div id="modal-xsd-form" class="modal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="servei.form.modal.xsd.titol"/></h3>
		</div>
		<div class="modal-body"></div>
		<div class="modal-footer">
			<a href="#" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tornar"/></a>
			<a href="#" id="modal-boto-submit-xsd" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></a>
		</div>
	</div>
	</div>
	</div>
</body>
</html>
