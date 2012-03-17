/*
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Show growl with message, title and theme passed in
 *
 * @param message message of this jGrowl
 * @param title title of this jGrowl, can be empty string for none
 * @param theme class to append to jGrowl classes, can be empty string for none
 */
function showGrowl(message, title, theme) {
    var context = getContext();
    if (theme) {
        context.jGrowl(message, { header: title, theme: theme});
    }
    else {
        context.jGrowl(message, { header: title});
    }
}

/**
 * Set default growl options for this view
 *
 * @param options
 */
function setGrowlDefaults(options) {
    var context = getContext();
    context.jGrowl.defaults = context.extend(context.jGrowl.defaults, options);
}

/**
 * Uses jQuery plug-in to show a loading notification for a page request. See
 * <link>http://plugins.jquery.com/project/showLoading</link> for documentation
 * on options.
 *
 * @param showLoading -
 *          boolean that indicates whether the loading indicator should be shown
 *          (true) or hidden (false)
 */
function createLoading(showLoading) {
    var loadingMessage =  '<h1><img src="' + getConfigParam("kradImageLocation") + 'loading.gif" alt="working..." />Loading...</h1>';
    var savingMessage = '<h1><img src="' + getConfigParam("kradImageLocation") + 'loading.gif" alt="working..." />Saving...</h1>';

    var methodToCall = jq("input[name='methodToCall']").val();
    var unblockUIOnLoading = jq("input[name='unblockUIOnLoading']").val();

    if (unblockUIOnLoading == null || unblockUIOnLoading.toUpperCase() == "false".toUpperCase()) {
        if (top == self) {
            //no portal
            if (showLoading) {
                if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                    jq.blockUI({message: savingMessage});
                }
                else {
                    jq.blockUI({message: loadingMessage});
                }
            }
            else {
                jq.unblockUI();
            }
        }
        else if (top.jq == null) {
            if (showLoading) {
                if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                    top.$.blockUI({message: savingMessage});
                }
                else {
                    top.$.blockUI({message: loadingMessage});
                }
            }
            else {
                top.$.unblockUI();
            }
        }
        else {
            if (showLoading) {
                if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                    top.jq.blockUI({message: savingMessage});
                }
                else {
                    top.jq.blockUI({message: loadingMessage});
                }
            }
            else {
                top.jq.unblockUI();
            }
        }
    }
}

function clearServerErrorColors(errorDivId){
    if (errorDivId) {
        var div = jq("#" + errorDivId);
        var label = jq("#" + errorDivId.replace("errors", "label"));
        var highlightLine = "";

        //check to see if the option to highlight fields is on
        if (div.length > 0 && !div.hasClass("noHighlight")) {
            if (div.parent().is("td") || (div.parent().is(".refreshWrapper") && div.parent().parent().is("td"))) {
                highlightLine = div.closest("td");
            }
            else {
                highlightLine = div.closest(".fieldLine");
            }

            if (highlightLine.length > 0) {
                highlightLine.removeClass("kr-serverError");
            }
        }
    }
}

/**
 * Applies the error coloring for fields with errors, warnings, or information
 */
