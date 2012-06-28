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

<tiles:useAttribute name="element" classname="org.kuali.rice.krad.uif.element.Action"/>

<%--
    HTML Link to Submit Form Via JavaScript
    
 --%>

<krad:attributeBuilder component="${element}"/>

<c:set var="pound" value="#"/>
<c:if test="${!empty element.navigateToPageId}">
  <c:set var="name" value="name=\"${element.navigateToPageId}\""/>
  <c:set var="href" value="href=\"${pound}${element.navigateToPageId}\""/>
</c:if>

<c:set var="tabindex" value="tabindex=0"/>
<c:if test="${element.skipInTabOrder}">
  <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${element.actionLabel != null}">
  <c:set var="imageRole" value="role='presentation'"/>
</c:if>

<c:choose>
  <c:when test="${(element.actionImage != null) && element.actionImage.render}">
    <c:if test="${not empty element.actionImage.height}">
      <c:set var="height" value="height='${element.actionImage.height}'"/>
    </c:if>

    <c:if test="${not empty element.actionImage.width}">
      <c:set var="width" value="width='${element.actionImage.width}'"/>
    </c:if>

    <c:choose>
      <c:when test="${element.actionImagePlacement != null && (element.actionImagePlacement eq 'RIGHT')}">
        <a id="${element.id}" ${href}
           onclick="return false;"${name} ${style} ${styleClass} ${tabindex} ${element.simpleDataAttributes} ${dataRoleAttribute}
          ${dataMetaAttribute} ${dataTypeAttribute}>${element.actionLabel}
          <img ${imageRole}
                  class="actionImage rightActionImage ${element.actionImage.styleClassesAsString}" ${height} ${width}
                  style="${element.actionImage.style}" src="${element.actionImage.source}"
                  alt="${element.actionImage.altText}"
                  title="${element.actionImage.title}"/>
        </a>
      </c:when>

      <c:otherwise>
        <a id="${element.id}" ${href}
           onclick="return false;"${name} ${style} ${styleClass} ${tabindex} ${element.simpleDataAttributes}
          ${dataRoleAttribute} ${dataMetaAttribute} ${dataTypeAttribute}>
          <img ${imageRole}
                class="actionImage leftActionImage ${element.actionImage.styleClassesAsString}" ${height} ${width}
                style="${element.actionImage.style}" src="${element.actionImage.source}"
                alt="${element.actionImage.altText}"
                title="${element.actionImage.title}"/>${element.actionLabel}
        </a>
      </c:otherwise>
    </c:choose>
  </c:when>

  <c:otherwise>
    <a id="${element.id}" ${href}
       onclick="return false;" ${name} ${style} ${styleClass} ${tabindex} ${element.simpleDataAttributes}
      ${dataRoleAttribute} ${dataMetaAttribute} ${dataTypeAttribute}>${element.actionLabel}</a>
  </c:otherwise>
</c:choose>

<c:if test="${(element.lightBoxLookup != null)}">
  <krad:template component="${element.lightBoxLookup}" componentId="${element.id}"/>
</c:if>