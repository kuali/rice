package org.kuali.rice.kns.datadictionary.validation;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MustOccurConstraint extends BaseConstraint {
	
    @XmlElement
    private List<PrerequisiteConstraint> prerequisiteConstraints;
	@XmlElement
    private List<MustOccurConstraint> mustOccurConstraints;
	@XmlElement
	private Integer min;
	@XmlElement
	private Integer max;

	public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
		return prerequisiteConstraints;
	}

	public void setPrerequisiteConstraints(List<PrerequisiteConstraint> prerequisiteConstraints) {
		this.prerequisiteConstraints = prerequisiteConstraints;
	}

	public List<MustOccurConstraint> getMustOccurConstraints() {
		return mustOccurConstraints;
	}

	public void setMustOccurConstraints(List<MustOccurConstraint> occurs) {
		this.mustOccurConstraints = occurs;
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
