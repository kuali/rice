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
<#macro uif_nextPager element>

    <@krad.wrap component=element renderAs="ul">

        <#if !element.centeredLinks>
            <#local prevClass="previous"/>
        </#if>

        <#if element.currentPage == 1>
            <#local prevClass="${prevClass!} disabled"/>
        </#if>

        <li class="${prevClass!}">
            <a data-onclick="${element.linkScript}" data-num="prev" class="uif-pagePrev"
               href="#" data-role="Action">${element.prevText}</a>
        </li>

        <#if !element.centeredLinks>
            <#local nextClass="next"/>
        </#if>

        <#if element.currentPage == element.numberOfPages>
            <#local nextClass="${nextClass!} disabled"/>
        </#if>

        <li class="${nextClass!}">
            <a data-onclick="${element.linkScript}" data-num="next" class="uif-pageNext"
               href="#" data-role="Action">${element.nextText}</a>
        </li>

    </@krad.wrap>

</#macro>



