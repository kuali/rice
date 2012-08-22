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

// global vars
var jq = jQuery.noConflict();

// clear out blockUI css, using css class overrides
jQuery.blockUI.defaults.css = {};
jQuery.blockUI.defaults.overlayCSS = {};

// validation init
var pageValidatorReady = false;
var validateClient = true;
var messageSummariesShown = false;
var pauseTooltipDisplay = false;
var haltValidationMessaging = false;

var errorImage;
var errorGreyImage;
var warningImage;
var infoImage;
var detailsOpenImage;
var detailsCloseImage;
var ajaxReturnHandlers = {};

// map of componentIds and refreshTimers
var refreshTimerComponentMap = {};

// common event registering done here through JQuery ready event
jQuery(document).ready(function () {
    setPageBreadcrumb();

    // buttons
    jQuery("input:submit").button();
    jQuery("input:button").button();
    jQuery("a.button").button();
    jQuery(".uif-dialogButtons").button();
    jQuery(".uif-dialogButtons").next('label').addClass('uif-primaryDialogButton');

    // common ajax setup
    jQuery.ajaxSetup({
        error:function (jqXHR, textStatus, errorThrown) {
            showGrowl('Status: ' + textStatus + '<br/>' + errorThrown, 'Server Response Error', 'errorGrowl');
        }
    });

    // stop previous loading message
    hideLoading();

    // hide the ajax progress display screen if the page is replaced e.g. by a login page when the session expires
    jQuery(window).unload(function () {
        hideLoading();
    });

    runHiddenScripts("");

    // show the page
    jQuery("#" + kradVariables.APP_ID).show();

    // setup the various event handlers for fields - THIS IS IMPORTANT
    initFieldHandlers();
});

/**
 * Sets up the various handlers for various field controls.
 * This function includes handlers that are critical to the behavior of KRAD validation and message frameworks
 * on the client
 */
