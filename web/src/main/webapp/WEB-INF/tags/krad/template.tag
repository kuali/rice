<%--
 Copyright 2005-2007 The Kuali Foundation

 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.opensource.org/licenses/ecl2.php

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ tag dynamic-attributes="templateParameters"%>

<%@ attribute name="component" required="true"
	description="The UIF component for which the template will be generated"
	type="org.kuali.rice.kns.uif.core.Component"%>

<%-- verify the component is not null and should be rendered --%>

<%-- check to see if the component should render, if this has progressiveDisclosure and not getting disclosed via ajax
still render, but render in a hidden span --%>
<c:if
	test="${!empty component && (component.render || (!component.render && !component.progressiveRenderViaAJAX && !empty component.progressiveRender))}">

	<c:choose>
		<c:when
			test="${!component.render && !component.progressiveRenderViaAJAX && !empty component.progressiveRender}">
			<span style="display: none;" id="${component.id}_refreshWrapper" class="refreshWrapper">
		</c:when>
		<c:otherwise>
			<c:if
				test="${!empty component.progressiveRender || !empty component.conditionalRefresh || !empty component.refreshWhenChanged}">
				<span id="${component.id}_refreshWrapper" class="refreshWrapper">
			</c:if>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<%-- for self rendered components, write out render output --%>
		<c:when test="${component.selfRendered}">
	        ${component.renderOutput}
	     </c:when>

		<%-- render component through template --%>
		<c:otherwise>
			<tiles:insertTemplate template="${component.template}">
				<tiles:putAttribute name="${component.componentTypeName}"
					value="${component}" />
				<c:forEach items="${templateParameters}" var="parameter">
					<tiles:putAttribute name="${parameter.key}"
						value="${parameter.value}" />
				</c:forEach>
			</tiles:insertTemplate>
		</c:otherwise>
	</c:choose>

	<%-- Conditional Refresh setup --%>
	<c:if test="${!empty component.conditionalRefresh}">
		<c:forEach items="${component.conditionalRefreshControlNames}"
			var="cName">
			<krad:script
				value="
			var condition = function(){return (${component.conditionalRefreshConditionJs});};
			setupRefreshCheck('${cName}', '${component.id}', condition);" />
		</c:forEach>
	</c:if>
	
		<%-- Conditional Refresh setup --%>
	<c:if test="${!empty component.refreshWhenChanged}">
		<c:forEach items="${component.refreshWhenChangedControlNames}"
			var="cName">
			<krad:script
				value="
			setupOnChangeRefresh('${cName}', '${component.id}');" />
		</c:forEach>
	</c:if>


	<%-- generate event code for component --%>
	<krad:eventScript component="${component}" />
	
	<c:if
		test="${!empty component.progressiveRender || !empty component.conditionalRefresh || !empty component.refreshWhenChanged}">
		</span>
	</c:if>
</c:if>

<c:if
	test="${(!empty component) && (!empty component.progressiveRender)}">
	<%-- For progressive rendering requiring an ajax call, put in place holder span --%>
	<c:if test="${!component.render && (component.progressiveRenderViaAJAX || component.progressiveRenderAndRefresh)}">
		<span id="${component.id}_refreshWrapper" class="unrendered refreshWrapper"
			style="display: none;"></span>
	</c:if>

	<%-- setup progressive handlers for each control which may satisfy a disclosure condition --%>
	<c:forEach items="${component.progressiveDisclosureControlNames}"
		var="cName">
		<krad:script
			value="
			var condition = function(){return (${component.progressiveDisclosureConditionJs});};
			setupProgressiveCheck('${cName}', '${component.id}', condition, ${component.progressiveRenderAndRefresh});" />
	</c:forEach>
	<krad:script
			value="
			hiddenInputValidationToggle('${component.id}_refreshWrapper');" />

</c:if>

