package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase


public class AgendaBo extends PersistableBusinessObjectBase {

	def String agendaId
	def String namespace
	def String name
	def String typeId
	def String contextId

	def String firstItemId
	def List<AgendaAttributeBo> attributes

//	
//	/**
//	* Converts a mutable bo to it's immutable counterpart
//	* @param bo the mutable business object
//	* @return the immutable object
//	*/
//   static Agenda to(AgendaBo bo) {
//	   if (bo == null) { return null }
//	   return org.kuali.rice.krms.api.repository.Agenda.Builder.create(bo).build()
//   }
//
//   /**
//	* Converts a immutable object to it's mutable bo counterpart
//	* TODO: move to() and from() to impl service
//	* @param im immutable object
//	* @return the mutable bo
//	*/
//   static AgendaBo from(Agenda im) {
//	   if (im == null) { return null }
//
//	   AgendaBo bo = new AgendaBo()
//	   bo.agendaId = im.agendaId
//	   bo.namespace = im.namespace
//	   bo.name = im.name
//	   bo.typeId = im.typeId
//	   bo.contextId = im.contextId
//	   bo.attributes = new ArrayList<AgendaAttributeBo>()
//	   bo.firstItemId = im.firstItemId
//	   for (attr in im.attributes){
//		   bo.attributes.add ( AgendaAttributeBo.from(attr) )
//	   }
//   }
// 

} 