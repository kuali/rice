<#--

Copyright 2005-2012 The Kuali Foundation

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
Standard HTML Select Input

-->
<@macro uif-select control field>

    <#local attributes='id="${control.id}" size="${control.size!}" cssClass="${control.styleClassesAsString!}"
          multiple="${control.multiple!}" tabindex="${control.tabIndex!}"  ${element.simpleDataAttributes!}'/>

    <#if control.disabled>
        <#local attributes='${attributes} disabled="true"'/>
    </#if>

    <@spring.formSelect path="KualiForm.${field.bindingInfo.bindingPath}" options=control.options attributes="${attributes}"/>

</@macro>
