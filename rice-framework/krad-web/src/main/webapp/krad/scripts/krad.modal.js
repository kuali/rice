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

/* ========================================================================
 * Script methods related to modal dialog and lightbox content.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * ========================================================================*/

/**
 * Invoked to show a dialog group as a modal.
 *
 * <p>The content given by element with the given dialogId is showed in a modal. If the content does not exist, a call
 * to retrieve the content from the server is made first</p>
 *
 * <p>Options and event handlers can be specified using the options parameter. Valid options are:
 *    responseHandler - handler function to invoke when a response is made, function should take the response event
 *    that will contain the response value, the jQuery action component, and the id for the dialog
 *    responseEventData - additional data that should be passed to the response handler in the event.data property
 *    showHandler - handler function to invoke when the dialog is shown
 *    hideHandler - handler function to invoke when the dialog is hidden
 *    alwaysRefresh - if true, indicates the contents should always be retrieved from the server before displaying
 *    resetDataOnRefresh - if true, indicates model data for the dialog should be cleared on refresh
 * </p>
 *
 * <p>Implemented using Bootstrap Modal:
 * <a href="http://getbootstrap.com/javascript/#modals">http://getbootstrap.com/javascript/#modals</a></p>
 *
 * @param dialogId id for the element to display in a modal
 * @param options (optional) object containing options (including event callbacks)
 */
function showDialog(dialogId, options) {
    var $dialog = jQuery('#' + dialogId);

    options = options || {};

    // if dialog contents are not present, or always refresh is set we need to make the call to retrieve
    if (($dialog.length === 0) || $dialog.hasClass(kradVariables.CLASSES.PLACEHOLDER) || options.alwaysRefresh) {
        var additionalSubmitData = {};
        if (options.resetDataOnRefresh) {
            additionalSubmitData.resetDataOnRefresh = options.resetDataOnRefresh;
        }

        createPlaceholderAndRetrieve(dialogId, function () {
            // set to false for the callback so we don't keep requesting the dialog
            options.alwaysRefresh = false;

            showDialog(dialogId, options);
        }, additionalSubmitData);

        return;
    }

    _addDialogDataAttributeToActions(dialogId);

    jQuery(document).on(kradVariables.EVENTS.UPDATE_CONTENT, '#' + dialogId, function (event) {
        _addDialogDataAttributeToActions(dialogId);
    });

    _attachDialogResponseHandler(dialogId, $dialog, options.responseHandler, options.responseEventData);
    _bindShowDialogHandlers($dialog, options.showHandler);
    _bindHideDialogHandlers($dialog, options.hideHandler);

    $dialog.modal();
}

/**
 * Invoked to dismiss a dialog that is currently being shown.
 *
 * <p>If a dialog if found with the given id, its hide method is invoked. If the optional action parameter
 * is passed in and has the response data attribute, the dialog response event is thrown to trigger response
 * handlers.</p>
 *
 * <p>This method is invoked by default in kradRequest, based on any dismiss dialog options set for the action</p>
 *
 * @param dialogId id for the dialog to dismiss
 * @param $action (optional) jQuery object for the action that triggered the dismiss, used to create a
 * response dialog event
 */
function dismissDialog(dialogId, $action) {
    var $dialog = jQuery('#' + dialogId);

    if (!$dialog) {
        return;
    }

    // trigger the dialog response event if necessary
    if ($action && $action.is("[" + kradVariables.ATTRIBUTES.DATA_RESPONSE + "]")) {
        var dialogResponseEvent = jQuery.Event(kradVariables.EVENTS.DIALOG_RESPONSE);

        dialogResponseEvent.response = $action.attr(kradVariables.ATTRIBUTES.DATA_RESPONSE);
        dialogResponseEvent.action = $action;
        dialogResponseEvent.dialogId = dialogId;

        $dialog.trigger(dialogResponseEvent);
    }

    $dialog.modal('hide');
}

/**
 * Indicates whether the given jQuery object represents a modal dialog that is currently open.
 *
 * @param $element jQuery object to check
 * @returns {boolean} true if element is an open modal, false if not
 */