function applyErrorColors(errorDivId, errorNum, warningNum, infoNum, clientSide) {
    if (errorDivId) {
        var div = jq("#" + errorDivId);
        var label = jq("#" + errorDivId.replace("errors", "label"));
        var highlightLine = "";

        //check to see if the option to highlight fields is on
        if (div.length > 0 && !div.hasClass("noHighlight")) {
            if (div.parent().is("td") || (div.parent().is(".refreshWrapper") && div.parent().parent().is("td"))) {
                highlightLine = div.closest("td");
            }
            else {
                highlightLine = div.closest(".fieldLine");
            }

            if (highlightLine.length > 0) {

                if (errorNum && !clientSide) {

                    highlightLine.addClass("kr-serverError");
                    label.addClass("kr-serverError");
                }
                else if (errorNum) {
                    highlightLine.addClass("kr-clientError");
                    label.addClass("kr-clientError");
                }
                else if (warningNum) {
                    highlightLine.addClass("kr-warning");
                    label.addClass("kr-warning");
                }
                else if (infoNum) {
                    highlightLine.addClass("kr-information");
                    label.addClass("kr-information");
                }
                else {
                    //we are only removing errors client side - no knowledge of warnings/infos
                    if (div.parent().hasClass("kr-errorsField")) {
                        var error_ul = div.parent().find(".kr-errorMessages").find("ul.errorLines");
                        var moreErrors = false;
                        error_ul.each(function() {
                            jq(this).children().each(function() {
                                if (jq(this).css("display") != "none") {
                                    moreErrors = true;
                                    return false;
                                }
                            });
                            if (moreErrors) {
                                return false;
                            }
                        });

                        label.removeClass("kr-clientError");
                        if (!moreErrors) {
                            highlightLine.removeClass("kr-clientError");
                        }
                    }
                    else {
                        highlightLine.removeClass("kr-clientError");
                        label.removeClass("kr-clientError");
                    }
                }
            }
        }

        //highlight tab that contains errors - no setting to turn this off because it is necessary
        var tabDiv = div.closest(".ui-tabs-panel");
        if (tabDiv.length > 0) {
            var tabId = tabDiv.attr("id");
            var tabAnchor = jq("a[href='#" + tabId + "']");
            var errorIcon = jq("#" + tabId + "_errorIcon");

            if (tabAnchor.length > 0) {
                var hasErrors = false;
                if (errorNum) {
                    hasErrors = true;
                }
                else {
                    var error_li = tabDiv.find(".kr-errorMessages").find("li");
                    error_li.each(function() {
                        if (jq(this).css("display") != "none") {
                            hasErrors = true;
                        }
                    });
                }

                if (hasErrors) {
                    tabAnchor.addClass("kr-clientError");
                    if (errorIcon.length == 0) {
                        tabAnchor.append("<img id='" + tabId + "_errorIcon' alt='error' src='" + getConfigParam("kradImageLocation") + "'errormark.gif'>");
                    }
                }
                else if (!hasErrors) {
                    tabAnchor.removeClass("kr-clientError");
                    errorIcon.remove();
                }
            }
        }
    }
}

/**
 * Shows the field error icon if errorCount is greater than one and errorsField
 * has the option turned on
 */
function showFieldIcon(errorsDivId, errorCount) {
    if (errorsDivId) {
        var div = jq("#" + errorsDivId);
        var inputId = errorsDivId.replace("_errors", "");

        if (inputId) {
            var input = jq("#" + inputId);
            var errorIcon = jq("#" + inputId + "_errorIcon");

            if (div.length > 0 && div.hasClass("addFieldIcon") && errorCount && errorIcon.length == 0) {
                if (input.length > 0) {
                    input.after("<img id='" + inputId + "_errorIcon' alt='error' src='" + getConfigParam("kradImageLocation") + "errormark.gif'>");
                }
                else {
                    // try for radios and checkboxes
                    input = jq("#" + errorDivId.replace("errors", "attribute1"));
                    if (input.length > 0) {
                        input.after("<img id='" + inputId + "_errorIcon' alt='error' src='" + getConfigParam("kradImageLocation") + "errormark.gif'>");
                    }
                }
            }
            else if (div.length > 0 && div.hasClass("addFieldIcon") && errorCount == 0) {
                if (errorIcon.length > 0) {
                    errorIcon.remove();
                }
            }
        }
    }
}

/**
 * Adds the icon that indicates the contents of a field have changed from the compared value (for instance the new side
 * on maintenance documents) to the field markers span
 *
 * @param fieldId - id for the field the icon should be added to
 */
function showChangeIcon(fieldId) {
    var fieldMarkerSpan = jq("#" + fieldId + "_attribute_markers");
    var fieldIcon = jq("#" + fieldId + "_changeIcon");

    if (fieldMarkerSpan.length > 0 && fieldIcon.length == 0) {
        fieldMarkerSpan.append("<img id='" + fieldId + "_changeIcon' alt='change' src='" + getConfigParam("kradImageLocation") + "asterisk_orange.png'>");
    }
}

/**
 * Add icon to a group header that indicates the data for the group has changed
 *
 * @param headerFieldId - id for the header field the icon should be added to
 */
function showChangeIconOnHeader(headerFieldId) {
    showChangeIconOnGroupHeader(headerFieldId, "_div");
}

/**
 * Add icon to a group header that indicates the data for the group has changed
 *
 * @param headerFieldId - id for the header field the icon should be added to
 */
function showChangeIconOnDisclosure(headerFieldId) {
    showChangeIconOnGroupHeader(headerFieldId, "_toggle");
}

/**
 * Add icon to a group header element (disclosure/header) that indicates the data for the group has changed
 *
 * @param fieldId - id for the header field the icon should be added to
 */
