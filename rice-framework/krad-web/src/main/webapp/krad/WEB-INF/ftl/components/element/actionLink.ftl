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
    HTML Link to Submit Form Via JavaScript

 -->

<#macro uif_actionLink element>

    <#if element.navigateToPageId?has_content>
        <#local pound="#"/>
        <#local href="href=\"${pound}${element.navigateToPageId}\""/>
        <#local name="name=\"${element.navigateToPageId}\""/>
    </#if>

    <#if element.skipInTabOrder>
        <#local tabindex="tabindex=-1"/>
    <#else>
        <#local tabindex="tabindex=0"/>
    </#if>

    <#if !element.actionLabel??>
        <#local imageRole="role='presentation'"/>
    </#if>

    <#if element.actionImage??>
        <#if element.actionImage.height?has_content>
            <#local height="height='${element.actionImage.height}'"/>
        </#if>

        <#if element.actionImage.width?has_content>
            <#local width="width='${element.actionImage.width}'"/>
        </#if>
    </#if>

    <#local imagePlacement="${element.actionImagePlacement}"/>

    <#local actionLabel="${element.actionLabel!}"/>
    <#if element.renderInnerTextSpan>
        <#local actionLabel="<span class=\"uif-innerText\">${element.actionLabel!}</span>"/>
    </#if>

    <#if element.iconClass??>
        <#if element.actionIconPlacement == 'ICON_ONLY'>
            <#-- no span necessary, icon class is on the link -->
            <a id="${element.id}" ${href!} ${name!} ${krad.attrBuild(element)}
            ${tabindex} ${element.simpleDataAttributes!}></a>
        <#elseif element.actionIconPlacement == 'LEFT'>
        <a id="${element.id}" ${href!} ${name!} ${krad.attrBuild(element)}
        ${tabindex} ${element.simpleDataAttributes!}><span class="${element.iconClass}"></span>${actionLabel}</a>
        <#elseif element.actionIconPlacement == 'RIGHT'>
        <a id="${element.id}" ${href!} ${name!} ${krad.attrBuild(element)}
        ${tabindex} ${element.simpleDataAttributes!}>${actionLabel}<span class="${element.iconClass}"></span></a>
        </#if>
    <#else>

        <a id="${element.id}" ${href!} ${name!} ${krad.attrBuild(element)}
        ${tabindex} ${element.simpleDataAttributes!}>

            <#if element.actionImage?? && element.actionImage.render && imagePlacement?has_content>
                <#if imagePlacement == 'RIGHT'>
                    <#local imageStyleClass="rightActionImage"/>
                <#elseif imagePlacement == 'LEFT'>
                    <#local imageStyleClass="leftActionImage"/>
                </#if>

                <#local imageTag>
                    <img ${imageRole!} ${height!} ${width!}
                            style="${element.actionImage.style!}"
                            class="actionImage ${imageStyleClass!} ${element.actionImage.styleClassesAsString!}"
                            src="${element.actionImage.source!}"
                            alt="${element.actionImage.altText!}"
                            title="${element.actionImage.title!}"/>
                </#local>

                <#if imagePlacement == 'RIGHT'>
                ${actionLabel!}${imageTag}
                <#elseif imagePlacement == 'LEFT'>
                ${imageTag}${actionLabel!}
                <#elseif imagePlacement == 'IMAGE_ONLY'>
                ${imageTag}
                <#else>
                ${actionLabel!}
                </#if>
            <#else>
            ${actionLabel!}
            </#if>

        </a>
    </#if>

    <@krad.disable control=element type="actionLink"/>

    <#-- render confirmation dialog for action -->
    <@krad.template component=element.confirmationDialog/>

</#macro>