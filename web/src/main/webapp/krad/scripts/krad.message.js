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
    var loadingMessage =  '<h1><img src="' + getConfigParam(kradVariables.IMAGE_LOCATION) + 'loading.gif" alt="working..." />Loading...</h1>';
    var savingMessage = '<h1><img src="' + getConfigParam(kradVariables.IMAGE_LOCATION) + 'loading.gif" alt="working..." />Saving...</h1>';

    var methodToCall = jQuery("input[name='methodToCall']").val();
    var unblockUIOnLoading = jQuery("input[name='unblockUIOnLoading']").val();

    if (unblockUIOnLoading == null || unblockUIOnLoading.toUpperCase() == "false".toUpperCase()) {
        if (top == self) {
            //no portal
            if (showLoading) {
                if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                    jQuery.blockUI({message: savingMessage});
                }
                else {
                    jQuery.blockUI({message: loadingMessage});
                }
            }
            else {
                jQuery.unblockUI();
            }
        }
        else if (top.jQuery == null) {
            if (showLoading) {
                if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                    top.jQuery.blockUI({message: savingMessage});
                }
                else {
                    top.jQuery.blockUI({message: loadingMessage});
                }
            }
            else {
                top.jQuery.unblockUI();
            }
        }
        else {
            if (showLoading) {
                if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                    top.jQuery.blockUI({message: savingMessage});
                }
                else {
                    top.jQuery.blockUI({message: loadingMessage});
                }
            }
            else {
                top.jQuery.unblockUI();
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
    var fieldMarkerSpan = jQuery("#" + fieldId + "_attribute_markers");
    var fieldIcon = jQuery("#" + fieldId + "_changeIcon");

    if (fieldMarkerSpan.length > 0 && fieldIcon.length == 0) {
        fieldMarkerSpan.append("<img id='" + fieldId + "_changeIcon' alt='change' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "asterisk_orange.png'>");
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
    var targetElement = jQuery("#" + fieldId + idSuffix).find("[class~=uif-headerText]");
    var headerIcon = jQuery("#" + fieldId + "_changeIcon");

    if (targetElement.length > 0 && headerIcon.length == 0) {
        targetElement.append("<img id='" + fieldId + "_changeIcon' class='" + kradVariables.CHANGED_HEADER_ICON_CLASS+"' alt='change' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "asterisk_orange.png'>");
    }
}

// Applies the watermark to the input with the id specified
function createWatermark(id, watermark) {
    jQuery("#" + id).watermark(watermark);
}

/**
 * If the content is an incident report view, replaces the current view with the incident report and
 * returns true, otherwise returns false
 *
 * @param content
 * @returns {Boolean} true if there was an incident, false otherwise
 */
function handleIncidentReport(content) {
    var viewId = jQuery("#viewId", content);
    if (viewId.length && viewId.val() === kradVariables.INCIDENT_REPORT_VIEW_CLASS) {
//        jQuery('#' + kradVariables.APP_ID).replaceWith(content);
//        runHiddenScriptsAgain();
        return true;
    }
    else {
        return false;
    }
}