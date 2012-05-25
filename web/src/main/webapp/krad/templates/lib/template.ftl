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

<#macro template component body componentUpdate tmplParms...>

    <#if !component?has_content>
        <#return>
    </#if>

    <#-- check to see if the component should render, if this has progressiveDisclosure and not getting disclosed via ajax
    still render, but render in a hidden container -->
    <#if component.render || (component.progressiveRender?has_content && !component.progressiveRenderViaAJAX
        && !component.progressiveRenderAndRefresh)>

        <#if component.selfRendered>
        ${component.renderedHtmlOutput}
        <#else>
            <#include "${component.template}" parse=true/>
        </#if>

        <#-- write data attributes -->
        <@script component=component role="dataScript" value="${component.complexDataAttributesJs}"/>

        <#-- generate event code for component -->
        <@eventScript component=component/>
    </#if>

    <#if componentUpdate>
        <#return>
    </#if>

    <#-- setup progressive render -->
    <#if component.progressRender?has_content>
    <#-- for progressive rendering requiring an ajax call, put in place holder div -->
        <#if !component.render && (component.progressiveRenderViaAJAX || component.progressiveRenderAndRefresh)>
        <span id="${component.id}" data-role="placeholder" class="uif-placeholder"></span>
        </#if>

        <#-- setup progressive handlers for each control which may satisfy a disclosure condition -->
        <#list component.progressiveDisclosureControlNames as cName>
            <@script value="var condition = function(){return (${component.progressiveDisclosureConditionJs});};
                  setupProgressiveCheck(&quot;${cName}&quot;, '${component.id}', '${component.baseId}', condition,
                  ${component.progressiveRenderAndRefresh}, '${component.methodToCallOnRefresh}');"/>
        </#list>
        <@script value="hiddenInputValidationToggle('${component.id}');"/>
    </#if>

    <#-- conditional Refresh setup -->
    <#if component.conditionalRefresh?has_content>
        <#list component.conditionalRefreshControlNames as cName>
            <@script value="var condition = function(){return (${component.conditionalRefreshConditionJs});};
                 setupRefreshCheck(&quot;${cName}&quot;, '${component.id}', '${component.baseId}', condition,
                 '${component.methodToCallOnRefresh}');"/>
        </#list>
    </#if>

    <#-- refresh when changed setup -->
    <#list component.refreshWhenChangedPropertyNames as cName>
        <@script value="setupOnChangeRefresh(&quot;${cName}&quot;, '${component.id}', '${component.baseId}',
        '${component.methodToCallOnRefresh}');"/>
    </#list>

    <#-- generate tooltip for component -->
    <@tooltip component=component/>

</#macro>