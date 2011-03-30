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
 * Note that if jsValue has any value it will always <b>override</b> the following...<b>
 * If the labelKey matches one of the following values:<br>
 * 		email<br>
		url<br>
		date<br>
		number<br>
		digits<br>
		creditcard<br>
		letterswithbasicpunc<br>
		alphanumeric<br>
		lettersonly<br>
		nowhitespace<br>
		integer<br>
		phoneUS<br>
		time<br>
 * or <b>any</b> other custom method added through the addMethod call on the validator in jQuery 
 * AND if a javascript value is not specified on this constraint, it will use the built in jQuery defined
 * validation method which matches that name.  Note using a validator method here which is not a regex validation does not
 * make sense within context of this constraint as a server side regex value should be defined.
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
     * The Java based regex for valid characters
     * This value should include the ^ and $ symbols after "regex:" if needed
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
	 * key maps to one of the default valid character methods contained in jQuery.
	 * This must be set if there is NO default method that matches the label key and applyClientSide is true.
	 * 
	 * This is completely ignored if applyClientSide is set to false.<br>
	 * This value should include /^ and $/ symbols in the regex, there is no prefix used, unlike the java value.
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