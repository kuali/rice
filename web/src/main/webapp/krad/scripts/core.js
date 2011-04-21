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
// Toggles a tab to show / hide and changes the source image to properly reflect this
// change. Returns false to avoid post. Example usage:
// onclick="javascript: return toggleTab(document, this, ${currentTabIndex}) }
function toggleTab(doc, tabKey) {
	if (doc.forms[0].elements['tabStates(' + tabKey + ')'].value == 'CLOSE') {
        showTab(doc, tabKey);
    } else {
        hideTab(doc, tabKey);
	}
	return false;
}

/** expands all tabs by unhiding them. */
function expandAllTab() {
	doToAllTabs(showTab);
	return false;
	}

/** collapses all tab by hiding them. */
function collapseAllTab() {
	doToAllTabs(hideTab);
	return false;
}

/**
 * executes a function on all tabs. The function will be passed a document &
 * partial tab name.
 */
function doToAllTabs(func) {
	var elements = document.getElementsByTagName('div');
	
	for (var x in elements) {
		if (elements[x].id && elements[x].id.substring(0, 4) === 'tab-' 
			&& elements[x].id.substring(elements[x].id.length - 4, elements[x].id.length) === '-div') {
			func(document, elements[x].id.substring(4, elements[x].id.length - 4));
		}
	}
	return false;
}

function showTab(doc, tabKey) {
    if (!doc.getElementById('tab-' + tabKey + '-div') || !doc.getElementById('tab-' + tabKey + '-imageToggle')) {
		return false;
	}
	
    // replaced 'block' with '' to make budgetExpensesRow.tag happy.
    doc.getElementById('tab-' + tabKey + '-div').style.display = '';
    doc.forms[0].elements['tabStates(' + tabKey + ')'].value = 'OPEN';
    var image = doc.getElementById('tab-' + tabKey + '-imageToggle');
    image.src = jsContextPath + '/kr/images/tinybutton-hide.gif';
    image.alt = image.alt.replace(/^show/, 'hide');
    image.alt = image.alt.replace(/^open/, 'close');
    image.title = image.title.replace(/^show/, 'hide');
    image.title = image.title.replace(/^open/, 'close');
    return false;
}

function hideTab(doc, tabKey) {
    if (!doc.getElementById('tab-' + tabKey + '-div') || !doc.getElementById('tab-' + tabKey + '-imageToggle')) {
		return false;
	}
	
    doc.getElementById('tab-' + tabKey + '-div').style.display = 'none';
    doc.forms[0].elements['tabStates(' + tabKey + ')'].value = 'CLOSE';
    var image = doc.getElementById('tab-' + tabKey + '-imageToggle');
    image.src = jsContextPath + '/kr/images/tinybutton-show.gif';
    image.alt = image.alt.replace(/^hide/, 'show');
    image.alt = image.alt.replace(/^close/, 'open');
    image.title = image.title.replace(/^hide/, 'show');
    image.title = image.title.replace(/^close/, 'open');
    return false;
}

function setIframeAnchor(iframeName) {
  var iframeWin = window.frames[iframeName];
  if (iframeWin && iframeWin.location.href.indexOf("#") > -1) {
    iframeWin.location.replace(iframeWin.location);
  }  
}

/*function jumpToAnchorName(anchor){
	var anchors = document.getElementsByName(anchor);
	if (anchors != null)
		location.href = '#'+anchors[0].name;
}*/

var formHasAlreadyBeenSubmitted = false;
var excludeSubmitRestriction = false;

function hasFormAlreadyBeenSubmitted() {
// alert( "submitting form" );
	try {
		// save the current scroll position
		saveScrollPosition();
	} catch ( ex ) {
		// do nothing - don't want to stop submit
	}

	if ( document.getElementById( "formComplete" ) ) { 
	    if (formHasAlreadyBeenSubmitted && !excludeSubmitRestriction) {
	       alert("Page already being processed by the server.");
	       return false;
	    } else {
	       formHasAlreadyBeenSubmitted = true;
	       return true;
	    }
	    excludeSubmitRestriction = false;
    } else {
	       alert("Page has not finished loading.");
	       return false;
	} 
}

