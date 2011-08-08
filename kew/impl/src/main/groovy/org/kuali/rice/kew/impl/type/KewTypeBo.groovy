package org.kuali.rice.kew.impl.type

// TODO: implement contract interface
public class KewTypeBo extends org.kuali.rice.krad.bo.PersistableBusinessObjectBase implements org.kuali.rice.krad.bo.MutableInactivatable {

	def String id
	def String name
	def String namespace
	def String serviceName
	def boolean active
	def List<KewTypeAttributeBo> attributes

//	/**
//	* Converts a mutable bo to it's immutable counterpart
//	* @param bo the mutable business object
//	* @return the immutable object
//	*/
//	static KewTypeDefinition to(KewTypeBo bo) {
//      throw new java.lang.UnsupportedOperationException();
//	}
//
//	/**
//	 * Converts a immutable object to it's mutable bo counterpart
//	 * @param im immutable object
//	 * @return the mutable bo
//	 */
//	static KewTypeBo from(KewTypeDefinition im) {
//      throw new java.lang.UnsupportedOperationException();
//	}

} 