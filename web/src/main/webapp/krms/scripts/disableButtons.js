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
var ADD_RULE = '#368';
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
    enableButton(ADD_RULE);
}

function enableTreeButtons() {
    enableButton(TREE_BUTTONS)
}

function disableTreeButtons() {
    disableButton(TREE_BUTTONS)
}

function propButtonsInit() {
    disableTreeButtons();
}

// my client js isn't getting triggered?
var onProp = false;
function enabledCheck(id) {
    if (onProp) return true;

    if (id == '356') { // 356 edit
        onProp = true;
        propButtonsInit();
    } else if (id == '344') { // 244 add
        onProp = true;
        propButtonsInit();
    } else if (id == '350') { // 350 add parent
        onProp = true;
        propButtonsInit();
    } else if (id == '365') { // 365 left
        onProp = true;
        propButtonsInit();
    } else if (id == '371') { // 371 right
        onProp = true;
        propButtonsInit();
    } else if (id == '377') { // 377 up
        onProp = true;
        propButtonsInit();
    } else if (id == '388') { // 388 down
        onProp = true;
        propButtonsInit();
    } else if (id == '392') { // 392 cut
        onProp = true;
        propButtonsInit();
    } else if (id == '398') { // 398 paste
        onProp = true;
        propButtonsInit();
    } else if (id == '404') { // 404 delete
        onProp = true;
        propButtonsInit();
    }
    return onProp;
}

function selectedCheck() {
    if (getSelectedItemInput().val() != "") {
        enableTreeButtons();
    }
}

jq(document).ready(function() {
    disableTreeButtons();
    enableAddButton();
    selectedCheck();
});
