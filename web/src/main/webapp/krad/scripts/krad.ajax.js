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
 * Submits the form via ajax or does a normal form submit depending on whether the form was submitted via ajax or not.
 * By default ajaxsubmit is true.
 *
 * @param component
 */
function actionInvokeHandler(component) {
    // Read the data attributes. All simple data attributes are lower-cased.
    var ajaxSubmit = jQuery(component).data("ajaxsubmit");
    var submitData = jQuery(component).data("submitData");
    var successCallback = jQuery(component).data("successcallback");
    var elementToBlock = jQuery(component).data("elementtoblock");
    var errorCallback = jQuery(component).data("errorcallback");
    var preSubmitCall = jQuery(component).data("presubmitcall");
    var validate = jQuery(component).data("validate");

    // methodToCall comes as a part of submitData
    var methodToCall = submitData['methodToCall'];

    if (ajaxSubmit) {
        if (validate) {
            validateAndAjaxSubmitForm(methodToCall, successCallback, submitData, elementToBlock, preSubmitCall, null);
        } else {
            ajaxSubmitForm(methodToCall, successCallback, submitData, elementToBlock, preSubmitCall, null);
        }
    } else {
        if (validate) {
            validateAndSubmitForm(methodToCall, submitData, preSubmitCall)
        } else {
            submitForm(methodToCall, submitData, preSubmitCall);
        }
    }
}
/**
 * Invokes ajaxSubmitFormFullOpts with null callbacks besides success and validate set to false
 *
 * @param methodToCall
 * @param successCallback
 * @param additionalData
 * @param elementToBlock
 * @param preSubmitCall
 * @param returnType
 */
function ajaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock, preSubmitCall, returnType) {
    ajaxSubmitFormFullOpts(methodToCall, successCallback, additionalData, elementToBlock, null, false, preSubmitCall, returnType);
}

/**
 * Invokes ajaxSubmitFormFullOpts with null callbacks besides success and validate set to true
 *
 * @param methodToCall
 * @param successCallback
 * @param additionalData
 * @param elementToBlock
 * @param preSubmitCall
 * @param returnType
 */
function validateAndAjaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock, preSubmitCall, returnType) {
    ajaxSubmitFormFullOpts(methodToCall, successCallback, additionalData, elementToBlock, null, true, preSubmitCall, returnType);
}

/**
 * Submits the form through an ajax submit, the response is the new page html
 * runs all hidden scripts passed back (this is to get around a bug with premature script evaluation)
 *
 * <p>
 * If a form has the properties enctype or encoding set to multipart/form-data, an iframe is created
 * to hold the response
 * If the returned response contains scripts that are meant to be run on page load,
 * they will be executed within the iframe since the jquery ready event is triggered
 * </p>
 *
 * <p>
 * For the above reason, the renderFullView below is set to false so that the script content between
 * <head></head> is left out
 * </p>
 *
 * @param methodToCall - the controller method to be called
 * @param successCallback - hook for any calls to be made on success
 * @param additionalData  - any additional data that needs to be passed to the server
 * @param elementToBlock  - element to be blocked while loading
 * @param errorCallback - hook for any calls to be made on error.
 * @param preSubmitCall -  hook to execute a call which if returns true the processing moves forward else return
 * @param returnType - this is used to indicate to the server a requested return type. The client requests a return
 *                     type but the server can change it. Defaults to update-page
 */
