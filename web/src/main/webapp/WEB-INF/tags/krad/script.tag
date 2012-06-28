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

<%@ attribute name="value" required="true" 
              description="The script to run - DO NOT include the script tags"%>
<%@ attribute name="component" required="false"
              description="The UIF component for which the script is for"
              type="org.kuali.rice.krad.uif.component.Component"%>
<%@ attribute name="role" required="false"
              description="The role of this script to identify scipts with particular uses"%>

<c:if test="${!empty component && !empty component.id}">
  <c:set var="dataFor" value="data-for=\"${component.id}\""/>
</c:if>

<c:set var="roleString" value="script"/>
<c:if test="${!empty role}">
  <c:set var="roleString" value="${role}"/>
</c:if>
<c:if test="${!empty value}">
  <input type="hidden" data-role="${roleString}" ${dataFor} name="script"  value="${fn:escapeXml(value)}"/>
</c:if>