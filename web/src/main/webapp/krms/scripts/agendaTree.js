function getSelectedItemInput() {
    return jq('input[id="agenda_item_selected_attribute"]');
}

function ajaxCall(controllerMethod, collectionGroupId) {

    var collectionGroupDivLocator = '#' + collectionGroupId + '_div';

    var elementToBlock = jq(collectionGroupDivLocator);
    var selectedItemInput = getSelectedItemInput();
    var selectedItemId = selectedItemInput.val();
    var selectedItemInputName = selectedItemInput.attr('name');

    if (selectedItemId) {
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
    } else {
        // TODO: refactor to disabled buttons, or externalize
        alert('Please select an agenda item first.');
    }
}

function initAgendaTree(componentId) {

    var componentLocator = '#' + componentId;

    // binding to tree loaded event
    jq(componentLocator).bind('loaded.jstree', function (event, data) {
        /* make the tree load with all nodes expanded */
        jq(componentLocator).jstree('open_all');


        // rule node clicks should set the selected item
        jq('a.ruleNode').click( function() {
            var agendaItemId = jq(this.parentNode).find('input').attr('value');
            var selectedItemTracker = getSelectedItemInput()
            selectedItemTracker.val(agendaItemId);
            // make li show containment of children
            jq('li').each( function() {
                jq(this).removeClass('ruleBlockSelected');
            });
            jq(this.parentNode).addClass('ruleBlockSelected');
        });

        // set type to 'logic' on logic nodes -- this prevents them from being selected
        jq('a.logicNode').each( function() {
            jq(componentLocator).jstree('set_type', 'logic', this.parentNode);
        });

        /* mark the selected node */
        jq('a.ruleNode').each( function() {
            var agendaItemId = jq(this.parentNode).find('input').attr('value');
            var selectedItemTracker = jq('input[id=\"agenda_item_selected_attribute\"]');
            var selectedItemId = selectedItemTracker.val();

            if (selectedItemId == agendaItemId) {
                // simulate click, which will mark it
                jq(this).click();
            }
        });
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
