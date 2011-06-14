package org.kuali.rice.krad.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Prerequisite constraints require that some other attribute be non-empty in order for the constraint to be valid. 
 * So, a 7-digit US phone number might have a prerequisite of an area code, or an address street2 might have a prerequisite
 * that street1 is non-empty. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PrerequisiteConstraint extends BaseConstraint {
	@XmlElement
    protected String attributePath;

	public String getAttributePath() {
		return attributePath;
	}

	public void setAttributePath(String attributePath) {
		this.attributePath = attributePath;
	}
}
