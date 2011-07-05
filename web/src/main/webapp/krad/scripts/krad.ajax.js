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

//Submits the form through an ajax submit, the response is the new page html
//runs all hidden scripts passed back (this is to get around a bug with pre mature
//script evaluation)
function submitForm() {
	writeHiddenToForm("renderFullView", "false");
	jq("#kualiForm").ajaxSubmit({
		success: function(response){
			var tempDiv = document.createElement('div');
			tempDiv.innerHTML = response;
			var hasError = handleIncidentReport(response);
			if(!hasError){
				var page = jq("#viewpage_div", tempDiv);
				jq("#viewpage_div").replaceWith(page);
				jq("#formComplete").html("");
				setPageBreadcrumb();
				pageValidatorReady = false;
				runHiddenScripts("viewpage_div");
			}
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

		//validate failed remove intended methodToCall just incase
		jq("input[name='methodToCall']").remove();

		jumpToTop();
		alert("The form contains errors.  Please correct these errors and try again.");
	}
}

//saves the current form by first validating client side and then attempting an ajax submit
function saveForm(){
	writeHiddenToForm("methodToCall", "save");
	validateAndSubmit();
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
	submitForm();
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
 */
function retrieveComponent(id, actualId){
	var elementToBlock = jq("#" + id + "_refreshWrapper");
	if(elementToBlock.find("#" + actualId + "_attribute_span").length){
		elementToBlock = jq("#" + actualId +"_attribute_span");
	}

	jq("#kualiForm").ajaxSubmit({
		data: {methodToCall: "updateComponent", reqComponentId: id, skipViewInit: "true"},
		beforeSend: function() {
			if(elementToBlock.hasClass("unrendered")){
				elementToBlock.append('<img src="/kr-dev/krad/images/loader.gif" alt="working..." /> Loading...');
				elementToBlock.show();
			}
			else{
				elementToBlock.block({
	                message: '<img src="/kr-dev/krad/images/loader.gif" alt="working..." /> Updating...',
	                fadeIn:  400,
	                fadeOut:  800,
	                overlayCSS:  {
	                    opacity: 0.3
	                }
	            });
			}
		},
		complete: null,
		error: function(){
			if(elementToBlock.hasClass("unrendered")){
				elementToBlock.hide();
			}
			else{
				elementToBlock.unblock();
			}
		},
		success: function(response){
			jq("#formComplete").html("");
			var tempDiv = document.createElement('div');
			tempDiv.innerHTML = response;
			var hasError = handleIncidentReport(response);
			if(!hasError){
				var component = jq("#" + id + "_refreshWrapper", tempDiv);
				//special label handling, if any
				var theLabel = jq("#" + actualId + "_label_span", tempDiv);
				if(jq(".displayWith-" + actualId).length && theLabel.length){
					theLabel.addClass("displayWith-" + actualId);
					jq("span.displayWith-" + actualId).replaceWith(theLabel);
					component.remove("#" + actualId + "_label_span");
				}

				elementToBlock.unblock({onUnblock: function(){
						//replace component
						if(jq("#" + id + "_refreshWrapper").length){
							jq("#" + id + "_refreshWrapper").replaceWith(component);
						}
						runHiddenScripts(id + "_refreshWrapper");

					}
				});

				jq(".displayWith-" + actualId).show();
			}
		}
	});
}

//called when a line is added to a collection
function addLineToCollection(collectionId){
	if(collectionId){
		var addFields = jq("td." + collectionId + "-addField").find("input:visible");
		jq.watermark.hideAll();

		var valid = true;
		addFields.each(function(){
			jq(this).removeClass("ignoreValid");
			jq(this).valid();
			if(jq(this).hasClass("error")){
				valid = false;
			}
			jq(this).addClass("ignoreValid");
		});

		jq.watermark.showAll();

		if(valid){
			//change to add line ajax call in future
			submitForm();
		}
		else{
			alert("This addition contains errors.  Please correct these errors and try again.");
		}
	}
}


/** Progressive Disclosure */

/**
 * Same as setupRefreshCheck except the condition will alwasy be true (always refresh when
 * value changed on control)
 *
 * @param controlName
 * @param refreshId
 */
function setupOnChangeRefresh(controlName, refreshId, baseId){
	setupRefreshCheck(controlName, refreshId, baseId, function(){return true;});
}

/**
 * Sets up the conditional refresh mechanism in js by adding a change handler to the control
 * which may satisfy the conditional refresh condition passed in.  When the condition is satisfied,
 * refresh the necessary content specified by id by making a server call to retrieve a new instance
 * of that component
 *
 * @param controlName
 * @param disclosureId
 * @param condition - function which returns true to refresh, false otherwise
 */
function setupRefreshCheck(controlName, refreshId, baseId, condition){
	jq("[name='"+ controlName +"']").change(function() {
		//visible check because a component must logically be visible to refresh
		var refreshComp = jq("#" + refreshId + "_refreshWrapper");
		if(refreshComp.length){
			if(condition()){
				retrieveComponent(refreshId, baseId);
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
 * @param controlName
 * @param disclosureId
 * @param condition - function which returns true to disclose, false otherwise
 */
function setupProgressiveCheck(controlName, disclosureId, baseId, condition, alwaysRetrieve){
	var actualId = retrieveOriginalId(disclosureId);
	if (!actualId.match("\_c0$")) {
		jq("[name='"+ controlName +"']").change(function() {
			var refreshDisclosure = jq("#" + disclosureId + "_refreshWrapper");
			if(refreshDisclosure.length){
				if(condition()){
					if(refreshDisclosure.hasClass("unrendered") || alwaysRetrieve){
						retrieveComponent(disclosureId, baseId);
					}
					else{
						//columnShownCheck(refreshDisclosure, true);
						refreshDisclosure.fadeIn("slow");
						//re-enable validation on now shown inputs
						hiddenInputValidationToggle(disclosureId + "_refreshWrapper");
						jq(".displayWith-" + actualId).show();
					}
				}
				else{
					refreshDisclosure.hide();
					//columnShownCheck(refreshDisclosure, false);
					//ignore validation on hidden inputs
					hiddenInputValidationToggle(disclosureId + "_refreshWrapper");
					jq(".displayWith-" + actualId).hide();
				}
			}
		});
	}
}

function columnShownCheck(refreshDisclosure, beingShown){
	var table = refreshDisclosure.closest('table.datatable');
	var td = refreshDisclosure.closest('td');

	if(table.length){
		if(beingShown){
			if(td.not(":visible")){
				var classes = td.attr("class").split(" ");
				var columnCss = "";
				for(var i =0; i < classes.length; i++){
					if(classes[i].indexOf("col") === 0){
						columnCss = classes[i];
						break;
					}
				}

				if(columnCss){
					var dataTablesWrap = td.closest(".dataTables_wrapper");
					var column;
					if(dataTablesWrap.length){
						column = dataTablesWrap.find("." + columnCss);
					}
					else{
						column = td.closest("table").find("." + columnCss);
					}
					column.show();
				}
			}
		}
		else{
			if(td.is(":visible")){
				var classes = td.attr("class").split(" ");
				var columnCss = "";
				for(var i =0; i < classes.length; i++){
					if(classes[i].indexOf("col") === 0){
						columnCss = classes[i];
						break;
					}
				}

				if(columnCss){
					var tds = table.find("td." + columnCss);

					var hide = true;
					tds.each(function(index){
						if(jq(this).children().find(":visible").length > 0){
							 hide = false;
							 return false;
						}
					});

					if(hide){
						var dataTablesWrap = td.closest(".dataTables_wrapper");
						var column;
						if(dataTablesWrap.length){
							column = dataTablesWrap.find("." + columnCss);
						}
						else{
							column = td.closest("table").find("." + columnCss);
						}
						column.hide();
					}
				}
			}
		}
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
	var element = jq("#" + id);
	if(element.length){
		if(element.css("display") == "none"){
			jq(":input:hidden", element).each(function(){
				jq(this).addClass("ignoreValid");
			});
		}
		else{
			jq(":input:visible", element).each(function(){
				jq(this).removeClass("ignoreValid");
			});
		}
	}
}

/**
 * Retrieves the original dictionary based id that was used to generate this component and/or its
 * children/parent.  Basically removes everything after the first "_" in the idString passed in.
 * Check if it is part of collections or old/new values of maintanance document to check
 * if the first one ot two underscores must be preserved.
 * @param idString
 */
function retrieveOriginalId(idString){
	var oneExtraUnderscoreFlag = idString.match("_add$|_add_|_[0-9]*_|_[0-9]*$|_c[0-1]_|_c[0-1]$");
	var twoExtraUnderscoreFlag = idString.match("_[0-9]*_c[0-1]_|_[0-9]*_c[0-1]$");
	var index = idString.indexOf("_");
	var id = idString;
	if(index){
		if (oneExtraUnderscoreFlag || twoExtraUnderscoreFlag) {
			index = idString.indexOf("_", index+1);
		}
		if (twoExtraUnderscoreFlag) {
			index = idString.indexOf("_", index+1);
		}
		if (index) {
			id = idString.substr(0,index);
		}
	}
	return id;
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
    var elementToBlock = jq("#" + collectionGroupId + "_div");

	jq("#kualiForm").ajaxSubmit({
		data: {methodToCall: "toggleInactiveRecordDisplay", reqComponentId: collectionGroupId,
               skipViewInit: "true", showInactiveRecords : showInactive},
		beforeSend: function() {
		     elementToBlock.block({
	            message: '<img src="/kr-dev/krad/images/loader.gif" alt="working..." /> Updating...',
	            fadeIn:  400,
	            fadeOut:  800,
	            overlayCSS:  {
	                opacity: 0.3
	            }
	         });
		},
		complete: null,
		error: function(){
			elementToBlock.unblock();
		},
		success: function(response){
			jq("#formComplete").html("");

			var tempDiv = document.createElement('div');
			tempDiv.innerHTML = response;
			var hasError = handleIncidentReport(response);
			if(!hasError){
				var component = jq("#" + collectionGroupId + "_div", tempDiv);

				elementToBlock.unblock({onUnblock: function(){
						//replace component
						if(jq("#" + collectionGroupId + "_div").length){
							jq("#" + collectionGroupId + "_div").replaceWith(component);
						}
						runHiddenScripts(collectionGroupId + "_div");
					}
				});
			}
		}
	});
}
