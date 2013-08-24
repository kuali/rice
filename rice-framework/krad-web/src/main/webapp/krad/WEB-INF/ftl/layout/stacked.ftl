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
    Stacked Layout Manager:

 -->

<#macro uif_stacked items manager container>

    <#if manager.styleClassesAsString?has_content>
        <#local styleClass="class=\"${manager.styleClassesAsString}\""/>
    </#if>

    <#if manager.style?has_content>
        <#local style="style=\"${manager.style}\""/>
    </#if>

    <#if manager.pagerWidget?has_content && container.useServerPaging>
        <@krad.template component=manager.pagerWidget parent=container/>
    </#if>

    <div id="${manager.id}" ${style!} ${styleClass!}>

    <#-- use wrapper group layout if defined, else default to vertical box -->
            <#if manager.wrapperGroup??>
        <@krad.template component=manager.wrapperGroup/>
    <#else>
        <#list manager.stackedGroups as item>
            <@krad.template component=item/>
        </#list>
    </#if>

    </div>

    <#if manager.pagerWidget?has_content && container.useServerPaging>
        <@krad.template component=manager.pagerWidget parent=container/>
    </#if>

</#macro>