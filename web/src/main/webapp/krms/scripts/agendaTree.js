

function ajaxDelete(collectionGroupId) {
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

        ajaxSubmitForm('ajaxDelete', updateCollectionCallback,
                {reqComponentId: collectionGroupId, skipViewInit: 'true', agenda_item_selected: selectedItemId},
                elementToBlock);
    }
}

jq(document).ready(function() {
    // rule nodes should set the selected item
    jq('a.ruleNode').click( function() {
        var agendaItemId = jq(this.parentNode).find('input').attr('value');
        var selectedItemTracker = jq('input[name="agenda_item_selected"]');
        selectedItemTracker.val(agendaItemId);
        // make li show containment of children
        jq('li').each( function() {
            jq(this).removeClass('ruleBlockSelected');
        });
        jq(this.parentNode).addClass('ruleBlockSelected');
    });
    // logic nodes should clear the selected item
    jq('a.logicNode').click( function() {
        var selectedItemTracker = jq('input[name="agenda_item_selected"]');
        selectedItemTracker.val('');
    });
});
