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

    <#local defaultCellWidth="${100/numberOfColumns}"/>

    <#local colCount=0/>
    <#local carryOverColCount=0/>
    <#local tmpCarryOverColCount=0/>

    <#list items as item>
        <#local colCount=colCount + 1/>
        <#local firstRow=(item_index == 0)/>

        <#-- begin table row -->
        <#if (colCount == 1) || (numberOfColumns == 1) || (colCount % numberOfColumns == 1)>
            <#if applyAlternatingRowStyles>
                <#if evenOddClass == "even">
                    <#local eventOddClass="odd"/>
                <#else>
                    <#local eventOddClass="even"/>
                </#if>
            </#if>


            <#if firstRow && firstLineStyle?has_content>
              <tr class="${firstLineStyle}">
            <#else>
              <tr class="${evenOddClass}">
            </#if>

            <#-- if alternating header columns, force first cell of row to be header -->
            <#if renderAlternatingHeaderColumns>
                <#local renderAlternateHeader=true/>
            </#if>

            <#-- if render first cell of each row as header, set cell to be rendered as header -->
            <#if renderRowFirstCellHeader>
                <#local renderFirstCellHeader=true/>
            </#if>
        </#if>

        <#-- build cells for row -->

        <#-- skip column positions from previous rowspan -->
        <#list 1..carryOverColCount as i>
            <#local colCount=colCount + 1/>
            <#local carryOverColCount=carryOverColCount - 1/>

            <#if colCount % numberOfColumns == 0>
              </tr><tr>
            </#if>
        </#list>

        <#-- determine cell width by using default or configured width -->
        <#if item.width?has_content>
            <#local cellWidth=item.width/>
        <#elseif applyDefaultCellWidths>
            <#local cellWidth="${defaultCellWidth * item.colSpan}%"/>
        </#if>

        <#if cellWidth?has_content>
            <#local cellWidth="width=\"${cellWidth}\""/>
        </#if>
    
        <#local singleCellRow=(numberOfColumns == 1) || (item.colSpan == numberOfColumns)/>
        <#local renderHeaderColumn=renderHeaderRow || (renderFirstRowHeader && firstRow)
                 || ((renderFirstCellHeader || renderAlternateHeader) && !singleCellRow)/>

        <#if renderHeaderColumn>
            <#if renderHeaderRow || (renderFirstRowHeader && firstRow)>
              <#local headerScope="col"/>
            <#else>
              <#local headerScope="row"/>
            </#if>

            <th scope="${headerScope}" ${cellWidth} colspan="${item.colSpan}"
                rowspan="${item.rowSpan}" ${attrBuild(component)}>
                <@template component=item/>
            </th>
        <#else>
            <td role="presentation" ${cellWidth} colspan="${item.colSpan}"
                rowspan="${item.rowSpan}" ${attrBuild(component)}>
                <@template component=item/>
            </td>
        </#if>

        <#-- flip alternating flags -->
        <#if renderAlternatingHeaderColumns>
            <#local renderAlternateHeader=!renderAlternateHeader/>
        </#if>

        <#if renderRowFirstCellHeader>
            <#local renderFirstCellHeader=false/>
        </#if>

        <#local colCount=colCount + item.colSpan - 1/>

        <#-- set carry over count to hold positions for fields that span multiple rows -->
        <#local tmpCarryOverColCount=tmpCarryOverColCount + item.rowSpan - 1/>

        <#-- end table row -->
        <#if !item_has_next || (colCount % numberOfColumns) == 0>
           </tr>

           <#local carryOverColCount=carryOverColCount + tmpCarryOverColCount/>
           <#local tmpCarryOverColCount=0/>
        </#if>
    </#list>

</#macro>