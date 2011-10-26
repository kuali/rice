function getSelectedPropositionInput() {
    return jq('input[id="proposition_selected_attribute"]');
}

function getCutPropositionInput() {
    return jq('input[id="proposition_cut_attribute"]');
}

function ajaxCallPropositionTree(controllerMethod, collectionGroupId) {

    var collectionGroupDivLocator = '#' + collectionGroupId + '_div';

    var elementToBlock = jq(collectionGroupDivLocator);
    var selectedItemInput = getSelectedPropositionInput();
    var selectedItemId = selectedItemInput.val();
    var selectedItemInputName = selectedItemInput.attr('name');

    var updateCollectionCallback = function(htmlContent){
        var component = jq(collectionGroupDivLocator, htmlContent);

        elementToBlock.unblock({onUnblock: function(){
            //replace component
            if(jq(collectionGroupDivLocator).length){
                jq(collectionGroupDivLocator).replaceWith(component);
            }
            runHiddenScripts(collectionGroupId + '_div');
        }
        });

    };

    ajaxSubmitForm(controllerMethod, updateCollectionCallback,
            {reqComponentId: collectionGroupId, skipViewInit: 'true', selectedItemInputName: selectedItemId},
            elementToBlock);
}

function ajaxCutPropositionTree(controllerMethod, collectionGroupId) {
    jq('a.ruleTreeNode').each( function() {
        var propositionId = jq(this.parentNode).find('input').attr('value');
        var selectedItemTracker = getSelectedPropositionInput();
        var selectedItemId = selectedItemTracker.val();
        var cutItemTracker = getCutPropositionInput();

        if (selectedItemId == propositionId) {
            // simulate click, which will mark it
            //jq(this).click();
//            jq(this.parentNode).addClass('ruleCutSelected');
//            jq(this.parentNode).removeClass('ruleBlockSelected');
            cutItemTracker.val(propositionId);
        }
    });
    ajaxCallPropositionTree(controllerMethod, collectionGroupId);
}

function ajaxPastePropositionTree(controllerMethod, collectionGroupId) {
    jq('a.ruleTreeNode').each( function() {
        jq(this.parentNode).removeClass('ruleCutSelected');
    });
    var cutItemTracker = getCutPropositionInput();
    cutItemTracker.val(null);
    ajaxCallPropositionTree(controllerMethod, collectionGroupId);
}

// binding to tree loaded event
function initRuleTree(componentId){
jq('#' + componentId).bind('loaded.jstree', function (event, data) {
    /* make the tree load with all nodes expanded */
    jq('#' + componentId).jstree('open_all');


    // rule node clicks should set the selected item
    jq('a.ruleTreeNode').click( function() {
        var propositionId = jq(this.parentNode).find('input').attr('value');
        var selectedItemTracker = getSelectedPropositionInput();
        selectedItemTracker.val(propositionId);
        // make li show containment of children
        jq('li').each( function() {
            jq(this).removeClass('ruleBlockSelected');
        });
        if (!jq(this.parentNode).hasClass('ruleCutSelected')){
            jq(this.parentNode).addClass('ruleBlockSelected');
        };
    });

    // set type to 'logic' on logic nodes -- this prevents them from being selected
    jq('a.compoundOpCodeNode').each( function() {
        jq('#' + componentId).jstree('set_type', 'logic', this.parentNode);
    });

    /* mark the selected node */
    jq('a.ruleTreeNode').each( function() {
        var propositionId = jq(this.parentNode).find('input').attr('value');
        var selectedItemTracker = getSelectedPropositionInput();
        var selectedItemId = selectedItemTracker.val();

        if (selectedItemId == propositionId) {
            // simulate click, which will mark it
            jq(this).click();
        }

        var cutItemTracker = getCutPropositionInput();
        var cutItemId = cutItemTracker.val();
        if (cutItemId == propositionId) {
            jq(this.parentNode).addClass('ruleCutSelected');
            cutItemTracker.val(cutItemId);
        } else {
            jq(this.parentNode).removeClass('ruleCutSelected');
        }
    });

    /* update sister compound operators and update proposition summary */
    jq("[name$='data.proposition.compoundOpCode']").change(function(){
      var onChangeElementId = this.id;

      jq("select").filter(function() {
        return this.id.match(
          new RegExp(onChangeElementId.replace(/^(\d+_node_)(\d+)(_.*)$/, '^$1\\d+$3$$'))
        );
      }).val(jq(this).val());

      ajaxCallPropositionTree('updateCompoundOperator', 'RuleEditorView-PropositionSummary');
    })

});

/* create the tree */
createTree(componentId, {
    'plugins' : ['themes','html_data', 'ui', 'crrm', 'types' /*, 'dnd' */ ], // disabled drag and drop plugin
    'ui' : { 'select_limit' : 1 },
    'themes' : { 'theme':'krms','dots': true ,'icons': false },
    'crrm' : {
        /* This is where you can control what is draggable onto what within the tree: */
        'move' : {
               /*
                * m.o - the node being dragged
                * m.r - the target node
                */
                'check_move' : function (m) {
                    var p = this._get_parent(m.o);
                    if(!p) return false;
                    p = p == -1 ? this.get_container() : p;

                    if (m.o.hasClass('logicNode')) return false;

                    if(p === m.np) return true;
                    if(p[0] && m.np[0] && p[0] === m.np[0]) return true;
                    return false;
                }
            }
        },
  'types' : {
       'types' : {
           /* nodes set to type 'logic' will not be selectable */
           'logic' : { 'select_node' : false }
       }
  },
  'dnd' : { 'drag_target' : false, 'drop_target' : false }
} );

}

 


