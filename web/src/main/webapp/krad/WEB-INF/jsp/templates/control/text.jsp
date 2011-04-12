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

<tiles:useAttribute name="control" classname="org.kuali.rice.kns.uif.control.TextControl"/>
<tiles:useAttribute name="field" classname="org.kuali.rice.kns.uif.field.AttributeField"/>

<%--
    Standard HTML Text Input
    
 --%>

<form:input id="${control.id}" path="${field.bindingInfo.bindingPath}" 
            size="${control.size}" maxlength="${field.maxLength}" readonly="${control.readOnly}"
            cssClass="${control.styleClassesAsString}" cssStyle="${control.style}"
            tabindex="${control.tabIndex}" minLength="${field.minLength}"/>

<c:if test="${(!empty control.watermarkText)}">
	<script type="text/javascript">
		createWatermark("${control.id}", "${control.watermarkText}");
	</script>
</c:if>     

<%-- render date picker widget --%> 
<krad:template component="${control.datePicker}" componentId="${control.id}"/>           
 