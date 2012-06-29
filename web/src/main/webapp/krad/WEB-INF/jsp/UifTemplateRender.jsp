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
<%@ page import="org.kuali.rice.krad.uif.UifParameters" %>
<%--
~ Copyright 2006-2012 The Kuali Foundation
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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<c:choose>
  <c:when test="${KualiForm.renderFullView}">

    <krad:html view="${KualiForm.view}">

      <c:if test="${!KualiForm.view.renderedInLightBox}">
        <krad:script value="
          jQuery(function(){
            publishHeight();
            window.onresize = publishHeight;
            window.setInterval(publishHeight, 249);
          });
      "/>
      </c:if>

      <%-- render growl script --%>
      <krad:script value="${KualiForm.growlScript}"/>
      <div id="Uif-Application" style="display:none;" class="uif-application">

        <!-- APPLICATION HEADER -->
        <krad:template component="${KualiForm.view.applicationHeader}"/>
        <krad:backdoor/>

        <c:set var="postUrl" value="${KualiForm.view.formPostUrl}"/>
        <c:if test="${empty postUrl}">
          <c:set var="postUrl" value="${KualiForm.formPostUrl}"/>
        </c:if>

        <krad:form render="${KualiForm.view.renderForm}"
                   postUrl="${postUrl}"
                   onSubmitScript="${KualiForm.view.onSubmitScript}">

          <c:if test="${KualiForm.view.renderForm}">
            <%-- write out view, page id as hidden so the view can be reconstructed if necessary --%>
            <form:hidden path="viewId"/>
            <%-- all forms will be stored in session, this is the conversation key --%>
            <form:hidden path="formKey"/>
            <%-- Based on its value, form elements will be checked for dirtyness --%>
            <form:hidden path="validateDirty"/>
          </c:if>

          <%-- render full view --%>
          <krad:template component="${KualiForm.view}"/>

        </krad:form>
        <krad:script value="${KualiForm.lightboxScript}"/>
      </div>

      <!-- APPLICATION FOOTER -->
      <krad:template component="${KualiForm.view.applicationFooter}"/>

    </krad:html>

  </c:when>
  <c:otherwise>

    <%-- render component only --%>
    <html>
      <%-- rerun view pre-load script to get new state variables for page --%>
    <krad:script value="${view.preLoadScript}"/>

    <%-- render growl script --%>
    <krad:script value="${KualiForm.growlScript}"/>

    <s:nestedPath path="KualiForm">
      <krad:template component="${KualiForm.view.breadcrumbs}"/>

      <c:if test="${!empty KualiForm.updateComponentId}">
        <%-- render errors field for page so they can be pulled and updated on the view --%>
        <krad:template component="${KualiForm.postedView.currentPage.validationMessages}"/>
      </c:if>

      <%-- now render the updated component (or page) wrapped in an update div --%>
      <div id="${Component.id}_update" data-handler="update-component">
        <krad:template componentUpdate="true" component="${Component}"/>
        <krad:script value="${KualiForm.lightboxScript}"/>
      </div>

    </s:nestedPath>
    </html>

  </c:otherwise>
</c:choose>
