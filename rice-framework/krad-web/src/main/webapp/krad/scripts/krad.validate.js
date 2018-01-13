/*
 * Copyright 2005-2018 The Kuali Foundation
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
var prevPageMessageTotal = 0;

/**
 * Get validation data for the component element by merging custom settings with defaults
 *
 * @param jqComponent a jq object that represents the component to retrieve validation data for
 * @param isGroup true if the component represents a group, false for fields
 * @return {*} data that represent validation message data and options for the component
 */
function getValidationData(jqComponent, isGroup) {
    var data = jqComponent.data(kradVariables.VALIDATION_MESSAGES);

    // determine defaults
    var defaults;
    if (isGroup) {
        defaults = groupValidationDefaults;
    }
    else {
        defaults = fieldValidationDefaults;
    }

    if (!defaults) {
        defaults = {};
    }

    // merge defaults into data if it exists, otherwise defaults are the data
    if (data) {
        data = jQuery.extend({}, defaults, data);
        jqComponent.data(kradVariables.VALIDATION_MESSAGES, data);
    }
    else {
        data = jQuery.extend({}, defaults);
        jqComponent.data(kradVariables.VALIDATION_MESSAGES, data);
    }

    return data;
}

/**
 * Hide the message tooltip associated with the field by id
 * @param fieldId the id of the field
 */
function hideMessageTooltip(fieldId) {

    var elementInfo = getHoverElement(fieldId);
    var element = elementInfo.element;
    if (elementInfo.type == "fieldset") {
        //for checkbox/radio fieldsets we put the tooltip on the label of the first input
        element = jQuery(element).filter(".uif-tooltip");
    }

    var popoverData = element.data(kradVariables.POPOVER_DATA);
    if (!popoverData) {
        return;
    }

    var data = getValidationData(jQuery("#" + fieldId));
    if (data && data.showTimer) {
        clearTimeout(data.showTimer);
    }

    var popover = jQuery(element).next(".popover");
    if (data.tooltipTheme) {
        popover.removeClass(data.tooltipTheme);
    }

    element.popover("hide");
    element.data(kradVariables.POPOVER_DATA).shown = false;
}

/**
 * Gets the hover elements for a field by id.  The hover elements are the elements which will cause the tooltip to
 * be shown, the element the tooltip is actually placed on is an item its hover elements.
 * @param fieldId the id of the field
 */
function getHoverElement(fieldId) {
    var fieldset = jQuery("#" + fieldId).find("fieldset");
    var hasFieldset = fieldset.length;
    var elementInfo = {};

    if (!hasFieldset || (hasFieldset && fieldset.data("type") == "InputSet")) {
        //regular case
        elementInfo.element = jQuery("#" + fieldId).find("input:text, input:password, input:file, input:checkbox, "
                + "select, textarea");
        elementInfo.type = "";
        if (elementInfo.element.is("input:checkbox")) {
            elementInfo.themeMargins = {
                total: '13px',
                difference: '2px'
            };
        }
    }
    else if (hasFieldset && jQuery("#" + fieldId).find("fieldset > span > input").length) {
        //radio and checkbox fieldset case
        //get the fieldset, the inputs its associated with, and the associated labels as hover elements
        elementInfo.element = jQuery("#" + fieldId).find("fieldset, fieldset > span > input:radio,"
                + "fieldset > span > input:checkbox, fieldset > span > label, .uif-tooltip");
        elementInfo.type = "fieldset";
        elementInfo.themeMargins = {
            total: '13px',
            difference: '2px'
        };
    }
    else {
        //not found or wrapping fieldset case
        elementInfo.element = [];
        elementInfo.type = "";
    }
    return elementInfo;
}

/**
 * Calculate the margin to be used based on the tooltipElement - adds left margin to allow center placement
 * @param tooltipElement to be evaluated to determin margin offset
 */
function getTooltipMargin(tooltipElement) {
    var tooltipElementWidth = (jQuery(tooltipElement).width()) / 2;
    return "0 0 0 " + (tooltipElementWidth - 20) + "px";
}

/**
 * Shows the message tooltip if there are messages for this field, otherwise hides it.  The showAndClose flag allows
 * the message to be closed automatically after some predetermined amount of time, and the change flag forces the
 * show call on the plugin to deal with potential placement issues that can occur.
 * @param fieldId id of the field to show the tooltip on
 * @param showAndClose when true, this tooltip will be shown and then closed after a certain length of time
 * @param change forces the tooltip show call to deal with placement issues when changing internal content
 */
function showMessageTooltip(fieldId, showAndClose, change) {
    var field = jQuery("#" + fieldId);
    var data = getValidationData(field);

    if (data && data.useTooltip && data.messagingEnabled && !haltValidationMessaging) {
        var elementInfo = getHoverElement(fieldId);
        var tooltipElement = jQuery(elementInfo.element);
        if (elementInfo.type == "fieldset") {
            tooltipElement = tooltipElement.filter(".uif-tooltip");
        }

        var popoverData = tooltipElement.data(kradVariables.POPOVER_DATA);
        if (!popoverData) {
            popoverData = initializeTooltip(tooltipElement);
        }

        var hasMessages = jQuery("[data-messages_for='" + fieldId + "']").children().length;

        if (hasMessages) {
            if (data.tooltipTimer) {
                //if there is a timer for this field in place, stop the timer from completing, to handle the new
                //show logic
                clearTimeout(data.tooltipTimer);
            }

            popoverData.options.content = jQuery("[data-messages_for='" + fieldId + "']").html();

            var show = true;
            //only do a timed close if there are also server messages left - means you got a new client
            //side error that has to be demonstrated visually to the user (it would have no visual indication
            //if we just closed immediately)
            if (showAndClose && !(data.serverErrors.length || data.serverWarnings.length || data.serverInfo.length)) {
                hideMessageTooltip(fieldId);
                show = false;
            }

            if (show) {
                if (!popoverData.shown) {
                    if (showAndClose) {
                        //close other bubble popups so we dont get too many during fast tabbing
                        hideTooltips();
                    }

                    tooltipElement.popover("show");

                    var popover = jQuery(tooltipElement).next(".popover");
                    if (data.tooltipTheme) {
                        popover.addClass(data.tooltipTheme);
                    }

                    popoverData.shown = true;
                }
                else if (popoverData.shown) {

                    if (change) {
                        //if the messages shown were changed, reshow to get around placement issues
                        if (showAndClose) {
                            //close other bubble popups so we dont get too many during fast tabbing
                            hideTooltips();
                        }

                        tooltipElement.popover("show");

                    }
                }

                if (showAndClose) {
                    //setup a timer to close the tooltip automatically
                    data.tooltipTimer = setTimeout("hideMessageTooltip('" + fieldId + "')", 3000);
                    field.data(kradVariables.VALIDATION_MESSAGES, data);
                }
            }
        }
        else {
            hideMessageTooltip(fieldId);
        }
    }
}

/**
 * Writes the any messages in the client or server side message arrays at the field level, applies the correct style
 * classes, and displays icons as necessary.  These messages are hidden content unless tooltips for messages are
 * turned off.
 * @param id id of the field to write messages to
 */
