/*
 * Copyright 2005-2014 The Kuali Foundation
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
 * Initialize action data for the action by merging any custom settings with global default settings
 *
 * @param jqComponent a jq object that represents the component to correct data for
 */
function initActionData(jqActionComponent) {
    // If an action does not have a setting for something in defaults, use the default
    jQuery.each(actionDefaults, function (key, value) {
        var dataValue = jqActionComponent.data(key.toLowerCase());
        if (dataValue === undefined) {
            jqActionComponent.data(key.toLowerCase(), value);
        }
    });

    // Insert focusId and jumpToId settings into submitData
    var submitData = jqActionComponent.data(kradVariables.SUBMIT_DATA);
    var focusId = jqActionComponent.data(kradVariables.FOCUS_ID);
    if (focusId && focusId !== kradVariables.SELF) {
        submitData.focusId = focusId;
    }
    else if (focusId && focusId === kradVariables.SELF) {
        submitData.focusId = jqActionComponent.attr("id");
    }

    var jumpToId = jqActionComponent.data(kradVariables.JUMP_TO_ID);
    if (jumpToId && jumpToId !== kradVariables.SELF) {
        submitData.jumpToId = jumpToId;
    }
    else if (jumpToId && jumpToId === kradVariables.SELF) {
        submitData.jumpToId = jqActionComponent.attr("id");
    }
}

/**
 * Sets up a new request configured from the given action component and submits
 * the request
 *
 * @param component - dom element the event occurred on (the action)
 */
function actionInvokeHandler(component) {
    var action = jQuery(component);

    var kradRequest = new KradRequest(action);
    kradRequest.send();
}

/**
 * Convenience method for submitting the form via Ajax
 *
 * <p>
 * For full options using the KradRequest function directly
 * </p>
 *
 * @param methodToCall - the controller method to be called
 * @param additionalData - any additional data that needs to be passed to the server
 */
function ajaxSubmitForm(methodToCall, additionalData) {
    var kradRequest = new KradRequest();

    kradRequest.methodToCall = methodToCall;
    kradRequest.additionalData = additionalData;

    kradRequest.send();
}

/**
 * Convenience method for submitting the form via standard browser submit
 *
 * <p>
 * For full options using the KradRequest function directly
 * </p>
 *
 * @param methodToCall - the controller method to be called
 * @param additionalData - any additional data that needs to be passed to the server
 */
function nonAjaxSubmitForm(methodToCall, additionalData) {
    var kradRequest = new KradRequest();

    kradRequest.methodToCall = methodToCall;
    kradRequest.additionalData = additionalData;
    kradRequest.ajaxSubmit = false;

    kradRequest.send();
}

/**
 * Convenience method for submitting the form with option for ajax or non-ajax submit
 *
 * <p>
 * For full options using the KradRequest function directly
 * </p>
 *
 * @param methodToCall - the controller method to be called
 * @param additionalData - any additional data that needs to be passed to the server
 * @param validate - indicates whethere client side validation should be performed before the submit
 * @param ajaxSubmit - whether the submit should be via ajax or standard browser submit
 * @param successCallback - method to invoke after a successful request, only applies to ajax calls
 * @param fieldsToSend (optional) - limit the fields to send to property names defined in an array
 */
function submitForm(methodToCall, additionalData, validate, ajaxSubmit, successCallback, fieldsToSend) {
    var kradRequest = new KradRequest();

    kradRequest.methodToCall = methodToCall;
    kradRequest.additionalData = additionalData;
    kradRequest.validate = validate;
    kradRequest.ajaxSubmit = ajaxSubmit;
    kradRequest.successCallback = successCallback;
    kradRequest.fieldsToSend = fieldsToSend;

    kradRequest.send();
}

/**
 * Within a multi-page view changes the currently loaded page to the page identified
 * by the given id
 *
 * @param pageId id for the page to navigate to
 */
function navigateToPage(pageId) {
    ajaxSubmitForm(kradVariables.NAVIGATE_METHOD_TO_CALL, {"actionParameters[navigateToPageId]": pageId});
}

/**
 * Convenience method for redirecting to a URL
 *
 * @param url to redirect to
 */
function redirect(url) {
    window.location = url;
}

