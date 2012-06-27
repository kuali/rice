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

<#macro uif_collectionGroup group>

    <@krad.groupWrap group=group>

        <#-- render collection quickfinder -->
        <@krad.template component=group.collectionLookup componentId="${group.id}"/>

        <#if group.renderAddBlankLineButton && (group.addLinePlacement == 'TOP')>
            <@krad.template component=group.addBlankLineAction/>
        </#if>

        <#if group.addViaLightBox && (group.addLinePlacement == 'TOP')>
            <@krad.template component=group.addViaLightBoxAction/>
        </#if>

        <#-- invoke layout manager -->
        <#include "${group.layoutManager.template}" parse=true/>

        <#local macroInvokeSrc="<" + "@${group.layoutManager.templateName} items=group.items"/>
        <#local macroInvokeSrc="${macroInvokeSrc} manager=group.layoutManager container=group/>"/>
        <#local macroInvoke = macroInvokeSrc?interpret>

        <@macroInvoke />

        <#if group.renderAddBlankLineButton && (group.addLinePlacement == 'BOTTOM')>
            <@krad.template component=group.addBlankLineAction/>
        </#if>

        <#if group.addViaLightBox && (group.addLinePlacement == 'BOTTOM')>
            <@krad.template component=group.addViaLightBoxAction/>
        </#if>

    </@krad.groupWrap>

</#macro>
