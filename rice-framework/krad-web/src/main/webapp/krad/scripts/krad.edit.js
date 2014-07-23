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
 *  Sets up the click event for inline edit fields.
 */
function initInlineEditFields() {
    jQuery(document).on("click", kradVariables.INLINE_EDIT.VIEW_CLASS, function (event) {
        event.preventDefault();

        // "View" refers to the view state of the inline edit, inline edits have a view state and edit state
        // this is the component which contains the view state
        var $viewButton = jQuery(this);
        showInlineEdit($viewButton);

        return false;
    });
}

/**
 * Shows the inline edit field (and retreives it if using ajax edit option).
 *
 * <p>Adds buttons and key handlers for the save and cancel functions.  Fields will return to the original value with
 * cancel, and will be sent to the server with methodToCall "saveField" when Saved.</p>
 *
 * @param $viewButton the element representing the origninal read only state of the field
 */
function showInlineEdit($viewButton) {
    // Inline edits have 2 states: a view state and an edit state
    var viewButtonId = $viewButton.attr("id");

    // Derive the editDivId from the viewButtonId by replacing its suffix
    var editDivId = viewButtonId.replace(kradVariables.INLINE_EDIT.VIEW_SUFFIX, kradVariables.INLINE_EDIT.EDIT_SUFFIX);

    var $editDiv = jQuery("#" + editDivId);
    var $control = $editDiv.find("[data-role='Control']");

    // Focus on the control if somehow the editDiv is visible already after an ajax retrieval
    if ($editDiv.is(":visible")) {
        $control.focus();
        return;
    }

    // Save original value of the control to be restored on cancel
    if ($editDiv.length) {
        $editDiv.data(kradVariables.INLINE_EDIT.ORIGINAL_VALUE, $control.val());
    }

    // If the edit version of the field does not exist, retrieve it
    if ($viewButton.data(kradVariables.INLINE_EDIT.AJAX_EDIT) === true && $editDiv.length === 0) {
        var fieldId = viewButtonId.replace(kradVariables.INLINE_EDIT.INLINE_EDIT_VIEW, "");
        retrieveComponent(fieldId, kradVariables.REFRESH_METHOD_TO_CALL, function () {
            var $newView = jQuery("#" + viewButtonId);

            // Recall this function to show the edit state of the retrieved field
            showInlineEdit($newView);
        });
        // Return because we are waiting for ajax component retrieval
        return;
    }

    $viewButton.hide();

    // Creating save function to be used by the save button (created here to use current var handles)
    var saveEditFunc = function (event) {
        event.preventDefault();
        _saveEdit($control, viewButtonId);

        return false;
    };

    // Creating cancel function to be used by the cancel button and other cancel actions
    // (created here to use current var handles)
    var cancelEditFunc = function (event) {
        event.preventDefault();

        return _cancelEdit($control, $editDiv, $viewButton);
    };

    // Cancel any edit fields that are currently open because we only allow one inline edit at a time
    jQuery(kradVariables.INLINE_EDIT.EDIT_CLASS).each(function () {
        jQuery(this).trigger("cancel." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    });

    // Add an handler for the cancel event used to close this edit when another is opened
    $editDiv.on("cancel." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE, function (event) {
        cancelEditFunc(event);
    });

    // Show the edit state
    $control.removeAttr("readonly");
    $editDiv.show();
    $control.focus();

    // Check to see if the buttons exist, if they don't create and append them (must happen here to correctly
    // position them)
    var $editButtonDiv = $editDiv.find(kradVariables.INLINE_EDIT.EDIT_BUTTONS_CLASS);
    if (!$editButtonDiv.length) {
        $editButtonDiv = _createInlineEditButtons($control, $editDiv, saveEditFunc, cancelEditFunc);
    }

    // Setup key handlers for inline edit
    _setupInlineEditKeyHandlers($control, cancelEditFunc);
}

/**
 * Save the edit field by calling the saveField method on the controller passing only the control value
 *
 * @param $control the control element
 * @param viewButtonId the id of the view div element for inline edit
 */
function _saveEdit($control, viewButtonId) {
    var valid = true;

    // Validate the new field value
    if (validateClient) {
        var fieldId = getAttributeId(jQuery($control).attr('id'));
        var data = getValidationData(jQuery("#" + fieldId));
        data.useTooltip = false;

        valid = validateFieldValue($control);
    }

    if (valid) {
        var propertyName = $control.attr('name');
        // Save by retrieving a new instance of the component using the saveField method
        retrieveComponent(fieldId, kradVariables.INLINE_EDIT.SAVE_FIELD_METHOD_TO_CALL, function () {
            var $newView = jQuery("#" + viewButtonId);
            $newView.focus();
        }, {saveFieldPath: propertyName}, false, [propertyName]);

        $control.unbind("keydown." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    }
}

/**
 * Cancel the edit on an inline edit control
 *
 * @param $control the control element
 * @param $editDiv the edit div element containing the edit state
 * @param $viewButton the view div element containing the view state
 * @returns {boolean} false for handler purposes
 */
function _cancelEdit($control, $editDiv, $viewButton) {
    $control.val($editDiv.data(kradVariables.INLINE_EDIT.ORIGINAL_VALUE));

    // Check for a very rare case where the cancel value may not be valid on the form anymore (this should never happen
    // if original data is truly valid).  May also occur in cross field constraint situations.
    var valid = true;
    if (validateClient) {
        var fieldId = getAttributeId(jQuery($control).attr('id'));
        var data = getValidationData(jQuery("#" + fieldId));
        data.useTooltip = false;

        valid = validateFieldValue($control);
    }

    if (!valid) {
        $control.focus();
        return false;
    }

    $editDiv.hide();
    $viewButton.show();
    $viewButton.focus();

    // Remove no longer needed handlers
    $control.unbind("keydown." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    $editDiv.unbind("cancel." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);

    return false;
}

/**
 * Creates the buttons and the div containing them used by inline edit for save and cancel actions
 *
 * @param $control the control used for editing
 * @param $editDiv the edit element div used for inline edit
 * @param saveEditFunc the function to call on save
 * @param cancelEditFunc the function to call on cancel
 * @return the edit button div element which contains the buttons
 * @private
 */
function _createInlineEditButtons($control, $editDiv, saveEditFunc, cancelEditFunc) {
    var saveText = getMessage(kradVariables.MESSAGE_SAVE);
    var cancelText = getMessage(kradVariables.MESSAGE_CANCEL);

    // Create Save and Cancel button content for inline edit
    var $editButtonDiv = jQuery("<div class='uif-inlineEdit-buttons' style='display:inline-block'></div>");

    var saveButton = jQuery("<button class='btn btn-default btn-sm icon-checkmark-circle' style='margin-right: 3px;' title='" + saveText
            + "'><span class='sr-only'>" + saveText + "</span></button>");
    saveButton.click(saveEditFunc);

    var cancelButton = jQuery("<button class='btn btn-default btn-sm icon-cancel-circle' title='" + cancelText
            + "'><span class='sr-only'>" + cancelText + "</span></button>");
    cancelButton.click(cancelEditFunc);

    $editButtonDiv.append(saveButton);
    $editButtonDiv.append(cancelButton);
    $editDiv.append($editButtonDiv);

    // Position the buttons at the bottom right of the control
    var top = $control.offset().top + $control.outerHeight();
    var left = $control.offset().left + $control.outerWidth() - $editButtonDiv.outerWidth();
    $editButtonDiv.offset({top: top, left: left});

    return $editButtonDiv;
}

/**
 * Setup the key handlers necessary for inline edit
 *
 * @param $control the control element
 * @param cancelEditFunc the cancel function to call to cancel
 */
function _setupInlineEditKeyHandlers($control, cancelEditFunc) {
    $control.on("keydown." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE,function (event) {
        var keycode = (event.keyCode ? event.keyCode : event.which);

        // check for escape key
        if (keycode === 27) {
            cancelEditFunc(event);
            return;
        }

    });
}

/**
 * Activate (ie show) the edit version of an inline edit enabled field by field id
 *
 * @param id the id of the field to change to edit mode
 */
function activateInlineEdit(id) {
    var $viewButton = jQuery("#" + id + "_inlineEdit_view");
    showInlineEdit($viewButton);
}