function initFieldHandlers() {
    // var HANDLE_FIELD_MESSAGES_EVENT = "handleFieldsetMessages";
    var validationTooltipOptions = {
        position:"top",
        align:"left",
        distance:0,
        manageMouseEvents:false,
        themePath:"../krad/plugins/tooltip/jquerybubblepopup-theme/",
        alwaysVisible:false,
        tail:{align:"left"},
        themeMargins: {total: "13px",difference: "2px"}
    };

    jQuery(document).on("mouseenter",
            "[data-role='InputField'] input:not([type='image']),"
                    + "[data-role='InputField'] fieldset, "
                    + "[data-role='InputField'] fieldset > span > input:radio,"
                    + "[data-role='InputField'] fieldset > span > input:checkbox,"
                    + "[data-role='InputField'] fieldset > span > label, "
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea",
            function (event) {
                var fieldId = jQuery(this).closest("[data-role='InputField']").attr("id");
                var data = jQuery("#" + fieldId).data("validationMessages");
                if (data && data.useTooltip) {
                    var elementInfo = getHoverElement(fieldId);
                    var element = elementInfo.element;
                    var tooltipElement = this;
                    var focus = jQuery(tooltipElement).is(":focus");
                    if (elementInfo.type == "fieldset") {
                        //for checkbox/radio fieldsets we put the tooltip on the label of the first input
                        tooltipElement = jQuery(element).filter(".uif-tooltip");
                        //if the fieldset or one of the inputs have focus then the fieldset is considered focused
                        focus = jQuery(element).filter("fieldset").is(":focus")
                                || jQuery(element).filter("input").is(":focus");
                    }

                    var hasMessages = jQuery("[data-messagesFor='" + fieldId + "']").children().length;

                    //only display the tooltip if not already focused or already showing
                    if (!focus && hasMessages && !jQuery(tooltipElement).IsBubblePopupOpen()) {
                        if (elementInfo.themeMargins) {
                            validationTooltipOptions.themeMargins = elementInfo.themeMargins;
                        }

                        //special case check for input within a fieldset, hide other tooltips to avoid overlap
                        if (jQuery(tooltipElement).is("select, input:text, textarea, input:file, input:password")
                                && jQuery(tooltipElement).parents("fieldset[data-type='CheckboxSet'], "
                                + "fieldset[data-type='RadioSet']").length) {
                            hideBubblePopups();
                        }
                        var show = true;

                        //special case check for if any internal inputs of a fieldset: if they are showing tooltips
                        //do not show this fieldset's tooltip to avoid overlap
                        if (elementInfo.type == "fieldset") {
                            jQuery("select, input:text, textarea, input:file, input:password", "#" + fieldId).each(function () {
                                if (jQuery(this).IsBubblePopupOpen()) {
                                    show = false;
                                }
                            });
                        }

                        if (show) {
                            var data = jQuery("#" + fieldId).data(kradVariables.VALIDATION_MESSAGES);
                            validationTooltipOptions.themeName = data.tooltipTheme;
                            validationTooltipOptions.innerHTML = jQuery("[data-messagesFor='" + fieldId + "']").html();
                            //set the margin to offset it from the left appropriately
                            validationTooltipOptions.divStyle = {margin:getTooltipMargin(tooltipElement)};
                            jQuery(tooltipElement).SetBubblePopupOptions(validationTooltipOptions, true);
                            jQuery(tooltipElement).SetBubblePopupInnerHtml(validationTooltipOptions.innerHTML, true);
                            jQuery(tooltipElement).ShowBubblePopup();
                        }
                    }
                }
            });

    jQuery(document).on("mouseleave",
            "[data-role='InputField'] input,"
                    + "[data-role='InputField'] fieldset, "
                    + "[data-role='InputField'] fieldset > span > input:radio,"
                    + "[data-role='InputField'] fieldset > span > input:checkbox,"
                    + "[data-role='InputField'] fieldset > span > label, "
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea",
            function (event) {
                var fieldId = jQuery(this).closest("[data-role='InputField']").attr("id");
                var data = jQuery("#" + fieldId).data("validationMessages");
                if (data && data.useTooltip) {
                    var elementInfo = getHoverElement(fieldId);
                    var element = elementInfo.element;
                    //first check to see if the mouse has entered part of the tooltip (in some cases it has invisible content
                    //above the field - so this is necessary) - also prevents non-displayed tooltips from hiding content
                    //when entered
                    var result = mouseInBubblePopupCheck(event, fieldId, element, this, elementInfo.type);
                    if (!result) {
                        return false;
                    }
                    //continue with the mouseleave event
                    mouseLeaveHideMessageTooltip(fieldId, this, element, elementInfo.type);
                }
            });

    //when these fields are focus store what the current errors are if any and show the messageTooltip
    jQuery(document).on("focus",
            "[data-role='InputField'] input:text, "
                    + "[data-role='InputField'] input:password, "
                    + "[data-role='InputField'] input:file, "
                    + "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio,"
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea, "
                    + "[data-role='InputField'] option",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));

                //keep track of what errors it had on initial focus
                var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);
                if (data && data.errors) {
                    data.focusedErrors = data.errors;
                }

                //show tooltip on focus
                showMessageTooltip(id, false);
            });

    //when these fields are focused out validate and if this field never had an error before, show and close, otherwise
    //immediately close the tooltip
    jQuery(document).on("focusout",
            "[data-role='InputField'] input:text, "
                    + "[data-role='InputField'] input:password, "
                    + "[data-role='InputField'] input:file, "
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));
                var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);
                var hadError = false;
                if (data && data.focusedErrors) {
                    hadError = data.focusedErrors.length;
                }
                var valid = true;

                if (validateClient) {
                    valid = validateFieldValue(this);
                }

                if (!hadError && !valid) {
                    //never had a client error before, so pop-up and delay
                    showMessageTooltip(id, true, true);
                }
                else {
                    hideMessageTooltip(id);
                }
            });

    //when these fields are changed validate immediately
    jQuery(document).on("change",
            "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio, "
                    + "[data-role='InputField'] select",
            function () {
                if (validateClient) {
                    validateFieldValue(this);
                }
            });

    //Greying out functionality
    jQuery(document).on("change",
            "[data-role='InputField'] input:text, "
                    + "[data-role='InputField'] input:password, "
                    + "[data-role='InputField'] input:file, "
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea, "
                    + "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));
                var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);
                if (data) {
                    data.fieldModified = true;
                    jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES, data);
                }
            });

    //special radio and checkbox control handling for click events
    jQuery(document).on("click",
            "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio,"
                    + "fieldset[data-type='CheckboxSet'] span > label,"
                    + "fieldset[data-type='RadioSet'] span > label",
            function () {
                var event = jQuery.Event("handleFieldsetMessages");
                event.element = this;
                //fire the handleFieldsetMessages event on every input of checkbox or radio fieldset
                jQuery("fieldset > span > input").not(this).trigger(event);
            });

    //special radio and checkbox control handling for focus events
    jQuery(document).on("focus",
            "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio",
            function () {
                var event = jQuery.Event("handleFieldsetMessages");
                event.element = this;
                //fire the handleFieldsetMessages event on every input of checkbox or radio fieldset
                jQuery("fieldset > span > input").not(this).trigger(event);
            });

    //when focused out the checkbox and radio controls that are part of a fieldset will check if another control in
    //their fieldset has received focus after a short period of time, otherwise the tooltip will close.
    //if not part of the fieldset, the closing behavior is similar to normal fields
    //in both cases, validation occurs when the field is considered to have lost focus (fieldset case - no control
    //in the fieldset has focus)
    jQuery(document).on("focusout",
            "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio",
            function () {
                var parent = jQuery(this).parent();
                var id = getAttributeId(jQuery(this).attr('id'));

                //radio/checkbox is in fieldset case
                if (parent.parent().is("fieldset")) {
                    //we only ever want this to be handled once per attachment
                    jQuery(this).one("handleFieldsetMessages", function (event) {
                        var proceed = true;
                        //if the element that invoked the event is part of THIS fieldset, we do not lose focus, so
                        //do not proceed with close handling
                        if (event.element
                                && jQuery(event.element).is(jQuery(this).closest("fieldset").find("input"))) {
                            proceed = false;
                        }

                        //the fieldset is focused out - proceed
                        if (proceed) {
                            var hadError = parent.parent().find("input").hasClass("error");
                            var valid = true;

                            if (validateClient) {
                                valid = validateFieldValue(this);
                            }

                            if (!hadError && !valid) {
                                //never had a client error before, so pop-up and delay close
                                showMessageTooltip(id, true, true);
                            }
                            else {
                                hideMessageTooltip(id);
                            }
                        }
                    });

                    var currentElement = this;

                    //if no radios/checkboxes are reporting events assume we want to proceed with closing the message
                    setTimeout(function () {
                        var event = jQuery.Event("handleFieldsetMessages");
                        event.element = [];
                        jQuery(currentElement).trigger(event);
                    }, 500);
                }
                //non-fieldset case
                else if (!jQuery(this).parent().parent().is("fieldset")) {
                    var hadError = jQuery(this).hasClass("error");
                    var valid = true;
                    //not in a fieldset - so validate directly
                    if (validateClient) {
                        valid = validateFieldValue(this);
                    }

                    if (!hadError && !valid) {
                        //never had a client error before, so pop-up and delay
                        showMessageTooltip(id, true, true);
                    }
                    else {
                        hideMessageTooltip(id);
                    }
                }
            });
}

