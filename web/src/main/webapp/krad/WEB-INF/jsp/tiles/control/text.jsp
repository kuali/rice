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

<tiles:useAttribute name="control" classname="org.kuali.rice.kns.ui.control.TextControl"/>
<tiles:useAttribute name="field" classname="org.kuali.rice.kns.ui.field.AttributeField"/>

<%--
    Standard HTML Text Input
    
 --%>

<form:input id="${control.id}" path="${field.bindingPath}" 
            size="${control.size}" maxlength="${field.maxLength}" 
            cssClass="${control.styleClass}" cssStyle="${control.style}"
            tabindex="${control.tabIndex}"/>

<%-- render date picker widget --%> 
<c:if test="${(!empty control.datePicker) && control.datePicker.render}">              
   <tiles:insertTemplate template="${control.datePicker.template}">
      <tiles:putAttribute name="${control.datePicker.componentTypeName}" value="${control.datePicker}"/>
      <tiles:putAttribute name="control" value="${control}"/>
   </tiles:insertTemplate>  
</c:if> 
 