function writeMessagesAtField(id) {
    var field = jQuery("#" + id);
    var data = getValidationData(field);

    if (data && data.displayMessages) {
        //initialize data if not present
        if (!data.errors) {
            data.errors = [];
        }
        if (!data.warnings) {
            data.warnings = [];
        }
        if (!data.info) {
            data.info = [];
        }

        var messagesDiv = jQuery("[data-messages_for='" + id + "']");
        var createMessagesDiv = messagesDiv.length === 0;

        if (createMessagesDiv) {
            messagesDiv = jQuery("<div id='" + id +
                    "_errors' class='alert' data-messages_for='" + id + "' style='display: none;'>");
        }

        //ensure the messagesDiv is hidden and empty
        if (data.useTooltip) {
            messagesDiv.hide();
        }
        else {
            messagesDiv.show();
        }
        messagesDiv.empty();

        //generate client side based messages
        var clientMessages = jQuery("<div class='" + kradVariables.CLIENT_MESSAGE_ITEMS_CLASS + "'><ul>"
                + generateListItems(data.errors, kradVariables.ERROR_MESSAGE_ITEM_CLASS, 0, false, errorImage)
                + generateListItems(data.warnings, kradVariables.WARNING_MESSAGE_ITEM_CLASS, 0, false, warningImage)
                + generateListItems(data.info, kradVariables.INFO_MESSAGE_ITEM_CLASS, 0, false, infoImage) + "</ul></div>");

        // Create an edit link for inline edit fields which now have associated server messages
        var editLink = "";
        if (field.data(kradVariables.INLINE_EDIT.INLINE_EDIT_DATA_ATTR)) {
            var editText = getMessage(kradVariables.MESSAGE_EDIT);
            editLink = " <a onclick=\"activateInlineEdit('" + id + "'); return false;\">" + editText + "</a>";
        }

        //generate server side based messages
        var serverMessages = jQuery("<div class='" + kradVariables.SERVER_MESSAGE_ITEMS_CLASS + "'><ul>"
                + generateListItems(data.serverErrors, kradVariables.ERROR_MESSAGE_ITEM_CLASS, 0, false, errorImage, editLink)
                + generateListItems(data.serverWarnings, kradVariables.WARNING_MESSAGE_ITEM_CLASS, 0, false, warningImage, editLink)
                + generateListItems(data.serverInfo, kradVariables.INFO_MESSAGE_ITEM_CLASS, 0, false, infoImage, editLink) + "</ul></div>");

        var hasServerMessages = false;
        //only append if messages exist
        if (jQuery(clientMessages).find("ul").children().length) {
            if (createMessagesDiv) {
                field.append(messagesDiv);
                createMessagesDiv = false;
            }

            jQuery(clientMessages).appendTo(messagesDiv);
        }

        if (jQuery(serverMessages).find("ul").children().length) {
            if (createMessagesDiv) {
                field.append(messagesDiv);
                createMessagesDiv = false;
            }

            jQuery(serverMessages).appendTo(messagesDiv);
            hasServerMessages = true;
        }

        var showImage = data.showIcons;
        //do not show the message icon next to field if this field is in a table collection layout
        if (field.parents(".uif-tableCollectionLayout").length) {
            showImage = false;
        }

        //remove any image and previous styles that may already be present
        jQuery("#" + id + " > .uif-validationImage").remove();
        field.removeClass(kradVariables.HAS_ERROR_CLASS);
        field.removeClass(kradVariables.HAS_MODIFIED_ERROR_CLASS);
        field.removeClass(kradVariables.HAS_WARNING_CLASS);
        field.removeClass(kradVariables.HAS_INFO_CLASS);

        //show appropriate icons/styles based on message severity level
        if (jQuery(messagesDiv).find(".uif-errorMessageItem-field").length) {
            if (data.errors.length) {
                jQuery(messagesDiv).find(".uif-clientMessageItems").addClass(kradVariables.CLIENT_ERROR_DIV_CLASS);
            }

            if (data.serverErrors.length) {
                jQuery(messagesDiv).find(".uif-serverMessageItems").addClass(kradVariables.CLIENT_ERROR_DIV_CLASS);
            }

            if (data.fieldModified && data.errors.length == 0) {
                //This is to represent the field has been changed after a server error, but may or
                //may not be fixed - greyed out image/border
                field.addClass(kradVariables.HAS_MODIFIED_ERROR_CLASS);
                if (showImage) {
                    jQuery(messagesDiv).before(errorGreyImage);
                }
            }
            else {
                field.addClass(kradVariables.HAS_ERROR_CLASS);
                if (showImage) {
                    jQuery(messagesDiv).before(errorImage);
                }
            }

            if (hasServerMessages) {
                data.tooltipTheme = "uif-tooltip-error-ss";
            }
            else {
                data.tooltipTheme = "uif-tooltip-error-cs";
            }

            handleTabStyle(id, true, false, false);
        }
        else if (jQuery(messagesDiv).find(".uif-warningMessageItem-field").length) {
            if (data.warnings.length) {
                jQuery(messagesDiv).find(".uif-clientMessageItems").addClass(kradVariables.CLIENT_WARNING_DIV_CLASS);
            }

            if (data.serverWarnings.length) {
                jQuery(messagesDiv).find(".uif-serverMessageItems").addClass(kradVariables.CLIENT_WARNING_DIV_CLASS);
            }

            field.addClass(kradVariables.HAS_WARNING_CLASS);
            if (showImage) {
                jQuery(messagesDiv).before(warningImage);
            }

            if (hasServerMessages) {
                data.tooltipTheme = "uif-tooltip-warning-ss";
            }
            else {
                data.tooltipTheme = "uif-tooltip-warning-cs";
            }

            handleTabStyle(id, false, true, false);
        }
        else if (jQuery(messagesDiv).find(".uif-infoMessageItem-field").length) {
            if (data.info.length) {
                jQuery(messagesDiv).find(".uif-clientMessageItems").addClass(kradVariables.CLIENT_INFO_DIV_CLASS);
            }

            if (data.serverInfo.length) {
                jQuery(messagesDiv).find(".uif-serverMessageItems").addClass(kradVariables.CLIENT_INFO_DIV_CLASS);
            }

            field.addClass(kradVariables.HAS_INFO_CLASS);
            if (showImage) {
                jQuery(messagesDiv).before(infoImage);
            }

            if (hasServerMessages) {
                data.tooltipTheme = "uif-tooltip-info-ss";
            }
            else {
                data.tooltipTheme = "uif-tooltip-info-cs";
            }

            handleTabStyle(id, false, false, true);
        }
        else {
            messagesDiv.hide();

            handleTabStyle(id, false, false, false);
        }
    }
}

/**
 * Handles the messages at the field level, this is different than writeMessagesAtField because it causes the messages
 * to bubble up multiple layers to obtain the summary effect - as well as making a call to writeMessagesAtField
 * - this call is made for client side messages only when
 * summary is already present
 * @param id id of the field to handle messages
 * @param pageSetupPhase(optional) is this the page setup phase?
 */
function handleMessagesAtField(id, pageSetupPhase) {
    var field = jQuery("#" + id);
    var skip = field.data("vignore");

    if (pageSetupPhase == undefined) {
        pageSetupPhase = false;
    }

    //check to see if the field exists in a summary and skip bubbling if clientErrorExistsCheck is on  (prevents
    //"jarring")
    var skipBubble = false;
    if (clientErrorExistsCheck && !summaryTextExistence[id]) {
        skipBubble = true;
    }

    if (skip !== "yes") {
        var data = getValidationData(field);
        if (data) {
            if (!pageSetupPhase || (pageSetupPhase && data.hasOwnMessages)) {
                writeMessagesAtField(id);

                var parent = field.data("parent");

                if (parent && !skipBubble) {
                    handleMessagesAtGroup(parent, id, data, pageSetupPhase);
                }

                data.processed = true;
            }
        }
    }
}

/**
 * Handles the messages for a particular field at the group identified by id.  This data is bubbled up through parent
 * groups as appropriate and fieldLinks/summary links are updated to represent the new message status for the field
 * passed in.
 * @param id - the id of the group
 * @param fieldId - the id of the field with message updates
 * @param fieldData - the new validation data for the field being updated
 */
function handleMessagesAtGroup(id, fieldId, fieldData, pageSetupPhase) {
    var group = jQuery("#" + id);
    var data = getValidationData(group, true);

    var pageLevel = false;
    var parent = group.data("parent");
    if (data) {
        var messageMap = data.messageMap;
        pageLevel = data.pageLevel;

        //init empty params
        if (!data.errors) {
            data.errors = [];
        }
        if (!data.warnings) {
            data.warnings = [];
        }
        if (!data.info) {
            data.info = [];
        }
        if (!messageMap) {
            messageMap = {};
            data.messageMap = messageMap;
        }

        //retrieve header for section
        if (data.isSection === undefined) {
            var sectionHeader = getGroupHeaderElement(id);
            data.isSection = sectionHeader.length;
        }

        //add fresh data to group's message data based on the new field info
        messageMap[fieldId] = fieldData;

        //write messages for this group
        if (!pageSetupPhase) {
            var forceWrite = jQuery("[data-messages_for='" + id + "']").find("li[data-messageitemfor='" + fieldId + "']").length;
            writeMessagesForGroup(id, data, forceWrite);
            displayHeaderMessageCount(id, data);
        }
    }

    if (!pageLevel && parent) {
        handleMessagesAtGroup(parent, fieldId, fieldData, pageSetupPhase);
    }
}

/**
 * Write the messages out for the group that are present its data
 *
 * @param id id of the group
 * @param data validationData for the group
 * @param forceWrite forces the group write to be processed
 */
