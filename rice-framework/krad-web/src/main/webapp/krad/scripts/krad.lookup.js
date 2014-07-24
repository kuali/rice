/*
 * Copyright 2005-2014 The Kuali Foundation
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
 * Script specific to lookup views.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

var returnByScriptHidden = "returnByScript";

/*
 There is an issue with jQuery re-running document ready twice. This happens when the innerHTML gets
 updated. Ref: http://shout.setfive.com/2010/02/22/javascript-document-ready-getting-called-twice-heres-why/

 This variable is a local solution within krad.lookup to prevent the updateSelectLineCount to be called only
 once onChange, as enabling/disabling 'return selected' depends on increment/decrement of a counter in its logic that
 gets corrupted by multiple calls for the same event.
 */
var _DONE = false;

jQuery(document).ready(function () {
    if (_DONE === true) {
        return;
    }

    _DONE = true;

    // multi value select handler to enable/disable return selected button for checkboxes with uif-select-line
    jQuery(document).on("change", "#" + kradVariables.LOOKUP_COLLECTION_ID + " input:checkbox." + kradVariables.SELECT_FIELD_STYLE_CLASS, function (e) {
        updateSelectLineCount(this);
    });

    // event handler for return links on lookups
    jQuery(document).on("click", "a[" + kradVariables.ATTRIBUTES.DATA_RETURN + "]", function (e) {
        e.preventDefault();

        // determine if the results should be returned through script
        var returnByScript = coerceValue(returnByScriptHidden);

        if (returnByScript) {
            returnLookupResultsByScript(this);
        }
        else {
            returnLookupResultReload(this);
        }
    });
});

/**
 * Submits the form based on the quickfinder action identified by the given id and display the result content in
 * a dialog using a modal. If we are not currently in a modal, we will request a URL to be used in the created dialog's
 * iframe content. Otherwise, the internal iframe of the dialog will be redirected.
 *
 * @param quickfinderActionId id for the action component that the fancybox should be linked to
 * @param lookupReturnByScript boolean that indicates whether the lookup should return through script
 *        or via a server post
 * @param lookupDialogId(optional) id of dialog to use, if not set Uif-DialogGroup-Iframe will be used
 */
function showLookupDialog(quickfinderActionId, lookupReturnByScript, lookupDialogId) {
    jQuery(function () {
        var data = {};
        var submitData = jQuery("#" + quickfinderActionId).data(kradVariables.SUBMIT_DATA);
        jQuery.extend(data, submitData);

        if (!lookupReturnByScript) {
            dirtyFormState.skipDirtyChecks = true;
        }

        // Check if this is not called within a lightbox
        var renderedInDialog = isCalledWithinDialog();
        if (!renderedInDialog) {
            if (top === self) {
                data['actionParameters[returnTarget]'] = '_parent';
            } else {
                data['actionParameters[returnTarget]'] = 'iframeportlet';
            }

            var baseURI = this.documentURI;
            if (baseURI.indexOf("?") > -1) {
                baseURI = baseURI.substring(0, baseURI.indexOf("?"));
            }

            data['actionParameters[returnLocation]'] = encodeURIComponent(baseURI);
            data['actionParameters[renderedInDialog]'] = true;
            data['actionParameters[returnByScript]'] = lookupReturnByScript;
            data['actionParameters[methodToCall]'] = "start";
            data['actionParameters[flowKey]'] = "start";
            data['actionParameters[returnFormKey]'] = jQuery("#" + kradVariables.FORM_INFO_ID).children("input[name='formKey']").val();

            var lookupParameters = data['actionParameters[lookupParameters]'];
            if (lookupParameters !== "" && typeof lookupParameters !== "undefined") {
                var lookupField = lookupParameters.substring(lookupParameters.indexOf(":") + 1);
                var lookupValue = jQuery("#" + quickfinderActionId).parent().parent().children("input").val();
                if (lookupField !== "" && typeof lookupField !== "undefined" && lookupValue !== "" && typeof lookupValue !== "undefined") {
                    data['actionParameters[lookupCriteria[&quot;' + lookupField + '&quot;]]'] = lookupValue;
                }
            }

            var lookupUrl = data['actionParameters[baseLookupUrl]'] + "?";

            for (var key in data) {
                if (key.indexOf("actionParameters") !== -1) {
                    var parameterName = key.replace("actionParameters[", "").replace("]", "").replace(new RegExp("&quot;", 'g'), "'");
                    lookupUrl += parameterName + "=" + data[key] + "&";
                }
            }

            // Trim the remaining ampersand
            lookupUrl = lookupUrl.substring(0, lookupUrl.length - 1);

            lookupUrl = lookupUrl.replace(/&amp;/g, '&');

            openIframeDialog(lookupUrl, lookupDialogId);
        } else {
            // add parameters for lightbox and do standard submit
            data['actionParameters[renderedInDialog]'] = 'true';
            data['actionParameters[returnTarget]'] = '_self';
            data['actionParameters[flowKey]'] = jQuery("input[name='" + kradVariables.FLOW_KEY + "']").val();

            nonAjaxSubmitForm(data['methodToCall'], data);
        }
    });
}