function ajaxSubmitFormFullOpts(methodToCall, successCallback, additionalData, elementToBlock, errorCallback, validate,
                                preSubmitCall, returnType) {
    var data = {};
    // invoke validateForm if validate flag is true, if returns false do not continue
    if (validate && !validateForm()) {
        return;
    }

    if (preSubmitCall != null && preSubmitCall !== "") {
        if (!eval(preSubmitCall)) {
            return;
        }

    }
    // check to see if methodToCall is still null
    if (methodToCall != null || methodToCall !== "") {
        data.methodToCall = methodToCall;
    }
    // Set the ajaxReturnType. Default to update-page if none provided
    if (returnType != null && returnType !== "") {
        data.ajaxReturnType = returnType;
    } else {
        data.ajaxReturnType = "update-page";
    }

    // Since we are explicitly setting renderFullView to false, we need to remove any input renderFullViewParam
    jQuery("input[name='renderFullView']").remove();
    data.renderFullView = false;

    // Since this method will only be called for an ajax submit set the ajaxRequest to true
    data.ajaxRequest = true;

    // remove this since the methodToCall was passed in or extracted from the page, to avoid issues
    jQuery("input[name='methodToCall']").remove();

    if (additionalData != null) {
        jQuery.extend(data, additionalData);
    }

    var viewState = jQuery(document).data(kradVariables.VIEW_STATE);
    if (!jQuery.isEmptyObject(viewState)) {
        var jsonViewState = jQuery.toJSON(viewState);

        // change double quotes to single because escaping causes problems on URL
        jsonViewState = jsonViewState.replace(/"/g, "'");
        jQuery.extend(data, {clientViewState:jsonViewState});
    }

    // check if called from a lightbox.  if it is set the componentId
    var componentId = undefined;
    if (jQuery('#kualiLightboxForm').children(':first').length == 1) {
        componentId = jQuery('#kualiLightboxForm').children(':first').attr('id');
    }

    var submitOptions = {
        data:data,
        success:function (response) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = response;
            var hasError = handleIncidentReport(response);
            //invoke the invokeAjaxReturnHandler to determine which data handler to use
            invokeAjaxReturnHandler(tempDiv);

            if (!hasError) {
                if(successCallback != null)  {
                    if (typeof successCallback == "string") {
                        eval(successCallback + "(tempDiv)");
                    } else {
                        successCallback(tempDiv);
                    }
                }
            } else if (errorCallback != null) {
                eval(errorCallback + "(tempDiv)");
            }

            jQuery("#formComplete").html("");

            //for lightbox copy data back into lightbox
            if (componentId !== undefined) {
                var component = jQuery('#' + componentId).clone(true, true);
                addIdPrefix(jQuery('#' + componentId), 'tmpForm_');
                jQuery('#tmpLightbox_' + componentId).replaceWith(component);
                jQuery('#' + componentId).css('display', '');
            }

        },
        error:function (jqXHR, textStatus) {
            alert("Request failed: " + textStatus);
        }
    };

    if (elementToBlock != null && elementToBlock.length) {
        var elementBlockingOptions = {
            beforeSend:function () {
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.append('<img src="' + getConfigParam(kradVariables.IMAGE_LOCATION) + 'loader.gif" alt="working..." /> Loading...');
                    elementToBlock.show();
                }
                else {
                    elementToBlock.block({
                        message:'<img src="' + getConfigParam(kradVariables.IMAGE_LOCATION) + 'loader.gif" alt="working..." /> Updating...',
                        fadeIn:400,
                        fadeOut:800
                    });
                }
            },
            complete:function () {
                // note that if you want to unblock simultaneous with showing the new retrieval
                // you must do so in the successCallback
                elementToBlock.unblock();

            },
            error:function () {
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.hide();
                }
                else {
                    elementToBlock.unblock();
                }
            }
        };
    }

    //for lightbox copy data back into form
    if (componentId !== undefined) {
        var component = jQuery('#' + componentId).clone(true, true);
        addIdPrefix(jQuery('#' + componentId), 'tmpLightbox_');
        jQuery('#tmpForm_' + componentId).replaceWith(component);
    }

    jQuery.extend(submitOptions, elementBlockingOptions);
    var form = jQuery("#kualiForm");
    form.ajaxSubmit(submitOptions);
}

/**
 * Calls the submitFormFullOpts with validate set to false
 *
 * @param methodToCall
 * @param additionalData
 * @param preSubmitCall
 */
function submitForm(methodToCall, additionalData, preSubmitCall) {
    // invoke submitFormFullOpts , validate false
    submitFormFullOpts(methodToCall, additionalData, false, preSubmitCall);
}

/**
 * Calls the submitFormFullOpts with validate set to true
 *
 * @param methodToCall
 * @param additionalData
 * @param preSubmitCall
 */
