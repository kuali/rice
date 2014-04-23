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
<#macro html view>

<!DOCTYPE HTML>
<html lang="en">
<head>

    <meta charset="UTF-8">

    <#list view.additionalMetaTags as tag>
        <#if tag.http_equiv?has_content>
            <meta http-equiv="${tag.http_equiv}" content="${tag.content}"/>
        <#else>
            <meta name="${tag.name}" content="${tag.content}"/>
        </#if>

    </#list>

    <title>
        <#if view.applicationTitleText?has_content>
           ${view.applicationTitleText}
        </#if>
    </title>


    <#list view.theme.cssFiles as cssFile>
        <#local relation="stylesheet"/>
        <#if cssFile?ends_with('.less')>
            <#local relation="stylesheet/less"/>
        </#if>

        <#if cssFile?starts_with('http')>
            <link href="${cssFile}" rel="${relation}" type="text/css"/>
        <#else>
            <link href="${request.contextPath}/${cssFile}" rel="${relation}" type="text/css"/>
        </#if>
    </#list>



    <#list view.additionalHeadLinks as headLink>
        <#local relation="stylesheet"/>
        <#local media="all"/>
        <#local type="text/css"/>

        <#if headLink.href?ends_with('.less')>
            <#local relation="stylesheet/less"/>
        </#if>

        <#if headLink.relation?has_content>
            <#local relation="${headLink.relation}"/>
        </#if>

        <#if headLink.type?has_content>
            <#local type="${headLink.type}"/>
        </#if>

        <#if headLink.href?starts_with('http')>
            <#local href="${headLink.href}"/>
        <#else>
            <#local href="${request.contextPath}/${headLink.href}"/>
        </#if>

        <#if headLink.media?has_content>
            <#local media="${headLink.media}"/>
        </#if>

        <#if headLink.includeCondition?has_content>
                <!--[${headLink.includeCondition}]>
            <link rel="${relation}" href="${href}" type="${type}" media="${media}"/>
                <![endif]-->
        <#else>
            <link rel="${relation}" href="${href}" type="${type}" media="${media}"/>
        </#if>


    </#list>

    <#list view.additionalCssFiles as cssFile>
        <#local relation="stylesheet"/>
        <#if cssFile?ends_with('.less')>
            <#local relation="stylesheet/less"/>
        </#if>

        <#if cssFile?starts_with('http')>
            <link href="${cssFile}" rel="${relation}" type="text/css"/>
        <#else>
            <link href="${request.contextPath}/${cssFile}" rel="${relation}" type="text/css"/>
        </#if>
    </#list>

</head>

<body id="Uif-Application" style="display:none;">
    <#nested/>

    <#list view.theme.scriptFiles as javascriptFile>
        <#if javascriptFile?starts_with('http')>
            <script type="text/javascript" src="${javascriptFile}"></script>
        <#else>
            <script type="text/javascript"
                    src="${request.contextPath}/${javascriptFile}"></script>
        </#if>
    </#list>

    <#list view.additionalScriptFiles as scriptFile>
        <#if scriptFile?starts_with('http')>
            <script type="text/javascript" src="${scriptFile}"></script>
        <#else>
            <script type="text/javascript"
                                        src="${request.contextPath}/${scriptFile}"></script>
        </#if>
    </#list>

    <#-- preload script (server variables) -->
    <#if view.preLoadScript?has_content>
        <script type="text/javascript">
            ${view.preLoadScript}
        </script>
    </#if>

    <#-- custom script for the view -->
    <#if view.onLoadScript?has_content>
        <script type="text/javascript">
            jQuery(document).ready(function () {
            ${view.onLoadScript}
            })
        </script>
    </#if>
</body>

</html>

</#macro>