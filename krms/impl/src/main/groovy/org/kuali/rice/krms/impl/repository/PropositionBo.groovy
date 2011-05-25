package org.kuali.rice.krms.impl.repository


import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;


public class PropositionBo extends PersistableBusinessObjectBase implements PropositionDefinitionContract {

	def String id
	def String description
    def String ruleId
	def String typeId
	def String propositionTypeCode
	
	def List<PropositionParameterBo> parameters
	
	// Compound parameter related properties
	def String compoundOpCode
	def List<PropositionBo> compoundComponents
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static PropositionDefinition to(PropositionBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.proposition.PropositionDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static PropositionBo from(PropositionDefinition im) {
	   if (im == null) { return null }

	   PropositionBo bo = new PropositionBo()
	   bo.id = im.id
	   bo.description = im.description

       //bo.ruleId = im.ruleId
       setRuleIdRecursive(im.ruleId, bo)
       
	   bo.typeId = im.typeId
	   bo.propositionTypeCode = im.propositionTypeCode
	   bo.parameters = new ArrayList<PropositionParameterBo>()
	   for ( parm in im.parameters){
		   bo.parameters.add (PropositionParameterBo.from(parm))
	   }
	   bo.compoundOpCode = im.compoundOpCode
	   bo.compoundComponents = new ArrayList<PropositionBo>()
	   for (prop in im.compoundComponents){
		   bo.compoundComponents.add (PropositionBo.from(prop))
	   }
	   bo.versionNumber = im.versionNumber
	   return bo
   }
   
   private static void setRuleIdRecursive(String ruleId, PropositionBo prop) {
       prop.ruleId = ruleId;
       if (prop.compoundComponents != null) for (PropositionBo child : prop.compoundComponents) if (child != null) {
           setRuleIdRecursive(ruleId, child);
       }
   }
 
} 