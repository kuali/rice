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
/** Navigation */

/**
 * Sets the breadcrumb to whatever the current page is, if this view has page navigation
 * Pages are handled by js on the breadcrumb because the page retrieval happens through
 * ajax
 */
function setPageBreadcrumb() {
    // check to see if page has navigation element, if so show breadcrumb
    if (jQuery("#Uif-Navigation").html() && jQuery("#breadcrumbs").length) {
        var pageTitle = jQuery("#pageTitle").val();
        var pageId = jQuery("#pageId").val();

        jQuery("#breadcrumbs").find("#page_breadcrumb").remove();

        // if page title not set attempt to find from navigation
        if ((!pageTitle || pageTitle == "&nbsp;") && pageId) {
            pageTitle = jQuery("a[name='" + escapeName(pageId) + "']").text();
        }

        if (pageTitle && pageTitle != "&nbsp;") {
            jQuery("#breadcrumbs").append("<li id='page_breadcrumb'><span role='presentation'>&raquo;</span> <span class='kr-current'>" + pageTitle + "</span></li>");
            jQuery("#current_breadcrumb_span").hide();

            if (jQuery("#current_breadcrumb_span").parent("li").length) {
                jQuery("#current_breadcrumb_span").unwrap();
            }

            jQuery("#current_breadcrumb_anchor").wrap("<li/>");
            jQuery("#current_breadcrumb_anchor").show();
        }
        else {
            jQuery("#current_breadcrumb_anchor").hide();
            if (jQuery("#current_breadcrumb_anchor").parent("li").length) {
                jQuery("#current_breadcrumb_anchor").unwrap();
            }

            jQuery("#current_breadcrumb_span").wrap("<li/>");
            jQuery("#current_breadcrumb_span").show();
        }
    }
}

/**
 * Renders a navigation group for the list with the given id. Helper methods are
 * called based on the type to implement a certain style of navigation.
 *
 * @param listId -
 *          unique id for the unordered list
 * @param navigationType -
 *          the navigation style to render
 */
function createNavigation(listId, navigationType, options) {
    if (navigationType == "VERTICAL_MENU") {
        createVerticalMenu(listId, options);
    }
    else if (navigationType == "TAB_MENU") {
        createTabMenu(listId, options);
    }
}

function createTabMenu(listId, options) {
    jQuery(document).ready(function () {
        jQuery("#" + listId).tabMenu(options);
    });
}

/**
 * Uses jQuery menu plug-in to build a menu for the list with the given id
 *
 * @param listId -
 *          unique id for the unordered list
 */
function createVerticalMenu(listId, options) {
    jQuery(document).ready(function () {
        jQuery("#" + listId).navMenu(options);
    });
}

/** Widgets */

/**
 * Sets ups a text popout button and window for this particular field that will be generated
 * when that button is clicked
 *
 * @param id - id of the control
 * @param label - label to be used in popout
 * @param summary - summary to be used in popout
 * @param constraint - constraint to be used in popout
 * @param imageUrl - the url for the popout icon
 */
function setupTextPopout(id, label, summary, constraint, imageUrl) {
    var options = {label:label, summary:summary, constraint:constraint};
    jQuery("#" + id).initPopoutText(options, imageUrl);
}

/**
 * Uses jQuery fancybox to open a lightbox for a link's content. The second
 * argument is a Map of options that are available for the FancyBox. See
 * <link>http://fancybox.net/api</link> for documentation on these options.
 * The third argument should only be true for inquiries and lookups.  When this
 * argument is true additional URL parameters are added for the bread crumbs history.
 *
 * @param linkId -
 *          id for the link that the fancybox should be linked to
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 * @parm isAddAppParms -
 *          true if application parameters should be added to the link, false otherwise
 */