/**
 * Calls the create call to initialize the bubblepopup plugin to take into account any content that may have
 * bubblepopups
 *
 * @param selector (optional) if specified used as the selection string to select an element to check to see if it has
 * elements that could have tooltips, if the content does not contain these elements, does not reinitialize the create
 * call
 */
function initBubblePopups(selector) {
    var runCreate = true;

    if(selector){
        var selection = jQuery(selector);
        if(selection.length){
            //if the content does not contain elements that can have a tooltip, jquery object length will be 0 (false)
            runCreate = selection.find("input:not(input[type='image']), input[data-role='help'], select, textarea, "
                        + ".uif-tooltip").not("input[type='hidden']").length;
        }
    }

    if(runCreate){
        var bubblePopupElements = jQuery("input:not(input[type='image']), input[data-role='help'], select, textarea, "
                                                + ".uif-tooltip").not("input[type='hidden']");
        bubblePopupElements.RemoveBubblePopup();

        //this can ONLY ever have ONE CALL that selects ALL elements that may have a BubblePopup
        //any other CreateBubblePopup calls besides this one (that explicitly selects any elements that may use them)
        //will cause a severe loss of functionality and buggy behavior
        //if new BubblePopups must be created due to new content on the screen this full function MUST be run again
        bubblePopupElements.CreateBubblePopup(
                {   manageMouseEvents:false,
                    themePath:"../krad/plugins/tooltip/jquerybubblepopup-theme/"
                });
    }
}

