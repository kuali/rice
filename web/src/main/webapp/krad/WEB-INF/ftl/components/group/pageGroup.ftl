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
<#macro uif_pageGroup group>

    <#include "group.ftl" parse=true/>
    <@uif_group group=group/>

    <!-- PAGE RELATED VARS -->
    <#if KualiForm.view.renderForm>

        <@spring.formHiddenInput id="pageId" path="KualiForm.view.currentPageId"/>
        <#if KualiForm.view.currentPage.header??>
            <@spring.formHiddenInput id="pageTitle" path="KualiForm.view.currentPage.header.headerText"/>
        </#if>

        <@spring.formHiddenInput id="historyParameterString" path="KualiForm.formHistory.historyParameterString"/>

    </#if>

</#macro>
