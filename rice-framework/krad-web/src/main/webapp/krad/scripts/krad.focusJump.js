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
 * Invoked after the page or a component is refreshed to perform any repositioning or setting
 * of focus
 *
 * @param setFocus - boolean that indicates whether focus should be set, if false just the jump will be performed
 * @param focusId - id of the dom element to focus on
 * @param jumpToId - id of the dom element to jump to
 * @param jumpToName - name of the dom element to jump to
 */
function performFocusAndJumpTo(setFocus, focusId, jumpToId, jumpToName) {
    if (setFocus) {
        performFocus(focusId);
    }

    if (jumpToId || jumpToName) {
        performJumpTo(jumpToId, jumpToName);
    }
}

//performs a 'jump' - a scroll to the necessary html element
function performJumpTo(jumpToId, jumpToName) {
    if (jumpToId) {
        if (jumpToId.toUpperCase() === "TOP") {
            jumpToTop();
        }
        else if (jumpToId.toUpperCase() === "BOTTOM") {
            jumpToBottom();
        }
        else {
            jumpToElementById(jumpToId);
        }
    }
    else if (jumpToName) {
        jumpToElementByName(jumpToName);
    }
    else {
        jumpToTop();
    }
}

/**
 * Performs a focus on an the element with the id preset
 *
 * @param focusId - id of the dom element to focus on
 * @param autoFocus - boolean that indicates where focus to top should happen if focus to not set
 */
function performFocus(focusId) {
    // Check to see if there are errors on the view. If error messages present use the first link of
    // validation messages as the focusId
    var errorMessageItem = jQuery(".uif-errorMessageItem").first().find("a");
    if(errorMessageItem.length > 0) {
        errorMessageItem.focus();
        return;
    }

    if (!focusId) {
        return;
    }

    if (focusId == "FIRST") {
        var id = jQuery("div[data-role='InputField']:first [data-role='Control']:input:first", "#kualiForm").attr("id");
        focus(id);
        return;
    }

    if (focusId.match("^" + kradVariables.NEXT_INPUT.toString())) {
        focusId = focusId.substr(kradVariables.NEXT_INPUT.length, focusId.length);
        var original = jQuery("#" + focusId);
        var inputs = jQuery(":input:visible, a:visible:not(\"a[data-role='disclosureLink']\")");
        var index = jQuery(inputs).index(original);
        if (index && jQuery(inputs).length > index + 1) {
            var id = jQuery(inputs).eq(index + 1).attr("id");
            focus(id);
        }
    } else {
        var focusElement = jQuery("#" + focusId);
        if (focusElement.length) {
            focus(focusId);
        }
        else {
            focusId = focusId.replace(/_control\S*/, "");
            focusElement = jQuery("#" + focusId).find(":input:visible, a:visible").first();
            if (focusElement.length) {
                focus(jQuery(focusElement).attr("id"));
            }
        }

    }
}

//performs a focus on an the element with the name specified
function focusOnElementByName(name) {
    var theElement = jQuery("[name='" + escapeName(name) + "']");
    if (theElement.length != 0) {
        theElement.focus();
    }
}

//performs a focus on an the element with the id specified
function focusOnElementById(focusId) {
    if (focusId) {
        jQuery("#" + focusId).focus();
    }
}

/**
 * This function focuses the element and if its a textual input puts the cursor after the content
 *
 * @param id
 */
function focus(id) {
    var inputField = document.getElementById(id);
    if (inputField != null && jQuery(inputField).is(":text,textarea,:password") &&
            inputField.value && inputField.value.length != 0) {
        if (inputField.createTextRange) {
            var FieldRange = inputField.createTextRange();
            FieldRange.moveStart('character', inputField.value.length);
            FieldRange.collapse();
            FieldRange.select();
        } else if (inputField.selectionStart ||
                (inputField.selectionStart != undefined && inputField.selectionStart == '0')) {
            var elemLen = inputField.value.length;
            inputField.selectionStart = elemLen;
            inputField.selectionEnd = elemLen;
            inputField.focus();
        }
    } else if (inputField != null) {
        inputField.focus();
    }
}

//Jump(scroll) to an element by name
function jumpToElementByName(name) {
    var theElement = jq("[name='" + escapeName(name) + "']");
    if (theElement.length != 0) {
        if (!usePortalForContext() || jQuery("#fancybox-frame", parent.document).length) {
            jQuery.scrollTo(theElement, 1);
        }
        else {
            var headerOffset = top.jQuery("#header").outerHeight(true) + top.jQuery(".header2").outerHeight(true);
            top.jQuery.scrollTo(theElement, 1, {offset: {top: headerOffset}});
        }
    }
}

//Jump(scroll) to an element by Id
function jumpToElementById(id) {
    var theElement = jq("#" + id);
    if (theElement.length != 0) {
        if (!usePortalForContext() || jQuery("#fancybox-frame", parent.document).length) {
            jQuery.scrollTo(theElement, 1);
        }
        else {
            var headerOffset = top.jQuery("#header").outerHeight(true) + top.jQuery(".header2").outerHeight(true);
            top.jQuery.scrollTo(theElement, 1, {offset: {top: headerOffset}});
        }
    }
}

//Jump(scroll) to the top of the current screen
function jumpToTop() {
    if (!usePortalForContext() || jQuery("#fancybox-frame", parent.document).length || !top.jQuery.scrollTo) {
        jQuery.scrollTo(0);
    }
    else {
        top.jQuery.scrollTo(0);
    }
}

//Jump(scroll) to the bottom of the current screen
function jumpToBottom() {
    if (!usePortalForContext() || jQuery("#fancybox-frame", parent.document).length) {
        jQuery.scrollTo("max", 1);
    }
    else {
        top.jQuery.scrollTo("max", 1);
    }
}
