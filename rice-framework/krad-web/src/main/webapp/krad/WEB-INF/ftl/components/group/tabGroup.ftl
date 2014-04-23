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
<#macro uif_tabGroup group>

    <@krad.groupWrap group=group>

    <!-- Tab panes -->
    <#local tabPanes>
        <div class="${group.tabsWidget.tabContentClass}">
            <#list group.items as item>
                <#if group.tabsWidget.defaultActiveTabId?has_content && group.tabsWidget.defaultActiveTabId == item.id>
                    <div id="${item.id}_tabPanel" class="tab-pane active"
                         role="tabpanel" aria-hidden="false" tabindex="0"
                         aria-labelledby="${item.id}_tab" data-tabwrapperfor="${item.id}" data-type="TabWrapper" >
                        <@krad.template component=item/>
                    </div>
                <#else>
                    <div id="${item.id}_tabPanel" class="tab-pane" role="tabpanel" aria-hidden="true" tabindex="-1"
                         aria-labelledby="${item.id}_tab" data-tabwrapperfor="${item.id}" data-type="TabWrapper" >
                        <@krad.template component=item/>
                    </div>
                </#if>
            </#list>
        </div>
    </#local>

    <#if group.tabsWidget.position == "BOTTOM" || group.tabsWidget.position == "RIGHT">
        ${tabPanes}
    </#if>

        <!-- Nav tabs -->
        <ul id="${group.id}_tabList" class="${group.tabsWidget.tabNavClass}" role="tablist">
            <#list group.items as item>
                <#if group.tabsWidget.defaultActiveTabId?has_content && group.tabsWidget.defaultActiveTabId == item.id>
                    <li data-tabfor="${item.id}" class="active" role="presentation">
                        <a id="${item.id}_tab" href="#${item.id}_tabPanel" role="tab" tabindex="0"
                           aria-controls="${item.id}_tabPanel" aria-expanded="true"
                           aria-selected="true" data-toggle="tab">
                            ${(item.header.headerText)}
                        </a>
                    </li>
                <#else>
                    <li data-tabfor="${item.id}" role="presentation">
                        <a id="${item.id}_tab" href="#${item.id}_tabPanel" role="tab" tabindex="-1"
                           aria-controls="${item.id}_tabPanel" aria-expanded="false"
                           aria-selected="false" data-toggle="tab">
                            ${(item.header.headerText)}
                        </a>
                    </li>
                </#if>
            </#list>
        </ul>

    <#if group.tabsWidget.position == "TOP" || group.tabsWidget.position == "LEFT">
        ${tabPanes}
    </#if>

    </@krad.groupWrap>

</#macro>


