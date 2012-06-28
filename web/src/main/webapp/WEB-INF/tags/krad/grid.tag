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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<%@ attribute name="items" required="true"
              description="List of fields to display within the grid"
              type="java.util.List" %>
<%@ attribute name="numberOfColumns" required="false"
              description="Number of columns the grid should contain, defaults to 2" %>
<%@ attribute name="renderFirstRowHeader" required="false"
              description="Boolean that indicates whether the first row of items should be rendered as th cells, defaults to false" %>
<%@ attribute name="renderHeaderRow" required="false"
              description="Boolean that indicates whether the row columns should rendered as th or td cell, defaults to false" %>
<%@ attribute name="applyAlternatingRowStyles" required="false"
              description="Boolean that indicates whether the even odd style classes should be applied to the rows, defaults to false" %>
<%@ attribute name="applyDefaultCellWidths" required="false"
              description="Boolean that indicates whether default widths should be applied to the cells, defaults to true" %>
<%@ attribute name="renderRowFirstCellHeader" required="false"
              description="Boolean that indicates whether the first cell of each row should be rendered as a th, defaults to false" %>
<%@ attribute name="renderAlternatingHeaderColumns" required="false"
              description="Boolean that indicates whether alternating header columns should be rendered, defaults to false" %>
<%@ attribute name="rowCssClasses" required="false"
              type="java.util.List" description="Styles for each row" %>

<c:if test="${empty numberOfColumns}">
  <c:set var="numberOfColumns" value="2"/>
</c:if>

<c:if test="${empty renderFirstRowHeader}">
  <c:set var="renderFirstRowHeader" value="false"/>
</c:if>

<c:if test="${empty renderHeaderRow}">
  <c:set var="renderHeaderRow" value="false"/>
</c:if>

<c:if test="${renderHeaderRow}">
  <c:set var="headerScope" value="col"/>
</c:if>

<c:if test="${empty applyAlternatingRowStyles}">
  <c:set var="applyAlternatingRowStyles" value="false"/>
</c:if>

<c:if test="${empty applyDefaultCellWidths}">
  <c:set var="applyDefaultCellWidths" value="true"/>
</c:if>

<c:if test="${empty renderRowFirstCellHeader}">
  <c:set var="renderRowFirstCellHeader" value="false"/>
</c:if>

<c:if test="${empty renderAlternatingHeaderColumns}">
  <c:set var="renderAlternatingHeaderColumns" value="false"/>
</c:if>

<c:set var="defaultCellWidth" value="${100/numberOfColumns}"/>

<c:set var="colCount" value="0"/>
<c:set var="carryOverColCount" value="0"/>
<c:set var="tmpCarryOverColCount" value="0"/>
<c:set var="rowCount" value="0"/>

<c:forEach items="${items}" var="item" varStatus="itemVarStatus">
  <c:set var="colCount" value="${colCount + 1}"/>
  <c:set var="actualColCount" value="${actualColCount + 1}"/>

  <%-- begin table row --%>
  <c:if test="${(colCount == 1) || (numberOfColumns == 1) || (colCount % numberOfColumns == 1)}">
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

    <tr class="${evenOddClass} ${rowCssClasses[rowCount]}">

    <%-- if alternating header columns, force first cell of row to be header --%>
    <c:if test="${renderAlternatingHeaderColumns}">
      <c:set var="renderAlternateHeader" value="true"/>
    </c:if>

    <%-- if render first cell of each row as header, set cell to be rendered as header --%>
    <c:if test="${renderRowFirstCellHeader}">
      <c:set var="renderFirstCellHeader" value="true"/>
    </c:if>

    <c:set var="rowCount" value="${rowCount + 1}"/>

    <c:set var="firstRow" value="${itemVarStatus.first}"/>

  </c:if>

  <%-- skip column positions from previous rowspan --%>
  <c:forEach var="i" begin="1" end="${carryOverColCount}" step="1" varStatus="status">
    <c:set var="colCount" value="${colCount + 1}"/>
    <c:set var="carryOverColCount" value="${carryOverColCount - 1}"/>

    <c:if test="${colCount % numberOfColumns == 0}">
      </tr><tr>
    </c:if>
  </c:forEach>

  <%-- determine cell width by using default or configured width --%>
  <c:set var="cellWidth" value=""/>
  <c:if test="${applyDefaultCellWidths}">
    <c:set var="cellWidth" value="${defaultCellWidth * item.colSpan}%"/>
  </c:if>

  <c:if test="${!empty item.width}">
    <c:set var="cellWidth" value="${item.width}"/>
  </c:if>

  <c:if test="${!empty cellWidth}">
    <c:set var="cellWidth" value="width=\"${cellWidth}\""/>
  </c:if>

  <%-- determine if we only have one cell for a non header row, in which case we don't want to render a th --%>
  <c:set var="singleCellOnly" value="false"/>
  <c:if test="${(numberOfColumns == 1) || (item.colSpan == numberOfColumns)}">
    <c:set var="singleCellOnly" value="true"/>
  </c:if>

  <c:set var="renderHeaderColumn" value="false"/>
  <c:if test="${renderHeaderRow || (renderFirstRowHeader && firstRow) ||
              ((renderFirstCellHeader || renderAlternateHeader) && !singleCellOnly)}">
    <c:set var="renderHeaderColumn" value="true"/>

    <c:choose>
      <c:when test="${renderHeaderRow || (renderFirstRowHeader && firstRow)}">
        <c:set var="headerScope" value="col"/>
      </c:when>
      <c:otherwise>
        <c:set var="headerScope" value="row"/>
      </c:otherwise>
    </c:choose>
  </c:if>

  <krad:attributeBuilder component="${item}"/>

  <%-- render cell and item template --%>
  <c:choose>
    <c:when test="${renderHeaderColumn}">
      <th scope="${headerScope}" ${cellWidth} colspan="${item.colSpan}"
          rowspan="${item.rowSpan}" ${style} ${title}>
        <krad:template component="${item}"/>
      </th>
    </c:when>
    <c:otherwise>
      <td role="presentation" ${cellWidth} colspan="${item.colSpan}" rowspan="${item.rowSpan}"
        ${style} ${title}>
        <krad:template component="${item}"/>
      </td>
    </c:otherwise>
  </c:choose>

  <%-- if alternating headers flip header flag --%>
  <c:if test="${renderAlternatingHeaderColumns}">
    <c:set var="renderAlternateHeader" value="${!renderAlternateHeader}"/>
  </c:if>

  <c:if test="${renderRowFirstCellHeader}">
    <c:set var="renderFirstCellHeader" value="false"/>
  </c:if>

  <%-- handle colspan for the count --%>
  <c:set var="colCount" value="${colCount + item.colSpan - 1}"/>

  <%-- set carry over count to hold positions for fields that span multiple rows --%>
  <c:set var="tmpCarryOverColCount" value="${tmpCarryOverColCount + item.rowSpan - 1}"/>

  <%-- end table row --%>
  <c:if test="${itemVarStatus.last || (colCount % numberOfColumns == 0)}">
    <c:set var="actualColCount" value="0"/>
    </tr>
    <c:set var="carryOverColCount" value="${carryOverColCount + tmpCarryOverColCount}"/>
    <c:set var="tmpCarryOverColCount" value="0"/>
  </c:if>
</c:forEach>