function createLightBoxLink(linkId, options, addAppParms) {
    jQuery(function () {
        // first time content is brought up in lightbox we don't want to show history
        var showHistory = false;

        // Check if this is called within a light box
        // TODO: utility function for checking whether in a lightbox
        if (!jQuery(".fancybox-iframe", parent.document).length) {
            // Check if this is called within a krad light box
            if (jQuery('#renderedInLightBox').val() == true) {
                // Perform cleanup when lightbox is closed
                options['beforeClose'] = cleanupClosedLightboxForms;
            }

            // If this is not the top frame, then create the lightbox
            // on the top frame to put overlay over whole window
            if (top == self) {
                jQuery("#" + linkId).fancybox(options);
            } else {
                jQuery("#" + linkId).click(function (e) {
                    e.preventDefault();

                    options['href'] = jQuery("#" + linkId).attr('href');
                    top.jQuery.fancybox(options);
                });
            }
        } else {
            jQuery("#" + linkId).attr('target', '_self');

            // for going to a new view in a lightbox we want to show history
            showHistory = true;
        }

        if (addAppParms) {
            // Set the renderedInLightBox = true param
            if (jQuery("#" + linkId).attr('href').indexOf('&renderedInLightBox=true') == -1) {
                var href = jQuery("#" + linkId).attr('href');
                var anchor = "";

                if (jQuery("#" + linkId).attr('href').indexOf('#') != -1) {
                    href = jQuery("#" + linkId).attr('href').substring(0, jQuery("#" + linkId).attr('href').indexOf('#'));
                    anchor = jQuery("#" + linkId).attr('href').substring(jQuery("#" + linkId).attr('href').indexOf('#'));
                }

                jQuery("#" + linkId).attr('href', href + '&renderedInLightBox=true'
                        + '&showHome=false' + '&showHistory=' + showHistory
                        + '&history=' + jQuery('#historyParameterString').val() + anchor);
            }
        }
    });
}

/**
 * Submits the form based on the quickfinder action identified by the given id and display the result content in
 * a lightbox using the jQuery fancybox. If we are not currently in a lightbox, we will request a redirect URL
 * for the lightbox contents. Otherwise, the internal iframe of the lightbox will be redirected.
 *
 * <p>
 * See <link>http://fancybox.net/api</link> for documentation on plugin options
 * </p>
 *
 * @param componentId -
 *          id for the action component that the fancybox should be linked to
 * @param options -
 *          map of option settings (option name/value pairs) for the fancybox plugin
 * @param lookupReturnByScript - boolean that indicates whether the lookup should return through script
 *        or via a server post
 */
function createLightBoxPost(componentId, options, lookupReturnByScript) {
    jQuery(function () {
        // get data that should be submitted when the action is selected
        var data = {};

        var submitData = jQuery("#" + componentId).data("submitData");
        jQuery.extend(data, submitData);

        // Check if this is not called within a lightbox
        if (!jQuery(".fancybox-iframe", parent.document).length) {
            jQuery("#" + componentId).click(function (e) {
                // Prevent the default submit
                e.preventDefault();

                data['jumpToId'] = componentId;
                data['actionParameters[renderedInLightBox]'] = 'true';
                data['actionParameters[lightBoxCall]'] = 'true';
                data['actionParameters[showHistory]'] = 'false';
                data['actionParameters[showHome]'] = 'false';
                data['actionParameters[returnByScript]'] = '' + lookupReturnByScript;

                // If this is the top frame, the page is not displayed in the iframeprotlet
                // set the return target
                if (top == self) {
                    data['actionParameters[returnTarget]'] = '_parent';
                } else {
                    data['actionParameters[returnTarget]'] = 'iframeportlet';
                }

                // TODO: we need a fix here so dirty fields don't get cleared out
                // if refreshing the page on return from lookup need to clear dirty fields else
                // a warning is given
                if (!lookupReturnByScript) {
                    jQuery('*').removeClass(kradVariables.DIRTY_CLASS);
                }

                // Do the Ajax submit on the kualiForm form
                jQuery("#kualiForm").ajaxSubmit({
                    data:data,
                    success:function (data) {
                        // Perform cleanup when lightbox is closed
                        // TODO: this stomps on the post form (clear out) so need to another
                        // way to clear forms when the lightbox performs a post back
                        // options['beforeClose'] = cleanupClosedLightboxForms;

                        // Add the returned URL to the FancyBox href setting
                        options['href'] = data.replace(/&amp;/g, '&');

                        // Open the light box
                        if (top == self) {
                            jQuery.fancybox(options);
                        } else {
                            // for portal usage
                            parent.jQuery.fancybox(options);
                        }
                    }
                });
            });
        } else {
            // add parameters for lightbox and do standard submit
            jQuery("#" + componentId).click(function (e) {
                // Prevent the default submit
                e.preventDefault();

                data['actionParameters[renderedInLightBox]'] = 'true';
                data['actionParameters[returnTarget]'] = '_self';
                data['actionParameters[showHistory]'] = 'true';
                data['actionParameters[showHome]'] = 'false';

                submitForm(data['methodToCall'], data, null);
            });
        }
    });
}

