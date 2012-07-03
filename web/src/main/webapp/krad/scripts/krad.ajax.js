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

function actionInvokeHandler(component) {

    var ajaxSubmit = jQuery(component).data("ajaxsubmit");
    var submitData = jQuery(component).data("submitData");
    var successCallback = jQuery(component).data("successcallback");
    var elementToBlock = jQuery(component).data("elementtoblock");
    var errorCallback = jQuery(component).data("errorcallback");
    var preSubmitCall = jQuery(component).data("presubmitcall");
    var validate = jQuery(component).data("validate");

    var additionalData = {};
    var methodToCall = submitData['methodToCall'];
    for (key in submitData) {
        additionalData[key] = submitData[key];
        //  alert(key + ":" + additionalData[key]);
    }
     if (successCallback == null || successCallback == "") {
         successCallback = updatePageCallback;
     }
    if (ajaxSubmit) {
            if(validate){
              validateAndAjaxSubmitForm(methodToCall, successCallback, additionalData,elementToBlock ,  preSubmitCall);
            } else{
                ajaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock ,  preSubmitCall);
            }
    } else {
        if(validate){
            validateAndSubmitForm(methodToCall, additionalData, preSubmitCall)
        }   else{
           submitForm(methodToCall, additionalData, preSubmitCall);
        }

    }
}

function ajaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock ,  preSubmitCall) {
    ajaxSubmitFormFullOpts(methodToCall, successCallback, additionalData, elementToBlock, null, false, preSubmitCall);
}

function validateAndAjaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock ,  preSubmitCall) {
    ajaxSubmitFormFullOpts(methodToCall, successCallback, additionalData, elementToBlock, null, true, preSubmitCall);
}

/**
 * Submits the form through an ajax submit, the response is the new page html
 * runs all hidden scripts passed back (this is to get around a bug with premature script evaluation)
 *
 * If a form has the properties enctype or encoding set to multipart/form-data, an iframe is created to hold the response
 * If the returned response contains scripts that are meant to be run on page load,
 * they will be executed within the iframe since the jquery ready event is triggered
 *
 * For the above reason, the renderFullView below is set to false so that the script content between <head></head> is left out
 */
