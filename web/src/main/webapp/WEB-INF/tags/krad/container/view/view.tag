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

<%@ attribute name="view" required="true" description="The view to be rendered" %>

<!-- begin of view render -->
<view:html htmlFormAction="${KualiForm.formAction}"
                  headerTitle="${view.title}" additionalScriptFiles="${view.additionalScriptFiles}"
                  renderMultipart="true">

    <!----------------------------------- #VIEW HEADER --------------------------------------->
    <jsp:include page="${view.header.handler}">
         <jsp:param name="header" value="${view.header}"/>
    </jsp:include>

    <!----------------------------------- #VIEW NAVIGATION --------------------------------------->
    <jsp:include page="${view.navigation.handler}">
        <jsp:param name="navigation" value="${view.navigation}"/>
      <jsp:param name="currentPageId" value="${view.currentPageId}"/>
    </jsp:include>

    <%-- begin of page render --%>
    <jsp:include page="${view.currentPage.handler}">
         <jsp:param name="page" value="${view.currentPage}"/>
    </jsp:include>
    <%-- end of page render --%>
    
    <%-- write out hiddens needed to maintain state --%>
    <jsp:include page="${view.stateHandler}"/>

</view:html>
<!-- end of view render -->