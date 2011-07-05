/*
 * Copyright 2006-2007 The Kuali Foundation
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
		context = parent.$;
	}
	return context;
}

//gets the the label for field with the corresponding id
function getLabel(id){
	var label =  jq("#" + id + "_label");
	if(label){
		return label.text();
	}
	else{
		return "";
	}
}

function runHiddenScripts(id){
	if(id){
		jq("#" + id).find("input[name='script']").each(function(){
			eval(jq(this).val());
			jq(this).removeAttr("name");
		});
	}
	else{
		jq("input[name='script']").each(function(){
			eval(jq(this).val());
			jq(this).removeAttr("name");
		});
	}
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
    jq("<input type='hidden' name='methodToCall' value='" + methodToCall + "'/>").appendTo(jq("#formComplete"));
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
    jq('input[name="' + propertyName + '"]').remove();

    if (propertyValue.indexOf("'") != -1) {
        jq("<input type='hidden' name='" + propertyName + "'" + ' value="' + propertyValue + '"/>').appendTo(jq("#formComplete"));
    } else {
        jq("<input type='hidden' name='" + propertyName + "' value='" + propertyValue + "'/>").appendTo(jq("#formComplete"));
    }
}

/**
 * Retrieves the actual value from the input widget specified by name
 */
function coerceValue(name){
	var value = "";
	var nameSelect = "[name='" + name + "']";
	if(jq(nameSelect + ":checkbox").length){
		value = jq(nameSelect + ":checked").val();
	}
	else if(jq(nameSelect + ":radio").length){
		value = jq(nameSelect + ":checked").val();
	}
	else if(jq(nameSelect).length){
		if (jq(nameSelect).hasClass("watermark")) {
			jq.watermark.hide(nameSelect);
			value = jq(nameSelect).val();
			jq.watermark.show(nameSelect);
		}
		else{
			value = jq(nameSelect).val();
		}
	}
	if(value == null){
		value = "";
	}

	return value;
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
	var field1 = jq("[name='" + name1 + "']");
	var field2 = jq("[name='" + name2 + "']");

	field1.addClass("prereqcheck");
	field2.addClass("prereqcheck");

	var fields = jq(".prereqcheck");

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
	var validateDirty = jq("[name='validateDirty']").val()
	var dirty = jq(".field_attribute").find("input.dirty")

	if (validateDirty == "true" && dirty.length > 0)
	{
		var answer = confirm ("Form has unsaved data. Do you want to leave anyway?")
		if (answer == false){
			event.preventDefault();
			event.stopImmediatePropagation();

			//Change the current nav button class to 'current' if user doesn't wants to leave the page
			var ul = jq("#" + event.target.id).closest("ul");
			if (ul.length > 0)
			{
				var pageId = jq("[name='pageId']").val();
				if(ul.hasClass("tabMenu")){
					jq("#" + ul.attr("id")).selectTab({selectPage : pageId});
				}
				else{
					jq("#" + ul.attr("id")).selectMenuItem({selectPage : pageId});
				}
			}
			return true;
		}
	}
	return false;
}

/**
 * Gets the actual attribute id to use element manipulation related to this attribute.
 * This method is necessary due to radio/checkboxes appending an additional suffix to the
 * id, and the hook being the base id without this suffix
 *
 * @param elementId
 * @param elementType
 */
function getAttributeId(elementId, elementType){
	var id = elementId;
	if(elementType == "radio" || elementType == "checkbox"){
		id = elementId.replace(/_attribute\S*/, "");
	}
	return id;
}

//performs a 'jump' - a scroll to the necessary html element
//The element that is used is based on the hidden value of jumpToId or jumpToName on the form
//if these hidden attributes do not contain a value it jumps to the top of the page by default
function performJumpTo(){
	var jumpToId = jq("[name='jumpToId']").val();
	var jumpToName = jq("[name='jumpToName']").val();
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

//performs a focus on an the element with the id specified
function performFocus(){
	var focusId = jq("[name='focusId']").val();
	if(focusId){
		jq("#" + focusId).focus();
	}
	else{
		jq("input:not(input[type='button'], input[type='submit']):visible:first", "#kualiForm").focus();
	}
}

//Jump(scroll) to an element by name
function jumpToElementByName(name){
	var theElement =  jq("[name='" + name + "']");
	if(theElement.length != 0){
		if(top == self || jq("#fancybox-frame", parent.document).length){
			jq.scrollTo(theElement, 0);
		}
		else{
			var headerOffset = top.$("#header").outerHeight(true) + top.$(".header2").outerHeight(true);
			top.$.scrollTo(theElement, 0, {offset: {top:headerOffset}});
		}
	}
}

//Jump(scroll) to an element by Id
function jumpToElementById(id){
	var theElement =  jq("#" + id);
	if(theElement.length != 0){
		if(top == self || jq("#fancybox-frame", parent.document).length){
			jq.scrollTo(theElement, 0);
		}
		else{
			var headerOffset = top.$("#header").outerHeight(true) + top.$(".header2").outerHeight(true);
			top.$.scrollTo(theElement, 0, {offset: {top:headerOffset}});
		}
	}
}

//Jump(scroll) to the top of the current screen
function jumpToTop(){
	if(top == self || jq("#fancybox-frame", parent.document).length){
		jq.scrollTo(jq("html"), 0);
	}
	else{
		top.$.scrollTo(top.$("html"), 0);
	}
}

//Jump(scroll) to the bottom of the current screen
function jumpToBottom(){
	if(top == self || jq("#fancybox-frame", parent.document).length){
		jq.scrollTo("max", 0);
	}
	else{
		top.$.scrollTo("max", 0);
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