/**
 *
 * @param {Object=} $group optional parameter to target a single group for validation with no bubbling of validation
 * messages
 * @returns {boolean} true if the group contains all valid fields, false otherwise
 */
function validate($group) {
    clientErrorStorage = new Object();
    var valid = true;

    jQuery.watermark.hideAll();
    pauseTooltipDisplay = true;

    if (validateClient) {
        pageValidationPhase = true;
        // Turn on this flag to avoid prematurely writing out messages which will cause performance issues if MANY
        // fields have validation errors simultaneously (first we are only checking for errors, not checking and
        // writing simultaneously like normal)
        clientErrorExistsCheck = true;

        // Temporarily turn off this flag to avoid traversing unneeded logic (all messages will be shown at the end)
        messageSummariesShown = false;

        if (!$group) {
            valid = _validateForm();
        }
        else {
            valid = _validateGroupOnly($group);
        }
    }

    if (!valid) {

        //ensure all non-visible controls are visible to the user
        jQuery(".error:not(:visible)").each(function () {
            cascadeOpen(jQuery(this));
        });

        if (!$group) {
            jumpToTop();
            jQuery(".uif-pageValidationMessages li.uif-errorMessageItem:first > a").focus();
        }
        else {

        }
    }

    jq.watermark.showAll();
    pauseTooltipDisplay = false;

    return valid;
}

/**
 * Runs client side validation on the entire form and returns the result (an alert is also given
 * if errors are encountered)
 */
function _validateForm() {
    // Validate the whole form
    validForm = jq("#kualiForm").valid();

    // Handle field message bubbling manually, but do not write messages out yet
    jQuery("div[data-role='InputField']").each(function () {
        var id = jQuery(this).attr("id");
        var field = jQuery("#" + id);
        var data = getValidationData(field);
        var parent = field.data(kradVariables.PARENT_DATA_ATTRIBUTE);
        handleMessagesAtGroup(parent, id, data, true);
    });

    // Toggle the flag back to default
    clientErrorExistsCheck = false;

    // Message summaries are going to be shown
    messageSummariesShown = true;

    // Finally, write the result of the validation messages
    writeMessagesForPage();
    pageValidationPhase = false;

    return validForm;
}

/**
 * Target a single group for client validation, and do not bubble error messages up
 *
 * @param $group jQuery object representing the group to be targetted
 * @returns boolean true if the group's fields are valid, false otherwise
 */
function _validateGroupOnly($group) {
    var id = $group.attr("id");
    var parentId = $group.attr("data-" + kradVariables.PARENT_DATA_ATTRIBUTE);
    $group.removeAttr("data-" + kradVariables.PARENT_DATA_ATTRIBUTE);

    var $groupControls = $group.find("[data-role='Control']");
    if ($groupControls.length === 0) {
        return true;
    }

    var validGroup = $groupControls.valid();

    // Handle field message bubbling manually, but do not write messages out yet
    $group.find("div[data-role='InputField']").each(function () {
        var id = jQuery(this).attr("id");
        var field = jQuery("#" + id);
        var data = getValidationData(field);
        var parent = field.data(kradVariables.PARENT_DATA_ATTRIBUTE);
        handleMessagesAtGroup(parent, id, data, true);
    });

    // Toggle the flag back to default
    clientErrorExistsCheck = false;

    // Message summaries are going to be shown
    messageSummariesShown = true;

    var validationData = getValidationData($group, true);
    // Finally, write the result of the validation messages
    if (validationData) {
        var messageMap = validationData.messageMap;
        if (!messageMap) {
            messageMap = {};
            validationData.messageMap = messageMap;
        }
    }

    writeMessagesForChildGroups(id);
    writeMessagesForGroup(id, validationData, true, false);
    displayHeaderMessageCount(id, validationData);
    $group.find(".uif-errorMessageItem > div").show();

    // Turn off page validation phase flag to return to normal validation processing
    pageValidationPhase = false;

    // Restore parent id if one exists, by default dialog groups do not have a parent id
    if (parentId) {
        $group.attr("data-" + kradVariables.PARENT_DATA_ATTRIBUTE, parentId);
    }

    return validGroup;
}

