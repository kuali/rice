<#--

    Copyright 2005-2013 The Kuali Foundation

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
<#--
    Bootstrap dropdown menu component

 -->

<#macro uif_dropdownMenu element>

    <#if element.dropdownToggle.render && element.dropdownToggle.messageText?has_content>

    <#if element.nestedMenu>
        <li id="${element.id!}" ${krad.attrBuild(element)} ${element.simpleDataAttributes}>
    <#else>
        <div id="${element.id!}" ${krad.attrBuild(element)} ${element.simpleDataAttributes}>
    </#if>


            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                <@krad.template component=element.dropdownToggle/>
                <#if element.includeToggleCaret>
                    <b class="caret"></b>
                </#if>
            </a>

          <#local ulClass="class=\"dropdown-menu\""/>
      </#if>

        <ul ${ulClass!}>

        <#if element.options??>

            <#list element.options as option>
                <#if option.optionDivider>
                    <li class="divider"></li>
                <#elseif option.optionHeader>
                    <li class="dropdown-header">${option.value?html}</li>
                <#else>
                    <#if option.disabled>
                       <#local disabled="class=\"disabled\""/>
                    </#if>

                    <li ${disabled!}>
                        <a href="${option.location.href}">${option.value?html}</a>
                    </li>
                </#if>
            </#list>

        <#elseif element.menuColumns??>

          <#list element.menuColumns as menuColumnDropdowns>

            <li class="col-lg-${element.menuNumberOfColumns}">
                <#list menuColumnDropdowns as dropdown>
                     <@krad.template component=dropdown/>
                </#list>
            </li>

          </#list>

        </#if>

        </ul>

    <#if element.dropdownToggle.render && element.dropdownToggle.messageText?has_content>
    <#if element.nestedMenu>
        </li>
    <#else>
        </div>
    </#if>
    </#if>

</#macro>
