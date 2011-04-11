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

<tiles:useAttribute name="field" classname="org.kuali.rice.kns.uif.field.HeaderField"/>

<c:set var="headerOpenTag" value="<${field.headerLevel}>"/>
<c:set var="headerCloseTag" value="</${field.headerLevel}>"/>

<c:if test="${!empty field.headerStyleClasses}">
  <c:set var="styleClass" value="class=\"${field.headerStyleClasses}\""/>
</c:if>

<c:if test="${!empty field.headerStyle}">
  <c:set var="style" value="style=\"${field.headerStyle}\""/>
</c:if>

<krad:div component="${field}">
  <div id="${field.id}_header" ${style} ${styleClass}>
     ${headerOpenTag}${field.headerText}${headerCloseTag}
  </div>
  
  <%-- render header group --%>
  <c:if test="${!empty field.group}">
    <krad:template component="${field.group}"/>
  </c:if>
</krad:div>