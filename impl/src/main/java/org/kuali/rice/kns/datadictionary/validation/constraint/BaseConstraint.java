package org.kuali.rice.kns.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


/**
 * A class that implements the required accessor for label keys. This provides a convenient base class
 * from which other constraints can be derived.
 * 
 * This class is a direct copy of one that was in Kuali Student. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseConstraint implements Constraint {
    @XmlElement
    protected String labelKey; // Label key will map to a message... for a field
								// there can be multiple contexts for the
								// label... a help context, a description
								// context, and a field label context for
								// example
    @XmlElement
    protected Boolean applyClientSide;
    
    public BaseConstraint(){
    	applyClientSide = Boolean.valueOf(true);
    }
    
	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	/**
	 * If this is true, the constraint should be applied on the client side when the user interacts with
	 * a field - if this constraint can be interpreted for client side use. Default is true.
	 * @return the applyClientSide
	 */
	public Boolean getApplyClientSide() {
		return this.applyClientSide;
	}

	/**
	 * @param applyClientSide the applyClientSide to set
	 */
	public void setApplyClientSide(Boolean applyClientSide) {
		this.applyClientSide = applyClientSide;
	}
	

}
