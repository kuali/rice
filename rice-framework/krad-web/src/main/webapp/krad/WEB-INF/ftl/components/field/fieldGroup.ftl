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
<#--
    Field that contains a nested Group

 -->


<#macro uif_fieldGroup field>

    <@krad.div component=field>

    <#-- check to see if label exists and if it has been rendered in another field (grid layout)-->
    <#local renderLabel=field.label?has_content && !field.labelRendered/>

    <#-- render field label top -->
    <#if renderLabel>
        <@krad.template component=field.fieldLabel/>
    </#if>

    <fieldset aria-labelledby="${field.id}_label" id="${field.id}_fieldset">
        <legend style="display: none">${field.label!}</legend>
        <@krad.template component=field.group/>
    </fieldset>

    </@krad.div>

</#macro>