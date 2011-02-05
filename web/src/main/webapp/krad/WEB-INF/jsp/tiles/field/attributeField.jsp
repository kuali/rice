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

<tiles:useAttribute name="field" classname="org.kuali.rice.kns.uif.field.AttributeField"/>

<%-- check to see if label has been rendered in another field (grid layout) and should be shown --%>
<c:set var="renderLabel" value="${!field.labelFieldRendered && field.showLabel}"/>

<krad:span field="${field}" idSuffix="attribute">

  <%-- render field label left --%>
  <c:if test="${renderLabel && (field.labelPlacement eq 'LEFT')}">
    <krad:template component="${field.labelField}"/>
  </c:if>

  <%-- render field summary --%>

  <%-- render field control --%>
  <c:if test="${field.readOnly}">
  	<s:bind path="${field.bindingInfo.bindingPath}">${status.value}</s:bind>
  </c:if>
  <c:if test="${!field.readOnly}">
    <krad:template component="${field.control}" field="${field}"/>
  </c:if>
  
  <%-- render field label right --%>
  <c:if test="${renderLabel && (field.labelPlacement eq 'RIGHT')}">
    <krad:template component="${field.labelField}"/>
  </c:if>

  <%-- render field constraint --%>

  <%-- render field quickfinder --%>

  <%-- render field help --%>
  
</krad:span>  
