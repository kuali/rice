<%@ tag import="org.kuali.rice.krad.util.KRADConstants" %>
<%@ tag import="java.util.Enumeration" %>
<%--
~ Copyright 2006-2011 The Kuali Foundation
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

<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<div class="header2">
  <div class="header2-left-focus">
    <div class="breadcrumb-focus">
    	<portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.kew.url}/ActionList.do'>
   		<img src="rice-portal/images/icon-port-actionlist.gif" alt="action list" width="91" height="19" border="0"></portal:portalLink>
    	<portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.documentsearch.base.url}'>
    	<img src="rice-portal/images/icon-port-docsearch.gif" alt="doc search" width="96" height="19" border="0"></portal:portalLink>
     </div>
  </div>
</div>
<div id="login-info">
  <c:set var="invalidUserMsg" value="Invalid username"/>
  <c:choose> <c:when test="${empty UserSession.loggedInUserPrincipalName}" > <strong>You are not logged in.</strong> </c:when><c:otherwise> <strong>Logged in User:&nbsp;${UserSession.loggedInUserPrincipalName}</strong> <c:if test="${UserSession.backdoorInUse}" > <strong>&nbsp;&nbsp;Impersonating User:&nbsp;${UserSession.principalName}</strong> </c:if>  <c:if test="${param.invalidUser}" >  <strong>&nbsp;&nbsp;Impersonating User:&nbsp;${invalidUserMsg}</strong></c:if></c:otherwise> </c:choose></div>

<div id="search">
  <c:choose> 
    <c:when test="${empty UserSession.loggedInUserPrincipalName}" >
    </c:when> 
    <c:when test="${fn:trim(ConfigProperties.environment) == fn:trim(ConfigProperties.production.environment.code)}" >
      <html:form action="/logout.do" method="post" style="margin:0; display:inline">
        <input name="imageField" type="submit" value="Logout" class="go" title="Click to logout.">
        <kul:csrf/>
      </html:form>
    </c:when>
    <c:otherwise> 
      <c:set var="backboorEnabled" value="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(org.kuali.rice.kew.api.KewApiConstants.KEW_NAMESPACE, org.kuali.rice.krad.util.KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, org.kuali.rice.kew.api.KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND)%>"/>
      <c:if test="${backboorEnabled}">
        <html:form action="/backdoorlogin.do" method="post" style="margin:0; display:inline">
          <input name="backdoorId" type="text" class="searchbox" size="10" title="Enter your backdoor ID here.">
          <input name="imageField" type="submit" value="Login" class="go" title="Click to login.">
          <input name="methodToCall" type="hidden" value="login" />
          <kul:csrf/>
        </html:form>
      </c:if>
      <html:form action="/backdoorlogin.do" method="post" style="margin:0; display:inline">
        <input name="imageField" type="submit" value="Logout" class="go" title="Click to logout.">
        <input name="methodToCall" type="hidden" value="logout" />
        <kul:csrf/>
      </html:form>
    </c:otherwise> 
  </c:choose>
  </div>
