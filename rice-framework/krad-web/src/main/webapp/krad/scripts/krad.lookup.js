/*
 * Copyright 2005-2013 The Kuali Foundation
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

jQuery(document).ready(function () {
    // multi value select handler to enable/disable return selected button for checkboxes with uif-select-line
    jQuery(document).on("change", "table.dataTable input:checkbox." + kradVariables.SELECT_FIELD_STYLE_CLASS, function (e) {
        setMultivalueLookupReturnButton(this);
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
 * Enables the return selected button on the multi value lookup when at least one item is selected.
 *
 * @param selectControl select control the change event occurred on
 */
function setMultivalueLookupReturnButton(selectControl) {
    refreshDatatableCellRedraw(selectControl);

    var oTable = getDataTableHandle(getParentRichTableId(selectControl));

    var checked = false;
    jQuery.each(getDataTablesColumnData(0, oTable), function (index, value) {
        if (jQuery(':input:checked', value).length) {
            checked = true;
        }
    });

    if (checked) {
        jQuery(':button.' + kradVariables.RETURN_SELECTED_ACTION_CLASS).removeAttr('disabled');
        jQuery(':button.' + kradVariables.RETURN_SELECTED_ACTION_CLASS).removeClass('disabled');
    } else {
        jQuery(':button.' + kradVariables.RETURN_SELECTED_ACTION_CLASS).attr('disabled', 'disabled');
    }
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

    closeLightbox();
}

/**
 * Reload page with lookup result URL
 */
function returnLookupResultReload(returnLink) {
    var jqReturnLink = jQuery(returnLink);

    var href = jqReturnLink.attr("href");
    var target = jqReturnLink.attr("target");

    var closeLightbox = true;

    if (parent.jQuery('iframe[id*=easyXDM_]').length > 0) {
        // portal and content on same domain
        top.jQuery('iframe[id*=easyXDM_]').contents().find('#' + kradVariables.PORTAL_IFRAME_ID).attr('src', href);
    } else if (parent.parent.jQuery('#' + kradVariables.PORTAL_IFRAME_ID).length > 0) {
        // portal and content on different domain
        parent.parent.jQuery('#' + kradVariables.PORTAL_IFRAME_ID).attr('src', href);
    } else {
        window.open(href, target);

        if (!target || (target === "_self")) {
            closeLightbox = false;
        }
    }

    if (closeLightbox) {
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

    closeLightbox();
}