/*
 * Function that returns lookup results by script
 */
function returnLookupResultByScript(fieldName, value) {
    var returnField;

    if (top != self) {
        returnField = parent.jQuery('#iframeportlet').contents().find('[name="' + escapeName(fieldName) + '"]');
    } else {
        returnField = parent.jQuery('[name="' + escapeName(fieldName) + '"]');
    }

    returnField.val(value);
    returnField.focus();
    returnField.blur();
    returnField.focus();

    // trigger change event
    returnField.change();
}

/*
 * Function that sets the return target when returning multiple lookup results
 */
function setMultiValueReturnTarget() {
    if (top != self) {
        jQuery('#kualiForm').attr('target', parent.jQuery('#iframeportlet').attr('name'));
    } else {
        jQuery('#kualiForm').attr('target', '_parent');
    }
}

/**
 * Opens the inquiry window
 * Is called from the onclick event on the direct inquiry
 * The parameters is added by dynamically getting the values
 * for the fields in the parameter maps and then added to the url string
 *
 * @param url -
 *          the base url to use to call the inquiry
 * @param paramMap -
 *          array of field parameters for the inquiry
 * @param showLightBox -
 *          flag to indicate if it must be shown in a lightbox
 * @param lightBoxOptions -
 *          map of option settings (option name/value pairs) for the lightbox plugin
 */
function showDirectInquiry(url, paramMap, showLightBox, lightBoxOptions) {
    var parameterPairs = paramMap.split(",");
    var queryString = "&showHome=false";

    for (i in parameterPairs) {
        var parameters = parameterPairs[i].split(":");

        if (jQuery('[name="' + escapeName(parameters[0]) + '"]').val() == "") {
            alert("Please enter a value in the appropriate field.");
            return false;
        } else {
            queryString = queryString + "&" + parameters[1] + "="
                    + jQuery('[name="' + escapeName(parameters[0]) + '"]').val();
        }
    }

    if (showLightBox) {
        // Check if this is called within a light box
        if (!jQuery(".fancybox-iframe", parent.document).length) {

            // Perform cleanup when lightbox is closed
            lightBoxOptions['beforeClose'] = cleanupClosedLightboxForms;

            // If this is not the top frame, then create the lightbox
            // on the top frame to put overlay over whole window
            queryString = queryString + "&showHistory=false&renderedInLightBox=true";
            if (top == self) {
                lightBoxOptions['href'] = url + queryString;
                jQuery.fancybox(lightBoxOptions);
            } else {
                lightBoxOptions['href'] = url + queryString;
                top.jQuery.fancybox(lightBoxOptions);
            }
        } else {
            // If this is already in a lightbox just open in current lightbox
            queryString = queryString + "&showHistory=true&renderedInLightBox=true";
            window.open(url + queryString, "_self");
        }
    } else {
        queryString = queryString + "&showHistory=false";
        window.open(url + queryString, "_blank", "width=640, height=600, scrollbars=yes");
    }
}

/**
 * Closes the lightbox window
 */
function closeLightbox() {
    top.jQuery.fancybox.close();
}

/**
 * Cleanup form data from server when lightbox window is closed
 */
function cleanupClosedLightboxForms() {
    if (jQuery('#formKey').length) {
        // get the formKey of the lightbox (fancybox)
        var context = getContext();
        var formKey = context('iframe.fancybox-iframe').contents().find('input#formKey').val();

        clearServerSideForm(formKey);
    }
}

