<%--

    Copyright 2005-2012 The Kuali Foundation

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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="view" classname="org.kuali.rice.krad.uif.view.View"/>

<krad:html view="${view}">

  <c:if test="${!view.dialogMode}">
    <krad:script value="
    jq(function(){
      publishHeight();
      window.onresize = publishHeight;
      window.setInterval(publishHeight, 500);
    });
"/>
  </c:if>

  <div id="view_div" style="display:none;" class="uif-application">

    <!----------------------------------- APPLICATION HEADER --------------------------------------->
    <krad:template component="${view.applicationHeader}"/>
    <krad:backdoor/>

    <c:set var="postUrl" value="${view.formPostUrl}"/>
    <c:if test="${empty postUrl}">
      <c:set var="postUrl" value="${KualiForm.formPostUrl}"/>
    </c:if>

    <krad:form render="${view.renderForm}" postUrl="${postUrl}" onSubmitScript="${view.onSubmitScript}">

      <c:if test="${view.renderForm}">
        <%-- write out view, page id as hidden so the view can be reconstructed if necessary --%>
        <form:hidden path="viewId"/>
        <%-- all forms will be stored in session, this is the conversation key --%>
        <form:hidden path="formKey"/>
        <%-- Based on its value, form elements will be checked for dirtyness --%>
        <form:hidden path="validateDirty"/>
      </c:if>

      <!----------------------------------- VIEW --------------------------------------->
      <krad:div component="${view}">

        <!----------------------------------- BREADCRUMBS --------------------------------------->
        <c:if test="${!view.breadcrumbsInApplicationHeader}">
          <krad:template component="${view.breadcrumbs}"/>
        </c:if>

        <!----------------------------------- VIEW HEADER --------------------------------------->
        <krad:template component="${view.header}"/>

        <!----------------------------------- VIEW CONTENT --------------------------------------->
        <div id="Uif-ViewContentWrapper" class="uif-viewContentWrapper">

          <!----------------------------------- VIEW NAVIGATION --------------------------------------->
          <div>
            <krad:template component="${view.navigation}" currentPageId="${view.currentPageId}"/>
          </div>

          <!----------------------------------- PAGE CONTENT --------------------------------------->
          <div id="viewpage_div" class="uif-pageContentWrapper">

            <!----------------------------------- PAGE --------------------------------------->
            <krad:template component="${view.currentPage}"/>

            <!----------------------------------- PAGE RELATED VARS --------------------------------------->
            <c:if test="${view.renderForm}">
              <form:hidden path="pageId"/>
              <c:if test="${!empty view.currentPage}">
                <form:hidden id="currentPageTitle" path="view.currentPage.title"/>
              </c:if>
              <form:hidden path="jumpToId"/>
              <form:hidden path="jumpToName"/>
              <form:hidden path="focusId"/>
              <form:hidden path="formHistory.historyParameterString"/>
            </c:if>

            <krad:script value="performJumpTo();"/>
            <c:if test="${view.currentPage.autoFocus}">
              <krad:script value="performFocus();"/>
            </c:if>
          </div>

        </div>

        <!----------------------------------- VIEW FOOTER --------------------------------------->
        <div id="viewfooter_div">
          <krad:template component="${view.footer}"/>
        </div>
      </krad:div>
    </krad:form>
  </div>

  <!----------------------------------- APPLICATION FOOTER --------------------------------------->
  <krad:template component="${view.applicationFooter}"/>

</krad:html>