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
<!-- The template to display files available for download -->

{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-download fade">
        <td>
            <p class="name">
                {% if (file.url) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}">{%=file.name%}</a>
                {% } else { %}
                    <span>{%=file.name%}</span>
                {% } %}
            </p>
            {% if (file.error) { %}
                <div><span class="label label-danger">Error</span> {%=file.error%}</div>
            {% } %}
        </td>
        <td>
            <span class="size">{%=o.formatFileSize(file.size)%}</span>
        </td>
        <td>
            <span class="size">{%=file.dateUploadedFormatted%}</span>
        </td>
        <td>
            {% if (file.deleteUrl) { %}
                <button class="btn btn-default delete" title="Delete" data-type="POST"
                    data-url="{%=file.deleteUrl%}">
                    <i class="icon-trash"></i>
                </button>
            {% } else { %}
                <button class="btn btn-default cancel" title="Cancel">
                    <i class="icon-trash"></i>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}