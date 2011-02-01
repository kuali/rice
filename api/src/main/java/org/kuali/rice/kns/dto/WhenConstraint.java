package org.kuali.rice.kns.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class WhenConstraint {
	protected List<Object> values;
	protected String valuePath;
	protected Constraint constraint;

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

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
}
