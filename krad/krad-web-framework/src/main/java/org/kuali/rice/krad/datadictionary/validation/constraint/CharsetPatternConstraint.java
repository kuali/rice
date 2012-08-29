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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.ricedictionaryvalidator.ErrorReport;
import org.kuali.rice.krad.ricedictionaryvalidator.TracerToken;
import org.kuali.rice.krad.ricedictionaryvalidator.XmlBeanParser;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pattern for matching any character in the given list (String)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CharsetPatternConstraint extends ValidCharactersPatternConstraint {
    protected String validChars;

    /**
     * @return String containing all valid chars for this charset
     */
    public String getValidChars() {
        return validChars;
    }

    /**
     * @param validChars for this charset
     */
    public void setValidChars(String validChars) {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalArgumentException("invalid (empty) validChars");
        }

        this.validChars = validChars;
    }


    /**
     * Escapes every special character I could think of, to limit potential misuse of this pattern.
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalStateException("validChars is empty");
        }

        // filter out and escape chars which would confuse the pattern-matcher
        Pattern filteringChars = Pattern.compile("([\\-\\[\\]\\{\\}\\$\\.\\^\\(\\)\\*\\&\\|])");
        String filteredChars = filteringChars.matcher(validChars).replaceAll("\\\\$1");

        StringBuffer regexString = new StringBuffer("[");
        regexString.append(filteredChars);
        if (filteredChars.endsWith("\\")) {
            regexString.append("\\");
        }
        regexString.append("]");

        return regexString.toString();
    }

	/**
	 * 
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
	 */
	@Override
	public String getLabelKey() {
		String labelKey = super.getLabelKey();
		if (StringUtils.isNotEmpty(labelKey)) {
			return labelKey;
		}
		return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "charsetPattern");
	}

    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        if(validationMessageParams == null){
            validationMessageParams = new ArrayList<String>();
            if (StringUtils.isNotBlank(validChars)) {
                validationMessageParams.add(validChars);
            }
            
        }
        return this.validationMessageParams;
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
        tracer.addBean("CharsetPatternConstraint",getLabelKey());

        if(getValidChars()==null){
            ErrorReport error = new ErrorReport(ErrorReport.ERROR);
            error.setValidationFailed("ValidChars must be set");
            error.setBeanLocation(tracer.getBeanLocation());
            error.addCurrentValue("validChars ="+getValidChars());
            reports.add(error);
        }

        reports.addAll(super.completeValidation(tracer.getCopy(),parser));

        return reports;
    }
}
