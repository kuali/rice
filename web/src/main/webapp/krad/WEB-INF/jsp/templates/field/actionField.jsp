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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="field"
                    classname="org.kuali.rice.krad.uif.field.ActionField"/>

<%--
    Standard HTML Input Submit - will create an input of type submit or type image if the action
    image field is configured
    
 --%>
<c:if test="${field.skipInTabOrder}">
  <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${field.actionImageField != null}">
  <c:if test="${not empty field.actionImageField.height}">
    <c:set var="height" value="height='${field.actionImageField.height}'"/>
  </c:if>
  <c:if test="${not empty field.actionImageField.width}">
    <c:set var="width" value="width='${field.actionImageField.width}'"/>
  </c:if>
</c:if>


<c:choose>

  <c:when
          test="${(field.actionImageField != null) && field.actionImageField.render && (empty field.actionImageLocation)}">
    <krad:attributeBuilder component="${field.actionImageField}"/>

    <input type="image" id="${field.id}"
           src="${field.actionImageField.source}"
           alt="${field.actionImageField.altText}" ${height} ${width} ${style} ${styleClass} ${title} ${tabindex} />
  </c:when>
  <c:otherwise>
    <krad:attributeBuilder component="${field}"/>


    <c:choose>
      <c:when test="${not empty field.actionImageLocation && (field.actionImageField != null) && field.actionImageField.render}">

        <c:choose>
          <c:when test="${(field.actionImageLocation eq 'TOP')}">
            <button id="${field.id}" ${style} ${styleClass} ${title}><span class="topBottomSpan"><img ${height} ${width}
                    class="actionImage topActionImage ${field.actionImageField.styleClassesAsString}"
                    style="${field.actionImageField.style}"
                    src="${field.actionImageField.source}"
                    alt="${field.actionImageField.altText}"/></span>${field.actionLabel}
            </button>
          </c:when>
          <c:when test="${(field.actionImageLocation eq 'BOTTOM')}">
            <button id="${field.id}" ${style} ${styleClass} ${title}>${field.actionLabel}<span
                    class="topBottomSpan"><img ${height} ${width}
                    style="${field.actionImageField.style}"
                    class="actionImage bottomActionImage ${field.actionImageField.styleClassesAsString}"
                    src="${field.actionImageField.source}"
                    alt="${field.actionImageField.altText}"/></span></button>
          </c:when>
          <c:when test="${(field.actionImageLocation eq 'RIGHT')}">
            <button id="${field.id}" ${style} ${styleClass} ${title}>${field.actionLabel}<img ${height} ${width}
                    style="${field.actionImageField.style}"
                    class="actionImage rightActionImage ${field.actionImageField.styleClassesAsString}"
                    src="${field.actionImageField.source}" alt="${field.actionImageField.altText}"/></button>
          </c:when>
          <c:when test="${(field.actionImageLocation eq 'LEFT')}">
            <button id="${field.id}" ${style} ${styleClass} ${title}><img ${height} ${width}
                    style="${field.actionImageField.style}"
                    class="actionImage leftActionImage ${field.actionImageField.styleClassesAsString}"
                    src="${field.actionImageField.source}"
                    alt="${field.actionImageField.altText}"/>${field.actionLabel}
            </button>
          </c:when>
          <c:otherwise>
            <button id="${field.id}" ${style} ${styleClass} ${title}>${field.actionLabel}</button>
          </c:otherwise>
        </c:choose>
      </c:when>
      <c:otherwise>
        <button id="${field.id}" ${style} ${styleClass} ${title}>${field.actionLabel}</button>
      </c:otherwise>
    </c:choose>

  </c:otherwise>
</c:choose>

<c:if test="${(field.lightBoxLookup != null)}">
  <krad:template component="${field.lightBoxLookup}" componentId="${field.id}"/>
</c:if>