function validateAndSubmitForm(methodToCall, additionalData, preSubmitCall) {
    // invoke submitFormFullOpts with null callback, validate true
    submitFormFullOpts(methodToCall, additionalData, true, preSubmitCall);
}
/**
 * Does a non ajax submit. If validate is set to true then it validates the form before proceeding.
 * If the preSubmitCall is provided then if it evaluates to true, it proceeds else the function returns.
 * The data attributes that are passed in as additional data are written to the form before the form is submitted.
 *
 * @param methodToCall
 * @param additionalData
 * @param validate
 * @param preSubmitCall
 */
function submitFormFullOpts(methodToCall, additionalData, validate, preSubmitCall) {
    // invoke validateForm if validate flag is true, if returns false do not continue
    if (validate && !validateForm()) {
        return;
    }

    // if presubmit call given, invoke. If returns false don't continue
    if (preSubmitCall != null && preSubmitCall !== "") {
        if (!eval(preSubmitCall)) {
            return;
        }
    }
    // write out methodToCall as hidden
    writeHiddenToForm("methodToCall", methodToCall);

    // if additional data write out as hiddens
    for (key in additionalData) {
        writeHiddenToForm(key, additionalData[key]);
    }

    // submit
    jQuery('#kualiForm').submit();
}

function validateForm() {
    jQuery.watermark.hideAll();

    var validForm = true;

    if (validateClient) {
        messageSummariesShown = true;
        pauseTooltipDisplay = true;
        validForm = jQuery("#kualiForm").valid();
        pauseTooltipDisplay = false;
    }

    return validForm;
}

/**
 * Validate form.  When no validation errors exists the form is submitted with the methodToCall of the form.
 * The page is then replaced with the result of the ajax call.
 */
function validateAndSubmitUsingFormMethodToCall() {
    validateAndSubmit(null, updatePageCallback);
}

/**
 * Invoked on success of an ajax call that refreshes the page
 *
 * <p>
 * Finds the page content in the returned content and updates on the page, then processes breadcrumbs and hidden
 * scripts. While processing, the page contents are hidden
 * </p>
 *
 * @param content - content returned from response
 */
function updatePageCallback(content) {
    var page = jQuery("[data-handler='update-component']", content);
    page.hide();

    // give a selector that will avoid the temporary iframe used to hold ajax responses by the jquery form plugin
    var pageInLayout = "#" + kradVariables.VIEW_CONTENT_HEADER_CLASS + " > #" + kradVariables.PAGE_CONTENT_HEADER_CLASS;
    jQuery(pageInLayout).empty().append(page.find(">*"));

    setPageBreadcrumb();

    pageValidatorReady = false;
    runHiddenScripts(jQuery(pageInLayout).attr("id"), false, true);

    jQuery(pageInLayout).show();
}

/**
 * Iterates over the divs in the content and reads the data-handler to
 * obtain the respective handler function to call.
 *
 * @param content - response sent from the server
 */
function invokeAjaxReturnHandler(content) {
    jQuery(content).children().each(function () {
        var div = jQuery(this);

        var handler = div.data("handler");
        var handlerFunc = ajaxReturnHandlers[handler];
        if (handlerFunc) {
            handlerFunc(div, div.data());
        }
    });
}

/**
 * Finds the page content in the returned content and updates the page, then processes breadcrumbs and hidden
 * scripts. While processing, the page contents are hidden
 *
 * @param content - content returned from response
 * @param dataAttr -  any additional data attributes that the server needs to send
 */
function updatePageHandler(content, dataAttr) {
    var page = jQuery("#page_update", content);
    page.hide();

    // give a selector that will avoid the temporary iframe used to hold ajax responses by the jquery form plugin
    var pageInLayout = "#" + kradVariables.VIEW_CONTENT_HEADER_CLASS + " > #" + kradVariables.PAGE_CONTENT_HEADER_CLASS;
    jQuery(pageInLayout).empty().append(page.find(">*"));

    setPageBreadcrumb();

    pageValidatorReady = false;
    runHiddenScripts(jQuery(pageInLayout).attr("id"), false, true);

    jQuery(pageInLayout).show();
}