/**
 * Registers the onChange event on the input element inside of the lookup results collection
 * by updating the selection count depending on whether the checkbox has been checked or not
 *
 * @param selectControl  select control the change event occurred on
 */
function updateSelectLineCount(selectControl) {
    input = jQuery(selectControl);

    // Fetch current selectLineCount
    var lookupResultsDiv = jQuery("#" + kradVariables.LOOKUP_COLLECTION_ID);
    var selectlinecount = lookupResultsDiv.data('selectedlinecount');

    if (input.attr('checked')) {
        lookupResultsDiv.data('selectedlinecount', selectlinecount + 1);
    } else if (selectlinecount > 0) {
        lookupResultsDiv.data('selectedlinecount', selectlinecount - 1);
    }

    setMultivalueLookupReturnButton("#" + kradVariables.LOOKUP_COLLECTION_ID);
}

/**
 * Enables the return selected button on the multi value lookup when at least one item is selected.
 *
 * @param selectControl select control the change event occurred on
 */
function setMultivalueLookupReturnButton(selectControl) {

    var lookupResultsDiv = jQuery(selectControl);

    var checked = lookupResultsDiv.data('selectedlinecount') > 0;

    if (checked) {
        jQuery(':button.' + kradVariables.RETURN_SELECTED_ACTION_CLASS).removeAttr('disabled');
        jQuery(':button.' + kradVariables.RETURN_SELECTED_ACTION_CLASS).removeClass('disabled');
    } else {
        jQuery(':button.' + kradVariables.RETURN_SELECTED_ACTION_CLASS).attr('disabled', 'disabled');
    }
}
/**
 * Select all checkboxes within the datatable/non datatable (all pages) that are marked with class 'uif-select-line'
 * (used for multi-value select collections)
 *
 * @param collectionId - id for the collection to select checkboxes for
 */
function selectAllLines(collectionId) {
    var query = "input:checkbox." + kradVariables.SELECT_FIELD_STYLE_CLASS;
    var lookupCollectionDiv = jQuery("#" + collectionId);

    // If results are displayed using dataTable
    if (jQuery('table.dataTable').length > 0) {

        // get a handle on the datatables plugin object for the results collection
        var oTable = getDataTableHandle(lookupCollectionDiv.find("table").attr('id'));

        jQuery(query, oTable.fnGetNodes()).each(function (index) {
            this.checked = true;
        });
    } else {
        jQuery(lookupCollectionDiv.find(query)).each(function (index) {
            jQuery(this).attr('checked', true);
        });
    }

    // Reset data attribute selectedlinecount to number of results in the lookup
    var lookupResultCount = lookupCollectionDiv.data('lookupresultscount');
    lookupCollectionDiv.data('selectedlinecount', lookupResultCount);

    setMultivalueLookupReturnButton(jQuery("#" + collectionId));
}

/**
 * Deselects all checkboxes within the datatable/non datatable (all pages) that are marked with class 'uif-select-line'
 * (used for multi-value select collections)
 *
 * @param collectionId - id for the collection to deselect checkboxes for
 */
function deselectAllLines(collectionId) {
    // get a handle on the datatables plugin object for the results collection
    var oTable = getDataTableHandle(jQuery("#" + collectionId).find("table").attr('id'));
    var query = "input:checkbox." + kradVariables.SELECT_FIELD_STYLE_CLASS;

    if (jQuery('table.dataTable').length > 0) {
        jQuery(query, oTable.fnGetNodes()).prop('checked', false);
    }

    // reset selectedlinecount to 0
    var lookupCollectionDiv = jQuery('#' + collectionId);
    lookupCollectionDiv.data('selectedlinecount', 0);

    setMultivalueLookupReturnButton(jQuery("#" + collectionId));
}

/**
 * Select all checkboxes within the collection div that are marked with class 'uif-select-line' (used
 * for multi-value select collections) on the current page and updates the selectedlinecount
 *
 * @param collectionId - id for the collection to select checkboxes for
 */
