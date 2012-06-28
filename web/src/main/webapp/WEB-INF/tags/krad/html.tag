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

<%@ attribute name="view" required="true" 
              description="The view instance the html page is being rendered for."
              type="org.kuali.rice.krad.uif.view.View"%>

<!DOCTYPE HTML>
<html lang="en">
  <%----------------------------------- HEAD ---------------------------------------%>
  <head>

    <c:if test="${not empty SESSION_TIMEOUT_WARNING_MILLISECONDS}">
      <script type="text/javascript">
        <!--
        setTimeout("alert('Your session will expire in ${SESSION_TIMEOUT_WARNING_MINUTES} minutes.')",'${SESSION_TIMEOUT_WARNING_MILLISECONDS}');
      // -->
      </script>
    </c:if>

    <krad:scriptingVariables/>

    <title>
      <s:message code="app.title"/>
      :: ${view.headerText}
    </title>

    <c:forEach items="${view.theme.cssFiles}" var="cssFile">
      <c:choose>
        <c:when test="${fn:startsWith(cssFile,'http')}">
          <link href="${cssFile}" rel="stylesheet" type="text/css"/>
        </c:when>
        <c:otherwise>
          <link href="${pageContext.request.contextPath}/${cssFile}" rel="stylesheet" type="text/css"/>
        </c:otherwise>
      </c:choose>
    </c:forEach>

    <c:forEach items="${view.additionalCssFiles}" var="cssFile">
      <c:if test="${fn:length(fn:trim(cssFile)) > 0}">
        <c:choose>
          <c:when test="${fn:startsWith(cssFile,'http')}">
            <link href="${cssFile}" rel="stylesheet" type="text/css"/>
          </c:when>
          <c:otherwise>
            <link href="${pageContext.request.contextPath}/${cssFile}" rel="stylesheet" type="text/css"/>
          </c:otherwise>
        </c:choose>
      </c:if>
    </c:forEach>

    <c:forEach items="${view.theme.scriptFiles}" var="javascriptFile">
      <c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
        <c:choose>
          <c:when test="${fn:startsWith(javascriptFile,'http')}">
            <script language="JavaScript" type="text/javascript" src="${javascriptFile}"></script>
          </c:when>
          <c:otherwise>
            <script language="JavaScript" type="text/javascript"
                    src="${pageContext.request.contextPath}/${javascriptFile}"></script>
          </c:otherwise>
        </c:choose>
      </c:if>
    </c:forEach>

    <c:forEach items="${view.additionalScriptFiles}" var="scriptFile">
      <c:if test="${fn:length(fn:trim(scriptFile)) > 0}">
        <c:choose>
          <c:when test="${fn:startsWith(scriptFile,'http')}">
            <script language="JavaScript" type="text/javascript" src="${scriptFile}"></script>
          </c:when>
          <c:otherwise>
            <script language="JavaScript" type="text/javascript"
                    src="${pageContext.request.contextPath}/${scriptFile}"></script>
          </c:otherwise>
        </c:choose>
      </c:if>
    </c:forEach>

    <!-- preload script (server variables) -->
    <script type="text/javascript">
        ${view.preLoadScript}
    </script>
    
    <!-- custom script for the view -->
    <script type="text/javascript">
      jQuery(document).ready(function() {
        ${view.onLoadScript}
      })
    </script>    
  </head>

  <%----------------------------------- BODY ---------------------------------------%>
  <body>
    <jsp:doBody/>
  </body>
</html>
