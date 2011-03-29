package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.Proposition
import org.kuali.rice.krms.api.repository.PropositionContract
import org.kuali.rice.krms.api.repository.PropositionParameter


public class CompoundPropositionBo extends PropositionBo implements PropositionContract {

//	def String compoundOpCode
//	def List<CompoundPropositionPropositionBo> compoundComponents;
//	
//	/**
//	* Converts a mutable bo to it's immutable counterpart
//	* @param bo the mutable business object
//	* @return the immutable object
//	*/
//   static CompoundProposition to(CompoundPropositionBo bo) {
//	   if (bo == null) { return null }
//	   return org.kuali.rice.krms.api.repository.CompoundProposition.Builder.create(bo).build();
//   }
//
//   /**
//	* Converts a immutable object to it's mutable bo counterpart
//	* @param im immutable object
//	* @return the mutable bo
//	*/
//   static CompoundPropositionBo from(CompoundProposition im) {
//	   if (im == null) { return null }
//
//	   CompoundPropositionBo bo = new CompoundPropositionBo()
//	   bo.propId = im.propId
//	   bo.description = im.description
//	   bo.typeId = im.typeId
//	   bo.propositionTypeCode = im.propositionTypeCode
//	   bo.parameters = new ArrayList<PropositionParameterBo>()
//	   for ( parm in im.parameters){
//		   bo.parameters.add (PropositionParameterBo.from(parm))
//	   }
//
//	   return bo
//   }
 
} 