function hideBubblePopups(element) {
    if (element != undefined && element.length) {
        jQuery(element).find("input:not(input[type='image']), input[data-role='help'], select, textarea, "
                + ".uif-tooltip").not("input[type='hidden']").HideAllBubblePopups()
    }
    else {
        jQuery("input:not(input[type='image']), input[data-role='help'], select, textarea,"
                + ".uif-tooltip").not("input[type='hidden']").HideAllBubblePopups();
    }
}

/**
 * Sets up the validator and the dirty check and other page scripts
 */
function setupPage(validate) {
    jQuery('#kualiForm').dirty_form({changedClass:kradVariables.DIRTY_CLASS, includeHidden:true});

    setupImages();

    //Reset summary state before processing each field - summaries are shown if server messages
    // or on client page validation
    messageSummariesShown = false;

    //flag to turn off and on validation mechanisms on the client
    validateClient = validate;

    //select current page
    var pageId = jQuery("[name='view.currentPageId']").val();
    jQuery("ul.uif-navigationMenu").selectMenuItem({selectPage:pageId});
    jQuery("ul.uif-tabMenu").selectTab({selectPage:pageId});

    //skip input field iteration and validation message writing, if no server messages
    var hasServerMessagesData = jQuery("[data-type='Page']").data("server-messages");
    if (hasServerMessagesData) {
        //Handle messages at field, if any
        jQuery("[data-role='InputField']").each(function () {
            var id = jQuery(this).attr('id');
            handleMessagesAtField(id, true);
        });

        //Write the result of the validation messages
        writeMessagesForPage();
        messageSummariesShown = true;
    }

    //focus on pageValidation header if there are messages on this page
    if (jQuery(".uif-pageValidationHeader").length) {
        jQuery(".uif-pageValidationHeader").focus();
    }

    // make sure form doesn't have any unsaved data when user clicks on any other portal links,
    // closes browser or presses fwd/back browser button
    jQuery(window).bind('beforeunload', function (event) {
        // methodToCall check is needed to skip form posts
        var methodToCall = jQuery("[name='methodToCall']").val();
        if (!methodToCall) {
            var dirty = checkDirty(event);

            // prompt does not come through in checkDirty since we are unloaded, so we
            // need to return the question
            if (dirty) {
                return "Form has unsaved data. Do you want to leave anyway?";
            }
        }
    });

    setupValidator(jQuery('#kualiForm'));

    jQuery(".required").each(function () {
        jQuery(this).attr("aria-required", "true");
    });

    jQuery(document).trigger(kradVariables.VALIDATION_SETUP_EVENT);
    pageValidatorReady = true;

    jQuery.watermark.showAll();
}

/**
 * Sets up the validator with the necessary default settings and methods on a form
 *
 * @param form
 */
