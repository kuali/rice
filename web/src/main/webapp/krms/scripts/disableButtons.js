/*
 * Copyright 2005-2012 The Kuali Foundation
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
var TREE = '.tree-bar-button';
var ADD = '.kr-add-button';
var REFRESH='.kr-refresh-button';

var ENABLED = true;

function disableButton(id) {
    if (ENABLED && jq(id) != null) {
        jq(id).attr('disabled', true);
//        not using grayed out images yet..
//        jq(id + ' > img').attr('src', 'yourdisabledimg.jpg');
//        did the css stuff in a new css-class
//        jq(id).css('cursor', 'default');
//        jq(id).css('color', 'gray');
        jq(id).removeClass('kr-button-primary');
        jq(id).removeClass('kr-button-secondary1');
        jq(id).addClass('kr-button-primary-disabled');
    }    
}

function enableButton(id) {
    if (ENABLED && jq(id) != null) {
        jq(id).removeAttr('disabled');
        jq(id).removeClass('kr-button-primary-disabled');
        jq(id).addClass('kr-button-primary');
    }
}

function enableAddButton() {
    enableButton(ADD);
}

function enableRefreshButton() {
    enableButton(REFRESH);
}

function enableTreeButtons() {
    enableButton(TREE)
}

function disableTreeButtons() {
    disableButton(TREE)
}

function propButtonsInit() {
    disableTreeButtons();
    enableAddButton();
    enableRefreshButton();
    selectedPropCheck();
}

var onProp = false;
// hack to disable buttons on Proposition Page when they are clicked (since document ready isn't called).
// these ID numbers are not really ids, they are parameters hardcoded and passed in from calling javascript 
function enabledCheck(id) {
    if (onProp) return true;

    if (id == '356') { // 356 edit
        onProp = true;
        propButtonsInit();
    } else if (id == 'add') {
        onProp = true;
        propButtonsInit();
    } else if (id == '385') { // 385 add parent
        onProp = true;
        propButtonsInit();
    } else if (id == '391') { // 391 left
        onProp = true;
        propButtonsInit();
    } else if (id == '397') { // 397 right
        onProp = true;
        propButtonsInit();
    } else if (id == '403') { // 403 up
        onProp = true;
        propButtonsInit();
    } else if (id == '409') { // 409 down
        onProp = true;
        propButtonsInit();
    } else if (id == '418') { // 418 cut
        onProp = true;
        propButtonsInit();
    } else if (id == '424') { // 424 paste
        onProp = true;
        propButtonsInit();
    } else if (id == 'refresh') { 
        onProp = true;
        propButtonsInit();
    } else if (id == '430') {
        onProp = true;
        propButtonsInit();
    }
    return onProp;
}

function selectedCheck() {
    if (getSelectedItemInput() != null) {
        if (getSelectedItemInput().val() != "" && getSelectedItemInput().val() != undefined) {
            enableTreeButtons();
        }
    }
}

function selectedPropCheck() {
    if (getSelectedPropositionInput() != null) {
        if (getSelectedPropositionInput().val() != "" && getSelectedPropositionInput().val() != undefined) {
            enableTreeButtons();
        }
    }
}

jq(document).ready(function() {
    if (ENABLED) {
        disableTreeButtons();
        enableAddButton();
        enableRefreshButton();
        selectedCheck();
    }
});
