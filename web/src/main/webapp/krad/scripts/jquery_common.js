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



// common event registering done here through JQuery ready event
jq(document).ready(function() {

// if (!dialogMode) {
// $.jGrowl("Save Successful", {
// sticky : true
// });
// }

	// $.loading(true, { img:'images/jquery/loading.gif', align:'center', text:
	// 'Loading...'});
 // jq("#red").loading();


	// jq(":input").watermark("Fill Me ...");

	// initializeInquiryHandlers();

	// initializeLookupHandlers();

// if (dialogMode) {
// initializeReturnHandlers();
// resizeDialog();
// }

	// this is for nested inquiries to keep opening in the same dialog
	// if (dialogMode) {
	// jq('a[href*=inquiry.do]').attr('target', '_self');
	// }
	
	// buttons
	jq( "input:submit" ).button();
	jq( "input:button" ).button();
	
	// hide loading indicator
	//doLoading(false);
	
	// validate form
	// jq("form").validate();
	
	
	// TMP for style stuff
    jq(document).ready(function() {
        jq(".green").slideUp(000);
		
        jq(".showgreen").toggle(function() {
            jq(".green").slideDown(600);
            
            jq(".showgreen").html(" close <img style='margin-left:12px; margin-right:4px;' src='/kr-dev/krad/images/cancel.png' width='16' height='16' alt='collapse'>");
        }, function() {
            jq(".showgreen").html("#34683456  <img style='margin-left:12px; margin-right:4px;' src='/kr-dev/krad/images/down.png' width='16' height='16' alt='collapse'>");
            jq(".green").slideUp(600);
     });
    });    

   // end tmp
})

jq(document).unload(function() {
	
})


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
		var href = jq(this).attr('href') + '&dialogMode=Y';
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

						var href = 'lookup.do?dialogMode=Y&docFormKey=0&returnLocation=portal.jsp&methodToCall=start&businessObjectClassName=';
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
 * Sets up the script necessary to toggle a group as a accordion
 * 
 * @param accordionToggleLink -
 *          id for the link that should toggle the accordion
 * @param openAccordionHeaderContents -
 *          contents that should go in the accordion header when the accordion is open
 * @param closedAccordionHeaderContents -
 *          contents that should go in the accordion header when the accordion is closed
 * @param accordionDiv -
 *          id for the div that wraps the accordion contents
 * @param isOpen -
 *          boolean that indicates whether the accordion should be set to open
 *          initially (true) or closed (false)
 */
function createAccordion(accordionToggleLink, openAccordionHeaderContents, closedAccordionHeaderContents, 
		             accordionDiv, isOpen) {
  jq(document).ready(function() {
  	if (isOpen) {
  		jq("#" + accordionDiv).slideDown(000);
  		jq("#" + accordionToggleLink).html(openAccordionHeaderContents);
  	}
  	else {
  		jq("#" + accordionDiv).slideUp(000);
  		jq("#" + accordionToggleLink).html(closedAccordionHeaderContents);
  	} 
 
    jq("#" + accordionToggleLink).toggle(
       function() {
         jq("#" + accordionDiv).slideUp(500);
         jq("#" + accordionToggleLink).html(closedAccordionHeaderContents);
       }, function() {
         jq("#" + accordionDiv).slideDown(500);
         jq("#" + accordionToggleLink).html(openAccordionHeaderContents);
       }
    );
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
	  jq("#view_div").showLoading();
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

