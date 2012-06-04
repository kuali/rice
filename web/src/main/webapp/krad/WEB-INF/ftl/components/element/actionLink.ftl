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

<#--
    HTML Link to Submit Form Via JavaScript

 -->

<#if element.navigateToPageId?has_content>
    <#assign pound="#"/>
    <#assign href="href=\"${pound}${element.navigateToPageId}\""/>
</#if>

<#if element.skipInTabOrder>
    <#assign tabindex="tabindex=-1"/>
<#else>
    <#assign tabindex="tabindex=0"/>
</#if>

<#if !element.actionLabel??>
    <#assign imageRole="role='presentation'"/>
</#if>

<#if element.actionImage??>
    <#if element.actionImage.height?has_content>
        <#assign height="height='${element.actionImage.height}'"/>
    </#if>

    <#if element.actionImage.width?has_content>
        <#assign width="width='${element.actionImage.width}'"/>
    </#if>
</#if>

<#assign imagePlacement="${element.actionImagePlacement}"/>

<a id="${element.id}" ${href!} onclick="return false;" ${attrBuild(element)}
   ${tabindex} ${element.simpleDataAttributes!}>

    <#if element.actionImage?? && element.actionImage.render && imagePlacement?has_content>
        <#if imagePlacement == 'RIGHT'>
            <#assign imageStyleClass="rightActionImage"/>
        <#elseif imagePlacement == 'LEFT'>
            <#assign imageStyleClass="leftActionImage"/>
        </#if>

        <#assign imageTag>
            <img ${imageRole!} ${height!} ${width!}
                 style="${element.actionImage.style!}"
                 class="actionImage ${imageStyleClass!} ${element.actionImage.styleClassesAsString!}"
                 src="${element.actionImage.source!}"
                 alt="${element.actionImage.altText!}"
                 title="${element.actionImage.title!}"/>
        </#assign>

       <#if imagePlacement == 'RIGHT'>
           ${element.actionLabel!}${imageTag}
       <#elseif imagePlacement == 'LEFT'>
           ${imageTag}${element.actionLabel!}
       <#elseif imagePlacement == 'IMAGE_ONLY'>
           ${imageTag}
       <#else>
           ${element.actionLabel!}
       </#if>
    </#if>

</a>

<@krad.template component=element.lightBoxLookup componentId="${element.id}"/>