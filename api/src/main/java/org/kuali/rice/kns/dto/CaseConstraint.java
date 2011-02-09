package org.kuali.rice.kns.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CaseConstraint implements Constraint {
	@XmlElement
    protected List<WhenConstraint> whenConstraint;
	@XmlElement
	protected String fieldPath;
	@XmlElement
	protected String operator;
	@XmlElement
	protected boolean caseSensitive;

	public List<WhenConstraint> getWhenConstraint() {
		return whenConstraint;
	}

	public void setWhenConstraint(List<WhenConstraint> whenConstraint) {
		this.whenConstraint = whenConstraint;
	}

	public String getFieldPath() {
		return fieldPath;
	}

	public void setFieldPath(String fieldPath) {
		this.fieldPath = fieldPath;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}