/**
 * Uses jQuery DatePicker to render a calendar that can be used to select date
 * values for the field with the given control id. The second argument is a Map
 * of options that are available for the DatePicker. See
 * <link>http://jqueryui.com/demos/datepicker/#option-showOptions</link> for
 * documentation on these options
 *
 * @param controlId -
 *          id for the control that the date picker should populate
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createDatePicker(controlId, options) {
    var fieldId = jQuery("#" + controlId).closest("[data-role='InputField']").attr("id");
    jQuery(function () {
        jQuery("#" + controlId).datepicker(options);
        jQuery("#" + controlId).datepicker('option', 'onClose',
                function () {
                    jQuery("#" + fieldId).data(kradVariables.VALIDATION_MESSAGES).messagingEnabled = true;
                    jQuery(this).trigger("focusout");
                    jQuery(this).trigger("focus");
                });
        jQuery("#" + controlId).datepicker('option', 'beforeShow',
                function () {
                    jQuery("#" + fieldId).data(kradVariables.VALIDATION_MESSAGES).messagingEnabled = false;
                });

    });

    // in order to compensate for jQuery's "Today" functionality (which does not actually return the date to the input box), alter the functionality
    jQuery.datepicker._gotoToday = function (id) {
        var target = jQuery(id);
        var inst = this._getInst(target[0]);
        if (this._get(inst, 'gotoCurrent') && inst.currentDay) {
            inst.selectedDay = inst.currentDay;
            inst.drawMonth = inst.selectedMonth = inst.currentMonth;
            inst.drawYear = inst.selectedYear = inst.currentYear;
        }
        else {
            var date = new Date();
            inst.selectedDay = date.getDate();
            inst.drawMonth = inst.selectedMonth = date.getMonth();
            inst.drawYear = inst.selectedYear = date.getFullYear();
        }
        this._notifyChange(inst);
        this._adjustDate(target);

        // The following two lines are additions to the original jQuery code
        this._setDateDatepicker(target, new Date());
        this._selectDate(id, this._getDateDatepicker(target));
    }
}

/**
 * Sets up the script necessary to toggle a group as a disclosure
 *
 * @param groupId -
 *          id for the group to be toggled
 * @param headerId -
 *          id for the group's header in which the toggle link and image will be
 *          inserted
 * @param widgetId - id for the accordion widget, used for updating state
 * @param defaultOpen -
 *          indicates whether the group should be initially open or close
 * @param collapseImgSrc -
 *          path to the image that should be displayed for collapsing the group
 * @param expandImgSrc -
 *          path to the image that should be displayed for expanding the group
 * @param animationSpeed -
 *          speed at which the group should be expanded or collapsed
 * @param renderImage -
 *          boolean that indicates whether the expanded or collapsed image should be rendered
 */
function createDisclosure(groupId, headerId, widgetId, defaultOpen, collapseImgSrc, expandImgSrc, animationSpeed, renderImage) {
    jQuery(document).ready(function () {
        var groupToggleLinkId = groupId + "_toggle";

        var expandImage = "";
        var collapseImage = "";
        if (renderImage) {
            var expandImage = "<img id='" + groupId + "_exp" + "' src='" + expandImgSrc + "' alt='expand' class='uif-disclosure-image'/>";
            var collapseImage = "<img id='" + groupId + "_col" + "' src='" + collapseImgSrc + "' alt='collapse' class='uif-disclosure-image'/>";
        }

        var groupAccordionSpanId = groupId + "_disclosureContent";

        // perform initial open/close and insert toggle link and image
        var headerText = jQuery("#" + headerId + " > :header, #" + headerId + " > label").find(".uif-headerText-span");
        if (defaultOpen) {
            jQuery("#" + groupAccordionSpanId).slideDown(000);
            headerText.prepend(expandImage);
        }
        else {
            jQuery("#" + groupAccordionSpanId).slideUp(000);
            headerText.prepend(collapseImage);
        }

        headerText.wrap("<a href='#' id='" + groupToggleLinkId + "'></a>");

        var animationFinishedCallback = function () {
            jQuery("#" + kradVariables.APP_ID).attr("data-skipResize", false);
        };
        // perform slide and switch image
        if (defaultOpen) {
            jQuery("#" + groupToggleLinkId).toggle(
                    function () {
                        jQuery("#" + kradVariables.APP_ID).attr("data-skipResize", true);
                        jQuery("#" + groupAccordionSpanId).slideUp(animationSpeed, animationFinishedCallback);
                        jQuery("#" + groupId + "_exp").replaceWith(collapseImage);
                        setComponentState(widgetId, 'defaultOpen', false);
                    }, function () {
                        jQuery("#" + kradVariables.APP_ID).attr("data-skipResize", true);
                        jQuery("#" + groupAccordionSpanId).slideDown(animationSpeed, animationFinishedCallback);
                        jQuery("#" + groupId + "_col").replaceWith(expandImage);
                        setComponentState(widgetId, 'defaultOpen', true);
                    }
            );
        }
        else {
            jQuery("#" + groupToggleLinkId).toggle(
                    function () {
                        jQuery("#" + kradVariables.APP_ID).attr("data-skipResize", true);
                        jQuery("#" + groupAccordionSpanId).slideDown(animationSpeed, animationFinishedCallback);
                        jQuery("#" + groupId + "_col").replaceWith(expandImage);
                        setComponentState(widgetId, 'defaultOpen', true);

                    }, function () {
                        jQuery("#" + kradVariables.APP_ID).attr("data-skipResize", true);
                        jQuery("#" + groupAccordionSpanId).slideUp(animationSpeed, animationFinishedCallback);
                        jQuery("#" + groupId + "_exp").replaceWith(collapseImage);
                        setComponentState(widgetId, 'defaultOpen', false);
                    }
            );
        }
    });
}

