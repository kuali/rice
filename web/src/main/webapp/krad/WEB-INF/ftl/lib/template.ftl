<#--

    Copyright 2005-2012 The Kuali Foundation

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
<#macro template component=[] body='' componentUpdate=false tmplParms...>
    <#if !(component!?size > 0)>
        <#return>
    </#if>

    <#-- check to see if the component should render, if this has progressiveDisclosure and not getting disclosed via ajax
         still render, but render in a hidden container -->
    <#if component.render || (component.progressiveRender?has_content && !component.progressiveRenderViaAJAX
    && !component.progressiveRenderAndRefresh)>

        <#if component.selfRendered>
            ${component.renderedHtmlOutput}
        <#else>
            <#local macroInvokeSrc="<" + "@.main.${component.templateName} ${component.componentTypeName}=component "/>
            <#list tmplParms?keys as parm>
                <#local macroInvokeSrc="${macroInvokeSrc} ${parm}=tmplParms['${parm}']!"/>
            </#list>

            <#if body?trim?has_content>
                <#local macroInvokeSrc="${macroInvokeSrc} body='${body}'"/>
            </#if>

            <#local macroInvokeSrc="${macroInvokeSrc}/>"/>

            <#local macroInvoke = macroInvokeSrc?interpret>
            <@macroInvoke />
        </#if>

        <#-- write data attributes -->
        <@krad.script component=component role="dataScript" value="${component.complexDataAttributesJs}"/>

        <#-- generate event code for component -->
        <@krad.eventScript component=component/>
    </#if>

    <#if componentUpdate>
        <#return>
    </#if>

    <#-- setup progressive render -->
    <#if component.progressiveRender?has_content>
        <#-- for progressive rendering requiring an ajax call, put in place holder div -->
        <#if !component.render && (component.progressiveRenderViaAJAX || component.progressiveRenderAndRefresh)>
            <span id="${component.id}" data-role="placeholder" class="uif-placeholder"></span>
        </#if>

        <#-- setup progressive handlers for each control which may satisfy a disclosure condition -->
        <#list component.progressiveDisclosureControlNames as cName>
            <@krad.script value="var condition = function(){return (${component.progressiveDisclosureConditionJs});};
                  setupProgressiveCheck('${cName?js_string}', '${component.id}', '${component.baseId}', condition,
                  ${component.progressiveRenderAndRefresh?string}, '${component.methodToCallOnRefresh!}');"/>
        </#list>
        <@script value="hiddenInputValidationToggle('${component.id}');"/>
    </#if>

    <#-- ajax dialog setup -->
    <#if component.progressiveRenderViaAJAX && !(component.progressiveRender!?length > 0)>
        <span id="${component.id}" data-role="placeholder" class="uif-placeholder"></span>
    </#if>

    <#-- conditional Refresh setup -->
    <#if component.conditionalRefresh?has_content>
        <#list component.conditionalRefreshControlNames as cName>
            <@krad.script value="var condition = function(){return (${component.conditionalRefreshConditionJs});};
                 setupRefreshCheck('${cName?js_string}', '${component.id}', condition,
                 '${component.methodToCallOnRefresh!}');"/>
        </#list>
    </#if>

    <#-- refresh when changed setup -->
    <#list component.refreshWhenChangedPropertyNames as cName>
        <@krad.script value="setupOnChangeRefresh('${cName?js_string}', '${component.id}',
        '${component.methodToCallOnRefresh!}');"/>
    </#list>

    <#-- generate tooltip for component -->
    <@krad.tooltip component=component/>

</#macro>