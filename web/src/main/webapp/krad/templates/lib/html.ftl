<#--
  ~ Copyright 2006-2012 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<#macro html>

<!DOCTYPE HTML>
<html lang="en">
<head>

    <#if SESSION_TIMEOUT_WARNING_MILLISECONDS?has_content>
        <script type="text/javascript">
            <!--
            setTimeout("alert('Your session will expire in ${SESSION_TIMEOUT_WARNING_MINUTES} minutes.')",
                    '${SESSION_TIMEOUT_WARNING_MILLISECONDS}');
            // -->
        </script>
    </#if>

    <scriptingVariables/>

    <title>
        <@spring.message "app.title"/>
        :: ${view.headerText}
    </title>

    <#list view.theme.cssFiles as cssFile>
        <#if cssFile?starts_with('http')>
            <link href="${cssFile}" rel="stylesheet" type="text/css"/>
        <#else>
            <link href="${pageContext.request.contextPath}/${cssFile}" rel="stylesheet" type="text/css"/>
        </#if>
    </#list>

    <#list view.additionalCssFiles as cssFile>
        <#if cssFile?starts_with('http')>
            <link href="${cssFile}" rel="stylesheet" type="text/css"/>
        <#else>
            <link href="${pageContext.request.contextPath}/${cssFile}" rel="stylesheet" type="text/css"/>
        </#if>
    </#list>

    <#list view.theme.scriptFiles as javascriptFile>
        <#if javascriptFile?starts_with('http')>
            <script language="JavaScript" type="text/javascript" src="${javascriptFile}"></script>
        <#else>
            <script language="JavaScript" type="text/javascript"
                    src="${pageContext.request.contextPath}/${javascriptFile}"></script>
        </#if>
    </#list>

    <#list view.additionalScriptFiles as scriptFile>
        <#if scriptFile?starts_with('http')>
            <script language="JavaScript" type="text/javascript" src="${scriptFile}"></script>
        <#else>
            <script language="JavaScript" type="text/javascript"
                                        src="${pageContext.request.contextPath}/${scriptFile}"></script>
        </#if>
    </#list>

    <!-- preload script (server variables) -->
    <script type="text/javascript">
        ${view.preLoadScript}
    </script>

    <!-- custom script for the view -->
    <script type="text/javascript">
        jQuery(document).ready(function () {
        ${view.onLoadScript}
        })
    </script>
</head>

<body>
  <#nested/>
</body>

</html>

</#macro>