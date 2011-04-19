package org.kuali.rice.krms.impl.repository

import java.util.Set;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinitionContract;


public class AgendaDefinitionBo extends PersistableBusinessObjectBase implements AgendaDefinitionContract{

	def String agendaId
	def String namespace
	def String name
	def String typeId
	def String contextId

	def String firstItemId
	def Set<AgendaAttributeBo> attributes

	public String getNamespaceCode(){
		return namespace
	}
	public Map<String,String> getAttributes(){
		// TODO: Implement
		Map<String, String> map = new HashMap<String,String>()
		return null
	}
	
	/**
	 * Converts a mutable bo to it's immutable counterpart
	 * @param bo the mutable business object
	 * @return the immutable object
	 */
	static AgendaDefinition to(AgendaDefinitionBo bo) {
		if (bo == null) { return null }
		return org.kuali.rice.krms.api.repository.agenda.AgendaDefinition.Builder.create(bo).build()
	}


	/**
	* Converts a immutable object to it's mutable bo counterpart
	* TODO: move to() and from() to impl service
	* @param im immutable object
	* @return the mutable bo
	*/
   static public AgendaDefinitionBo from(AgendaDefinition im) {
	   if (im == null) { return null }

	   AgendaDefinitionBo bo = new AgendaDefinitionBo()
	   bo.setAgendaId( im.getAgendaId() )
	   bo.setNamespace( im.getNamespaceCode() )
	   bo.setName( im.getName() )
	   bo.setTypeId( im.getTypeId() )
	   bo.setContextId( im.getContextId() )
	   bo.setFirstItemId( im.getFirstItemId() )
	   List<AgendaAttributeBo> attrList = new ArrayList<AgendaAttributeBo>()
	   for (attr in im.getAttributes()){
		   attrList.add ( AgendaAttributeBo.from() )
	   }
	   bo.setAttributes(attrList)
	   return bo
   }
   

} 