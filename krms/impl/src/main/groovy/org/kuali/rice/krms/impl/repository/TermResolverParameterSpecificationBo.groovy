package org.kuali.rice.krms.impl.repository

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.TermParameterDefinition
import org.kuali.rice.krms.api.repository.TermParameterDefinitionContract
import org.kuali.rice.krms.api.repository.TermResolverDefinition;


public class TermResolverParameterSpecificationBo extends PersistableBusinessObjectBase {

	def String id
	def String termResolverId
	def String name
		
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static String to(TermResolverParameterSpecificationBo bo) {
	   if (bo == null) { return null }
	   return bo.name;
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static TermResolverParameterSpecificationBo from(TermResolverDefinition resolver, String name) {
	   if (resolver == null) { return null }
	   if (StringUtils.isBlank(name)) { return null }

	   TermResolverParameterSpecificationBo bo = new TermResolverParameterSpecificationBo()
	   bo.termResolverId = resolver.id;
	   bo.name = name;

	   return bo
   }
 
} 