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
<#--
 * spring.ftl
 *
 * This file consists of a collection of FreeMarker macros aimed at easing
 * some of the common requirements of web applications - in particular
 * handling of forms.
 *
 * Spring's FreeMarker support will automatically make this file and therefore
 * all macros within it available to any application using Spring's
 * FreeMarkerConfigurer.
 *
 * To take advantage of these macros, the "exposeSpringMacroHelpers" property
 * of the FreeMarker class needs to be set to "true". This will expose a
 * RequestContext under the name "springMacroRequestContext", as needed by
 * the macros in this library.
 *
 * @author Darren Davison
 * @author Juergen Hoeller
 * @since 1.1
 -->

<#--
 * message
 *
 * Macro to translate a message code into a message.
 -->
<#macro message code>${springMacroRequestContext.getMessage(code)}</#macro>

<#--
 * messageText
 *
 * Macro to translate a message code into a message,
 * using the given default text if no message found.
 -->
<#macro messageText code, text>${springMacroRequestContext.getMessage(code, text)}</#macro>

<#--
 * messageArgs
 *
 * Macro to translate a message code with arguments into a message.
 -->
<#macro messageArgs code, args>${springMacroRequestContext.getMessage(code, args)}</#macro>

<#--
 * messageArgsText
 *
 * Macro to translate a message code with arguments into a message,
 * using the given default text if no message found.
 -->
<#macro messageArgsText code, args, text>${springMacroRequestContext.getMessage(code, args, text)}</#macro>

<#--
 * theme
 *
 * Macro to translate a theme message code into a message.
 -->
<#macro theme code>${springMacroRequestContext.getThemeMessage(code)}</#macro>

<#--
 * themeText
 *
 * Macro to translate a theme message code into a message,
 * using the given default text if no message found.
 -->
<#macro themeText code, text>${springMacroRequestContext.getThemeMessage(code, text)}</#macro>

<#--
 * themeArgs
 *
 * Macro to translate a theme message code with arguments into a message.
 -->
<#macro themeArgs code, args>${springMacroRequestContext.getThemeMessage(code, args)}</#macro>

<#--
 * themeArgsText
 *
 * Macro to translate a theme message code with arguments into a message,
 * using the given default text if no message found.
 -->
<#macro themeArgsText code, args, text>${springMacroRequestContext.getThemeMessage(code, args, text)}</#macro>

<#--
 * url
 *
 * Takes a relative URL and makes it absolute from the server root by
 * adding the context root for the web application.
 -->
<#macro url relativeUrl extra...><#if extra?? && extra?size!=0>${springMacroRequestContext.getContextUrl(relativeUrl,extra)}<#else>${springMacroRequestContext.getContextUrl(relativeUrl)}</#if></#macro>

<#--
 * bind
 *
 * Exposes a BindStatus object for the given bind path, which can be
 * a bean (e.g. "person") to get global errors, or a bean property
 * (e.g. "person.name") to get field errors. Can be called multiple times
 * within a form to bind to multiple command objects and/or field names.
 *
 * This macro will participate in the default HTML escape setting for the given
 * RequestContext. This can be customized by calling "setDefaultHtmlEscape"
 * on the "springMacroRequestContext" context variable, or via the
 * "defaultHtmlEscape" context-param in web.xml (same as for the JSP bind tag).
 * Also regards a "htmlEscape" variable in the namespace of this library.
 *
 * Producing no output, the following context variable will be available
 * each time this macro is referenced (assuming you import this library in
 * your templates with the namespace 'spring'):
 *
 *   spring.status : a BindStatus instance holding the command object name,
 *   expression, value, and error messages and codes for the path supplied
 *
 * @param path : the path (string value) of the value required to bind to.
 *   Spring defaults to a command name of "command" but this can be overridden
 *   by user config.
 -->
