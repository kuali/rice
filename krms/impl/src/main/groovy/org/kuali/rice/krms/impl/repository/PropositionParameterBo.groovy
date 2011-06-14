package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterContract

public class PropositionParameterBo extends PersistableBusinessObjectBase implements PropositionParameterContract {

	def String id
	def String propId
	def String value
	def String parameterType
	def Integer sequenceNumber
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static PropositionParameter to(PropositionParameterBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.proposition.PropositionParameter.Builder.create(bo).build()
   }

	/**
	* Converts a list of mutable bos to it's immutable counterpart
	* @param bos the list of smutable business objects
	* @return and immutable list containing the immutable objects
	*/
   static List<PropositionParameter> to(List<PropositionParameterBo> bos) {
	   if (bos == null) { return null }
	   List<PropositionParameter> parms = new ArrayList<PropositionParameter>();
	   for (PropositionParameterBo p : bos){
		   parms.add(PropositionParameter.Builder.create(p).build())
	   }
	   return Collections.unmodifiableList(parms)
   }
   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static PropositionParameterBo from(PropositionParameter im) {
	   if (im == null) { return null }

	   PropositionParameterBo bo = new PropositionParameterBo()
	   bo.id = im.id
	   bo.propId = im.propId
	   bo.value = im.value
	   bo.parameterType = im.parameterType
	   bo.sequenceNumber = im.sequenceNumber
	   bo.versionNumber = im.versionNumber
	   return bo
   }
   
   static List<PropositionParameterBo> from(List<PropositionParameter> ims){
	   if (ims == null) {return null }
	   List<PropositionParameterBo> bos = new ArrayList<PropositionParameterBo>();
	   for (PropositionParameterBo im : ims){
		   PropositionParameterBo bo = new PropositionParameterBo()
		   bo.id = im.id
		   bo.propId = im.propId
		   bo.value = im.value
		   bo.parameterType = im.parameterType
		   bo.sequenceNumber = im.sequenceNumber
		   bo.versionNumber = im.versionNumber
	   	   bos.add(bo);
	   }
	   return Collections.unmodifiableList(bos)
   }
 
} 