function showChangeIconOnGroupHeader(fieldId, idSuffix) {
    var targetElement = jq("#" + fieldId + idSuffix).find("[class~=uif-headerText]");
    var headerIcon = jq("#" + fieldId + "_changeIcon");

    if (targetElement.length > 0 && headerIcon.length == 0) {
        targetElement.append("<img id='" + fieldId + "_changeIcon' class='uif-changedHeaderIcon' alt='change' src='" + getConfigParam("kradImageLocation") + "asterisk_orange.png'>");
    }
}

// Applies the watermark to the input with the id specified
function createWatermark(id, watermark) {
    jq("#" + id).watermark(watermark);
}

/**
 * If the content is an incident report view, replaces the current view with the incident report and
 * returns true, otherwise returns false
 *
 * @param content
 * @returns {Boolean} true if there was an incident, false otherwise
 */
function handleIncidentReport(content) {
    var viewId = jq("#viewId", content);
    if (viewId.length && viewId.val() === "Uif-IncidentReportView") {
        jq('#view_div').replaceWith(content);
        runHiddenScriptsAgain();
        return true;
    }
    else {
        return false;
    }
}



function writeMessagesAtField(id){

    var data = jQuery("#" + id).data("validationMessages");

    if(!data.errors){
        data.errors = [];
    }
    if(!data.warnings){
        data.warnings = [];
    }
    if(!data.info){
        data.info = [];
    }

    var messagesDiv = jQuery("[data-messagesFor='"+ id +"']");
    jQuery(messagesDiv).hide();
    jQuery(messagesDiv).empty();

    var clientMessages = jQuery("<div class='uif-clientMessageItems'><ul>"
                + generateListItems(data.errors, "uif-errorMessageItem", 0, false)
                + generateListItems(data.warnings, "uif-warningMessageItem", 0, false)
                + generateListItems(data.info, "uif-infoMessageItem", 0, false) + "</ul></div>");

    var serverMessages = jQuery("<div class='uif-serverMessageItems'><ul>"
            + generateListItems(data.serverErrors, "uif-errorMessageItem", 0, false)
            + generateListItems(data.serverWarnings, "uif-warningMessageItem", 0, false)
            + generateListItems(data.serverInfo, "uif-infoMessageItem", 0, false) + "</ul></div>");

    if(jQuery(clientMessages).find("ul").children().length){
        jQuery(clientMessages).appendTo(messagesDiv);
    }

    if(jQuery(serverMessages).find("ul").children().length){
        jQuery(serverMessages).appendTo(messagesDiv);
    }

    var showImage = true;
    if(jQuery("#" + id).parent().is("td")){
        showImage = false;
    }

    jQuery("#" + id + " > .uif-validationImage").remove();
    //show appropriate icons/styles
    if(jQuery(messagesDiv).find(".uif-errorMessageItem").length){
        jQuery("#" + id).addClass("uif-hasError");
        if(showImage){
            jQuery(messagesDiv).before(errorImage);
        }
        handleTabStyle(id, true, false, false);
    }
    else if(jQuery(messagesDiv).find(".uif-warningMessageItem").length){
        jQuery("#" + id).addClass("uif-hasWarning");
        if(showImage){
            jQuery(messagesDiv).before(warningImage);
        }
        handleTabStyle(id, false, true, false);
    }
    else if(jQuery(messagesDiv).find(".uif-infoMessageItem").length){
        jQuery("#" + id).addClass("uif-hasInfo");
        if(showImage){
            jQuery(messagesDiv).before(infoImage);
        }
        handleTabStyle(id, false, false, true);
    }
    else{
        jQuery("#" + id).removeClass("uif-hasError");
        jQuery("#" + id).removeClass("uif-hasWarning");
        jQuery("#" + id).removeClass("uif-hasInfo");
        handleTabStyle(id, false, false, false);
    }
}

function handleMessagesAtField(id){
    var skip = jQuery("#" + id).data("vignore");
    if(!(skip == "yes")){
        var data = jQuery("#" + id).data("validationMessages");

        writeMessagesAtField(id);

        var parent = jQuery("#" + id).data("parent");

        if(parent){
            handleMessagesAtGroup(parent, id, data);
        }
    }
}

