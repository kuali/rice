
// binding to tree loaded event
function bindRuleTree(componentId){
jq('#' + componentId).bind('loaded.jstree', function (event, data) {
    /* make the tree load with all nodes expanded */
    jq('#' + componentId).jstree('open_all');


    // rule node clicks should set the selected item
    jq('a.ruleTreeNode').click( function() {
        var propositionId = jq(this.parentNode).find('input').attr('value');
        var selectedItemTracker = jq('input[name=\"agenda_item_selected\"]');
        selectedItemTracker.val(propositionId);
        // make li show containment of children
        jq('li').each( function() {
            jq(this).removeClass('ruleBlockSelected');
        });
        jq(this.parentNode).addClass('ruleBlockSelected');
    });

    // set type to 'logic' on logic nodes -- this prevents them from being selected
    jq('a.compoundOpCodeNode').each( function() {
        jq('#' + componentId).jstree('set_type', 'logic', this.parentNode);
    });

    /* mark the selected node */
    jq('a.ruleTreeNode').each( function() {
        var propositionId = jq(this.parentNode).find('input').attr('value');
        var selectedItemTracker = jq('input[name=\"agenda_item_selected\"]');
        var selectedItemId = selectedItemTracker.val();

        if (selectedItemId == propositionId) {
            // simulate click, which will mark it
            jq(this).click();
        }
    });
});
}

 


