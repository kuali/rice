<#--
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
  -->

<#assign readOnly=field.readOnly || !field.inputAllowed/>

<@krad.div component=field>

  <@krad.fieldLbl field=field>

      <#-- render field value (if read-only) or control (if edit) -->
      <#if readOnly>

          <#assign readOnlyDisplay>
              <#-- display replacement display value if set -->
              <#if field.readOnlyDisplayReplacement?has_content>
                  ${field.readOnlyDisplayReplacement}
              <#else>
                  <#-- display actual field value -->
                  <@spring.bindEscaped path="${field.bindingInfo.bindingPath}"
                                       htmlEscape=field.escapeHtmlInPropertyValue/>
                  ${spring.status.value?default("")}

                  <%-- add display suffix value if set --%>
                  <#if field.readOnlyDisplaySuffix?has_content>
                      *-* ${field.readOnlyDisplaySuffix}
                  </#if>
              </#if>
          </#assign>

          <span id="${field.id}">
              <#-- render inquiry if enabled -->
              <#if field.inquiry.render>
                  <@krad.template component=field.inquiry componentId="${field.id}" body="${readOnlyDisplay}"
                                  readOnly=field.readOnly/>
              <#else>
                  ${readOnlyDisplay}
              </#if>
          </span>

      <#else>

          <#-- render field instructional text -->
          <@krad.template component=field.instructionalMessage/>

          <#-- render control for input -->
          <@krad.template component=field.control field="${field}"/>

      </#if>

      <#-- render field quickfinder -->
      <#if field.inputAllowed>
          <@krad.template component=field.quickfinder componentId="${field.id}"/>
      </#if>

      <#-- render field direct inquiry if field is editable and inquiry is enabled-->
      <#if !readOnly && field.inquiry.render>
          <@krad.template component=field.inquiry componentId="${field.id}" readOnly=field.readOnly/>
      </#if>

  </@krad.fieldLbl>

  <!-- placeholder for dynamic field markers -->
  <span id="${field.id}_markers"></span>

  <#if !readOnly>
      <#-- render error container for field -->
      <@krad.template component=field.validationMessages/>

      <#-- render field constraint -->
      <@krad.template component=field.constraintMessage/>
  </#if>

  <#-- render span and values for informational properties -->
  <span id="${field.id}_info_message"></span>

  <#list field.propertyNamesForAdditionalDisplay as infoPropertyPath>
      <span id="${field.id}_info_${infoPropertyPath_index}" class="uif-informationalMessage">

      </span>
  </#list>

</@krad.div>






  <%-- render span and values for informational properties --%>
  <span id="${field.id}_info_message"></span>
  <c:forEach items="${field.propertyNamesForAdditionalDisplay}" var="infoPropertyPath" varStatus="status">
    <%-- TODO: clean this up somehow! --%>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyPath,'.','_')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,'[','-lbrak-')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,']','-rbrak-')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,'\\\'','-quot-')}"/>
     <span id="${field.id}_info_${infoPropertyPath_index}" class="uif-informationalMessage">
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