// Called when we want to submit the form from a field event and
// want focus to be placed on the next field according to the current tab order
// when the page refreshes
function setFieldToFocusAndSubmit(triggerElement) {
	if(document.forms[0].fieldNameToFocusOnAfterSubmit) {
		if (document.forms.length > 0) {
			var nextTabField;
			var field = document.forms[0];
			for (i = 0; i < field.length; i++) {
				if (field.elements[i].tabIndex > triggerElement.tabIndex) {
					if (nextTabField) {
						if (field.elements[i].tabIndex < nextTabField.tabIndex) {
					       nextTabField = field.elements[i];
			         	}
					}
		       	    else {
				       nextTabField = field.elements[i];
			        }
				}	
			}
	
	        if (nextTabField) {
	        	document.forms[0].fieldNameToFocusOnAfterSubmit.value = nextTabField.name;
	        }
		}	
	}
	
    document.forms[0].submit();
}

//Submits the form through an ajax submit, the response is the new page html
//runs all hidden scripts passed back (this is to get around a bug with pre mature
//script evaluation)
function submitForm() {
	jq("#kualiForm").ajaxSubmit({
		success: function(response){
			var tempDiv = document.createElement('div');
			tempDiv.innerHTML = response;
			var page = jq("#viewpage_div", tempDiv);
			jq("#viewpage_div").replaceWith(page);
			jq("#formComplete").html("");
			runHiddenScripts("viewpage_div");
		}
	});
}

//Called when a form is being persisted to assure all validation passes
function validateAndSubmit(){
	jq.watermark.hideAll();
	
	if(jq("#kualiForm").valid()){
		jq.watermark.showAll();
		submitForm();
	}
	else{
		jq.watermark.showAll();
		jumpToTop();
		alert("The form contains errors.  Please correct these errors and try again.");
		
	}
}

//saves the current form by first validating client side and then attempting an ajax submit
function saveForm(){
	writeHiddenToForm("methodToCall", "save");
	validateAndSubmit();
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

//Jump(scroll) to an element by name
function jumpToElementByName(name){
	if(top == self){
		jq.scrollTo(jq("[name='" + name + "']"), 0);
	}
	else{
		var headerOffset = top.$("#header").outerHeight(true) + top.$(".header2").outerHeight(true);
		top.$.scrollTo(jq("[name='" + name + "']"), 0, {offset: {top:headerOffset}});
	}
}

//Jump(scroll) to an element by Id
function jumpToElementById(id){
	if(top == self){
		jq.scrollTo(jq("#" + id), 0);
	}
	else{
		var headerOffset = top.$("#header").outerHeight(true) + top.$(".header2").outerHeight(true);
		top.$.scrollTo(jq("#" + id), 0, {offset: {top:headerOffset}});
	}
}

//Jump(scroll) to the top of the current screen
function jumpToTop(){
	if(top == self){
		jq.scrollTo(jq("html"), 0);
	}
	else{
		top.$.scrollTo(top.$("html"), 0);
	}
}

//Jump(scroll) to the bottom of the current screen
function jumpToBottom(){
	if(top == self){
		jq.scrollTo("max", 0);
	}
	else{
		top.$.scrollTo("max", 0);
	}
}

function saveScrollPosition() {
// alert( document.forms[0].formKey );
	if ( document.forms[0].formKey ) {
		formKey = document.forms[0].formKey.value;
		if( document.documentElement ) { 
			x = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft); 
		  	y = Math.max(document.documentElement.scrollTop, document.body.scrollTop); 
		} else if( document.body && typeof document.body.scrollTop != "undefined" ) { 
			x = document.body.scrollLeft; 
		  	y = document.body.scrollTop; 
		} else if ( typeof window.pageXOffset != "undefined" ) { 
			x = window.pageXOffset; 
		  	y = window.pageYOffset; 
		} 
		document.cookie = "KulScrollPos"+formKey+"="+x+","+y+"; path="+document.location.pathname;
	}
	// test read cookie back
// matchResult = document.cookie.match(new
// RegExp("KulScrollPos"+formKey+"=([^;]+);?"));
// if ( matchResult ) {
// alert( "Cookie: " + matchResult[1] );
// }
}

