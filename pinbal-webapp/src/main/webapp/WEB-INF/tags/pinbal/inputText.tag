<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="comment" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="tooltip" required="false" rtexprvalue="true"%>
<%@ attribute name="tooltipMsg" required="false" rtexprvalue="true"%>
<%@ attribute name="button" required="false" rtexprvalue="true"%>
<%@ attribute name="buttonMsg" required="false" rtexprvalue="true"%>
<%@ attribute name="icon" required="false" rtexprvalue="true"%>
<%@ attribute name="readonly" required="false" rtexprvalue="true"%>

<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<c:set var="myReadonly">
	<c:choose>
		<c:when test="${empty readonly}">false</c:when>
		<c:otherwise>${readonly}</c:otherwise>
	</c:choose>
</c:set>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
<c:choose>
	<c:when test="${not inline}">
		<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
		<div class="col-xs-${campInputSize}">
			<c:choose>
				<c:when test="${tooltip && not button}">
					<c:set var="tooltipMsg"><spring:message code="${tooltipMsg}"/></c:set>
					<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" data-toggle="tooltip" data-placement="bottom" title="${tooltipMsg}" readonly="${myReadonly}"/>
				</c:when>
				<c:when test="${button && not tooltip}">
					<c:set var="buttonMsg"><spring:message code="${buttonMsg}"/></c:set>
					<div class="input-group mb-3">
					<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" data-toggle="tooltip" data-placement="bottom" title="${tooltipMsg}" readonly="${myReadonly}"/>
					<span class="input-group-addon ${campPath}_btn" title="${buttonMsg}"><i class="${icon}"></i></span>
					</div>
				</c:when>
				<c:when test="${button && tooltip}">
					<c:set var="buttonMsg"><spring:message code="${buttonMsg}"/></c:set>
					<div class="input-group mb-3">
						<c:set var="tooltipMsg"><spring:message code="${tooltipMsg}"/></c:set>
						<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" data-toggle="tooltip" data-placement="bottom" title="${tooltipMsg}" readonly="${myReadonly}"/>
						<span class="input-group-addon ${campPath}_btn" title="${buttonMsg}"><i class="${icon}"></i></span>
					</div>
				</c:when>
				<c:otherwise>
					<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}"  readonly="${myReadonly}"/>
				</c:otherwise>
			</c:choose>
			
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			<c:if test="${not empty comment}"><p class="comentari col-xs-${12 - labelSize} col-xs-offset-${labelSize}"><spring:message code="${comment}"/></p></c:if>
		</div>
	</c:when>
	<c:otherwise>
		<label class="sr-only" for="${campPath}">${campLabelText}</label>
   		<form:input path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}" readonly="${myReadonly}"/>
		<c:if test="${button}">
			<button class="btn btn-outline-secondary" type="button">Button</button>
		</c:if>
		<c:if test="${not empty comment}"><p class="comentari col-xs-${12 - labelSize} col-xs-offset-${labelSize}"><spring:message code="${comment}"/></p></c:if>
	</c:otherwise>
</c:choose>
</div>