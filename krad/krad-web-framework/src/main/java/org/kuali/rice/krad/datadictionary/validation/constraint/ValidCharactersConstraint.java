package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.ArrayList;
import java.util.List;

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
     * This value should include the ^ and $ symbols if needed
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