/**
 * Creates the placeholder span necessary to place the retrieved component (or if the component exists in the
 * dom it is replaced by the placeholder span), then retrieves the component.
 *
 * @param componentId id for the component to create placeholder for and retrieve
 * @param callback function callback to invoke after the component has been retrieved
 * @param additionalData data to add to the retrieve ajax request
 */
function createPlaceholderAndRetrieve(componentId, callback, additionalData) {
    var placeholderSpan = '<span id="' + componentId + '"class="' + kradVariables.CLASSES.PLACEHOLDER +
            '" data-role="' + kradVariables.DATA_ROLES.PLACEHOLDER + '"></span>';

    if (jQuery('#' + componentId).length == 0) {
        jQuery('#' + kradVariables.IDS.DIALOGS).append(placeholderSpan);
    } else {
        jQuery('#' + componentId).replaceWith(placeholderSpan);
    }

    retrieveComponent(componentId, undefined, callback, additionalData, true);
}

/**
 * Calls the updateComponent method on the controller with component id passed in, this id is
 * the component id with any/all suffixes on it not the dictionary id
 *
 * <p>
 * Retrieves the component with the matching id from the server and replaces a matching
 * _refreshWrapper marker span with the same id with the result.  In addition, if the result contains a label
 * and a displayWith marker span has a matching id, that span will be replaced with the label content
 * and removed from the component.  This allows for label and component content separation on fields
 * </p>
 *
 * @param id - id for the component to retrieve
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 * @param successCallback - (optional) additional callback function to be executed after the component is retrieved
 * @param additionalData - (optional) additional data to be submitted with the request
 * @param disableBlocking - (optional) turns off blocking and loading messaging
 * @param fieldsToSend (optional) - limit the fields to send to property names defined in an array
 */
function retrieveComponent(id, methodToCall, successCallback, additionalData, disableBlocking, fieldsToSend) {
    var refreshComp = jQuery("#" + id);

    // if a call is made from refreshComponentUsingTimer() and the component does not exist on the page or is hidden
    // then get the handle of the refreshTimer and clear the timer. Also remove it from the refreshTimerComponentMap
    if (refreshComp === undefined || refreshComp.filter(':visible').length === 0) {
        var refreshHandle = refreshTimerComponentMap[id];
        if (!(refreshHandle === undefined)) {
            clearInterval(refreshHandle);
            delete refreshTimerComponentMap[id];

            return;
        }
    }

    if (!methodToCall) {
        methodToCall = kradVariables.REFRESH_METHOD_TO_CALL;
    }

    var kradRequest = new KradRequest();

    kradRequest.methodToCall = methodToCall;
    kradRequest.ajaxReturnType = kradVariables.RETURN_TYPE_UPDATE_COMPONENT;
    kradRequest.successCallback = successCallback;
    kradRequest.additionalData = additionalData;
    kradRequest.refreshId = id;
    kradRequest.fieldsToSend = fieldsToSend;

    if (disableBlocking) {
        kradRequest.disableBlocking = disableBlocking;
    }

    kradRequest.send();
}

/**
 * Performs client side validation against the controls present in a collection add line.
 *
 * @param collectionGroupId id for the collection whose add line should be validated
 */
function validateAddLine(collectionGroupId) {
    var collectionGroup = jQuery("#" + collectionGroupId);
    var addControls = collectionGroup.data(kradVariables.ADD_CONTROLS);

    var controlsToValidate = jQuery(addControls, collectionGroup);

    var valid = validateLineFields(controlsToValidate, false);
    if (!valid) {
        return false;
    }

    return true;
}

/**
 * Performs client side validation against the controls present in a collection line
 *
 * @param collectionName - name (binding path) for the collection
 * @param lineIndex - zero based index for the collection line
 */
function validateLine(collectionName, lineIndex) {
    var controlsToValidate = jQuery("[name^='" + collectionName + "[" + lineIndex + "]']");

    var valid = validateLineFields(controlsToValidate, true);
    if (!valid) {
        return false;
    }

    return true;
}

/**
 * Performs client side validation on the list of given controls and returns whether the controls
 * are valid
 *
 * @param controlsToValidate - list of controls (jQuery wrapping objects) that should be validated
 */
