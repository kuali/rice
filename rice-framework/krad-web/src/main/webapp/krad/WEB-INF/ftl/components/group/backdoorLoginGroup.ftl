<#--

    Copyright 2005-2018 The Kuali Foundation

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
    Backdoor login group
 -->

<#macro uif_backdoorLoginGroup group>

    <form id="backdoorForm" ${krad.attrBuild(group)} ${group.simpleDataAttributes}  method="post" action="${group.formPostUrl}">

        <#if group.items?has_content>
            <#-- invoke layout manager -->
            <#local templateName=".main.${group.layoutManager.templateName}"/>
            <#local templateParms="items=group.items manager=group.layoutManager container=group"/>
            <#dyncall templateName templateParms/>
        </#if>

        <@krad.csrfToken/>

    </form>

</#macro>
