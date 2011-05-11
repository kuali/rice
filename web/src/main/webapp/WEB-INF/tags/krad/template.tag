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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<%@ tag dynamic-attributes="templateParameters"%>

<%@ attribute name="component" required="true" 
              description="The UIF component for which the template will be generated" 
              type="org.kuali.rice.kns.uif.core.Component"%>
              
<%-- verify the component is not null and should be rendered --%>   
<c:if test="${(!empty component) && component.render}">     
   <%-- render component through template --%>    
   <tiles:insertTemplate template="${component.template}">
       <tiles:putAttribute name="${component.componentTypeName}" value="${component}"/>
       <c:forEach items="${templateParameters}" var="parameter">
         <tiles:putAttribute name="${parameter.key}" value="${parameter.value}"/>
       </c:forEach>
   </tiles:insertTemplate>
       
   <%-- generate event code for component --%>
   <krad:eventScript component="${component}"/>
</c:if>            
