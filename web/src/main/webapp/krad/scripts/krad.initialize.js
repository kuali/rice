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
var $dialog = null;
var jq = jQuery.noConflict();

//clear out blockUI css, using css class overrides
jQuery.blockUI.defaults.css = {};
jQuery.blockUI.defaults.overlayCSS = {};

// validation init
var pageValidatorReady = false;
var validateClient = true;
var messageSummariesShown = false;

var errorImage;
var warningImage;
var infoImage;

// common event registering done here through JQuery ready event
jq(document).ready(function() {
	setPageBreadcrumb();

	// buttons
	jq("input:submit").button();
	jq("input:button").button();
    jq("a.button").button();

    // common ajax setup
	jq.ajaxSetup({
		  beforeSend: function() {
		     createLoading(true);
		  },
		  complete: function(){
			 createLoading(false);
		  },
		  error: function(jqXHR, textStatus, errorThrown){
			 createLoading(false);
			 showGrowl('Status: ' + textStatus + '<br/>' + errorThrown, 'Server Response Error', 'errorGrowl');
		  }
	});

	runHiddenScripts("");
    jq("#view_div").show();
    createLoading(false);

    // hide the ajax progress display screen if the page is replaced e.g. by a login page when the session expires
    jq(window).unload(function() {
        createLoading(false);
    });

    //setup the various event handlers for fields - THIS IS IMPORTANT
    initFieldHandlers();
});

/**
 * Sets up the various handlers for various field controls.
 * This function includes handlers that are critical to the behavior of KRAD validation and message frameworks
 * on the client
 */
