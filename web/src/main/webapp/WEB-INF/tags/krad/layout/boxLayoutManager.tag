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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="components" required="true" description="The list of components to render" %>
<%@ attribute name="orientation" required="false" description="How the items should be laid out, either horizonal or vertical (defaults to horizontal)" %>
<%@ attribute name="seperationPadding" required="false" description="The number of blanks or spaces between components (defaults to 1)" %>\

<%@ attribute name="layoutManager" required="false" description="A layout manager instance that contains the configuration" %>

<% -- begin vars --%>
<c:if test="${empty orientation && !empty layoutManager}">
  <c:set var="orientation" value="${layoutManager.orientation}"/>
</c:if>

<c:if test="${empty orientation}">
  <c:set var="orientation" value="horizontal"/>
</c:if>

<c:set var="orientation" value="${fn:toLowerCase(orientation)}"/>

<c:if test="${empty seperationPadding && !empty layoutManager}">
  <c:set var="seperationPadding" value="${layoutManager.seperationPadding}"/>
</c:if>

<c:if test="${empty seperationPadding}">
  <c:set var="seperationPadding" value="1"/>
</c:if>

<c:set var="padding" value="&nbsp;"/>
<c:if test="${orientation eq 'vertical' )">
  <c:set var="padding" value="<br/>"/>
</c:if>
<% -- end vars --%>

<c:forEach items="${components}" var="component" varStatus="listVarStatus">
  <jsp:include page="${component.handler}">
     <jsp:param name="component" value="${component}"/>
  </jsp:include>
  
  <c:if test="${!listVarStatus.last}">
    <c:forEach var="i" begin="1" end="${seperationPadding}" step="1">
      ${padding}
    </c:forEach>  
  </c:if>
</c:forEach>

