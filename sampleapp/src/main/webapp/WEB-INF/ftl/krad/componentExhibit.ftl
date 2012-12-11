<#macro uif_exhibit element>

    <@krad.template component=element.tabGroup/>

    <div id="demo-exhibitSource" style="display:none;">
        <#list element.demoSourceCode as sourceCode>
            <pre class="${element.sourceCodeViewer.pluginCssClass}"
                  data-index="${sourceCode_index}">${sourceCode}</pre>
        </#list>
    </div>

    <@krad.template component=element.sourceCodeViewer/>



    <@krad.script value="setupExhibitHandlers()" />
</#macro>