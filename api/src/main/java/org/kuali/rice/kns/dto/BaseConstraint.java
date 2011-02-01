package org.kuali.rice.kns.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class BaseConstraint {
    @XmlElement
    protected String labelKey; // Label key will map to a message... for a field
								// there can be multiple contexts for the
								// label... a help context, a description
								// context, and a field label context for
								// example

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
}
