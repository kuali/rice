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
<#macro uif_label element>

    <#if element.labelText?has_content>
        <#local label="${element.labelText}"/>
        <#local colon=""/>

        <#if element.renderColon>
            <#local colon=":"/>
        </#if>

        <#if element.labelForComponentId?has_content>
            <#local for='for="${element.labelForComponentId!}"'>
        </#if>

        <@compress single_line=true>
            <label id="${element.id}" ${for!} ${krad.attrBuild(element)}
                ${element.simpleDataAttributes!}>

                <#if element.richLabelMessage?has_content>
                    <@krad.template component=element.richLabelMessage/>${colon}
                <#else>
                    ${label}${colon}
                </#if>

                <#-- required indicator -->
                <#if element.requiredIndicator?? && element.renderRequiredIndicator>
                    <span class="uif-requiredMessage">${element.requiredIndicator!}</span>
                </#if>

            </label>
        </@compress>
    </#if>
</#macro>