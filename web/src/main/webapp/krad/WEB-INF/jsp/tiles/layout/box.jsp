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

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.rice.kns.uif.layout.BoxLayoutManager"/>

<%--
    Box Layout Manager:
    
      Places each component of the given list into a horizontal or vertical row.
      
      The amount of padding is configured by the seperationPadding 
      property of the layout manager. The padding is implemented by setting the margin of the wrapping
      span style. For vertical orientation, the span style is set to block.
 --%>
 
<c:if test="${!empty manager.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
  <c:set var="style" value="style=\"${manager.style}\""/>
</c:if>

<%-- render items --%> 
<div id="${manager.id}" ${style} ${styleClass}>
   <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
     <%-- wrapping span gives a way to pad items and line break for vertical orientation --%>
     <span style="${manager.itemSpanStyle}">
       <krad:template component="${item}"/>
     </span>  
   </c:forEach>
</div> 