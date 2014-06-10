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
    Renders a progress bar
 -->
<#macro uif_progressBar element renderOwnDiv=true>
    <#-- render's own div when not called by stepProgressBar -->
    <#if renderOwnDiv>
        <@krad.div component=element>
            <#if element.percentComplete?has_content>
                <span class="sr-only">${element.percentComplete!}%</span>
            </#if>

            <#list element.segmentSizes as size>
                    <div style="${size!};" class="${element.segmentClasses[size_index]!}"></div>
            </#list>
        </@krad.div>
    <#else>
        <#if element.percentComplete?has_content>
            <span class="sr-only">${element.percentComplete!}%</span>
        </#if>

        <#list element.segmentSizes as size>
                <div style="${size!};" class="${element.segmentClasses[size_index]!}"></div>
        </#list>
    </#if>
</#macro>