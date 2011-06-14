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

<tiles:useAttribute name="field"
	classname="org.kuali.rice.krad.uif.field.LinkField" />

<%--
    Standard HTML Link     
 --%>

<krad:attributeBuilder component="${field}" />

<c:if test="${(field.lightBox != null)}">
	<krad:template component="${field.lightBox}" componentId="${field.id}" />
</c:if>

<c:if test="${field.skipInTabOrder}">
	<c:set var="tabindex" value="tabindex=-1" />
</c:if>

<a id="${field.id}" href="${field.hrefText}" target="${field.target}"${style} ${styleClass}  ${tabindex} >${field.linkLabel}</a>