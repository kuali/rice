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

import org.kuali.rice.core.util.Node;
import org.kuali.rice.core.util.Tree;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBo;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class EditorDocument extends PersistableBusinessObjectBase {
	
	private static final long serialVersionUID = 1L;
	
	private ContextBo context;
	private AgendaBo agenda;
	private AgendaItemBo agendaItemAddLine;

    public Tree<AgendaItemBo, String> getAgendaRuleTree() {
        Tree<AgendaItemBo, String> agendaTree = new Tree<AgendaItemBo, String>();

        Node<AgendaItemBo, String> rootNode = new Node<AgendaItemBo, String>();
        agendaTree.setRootElement(rootNode);

        Node<AgendaItemBo, String> child1 = new Node<AgendaItemBo, String>();
        child1.setNodeLabel("Animal Expenses Included. Reminder to include Animal special review");
        child1.setData(new AgendaItemBo());
        rootNode.getChildren().add(child1);

        Node<AgendaItemBo, String> child2 = new Node<AgendaItemBo, String>();
        child2.setNodeLabel("Animal Expenses Included. Reminder to include Animal special review");
        child2.setData(new AgendaItemBo());
        child1.getChildren().add(child2);

        Node<AgendaItemBo, String> child3 = new Node<AgendaItemBo, String>();
        child3.setNodeLabel("Animal Expenses Included. Reminder to include Animal special review");
        child3.setData(new AgendaItemBo());
        rootNode.getChildren().add(child3);

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