/**
 * Expands all the disclosure divs on the page
 */
function expandDisclosures() {
    jQuery('img[alt="collapse"]').click();
}

/**
 * Collapses all the disclosure divs on the page
 */
function collapseDisclosures() {
    jQuery('img[alt="expand"]').click();
}

/**
 * Uses jQuery DataTable plug-in to decorate a table with functionality like
 * sorting and page. The second argument is a Map of options that are available
 * for the plug-in. See <link>http://www.datatables.net/usage/</link> for
 * documentation on these options
 *
 * @param tableId -
 *          id for the table that should be decorated
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createTable(tableId, options) {
    jQuery(document).ready(function () {
        var oTable = jQuery("#" + tableId).dataTable(options);
        // allow table column size recalculation on window resize
        jQuery(window).bind('resize', function () {
            oTable.fnAdjustColumnSizing();
        });
    });
}

/**
 * Expands a data table row by finding the row that matches the actionComponent passed in, in the
 * dataTable which matches tableId.  If useImages is true, images will be swapped out when the
 * action is clicked.
 * @param actionComponent the actionComponent clicked
 * @param tableId the dataTable to expand the clicked row in
 * @param useImages if true, swap open/close images on click
 */
function expandDataTableDetail(actionComponent, tableId, useImages) {
    var oTable = null;
    var tables = jQuery.fn.dataTable.fnTables();
    jQuery(tables).each(function () {
        var dataTable = jQuery(this).dataTable();
        //ensure the dataTable is the one that contains the action that was clicked
        if (jQuery(actionComponent).closest(dataTable).length) {
            oTable = dataTable;
        }
    });

    if (oTable != null) {
        var nTr = jQuery(actionComponent).parents('tr')[0];
        if (oTable.fnIsOpen(nTr)) {
            if (useImages && jQuery(actionComponent).find("img").length) {
                jQuery(actionComponent).find("img").replaceWith(detailsOpenImage.clone());
            }
            jQuery(nTr).next().first().find(".uif-group").first().slideUp(function () {
                oTable.fnClose(nTr);
            });
        }
        else {
            if (useImages && jQuery(actionComponent).find("img").length) {
                jQuery(actionComponent).find("img").replaceWith(detailsCloseImage.clone());
            }
            var newRow = oTable.fnOpen(nTr, fnFormatDetails(actionComponent), "uif-rowDetails");
            jQuery(newRow).find(".uif-group").first().slideDown();
        }
    }
}

/**
 * Finds the hidden content generated by the framework for the the data table row which
 * contains the actionComponent passed in.  Returns the html as required by the fnOpen function
 * call.  Should not be called directly.
 * @param actionComponent the action that was clicked to open the row
 */
function fnFormatDetails(actionComponent) {
    var hiddenGroup = jQuery(actionComponent).parent().find(".uif-group:first");
    var html = "";

    html = jQuery(hiddenGroup).clone().wrap("<div>").parent().html();

    return html;
}

/**
 * Select all checkboxes within the collection div that are marked with class 'kr-select-line' (used
 * for multi-value select collections)
 *
 * @param collectionId - id for the collection to select checkboxes for
 */
function selectAllLines(collectionId) {
    jQuery("#" + collectionId + " input:checkbox.kr-select-line").attr('checked', true);
}

/**
 * Deselects all checkboxes within the collection div that are marked with class 'kr-select-line' (used
 * for multi-value select collections)
 *
 * @param collectionId - id for the collection to deselect checkboxes for
 */
function deselectAllLines(collectionId) {
    jQuery("#" + collectionId + " input:checkbox.kr-select-line").attr('checked', false);
}

