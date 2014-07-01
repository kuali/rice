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
// global vars
var jq = jQuery.noConflict();

// clear out blockUI css, using css class overrides
jQuery.blockUI.defaults.css = {};
jQuery.blockUI.defaults.overlayCSS = {};

// script cleanup flag
var scriptCleanup;

//stickyContent globals
var stickyContent;
var stickyContentOffset;
var currentHeaderHeight = 0;
var currentFooterHeight = 0;
var stickyFooterContent;
var applicationFooter;

// validation init
var pageValidatorReady = false;
var validateClient = true;
var messageSummariesShown = false;
var pauseTooltipDisplay = false;
var haltValidationMessaging = false;
var pageValidationPhase = false;
var gAutoFocus = false;
var clientErrorStorage = new Object();
var summaryTextExistence = new Object();
var clientErrorExistsCheck = false;
var skipPageSetup = false;
var groupValidationDefaults;
var fieldValidationDefaults;

// Action option defaults
var actionDefaults;

// dirty form state management
var dirtyFormState;

// view state
var initialViewLoad = false;

var originalPageTitle;
var errorImage;
var errorGreyImage;
var warningImage;
var infoImage;
var detailsOpenImage;
var detailsCloseImage;
var refreshImage;
var navigationImage;
var ajaxReturnHandlers = {};

var activeDialogId;
var sessionWarningTimer;
var sessionTimeoutTimer;

// delay function
var delay = (function () {
    var timer = 0;
    return function (callback, ms) {
        clearTimeout(timer);
        timer = setTimeout(callback, ms);
    };
})();

// map of componentIds and refreshTimers
var refreshTimerComponentMap = {};

// setup handler for opening form content popups with errors
jQuery(document).on(kradVariables.PAGE_LOAD_EVENT, function (event) {
    openPopoverContentsWithErrors();
});

// common event registering done here through JQuery ready event
jQuery(document).ready(function () {
    time(true, "viewSetup-phase-1");

    // mark initial view load
    initialViewLoad = true;

    // determine whether we need to refresh or update the page
    skipPageSetup = handlePageAndCacheRefreshing();
    dirtyFormState = new DirtyFormState();

    // script cleanup setting
    scriptCleanup = getConfigParam("scriptCleanup").toLowerCase() === "true";

    // buttons
    jQuery("input:submit, input:button, a.button, .uif-dialogButtons").button();
    jQuery(".uif-dialogButtons").next('label').addClass('uif-primaryDialogButton');

    // common ajax setup
    jQuery.ajaxSetup({
        error: function (jqXHR, textStatus, errorThrown) {
            showGrowl(getMessage(kradVariables.MESSAGE_STATUS_ERROR, null, null, textStatus, errorThrown), getMessage(kradVariables.MESSAGE_SERVER_RESPONSE_ERROR), 'errorGrowl');
        },
        complete: function (jqXHR, textStatus) {
            resetSessionTimers();
        },
        statusCode: {403: function (jqXHR, textStatus) {
            handleAjaxSessionTimeout(jqXHR.responseText);
        }}
    });

    // stop previous loading message
    hideLoading();

    // hide the ajax progress display screen if the page is replaced e.g. by a login page when the session expires
    jQuery(window).unload(function () {
        hideLoading();
    });

    time(false, "viewSetup-phase-1");

    // show the page
    jQuery("#" + kradVariables.APP_ID).show();

    // run all the scripts
    runHiddenScripts("");

    time(true, "viewSetup-phase-2");

    // setup dirty field processing
    dirtyFormState.dirtyHandlerSetup();

    // disclosure handler setup
    setupDisclosureHandler();

    setupHelperTextHandler();

    // setup document level handling of drag and drop for files
    setupFileDragHandlers();

    // setup the various event handlers for fields - THIS IS IMPORTANT
    initFieldHandlers();

    jQuery(window).unbind("resize.tooltip");
    jQuery(window).bind("resize.tooltip", function(){
        var visibleTooltips = jQuery(".popover:visible");
        visibleTooltips.each(function(){
            // bug with popover plugin does not reposition tooltip on window resize, forcing it here
            jQuery(this).prev("[data-hasTooltip]").popover("show");
        });
    });

    // setup the handler for inline field editing
    initInlineEditFields();

    // setup the handler for enter key event actions
    initEnterKeyHandler();

    // setup any potential sticky/fixed content
    setupStickyHeaderAndFooter();

    hideEmptyCells();

    jQuery(document).on(kradVariables.PAGE_LOAD_EVENT, function () {
        initialViewLoad = false;
    });

    time(false, "viewSetup-phase-2");
});

function initInlineEditFields(){
    jQuery(document).on("click", kradVariables.INLINE_EDIT.VIEW_CLASS, function (event) {
        var $view = jQuery(this);
        showInlineEdit($view);
    });

    jQuery(document).on("keyup", kradVariables.INLINE_EDIT.VIEW_CLASS, function (event) {
        // grab the keycode based on browser
        var keycode = (event.keyCode ? event.keyCode : event.which);

        // Only continue for enter
        if (keycode !== 13) {
            return;
        }

        var $view = jQuery(this);
        showInlineEdit($view);
    });

}

