<#--

    Copyright 2005-2017 The Kuali Foundation

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
<#-- generate the standard HTML form element -->

<#macro form postUrl onSubmitScript disableNativeAutocomplete=false render=true onSubmitAttribute=''>

    <#if !postUrl?has_content>
        <#local render=false>
    </#if>

    <#if render>
      <#if disableNativeAutocomplete>
          <#local disableAutocompleteAttr='autocomplete="off"'/>
      </#if>

      <#if onSubmitScript?has_content>
          <#local onSubmitAttribute='onsubmit="${onSubmitScript}"'/>
      </#if>

      <form id="kualiForm" action="${postUrl}" method="post" accept-charset="UTF-8"
            ${onSubmitAttribute} ${disableAutocompleteAttr!}>

          <#nested/>

          <@krad.csrfToken/>

          <span id="formComplete"></span>
      </form>
    <#else>
        <#nested/>
    </#if>

</#macro>