function handleMessagesAtGroup(id, fieldId, fieldData){
    var data = jQuery("#" + id).data("validationMessages");
    var pageLevel = false;

    if(data){
        var order = data.order;
        var sections = data.sections;
        var messageMap = data.messageMap;
        pageLevel = data.pageLevel;

        //init empty params
        if(!data.errors){
            data.errors = [];
        }
        if(!data.warnings){
            data.warnings = [];
        }
        if(!data.info){
            data.info = [];
        }
        if(!messageMap){
            messageMap = {};
        }



        //add fresh data to group's message data based on the new field info
        messageMap[fieldId] = fieldData;
        data.messageMap = messageMap;
        data = calculateMessageTotals(data);

        //update data
        jQuery("#" + id).data("validationMessages", data);

        var sectionHeader = jQuery("[data-headerFor='" + id + "']").find("> :header, > label, > a > :header, > a > label");
        var isSection = sectionHeader.length || data.forceShow;

        if(isSection && (data.displayErrors || data.displayWarnings || data.displayInfo)){

            var newList = jQuery("<ul class='uif-validationMessagesList'></ul>");
            newList = generateSectionLevelMessages(id, data, newList);
            
            if(data.summarize){
                newList = generateSummaries(id, messageMap, sections, order, newList);
            }
            else{
                //if not generating summaries just output field links
                for(var key in messageMap){
                    var link = generateFieldLink(messageMap[key], key);
                    newList = writeMessageItemToList(link, newList);
                }
            }
        }

        if(isSection){
            //clear and write the new list of summary items
            clearMessages(id);
            //TODO add flag for checking if you want to display message count in header
            displayHeaderMessageCount(id, data);
            handleTabStyle(id, data.errorTotal, data.warningTotal, data.infoTotal);
            writeMessages(id, newList);
            if(pageLevel){
                if(newList.children().length){
                    var messagesDiv = jQuery("[data-messagesFor='"+ id +"']");
                    var countMessage = generateCountString(data.errorTotal, data.warningTotal,
                            data.infoTotal);
                    var pageDisclosureLink = jQuery("<a class='uif-pageValidationDisclosureLink' "
                            + "href='#'><h3 id='pageValidationDisclosure'>The Page submission has "+ countMessage +"</h2></a>");
                    jQuery(pageDisclosureLink).toggle(
                        function() {
                            jQuery(".uif-validationMessagesList").slideDown(250);
                        }, function() {
                            jQuery(".uif-validationMessagesList").slideUp(250);
                        }
                    );
                    jQuery(messagesDiv).prepend(pageDisclosureLink);
                    jQuery(messagesDiv).find(".uif-validationMessagesList").attr("id", "pageValidationList");
                    jQuery(messagesDiv).find(".uif-validationMessagesList").attr("aria-labelledby",
                            "pageValidationDisclosure");
                }
            }
        }
    }
    var parent = jQuery("#" + id).data("parent");
    if(!pageLevel && parent != null && parent != undefined && parent != ""){
        handleMessagesAtGroup(parent, fieldId, fieldData);
    }
}

function handleTabStyle(id, error, warning, info){
    var tabWrapper = jQuery("[data-tabWrapperFor='"+ id + "']");
    if(tabWrapper.length){
        var tab = jQuery("[data-tabFor='"+ id + "']");
        tab.find(".uif-validationImage").remove();
        if(error){
            tab.find("a").append(errorImage);
        }
        else if(warning){
            tab.find("a").append(warningImage);
        }
        else if(info){
            tab.find("a").append(infoImage);
        }
    }
}

function generateSectionLevelMessages(id, data, newList){
    if(data != undefined && data != null){
        //Write all message items for section
        var linkType = "uif-errorMessageItem";
        var errors = jQuery(generateListItems(data.errors, "uif-errorMessageItem", 0, true)
                + generateListItems(data.serverErrors, "uif-errorMessageItem", 0, true));
        var warnings = jQuery(generateListItems(data.warnings, "uif-warningMessageItem", 0, true)
                + generateListItems(data.serverWarnings, "uif-warningMessageItem", 0, true));
        var info = jQuery(generateListItems(data.info, "uif-infoMessageItem", 0, true)
                + generateListItems(data.serverInfo, "uif-infoMessageItem", 0, true));

        //Write all items to the list
        newList = writeMessageItemToList(errors, newList);
        newList = writeMessageItemToList(warnings, newList);
        newList = writeMessageItemToList(info, newList);
    }
    return newList;
}