function writeMessagesForGroup(id, data, forceWrite, skipCalculateTotals) {
    var group = jQuery("#" + id);
    var parent = group.data("parent");

    if (data) {
        var messageMap = data.messageMap;
        var pageLevel = data.pageLevel;
        var order = data.order;
        var sections = data.sections;

        //retrieve header for section
        if (data.isSection === undefined) {
            var sectionHeader = getGroupHeaderElement(id);
            data.isSection = sectionHeader.length;
        }

        //show messages if data is received as force show or if this group is considered a section
        var showMessages = (data.isSection || data.forceShow) && (!data.closed || pageValidationPhase);

        //TabGroups rely on tab error indication to indicate messages - don't show messages here
        var type = group.data("type");
        if (type && type === kradVariables.TAB_GROUP_CLASS) {
            showMessages = false;
        }

        //if this group is in a tab in a tab group show your messages because TabGroups will not
        if (parent) {
            var parentType = jQuery("#" + parent).data("type");
            if (parentType && parentType === kradVariables.TAB_GROUP_CLASS) {
                showMessages = true;
            }
        }

        //init empty params
        if (!data.errors) {
            data.errors = [];
        }
        if (!data.warnings) {
            data.warnings = [];
        }
        if (!data.info) {
            data.info = [];
        }

        if (!skipCalculateTotals) {
            data = calculateMessageTotals(id, data);
        }

        if (showMessages) {

            var newList = jQuery("<ul class='" + kradVariables.VALIDATION_MESSAGES_CLASS + "'></ul>");

            if (data.messageTotal || jQuery("span.uif-correctedError").length || forceWrite) {

                newList = generateSectionLevelMessages(id, data, newList);

                if (data.summarize) {
                    newList = generateSummaries(id, messageMap, sections, order, newList);
                }
                else {
                    //if not generating summaries just output field links
                    for (var key in messageMap) {
                        var link = generateFieldLink(messageMap[key], key, data.collapseFieldMessages, data.displayLabel);
                        newList = writeMessageItemToList(link, newList);
                    }
                }

                var messageBlock = jQuery("[data-messages_for='" + id + "']");

                if (messageBlock.length === 0) {
                    var cssClasses = "alert";

                    messageBlock = jQuery("<div id='" + id + "_messages' class='"
                            + cssClasses + "' data-messages_for='" + id + "' "
                            + "style='display: none;'>");

                    if (data.closeable) {
                        messageBlock.bind('closed.bs.alert', function () {
                            var data = getValidationData(group, true);
                            data.closed = true;
                        });
                    }

                    var disclosureBlock = group.find("#" + id + "_disclosureContent");
                    if (disclosureBlock.length) {
                        disclosureBlock.prepend(messageBlock);
                    } else if (!data.isSection) {
                        group.prepend(messageBlock);
                    } else if (data.isSection) {
                        header = group.find("[data-header_for='" + id + "']");
                        header.after(messageBlock);
                    }
                }

                //remove old block styling
                messageBlock.removeClass("alert");
                messageBlock.removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_ERROR_CLASS);
                messageBlock.removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_WARNING_CLASS);
                messageBlock.removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_INFO_CLASS);
                messageBlock.removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_SUCCESS_CLASS);

                //give the block styling
                if (data.errorTotal > 0) {
                    messageBlock.removeClass("uif-validationMessages");
                    messageBlock.removeClass("uif-groupValidationMessages");
                    messageBlock.addClass("alert");
                    messageBlock.addClass(kradVariables.PAGE_VALIDATION_MESSAGE_ERROR_CLASS);
                }
                else if (data.warningTotal > 0) {
                    messageBlock.removeClass("uif-validationMessages");
                    messageBlock.removeClass("uif-groupValidationMessages");
                    messageBlock.addClass("alert");
                    messageBlock.addClass(kradVariables.PAGE_VALIDATION_MESSAGE_WARNING_CLASS);
                }
                else if (data.infoTotal > 0) {
                    messageBlock.removeClass("uif-validationMessages");
                    messageBlock.removeClass("uif-groupValidationMessages");
                    messageBlock.addClass("alert");
                    messageBlock.addClass(kradVariables.PAGE_VALIDATION_MESSAGE_INFO_CLASS);
                }
                else {
                    messageBlock.addClass("alert");
                    messageBlock.addClass(kradVariables.PAGE_VALIDATION_MESSAGE_SUCCESS_CLASS);
                }

                //clear and write the new list of summary items
                clearMessages(id, false);
                handleTabStyle(id, data.errorTotal, data.warningTotal, data.infoTotal);
                writeMessages(id, newList);

                //page level validation messsage header handling
                if (pageLevel) {
                    if (newList.children().length) {

                        var countMessage = generateCountString(data.errorTotal, data.warningTotal, data.infoTotal);

                        //set the window title
                        addCountToDocumentTitle(countMessage);

                        var single = isSingularMessage(newList);
                        var pageValidationHeader;
                        if (!single) {
                            pageValidationHeader = jQuery("<h3 tabindex='0' class='" + kradVariables.VALIDATION_PAGE_HEADER_CLASS + "' "
                                    + "id='pageValidationHeader'>This page has " + countMessage + "</h3>");
                        }
                        else {
                            pageValidationHeader = jQuery(newList).detach();
                        }

                        pageValidationHeader.find(".uif-validationImage").remove();
                        var pageSummaryClass = "";
                        var image = errorGreyImage;
                        if (data.errorTotal) {
                            pageSummaryClass = kradVariables.PAGE_VALIDATION_MESSAGE_ERROR_CLASS;
                            image = errorImage;
                        }
                        else if (data.warningTotal) {
                            pageSummaryClass = kradVariables.PAGE_VALIDATION_MESSAGE_WARNING_CLASS;
                            image = warningImage;
                        }
                        else if (data.infoTotal) {
                            pageSummaryClass = kradVariables.PAGE_VALIDATION_MESSAGE_INFO_CLASS;
                            image = infoImage;
                        }

                        if (!single) {
                            pageValidationHeader.prepend(image);
                        }
                        else {
                            pageValidationHeader.find("li").prepend(image);
                            pageValidationHeader.addClass("uif-pageValidationMessage-single")
                        }

                        messageBlock.prepend(pageValidationHeader);

                        //Handle special classes
                        pageValidationHeader.parent().removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_ERROR_CLASS);
                        pageValidationHeader.parent().removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_WARNING_CLASS);
                        pageValidationHeader.parent().removeClass(kradVariables.PAGE_VALIDATION_MESSAGE_INFO_CLASS);
                        pageValidationHeader.parent().addClass(pageSummaryClass);

                        if (!data.showPageSummaryHeader && !single) {
                            pageValidationHeader.hide();
                        }

                        messageBlock.find(".uif-validationMessagesList").attr("id", "pageValidationList");
                        messageBlock.find(".uif-validationMessagesList").attr("aria-labelledby",
                                "pageValidationHeader");
                    }
                }

                if (data.closeable) {
                    messageBlock.prepend("<button type='button' class='close' "
                            + "data-dismiss='alert' aria-hidden='true'>x</button>");
                    messageBlock.alert();
                }
            }
            else {
                clearMessages(id, true);
            }
        }
    }
}

/**
 * Convience method for checking if a string is undefined, blank, or empty
 *
 * @param str String for testing
 */
function isEmpty(str) {
    return (!str || 0 === str.length);
}

/**
 * Appends the message count to the document title (window title) 
 * 
 * First look to see if there is a error/warning/info message already 
 * in the tab title, in the event that the there is no error/warning/info 
 * at all add the message to the title. If there is an error/warning/info 
 * message and then either update the new message or remove the error and 
 * the dash. 
 *
 * @param countMessage the new message to append
 */
function addCountToDocumentTitle(countMessage) {
	var tokenIndex = document.title.lastIndexOf(" - ");

    if (tokenIndex > -1 && !isEmpty(countMessage)) {
        document.title = document.title.substr(0, tokenIndex) + " - " + countMessage;
    } else if (tokenIndex > -1 && isEmpty(countMessage)) {
        document.title = document.title.substr(0, tokenIndex);
    } else if (!isEmpty(countMessage)) {
        document.title = document.title + " - " + countMessage;
    }
}

/**
 * Returns true if newList contains a singular li item
 *
 * @param newList the list to check
 * @return {boolean} true if only one li in list
 */
function isSingularMessage(newList) {
    var single = false;
    var lines = jQuery(newList).find("li");
    if (lines.length == 1) {
        single = true;
    }
    return single;
}

/**
 * Write messages out for the page if any exist
 */
function writeMessagesForPage() {
    clientErrorStorage = new Object();
    var summaryTextExistence = new Object();
    var page = jQuery("[data-role='Page']");
    var pageId = page.attr("id");
    var data = getValidationData(page, true);

    calculateMessageTotals(pageId, data);
    if (prevPageMessageTotal === 0 && data.messageTotal == 0) {
        return;
    }

    prevPageMessageTotal = data.messageTotal;

    if (data) {
        var messageMap = data.messageMap;
        if (!messageMap) {
            messageMap = {};
            data.messageMap = messageMap;
        }
    }

    writeMessagesForChildGroups(pageId);
    writeMessagesForGroup(pageId, data, false, true);
    displayHeaderMessageCount(pageId, data);
    jQuery(".uif-errorMessageItem > div").show();
}

/**
 * Write messages out for each of the child groups of the parent with parentId
 *
 * @param parentId - id of parent
 */
