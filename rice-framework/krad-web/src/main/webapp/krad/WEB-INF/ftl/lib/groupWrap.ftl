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
<#macro groupWrap group>

    <@div component=group>

        <@template component=group.header/>

        <#if !group.disclosure?has_content || !group.disclosure.ajaxRetrievalWhenOpened
            || (group.disclosure.render && group.disclosure.ajaxRetrievalWhenOpened && group.disclosure.defaultOpen)>

            <#if group.disclosure?has_content && group.disclosure.render>
                <div id="${group.id}_disclosureContent" data-role="disclosureContent"
                     data-open="${group.disclosure.defaultOpen?string}" class="uif-disclosureContent">
            </#if>

            <@template component=group.validationMessages/>
            <@template component=group.instructionalMessage/>

            <#nested/>

            <@template component=group.footer/>

            <#if group.disclosure?has_content && group.disclosure.render>
                </div>
            </#if>

        <#else>
            <div id="${group.id}_disclosureContent" data-role="placeholder"> Loading... </div>
        </#if>

        <#if group.disclosure?has_content && group.disclosure.render>
            <@template component=group.disclosure parent=group/>
        </#if>

    </@div>

</#macro>