function showInlineEdit($view) {
    var viewId = $view.attr("id");
    var editId = viewId.replace(kradVariables.INLINE_EDIT.VIEW_SUFFIX, kradVariables.INLINE_EDIT.EDIT_SUFFIX);
    var $edit = jQuery("#" + editId);
    var $control = $edit.find("[data-role='Control']");
    var keycodes = { 16: false, 13: false, 27: false };

    if($edit.length){
        $edit.data("origVal", $control.val());
    }

    if ($edit.is(":visible")) {
        $edit.focus();
        return;
    }

    $view.hide();

    if ($view.data("ajax_edit") === true && $edit.length === 0) {
        var fieldId = viewId.replace(kradVariables.INLINE_EDIT.INLINE_EDIT_VIEW, "");
        retrieveComponent(fieldId, "refresh", function (){
            var $newView = jQuery("#" + viewId);
            showInlineEdit($newView);
        });
        // Return because we are waiting for ajax component retrieval
        return;
    }

    $edit.show();

    $control.removeAttr("readonly");
    $control.focus();

    $control.on("keydown.inlineEdit", function (event) {
        var keycode = (event.keyCode ? event.keyCode : event.which);

        if (event.keyCode in keycodes) {
            keycodes[event.keyCode] = true;

            // check for shift-enter
            if (keycodes[16] && keycodes[13] && $control.is("textarea")) {
                //alert("shift + enter");
                keycodes[16] = false;
                keycodes[13] = false;
                event.stopPropagation();
                return;
            }

            // check for escape key
            if (keycodes[27] ) {
                $edit.hide();
                $control.val($edit.data("origVal"));
                $view.show();
                $view.focus();
                return;
            }
        }

        // check for enter key
        if (keycode !== 13) {
           return;
        }

        var valid = true;

        if (validateClient) {
            var fieldId = getAttributeId(jQuery(this).attr('id'));
            var data = getValidationData(jQuery("#" + fieldId));
            data.useTooltip = false;

            valid = validateFieldValue(this);
        }

        if (valid) {
            retrieveComponent(fieldId, "saveField", function (){
                var $newView = jQuery("#" + viewId);
                $newView.focus();
            });

            $control.unbind("keydown.inlineEdit");
        }

        return false;
    }).on("keyup.inlineEdit", function (event) {
        event.preventDefault();
        event.stopPropagation();

        if (event.keyCode in keycodes) {
            keycodes[event.keyCode] = false;
        }

        $control.unbind("keyup.inlineEdit");
    });
}

/**
 * Sets up and initializes the handlers for enter key actions.
 *
 * <p>This function determines which button/action should fire when the enter key is pressed while focus is on a configured input</p>
 *
 */
function initEnterKeyHandler(){
    jQuery(document).on("keyup", "[data-enter_key]", function(event) {
        // grab the keycode based on browser
        var keycode = (event.keyCode ? event.keyCode : event.which);

        // check for enter key
        if(keycode !== 13) { return; }
            event.preventDefault();
            event.stopPropagation();

            // using event bubbling, we search for inner most element with data attribute kradVariables.ENTER_KEY_SUFFIX and assign it's value as an ID
            var enterKeyId = jQuery(event.currentTarget).data(kradVariables.ENTER_KEY_SUFFIX);

            // make sure the targeted action is a permitted element
            if(jQuery(event.target).is(":not(a, button, submit, img[data-role='" + kradVariables.DATA_ROLES.ACTION +  "'], input[data-role='" + kradVariables.DATA_ROLES.ACTION +  "'] )")){
                // check to see if primary enter key action button is targeted
                if(enterKeyId === kradVariables.ENTER_KEY_DEFAULT){
                    // find all primary action buttons on page with attribute data-default_enter_key_action='true'
                    var primaryButtons = jQuery(event.currentTarget).find("[data-default_enter_key_action='true']");

                    // filter the buttons only one parent section deep
                    var primaryButton = primaryButtons.filter(function() {
                        return jQuery(this).parents('[data-enter_key]').length < 2;
                    });

                    // if the button exists get it's id
                    if (primaryButton.length) {
                        enterKeyId = primaryButton.attr("id");
                    }
                }

                // if enterKeyAction is still set to  ENTER_KEY_PRIMARY value, do nothing, button doesn't exist
                if(enterKeyId === kradVariables.ENTER_KEY_DEFAULT){
                     return false;
                }

                // make sure action button is visible and not disabled before we fire it
                if(jQuery('#' + enterKeyId).is(":visible") && jQuery('#' + enterKeyId).is(":disabled") === false){
                    jQuery(document).find('#' + enterKeyId).click();
                }
        }
    });

    // a hack to capture the native browser enter key behavior..  keydown and keyup
    jQuery(document).on("keydown", "[data-enter_key], [data-inline_edit] [data-role='Control']", function(event){
        // grab the keycode based on browser
        var keycode = (event.keyCode ? event.keyCode : event.which);

        // check for enter key
        if (keycode === 13) {
            event.preventDefault();
            return false;
        }
    });
}

/**
 * Sets up and initializes the handlers for sticky header and footer content
 */
