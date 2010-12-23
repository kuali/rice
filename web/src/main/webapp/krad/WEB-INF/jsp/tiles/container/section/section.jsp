<%--
 Copyright 2006-2007 The Kuali Foundation
 
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

<tiles:useAttribute name="section" classname="org.kuali.rice.kns.ui.container.Section"/>

<div class="${section.styleClass}" id="${section.id}">

  <c:if test="${section.renderHeader}">
    <!----------------------------------- #SECTION HEADER --------------------------------------->
    <tiles:insertTemplate template="${section.header.template}">
          <tiles:putAttribute name="headerGroup" value="${section.header}"/>
    </tiles:insertTemplate>
  </c:if>
  
  <%-- render groups --%>
  <c:forEach items="${section.items}" var="group" varStatus="groupVarStatus">
    <tiles:insertTemplate template="${group.template}">
          <tiles:putAttribute name="${group.componentTypeName}" value="${group}"/>
    </tiles:insertTemplate>
  </c:forEach>
  
</div>