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
 * Setup the breadcrumbs for this view by replacing the old breadcrumbs with the newest from the page
 *
 * @param displayBreadcrumbsWhenOne display the breadcrumbs when there is only one when true, otherwise do not
 */
function setupBreadcrumbs(displayBreadcrumbsWhenOne) {
    var breadcrumbsWrapper = jQuery("#Uif-BreadcrumbWrapper");

    if (!breadcrumbsWrapper.length) {
        return;
    }

    //clear the old breadcrumbs
    breadcrumbsWrapper.empty();
    breadcrumbsWrapper.show();

    var breadcrumbUpdate = jQuery("#Uif-BreadcrumbUpdate");

    //find the new ones
    var breadcrumbList = breadcrumbUpdate.find("> ol").detach();
    var items = breadcrumbList.find("> li");

    //dont display if display when one is false and there is only one item
    if ((!displayBreadcrumbsWhenOne && items.length == 1) || items.length == 0) {
        breadcrumbsWrapper.hide();
        breadcrumbUpdate.remove();
        return;
    }

    //set up sibling breadcrumb handler
    jQuery(breadcrumbList).on("click", ".uif-breadcrumbSiblingLink", function () {
        var content = jQuery(this).parent().find(".uif-breadcrumbSiblingContent");
        var breadcrumb = jQuery(this).parent().find("[data-role='breadcrumb']");
        var siblingLink = this;

        if (content.length && !content.is(":visible") && breadcrumb.length && !jQuery(siblingLink).data("close")) {
            content.attr("style", "");
            content.position({
                my: "left top",
                at: "left bottom+5",
                of: breadcrumb
            });
            content.show();

            jQuery(document).on("mouseup.bc-sibling", function (e) {
                var container = jQuery(".uif-breadcrumbSiblingContent:visible");

                //if not in the breadcrumb sibling content, close and remove this handler
                if (container.has(e.target).length === 0) {
                    container.hide();
                    jQuery(document).off("mouseup.bc-sibling");
                }

                //if the target clicked is the siblingLink, mark it with a close flag (so click handler does not
                //reopen - processed after the mouseup)
                if (e.target == siblingLink) {
                    jQuery(siblingLink).data("close", true);
                }
            });

        }

        //remove the close flag
        if (jQuery(siblingLink).data("close")) {
            jQuery(siblingLink).data("close", false);
        }
    });

    //if the last item has a link, make it a span
    var lastLink = items.last().find("> a[data-role='breadcrumb']");
    if (lastLink.length) {
        lastLink.replaceWith(function () {
            return jQuery("<span data-role='breadcrumb'>" + jQuery(this).html() + "</span>");
        });
    }

    //append to the wrapper
    jQuery("#Uif-BreadcrumbWrapper").append(breadcrumbList);
    breadcrumbUpdate.remove();
}

function setupLocationSelect(controlId) {
    var control = jQuery("select#" + controlId);
    if (control.length) {
        //navigate if the value changes
        control.on("change", function () {
            var selectedOption = jQuery(this).find("option:selected");
            if (!selectedOption.length) {
                return;
            }

            var location = selectedOption.data("location");
            if (!location) {
                return;
            }

            window.location.href = location;
        });

        //navigate when the same option is clicked
        control.find("option").on("mouseup", function () {
            var selectedOption = jQuery(this);
            if (!selectedOption && selectedOption.is("selected")) {
                return;
            }

            var location = selectedOption.data("location");
            if (!location) {
                return;
            }

            window.location.href = location;
        });

        //navigate on enter event
        control.on("keyup", function (e) {
            if (e.keyCode == 13) {
                var selectedOption = jQuery(this).find("option:selected");
                if (!selectedOption.length) {
                    return;
                }

                var location = selectedOption.data("location");
                if (!location) {
                    return;
                }

                window.location.href = location;
            }
        });
    }
}

/**
 * Initializes the tab menu to give the active style to the current page, if given, otherwise the first the first
 * tabe is given the active style
 *
 * @param {string} listId the id of the ul element for this tab menu
 * @param {string=} currentPage
 */
function initTabMenu(listId, currentPage) {
    jQuery(document).ready(function () {
        var $nav = jQuery("#" + listId);
        $nav.find("li").removeClass("active");

        var firstItem = $nav.find("li:first");
        var currentTab = firstItem.find("a");

        if (currentPage) {
            currentTab = $nav.find("a[name='" + currentPage + "']");

        }

        if (currentTab) {
            currentTab.closest("li").addClass("active");
        }
    });
}

/**
 * Setup the sidebar navigation menu scripts, which allow for collapsing, and toggling of sub menus, as well as icon
 * swapping when interacting with these toggles
 *
 * @param id the id of the navigation group
 * @param openedToggleIconClass the icon to use when a sub toggle menu is open
 * @param closedToggleIconClass the icon to use when a sub toggle menu is closed
 */