function setupStickyHeaderAndFooter() {

    // sticky(header) content variables must be initialized here to retain sticky location across page request
    stickyContent = jQuery("[data-sticky='true']:visible");
    if (stickyContent.length) {
        stickyContent.each(function () {
            jQuery(this).data("offset", jQuery(this).offset());
        });

        stickyContentOffset = stickyContent.offset();

        initStickyContent();
    }

    // find and initialize stickyFooters
    stickyFooterContent = jQuery("[data-sticky_footer='true']:visible");
    applicationFooter = jQuery("#" + kradVariables.APPLICATION_FOOTER_WRAPPER);

    initStickyFooterContent();

    // bind scroll and resize events to dynamically update sticky content positions
    jQuery(window).unbind("scroll.sticky");
    jQuery(window).bind("scroll.sticky", function () {
        handleStickyContent();
        handleStickyFooterContent();
    });

    jQuery(window).unbind("resize.sticky");
    jQuery(window).bind("resize.sticky", function () {
        handleStickyContent();
        handleStickyFooterContent();
    });
}

/**
 * Sets up the various handlers for various field controls.
 * This function includes handlers that are critical to the behavior of KRAD validation and message frameworks
 * on the client
 */
function initFieldHandlers() {
    time(true, "field-handlers");

    // add global action handler
    jQuery(document).on("click", "a[data-role='Action'], button[data-role='Action'], "
            + "img[data-role='Action'], input[data-role='Action']",
            function (e) {
                e.preventDefault();
                var action = jQuery(this);

                // Disabled check
                if(action.hasClass(kradVariables.DISABLED_CLASS)){
                    return false;
                }

                initActionData(action);

                // Dirty check (if enabled)
                if (action.data(kradVariables.PERFORM_DIRTY_VALIDATION) ===  true && dirtyFormState.checkDirty(e)) {
                    return;
                }

                var functionData = action.data(kradVariables.ACTION_ONCLICK_DATA);
                eval("var actionFunction = function(e) {" + functionData + "};");

                return actionFunction.call(this, e);
            });

    // add a focus handler for scroll manipulation when there is a sticky header or footer, so content stays in view
    jQuery("[data-role='Page']").on("focus", "a[href], area[href], input:not([disabled]), "
            + "select:not([disabled]), textarea:not([disabled]), button:not([disabled]), "
            + "iframe, object, embed, *[tabindex], *[contenteditable]",
            function () {
                var element = jQuery(this);
                var buffer = 10;
                var elementHeight = element.outerHeight();
                if (!elementHeight) {
                    elementHeight = 24;
                }

                // if something is focused under the footer, adjust the scroll
                if (stickyFooterContent && stickyFooterContent.length) {
                    var footerOffset = stickyFooterContent.offset().top;
                    if (element.offset().top + elementHeight > footerOffset) {
                        var visibleContentSize = jQuery(window).height() - currentHeaderHeight - currentFooterHeight;
                        jQuery(document).scrollTo(element.offset().top + elementHeight + buffer
                                - currentHeaderHeight - visibleContentSize);
                        return true;
                    }
                }

                // if something is focused under the header content, adjust the scroll
                if (stickyContent && stickyContent.length) {
                    var reversedStickyContent = jQuery(stickyContent.get().reverse());
                    var headerOffset = reversedStickyContent.offset().top + reversedStickyContent.outerHeight();
                    if (element.offset().top < headerOffset) {
                        jQuery(document).scrollTo(element.offset().top - currentHeaderHeight - buffer);
                        return true;
                    }
                }

                return true;
            });

    jQuery(document).on("mouseenter",
            "div[data-role='InputField'] input:not([type='image']),"
                    + "div[data-role='InputField'] fieldset, "
                    + "div[data-role='InputField'] fieldset > span > input:radio,"
                    + "div[data-role='InputField'] fieldset > span > input:checkbox,"
                    + "div[data-role='InputField'] fieldset > span > label, "
                    + "div[data-role='InputField'] select, "
                    + "div[data-role='InputField'] textarea",
            function (event) {
                var fieldId = jQuery(this).closest("div[data-role='InputField']").attr("id");
                var data = getValidationData(jQuery("#" + fieldId));
                if (data && data.useTooltip) {
                    var elementInfo = getHoverElement(fieldId);
                    var element = elementInfo.element;
                    var tooltipElement = this;
                    var focus = jQuery(tooltipElement).is(":focus");
                    if (elementInfo.type == "fieldset") {
                        // for checkbox/radio fieldsets we put the tooltip on the label of the first input
                        tooltipElement = jQuery(element).filter(".uif-tooltip");
                        // if the fieldset or one of the inputs have focus then the fieldset is considered focused
                        focus = jQuery(element).filter("fieldset").is(":focus")
                                || jQuery(element).filter("input").is(":focus");
                    }

                    var hasMessages = jQuery("[data-messages_for='" + fieldId + "']").children().length;

                    // only display the tooltip if not already focused or already showing
                    if (!focus && hasMessages) {
                        showMessageTooltip(fieldId);
                    }
                }
            });

    jQuery(document).on("mouseleave",
            "div[data-role='InputField'] input,"
                    + "div[data-role='InputField'] fieldset, "
                    + "div[data-role='InputField'] fieldset > span > input:radio,"
                    + "div[data-role='InputField'] fieldset > span > input:checkbox,"
                    + "div[data-role='InputField'] fieldset > span > label, "
                    + "div[data-role='InputField'] select, "
                    + "div[data-role='InputField'] textarea",
            function (event) {
                var fieldId = jQuery(this).closest("div[data-role='InputField']").attr("id");
                var data = getValidationData(jQuery("#" + fieldId));
                if (data && data.useTooltip) {
                    var elementInfo = getHoverElement(fieldId);
                    var element = elementInfo.element;
                    var tooltipElement = this;
                    var focus = jQuery(tooltipElement).is(":focus");
                    if (elementInfo.type == "fieldset") {
                        // for checkbox/radio fieldsets we put the tooltip on the label of the first input
                        tooltipElement = jQuery(element).filter(".uif-tooltip");
                        // if the fieldset or one of the inputs have focus then the fieldset is considered focused
                        focus = jQuery(element).filter("fieldset").is(":focus")
                                || jQuery(element).filter("input").is(":focus");
                    }

                    if (!focus) {
                        hideMessageTooltip(fieldId);
                    }

                }
            });

    // when these fields are focus store what the current errors are if any and show the messageTooltip
    jQuery(document).on("focus",
            "div[data-role='InputField'] input:text, "
                    + "div[data-role='InputField'] input:password, "
                    + "div[data-role='InputField'] input:file, "
                    + "div[data-role='InputField'] input:checkbox, "
                    + "div[data-role='InputField'] input:radio,"
                    + "div[data-role='InputField'] select, "
                    + "div[data-role='InputField'] textarea, "
                    + "div[data-role='InputField'] option",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));
                if(!id){ return; }
                // keep track of what errors it had on initial focus
                var data = getValidationData(jQuery("#" + id));
                if (data && data.errors) {
                    data.focusedErrors = data.errors;
                }

                //show tooltip on focus
                showMessageTooltip(id, false);
            });

    // when these fields are focused out validate and if this field never had an error before, show and close, otherwise
    // immediately close the tooltip
    jQuery(document).on("focusout",
            "div[data-role='InputField'] input:text, "
                    + "div[data-role='InputField'] input:password, "
                    + "div[data-role='InputField'] input:file, "
                    + "div[data-role='InputField'] select, "
                    + "div[data-role='InputField'] textarea",
            function (event) {
                var id = getAttributeId(jQuery(this).attr('id'));
                if(!id || isRelatedTarget(this.parentElement) === true){ return; }
                var data = getValidationData(jQuery("#" + id));
                var hadError = false;
                if (data && data.focusedErrors) {
                    hadError = data.focusedErrors.length;
                }
                var valid = true;

                if (validateClient) {
                    valid = validateFieldValue(this);
                }

                // mouse in tooltip check
                var mouseInTooltip = false;
                if (data && data.useTooltip && data.mouseInTooltip) {
                    mouseInTooltip = data.mouseInTooltip;
                }

                if (!hadError && !valid) {
                    // never had a client error before, so pop-up and delay
                    showMessageTooltip(id, true, true);
                }
                else if (!mouseInTooltip) {
                    hideMessageTooltip(id);
                }
            });

    // when these fields are changed validate immediately
    jQuery(document).on("change",
            "div[data-role='InputField'] input:checkbox, "
                    + "div[data-role='InputField'] input:radio, "
                    + "div[data-role='InputField'] select",
            function () {
                if (validateClient) {
                    validateFieldValue(this);
                }
            });

    // Greying out functionality
    jQuery(document).on("change",
            "div[data-role='InputField'] input:text, "
                    + "div[data-role='InputField'] input:password, "
                    + "div[data-role='InputField'] input:file, "
                    + "div[data-role='InputField'] select, "
                    + "div[data-role='InputField'] textarea, "
                    + "div[data-role='InputField'] input:checkbox, "
                    + "div[data-role='InputField'] input:radio",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));
                if(!id){ return; }
                var field = jQuery("#" + id);

                var data = getValidationData(field);
                if (data) {
                    data.fieldModified = true;
                    field.data(kradVariables.VALIDATION_MESSAGES, data);
                }
            });

    // special radio and checkbox control handling for click events
    jQuery(document).on("click",
            "div[data-role='InputField'] input:checkbox, "
                    + "div[data-role='InputField'] input:radio,"
                    + "fieldset[data-type='CheckboxSet'] span > label,"
                    + "fieldset[data-type='RadioSet'] span > label",
            function () {
                var event = jQuery.Event("handleFieldsetMessages");
                event.element = this;
                //fire the handleFieldsetMessages event on every input of checkbox or radio fieldset
                jQuery("fieldset > span > input").not(this).trigger(event);
            });

    // special radio and checkbox control handling for focus events
    jQuery(document).on("focus",
            "div[data-role='InputField'] input:checkbox, "
                    + "div[data-role='InputField'] input:radio",
            function () {
                var event = jQuery.Event("handleFieldsetMessages");
                event.element = this;
                //fire the handleFieldsetMessages event on every input of checkbox or radio fieldset
                jQuery("fieldset > span > input").not(this).trigger(event);
            });

    // when focused out the checkbox and radio controls that are part of a fieldset will check if another control in
    // their fieldset has received focus after a short period of time, otherwise the tooltip will close.
    // if not part of the fieldset, the closing behavior is similar to normal fields
    // in both cases, validation occurs when the field is considered to have lost focus (fieldset case - no control
    // in the fieldset has focus)
    jQuery(document).on("focusout",
            "div[data-role='InputField'] input:checkbox, "
                    + "div[data-role='InputField'] input:radio",
            function () {
                var parent = jQuery(this).parent();
                var id = getAttributeId(jQuery(this).attr('id'));
                if(!id){ return; }
                var data = getValidationData(jQuery("#" + id));
                //mouse in tooltip check
                var mouseInTooltip = false;
                if (data && data.useTooltip && data.mouseInTooltip) {
                    mouseInTooltip = data.mouseInTooltip;
                }

                //radio/checkbox is in fieldset case
                if (parent.parent().is("fieldset")) {
                    // we only ever want this to be handled once per attachment
                    jQuery(this).one("handleFieldsetMessages", function (event) {
                        var proceed = true;
                        // if the element that invoked the event is part of THIS fieldset, we do not lose focus, so
                        // do not proceed with close handling
                        if (event.element
                                && jQuery(event.element).is(jQuery(this).closest("fieldset").find("input"))) {
                            proceed = false;
                        }

                        // the fieldset is focused out - proceed
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
                            else if (!mouseInTooltip) {
                                hideMessageTooltip(id);
                            }
                        }
                    });

                    var currentElement = this;

                    // if no radios/checkboxes are reporting events assume we want to proceed with closing the message
                    setTimeout(function () {
                        var event = jQuery.Event("handleFieldsetMessages");
                        event.element = [];
                        jQuery(currentElement).trigger(event);
                    }, 500);
                }
                // non-fieldset case
                else if (!jQuery(this).parent().parent().is("fieldset")) {
                    var hadError = jQuery(this).hasClass("error");
                    var valid = true;
                    // not in a fieldset - so validate directly
                    if (validateClient) {
                        valid = validateFieldValue(this);
                    }

                    if (!hadError && !valid) {
                        // never had a client error before, so pop-up and delay
                        showMessageTooltip(id, true, true);
                    }
                    else if (!mouseInTooltip) {
                        hideMessageTooltip(id);
                    }
                }
            });

    jQuery(document).on("change", "table.dataTable div[data-role='InputField'][data-total='change'] :input", function () {
        refreshDatatableCellRedraw(this);
    });

    jQuery(document).on("input", "table.dataTable div[data-role='InputField'][data-total='keyup'] :input", function () {
        var input = this;
        delay(function () {
            refreshDatatableCellRedraw(input)
        }, 300);
    });

    // capture tabbing through widget elements to make sure we only validate the control field when we leave the entire
    // widget
    var buttonHovered = false;

    // capture mousing over button of the widget if there is one
    jQuery(document).on("mouseover", "div[data-role='InputField'] div.input-group div.input-group-btn a", function(){
        buttonHovered = true;

    // capture mousing out of button in the widget
    }).on("mouseout", "div[data-role='InputField'] div.input-group div.input-group-btn a", function(){
        buttonHovered = false;

    // capture leaving the control field
    }).on("focusout", "div[data-role='InputField'] div.input-group", function () {
        currentControl = this;

        // determine whether we are still in the widget. If we are out of the widget, then validate
        if(isRelatedTarget(this) !== true && buttonHovered === false){
            validateFieldValue(jQuery(this).children("[data-role='Control']"));
        }
    });

    // capture datepicker widget button
    jQuery(document).on("mouseover", ".ui-datepicker", function(){
        buttonHovered = true;
    }).on("mouseout", ".ui-datepicker", function(){
        buttonHovered = false;
    });

    time(false, "field-handlers");
}