function initFieldHandlers(){
    //when these fields are focus store what the current errors are if any and show the messageTooltip
    jq(document).on("focus",
            "[data-role='InputField'] input, "
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea, "
                    + "[data-role='InputField'] option",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));

                //keep track of what errors it had on initial focus
                var data = jQuery("#" + id).data("validationMessages");
                data.focusedErrors = data.errors;
                jQuery("#" + id).data("validationMessages", data);

                //show tooltip on focus
                showMessageTooltip(id, false);
            });

    //when these fields are focused out validate and if this field never had an error before, show and close, otherwise
    //immediately close the tooltip
    jq(document).on("focusout",
            "[data-role='InputField'] input:text, "
                    + "[data-role='InputField'] input:password, "
                    + "[data-role='InputField'] input:file, "
                    + "[data-role='InputField'] select, "
                    + "[data-role='InputField'] textarea",
            function () {
                var id = getAttributeId(jQuery(this).attr('id'));
                var data = jQuery("#" + id).data("validationMessages");
                var hadError = data.focusedErrors.length;

                if (validateClient) {
                    var valid = jq(this).valid();
                    dependsOnCheck(this, new Array());
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
    jq(document).on("change",
            "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio, "
                    + "[data-role='InputField'] select",
            function () {
                if (validateClient) {
                    jq(this).valid();
                    dependsOnCheck(this, new Array());
                }
            });

    //special radio and checkbox control handling for click events
    jq(document).on("click",
            "[data-role='InputField'] input:checkbox, "
                    + "[data-role='InputField'] input:radio,"
                    + "fieldset[data-type='CheckboxSet'] label,"
                    + "fieldset[data-type='RadioSet'] label",
            function () {
                var event = jQuery.Event("handleFieldsetMessages");
                event.element = this;
                //fire the handleFieldsetMessages event on every input of checkbox or radio fieldset
                jQuery("fieldset > span > input").not(this).trigger(event);
            });

    //special radio and checkbox control handling for focus events
    jq(document).on("focus",
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
    jq(document).on("focusout",
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

                            if (validateClient) {
                                var valid = jq(this).valid();
                                dependsOnCheck(this, new Array());
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
                    var hadError = jq(this).hasClass("error");
                    //not in a fieldset - so validate directly
                    if (validateClient) {
                        var valid = jq(this).valid();
                        dependsOnCheck(this, new Array());
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

function initBubblePopups(){
    //this can ONLY ever have ONE CALL that selects ALL elements that may have a BubblePopup
    //any other CreateBubblePopup calls besides this one (that explicitly selects any elements that may use them)
    //will cause a severe loss of functionality and buggy behavior
    //if new BubblePopups must be created due to new content on the screen this full selection MUST be run again
    jq("input, select, textarea, "
            + " label").CreateBubblePopup(
            {   manageMouseEvents: false,
                themePath: "../krad/plugins/tooltip/jquerybubblepopup-theme/"});
}

//sets up the validator with the necessary default settings and methods
//also sets up the dirty check and other page scripts
//note the use of onClick and onFocusout for on the fly validation client side
function setupPage(validate){
	jq('#kualiForm').dirty_form({changedClass: 'dirty', includeHidden: true});

    errorImage = "<img class='uif-validationImage' src='" + getConfigParam("kradImageLocation") + "validation/error.png' alt='Error' /> ";
    warningImage = "<img class='uif-validationImage' src='" + getConfigParam("kradImageLocation") + "validation/warning.png' alt='Warning' /> ";
    infoImage = "<img class='uif-validationImage' src='" + getConfigParam("kradImageLocation") + "validation/info.png' alt='Information' /> ";

    //Reset summary state before processing each field - summaries are shown if server messages
    // or on client page validation
    messageSummariesShown = false;

    //flag to turn off and on validation mechanisms on the client
    validateClient = validate;

    jq("[data-role='InputField']").each(function(){
        var id = jQuery(this).attr('id');
        handleMessagesAtField(id);
    });

	//Make sure form doesn't have any unsaved data when user clicks on any other portal links, closes browser or presses fwd/back browser button
	jq(window).bind('beforeunload', function(evt){
        var validateDirty = jq("[name='validateDirty']").val();
		if (validateDirty == "true")
		{
			var dirty = jq(".uif-field").find("input.dirty");
			//methodToCall check is needed to skip from normal way of unloading (cancel,save,close)
			var methodToCall = jq("[name='methodToCall']").val();
			if (dirty.length > 0 && methodToCall == null)
			{
				return "Form has unsaved data. Do you want to leave anyway?";
			}
		}
	});

	jq('#kualiForm').validate(
	{
		onsubmit: false,
		ignore: ".ignoreValid",
		wrapper: "",
        onfocusout: false,
        onclick: false,
        onkeyup: function(element){
            if(validateClient){
                var id = getAttributeId(jQuery(element).attr('id'));
                var data = jQuery("#" + id).data("validationMessages");

                //if this field previously had errors validate on key up
                if(data.focusedErrors && data.focusedErrors.length){
                    jq(element).valid();
                    dependsOnCheck(element, new Array());
                }
            }
        },
		highlight: function(element, errorClass, validClass) {
			jq(element).addClass(errorClass).removeClass(validClass);
            jq(element).attr("aria-invalid", "true");
		},
		unhighlight: function(element, errorClass, validClass) {
			jq(element).removeClass(errorClass).addClass(validClass);
            jq(element).removeAttr("aria-invalid");

            var id = getAttributeId(jQuery(element).attr("id"));
            var data = jQuery("#" + id).data("validationMessages");

            data.errors = [];
            jQuery("#" + id).data("validationMessages", data);
            if(messageSummariesShown){
               handleMessagesAtField(id);
            }
            else{
               writeMessagesAtField(id);
            }
            hideMessageTooltip(id);

		},
		errorPlacement: function(error, element) {},
        showErrors: function (nameErrorMap, elementObjectList) {
            this.defaultShowErrors();

            for(var i in elementObjectList){
                var element = elementObjectList[i].element;
                var message = elementObjectList[i].message;
                var id = getAttributeId(jQuery(element).attr('id'));

                var data = jQuery("#" + id).data("validationMessages");

                var exists = false;
                if(data.errors.length){
                    for(var j in data.errors){
                        if(data.errors[j] === message){
                            exists = true;
                        }
                    }
                }

                if(!exists){
                    data.errors = [];
                    data.errors.push(message);
                    jQuery("#" + id).data("validationMessages", data);

                    if(messageSummariesShown){
                       handleMessagesAtField(id);
                    }
                    else{
                       writeMessagesAtField(id);
                    }
                    showMessageTooltip(id, false, true);
                }
            }

        },
        success: function (label) {
            var htmlFor = jQuery(label).attr('for');
            var id = "";
            if(htmlFor.indexOf("_control") >= 0){
                id = getAttributeId(htmlFor);
            }
            else{
                id = jQuery("[name='" + htmlFor +"']:first").attr("id");
                id = getAttributeId(id);
            }

            var data = jQuery("#" + id).data("validationMessages");
            if(data.errors.length){
                data.errors = [];
                jQuery("#" + id).data("validationMessages", data);
                if(messageSummariesShown){
                   handleMessagesAtField(id);
                }
                else{
                   writeMessagesAtField(id);
                }
                showMessageTooltip(id, false, true);
            }
        }
	});

    jq(".required").each(function(){
        jq(this).attr("aria-required", "true");
    });

	jq(document).trigger('validationSetup');
	pageValidatorReady = true;

	jq.watermark.showAll();
}

/**
 * Retrieves the value for a configuration parameter
 *
 * @param paramName - name of the parameter to retrieve
 */
function getConfigParam(paramName) {
    var configParams = jq(document).data("ConfigParameters");
    if (configParams) {
        return configParams[paramName];
    }
    return "";
}

jQuery.validator.addMethod("minExclusive", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || value > param[0];
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("maxInclusive", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || value <= param[0];
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("minLengthConditional", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || this.getLength(jq.trim(value), element) >= param[0];
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("maxLengthConditional", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || this.getLength(jq.trim(value), element) <= param[0];
	}
	else{
		return true;
	}
});

// data table initialize default sorting
jQuery.fn.dataTableExt.oSort['kuali_date-asc']  = function(a,b) {
	var date1 = a.split('/');
	var date2 = b.split('/');
	var x = (date1[2] + date1[0] + date1[1]) * 1;
	var y = (date2[2] + date2[0] + date2[1]) * 1;
	return ((x < y) ? -1 : ((x > y) ?  1 : 0));
};

jQuery.fn.dataTableExt.oSort['kuali_date-desc'] = function(a,b) {
	var date1 = a.split('/');
	var date2 = b.split('/');
	var x = (date1[2] + date1[0] + date1[1]) * 1;
	var y = (date2[2] + date2[0] + date2[1]) * 1;
	return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
};

jQuery.fn.dataTableExt.oSort['kuali_percent-asc'] = function(a,b) {
	var num1 = a.replace(/[^0-9]/g, '');
	var num2 = b.replace(/[^0-9]/g, '');
	num1 = (num1 == "-" || num1 === "" || isNaN(num1)) ? 0 : num1*1;
	num2 = (num2 == "-" || num2 === "" || isNaN(num2)) ? 0 : num2*1;
	return num1 - num2;
};

jQuery.fn.dataTableExt.oSort['kuali_percent-desc'] = function(a,b) {
	var num1 = a.replace(/[^0-9]/g, '');
	var num2 = b.replace(/[^0-9]/g, '');
	num1 = (num1 == "-" || num1 === "" || isNaN(num1)) ? 0 : num1*1;
	num2 = (num2 == "-" || num2 === "" || isNaN(num2)) ? 0 : num2*1;
	return num2 - num1;
};

jQuery.fn.dataTableExt.oSort['kuali_currency-asc'] = function(a,b) {
	/* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
	var x = a == "-" ? 0 : a.replace( /,/g, "" );
	var y = b == "-" ? 0 : b.replace( /,/g, "" );
	/* Remove the currency sign */
	x = x.substring( 1 );
	y = y.substring( 1 );
	/* Parse and return */
	x = parseFloat( x );
	y = parseFloat( y );
	
	x = isNaN(x) ? 0 : x*1;
	y = isNaN(y) ? 0 : y*1;
	
	return x - y;
};

jQuery.fn.dataTableExt.oSort['kuali_currency-desc'] = function(a,b) {
	/* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
	var x = a == "-" ? 0 : a.replace( /,/g, "" );
	var y = b == "-" ? 0 : b.replace( /,/g, "" );
	/* Remove the currency sign */
	x = x.substring( 1 );
	y = y.substring( 1 );
	/* Parse and return */
	x = parseFloat( x );
	y = parseFloat( y );
	
	x = isNaN(x) ? 0 : x;
	y = isNaN(y) ? 0 : y;
	
	return y - x;
};

jQuery.fn.dataTableExt.afnSortData['dom-text'] = function (oSettings, iColumn) {
    var aData = [];
    jq('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var input = jq(this).find('input:text');
        if (input.length != 0) {
            aData.push(input.val());
        } else {
            // find span for the data or input field and get its text
            var input1 = jq(this).find('.uif-field');
            if (input1.length != 0) {
                aData.push(jq.trim(input1.find("span:first").text()));
            } else {
                // just use the text within the cell
                aData.push(jq(this).text());
            }
        }

    });

    return aData;
}

/* Create an array with the values of all the select options in a column */
jQuery.fn.dataTableExt.afnSortData['dom-select'] = function (oSettings, iColumn) {
    var aData = [];
    jq('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var selected = jq(this).find('select option:selected:first');
        if (selected.length != 0) {
            aData.push(selected.text());
        } else {
            var input1 = jq(this).find('.uif-inputField');
            if (input1.length != 0) {
                aData.push(jq.trim(input1.text()));
            } else {
                aData.push("");
            }
        }

    });

    return aData;
}

/* Create an array with the values of all the checkboxes in a column */
jQuery.fn.dataTableExt.afnSortData['dom-checkbox'] = function (oSettings, iColumn) {
    var aData = [];
    jq('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var checkboxes = jq(this).find('input:checkbox');
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
            var input1 = jq(this).find('.uif-inputField');
            if (input1.length != 0) {
                aData.push(jq.trim(input1.text()));
            } else {
                aData.push("");
            }
        }

    });

    return aData;
}

jQuery.fn.dataTableExt.afnSortData['dom-radio'] = function (oSettings, iColumn) {
    var aData = [];
    jq('td:eq(' + iColumn + ')', oSettings.oApi._fnGetTrNodes(oSettings)).each(function () {
        var radioButtons = jq(this).find('input:radio');
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
            var input1 = jq(this).find('.uif-inputField');
            if (input1.length != 0) {
                aData.push(jq.trim(input1.text()));
            } else {
                aData.push("");
            }
        }

    });

    return aData;
}

// setup window javascript error handler
window.onerror = errorHandler;

function errorHandler(msg,url,lno)
{
  jq("#view_div").show();
  jq("#viewpage_div").show();
  var context = getContext();
  context.unblockUI();
  showGrowl(msg + '<br/>' + url + '<br/>' + lno, 'Javascript Error', 'errorGrowl');
  return false;
}


// script that should execute when the page unloads
jq(window).bind('beforeunload', function (evt) {
    // clear server form if closing the browser tab/window or going back
    // TODO: work out back button problem so we can add this clearing
//    if (!event.pageY || (event.pageY < 0)) {
//        clearServerSideForm();
//    }
    console.log("unload");
});



