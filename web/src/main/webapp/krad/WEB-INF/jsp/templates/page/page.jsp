<%--

    Copyright 2005-2012 The Kuali Foundation

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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="pageGroup" classname="org.kuali.rice.krad.uif.container.PageGroup"/>


<!-- PAGE -->
<krad:group group="${pageGroup}">

  <%-- render items through layout manager --%>
  <tiles:insertTemplate template="${pageGroup.layoutManager.template}">
    <tiles:putAttribute name="items" value="${pageGroup.items}"/>
    <tiles:putAttribute name="manager" value="${pageGroup.layoutManager}"/>
    <tiles:putAttribute name="container" value="${pageGroup}"/>
  </tiles:insertTemplate>

</krad:group>

<!-- PAGE RELATED VARS -->
<c:if test="${KualiForm.view.renderForm}">
  <form:hidden path="pageId"/>

  <c:if test="${!empty pageGroup}">
    <form:hidden id="currentPageTitle" path="view.currentPage.header.headerText"/>
  </c:if>

  <form:hidden path="jumpToId"/>
  <form:hidden path="jumpToName"/>
  <form:hidden path="focusId"/>
  <form:hidden path="formHistory.historyParameterString"/>
</c:if>



