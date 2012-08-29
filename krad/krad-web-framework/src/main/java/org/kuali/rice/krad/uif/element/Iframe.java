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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.ricedictionaryvalidator.ErrorReport;
import org.kuali.rice.krad.ricedictionaryvalidator.RDValidator;
import org.kuali.rice.krad.ricedictionaryvalidator.TracerToken;
import org.kuali.rice.krad.ricedictionaryvalidator.XmlBeanParser;

import java.util.ArrayList;

/**
 * Content element that encloses an iframe
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Iframe extends ContentElementBase {
	private static final long serialVersionUID = 5797473302619055088L;

	private String source;
	private String height;
	private String frameborder;

	public Iframe() {
		super();
	}

    /**
     * The IFrame's source
     *
     * @return String source
     */
	public String getSource() {
		return this.source;
	}

    /**
     * Setter for the IFrame's source
     *
     * @param source
     */
	public void setSource(String source) {
		this.source = source;
	}

    /**
     * The IFrame's height
     *
     * @return String height
     */
	public String getHeight() {
		return this.height;
	}

    /**
     * Setter for the IFrame's height
     *
     * @param height
     */
	public void setHeight(String height) {
		this.height = height;
	}

    /**
     * The IFrame's frame border
     *
     * @return String frameborder
     */
	public String getFrameborder() {
		return this.frameborder;
	}

    /**
     * Setter for the IFrame's frame border
     *
     * @param frameborder
     */
	public void setFrameborder(String frameborder) {
		this.frameborder = frameborder;
	}

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public ArrayList<ErrorReport> completeValidation(TracerToken tracer, XmlBeanParser parser){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean(this);

        // Checks that a source is set
        if(getSource()==null){
            if(!RDValidator.checkExpressions(this)){
                ErrorReport error = new ErrorReport(ErrorReport.ERROR);
                error.setValidationFailed("Source must be set");
                error.setBeanLocation(tracer.getBeanLocation());
                error.addCurrentValue("source ="+getSource());
                reports.add(error);
            }
        }

        reports.addAll(super.completeValidation(tracer.getCopy(),parser));

        return reports;
    }
}
