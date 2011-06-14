package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.function.FunctionParameterDefinition
import org.kuali.rice.krms.api.repository.function.FunctionParameterDefinitionContract

public class FunctionParameterBo extends PersistableBusinessObjectBase implements FunctionParameterDefinitionContract {

	def String id
	def String name
	def String description
	def String functionId
	def String parameterType
	def Integer sequenceNumber
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static FunctionParameterDefinition to(FunctionParameterBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.function.FunctionParameterDefinition.Builder.create(bo).build()
   }

	/**
	* Converts a list of mutable bos to it's immutable counterpart
	* @param bos the list of mutable business objects
	* @return and immutable list containing the immutable objects
	*/
   static List<FunctionParameterDefinition> to(List<FunctionParameterBo> bos) {
	   if (bos == null) { return null }
	   List<FunctionParameterDefinition> parms = new ArrayList<FunctionParameterDefinition>();
	   for (FunctionParameterBo p : bos){
		   parms.add(FunctionParameterDefinition.Builder.create(p).build())
	   }
	   return Collections.unmodifiableList(parms)
   }
   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static FunctionParameterBo from(FunctionParameterDefinition im) {
	   if (im == null) { return null }

	   FunctionParameterBo bo = new FunctionParameterBo()
	   bo.id = im.id
	   bo.name = im.name
	   bo.description = im.description
	   bo.functionId = im.functionId
	   bo.parameterType = im.parameterType
	   bo.sequenceNumber = im.sequenceNumber
	   bo.versionNumber = im.versionNumber
	   return bo
   }
   
   static List<FunctionParameterBo> from(List<FunctionParameterDefinition> ims){
	   if (ims == null) {return null }
	   List<FunctionParameterBo> bos = new ArrayList<FunctionParameterBo>();
	   for (FunctionParameterBo im : ims){
		   FunctionParameterBo bo = from(im)
	   	   bos.add(bo);
	   }
	   return Collections.unmodifiableList(bos)
   }
 
} 