function setupValidator(form) {
    jQuery(form).validate(
            {
                onsubmit:false,
                ignore:".ignoreValid",
                wrapper:"",
                onfocusout:false,
                onclick:false,
                onkeyup:function (element) {
                    if (validateClient) {
                        var id = getAttributeId(jQuery(element).attr('id'));
                        var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);

                        //if this field previously had errors validate on key up
                        if (data && data.focusedErrors && data.focusedErrors.length) {
                            validateFieldValue(element);
                        }
                    }
                },
                highlight:function (element, errorClass, validClass) {
                    jQuery(element).addClass(errorClass).removeClass(validClass);
                    jQuery(element).attr("aria-invalid", "true");
                },
                unhighlight:function (element, errorClass, validClass) {
                    jQuery(element).removeClass(errorClass).addClass(validClass);
                    jQuery(element).removeAttr("aria-invalid");

                    var id = getAttributeId(jQuery(element).attr("id"));
                    var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);

                    if (data) {
                        data.errors = [];
                        jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES, data);

                        if (messageSummariesShown) {
                            handleMessagesAtField(id);
                        }
                        else {
                            writeMessagesAtField(id);
                        }

                        //force hide of tooltip if no messages present
                        if (!(data.warnings.length || data.info.length || data.serverErrors.length
                                || data.serverWarnings.length || data.serverInfo.length)) {
                            hideMessageTooltip(id);
                        }
                    }
                },
                errorPlacement:function (error, element) {
                },
                showErrors:function (nameErrorMap, elementObjectList) {
                    this.defaultShowErrors();

                    for (var i in elementObjectList) {
                        var element = elementObjectList[i].element;
                        var message = elementObjectList[i].message;
                        var id = getAttributeId(jQuery(element).attr('id'));

                        var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);

                        var exists = false;
                        if (data && data.errors && data.errors.length) {
                            for (var j in data.errors) {
                                if (data.errors[j] === message) {
                                    exists = true;
                                }
                            }
                        }

                        if (data && !exists) {
                            data.errors = [];
                            data.errors.push(message);
                            jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES, data);

                            if (messageSummariesShown) {
                                handleMessagesAtField(id);
                            }
                            else {
                                writeMessagesAtField(id);
                            }

                            if (!pauseTooltipDisplay) {
                                showMessageTooltip(id, false, true);
                            }
                        }
                    }

                },
                success:function (label) {
                    var htmlFor = jQuery(label).attr('for');
                    var id = "";
                    if (htmlFor.indexOf("_control") >= 0) {
                        id = getAttributeId(htmlFor);
                    }
                    else {
                        id = jQuery("[name='" + escapeName(htmlFor) + "']:first").attr("id");
                        id = getAttributeId(id);
                    }

                    var data = jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES);
                    if (data && data.errors && data.errors.length) {
                        data.errors = [];
                        jQuery("#" + id).data(kradVariables.VALIDATION_MESSAGES, data);
                        if (messageSummariesShown) {
                            handleMessagesAtField(id);
                        }
                        else {
                            writeMessagesAtField(id);
                        }
                        showMessageTooltip(id, false, true);
                    }
                }
            });
}

/**
 * Initializes all of the image variables
 */
