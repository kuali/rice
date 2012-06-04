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
    Standard HTML Input Submit - will create an input of type submit or type image if the action
    image element is configured

 -->

<#if element.skipInTabOrder>
    <#assign tabIndex="tabindex=-1"/>
</#if>

<#if element.actionImage??>
    <#if element.actionImage.height?has_content>
        <#assign height="height='${element.actionImage.height}'"/>
    </#if>

    <#if element.actionImage.width?has_content>
        <#assign width="width='${element.actionImage.width}'"/>
    </#if>
</#if>

<#if element.disabled>
    <#assign disabled="disabled=\"true\""/>
</#if>

<#assign imagePlacement="${element.actionImagePlacement}"/>

<#-- determine if input of type image should be rendered -->
<#if element.actionImage?? && element.actionImage.render && (!imagePlacement?has_content
    || (imagePlacement == 'IMAGE_ONLY'))>

    <input type="image" id="${element.id}" ${disabled!}
           src="${element.actionImage.source}"
           alt="${element.actionImage.altText!}"
           title="${element.actionImage.title!}" ${height!} ${width!}
           ${attrBuild(element)} ${tabindex!}
           ${element.simpleDataAttributes!}/>
<#else>

   <#-- build a button with or without an image -->
   <button id="${element.id}" ${attrBuild(element)} ${tabindex!} ${disabled!} ${element.simpleDataAttributes}>

      <#if element.actionImage?? && element.actionImage.render && imagePlacement?has_content>
           <#if imagePlacement == 'TOP'>
               <#assign imageStyleClass="topActionImage"/>
               <#assign spanBeginTag="<span class=\"topBottomSpan\">"/>
               <#assign spanEndTag="</span>"/>
           <#elseif imagePlacement == 'BOTTOM'>
               <#assign imageStyleClass="bottomActionImage"/>
               <#assign spanBeginTag="<span class=\"topBottomSpan\">"/>
               <#assign spanEndTag="</span>"/>
           <#elseif imagePlacement == 'RIGHT'>
               <#assign imageStyleClass="rightActionImage"/>
           <#elseif imagePlacement == 'LEFT'>
               <#assign imageStyleClass="leftActionImage"/>
           </#if>

           <#assign imageTag>
               <img ${height!} ${width!}
                    style="${element.actionImage.style!}"
                    class="actionImage ${imageStyleClass!} ${element.actionImage.styleClassesAsString!}"
                    src="${element.actionImage.source}"
                    alt="${element.actionImage.altText!}"
                    title="${element.actionImage.title!}"/>
           </#assign>
      </#if>

      <#if ['TOP','LEFT']?seq_contains(element.actionImagePlacement)>
           ${spanBeginTag!}${imageTag!}${spanEndTag!}${element.actionLabel}
      <#elseif ['BOTTOM','RIGHT']?seq_contains(element.actionImagePlacement)>
           ${element.actionLabel}${spanBeginTag!}${imageTag!}${spanEndTag!}
      <#else>
          <#-- no image, just render label text -->
          ${element.actionLabel!}
      </#if>

   </button>

</#if>

<@krad.template component=element.lightBoxLookup componentId="${element.id}"/>
