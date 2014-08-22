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