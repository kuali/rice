package org.kuali.rice.kns.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * This is a constraint that limits attribute values to some subset of valid characters or to match a particular regular expression.
 * 
 * For example: 
 * - To limit to both upper and lower-case letters, value can be set to "[A-Za-z]*"
 * - To limit to any character except carriage returns and line feeds, value can be set to "[^\n\r]*"
 * 
 * Alternative processors can be specified, though the current implementation (1.1) does not handle any processor but regex. 
 * TODO delyea: remove jsValue from docs here
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

}