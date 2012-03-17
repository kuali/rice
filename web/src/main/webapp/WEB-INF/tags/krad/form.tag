<%--
  ~ Copyright 2006-2012 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="render" required="false" description="Whether to render the wrapping form tag or not"%>
<%@ attribute name="postUrl" required="false" description="Url to post form to"%>
<%@ attribute name="onSubmitScript" required="false" description="script to be run when onSubmit event occurs"%>

<c:if test="${empty postUrl}">
  <c:set var="render" value="false"/>
</c:if>

<c:if test="${empty render}">
  <c:set var="render" value="true"/>
</c:if>

<c:if test="${render}">
  <form:form id="kualiForm" action="${postUrl}" method="post" enctype="multipart/form-data" modelAttribute="KualiForm"
    onsubmit="${onSubmitScript}" cssStyle="uif-form">
    <a name="topOfForm"></a>
      <jsp:doBody/>
    <span id="formComplete"></span>
  </form:form>
</c:if>

<c:if test="${!render}">
  <jsp:doBody/>
</c:if>
