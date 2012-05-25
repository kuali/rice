<#--
  ~ Copyright 2006-2012 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<#macro grid items firstLineStyle numberOfColumns=2 renderFirstRowHeader=false renderHeaderRow=false applyAlternatingRowStyles=false
        applyDefaultCellWidths=true renderRowFirstCellHeader=false renderAlternatingHeaderColumns=false>

    <#if renderHeaderRow>
        <#assign headerScope="col"/>
    </#if>

  <#assign defaultCellWidth="${100/numberOfColumns}"/>


<c:set var="defaultCellWidth" value="${100/numberOfColumns}"/>

<c:set var="colCount" value="0"/>
<c:set var="carryOverColCount" value="0"/>
<c:set var="tmpCarryOverColCount" value="0"/>

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

    <c:choose>
      <c:when test="${itemVarStatus.first}">
        <tr class="${firstLineStyle}">
        <c:set var="firstRow" value="true"/>
      </c:when>
      <c:otherwise>
        <tr class="${evenOddClass}">
        <c:set var="firstRow" value="false"/>
      </c:otherwise>
    </c:choose>

    <%-- if alternating header columns, force first cell of row to be header --%>
    <c:if test="${renderAlternatingHeaderColumns}">
      <c:set var="renderAlternateHeader" value="true"/>
    </c:if>

    <%-- if render first cell of each row as header, set cell to be rendered as header --%>
    <c:if test="${renderRowFirstCellHeader}">
      <c:set var="renderFirstCellHeader" value="true"/>
    </c:if>
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

</#macro>