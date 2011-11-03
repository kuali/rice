/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

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
