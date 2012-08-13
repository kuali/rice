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
<#include "libInclude.ftl" parse=true/>

<#compress>

    <#if KualiForm.ajaxRequest>
        <#if KualiForm.ajaxReturnType == "update-view">
            <div data-handler="update-view">
                <#include "fullView.ftl"  parse=true/>
            </div>

        <#elseif KualiForm.ajaxReturnType == "update-component">
            <div data-handler="update-component" data-updateComponentId="${Component.id!}">
                <#include "updateComponent.ftl"  parse=true/>
            </div>

        <#elseif KualiForm.ajaxReturnType == "update-page">
            <div data-handler="update-page">
                <#include "updatePage.ftl"  parse=true/>
            </div>

       <#elseif KualiForm.ajaxReturnType == "redirect">
            <div data-handler="redirect">
                <#include "redirect.ftl"  parse=true/>
            </div>

        <#elseif KualiForm.ajaxReturnType == "display-lightbox">
            <div data-handler="display-lightbox">
                <#include "updateComponent.ftl"  parse=true/>
            </div>
        </#if>
    <#else>
        <#include "fullView.ftl" parse=true/>
    </#if>

</#compress>