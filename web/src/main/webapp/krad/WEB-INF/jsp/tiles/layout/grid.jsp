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
<tiles:useAttribute name="manager" classname="org.kuali.rice.kns.ui.layout.GridLayoutManager"/>

<%--
    Grid Layout Manager:
    
      Places each component of the given list into a table cell. A new row is created after the
      configured number of columns is rendered.
      
      The number of horizontal places a field takes up in the grid is determined by the configured colSpan. 
      Likewise the number of vertical places a field takes up is determined by the configured rowSpan. 
      
      If the width for the column is not given by the field, it will be calculated by equally dividing the
      space by the number of columns.
 --%>

<c:set var="numberOfColumns" value="${manager.numberOfColumns}"/>
<c:set var="defaultCellWidth" value="${100/numberOfColumns}"/>

<table id="${manager.id}" style="${manager.style}" class="${manager.styleClass}">

  <c:set var="colCount" value="0"/>
  <c:set var="carryOverColCount" value="0"/>
  <c:set var="tmpCarryOverColCount" value="0"/>
  <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
     <c:set var="colCount" value="${colCount + 1}"/> 
     
     <%-- begin table row --%>
     <c:if test="${itemVarStatus.first || (colCount % numberOfColumns == 1)}">
       <tr>
     </c:if>
     
     <%-- skip column positions from previous rowspan --%>
     <c:forEach var="i" begin="1" end="${carryOverColCount}" step="1" varStatus ="status">
        <c:set var="colCount" value="${colCount + 1}"/>
        <c:set var="carryOverColCount" value="${carryOverColCount - 1}"/>
        
        <c:if test="${colCount % numberOfColumns == 0}">
          </tr><tr>
        </c:if>
     </c:forEach>
     
     <%-- determine cell width by using default or configured width --%>
     <c:set var="cellWidth" value="${defaultCellWidth * item.colSpan}%"/>
     <c:if test="${!empty item.width}">
        <c:set var="cellWidth" value="${item.width}"/>
     </c:if>
     
     <%-- render cell and item template --%>
     <td width="${cellWidth}" align="${item.align}" valign="${item.valign}" 
         colspan="${item.colSpan}" rowspan="${item.rowSpan}"
         style="${item.style}" class="${item.styleClass}">
       <krad:template component="${item}"/>
     </td>
     
     <%-- handle colspan for the count --%>  
     <c:set var="colCount" value="${colCount + item.colSpan - 1}"/>  
     
     <%-- set carry over count to hold positions for fields that span multiple rows --%>
     <c:set var="tmpCarryOverColCount" value="${tmpCarryOverColCount + item.rowSpan - 1}"/>
     
     <%-- end table row --%>  
     <c:if test="${itemVarStatus.last || (colCount % numberOfColumns == 0)}">
       </tr>
       <c:set var="carryOverColCount" value="${carryOverColCount + tmpCarryOverColCount}"/>
       <c:set var="tmpCarryOverColCount" value="0"/>
     </c:if>  
  </c:forEach>

</table>