function selectAllPageLines(collectionId) {
    var selectedLineCount = jQuery('#' + collectionId).data('selectedlinecount');
    jQuery("#" + collectionId).find("input:checkbox." + kradVariables.SELECT_FIELD_STYLE_CLASS).each(function (index) {
        if (jQuery(this).attr('checked') != true) {
            jQuery(this).attr('checked', true);
            selectedLineCount = selectedLineCount + 1;
        }
    });
    var lookupCollectionDiv = jQuery('#' + collectionId);
    lookupCollectionDiv.data('selectedlinecount', selectedLineCount);

    setMultivalueLookupReturnButton(jQuery("#" + collectionId));

}

/**
 * Deselects all checkboxes within the collection div that are marked with class 'uif-select-line' (used
 * for multi-value select collections) on the current page and updates the selectedlinecount
 *
 * @param collectionId - id for the collection to deselect checkboxes for
 */
function deselectAllPageLines(collectionId) {
    var selectedLineCount = jQuery('#' + collectionId).data('selectedlinecount');
    jQuery("#" + collectionId).find("input:checkbox." + kradVariables.SELECT_FIELD_STYLE_CLASS).each(function (index) {
        jQuery(this).attr('checked', false);
        if (selectedLineCount > 0) {
            selectedLineCount = selectedLineCount - 1;
        }
    });

    jQuery('#' + collectionId).data('selectedlinecount', selectedLineCount);
    setMultivalueLookupReturnButton(jQuery("#" + collectionId));
}

/**
 * Function that returns results field values when a return link is invoked
 *
 * @param returnLink link that was selected
 */
function returnLookupResultsByScript(returnLink) {
    var jqReturnLink = jQuery(returnLink);

    var returnData = jqReturnLink.attr(kradVariables.ATTRIBUTES.DATA_RETURN);
    var returnFieldValues = jQuery.parseJSON(returnData);

    var returnField;
    for (var returnFieldName in returnFieldValues) {
        if (!returnFieldValues.hasOwnProperty(returnFieldName)) {
            continue;
        }

        var returnFieldValue = returnFieldValues[returnFieldName];

        returnField = findElement('[name="' + escapeName(returnFieldName) + '"]');

        if (!returnField.length) {
            continue;
        }

        returnField.val(returnFieldValue);

        // trigger any ajax queries with the blur event
        returnField.focus();
        returnField.blur();

        returnField.change();
    }

    // set focus to last returned field
    if (returnField) {
        returnField.focus();
    }

    closeIframeDialog();
}

/**
 * Reload page with lookup result URL
 */
function returnLookupResultReload(returnLink) {
    var jqReturnLink = jQuery(returnLink);

    var href = jqReturnLink.attr("href");
    var target = jqReturnLink.attr("target");

    var closedLightboxNeeded = true;

    if (parent.jQuery('iframe[id*=easyXDM_]').length > 0) {
        // portal and content on same domain
        top.jQuery('iframe[id*=easyXDM_]').contents().find('#' + kradVariables.PORTAL_IFRAME_ID).attr('src', href);
    } else if (parent.parent.jQuery('#' + kradVariables.PORTAL_IFRAME_ID).length > 0) {
        // portal and content on different domain
        parent.parent.jQuery('#' + kradVariables.PORTAL_IFRAME_ID).attr('src', href);
    } else {
        window.open(href, target);
        closedLightboxNeeded = false;
    }

    if (closedLightboxNeeded) {
        closeLightbox();
    }
}

/**
 * Sets form target for the multi-value return and closes the lightbox
 */
function setupMultiValueReturn() {
    if ((parent.jQuery('iframe[id*=easyXDM_]').length > 0) || (parent.parent.jQuery('#' + kradVariables.PORTAL_IFRAME_ID).length > 0)) {
        jQuery('#' + kradVariables.KUALI_FORM).attr('target', kradVariables.PORTAL_IFRAME_ID);
    }
    else {
        jQuery('#' + kradVariables.KUALI_FORM).attr('target', '_parent');
    }

    // Data table only retains elements on the visible page within the DOM.
    // To be able to preserve selections from hidden pages, we need to extract
    // those elements from the datatable and re-insert them back into the form
    if (jQuery('table.dataTable').length > 0) {
        // Find all the input type: hidden elements in the data table
        var oTable = jQuery('.dataTable').dataTable();
        var sData = jQuery('input:hidden', oTable.fnGetNodes()).serializeArray();

        // For each hidden element insert it back to the form
        jQuery.each(sData, function (i, field) {

            jQuery('<input>').attr({
                type: 'hidden',
                id: field.id,
                name: field.name,
                value: field.value
            }).appendTo('#' + kradVariables.KUALI_FORM);
        });
    }
}

