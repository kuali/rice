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

<tiles:useAttribute name="element" classname="org.kuali.rice.krad.uif.element.Link"/>
<tiles:useAttribute name="body"/>

<%--
    Standard HTML Link     
 --%>

<c:if test="${element.skipInTabOrder}">
  <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${empty fn:trim(body)}">
  <c:set var="body" value="${element.linkText}"/>
</c:if>

<krad:attributeBuilder component="${element}"/>

<a id="${element.id}" href="${element.href}" target="${element.target}" ${title}
${style} ${styleClass} ${tabindex} ${element.simpleDataAttributes}
${dataRoleAttribute} ${dataMetaAttribute} ${dataTypeAttribute}>${body}</a>

<c:if test="${(element.lightBox != null)}">
  <krad:template component="${element.lightBox}" componentId="${element.id}"/>
</c:if>
