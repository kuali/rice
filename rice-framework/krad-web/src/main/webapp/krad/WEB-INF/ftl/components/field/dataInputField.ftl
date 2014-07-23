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
<#macro uif_dataInputField field>

    <#local readOnly=field.readOnly || !field.inputAllowed/>

    <#local inlineEdit=(field.inlineEdit)?? && field.inlineEdit/>
    <#local ajaxInlineEdit=(field.ajaxInlineEdit)?? && field.ajaxInlineEdit/>
    <#local ajaxInlineEditRefresh =ajaxInlineEdit && KualiForm.updateComponentId?? &&  KualiForm.updateComponentId == field.id/>

    <@krad.div component=field>

        <#local renderLabel=field.label?has_content && !field.labelRendered/>

        <#-- render field label -->
        <#if renderLabel>
            <@krad.template component=field.fieldLabel/>
        </#if>

        <#-- wrap content if displaying label to the left -->
        <#if field.labelLeft && renderLabel>
            <div class="uif-fieldContent">
        </#if>

        <#-- TODO: verify removal -->
        <#--<#if field.renderFieldset>-->
            <#--<fieldset data-type="InputSet" aria-labelledby="${field.id}_label" id="${field.id}_fieldset">-->
                <#--<legend style="display: none">${field.label!}</legend>-->
        <#--</#if>-->

        <#local quickfinderInputOnly=(field.widgetInputOnly!false) && ((field.quickfinder.dataObjectClassName)!"")?has_content />

        <#-- render field value (if read-only/quickfinder-input-only) or control (if edit) -->
        <#if readOnly || inlineEdit || ajaxInlineEdit>
            <#if inlineEdit>
                <button class="uif-inlineEdit-view" id="${field.id}_inlineEdit_view" tabindex="0" title="Click to Edit">
            <#elseif ajaxInlineEdit>
                <button class="uif-inlineEdit-view" id="${field.id}_inlineEdit_view" tabindex="0" title="Click to Edit"
                    data-ajax_edit="true">
            <#else>
                <span id="${field.id}_control" tabindex="0">
            </#if>

            <#local readOnlyDisplay>
                <#if field.forcedValue?has_content>
                    ${field.forcedValue}
                <#else>
                    <#-- display replacement display value if set -->
                    <#if field.readOnlyDisplayReplacement?has_content>
                         ${field.readOnlyDisplayReplacement}
                    <#else>
                        <#-- display actual field value -->
                        <@spring.bind path="KualiForm.${field.bindingInfo.bindingPath}"/>

                        <#-- check escape flag -->
                        <#if field.escapeHtmlInPropertyValue>
                            ${(spring.status.value?default(""))?html}
                        <#else>
                            ${(spring.status.value?default(""))}
                        </#if>

                        <#-- add display suffix value if set -->
                        <#if field.readOnlyDisplaySuffix?has_content>
                             *-* ${field.readOnlyDisplaySuffix}
                        </#if>
                    </#if>
                </#if>
            </#local>

            <#if field.multiLineReadOnlyDisplay>
                <#local readOnlyDisplay="<pre class='no-pad-bkgd-bor'>${readOnlyDisplay?trim?replace(' ','&nbsp;')}</pre>"/>
            </#if>

            <#-- render inquiry if enabled -->
            <#if field.inquiry?has_content && field.inquiry.render>
                <@krad.template component=field.inquiry componentId="${field.id}" body="${readOnlyDisplay}"
                  readOnly=field.readOnly/>
            <#else>
                ${readOnlyDisplay}
            </#if>

            <#--render field quickfinder -->
            <#if field.inputAllowed>
                <@krad.template component=field.quickfinder componentId="${field.id}"/>
            </#if>

            <#if inlineEdit || ajaxInlineEdit>
                </button>
            <#else>
                </span>
            </#if>
        </#if>

        <#if (!readOnly && !ajaxInlineEdit) || (!readOnly && ajaxInlineEditRefresh)>

            <#if inlineEdit || ajaxInlineEditRefresh>
                <div class="uif-inlineEdit-edit" id="${field.id}_inlineEdit_edit">
            </#if>

            <#if quickfinderInputOnly>
               <#local readOnlyDisplay>
                   <#if field.forcedValue?has_content>
                       ${field.forcedValue}
                   <#else>
                       <#-- display replacement display value if set -->
                       <#if field.readOnlyDisplayReplacement?has_content>
                            ${field.readOnlyDisplayReplacement}
                       <#else>
                           <#-- display actual field value -->
                           <@spring.bind path="KualiForm.${field.bindingInfo.bindingPath}"/>

                           <#-- check escape flag -->
                           <#if field.escapeHtmlInPropertyValue>
                               ${(spring.status.value?default(""))?html}
                           <#else>
                               ${(spring.status.value?default(""))}
                           </#if>

                           <#-- add display suffix value if set -->
                           <#if field.readOnlyDisplaySuffix?has_content>
                                *-* ${field.readOnlyDisplaySuffix}
                           </#if>
                       </#if>
                   </#if>
               </#local>

               <#if field.multiLineReadOnlyDisplay>
                   <#local readOnlyDisplay="<pre class='no-pad-bkgd-bor'>${readOnlyDisplay?trim?replace(' ','&nbsp;')}</pre>"/>
               </#if>

               <span id="${field.id}_control" class="uif-readOnlyContent">
                   <#-- render inquiry if enabled -->
                   <#if field.inquiry?has_content && field.inquiry.render>
                       <@krad.template component=field.inquiry componentId="${field.id}" body="${readOnlyDisplay}"
                         readOnly=field.readOnly/>
                   <#else>
                       ${readOnlyDisplay}
                   </#if>
               </span>

               <#if field.postInputAddons?? || field.renderInputAddonGroup>
                   <div class="input-group inlineBlock">
               </#if>

                <#if field.postInputAddons?? >
                    <#list field.postInputAddons as postAddon>
                        <@krad.template component=postAddon/>
                    </#list>
                </#if>

               <#if field.postInputAddons?? || field.renderInputAddonGroup>
                   </div>
               </#if>

            <#else>

                <#-- render field instructional text -->
                <@krad.template component=field.instructionalMessage/>

                <#if field.postInputAddons?? || field.renderInputAddonGroup>
                    <div class="input-group">
                </#if>

                <#-- render control for input -->
                <@krad.template component=field.control field=field/>

                <#if field.postInputAddons?? || field.renderInputAddonGroup>
                     <div class="${field.postInputCssClassesAsString}">
                </#if>

                <#if field.postInputAddons??>
                    <#list field.postInputAddons as postAddon>
                        <@krad.template component=postAddon/>
                    </#list>
                </#if>

                <#-- render field help -->
                <@krad.template component=field.help/>

                <#if field.postInputAddons?? || field.renderInputAddonGroup>
                        </div>
                </#if>

                <#if field.helperText?has_content>
                    <div class="uif-helperText">
                        ${field.helperText}
                    </div>
                </#if>

                <#if field.postInputAddons?? || field.renderInputAddonGroup>
                    </div>
                </#if>

            </#if>

            <#-- render field direct inquiry if field is editable and inquiry is enabled-->
            <#if (field.inquiry.render)!false>
                <@krad.template component=field.inquiry componentId="${field.id}" readOnly=field.readOnly/>
            </#if>

            <#if field.renderMarkerIconSpan>
                <span id="${field.id}_markers"></span>
            </#if>

            <#-- render field constraint -->
            <@krad.template component=field.constraintMessage/>

            <#-- render field suggest if field is editable -->
            <@krad.template component=field.suggest parent=field/>

        <#-- transform all text on attribute field to uppercase -->
            <#if field.control?? && field.uppercaseValue>
                <@krad.script value="uppercaseValue('${field.control.id}');"/>
            </#if>

            <#if inlineEdit || ajaxInlineEditRefresh>
                </div>
            </#if>

        </#if>

        <#-- render span and values for informational properties -->
        <#if field.renderInfoMessageSpan>
            <span id="${field.id}_info_message"></span>
        </#if>

        <#if field.propertyNamesForAdditionalDisplay??>
	        <#list field.propertyNamesForAdditionalDisplay as infoPropertyPath>
	            <span id="${field.id}_info_${krad.cleanPath(infoPropertyPath)}" class="uif-informationalMessage">
	                <@spring.bind path="KualiForm.${infoPropertyPath}"/>
	                 ${spring.status.value?default("")}
	            </span>
	        </#list>
        </#if>

        <#-- render hidden fields -->
        <#-- TODO: always render hiddens if configured? -->
        <#if field.additionalHiddenPropertyNames??>
	        <#list field.additionalHiddenPropertyNames as hiddenPropertyName>
	            <@spring.formHiddenInput id="${field.id}_h${hiddenPropertyName_index}"
	            path="KualiForm.${hiddenPropertyName}"/>
	        </#list>
        </#if>

        <#if field.labelLeft && renderLabel>
            </div>
        </#if>

    </@krad.div>

</#macro>


