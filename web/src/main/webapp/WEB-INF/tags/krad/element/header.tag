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

<%@ attribute name="text" required="true" description="Text to display in header." %>
<%@ attribute name="level" required="false" description="The header level (1,2,3,4)." %>
<%@ attribute name="cellSpan" required="false" description="Span for generated cell." %>

<c:if test="${empty level}">
    <c:set var="level" value="1" />
</c:if>

<c:if test="${empty cellSpan}">
    <c:set var="cellSpan" value="1" />
</c:if>

<c:choose>
  <c:when test="${level==1}">
    <c:set var="headerClass" value="tab-subhead"/>
  </c:when>
  <c:when test="${level==2}">
    <c:set var="headerClass" value="tab-subhead"/>
  </c:when>
  <c:when test="${level==3}">
    <c:set var="headerClass" value="tab-subhead"/>
  </c:when>
  <c:otherwise>
    <c:set var="headerClass" value=""/>
  </c:otherwise>
</c:choose>

<td colspan="${cellSpan}" class="headerClass">
    <c:out value="${text}"/>&nbsp;
</td>