package org.kuali.rice.krms.impl.type;

import org.kuali.rice.krms.framework.type.AgendaTypeService;

/**
 * Base class for {@link org.kuali.rice.krms.framework.type.AgendaTypeService} implementations, providing
 * boilerplate for attribute building and merging from various sources.
 */
public class AgendaTypeServiceBase extends KrmsTypeServiceBase implements AgendaTypeService {

    public static final AgendaTypeService defaultAgendaTypeService = new AgendaTypeServiceBase();

}
