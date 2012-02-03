/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krms.impl.type;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.framework.engine.Agenda;
import org.kuali.rice.krms.framework.engine.BasicAgenda;
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.krms.impl.provider.repository.LazyAgendaTree;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslatorImpl;
import org.kuali.rice.krms.impl.util.KRMSServiceLocatorInternal;

/**
 * Base class for {@link org.kuali.rice.krms.framework.type.AgendaTypeService} implementations, providing
 * boilerplate for attribute building and merging from various sources.
 */
public class AgendaTypeServiceBase extends KrmsTypeServiceBase implements AgendaTypeService {

    public static final AgendaTypeService defaultAgendaTypeService = new AgendaTypeServiceBase();

    @Override
    public Agenda loadAgenda(AgendaDefinition agendaDefinition) {

        if (agendaDefinition == null) { throw new RiceIllegalArgumentException("agendaDefinition must not be null"); }
        RepositoryToEngineTranslatorImpl repositoryToEngineTranslator = KRMSServiceLocatorInternal.getService(
                "repositoryToEngineTranslator");
        if (repositoryToEngineTranslator == null) {
            return null;
        }
        return new BasicAgenda(agendaDefinition.getAttributes(), new LazyAgendaTree(agendaDefinition, repositoryToEngineTranslator));
    }
}