/**
 * Test if an input field is part of a widget by examining event.currentTarget and event.target
 *
 */
function isRelatedTarget(element){
    if(event === undefined) return true;

    try {

        // test for lightbox widget by matching a fancy-box event property
        for (var key in event.currentTarget){
            if( key.match(/fancy/g) && key !== undefined) {
                console.log(key);
                return true;
            }
        }

        // here we check to see if the element we are focusing out of is nested in a input-group div or within
        // input-group-btn div. If so then they are related to the widget
        if(("relatedTarget" in event && event.relatedTarget !== null
                && element === event.relatedTarget.parentElement.parentElement)
                || ("relatedTarget" in event && event.relatedTarget !== null
                        && element === event.relatedTarget.parentElement)
                ) {
            return true;
        }

        return false;

    }catch(e){
        return false;
    }
}

/**
 * Setup a global disclosure handler which will handle click events on disclosure links to toggle them open and closed
 */
function setupDisclosureHandler() {
    jQuery(document).on("click",
            "a[data-role='" + kradVariables.DATA_ROLES.DISCLOSURE_LINK + "']", function (event) {
                event.preventDefault();

                var link = jQuery(this);

                var disclosureContent = jQuery("#" + link.data("linkfor"));

                var isOpen = disclosureContent.attr(kradVariables.ATTRIBUTES.DATA_OPEN);
                var animationSpeed = link.data("speed");
                var linkId = link.attr("id");
                var widgetId = link.data("widgetid");
                var ajax = link.data("ajax");

                if (isOpen == "true") {
                    disclosureContent.attr(kradVariables.ATTRIBUTES.DATA_OPEN, false);

                    var options = {
                        duration: animationSpeed,
                        step: function(){
                            disclosureContent.trigger(kradVariables.EVENTS.ADJUST_STICKY);
                        }
                    };

                    disclosureContent.slideUp(options);

                    link.find("#" + linkId + "_exp").hide();
                    link.find("#" + linkId + "_col").show();

                    setComponentState(widgetId, 'open', false);
                    disclosureContent.trigger(kradVariables.EVENTS.ADJUST_STICKY);
                }
                else {
                    disclosureContent.attr(kradVariables.ATTRIBUTES.DATA_OPEN, true);

                    // run scripts for previously hidden content
                    runHiddenScripts(disclosureContent, true, false);

                    link.find("#" + linkId + "_exp").show();
                    link.find("#" + linkId + "_col").hide();

                    setComponentState(widgetId, 'open', true);

                    if (ajax && disclosureContent.data("role") == "placeholder") {
                        // If there is a placeholder present, retrieve the new content
                        showLoading("Loading...", disclosureContent, true);
                        disclosureContent.show();
                        disclosureContent.trigger(kradVariables.EVENTS.ADJUST_STICKY);

                        // This a specialized methodToCall passed in for retrieving the originally generated component
                        retrieveComponent(linkId.replace("_toggle", ""), null, null, null, true);
                    }
                    else{
                        // If no ajax retrieval, slide down animationg
                        var options = {
                            duration: animationSpeed,
                            step: function(){
                                disclosureContent.trigger(kradVariables.EVENTS.ADJUST_STICKY);
                            }
                        };
                        disclosureContent.slideDown(options);

                    }
                }
            });
}

