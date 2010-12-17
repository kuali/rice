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

<!-- begin of view render -->
<krad:html htmlFormAction="${KualiForm.formAction}"
                  headerTitle="${view.title}" additionalScriptFiles="${view.additionalScriptFiles}"
                  renderMultipart="true">

    <!----------------------------------- #VIEW HEADER --------------------------------------->
    <tiles:insertTemplate template="${view.header.template}">
          <tiles:putAttribute name="header" value="${view.header}" />
    </tiles:insertTemplate>

    <!----------------------------------- #VIEW NAVIGATION --------------------------------------->
    <tiles:insertTemplate template="${view.navigation.template}">
          <tiles:putAttribute name="navigation" value="${view.navigation}" />
          <tiles:putAttribute name="currentPageId" value="${view.currentPageId}" />
    </tiles:insertTemplate>    

    <%-- begin of page render --%>
    <tiles:insertTemplate template="${view.currentPage.template}">
          <tiles:putAttribute name="page" value="${view.currentPage}" />
    </tiles:insertTemplate>    
    <%-- end of page render --%>
    
    <%-- write out hiddens needed to maintain state --%>
    <tiles:insertTemplate template="${view.state.template}">
    
    <!----------------------------------- #VIEW FOOTER --------------------------------------->
    <tiles:insertTemplate template="${view.footer.template}">
          <tiles:putAttribute name="footer" value="${view.footer}" />
    </tiles:insertTemplate>

</krad:html>
<!-- end of view render -->