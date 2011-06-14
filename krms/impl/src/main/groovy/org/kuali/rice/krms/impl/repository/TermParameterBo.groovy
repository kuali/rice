package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinitionContract;

public class TermParameterBo extends PersistableBusinessObjectBase implements TermParameterDefinitionContract{

	def String id
	def String termId
	def String name
	def String value
		
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static TermParameterDefinition to(TermParameterBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.term.TermParameterDefinition.Builder.create(bo).build()
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
	   bo.termId = im.termId
	   bo.name = im.name
	   bo.value = im.value
	   bo.versionNumber = im.versionNumber
	   
	   return bo
   }
 
} 