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
<#--
    Renders a collection of files
 -->
<#macro uif_multiFileUploadCollection element>
    <@krad.div component=element>
        <#if !element.readOnly>
            <div class="row fileupload-buttonbar">
              <div class="col-md-12">
                <span class="btn btn-primary btn-xs fileinput-button"> <span>${element.addFilesButtonText!}</span>
                    <input id="${element.id}_fileInput" type="file" name="${element.propertyPath!}" multiple>
                </span>
              </div>
            </div>
        </#if>

        <@krad.template component=element.collection/>

        <@krad.script component=element
            value="createMultiFileUploadForCollection('${element.id}', '${element.collection.id}', ${element.templateOptionsJSString});"/>
    </@krad.div>
</#macro>