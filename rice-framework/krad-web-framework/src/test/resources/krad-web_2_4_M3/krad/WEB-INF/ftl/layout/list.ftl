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
<#macro uif_list items manager container>

    <#local listType="ul">
    <#if manager.orderedList>
        <#local listType="ol">
    </#if>

    <#if manager.styleClassesAsString?has_content>
        <#local styleClass="class=\"${manager.styleClassesAsString}\""/>
    </#if>

    <#if manager.style?has_content>
        <#local style="style=\"${manager.style}\""/>
    </#if>

    <#local inList=false/>

    <#list items as item>
        <#-- if the item is a header, render outside the list -->
        <#if HelperMethods.isHeader(item.getClass())>
            <#if inList>
                </${listType}>
                <#local inList=false/>
            </#if>

            <@krad.template component=item/>
        <#else>
            <#if !inList>
                <${listType} ${style!} ${styleClass!}>
                <#local inList=true/>
            </#if>

            <#-- if item is list aware, pull its classes and style to the list element -->
            <#if HelperMethods.isListAware(item.getClass())>
                <#if item.styleClassesAsString?has_content>
                     <#local itemStyleClass="class=\"${item.styleClassesAsString}\""/>
                </#if>

                <#if item.style?has_content>
                    <#local itemStyle="style=\"${item.style}\""/>
                </#if>
            </#if>

            <li ${itemStyle!} ${itemStyleClass!}>
                <@krad.template component=item/>
            </li>
        </#if>
    </#list>

    <#if inList>
        </${listType}>
    </#if>

</#macro>