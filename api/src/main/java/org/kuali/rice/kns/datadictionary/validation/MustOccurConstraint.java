package org.kuali.rice.kns.datadictionary.validation;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MustOccurConstraint implements Constraint {
    @XmlElement
    private List<DependencyConstraint> requiredFields;
	@XmlElement
    private List<MustOccurConstraint> occurs;
	@XmlElement
	private Integer min;
	@XmlElement
	private Integer max;

	public List<DependencyConstraint> getRequiredFields() {
		return requiredFields;
	}

	public void setRequiredFields(List<DependencyConstraint> requiredFields) {
		this.requiredFields = requiredFields;
	}

	public List<MustOccurConstraint> getOccurs() {
		return occurs;
	}

	public void setOccurs(List<MustOccurConstraint> occurs) {
		this.occurs = occurs;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
}
