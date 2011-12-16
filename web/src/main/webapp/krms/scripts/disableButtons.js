// Add Rule 368
// Edit Rule 371
// Left 377
// Right 383
// Up 389
// Down 395
// Cut 404
// Paste 410
// Delete 416
// refresh 422
var TREE_BUTTONS = '.tree-bar-button';
var RULE_ADD = '#368';
var RULE_REFRESH='#422';

var PROP_ADD = '#370';
var PROP_ADD_PARENT = '#376';
var PROP_REFRESH='#436';
//var EDIT_RULE = '#371';

function disableButton(id) {
    jq(id).attr('disabled', true);
// not using grayed out images yet..
//  jq(id + ' > img').attr('src', 'yourdisabledimg.jpg');
// did the css stuff in a new css-class
//  jq(id).css('cursor', 'default');
//  jq(id).css('color', 'gray');
    jq(id).removeClass('kr-button-primary');
    jq(id).removeClass('kr-button-secondary1');
    jq(id).addClass('kr-button-primary-disabled');
}

function enableButton(id) {
    jq(id).removeAttr('disabled');
    jq(id).removeClass('kr-button-primary-disabled');
    jq(id).addClass('kr-button-primary');
}

function enableAddButton() {
    enableButton(RULE_ADD);
    enableButton(PROP_ADD);
    enableButton(PROP_ADD_PARENT);
}

function enableRefreshButton() {
    enableButton(RULE_REFRESH);
    enableButton(PROP_REFRESH);
}

function enableTreeButtons() {
    enableButton(TREE_BUTTONS)
}

function disableTreeButtons() {
    disableButton(TREE_BUTTONS)
}

function propButtonsInit() {
    disableTreeButtons();
    enableAddButton();
    enableRefreshButton();
    selectedPropCheck();
}

var onProp = false;
function enabledCheck(id) {
    if (onProp) return true;

    if (id == '356') { // 356 edit
        onProp = true;
        propButtonsInit();
    } else if (id == '370') { // 370 add
        onProp = true;
        propButtonsInit();
    } else if (id == '376') { // 376 add parent
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
    } else if (id == '430') { // 430 delete
        onProp = true;
        propButtonsInit();
    } else if (id == '436') { // 436 refresh
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
    disableTreeButtons();
    enableAddButton();
    enableRefreshButton();
    selectedCheck();
});
