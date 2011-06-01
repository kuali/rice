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

// global vars
var $dialog = null;
var jq = jQuery.noConflict();

//BlockUi defaults
var loadingMessage =  '<h1><img src="/kr-dev/krad/images/loading.gif" alt="working..." />Loading...</h1>';
var savingMessage = '<h1><img src="/kr-dev/krad/images/loading.gif" alt="working..." />Saving...</h1>';
//Custom built in validator methods
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

window.onerror = errorHandler;

function errorHandler(msg,url,lno)
{
  if (top == self) {
	  jq.unblockUI();
	  jq.jGrowl('A javascript error occured: <br/>'  + msg);
  }
  else{
	  top.$.unblockUI(); 
	  top.$.jGrowl('A javascript error occured: <br/>'  + msg);
  }
  return false;
}

// common event registering done here through JQuery ready event
jq(document).ready(function() {
	window.globalReadyCalled = true;
	createLoading(false);
	setPageBreadcrumb();
	
	// buttons
	jq("input:submit").button();
	jq("input:button").button();
	jq.ajaxSetup({
		  beforeSend: function() {
		     createLoading(true);
		  },
		  complete: function(){
			 createLoading(false);
		  }
	});
	
	runHiddenScripts("");
	
});

jq(document).unload(function() {
	
});

jQuery.fn.dataTableExt.oSort['kuali_date-asc']  = function(a,b) {
	var ukDatea = a.split('/');
	var ukDateb = b.split('/');
	var x = (ukDatea[2] + ukDatea[0] + ukDatea[1]) * 1;
	var y = (ukDateb[2] + ukDateb[0] + ukDateb[1]) * 1;
	return ((x < y) ? -1 : ((x > y) ?  1 : 0));
};
	 
jQuery.fn.dataTableExt.oSort['kuali_date-desc'] = function(a,b) {
	var ukDatea = a.split('/');
	var ukDateb = b.split('/');
	var x = (ukDatea[2] + ukDatea[0] + ukDatea[1]) * 1;
	var y = (ukDateb[2] + ukDateb[0] + ukDateb[1]) * 1;
	return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
};
	
jQuery.fn.dataTableExt.afnSortData['dom-text'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+') input', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		aData.push( this.value );
	} );
	return aData;
}

/* Create an array with the values of all the select options in a column */
jQuery.fn.dataTableExt.afnSortData['dom-select'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+') select', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		aData.push( jq(this).val() );
	} );
	return aData;
}

/* Create an array with the values of all the checkboxes in a column */
jQuery.fn.dataTableExt.afnSortData['dom-checkbox'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+') input', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		aData.push( this.checked==true ? "1" : "0" );
	} );
	return aData;
}


function resizeDialog() {
	var width = jq(document).width();
	var height = jq(document).height() + 50;
	
	window.parent.$dialog.dialog('option', 'width', width);
	window.parent.$dialog.dialog('option', 'height', height);
}


/**
 * Sets the breadcrumb to whatever the current page is, if this view has page navigation
 * Pages are handled by js on the breadcrumb because the page retrieval happens through
 * ajax
 */
function setPageBreadcrumb(){
	//check to see if page has navigation element, if so show breadcrumb
	if(jq("#viewnavigation_div").html() && jq("#breadcrumbs").length){
		var pageTitle = jq("#currentPageTitle").val();
		var pageId = jq("#pageId").val();
		jq("#breadcrumbs").find("#page_breadcrumb").remove();
		var bcSet = false;
		if(pageTitle){
			jq("#breadcrumbs").append("<span id='page_breadcrumb'> &raquo; <span class='current'>" + pageTitle + "</span></span>");
			jq("#current_breadcrumb_span").hide();
			jq("#current_breadcrumb_anchor").show();
			bcSet = true;
		}
		else if(pageId){
			pageTitle = jq("a[name='"+ pageId + "']").text();
			if(pageTitle){
				jq("#breadcrumbs").append("<span id='page_breadcrumb'> � <span class='current'>" + pageTitle + "</span></span>");
				jq("#current_breadcrumb_span").hide();
				jq("#current_breadcrumb_anchor").show();
				bcSet=true;
			}
		}
		
		if(!bcSet){
			jq("#current_breadcrumb_anchor").hide();
			jq("#current_breadcrumb_span").show();
		}
	}
}