function setupImages() {
    errorImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/error.png' alt='Error' /> ";
    errorGreyImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/error-grey.png' alt='Error - but field was modified)' /> ";
    warningImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/warning.png' alt='Warning' /> ";
    infoImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/info.png' alt='Information' /> ";
    detailsOpenImage = jQuery("<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "details_open.png' alt='Details' /> ");
    detailsCloseImage = jQuery("<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "details_close.png' alt='Close Details' /> ");
}

/**
 * Retrieves the value for a configuration parameter
 *
 * @param paramName - name of the parameter to retrieve
 */
function getConfigParam(paramName) {
    var configParams = jQuery(document).data("ConfigParameters");
    if (configParams) {
        return configParams[paramName];
    }
    return "";
}

jQuery.validator.addMethod("minExclusive", function (value, element, param) {
    if (param.length == 1 || param[1]()) {
        return this.optional(element) || value > param[0];
    }
    else {
        return true;
    }
});
jQuery.validator.addMethod("maxInclusive", function (value, element, param) {
    if (param.length == 1 || param[1]()) {
        return this.optional(element) || value <= param[0];
    }
    else {
        return true;
    }
});
jQuery.validator.addMethod("minLengthConditional", function (value, element, param) {
    if (param.length == 1 || param[1]()) {
        return this.optional(element) || this.getLength(jQuery.trim(value), element) >= param[0];
    }
    else {
        return true;
    }
});
jQuery.validator.addMethod("maxLengthConditional", function (value, element, param) {
    if (param.length == 1 || param[1]()) {
        return this.optional(element) || this.getLength(jQuery.trim(value), element) <= param[0];
    }
    else {
        return true;
    }
});

/**
 * a plugin function for sorting values for columns marked with sType:kuali_date in aoColumns in ascending order
 *
 * <p>The values to be compared are returned by custom function for returning cell data if it exists, otherwise
 * the cell contents (innerHtml) are converted to string and compared against each other. One such function is defined
 * below - jQuery.fn.dataTableExt.afnSortData['dom-text'] - which returns values for the 'dom-text' custom sorting plugin<p>
 *
 * @param a - the first value to use in comparison
 * @param b - the second value to use in comparison
 * @return a number that will be used to determine whether a is greater than b
 */
jQuery.fn.dataTableExt.oSort['kuali_date-asc'] = function (a, b) {
    var date1 = a.split('/');
    var date2 = b.split('/');
    var x = (date1[2] + date1[0] + date1[1]) * 1;
    var y = (date2[2] + date2[0] + date2[1]) * 1;
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
};

/**
 * a plugin function for sorting values for columns marked with sType:kuali_date in aoColumns in descending order
 *
 * <p>The values to be compared are returned by custom function for returning cell data if it exists, otherwise
 * the cell contents (innerHtml) are converted to string and compared against each other. One such function is defined
 * below - jQuery.fn.dataTableExt.afnSortData['dom-text'] - which returns values for the 'dom-text' custom sorting plugin<p>
 *
 * @param a - the first value to use in comparison
 * @param b - the second value to use in comparison
 * @return a number that will be used to determine whether a is greater than b
 */
jQuery.fn.dataTableExt.oSort['kuali_date-desc'] = function (a, b) {
    var date1 = a.split('/');
    var date2 = b.split('/');
    var x = (date1[2] + date1[0] + date1[1]) * 1;
    var y = (date2[2] + date2[0] + date2[1]) * 1;
    return ((x < y) ? 1 : ((x > y) ? -1 : 0));
};

/**
 * a plugin function for sorting values for columns marked with sType:kuali_percent in aoColumns in ascending order
 *
 * <p>The values to be compared are returned by custom function for returning cell data if it exists, otherwise
 * the cell contents (innerHtml) are converted to string and compared against each other. One such function is defined
 * below - jQuery.fn.dataTableExt.afnSortData['dom-text'] - which returns values for the 'dom-text' custom sorting plugin<p>
 *
 * @param a - the first value to use in comparison
 * @param b - the second value to use in comparison
 * @return a number that will be used to determine whether a is greater than b
 */
jQuery.fn.dataTableExt.oSort['kuali_percent-asc'] = function (a, b) {
    var num1 = a.replace(/[^0-9]/g, '');
    var num2 = b.replace(/[^0-9]/g, '');
    num1 = (num1 == "-" || num1 === "" || isNaN(num1)) ? 0 : num1 * 1;
    num2 = (num2 == "-" || num2 === "" || isNaN(num2)) ? 0 : num2 * 1;
    return num1 - num2;
};

/**
 * a plugin function for sorting values for columns marked with sType:kuali_percent in aoColumns in descending order
 *
 * <p>The values to be compared are returned by custom function for returning cell data if it exists, otherwise
 * the cell contents (innerHtml) are converted to string and compared against each other. One such function is defined
 * below - jQuery.fn.dataTableExt.afnSortData['dom-text'] - which returns values for the 'dom-text' custom sorting plugin<p>
 *
 * @param a - the first value to use in comparison
 * @param b - the second value to use in comparison
 * @return a number that will be used to determine whether a is greater than b
 */
jQuery.fn.dataTableExt.oSort['kuali_percent-desc'] = function (a, b) {
    var num1 = a.replace(/[^0-9]/g, '');
    var num2 = b.replace(/[^0-9]/g, '');
    num1 = (num1 == "-" || num1 === "" || isNaN(num1)) ? 0 : num1 * 1;
    num2 = (num2 == "-" || num2 === "" || isNaN(num2)) ? 0 : num2 * 1;
    return num2 - num1;
};

/**
 * a plugin function for sorting values for columns marked with sType:kuali_currency in aoColumns in ascending order
 *
 * <p>The values to be compared are returned by custom function for returning cell data if it exists, otherwise
 * the cell contents (innerHtml) are converted to string and compared against each other. One such function is defined
 * below - jQuery.fn.dataTableExt.afnSortData['dom-text'] - which returns values for the 'dom-text' custom sorting plugin<p>
 *
 * @param a - the first value to use in comparison
 * @param b - the second value to use in comparison
 * @return a number that will be used to determine whether a is greater than b
 */
jQuery.fn.dataTableExt.oSort['kuali_currency-asc'] = function (a, b) {
    /* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
    var x = a == "-" ? 0 : a.replace(/,/g, "");
    var y = b == "-" ? 0 : b.replace(/,/g, "");
    /* Remove the currency sign */
    x = x.substring(1);
    y = y.substring(1);
    /* Parse and return */
    x = parseFloat(x);
    y = parseFloat(y);

    x = isNaN(x) ? 0 : x * 1;
    y = isNaN(y) ? 0 : y * 1;

    return x - y;
};

/**
 * a plugin function for sorting values for columns marked with sType:kuali_currency in aoColumns in descending order
 *
 * <p>The values to be compared are returned by custom function for returning cell data if it exists, otherwise
 * the cell contents (innerHtml) are converted to string and compared against each other. One such function is defined
 * below - jQuery.fn.dataTableExt.afnSortData['dom-text'] - which returns values for the 'dom-text' custom sorting plugin<p>
 *
 * @param a - the first value to use in comparison
 * @param b - the second value to use in comparison
 * @return a number that will be used to determine whether a is greater than b
 */
jQuery.fn.dataTableExt.oSort['kuali_currency-desc'] = function (a, b) {
    /* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
    var x = a == "-" ? 0 : a.replace(/,/g, "");
    var y = b == "-" ? 0 : b.replace(/,/g, "");
    /* Remove the currency sign */
    x = x.substring(1);
    y = y.substring(1);
    /* Parse and return */
    x = parseFloat(x);
    y = parseFloat(y);

    x = isNaN(x) ? 0 : x;
    y = isNaN(y) ? 0 : y;

    return y - x;
};

/**
 * retrieve column values for sorting a column marked with sSortDataType:dom-text in aoColumns
 *
 * @param oSettings - an object provided by datatables containing table information and configuration
 * @param iColumn - the column whose values are to be retrieved
 * @return an array of column values - extracted from any surrounding markup
 */
jQuery.fn.dataTableExt.afnSortData['dom-text'] = function (oSettings, iColumn) {
    var aData = [];
    jQuery('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var input = jQuery(this).find('input:text');
        if (input.length != 0) {
            aData.push(input.val());
        } else {
            // find span for the data or input field and get its text
            var input1 = jQuery(this).find('.uif-field');
            if (input1.length != 0) {
                aData.push(jQuery.trim(input1.find("span:first").text()));
            } else {
                // just use the text within the cell
                aData.push(jQuery(this).text());
            }
        }

    });

    return aData;
}