/**
 * Sets up focus and blur events for inputs with helper text.
 */
function setupHelperTextHandler() {
    jQuery(document).on(kradVariables.EVENTS.UPDATE_CONTENT + " ready", function() {
        if (jQuery('.uif-helperText').length) {
            jQuery('.uif-helperText').slideUp();
        }

        jQuery('.has-helper').on('focus', function () {
            if (jQuery(this).parent().find('.uif-helperText')) {
                jQuery(this).parent().find('.uif-helperText').slideDown();
            }
        });

        jQuery('.has-helper').on('blur', function () {
            if (jQuery(this).parent().find('.uif-helperText')) {
                jQuery(this).parent().find('.uif-helperText').slideUp();
            }
        });
    });
}

/**
 * Setup document level dragover, drop, and dragleave events to handle file drops and indication when dropping a
 * file into appropriate elements
 */
function setupFileDragHandlers(){
    // Prevent drag and drop events on the document to support file drags into upload widget
    jQuery(document).on("dragover", function (e) {
        e.preventDefault();
        var $fileCollections = jQuery(".uif-fileUploadCollection");
        $fileCollections.each(function(){
            var $fileCollection = jQuery(this);
            var id = $fileCollection.attr("id");
            var drop = $fileCollection.find(".uif-drop");
            if (!drop.length) {
                drop = jQuery("<div class='uif-drop uif-dropBlock'></div>");
                drop = drop.add("<span class='uif-drop uif-dropText'><span class='icon-plus'/> Drop Files to Add...</span>");
                drop.bind("drop", function (){
                    e.preventDefault();
                    jQuery("#" + id).trigger("drop");
                    jQuery(this).hide();
                });
                $fileCollection.append(drop);
            } else {
                drop.show();
            }
        });
    });

    jQuery(document).on("drop dragleave", function (e) {
        e.preventDefault();
        var fileCollections = jQuery(".uif-drop");
        fileCollections.each(function(){
            jQuery(this).hide();
        });
    });
}

