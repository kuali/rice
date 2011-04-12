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

// common event registering done here through JQuery ready event
jq(document).ready(function() {

// if (!dialogMode) {
// $.jGrowl("Save Successful", {
// sticky : true
// });
// }
	
	// buttons
	jq( "input:submit" ).button();
	jq( "input:button" ).button();
})

jq(document).unload(function() {
	
})

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
    	if (!jq("#fancybox-frame", parent.document).length) {
    	jq("#" + controlId).fancybox(options);    	
    	}else{
    		jq("#" + controlId).attr('target', '_self');
    	}
    	if (!jq("#" + controlId).attr('href').indexOf('&dialogMode=true') == -1) {
    		jq("#" + controlId).attr('href', jq("#" + controlId).attr('href') + '&dialogMode=true')
    	}
    });			
}

/**
 * To fix : 1. does not work in iframe 2. Get post paramaters dynamic Uses
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
    	if (!jq("#fancybox-frame", parent.document).length) {
        jq("#" + controlId).click(function (e) {
        	// Prevent the default submit
            e.preventDefault();
            // Add the ajaxCall parameter so that the controller can avoid the redirect
            //dialogMode=Y
	            actionParameterMapString['actionParameters[dialogMode]'] = 'true';
            actionParameterMapString['actionParameters[ajaxCall]'] = 'true';
            // Do the Ajax submit on the kualiForm form
            jq("#kualiForm").ajaxSubmit({  
            	// The additional data ie. baseLookupURL, bussObject
            	data: actionParameterMapString,
        		success: function(data) {
            		// Add the returned URL to the FancyBox href setting
            		options['href'] = data;
        			jq.fancybox(options);
        			jq.watermark.showAll();        			
        		}
        	});            
        });
    	}else{
			jq("#" + controlId).click(function (e) {
				actionParameterMapString['actionParameters[dialogMode]'] = 'true';
				for (var key in actionParameterMapString) {
			 	 	writeHiddenToForm(key , actionParameterMapString[key]);
			 	}
			});
    	}
        
    });		
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
	if (showLoading) {
	  jq("#view_div").showLoading({'hPos': 'center', 'vPos': 'center'});
	}
	else {
		jq("#view_div").hideLoading();
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
	})
}

/**
* Applies the error coloring for fields with errors, warnings, or information
*/
function applyErrorColors(errorDivId, errorNum, warningNum, infoNum, clientSide){
	var div = jq("#" + errorDivId);
	var label = jq("#" + errorDivId.replace("errors_div", "label"));
	var highlightLine = "";
	//check to see if the option to highlight fields is on
	if(!div.hasClass("noHighlight")){
		if (div.parent().is("td")) {
			highlightLine = div.parent();
		}
		else{
			highlightLine = div.closest(".fieldLine");
		}
		if (highlightLine) {
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
				highlightLine.removeClass("clientError");
				label.removeClass("clientError");
			}
		}
	}
}
	
/**
*  Shows the field error icon if errorCount is greater than one and errorsField has the option turned on
*/
function showFieldIcon(errorsDivId, errorCount){
	var div = jq("#" + errorsDivId);
	var inputId = errorsDivId.replace("_errors_div", "");
	var input = jq("#" + inputId);
	var errorIcon = jq("#" + inputId + "_errorIcon");
	if (div.hasClass("addFieldIcon") && errorCount && errorIcon.length == 0) {
		if (input) {
			input.after("<img id='"+ inputId +"_errorIcon' alt='error' src='/kr-dev/kr/static/images/errormark.gif'>");
		}
		else {
			//try for radios and checkboxes
			input = jq("#" + errorDivId.replace("errors_div", "attribute1"));
			if (input) {
				input.after("<img id='"+ inputId +"_errorIcon' alt='error' src='/kr-dev/kr/static/images/errormark.gif'>");
			}
		}
	}
	else if(div.hasClass("addFieldIcon") && errorCount == 0){
		if(errorIcon){
			errorIcon.remove();
		}
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