/**
 * Retrieves the component with the matching id from the server and replaces a matching
 * _refreshWrapper marker span with the same id with the result.  In addition, if the result contains a label
 * and a displayWith marker span has a matching id, that span will be replaced with the label content
 * and removed from the component.  This allows for label and component content seperation on fields
 *
 * @param content - content returned from response
 * @param dataAttr -  any additional data attributes that the server needs to send
 */
function updateComponentHandler(content, dataAttr) {
    var id = dataAttr.updatecomponentid;
    var elementToBlock = jQuery("#" + id);

    var component = jQuery("#" + id + "_update", content);

        var displayWithId = id;

        // special label handling, if any
        var theLabel = jQuery("#" + displayWithId + "_label_span", component);
        if (jQuery(".displayWith-" + displayWithId).length && theLabel.length) {
            theLabel.addClass("displayWith-" + displayWithId);
            jQuery("span.displayWith-" + displayWithId).replaceWith(theLabel);
            component.remove("#" + displayWithId + "_label_span");
        }

        elementToBlock.unblock({onUnblock: function() {
            var origColor = jQuery(component).find("#" + id).css("background-color");
            jQuery(component).find("#" + id).css("background-color", "");
            jQuery(component).find("#" + id).addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);

                // remove old stuff
                if(jQuery("#" + id + "_errors").length){
                    jQuery("#" + id + "_errors").remove();
                }

                jQuery("input[data-for='"+ id +"']").each(function () {
                    jQuery(this).remove();
                });

            // replace component
            if (jQuery("#" + id).length) {
                jQuery("#" + id).replaceWith(component.html());
            }

            if (jQuery("#" + id).parent().is("td")) {
                jQuery("#" + id).parent().show();
            }

            //runs scripts on the span or div with id
            runHiddenScripts(id);

            if (origColor == "") {
                origColor = "transparent";
            }

            jQuery("#" + id).animate({backgroundColor: origColor}, 5000);
		}
		});

        var displayWithLabel = jQuery(".displayWith-" + displayWithId);
        displayWithLabel.show();
        if (displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")) {
            displayWithLabel.parent().show();
        }


}

/**
 *  Replaces the view with the given content and run the hidden scripts.
 *
 * @param content  - server response
 * @param dataAttr -  any additional data attributes that the server needs to send
 */
function updateViewHandler(content, dataAttr){
    jQuery('#' + kradVariables.APP_ID).replaceWith(content);
        runHiddenScriptsAgain();

 }

/**
 * Redirect to the url sent as a response when an ajax redirect is requested.
 *
 * @param content  - server response
 * @param dataAttr -  any additional data attributes that the server needs to send
 */
function redirectHandler(content, dataAttr) {
// get contents between div and do window.location = parsed href
   window.location.href = jQuery(content).text();
}

function successCallbackF(content) {
    alert("Test");
}

/**
 * Handles a link that should post the form. Should be called from the methods
 * onClick event
 *
 * @param methodToCall -
 *          the value that should be set for the methodToCall parameter
 * @param navigateToPageId -
 *          the id for the page that the link should navigate to
 */
function handleActionLink(component, methodToCall, navigateToPageId) {
    var submitData = {};
    submitData = jQuery(component).data('submitData');
    submitData['navigateToPageId'] = navigateToPageId;

    ajaxSubmitForm(methodToCall, updatePageCallback, submitData, null, null, null);
}

/**
 * Calls the updateComponent method on the controller with component id passed in.  This id is
 * the component id with any/all suffixes on it not the dictionary id.
 * Retrieves the component with the matching id from the server and replaces a matching
 * _refreshWrapper marker span with the same id with the result.  In addition, if the result contains a label
 * and a displayWith marker span has a matching id, that span will be replaced with the label content
 * and removed from the component.  This allows for label and component content seperation on fields
 *
 * @param id - id for the component to retrieve
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 * @param addCallbackFunc - additional callback function to be executed (optional)
 */
