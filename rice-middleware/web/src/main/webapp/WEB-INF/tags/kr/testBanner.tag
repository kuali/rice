<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%--
  ~ Copyright 2006-2014 The Kuali Foundation
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

<%-- Added a new tag which displays a banner when in the testing environments --%>
<c:if test="${!empty UserSession && UserSession.displayTestBanner }">
  <div class="testBanner">
    <img src="${pageContext.request.contextPath}/kr/static/images/alert.png" alt="Alert" />
    <c:choose>
      <c:when test="${fn:toUpperCase(UserSession.currentEnvironment) eq 'STG'}">
        <c:set var="envDisplay" value="Staging" />
      </c:when>
      <c:when test="${fn:toUpperCase(UserSession.currentEnvironment) eq 'DEV'}">
        <c:set var="envDisplay" value="Development" />
      </c:when>
      <c:otherwise>
        <c:set var="envDisplay" value="Test ${fn:toUpperCase(UserSession.currentEnvironment)}" />
      </c:otherwise>
    </c:choose>
    <bean:message key="test.banner.message" arg0="${envDisplay}" />
  </div>
</c:if>
