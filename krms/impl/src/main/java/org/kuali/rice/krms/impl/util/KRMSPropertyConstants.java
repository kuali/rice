/*
 * Copyright 2008-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.util;

import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionBo;

/**
 * This class contains constants associated with the KRMS Repository 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class KRMSPropertyConstants {

	public static final class Action {
		public static final String ACTION_ID ="actionId";
	}

	public static final class Agenda {
		public static final String ID = "id";
		public static final String AGENDA_ID ="agendaId";
        public static final String NAME = "dataObject.agenda.name";
        public static final String CONTEXT = "dataObject.agenda.contextId";
	}
	
	public static final class Context {
		public static final String CONTEXT_ID ="contextId";
		public static final String NAME = "name";
		public static final String NAMESPACE = "namespace";
		public static final String ATTRIBUTE_BOS = "attributeBos";
	}

	public static final class Rule {
		public static final String RULE_ID ="ruleId";
        public static final String NAME = "dataObject.agendaItemLine.rule.name";
	}

	public static final class KrmsAttributeDefinition {
		public static final String NAME = "name";
		public static final String NAMESPACE = "namespace";		
	}

	public static final class BaseAttribute {
		public static final String ATTRIBUTE_DEFINITION_ID = "attributeDefinitionId";
		public static final String VALUE = "value";
		public static final String ATTRIBUTE_DEFINITION = "attributeDefinition";
	}

}