function hideTooltips(element) {
    if (element != undefined && element.length) {
        jQuery(element).find("[data-hasTooltip]").popover("hide");
    }
    else {
        jQuery("[data-hasTooltip]").popover("hide");
    }
}

/**
 * Sets up the validator and the dirty check and other page scripts
 */
function setupPage(validate) {
    time(true, "page-setup");

    dirtyFormState.resetDirtyFieldCount();

    // if we are skipping this page setup, reset the flag, and return (this logic is for redirects)
    if (skipPageSetup) {
        skipPageSetup = false;
        return;
    }

    // update the top group per page
    var topGroupUpdateDiv = jQuery("#" + kradVariables.TOP_GROUP_UPDATE);
    var topGroupUpdate = topGroupUpdateDiv.find(">").detach();
    if (topGroupUpdate.length && !initialViewLoad) {
        jQuery("#Uif-TopGroupWrapper >").replaceWith(topGroupUpdate);
    }
    topGroupUpdateDiv.remove();

    // update the view header per page
    var headerUpdateDiv = jQuery("#" + kradVariables.VIEW_HEADER_UPDATE);
    var viewHeaderUpdate = headerUpdateDiv.find(".uif-viewHeader").detach();
    if (viewHeaderUpdate.length && !initialViewLoad) {
        var currentHeader = jQuery(".uif-viewHeader");
        if (currentHeader.data("offset")) {
            viewHeaderUpdate.data("offset", currentHeader.data("offset"));
        }
        jQuery(".uif-viewHeader").replaceWith(viewHeaderUpdate);
        stickyContent = jQuery("[data-sticky='true']:visible");
    }
    headerUpdateDiv.remove();

    originalPageTitle = document.title;

    setupImages();

    // reinitialize sticky footer content because page footer can be sticky
    jQuery(document).on(kradVariables.EVENTS.ADJUST_STICKY, function(){
        stickyFooterContent = jQuery("[data-sticky_footer='true']");
        initStickyFooterContent();
        handleStickyFooterContent();
        initStickyContent();
    });

    // Initialize global validation defaults
    if (groupValidationDefaults == undefined || fieldValidationDefaults == undefined) {
        groupValidationDefaults = jQuery("[data-role='View']").data(kradVariables.GROUP_VALIDATION_DEFAULTS);
        fieldValidationDefaults = jQuery("[data-role='View']").data(kradVariables.FIELD_VALIDATION_DEFAULTS);
    }

    if (actionDefaults == undefined) {
        actionDefaults = jQuery("[data-role='View']").data(kradVariables.ACTION_DEFAULTS);
    }

    // Reset summary state before processing each field - summaries are shown if server messages
    // or on client page validation
    messageSummariesShown = false;

    // flag to turn off and on validation mechanisms on the client
    validateClient = validate;

    // select current page
    var pageId = getCurrentPageId();

    // update URL to reflect the current page
    updateRequestUrl(pageId);

    prevPageMessageTotal = 0;

    var page = jQuery("[data-role='Page']");
    // skip input field iteration and validation message writing, if no server messages
    var hasServerMessagesData = page.data(kradVariables.SERVER_MESSAGES);
    if (hasServerMessagesData) {
        pageValidationPhase = true;
        // Handle messages at field, if any
        jQuery("div[data-role='InputField']").each(function () {
            var id = jQuery(this).attr('id');
            handleMessagesAtField(id, true);
        });

        // Write the result of the validation messages
        writeMessagesForPage();
        messageSummariesShown = true;
        pageValidationPhase = false;
    }
     //TODO: Looks like this class is not being used anywhere  - Remove?
    // focus on pageValidation header if there are messages on this page
    if (jQuery(".uif-pageValidationHeader").length) {
        jQuery(".uif-pageValidationHeader").focus();
    }

    setupValidator(jQuery('#kualiForm'));

    jQuery(".required").each(function () {
        jQuery(this).attr("aria-required", "true");
    });

    jQuery(document).trigger(kradVariables.VALIDATION_SETUP_EVENT);

    pageValidatorReady = true;

    jQuery(document).trigger(kradVariables.PAGE_LOAD_EVENT);

    jQuery.watermark.showAll();

    // If no focusId is specified through data attribute, default to FIRST input on the page
    var focusId = page.data(kradVariables.FOCUS_ID);
    if(!focusId) {
        focusId = "FIRST";
    }

    // Perform focus and jumpTo based on the data attributes
    performFocusAndJumpTo(true, focusId, page.data(kradVariables.JUMP_TO_ID), page.data(kradVariables.JUMP_TO_NAME));

    time(false, "page-setup");
}