function setupSidebarNavMenu(id, openedToggleIconClass, closedToggleIconClass) {
    var navMenu = jQuery("#" + id);
    var viewContent = jQuery("#" + kradVariables.VIEW_CONTENT_WRAPPER);

    adjustPageLeftMargin();
    viewContent.on(kradVariables.EVENTS.ADJUST_PAGE_MARGIN, function () {
        adjustPageLeftMargin();
    });

    // TODO Unsure if the following line is needed:
    jQuery(".show-popover").popover();

    // Animation and icon swapping handler for the sub toggle menus
    jQuery("a.dropdown-toggle", navMenu).click(function () {
        var subMenu = jQuery(this).next(".submenu");
        var icon = jQuery(this).children("." + kradVariables.TOGGLE_ARROW_CLASS);
        if (icon.hasClass(closedToggleIconClass)) {
            icon.addClass("anim-turn90");
        } else {
            icon.addClass("anim-turn-90");
        }
        subMenu.slideToggle(400, function () {
            if (jQuery(this).is(":hidden")) {
                icon.attr("class", kradVariables.TOGGLE_ARROW_CLASS + " " + closedToggleIconClass);
            } else {
                icon.attr("class", kradVariables.TOGGLE_ARROW_CLASS + " " + openedToggleIconClass);
            }
            icon.removeClass("anim-turn90").removeClass("anim-turn-90");
        });
    });

    // If menu is already collapsed, show appropriate icon
    jQuery("#" + id + "." + kradVariables.MENU_COLLAPSED
            + "." + kradVariables.MENU_COLLAPSE_ACTION + " > span").attr("class", kradVariables.MENU_COLLAPSE_ICON_RIGHT);

    // Collapsing handler for when the menu collapse is clicked, swaps icon, classes, and page margin
    jQuery("." + kradVariables.MENU_COLLAPSE_ACTION).click(function () {
        jQuery("#" + id).toggleClass(kradVariables.MENU_COLLAPSED);
        var menuWidth = jQuery("#" + id).outerWidth(true);
        jQuery("[data-role='Page']").css("margin-left", menuWidth);
        if (jQuery("#" + id).hasClass(kradVariables.MENU_COLLAPSED)) {
            jQuery("." + kradVariables.MENU_COLLAPSE_ACTION + " > span").attr("class", kradVariables.MENU_COLLAPSE_ICON_RIGHT);
            jQuery.cookie(kradVariables.MENU_COLLAPSED, "true");
        } else {
            jQuery("." + kradVariables.MENU_COLLAPSE_ACTION + " > span").attr("class", kradVariables.MENU_COLLAPSE_ICON_LEFT);
            jQuery.cookie(kradVariables.MENU_COLLAPSED, "false");
        }
    });

    // Setup event that can be fired to open the menu
    jQuery("#" + id).on("show.bs.collapse", function () {
        if (jQuery(this).hasClass(kradVariables.MENU_COLLAPSED)) {
            jQuery(this).removeClass(kradVariables.MENU_COLLAPSED);
        }
    });

    // Mark the current active link
    markActiveMenuLink();

    // Add open toggleClass if the item is active
    jQuery(".nav > li." + kradVariables.ACTIVE_CLASS + " > a > ." + kradVariables.TOGGLE_ARROW_CLASS,
            navMenu).removeClass(closedToggleIconClass).addClass(openedToggleIconClass);
}

function adjustPageLeftMargin() {
    var page = jQuery("[data-role='Page']");
    var menuWidth = jQuery("#Uif-Navigation >").outerWidth(true);
    page.css("margin-left", menuWidth);
    page.addClass("uif-hasLeftNav");
}

/**
 * Mark the menu link that is considered to be active for the current page
 */
