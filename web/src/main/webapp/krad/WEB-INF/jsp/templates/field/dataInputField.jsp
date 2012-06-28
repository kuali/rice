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

<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.DataField"/>

<c:set var="readOnly" value="${field.readOnly || !field.inputAllowed}"/>

<krad:div component="${field}">

  <krad:fieldLabel field="${field}">

    <%-- render field value (if read-only) or control (if edit) --%>
    <c:choose>
      <c:when test="${readOnly}">
        <c:set var="readOnlyDisplay">
          <%-- display alternate display value if set --%>
          <c:if test="${not empty field.readOnlyDisplayReplacement}">
            ${field.readOnlyDisplayReplacement}
          </c:if>

          <c:if test="${empty field.readOnlyDisplayReplacement}">
            <%-- display actual field value --%>
            <s:bind path="${field.bindingInfo.bindingPath}"
                    htmlEscape="${field.escapeHtmlInPropertyValue}">${status.value}</s:bind>

            <%-- add alternate display value if set --%>
            <c:if test="${not empty field.readOnlyDisplaySuffix}">
              *-* ${field.readOnlyDisplaySuffix}
            </c:if>
          </c:if>
        </c:set>

        <span id="${field.id}">
          <%-- render inquiry if enabled --%>
          <c:choose>
            <c:when test="${field.inquiry.render}">
              <krad:template component="${field.inquiry}" componentId="${field.id}" body="${readOnlyDisplay}"
                             readOnly="${field.readOnly}" />
            </c:when>

            <c:otherwise>
              ${readOnlyDisplay}
            </c:otherwise>
          </c:choose>
        </span>
      </c:when>

      <c:otherwise>
        <%-- render field instructional text --%>
        <krad:template component="${field.instructionalMessage}"/>

        <%-- render control for input --%>
        <krad:template component="${field.control}" field="${field}"/>
      </c:otherwise>
    </c:choose>

    <%-- render field quickfinder --%>
    <c:if test="${field.inputAllowed}">
      <krad:template component="${field.quickfinder}" componentId="${field.id}"/>
    </c:if>

    <%-- render field direct inquiry if field is editable and inquiry is enabled--%>
    <c:if test="${!readOnly && field.inquiry.render}">
      <krad:template component="${field.inquiry}" componentId="${field.id}" readOnly="${field.readOnly}"/>
    </c:if>

  </krad:fieldLabel>

  <!-- placeholder for dynamic field markers -->
  <span id="${field.id}_markers"></span>

  <%-- render error container for field --%>
  <c:if test="${!readOnly}">
    <krad:template component="${field.validationMessages}"/>
  </c:if>

  <%-- render field constraint if field is editable --%>
  <c:if test="${!readOnly}">
    <krad:template component="${field.constraintMessage}"/>
  </c:if>

  <%-- render span and values for informational properties --%>
  <span id="${field.id}_info_message"></span>
  <c:forEach items="${field.propertyNamesForAdditionalDisplay}" var="infoPropertyPath" varStatus="status">
    <%-- TODO: clean this up somehow! --%>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyPath,'.','_')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,'[','-lbrak-')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,']','-rbrak-')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,'\\\'','-quot-')}"/>
     <span id="${field.id}_info_${infoPropertyId}" class="uif-informationalMessage">
        <s:bind path="${infoPropertyPath}">${status.value}</s:bind>
     </span>
  </c:forEach>

  <%-- render field help --%>
  <krad:template component="${field.help}"/>

  <%-- render field suggest if field is editable --%>
  <c:if test="${!readOnly}">
    <krad:template component="${field.suggest}" parent="${field}"/>
  </c:if>

  <%-- render hidden fields --%>
  <%-- TODO: always render hiddens if configured? --%>
  <c:forEach items="${field.additionalHiddenPropertyNames}" var="hiddenPropertyName" varStatus="status">
    <form:hidden id="${field.id}_h${status.count}" path="${hiddenPropertyName}"/>
  </c:forEach>

  <%-- transform all text on attribute field to uppercase --%>
  <c:if test="${!readOnly && field.uppercaseValue}">
    <krad:script value="uppercaseValue('${field.control.id}');"/>
  </c:if>
</krad:div>




