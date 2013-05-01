<#macro uif_lightTable group params...>
    <@krad.groupWrap group=group>

    <#if !group.emptyTable>
        <#local row>
            <#compress>
            <#list group.items as item>
            <td><@krad.template component=item/></td>
            </#list>
            </#compress>
        </#local>
    </#if>

    <table id="${group.id}_lightTable">
        <thead>
            <tr>
                <#list group.headerLabels as label>
                    <th><@krad.template component=label/></th>
                </#list>
            </tr>
        </thead>
        <tbody>
        <!-- call to get table content -->
            ${group.buildRows(row, KualiForm)}
        </tbody>
    </table>

       <@krad.script value="createTable('${group.id}_lightTable', ${group.richTable.templateOptionsJSString}); "/>
    </@krad.groupWrap>
</#macro>