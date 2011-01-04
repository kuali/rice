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
<tiles:useAttribute name="manager" classname="org.kuali.rice.kns.ui.layout.BoxLayoutManager"/>

<%--
    Box Layout Manager:
    
      Places each component of the given list into a horizontal or vertical row.
      
      The amount of padding is configured by the seperationPadding 
      property of the layout manager. The padding is implemented by using a clear image and setting the width or
      height to the padding configuration.
 --%>

<%-- setup the correct item padding --%> 
<c:set var="paddingWidth" value="1px"/>
<c:set var="paddingHeight" value="1px"/>

<c:choose>
  <c:when test="${manager.orientation eq UIFConstants.Orientation.VERTICAL}">
     <c:set var="paddingHeight" value="${manager.seperationPadding}"/>
  </c:when>
  <c:otherwise>
     <c:set var="paddingWidth" value="${manager.seperationPadding}"/>
  </c:otherwise>
</c:choose>

<%-- render items --%> 
<div id="${manager.id}_layout" style="${manager.style}" class="${manager.styleClass}">

   <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
       <krad:template component="${item}"/>
       
       <%-- add line breaks for vertical orientation --%>
       <c:if test="${manager.orientation eq UIFConstants.Orientation.VERTICAL}">
         <br/>
       </c:if>       
       
       <%-- add padding --%>
       <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="${paddingWidth}" height="${paddingHeight}"/>       
       
       <%-- add line breaks for vertical orientation --%>
       <c:if test="${manager.orientation eq UIFConstants.Orientation.VERTICAL}">
         <br/>
       </c:if>
   </c:forEach>

</div> 