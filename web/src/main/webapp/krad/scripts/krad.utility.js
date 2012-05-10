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
var bodyHeight;

/**
 * Takes a name that may have characters incompatible with jQuery selection and escapes them so they can
 * be used in selectors.  This method MUST be called when selecting on a name that can be ANY name on the page
 * to avoid issues with collections(mainly)
 *
 * @returns a string that has been escaped for use in jQuery selectors
 */
function escapeName(name){
    name = name.replace(/\\'/g, "'");
    name = name.replace(/'/g, "\\'");
    name = name.replace(/\\"/g, "\"");
    name = name.replace(/"/g, "\\\"");
    name = name.replace(/\./g, "\\.");
    name = name.replace(/\[/g, "\\[");
    name = name.replace(/\]/g, "\\]");
    return name;
}

function publishHeight(){
    var parentUrl = "";
    if(navigator.cookieEnabled){
        parentUrl = jQuery.cookie('parentUrl');
        var passedUrl = decodeURIComponent( document.location.hash.replace( /^#/, '' ) );
        if(passedUrl && passedUrl.substring(0, 4) === "http"){
            jQuery.cookie('parentUrl', passedUrl, {path: '/'});
            parentUrl = passedUrl;
        }
    }

    if(parentUrl === ""){
        //make the assumption for not cross-domain, will have no effect if cross domain (message wont be
        //received)
        parentUrl = window.location;
        parentUrl = decodeURIComponent(parentUrl);
    }

    var height = jQuery("body").outerHeight();
    jQuery("body").attr("style", "overflow-x: auto; padding-right: 20px;");
    if (parentUrl && !isNaN(height) && height > 0) {
        jQuery.postMessage({ if_height: height}, parentUrl, parent);
        bodyHeight = height;
    }
}

/**
 * Get the current context
 *
 * @returns the jQuery context that can be used to perform actions that must be global to the entire page
 * ie, showing lightBoxes and growls etc
 */
function getContext(){
	var context;
	if(top == self){
		context = jq;
	}
	else{
		context = parent.jQuery;
	}

	return context;
}

/**
 * Sets a configuration parameter that will be accessible with script
 *
 * <p>
 * Configuration parameters are sent from the server and represent non-component
 * state, such as location of images
 * </p>
 *
 * @param paramName - name of the configuration parameter
 * @param paramValue - value for the configuration parameter
 */
function setConfigParam(paramName, paramValue) {
    var configParams = jQuery(document).data("ConfigParameters");
    if (!configParams) {
        configParams = new Object();
        jQuery(document).data("ConfigParameters", configParams);
    }
    configParams[paramName] = paramValue;
}



/**
 * Called when a view is rendered to initialize the state of components
 * that need to be accessed client side
 *
 * @param viewState - map (object) containing the view state
 */
function initializeViewState(viewState) {
    jQuery(document).data(kradVariables.VIEW_STATE, viewState);
}

/**
 * Updates the current view state with the given map of view state
 *
 * <p>
 * The given state will be merged with the current. Matching keys for simple properties will be overridden
 * if contained in the second map, in all cases except when the value is another map, in which case the map
 * value will be merged
 * </p>
 *
 * @param viewState - view state to merge in
 */
function updateViewState(viewState) {
    var currentViewState = jQuery(document).data(kradVariables.VIEW_STATE);
    if (currentViewState) {
        jQuery.extend(currentViewState, viewState);
    }
    else {
        jQuery(document).data(kradVariables.VIEW_STATE, viewState);
    }
}

/**
 * Sets a key/value pair in the view state
 *
 * @param key - name to reference state by
 * @param value - value for the state
 */
function setViewState(key, value) {
    var viewState = jQuery(document).data(kradVariables.VIEW_STATE);
    if (!viewState) {
        viewState = new Object();
        jQuery(document).data(kradVariables.VIEW_STATE, viewState);
    }
    viewState[key] = value;
}

/**
 * Retrieves the current value for a given key in the view state, if
 * not found empty string is returned
 *
 * @param key - name of the variable in view state to return
 */
function getViewState(key) {
    var viewState = jQuery(document).data(kradVariables.VIEW_STATE);
    if (viewState && viewState.hasOwnProperty(key)) {
        return viewState[key];
    }
    return "";
}

/**
 * Adds the given key/value pair to the state maintained for the give component
 *
 * @param componentId - id for the component the state should be associated with
 * @param key - name to reference the state by
 * @param value - value for the state
 */
function setComponentState(componentId, key, value) {
    var componentState = getViewState(componentId);
    if (!componentState) {
        componentState = new Object();
        setViewState(componentId, componentState);
    }
    componentState[key] = value;
}

/**
 * Retrieves the state value for the given key and given component
 *
 * @param componentId - id for the component the key is associated with
 * @param key - name of the state to retrieve
 */
function getComponentState(componentId, key) {
    var componentState = getViewState(componentId);
    if (componentState && componentState.hasOwnProperty(key)) {
        return componentState[key];
    }
    return "";
}

// gets the the label for field with the corresponding id
function getLabel(id){
	var label =  jQuery("#" + id + "_label");
	if(label){
		return label.text();
	}
	else{
		return "";
	}
}
/**
 * runs hidden scripts. The hidden scripts are contained in hidden input elements
 *
 * @param id - the tag id or selector expression to use. If empty, run all hidden scripts
 * @param isSelector - when present and true, the value given for the id used as jquery selector expression
 */
function runHiddenScripts(id, isSelector){
	if(id){
        //run dataScript first always
        jQuery("#" + id).find("input[data-role='dataScript']").each(function(){
            eval(jQuery(this).val());
            jQuery(this).attr("script", "first_run");
            jQuery(this).removeAttr("name");
        });

        var selector = "#" + id;
        if (isSelector && isSelector == true) {
            selector = id;
        }

		jQuery(selector).find("input[name='script']").each(function(){
			eval(jQuery(this).val());
            jQuery(this).attr("script", "first_run");
			jQuery(this).removeAttr("name");
		});

        runScriptsForId(id);

        //reinit dirty fields
        jQuery('#kualiForm').dirty_form({changedClass:kradVariables.DIRTY_CLASS, includeHidden:true});

        //reinitialize BubblePopup
        initBubblePopups();

        //Interpret new server message state for refreshed InputFields and write them out

        jQuery(selector).find("[data-role='InputField']").andSelf().filter("[data-role='InputField']").each(function(){
            var data = jQuery(this).data(kradVariables.VALIDATION_MESSAGES);
            if(!data.processed){
                handleMessagesAtField(jQuery(this).attr('id'), true);
            }
        });
        writeMessagesForPage();
	}
	else{
        //run dataScript first always
        jQuery("input[data-role='dataScript']").each(function(){
            eval(jQuery(this).val());
            jQuery(this).attr("script", "first_run");
            jQuery(this).removeAttr("name");
        });

		jQuery("input[name='script']").each(function(){
			eval(jQuery(this).val());
            jQuery(this).attr("script", "first_run");
			jQuery(this).removeAttr("name");
		});

        //reinitialize BubblePopup
        initBubblePopups();
        //Interpret new server message state for refreshed InputFields
        jQuery("[data-role='InputField']").each(function(){
            var data = jQuery(this).data(kradVariables.VALIDATION_MESSAGES);
            if(data != undefined && !data.processed){
                handleMessagesAtField(jQuery(this).attr('id'));
            }
        });
	}
}

/**
 * runs hidden scripts. The hidden scripts are contained in hidden input elements
 *
 * @param id - the tag id to use
 */
function runScriptsForId(id) {
    if (id) {
        jQuery("input[data-role='dataScript']").each(function () {
            if (jQuery(this).data("for") === id) {
                eval(jQuery(this).val());
                jQuery(this).attr("script", "first_run");
                jQuery(this).removeAttr("name");
            }
        });

        jQuery("input[name='script']").each(function () {
            if (jQuery(this).data("for") === id) {
                eval(jQuery(this).val());
                jQuery(this).attr("script", "first_run");
                jQuery(this).removeAttr("name");
            }
        });
    }
}

function runHiddenScriptsAgain(){
    jQuery("input[data-role='dataScript']").each(function(){
        eval(jQuery(this).val());
        jQuery(this).removeAttr("script");
    });
    jQuery("input[script='first_run']").each(function(){
        eval(jQuery(this).val());
        jQuery(this).removeAttr("script");
    });
}

/**
 * Writes a hidden for property 'methodToCall' set to the given value. This is
 * useful for submitting forms with JavaScript where the methodToCall needs to
 * be set before the form is submitted.
 *
 * @param methodToCall -
 *          the value that should be set for the methodToCall parameter
 */
function setMethodToCall(methodToCall) {
    jQuery("<input type='hidden' name='methodToCall' value='" + methodToCall + "'/>").appendTo(jQuery("#formComplete"));
}

/**
 * Writes a property name/value pair as a hidden input field on the form. Called
 * to dynamically set request parameters based on a chosen action. Assumes
 * existence of a div named 'formComplete' where the hidden inputs will be
 * inserted
 *
 * @param propertyName -
 *          name for the input field to write
 * @param propertyValue -
 *          value for the input field to write
 */
function writeHiddenToForm(propertyName, propertyValue) {
    //removing because of performFinalize bug
    jQuery('input[name="' + escapeName(propertyName) + '"]').remove();

    if (propertyValue.indexOf("'") != -1) {
        jQuery("<input type='hidden' name='" + propertyName + "'" + ' value="' + propertyValue + '"/>').appendTo(jQuery("#formComplete"));
    } else {
        jQuery("<input type='hidden' name='" + propertyName + "' value='" + propertyValue + "'/>").appendTo(jQuery("#formComplete"));
    }
}

/**
 * Retrieves the actual value from the input widget specified by name
 */
function coerceValue(name){
	var value = "";
	var nameSelect = "[name='" + escapeName(name) + "']";
	if(jQuery(nameSelect + ":checkbox").length){
		value = jQuery(nameSelect + ":checked").val();
	}
	else if(jQuery(nameSelect + ":radio").length){
		value = jQuery(nameSelect + ":checked").val();
	}
	else if(jQuery(nameSelect).length){
		if (jQuery(nameSelect).hasClass("watermark")) {
            jQuery.watermark.hide(nameSelect);
			value = jQuery(nameSelect).val();
            jQuery.watermark.show(nameSelect);
		}
		else{
			value = jQuery(nameSelect).val();
		}
	}

	if(value == null){
		value = "";
	}

	return value;
}

/**
 * Sets a value on the control with the given name attribute
 *
 * @param name - name on control to set value for
 * @param value - value to set
 */
function setValue(name, value) {
    var nameSelect = "[name='" + escapeName(name) + "']";
    jQuery(nameSelect).val(value);
}

function isValueEmpty(value){
	if(value != undefined && value != null && value != ""){
		return false;
	}
	else{
		return true;
	}
}

//returns true if the field with name of name1 occurs before field with name2
function occursBefore(name1, name2){
	var field1 = jQuery("[name='" + escapeName(name1) + "']");
	var field2 = jQuery("[name='" + escapeName(name2) + "']");

	field1.addClass("prereqcheck");
	field2.addClass("prereqcheck");

	var fields = jQuery(".prereqcheck");

	field1.removeClass("prereqcheck");
	field2.removeClass("prereqcheck");

	if(fields.index(field1) < fields.index(field2) ){
		return true;
	}
	else{
		return false;
	}
}

/**
 * Validate dirty fields on the form.
 *
 * <p>Whenever the user clicks on the action field which has action methods set to <code>REFRESH,NAVIGATE,CANCEL,CLOSE</code>,
 * form dirtyness is checked. It checks for any input elements which has "dirty" class. If present, it pops a message to
 * the user to confirm whether they want to stay on the page or want to navigate.
 * </p>
 * @param event
 * @returns true if the form has dirty fields
 */
function checkDirty(event){
	var validateDirty = jQuery("[name='validateDirty']").val()
	var dirty = jQuery(".uif-field").find("input.dirty")

	if (validateDirty == "true" && dirty.length > 0)
	{
		var answer = confirm ("Form has unsaved data. Do you want to leave anyway?")
		if (answer == false){
			event.preventDefault();
			event.stopImmediatePropagation();

			//Change the current nav button class to 'current' if user doesn't wants to leave the page
			var ul = jQuery("#" + event.target.id).closest("ul");
			if (ul.length > 0)
			{
				var pageId = jQuery("[name='pageId']").val();
				if(ul.hasClass(kradVariables.TAB_MENU_CLASS)){
					jQuery("#" + ul.attr("id")).selectTab({selectPage : pageId});
				}
				else{
					jQuery("#" + ul.attr("id")).selectMenuItem({selectPage : pageId});
				}
			}
			return true;
		}
	}
	return false;
}

/**
 * Gets the actual attribute id to use element manipulation related to this attribute.
 *
 * @param elementId
 * @param elementType
 */
function getAttributeId(elementId){
	var id = elementId;
	id = elementId.replace(/_control\S*/, "");
	return id;
}

//performs a 'jump' - a scroll to the necessary html element
//The element that is used is based on the hidden value of jumpToId or jumpToName on the form
//if these hidden attributes do not contain a value it jumps to the top of the page by default
function performJumpTo(){
	var jumpToId = jQuery("[name='jumpToId']").val();
	var jumpToName = jQuery("[name='jumpToName']").val();
	if(jumpToId){
		if(jumpToId.toUpperCase() === "TOP"){
			jumpToTop();
		}
		else if(jumpToId.toUpperCase() === "BOTTOM"){
			jumpToBottom();
		}
		else{
			jumpToElementById(jumpToId);
		}
	}
	else if(jumpToName){
		jumpToElementByName(jumpToName);
	}
	else{
		jumpToTop();
	}
}

//performs a focus on an the element with the id preset
function performFocus(){
	var focusId = jQuery("[name='focusId']").val();
	if(focusId){
		jQuery("#" + focusId).focus();
	}
	else{
		jQuery("[data-role='InputField'] .uif-control:visible:first", "#kualiForm").focus();
	}
}

//performs a focus on an the element with the name specified
function focusOnElementByName(name){
	var theElement =  jQuery("[name='" + escapeName(name) + "']");
	if(theElement.length != 0){
		theElement.focus();
	}
}

//performs a focus on an the element with the id specified
function focusOnElementById(focusId){
	if(focusId){
		jQuery("#" + focusId).focus();
	}
}

//Jump(scroll) to an element by name
function jumpToElementByName(name){
	var theElement =  jQuery("[name='" + escapeName(name) + "']");
	if(theElement.length != 0){
		if(top == self || jQuery("#fancybox-frame", parent.document).length){
            jQuery.scrollTo(theElement, 0);
		}
		else{
			var headerOffset = top.jQuery("#header").outerHeight(true) + top.jQuery(".header2").outerHeight(true);
			top.jQuery.scrollTo(theElement, 0, {offset: {top:headerOffset}});
		}
	}
}

//Jump(scroll) to an element by Id
function jumpToElementById(id){
	var theElement =  jQuery("#" + id);
	if(theElement.length != 0){
		if(top == self || jQuery("#fancybox-frame", parent.document).length){
            jQuery.scrollTo(theElement, 0);
		}
		else{
			var headerOffset = top.jQuery("#header").outerHeight(true) + top.jQuery(".header2").outerHeight(true);
			top.jQuery.scrollTo(theElement, 0, {offset: {top:headerOffset}});
		}
	}
}

//Jump(scroll) to the top of the current screen
function jumpToTop(){
	if(top == self || jQuery("#fancybox-frame", parent.document).length){
        jQuery.scrollTo(jQuery("html"), 0);
	}
	else{
		top.jQuery.scrollTo(top.jQuery("html"), 0);
	}
}

//Jump(scroll) to the bottom of the current screen
function jumpToBottom(){
	if(top == self || jQuery("#fancybox-frame", parent.document).length){
        jQuery.scrollTo("max", 0);
	}
	else{
		top.jQuery.scrollTo("max", 0);
	}
}

// The following javascript is intended to resize the route log iframe
// to stay at an appropriate height based on the size of the documents
// contents contained in the iframe.
// NOTE: this will only work when the domain serving the content of kuali
// is the same as the domain serving the content of workflow.
var routeLogResizeTimer = "";
var currentHeight = 500;
var safari = navigator.userAgent.toLowerCase().indexOf('safari');

function setRouteLogIframeDimensions() {
    var routeLogFrame = document.getElementById("routeLogIFrame");
    var routeLogFrame = document.getElementById("routeLogIFrame");
    var routeLogFrameWin = window.frames["routeLogIFrame"];
    var frameDocHeight = 0;
    try {
        frameDocHeight = routeLogFrameWin.document.documentElement.scrollHeight;
    } catch (e) {
        // unable to set due to cross-domain scripting
        frameDocHeight = 0;
    }

    if (frameDocHeight > 0) {
        if (routeLogFrame && routeLogFrameWin) {

            if ((Math.abs(frameDocHeight - currentHeight)) > 30) {
                if (safari > -1) {
                    if ((Math.abs(frameDocHeight - currentHeight)) > 59) {
                        routeLogFrame.style.height = (frameDocHeight + 30) + "px";
                        currentHeight = frameDocHeight;
                    }
                } else {
                    routeLogFrame.style.height = (frameDocHeight + 30) + "px";
                    currentHeight = frameDocHeight;
                }
            }
        }
    }

    if (routeLogResizeTimer == "") {
        routeLogResizeTimer = setInterval("resizeTheRouteLogFrame()", 300);
    }
}

function resizeTheRouteLogFrame() {
    setRouteLogIframeDimensions();
}

/**
 * Adds or adds value to the attribute on the element.
 *
 * @param id - element id
 * @param attributeName - name of the attribute to add/add to
 * @param attributeValue - value of the attribute
 * @param concatFlag - indicate if value should be added to current value
 */
function addAttribute(id, attributeName, attributeValue, concatFlag) {
    hasAttribute = jQuery("#" + id).is('[' + attributeName + ']');
    if (concatFlag && hasAttribute) {
        jQuery("#" + id).attr(attributeName, jQuery("#" + id).attr(attributeName) + " " + attributeValue);
    }else{
        jQuery("#" + id).attr(attributeName, attributeValue);
    }
}

/**
 * Open new browser window for the specified help url
 *
 * The help window is positioned in the center of the screen and resized to 1/4th of the screen.
 *
 * Browsers don't allow one to modify windows of other domains.  Thus to ensure that only one help window exist
 * and to guarantee it's placement, size and that the window is in the foreground the following process is performed:
 *   1) open the help window - this ensures that we get a window handle to any existing help window
 *   2) close the help window
 *   3) open a new help window with the correct placement, size and url
 *
 * @param url - url of the help window content
 */
function openHelpWindow(url) {
    var windowWidth =  screen.availWidth/2;
    var windowHeight = screen.availHeight/2;
    var windowPositionY = parseInt((screen.availWidth/2) - (windowWidth/2));
    var windowPositionX = parseInt((screen.availHeight/2) - (windowHeight/2));

    var windowUrl = url;
    var windowName = 'HelpWindow';
    var windowOptions = 'width=' + windowWidth + ',height=' + windowHeight + ',top=' + windowPositionX + ',left=' + windowPositionY + ',scrollbars=yes,resizable=yes';

    var myWindow = window.open('', windowName);
    myWindow.close()
    myWindow = window.open(windowUrl, windowName, windowOptions);
}

/**
 * Uppercases the current value for the control with the given id
 *
 * @param controlId - id for the control whose value should be uppercased
 */
function uppercaseValue(controlId) {
    jQuery("#" + controlId).css('text-transform', 'uppercase');
}