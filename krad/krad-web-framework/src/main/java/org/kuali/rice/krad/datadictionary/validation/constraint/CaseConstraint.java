package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A case constraint is a constraint that is imposed only when a certain condition is met, for example, if the country attribute value is "USA",
 * then a prerequisite constraint may be imposed that the 'State' attribute is non-null. 
 * 
 * This class is a direct copy of one that was in Kuali Student. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CaseConstraint extends BaseConstraint {
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