/**
 * Uses jQuery jsTree plug-in to decorate a div with tree functionality. The
 * second argument is a Map of options that are available
 * for the plug-in. See <link>http://www.jstree.com/documentation/</link> for
 * documentation on these options
 *
 * @param divId -
 *          id for the div that should be decorated
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createTree(divId, options) {
    jQuery(document).ready(function () {
        jQuery("#" + divId).jstree(options);
    });
}

// Creates tabs for the tabs div id specified, this div is created by tabGroup
function createTabs(id, options) {
    jQuery("#" + id + "_tabs").tabs(options);
}

/**
 * Uses jQuery UI Auto-complete widget to provide suggest options for the given field. See
 * <link>http://jqueryui.com/demos/autocomplete/</link> for documentation on this widget
 *
 * @param controlId -
 *           id for the html control the autocomplete will be enabled for
 * @param options -
 *           map of option settings (option name/value pairs) for the widget
 * @param queryFieldId -
 *          id for the attribute field the control belongs to, used when making the
 * request to execute the associated attribute query
 * @param queryParameters -
 *         map of parameters that should be sent along with the query. map key gives
 * the name of the parameter to send, and the value gives the name of the field to pull the value from
 */
function createSuggest(controlId, options, queryFieldId, queryParameters) {
    options.source = function (request, response) {
        var queryData = {};
        queryData.methodToCall = 'performFieldSuggest';
        queryData.skipViewInit = 'true';
        queryData.formKey = jQuery("input#formKey").val();
        queryData.queryTerm = request.term;
        queryData.queryFieldId = queryFieldId;

        for (var parameter in queryParameters) {
            queryData['queryParameter.' + parameter] = coerceValue(queryParameters[parameter]);
        }

        jQuery.ajax({
            url:jQuery("form#kualiForm").attr("action"),
            dataType:"json",
            beforeSend:null,
            complete:null,
            error:null,
            data:queryData,
            success:function (data) {
                response(data.resultData);
            }
        });
    };

    jQuery(document).ready(function () {
        jQuery("#" + controlId).autocomplete(options);
    });
}

/**
 * Uses the Fluid Reorderer plug-in to allow the items of the given div to be reordered by the user.
 * See <link>http://wiki.fluidproject.org/display/fluid/Grid+Reorderer+API</link> for documentation on the
 * options available for the plug-in
 *
 * @param divId - id for the div containing the items to be reordered
 * @param options - options for reorderer plug-in
 */
function createReorderer(divId, options) {
    fluid.reorderGrid(jQuery("#" + divId), options);
}

/**
 * Creates the spinner widget for an input
 *
 * @param id - id for the control to apply the spinner to
 * @param options - options for the spinner
 */
function createSpinner(id, options) {
    jQuery("#" + id).spinit(options);
}

/**
 * Creates the tooltip widget for an component
 *
 * @param id - id for the component to apply the tooltip to
 * @param options - options for the tooltip
 */
function createTooltip(id, text, options, onMouseHoverFlag, onFocusFlag) {
    var elementInfo = getHoverElement(id);
    var element = elementInfo.element;

    options['innerHtml'] = text;
    options['manageMouseEvents'] = false;
    if (onFocusFlag) {
        // Add onfocus trigger
        jQuery("#" + id).focus(function () {
//            if (!jQuery("#" + id).IsBubblePopupOpen()) {
            // TODO : use data attribute to check if control
            if (!isControlWithMessages(id)) {
                jQuery("#" + id).SetBubblePopupOptions(options, true);
                jQuery("#" + id).SetBubblePopupInnerHtml(options.innerHTML, true);
                jQuery("#" + id).ShowBubblePopup();
            }
//            }
        });
        jQuery("#" + id).blur(function () {
            jQuery("#" + id).HideBubblePopup();
        });
    }
    if (onMouseHoverFlag) {
        // Add mouse hover trigger
        jQuery("#" + id).hover(function () {
            if (!jQuery("#" + id).IsBubblePopupOpen()) {
                if (!isControlWithMessages(id)) {
                    jQuery("#" + id).SetBubblePopupOptions(options, true);
                    jQuery("#" + id).SetBubblePopupInnerHtml(options.innerHTML, true);
                    jQuery("#" + id).ShowBubblePopup();
                }
            }
        }, function (event) {
            if (!onFocusFlag || !jQuery("#" + id).is(":focus")) {
                var result = mouseInTooltipCheck(event, id, element, this, elementInfo.type);
                if (result) {
                    mouseLeaveHideTooltip(id, jQuery("#" + id), element, elementInfo.type);
                }
            }
        });
    }
}

