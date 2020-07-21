<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/pinbal" prefix="pbl" %>
<c:choose>
	<c:when test="${not reloadPage}">
		<script>
$(document).ready(function() {
	var fileTextInput = $('#nomArxiu');
		
	if (!$('#nomArxiu').val()) {
		$("#xsd_file").click(function() {
			if (!$('#nomArxiu').val())
				$('#contingut').trigger('click');
		});
	}else {
		$('#nomArxiu').attr('disabled', 'disabled');
	}

	$("#contingut").change(
		function() {
			var input = $(this),
			numFiles = input.get(0).files ? input.get(0).files.length : 1,
			label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
			input.trigger('fileselect', [numFiles, label]);
		}
	);
	$("#nomArxiu").click(
		function() {
			if (!$('#nomArxiu').val())
				$('#contingut').trigger('click');
		}	
	);
	$('label[for="contingut"]').click(
		function(e) { 
	    	e.preventDefault();
		}	
	);
	$("#xsd_clean").click(
		function() {
			// Nomes s'ha d'actualitzar l'input de tipus text!!!
			$('#nomArxiu').val('');
			$('#nomArxiu').removeAttr('disabled');
			$("#xsd_file").unbind("click");
			$("#xsd_file").bind("click", function() {
				if (!$('#nomArxiu').val())
					$('#contingut').trigger('click');
			});
			$('#xsd_file').removeAttr('disabled');
			$('#contingut').val('');
		}
	);
	$('#contingut').on('fileselect', function (event, numFiles, label) {
		if (label) {
			$('#xsd_clean').removeAttr('disabled');
			$('#xsd_file').attr('disabled', 'disabled');
			$('#nomArxiuControl').removeClass("error");
			$('#nomArxiuLabel').css("display", "none");
			// Nomes s'ha d'actualitzar l'input de tipus text!!!
			$('#nomArxiu').val(label);
			$('#nomArxiu').attr('disabled', 'disabled');
			$('#nomArxiu').change();
		}
    });
});
		</script>
		<form:form id="xsd-form" method="post" action="/servei/${serveiCodi}/xsd/save" commandName="serveiXsdCommand" role="form" enctype="multipart/form-data">	
			<c:set var="campPath" value="fitxerXsd"/>
			<form:input path="codi" type="hidden" value="${serveiCodi}"/>
			<div class="form-group">
				<div class="controls input-append" style="margin-left:20px;width:99%;">
					<div class="form-group" id="tipusControl">
						<pbl:inputSelectGestioXsd name="tipus" inline="true" textKey="servei.xsd.camp.tipus" labelSize="8" required="true" optionItems="${xsdTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
						<label id="tipusLabel" style="color: #b94a48;"></label>
					</div>
					<div class="form-group" id="nomArxiuControl" style="display: inline;">
						<label for="nomArxiu"><spring:message code="servei.form.camp.fitxer.xsd"/></label>
						<input type="text" name="nomArxiu" id="nomArxiu" class="form-control xsd_file" style="width:73%;"/>
						<span id="xsd_file" class="btn btn-default btn-file xsd_file"><i class='icon-file'></i></span>
						<span id="xsd_clean" class="btn btn-default btn-file-clean" style="border-radius: 0px 4px 4px 0px;"><i class='glyphicon-trash'></i></span>
						<input type="file" name="contingut" id="contingut" class="hide" />
						<label id="nomArxiuLabel" style="color: #b94a48; display: none;"></label>
					</div>
				</div>
			</div>
		</form:form>
	</c:when>
	<c:otherwise><script>location.reload();</script></c:otherwise>
</c:choose>