function isDisplayedModal($element) {
    if ($element.hasClass(kradVariables.CLASSES.MODAL) && $element.hasClass(kradVariables.CLASSES.IN)) {
        return true;
    }

    return false;
}

/**
 * Invoked to show a confirmation dialog created dynamically.
 *
 * <p>Similar to showDialog, except the dialog is created on the fly from a prototype. For simple confirmation
 * dialogs, this is a much lighter weight method since the unique dialog content doesn't have to present on the
 * view. In particular, for confirmations on collection actions this method should be used if possible.</p>
 *
 * @param confirmText text to display as the dialog prompt
 * @param headerText (optional) text to display as the dialog header
 * @param options (optional) options for the modal dialog, see showDialog for more information
 * @param protoDialodId id for a dialog to use as a prototype, must either be a valid dom element or valid component
 * id in the UIF dictionary, default to KradVariables.IDS.DIALOG_YESNO
 */
function confirmDialog(confirmText, headerText, options, protoDialogId) {
    protoDialogId = protoDialogId || kradVariables.IDS.DIALOG_YESNO;

    var $protoDialog = jQuery('#' + protoDialogId);

    options = options || {};

    // retrieve the dialog contents from the server, if necessary
    if (($protoDialog.length === 0) || $protoDialog.hasClass(kradVariables.CLASSES.PLACEHOLDER)) {
        createPlaceholderAndRetrieve(protoDialogId, function () {
            confirm(confirmText, headerText, options);
        });

        return;
    }

    var $dialog = $protoDialog.clone(true, true);

    // adjust the id so it doesn't conflict with the proto dialog
    var dialogId = protoDialogId + 'tmp';
    $dialog.attr(kradVariables.ATTRIBUTES.ID, dialogId);

    var dialogPrompt = findByDataRole(kradVariables.DATA_ROLES.PROMPTTEXT, $dialog);
    if (dialogPrompt && (dialogPrompt.length > 0)) {
        dialogPrompt.text(confirmText);
    }
    else {
        throw new Error("Unable to set dialog confirm text");
    }

    if (headerText) {
        var dialogHeaderText = findByDataRole(kradVariables.DATA_ROLES.DIALOGHEADER, $dialog);
        if (dialogHeaderText && (dialogHeaderText.length > 0)) {
            dialogHeaderText.find(":header").text(headerText);
        }
        else {
            throw new Error("Unable to set dialog header text");
        }
    }

    jQuery('body').append($dialog);

    // handler to clear out the dialog after it is closed
    $dialog.bind(kradVariables.EVENTS.HIDDEN_MODAL, function (event) {
        $dialog.remove();
    });

    showDialog(dialogId, options);
}

/**
 * Finds an elements within the dialog that contain the dismiss dialog data attribute, and adds a data attribute
 * containing the dialog id.
 *
 * <p>The dialog id data attribute is necessary on the action elements to correctly trigger dismiss dialog calls.</p>
 *
 * @param dialogId id for the dialog to add data attribute for
 * @private
 */
function _addDialogDataAttributeToActions(dialogId, $dialog) {
    jQuery('#' + dialogId).find('[' + kradVariables.ATTRIBUTES.DISMISS_DIALOG_OPTION + ']').attr(
            kradVariables.ATTRIBUTES.DIALOG_ID, dialogId);
}

/**
 * Registers any configured response handlers for the dialog response event.
 *
 * <p>If the response handler is passed in it will be registered for the event. If not, a check is also made
 * on the dialog for existence of a response handler data attribute. If found, the attribute is wrapped in a
 * event handler function and registered for the dialog response event</p>
 *
 * @param dialogId id for the dialog to register handlers for
 * @param $dialog jQuery object for the dialog
 * @param responseHandler response event handler that was initially passed into the show dialog call
 * @param responseEventData response event data that was initially passed into the show dialog call
 * @private
 * @see krad.utilty#wrapAsHandler
 */
