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
      
      The components can be padded with spaces for horizontal orientation and line breaks
      for vertical orientation. The amount of padding is configured by the seperationPadding 
      property of the layout manager.
 --%>
 
<%-- setup the correct item padding --%> 
<c:set var="paddingChar" value="&nbsp;"/>
<c:if test="${manager.orientation eq UIFConstants.Orientation.VERTICAL}">
   <c:set var="paddingChar" value="</br>"/>
</c:if>

<c:set var="padding" value="${paddingChar}"/>
<c:forEach var="i" begin="2" end="${manager.seperationPadding}" step="1">
   <c:set var="padding" value="${padding}${paddingChar}"/>
</c:forEach>

<%-- render items --%> 
<div id="${manager.id}" style="${manager.style}" class="${manager.styleClass}">

   <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
       <tiles:insertTemplate template="${item.template}">
          <tiles:putAttribute name="${item.componentTypeName}" value="${item}"/>
       </tiles:insertTemplate>
       ${padding}
   </c:forEach>

</div> 