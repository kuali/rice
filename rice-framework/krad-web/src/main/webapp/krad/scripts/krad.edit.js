/*
 * Copyright 2006-2014 The Kuali Foundation
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

        var $view = jQuery(this);
        showInlineEdit($view);

        return false;
    });
}

/**
 * Shows the inline edit field (and retreives it if using ajax edit option).
 *
 * <p>Adds buttons and key handlers for the save and cancel functions.  Fields will return to the original value with
 * cancel, and will be sent to the server with methodToCall "saveField" when Saved.</p>
 *
 * @param $view the div element representing the origninal read only state of the field
 */
function showInlineEdit($view) {
    var viewId = $view.attr("id");
    var editId = viewId.replace(kradVariables.INLINE_EDIT.VIEW_SUFFIX, kradVariables.INLINE_EDIT.EDIT_SUFFIX);
    var $edit = jQuery("#" + editId);
    var $control = $edit.find("[data-role='Control']");

    var $editButtonBlock = $edit.find(kradVariables.INLINE_EDIT.EDIT_BUTTONS_CLASS);

    if ($edit.length) {
        $edit.data("origVal", $control.val());
    }

    if ($edit.is(":visible")) {
        $edit.focus();
        return;
    }

    $view.hide();

    if ($view.data(kradVariables.INLINE_EDIT.AJAX_EDIT) === true && $edit.length === 0) {
        var fieldId = viewId.replace(kradVariables.INLINE_EDIT.INLINE_EDIT_VIEW, "");
        retrieveComponent(fieldId, kradVariables.REFRESH_METHOD_TO_CALL, function () {
            var $newView = jQuery("#" + viewId);
            showInlineEdit($newView);
        }, null, false, [kradVariables.NO_FIELDS_TO_SEND]);
        // Return because we are waiting for ajax component retrieval
        return;
    }

    var saveEditFunc = function (event) {
        event.preventDefault();
        _saveEdit($control, viewId);

        return false;
    };

    var cancelEditFunc = function (event) {
        event.preventDefault();

        return _cancelEdit($control, $edit, $view);
    };

    jQuery(kradVariables.INLINE_EDIT.EDIT_CLASS).each(function () {
        jQuery(this).trigger("cancel." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    });

    $edit.on("cancel." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE, function (event) {
        cancelEditFunc(event);
    });

    $edit.show();

    if (!$editButtonBlock.length) {
        $editButtonBlock = _createInlineEditButtons($control, $edit, saveEditFunc, cancelEditFunc);
    }

    $control.removeAttr("readonly");
    $control.focus();

    _setupInlineEditKeyHandlers($control, cancelEditFunc);
}

/**
 * Save the edit field by calling the saveField method on the controller passing only the control value
 *
 * @param $control the control element
 * @param viewId the id of the view div element for inline edit
 */
function _saveEdit($control, viewId) {
    var valid = true;

    if (validateClient) {
        var fieldId = getAttributeId(jQuery($control).attr('id'));
        var data = getValidationData(jQuery("#" + fieldId));
        data.useTooltip = false;

        valid = validateFieldValue($control);
    }

    if (valid) {
        // Save by retrieving a new instance of the component using the saveField method
        retrieveComponent(fieldId, kradVariables.INLINE_EDIT.SAVE_FIELD_METHOD_TO_CALL, function () {
            var $newView = jQuery("#" + viewId);
            $newView.focus();
        }, null, false, [$control.attr('name')]);

        $control.unbind("keydown." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
        $control.unbind("blur." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    }
}

/**
 * Cancel the edit on an inline edit control
 *
 * @param $control the control element
 * @param $edit the edit div element containing the edit state
 * @param $view the view div element containing the view state
 * @returns {boolean} false for handler purposes
 */
function _cancelEdit($control, $edit, $view) {
    $control.val($edit.data(kradVariables.INLINE_EDIT.ORIGINAL_VALUE));

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

    $edit.hide();
    $view.show();
    $view.focus();

    $control.unbind("keydown." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    $control.unbind("blur." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
    $edit.unbind("cancel." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);

    return false;
}

/**
 * Creates the buttons and the div containing them used by inline edit for save and cancel actions
 *
 * @param $control the control used for editing
 * @param $edit the edit element div used for inline edit
 * @param saveEditFunc the function to call on save
 * @param cancelEditFunc the function to call on cancel
 * @return the edit button div element which contains the buttons
 * @private
 */
function _createInlineEditButtons($control, $edit, saveEditFunc, cancelEditFunc) {
    var saveText = getMessage(kradVariables.MESSAGE_SAVE);
    var cancelText = getMessage(kradVariables.MESSAGE_CANCEL);
    var $editButtonBlock = jQuery("<div class='uif-inlineEdit-buttons' style='display:inline-block'></div>");

    var saveButton = jQuery("<button class='btn btn-default btn-sm icon-checkmark-circle' style='margin-right: 3px;' title='" + saveText
            + "'><span class='sr-only'>" + saveText + "</span></button>");
    saveButton.click(saveEditFunc);

    var cancelButton = jQuery("<button class='btn btn-default btn-sm icon-cancel-circle' title='" + cancelText
            + "'><span class='sr-only'>" + cancelText + "</span></button>");
    cancelButton.click(cancelEditFunc);

    $editButtonBlock.append(saveButton);
    $editButtonBlock.append(cancelButton);
    $edit.append($editButtonBlock);

    var top = $control.offset().top + $control.outerHeight();
    var left = $control.offset().left + $control.outerWidth() - $editButtonBlock.outerWidth();
    $editButtonBlock.offset({top: top, left: left});

    return $editButtonBlock;
}

/**
 * Setup the key handlers necessary for inline edit
 *
 * @param $control the control element
 * @param cancelEditFunc the cancel function to call to cancel
 */
function _setupInlineEditKeyHandlers($control, cancelEditFunc) {
    var keycodes = { 16: false, 13: false, 27: false };

    $control.on("keydown." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE,function (event) {
        var keycode = (event.keyCode ? event.keyCode : event.which);

        if (event.keyCode in keycodes) {
            keycodes[event.keyCode] = true;

            // check for escape key
            if (keycodes[27]) {
                cancelEditFunc(event);
                return;
            }
        }
    }).on("keyup." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE, function (event) {
                event.preventDefault();
                event.stopPropagation();

                if (event.keyCode in keycodes) {
                    keycodes[event.keyCode] = false;
                }

                $control.unbind("keyup." + kradVariables.INLINE_EDIT.INLINE_EDIT_NAMESPACE);
            });
}