function validateLineFields(controlsToValidate, writePageMessages) {
    var valid = true;
    var invalidId = "";

    // skip completely if client validation is off
    if (!validateClient) {
        return valid;
    }

    jQuery.watermark.hideAll();

    // Turn on this flag to avoid prematurely writing out messages which will cause performance issues if MANY
    // fields have validation errors simultaneously (first we are only checking for errors, not checking and
    // writing simultaneously like normal)
    clientErrorExistsCheck = true;

    // Temporarily turn off this flag to avoid traversing unneeded logic (all messages will be shown at the end)
    var tempMessagesSummariesShown = messageSummariesShown;
    messageSummariesShown = false;

    controlsToValidate.each(function () {
        var control = jQuery(this);
        var fieldId = jQuery(this).closest("div[data-role='InputField']").attr("id");
        var field = jQuery("#" + fieldId);
        var parent = field.data("parent");
        var validValue = true;

        // remove ignoreValid because there are issues with the plugin if it stays on
        control.removeClass("ignoreValid");

        haltValidationMessaging = true;

        if (control.length && !control.prop("disabled")) {
            control.valid();
            if (control.hasClass("error")) {
                validValue = false;
            }
        }

        var data = getValidationData(field);
        handleMessagesAtGroup(parent, fieldId, data, true);

        haltValidationMessaging = false;

        //details visibility check
        if (control.not(":visible") && !validValue) {
            cascadeOpen(control);
        }

        if (!validValue) {
            valid = false;
            if (invalidId.length == 0) {
                invalidId = this.id;
            }
        }

        control.addClass("ignoreValid");
    });

    // Toggle the flag back to default
    clientErrorExistsCheck = false;

    // Message summaries are going to be shown
    messageSummariesShown = tempMessagesSummariesShown;

    if (writePageMessages && messageSummariesShown) {
        // Finally, write the result of the validation messages
        writeMessagesForPage();
    }

    jQuery.watermark.showAll();

    if (invalidId.length != 0) {
        jQuery("#" + invalidId).focus();
    }

    return valid;
}

/**
 * Retrieves a page for the collection by id specified, the linkElement supplied must have "num" data to retrieve
 * the page; this method refreshes the collection with new page showing
 *
 * @param linkElement the link clicked with "num" data specifying the page to retrieve
 * @param collectionId the collection by id to retrieve the new page from
 */
function retrieveCollectionPage(linkElement, collectionId) {
    var link = jQuery(linkElement);
    var parentLI = link.parent();

    // Skip processing if the link supplied is disabled or active
    if (parentLI.is("." + kradVariables.DISABLED_CLASS) || parentLI.is("." + kradVariables.ACTIVE_CLASS)) {
        return;
    }

    var pageNumber = jQuery(linkElement).data(kradVariables.PAGE_NUMBER_DATA);
    retrieveComponent(collectionId, kradVariables.RETRIEVE_COLLECTION_PAGE_METHOD_TO_CALL,
            null, {pageNumber: pageNumber}, true);
}

/**
 * Ensures that the componentObject is visible by "opening" mechanisms that may be hiding it such as
 * row details or group disclosure.  Used to make invalid fields visible on validation.
 *
 * @param componentObject the object to check for visibility of and "open" parent containing elements to make
 * it visible
 */
function cascadeOpen(componentObject) {
    if (componentObject.not(":visible")) {
        var detailsDivs = componentObject.parents("[data-role='details']");
        detailsDivs.each(function () {
            jQuery(this).parent().find("> a").click();
        });

        var disclosureDivs = componentObject.parents("[data-role='disclosureContent']");
        disclosureDivs.each(function () {
            if (!jQuery(this).data("open")) {
                jQuery(this).parent().find("a[data-linkfor='" + jQuery(this).attr("id") + "']").click();
            }
        });
    }
}

/** Progressive Disclosure */

/**
 * Same as setupRefreshCheck except the condition will always be true (always refresh when
 * value changed on control)
 *
 * @param controlName - value for the name attribute for the control the event should be generated for
 * @param refreshId - id for the component that should be refreshed when change occurs
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 * @param fieldsToSend (optional) - limit the fields to send to property names defined in an array
 */
