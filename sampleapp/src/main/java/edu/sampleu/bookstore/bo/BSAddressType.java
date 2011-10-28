package edu.sampleu.bookstore.bo;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * AddressType Business Object class file relative to AddressType maintenance object.
 */

public class BSAddressType extends PersistableBusinessObjectBase implements MutableInactivatable {
	
	private String type;
	private String description;
	private boolean active = true;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2789636716684794794L;
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
 

}
