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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import org.kuali.rice.krad.ricedictionaryvalidator.ErrorReport;
import org.kuali.rice.krad.ricedictionaryvalidator.TracerToken;
import org.kuali.rice.krad.ricedictionaryvalidator.XmlBeanParser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * This is a constraint that limits attribute values to some subset of valid characters or to match a particular regular expression.
 * 
 * For example: 
 * - To limit to both upper and lower-case letters, value can be set to "[A-Za-z]*"
 * - To limit to any character except carriage returns and line feeds, value can be set to "[^\n\r]*"
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidCharactersConstraint extends BaseConstraint {

    protected String value;

    /**
     * The Java based regex for valid characters
     * This value should include the ^ and $ symbols if needed
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Validates different requirements of component compiling a series of reports detailing information on errors
     * found in the component.  Used by the RiceDictionaryValidator.
     *
     * @param tracer Record of component's location
     * @param parser Set of tools for parsing the xml files which were used to create the component
     * @return A list of ErrorReports detailing errors found within the component and referenced within it
     */
    @Override
    public ArrayList<ErrorReport> completeValidation(TracerToken tracer, XmlBeanParser parser){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean("ValidCharacterConstraint",getLabelKey());

        if(getValue()==null){
            ErrorReport error = new ErrorReport(ErrorReport.WARNING);
            error.setValidationFailed("GetValue should return something");
            error.setBeanLocation(tracer.getBeanLocation());
            error.addCurrentValue("getValue ="+getValue());
            reports.add(error);
        }

        reports.addAll(super.completeValidation(tracer.getCopy(),parser));

        return reports;
    }
}