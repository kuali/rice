<#macro uif_list items manager container>

    <#local listType="ul">
    <#if manager.orderedList>
        <#local listType="ol">
    </#if>

    <#if manager.styleClassesAsString?has_content>
        <#local styleClass="class=\"${manager.styleClassesAsString}\""/>
    </#if>

    <#if manager.style?has_content>
        <#local style="style=\"${manager.style}\""/>
    </#if>

    <${listType!} id="${manager.id}" ${style!} ${styleClass!}>
        <#list items as item>
            <li>
                <@krad.template component=item/>
            </li>
        </#list>
    </${listType!}>

</#macro>