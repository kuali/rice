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
	type="org.kuali.rice.krad.uif.component.Component"%>
<%@ attribute name="body" required="false"
	description="If the template takes a body (wraps content) that content should be passed with this parameter" %>
<%@ attribute name="componentUpdate" required="false"
	description="if this is true, then don't render the progressive/refresh scripts at this level - they have already been run" %>

<c:if test="${empty body}">
  <c:set var="body" value=""/>
</c:if>

<c:if test="${empty componentUpdate}">
  <c:set var="componentUpdate" value="false"/>
</c:if>

<%-- verify the component is not null and should be rendered --%>

<%-- check to see if the component should render, if this has progressiveDisclosure and not getting disclosed via ajax
still render, but render in a hidden container --%>
<c:if test="${!empty component && (component.render || (!component.render && !component.progressiveRenderViaAJAX
  && !component.progressiveRenderAndRefresh && !empty component.progressiveRender))}">

	<c:choose>
		<%-- for self rendered components, write out render output --%>
		<c:when test="${component.selfRendered}">
	        ${component.renderedHtmlOutput}
	  </c:when>

		<%-- render component through template --%>
		<c:otherwise>
			<tiles:insertTemplate template="${component.template}">
				<tiles:putAttribute name="${component.componentTypeName}" value="${component}" />
        <tiles:putAttribute name="body"	value="${body}" />
				<c:forEach items="${templateParameters}" var="parameter">
					<tiles:putAttribute name="${parameter.key}"	value="${parameter.value}" />
				</c:forEach>
			</tiles:insertTemplate>
		</c:otherwise>
	</c:choose>

	<%-- write data attributes --%>
  <krad:script component="${component}" role="dataScript" value="${component.complexDataAttributesJs}"/>
  <%-- generate event code for component --%>
	<krad:eventScript component="${component}" />

</c:if>

<c:if test="${!componentUpdate}">
  <c:if test="${(!empty component) && (!empty component.progressiveRender)}">
    <%-- For progressive rendering requiring an ajax call, put in place holder div --%>
    <c:if test="${!component.render && (component.progressiveRenderViaAJAX || component.progressiveRenderAndRefresh)}">
      <span id="${component.id}" data-role="placeholder" class="uif-placeholder"></span>
    </c:if>

    <%-- setup progressive handlers for each control which may satisfy a disclosure condition --%>
    <c:forEach items="${component.progressiveDisclosureControlNames}" var="cName">
      <krad:script
        value="var condition = function(){return (${component.progressiveDisclosureConditionJs});};
        setupProgressiveCheck(&quot;${cName}&quot;, '${component.id}', '${component.baseId}', condition, ${component.progressiveRenderAndRefresh}, '${component.methodToCallOnRefresh}');" />
    </c:forEach>
    <krad:script value="hiddenInputValidationToggle('${component.id}');" />
  </c:if>

  <%-- Conditional Refresh setup --%>
  <c:if test="${!empty component.conditionalRefresh}">
    <c:forEach items="${component.conditionalRefreshControlNames}" var="cName">
      <krad:script
        value="var condition = function(){return (${component.conditionalRefreshConditionJs});};
      setupRefreshCheck(&quot;${cName}&quot;, '${component.id}', '${component.baseId}', condition, '${component.methodToCallOnRefresh}');" />
    </c:forEach>
  </c:if>

  <%-- Refresh when changed setup --%>
  <c:forEach items="${component.refreshWhenChangedPropertyNames}" var="cName">
    <krad:script value="setupOnChangeRefresh(&quot;${cName}&quot;, '${component.id}', '${component.baseId}', '${component.methodToCallOnRefresh}');"/>
  </c:forEach>

  <%-- generate tooltip for component --%>
  <krad:tooltip component="${component}" />

</c:if>