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
    Renders a step progress bar
 -->
<#macro uif_stepProgressBar element>
    <#include 'progressBar.ftl' parse=true/>

    <#if element.verticalHeight?has_content>
        <#local vHeight="style='height: ${element.verticalHeight}px;'"/>
    </#if>

    <@krad.div component=element>
        <#-- when not vertical put the step label text first, otherwise after -->
        <#if !element.vertical>
            <div class="progress-details" ${vHeight!}>
                <#list element.segmentSizes as size>
                        <div style="${size!};" class="${element.stepLabelClasses[size_index]!}">
                            <span class="sr-only">${element.accessibilityText[size_index]!}</span>
                            ${element.stepCollection[size_index]!}
                        </div>
                </#list>
            </div>
        </#if>

        <div class="progress" ${vHeight!}>
            <@uif_progressBar element=element renderOwnDiv=false/>
        </div>

        <#if element.vertical>
            <div class="progress-details" ${vHeight!}>
                <#list element.segmentSizes as size>
                        <div style="${size!};" class="${element.stepLabelClasses[size_index]!}">
                            <span class="sr-only">${element.accessibilityText[size_index]!}</span>
                            <span>${element.stepCollection[size_index]!}</span>
                        </div>
                </#list>
            </div>
        </#if>
    </@krad.div>
</#macro>