/**
 * Sets up the validator with the necessary default settings and methods on a form
 *
 * @param form
 */
function setupValidator(form) {
    jQuery(form).validate();
}

/**
 * Initializes all of the image variables
 */
function setupImages() {
    errorImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/error.png' alt='" + getMessage(kradVariables.MESSAGE_ERROR) + "' /> ";
    errorGreyImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/error-grey.png' alt='" + getMessage(kradVariables.MESSAGE_ERROR_FIELD_MODIFIED) + "' /> ";
    warningImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/warning.png' alt='" + getMessage(kradVariables.MESSAGE_WARNING) + "' /> ";
    infoImage = "<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "validation/info.png' alt='" + getMessage(kradVariables.MESSAGE_INFORMATION) + "' /> ";
    detailsOpenImage = jQuery("<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "details_open.png' alt='" + getMessage(kradVariables.MESSAGE_DETAILS) + "' /> ");
    detailsCloseImage = jQuery("<img class='" + kradVariables.VALIDATION_IMAGE_CLASS + "' src='" + getConfigParam(kradVariables.IMAGE_LOCATION) + "details_close.png' alt='" + getMessage(kradVariables.MESSAGE_CLOSE_DETAILS) + "' /> ");
    refreshImage = jQuery("<img src='" + getContext().blockUI.defaults.refreshOptions.blockingImage + "' alt='" + getMessage(kradVariables.MESSAGE_LOADING) + "' /> ");
    navigationImage = jQuery("<img src='" + getContext().blockUI.defaults.navigationOptions.blockingImage + "' alt='" + getMessage(kradVariables.MESSAGE_LOADING) + "' /> ");
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

jQuery.validator.setDefaults({
    onsubmit: false,
    ignore: ".ignoreValid",
    wrapper: "",
    onfocusout: false,
    onclick: false,
    onkeyup: function (element) {
        if (validateClient) {
            var id = getAttributeId(jQuery(element).attr('id'));
            if(!id){ return; }
            var data = getValidationData(jQuery("#" + id));

            // if this field previously had errors validate on key up
            if (data && data.focusedErrors && data.focusedErrors.length) {
                var valid = validateFieldValue(element);
                if (!valid) {
                    showMessageTooltip(id, false, true);
                }
            }
        }
    },
    highlight: function (element, errorClass, validClass) {
        jQuery(element).addClass(errorClass).removeClass(validClass);
        jQuery(element).attr("aria-invalid", "true");
    },
    unhighlight: function (element, errorClass, validClass) {
        jQuery(element).removeClass(errorClass).addClass(validClass);
        jQuery(element).removeAttr("aria-invalid");

        var id = getAttributeId(jQuery(element).attr("id"));
        if(!id){ return; }
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
    },
    errorPlacement: function (error, element) {
    },
    showErrors: function (nameErrorMap, elementObjectList) {
        this.defaultShowErrors();

        for (var i in elementObjectList) {
            var element = elementObjectList[i].element;
            var message = elementObjectList[i].message;
            var id = getAttributeId(jQuery(element).attr('id'));
            if(!id){ return; }
            var field = jQuery("#" + id);
            var data = getValidationData(field);

            var exists = false;
            if (data && data.errors && data.errors.length) {
                for (var j in data.errors) {
                    if (data.errors[j] === message) {
                        exists = true;
                    }
                }
            }

            if (!exists) {
                data.errors = [];
                data.errors.push(message);
                field.data(kradVariables.VALIDATION_MESSAGES, data);
            }

            if (data) {
                if (messageSummariesShown) {
                    handleMessagesAtField(id);
                }
                else {
                    writeMessagesAtField(id);
                }
            }

            if (data && !exists && !pauseTooltipDisplay) {

            }
        }

    },
    success: function (label) {
        var htmlFor = jQuery(label).attr('for');
        var id = "";
        if (htmlFor.indexOf("_control") >= 0) {
            id = getAttributeId(htmlFor);
            if(!id){ return; }
        }
        else {
            id = jQuery("[name='" + escapeName(htmlFor) + "']:first").attr("id");
            id = getAttributeId(id);
            if(!id){ return; }
        }

        var field = jQuery("#" + id);
        var data = getValidationData(field);

        if (data && data.errors && data.errors.length) {
            data.errors = [];
            field.data(kradVariables.VALIDATION_MESSAGES, data);
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
    if (x.charAt(0) == '$') {
        x = x.substring(1);
    }
    if (y.charAt(0) == '$') {
        y = y.substring(1);
    }
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
    if (x.charAt(0) == '$') {
        x = x.substring(1);
    }
    if (y.charAt(0) == '$') {
        y = y.substring(1);
    }
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
jQuery.fn.dataTableExt.afnSortData['dom-text'] = function (oSettings, iColumn, iVisColumn) {
    var aData = [];
    jQuery(oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var td = jQuery('>td:eq(' + iVisColumn + '):first', this);
        var input = jQuery(td).find('input:text');
        var value = "";
        if (input.length != 0) {
            value = input.val();
        } else {
            // check for linkField
            var linkField = jQuery(td).find('.uif-linkField');
            if (linkField.length != 0) {
                value = linkField.text().trim();
            } else {
                // find span for the data or input field and get its text
                var inputField = jQuery(td).find('.uif-field');
                if (inputField.length != 0) {
                    value = jQuery.trim(inputField.text());
                } else {
                    // just use the text within the cell
                    value = jQuery(td).text().trim();
                    // strip leading $ if present
                    if (value.charAt(0) == '$') {
                        value = value.substring(1);
                    }
                }
            }
        }
        var additionalDisplaySeparatorIndex = value.indexOf("*-*");
        if (additionalDisplaySeparatorIndex != -1) {
            value = value.substring(0, additionalDisplaySeparatorIndex).trim();
        }
        aData.push(value);
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
jQuery.fn.dataTableExt.afnSortData['dom-select'] = function (oSettings, iColumn, iVisColumn) {
    var aData = [];
    jQuery(oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var td = jQuery('>td:eq(' + iVisColumn + '):first', this);
        var selected = jQuery(td).find('select option:selected:first');
        if (selected.length != 0) {
            aData.push(selected.text());
        } else {
            var input1 = jQuery(td).find("[data-role='InputField']");
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
jQuery.fn.dataTableExt.afnSortData['dom-checkbox'] = function (oSettings, iColumn, iVisColumn) {
    var aData = [];
    jQuery(oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var td = jQuery('>td:eq(' + iVisColumn + '):first', this);
        var checkboxes = jQuery(td).find('input:checkbox');
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
            var input1 = jQuery(td).find("[data-role='InputField']");
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
jQuery.fn.dataTableExt.afnSortData['dom-radio'] = function (oSettings, iColumn, iVisColumn) {
    var aData = [];
    jQuery(oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var td = jQuery('>td:eq(' + iVisColumn + '):first', this);
        var radioButtons = jQuery(td).find('input:radio');
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
            var input1 = jQuery(td).find("[data-role='InputField']");
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
    jQuery("[data-role='Page']").show();
    var context = getContext();
    context.unblockUI();
    var errorMessage = msg + '<br/>' + url + '<br/>' + lno;
    showGrowl(errorMessage, 'Javascript Error', 'errorGrowl');
    if (window.console) {
        console.log(errorMessage);
    }
    return false;
}

// script that should execute when the page unloads
// jQuery(window).bind('beforeunload', function (evt) {
// clear server form if closing the browser tab/window or going back
// TODO: work out back button problem so we can add this clearing
//    if (!event.pageY || (event.pageY < 0)) {
//        clearServerSideForm();
//    }
//});



