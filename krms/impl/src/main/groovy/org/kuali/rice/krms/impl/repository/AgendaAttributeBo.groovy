package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.agenda.AgendaAttribute;
import org.kuali.rice.krms.api.repository.agenda.AgendaAttributeContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

/**
 * This class represents an AgendaAttribute business object.
 * The agenda attributes are used to associate an agenda with an event and to 
 * distinguish agendas from each other.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AgendaAttributeBo extends BaseAttributeBo implements AgendaAttributeContract{

	// reference to the agenda associated with this attribute
	def String agendaId

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static AgendaAttribute to(AgendaAttributeBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.agenda.AgendaAttribute.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static AgendaAttributeBo from(AgendaAttribute im) {
	   if (im == null) { return null }

	   AgendaAttributeBo bo = new AgendaAttributeBo()
	   bo.id = im.id
	   bo.AgendaId = im.agendaId
	   bo.attributeDefinitionId = im.attributeDefinitionId
	   bo.value = im.value
	   bo.attributeDefinition = KrmsAttributeDefinitionBo.from( im.attributeDefinition )

	   return bo
   }
} 