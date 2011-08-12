<%--
 Copyright 2006-2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="widget" classname="org.kuali.rice.krad.uif.widget.TreeWidget"/>
<tiles:useAttribute name="componentId"/>
<script language="JavaScript" type="text/javascript" src="../krms/scripts/ruleTree.js"></script>

<%--
    TODO:
    TODO:
    TODO: This should be somewhere else.  In a KRMS web module?
    TODO:
    TODO:
--%>

<%--
    Invokes JS method to implement a tree plug-in
 --%>

<!-- keep track of the agenda item that is selected:
<input type="hidden" name="selected_prop" value=""/>
-->
<krad:script value="
   bindRuleTree('${componentId}');
/* create the tree */
createTree('${componentId}', {
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


"/>
