<#--

    Copyright 2005-2013 The Kuali Foundation

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
<@krad.html view=view>

    <@krad.script value="${KualiForm.growlScript!}"/>

    <div id="Uif-Application" style="display:none;" class="uif-application">

        <!-- APPLICATION HEADER -->
        <@krad.template component=view.applicationHeader/>
        <@krad.backdoor/>

        <@krad.form render=view.renderForm postUrl="${view.formPostUrl!KualiForm.formPostUrl}"
        onSubmitScript="${view.onSubmitScript!}">

            <#if view.renderForm>
                <#-- write out view, page id as hidden so the view can be reconstructed if necessary -->
                <@spring.formHiddenInput id="viewId" path="KualiForm.viewId"/>

                <#-- all forms will be stored in session, this is the conversation key -->
                <@spring.formHiddenInput id="formKey" path="KualiForm.formKey"/>

                <#-- Based on the view setting, form elements will be checked for dirtyness -->
                <@spring.formHiddenInput id="validateDirty" path="KualiForm.view.applyDirtyCheck"/>

                <#-- Indicator which is set to true when content is being rendered inside a lightbox -->
                <@spring.formHiddenInput id="renderedInLightBox" path="KualiForm.renderedInLightBox"/>
            </#if>

            <@krad.template component=view/>
        </@krad.form>

        <@krad.script value="${KualiForm.lightboxScript!}"/>

        <#-- set focus and perform jump to -->
        <@krad.script value="performFocusAndJumpTo(${view.currentPage.autoFocus?string}, true, true, '${KualiForm.focusId!}',
                                      '${KualiForm.jumpToId!}', '${KualiForm.jumpToName!}');" component=Component/>

    </div>

    <!-- APPLICATION FOOTER -->
    <@krad.template component=view.applicationFooter/>

</@krad.html>
