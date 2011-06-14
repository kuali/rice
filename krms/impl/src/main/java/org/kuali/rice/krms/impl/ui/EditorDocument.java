/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.ui;

import java.util.List;

import org.kuali.rice.core.util.Node;
import org.kuali.rice.core.util.Tree;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBo;

/**
 * BO for the KRMS agenda editor
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
// TODO: this is not a document, rename?
public class EditorDocument extends PersistableBusinessObjectBase {
	
	private static final long serialVersionUID = 1L;
	
	private ContextBo context;
	private AgendaBo agenda;
	private AgendaItemBo agendaItemAddLine;
	
	private void addAgendaItemAndChildren(Node<AgendaTreeNode, String> parent, AgendaItemBo agendaItem) {
	    Node<AgendaTreeNode, String> child = new Node<AgendaTreeNode, String>();
	    child.setNodeLabel(agendaItem.getRuleText());
	    child.setNodeType("agendaNode ruleNode");
	    child.setData(new AgendaTreeRuleNode(agendaItem));
	    parent.getChildren().add(child);
	    
	    // deal with peer nodes:
	    if (agendaItem.getAlways() != null) addAgendaItemAndChildren(parent, agendaItem.getAlways());
	    
	    // deal with child nodes:
	    if (agendaItem.getWhenTrue() != null) {
	        Node<AgendaTreeNode, String> whenTrue = new Node<AgendaTreeNode, String>();
	        whenTrue.setNodeLabel("When TRUE");
	        whenTrue.setNodeType("agendaNode logicNode whenTrueNode");
	        whenTrue.setData(new AgendaTreeLogicNode());
	        child.getChildren().add(whenTrue);
	        
	        addAgendaItemAndChildren(whenTrue, agendaItem.getWhenTrue());
	    }
        if (agendaItem.getWhenFalse() != null) {
            Node<AgendaTreeNode, String> whenFalse = new Node<AgendaTreeNode, String>();
            whenFalse.setNodeLabel("When FALSE");
            whenFalse.setNodeType("agendaNode logicNode whenFalseNode");
            whenFalse.setData(new AgendaTreeLogicNode());
            child.getChildren().add(whenFalse);
            
            addAgendaItemAndChildren(whenFalse, agendaItem.getWhenFalse());
        }
	}
	
    public Tree<? extends AgendaTreeNode, String> getAgendaRuleTree() {
        Tree<AgendaTreeNode, String> agendaTree = new Tree<AgendaTreeNode, String>();

        Node<AgendaTreeNode, String> rootNode = new Node<AgendaTreeNode, String>();
        agendaTree.setRootElement(rootNode);
        
        if (agenda != null) {
            String firstItemId = agenda.getFirstItemId();
            List<AgendaItemBo> items = agenda.getItems();
            AgendaItemBo firstItem = null;

            if (items != null && firstItemId != null) {
                for (AgendaItemBo item : items) {
                    if (firstItemId.equals(item.getId())) {
                        firstItem = item;
                        break;
                    }
                }
            }

            if (firstItem != null) addAgendaItemAndChildren(rootNode, firstItem);
        } else {
            agenda = new AgendaBo();

            // TODO: Remove.
            // For demo purposes, populate the tree even though it's bogus.
            
            Node<AgendaTreeNode, String> child1 = new Node<AgendaTreeNode, String>();
            child1.setNodeLabel("Bogus Agenda Data Check -- YES, this is bogus data.");
            child1.setNodeType("agendaNode ruleNode");
            child1.setData(new AgendaTreeRuleNode(new AgendaItemBo()));
            rootNode.getChildren().add(child1);

            Node<AgendaTreeNode, String> whenTrue1 = new Node<AgendaTreeNode, String>();
            whenTrue1.setNodeLabel("When TRUE");
            whenTrue1.setNodeType("agendaNode logicNode whenTrueNode");
            whenTrue1.setData(new AgendaTreeLogicNode());
            child1.getChildren().add(whenTrue1);

            Node<AgendaTreeNode, String> whenFalse1 = new Node<AgendaTreeNode, String>();
            whenFalse1.setNodeLabel("When FALSE");
            whenFalse1.setNodeType("agendaNode logicNode whenFalseNode");
            whenFalse1.setData(new AgendaTreeLogicNode());
            child1.getChildren().add(whenFalse1);

            Node<AgendaTreeNode, String> child2 = new Node<AgendaTreeNode, String>();
            child2.setNodeLabel("Check for Animal Facility space");
            child2.setNodeType("agendaNode ruleNode");
            child2.setData(new AgendaTreeRuleNode(new AgendaItemBo()));
            whenTrue1.getChildren().add(child2);

            Node<AgendaTreeNode, String> child3 = new Node<AgendaTreeNode, String>();
            child3.setNodeLabel("Exotic animal check");
            child3.setNodeType("agendaNode ruleNode");
            child3.setData(new AgendaTreeRuleNode(new AgendaItemBo()));
            whenFalse1.getChildren().add(child3);

            Node<AgendaTreeNode, String> child4 = new Node<AgendaTreeNode, String>();
            child4.setNodeLabel("Reptile escape insurance check");
            child4.setNodeType("agendaNode ruleNode");
            child4.setData(new AgendaTreeRuleNode(new AgendaItemBo()));
            whenFalse1.getChildren().add(child4);

            Node<AgendaTreeNode, String> child5 = new Node<AgendaTreeNode, String>();
            child5.setNodeLabel("Alien abduction permitting check");
            child5.setNodeType("agendaNode ruleNode");
            child5.setData(new AgendaTreeRuleNode(new AgendaItemBo()));
            rootNode.getChildren().add(child5);

        }
        
        return agendaTree;
    }	
	
	/**
     * @return the agendaItemAddLine
     */
    public AgendaItemBo getAgendaItemAddLine() {
        return this.agendaItemAddLine;
    }
    /**
     * @param agendaItemAddLine the agendaItemAddLine to set
     */
    public void setAgendaItemAddLine(AgendaItemBo agendaItemAddLine) {
        this.agendaItemAddLine = agendaItemAddLine;
    }
    /**
	 * @return the context
	 */
	public ContextBo getContext() {
		return this.context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(ContextBo context) {
		this.context = context;
	}
	/**
	 * @return the agenda
	 */
	public AgendaBo getAgenda() {
		return this.agenda;
	}
	/**
	 * @param agenda the agenda to set
	 */
	public void setAgenda(AgendaBo agenda) {
		this.agenda = agenda;
	}
	
	
}
