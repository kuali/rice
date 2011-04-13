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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krms.api.engine.TermResolver;
import org.kuali.rice.krms.api.repository.AgendaDefinition;
import org.kuali.rice.krms.api.repository.ContextDefinition;
import org.kuali.rice.krms.framework.engine.Agenda;
import org.kuali.rice.krms.framework.engine.AgendaTree;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.engine.BasicContext;
import org.kuali.rice.krms.framework.engine.Context;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RepostoryToEngineTranslatorImpl implements
		RepositoryToEngineTranslator {

	@Override
	public Context translateContextDefinition(ContextDefinition contextDefinition) {
		if (contextDefinition == null) {
			return null;
		}
		List<Agenda> agendas = new ArrayList<Agenda>();
		for (AgendaDefinition agendaDefinition : contextDefinition.getAgendas()) {
			AgendaTree agendaTree = translateAgendaDefinitionToAgendaTree(agendaDefinition);
			Agenda agenda = new BasicAgenda(agendaDefinition.getEventName(), agendaDefinition.getAttributes(), agendaTree);
			agendas.add(agenda);
		}
		
		// TODO hook up the term resolvers
		List<TermResolver<?>> termResolvers = new ArrayList<TermResolver<?>>();
		
		return new BasicContext(agendas, termResolvers); 
	}
		
	@Override
	public AgendaTree translateAgendaDefinitionToAgendaTree(AgendaDefinition agendaDefinition) {
		// TODO
		return null;
	}

}
