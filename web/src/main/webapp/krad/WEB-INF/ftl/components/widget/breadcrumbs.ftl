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
<#-- Create the breadcrumbs using the generatedBreadcrumbs from history, note that current
is omitted by default, but the link to it is still present, it can be shown as a clickable
link again through jquery as in setPageBreadcrumb when needed -->

<#macro uif_breadcrumbs widget>

    <#local current=KualiForm.formHistory.generatedCurrentBreadcrumb/>
    <#local crumbs=KualiForm.formHistory.generatedBreadcrumbs/>

    <#if (crumbs?size >= 1) || widget.displayBreadcrumbsWhenOne>
        <label id="breadcrumb_label" class="offScreen">Breadcrumbs</label>

        <span class="${widget.styleClassesAsString!}">
            <ol id="breadcrumbs" role="navigation" aria-labelledby="breadcrumb_label">

                <#list crumbs as crumb>
                    <li><a href="${crumb.url!}">${crumb.title!}</a><span role="presentation"> &raquo; </span></li>
                </#list>

                <span class="kr-current" id="current_breadcrumb_span">${current.title!}</span>
                <a style="display:none;" id="current_breadcrumb_anchor" href="${current.url!}">${current.title!}</a>
            </ol>
        </span>

    </#if>

</#macro>