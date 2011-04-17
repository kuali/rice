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
package org.kuali.rice.krms.impl.provider.repository;

import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.repository.ActionDefinition;
import org.kuali.rice.krms.api.repository.AgendaDefinition;
import org.kuali.rice.krms.api.repository.AgendaTreeDefinition;
import org.kuali.rice.krms.api.repository.ContextDefinition;
import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.api.repository.RuleDefinition;
import org.kuali.rice.krms.api.repository.TermDefinition;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.engine.Agenda;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.Context;
import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.rice.krms.framework.engine.Rule;
import org.kuali.rice.krms.framework.engine.SubAgenda;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface RepositoryToEngineTranslator {

	public Context translateContextDefinition(ContextDefinition contextDefinition);
	
	public Agenda translateAgendaDefinition(AgendaDefinition agendaDefinition);
	
	public AgendaTree translateAgendaDefinitionToAgendaTree(AgendaDefinition agendaDefinition);
	
	public AgendaTree translateAgendaTreeDefinition(AgendaTreeDefinition agendaTreeDefinition);
		
	public Rule translateRuleDefinition(RuleDefinition ruleDefinition);
	
	public SubAgenda translateAgendaTreeDefinitionToSubAgenda(AgendaTreeDefinition subAgendaDefinition);
	
	public Proposition translatePropositionDefinition(PropositionDefinition propositionDefinition);
	
	public Action translateActionDefinition(ActionDefinition actionDefinition);
	
}
