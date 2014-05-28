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
<@krad.html view=view>

    <a href="#mainContent" class="sr-only">Skip to main content</a>

    <@krad.script value="${KualiForm.growlScript!}"/>

    <!-- APPLICATION HEADER -->
    <#if view.applicationHeader?has_content>
        <#assign stickyDataAttribute=""/>
        <#if view.stickyApplicationHeader>
            <#assign stickyDataAttribute="data-sticky='true'"/>
        </#if>

        <#if view.applicationHeader?? && view.applicationHeader.render>
            <header id="Uif-ApplicationHeader-Wrapper" ${stickyDataAttribute}>
                <@krad.template component=view.applicationHeader/>

                <!-- Backdoor info (here to inherit stickyness with the header, if set) -->
                <@krad.backdoor/>
            </header>
        </#if>
    <#else>
        <!-- Backdoor info -->
        <@krad.backdoor/>
    </#if>

    <@krad.form render=view.renderForm postUrl="${KualiForm.formPostUrl}"
    onSubmitScript="${view.onSubmitScript!}" disableNativeAutocomplete=view.disableNativeAutocomplete>
        <@krad.template component=view/>

        <#if view.renderForm>
            <span id="formInfo">
                <#-- write out view, page id as hidden so the view can be reconstructed if necessary -->
                <@spring.formHiddenInput path="KualiForm.viewId"/>

                <#-- all forms will be stored in session, this is the conversation key -->
                <@spring.formHiddenInput path="KualiForm.formKey"/>

                <#-- original form key requested, may differ from actual form key-->
                <@spring.formHiddenInput path="KualiForm.requestedFormKey"/>

                <#-- tracks the session, used to determine timeouts -->
                <@spring.formHiddenInput path="KualiForm.sessionId"/>

                <#-- flow key to maintain a history flow -->
                <@spring.formHiddenInput path="KualiForm.flowKey"/>

                <#-- based on the view setting, form elements will be checked for dirtyness -->
                <@spring.formHiddenInput path="KualiForm.view.applyDirtyCheck"/>

                <#-- based on the view setting, form elements will be checked for dirtyness -->
                <@spring.formHiddenInput path="KualiForm.dirtyForm"/>

                <#-- indicator which is set to true when content is being rendered inside a lightbox -->
                <@spring.formHiddenInput path="KualiForm.renderedInLightBox"/>

                <#-- indicator for single page view, used to drive script page handling logic -->
                <@spring.formHiddenInput path="KualiForm.view.singlePageView"/>

                <#-- indicator for disabling browser caching of the view -->
                <@spring.formHiddenInput path="KualiForm.view.disableBrowserCache"/>

                <#if KualiForm.view.additionalHiddenValues??>
                    <#list KualiForm.view.additionalHiddenValues?keys as additionalHiddenName>
                        <input name="${additionalHiddenName}" type="hidden" value="${KualiForm.view.additionalHiddenValues[additionalHiddenName]}"/>
                    </#list>
                </#if>
            </span>
        </#if>
    </@krad.form>

    <!-- APPLICATION FOOTER -->
    <#if view.applicationFooter?? && view.applicationFooter.render>
        <#assign stickyFooterDataAttribute=""/>
        <#if view.stickyApplicationFooter>
            <#assign stickyFooterDataAttribute="data-sticky_footer='true'"/>
        </#if>

        <footer id="Uif-ApplicationFooter-Wrapper" ${stickyFooterDataAttribute}>
            <#if view.stickyApplicationFooter>
                <div class="${view.contentContainerClassesAsString}">
            </#if>

            <@krad.template component=view.applicationFooter/>

            <#if view.stickyApplicationFooter>
                </div>
            </#if>
        </footer>
    </#if>

</@krad.html>
