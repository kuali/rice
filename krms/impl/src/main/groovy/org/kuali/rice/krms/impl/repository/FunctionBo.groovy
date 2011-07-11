package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.MutableInactivatable
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.function.FunctionDefinition
import org.kuali.rice.krms.api.repository.function.FunctionDefinitionContract

public class FunctionBo extends PersistableBusinessObjectBase implements MutableInactivatable, FunctionDefinitionContract {

	def String id
	def String namespace
	def String name
	def String description
	def String returnType
	def String typeId
	def boolean active	

	def List<FunctionParameterBo> parameters
    def List<CategoryBo> categories
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static FunctionDefinition to(FunctionBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.function.FunctionDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static FunctionBo from(FunctionDefinition im) {
	   if (im == null) { return null }

	   FunctionBo bo = new FunctionBo()
	   bo.id = im.id
	   bo.namespace = im.namespace
	   bo.name = im.name
	   bo.description = im.description
	   bo.returnType = im.returnType
	   bo.typeId = im.typeId
	   bo.active = im.active
	   bo.versionNumber = im.versionNumber
	   bo.parameters = new ArrayList<FunctionParameterBo>()
	   for (parm in im.parameters){
		   bo.parameters.add( FunctionParameterBo.from(parm) )
	   }
       bo.categories = new ArrayList<CategoryBo>()
       for (category in im.categories) {
           bo.categories.add(CategoryBo.from(category))
       }
	   bo.versionNumber = im.versionNumber
	   return bo
   }
 
   
} 