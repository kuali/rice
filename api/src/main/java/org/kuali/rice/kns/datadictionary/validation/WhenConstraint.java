package org.kuali.rice.kns.datadictionary.validation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;

@XmlAccessorType(XmlAccessType.FIELD)
public class WhenConstraint implements Constraint {
	protected List<Object> values;
	protected String valuePath;
	protected Validatable constraint;

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
        this.values = values;
    }

    public void setValue(Object value) {	    
	    values = new ArrayList<Object>();
	    values.add(value);
	}

	public String getValuePath() {
		return valuePath;
	}

	public void setValuePath(String valuePath) {
		this.valuePath = valuePath;
	}

	public Validatable getConstraint() {
		return constraint;
	}

	public void setConstraint(Validatable constraint) {
		this.constraint = constraint;
	}
}
