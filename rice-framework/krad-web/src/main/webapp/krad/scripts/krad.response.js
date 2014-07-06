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
 * Processes a response that has been returned from an Ajax call
 *
 * @param contents - the response contents (or body)
 */
function KradResponse(contents) {
    this.responseContents = contents;
}

KradResponse.prototype = {
    // full response contents
    responseContents: null,

    // maps return types to handler function names
    handlerMapping: {"update-form": "updateFormHandler", "update-page": "updatePageHandler", "update-component": "updateComponentHandler",
        "update-view": "updateViewHandler", "redirect": "redirectHandler",
        "display-lightbox": "displayLightBoxHandler", "update-dialog":"updateDialogHandler"},

    // invoked to process the response contents by invoking necessary handlers
    processResponse: function () {
        var responseFn = this;

        // iterate over returned contents divs and invoke handler
        jQuery(this.responseContents).children().each(function () {
            var div = jQuery(this);

            // get the return type sent by the server
            var returnType = div.data("returntype");

            // find the handler function from the mapping
            var functionName = responseFn.handlerMapping[returnType];
            var handlerFunc = responseFn[functionName];

            // invoke the handler function
            if (handlerFunc) {
                handlerFunc(div, div.data());
            }

            hideEmptyCells();
        });
    },

    // finds the page content in the returned content and updates the page, then processes breadcrumbs and hidden
    // scripts. While processing, the page contents are hidden
    updatePageHandler: function (content, dataAttr) {
        var pageUpdate = jQuery("#page_update", content);
        var page = jQuery("[data-role='Page']", pageUpdate);
        var viewContent = jQuery("#" + kradVariables.VIEW_CONTENT_WRAPPER);

        page.hide();

        // give a selector that will avoid the temporary iframe used to hold ajax responses by the jquery form plugin
        var pageInLayout = "#" + kradVariables.VIEW_CONTENT_WRAPPER + " [data-role='Page']:first";
        hideTooltips(pageInLayout);

        var $pageInLayout = jQuery(pageInLayout);

        // update page contents from response
        viewContent.find("[data-for='" + $pageInLayout.attr("id") + "']").remove();
        $pageInLayout.replaceWith(pageUpdate.find(">*"));
        $pageInLayout = jQuery(pageInLayout);

        pageValidatorReady = false;
        runHiddenScripts(kradVariables.VIEW_CONTENT_WRAPPER, false, true);

        markActiveMenuLink();

        viewContent.trigger(kradVariables.EVENTS.ADJUST_PAGE_MARGIN);
        $pageInLayout.trigger(kradVariables.EVENTS.UPDATE_CONTENT);

        $pageInLayout.show();

        $pageInLayout.trigger(kradVariables.EVENTS.ADJUST_STICKY);
        $pageInLayout.trigger(kradVariables.EVENTS.PAGE_UPDATE_COMPLETE);

        // Perform focus and jumpTo based on the data attributes
        performFocusAndJumpTo(true, page.data(kradVariables.FOCUS_ID), page.data(kradVariables.JUMP_TO_ID), page.data(kradVariables.JUMP_TO_NAME) );
    },


    // finds the dialog content in the returned content and updates the view
    updateDialogHandler: function (content, dataAttr) {
        var id = dataAttr.updatecomponentid;
        var component = jQuery("#" + id + "_update", content);

        // remove old stuff
        if (jQuery("#" + id + "_errors").length) {
            jQuery("#" + id + "_errors").remove();
        }

        jQuery("input[data-for='" + id + "']").each(function () {
            jQuery(this).remove();
        });

        // replace component
        var $dialog = jQuery("#" + id);
        if ($dialog.length) {
            $dialog.replaceWith(component.html());
        }
        else {
            jQuery('#' + kradVariables.IDS.DIALOGS).append(component.html());
        }

        $dialog.trigger(kradVariables.EVENTS.UPDATE_CONTENT);

        runHiddenScripts(id);
    },


    // retrieves the component with the matching id from the server and replaces a matching
    // _refreshWrapper marker span with the same id with the result.  In addition, if the result contains a label
    // and a displayWith marker span has a matching id, that span will be replaced with the label content
    // and removed from the component.  This allows for label and component content separation on fields
    updateComponentHandler: function (content, dataAttr) {
        var id = dataAttr.id;

        var $componentInDom = jQuery("#" + id);

        hideTooltips($componentInDom);

        var component = jQuery("#" + id + "_update", content);

        // special label handling, if any
        var theLabel = jQuery("[data-label_for='" + id + "']", component);
        if (jQuery(".displayWith-" + id).length && theLabel.length) {
            theLabel.addClass("displayWith-" + id);
            jQuery("span.displayWith-" + id).replaceWith(theLabel);

            component.remove("[data-label_for='" + id + "']");
        }

        // remove old stuff
        if (jQuery("#" + id + "_errors").length) {
            jQuery("#" + id + "_errors").remove();
        }

        jQuery("input[data-for='" + id + "']").each(function () {
            jQuery(this).remove();
        });

        // replace component
        if ($componentInDom.length) {
            var wasPlaceholder = $componentInDom.hasClass(kradVariables.CLASSES.PLACEHOLDER);

            var componentContent = component.html();

            // for modal content update, we just want to replace the contents within the component
            var displayedModal = isDisplayedModal($componentInDom);
            if (displayedModal) {
                var innerComponentContent = jQuery(componentContent).html();
                $componentInDom.html(innerComponentContent);
            }
            else {
                $componentInDom.replaceWith(componentContent);
            }

            $componentInDom = jQuery("#" + id);

            if ($componentInDom.parent().is("td")) {
                $componentInDom.parent().show();
            }

            var displayWithLabel = jQuery(".displayWith-" + id);
            displayWithLabel.show();
            if (displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")) {
                displayWithLabel.parent().show();
            }

            // assume this content is open if being refreshed
            var open = $componentInDom.attr("data-open");
            if (open !== undefined && open === "false") {
                $componentInDom.attr("data-open", "true");
                $componentInDom.show();
            }

            // runs scripts on the span or div with id
            runHiddenScripts(id);

            // Only for table layout collections. Keeps collection on same page.
            var currentPage = retrieveFromSession(id + ":currentPageRichTable");
            if (currentPage != null) {
                openDataTablePage(id, currentPage);
            }

            $componentInDom.unblock({onUnblock: function () {
                var isDialog = $componentInDom.hasClass(kradVariables.CLASSES.MODAL);

                // if this is the first time the content is being shown, and it is not a dialog, add highlighting
                if (wasPlaceholder && !isDialog) {
                    $componentInDom.addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);
                    $componentInDom.animate({backgroundColor: "transparent"}, 6000);
                }
              }
            });

            $componentInDom.trigger(kradVariables.EVENTS.ADJUST_STICKY);
            $componentInDom.trigger(kradVariables.EVENTS.UPDATE_CONTENT);

            // Perform focus and jumpTo based on the data attributes
            performFocusAndJumpTo(true, $componentInDom.data(kradVariables.FOCUS_ID), $componentInDom.data(kradVariables.JUMP_TO_ID), $componentInDom.data(kradVariables.JUMP_TO_NAME) );
        }
    },

    // performs a redirect to the URL found in the returned contents
    redirectHandler: function (content, dataAttr) {
        // get url contents between div
        var redirectUrl = jQuery(content).text().trim();

        // don't check dirty state on a simple refresh (old url starts with the new one's url text)
        if (redirectUrl.indexOf("performDirtyCheck=false") > -1) {
            dirtyFormState.skipDirtyChecks = true;
        }

        // redirect
        window.location.href = redirectUrl;
    },

    // replaces the view with the given content and run the hidden scripts
    updateViewHandler: function (content, dataAttr) {
        var app = jQuery("#" + kradVariables.APP_ID);
        app.hide();

        var update = jQuery(content);

        var appHeaderUpdate = update.find("#" + kradVariables.APPLICATION_HEADER_WRAPPER);
        app.find("#" + kradVariables.APPLICATION_HEADER_WRAPPER).replaceWith(appHeaderUpdate);

        var kualiForm = app.find("#kualiForm");
        var kualiFormReplacement = update.find("#kualiForm");
        var view = app.find("[data-role='View']");
        var viewUpdate = update.find("[data-role='View']");

        if(kualiForm.length && kualiFormReplacement) {
            kualiForm.replaceWith(kualiFormReplacement);
        }
        else if (kualiForm.length && !kualiFormReplacement.length){
            kualiForm.replaceWith(viewUpdate);
        }
        else if (!kualiForm.length && kualiFormReplacement.length) {
            view.replaceWith(kualiFormReplacement);
        }
        else {
            view.replaceWith(viewUpdate);
        }

        var appFooterUpdate = update.find("#" + kradVariables.APPLICATION_FOOTER_WRAPPER);
        app.find("#" + kradVariables.APPLICATION_FOOTER_WRAPPER).replaceWith(appFooterUpdate);

        app.show();
        setupStickyHeaderAndFooter();
        runHiddenScripts(kradVariables.APP_ID);

        view.trigger(kradVariables.EVENTS.UPDATE_CONTENT);
    },

    // displays the response contents in a lightbox
    displayLightBoxHandler: function (content, dataAttr) {
        showLightboxContent(content);
    },

    // replaces the form action with the given content
    updateFormHandler: function (content, dataAttr) {
        var action = jQuery(content).html();

        jQuery("form#" + kradVariables.KUALI_FORM).attr('action', jQuery.trim(action));
    }
}