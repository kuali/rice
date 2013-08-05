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
<#macro template component=[] body='' componentUpdate=false includeSrc=false tmplParms...>
<#-- compress to avoid white space in tags -->
    <#compress>

        <#if !(component!?size > 0)>
            <#return>
        </#if>

    <#-- check to see if the component should render, if this has progressiveDisclosure and not getting disclosed via ajax
         still render, but render in a hidden container -->
        <#if (component.render && !component.retrieveViaAjax)
        || (component.render && component.retrieveViaAjax && componentUpdate)
        || (component.progressiveRender?has_content && !component.progressiveRenderViaAJAX && !component.progressiveRenderAndRefresh)>

            <#if component.preRenderContent?has_content>
            ${component.preRenderContent?html}
            </#if>

            <#if component.selfRendered>
            ${component.renderedHtmlOutput}
            <#-- in progress, string render -->
            <#--<#elseif StringRenderContext.supportsStringRender(component) && !inStringRenderInvocation!false>-->
            <#--<#local stringRenderTemplateHolder=StringRenderContext.getStringRenderTemplateHolder(component)/>-->

            <#--<#if !stringRenderTemplateHolder.stringTemplate?has_content>-->
            <#--<#global inStringRenderInvocation=true/>-->
            <#--<#local componentModel=stringRenderTemplateHolder.componentModel/>-->

            <#--<#local stringTemplate>-->
            <#--<#if includeSrc>-->
            <#--<#include "${component.template}" parse=true/>-->
            <#--</#if>-->

            <#--<#local templateName=".main.${component.templateName}"/>-->

            <#--<#local templateParms="${component.componentTypeName}=component "/>-->
            <#--<#list tmplParms?keys as parm>-->
            <#--<#local templateParms="${templateParms} ${parm}=tmplParms['${parm}']!"/>-->
            <#--</#list>-->

            <#--<#if body?trim?has_content>-->
            <#--<#local templateParms="${templateParms} body='${body}'"/>-->
            <#--</#if>-->

            <#--<#dyncall templateName templateParms/>-->
            <#--</#local>-->

            <#--<#global inStringRenderInvocation=false/>-->

            <#--${stringRenderTemplateHolder.setStringTemplate(stringTemplate)}-->
            <#--</#if>-->

            <#--${stringRenderTemplateHolder.render(component)}-->
            <#else>
                <#if includeSrc>
                    <#include "${component.template}" parse=true/>
                </#if>

                <#local templateName=".main.${component.templateName}"/>

                <#local templateParms="${component.componentTypeName}=component "/>
                <#list tmplParms?keys as parm>
                    <#local templateParms="${templateParms} ${parm}=tmplParms['${parm}']!"/>
                </#list>

                <#if body?trim?has_content>
                    <#local templateParms="${templateParms} body='${body}'"/>
                </#if>

                <#dyncall templateName templateParms/>
            </#if>

        <#-- generate event code for component -->
            <@krad.script component=component value="${component.eventHandlerScript}" />

            <#if component.postRenderContent?has_content>
            ${component.postRenderContent?html}
            </#if>
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

    <#-- alternate ajax placeholder setup -->
        <#if (component.progressiveRenderViaAJAX && !(component.progressiveRender!?length > 0))
        || (!component.render && (component.disclosedByAction || component.refreshedByAction))
        || (component.retrieveViaAjax)>
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

    </#compress>
</#macro>