/**
 * Sets the click handler for all inquiry URLs to open in a JQuery dialog
 */
function initializeInquiryHandlers() {
	// select all links pointing to the inquiry action
	jq('a[href*=inquiry.do]').click(function(e) {
		// cancel the link behavior
		e.preventDefault();

		// get the inquiry link target
		var href = jq(this).attr('href') + '&dialogMode=true';
		showIFrameDialog(href, 'Inquiry');
	})
}

function initializeLookupHandlers() {
	// select inputs of type 'image' that have performLookup in name
	jq(':input[type=image][name*=performLookup]')
			.click(
					function(e) {
						// cancel the submit behavior
						e.preventDefault();

						// build lookup URL from input name
						var name = jq(this).attr('name');

						var href = 'lookup.do?dialogMode=true&returnLocation=portal.jsp&methodToCall=start&businessObjectClassName=';
						var businessObjectClass = substringBetween(name, '(!!', '!!)');
						if (businessObjectClass != null) {
							href += businessObjectClass;
						}

						href += '&conversionFields=';
						var conversionFields = substringBetween(name, '(((', ')))');
						if (conversionFields != null) {
							href += conversionFields;
						}

						showIFrameDialog(href, 'Lookup Records');
					})
}

function initializeReturnHandlers() {
	// select links that have 'refreshCaller' parameter in href
	jq('a[href*=refreshCaller]').click(function(e) {
		// cancel the link behavior
		e.preventDefault();

		// get the inquiry link target
		var href = jq(this).attr('href');

		// parse out return parameters and set form values
		var parameterString = href.substring(href.indexOf('?') + 1);
		var parameters = parameterString.split('&');
		for ( var i = 0; i < parameters.length; i++) {
			var keyValue = parameters[i].split('=');
			var parameterName = keyValue[0];
			var parameterValue = keyValue[1];
			// TODO: this filtering isn't working, need a way to filter out parms we
			// don't want to populate
			if (window.parent.jq('[name=' + parameterName + ']')) {
				// set focus to remove any water-marks
				window.parent.jq('[name=' + parameterName + ']').focus();
				window.parent.jq('[name=' + parameterName + ']').val(parameterValue);
			}
		}

		// close dialog
		window.parent.$dialog.dialog('close');
	})
}

/**
 * Constructs the source for an iframe pointing to the given URL and then
 * creates/opens a JQuery dialog for the iframe
 */
function showIFrameDialog(href, title) {
	var width = (jq(document).width() / 4) * 3;
	var height = (jq(document).height() / 4) * 3;

	if (dialogMode) {
		width = jq(window).width();
		height = jq(window).height();
	}

	var iframe = "<div><iframe id='dialogIFrame' src='" + href + "' height='100%' width='100%'/></div>";
			
// var iframe = "<div><iframe id='dialogIFrame' src='" + href + "' height='"
// + height + "px' width='" + width + "px'/></div>";

	// create dialog with iframe source
	if (dialogMode) {
		$dialog = window.parent.jq(iframe).dialog({
			autoOpen : false,
			draggable : true,
			resizable : true,
			modal : true,
			minWidth : width + 2,
			minHeight : height + 2,
			title : title
		});

	} else {
		$dialog = jq(iframe).dialog({
			autoOpen : false,
			draggable : true,
			resizable : true,
			modal : true,
			minWidth : width + 2,
			minHeight : height + 2,
			title : title
		});
	}

	$dialog.dialog('open');
}

/**
 * Utility method to get the string between two given strings
 * 
 * @param parseString -
 *          the string to parse
 * @param matchString1 -
 *          the beginning delimiter for the substring
 * @param matchString2 -
 *          the ending delimiter for the substring
 * @returns - the parsed substring, or null if the matches were not made
 */
