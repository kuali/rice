<#macro uif_accordionGroup group params...>

    <@krad.groupWrap group=group>

        <#-- render items in list -->
        <ul id="${group.id}_accordList">
            <#list group.items as item>
                <li class="uif-accordionTab" data-tabfor="${item.id}">
                    <a href="#${item.id}_accordTitle">${item.header.headerText}</a>
                    <@krad.template component=item/>
                </li>
            </#list>
        </ul>

        <#-- render accordion widget -->
        <@krad.template component=group.accordionWidget parent=group/>

    </@krad.groupWrap>

</#macro>