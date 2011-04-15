package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.TermParameterDefinition
import org.kuali.rice.krms.api.repository.TermParameterDefinitionContract

public class TermParameterBo extends PersistableBusinessObjectBase implements TermParameterDefinitionContract{

	def String id
	def String name
	def String value
		
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static TermParameterDefinition to(TermParameterBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.TermParameterDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static TermParameterBo from(TermParameterDefinition im) {
	   if (im == null) { return null }

	   TermParameterBo bo = new TermParameterBo()
	   bo.id = im.id
	   bo.name = im.name
	   bo.value = im.value

	   return bo
   }
 
} 