package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinitionContract;


public class TermBo extends PersistableBusinessObjectBase implements TermDefinitionContract {

	def String id
	def String specificationId

	def TermSpecificationBo specification
	def Set<TermParameterBo> parameters

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static TermDefinition to(TermBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.term.TermDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static TermBo from(TermDefinition im) {
	   if (im == null) { return null }

	   TermBo bo = new TermBo()
	   bo.id = im.id
	   bo.specificationId = im.specification.id
	   bo.specification = TermSpecificationBo.from(im.specification)
	   bo.parameters = new ArrayList<TermParameterBo>()
	   for (parm in im.parameters){
		   bo.parameters.add ( TermParameterBo.from(parm) )
	   }
	   return bo
   }
 
   public TermSpecificationBo getSpecification(){
	   return specification
   }
   
   public Set<TermParameterBo> getParameters(){
	   return parameters
   }
   
} 