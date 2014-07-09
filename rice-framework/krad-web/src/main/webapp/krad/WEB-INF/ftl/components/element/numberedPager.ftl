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
<#macro uif_numberedPager element>

    <@krad.wrap component=element renderAs="ul">
        <#if element.currentPage == 1>
            <#local prevClass="class='disabled'"/>
        </#if>

        <#if element.renderFirstLast>
            <li ${prevClass!}>
                <a data-onclick="${element.linkScript}" data-num="first" class="uif-pageFirst"
                   href="#" data-role="Action">${element.firstText}</a>
            </li>
        </#if>

        <#if element.renderPrevNext>
            <li ${prevClass!}>
                <a data-onclick="${element.linkScript}" data-num="prev" class="uif-pagePrev"
                   href="#" data-role="Action">${element.prevText}</a>
            </li>
        </#if>

        <#list element.pagesStart..element.pagesEnd as pageNum>
            <#local liClass=""/>
            <#if element.currentPage == pageNum>
                <#local liClass="class='active'"/>
            </#if>

            <li ${liClass!}>
                <a data-onclick="${element.linkScript}" data-num="${pageNum}"
                   href="#" data-role="Action">${pageNum}</a>
            </li>
        </#list>

        <#if element.currentPage == element.numberOfPages>
            <#local nextClass="class='disabled'"/>
        </#if>

        <#if element.renderPrevNext>
            <li ${nextClass!}>
                <a data-onclick="${element.linkScript}" data-num="next" class="uif-pageNext"
                   href="#" data-role="Action">${element.nextText}</a>
            </li>
        </#if>

        <#if element.renderFirstLast>
            <li ${nextClass!}>
                <a data-onclick="${element.linkScript}" data-num="last" class="uif-pageLast"
                   href="#" data-role="Action">${element.lastText}</a>
            </li>
        </#if>
    </@krad.wrap>

</#macro>