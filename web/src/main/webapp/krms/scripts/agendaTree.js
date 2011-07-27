

function ajaxCall(controllerMethod, collectionGroupId) {
    var elementToBlock = jq('#' + collectionGroupId + '_div');
    var selectedItemId = jq('input[name="agenda_item_selected"]').val();

    if (selectedItemId) {
        var updateCollectionCallback = function(htmlContent){
            var component = jq('#' + collectionGroupId + '_div', htmlContent);

            elementToBlock.unblock({onUnblock: function(){
                //replace component
                if(jq('#' + collectionGroupId + '_div').length){
                    jq('#' + collectionGroupId + '_div').replaceWith(component);
                }
                runHiddenScripts(collectionGroupId + '_div');
            }
            });

        };

        ajaxSubmitForm(controllerMethod, updateCollectionCallback,
                {reqComponentId: collectionGroupId, skipViewInit: 'true', agenda_item_selected: selectedItemId},
                elementToBlock);
    } else {
        alert('Please select an agenda item first.');
    }
}