function setupOnChangeRefresh(controlName, refreshId, methodToCall, fieldsToSend) {
    setupRefreshCheck(controlName, refreshId, function () {
        return true;
    }, methodToCall, fieldsToSend);
}

/**
 * Sets up the conditional refresh mechanism in js by adding a change handler to the control
 * which may satisfy the conditional refresh condition passed in.  When the condition is satisfied,
 * refresh the necessary content specified by id by making a server call to retrieve a new instance
 * of that component
 *
 * @param controlName - value for the name attribute for the control the event should be generated for
 * @param refreshId - id for the component that should be refreshed when condition occurs
 * @param condition - function which returns true to refresh, false otherwise
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 * @param fieldsToSend (optional) - limit the fields to send to property names defined in an array
 */
function setupRefreshCheck(controlName, refreshId, condition, methodToCall, fieldsToSend) {
    jQuery("[name='" + escapeName(controlName) + "']").live('change', function () {
        // visible check because a component must logically be visible to refresh
        var refreshComp = jQuery("#" + refreshId);
        if (refreshComp.length) {
            if (condition()) {
                retrieveComponent(refreshId, methodToCall, null, null, false, fieldsToSend);
            }
        }
    });
}

/**
 * Setup disabled check handlers that will evaluate a passed in condition and will disable/enable the component
 * based on the result (true to disable, false to enable).  controlName represents the field to be evaluated and
 * disableCompId represents the component by id to be disabled/enabled as a result.
 *
 * @param controlName name of the control to put a handler on
 * @param disableCompId id of the component to disable/enable
 * @param disableCompType type of the component being disabled/enabled
 * @param condition function that if returns true disables the component, and if returns false enables the component
 * @param onKeyUp true if evaluating on keyUp, only applies to textarea/text inputs
 */
function setupDisabledCheck(controlName, disableCompId, disableCompType, condition, onKeyUp) {
    var theControl = jQuery("[name='" + escapeName(controlName) + "']");

    // Namespace the event type to avoid duplicates if the disabled enhanced component gets refreshed
    var eventType = "change.disable-" + disableCompId;

    if (onKeyUp && (theControl.is("textarea") || theControl.is("input[type='text'], input[type='password']"))) {
        // Uses input event to account for all text changes
        eventType = "input.disable-" + disableCompId;
    }

    if (disableCompType == "radioGroup" || disableCompType == "checkboxGroup") {
        jQuery(document).off(eventType);
        jQuery(document).on(eventType, "[name='" + escapeName(controlName) + "']", function () {
            if (condition()) {
                jQuery("input[id^='" + disableCompId + "']").prop("disabled", true);
            }
            else {
                jQuery("input[id^='" + disableCompId + "']").prop("disabled", false);
            }
        });
    }
    else {
        // if disabledWhenChangedPropertyNames is configured with multiple property names the eventtype is the same
        // adding the controlName to make it specific
        jQuery(document).off(eventType, "[name='" + escapeName(controlName) + "']");
        jQuery(document).on(eventType, "[name='" + escapeName(controlName) + "']", function () {
            var disableControl = jQuery("#" + disableCompId);
            if (condition()) {
                disableControl.prop("disabled", true);
                disableControl.addClass("disabled");
                if (disableCompType === "actionLink" || disableCompType === "action") {
                    disableControl.attr("tabIndex", "-1");
                }

                if (disableControl.is(".hasDatepicker")) {
                    disableControl.datepicker("disable");
                    disableControl.next(".ui-datepicker-trigger").css("cursor", "not-allowed");
                }
                if (disableControl.is(".uif-spinnerControl")) {
                    disableControl.spinner("disable");
                }
            }
            else {
                disableControl.prop("disabled", false);
                disableControl.removeClass("disabled");
                if (disableCompType === "actionLink" || disableCompType === "action") {
                    disableControl.attr("tabIndex", "0");
                }

                if (disableControl.is(".hasDatepicker")) {
                    disableControl.datepicker("enable");
                    disableControl.next(".ui-datepicker-trigger").css("cursor", "pointer");
                }
                if (disableControl.is(".uif-spinnerControl")) {
                    disableControl.spinner("enable");
                }
            }
        });
    }
}