function calculateMessageTotals(data){
    var errorTotal = 0;
    var warningTotal = 0;
    var infoTotal = 0;
    var messageMap = data.messageMap;
    //Add totals for messages of fields in group
    for(var id in messageMap){
        var currentData = messageMap[id];
        errorTotal = errorTotal + currentData.serverErrors.length + currentData.errors.length;
        warningTotal = warningTotal + currentData.serverWarnings.length + currentData.warnings.length;
        infoTotal = infoTotal + currentData.serverInfo.length + currentData.info.length;
    }
    //Add totals for messages for THIS Group
    data.errorTotal = errorTotal + data.serverErrors.length + data.errors.length;
    data.warningTotal = warningTotal + data.serverWarnings.length + data.warnings.length;
    data.infoTotal = infoTotal + data.serverInfo.length + data.info.length;
    data.messageTotal = errorTotal + warningTotal + infoTotal;
    return data;
}

function displayHeaderMessageCount(sectionId, sectionData){
    var sectionHeader = jQuery("[data-headerFor='" + sectionId + "']").find("> :header, > label, > a > :header, > a > label");
    if(sectionHeader.length){

        var countMessage = generateCountString(sectionData.errorTotal, sectionData.warningTotal, sectionData.infoTotal);
        var image = "";
        if(sectionData.errorTotal){
            image = errorImage;
        }
        else if(sectionData.warningTotal){
            image = warningImage;
        }
        else if(sectionData.infoTotal){
            image = infoImage;
        }

        jQuery(sectionHeader).find("span.uif-messageCount").remove();

        if(countMessage != ""){
            jQuery("<span class='uif-messageCount'> - "+ countMessage + " " + image +"</span>").appendTo(sectionHeader);
        }
    }
}

function generateCountString(errorTotal, warningTotal, infoTotal){
    var countMessage = "";
    if(errorTotal){
        if(errorTotal == 1){
            countMessage = errorTotal + " error";
        }
        else{
            countMessage = errorTotal + " errors";
        }
    }

    if((errorTotal > 0) + (warningTotal > 0) +(infoTotal > 0) == 3){
        countMessage = countMessage + " & " + (warningTotal + infoTotal) + " other messages";
    }
    else{

        if(warningTotal){
            if(countMessage != ""){
                countMessage = countMessage + " & ";
            }

            if(warningTotal == 1){
                countMessage = countMessage + warningTotal + " warning";
            }
            else{
                countMessage = countMessage + warningTotal + " warnings";
            }
        }

        if(infoTotal){
            if(countMessage != ""){
                countMessage = countMessage + " & ";
            }

            if(infoTotal == 1){
                countMessage = countMessage + infoTotal + " message";
            }
            else{
                countMessage = countMessage + infoTotal + " messages";
            }
        }
    }
    return countMessage;
}

function clearMessages(messagesForId){
    var messagesDiv = jQuery("[data-messagesFor='"+ messagesForId +"']");
    jQuery(messagesDiv).empty();
}

function writeMessages(messagesForId, newList){
    var messagesDiv = jQuery("[data-messagesFor='"+ messagesForId +"']");
    if(newList.children().length){
        jQuery(messagesDiv).show();
        jQuery(newList).appendTo(messagesDiv);
        messageSummariesShown = true;
    }
    else{
        jQuery(messagesDiv).hide();
    }
}

function writeMessageItemToList(item, newList){
    if(item != null){
        jQuery(item).appendTo(newList);
    }
    return newList;
}

function generateListItems(messageArray, itemClass, startIndex, focusable){
    var elements = "";
    if(messageArray.length){
        for(var i = startIndex; i < messageArray.length; i++){
            if(focusable){
                elements = elements + "<li tabindex='0' class='"+ itemClass +"'>" + messageArray[i] + "</li>";
            }
            else{
                elements = elements + "<li class='"+ itemClass +"'>" + messageArray[i] + "</li>";
            }
        }
    }
    return elements;
}

function generateSummaries(id, messageMap, sections, order, newList){
    //if no nested sections just output fieldLinks
    if(sections.length == 0){
        for(var key in messageMap){
            var link = generateFieldLink(messageMap[key], key);
            newList = writeMessageItemToList(link, newList);
        }
    }
    else{
        var currentFields = {};
        var currentSectionId;
        jQuery.each(order, function(index, value){
            //if it doesn't start with an s$ its not a section or f$ its not a field group
            if(!(value.indexOf("s$") == 0) && !(value.indexOf("f$") == 0)){
                currentFields[index] = value;
            }
            else{
                currentSectionId = value.substring(2);
                var disclosureLink = generateFieldLinkDisclosure(currentFields, messageMap, currentSectionId, true);
                newList = writeMessageItemToList(disclosureLink, newList);
                var sectionId = value.slice(2);
                var summaryLink = generateSummaryLink(sectionId);
                newList = writeMessageItemToList(summaryLink, newList);
                currentFields = {};
            }
        });

        if(currentFields.length > 0){
            var disclosureLink = generateFieldLinkDisclosure(currentFields, messageMap, currentSectionId, false);
            newList = writeMessageItemToList(disclosureLink, newList);
        }
    }
    return newList;
}



