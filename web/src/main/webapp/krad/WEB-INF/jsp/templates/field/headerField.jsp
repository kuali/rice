<%--

    Copyright 2005-2011 The Kuali Foundation

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

<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.HeaderField"/>

<c:if test="${!empty field.headerStyleClasses}">
  <c:set var="styleClass" value="class=\"${field.headerStyleClasses}\""/>
</c:if>

<c:if test="${!empty field.headerStyle}">
  <c:set var="style" value="style=\"${field.headerStyle}\""/>
</c:if>

<c:set var="headerOpenTag" value="<${field.headerLevel} ${style} ${styleClass}>"/>
<c:set var="headerCloseTag" value="</${field.headerLevel}>"/>

<c:if test="${!empty field.headerDivStyleClasses}">
  <c:set var="divStyleClass" value="class=\"${field.headerDivStyleClasses}\""/>
</c:if>

<c:if test="${!empty field.headerDivStyle}">
  <c:set var="divStyle" value="style=\"${field.headerDivStyle}\""/>
</c:if>

<krad:div component="${field}">
  <c:if test="${!empty field.headerText}">
    <div id="${field.id}_header" ${divStyleClass} ${divStyle}>
        ${headerOpenTag}${field.headerText}${headerCloseTag}
    </div>
  </c:if>
  
  <%-- render header group --%>
  <c:if test="${!empty field.group}">
    <krad:template component="${field.group}"/>
  </c:if>
</krad:div>