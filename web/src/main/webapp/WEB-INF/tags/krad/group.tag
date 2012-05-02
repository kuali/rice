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

<%@ attribute name="group" required="true"
              description="The group instance that is being rendered"
              type="org.kuali.rice.krad.uif.container.Group"%>
<!-- GROUP '${group.id}' HEADER -->

<krad:div component="${group}">

  <c:if test="${!empty group.header}">
    <krad:template component="${group.header}"/>
  </c:if>

  <c:if test="${group.disclosure.render}">
    <div id="${group.id}_disclosureContent" class="uif-disclosureContent">
  </c:if>

  <krad:template component="${group.validationMessages}"/>
  <krad:template component="${group.instructionalMessage}"/>

  <jsp:doBody/>

  <!-- GROUP '${group.id}' FOOTER -->
  <c:if test="${!empty group.footer}">
    <krad:template component="${group.footer}"/>
  </c:if>

  <c:if test="${group.disclosure.render}">
    <%-- render group disclosure --%>
    <krad:template component="${group.disclosure}" parent="${group}"/>
    </div>
  </c:if>

</krad:div>