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

<tiles:useAttribute name="group" classname="org.kuali.rice.kns.ui.container.Group"/>

<div id="${group.id}" class="${group.styleClass}">

  <c:if test="${group.renderHeader}">
    <!----------------------------------- #GROUP HEADER --------------------------------------->
    <tiles:insertTemplate template="${group.header.template}">
          <tiles:putAttribute name="headerGroup" value="${group.header}"/>
    </tiles:insertTemplate>
  </c:if>

  <%-- render items through layout manager --%>
  <tiles:insertTemplate template="${group.layoutManager.template}">
        <tiles:putAttribute name="items" value="${group.items}"/>
        <tiles:putAttribute name="manager" value="${group.layoutManager}"/>
  </tiles:insertTemplate>
  
</div>