function restoreScrollPosition() {
    if ( document.forms[0].formKey ) {
        formKey = document.forms[0].formKey.value;
        var cookieName = "KulScrollPos"+formKey;
        var matchResult = document.cookie.match(new RegExp(cookieName+"=([^;]+);?"));
        if ( matchResult ) {
            var coords = matchResult[1].split( ',' );
            window.scrollTo(coords[0],coords[1]);
            expireCookie( cookieName );
            return true;
        } else { // check for entry before form key set
        	cookieName = "KulScrollPos";
	        var matchResult = document.cookie.match(new RegExp(cookieName+"=([^;]+);?"));
	        if ( matchResult ) {
	            var coords = matchResult[1].split( ',' );
	            window.scrollTo(coords[0],coords[1]);
	            expireCookie( cookieName );
	            return true;
	        }
        }
    }
    return false;
}

function expireCookie( cookieName ) {
	var date = new Date();
	date.setTime( date.getTime() - 60000 );
	document.cookie = cookieName+"=0,0; expires="+date.toGMTString()+"; path="+document.location.pathname;
}

/*
 * script to prevent the return key from submitting a form unless the user is on
 * a button or on a link. fix for KULFDBCK-555
 */ 
function isReturnKeyAllowed(buttonPrefix , event) {
	/* use IE naming first then firefox. */
    var elemType = event.srcElement ? event.srcElement.type : event.target.type;
    if (elemType != null && elemType.toLowerCase() == 'textarea') {
      // KULEDOCS-1728: textareas need to have the return key enabled
      return true;
    }
	var initiator = event.srcElement ? event.srcElement.name : event.target.name;
	var key = event.keyCode;
	/*
	 * initiator is undefined check is to prevent return from doing anything if
	 * not in a form field since the initiator is undefined
	 */
	/* 13 is return key code */
	/* length &gt; 0 check is to allow user to hit return on links */
	if ( key == 13 ) {
		if( initiator == undefined || ( initiator.indexOf(buttonPrefix) != 0 && initiator.length > 0) ) {
		  // disallow enter key from fields that dont match prefix.
		  return false;
		}
	}
    return true;
}

// The following javascript is intended to resize the route log iframe
// to stay at an appropriate height based on the size of the documents
// contents contained in the iframe.
// NOTE: this will only work when the domain serving the content of kuali
// is the same as the domain serving the content of workflow.
var routeLogResizeTimer = ""; // holds the timer for the route log iframe
															// resizer
var currentHeight = 500; // holds the current height of the iframe
var safari = navigator.userAgent.toLowerCase().indexOf('safari');

