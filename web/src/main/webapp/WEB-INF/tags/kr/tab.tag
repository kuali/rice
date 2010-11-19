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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="tabTitle" required="false" description="The label to render for the tab." %>
<%@ attribute name="spanForLongTabTitle" required="false" description="If true, sets the CSS class for the title such that it will display over multiple columns" %>
<%@ attribute name="tabDescription" required="false" description="An explanatory description which will be rendered on the tab." %>
<%@ attribute name="defaultOpen" required="true" description="Whether the tab should default to rendering as open." %>
<%@ attribute name="tabErrorKey" required="false" description="The property key this tab should display errors associated with." %>
<%@ attribute name="innerTabErrorKey" required="false" description="The error path for errors whose message should not be displayed in this tab.  Errors will cause the tab to be opened. Path can be wildcarded with and asterisk.  Multiple paths must be separated with a comma and no white spaces." %>
<%@ attribute name="auditCluster" required="false" description="The error audit cluster associated with this page." %>
<%@ attribute name="tabAuditKey" required="false" description="The property key this tab should display audit errors associated with." %>
<%@ attribute name="tabItemCount" required="false" description="Expands the title to display this count alongside." %>
<%@ attribute name="helpUrl" required="false" description="Will display as a standard help link/image in the tab." %>
<%@ attribute name="leftSideHtmlProperty" required="false" description="The property name of an attribute to display at the left side of the tab. Used with leftSideHtmlAttribute." %>
<%@ attribute name="leftSideHtmlAttribute" required="false" type="java.util.Map" description="The data dictionary entry for an attribute to display at the left side of the tab.  Used with leftSideHtmlProperty." %>
<%@ attribute name="leftSideHtmlDisabled" required="false" description="If leftSideHtmlProperty and leftSideHtmlAttribute have been utilized, whether to display the left hand attribute as disabled." %>
<%@ attribute name="rightSideHtmlProperty" required="false" description="The property name of an attribute to display at the right side of the tab. Used with rightSideHtmlAttribute." %>
<%@ attribute name="rightSideHtmlAttribute" required="false" type="java.util.Map" description="The data dictionary entry for an attribute to display at the right side of the tab.  Used with rightSideHtmlProperty." %>
<%@ attribute name="transparentBackground" required="false" description="Whether the tab should render as having the background transparent around the corners of the tab." %>
<%@ attribute name="highlightTab" required="false" description="Whether the tab should be highlighted with the orange asterisk icon." %>
<%@ attribute name="extraButtonSource" required="false" description="The image source for an extra button to display on the tab." %>
<%@ attribute name="useCurrentTabIndexAsKey" required="false" description="Whether to use the current tab index as the current tab key, or (if this is false) generate a new one." %>
<%@ attribute name="hidden" required="false" description="Renders the tab as closed." %>
<%@ attribute name="useRiceAuditMode" required="false" description="If present and tabAuditKey is not present, renders all the audit errors in the audit cluster." %>
<%@ attribute name="midTabClassReplacement" required="false" description="Text to use as a replacement for the show/hide buttons rendering." %>
<%@ attribute name="boClassName" required="false" description="If present, makes the tab title an inquiry link using the business object class declared here.  Used with the keyValues attribute." %>
<%@ attribute name="keyValues" required="false" description="If present, makes the tab title an inquiry link using the primary key values declared here.  Used with the boClassName attribute." %>
<%@ attribute name="alwaysOpen" required="false" description="allows a tab to always stay open" %>

<%@ variable name-given="tabKey" scope="NESTED" description="forces the tabKey variable to nested scope" %>

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request"/>
<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request"/>

<c:choose>
    <c:when test="${(useCurrentTabIndexAsKey)}">
        <c:set var="tabKey" value="${currentTabIndex}"/>
    </c:when>
    <c:otherwise>
        <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
    </c:otherwise>
</c:choose>

<!--  hit form method to increment tab index -->
<c:set var="doINeedThis" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
    </c:when>
</c:choose>

<!-- if the section has errors, override and set isOpen to true -->
<c:if test="${!empty tabErrorKey or not empty tabAuditKey}">
  <kul:checkErrors keyMatch="${tabErrorKey}" auditMatch="${tabAuditKey}"/>
  <c:set var="isOpen" value="${hasErrors ? true : isOpen}"/>
</c:if>

<c:if test="${isOpen != 'true' and !empty innerTabErrorKey}">
    <kul:checkErrors keyMatch="${innerTabErrorKey}" />
    <c:set var="isOpen" value="${hasErrors ? true : isOpen}" />
</c:if>

<c:if test="${hidden}">
	<c:set var="isOpen" value="false"/>
</c:if>

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />
<!-- TAB -->

<c:if test="${! empty tabItemCount}">
  <c:set var="tabTitle" value="${tabTitle} (${tabItemCount})" />
</c:if>

<c:set var="tabTitleSpan" value="1" />
<c:if test="${! empty spanForLongTabTitle && spanForLongTabTitle eq true}">
	<c:set var="tabTitleSpan" value="${tabTitleSpan + 1}" />
</c:if>

  <div class="tab-new">
      <h2><a href="#" id="show_tab-${tabKey}-div"><img src="images/arrow-col.png" width="16" height="16" alt="collapse">${tabTitle}</a></h2>
  </div>

  <div class="tab-container" id="tab-${tabKey}-div">

        <!-- display errors for this tab -->
        <c:if test="${!(empty tabErrorKey) && hasErrors}">
          <div class="ui-state-error"><kul:errors keyMatch="${tabErrorKey}"/></div><br/>
        </c:if>

        <!-- comment for reference by KRA devs during KNS extraction -->
        <c:if test="${! (empty tabAuditKey) && (useRiceAuditMode == 'true')}">
        	<div class="tab-container-error"><div class="left-errmsg-tab">
				<c:forEach items="${fn:split(auditCluster,',')}" var="cluster">
        	   		<kul:auditErrors cluster="${cluster}" keyMatch="${tabAuditKey}" isLink="false" includesTitle="true"/>
				</c:forEach>
        	</div></div>
      	</c:if>

        <!-- Before the jsp:doBody of the kul:tab tag -->
        <jsp:doBody/>
        <!-- After the jsp:doBody of the kul:tab tag -->

  </div>
  
  <script type="text/javascript">
    $(document).ready(function() {
       <c:if test="${isOpen}">
         $("#tab-${tabKey}-div").slideDown(000);
       </c:if>
       
       <c:if test="${!isOpen}">
         $("#tab-${tabKey}-div").slideUp(000);
       </c:if>
  
        $("#show_tab-${tabKey}-div").toggle(
          function() {
            $("#tab-${tabKey}-div").slideUp(600);
            $("#show_tab-${tabKey}-div").html("<img src='images/arrow-col.png' width='16' height='16' alt='collapse'>${tabTitle}");
          }, function() {
            $("#tab-${tabKey}-div").slideDown(600);
            $("#show_tab-${tabKey}-div").html("<img src='images/arrow-exp.png' width='16' height='16' alt='expand'>${tabTitle}");
          }
        );
    });
  </script>
