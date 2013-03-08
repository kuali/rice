<#macro uif_breadcrumb element breadcrumbsWidget>

    <#local id=""/>

    <#if element.id?has_content>
        <#local id="id=\"${element.id}\""/>
    </#if>

    <#if element.render && element.label?has_content && element.label != "&nbsp;">
    <li>
        <#if element.renderAsLink>
            <a ${id} href="${element.url.href}" ${krad.attrBuild(element)}>${element.label}</a>
        <#else>
            <span ${id} ${krad.attrBuild(element)}>${element.label}</span>
        </#if>

        <#if element.siblingBreadcrumbComponent?has_content>
            <div class="uif-breadcrumbSiblingContent" style="display: none;">
                <@krad.template component=element.siblingBreadcrumbComponent/>
            </div>
        </#if>
    </li>
    </#if>

</#macro>