function setRouteLogIframeDimensions() {
  var routeLogFrame = document.getElementById("routeLogIFrame");
  var routeLogFrame = document.getElementById("routeLogIFrame");
  var routeLogFrameWin = window.frames["routeLogIFrame"];
  var frameDocHeight = 0;
  try {
    frameDocHeight = routeLogFrameWin.document.documentElement.scrollHeight;
  } catch ( e ) {
    // unable to set due to cross-domain scripting
    frameDocHeight = 0;
  }

  if ( frameDocHeight > 0 ) {
	  if (routeLogFrame && routeLogFrameWin) {
	  	
	    if ((Math.abs(frameDocHeight - currentHeight)) > 30 ) {
	      if (safari > -1) {
	        if ((Math.abs(frameDocHeight - currentHeight)) > 59 ) {
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
	  
	    if (routeLogResizeTimer == "" ) {
	      routeLogResizeTimer = setInterval("resizeTheRouteLogFrame()",300);
	    }
	  }

function resizeTheRouteLogFrame() {
  setRouteLogIframeDimensions();
}

// should be in rice for direct inquiry
 function inquiryPop(boClassName, inquiryParameters){
  parameterPairs = inquiryParameters.split(",");
  queryString="businessObjectClassName="+boClassName+"&methodToCall=start"
  for (i in parameterPairs) {
  
    parameters = parameterPairs[i].split(":");
  	if (document.forms[0].elements[parameters[0]].value=="") 
  	{
  		alert("Please enter a value in the appropriate field.");
  		// queryString=queryString+"&"+parameters[1]+"=directInquiryParameterNotSpecified";
		return false;
  	} else {
    	queryString=queryString+"&"+parameters[1]+"="+document.forms[0].elements[parameters[0]].value;
  	}
  }
  url=window.location.href
  pathname=window.location.pathname
  idx1=url.indexOf(pathname);
  idx2=url.indexOf("/",idx1+1);
  baseUrl=url.substr(0,idx2)
  window.open(baseUrl+"/kr/directInquiry.do?"+queryString, "_blank", "width=640, height=600, scrollbars=yes");
}
 
function textAreaPop(textAreaName, htmlFormAction, textAreaLabel, docFormKey, textAreaReadOnly, textAreaMaxLength) {
	
	if (textAreaReadOnly === null || textAreaReadOnly === undefined) {
		textAreaReadOnly = false;
	}
	
	if (textAreaMaxLength === null || textAreaMaxLength === undefined) {
		textAreaMaxLength = "";
	}
	
	var documentWebScope="session"
	window.open("updateTextArea.do?textAreaFieldName="+textAreaName+"&htmlFormAction="+htmlFormAction+"&textAreaFieldLabel="+textAreaLabel+"&docFormKey="+docFormKey+"&documentWebScope="+documentWebScope+"&textAreaReadOnly="+textAreaReadOnly+"&textAreaMaxLength="+textAreaMaxLength, "_blank", "width=650, height=650, scrollbars=yes");
}

function setTextArea(textAreaName) {
  document.getElementById(textAreaName).value = window.opener.document.getElementById(textAreaName).value; 
}

function textLimit(taElement, maxlen) 
{
	var fieldValue = taElement.value;
    if (fieldValue.length > maxlen) 
    { 
	    taElement.value = taElement.value.substr(0, maxlen); 
    } 
} 

function postValueToParentWindow(textAreaName) {
  window.opener.document.getElementById(textAreaName).value = document.getElementById(textAreaName).value; 
  self.close();
}

function showHide(showId,hideId){
  var style_sheet = getStyleObject(showId);
  if (style_sheet)
  {
	changeObjectVisibility(showId, "block");
	changeObjectVisibility(hideId, "none");
  }
  else 
  {
    alert("sorry, this only works in browsers that do Dynamic HTML");
  }
}

function changeObjectVisibility(objectId, newVisibility) {
    // first get the object's stylesheet
    var styleObject = getStyleObject(objectId);

    // then if we find a stylesheet, set its visibility
    // as requested
    //
    if (styleObject) {
		styleObject.display = newVisibility;
	return true;
    } else {
	return false;
    } 
}

function getStyleObject(objectId) {
  // checkW3C DOM, then MSIE 4, then NN 4.
  //
  if(document.getElementById && document.getElementById(objectId)) {
	return document.getElementById(objectId).style;
   }
   else if (document.all && document.all(objectId)) {  
	return document.all(objectId).style;
   } 
   else if (document.layers && document.layers[objectId]) { 
	return document.layers[objectId];
   } else {
	return false;
   }
}

function placeFocus() {
	if (document.forms.length > 0) {
	  var fieldNameToFocus;
	  if (document.forms[0].fieldNameToFocusOnAfterSubmit) {
	    fieldNameToFocus = document.forms[0].fieldNameToFocusOnAfterSubmit.value;
	  }
	  
	  var focusSet = false;
	  var field = document.forms[0];
	  for (i = 0; i < field.length; i++) {
		if (fieldNameToFocus) {
	  	  if (field.elements[i].name == fieldNameToFocus) {
			  document.forms[0].elements[i].focus();
			  focusSet = true;
		  }	 
		}
		else if ((field.elements[i].type == "text") || (field.elements[i].type == "textarea")) {
		  document.forms[0].elements[i].focus();
		  focusSet = true;
		}
		
		if (focusSet) {
			break;
		}
	  }
   }
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
	/*
setMethodToCall(methodToCall);
*/
	submitForm();
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
	jq('input[name="' +propertyName + '"]').remove();
	if (propertyValue.indexOf("'") != -1) {
		jq("<input type='hidden' name='" + propertyName + "'" + ' value="' + propertyValue + '"/>').appendTo(jq("#formComplete"));
	}else{
	jq("<input type='hidden' name='" + propertyName + "' value='" + propertyValue + "'/>").appendTo(jq("#formComplete"));
}
}

/**
 * Expands all the accordion divs on the page
 */
function expandAccordions() {
	jq('img[alt="collapse"]').click();
}

/**
 * Collapses all the accordion divs on the page
 */
function collapseAccordions() {
	jq('img[alt="expand"]').click();
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

function getAttributeId(elementId, elementType){
	var id = elementId;
	if(elementType == "radio" || elementType == "checkbox"){
		id = elementId.replace(/_attribute\S*/, "");
	}
	return id;
}

//sets up the validator with the necessary default settings and methods
//note the use of onClick and onFocusout for on the fly validation client side
function setupValidator(){
	jq('#kualiForm').validate(
	{ 
		onsubmit: false,
		onclick: function(element) { 
			jq(element).valid();
			dependsOnCheck(element); 
		}, 
		onfocusout: function(element) {
			jq(element).valid();
			dependsOnCheck(element); 
		},
		wrapper: "li",
		highlight: function(element, errorClass, validClass) {
			jq(element).addClass(errorClass).removeClass(validClass);
			applyErrorColors(getAttributeId(element.id, element.type) + "_errors_div", 1, 0, 0, true);
			showFieldIcon(getAttributeId(element.id, element.type) + "_errors_div", 1);
		},
		unhighlight: function(element, errorClass, validClass) {
			jq(element).removeClass(errorClass).addClass(validClass);
			applyErrorColors(getAttributeId(element.id, element.type) + "_errors_div", 0, 0, 0, true);
			showFieldIcon(getAttributeId(element.id, element.type) + "_errors_div", 0);
		},
		errorPlacement: function(error, element) {
			var id = getAttributeId(element.attr('id'), element.attr('type'));
			//check to see if the option to use labels is on
			if (!jq("#" + id + "_errors_div").hasClass("noLabels")) {
				var label = getLabel(id);
				label = jq.trim(label);
				if (label) {
					if (label.charAt(label.length - 1) == ":") {
						label = label.slice(0, -1);
					}
					error.find("label").before(label + " - ");
				}
			}
			jq("#" + id + "_errors_div").show();
			jq("#" + id + "_errors_errorMessages").show();
			var errorList = jq("#" + id + "_errors_errorMessages ul");
			error.appendTo(errorList);
		}
	});
	jq.watermark.showAll();
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

//checks to see if any fields depend on the field being validated, if they do calls validate
//on them as well which will either add errors or remove them
//Note: with the way that validation work the field must have been previously validated (ie validated)
function dependsOnCheck(element){
	var name = jq(element).attr('name');
	jq(".dependsOn-" + name).each(function(){
		if (jq(this).hasClass("valid") || jq(this).hasClass("error")) {
			jq.watermark.hide(this);
			jq(this).valid();
			jq.watermark.show(this);
		}
	});
}

//checks to see if the fields with names specified in the name array contain a value
//if they do - returns the total if the num of fields matched
function mustOccurTotal(nameArray, min, max){
	var total = 0;
	for(i=0; i < nameArray.length; i++){
		if(coerceValue(nameArray[i])){
			total++;
		}
	}
	
	return total;

}

//checks to see if the fields with names specified in the name array contain a value
//if they do - returns 1 if the num of fields with values are between min/max
//this function is used to for mustoccur constraints nested in others
function mustOccurCheck(total, min, max){
	
	if (total >= min && total <= max) {
		return 1;
	}
	else {
		return 0;
	}
}

//returns true if the field with name of name1 occurs before field with name2
function occursBefore(name1, name2){
	var field1 = jq("[name=" + name1 + "]");
	var field2 = jq("[name=" + name2 + "]");
	
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