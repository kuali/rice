<#--

    Copyright 2005-2014 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<#--
    items=TableLayoutManager.getAllRowFields() returns List<Field>
    numberOfColumns=GridLayoutManager.getNumberOfColumns()
 -->

<#macro grid items rowCssClasses=[] rowDataAttributes=[] numberOfColumns=2 renderFirstRowHeader=false renderHeaderRow=false applyAlternatingRowStyles=false
applyDefaultCellWidths=true renderRowFirstCellHeader=false renderAlternatingHeaderColumns=false>

    <#if numberOfColumns == 0>
        <#return/>
    </#if>

<#--
rowCssClasses<br>
<#list rowCssClasses as x>
  ${x_index} => ${x}<br>
</#list>
rowDataAttributes<br>
<#list rowDataAttributes as x>
  ${x_index} => ${x}<br>
</#list>
 -->

    <#local defaultCellWidth=100/numberOfColumns/>

    <#local colCount=0/>
    <#local itemIndex=0/>
    <#local carryOverColCount=0/>
    <#local tmpCarryOverColCount=0/>
    <#local rowCount=0/>
    <#local indexCount= 1/>
    <#local splitter = ";"/>
    <#local columnArray=[]/>
    <#local columnLoopArray=""/>
    <#local hasRowSpan=false/>
    <#local hasColSpan=false/>

    <#local firstRow=true/>

    <#list 1..numberOfColumns as i>
        <#local columnArray = columnArray + [1] />
    </#list>

    <#local loopCounter = 0/>

    <#list 0..100000 as i>
        <#if (loopCounter >= items?size)>
           <#break>
        </#if>

        <#local item = items[loopCounter] />
        <#local loopCounter = loopCounter + 1/>
        <#local columnIndex = (colCount % numberOfColumns)/>
        <#local colCount=colCount + 1/>

        <#-- begin table row -->
        <#if (colCount == 1) || (numberOfColumns == 1) || (colCount % numberOfColumns == 1)>
            <#if applyAlternatingRowStyles>
                <#if !evenOddClass?? || evenOddClass == "even">
                    <#local eventOddClass="odd"/>
                <#else>
                    <#local eventOddClass="even"/>
                </#if>
            </#if>

            <#local trClasses="${evenOddClass!} ${rowCssClasses[rowCount]!}"/>
            <#local trDataAttributes="${rowDataAttributes[rowCount]!}"/>
            <#if trClasses?trim?has_content>
                <tr class="${trClasses?trim}" ${trDataAttributes?trim}>
            <#else>
                <tr ${trDataAttributes?trim}>
            </#if>

            <#-- if alternating header columns, force first cell of row to be header -->
            <#local renderAlternateHeader=renderAlternatingHeaderColumns/>

            <#-- if render first cell of each row as header, set cell to be rendered as header -->
            <#local renderFirstCellHeader=renderRowFirstCellHeader/>

            <#local rowCount=rowCount + 1/>
        </#if>

        <#-- determine cell width by using default or configured width and round off to two decimal places-->
        <#if cellWidth?has_content>
            <#local cellWidth="width=\"${item.cellWidth}\""/>
        </#if>

        <#local singleCellRow=(numberOfColumns == 1) || (item.colSpan == numberOfColumns)/>
        <#local renderHeaderColumn=renderHeaderRow || (renderFirstRowHeader && firstRow) || ((renderFirstCellHeader || renderAlternateHeader) && !singleCellRow)/>

        <#-- build cells for row if value @ columnArray itemIndex = 1 -->
        <#local index = columnArray[columnIndex]?number />

        <#local cellClassAttr=""/>
        <#if item.wrapperCssClassesAsString?has_content>
            <#local cellClassAttr="class=\"${item.wrapperCssClassesAsString}\""/>
        </#if>

        <#local cellStyleAttr=""/>
        <#if item.wrapperStyle?has_content>
            <#local cellStyleAttr="style=\"${item.wrapperStyle}\""/>
        </#if>

        <#if (index == 1)>

            <#if item.colSpan != 1 || hasColSpan>
                <#local colSpan="colspan=\"${item.colSpan}\""/>
                <#local hasColSpan=true/>
            </#if>

            <#if item.rowSpan != 1 || hasRowSpan>
                <#local rowSpan="rowspan=\"${item.rowSpan}\""/>
                <#local hasRowSpan=true/>
            </#if>

            <#if renderHeaderColumn>
                <#if renderHeaderRow || (renderFirstRowHeader && firstRow)>
                  <#local headerScope="col"/>
                <#else>
                  <#local headerScope="row"/>
                </#if>

                <th scope="${headerScope}" ${cellWidth!} ${colSpan!}
                    ${rowSpan!} ${cellClassAttr!} ${cellStyleAttr!}><@template component=item/></th>
            <#else>
                <td ${cellWidth!} ${colSpan!}
                    ${rowSpan!} ${cellClassAttr!} ${cellStyleAttr!}><@template component=item/></td>
            </#if>

            <#local columnLoopArray = columnLoopArray + item.rowSpan + splitter />
            <#local colCount=colCount + item.colSpan - 1/>

            <#--skip the number of columns if colspan more than 1 and append the rowspan-->
            <#if (item.colSpan > 1)>
                <#list 1..item.colSpan - 1 as j>
                    <#local jValue = (columnArray[columnIndex + j]?number)/>
                    <#if (jValue > 1)>
                        <#local jValue = (jValue -1)/>
                    </#if>

                    <#local columnLoopArray = columnLoopArray + jValue + splitter />
                </#list>
            </#if>
        <#else>
            <#local loopCounter = (loopCounter - 1)/>
            <#local columnLoopArray = columnLoopArray + (index - 1) + splitter />
        </#if>

        <#-- flip alternating flags -->
        <#if renderAlternatingHeaderColumns>
            <#local renderAlternateHeader=!renderAlternateHeader/>
        </#if>

        <#if renderRowFirstCellHeader>
            <#local renderFirstCellHeader=false/>
        </#if>

        <#-- end table row -->
        <#if (colCount % numberOfColumns) == 0>
           </tr>

           <#local firstRow=false/>

           <#local columnArray = columnLoopArray?split(splitter) />
           <#local columnLoopArray=""/>
        </#if>

    </#list>
</#macro>