/**
 * Sets up the progressive disclosure mechanism in js by adding a change handler to the control
 * which may satisfy the progressive disclosure condition passed in.  When the condition is satisfied,
 * show the necessary content, otherwise hide it.  If the content has not yet been rendered then a server
 * call is made to retrieve the content to be shown.  If alwaysRetrieve is true, the component
 * is always retrieved from the server when disclosed.
 * Do not add check if the component is part of the "old" values on a maintanance document (endswith _c0).
 *
 * @param controlName
 * @param disclosureId
 * @param condition - function which returns true to disclose, false otherwise
 * @param methodToCall - name of the method that should be invoked for the retrieve call (if custom method is needed)
 * @param fieldsToSend (optional) - limit the fields to send to property names defined in an array
 */
function setupProgressiveCheck(controlName, disclosureId, condition, alwaysRetrieve, methodToCall, fieldsToSend) {
    jQuery("[name='" + escapeName(controlName) + "']").live('change', function () {
        var refreshDisclosure = jQuery("#" + disclosureId);
        if (refreshDisclosure.length) {
            var displayWithId = disclosureId;

            if (condition()) {
                if (refreshDisclosure.data("role") == "placeholder" || alwaysRetrieve) {
                    retrieveComponent(disclosureId, methodToCall, null, null, false, fieldsToSend);
                }
                else {
                    refreshDisclosure.addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);
                    refreshDisclosure.show();

                    if (refreshDisclosure.parent().is("td")) {
                        refreshDisclosure.parent().show();
                    }

                    refreshDisclosure.animate({backgroundColor: "transparent"}, 6000);

                    //re-enable validation on now shown inputs
                    hiddenInputValidationToggle(disclosureId);

                    var displayWithLabel = jQuery(".displayWith-" + displayWithId);
                    displayWithLabel.show();
                    if (displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")) {
                        displayWithLabel.parent().show();
                    }
                }
            }
            else {
                refreshDisclosure.hide();

                // ignore validation on hidden inputs
                hiddenInputValidationToggle(disclosureId);

                var displayWithLabel = jQuery(".displayWith-" + displayWithId);
                displayWithLabel.hide();
                if (displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")) {
                    displayWithLabel.parent().hide();
                }
            }

            hideEmptyCells();
        }
    });
}

/**
 * Disables client side validation on any inputs within the element(by id) passed in , if
 * that element is hidden.  Otherwise, it turns input validation back on if the element and
 * its children are visible
 *
 * @param id - id for the component for which the input hiddens should be processed
 */
function hiddenInputValidationToggle(id) {
    var element = jQuery("#" + id);
    if (element.length) {
        if (element.css("display") === "none") {
            jQuery(":input:hidden", element).each(function () {
                storeOriginalDisabledProperty(jQuery(this));
                jQuery(this).addClass("ignoreValid");
                //disable hidden inputs to prevent from being submitted unless it is a hidden field
                if (!jQuery(this).is("input[type='hidden']")) {
                    jQuery(this).prop("disabled", true);
                }
            });
        }
        else {
            jQuery(":input:visible", element).each(function () {
                storeOriginalDisabledProperty(jQuery(this));
                jQuery(this).removeClass("ignoreValid");
                //return to original disabled property value
                jQuery(this).prop("disabled", jQuery(this).data('original-disabled'));
            });
        }
    }
}

/**
 * Stores the original value of the disabled property of the element into jquery data.
 * This ensures that the correct value is set after toggling in hiddenInputValidation().
 *
 * @param element - jQuery element to examine and set the original-disabled data.
 */
function storeOriginalDisabledProperty(element) {
    //capture original disabled property value
    if (element.data('original-disabled') === undefined) {
        element.data("original-disabled", element.prop("disabled"));
    }
}

/**
 * Refreshes a component by calling retrieveComponent() at the given time interval
 *
 * @param componentId - id of the component to be refreshed
 * @param methodToCall - controller method to call on refresh
 * @param timeInterval -  interval in seconds at which the component should be refreshed
 */
