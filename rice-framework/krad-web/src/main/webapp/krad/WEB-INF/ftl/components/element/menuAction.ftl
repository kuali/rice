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
    Renders a menu action item

 -->

<#include "actionLink.ftl" parse=true/>

<#macro uif_menuAction element>

    <#local styleClass="${element.styleClassesAsString}"/>

    <#if element.menuDivider>
        <#local styleClass="${styleClass} divider"/>
    <#elseif element.menuHeader>
        <#local styleClass="${styleClass} dropdown-header"/>
    </#if>

    <#if element.disabled>
        <#local styleClass="${styleClass} disabled"/>
    </#if>

    <#if styleClass?has_content>
        <#local styleClass="class=\"${styleClass}\""/>
    </#if>

    <#if element.style?has_content>
        <#local style="style=\"${element.style}\""/>
    </#if>

    <li ${styleClass!} ${style!}>
        <#if element.menuHeader>
            ${element.actionLabel?html}
        <#elseif !element.menuDivider>
            <@uif_actionLink element=element/>
        </#if>
    </li>

</#macro>