<#macro bind path>
    <#if htmlEscape?exists>
        <#assign status = springMacroRequestContext.getBindStatus(path, htmlEscape)>
    <#else>
        <#assign status = springMacroRequestContext.getBindStatus(path)>
    </#if>
    <#-- assign a temporary value, forcing a string representation for any
    kind of variable. This temp value is only used in this macro lib -->
    <#if status.value?exists && status.value?is_boolean>
        <#assign stringStatusValue=status.value?string>
    <#else>
        <#assign stringStatusValue=status.value?default("")>
    </#if>
</#macro>

<#--
 * bindEscaped
 *
 * Similar to spring:bind, but takes an explicit HTML escape flag rather
 * than relying on the default HTML escape setting.
 -->
<#macro bindEscaped path, htmlEscape>
    <#assign status = springMacroRequestContext.getBindStatus(path, htmlEscape)>
    <#-- assign a temporary value, forcing a string representation for any
    kind of variable. This temp value is only used in this macro lib -->
    <#if status.value?exists && status.value?is_boolean>
        <#assign stringStatusValue=status.value?string>
    <#else>
        <#assign stringStatusValue=status.value?default("")>
    </#if>
</#macro>

<#--
 * formInput
 *
 * Display a form input field of type 'text' and bind it to an attribute
 * of a command or bean.
 *
 * @param path the name of the field to bind to
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
 -->
<#macro formInput path id="" attributes="" fieldType="text">
    <@bind path/>
    <#if fieldType != "file" && fieldType != "password">
        <#local value='value="${stringStatusValue}"'/>
    </#if>
    <#if id?has_content>
        <#local idAttr="id='${id!}'"/>
    </#if>
    <input ${idAttr!} type="${fieldType}" name="${status.expression}" ${value!} ${attributes}<@closeTag/>
</#macro>

<#--
 * formPasswordInput
 *
 * Display a form input field of type 'password' and bind it to an attribute
 * of a command or bean. No value will ever be displayed. This functionality
 * can also be obtained by calling the formInput macro with a 'type' parameter
 * of 'password'.
 *
 * @param path the name of the field to bind to
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
 -->
<#macro formPasswordInput path id="" attributes="">
    <@formInput path, id, attributes, "password"/>
</#macro>

<#--
 * formHiddenInput
 *
 * Generate a form input field of type 'hidden' and bind it to an attribute
 * of a command or bean. This functionality can also be obtained by calling
 * the formInput macro with a 'type' parameter of 'hidden'.
 *
 * @param path the name of the field to bind to
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
 -->
<#macro formHiddenInput path id="" attributes="">
    <@formInput path, id, attributes, "hidden"/>
</#macro>

<#--
 * formTextarea
 *
 * Display a text area and bind it to an attribute of a command or bean.
 *
 * @param path the name of the field to bind to
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
 -->
<#macro formTextarea path id="" attributes="">
    <@bind path/>
    <textarea id="${id!}" name="${status.expression}" ${attributes}>${stringStatusValue}</textarea>
</#macro>