function retrieveComponent(id, methodToCall, addCallbackFunc) {
    var elementToBlock = jQuery("#" + id);

    var updateRefreshableComponentCallback = function (htmlContent) {
        var component = jQuery("#" + id + "_update", htmlContent);

        var displayWithId = id;

        // special label handling, if any
        var theLabel = jQuery("#" + displayWithId + "_label_span", component);
        if (jQuery(".displayWith-" + displayWithId).length && theLabel.length) {
            theLabel.addClass("displayWith-" + displayWithId);
            jQuery("span.displayWith-" + displayWithId).replaceWith(theLabel);
            component.remove("#" + displayWithId + "_label_span");
        }

        // lightbox specific processing
        if (jQuery('#renderedInLightBox').val() == 'true') {
            component.find('.uif-dialogButtons').button();
        }

        elementToBlock.unblock({onUnblock:function () {
            var origColor = jQuery(component).find("#" + id).css("background-color");
            jQuery(component).find("#" + id).css("background-color", "");
            jQuery(component).find("#" + id).addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);

            // remove old stuff
            if (jQuery("#" + id + "_errors").length) {
                jQuery("#" + id + "_errors").remove();
            }

            jQuery("input[data-for='" + id + "']").each(function () {
                jQuery(this).remove();
            });

            // replace component
            if (jQuery("#" + id).length) {
                jQuery("#" + id).replaceWith(component.html());
            }

            if (jQuery("#" + id).parent().is("td")) {
                jQuery("#" + id).parent().show();
            }

            //runs scripts on the span or div with id
            runHiddenScripts(id);

            // lightbox specific processing
            if (jQuery('#renderedInLightBox').val() == 'true') {
                jQuery("#" + id).css('display', 'none');
            }

            if (origColor == "") {
                origColor = "transparent";
            }

            jQuery("#" + id).animate({backgroundColor:origColor}, 5000);

            // execute additional callback function if specified
            if (addCallbackFunc) {
                addCallbackFunc();
            }
        }
        });

        var displayWithLabel = jQuery(".displayWith-" + displayWithId);
        displayWithLabel.show();
        if (displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")) {
            displayWithLabel.parent().show();
        }

    };

    if (!methodToCall) {
        methodToCall = "refresh";
    }

    // Since we are always setting skipViewInit to true, remove any existing input skipViewInit param
    jQuery("input[name='skipViewInit']").remove();

    ajaxSubmitForm(methodToCall, updateRefreshableComponentCallback,
            {updateComponentId:id, skipViewInit:"true"}, elementToBlock, null, "update-component");
}

/**
 * Invoked when the Show/Hide Inactive button is clicked for a collection to toggle the
 * display of inactive records within the collection. A request is made with ajax to update
 * the collection flag on the server and render the collection group. The updated collection
 * groups contents are then updated in the dom
 *
 * @param collectionGroupId - id for the collection group to update
 * @param showInactive - boolean indicating whether inactive records should be displayed (true) or
 * not displayed (false)
 */
function toggleInactiveRecordDisplay(component, collectionGroupId, showInactive) {
    var elementToBlock = jQuery("#" + collectionGroupId);
    var updateCollectionCallback = function (htmlContent) {
        var component = jQuery("#" + collectionGroupId, htmlContent);

        elementToBlock.unblock({onUnblock:function () {
            //replace component
            if (jQuery("#" + collectionGroupId).length) {
                jQuery("#" + collectionGroupId).replaceWith(component);
            }
            runHiddenScripts(collectionGroupId);
        }
        });
    };

    // Since we are always setting skipViewInit to true, remove any existing skipViewInit input param
    jQuery("input[name='skipViewInit']").remove();

    var submitData = {};
    submitData = jQuery(component).data('submitData');
    submitData['updateComponentId'] = collectionGroupId;
    submitData['skipViewInit'] = "true";
    submitData['showInactiveRecords'] = showInactive;

    ajaxSubmitForm("toggleInactiveRecordDisplay", updateCollectionCallback,
            submitData,
            elementToBlock, null, "update-component");
}

