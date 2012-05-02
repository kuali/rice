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

<tiles:useAttribute name="element" classname="org.kuali.rice.krad.uif.element.Label"/>

<krad:attributeBuilder component="${element}"/>

<c:set var="label" value="${element.labelText}"/>

<c:if test="${element.renderColon}">
  <c:set var="label" value="${label}:"/>
</c:if>

<krad:span component="${element}">
  <%-- required message --%>
  <c:if test="${element.requiredMessagePlacement eq 'LEFT'}">
    <krad:template component="${element.requiredMessage}"/>
  </c:if>

  <label id="${element.id}" for="${element.labelForComponentId}" ${element.simpleDataAttributes}
    ${title} ${dataRoleAttribute} ${dataMetaAttribute} ${dataTypeAttribute}>
    ${label}
  </label>

  <%-- required message --%>
  <c:if test="${element.requiredMessagePlacement eq 'RIGHT'}">
    <krad:template component="${element.requiredMessage}"/>
  </c:if>

</krad:span>