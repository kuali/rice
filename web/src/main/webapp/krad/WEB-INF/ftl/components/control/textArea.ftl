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
Standard HTML TextArea Input

-->

<@macro uif-textarea control field>

    <#local attributes='id="${control.id}" rows="${control.rows!}" cols="${control.cols!}"
    cssClass="${control.styleClassesAsString!}" tabindex="${control.tabIndex!}" ${element.simpleDataAttributes!}'/>

    <#if control.disabled>
        <#local attributes='${attributes} disabled="true"'/>
    </#if>

    <#if control.readOnly>
        <#local attributes='${attributes} readonly="true"'/>
    </#if>

    <#if control.style?has_content>
        <#local attributes='${attributes} cssStyle="${control.style}"'/>
    </#if>

    <#if control.maxLength??>
        <#local attributes='${attributes} maxlength="${control.maxLength}"'/>
    </#if>

    <#if control.minLength??>
        <#local attributes='${attributes} minLength="${control.minLength}"'/>
    </#if>

    <@spring.textarea path="KualiForm.${field.bindingInfo.bindingPath}" attributes="${attributes}"/>

    <#--
    Use double quotes around watermark text to avoid apostrophe trouble
    credit - http://rayaspnet.blogspot.com/2011/03/how-to-handle-apostrophe-in-javascript.html
     -->
    <#if control.watermarkText?has_content>
        <@krad.script value="createWatermark('${control.id}', \"${control.watermarkText}\");"/>
    </#if>

    <#if control.textExpand>
        <@krad.script value="setupTextPopout('${control.id}', '${field.label!}', '${field.instructionalMessage.messageText!}', '${field.constraintMessage.messageText!}', '${ConfigProperties['krad.externalizable.images.url']}');" />
    </#if>

</@macro>