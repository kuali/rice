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