function _attachDialogResponseHandler(dialogId, $dialog, responseHandler, responseEventData) {
    // check for a response handler defined on the dialog group itself
    if (!responseHandler && $dialog.is("[" + kradVariables.ATTRIBUTES.DATA_RESPONSE_HANDLER +"]")) {
        responseHandler = wrapAsHandler($dialog.attr(kradVariables.ATTRIBUTES.DATA_RESPONSE_HANDLER));
    }

    if (!responseHandler) {
        return;
    }

    // unbind is needed so handlers don't get attached multiple times
    $dialog.unbind(kradVariables.EVENTS.DIALOG_RESPONSE);
    $dialog.bind(kradVariables.EVENTS.DIALOG_RESPONSE, responseEventData, responseHandler);
}

/**
 * Registers any configured show handlers for the dialog response event.
 *
 * <p>If the show handler is passed in it will be registered for the event. If not, a check is also made
 * on the dialog for existence of a show handler data attribute. If found, the attribute is wrapped in a
 * event handler function and registered for the show dialog event</p>
 *
 * @param $dialog jQuery object for the dialog to register the handler for
 * @param showHandler show event handler that was initially passed into the show dialog call
 * @private
 * @see krad.utilty#wrapAsHandler
 */
function _bindShowDialogHandlers($dialog, showHandler) {
    // check for a show handler defined on the dialog group itself
    if (!showHandler && $dialog.is("[" + kradVariables.ATTRIBUTES.DATA_SHOW_HANDLER + "]")) {
        showHandler = wrapAsHandler($dialog.attr(kradVariables.ATTRIBUTES.DATA_SHOW_HANDLER));
    }

    if (showHandler) {
        $dialog.unbind(kradVariables.EVENTS.SHOW_MODAL);
        $dialog.bind(kradVariables.EVENTS.SHOW_MODAL, showHandler);
    }
}

/**
 * Registers any configured hide handlers for the dialog response event.
 *
 * <p>If the hide handler is passed in it will be registered for the event. If not, a check is also made
 * on the dialog for existence of a hide handler data attribute. If found, the attribute is wrapped in a
 * event handler function and registered for the hide dialog event</p>
 *
 * @param $dialog jQuery object for the dialog to register the handler for
 * @param hideHandler hide event handler that was initially passed into the show dialog call
 * @private
 * @see krad.utilty#wrapAsHandler
 */
function _bindHideDialogHandlers($dialog, hideHandler) {
    // check for a show handler defined on the dialog group itself
    if (!hideHandler && $dialog.is("[" + kradVariables.ATTRIBUTES.DATA_HIDE_HANDLER +"]")) {
        hideHandler = wrapAsHandler($dialog.attr(kradVariables.ATTRIBUTES.DATA_HIDE_HANDLER));
    }

    if (hideHandler) {
        $dialog.unbind(kradVariables.EVENTS.HIDE_MODAL);
        $dialog.bind(kradVariables.EVENTS.HIDE_MODAL, hideHandler);
    }
}

/**
 * Dialog response event handler that is used by default for dialogs that are triggered by the server.
 *
 * <p>First a check is made on the event.data to see if the dialog was a confirmation. If so and the response
 * was false, the handler simply returns without retriggering the action. If not, if picks up the action that
 * initially triggered the server call (which sent back the dialog), adds additional submit data for the dialog
 * response, and sends the request.</p>
 *
 * @param event dialog response event
 */
function handleServerDialogResponse(event) {
    var dialogResponse = event.response;

    // if dialog was a confirmation and they select false (cancel), just return
    var confirmationDialog = event.data.confirmation;
    if (confirmationDialog && (dialogResponse === 'false')) {
        return;
    }

    var $triggerAction = jQuery('#' + event.data.triggerActionId);
    if (!$triggerAction.length) {
        return;
    }

    var request = new KradRequest($triggerAction);

    request.additionalData.returnDialogId = event.dialogId;
    request.additionalData.returnDialogResponse = dialogResponse;
    request.additionalData.returnFromDialog = true;
    request.confirmDialogId = null;

    request.send();
}