function generateFieldLink(messageData, fieldId){
    var link = null;

    if(messageData != null){
        var linkType;
        var highlight;
        var collapse = false;
        var linkText = "";
        var separator = "";
        var collapsedErrors = {};
        var collapsedWarnings = {};
        var collapsedInfo = {};
        var image = "";

        if(messageData.serverErrors.length || messageData.errors.length){
            collapse = true;
            image = errorImage;
            linkType = "uif-errorMessageItem";
            highlight = "uif-errorHighlight";
            if(messageData.errors.length){
                linkText = "<span class='uif-validationMessageLink-client'>" + messageData.errors[0] +"</span>";
                if(messageData.errors.length > 1){
                    collapsedErrors.exist = true;
                    collapsedErrors.errorIndex = 1;
                }
            }
            if(messageData.serverErrors.length){
                if(linkText){
                    separator = ", ";    
                }
                linkText = linkText + "<span class='uif-validationMessageLink-server'>" 
                        + separator + messageData.serverErrors[0] +"</span>";
                if(messageData.serverErrors.length > 1){
                    collapsedErrors.exist = true;
                    collapsedErrors.serverErrorIndex = 1;
                }    
            }
        }
        if(messageData.serverWarnings.length || messageData.warnings.length){
            if(collapse){
                if(messageData.warnings.length){
                    collapsedWarnings.exist = true;
                    collapsedWarnings.warningIndex = 0;
                }
                if(messageData.serverWarnings.length){
                    collapsedWarnings.exist = true;
                    collapsedWarnings.serverWarningIndex = 0;
                }
            }
            else{
                collapse = true;
                image = warningImage;
                linkType = "uif-warningMessageItem";
                highlight = "uif-warningHighlight";
                if(messageData.warnings.length){
                    linkText = "<span class='uif-validationMessageLink-client'>" + messageData.warnings[0] +"</span>";
                    if(messageData.warnings.length > 1){
                        collapsedWarnings.exist = true;
                        collapsedWarnings.warningIndex = 1;
                    }
                }
                if(messageData.serverWarnings.length){
                    if(linkText){
                        separator = ", ";    
                    }
                    linkText = linkText + "<span class='uif-validationMessageLink-server'>" 
                            + separator + messageData.serverWarnings[0] +"</span>";
                    if(messageData.serverWarnings.length > 1){
                        collapsedWarnings.exist = true;
                        collapsedWarnings.serverWarningIndex = 1;
                    }    
                }
            }
        }
        if(messageData.serverInfo.length || messageData.info.length){
            if(collapse){
                if(messageData.info.length){
                    collapsedInfo.exist = true;
                    collapsedInfo.infoIndex = 0;
                }
                if(messageData.serverInfo.length){
                    collapsedInfo.exist = true;
                    collapsedInfo.serverInfoIndex = 0;
                }
            }
            else{
                collapse = true;
                image = infoImage;
                linkType = "uif-infoMessageItem";
                highlight = "uif-infoHighlight";
                if(messageData.info.length){
                    linkText = "<span class='uif-validationMessageLink-client'>" + messageData.info[0] +"</span>";
                    if(messageData.info.length > 1){
                        collapsedInfo.exist = true;
                        collapsedInfo.infoIndex = 1;
                    }
                }
                if(messageData.serverInfo.length){
                    if(linkText){
                        separator = ", ";    
                    }
                    linkText = linkText + "<span class='uif-validationMessageLink-server'>" 
                            + separator + messageData.serverInfo[0] +"</span>";
                    if(messageData.serverInfo.length > 1){
                        collapsedInfo.exist = true;
                        collapsedInfo.serverInfoIndex = 1;
                    }    
                }
            }
        }

        if(linkText != ""){
            var collapsedElements = "";
            if(collapsedErrors.exist){
                var count = 0;
                if(collapsedErrors.errorIndex != undefined && collapsedErrors.errorIndex >= 0){
                    count = count + messageData.errors.length - collapsedErrors.errorIndex;
                }
                if(collapsedErrors.serverErrorIndex != undefined && collapsedErrors.serverErrorIndex >= 0){
                    count = count + messageData.serverErrors.length - collapsedErrors.serverErrorIndex;
                }

                if(count > 1){
                    collapsedElements = collapsedElements + "<span class='uif-collapsedErrors'>[+"
                            + count +" errors]</span>";
                }
                else{
                    collapsedElements = collapsedElements + "<span class='uif-collapsedErrors'>[+"
                            + count +" error]</span>";
                }
            }
            if(collapsedWarnings.exist){
                var count = 0;
                if(collapsedWarnings.warningIndex != undefined && collapsedWarnings.warningIndex >= 0){
                    count = count + messageData.warnings.length - collapsedWarnings.warningIndex;
                }
                if(collapsedWarnings.serverWarningIndex != undefined && collapsedWarnings.serverWarningIndex >= 0){
                    count = count + messageData.serverWarnings.length - collapsedWarnings.serverWarningIndex;
                }

                if(count > 1){
                    collapsedElements = collapsedElements + "<span class='uif-collapsedWarnings'>[+"
                            + count +" warnings]</span>";
                }
                else{
                    collapsedElements = collapsedElements + "<span class='uif-collapsedWarnings'>[+"
                            + count +" warning]</span>";
                }
            }
            if(collapsedInfo.exist){
                var count = 0;
                if(collapsedInfo.infoIndex != undefined && collapsedInfo.infoIndex >= 0){
                    count = count + messageData.info.length - collapsedInfo.infoIndex;
                }
                if(collapsedInfo.serverInfoIndex != undefined && collapsedInfo.serverInfoIndex >= 0){
                    count = count + messageData.serverInfo.length - collapsedInfo.serverInfoIndex;
                }

                if(count > 1){
                    collapsedElements = collapsedElements + "<span class='uif-collapsedInfo'>[+"
                            + count +" messages]</span>";
                }
                else{
                    collapsedElements = collapsedElements + "<span class='uif-collapsedInfo'>[+"
                            + count +" message]</span>";
                }
            }

            var name = jQuery("#" + fieldId).data("label");
            if(name){
                name = name + ": ";
            }
            else{
                name = "";
            }

            link = jQuery("<li data-messageItemFor='"+ fieldId + "'>"+ image +"<a href='#'>"
                    + name + linkText
                    + " " + collapsedElements + "</a> </li>");
            jQuery(link).addClass(linkType);
            jQuery(link).find("a").click(function(){
                var control = jQuery("#" + fieldId + "_control");
                if(control.length){
                    jQuery(control).focus();
                }
                else{
                    jQuery("#" + fieldId + "_control1").focus();
                }
            });
            jQuery(link).find("a").focus(function(){
                jQuery("#" + fieldId).addClass(highlight);
            });
            jQuery(link).find("a").blur(function(){
                jQuery("#" + fieldId).removeClass(highlight);
            });
            jQuery(link).find("a").hover(
            function(){
                jQuery("#" + fieldId).addClass(highlight);
            },
            function(){
                jQuery("#" + fieldId).removeClass(highlight);
            });
        }
    }

    return link;
}

