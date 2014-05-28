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
<html>
    <#-- set the focusId data attribute if set on the form -->
    <#if KualiForm.focusId?has_content>
        <#assign focusIdDataAttribute="data-focusId='${KualiForm.focusId}'"/>
    <#else>
        <#if KualiForm.view.currentPage.autoFocus>
            <#assign focusIdDataAttribute="data-focusId='FIRST'"/>
        <#else>
            <#assign focusIdDataAttribute=""/>
        </#if>
    </#if>

    <#-- set the jumpToId data attribute if set on the form -->
    <#if KualiForm.jumpToId?has_content>
        <#assign jumpToIdDataAttribute="data-jumpToId='${KualiForm.jumpToId}'"/>
    <#else>
        <#assign jumpToIdDataAttribute=""/>
    </#if>

    <#-- set the jumpToName data attribute if set on the form -->
    <#if KualiForm.jumpToName?has_content>
        <#assign jumpToNameDataAttribute="data-jumpToName='${KualiForm.jumpToName}'"/>
    <#else>
        <#assign jumpToNameDataAttribute=""/>
    </#if>
    <#-- now render the updated component (or page) wrapped in an update div. Add the data attributes as part of the div -->
    <div id="page_update" ${focusIdDataAttribute} ${jumpToIdDataAttribute} ${jumpToNameDataAttribute}>
        <#list view.viewTemplates as viewTemplate>
            <#include "${viewTemplate}" parse=true/>
        </#list>

        <#-- rerun view pre-load script to get new state variables for component -->
        <@krad.script value="${view.preLoadScript!}" component=KualiForm.updateComponent/>

        <@krad.template componentUpdate=true component=KualiForm.updateComponent/>

        <#-- show added growls -->
        <@krad.script value="${KualiForm.growlScript!}" component=KualiForm.updateComponent/>
    </div>
</html>
