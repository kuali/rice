/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.impl.repository;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.AgendaDefinition;
import org.kuali.rice.krms.api.repository.AgendaAttribute;

import java.util.*;

public final class AgendaRepositoryServiceImpl implements AgendaRepositoryService {

    private BusinessObjectService businessObjectService;

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   public AgendaDefinition to(AgendaDefinitionBo bo) {
	   if (bo == null) { return null; }
	   // TODO implement
	   return null;
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* TODO: move to() and from() to impl service
	* @param im immutable object
	* @return the mutable bo
	*/
   public AgendaDefinitionBo from(AgendaDefinition im) {
	   if (im == null) { return null; }

	   AgendaDefinitionBo bo = new AgendaDefinitionBo();
	   bo.setAgendaId( im.getAgendaId() );
	   bo.setNamespace( im.getNamespaceCode() );
	   bo.setName( im.getName() );
	   bo.setTypeId( im.getTypeId() );
	   bo.setContextId( im.getContextId() );
	   bo.setFirstItemId( im.getFirstItemId() );
	   List<AgendaAttributeBo> attrList = new ArrayList<AgendaAttributeBo>();
	   for (AgendaAttribute attr : im.getAttributes()){
		   attrList.add ( AgendaAttributeBo.from(attr) );
	   }
	   bo.setAttributes(attrList);
	   return bo;
   }
 

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Converts a List<AgendaBo> to an Unmodifiable List<Agenda>
     *
     * @param AgendaBos a mutable List<AgendaBo> to made completely immutable.
     * @return An unmodifiable List<Agenda>
     */
    List<AgendaDefinition> convertListOfBosToImmutables(final Collection<AgendaDefinitionBo> agendaBos) {
        ArrayList<AgendaDefinition> agendas = new ArrayList<AgendaDefinition>();
        for (AgendaDefinitionBo bo : agendaBos) {
            AgendaDefinition agenda = to(bo);
            agendas.add(agenda);
        }
        return Collections.unmodifiableList(agendas);
    }

}
