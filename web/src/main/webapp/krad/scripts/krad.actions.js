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
 */
function submitForm(methodToCall, additionalData, validate, ajaxSubmit, successCallback) {
    var kradRequest = new KradRequest();

    kradRequest.methodToCall = methodToCall;
    kradRequest.additionalData = additionalData;
    kradRequest.validate = validate;
    kradRequest.ajaxSubmit = ajaxSubmit;
    kradRequest.successCallback = successCallback;

    kradRequest.send();
}

/**
 * Runs client side validation on the entire form and returns the result (an alert is also given
 * if errors are encountered)
 */
function validateForm() {
    var validForm = true;

    jQuery.watermark.hideAll();
    pauseTooltipDisplay = true;

    if(validateClient){
        // turn on this flag to enable the page level summaries to now be shown for errors
        messageSummariesShown = true;
        validForm = jq("#kualiForm").valid();
    }

	if(!validForm){
        validForm = false;

        //ensure all non-visible controls are visible to the user
        jQuery(".error:not(:visible)").each(function(){
            cascadeOpen(jQuery(this));
        });

		jumpToTop();
        showClientSideErrorNotification();
	}

    jq.watermark.showAll();
    pauseTooltipDisplay = false;

    return validForm;
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
 */
function retrieveComponent(id, methodToCall, successCallback, additionalData) {
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

    kradRequest.send();
}

/**
 * Performs client side validation against the controls present in a collection add line
 *
 * @param collectionGroupId - id for the collection whose add line should be validated
 * @param addViaLightbox - (optional) flag to indicate if add controls are in a lightbox
 */
function validateAddLine(collectionGroupId, addViaLightbox) {
    var collectionGroup = jQuery("#" + collectionGroupId);
    var addControls = collectionGroup.data("addcontrols");

    if (addViaLightbox) {
        collectionGroup = jQuery("#kualiLightboxForm");
    }

    var controlsToValidate = jQuery(addControls, collectionGroup);

    var valid = validateLineFields(controlsToValidate);
    if (!valid) {
        showClientSideErrorNotification();

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

    var valid = validateLineFields(controlsToValidate);
    if (!valid) {
        showClientSideErrorNotification();

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
function validateLineFields(controlsToValidate) {
    var valid = true;

    jQuery.watermark.hideAll();

    controlsToValidate.each(function () {
        var control = jQuery(this);
        control.removeClass("ignoreValid");
        var validValue = true;

        haltValidationMessaging = true;

        if(!control.prop("disabled")){
            control.valid();
            if(control.hasClass("error")){
                validValue = false;
            }
        }

        haltValidationMessaging = false;

        //details visibility check
        if (control.not(":visible") && !validValue){
            cascadeOpen(control);
        }

        if (!validValue) {
            valid = false;
        }

        control.addClass("ignoreValid");
    });

    jQuery.watermark.showAll();

    return valid;
}

/**
 * Ensures that the componentObject is visible by "opening" mechanisms that may be hiding it such as
 * row details or group disclosure.  Used to make invalid fields visible on validation.
 *
 * @param componentObject the object to check for visibility of and "open" parent containing elements to make
 * it visible
 */
function cascadeOpen(componentObject){
    if(componentObject.not(":visible")){
        var detailsDivs = componentObject.parents("div[data-role='details']");
        detailsDivs.each(function(){
            jQuery(this).parent().find("> a").click();
        });

        var disclosureDivs = componentObject.parents("div[data-role='disclosureContent']");
        disclosureDivs.each(function(){
            if(!jQuery(this).data("open")){
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
 */
function setupOnChangeRefresh(controlName, refreshId, methodToCall) {
    setupRefreshCheck(controlName, refreshId, function () {
        return true;
    }, methodToCall);
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
 */
function setupRefreshCheck(controlName, refreshId, condition, methodToCall) {
    jQuery("[name='" + escapeName(controlName) + "']").live('change', function () {
        // visible check because a component must logically be visible to refresh
        var refreshComp = jQuery("#" + refreshId);
        if (refreshComp.length) {
            if (condition()) {
                retrieveComponent(refreshId, methodToCall);
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
function setupDisabledCheck(controlName, disableCompId, disableCompType, condition, onKeyUp){
    var theControl = jQuery("[name='" + escapeName(controlName) + "']");
    var eventType = 'change';

    if(onKeyUp && (theControl.is("textarea") || theControl.is("input[type='text'], input[type='password']"))){
        eventType = 'keyup';
    }

    if(disableCompType == "radioGroup" || disableCompType == "checkboxGroup"){
        theControl.on(eventType, function (){
            if(condition()){
                jQuery("input[id^='" + disableCompId + "']").prop("disabled", true);
            }
            else{
                jQuery("input[id^='" + disableCompId + "']").prop("disabled", false);
            }
        });
    }
    else{
        theControl.on(eventType, function (){
            var disableControl = jQuery("#" + disableCompId);
            if(condition()){
                disableControl.prop("disabled", true);
                disableControl.addClass("disabled");
                if(disableCompType === "actionLink" || disableCompType === "action"){
                    disableControl.attr("tabIndex", "-1");
                }
            }
            else{
                disableControl.prop("disabled", false);
                disableControl.removeClass("disabled");
                if(disableCompType === "actionLink" || disableCompType === "action"){
                    disableControl.attr("tabIndex", "0");
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
 */
function setupProgressiveCheck(controlName, disclosureId, baseId, condition, alwaysRetrieve, methodToCall) {
    if (!baseId.match("\_c0$")) {
        jQuery("[name='" + escapeName(controlName) + "']").live('change', function () {
            var refreshDisclosure = jQuery("#" + disclosureId);
            if (refreshDisclosure.length) {
                var displayWithId = disclosureId;

                if (condition()) {
                    if (refreshDisclosure.data("role") == "placeholder" || alwaysRetrieve) {
                        retrieveComponent(disclosureId, methodToCall);
                    }
                    else {
                        refreshDisclosure.addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);
                        refreshDisclosure.show();

                        if (refreshDisclosure.parent().is("td")) {
                            refreshDisclosure.parent().show();
                        }

                        refreshDisclosure.animate({backgroundColor:"transparent"}, 6000);

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
            }
        });
    }
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
        if (element.css("display") == "none") {
            jQuery(":input:hidden", element).each(function () {
                jQuery(this).addClass("ignoreValid");
                jQuery(this).prop("disabled", true);
            });
        }
        else {
            jQuery(":input:visible", element).each(function () {
                jQuery(this).removeClass("ignoreValid");
                jQuery(this).prop("disabled", false);
            });
        }
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