function substringBetween(parseString, matchString1, matchString2) {
	var beginIndex = parseString.indexOf(matchString1);
	var endIndex = parseString.indexOf(matchString2);

	if (beginIndex > 0 && endIndex > 0) {
		return parseString.substring(beginIndex + matchString1.length, endIndex);
	} else {
		return null;
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
	else if(navigationType == "TAB_MENU"){
		createTabMenu(listId, options);
	}
}

function createTabMenu(listId, options) {
	jq(document).ready(function(){
		jq("#" + listId).tabMenu(options);
	});
}

/**
 * Uses jQuery menu plug-in to build a menu for the list with the given id
 * 
 * @param listId -
 *          unique id for the unordered list
 */
function createVerticalMenu(listId, options) {
	jq(document).ready(function() {
		jq("#" + listId).navMenu(options);
	});
}

/**
 * Sets ups a text popout button and window for this particular field that will be generated
 * when that button is clicked
 * @param id - id of the control
 * @param label - label to be used in popout
 * @param summary - summary to be used in popout
 * @param constraint - constraint to be used in popout
 */
function setupTextPopout(id, label, summary, constraint){
	var options = {label: label, summary: summary, constraint: constraint};
	jq("#" + id).initPopoutText(options);
}


/**
 * Show growl with message, title and theme passed in
 * @param message message of this jGrowl
 * @param title title of this jGrowl, can be empty string for none
 * @param theme class to append to jGrowl classes, can be empty string for none
 */
function showGrowl(message, title, theme){
	var context = getContext();
	if(theme){
		context.jGrowl(message, { header: title, theme: theme});
	}
	else{
		context.jGrowl(message, { header: title});
	}
}

/**
 * Set default growl options for this view
 * @param options
 */
function setGrowlDefaults(options){
	var context = getContext();
	context.jGrowl.defaults = context.extend(context.jGrowl.defaults, options);
}

/**
 * Get the current context
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
  jq(function() {
   	jq("#" + controlId).datepicker(options);
	});	
}

/**
 * Uses jQuery fancybox to link a fancybox to a given control id. The second
 * argument is a Map of options that are available for the FancyBox. See
 * <link>http://fancybox.net/api</link> for documentation on these options
 * 
 * @param controlId -
 *          id for the control that the fancybox should be linked to
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createLightBox(controlId, options) {		
    jq(function () {
    	var showHistory = false;
    	// Check if this is called within a lightbox
    	if (!jq("#fancybox-frame", parent.document).length) {
    		// If this is not the top frame, then create the lightbox
    		// on the top frame to put overlay over whole window
    		if (top == self) {
    			jq("#" + controlId).fancybox(options);    			
    		}else{
    			jq("#" + controlId).click(function (e) {
			  	   e.preventDefault(); 	  			  	   
			  	   options['href'] = jq("#" + controlId).attr('href');
			  	   top.$.fancybox(options);
    			});
    		}
    	}else{
    		jq("#" + controlId).attr('target', '_self');
    		showHistory = true;
    	}
    	// Set the dialogMode = true param
    	if (jq("#" + controlId).attr('href').indexOf('&dialogMode=true') == -1) {
    		jq("#" + controlId).attr('href', jq("#" + controlId).attr('href') + '&dialogMode=true'
    				+ '&showHome=false' + '&showHistory=' + showHistory 
    				+ '&history=' + jq('#formHistory\\.historyParameterString').val());
    	}    	
    });			
}

/**
 * Get post paramaters dynamic Uses
 * jQuery fancybox to create lightbox for lookups. It prevents the default
 * submit and makes an ajax post. The second argument is a Map of options that
 * are available for the FancyBox. See <link>http://fancybox.net/api</link> for
 * documentation on these options
 * 
 * @param controlId -
 *          id for the control that the fancybox should be linked to
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createLightBoxLookup(controlId, options, actionParameterMapString) {
    jq(function () {    
    	// Check if this is not called within a lightbox
    	if (!jq("#fancybox-frame", parent.document).length) {
    		jq("#" + controlId).click(function (e) {
        	// Prevent the default submit    		
            e.preventDefault();   
            jq("[name='jumpToId']").val(controlId);
            // Add the ajaxCall parameter so that the controller can avoid the redirect
	        actionParameterMapString['actionParameters[dialogMode]'] = 'true';
            actionParameterMapString['actionParameters[ajaxCall]'] = 'true';
            actionParameterMapString['actionParameters[showHistory]'] = 'false';
            actionParameterMapString['actionParameters[showHome]'] = 'false';
            // If this is the top frame, the page is not displayed in the iframeprotlet
            // set the return target
            if (top == self) {
            	actionParameterMapString['actionParameters[returnTarget]'] = '_parent';
            }else{
            	actionParameterMapString['actionParameters[returnTarget]'] = 'iframeportlet';
            }
			for (var key in actionParameterMapString) {
		 	 	writeHiddenToForm(key , actionParameterMapString[key]);
		 	}
            // Do the Ajax submit on the kualiForm form
            jq("#kualiForm").ajaxSubmit({  
            	// The additional data ie. baseLookupURL, bussObject
//            	data: actionParameterMapString,
        		success: function(data) {
            		// Add the returned URL to the FancyBox href setting
            		options['href'] = data;
            		if (top == self) {
            			jq.fancybox(options);
            		}else{
            			parent.$.fancybox(options);
            		}
        			jq.watermark.showAll();        			
        		}
        	});            
        });
    	}else{
			jq("#" + controlId).click(function (e) {
				actionParameterMapString['actionParameters[dialogMode]'] = 'true';
				actionParameterMapString['actionParameters[returnTarget]'] = '_self';
				actionParameterMapString['actionParameters[showHistory]'] = 'true';
	            actionParameterMapString['actionParameters[showHome]'] = 'false';
				for (var key in actionParameterMapString) {
			 	 	writeHiddenToForm(key , actionParameterMapString[key]);
			 	}
			});
    	}
        
    });		
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
 *          map of option settings (option name/value pairs) for the plugin
 * @param showLightBox -
 *          flag to indicate if it must be shown in a lightbox
 * @param lightBoxOptions -
 *          map of option settings (option name/value pairs) for the lightbox plugin                  
 */
function showDirectInquiry(url, paramMap, showLightBox, lightBoxOptions) {
	parameterPairs = paramMap.split(",");
	queryString="&showHome=false";
	  for (i in parameterPairs) {	  
	    parameters = parameterPairs[i].split(":");
	  	if (jq('[name="' + parameters[0] + '"]').val()=="") 
	  	{
	  		alert("Please enter a value in the appropriate field.");
			return false;
	  	} else {
	    	queryString=queryString+"&"+parameters[1]+"="+jq('[name="' + parameters[0] + '"]').val();
	  	}
	  }
	if (showLightBox) {
    	if (!jq("#fancybox-frame", parent.document).length) {
    		// If this is not the top frame, then create the lightbox
    		// on the top frame to put overlay over whole window
    		queryString=queryString + "&showHistory=false";
    		if (top == self) {
    			lightBoxOptions['href'] = url+queryString;    			
    			jq.fancybox(lightBoxOptions);    			
    		}else{  
    			lightBoxOptions['href'] = url+queryString;
		  	    top.$.fancybox(lightBoxOptions);
    		}
    	}else{
    		// If this is already in a lightbox just open in current lightbox
    		queryString=queryString + "&showHistory=true";
    		window.open(url+queryString, "_self");    		
    	}
	}else{
		queryString=queryString + "&showHistory=false";
		window.open(url+queryString, "_blank", "width=640, height=600, scrollbars=yes");
	}
}
/**
 * Sets up the script necessary to toggle a group as a accordion
 * 
 * @param groupId -
 *          id for the group to be toggled
 * @param headerId -
 *          id for the group's header in which the toggle link and image will be
 *          inserted
 * @param defaultOpen -
 *          indicates whether the group should be initially open or close
 * @param collapseImgSrc -
 *          path to the image that should be displayed for collapsing the group
 * @param expandImgSrc -
 *          path to the image that should be displayed for expanding the group
 * @param animationSpeed -
 *          speed at which the group should be expanded or collapsed
 * @param isOpen -
 *          boolean that indicates whether the accordion should be set to open
 *          initially (true) or closed (false)
 */
function createAccordion(groupId, headerId, defaultOpen, collapseImgSrc, expandImgSrc, animationSpeed) {
  jq(document).ready(function() {
  	var groupToggleLinkId = groupId + "_toggle";
  	var groupToggleLink = "<a href='#' id='" + groupToggleLinkId + "'></a>";
  	
  	var expandImage = "<img id='" + groupId + "_exp" + "' src='" + expandImgSrc + "' alt='expand' class='expand_collapse-buttons'/>";
  	var collapseImage = "<img id='" + groupId + "_col" + "' src='" + collapseImgSrc + "' alt='collapse' class='expand_collapse-buttons'/>";
 
  	var groupAccordionSpanId = groupId + "_group";
  	
  	// perform initial open/close and insert toggle link and image
  	if (defaultOpen) {
  		jq("#" + groupAccordionSpanId).slideDown(000);
  		jq("#" + headerId + "_header > :header").prepend("<a href='#' id='" + groupToggleLinkId + "'>" + expandImage + "</a>");
  	}
  	else {
  		jq("#" + groupAccordionSpanId).slideUp(000);
  		jq("#" + headerId + "_header > :header").prepend("<a href='#' id='" + groupToggleLinkId + "'>" + collapseImage + "</a>");
  	} 
 
  	// perform slide and switch image
  	if (defaultOpen) {
      jq("#" + groupToggleLinkId).toggle(
          function() {
            jq("#" + groupAccordionSpanId).slideUp(animationSpeed);
            jq("#" + groupId + "_exp").replaceWith(collapseImage);
          }, function() {
            jq("#" + groupAccordionSpanId).slideDown(animationSpeed);
            jq("#" + groupId + "_col").replaceWith(expandImage);
          }
       );      		
  	}
    else {
      jq("#" + groupToggleLinkId).toggle(
          function() {
            jq("#" + groupAccordionSpanId).slideDown(animationSpeed);
            jq("#" + groupId + "_col").replaceWith(expandImage);
          }, function() {
            jq("#" + groupAccordionSpanId).slideUp(animationSpeed);
            jq("#" + groupId + "_exp").replaceWith(collapseImage);
          }
       );     	
    }
  });	
}

/**
 * Uses jQuery plug-in to show a loading notification for a page request. See
 * <link>http://plugins.jquery.com/project/showLoading</link> for documentation
 * on options.
 * 
 * @param showLoading -
 *          boolean that indicates whether the loading indicator should be shown
 *          (true) or hidden (false)
 */
function createLoading(showLoading) {
	var methodToCall = jq("input[name='methodToCall']").val();
	if(top == self){
		//no portal
		if (showLoading) {
			if(methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()){
				jq.blockUI({message: savingMessage});
			}
			else{
				jq.blockUI({message: loadingMessage});
			}
		}
		else {
			jq.unblockUI();
		}
	}
	else{
		if (showLoading) {
			if(methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()){
				top.$.blockUI({message: savingMessage});
			}
			else{
				top.$.blockUI({message: loadingMessage});
			}
		}
		else {
			top.$.unblockUI();
		}
	}
}

/**
 * Uses jQuery DataTable plug-in to decorate a table with functionality like
 * sorting and page. The second argument is a Map of options that are available
 * for the plug-in. See <link>http://www.datatables.net/usage/</link> for
 * documentation on these options
 * 
 * @param controlId -
 *          id for the table that should be decorated
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createTable(controlId, options) {
	jq(document).ready(function() {
		var oTable = jq("#" + controlId).dataTable(options);
	});
}

/**
 * Uses jQuery jsTree plug-in to decorate a div with tree functionality. The
 * second argument is a Map of options that are available
 * for the plug-in. See <link>http://www.jstree.com/documentation/</link> for
 * documentation on these options
 *
 * @param controlId -
 *          id for the div that should be decorated
 * @param options -
 *          map of option settings (option name/value pairs) for the plugin
 */
function createTree(controlId, options) {
	jq(document).ready(function() {
		jq("#" + controlId).jstree(options);
	});
}

/**
* Applies the error coloring for fields with errors, warnings, or information
*/
function applyErrorColors(errorDivId, errorNum, warningNum, infoNum, clientSide){
	if(errorDivId){
		var div = jq("#" + errorDivId);
		var label = jq("#" + errorDivId.replace("errors_div", "label"));
		var highlightLine = "";
		
		//check to see if the option to highlight fields is on
		if(div.length > 0 && !div.hasClass("noHighlight")){
			if (div.parent().is("td")) {
				highlightLine = div.parent();
			}
			else{
				highlightLine = div.closest(".fieldLine");
			}
			
			if (highlightLine.length > 0) {
				if(errorNum && !clientSide){
					highlightLine.addClass("serverError");
					label.addClass("serverError"); 
				}
				else if(errorNum){
					highlightLine.addClass("clientError");
					label.addClass("clientError");
				}
				else if(warningNum){
					highlightLine.addClass("warning");
					label.addClass("warning");
				}
				else if(infoNum){
					highlightLine.addClass("information");
					label.addClass("information");
				}
				else{
					//we are only removing errors client side - no knowledge of warnings/infos
					if(div.parent().hasClass("errorsField")){
						var error_li = div.parent().find(".errorMessages").find("li");
						var moreErrors = false;
						error_li.each(function(){
							if(jq(this).css("display") != "none"){
								moreErrors = true;
							}
						});
						
						label.removeClass("clientError");
						if(!moreErrors){
							highlightLine.removeClass("clientError");
						}
					}
					else{
						highlightLine.removeClass("clientError");
						label.removeClass("clientError");
					}
				}
			}
		}
		
		//highlight tab that contains errors - no setting to turn this off because it is necessary
		var tabDiv = div.closest(".ui-tabs-panel");
		if(tabDiv.length > 0){
			var tabId = tabDiv.attr("id");
			var tabAnchor = jq("a[href='#" + tabId + "']");
			var errorIcon = jq("#" + tabId + "_errorIcon");
			
			if(tabAnchor.length > 0){
				var hasErrors = false;
				if(errorNum){
					hasErrors = true;
				}
				else{
					var error_li = tabDiv.find(".errorMessages").find("li");
					error_li.each(function(){
						if(jq(this).css("display") != "none"){
							hasErrors = true;
						}
					});
				}
	
				if(hasErrors){
					tabAnchor.addClass("clientError");
					if(errorIcon.length == 0){
						tabAnchor.append("<img id='"+ tabId +"_errorIcon' alt='error' src='/kr-dev/kr/static/images/errormark.gif'>");
					}
				}
				else if(!hasErrors){
					tabAnchor.removeClass("clientError");
					errorIcon.remove();
				}
			}
		}
	}
}
	
/**
 * Shows the field error icon if errorCount is greater than one and errorsField
 * has the option turned on
 */
function showFieldIcon(errorsDivId, errorCount){
	if(errorsDivId){
		var div = jq("#" + errorsDivId);
		var inputId = errorsDivId.replace("_errors_div", "");
		
		if(inputId){
			var input = jq("#" + inputId);
			var errorIcon = jq("#" + inputId + "_errorIcon");
			
			if (div.length > 0 && div.hasClass("addFieldIcon") && errorCount && errorIcon.length == 0) {
				if (input.length > 0) {
					input.after("<img id='"+ inputId +"_errorIcon' alt='error' src='/kr-dev/kr/static/images/errormark.gif'>");
				}
				else {
					// try for radios and checkboxes
					input = jq("#" + errorDivId.replace("errors_div", "attribute1"));
					if (input.length > 0) {
						input.after("<img id='"+ inputId +"_errorIcon' alt='error' src='/kr-dev/kr/static/images/errormark.gif'>");
					}
				}
			}
			else if(div.length > 0 && div.hasClass("addFieldIcon") && errorCount == 0){
				if(errorIcon.length > 0){
					errorIcon.remove();
				}
			}
	    }
	}
}

/**
 * Adds the icon that indicates the contents of a field have changed from the compared value (for instance the new side
 * on maintenance documents) to the field markers span
 *
 * @param fieldId - id for the field the icon should be added to
 */
function showChangeIcon(fieldId) {
    var fieldMarkerSpan = jq("#" + fieldId + "_markers");
    var fieldIcon = jq("#" + fieldId + "_changeIcon");

    if (fieldMarkerSpan.length > 0 && fieldIcon.length == 0) {
       fieldMarkerSpan.append("<img id='"+ fieldId +"_changeIcon' alt='change' src='/kr-dev/krad/images/asterisk_orange.png'>");
    }
}

/**
 * Add icon to a group header that indicates the data for the group has changed
 *
 * @param headerFieldId - id for the header field the icon should be added to
 */
function showChangeIconOnHeader(headerFieldId) {
    var headerSpan = jq("#" + headerFieldId + "_header");
    var headerIcon = jq("#" + headerFieldId + "_changeIcon");

    if (headerSpan.length > 0 && headerIcon.length == 0) {
       headerSpan.append("<img id='"+ headerFieldId +"_changeIcon' alt='change' src='/kr-dev/krad/images/asterisk_orange.png'>");
    }
}

//Applies the watermark to the input with the id specified
function createWatermark(id, watermark){
	jq("#" + id).watermark(watermark);
}

//Creates tabs for the tabs div id specified, this div is created by tabGroup
function createTabs(id, options){
	jq("#" + id + "_tabs").tabs(options);
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