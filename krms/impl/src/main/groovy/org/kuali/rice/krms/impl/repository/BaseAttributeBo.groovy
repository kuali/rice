package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.BaseAttributeContract;

/**
 * This class contains the common elements of a KRMS attribute.
 * <p>
 * Attributes are used to distinguish KRMS repository entities. 
 * Rules, Actions, Contexts, Agendas and Term Resolvers have their own specific
 * attribute types. This class contains their common fields. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BaseAttributeBo extends PersistableBusinessObjectBase implements BaseAttributeContract {

	def String id
	def String attributeDefinitionId
	def String value
	def KrmsAttributeDefinitionBo attributeDefinition
	
   @Override
   public KrmsAttributeDefinitionBo getAttributeDefinition() {
	   return attributeDefinition
   }
} 