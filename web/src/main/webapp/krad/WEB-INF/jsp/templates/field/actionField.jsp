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

<tiles:useAttribute name="field" classname="org.kuali.rice.kns.uif.field.ActionField"/>

<%--
    Standard HTML Input Submit - will create an input of type submit or type image if the action
    image field is configured
    
 --%>

<c:choose>
  <c:when test="${(field.actionImageField != null) && field.actionImageField.render}">
     <krad:attributeBuilder component="${field.actionImageField}"/>
  
     <input type="image" id="${field.id}" src="${field.actionImageField.source}" 
            alt="${field.actionImageField.altText}" ${style} ${styleClass} ${title}/>
  </c:when>
  <c:otherwise>
     <krad:attributeBuilder component="${field}"/>
   
     <input type="submit" id="${field.id}" value="${field.actionLabel}" ${style} ${styleClass} ${title}/>
  </c:otherwise>
</c:choose>

<c:if test="${(field.lightBoxLookup != null)}">
		<krad:template component="${field.lightBoxLookup}" componentId="${field.id}"/>
</c:if>	