function ajaxSubmitFormFullOpts(methodToCall, successCallback, additionalData, elementToBlock, errorCallback, validate, preSubmitCall  ) {
    var data = {};
    // invoke validateForm if validate flag is true, if returns false do not continue
    if (validate && !validateForm()) {
        return;
    }

    if(preSubmitCall!= null && preSubmitCall!=="") {
         if(!eval(preSubmitCall)){
            return;
        }

     }
    // check to see if methodToCall is still null
    if (methodToCall != null || methodToCall !== "") {
        data.methodToCall = methodToCall;
    }

    // Since we are explicitly setting renderFullView to false, we need to remove any input renderFullViewParam
    jQuery("input[name='renderFullView']").remove();
    data.renderFullView = false;

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
        jQuery.extend(data, {clientViewState: jsonViewState});
    }

    // check if called from a lightbox.  if it is set the componentId
    var componentId = undefined;
    if (jQuery('#kualiLightboxForm').children(':first').length == 1) {
        componentId = jQuery('#kualiLightboxForm').children(':first').attr('id');
    }

    var submitOptions = {
        data: data,
        success: function(response) {
            var tempDiv = document.createElement('div');
            tempDiv.innerHTML = response;
            var hasError = handleIncidentReport(response);
            if (!hasError) {
               // alert("Type :: "+typeof successCallback)
                if(typeof successCallback == "string"){
                    eval(successCallback + "(tempDiv)");
                } else {
                     successCallback(tempDiv);
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
        error: function(jqXHR, textStatus) {
            alert("Request failed: " + textStatus);
        }
    };

    if (elementToBlock != null && elementToBlock.length) {
        var elementBlockingOptions = {
            beforeSend: function() {
                if (elementToBlock.hasClass("unrendered")) {
                    elementToBlock.append('<img src="' + getConfigParam(kradVariables.IMAGE_LOCATION) + 'loader.gif" alt="working..." /> Loading...');
                    elementToBlock.show();
                }
                else {
                    elementToBlock.block({
                        message: '<img src="' + getConfigParam(kradVariables.IMAGE_LOCATION) + 'loader.gif" alt="working..." /> Updating...',
                        fadeIn:  400,
                        fadeOut:  800
                    });
                }
            },
            complete: function() {
                // note that if you want to unblock simultaneous with showing the new retrieval
                // you must do so in the successCallback
                elementToBlock.unblock();

            },
            error: function() {
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

function submitForm(methodToCall, additionalData, preSubmitCall) {
    // invoke submitFormFullOpts with null callback, validate false
    submitFormFullOpts(methodToCall, additionalData, false, preSubmitCall);
}

function validateAndSubmitForm(methodToCall, additionalData, preSubmitCall) {
    // invoke submitFormFullOpts with null callback, validate true
    submitFormFullOpts(methodToCall, additionalData, true, preSubmitCall);
}

function submitFormFullOpts(methodToCall, additionalData, validate, preSubmitCall) {
    // invoke validateForm if validate flag is true, if returns false do not continue
    if (validate && !validateForm()) {
        return;
    }

    // if presubmit call given, invoke. If returns false don't continue

    if(preSubmitCall!= null && preSubmitCall!=="") {
         if(!eval(preSubmitCall)){
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
 * Submits a form via ajax using the jquery form plugin
 * The methodToCall parameter is used to determine the controller method to invoke
 */
//function submitForm(){
//	var methodToCall = jQuery("input[name='methodToCall']").val();
//	ajaxSubmitForm(methodToCall, updatePageCallback, null, null, null);
//}



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
function handleActionLink(methodToCall, navigateToPageId) {
    ajaxSubmitForm(methodToCall, "updatePageCallback", {navigateToPageId:navigateToPageId}, null, null);
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
 * @param baseId - base id (without suffixes) for the component that should be refreshed
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 */
function retrieveComponent(id, baseId, methodToCall){
	var elementToBlock = jQuery("#" + id);

	var updateRefreshableComponentCallback = function(htmlContent){
		var component = jQuery("#" + id + "_update", htmlContent);

        var displayWithId = id;

		// special label handling, if any
		var theLabel = jQuery("#" + displayWithId + "_label_span", component);
		if(jQuery(".displayWith-" + displayWithId).length && theLabel.length){
			theLabel.addClass("displayWith-" + displayWithId);
            jQuery("span.displayWith-" + displayWithId).replaceWith(theLabel);
			component.remove("#" + displayWithId + "_label_span");
		}

		elementToBlock.unblock({onUnblock: function(){
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
				if(jQuery("#" + id).length){
                    jQuery("#" + id).replaceWith(component.html());
				}

                if(jQuery("#" + id).parent().is("td")){
                    jQuery("#" + id).parent().show();
                }

                //runs scripts on the span or div with id
				runHiddenScripts(id);

                if(origColor == ""){
                    origColor = "transparent";
                }

            jQuery("#" + id).animate({backgroundColor: origColor}, 5000);
			}
		});

        var displayWithLabel = jQuery(".displayWith-" + displayWithId);
        displayWithLabel.show();
        if(displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")){
            displayWithLabel.parent().show();
        }
	};

    if (!methodToCall) {
        methodToCall = "refresh";
    }

        // Since we are always setting skipViewInit to true, remove any existing input skipViewInit param
    jQuery("input[name='skipViewInit']").remove();

    ajaxSubmitForm(methodToCall, updateRefreshableComponentCallback,
            {updateComponentId: id, skipViewInit: "true"}, elementToBlock, null);
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
function toggleInactiveRecordDisplay(collectionGroupId, showInactive) {
    var elementToBlock = jQuery("#" + collectionGroupId);
    var updateCollectionCallback = function(htmlContent){
    	var component = jQuery("#" + collectionGroupId, htmlContent);

		elementToBlock.unblock({onUnblock: function(){
				//replace component
				if(jQuery("#" + collectionGroupId).length){
                    jQuery("#" + collectionGroupId).replaceWith(component);
				}
				runHiddenScripts(collectionGroupId);
			}
		});
    };


    // Since we are always setting skipViewInit to true, remove any existing skipViewInit input param
    jQuery("input[name='skipViewInit']").remove();

    ajaxSubmitForm("toggleInactiveRecordDisplay", updateCollectionCallback,
            {updateComponentId: collectionGroupId, skipViewInit: "true", showInactiveRecords : showInactive},
            elementToBlock, null);
}

function performCollectionAction(collectionGroupId){
	if(collectionGroupId){
		var elementToBlock = jQuery("#" + collectionGroupId);
	    var updateCollectionCallback = function(htmlContent){
	    	var component = jQuery("#" + collectionGroupId, htmlContent);

			elementToBlock.unblock({onUnblock: function(){
					//replace component
					if(jQuery("#" + collectionGroupId).length){
                        jQuery("#" + collectionGroupId).replaceWith(component);
					}
					runHiddenScripts(collectionGroupId);
				}
			});
	    };

	    var methodToCall = jQuery("input[name='methodToCall']").val();
        // Since we are always setting skipViewInit to true, remove any existing skipViewInit input param
        jQuery("input[name='skipViewInit']").remove();

        ajaxSubmitForm(methodToCall, updateCollectionCallback, {updateComponentId: collectionGroupId, skipViewInit: "true"},
                elementToBlock, null);
    }
}



//called when a line is added to a collection
function addLineToCollection(collectionGroupId, collectionBaseId){
	if(collectionBaseId){
		var addFields = jQuery("." + collectionBaseId + "-addField:visible");
        jQuery.watermark.hideAll();

		var valid = true;
		addFields.each(function(){
            jQuery(this).removeClass("ignoreValid");
            jQuery(this).valid();
			if(jQuery(this).hasClass("error")){
				valid = false;
			}
            jQuery(this).addClass("ignoreValid");
		});

        jQuery.watermark.showAll();

		if(valid){
			performCollectionAction(collectionGroupId);
		}
		else{
            jQuery("#formComplete").html("");
			alert("This addition contains errors.  Please correct these errors and try again.");
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
function validateAndPerformCollectionAction(collectionGroupId, collectionName){
    if(collectionName){

        // Get the fields to validate by combining the collection property name and the selected row
        var selectedIndex = jQuery("[name='actionParameters[selectedLineIndex]']").val();
        var fields = jQuery("[name^='" + collectionName + "[" + selectedIndex + "]']");

        jQuery.watermark.hideAll();

        var valid = true;
        fields.each(function(){
            jQuery(this).removeClass("ignoreValid");
            jQuery(this).valid();
            if(jQuery(this).hasClass("error")){
                valid = false;
            }
            jQuery(this).addClass("ignoreValid");
        });

        jQuery.watermark.showAll();

        if(valid){
            performCollectionAction(collectionGroupId);
        }
        else{
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
 * @param baseId - base id (without suffixes) for the component that should be refreshed
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 */
function setupOnChangeRefresh(controlName, refreshId, baseId, methodToCall){
	setupRefreshCheck(controlName, refreshId, baseId, function(){return true;}, methodToCall);
}

/**
 * Sets up the conditional refresh mechanism in js by adding a change handler to the control
 * which may satisfy the conditional refresh condition passed in.  When the condition is satisfied,
 * refresh the necessary content specified by id by making a server call to retrieve a new instance
 * of that component
 *
 * @param controlName - value for the name attribute for the control the event should be generated for
 * @param refreshId - id for the component that should be refreshed when condition occurs
 * @param baseId - base id (without suffixes) for the component that should be refreshed
 * @param condition - function which returns true to refresh, false otherwise
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 */
function setupRefreshCheck(controlName, refreshId, baseId, condition, methodToCall){
    jQuery("[name='"+ escapeName(controlName) +"']").live('change', function() {
		// visible check because a component must logically be visible to refresh
		var refreshComp = jQuery("#" + refreshId);
		if(refreshComp.length){
			if(condition()){
				retrieveComponent(refreshId, baseId, methodToCall);
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
function setupProgressiveCheck(controlName, disclosureId, baseId, condition, alwaysRetrieve, methodToCall){
	if (!baseId.match("\_c0$")) {
        jQuery("[name='"+ escapeName(controlName) +"']").live('change', function() {
			var refreshDisclosure = jQuery("#" + disclosureId);
			if(refreshDisclosure.length){
                var displayWithId = disclosureId;

				if(condition()){
					if(refreshDisclosure.data("role") == "placeholder" || alwaysRetrieve){
						retrieveComponent(disclosureId, baseId, methodToCall);
					}
					else{
                        var origColor = refreshDisclosure.css("background-color");
                        refreshDisclosure.css("background-color", "");
                        refreshDisclosure.addClass(kradVariables.PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS);
						refreshDisclosure.show();
                        if(refreshDisclosure.parent().is("td")){
                            refreshDisclosure.parent().show();
                        }
                        if(origColor == ""){
                           origColor = "transparent";
                        }
                        refreshDisclosure.animate({backgroundColor: origColor}, 5000);

						//re-enable validation on now shown inputs
						hiddenInputValidationToggle(disclosureId);
                        var displayWithLabel = jQuery(".displayWith-" + displayWithId);
                        displayWithLabel.show();
                        if(displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")){
                            displayWithLabel.parent().show();
                        }

					}
				}
				else{
					refreshDisclosure.hide();
					// ignore validation on hidden inputs
					hiddenInputValidationToggle(disclosureId);
                    var displayWithLabel = jQuery(".displayWith-" + displayWithId);
                    displayWithLabel.hide();
                    if(displayWithLabel.parent().is("td") || displayWithLabel.parent().is("th")){
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
function hiddenInputValidationToggle(id){
	var element = jQuery("#" + id);
	if(element.length){
		if(element.css("display") == "none"){
            jQuery(":input:hidden", element).each(function(){
                jQuery(this).addClass("ignoreValid");
			});
		}
		else{
            jQuery(":input:visible", element).each(function(){
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
