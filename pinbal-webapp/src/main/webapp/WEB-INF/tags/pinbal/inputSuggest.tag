<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="urlConsultaLlistat" required="true" rtexprvalue="true"%>
<%@ attribute name="urlConsultaInicial" required="true" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="netejar" required="false" rtexprvalue="true"%>
<%@ attribute name="minimumInputLength" required="false" rtexprvalue="true"%>
<%@ attribute name="suggestValue" required="false" rtexprvalue="true"%>
<%@ attribute name="suggestText" required="false" rtexprvalue="true"%>
<%@ attribute name="suggestTextAddicional" required="false" rtexprvalue="true"%>
<%@ attribute name="urlParamAddicional" required="false" rtexprvalue="true"%>
<%@ attribute name="icon" required="false" rtexprvalue="true"%>

<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="minimumInputLength"><c:choose><c:when test="${not empty minimumInputLength}">${minimumInputLength}</c:when><c:otherwise>${3}</c:otherwise></c:choose></c:set>
<c:set var="suggestValue"><c:choose><c:when test="${not empty suggestValue}">${suggestValue}</c:when><c:otherwise>id</c:otherwise></c:choose></c:set>
<c:set var="suggestText"><c:choose><c:when test="${not empty suggestText}">${suggestText}</c:when><c:otherwise>text</c:otherwise></c:choose></c:set>

<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<spring:bind path="${name}">
	<c:set var="campValue" value="${status.value}"/>
</spring:bind>
<c:choose>
	<c:when test="${not empty placeholderKey}"><c:set var="placeholderText"><spring:message code="${placeholderKey}"/></c:set></c:when>
	<c:otherwise><c:set var="placeholderText" value="${placeholder}"/></c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${campLabelSize}" for="${campPath}">
				<c:choose>
					<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
					<c:when test="${not empty text}">${text}</c:when>
					<c:otherwise>${campPath}</c:otherwise>
				</c:choose>
				<c:if test="${required}">*</c:if>
			</label>
			<div class="controls col-xs-${campInputSize}">
			<c:choose>
				<c:when test="${not empty icon}">
					<div class="input-group select2-bootstrap-append">
						<form:select path="${campPath}" cssClass="form-control"
							id="${campPath}" disabled="${disabled}" style="width:100%"
							data-toggle="suggest" data-netejar="${netejar}"
							data-placeholder="${placeholderText}"
							data-minimum-input-length="${minimumInputLength}"
							data-url-llistat="${urlConsultaLlistat}"
							data-url-inicial="${urlConsultaInicial}"
							data-current-value="${campValue}"
							data-suggest-value="${suggestValue}"
							data-suggest-text="${suggestText}"
							data-suggest-text-addicional="${suggestTextAddicional}" 
							data-url-param-addicional="${urlParamAddicional}" 
							/>
							<a class="input-group-addon btn btn-default ${campPath}_btn" onclick="toggleCarrecs()"><i class="${icon}"></i></a>
					</div>
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
				</c:when>
				<c:otherwise>
					<form:select path="${campPath}" cssClass="form-control"
							id="${campPath}" disabled="${disabled}" style="width:100%"
							data-toggle="suggest" data-netejar="${netejar}"
							data-placeholder="${placeholderText}"
							data-minimum-input-length="${minimumInputLength}"
							data-url-llistat="${urlConsultaLlistat}"
							data-url-inicial="${urlConsultaInicial}"
							data-current-value="${campValue}"
							data-suggest-value="${suggestValue}"
							data-suggest-text="${suggestText}"
							data-suggest-text-addicional="${suggestTextAddicional}" 
							data-url-param-addicional="${urlParamAddicional}" 
							/>
						<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
				</c:otherwise>
			</c:choose>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<form:select path="${campPath}" cssClass="form-control" id="${campPath}"
			disabled="${disabled}" style="width:100%" data-toggle="suggest"
			data-netejar="${netejar}" data-placeholder="${placeholderText}"
			data-minimum-input-length="${minimumInputLength}"
			data-url-llistat="${urlConsultaLlistat}" 
			data-url-inicial="${urlConsultaInicial}"
			data-current-value="${campValue}" 
			data-suggest-value="${suggestValue}"
			data-suggest-text="${suggestText}"
			data-suggest-text-addicional="${suggestTextAddicional}" 
			data-url-param-addicional="${urlParamAddicional}"/>
	</c:otherwise>
</c:choose>