/**
 * retrieve column values for sorting a column marked with sSortDataType:dom-select in aoColumns
 *
 * <p>Create an array with the values of all the select options in a column</p>
 *
 * @param oSettings - an object provided by datatables containing table information and configuration
 * @param iColumn - the column whose values are to be retrieved
 * @return an array of column values - extracted from any surrounding markup
 */
jQuery.fn.dataTableExt.afnSortData['dom-select'] = function (oSettings, iColumn) {
    var aData = [];
    jQuery('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var selected = jQuery(this).find('select option:selected:first');
        if (selected.length != 0) {
            aData.push(selected.text());
        } else {
            var input1 = jQuery(this).find('.uif-inputField');
            if (input1.length != 0) {
                aData.push(jQuery.trim(input1.text()));
            } else {
                aData.push("");
            }
        }

    });

    return aData;
}

/**
 * retrieve column values for sorting a column marked with sSortDataType:dom-checkbox in aoColumns
 *
 * <p>Create an array with the values of all the checkboxes in a column</p>
 *
 * @param oSettings - an object provided by datatables containing table information and configuration
 * @param iColumn - the column whose values are to be retrieved
 * @return an array of column values - extracted from any surrounding markup
 */
jQuery.fn.dataTableExt.afnSortData['dom-checkbox'] = function (oSettings, iColumn) {
    var aData = [];
    jQuery('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var checkboxes = jQuery(this).find('input:checkbox');
        if (checkboxes.length != 0) {
            var str = "";
            for (i = 0; i < checkboxes.length; i++) {
                var check = checkboxes[i];
                if (check.checked == true && check.value.length > 0) {
                    str += check.value + " ";
                }
            }
            aData.push(str);
        } else {
            var input1 = jQuery(this).find('.uif-inputField');
            if (input1.length != 0) {
                aData.push(jQuery.trim(input1.text()));
            } else {
                aData.push("");
            }
        }

    });

    return aData;
}