/**
 * Checks if the component is a control or contains a control that contains validation messages
 *
 * @param id the id of the field
 */
function isControlWithMessages(id) {
    // check if component is or contains a control
    if (jQuery("#" + id).is(".uif-control")
            || (jQuery("#" + id).is(".uif-inputField") && jQuery("#" + id + "_control").is(".uif-control"))) {
        return hasMessage(id)
    }
    return false;
}

/**
 * Checks if a field has any messages
 *
 * @param id
 */
function hasMessage(id) {
    var fieldId = getAttributeId(id);
    var messageData = jQuery("#" + fieldId).data(kradVariables.VALIDATION_MESSAGES);
    if (messageData && (messageData.serverErrors.length || (messageData.errors && messageData.errors.length)
            || messageData.serverWarnings.length || (messageData.warnings && messageData.warnings.length)
            || messageData.serverInfo.length || (messageData.info && messageData.info.length))) {
        return true;
    }
    return false;
}

/**
 * Workaround to prevent hiding the tooltip when the mouse actually may still be hovering over the field
 * correctly, checks to see if the mouseleave event was entering the tooltip and if so dont continue the
 * hide action, rather add a mouseleave handler that will only be invoked once for that segment, when this
 * is left the check occurs again, until the user has either left the tooltip or the field - then the tooltip
 * is hidden appropriately
 * @param event - mouseleave event
 * @param fieldId - id of the field this logic is being applied to
 * @param triggerElements - the elements that can trigger mouseover
 * @param callingElement - original element that invoked the mouseleave
 * @param type - type of the field
 */
function mouseInTooltipCheck(event, fieldId, triggerElements, callingElement, type) {
    if (event.relatedTarget &&
            jQuery(event.relatedTarget).length &&
            jQuery(event.relatedTarget).attr("class") != null &&
            jQuery(event.relatedTarget).attr("class").indexOf("jquerybubblepopup") >= 0) {
        //this bind is only every invoked once, then unbound - return false to stop hide
        jQuery(event.relatedTarget).one("mouseleave", function (event) {
            mouseInTooltipCheck(event, fieldId, triggerElements, callingElement, type);
        });
        return false;
    }
    //If target moving into is not a triggerElement for this hover
    // and if the source of the event is not a trigger element
    else if (!jQuery(event.relatedTarget).is(triggerElements) && !jQuery(event.target).is(triggerElements)) {
        //hide the tooltip for the original element
        mouseLeaveHideTooltip(fieldId, callingElement, triggerElements, type, true);
        return true;
    }
    else {
        return true;
    }
}

/**
 * Method to hide the tooltip when the mouse leave event was successful for the field
 * @param id id of the field
 * @param currentElement the current element be iterated on
 * @param elements all elements within the hover set
 * @param type type of field
 */
function mouseLeaveHideTooltip(id, currentElement, elements, type, force) {
    var hide = true;
    var tooltipElement = jQuery(currentElement);

    if (type == "fieldset") {
        //hide only if mouseleave is on fieldset not its internal radios/checkboxes
        hide = force || jQuery(currentElement).is("fieldset");
        tooltipElement = elements.filter("label:first");
    }

    //hide only if hide flag is true and the tooltip is open
    if (hide && jQuery(tooltipElement).IsBubblePopupOpen()) {
        hideTooltip(id);
    }
}

/**
 * Hide the tooltip associated with the field by id
 * @param fieldId the id of the field
 */
function hideTooltip(fieldId) {
    var elementInfo = getTooltipElement(fieldId);
    var element = elementInfo.element;
    if (elementInfo.type == "fieldset") {
        //for checkbox/radio fieldsets we put the tooltip on the label of the first input
        element = jQuery(element).filter("label:first");
    }
    var data = jQuery("#" + fieldId).data(kradVariables.VALIDATION_MESSAGES);
    if (data && data.showTimer) {
        clearTimeout(data.showTimer);
    }
    var tooltipId = jQuery(element).GetBubblePopupID();
    if (tooltipId) {
        //this causes the tooltip to be IMMEDIATELY hidden, rather than wait for animation
        jQuery("#" + tooltipId).css("opacity", 0);
        jQuery("#" + tooltipId).hide();
    }
    jQuery(element).HideBubblePopup();

}

