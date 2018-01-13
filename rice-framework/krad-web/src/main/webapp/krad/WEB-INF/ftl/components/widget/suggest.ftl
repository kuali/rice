<#--

    Copyright 2005-2018 The Kuali Foundation

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
<#macro uif_suggest widget parent>

    <#--
    Invokes JS method to implement suggest (autocomplete) functionality
    -->

    <@krad.script value="
      createSuggest('${parent.control.id}', ${widget.templateOptionsJSString}, '${parent.id}',
       ${widget.suggestQuery.queryMethodArgumentFieldsJsString}, ${widget.retrieveAllSuggestions?string},
       ${widget.suggestOptionsJsString!},'${widget.labelPropertyName!}', '${widget.valuePropertyName!}',
       ${widget.returnFullQueryObject?string});"/>

</#macro>


