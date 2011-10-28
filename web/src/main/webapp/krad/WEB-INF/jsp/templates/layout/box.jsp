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
<tiles:useAttribute name="manager" classname="org.kuali.rice.krad.uif.layout.BoxLayoutManager"/>
<tiles:useAttribute name="container" classname="org.kuali.rice.krad.uif.container.ContainerBase"/>

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

<c:set var="itemSpanClassesHorizontal" value="class=\"fieldLine boxLayoutHorizontalItem\""/>
<c:set var="itemSpanClassesVertical" value="class=\"fieldLine boxLayoutVerticalItem clearfix\""/>

<c:if test="${container.fieldContainer}">
  <c:set var="fieldItemsStyle" value="style=\"float:left;\""/>
  <c:set var="itemSpanClassesHorizontal" value="class=\"boxLayoutHorizontalItem\""/>
  <c:set var="itemSpanClassesVertical" value="class=\"fieldContainerVerticalItem clearfix\""/>
</c:if>

<%-- render items --%>
<div id="${manager.id}" ${style} ${styleClass}>
  <span ${fieldItemsStyle}>
   <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
     <c:choose>
       <c:when test="${manager.orientation=='HORIZONTAL'}">
         <span ${itemSpanClassesHorizontal} style="${manager.itemStyle}">
            <krad:template component="${item}"/>
         </span>
       </c:when>
       <c:otherwise>
	       <span ${itemSpanClassesVertical} style="${manager.itemStyle}">
	        	<krad:template component="${item}"/>
	       </span>
       </c:otherwise>
     </c:choose>
   </c:forEach>
  </span>

  <%--
     Adds a special error container for horizontal case, fields will instead display their errors here
     (errorsField in attributeFields of this layout will not generate their errorsField through their jsp, as normal)
     see BoxLayoutManager.java
  --%>
  <c:if test="${manager.layoutFieldErrors}">
	   <span id="${manager.id}_errors_block" class="kr-errorsField" style="float:left;">
	   		<c:forEach items="${container.inputFields}" var="item">
           <krad:template component="${item.errorsField}"/>
         </c:forEach>
	   </span>
  </c:if>

</div> 