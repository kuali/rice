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
<#--
    Css Grid Layout Manager

    This is a layout that uses divs instead of a table to achieve a table look and feel.  This is done through the
    use of css with divs which represent "rows" and "cells" of the layout.  Two variations can be achieved through
    this layout, either a fluid version (stretches and reacts to resizing the window) or fixed (does not change
    the size of the "cells").
 -->
<#include "../components/element/label.ftl"/>

<#macro uif_cssGrid items manager container>
    <#local cellIndex = 0/>

    <#list manager.cellItems as item>
        <div class="${manager.cellCssClassAttributes[cellIndex]}">
            <@krad.template component=item/>
        </div>
        <#local cellIndex = cellIndex + 1/>
    </#list>

</#macro>