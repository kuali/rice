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
<#macro uif_multiFileUpload element>
    <@krad.div component=element>
        <#if !element.readOnly>
            <div class="row fileupload-buttonbar">
              <div class="col-md-12">
                <span class="btn btn-primary btn-xs fileinput-button"> <span>${element.addFilesButtonText!}</span>
                    <input id="${element.id}_fileInput" type="file" name="${element.propertyPath!}" multiple>
                </span>
                <button type="submit" class="btn btn-default btn-xs start">
                    <span>${element.uploadAllButtonText!}</span>
                </button>
                <button type="reset" class="btn btn-default btn-xs cancel">
                    <span>${element.cancelAllButtonText!}</span>
                </button>
              </div>
            </div>
        </#if>

        <div id="${element.id}_fileTable" class="batch">
            <table class="table table-condensed">
                <thead>
                    <tr>
                        <#list element.fileTableColumnNames as name>
                            <th>${name!}</th>
                        </#list>
                    </tr>
                </thead>

                <tbody id="${element.id}_files" class="files"></tbody>
            </table>
        </div>
    </@krad.div>

    <#if !element.readOnly>
        <script id="${element.id}_uploadTemplate" type="text/x-tmpl">
            <#include "${element.fileUploadRowTemplate}" parse=true/>
        </script>
    </#if>

    <script id="${element.id}_downloadTemplate" type="text/x-tmpl">
        <#include "${element.fileDownloadRowTemplate}" parse=true/>
    </script>

    <@krad.script component=parent
        value="createMultiFileUpload('${element.id}', ${element.templateOptionsJSString});"/>
</#macro>