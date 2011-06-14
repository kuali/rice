package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A when constraint is a child of a case constraint. It provides a specific additional constraint that should be processed when 
 * the condition itself is true. 
 * 
 * So a case constraint on country, might have a when constraint with value='USA', and another with value='Canada'. Each of these
 * when constraints would define a constraint of their own that would only be processed when the country was USA, or when the country 
 * was Canada. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WhenConstraint implements Constraint {
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
