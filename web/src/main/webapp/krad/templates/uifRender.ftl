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

<#include "libInclude.ftl" parse=true/>

<#assign view=KualiForm.view/>

<#if KualiForm.renderFullView>

    <@kul.html view=view>

        <#if !view.renderedInLightBox>
            <@kul.script value="
                jQuery(function(){
                  publishHeight();
                  window.onresize = publishHeight;
                  window.setInterval(publishHeight, 249);
                });
            "/>
        </#if>

        <@kul.script value="${KualiForm.growlScript}"/>

        <div id="Uif-Application" style="display:none;" class="uif-application">

            <!-- APPLICATION HEADER -->
            <@kul.template component=view.applicationHeader/>
            <@kul.backdoor/>

            <@kul.form render=view.renderForm postUrl="${view.formPostUrl!KualiForm.formPostUrl}"
            onSubmitScript="${view.onSubmitScript}">

                <#if view.renderForm>
                    <#-- write out view, page id as hidden so the view can be reconstructed if necessary -->
                    <@spring.formHiddenInput path="viewId"/>

                    <#-- all forms will be stored in session, this is the conversation key -->
                    <@spring.formHiddenInput path="formKey"/>

                    <#-- Based on its value, form elements will be checked for dirtyness -->
                    <@spring.formHiddenInput path="validateDirty"/>
                </#if>

                <@kul.template component=view/>
            </@kul.form>

        </div>

        <!-- APPLICATION FOOTER -->
        <@kul.template component=view.applicationFooter/>

    </@kul.html>

<#else>

    <#-- render component only -->
    <html>

        <#-- rerun view pre-load script to get new state variables for page -->
        <@kul.script value="${view.preLoadScript}"/>

        <@kul.script value="${KualiForm.growlScript}"/>

        <#-- update for breadcrumbs -->
        <@kul.template component=view.breadcrumbs/>

        <#-- if full page is not being refreshed need to render the pages
        errors so they can be updated by the client -->
        <#if KualiForm.updateComponentId?has_content>
            <@kul.template component=KualiForm.postedView.currentPage.validationMessages/>
        </#if>

        <#-- now render the updated component (or page) wrapped in an update div -->
        <div id="${Component.id}_update" data-handler="update-component">
            <@kul.template componentUpdate=true component=Component/>
        </div>

    </html>

</#if>
