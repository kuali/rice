package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.TermResolverDefinition
import org.kuali.rice.krms.api.repository.TermResolverDefinitionContract

public class TermResolverBo extends PersistableBusinessObjectBase implements TermResolverDefinitionContract {

	def String id
	def String namespaceCode
	def String name
	def String typeId
	def String outputId
	
	def TermSpecificationBo output
	def Set<TermSpecificationBo> prerequisites
	def Set<String> parameterNames
	def Set<TermResolverAttributeBo> attributes
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static TermResolverDefinition to(TermResolverBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.TermResolverDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static TermResolverBo from(TermResolverDefinition im) {
	   if (im == null) { return null }

	   TermResolverBo bo = new TermResolverBo()
	   bo.id = im.id
	   bo.namespaceCode = im.namespaceCode
	   bo.name = im.name
	   bo.typeId = im.typeId
	   bo.output = TermSpecificationBo.from(im.output)
	   bo.parameterNames in im.parameterNames
	   bo.prerequisites = new HashSet<TermSpecificationBo>()
	   for (prereq in im.prerequisites){
		   bo.prerequisites.add (TermSpecificationBo.from(prereq))
	   }
	   bo.attributes = new HashSet<TermResolverAttributeBo>()
	   for (attr in im.attributes){
		   bo.attributes.add (TermResolverAttributeBo.from(attr))
	   }	   
	   return bo
   }
   public TermSpecificationBo getOutput(){
	   return output;
   }

} 