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

// common event registering done here through JQuery ready event
$(document).ready(function() {

// if (!dialogMode) {
// $.jGrowl("Save Successful", {
// sticky : true
// });
// }

	// $.loading(true, { img:'images/jquery/loading.gif', align:'center', text:
	// 'Loading...'});
 // $("#red").loading();


	// $(":input").watermark("Fill Me ...");

	// initializeInquiryHandlers();

	// initializeLookupHandlers();

// if (dialogMode) {
// initializeReturnHandlers();
// resizeDialog();
// }

	// this is for nested inquiries to keep opening in the same dialog
	// if (dialogMode) {
	// $('a[href*=inquiry.do]').attr('target', '_self');
	// }
	
	// buttons
	$( "input:submit" ).button();
	
	// hide loading indicator
	//doLoading(false);
	
	// validate form
	// $("form").validate();

})

$(document).unload(function() {
	
})


function resizeDialog() {
	var width = $(document).width();
	var height = $(document).height() + 50;
	
	window.parent.$dialog.dialog('option', 'width', width);
	window.parent.$dialog.dialog('option', 'height', height);
}

/**
 * Sets the click handler for all inquiry URLs to open in a JQuery dialog
 */
function initializeInquiryHandlers() {
	// select all links pointing to the inquiry action
	$('a[href*=inquiry.do]').click(function(e) {
		// cancel the link behavior
		e.preventDefault();

		// get the inquiry link target
		var href = $(this).attr('href') + '&dialogMode=Y';
		showIFrameDialog(href, 'Inquiry');
	})
}

function initializeLookupHandlers() {
	// select inputs of type 'image' that have performLookup in name
	$(':input[type=image][name*=performLookup]')
			.click(
					function(e) {
						// cancel the submit behavior
						e.preventDefault();

						// build lookup URL from input name
						var name = $(this).attr('name');

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
	$('a[href*=refreshCaller]').click(function(e) {
		// cancel the link behavior
		e.preventDefault();

		// get the inquiry link target
		var href = $(this).attr('href');

		// parse out return parameters and set form values
		var parameterString = href.substring(href.indexOf('?') + 1);
		var parameters = parameterString.split('&');
		for ( var i = 0; i < parameters.length; i++) {
			var keyValue = parameters[i].split('=');
			var parameterName = keyValue[0];
			var parameterValue = keyValue[1];
			// TODO: this filtering isn't working, need a way to filter out parms we
			// don't want to populate
			if (window.parent.$('[name=' + parameterName + ']')) {
				// set focus to remove any water-marks
				window.parent.$('[name=' + parameterName + ']').focus();
				window.parent.$('[name=' + parameterName + ']').val(parameterValue);
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
	var width = ($(document).width() / 4) * 3;
	var height = ($(document).height() / 4) * 3;

	if (dialogMode) {
		width = $(window).width();
		height = $(window).height();
	}

	var iframe = "<div><iframe id='dialogIFrame' src='" + href + "' height='100%' width='100%'/></div>";
			
// var iframe = "<div><iframe id='dialogIFrame' src='" + href + "' height='"
// + height + "px' width='" + width + "px'/></div>";

	// create dialog with iframe source
	if (dialogMode) {
		$dialog = window.parent.$(iframe).dialog({
			autoOpen : false,
			draggable : true,
			resizable : true,
			modal : true,
			minWidth : width + 2,
			minHeight : height + 2,
			title : title
		});

	} else {
		$dialog = $(iframe).dialog({
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
function createNavigation(listId, navigationType) {
	if (navigationType == "VERTICAL_MENU") {
		doVerticalMenu(listId);
	}
}

/**
 * Uses jQuery menu plug-in to build a menu for the list with the given id
 * 
 * @param listId -
 *          unique id for the unordered list
 */
function createVerticalMenu(listId) {
	$(document).ready(function() {
		$("#" + listId).sidebar();
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
  $(function() {
   	$("#" + controlId).datepicker(options);
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
  $(document).ready(function() {
  	if (isOpen) {
  		$("#" + accordionDiv).slideDown(000);
  		$("#" + accordionToggleLink).html(openAccordionHeaderContents);
  	}
  	else {
  		$("#" + accordionDiv).slideUp(000);
  		$("#" + accordionToggleLink).html(closedAccordionHeaderContents);
  	} 
 
    $("#" + accordionToggleLink).toggle(
       function() {
         $("#" + accordionDiv).slideUp(500);
         $("#" + accordionToggleLink).html(closedAccordionHeaderContents);
       }, function() {
         $("#" + accordionDiv).slideDown(500);
         $("#" + accordionToggleLink).html(openAccordionHeaderContents);
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
	  $("#view_div").showLoading();
	}
	else {
		$("#view_div").hideLoading();
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
	$(document).ready(function() {
		var oTable = $("#" + controlId).dataTable(options);
	})
}

