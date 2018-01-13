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
<#macro testBanner>
    <#if UserSession?? && UserSession.displayTestBanner >
    <div class="testBanner">
        <img src="${request.contextPath}/kr/static/images/alert.png" alt="Alert" />
        <#if UserSession.currentEnvironment?upper_case == 'STG'>
            <#assign envDisplay="Staging" />
        <#elseif UserSession.currentEnvironment?upper_case == 'DEV'>
            <#assign envDisplay="Development" />
        <#else>
            <#assign envDisplay= "Test ${UserSession.currentEnvironment?upper_case}" />
        </#if>
        <#assign arguments = ["${envDisplay}"]>
        <@spring.messageArgs "test.banner.message" arguments />
    </div>
    </#if>
</#macro>