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

<%@ attribute name="headerTitle" required="false" description="The title of this page which will be displayed in the browser's header bar." %>
<%@ attribute name="additionalScriptFiles" required="false" type="java.util.List" description="A List of JavaScript file names to have included on the page." %>
<%@ attribute name="htmlFormAction" required="false" description="The URL that the HTML form rendered on this page will be posted to." %>
<%@ attribute name="renderForm" required="false" description="Boolean value indicating whether a form tag should be rendered or not. (defaults to true)" %>
<%@ attribute name="renderMultipart" required="false" description="Boolean value of whether the HTML form rendred on this page will be encoded to accept multipart - ie, uploaded attachment - input." %>

<c:if test="${empty renderForm}">
  <c:set var="renderForm" value="true"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
      <!-- bean:message key="app.title" /--> <!--  find spring replacement -->
      :: ${headerTitle}
    </title>

    <c:forEach items="${fn:split(ConfigProperties.css.files, ',')}"	var="cssFile">
      <c:if test="${fn:length(fn:trim(cssFile)) > 0}">
        <link href="${pageContext.request.contextPath}/${cssFile}" rel="stylesheet" type="text/css" />
      </c:if>
    </c:forEach>

    <c:forEach items="${fn:split(ConfigProperties.javascript.files, ',')}"	var="javascriptFile">
      <c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
        <script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/${javascriptFile}"></script>
      </c:if>
    </c:forEach>
    
    <c:forEach items="${additionalScriptFiles}" var="scriptFile" >
        <script language="JavaScript" type="text/javascript" src="${scriptFile}"></script>
    </c:forEach>
  </head>

  <!----------------------------------- #BEGIN BODY --------------------------------------->

  <body>

  <krad:backdoor/>

  <c:set var="encoding" value=""/>
  <c:if test="${not empty renderMultipart and renderMultipart eq true}">
     <c:set var="encoding" value="multipart/form-data"/>
  </c:if>

  <!----------------------------------- #BEGIN FORM --------------------------------------->
  <c:if test="${renderForm}">
    <form:form 
       id="kualiForm"
       action="${htmlFormAction}"
       method="post"
       enctype="${encoding}"
       modelAttribute="KualiForm"
       onsubmit="return hasFormAlreadyBeenSubmitted();">

       <a name="topOfForm"></a>
      
       <jsp:doBody/>

      </form:form>
      <div id="formComplete"></div>
      <!----------------------------------- End Form --------------------------------------->
   </c:if>  
   
   <c:if test="${!renderForm}"> 
     <jsp:doBody/>
   </c:if>  
    
   </body>
</html>
