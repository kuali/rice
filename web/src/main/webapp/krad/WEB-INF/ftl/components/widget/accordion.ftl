<#macro uif_accordion widget parent>

    <@krad.script component=parent value="createAccordion('${parent.id}', ${widget.templateOptionsJSString},
        ${widget.active});"/>

</#macro>