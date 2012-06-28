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

<#compress>

<#if KualiForm.renderFullView>

    <#assign view=KualiForm.view/>

    <@krad.html view=view>

         <#if !view.renderedInLightBox>
             <@krad.script value="
                 jQuery(function(){
                   publishHeight();
                   window.onresize = publishHeight;
                   window.setInterval(publishHeight, 249);
                 });
             "/>
         </#if>

         <@krad.script value="${KualiForm.growlScript!}"/>

         <div id="Uif-Application" style="display:none;" class="uif-application">

             <!-- APPLICATION HEADER -->
             <@krad.template component=view.applicationHeader/>
             <@krad.backdoor/>

             <@krad.form render=view.renderForm postUrl="${view.formPostUrl!KualiForm.formPostUrl}"
             onSubmitScript="${view.onSubmitScript}">

                 <#if view.renderForm>
                     <#-- write out view, page id as hidden so the view can be reconstructed if necessary -->
                     <@spring.formHiddenInput path="KualiForm.viewId" attributes="id=\"viewId\""/>

                     <#-- all forms will be stored in session, this is the conversation key -->
                     <@spring.formHiddenInput path="KualiForm.formKey" attributes="id=\"formKey\""/>

                     <#-- Based on its value, form elements will be checked for dirtyness -->
                     <@spring.formHiddenInput path="KualiForm.validateDirty" attributes="id=\"validateDirty\""/>
                 </#if>

                 <@krad.template component=view/>
             </@krad.form>

             <@krad.script value="${KualiForm.lightboxScript!}"/>

         </div>

         <!-- APPLICATION FOOTER -->
         <@krad.template component=view.applicationFooter/>

     </@krad.html>

<#else>

    <#-- render component only -->
    <html>

        <#assign isPageRefresh=!KualiForm.updateComponentId?has_content/>

        <#if isPageRefresh>
            <#-- rerun view pre-load script to get new state variables for page -->
            <@krad.script value="${KualiForm.view.preLoadScript!}"/>

            <#-- update for breadcrumbs -->
            <@krad.template component=KualiForm.view.breadcrumbs/>
        <#else>
            <#-- rerun view pre-load script to get new state variables for component -->
            <@krad.script value="${KualiForm.postedView.preLoadScript!}"/>

            <#-- need to render the pages errors since the component could have added errors for the page -->
            <@krad.template component=KualiForm.postedView.currentPage.validationMessages/>
        </#if>

        <#-- now render the updated component (or page) wrapped in an update div -->
        <div id="${Component.id}_update" data-handler="update-component">
            <@krad.template componentUpdate=true component=Component/>
        </div>

        <#-- show added growls -->
        <@krad.script value="${KualiForm.growlScript!}"/>
        <@krad.script value="${KualiForm.lightboxScript!}"/>
    </html>

</#if>

</#compress>