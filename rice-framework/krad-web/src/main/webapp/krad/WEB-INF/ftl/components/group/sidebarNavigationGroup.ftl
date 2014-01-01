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
<#-- renders a list of items with a collapse icon and calls the appropriate setup script -->

<#macro uif_sidebarNavigationGroup group currentPageId>
    <@krad.groupWrap group=group>

        <#if group.renderCollapse>
            <div class="sidebar-collapse">
                <span class="icon-angle-left"></span>
            </div>
        </#if>

        <!-- NAVIGATION -->
        <ul class="nav nav-list">
            <#list group.items as item>
                <li>
                    <@krad.template component=item/>
                </li>
            </#list>
        </ul>
    </@krad.groupWrap>

    <@krad.script value="setupSidebarNavMenu('${group.id}', '${group.openedToggleIconClass!}',
        '${group.closedToggleIconClass!}');"/>

</#macro>