<#--
 * formSingleSelect
 *
 * Show a selectbox (dropdown) input element allowing a single value to be chosen from a list of options.
 *
 * @param path the name of the field to bind to
 * @param options a list of key value pairs of all the available options
 * @param attributes any additional attributes for the element (such as class or CSS styles or size
-->
<#macro formSingleSelect path options id="" attributes="">
    <@bind path/>
    <#assign inGroup=false>
    <select id="${id!}" name="${status.expression}" ${attributes}>
       <#list options as option>
          <#if option.label?has_content>
               <#if inGroup>
                   </optgroup>
               </#if>
               <optgroup label="${option.label?html}">
               <#assign inGroup=true>
          <#elseif option.location?has_content && option.location.href?has_content>
              <option data-location="${option.location.href}" value="${option.key?html}"<@checkSelected option.key/>>${option.value?html}</option>
          <#else>
              <option value="${option.key?html}"<@checkSelected option.key/>>${option.value?html}</option>
          </#if>
       </#list>
       <#if inGroup>
           </optgroup>
       </#if>
    </select>
</#macro>

<#--
 * formMultiSelect
 *
 * Show a listbox of options allowing the user to make 0 or more choices from the list of options.
 *
 * @param path the name of the field to bind to
 * @param options a list of key value pairs of all the available options
 * @param attributes any additional attributes for the element (such as class or CSS styles or size
-->
<#macro formMultiSelect path options id="" attributes="">
    <@bind path/>
<#if status.expression = "names">
expression=>${status.expression}<br>
<#if status.actualValue??><#if status.actualValue?is_sequence>actualValue=>${status.actualValue?join(", ")}<#else>actualValue=>${status.actualValue}</#if><#else>actualValue=>null</#if><br>
<#if status.value??><#if status.value?is_sequence>value=>${status.value?join(", ")}<#else>value=>${status.value}</#if><#else>value=>null</#if><br>
<br>
</#if>
    <select multiple="multiple" id="${id!}" name="${status.expression}" ${attributes}>
        <#assign inGroup=false>
        <#list options as option>
            <#if option.label?has_content>
                <#if inGroup>
                    </optgroup>
                </#if>
                <optgroup label="${option.label?html}">
                <#assign inGroup=true>
            <#else>
                <#assign isSelected = contains(status.value?default([""]), option.key)>
                <option value="${option.key?html}"<#if isSelected> selected="selected"</#if>>${option.value?html}</option>
            </#if>
        </#list>
    </select>
    <input type="hidden" name="_${status.expression}" value="on"/>
</#macro>

<#--
 * formRadioButtons
 *
 * Show radio buttons.
 *
 * @param id the id for generated inputs, index is appended with underscore
 * @param path the name of the field to bind to
 * @param options a list of key value pairs of all the available options
 * @param separator the html tag or other character list that should be used to
 *    separate each option. Typically '&nbsp;' or '<br>'
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
-->
<#macro formRadioButtons id path options separator attributes="">
    <#-- Start Kuali enhancements and changes -->
    <#list options as option>
    <@bind path/>
    <#local controlId="${id}_${option_index}">
    <span class="uif-tooltip">
        <input type="radio" id="${controlId}" name="${status.expression}" value="${option.key?html}"<#if stringStatusValue == option.key> checked="checked"</#if> ${attributes}<@closeTag/>
        <#if option.message.richMessage>
            <label for="${controlId}" onclick="handleRadioLabelClick('${controlId}',event); return false;"><@krad.template component=option.message/></label>
        <#else>
            <label for="${controlId}">${option.value!}</label>
        </#if>
    </span>
    <#if option_has_next>
        ${separator}
    </#if>
    </#list>
    <#-- End Kuali enhancements and changes -->
</#macro>

<#--
 * formCheckboxes
 *
 * Show checkboxes.
 *
 * @param id the id for generated inputs, index is appended with underscore
 * @param path the name of the field to bind to
 * @param options a list of KeyValue pairs of all the available options
 * @param separator the html tag or other character list that should be used to
 *    separate each option. Typically '&nbsp;' or '<br>'
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
-->
<#macro formCheckboxes id path options separator attributes="">
    <#-- Start Kuali enhancements and changes -->
    <#list options as option>
    <@bind path/>
    <#local controlId="${id}_${option_index}">
    <#local isSelected = contains(status.value?default([""]), option.key)>
    <span class="uif-tooltip">
        <input type="checkbox" id="${controlId}" name="${status.expression}" value="${option.key?html}"<#if isSelected> checked="checked"</#if> ${attributes}<@closeTag/>
        <#if option.message.richMessage>
            <label onclick="handleCheckboxLabelClick('${controlId}',event); return false;" for="${controlId}"><@krad.template component=option.message/></label>
        <#else>
            <label for="${controlId}">${option.value!}</label>
        </#if>
    </span>
    <#if option_has_next>
        ${separator}
    </#if>
    </#list>
    <input type="hidden" name="_${status.expression}" value="on"/>
    <#-- End Kuali enhancements and changes -->
</#macro>

<#--
 * formCheckbox
 *
 * Show a single checkbox.
 *
 * @param path the name of the field to bind to
 * @param attributes any additional attributes for the element (such as class
 *    or CSS styles or size
-->
<#macro formCheckbox path label id="" attributes="">
    <#-- Start Kuali enhancements and changes -->
    <@bind path />
    <#local name="${status.expression}">
    <#local isSelected = false>
    <#if status.value??>
        <#if status.value?is_sequence>
            <#local start = attributes?index_of("value=\"") + 7 >
            <#local end = attributes?index_of("\"",start) >
            <#local value = attributes?substring(start, end) >
            <#local isSelected = status.value?seq_contains(value)>
        <#else>
            <#local isSelected = status.value?string=="true">
        </#if>
    </#if>
    <input type="hidden" name="_${name}" value="on"/>
    <input type="checkbox" id="${id!}" name="${name}"<#if isSelected> checked="checked"</#if> ${attributes}/>
    <#if label?has_content && label.messageText?has_content>
        <label onclick="handleCheckboxLabelClick('${id}',event); return false;" for="${id}">
            <@krad.template component=label/>
        </label>
    </#if>
    <#-- End Kuali enhancements and changes -->
</#macro>

<#--
 * showErrors
 *
 * Show validation errors for the currently bound field, with
 * optional style attributes.
 *
 * @param separator the html tag or other character list that should be used to
 *    separate each option. Typically '<br>'.
 * @param classOrStyle either the name of a CSS class element (which is defined in
 *    the template or an external CSS file) or an inline style. If the value passed in here
 *    contains a colon (:) then a 'style=' attribute will be used, else a 'class=' attribute
 *    will be used.
-->
<#macro showErrors separator classOrStyle="">
    <#list status.errorMessages as error>
    <#if classOrStyle == "">
        <b>${error}</b>
    <#else>
        <#if classOrStyle?index_of(":") == -1><#assign attr="class"><#else><#assign attr="style"></#if>
        <span ${attr}="${classOrStyle}">${error}</span>
    </#if>
    <#if error_has_next>${separator}</#if>
    </#list>
</#macro>

<#--
 * checkSelected
 *
 * Check a value in a list to see if it is the currently selected value.
 * If so, add the 'selected="selected"' text to the output.
 * Handles values of numeric and string types.
 * This function is used internally but can be accessed by user code if required.
 *
 * @param value the current value in a list iteration
-->
<#macro checkSelected value>
    <#if stringStatusValue?is_number && stringStatusValue?string == value>selected="selected"</#if>
    <#if stringStatusValue?is_string && (stringStatusValue == value || stringStatusValue == value?html)>selected="selected"</#if>
</#macro>

<#--
 * contains
 *
 * Macro to return true if the list contains the scalar, false if not.
 * Surprisingly not a FreeMarker builtin.
 * This function is used internally but can be accessed by user code if required.
 *
 * @param list the list to search for the item OR a string with comma as a delimiter
 * @param item the item to search for in the list
 * @return true if item is found in the list, false otherwise
-->
<#function contains list item>
    <#if list?is_sequence>
        <#list list as nextInList>
            <#if nextInList == item><#return true></#if>
        </#list>
    <#else>
        <#list list?split(",") as nextInList>
            <#if nextInList == item><#return true></#if>
        </#list>
    </#if>
    <#return false>
</#function>

<#--
 * closeTag
 *
 * Simple macro to close an HTML tag that has no body with '>' or '/>',
 * depending on the value of a 'xhtmlCompliant' variable in the namespace
 * of this library.
-->
<#macro closeTag>
    <#if xhtmlCompliant?exists && xhtmlCompliant>/><#else>></#if>
</#macro>