function writeMessagesForChildGroups(parentId) {
    jQuery("[data-parent='" + parentId + "']").each(function () {

        var currentGroup = jQuery(this);
        var id = currentGroup.attr("id");
        var data = getValidationData(currentGroup, true);

        if (data) {
            var messageMap = data.messageMap;
            if (!messageMap) {
                messageMap = {};
                data.messageMap = messageMap;
            }
        }

        if (!(currentGroup.is("div[data-role='InputField']"))) {
            writeMessagesForChildGroups(id);
            writeMessagesForGroup(id, data);
            displayHeaderMessageCount(id, data);
        }
    });
}

/**
 * Handles tab styling if a group is in a tab
 * @param id - the id of the group or field
 * @param error - true if errors exist
 * @param warning - true if warnings exist
 * @param info - true if info exist
 */
function handleTabStyle(id, error, warning, info) {
    var tabWrapper = jQuery("#" + id).closest("[data-type='TabWrapper']");
    if (tabWrapper.length) {
        var tabId = jQuery(tabWrapper).data("tabwrapperfor");
        var tab = jQuery("[data-tabfor='" + tabId + "']");
        tab.find(".uif-validationImage").remove();
        if (error) {
            tab.find("a").append(errorImage);
        }
        else if (warning) {
            tab.find("a").append(warningImage);
        }
        else if (info) {
            tab.find("a").append(infoImage);
        }
    }
}

/**
 * Generates section level messages - messages that are only associated with a section by id or additionalKeysToMatch.
 * This does not include the field messages themselves.
 * @param id - id of the the group
 * @param data - the group's 'validationMessages' data
 * @param newList - the ul jQuery element being generated in this pass
 */