/**
 * retrieve column values for sorting a column marked with sSortDataType:dom-radio in aoColumns
 *
 * <p>Create an array with the values of all the radio buttons in a column</p>
 *
 * @param oSettings - an object provided by datatables containing table information and configuration
 * @param iColumn - the column whose values are to be retrieved
 * @return an array of column values - extracted from any surrounding markup
 */
jQuery.fn.dataTableExt.afnSortData['dom-radio'] = function (oSettings, iColumn) {
    var aData = [];
    jQuery('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var radioButtons = jQuery(this).find('input:radio');
        if (radioButtons.length != 0) {
            var value = "";
            for (i = 0; i < radioButtons.length; i++) {
                var radio = radioButtons[i];
                if (radio.checked == true) {
                    value = radio.value;
                    break;
                }
            }
            aData.push(value);
        } else {
            var input1 = jQuery(this).find('.uif-inputField');
            if (input1.length != 0) {
                aData.push(jQuery.trim(input1.text()));
            } else {
                aData.push("");
            }
        }

    });

    return aData;
}

// setup window javascript error handler
window.onerror = errorHandler;

function errorHandler(msg, url, lno) {
    jQuery("#" + kradVariables.APP_ID).show();
    jQuery("#" + kradVariables.PAGE_CONTENT_HEADER_CLASS).show();
    var context = getContext();
    context.unblockUI();
    showGrowl(msg + '<br/>' + url + '<br/>' + lno, 'Javascript Error', 'errorGrowl');
    return false;
}

// script that should execute when the page unloads
jQuery(window).bind('beforeunload', function (evt) {
    // clear server form if closing the browser tab/window or going back
    // TODO: work out back button problem so we can add this clearing
//    if (!event.pageY || (event.pageY < 0)) {
//        clearServerSideForm();
//    }
});