/**
 * Gets the hover elements for a field by id.  The hover elements are the elements which will cause the tooltip to
 * be shown, the element the tooltip is actually placed on is an item its hover elements.
 * @param fieldId the id of the field
 */
function getTooltipElement(fieldId) {
    var hasFieldset = jQuery("#" + fieldId).find("fieldset").length;
    var elementInfo = {};

    if (!hasFieldset) {
        //regular case
        elementInfo.element = jQuery("#" + fieldId);
        elementInfo.type = "";
        if (elementInfo.element.is("input:checkbox")) {
            elementInfo.themeMargins = {
                total:'13px',
                difference:'0px'
            };
        }
    }
    else if (hasFieldset && jQuery("#" + fieldId).find("fieldset > span > input").length) {
        //radio and checkbox fieldset case
        //get the fieldset, the inputs its associated with, and the associated labels as hover elements
        elementInfo.element = jQuery("#" + fieldId).find("fieldset, fieldset input, fieldset label");
        elementInfo.type = "fieldset";
        elementInfo.themeMargins = {
            total:'13px',
            difference:'2px'
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
 * Executes a query with ajax for the given field to retrieve additional information after
 * the field has been updated (on blur)
 *
 * @param controlId -
 *           id for the html control to pull current value from
 * @param queryFieldId -
 *          id for the attribute field the control belongs to, used when making the
 * request to execute the associated field query
 * @param queryParameters -
 *         map of parameters that should be sent along with the query. map key gives
 * the name of the parameter to send, and the value gives the name of the field to pull the value from
 * @param queryMethodArgs -
 *         list of parameters that should be sent along with the query, the list gives the
 * name of the field in the view to pull values from, and will be sent with the same name
 * as a query parameter on the request
 * @param returnFieldMapping -
 *        map of fields that should be returned (updated) from the query. map key gives
 * the name of the parameter to update, map value is the name of field to pull value from
 */
function executeFieldQuery(controlId, queryFieldId, queryParameters, queryMethodArgs, returnFieldMapping) {
    var queryData = {};

    queryData.methodToCall = 'performFieldQuery';
    queryData.skipViewInit = 'true';
    queryData.formKey = jQuery("input#formKey").val();
    queryData.queryFieldId = queryFieldId;

    for (var parameter in queryParameters) {
        queryData['queryParameter.' + queryParameters[parameter]] = coerceValue(parameter);
    }

    for (var i = 0; i < queryMethodArgs.length; i++) {
        var parameter = queryMethodArgs[i];
        queryData['queryParameter.' + parameter] = coerceValue(parameter);
    }

    jQuery.ajax({
        url:jQuery("form#kualiForm").attr("action"),
        dataType:"json",
        data:queryData,
        beforeSend:null,
        complete:null,
        error:null,
        success:function (data) {
            // write out return message (or blank)
            var returnMessageSpan = jQuery("#" + queryFieldId + "_info_message");
            if (returnMessageSpan.length > 0) {
                returnMessageSpan.html(data.resultMessage);
                if (data.resultMessageStyleClasses) {
                    returnMessageSpan.addClass(data.resultMessageStyleClasses);
                }
            }

            // write out informational field values, note if data does not exist
            // this will clear the field values
            for (var returnField in returnFieldMapping) {
                var fieldValue = data.resultFieldData[returnField];
                if (!fieldValue) {
                    fieldValue = "";
                }

                // check for regular fields
                var infoFieldSpan = jQuery("[name='" + escapeName(returnField) + "']");
                if (infoFieldSpan.length > 0) {
                    infoFieldSpan.val(fieldValue);
                    infoFieldSpan.change();
                }

                // check for info spans
                var returnFieldId = returnField.replace(/\./g, "_")
                        .replace(/\[/g, "-lbrak-")
                        .replace(/\]/g, "-rbrak-")
                        .replace(/\'/g, "-quot-");
                infoFieldSpan = jQuery("#" + queryFieldId + "_info_" + returnFieldId);
                if (infoFieldSpan.length > 0) {
                    infoFieldSpan.html(fieldValue);
                }
            }
        }
    });
}
