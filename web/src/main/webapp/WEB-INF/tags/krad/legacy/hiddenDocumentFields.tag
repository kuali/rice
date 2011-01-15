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
<%@ include file="/krad/WEB-INF/jsp/test/tldHeader.jsp"%>
<%@ attribute name="includeDocumentHeaderFields" required="false" description="Whether to include the document number as a hidden field." %>
<%@ attribute name="includeEditMode" required="false" description="Whether to include the current edit modes as hidden fields." %>

<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />

<%-- set default values --%>
<c:if test="${empty includeDocumentHeaderFields}">
    <c:set var="includeDocumentHeaderFields" value="true" />
</c:if>
<c:if test="${empty includeEditMode}">
    <c:set var="includeEditMode" value="true" />
</c:if>

<html:hidden  path="docId" />
<html:hidden path="document.documentNumber" />

<c:if test="${includeDocumentHeaderFields}">
  <html:hidden path="document.documentHeader.documentNumber" />  
</c:if>
<c:if test="${includeEditMode}">
    <c:forEach items="${KualiForm.editingMode}" var="mode" varStatus="status">
<!--
      <html:hidden path="editingMode(${mode.key})"/>
-->
      <html:hidden path="editingMode[${status.index}]"/>
    </c:forEach>
</c:if>