function performCollectionAction(component, collectionGroupId) {
    if (collectionGroupId) {
        var elementToBlock = jQuery("#" + collectionGroupId);
        var updateCollectionCallback = function (htmlContent) {
            var component = jQuery("#" + collectionGroupId, htmlContent);

            elementToBlock.unblock({onUnblock:function () {
                //replace component
                if (jQuery("#" + collectionGroupId).length) {
                    jQuery("#" + collectionGroupId).replaceWith(component);
                }
                runHiddenScripts(collectionGroupId);
            }
            });
        };

        var methodToCall = jQuery("input[name='methodToCall']").val();
        // Since we are always setting skipViewInit to true, remove any existing skipViewInit input param
        jQuery("input[name='skipViewInit']").remove();

        var submitData = {};
        submitData = jQuery(component).data('submitData');
        submitData['updateComponentId'] = collectionGroupId;
        submitData['skipViewInit'] = "true";

        ajaxSubmitForm(methodToCall, updateCollectionCallback, submitData,
                elementToBlock, null, "update-component");
    }
}

//called when a line is added to a collection
function addLineToCollection(component, collectionGroupId, collectionBaseId) {
    if (collectionBaseId) {
        var addFields = jQuery("." + collectionBaseId + "-addField:visible");
        jQuery.watermark.hideAll();

        var valid = true;
        addFields.each(function () {
            jQuery(this).removeClass("ignoreValid");
            jQuery(this).valid();
            if (jQuery(this).hasClass("error")) {
                valid = false;
            }
            jQuery(this).addClass("ignoreValid");
        });

        jQuery.watermark.showAll();

        if (valid) {
            performCollectionAction(component, collectionGroupId);
            return true;
        }
        else {
            jQuery("#formComplete").html("");
            alert("This addition contains errors.  Please correct these errors and try again.");
            return false;
        }
    }
}

/**
 * Does client side validation when row save is clicked and if valid calls the performCollectionAction function that
 * does an ajax call to the controller
 *
 * @param collectionGroupId - the collection group id
 * @param collectionName - the property name of the collection used to get the fields
 */
function validateAndPerformCollectionAction(component, collectionGroupId, collectionName) {
    if (collectionName) {

        // Get the fields to validate by combining the collection property name and the selected row
        var submitData = jQuery(component).data("submitData");
        var selectedIndex = submitData['actionParameters[selectedLineIndex]'];
        var fields = jQuery("[name^='" + collectionName + "[" + selectedIndex + "]']");

        jQuery.watermark.hideAll();

        var valid = true;
        fields.each(function () {
            jQuery(this).removeClass("ignoreValid");
            jQuery(this).valid();
            if (jQuery(this).hasClass("error")) {
                valid = false;
            }
            jQuery(this).addClass("ignoreValid");
        });

        jQuery.watermark.showAll();

        if (valid) {
            performCollectionAction(component, collectionGroupId);
        }
        else {
            jQuery("#formComplete").html("");
            alert("This line contains errors.  Please correct these errors and try again.");
        }
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
                        var origColor = refreshDisclosure.css("background-color");
                        refreshDisclosure.css("background-color", "");
                        refreshDisclosure.addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);
                        refreshDisclosure.show();
                        if (refreshDisclosure.parent().is("td")) {
                            refreshDisclosure.parent().show();
                        }
                        if (origColor == "") {
                            origColor = "transparent";
                        }
                        refreshDisclosure.animate({backgroundColor:origColor}, 5000);

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
            });
        }
        else {
            jQuery(":input:visible", element).each(function () {
                jQuery(this).removeClass("ignoreValid");
            });
        }
    }
}

/**
 * Makes an get request to the server so that the form with the specified formKey will
 * be cleared server side
 */
function clearServerSideForm(formKey) {
    var queryData = {};

    queryData.methodToCall = 'clearForm';
    queryData.skipViewInit = 'true';
    queryData.formKey = formKey;

    var postUrl = getConfigParam("kradUrl") + "/listener";

    jQuery.ajax({
        url:postUrl,
        dataType:"json",
        data:queryData,
        async:false,
        beforeSend:null,
        complete:null,
        error:null,
        success:null
    });
}