function markActiveMenuLink() {
    // Clear current active
    jQuery("#" + kradVariables.NAVIGATION_ID + " li." + kradVariables.ACTIVE_CLASS).removeClass(kradVariables.ACTIVE_CLASS);

    // Select active
    var pageId = getCurrentPageId();
    var liParents = jQuery("a[name='" + pageId + "']").parents("li");
    liParents.addClass(kradVariables.ACTIVE_CLASS);
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
 */
function setupTextPopout(id, label, summary, constraint, readOnly) {
    var options = {label: label, summary: summary, constraint: constraint, readOnly: readOnly};
    jQuery("#" + id).initPopoutText(options);
}

/**
 * Check if the code is inside a lightbox
 *
 * @return true if called within a lightbox, false otherwise
 */
function isCalledWithinDialog() {
    var isRenderedInDialog = jQuery("input[name='" + kradVariables.RENDERED_IN_DIALOG + "']").val();
    if (isRenderedInDialog == undefined) {
        return false;
    }

    return isRenderedInDialog.toUpperCase() == 'TRUE' || isRenderedInDialog.toUpperCase() == 'YES';
    // reverting for KULRICE-8346
//    try {
//        // For security reasons the browsers will not allow cross server scripts and
//        // throw an exception instead.
//        // Note that bad browsers (e.g. google chrome) will not catch the exception
//        if (jQuery("#fancybox-frame", parent.document).length) {
//            return true;
//        }
//    }
//    catch (e) {
//        // ignoring error
//    }
//
//    return false;
}

/**
 * Shows the direct inquiry dialog
 *
 * <p>Function that is called from the onclick event on the direct inquiry.
 * The parameters is added by dynamically getting the values
 * for the fields in the parameter maps and then added to the url string.</p>
 *
 * @param url the base url to use to call the inquiry
 * @param paramMap array of field parameters for the inquiry
 * @param showInDialog flag to indicate if it must be shown in a dialog
 * @param dialogId(optional) id of dialog to use, if not set Uif-DialogGroup-Iframe will be used
 */
function showDirectInquiry(url, paramMap, showInDialog, dialogId) {
    var parameterPairs = paramMap.split(",");
    var queryString = "";

    for (i in parameterPairs) {
        var parameters = parameterPairs[i].split(":");
        var value = checkDirectInquiryValueValid(jQuery('[name="' + escapeName(parameters[0]) + '"]').val());
        if (!value) {
            alert(getMessage(kradVariables.MESSAGE_PLEASE_ENTER_VALUE));
            return false;
        } else {
            queryString = queryString + "&" + parameters[1] + "=" + value;
        }
    }

    if (showInDialog) {
        // Check if this is called within a light box
        if (!getContext().find('.fancybox-inner', parent.document).length) {

            queryString = queryString + "&flow=start&renderedInDialog=true";
            url = url + queryString;
            openIframeDialog(url, dialogId);
        } else {
            // If this is already in a lightbox just open in current lightbox
            queryString = queryString + "&flow="
                    + jQuery("input[name='" + kradVariables.FLOW_KEY + "']").val() + "&renderedInDialog=true";
            window.open(url + queryString, "_self");
        }
    } else {
        window.open(url + queryString, "_blank", "width=640, height=600, scrollbars=yes");
    }
}

/**
 * Removes wildcards and check for empty values
 *
 * @param value - value without wildcards or false if empty
 */
function checkDirectInquiryValueValid(value) {
    value = value.replace(/\*/g, '');
    if (value == "") {
        return false;
    }
    return value;
}

/**
 * Cleanup form data from server when lightbox window is closed
 */
function cleanupClosedLightboxForms() {
    if (jQuery("input[name='" + kradVariables.FORM_KEY + "']").length) {
        // get the formKey of the lightbox (fancybox)
        var context = getContext();
        var formKey = context('iframe.fancybox-iframe').contents().find("input[name='" + kradVariables.FORM_KEY + "']").val();

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
function createDatePicker(controlId, options, disabled) {
    var fieldId = jQuery("#" + controlId).closest("div[data-role='InputField']").attr("id");
    jQuery(function () {
        var datePickerControl = jQuery("#" + controlId);
        datePickerControl.datepicker(options);
        datePickerControl.datepicker('option', 'onClose',
                function () {
                    getValidationData(jQuery("#" + fieldId)).messagingEnabled = true;
                    jQuery(this).trigger("focusout");
                    jQuery(this).trigger("focus");
                });
        datePickerControl.datepicker('option', 'beforeShow',
                function () {
                    getValidationData(jQuery("#" + fieldId)).messagingEnabled = false;
                });

        //KULRICE-7310 can't change only month or year with picker (jquery limitation)
        datePickerControl.datepicker('option', 'onChangeMonthYear',
                function (y, m, i) {
                    var d = i.selectedDay;
                    jQuery(this).datepicker('setDate', new Date(y, m - 1, d));
                });

        //KULRICE-7261 fix date format passed back.  jquery expecting mm-dd-yy
        if (options.dateFormat == "mm-dd-yy" && datePickerControl[0].getAttribute("value").indexOf("/") != -1) {
            datePickerControl.datepicker('setDate', new Date(datePickerControl[0].getAttribute("value")));
        }
        if (disabled === true) {
            datePickerControl.datepicker('disable');
            datePickerControl.next(".ui-datepicker-trigger").css("cursor", "not-allowed");
        }
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
 * @param collapsedIconClass -
 *          class for the icon that is displayed with the group is collapsed
 * @param expandedIconClass -
 *          class for the icon that is displayed with the group is expanded
 * @param animationSpeed -
 *          speed at which the group should be expanded or collapsed
 * @param renderIcon -
 *          boolean that indicates whether the expanded or collapsed icon should be rendered
 * @param ajaxRetrieval -
 *          boolean that indicates whether the disclosure group should be retrieved when open
 */
function createDisclosure(groupId, headerId, widgetId, defaultOpen, collapsedIconClass, expandedIconClass, animationSpeed, renderIcon, ajaxRetrieval) {
    jQuery(document).ready(function () {
        var groupToggleLinkId = groupId + kradVariables.ID_SUFFIX.DISCLOSURE_TOGGLE;

        var expandedIcon = "";
        var collapsedIcon = "";
        if (renderIcon && defaultOpen) {
            expandedIcon = "<span id='" + groupToggleLinkId + "_exp" + "' class='" + expandedIconClass + "'></span>";
            collapsedIcon = "<span style='display:none;' id='" + groupToggleLinkId + "_col" + "' class='" + collapsedIconClass + "'></span>";
        }
        else if (renderIcon && !defaultOpen) {
            collapsedIcon = "<span id='" + groupToggleLinkId + "_col" + "' class='" + collapsedIconClass + "'></span>";
            expandedIcon = "<span style='display:none;' id='" + groupToggleLinkId + "_exp" + "' class='" + expandedIconClass + "'></span>";
        }

        var content = jQuery("#" + groupId + kradVariables.ID_SUFFIX.DISCLOSURE_CONTENT);

        // perform initial open/close and insert toggle link and image
        var headerText = jQuery("#" + headerId).find(".uif-headerText-span:first");
        if (defaultOpen) {
            content.show();

            content.attr(kradVariables.ATTRIBUTES.DATA_OPEN, true);

            headerText.prepend(collapsedIcon);
            headerText.prepend(expandedIcon);
        }
        else {
            content.hide();

            content.attr(kradVariables.ATTRIBUTES.DATA_OPEN, false);

            headerText.prepend(expandedIcon);
            headerText.prepend(collapsedIcon);
        }

        headerText.wrap("<a data-role=" + kradVariables.DATA_ROLES.DISCLOSURE_LINK + " data-linkfor='"
                + content.attr("id") + "' href='#' "
                + "id='" + groupToggleLinkId + "' "
                + "data-open='" + defaultOpen + "' "
                + "data-widgetid='" + widgetId + "' "
                + "data-speed='" + animationSpeed + "' "
                + "data-ajax='" + ajaxRetrieval + "'"
                + "></a>");
    });
}

/**
 * Expands all the disclosure divs on the page
 */
function expandDisclosures() {
    jQuery("a[data-role='" + kradVariables.DATA_ROLES.DISCLOSURE_LINK + "']").each(function () {
        var contentId = jQuery(this).attr("data-linkfor");
        if (jQuery("#" + contentId).attr(kradVariables.ATTRIBUTES.DATA_OPEN) == "false") {
            jQuery(this).click();
        }
    });
}

/**
 * Collapses all the disclosure divs on the page
 */
function collapseDisclosures() {
    jQuery("a[data-role='" + kradVariables.DATA_ROLES.DISCLOSURE_LINK + "']").each(function () {
        var contentId = jQuery(this).attr("data-linkfor");
        if (jQuery("#" + contentId).attr(kradVariables.ATTRIBUTES.DATA_OPEN) == "true") {
            jQuery(this).click();
        }
    });
}

/**
 * Create a multi file upload collection element by invoking the fileUpload plugin
 *
 * @param id the id of the element
 * @param collectionId the id of the contained collection
 * @param additionalOptions options to pass to the plugin
 */
function createMultiFileUploadForCollection(id, collectionId, additionalOptions) {
    var options = {
        dropZone: jQuery("#" + id)
    };
    options = jQuery.extend(options, additionalOptions);

    if (!options.url) {
        options.url = "?" + getUrlQueryString("methodToCall", "fileUpload");
    }

    if (options.acceptFileTypes) {
        options.acceptFileTypes = new RegExp(options.acceptFileTypes);
    }

    var $fileInput = jQuery("#" + id);
    if ($fileInput.length) {
        $fileInput.fileupload(options);

        $fileInput.bind('fileuploadsend', function (e, data) {
            if (!jQuery("#" + id + "_upload").length) {
                var $buttons = jQuery("#" + id + " > .fileupload-buttonbar > div");
                jQuery("<span id='" + id + "_upload' style='display:inline;'/>").text(" Uploading...")
                        .appendTo($buttons);
            }

            data.collectionId = collectionId;
        });

        $fileInput.bind('fileuploaddone', function (e, data) {
            var responseContents = document.createElement('div');
            responseContents.innerHTML = data.result;

            // create a response object to process the response contents
            var kradResponse = new KradResponse(responseContents);
            kradResponse.processResponse();
            jQuery("#" + id + "_upload").remove();
        });

        $fileInput.bind('fileuploadprocessfail', function (e, data) {
            alert('Processing ' + data.files[data.index].name + ' of type ' + data.files[data.index].type + ' failed.\nError: ' + data.files[data.index].error);
        });
    }
}

/**
 * Uses jQuery DataTable plug-in to decorate a table with functionality like
 * sorting and page. The second argument is a Map of options that are available
 * for the plug-in. See <a href=http://www.datatables.net/usage/>datatables</a> for
 * documentation on these options
 *
 * @param tableId id for the table that should be decorated
 * @param additionalOptions map of additional or override option settings (option name/value pairs) for the plugin
 * @param groupingOptions (optional) if supplied, the collection will use rowGrouping with these options
 */
function createTable(tableId, additionalOptions, groupingOptions) {
    jQuery(document).ready(function () {
        var table = jQuery("#" + tableId);

        var detailsOpen = table.parent().data("details_default_open");
        table.data("open", detailsOpen);

        if (groupingOptions) {
            table.attr("data-groups", "true");
        }

        var options = {
            "bDestory": true,
            "bStateSave": true,
            "fnStateSave": function (oSettings, oData) {
                setComponentState(tableId, 'richTableState', oData);
            },
            "fnStateLoad": function (oSettings) {
                var oData = getComponentState(tableId, 'richTableState');

                return oData;
            }
        }

        options = jQuery.extend(options, additionalOptions);

        var hideActionColumnOption = {
            "fnDrawCallback": function (oSettings) {
                hideEmptyActionColumn(tableId, ".uif-collection-column-action");
            }
        }

        options = jQuery.extend(options, hideActionColumnOption);

        var exportOptions = {
            "sDownloadSource": additionalOptions.sDownloadSource,
            "oTableTools": {
                "aButtons": [
                    {
                        "sExtends": "text",
                        "sButtonText": "csv",
                        "fnClick": function (nButton, oConfig) {
                            window.location.href = additionalOptions.sDownloadSource + "&methodToCall=tableCsvRetrieval&formatType=csv";
                        },
                    },
                    {
                        "sExtends": "text",
                        "sButtonText": "xml",
                        "fnClick": function (nButton, oConfig) {
                            window.location.href = additionalOptions.sDownloadSource + "&methodToCall=tableXmlRetrieval&formatType=xml";
                        },
                    },
                    {
                        "sExtends": "text",
                        "sButtonText": "xls",
                        "fnClick": function (nButton, oConfig) {
                            window.location.href = additionalOptions.sDownloadSource + "&methodToCall=tableXlsRetrieval&formatType=xls";
                        },
                    }
                ]
            }
        }

        /// check that the export feature is turned on
        if (options.sDom && options.sDom.search("T") >= 0) {
            options = jQuery.extend(options, exportOptions)
        }

        var oTable = table.dataTable(options);

        //make sure scripts are run after table renders (must be done here for deferred rendering)
        runHiddenScripts(tableId, false, true);

        //insure scripts (if any) are run on each draw, fixes bug with scripts lost when paging after a refresh
        jQuery(oTable).on("dataTables.tableDraw", function (event, tableData) {
            if (event.currentTarget != event.target) {
                return;
            }

            runHiddenScripts(tableId, false, true);
            jQuery("div[data-role='InputField'][data-has_messages='true']", "#" + tableId).each(function () {
                var id = jQuery(this).attr('id');
                var validationData = getValidationData(jQuery("#" + id));

                if (validationData && validationData.hasOwnMessages) {
                    handleMessagesAtField(id);
                }
            });
        });

        //handle row details related functionality setup
        if (detailsOpen != undefined) {
            jQuery(oTable).on("dataTables.tableDraw", function (event, tableData) {
                if (event.currentTarget != event.target) {
                    return;
                }

                if (table.data("open")) {
                    openAllDetails(tableId);
                }
            });

            if (detailsOpen) {
                openAllDetails(tableId, false);
            }
        }

        // allow table column size recalculation on window resize
        jQuery(window).bind('resize', function () {
            // passing false to avoid copious ajax requests during window resize
            oTable.fnAdjustColumnSizing(false);
        });

        if (groupingOptions) {
            oTable.rowGrouping(groupingOptions);
        }

        restoreDetailState(tableId);
    });
}

/**
 * Restores the open/close state of each details section within the table
 *
 * @param tableId the table displayed
 */
function restoreDetailState(tableId) {
    var oTable = getDataTableHandle(tableId);

    if (oTable != null) {
        var rows = jQuery(oTable).find('tr').not(".detailsRow");
        rows.each(function () {
            var row = jQuery(this);
            var detailsElement = jQuery(row.find("[data-open]"));
            if (detailsElement.length === 0) return true;

            var detailsId = jQuery(detailsElement[0]).attr("id");
            var detailState = getComponentState(detailsId, 'open');

            if (detailState != "" && detailState === true) {
                var actionComponent = row.find("a[data-role='detailsLink']");

                openDetails(oTable, row, actionComponent, false);
            }
        });
    }
}

/**
 * Expands a data table row by finding the row that matches the actionComponent passed in, in the
 * dataTable which matches tableId.  If useImages is true, images will be swapped out when the
 * action is clicked.  The row will be closed if already open.
 *
 * @param actionComponent the actionComponent clicked
 * @param tableId the dataTable to expand the clicked row in
 * @param useImages if true, swap open/close images on click
 */
function rowDetailsActionHandler(actionComponent, tableId) {
    var oTable = getDataTableHandle(tableId);

    if (oTable != null) {
        var row = jQuery(actionComponent).parents('tr')[0];
        if (oTable.fnIsOpen(row)) {
            closeDetails(oTable, jQuery(row), actionComponent, true);
        }
        else {
            openDetails(oTable, jQuery(row), actionComponent, true);
        }
        jQuery(row).data("det-interact", true);
    }
}

/**
 * Open all row details in the table specified by id
 *
 * @param tableId id of the table to open all details for
 * @param animate [optional] if true, animate during opening the rows
 * @param forceOpen [optional] if true, force the each row details to open and reset interaction flag, otherwise if the
 * row has been interacted with skip (used to retain state)
 */
function openAllDetails(tableId, animate, forceOpen) {
    var oTable = getDataTableHandle(tableId);

    if (oTable != null) {
        var rows = jQuery(oTable).find('tr').not(".detailsRow");
        rows.each(function () {
            var row = jQuery(this);
            //Means the row is not open and the user has not interacted with it (or force if forceOpen is true)
            //This is done to retain row details "state" between table draws for rows the user may have interacted with
            if (!oTable.fnIsOpen(this) && (!row.data("det-interact") || forceOpen)) {
                var actionComponent = row.find("a[data-role='detailsLink']");

                openDetails(oTable, row, actionComponent, animate);

                //reset user interaction flag
                row.data("det-interact", false);
            }
        });
    }
}

/**
 * Open the row details. If the ajaxRetrieval option is set this will retrieve the detail content.
 *
 * @param oTable the dataTable object handle
 * @param row the row to open details for
 * @param actionComponent [optional] actionComponent used to invoke the action, required if using image swap
 * or ajaxRetrieval
 * @param animate if true, the open will have an animation effect
 */
function openDetails(oTable, row, actionComponent, animate) {
    var detailsGroup = row.find("[data-role='details'], span[data-role='placeholder']").filter(":first");
    var ajaxRetrieval = jQuery(detailsGroup).is("span[data-role='placeholder']");
    var detailsId = jQuery(detailsGroup).attr("id");

    if (actionComponent && jQuery(actionComponent).data("swap") && jQuery(actionComponent).find("img").length) {
        jQuery(actionComponent).find("img").replaceWith(detailsCloseImage.clone());
    }

    var newRow = oTable.fnOpenCustom(row[0], detailsGroup, "uif-rowDetails");
    detailsGroup = jQuery(newRow).find("[data-role='details'], span[data-role='placeholder']").filter(":first");

    detailsGroup.attr("data-open", "true");
    setComponentState(detailsId, 'open', true);

    //make sure scripts are run on the now shown group
    runHiddenScripts(detailsGroup, true, true);

    //show the group
    detailsGroup.show();

    if (ajaxRetrieval) {
        var kradRequest = new KradRequest(jQuery(actionComponent));

        if (!kradRequest.methodToCall) {
            kradRequest.methodToCall = kradVariables.REFRESH_METHOD_TO_CALL;
        }

        kradRequest.successCallback = function () {
            jQuery("#" + detailsId).show();
        };

        kradRequest.ajaxReturnType = kradVariables.RETURN_TYPE_UPDATE_COMPONENT;
        kradRequest.refreshId = detailsId;

        kradRequest.send();
    }
}

/**
 * Toggles column based on visibility indicator
 *
 * Handles headers and footers based on column class while using
 * the footers index placement as the footer column cells do not
 * include the css column class.
 *
 * @param tableId id of the html table element
 * @param columnId css class specific to the table column cells
 * @param bVisibility true to show elements, false to hide elements
 */
function toggleColumnVisibility(tableId, columnId, bVisibility) {
    var oTable = getDataTableHandle(tableId);
    var columnIndex = jQuery(oTable).find('thead th' + columnId).index();
    var header = jQuery(oTable).find('thead th' + columnId);
    var columns = jQuery(oTable).find('tr td:nth-child(' + (columnIndex + 1) + ')');
    var footer = jQuery(oTable).find('tfoot th').eq(columnIndex);
    if (bVisibility) {
        header.show();
        columns.show();
        footer.show();
    } else {
        // hide header, fields, footer
        header.hide();
        columns.hide();
        footer.hide();
    }
}

/**
 * Identifies if there are visible elements in data column.
 *
 * Currently determines visibility for action columns and uses
 * links, inputs, buttons or images as test. Should be expanded
 * later to include divs/spans with text.
 *
 * @param tableId
 * @param columnId
 * @returns {boolean}
 */
function hasVisibleElementsInColumn(tableId, columnId) {
    var oTable = getDataTableHandle(tableId);
    var columnIndex = jQuery(oTable).find('thead th' + columnId).index();
    var columns = jQuery(oTable).find('tr td:nth-child(' + (columnIndex + 1) + ')');
    var isColumnsEmpty = true;

    jQuery.each(columns, function (index, td) {
        var column = jQuery(td);
        var columnContent = column.find("a, img, input[type!='hidden'], button");
        var columnGroup = column.find("> div");
        var columnGroupVisible = true;

        if (columnGroup.css("display") == "none") {
            columnGroupVisible = false;
        }

        columnContent.filter(function () {
            return jQuery(this).css("display") != "none";
        });

        if (columnContent.size() > 0 && columnGroupVisible) {
            isColumnsEmpty = false;

            // break
            return false;
        }
    });

    return !isColumnsEmpty;
}

/**
 * Checks for visible elements in the action column and toggle its
 * display accordingly.
 *
 * @param tableId
 * @param columnId
 */
function hideEmptyActionColumn(tableId, columnId) {
    var bVisibility = hasVisibleElementsInColumn(tableId, columnId);
    toggleColumnVisibility(tableId, columnId, bVisibility);
}

/**
 * Close all row details in the table specified by id
 *
 * @param tableId id of the table to close all details for
 * @param animate [optional] if true, animate during closing the rows
 * @param forceOpen [optional] if true, force the each row details to close and reset interaction flag, otherwise if the
 * row has been interacted with skip (used to retain state)
 */
function closeAllDetails(tableId, animate, forceClose) {
    var oTable = getDataTableHandle(tableId);

    if (oTable != null) {
        var rows = jQuery(oTable).find('tr').not(".detailsRow");
        rows.each(function () {
            var row = jQuery(this);
            //Means the row is open and the user has not interacted with it (or force if forceClose is true)
            //This is done to retain row details "state" between table draws for rows the user may have interacted with
            if (oTable.fnIsOpen(this) && (!row.data("det-interact") || forceClose)) {
                var actionComponent = row.find("a[data-role='detailsLink']");

                closeDetails(oTable, row, actionComponent, animate);

                //reset user interaction flag
                row.data("det-interact", false);
            }
        });
    }
}

/**
 * Close the row details.
 *
 * @param oTable the dataTable object handle
 * @param row the row to close details for
 * @param actionComponent [optional] actionComponent used to invoke the action, required if using image swap
 * @param animate if true, the close will have an animation effect
 */
function closeDetails(oTable, row, actionComponent, animate) {
    var fieldGroupWrapper = row.find("> td > [data-role='detailsFieldGroup']");
    var detailsContent = row.next().first().find("> td > [data-role='details'], "
            + "> td > span[data-role='placeholder']").filter(":first");

    if (actionComponent && jQuery(actionComponent).data("swap") && jQuery(actionComponent).find("img").length) {
        jQuery(actionComponent).find("img").replaceWith(detailsOpenImage.clone());
    }

    detailsContent.attr("data-open", "false");
    setComponentState(jQuery(detailsContent).attr("id"), 'open', false);

    detailsContent.hide();
    fieldGroupWrapper.append(detailsContent.detach());
    oTable.fnClose(row[0]);

}

/**
 * Open or close all rows for the table specified by the actionComponent's "tableid" data attribute
 *
 * @param actionComponent the calling action component
 */
function toggleRowDetails(actionComponent) {
    var action = jQuery(actionComponent);
    var tableId = action.data("tableid");
    var open = action.data("open");
    if (open) {
        closeAllDetails(tableId, true, true);
        action.data("open", false);
        jQuery("#" + tableId).data("open", false);
    }
    else {
        openAllDetails(tableId, true, true);
        action.data("open", true);
        jQuery("#" + tableId).data("open", true);
    }
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

/**
 * Adds a ZeroClipboard flash movie to the copy trigger element which will copy the content of the content element
 * on mousedown. This uses the ZeroClipboard plugin bundled with the Datatables plugin
 *
 * @param componentId - id of the parent component
 * @param copyTriggerId - id of the element that must trigger the copy action
 * @param contentElementId - id of the element that the value must be copied from
 * @param showCopyConfirmation [optional] if supplied and true, a dialog will be triggered displaying the copied value
 * after copy action
 */
function createCopyToClipboard(componentId, copyTriggerId, contentElementId, showCopyConfirmation) {

    // the ZeroClipboard flash movie can only be added to visible elements so must be added on document ready
    jQuery(document).ready(function () {

        // Do not add flash to hidden syntax highlighters as this causes exception
        if (jQuery("#" + componentId).is(':visible')) {

            // setup new client for this
            //KULRICE-10007 swf file needs to be unique in order to avoid caching.
            var d = new Date();
            ZeroClipboard.setMoviePath(getConfigParam(kradVariables.APPLICATION_URL)
                    + '/plugins/datatables/copy_cvs_xls_pdf.swf?bogus=' + d.getTime());
            var clip = new ZeroClipboard.Client();

            // copy text on mousedown
            clip.addEventListener('mousedown', function (client) {
                clip.setText(jQuery("#" + contentElementId).text());
            });

            // show dialog
            if (showCopyConfirmation) {
                clip.addEventListener('complete', function (client, text) {
                    alert('Copied to Clipboard :\n\n' + text);
                });
            }

            // the element needs to be visible when adding the flah movie to it
            // just reset the display css on the element after showing it
            jQuery('#' + copyTriggerId).show();
            clip.glue(jQuery('#' + copyTriggerId).get(0));
            jQuery('#' + copyTriggerId).css("display", "");
        }

    });
}

function createAccordion(id, options, active) {
    if (active == false) {
        active = "false";
    }

    options = options || {};
    options = jQuery.extend({
        active: active,
        heightStyle: "content",
        collapsible: true
    }, options);

    jQuery("#" + id + " > ul").accordion(options);
    //jQuery("#id > ul").accordion("option", "active", active);
}

/**
 * Uses jQuery UI Auto-complete widget to provide suggest options for the given field. See
 * <link>http://jqueryui.com/demos/autocomplete/</link> for documentation on this widget
 *
 * @param controlId id for the html control the autocomplete will be enabled for
 * @param options map of option settings (option name/value pairs) for the widget
 * @param queryFieldId id for the attribute field the control belongs to, used when making the
 * request to execute the associated attribute query
 * @param queryParameters map of parameters that should be sent along with the query. map key gives
 * @param localSource indicates whether the suggest options will be provided locally instead of by
 * a query
 * @param suggestOptions when localSource is set to true provides the suggest options
 * the name of the parameter to send, and the value gives the name of the field to pull the value from
 * @param labelProp the property name that holds the label for the plugin
 * @param valueProp the property name that holds the value for the plugin
 * @param returnCustomObj if true, the full object is expected as return value
 */
function createSuggest(controlId, options, queryFieldId, queryParameters, localSource, suggestOptions, labelProp, valueProp, returnCustomObj) {
    if (localSource) {
        options.source = suggestOptions;
    }
    else {

        options.source = function (request, response) {
            var successFunction = function (data) {
                response(data.resultData);
            };

            //special success logic for the object return case with label/value props specified
            if (returnCustomObj && (labelProp || valueProp)) {
                successFunction = function (data) {
                    var isObject = false;

                    if (data.resultData && data.resultData.length && data.resultData[0]) {
                        isObject = (data.resultData[0].constructor === Object);
                    }

                    if (data.resultData && data.resultData.length && isObject) {
                        //find and match props, and set them into each object so the autocomplete plugin can read them
                        jQuery.each(data.resultData, function (index, object) {
                            if (labelProp && object[labelProp]) {
                                object.label = object[labelProp];
                            }

                            if (valueProp && object[valueProp]) {
                                object.value = object[valueProp];
                            }
                        });
                        response(data.resultData);
                    }
                    else {
                        response(data.resultData);
                    }

                };
            }

            var queryData = {};

            queryData.methodToCall = 'performFieldSuggest';
            queryData.ajaxRequest = true;
            queryData.ajaxReturnType = 'update-none';
            queryData.formKey = jQuery("input[name='" + kradVariables.FORM_KEY + "']").val();
            queryData.queryTerm = request.term;
            queryData.queryFieldId = queryFieldId;

            //If no queryTerm, exit, onBlur event has been fired with no content in the field
            if (queryData.queryTerm === '') {
                return;
            }

            for (var parameter in queryParameters) {
                queryData['queryParameters.' + parameter] = coerceValue(queryParameters[parameter]);
            }

            jQuery.ajax({
                url: jQuery("form#kualiForm").attr("action"),
                dataType: "json",
                beforeSend: null,
                complete: null,
                error: null,
                data: queryData,
                success: successFunction
            });
        };
    }

    jQuery(document).ready(function () {
        jQuery("#" + controlId).autocomplete(options);
    });
}

/**
 * Create a locationSuggest which overrides the select method with one that will navigate the user based on url
 * settings
 *
 * @param baseUrl baseUrl of the urls being built
 * @param hrefProperty href url property name - if found use this always (do not build url)
 * @param addUrlProperty additional url appendage property name for built urls
 * @param requestParamNamesObj obj containing key/propertyName pairs for requestParameters on a built url
 * @param requestParameterString static request parameter string to append
 * @param controlId id for the html control the autocomplete will be enabled for
 * @param options map of option settings (option name/value pairs) for the widget
 * @param queryFieldId id for the attribute field the control belongs to, used when making the
 * request to execute the associated attribute query
 * @param queryParameters map of parameters that should be sent along with the query. map key gives
 * @param localSource indicates whether the suggest options will be provided locally instead of by
 * a query
 * @param suggestOptions when localSource is set to true provides the suggest options
 * the name of the parameter to send, and the value gives the name of the field to pull the value from
 * @param labelProp the property name that holds the label for the plugin
 * @param valueProp the property name that holds the value for the plugin
 * @param returnCustomObj if true, the full object is expected as return value
 */
function createLocationSuggest(baseUrl, hrefProperty, addUrlProperty, requestParamNamesObj, requestParameterString, controlId, options, queryFieldId, queryParameters, localSource, suggestOptions, labelProp, valueProp, returnCustomObj) {

    var originalFunction = undefined;
    if (options.select != undefined) {
        originalFunction = options.select;
    }

    options.select = function (event, object) {
        var originalFunctionResult = true;
        if (originalFunction) {
            originalFunctionResult = originalFunction();
        }

        if (object && object.item && hrefProperty && object.item[hrefProperty]) {
            window.location.href = object.item[hrefProperty];
        }
        else if (object && object.item && baseUrl) {
            var builtUrl = baseUrl;

            if (addUrlProperty && object.item[addUrlProperty]) {
                builtUrl = builtUrl + object.item[addUrlProperty];
            }

            var addParams = "";
            if (requestParamNamesObj) {
                jQuery.each(requestParamNamesObj, function (key, propName) {
                    if (object.item[propName]) {
                        addParams = addParams + "&" + key + "=" + object.item[propName];
                    }
                });
            }

            if (requestParameterString) {
                builtUrl = builtUrl + requestParameterString + addParams;
            }
            else if (addParams) {
                builtUrl = builtUrl + "?" + addParams.substr(1, addParams.length);
            }

            window.location.href = builtUrl;
        }

        return originalFunctionResult;
    };

    createSuggest(controlId, options, queryFieldId, queryParameters, localSource, suggestOptions,
            labelProp, valueProp, returnCustomObj);
}

/**
 * Creates the spinner widget for an input
 *
 * @param id - id for the control to apply the spinner to
 * @param options - options for the spinner
 */
function createSpinner(id, options) {
    jQuery("#" + id).spinner(options);
}

/**
 * Creates the tooltip widget for an component
 *
 * @param id - id for the component to apply the tooltip to
 * @param options - options for the tooltip
 */
function createTooltip(id, text, options, onMouseHoverFlag, onFocusFlag) {
    //var elementInfo = getHoverElement(id);
    //var element = elementInfo.element;
    var tooltipElement = jQuery("#" + id);

    if (tooltipElement.is("header")) {
        var innerHeaderSpan = tooltipElement.find("#" + id + "_header > .uif-headerText-span");
        if (innerHeaderSpan.length == 1) {
            tooltipElement = innerHeaderSpan;
            options.container = jQuery("#" + id);
        }
    }

    options.content = text;

    if (onFocusFlag) {

        // Add onfocus trigger
        tooltipElement.focus(function () {
            if (!isControlWithMessages(id)) {
                var tooltipElement = jQuery(this);
                var popoverData = tooltipElement.data(kradVariables.POPOVER_DATA);
                if (!popoverData) {
                    popoverData = initializeTooltip(tooltipElement, options);
                }

                if (!popoverData.shown) {
                    popoverData.options.content = text;
                    tooltipElement.popover("show");
                    popoverData.shown = true;
                }
            }
        });

        tooltipElement.blur(function () {
            if (!isControlWithMessages(id)) {
                var tooltipElement = jQuery(this);
                var popoverData = tooltipElement.data(kradVariables.POPOVER_DATA);

                if (popoverData && popoverData.shown) {
                    tooltipElement.popover("hide");
                    popoverData.shown = false;
                }
            }
        });
    }
    if (onMouseHoverFlag) {

        tooltipElement.on("mouseover", function () {
            if (!isControlWithMessages(id)) {
                var tooltipElement = jQuery(this);
                var popoverData = tooltipElement.data(kradVariables.POPOVER_DATA);
                if (!popoverData) {
                    popoverData = initializeTooltip(tooltipElement, options);
                }

                if (!popoverData.shown) {
                    popoverData.options.content = text;
                    tooltipElement.popover("show");
                    popoverData.shown = true;
                }
            }
        });

        tooltipElement.on("mouseout", function () {
            if (!isControlWithMessages(id) && !(onFocusFlag && jQuery("#" + id).is(":focus"))) {
                var tooltipElement = jQuery(this);
                var popoverData = tooltipElement.data(kradVariables.POPOVER_DATA);

                if (popoverData && popoverData.shown) {
                    tooltipElement.popover("hide");
                    popoverData.shown = false;
                }
            }
        });
    }
}

function initializeTooltip(tooltipElement, extendedOptions, additionalClasses) {
    var classAttr = "popover";
    if (additionalClasses) {
        classAttr = classAttr + " " + additionalClasses;
    }
    var options = {
        trigger: "manual",
        placement: "auto top",
        html: true,
        animation: false,
        template: '<div class="' + classAttr + '"><div class="arrow"></div><div class="popover-content"></div></div>'
    };

    if (extendedOptions) {
        jQuery.extend(options, extendedOptions);
    }

    tooltipElement.popover(options);
    tooltipElement.attr("data-hasTooltip", "true");

    return tooltipElement.data(kradVariables.POPOVER_DATA);
}

/**
 * Checks if the component is a control or contains a control that contains validation messages
 *
 * @param id the id of the field
 */
function isControlWithMessages(id) {
    // check if component is or contains a control
    if (jQuery("#" + id).is("[data-role='Control']")
            || (jQuery("#" + id).is("[data-role='InputField']") && jQuery("#" + id + "_control").is("[data-role='Control']"))) {
        return hasMessage(id);
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
    var messageData = getValidationData(jQuery("#" + fieldId));
    if (messageData && (messageData.serverErrors.length || (messageData.errors && messageData.errors.length)
            || messageData.serverWarnings.length || (messageData.warnings && messageData.warnings.length)
            || messageData.serverInfo.length || (messageData.info && messageData.info.length))) {
        return true;
    }
    return false;
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
    var data = getValidationData(jQuery("#" + fieldId));
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
                total: '13px',
                difference: '0px'
            };
        }
    }
    else if (hasFieldset && jQuery("#" + fieldId).find("fieldset > span > input").length) {
        //radio and checkbox fieldset case
        //get the fieldset, the inputs its associated with, and the associated labels as hover elements
        elementInfo.element = jQuery("#" + fieldId).find("fieldset, fieldset input, fieldset label");
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
 * as a query parameter on the request (this will only be used by the js if a queryParameters mapping does not exist)
 * @param returnFieldMapping -
 *        map of fields that should be returned (updated) from the query. map key gives
 * the name of the parameter to update, map value is the name of field to pull value from
 */
function executeFieldQuery(controlId, queryFieldId, queryParameters, queryMethodArgs, returnFieldMapping) {
    var queryData = {};

    queryData.methodToCall = 'performFieldQuery';
    queryData.ajaxRequest = true;
    queryData.ajaxReturnType = 'update-none';
    queryData.formKey = jQuery("input[name='" + kradVariables.FORM_KEY + "']").val();
    queryData.queryFieldId = queryFieldId;

    var queryParamLength = 0;
    for (var parameter in queryParameters) {
        queryData['queryParameters.' + queryParameters[parameter]] = coerceValue(parameter);
        queryParamLength++;
    }

    if (queryParamLength === 0) {
        for (var parameter in queryMethodArgs) {
            queryData['queryParameters.' + queryMethodArgs[parameter]] = coerceValue(parameter);
        }
    }

    var submitOptions = {
        url: jQuery("form#kualiForm").attr("action"),
        dataType: "json",
        data: queryData,
        beforeSend: null,
        complete: null,
        error: null,
        success: function (data) {
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
                var control = jQuery("[name='" + escapeName(returnField) + "']");
                if (control.length > 0) {
                    setValue(returnField, fieldValue);
                    control.change();
                }

                // check for info spans
                var returnFieldId = returnField.replace(/\./g, "_")
                        .replace(/\[/g, "-lbrak-")
                        .replace(/\]/g, "-rbrak-")
                        .replace(/\'/g, "-quot-");
                var infoFieldSpan = jQuery("#" + queryFieldId + "_info_" + returnFieldId);
                if (infoFieldSpan.length > 0) {
                    infoFieldSpan.html(fieldValue);
                }
            }
        }
    };

    jQuery.ajax(submitOptions);
}