function generateFieldLinkDisclosure(currentFields, messageMap, sectionId, before){

    var sectionTitle = jQuery("[data-headerFor='" + sectionId + "']").find("> :header, > label, > a > :header, > a > label").html();
    var sectionType = "section";
    if(sectionTitle == null){
        //field group case
        sectionTitle = jQuery("#" + sectionId).data("label");
        sectionType = "field group";
    }

    var disclosureText = "";
    var links = [];
    var errorCount = 0;
    var warningCount = 0;
    var infoCount = 0;
    var disclosureLink = null;
    var image = "";
    var linkType = "";

    for(var i in currentFields){
        if(currentFields[i] != null){

            var fieldId = currentFields[i];

            var messageData = messageMap[fieldId];
                if(messageData != undefined && messageData != null){
                errorCount = errorCount + messageData.serverErrors.length + messageData.errors.length;
                warningCount = warningCount + messageData.serverWarnings.length + messageData.warnings.length;
                infoCount = infoCount + messageData.serverInfo.length + messageData.info.length;

                var link = generateFieldLink(messageData, fieldId);
                if(link != null){
                    links.push(link);
                }
            }
        }
    }
    if(errorCount || warningCount || infoCount){
        var locationText = "before";
        if(!before){
            locationText = "after";
        }

        if(errorCount){
            image = errorImage;
            linkType = "uif-errorMessageItem";
        }
        else if(warningCount){
            image = warningImage;
            linkType = "uif-warningMessageItem";
        }
        else if(infoCount){
            image = infoImage;
            linkType = "uif-infoMessageItem";
        }

        var countMessage = generateCountString(errorCount, warningCount, infoCount);

        disclosureText = countMessage + " " + locationText + " the \"" + sectionTitle + "\" " + sectionType;

        if(links.length){
            var subSummary = jQuery("<ul></ul>");
            for(var i in links){
                jQuery(links[i]).appendTo(subSummary)
            }
            //jQuery(subSummary).hide();
        }

        //write disclosure link and div
        //disclosureLink = jQuery("<li class='"+ linkType +"'><a href='#'>" + disclosureText + "</a></li>");
        disclosureLink = jQuery("<li tabindex='0' class='"+ linkType +"'>" + disclosureText + "</li>");
        jQuery(disclosureLink).find(".uif-messageCount").remove();
        jQuery(disclosureLink).find("img").remove();
        jQuery(disclosureLink).prepend(image);
/*        jQuery(disclosureLink).find("a").toggle(
            function() {
                jQuery(subSummary).slideDown(250);
                jQuery(subSummary).find("li:first > a").focus();
            }, function() {
                jQuery(subSummary).slideUp(250);
            }
        );*/
        jQuery(subSummary).appendTo(disclosureLink);
    }
    return disclosureLink;
}

