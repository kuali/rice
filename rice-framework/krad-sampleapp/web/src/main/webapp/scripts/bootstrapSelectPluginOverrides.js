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


    jQuery(document).on("focusout", "button[data-id]", function(){
        var controlId = jQuery(this).data("id");
        jQuery("#" + controlId).blur();

    });

    jQuery(document).on("focus", "button[data-id]", function(){
        var controlId = jQuery(this).data("id");
        var id = getAttributeId(controlId);
        if(!id){ return; }
        // keep track of what errors it had on initial focus
        var data = getValidationData(jQuery("#" + id));
        if (data && data.errors) {
            data.focusedErrors = data.errors;
        }

        //show tooltip on focus
        showMessageTooltip(id, false);
    });
    jQuery(document).on("mouseleave", "button[data-id]", function(){
        var controlId = jQuery(this).data("id");
        jQuery("#" + controlId).trigger("mouseleave");
    });

    jQuery(document).on("mouseenter", "button[data-id]", function(){
        var controlId = jQuery(this).data("id");
        jQuery("#" + controlId).trigger("mouseenter");
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
                if(!id){ return; }
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

    time(false, "field-handlers");
}

function getHoverElement(fieldId) {
    var fieldset = jQuery("#" + fieldId).find("fieldset");
    var hasFieldset = fieldset.length;
    var elementInfo = {};

    if(jQuery("#" + fieldId + "_control").is("select:hidden")){
        elementInfo.element = jQuery("[data-id='" + fieldId + "_control']");
        elementInfo.type = "";
    }
    else if (!hasFieldset || (hasFieldset && fieldset.data("type") == "InputSet")) {
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

            //modified appendange
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
                if (control.length) {
                    jQuery(control).focus();
                }else if(jQuery("[data-id='" + fieldId + "_control']").length){
                    control =  jQuery("[data-id='" + fieldId + "_control']");
                    control.focus;
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