function generateSectionLevelMessages(id, data, newList) {
    if (data != undefined && data != null) {
        if (data.hasOwnMessages) {
            //Write all message items for section
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

        jQuery("#" + id).find("div[data-parent='" + id + "']").not("div[data-role='InputField']").each(function () {
            var groupData = getValidationData(jQuery(this), true);
            if (groupData && !groupData.isSection) {
                newList = generateSectionLevelMessages(jQuery(this).attr("id"), groupData, newList);
            }
        });
    }
    return newList;
}

/**
 * Calculates the message totals for the data passed in, appends these totals to the data map, and passes it back
 * @param data - 'validationMessages' data to count message totals on
 */
function calculateMessageTotals(id, data) {
    var errorTotal = 0;
    var warningTotal = 0;
    var infoTotal = 0;
    var messageMap = data.messageMap;
    //Add totals for messages of fields in group
    for (var fId in messageMap) {
        var currentData = messageMap[fId];
        errorTotal = errorTotal + currentData.serverErrors.length + currentData.errors.length;
        warningTotal = warningTotal + currentData.serverWarnings.length + currentData.warnings.length;
        infoTotal = infoTotal + currentData.serverInfo.length + currentData.info.length;
    }

    var childGroupCount = recursiveGroupMessageCount(id);
    errorTotal += childGroupCount.errorTotal;
    warningTotal += childGroupCount.warningTotal;
    infoTotal += childGroupCount.infoTotal;

    //Add totals for messages for THIS Group
    data.errorTotal = errorTotal + data.serverErrors.length + data.errors.length;
    data.warningTotal = warningTotal + data.serverWarnings.length + data.warnings.length;
    data.infoTotal = infoTotal + data.serverInfo.length + data.info.length;
    data.messageTotal = data.errorTotal + data.warningTotal + data.infoTotal;

    return data;
}

/**
 * Find the count of messages that each subGroup of the parent by id has for its group specifically (does not include
 * its fields - these are messages keyed to that group)
 *
 * @param parentId parent group id to use
 * @return {Object} data object containing the message count totals
 */
function recursiveGroupMessageCount(parentId) {
    var data = {
        errorTotal: 0,
        warningTotal: 0,
        infoTotal: 0
    };

    if (!parentId) {
        return data;
    }

    jQuery("#" + parentId).find("[data-parent='" + parentId + "']").not("div[data-role='InputField']").each(function () {
        var groupData = getValidationData(jQuery(this), true);
        if (groupData) {
            data.errorTotal = data.errorTotal + groupData.serverErrors.length + (groupData.errors ? groupData.errors.length : 0);
            data.warningTotal = data.warningTotal + groupData.serverWarnings.length + (groupData.warnings ? groupData.warnings.length : 0);
            data.infoTotal = data.infoTotal + groupData.serverInfo.length + (groupData.info ? groupData.info.length : 0);
        }

        var childData = recursiveGroupMessageCount(jQuery(this).attr("id"));
        data.errorTotal += childData.errorTotal;
        data.warningTotal += childData.warningTotal;
        data.infoTotal += childData.infoTotal;
    });

    return data;
}

/**
 * Displays the message count string on the header for the section specified.  calculateMessageTotals must be called
 * before this call
 * @param sectionId - id of the section
 * @param sectionData - 'validationMessages' data of the section
 */
function displayHeaderMessageCount(sectionId, sectionData) {
    if (sectionData && sectionData.displayHeaderSummary) {
        var sectionHeader = getGroupHeaderElement(sectionId);

        if (errorImage == undefined) {
            setupImages();
        }

        if (sectionHeader.length && sectionData.messageTotal) {

            var countMessage = generateCountString(sectionData.errorTotal, sectionData.warningTotal, sectionData.infoTotal);
            var image = "";
            if (sectionData.errorTotal) {
                image = errorImage;
            }
            else if (sectionData.warningTotal) {
                image = warningImage;
            }
            else if (sectionData.infoTotal) {
                image = infoImage;
            }

            var messageCountElement = jQuery(sectionHeader).find("." + kradVariables.MESSAGE_COUNT_CLASS);
            if (messageCountElement.length) {
                messageCountElement.remove();
            }

            if (countMessage != "") {
                jQuery("<div class='" + kradVariables.MESSAGE_COUNT_CLASS + "'>" + image + " " + countMessage + "</div>").appendTo(sectionHeader);
            }
        }
        else if (sectionHeader.length && sectionData.messageTotal == 0) {
            jQuery(sectionHeader).find("." + kradVariables.MESSAGE_COUNT_CLASS).remove();
        }
    }
}

/**
 * Generates a count message that can be used in a header or summary link based on total amount of errors, warnings,
 * and info messages totals passed in
 * @param errorTotal - total number of errors
 * @param warningTotal - total number of warnings
 * @param infoTotal - total number of infos
 */
function generateCountString(errorTotal, warningTotal, infoTotal) {
    var countMessage = "";

    if (errorTotal == 1) {
        countMessage = getMessage(kradVariables.MESSAGE_TOTAL_ERROR, null, null, errorTotal);
    }
    else {
        countMessage = getMessage(kradVariables.MESSAGE_TOTAL_ERRORS, null, null, errorTotal);
    }

    if ((errorTotal > 0) + (warningTotal > 0) + (infoTotal > 0) == 3) {
        countMessage = getMessage(kradVariables.MESSAGE_TOTAL_OTHER_MESSAGES, null, null, countMessage, (warningTotal + infoTotal));
    }
    else {

        if (warningTotal) {

            if (errorTotal == 0) {
                countMessage = "";
            }

            if (countMessage != "") {
                countMessage = countMessage + " & ";
            }

            if (warningTotal == 1) {
                countMessage = countMessage + getMessage(kradVariables.MESSAGE_TOTAL_WARNING, null, null, warningTotal);
            }
            else {
                countMessage = countMessage + getMessage(kradVariables.MESSAGE_TOTAL_WARNINGS, null, null, warningTotal);
            }
        }

        if (infoTotal) {

            if (errorTotal == 0) {
                countMessage = "";
            }

            if (countMessage != "") {
                countMessage = countMessage + " & ";
            }

            if (infoTotal == 1) {

//              Check to see if the info message is coming from a lookup result page. If it is, do not add single
//              messages to the top of the page unless there are other messages to display as well.
                if (jQuery("#uLookupResults").children().length > 0) {
                    if (countMessage != "") {
                        countMessage = countMessage + getMessage(kradVariables.MESSAGE_TOTAL_MESSAGES, null, null, infoTotal);
                    }
                } else {
                    countMessage = countMessage + getMessage(kradVariables.MESSAGE_TOTAL_MESSAGES, null, null, infoTotal);
                }
            }
            else {
                countMessage = countMessage + getMessage(kradVariables.MESSAGE_TOTAL_MESSAGES, null, null, infoTotal);
            }

        }
    }
    return countMessage;
}

/**
 * Clear all the messages in the messages div for this group or field by id
 * @param messagesForId - id of the group or field to clear messages for
 * @param hide - wherether or not to also hide the message div after clearing
 */
function clearMessages(messagesForId, hide) {
    var messagesDiv = jQuery("[data-messages_for='" + messagesForId + "']");
    jQuery(messagesDiv).empty();
    if (hide) {
        jQuery(messagesDiv).hide();
    }
}

/**
 * Write the new messages to the messages div
 * @param messagesForId - id of the group to write messages for
 * @param newList - the new content to write
 */
function writeMessages(messagesForId, newList) {
    var data = getValidationData(jQuery("#" + messagesForId), true);
    var messagesDiv = jQuery("[data-messages_for='" + messagesForId + "']");
    if (newList.children().length && data.displayMessages) {
        jQuery(messagesDiv).show();
        jQuery(newList).appendTo(messagesDiv);
    }
    else if (newList.children().length && !data.displayMessages) {
        jQuery(messagesDiv).hide();
        jQuery(newList).appendTo(messagesDiv);
    }
    else {
        jQuery(messagesDiv).hide();
    }
}

/**
 * Append an item to the list, checks for null
 * @param item - item to appendTo the list
 * @param newList - the ul jQuery element to append to
 */
function writeMessageItemToList(item, newList) {
    if (item != null) {
        jQuery(item).appendTo(newList);
    }
    return newList;
}

/**
 * Generate the message list items based on the content of the messageArray passed in.  Returns the html for the li
 * elements generated.
 * @param messageArray - array of messages to generate list items for
 * @param itemClass - class to apply to each li item generated
 * @param startIndex - where to start in the array passed in for generation, normally 0
 * @param focusable - whether or not this li element should be focusable by the user
 * @param image - the image to use at the beginning of each li element
 */
function generateListItems(messageArray, itemClass, startIndex, focusable, image, editLink) {
    var elements = "";
    if (!image) {
        image = "";
    }

    if (!editLink) {
        editLink = "";
    }

    if (messageArray && messageArray.length) {
        for (var i = startIndex; i < messageArray.length; i++) {
            if (focusable) {
                elements = elements + "<li tabindex='0' class='" + itemClass + "'>" + image + " "
                        + convertToHtml(messageArray[i]) + editLink + "</li>";
            }
            else {
                elements = elements + "<li class='" + itemClass + "'>" + image + " "
                        + convertToHtml(messageArray[i]) + editLink + "</li>";
            }
        }
    }
    return elements;
}

/**
 * Generates the summaries for the group specified by id.  messageMap represents the message data for fields in this
 * group, sections are the sections of this group, order specifies the order that fields and sub-groups of this group
 * occur in, and newList is the ul element that is being generated for this particular group.  Returns newList (ul) with
 * the appropriate li items appended to it.
 * @param id - id of the group
 * @param messageMap - messageData for each field of this group
 * @param sections - sections of this group
 * @param order - order of the fields and sections in this group
 * @param newList - the ul being built by this call
 */
function generateSummaries(id, messageMap, sections, order, newList) {
    var data = getValidationData(jQuery("#" + id), true);
    //if no nested sections just output the fieldLinks
    if (sections.length == 0 || data.isTableCollection == "true") {
        for (var key in messageMap) {
            var link = generateFieldLink(messageMap[key], key, data.collapseFieldMessages, data.displayLabel);
            newList = writeMessageItemToList(link, newList);
        }
    }
    else {
        var currentFields = [];
        var currentSectionId;

        //if sections are present iterate over the fields and sections in order by collecting fields that are considered
        //"direct" descendants of this section - contained in a group (not a section) or just on the section itself.
        //when the iterator hits a section generate a summary sublist which describes the fields that occur before
        //that section (or field group) and add it to the list - then generate the summary link for that section (or
        //or field group).  Add both to the list be generated.

        jQuery.each(order, function (index, value) {
            //if it doesn't start with an s$ its not a section or f$ its not a field group
            if (!(value.indexOf("s$") == 0) && !(value.indexOf("f$") == 0)) {
                currentFields.push(value);
            }
            else if (value.indexOf("c$") == 0) {
                var collectionId = value.substring(2);
                var collectionData = getValidationData(jquery("#" + collectionId), true);
                for (var key in collectionData.messageMap) {
                    var link = generateFieldLink(collectionData.messageMap[key],
                            key, collectionData.collapseFieldMessages, collectionData.displayLabel);
                    newList = writeMessageItemToList(link, newList);
                }
            }
            else {
                var sectionId = value.substring(2);
                if (jQuery("#" + sectionId).data("role") && jQuery("#" + sectionId).data("role") == "placeholder") {
                    //do nothing this is a blank/non-visible section
                }
                else {
                    currentSectionId = sectionId;
                    var sublist = generateFieldLinkSublist(data, currentFields, messageMap, currentSectionId, true);
                    newList = writeMessageItemToList(sublist, newList);
                    var summaryLink = generateSummaryLink(currentSectionId);
                    newList = writeMessageItemToList(summaryLink, newList);
                    currentFields = [];
                }
            }
        });

        //if there are more fields which occur after the final section - generate a sublist summary of those fields
        if (currentFields.length > 0) {
            var sublist = generateFieldLinkSublist(data, currentFields, messageMap, currentSectionId, false);
            newList = writeMessageItemToList(sublist, newList);
        }
    }
    return newList;
}

/**
 * Generate a link that focuses on a field when clicked, based on the messageData passed in.  This generates a link
 * that will summarize the messages involved, based on severity and type - server or client
 * @param messageData - messageData for this field
 * @param fieldId - id of the field to be linked to
 */
function generateFieldLink(messageData, fieldId, collapseMessages, showLabel) {
    var link = null;

    if (messageData != null) {
        //if messages aren't displayed at the field level - force uncollapse
        if (!messageData.displayMessages) {
            collapseMessages = false;
        }
        var linkType;
        var highlight;
        var collapse = false;
        var linkText = "";
        var separator = "";
        var collapsedErrors = {};
        var collapsedWarnings = {};
        var collapsedInfo = {};
        var image = "";
        var isLink = true;
        var hasServerMessages = messageData.serverErrors.length || messageData.serverWarnings.length ||
                messageData.serverInfo.length;

        //Evaluate if there are errors first, warnings second, and info third, link text content is generated by
        //severity level, a higher severity present will "collapse" the following severity levels displayed by default

        if (messageData.serverErrors.length || messageData.errors.length) {
            collapse = true;
            image = errorImage;
            linkType = "uif-errorMessageItem";
            highlight = "uif-errorHighlight";
        }

        if (messageData.errors.length) {
            linkText = convertToHtml(messageData.errors[0], true);

            clientErrorStorage[fieldId] = linkText;

            if (messageData.errors.length > 1) {
                collapsedErrors.exist = true;
                collapsedErrors.errorIndex = 1;
            }
        }
        else {
            //no error so need to cross out previous client error
            var previousError = clientErrorStorage[fieldId];

            if (previousError) {
                linkText = "<span class='uif-correctedError'>" + previousError + "</span>";
                isLink = false;

                if (messageData.warnings.length || messageData.info.length || hasServerMessages) {
                    isLink = true;
                }
            }
        }

        if (messageData.serverErrors.length) {
            if (linkText) {
                separator = ", ";
            }
            linkText = linkText + separator + convertToHtml(messageData.serverErrors[0], true);
            if (messageData.serverErrors.length > 1) {
                collapsedErrors.exist = true;
                collapsedErrors.serverErrorIndex = 1;
            }
        }

        if (messageData.serverWarnings.length || messageData.warnings.length) {
            if (collapse) {
                if (messageData.warnings.length) {
                    collapsedWarnings.exist = true;
                    collapsedWarnings.warningIndex = 0;
                }
                if (messageData.serverWarnings.length) {
                    collapsedWarnings.exist = true;
                    collapsedWarnings.serverWarningIndex = 0;
                }
            }
            else {
                collapse = true;
                image = warningImage;
                linkType = "uif-warningMessageItem";
                highlight = "uif-warningHighlight";
                if (messageData.warnings.length) {
                    linkText = convertToHtml(messageData.warnings[0], true);
                    if (messageData.warnings.length > 1) {
                        collapsedWarnings.exist = true;
                        collapsedWarnings.warningIndex = 1;
                    }
                }
                if (messageData.serverWarnings.length) {
                    if (linkText) {
                        separator = ", ";
                    }
                    linkText = linkText + separator + convertToHtml(messageData.serverWarnings[0], true);
                    if (messageData.serverWarnings.length > 1) {
                        collapsedWarnings.exist = true;
                        collapsedWarnings.serverWarningIndex = 1;
                    }
                }
            }
        }
        if (messageData.serverInfo.length || messageData.info.length) {
            if (collapse) {
                if (messageData.info.length) {
                    collapsedInfo.exist = true;
                    collapsedInfo.infoIndex = 0;
                }
                if (messageData.serverInfo.length) {
                    collapsedInfo.exist = true;
                    collapsedInfo.serverInfoIndex = 0;
                }
            }
            else {
                collapse = true;
                image = infoImage;
                linkType = "uif-infoMessageItem";
                highlight = "uif-infoHighlight";
                if (messageData.info.length) {
                    linkText = convertToHtml(messageData.info[0], true);
                    if (messageData.info.length > 1) {
                        collapsedInfo.exist = true;
                        collapsedInfo.infoIndex = 1;
                    }
                }
                if (messageData.serverInfo.length) {
                    if (linkText) {
                        separator = ", ";
                    }
                    linkText = linkText + separator + convertToHtml(messageData.serverInfo[0], true);
                    if (messageData.serverInfo.length > 1) {
                        collapsedInfo.exist = true;
                        collapsedInfo.serverInfoIndex = 1;
                    }
                }
            }
        }

        if (linkText != "") {
            //mark field as having summary text
            summaryTextExistence[fieldId] = true;

            //generate collapsed information - messages that are present but not being shown at this level
            var collapsedElements = handleCollapsedElements(messageData, collapsedErrors, collapsedWarnings,
                    collapsedInfo, collapseMessages);

            var name = jQuery("#" + fieldId).data("label");

            if (name && showLabel) {
                name = jQuery.trim(name);
                if (name.indexOf(":") == name.length - 1) {
                    name = name + " ";
                }
                else {
                    name = name + ": ";
                }

            }
            else {
                name = "";
            }

            if (isLink) {
                link = jQuery("<li data-messageItemFor='" + fieldId + "'><a class='alert-link' href='#'>"
                        + name + linkText + collapsedElements + "</a> </li>");
            }
            else {
                link = jQuery("<li tabindex='0' data-messageItemFor='" + fieldId
                        + "' class='alert-link uif-correctedError'>"
                        + name + linkText + collapsedElements + "</li>");
            }

            //modified appendage
            if (messageData.fieldModified && hasServerMessages) {
                jQuery(link).find("a").prepend("<span class='modified'>(Modified) </span>");
                if (!(messageData.errors.length)) {
                    jQuery(link).addClass("uif-errorMessageItem-modified");
                }
            }

            var linkObject = jQuery(link);
            var field = jQuery("#" + fieldId);

            linkObject.addClass(linkType);
            linkObject.find("a").click(function (event) {
                event.preventDefault();
                var control = jQuery("#" + fieldId + "_control");
                var field = jQuery("#" + fieldId);

                // Inline edit view check
                if (field.is("[data-inline_edit]") && field.find(".uif-inlineEdit-view:visible").length) {
                    field.find(".uif-inlineEdit-view").focus();
                }
                else if (control.length) {
                    jQuery(control).focus();
                }
                else {
                    jQuery("#" + fieldId + "_control_0").focus();
                }
            });
            linkObject.find("a").focus(function () {
                field.addClass(highlight);
            });
            linkObject.find("a").blur(function () {
                field.removeClass(highlight);
            });
            linkObject.find("a").hover(
                    function () {
                        field.addClass(highlight);
                    },
                    function () {
                        field.removeClass(highlight);
                    });
        }
        else {
            summaryTextExistence[fieldId] = false;
        }
    }

    return link;
}

function handleCollapsedElements(messageData, collapsedErrors, collapsedWarnings, collapsedInfo, collapseMessages) {
    var collapsedElements = "";
    if (collapsedErrors.exist && collapseMessages) {
        var count = 0;
        if (collapsedErrors.errorIndex != undefined && collapsedErrors.errorIndex >= 0) {
            count = count + messageData.errors.length - collapsedErrors.errorIndex;
        }
        if (collapsedErrors.serverErrorIndex != undefined && collapsedErrors.serverErrorIndex >= 0) {
            count = count + messageData.serverErrors.length - collapsedErrors.serverErrorIndex;
        }

        if (count > 1) {
            collapsedElements = collapsedElements + "<span class='" + kradVariables.COLLAPSED_ERRORS_CLASS + "'> [+"
                    + getMessage(kradVariables.MESSAGE_TOTAL_ERRORS, null, null, count) + "]</span>";
        }
        else {
            collapsedElements = collapsedElements + "<span class='" + kradVariables.COLLAPSED_ERRORS_CLASS + "'> [+"
                    + getMessage(kradVariables.MESSAGE_TOTAL_ERROR, null, null, count) + "]</span>";
        }
    }
    else if (collapsedErrors.exist && !collapseMessages) {
        if (collapsedErrors.errorIndex != undefined) {
            for (var i = collapsedErrors.errorIndex; i < messageData.errors.length; i++) {
                collapsedElements = collapsedElements + ", " + messageData.errors[i];
            }
        }
        if (collapsedErrors.serverErrorIndex != undefined) {
            for (var i = collapsedErrors.serverErrorIndex; i < messageData.serverErrors.length; i++) {
                collapsedElements = collapsedElements + ", " + messageData.serverErrors[i];
            }
        }
    }

    //collapsed warning handling
    if (collapsedWarnings.exist && collapseMessages) {
        var count = 0;
        if (collapsedWarnings.warningIndex != undefined && collapsedWarnings.warningIndex >= 0) {
            count = count + messageData.warnings.length - collapsedWarnings.warningIndex;
        }
        if (collapsedWarnings.serverWarningIndex != undefined && collapsedWarnings.serverWarningIndex >= 0) {
            count = count + messageData.serverWarnings.length - collapsedWarnings.serverWarningIndex;
        }

        if (count > 1) {
            collapsedElements = collapsedElements + "<span class='" + kradVariables.COLLAPSED_WARNINGS_CLASS + "'> [+"
                    + getMessage(kradVariables.MESSAGE_TOTAL_WARNINGS, null, null, count) + "]</span>";
        }
        else {
            collapsedElements = collapsedElements + "<span class='" + kradVariables.COLLAPSED_WARNINGS_CLASS + "'> [+"
                    + getMessage(kradVariables.MESSAGE_TOTAL_WARNING, null, null, count) + "]</span>";
        }
    }
    else if (collapsedWarnings.exist && !collapseMessages) {
        if (collapsedWarnings.warningIndex != undefined) {
            for (var i = collapsedWarnings.warningIndex; i < messageData.warnings.length; i++) {
                collapsedElements = collapsedElements + ", " + messageData.warnings[i];
            }
        }
        if (collapsedWarnings.serverWarningIndex != undefined) {
            for (var i = collapsedWarnings.serverWarningIndex; i < messageData.serverWarnings.length; i++) {
                collapsedElements = collapsedElements + ", " + messageData.serverWarnings[i];
            }
        }
    }

    //collapsed information handling
    if (collapsedInfo.exist && collapseMessages) {
        var count = 0;
        if (collapsedInfo.infoIndex != undefined && collapsedInfo.infoIndex >= 0) {
            count = count + messageData.info.length - collapsedInfo.infoIndex;
        }
        if (collapsedInfo.serverInfoIndex != undefined && collapsedInfo.serverInfoIndex >= 0) {
            count = count + messageData.serverInfo.length - collapsedInfo.serverInfoIndex;
        }

        if (count > 1) {
            collapsedElements = collapsedElements + "<span class='" + kradVariables.COLLAPSED_INFO_CLASS + "'> [+"
                    + getMessage(kradVariables.MESSAGE_TOTAL_MESSAGES, null, null, count) + "]</span>";
        }
        else {
            collapsedElements = collapsedElements + "<span class='" + kradVariables.COLLAPSED_INFO_CLASS + "'> [+"
                    + getMessage(kradVariables.MESSAGE_TOTAL_MESSAGE, null, null, count) + "]</span>";
        }
    }
    else if (collapsedInfo.exist && !collapseMessages) {
        if (collapsedInfo.infoIndex != undefined) {
            for (var i = collapsedInfo.infoIndex; i < messageData.info.length; i++) {
                collapsedElements = collapsedElements + ", " + messageData.info[i];
            }
        }
        if (collapsedInfo.serverInfoIndex != undefined) {
            for (var i = collapsedInfo.serverInfoIndex; i < messageData.serverInfo.length; i++) {
                collapsedElements = collapsedElements + ", " + messageData.serverInfo[i];
            }
        }
    }
    return collapsedElements;
}

/**
 * Generates a field link sub list summary that generates a list item which contains a sublist of items.  The text
 * of this li will be similar to: "3 errors before 'Section Name' section" (actual text dependant on what messages
 * are present) followed by an ul - the sublist - of field link items for each field specified in currentFields.
 *
 * @param parentSectionData - the data of the section this sublist will be provided for
 * @param currentFields - the fields to generate field links for by id
 * @param messageMap - map of the messageData associated with the fields; currentFields specifies a subset of these
 * @param sectionId - the id of the section that occurs after (or in some cases) before the fields to be contained in
 * the sublist
 * @param before - true if these field are before the section specified by id, false otherwise
 */
function generateFieldLinkSublist(parentSectionData, currentFields, messageMap, sectionId, before) {

    var sectionTitle = getGroupHeaderElement(sectionId).find(".uif-headerText-span").text();
    if (sectionTitle == null || sectionTitle == "") {
        //field group case
        sectionTitle = jQuery("#" + sectionId).data("label");
    }

    // for empty section titles (should not happen in unless we force it to)
    if (sectionTitle == null || sectionTitle == undefined) {
        sectionTitle = "group";
    }

    var disclosureText = "";
    var links = [];
    var errorCount = 0;
    var warningCount = 0;
    var infoCount = 0;
    var disclosureLink = null;
    var image = "";
    var linkType = "";

    for (var i in currentFields) {
        if (currentFields[i] != null) {

            var fieldId = currentFields[i];

            var messageData = messageMap[fieldId];
            if (messageData != undefined && messageData != null) {
                errorCount = errorCount + messageData.serverErrors.length + messageData.errors.length;
                warningCount = warningCount + messageData.serverWarnings.length + messageData.warnings.length;
                infoCount = infoCount + messageData.serverInfo.length + messageData.info.length;

                var link = generateFieldLink(messageData, fieldId, parentSectionData.collapseFieldMessages,
                        parentSectionData.displayLabel);
                if (link != null) {
                    links.push(link);
                }
            }
        }
    }
    if (errorCount || warningCount || infoCount) {
        var locationText = getMessage(kradVariables.MESSAGE_BEFORE);
        if (!before) {
            locationText = getMessage(kradVariables.MESSAGE_AFTER);
        }

        if (errorCount) {
            image = errorImage;
            linkType = "uif-errorMessageItem";
        }
        else if (warningCount) {
            image = warningImage;
            linkType = "uif-warningMessageItem";
        }
        else if (infoCount) {
            image = infoImage;
            linkType = "uif-infoMessageItem";
        }

        var countMessage = generateCountString(errorCount, warningCount, infoCount);

        sectionTitle = sectionTitle.replace(/\r?\n/g, "");
        disclosureText = countMessage + " " + locationText + " " + sectionTitle;

        if (links.length) {
            var subSummary = jQuery("<ul></ul>");
            for (var j in links) {
                jQuery(links[j]).appendTo(subSummary)
            }
            //jQuery(subSummary).hide();
        }

        //write disclosure link and div
        disclosureLink = jQuery("<li tabindex='0' class='" + linkType + "'>" + disclosureText + "</li>");
        jQuery(subSummary).appendTo(disclosureLink);
    }
    return disclosureLink;
}

/**
 * Generates a summary link for the section specified by id, this link describes how many messages of each type can
 * be found in a particular section.  The link links, the 'header' of that section.
 * @param sectionId - the id of the section to create a summary link for
 */
function generateSummaryLink(sectionId) {
    //determine section title and section type
    var sectionTitle = getGroupHeaderElement(sectionId).find(".uif-headerText-span").text();
    if (sectionTitle == null || sectionTitle == "") {
        //field group case
        sectionTitle = jQuery("#" + sectionId).data("label");
        sectionId = jQuery("#" + sectionId).data("group");
    }

    // for empty section titles (should not happen in unless we force it to)
    if (sectionTitle == null || sectionTitle == undefined) {
        sectionTitle = "";
    }

    var sectionData = getValidationData(jQuery("#" + sectionId), true);
    var summaryLink = null;
    var summaryMessage = "";
    var image = "";
    var linkType = "";
    var highlight = "";

    var sectionHasCorrectedErrors = jQuery("[data-messages_for='" + sectionId + "']").find("span.uif-correctedError").length;

    if (sectionData && (sectionData.messageTotal || sectionHasCorrectedErrors)) {
        var countMessage = generateCountString(sectionData.errorTotal, sectionData.warningTotal, sectionData.infoTotal);
        //remove newline characters
        sectionTitle = sectionTitle.replace(/\r?\n/g, "");
        if (sectionTitle && countMessage != "") {
            summaryMessage = sectionTitle + ": " + countMessage;
        }
        else {
            summaryMessage = countMessage;
        }
    }

    if (summaryMessage != "") {
        if (sectionData.errorTotal) {
            image = errorImage;
            linkType = "uif-errorMessageItem";
            highlight = kradVariables.ERROR_HIGHLIGHT_SECTION_CLASS;
        }
        else if (sectionData.warningTotal) {
            image = warningImage;
            linkType = "uif-warningMessageItem";
            highlight = kradVariables.WARNING_HIGHLIGHT_SECTION_CLASS;
        }
        else if (sectionData.infoTotal) {
            image = infoImage;
            linkType = "uif-infoMessageItem";
            highlight = kradVariables.INFO_HIGHLIGHT_SECTION_CLASS;
        }
        summaryLink = jQuery("<li data-messageItemFor='" + sectionId + "' class='" + linkType + "'><a "
                + "class='alert-link' href='#'>"
                + summaryMessage + "</a></li>");

        summaryLink.find("a").click(function (event) {
            event.preventDefault();

            jumpToElementById(sectionId);

            var firstItem = jQuery("[data-messages_for='" + sectionId + "'] > ul > li:first");
            if (firstItem.length) {
                if (jQuery(firstItem).find("> a").length) {
                    jQuery(firstItem).find("> a").focus();
                }
                else {
                    jQuery(firstItem).focus();
                }
            }

        });

        summaryLink.find("a").focus(function () {
            jQuery("#" + sectionId).addClass(highlight);
        });
        summaryLink.find("a").blur(function () {
            jQuery("#" + sectionId).removeClass(highlight);
        });
        summaryLink.find("a").hover(
                function () {
                    jQuery("#" + sectionId).addClass(highlight);
                },
                function () {
                    jQuery("#" + sectionId).removeClass(highlight);
                });

        //case where this section is not showing its own messages show them here
        if (!sectionData.displayMessages) {
            var sectionLinks = jQuery("[data-messages_for='" + sectionId + "']");
            sectionLinks.removeAttr("class");
            sectionLinks.removeAttr("style");
            //summaryLink.append(sectionLinks); (incase we want to go back to previous impl)
            jQuery(sectionLinks).show();
            return sectionLinks;
        }
    }
    return summaryLink;
}

/**
 * Runs the validation script if the validator is already setup, otherwise adds a handler
 * to the document which will run once when the 'validationSetup' event is fired
 *
 * @param scriptFunction
 */
function runValidationScript(scriptFunction) {
    if (pageValidatorReady) {
        scriptFunction();
    }
    else {
        jQuery(document).bind(kradVariables.VALIDATION_SETUP_EVENT, function (event) {
            jQuery(this).unbind(event);
            scriptFunction();
        });
    }
}

/**
 * Validate that a specific field's control defined by the selector/jQuery array passed in.  Also calls dependsOnCheck
 * to validate any dependant fields.
 *
 * @param fieldControl selector/jQuery array that represents the control to validate
 */
function validateFieldValue(fieldControl) {
    // skip validation for add line fields unless there is a value. The add button will handle validation
    if (jQuery(fieldControl).attr('id').match(new RegExp(kradVariables.ID_SUFFIX.ADD_LINE_INPUT_FIELD))
            && !jQuery(fieldControl).val()) {
        return true;
    }

    //remove the ignore class if any due to a bug in the validate 
    //plugin for direct validation on certain types
    if (jQuery(fieldControl).attr('id').match(/ID_SUFFIXADD_LINE_INPUT_FIELD/)) {
        jQuery(fieldControl).removeClass("ignoreValid");
        return true;
    }

    var hadIgnore = false;
    if (jQuery(fieldControl).hasClass("ignoreValid")) {
        jQuery(fieldControl).removeClass("ignoreValid");
        hadIgnore = true;
    }
    clientErrorExistsCheck = true;

    // skip fields in hidden dialogs
    if (jQuery(fieldControl).is(kradVariables.DIALOG_SELECTOR + ":hidden [data-role='Control']")) {
        return true;
    }

    //the validation call
    var valid = jQuery(fieldControl).valid();
    dependsOnCheck(fieldControl, new Array());
    clientErrorExistsCheck = false;
    if (hadIgnore) {
        jQuery(fieldControl).addClass("ignoreValid");
    }

    return valid;
}

/**
 * Validates all fields requiring validation. Removes any
 * kradVariables.IGNORE_VALIDATION_TEMP_CLASS class names which may have been applied
 * when limiting validation to a subset of fields.
 *
 * @param $action(optional) element of that initiated the request, used to determine if invoked in dialog
 *
 * @returns {boolean} true if all fields requiring validation are valid, false otherwise
 */
function validateForm($action) {

    jQuery("." + kradVariables.IGNORE_VALIDATION_TEMP_CLASS).removeClass(kradVariables.IGNORE_VALIDATION_TEMP_CLASS);

    return _validateFormOrDialog($action);
}

/**
 * Validates fields requiring validation, except for those listed, if a specified
 * condition is met.
 *
 * @param $fieldsToSkip Array of jQuery objects on which to ignore validation.
 * @param skipConditionFunc callback function which determines if validation should
 * be ignored for $fieldsToSkip.
 * @param $action(optional) element of that initiated the request, used to determine if invoked in dialog
 *
 * @returns {boolean} true if all fields requiring validation are valid, false otherwise
 */
function validatePartialForm($fieldsToSkip, skipConditionFunc, $action) {

    if (skipConditionFunc()) {
        $fieldsToSkip.addClass(kradVariables.IGNORE_VALIDATION_TEMP_CLASS);
        jQuery("." + kradVariables.IGNORE_VALIDATION_CLASS + ", ." + kradVariables.IGNORE_VALIDATION_TEMP_CLASS).each(function () {
            removeClientValidationError(this);
        });
    } else {
        jQuery("." + kradVariables.IGNORE_VALIDATION_TEMP_CLASS).removeClass(kradVariables.IGNORE_VALIDATION_TEMP_CLASS);
    }

    return _validateFormOrDialog($action);
}

/**
 * Validates all fields requiring validation, in a form or dialog.
 *
 * @param $action(optional) element that initiated the request, used to determine if invoked in dialog
 *
 * @returns {boolean} true if all fields requiring validation are valid, false otherwise
 *
 * @private
 */
function _validateFormOrDialog($action) {

    var inDialog = false;
    if ($action) {
        var $dialogGroup = $action.closest(kradVariables.DIALOG_SELECTOR);
        inDialog = $dialogGroup.length;
    }

    var valid = true;
    if (!inDialog) {
        valid = validate();
    }
    else if (inDialog) {
        valid = validate($dialogGroup);
    }

    return valid;
}

/**
 * Checks to see if any controls depend on the control being validated, if they do calls validate
 * on them as well which will either add errors or remove them
 * Note: with the way that validation works the field must have been previously validated
 *
 * @param element control to check and validate dependent controls for
 * @param nameArray an array that is passed into this method that collects the names that have already been
 * validated/checked, to skip those names in future iterations because this method is recursive
 */
function dependsOnCheck(element, nameArray) {
    if (nameArray == undefined) {
        nameArray = new Array();
    }
    var name;
    if (jQuery(element).is("option")) {
        name = jQuery(element).parent().attr('name');
    }
    else {
        name = jQuery(element).attr('name');
    }
    name = escapeName(name);
    jQuery("[name='" + name + "']").trigger("checkReq");
    nameArray.push(name);

    jQuery(".dependsOn-" + name).each(function () {

        var elementName;
        if (jQuery(this).is("option")) {
            elementName = jQuery(this).parent().attr('name');
        }
        else {
            elementName = jQuery(this).attr('name');
        }
        elementName = escapeName(elementName);

        //if it has one of these classes it means it was already visited by the user
        if (jQuery(this).hasClass("valid") || jQuery(this).hasClass("error")) {
            jQuery.watermark.hide(this);

            //remove the ignore class if any due to a bug in the validate plugin for direct validation on certain types
            var hadIgnore = false;
            if (jQuery(this).hasClass("ignoreValid")) {
                jQuery(this).removeClass("ignoreValid");
                hadIgnore = true;
            }
            var valid = jQuery(this).valid();
            if (hadIgnore) {
                jQuery(this).addClass("ignoreValid");
            }

            if (valid) {
                jQuery(element).removeAttr("aria-invalid");
            }
            else {
                jQuery(element).attr("aria-invalid", "true");
                if (!jQuery(this).is(":focus")) {
                    var id = getAttributeId(jQuery(this).attr('id'));
                    showMessageTooltip(id, true);
                }
            }
            jQuery.watermark.show(this);
            var namePresent = jQuery.inArray(elementName, nameArray);
            if (namePresent == undefined || namePresent == -1) {
                dependsOnCheck(this, nameArray);
            }
        }
    });
}

/**
 * Sets up a req indicator check for the controlName, when it changes, checks to see if it satisfies
 * some booleanFunction and then shows an indicator on the now required field (identified by requiredName).
 * If not satisfied, removes the indicator
 *
 * @param controlName
 * @param requiredName
 * @param booleanFunction
 */
function setupShowReqIndicatorCheck(controlName, requiredName, booleanFunction) {
    if (jQuery("[name='" + escapeName(controlName) + "']").length) {

        var id = jQuery("[name='" + escapeName(requiredName) + "']").attr("id");
        id = getAttributeId(id);

        var label;
        // this check if for use in collections with 'old' and 'new' listings
        // in these cases the 'new' id will be like xxx_comp1
        // however the label is setup for the 'old' control and will be like xxx_comp0
        // therefore strip off the '_comp' portion and search for a label that 'contains' the new id
        if (id.indexOf('_comp') > 0) {
            id = id.substring(0, id.indexOf('_comp'));
            label = jQuery("label[data-label_for*='" + id + "']");
        }
        else {
            label = jQuery("label[data-label_for='" + id + "']");
        }

        // get what to use for the required indicator
        var indicator;
        if (id) {
            indicator = label.data("req_indicator");
            if (indicator === undefined) {
                indicator = jQuery("[data-role='View']").data("req_indicator");
            }
        }

        // check right now if it satisfies the condition, only if an indicator is not shown
        // (indicators that are shown stay shown for this check, as the server or another check must have shown them)
        if (label.find(kradVariables.REQUIRED_MESSAGE_CLASS).length == 0) {
            checkForRequiredness(requiredName, booleanFunction, indicator);
        }

        // also check condition when corresponding control is changed
        jQuery("[name='" + escapeName(controlName) + "']").change(function () {
            checkForRequiredness(requiredName, booleanFunction, indicator);
        });

        jQuery("[name='" + escapeName(controlName) + "']").bind("checkReq", function () {
            checkForRequiredness(requiredName, booleanFunction, indicator);
        });
    }
}

/**
 * Checks a particular field to see if it is now required, the field is considered required if the booleanFunction
 * evaluates to true.
 *
 * @param requiredName
 * @param booleanFunction
 * @param indicator
 */
function checkForRequiredness(requiredName, booleanFunction, indicator) {
    var requiredControl = jQuery("[name='" + escapeName(requiredName) + "']");
    var id = requiredControl.attr("id");

    var label;
    // this check if for use in collections with 'old' and 'new' listings
    // in these cases the 'new' id will be like xxx_comp1
    // however the label is setup for the 'old' control and will be like xxx_comp0
    // therefore strip off the '_comp' portion and search for a label that 'contains' the new id
    if (id.indexOf('_comp') > 0) {
        id = getAttributeId(id);
        id = id.substring(0, id.indexOf('_comp'));
        label = jQuery("label[data-label_for*='" + id + "']");
    }
    else {
        label = jQuery("label[data-label_for='" + id + "']");
    }

    if (indicator != null && indicator.length) {
        if (booleanFunction()) {
            // add required span, aria, and css class
            if (label.find("." + kradVariables.REQUIRED_MESSAGE_CLASS).length == 0) {
                label.append("<span class='" + kradVariables.REQUIRED_MESSAGE_CLASS + "'>" + indicator + "</span>");
            }
            requiredControl.attr("aria-required", "true");
            requiredControl.addClass("required");
        }
        else {
            // remove required span, set aria, and remove css class
            label.find("." + kradVariables.REQUIRED_MESSAGE_CLASS).remove();
            requiredControl.attr("aria-required", "false");
            requiredControl.removeClass("required");
        }
    }
}

//checks to see if the fields with names specified in the name array contain a value
//if they do - returns the total if the num of fields matched
function mustOccurTotal(nameArray, min, max) {
    var total = 0;
    for (i = 0; i < nameArray.length; i++) {
        if (coerceValue(nameArray[i])) {
            total++;
        }
    }

    return total;

}

//checks to see if the fields with names specified in the name array contain a value
//if they do - returns 1 if the num of fields with values are between min/max
//this function is used to for mustoccur constraints nested in others
function mustOccurCheck(total, min, max) {

    if (total >= min && total <= max) {
        return 1;
    }
    else {
        return 0;
    }
}

/**
 * Remove client side validation from the control specified (highlight and messages)
 *
 * @param control the control to remove validation from
 */
function removeClientValidationError(control) {
    var errorClass = kradVariables.ERROR_CLASS;
    var validClass = kradVariables.VALID_CLASS;

    jQuery(control).removeClass(errorClass).addClass(validClass);
    jQuery(control).removeAttr("aria-invalid");

    var id = getAttributeId(jQuery(control).attr("id"));
    if (!id) {
        return;
    }
    var field = jQuery("#" + id);
    var data = getValidationData(field);

    if (data) {
        data.errors = [];
        field.data(kradVariables.VALIDATION_MESSAGES, data);

        if (messageSummariesShown) {
            handleMessagesAtField(id);
        }
        else {
            writeMessagesAtField(id);
        }

        // force hide of tooltip if no messages present
        if (!(data.warnings.length || data.info.length || data.serverErrors.length
                || data.serverWarnings.length || data.serverInfo.length)) {
            hideMessageTooltip(id);
        }
    }
}