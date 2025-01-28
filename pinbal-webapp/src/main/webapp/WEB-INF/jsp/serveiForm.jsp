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
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<link href="<c:url value="/js/jHtmlArea/style/jHtmlArea.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/js/jHtmlArea/style/jHtmlArea.ColorPickerMenu.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/jHtmlArea/scripts/jHtmlArea-0.8.min.js"/>"></script>
	<script src="<c:url value="/js/jHtmlArea/scripts/jHtmlArea.ColorPickerMenu-0.8.min.js"/>"></script>
	<link href="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/css/jasny-bootstrap.min.css"/>" rel="stylesheet"> 
	<script src="<c:url value="/webjars/jasny-bootstrap/3.1.3/dist/js/jasny-bootstrap.min.js"/>"></script> 
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<style type="text/css">
	.vcenter {
	 	display: flex;
	    align-items: center;
	}
	.h1, .h2, .h3, .h4, .h5, .h6 {
	    margin: 0px;
	}
	.data-info {
		position: relative;
		top: 40px;
	}
	</style>
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
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" 
			   commandName="serveiCommand" enctype="multipart/form-data">
		<form:hidden path="creacio"/>
		<fieldset>
			<div class="row">
				<div class="col-md-12">
					<pbl:inputText name="codi" labelSize="1" textKey="servei.form.camp.codi" required="true"/>
					<pbl:inputText name="descripcio" labelSize="1" textKey="servei.form.camp.descripcio" required="true"/>
					<pbl:inputSelect name="scspEmisor" labelSize="1" 
									 required="true"
									 placeholderKey="servei.form.camp.scsp.emisor" 
									 textKey="servei.form.camp.scsp.emisor"
									 optionItems="${emisors}"
									 optionValueAttribute="cif"
									 optionTextAttribute="nom"
									 emptyOption="true"/>
				</div>
			</div>	
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.param.pinbal"/></legend>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputText name="pinbalCustodiaCodi" textKey="servei.form.camp.pinbal.custodia.codi"/>
					<pbl:inputText name="pinbalCondicioBusClass" textKey="servei.form.camp.pinbal.condicio.bus.class"/>
					<pbl:inputSelect name="pinbalJustificantTipus" textKey="servei.form.camp.pinbal.justificant.tipus" 
									 emptyOption="true"
									 emptyOptionTextKey="comu.opcio.sense.definir"
									 optionItems="${ justificantTipusLlista }"/>
					
					<div class="form-group">					
						<label class="control-label col-md-4"><spring:message code="servei.form.camp.pinbal.camps.dadesgen"/></label>
						<div class="col-md-8">
							<c:set var="campPath" value="pinbalActiuCampNom"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.nom"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampLlinatge1"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.llinatge1"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampLlinatge2"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.llinatge2"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampNomComplet"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.nomcomp"/>
							</label>
							<c:set var="campPath" value="pinbalActiuCampDocument"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.camps.dadesgen.document"/>
							</label>
						</div>
					</div>
					<c:set var="campPath" value="pinbalDocumentObligatori"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label col-md-4" for="${campPath}"><spring:message code="servei.form.camp.pinbal.document.obligatori"/></label>
						<div class="col-md-8">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
				</div>
				<div class="col-md-6">
					<pbl:inputText name="pinbalRoleName" textKey="servei.form.camp.pinbal.role.name"/>
					<pbl:inputSelect name="pinbalEntitatTipus" textKey="servei.form.camp.pinbal.entitat.tipus" 
									 emptyOption="true"
									 optionItems="${ entitatTipusLlista}" emptyOptionTextKey="comu.opcio.sense.definir"/>
					<pbl:inputText name="pinbalJustificantXpath" textKey="servei.form.camp.pinbal.justificant.xpath"/>
					<div class="form-group">
						<label class="control-label col-md-4"><spring:message code="servei.form.camp.pinbal.document.tipus"/></label>
						<div class="col-md-8">
							<c:set var="campPath" value="pinbalPermesDocumentTipusDni"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.DNI"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusNif"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.NIF"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusCif"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.CIF"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusNie"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.NIE"/>
							</label>
							<c:set var="campPath" value="pinbalPermesDocumentTipusPas"/>
							<label class="checkbox-inline" for="${campPath}">
								<form:checkbox path="${campPath}" id="${campPath}"/> <spring:message code="servei.form.camp.pinbal.document.tipus.Passaport"/>
							</label>
						</div>
					</div>
					<c:set var="campPath" value="pinbalComprovarDocument"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label col-md-4" for="${campPath}"><spring:message code="servei.form.camp.pinbal.comprovar.document"/></label>
						<div class="col-md-8">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
				</div>
			</div>
		
			<div class="row">
				<div class="col-md-12">
					<c:set var="campPath" value="pinbalUnitatDir3FromEntitat"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label col-md-1" for="${campPath}"><spring:message code="servei.form.camp.pinbal.dir3.from.entitat"/></label>
						<div class="col-md-11">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
					<pbl:inputText name="pinbalUnitatDir3" labelSize="1" textKey="servei.form.camp.pinbal.dir3"/>
					<pbl:inputText name="maxPeticionsMinut" labelSize="1" inputSize="1" textKey="servei.form.camp.pinbal.max.minut"/>
					<pbl:inputTextarea name="ajuda" labelSize="1" textKey="servei.form.camp.ajuda" />
					<c:set var="campPath" value="fitxerAjuda"/>				
					<pbl:inputFile name="${campPath}" labelSize="1" inline="true" textKey="servei.form.camp.fitxer.ajuda"/>
					<form:errors path="${campPath}" cssClass="help-block"/>

					<div class="row">
						<div class="col-md-6">
							<c:set var="campPath" value="pinbalIniDadesExpecifiques"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label col-md-4" for="${campPath}" title="<spring:message code="servei.form.camp.pinbal.ini.dades.especifiques.info"/>"><spring:message code="servei.form.camp.pinbal.ini.dades.especifiques"/></label>
								<div class="col-md-8">
									<form:checkbox path="${campPath}" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>
						<div class="col-md-6">
							<c:set var="campPath" value="pinbalAddDadesEspecifiques"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label col-md-4" for="${campPath}" title="<spring:message code="servei.form.camp.pinbal.add.dades.especifiques.buid.info"/>"><spring:message code="servei.form.camp.pinbal.add.dades.especifiques.buid"/></label>
								<div class="col-md-8">
									<form:checkbox path="${campPath}" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-6">
							<c:set var="campPath" value="useAutoClasse"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label col-md-4" for="${campPath}" title="<spring:message code="servei.form.camp.pinbal.use.auto.classe.info"/>"><spring:message code="servei.form.camp.pinbal.use.auto.classe"/></label>
								<div class="col-md-8">
									<form:checkbox path="${campPath}" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>

						<div class="col-md-6">
							<c:set var="campPath" value="enviarSolicitant"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
								<label class="control-label col-md-4" for="${campPath}" title="<spring:message code="servei.form.camp.pinbal.enviar.solicitant.info"/>"><spring:message code="servei.form.camp.pinbal.enviar.solicitant"/></label>
								<div class="col-md-8">
									<form:checkbox path="${campPath}" id="${campPath}"/>
									<form:errors path="${campPath}" cssClass="help-block"/>
								</div>
							</div>
						</div>
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
			<legend><spring:message code="servei.form.legend.config.bus"/></legend>
			<div class="row">
				<div class="col-md-12">
					<a class="btn btn-primary pull-right" href="<c:url value="/modal/servei/${serveiCommand.codi}/redir/new"/>" 
					   onclick="showModalRedir(this);return false"><i class="fas fa-plus"></i>&nbsp;<spring:message code="servei.form.boto.nova.redireccio"/></a>
				</div>
			</div>
			<c:if test="${not empty serveisBus}">
				<div class="row">
					<div class="col-md-12">
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
			</c:if>
		</fieldset>
		</c:if>
		<fieldset>
			<legend><spring:message code="servei.form.legend.validesa.servei"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputDate name="scspFechaAlta" labelSize="2" textKey="servei.form.camp.scsp.fecha.alta"/>
					<pbl:inputDate name="scspCaducidad" labelSize="2" textKey="servei.form.camp.scsp.caducidad" 
					               placeholderKey="servei.form.legend.ubicacio.servei"/>
				</div>
				<div class="col-md-6">
					<pbl:inputDate name="scspFechaBaja" labelSize="2" textKey="servei.form.camp.scsp.fecha.baja"/>
				</div>
			</div>
		</fieldset>
		
		<fieldset>
			<legend><spring:message code="servei.form.legend.ubicacio.servei"/></legend>
			<div class="row">
				<div class="col-md-12">
					<pbl:inputText name="scspUrlSincrona" labelSize="1" textKey="servei.form.camp.scsp.url.sincrona"/>
					<pbl:inputText name="scspUrlAsincrona" labelSize="1" textKey="servei.form.camp.scsp.url.asincrona"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputText name="scspActionSincrona" labelSize="2" textKey="servei.form.camp.scsp.action.sincrona"/>
					<pbl:inputText name="scspActionSolicitud" labelSize="2" textKey="servei.form.camp.scsp.action.solicitud"/>
					<pbl:inputText name="scspVersionEsquema" required="true" labelSize="2" textKey="servei.form.camp.scsp.version.esquema"/>
				</div>
				<div class="col-md-6">
					<pbl:inputText name="scspActionAsincrona" labelSize="2" textKey="servei.form.camp.scsp.action.asincrona"/>
					<c:set var="campPath" value="activaGestioXsd"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="form-group vcenter<c:if test="${not empty campErrors}"> error</c:if>">
						<label class="control-label col-md-2" for="${campPath}"><spring:message code="servei.form.camp.scsp.activa.gestio.xsd"/></label>
						<div class="col-md-10">
							<form:checkbox path="${campPath}" id="${campPath}"/>
							<form:errors path="${campPath}" cssClass="help-block"/>
						</div>
					</div>
					<pbl:inputText name="scspEsquemas" labelSize="2" textKey="servei.form.camp.scsp.esquemas"/>
				</div>
			</div>
		</fieldset>
		
		<c:if test="${not empty serveiCommand.codi}">
			<fieldset id="gestioXsdFieldSet" hidden="${ activaGestioXsd ? '' : 'true'}">
				<legend><spring:message code="servei.form.legend.gestio.xsd"/></legend>
				<div class="row">
					<div class="col-md-12">
						<a class="btn btn-primary pull-right" href="<c:url value="/modal/servei/${serveiCommand.codi}/xsd/new"/>" onclick="showModalXsd(this);return false"><i class="fas fa-plus"></i>&nbsp;<spring:message code="servei.list.boto.nou.arxiuXsd"/></a>

					</div>
				</div>
				<c:if test="${not empty serveiCommand.fitxersXsd}">
					<div class="row">
						<div class="col-md-12">
							<br/>
							<table id="arxiusXsd" class="table table-striped table-bordered" style="width: 100%">
							<thead>
								<tr>
									<th width="30%"><spring:message code="servei.xsd.camp.tipus" /></th>
									<th><spring:message code="servei.xsd.camp.arxiu" /></th>
									<th width="15%"><spring:message code="servei.xsd.camp.data.modificacio" /></th>
									<th width="1%"></th>
									<th width="1%"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${serveiCommand.fitxersXsd}" var="registre">
						   			<tr>
										<td>${ registre.tipus }</td>
										<td>${registre.nomArxiu}</td>
										<td>${registre.dataModificacioString}</td>
										<td>
											<a class="btn btn-default btn-sm" href="<c:url value="/servei/${serveiCommand.codi}/xsd/${registre.tipus}/download"/>">
												<i class="fas fa-download"></i>&nbsp;<spring:message code="comu.boto.baixar"/>
											</a>
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
						</div>
					</div>
				</c:if>
			</fieldset>
		</c:if>
		<fieldset>
			<legend><spring:message code="servei.form.legend.xifrat.seguretat"/></legend>
			<div class="clearfix legend-margin-bottom"></div>
			<div class="row">
				<div class="col-md-6">
					<c:set var="campPath" value="scspTipoSeguridad"/>
					<pbl:inputSelect name="${campPath}" textKey="servei.form.camp.scsp.tipo.seguridad" 
									 required="true"
									 emptyOption="true" labelSize="2" 
									 optionItems="${tipusSeguretat}" 
									 emptyOptionTextKey="comu.opcio.sense.definir"/>
				</div>
			</div>	
		    <div class="row">
				<div class="col-md-6">
					<pbl:inputText name="scspAlgoritmoCifrado" labelSize="2" textKey="servei.form.camp.scsp.algoritmo.cifrado"/>
					<pbl:inputSelect name="scspClaveFirma" textKey="servei.form.camp.scsp.clave.firma" 
									 optionValueAttribute="alies"
									 optionTextAttribute="nom"
									 required="true"
									 emptyOption="true" labelSize="2" 
									 optionItems="${clausPrivades}" 
									 emptyOptionTextKey="comu.opcio.sense.definir"/>
					<pbl:inputText name="scspXpathCifradoSincrono" labelSize="2" textKey="servei.form.camp.scsp.xpath.cifrado.sincrono"/>
				</div>
				<div class="col-md-6">
					<pbl:inputText name="scspValidacionFirma" labelSize="2" textKey="servei.form.camp.scsp.validacion.firma"/>
					<pbl:inputSelect name="scspClaveCifrado" textKey="servei.form.camp.scsp.clave.cifrado" 
									 optionValueAttribute="alies"
									 optionTextAttribute="nom"
									 required="true"
									 emptyOption="true" labelSize="2" 
									 optionItems="${clausPubliques}" 
									 emptyOptionTextKey="comu.opcio.sense.definir"/>
					<pbl:inputText name="scspXpathCifradoAsincrono" labelSize="2" textKey="servei.form.camp.scsp.xpath.cifrado.asincrono"/>
				</div>
			</div>
		</fieldset>
		<fieldset>
			<legend><spring:message code="servei.form.legend.config.enviaments"/></legend>
			<div class="row">
				<div class="col-md-6">
					<pbl:inputText name="scspPrefijoPeticion" labelSize="2" textKey="servei.form.camp.scsp.prefijo.peticion"/>
					<pbl:inputText name="scspNumeroMaximoReenvios" labelSize="2" textKey="servei.form.camp.scsp.num.max.reenvios"/>
					<pbl:inputText name="scspXpathCodigoError" labelSize="2" textKey="servei.form.camp.scsp.xpath.codigo.error"/>
					<pbl:inputText name="scspTimeout" labelSize="2" textKey="servei.form.camp.scsp.timeout"/>
				</div>
				<div class="col-md-6">
					<pbl:inputText name="scspPrefijoIdTransmision" labelSize="2" textKey="servei.form.camp.scsp.prefijo.id.transmision"/>
					<pbl:inputText name="scspMaxSolicitudesPeticion" labelSize="2" textKey="servei.form.camp.scsp.max.solicitudes.peticion"/>
					<pbl:inputText name="scspXpathLiteralError" labelSize="2" textKey="servei.form.camp.scsp.xpath.literal.error"/>
				</div>
			</div>
		</fieldset>
		<div class="data-info"><spring:message code="servei.form.camp.data.ultima.actualitzacio"/>: ${serveiCommand.dataDarreraActualitzacioString} </div>
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
			<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></button>
			<button id="modal-boto-submit" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
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
				<button class="btn btn-default" data-dismiss="modal"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.cancelar"/></button>
				<button id="modal-boto-submit-xsd" class="btn btn-primary"><spring:message code="comu.boto.guardar"/></button>
			</div>
		</div>
	</div>
</div>
</body>
</html>
