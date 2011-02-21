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

<%@ attribute name="items" required="true" 
              description="List of fields to display within the grid" 
              type="java.util.List"%>
<%@ attribute name="numberOfColumns" required="false" 
              description="Number of columns the grid should contain, defaults to 2"%>  
<%@ attribute name="renderHeaderColumns" required="false" 
              description="Boolean that indicates whether the columns should rendered as th or td cell, defaults to false"%>   
<%@ attribute name="applyAlternatingRowStyles" required="false" 
              description="Boolean that indicates whether the even odd style classes should be applied to the rows, defaults to false"%>                            
              
<c:if test="${empty numberOfColumns}">
  <c:set var="numberOfColumns" value="2"/>
</c:if>      

<c:if test="${empty renderHeaderColumns}">
  <c:set var="renderHeaderColumns" value="false"/>
</c:if>        

<c:if test="${empty applyAlternatingRowStyles}">
  <c:set var="applyAlternatingRowStyles" value="false"/>
</c:if>   
   
<c:set var="defaultCellWidth" value="${100/numberOfColumns}"/> 
             
<c:set var="colCount" value="0"/>
<c:set var="carryOverColCount" value="0"/>
<c:set var="tmpCarryOverColCount" value="0"/>

<c:forEach items="${items}" var="item" varStatus="itemVarStatus">
   <c:set var="colCount" value="${colCount + 1}"/> 
     
   <%-- begin table row --%>
   <c:if test="${itemVarStatus.first || (colCount % numberOfColumns == 1)}">
     <tr>
     
     <%-- determine if even or add style should be applied --%>
     <c:if test="${applyAlternatingRowStyles}">
       <c:choose>
         <c:when test="${evenOddClass eq 'even'}">
           <c:set var="evenOddClass" value="odd"/>
         </c:when>
         <c:otherwise>
           <c:set var="evenOddClass" value="even"/>
         </c:otherwise>
       </c:choose>  
     </c:if>
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
   
   <krad:attributeBuilder component="${item}"/>
     
   <%-- render cell and item template --%>
   <c:if test="${renderHeaderColumns}">
      <th width="${cellWidth}" colspan="${item.colSpan}" rowspan="${item.rowSpan}" ${style} ${class}>
       <krad:template component="${item}"/>
      </th>  
   </c:if>
   <c:if test="${!renderHeaderColumns}">
     <td width="${cellWidth}" colspan="${item.colSpan}" rowspan="${item.rowSpan}"
         ${style} ${class} ${evenOddClass}">
       <krad:template component="${item}"/>
     </td>
   </c:if>
     
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