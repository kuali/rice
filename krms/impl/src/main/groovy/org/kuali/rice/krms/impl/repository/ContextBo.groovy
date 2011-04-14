package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.ContextDefinition
import org.kuali.rice.krms.api.repository.ContextDefinitionContract


public class ContextBo extends PersistableBusinessObjectBase implements ContextDefinitionContract {

	def String contextId
	def String name
	def String namespace
	def String typeId

	def Set<AgendaDefinitionBo> agendas
		
	def Set<ContextAttributeBo> attributes
	def Set<ContextValidEventBo> validEvents
	def Set<ContextValidActionBo> validActions
	
	def Long versionNumber
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static ContextDefinition to(ContextBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.ContextDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static ContextBo from(ContextDefinition im) {
	   if (im == null) { return null }

	   ContextBo bo = new ContextBo()
	   bo.contextId = im.contextDefinitionId
	   bo.namespace = im.namespaceCode
	   bo.name = im.name
	   bo.typeId = im.typeId
	   bo.agendas = new HashSet<AgendaDefinitionBo>()
	   for (agenda in im.agendas){
		   bo.agendas.add( AgendaDefinitionBo.from(agenda) )
	   }
	   
	   bo.versionNumber = im.versionNumber
	   return bo
   }
 
   @Override
   public String getContextDefinitionId(){
	   return contextId
   }
   @Override
   public String getNamespaceCode(){
	   return namespace
   }
   
   @Override
   public Set<AgendaDefinitionBo> getAgendas(){
	   return agendas
   }
   

} 