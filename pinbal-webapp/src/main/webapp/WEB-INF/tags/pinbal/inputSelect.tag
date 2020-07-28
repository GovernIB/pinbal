<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="optionItems" required="false" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="optionValueAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextKeyAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionNivellAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionEnum" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOption" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOptionText" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOptionTextKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="netejar" required="false" rtexprvalue="true"%>
<%@ attribute name="optionMinimumResultsForSearch" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="botons" required="false" rtexprvalue="true"%>
<%@ attribute name="buttonMsg" required="false" rtexprvalue="true"%>
<%@ attribute name="deleteMsg" required="false" rtexprvalue="true"%>
<%@ attribute name="icon" required="false" rtexprvalue="true"%>
<%@ attribute name="iconDelete" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campId" value="${campPath}"/><c:if test="${not empty id}"><c:set var="campId" value="${id}"/></c:if>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="minimumResultsForSearch"><c:choose><c:when test="${not empty optionMinimumResultsForSearch}">${optionMinimumResultsForSearch}</c:when><c:otherwise>${-1}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<spring:bind path="${name}">
	<c:set var="campValue" value="${status.value}"/>
</spring:bind>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
<c:choose>
	<c:when test="${not inline && not botons}">
		<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
		<div class="controls col-xs-${campInputSize}">
			<form:select path="${campPath}" cssClass="form-control" id="${campId}" disabled="${disabled}" style="width:100%"   data-netejar="${netejar}" data-placeholder="${campPlaceholder}" data-minimumresults="${minimumResultsForSearch}" data-enum="${optionEnum}" data-enum-value="${campValue}" multiple="${multiple}">
				<c:if test="${emptyOption == 'true'}">
					<c:choose>
						<c:when test="${not empty emptyOptionTextKey}"><option value=""><spring:message code="${emptyOptionTextKey}"/></option></c:when>
						<c:when test="${not empty emptyOptionText}"><option value="">${emptyOptionText}</option></c:when>
						<c:otherwise><option value=""></option></c:otherwise>
					</c:choose>
				</c:if>
				<c:choose>
					<c:when test="${not empty optionItems}">
						<c:forEach var="opt" items="${optionItems}">
							<c:set var="nivellTxt"><c:if test="${not empty optionNivellAttribute}"><c:forEach begin="${0}" end="${(opt[optionNivellAttribute])}" varStatus="status"><c:if test="${status.index >= 1}">&nbsp;&nbsp;&nbsp;&nbsp;</c:if></c:forEach></c:if></c:set>
							<c:choose>
								<c:when test="${not empty optionValueAttribute}">
									<c:choose>
										<c:when test="${not empty optionTextAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}${opt[optionTextAttribute]}</form:option></c:when>
										<c:when test="${not empty optionTextKeyAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}<spring:message code="${opt[optionTextKeyAttribute]}"/></form:option></c:when>
										<c:otherwise><form:option value="${opt[optionValueAttribute]}"/></c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise><form:option value="${opt}"/></c:otherwise>
							</c:choose>
						</c:forEach>
					</c:when>
					<c:when test="${not empty optionEnum}"></c:when>
					<c:otherwise><form:options/></c:otherwise>
				</c:choose>
			</form:select>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
		</div>
	</c:when>
	<c:when test="${not inline && botons}">
		<c:set var="buttonMsg"><spring:message code="${buttonMsg}"/></c:set>
		<c:set var="deleteMsg"><spring:message code="${deleteMsg}"/></c:set>
		<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
		<div class="controls col-xs-${campInputSize} ">
		<div class="input-group select2-bootstrap-append">
			<form:select path="${campPath}" cssClass="form-control" id="${campId}" disabled="${disabled}" style="width:100%" data-toggle="select2" data-netejar="${netejar}" data-placeholder="${campPlaceholder}" data-minimumresults="${minimumResultsForSearch}" data-enum="${optionEnum}" data-enum-value="${campValue}" multiple="${multiple}">
				<c:if test="${emptyOption == 'true'}">
					<c:choose>
						<c:when test="${not empty emptyOptionTextKey}"><option value=""><spring:message code="${emptyOptionTextKey}"/></option></c:when>
						<c:when test="${not empty emptyOptionText}"><option value="">${emptyOptionText}</option></c:when>
						<c:otherwise><option value=""></option></c:otherwise>
					</c:choose>
				</c:if>
				<c:choose>
					<c:when test="${not empty optionItems}">
						<c:forEach var="opt" items="${optionItems}">
							<c:set var="nivellTxt"><c:if test="${not empty optionNivellAttribute}"><c:forEach begin="${0}" end="${(opt[optionNivellAttribute])}" varStatus="status"><c:if test="${status.index >= 1}">&nbsp;&nbsp;&nbsp;&nbsp;</c:if></c:forEach></c:if></c:set>
							<c:choose>
								<c:when test="${not empty optionValueAttribute}">
									<c:choose>
										<c:when test="${not empty optionTextAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}${opt[optionTextAttribute]}</form:option></c:when>
										<c:when test="${not empty optionTextKeyAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}<spring:message code="${opt[optionTextKeyAttribute]}"/></form:option></c:when>
										<c:otherwise><form:option value="${opt[optionValueAttribute]}"/></c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise><form:option value="${opt}"/></c:otherwise>
							</c:choose>
						</c:forEach>
					</c:when>
					<c:when test="${not empty optionEnum}"></c:when>
					<c:otherwise><form:options/></c:otherwise>
				</c:choose>
			</form:select>
			<span class="input-group-btn">
				<a class="btn btn-default ${campPath}_btn_edicio" title="${buttonMsg}"><i class="${icon}"></i></a>
				<c:if test="${not empty iconDelete}">
					<a class="btn btn-default ${campPath}_btn_esborrar" title="${deleteMsg}"><i class="${iconDelete}"></i></a>
				</c:if>
			</span>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
		</div>
		</div>
	</c:when>
	<c:otherwise>
   		<label class="sr-only" for="${campPath}">${campLabelText}</label>
		<form:select path="${campPath}" cssClass="form-control" id="${campId}" disabled="${disabled}" data-netejar="${netejar}" data-toggle="select2" data-placeholder="${campPlaceholder}" data-minimumresults="${minimumResultsForSearch}" data-enum="${optionEnum}" data-enum-value="${campValue}">
			<c:if test="${emptyOption == 'true'}">
				<c:choose>
					<c:when test="${not empty emptyOptionTextKey}"><option value=""><spring:message code="${emptyOptionTextKey}"/></option></c:when>
					<c:when test="${not empty emptyOptionText}"><option value="">${emptyOptionText}</option></c:when>
					<c:otherwise><option value=""></option></c:otherwise>
				</c:choose>
			</c:if>
			<c:choose>
				<c:when test="${not empty optionItems}">
					<c:forEach var="opt" items="${optionItems}">
						<c:set var="nivellTxt">
							<c:if test="${not empty optionNivellAttribute}">
							<c:forEach begin="${0}" end="${(opt[optionNivellAttribute])}" varStatus="status">
								<c:if test="${status.index >= 1}">&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
								</c:forEach>
							</c:if>
						</c:set>
						<c:choose>
							<c:when test="${not empty optionValueAttribute}">
								<c:choose>
									<c:when test="${not empty optionTextAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}${opt[optionTextAttribute]}</form:option></c:when>
									<c:when test="${not empty optionTextKeyAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}<spring:message code="${opt[optionTextKeyAttribute]}"/></form:option></c:when>
									<c:otherwise>
									<form:option value="${opt[optionValueAttribute]}"/></c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<form:option value="${opt}"/>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:when>
				<c:when test="${not empty optionEnum}"></c:when>
				<c:otherwise><form:options/></c:otherwise>
			</c:choose>
		</form:select>
	</c:otherwise>
</c:choose>
</div>