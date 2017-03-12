<#--

    Copyright 2005-2016 The Kuali Foundation

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
<!--
    Implemented this to replicate the custom #dyncall macro that was hacked into Freemarker code for the early
    KRAD work. Didn't want to depend on a customized version of Freemarker any longer (and the source code no longer
    exists anyway.

    Note that in all the cases this was used, templateParams included values that were pulling from a "group" variable,
    so the group is included as a parameter here so that it will be available within the scope when dynamically
    executing the template.
-->
<#macro dyncall templateName templateParams group>
  <#assign inlineTemplateSrc = "<@${templateName} ${templateParams}/>">
  <#assign inlineTemplate = inlineTemplateSrc?interpret/>
  <@inlineTemplate />
</#macro>