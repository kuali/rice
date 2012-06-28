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
    Standard HTML Input Submit - will create an input of type submit or type image if the action
    image element is configured
    
 --%>
<c:if test="${element.skipInTabOrder}">
  <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${element.actionImage != null}">
  <c:if test="${not empty element.actionImage.height}">
    <c:set var="height" value="height='${element.actionImage.height}'"/>
  </c:if>
  <c:if test="${not empty element.actionImage.width}">
    <c:set var="width" value="width='${element.actionImage.width}'"/>
  </c:if>
</c:if>

<c:if test="${element.disabled}">
  <c:set var="disabled" value="disabled=\"true\""/>
</c:if>

<c:set var="tagId" value="${element.id}"/>
<c:choose>

  <c:when test="${(element.actionImage != null) && element.actionImage.render && (empty element.actionImagePlacement || element.actionImagePlacement eq 'IMAGE_ONLY')}">
    <krad:attributeBuilder component="${element.actionImage}"/>

    <input type="image" id="${tagId}" ${disabled}
           src="${element.actionImage.source}"
           alt="${element.actionImage.altText}"
           title="${element.actionImage.title}" ${height} ${width} ${style} ${styleClass} ${title} ${tabindex}
           ${element.simpleDataAttributes} data-role="${element.dataRoleAttribute}" data-type="${element.dataTypeAttribute}"
           data-meta="${element.dataMetaAttribute}"/>
  </c:when>
  <c:otherwise>
    <krad:attributeBuilder component="${element}"/>

    <c:set var="dataAttributes" value="${dataRoleAttribute} ${dataMetaAttribute} ${dataTypeAttribute}"/>

    <c:choose>
      <c:when test="${not empty element.actionImagePlacement && (element.actionImage != null) && element.actionImage.render}">
        <c:choose>
          <c:when test="${(element.actionImagePlacement eq 'TOP')}">
            <button id="${tagId}" ${style} ${styleClass} ${title} ${disabled} ${element.simpleDataAttributes} ${dataAttributes}><span
                    class="topBottomSpan"><img ${height} ${width}
                    class="actionImage topActionImage ${element.actionImage.styleClassesAsString}"
                    style="${element.actionImage.style}"
                    src="${element.actionImage.source}"
                    alt="${element.actionImage.altText}"
                    title="${element.actionImage.title}"/></span>${element.actionLabel}
            </button>
          </c:when>

          <c:when test="${(element.actionImagePlacement eq 'BOTTOM')}">
            <button id="${tagId}" ${style} ${styleClass} ${title} ${disabled} ${element.simpleDataAttributes} ${dataAttributes}>
            ${element.actionLabel}<span
                    class="topBottomSpan"><img ${height} ${width}
                    style="${element.actionImage.style}"
                    class="actionImage bottomActionImage ${element.actionImage.styleClassesAsString}"
                    src="${element.actionImage.source}"
                    alt="${element.actionImage.altText}"
                    title="${element.actionImage.title}"/></span></button>
          </c:when>

          <c:when test="${(element.actionImagePlacement eq 'RIGHT')}">
            <button id="${tagId}" ${style} ${styleClass} ${title} ${disabled} ${element.simpleDataAttributes} ${dataAttributes}>
            ${element.actionLabel}<img ${height} ${width}
                    style="${element.actionImage.style}"
                    class="actionImage rightActionImage ${element.actionImage.styleClassesAsString}"
                    src="${element.actionImage.source}"
                    alt="${element.actionImage.altText}"
                    title="${element.actionImage.title}"/></button>
          </c:when>

          <c:when test="${(element.actionImagePlacement eq 'LEFT')}">
            <button id="${tagId}" ${style} ${styleClass} ${title} ${disabled} ${element.simpleDataAttributes} ${dataAttributes}>
              <img id="${element.actionImage.id}" ${height} ${width}
                    style="${element.actionImage.style}"
                    class="actionImage leftActionImage ${element.actionImage.styleClassesAsString}"
                    src="${element.actionImage.source}"
                    alt="${element.actionImage.altText}"
                    title="${element.actionImage.title}"/>${element.actionLabel}
            </button>
          </c:when>

          <c:otherwise>
            <button id="${tagId}" ${style} ${styleClass} ${title} ${disabled} ${element.simpleDataAttributes} ${dataAttributes}>
            ${element.actionLabel}</button>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:otherwise>
        <button id="${tagId}" ${style} ${styleClass} ${title} ${disabled} ${element.simpleDataAttributes} ${dataAttributes}>
        ${element.actionLabel}</button>
      </c:otherwise>
    </c:choose>

  </c:otherwise>
</c:choose>

<c:if test="${(element.lightBoxLookup != null)}">
  <krad:template component="${element.lightBoxLookup}" componentId="${element.id}"/>
</c:if>