function refreshComponentUsingTimer(componentId, methodToCall, timeInterval) {
    var refreshTimer = refreshTimerComponentMap[componentId];

    // if a timer already exists for the component then clear it and remove it from the map
    // this is done so that the time interval between executions remains the same.
    if (refreshTimer != null) {
        clearInterval(refreshTimer);
        delete refreshTimerComponentMap[componentId];
    }

    //set a new timer on the component
    refreshTimerComponentMap[componentId] = setInterval(function () {
        retrieveComponent(componentId, methodToCall);
    }, timeInterval * 1000);
}

/**
 *  Open a hidden section in a bootstrap popover and freeze it until the user
 *  clicks outside of the popup, or on the optional close button.
 *
 * @param e event (required)
 * @param contentId id of hidden section with content (required)
 * @param popoverOptions map of popover options (optional)
 * @param closeButton when true, a small close button is rendered in the top-right corner of the popup (optional)
 **/
function openPopoverContent(e, contentId, popoverOptions, useCloseButton) {
    stopEvent(e);

    var popupTarget = jQuery((e.currentTarget) ? e.currentTarget : e.srcElement);
    _openPopover(popupTarget, contentId, popoverOptions, useCloseButton);
}

/**
 *  Open a hidden section in a bootstrap popover and freeze it until the user
 *  clicks outside of the popup, or on the optional close button.
 *
 * @param popupTarget jQuery object representing the target
 * @param contentId id of hidden section with content (required)
 * @param popoverOptions map of popover options (optional)
 * @param closeButton when true, a small close button is rendered in the top-right corner of the popup (optional)
 **/
function _openPopover(popupTarget, contentId, popoverOptions, useCloseButton) {
    var popoverData = jQuery(popupTarget).data(kradVariables.POPOVER_DATA);
    var popupContent = jQuery("#" + contentId);
    if (popoverData && popoverData.shown) {
        _hidePopover(popupTarget);
        return;
    }

    hideTooltips();

    var clickName = "click." + popupTarget.attr('id');
    popupTarget.attr("data-popupContentId", popupContent.attr("id"));
    popupContent.after("<div id='" + contentId + "_popupPlaceholder' style='display:none'></div>");
    popupContent = popupContent.detach().show();
    popupContent.addClass("uif-popupContent-inner");

    // add required class uif-tooltip to action and create popup
    if (!popoverData) {
        if (popoverOptions && !popoverOptions.placement) {
            popoverOptions.placement = "auto bottom";
        }
        else if (!popoverOptions) {
            popoverOptions = {placement: "auto bottom"};
        }

        popoverData = initializeTooltip(popupTarget, popoverOptions, "uif-popupContent");

        if (useCloseButton) {
            var closeButton = jQuery('<div class="uif-popup-closebutton"/>');
            closeButton.on(clickName, function () {
                _hidePopover(popupTarget);
            });
            popupContent.prepend(closeButton);
        }
    }

    popoverData.options.content = popupContent;

    popupTarget.popover("show");

    popoverData.shown = true;

    // close popup on any click outside current popup
    jQuery(document).on(clickName, function (e) {
        var docTarget = jQuery((e.target) ? e.target : e.srcElement);
        if (docTarget.parents("div.popover").length === 0) {
            _hidePopover(popupTarget);
        }
    });

    function _hidePopover(target) {
        var popoverData = jQuery(target).data(kradVariables.POPOVER_DATA);
        popoverData.shown = false;
        jQuery("#" + contentId + "_popupPlaceholder").replaceWith(popupContent.hide());
        target.popover("hide");
        jQuery(document).off("click." + target.attr('id'));
    }
}

/**
 *  Locate all bubblepopup content and see if any have a error displayed (via
 *  class "uif-hasError").  If so, locate the action which opens the content
 *  and submit the click event for that action.
 **/
function openPopoverContentsWithErrors() {
    var popupFormId;
    var popupContent = {};
    jQuery("div.uif-popupContent-inner").each(function () {
        if (popupContent[this.id] === true) {
            // .detach() apparently creates duplicates in the DOM, and this code eliminates them
            return false;
        }
        popupContent[this.id] = true;

        if (jQuery(".uif-hasError", jQuery(this)).length > 0) {
            popupFormId = this.id;

            var popupTarget = jQuery("[data-popupContentId='" + this.id + "']");
            _openPopover(popupTarget, popupFormId);

        }
    });
}

