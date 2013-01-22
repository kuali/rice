<#--

    Copyright 2005-2013 The Kuali Foundation

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
    Table Layout Manager:

      Works on a collection group to lay out the items as a table.
 -->

<#macro uif_table items manager container>

    <#if manager.styleClassesAsString?has_content>
        <#local styleClass="class=\"${manager.styleClassesAsString}\""/>
    </#if>

    <#if manager.style?has_content>
        <#local style="style=\"${manager.style}\""/>
    </#if>

    <#if manager.separateAddLine>
        <@krad.template component=manager.addLineGroup/>
    </#if>

    <#if manager.dataFields?? && (manager.dataFields?size gt 0)>

    <#-- action button for opening and closing all details -->
    <#if manager.showToggleAllDetails>
        <@krad.template component=manager.toggleAllDetailsAction/>
    </#if>

    <table id="${manager.id}" ${style!} ${styleClass!}>

        <#if manager.headerLabels?? && (manager.headerLabels?size gt 0)>
            <thead>
                <@krad.grid items=manager.headerLabels numberOfColumns=manager.numberOfColumns
                renderHeaderRow=true renderAlternatingHeaderColumns=false
                applyDefaultCellWidths=manager.applyDefaultCellWidths/>
            </thead>
        </#if>

        <tbody>
            <@krad.grid items=manager.dataFields numberOfColumns=manager.numberOfColumns
            applyAlternatingRowStyles=manager.applyAlternatingRowStyles
            applyDefaultCellWidths=manager.applyDefaultCellWidths
            renderAlternatingHeaderColumns=false
            rowCssClasses=manager.rowCssClasses/>
        </tbody>

        <#if manager.footerCalculationComponents?has_content>
            <tfoot>
            <tr>
                <#list manager.footerCalculationComponents as component>
                    <th rowspan="1" colspan="1">
                        <#if component??>
                            <@krad.template component=component/>
                        </#if>
                    </th>
                </#list>
            </tr>
            </tfoot>
        </#if>
    </table>

    <#-- invoke table tools widget -->
    <@krad.template component=manager.richTable componentId="${manager.id}"/>

    </#if>

</#macro>