package org.kuali.rice.kns.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * This is a constraint that limits attribute values to some subset of valid characters or to match a particular regular expression.
 * 
 * For example: 
 * - To limit to both upper and lower-case letters, value can be set to "regex:[A-Za-z]*"
 * - To limit to any character except carriage returns and line feeds, value can be set to "regex:[^\n\r]*"
 * 
 * Alternative processors can be specified, though the current implementation (1.1) does not handle any processor but regex. 
 * 
 * @author Kuali Student Team
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidCharactersConstraint extends BaseConstraint {
	    
    @XmlElement
    protected String value;
    
    @XmlElement
    protected String jsValue;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

	/**
	 * Javascript version of the regex defined in value.  This does not have to be set if this constraint's
	 * key maps to one of the default valid character methods contained in jQuery - (insert that list here).
	 * This must be set if there is no default method and applyClientSide is true for validation to occur.
	 * Otherwise, the field will accept any value client side.
	 * 
	 * This is completely ignored if applyClientSide is set to false.
	 * 
	 * @return the jsValue
	 */
	public String getJsValue() {
		return this.jsValue;
	}

	/**
	 * @param jsValue the jsValue to set
	 */
	public void setJsValue(String jsValue) {
		this.jsValue = jsValue;
	}
    
    
    
}