<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
	<title>&nbsp;</title>
	<script src="<c:url value="/js/vkbeautify.js"/>"></script>
</head>
<body>

	<c:choose>
		<c:when test="${mostrarPeticio}">
			<textarea id="missatgeXml" rows="16" class="input-lg form-control" style="font-size: 15px;">${consulta.peticioXml}</textarea>
		</c:when>
		<c:when test="${mostrarResposta}">
			<textarea id="missatgeXml" rows="16" class="input-lg form-control" style="font-size: 15px;">${consulta.respostaXml}</textarea>
		</c:when>
	</c:choose>
	<script type="text/javascript">
	$('#missatgeXml').val(vkbeautify.xml($('#missatgeXml').val()));
	</script>

</body>
</html>
