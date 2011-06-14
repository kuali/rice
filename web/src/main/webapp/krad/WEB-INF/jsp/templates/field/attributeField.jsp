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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.AttributeField"/>

<%-- check to see if label has been rendered in another field (grid layout)--%>
<c:set var="renderLabel" value="${!field.labelFieldRendered}"/>

<krad:span component="${field}">

  <%-- render field label left --%>
  <c:if test="${renderLabel && ((field.labelPlacement eq 'LEFT') || (field.labelPlacement eq 'TOP'))}">
    <krad:template component="${field.labelField}"/>
  </c:if>

  <%-- render field value (if read-only) or control (if edit) --%>
	<c:choose>
		<c:when test="${field.readOnly}">
			<c:if test="${field.fieldInquiry.render}">
				<krad:template component="${field.fieldInquiry}" componentId="${field.id}" />
			</c:if>

			<c:if test="${!field.fieldInquiry.render}">
				<%-- Display alternate display value if it's set --%>
				<c:if test="${not empty field.alternateDisplayValue}">
					<c:out value="${field.alternateDisplayValue}" />
				</c:if>

				<c:if test="${empty field.alternateDisplayValue}">
					<%-- If alternate display value is not present, look for additional property to display --%>
					<c:if test="${not empty field.additionalDisplayValue}">
						<s:bind path="${field.bindingInfo.bindingPath}">${status.value} *-* </s:bind>
						<c:out value="${field.additionalDisplayValue}" />
					</c:if>

					<%-- If either alternate value or additional property not preset, display the actual property value --%>
					<c:if test="${empty field.additionalDisplayValue}">
						<s:bind path="${field.bindingInfo.bindingPath}">${status.value}</s:bind>
					</c:if>
				</c:if>
			</c:if>
		</c:when>

		<c:otherwise>
			<%-- render field summary --%>
			<krad:template component="${field.summaryMessageField}" />

			<krad:template component="${field.control}" field="${field}" />
		</c:otherwise>
	</c:choose>

	<%-- render field quickfinder --%>
  <krad:template component="${field.fieldLookup}" componentId="${field.id}"/>

  <%-- render field direct inquiry if field is editable --%>
  <c:if test="${field.fieldDirectInquiry.render && !field.readOnly}">
        <krad:template component="${field.fieldDirectInquiry}" componentId="${field.id}"/>
  </c:if>
  
  <%-- render field label right --%>
  <c:if test="${renderLabel && (field.labelPlacement eq 'RIGHT')}">
    <krad:template component="${field.labelField}"/>
  </c:if>

  <!-- placeholder for dynamic field markers -->
  <span id="${field.id}_markers"></span>

  <c:if test="${!field.readOnly}">
    <%-- render field constraint --%>
    <krad:template component="${field.constraintMessageField}"/>
  </c:if>

  <%-- render span and values for informational properties --%>
  <span id="${field.id}_info_message"></span>
  <c:forEach items="${field.informationalDisplayPropertyNames}" var="infoPropertyPath" varStatus="status">
     <span id="${field.id}_info_${fn:replace(infoPropertyPath,'.','_')}" class="info-field">
        <s:bind path="${infoPropertyPath}">${status.value}</s:bind>
     </span>
  </c:forEach>

  <%-- render field help --%>

  <%-- render field suggest --%>
  <c:if test="${!field.readOnly}">
     <krad:template component="${field.fieldSuggest}" parent="${field}"/>
  </c:if>
  
</krad:span>

<c:if test="${!field.errorsField.alternateContainer}">
	<krad:template component="${field.errorsField}"/>
</c:if>