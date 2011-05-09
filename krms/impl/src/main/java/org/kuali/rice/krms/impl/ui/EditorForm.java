/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBo;

/**
 * Form for Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EditorForm extends UifFormBase {
	private static final long serialVersionUID = -2054046347823986319L;
	
	private ContextBo context;
	private AgendaBo agenda;
	private AgendaItemBo agendaItemAddLine;
	
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
