/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.control;

import org.kuali.rice.krad.ricedictionaryvalidator.ErrorReport;
import org.kuali.rice.krad.ricedictionaryvalidator.TracerToken;
import org.kuali.rice.krad.ricedictionaryvalidator.XmlBeanParser;
import org.kuali.rice.krad.uif.element.ContentElementBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all <code>Control</code> implementations
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @see org.kuali.rice.krad.uif.control.Control
 */
public abstract class ControlBase extends ContentElementBase implements Control {
	private static final long serialVersionUID = -7898244978136312663L;
	
	private int tabIndex;

    private boolean disabled;
    private String disabledReason;

    public ControlBase() {
        super();

        disabled = false;
    }

	/**
	 * @see org.kuali.rice.krad.uif.component.Component#getComponentTypeName()
	 */
	@Override
	public final String getComponentTypeName() {
		return "control";
	}

    /**
     * @see Control#getTabIndex()
     */
	public int getTabIndex() {
		return this.tabIndex;
	}

    /**
     * @see Control#setTabIndex(int)
     */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

    /**
     * @see Control#isDisabled()
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @see Control#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @see Control#getDisabledReason()
     */
    public String getDisabledReason() {
        return disabledReason;
    }

    /**
     * @see Control#setDisabledReason(java.lang.String)
     */
    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    /**
     * Returns js that will add data to this component by the element which matches its id.
     *
     * <p> This will return script for all the data elements since this component is implemented as a spring form:input tag
     * that does not allow for the insertion of simple attributes. Therefore, the complex attributes script should include
     * all the attributes since is it is inserted each time krad:template is used to display a control</p>
     *
     * @return jQuery data script for all data attributes
     */
    @Override
    public String getComplexDataAttributesJs() {
        /*TODO find out if all controls will need to override this. If not, uncomment and add the ones that need to the array
        // classes which will exhibit the overriding behaviour
        Class[] allowedcontrols = {TextAreaControl.class, TextControl.class, FileControl.class};
        for (Class klass: allowedcontrols) {
            if (klass.isAssignableFrom(this.getClass())) {
                return super.getAllDataAttributesJs();
            }
        }
        return super.getComplexDataAttributesJs();*/
        return super.getAllDataAttributesJs();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public ArrayList<ErrorReport> completeValidation(TracerToken tracer, XmlBeanParser parser){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean(this);

        reports.addAll(super.completeValidation(tracer.getCopy(),parser));

        return reports;
    }
}
