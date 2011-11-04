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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>

  <!----------------------------------- #BEGIN HEAD --------------------------------------->
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
      :: ${view.title}
    </title>

    <c:forEach items="${fn:split(ConfigProperties.css.files, ',')}"	var="cssFile">
      <c:if test="${fn:length(fn:trim(cssFile)) > 0}">
        <link href="${pageContext.request.contextPath}/${cssFile}" rel="stylesheet" type="text/css" />
      </c:if>
    </c:forEach>
    
    <c:forEach items="${view.additionalCssFiles}" var="cssFile" >
      <c:if test="${fn:startsWith(cssFile, '/')}">
        <c:set var="cssFile" value="${pageContext.request.contextPath}/${fn:substringAfter(cssFile,'/')}"/>
      </c:if>
      <link href="${cssFile}" rel="stylesheet" type="text/css" />
    </c:forEach>

    <c:forEach items="${fn:split(ConfigProperties.javascript.files, ',')}"	var="javascriptFile">
      <c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
        <script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/${javascriptFile}"></script>
      </c:if>
    </c:forEach>
    
    <c:forEach items="${view.additionalScriptFiles}" var="scriptFile" >
      <c:if test="${fn:startsWith(scriptFile, '/')}">
        <c:set var="scriptFile" value="${pageContext.request.contextPath}/${fn:substringAfter(scriptFile,'/')}"/>
      </c:if>
      <script language="JavaScript" type="text/javascript" src="${scriptFile}"></script>
    </c:forEach>

    <!-- preload script (server variables) -->
    <script type="text/javascript">
        ${view.preLoadScript}
    </script>
    
    <!-- custom script for the view -->
    <script type="text/javascript">
      jq(document).ready(function() {
        ${view.onLoadScript}
      })
    </script>    
  </head>

  <!----------------------------------- #BEGIN BODY --------------------------------------->

  <body>
    <div id="view_div">
     <krad:div component="${view}">

      <krad:backdoor/>

      <!----------------------------------- #BEGIN FORM --------------------------------------->
      <c:if test="${view.renderForm}">
        <c:set var="postUrl" value="${view.formPostUrl}"/>
        <c:if test="${empty postUrl}">
          <c:set var="postUrl" value="${KualiForm.formPostUrl}"/>
        </c:if>
        
        <form:form 
           id="kualiForm"
           action="${postUrl}"
           method="post"
           enctype="multipart/form-data"
           modelAttribute="KualiForm"
           onsubmit="${view.onSubmitScript}"
           cssStyle="form_format topLabel page">

           <a name="topOfForm"></a>
      
           <jsp:doBody/>

           <span id="formComplete"></span>
        </form:form>
        <!----------------------------------- End Form --------------------------------------->
      </c:if>  
   
      <c:if test="${!view.renderForm}"> 
         <jsp:doBody/>
      </c:if>  
    
     </krad:div>
    </div>
  </body>
</html>