function generateSummaryLink(sectionId){
    //determine section title and section type
    var sectionTitle = jQuery("[data-headerFor='" + sectionId + "']").find("> :header, > label, > a > :header, > a > label").html();
    var sectionType = "section";
    if(sectionTitle == null){
        //field group case
        sectionTitle = jQuery("#" + sectionId).data("label");
        sectionType = "field group";
        sectionId =  jQuery("#" + sectionId).data("group");
    }

    var sectionData = jQuery("#" + sectionId).data("validationMessages");
    var summaryLink = null;
    var summaryMessage = "";
    var image = "";
    var linkType = "";
    var highlight = "";

    if(sectionData.messageTotal){
        var countMessage = generateCountString(sectionData.errorTotal, sectionData.warningTotal, sectionData.infoTotal);

        summaryMessage = "The \"" + sectionTitle + "\" " + sectionType + " has " + countMessage;
    }

    if(summaryMessage != ""){
        if(sectionData.errorTotal){
            image = errorImage;
            linkType = "uif-errorMessageItem";
            highlight = "uif-errorHighlight-section";
        }
        else if(sectionData.warningTotal){
            image = warningImage;
            linkType = "uif-warningMessageItem";
            highlight = "uif-warningHighlight-section";
        }
        else if(sectionData.infoTotal){
            image = infoImage;
            linkType = "uif-infoMessageItem";
            highlight = "uif-infoHighlight-section";
        }
        summaryLink = jQuery("<li data-messageItemFor='"+ sectionId + "' class='"+ linkType +"'><a href='#'>"
                + summaryMessage + "</a></li>");
        jQuery(summaryLink).find(".uif-messageCount").remove();
        jQuery(summaryLink).find("img").remove();
        jQuery(summaryLink).prepend(image);

        jQuery(summaryLink).find("a").click(function(){
            var header = jQuery("[data-headerFor='" + sectionId + "']").find("> :header, > label, > a > :header, > a > label");
            if(header.length){
                if(jQuery(header).parent().is("a")){
                    jQuery(header).parent().focus();
                }
                else{
                    jQuery(header).bind("blur.validation", function(){
                        jQuery(this).removeAttr("tabindex");
                        jQuery(this).unbind("blur.validation");
                    });
                    jQuery(header).attr("tabindex", "0");
                    jQuery(header).focus();
                }
            }
            else{
                var firstItem = jQuery("[data-messagesFor='" + sectionId + "'] > ul > li:first > a");
                if(firstItem.length){
                    jQuery(firstItem).focus();
                }
            }
        });

        jQuery(summaryLink).find("a").focus(function(){
            jQuery("#" + sectionId).addClass(highlight);
        });
        jQuery(summaryLink).find("a").blur(function(){
            jQuery("#" + sectionId).removeClass(highlight);
        });
        jQuery(summaryLink).find("a").hover(
        function(){
            jQuery("#" + sectionId).addClass(highlight);
        },
        function(){
            jQuery("#" + sectionId).removeClass(highlight);
        });
    }
    return summaryLink;
}