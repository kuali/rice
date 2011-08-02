/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;

/**
 * TODO Administrator don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DatePatternConstraint extends ValidDataPatternConstraint {

    private List<String> allowedFormats;

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidDataPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        List<String> dateFormatParams =
                parseConfigValues(ConfigContext.getCurrentContextConfig().getProperty(
                        CoreConstants.STRING_TO_DATE_FORMATS));
        if (allowedFormats != null && !allowedFormats.isEmpty()) {
            if (dateFormatParams.containsAll(allowedFormats)) {
                dateFormatParams = allowedFormats;
            } else {
                //throw new Exception("Some of these formats do not exist in configured allowed date formats: " + allowedFormats.toString());
            }
        }
        
        if(dateFormatParams.isEmpty()){
            //exception
        }
        String regex = "";
        int i = 0;
        for (String format : dateFormatParams) {
            if(i == 0){
                regex = "(^" + convertDateFormatToRegex(format.trim()) + "$)";
            }
            else{
                regex = regex + "|(^" + convertDateFormatToRegex(format.trim()) + "$)";
            }
            i++;
        }
        return regex;
    }

    private String convertDateFormatToRegex(String format) {
        format = format.replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("-", "\\-")
                .replace("+", "\\+")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("|", "\\|")
                .replace("yyyy", "((19|2[0-9])[0-9]{2})")
                .replace("yy", "([0-9]{2})")
                .replaceAll("M{4,}", "([@]+)") //"(January|February|March|April|May|June|July|August|September|October|November|December)")
                .replace("MMM", "([@]{3})") //"(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)")
                .replace("MM", "(0[1-9]|1[012])")
                .replace("M", "(0?[1-9]|1[012])")
                .replace("dd", "(0[1-9]|[12][0-9]|3[01])")
                .replace("d", "(0?[1-9]|[12][0-9]|3[01])")
                .replace("hh", "(1[0-2]|0[1-9])")
                .replace("h", "(1[0-2]|0?[1-9])")
                .replace("HH", "(2[0-3]|1[0-9]|0[0-9])")
                .replace("H", "(2[0-3]|1[0-9]|0?[0-9])")
                .replace("kk", "(2[0-4]|1[0-9]|0[1-9])")
                .replace("k", "(2[0-4]|1[0-9]|0?[1-9])")
                .replace("KK", "(1[01]|0[0-9])")
                .replace("K", "(1[01]|0?[0-9])")
                .replace("mm", "([0-5][0-9])")
                .replace("m", "([1-5][0-9]|0?[0-9])")
                .replace("ss", "([0-5][0-9])")
                .replace("s", "([1-5][0-9]|0?[0-9])")
                .replace("SSS", "([0-9][0-9][0-9])")
                .replace("SS", "([0-9][0-9][0-9]?)")
                .replace("S", "([0-9][0-9]?[0-9]?)")
                .replaceAll("E{4,}", "([@]+)")//"(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)")
                .replaceAll("E{1,3}", "([@]{3})")//"(Mon|Tue|Wed|Thu|Fri|Sat|Sun)")
                .replace("DDD", "(3[0-6][0-5]|[1-2][0-9][0-9]|0[0-9][1-9])")
                .replace("DD", "(3[0-6][0-5]|[1-2][0-9][0-9]|0?[0-9][1-9])")
                .replace("D", "(3[0-6][0-5]|[1-2][0-9][0-9]|0?[0-9]?[1-9])")
                .replace("F", "([1-5])")
                .replace("ww", "(5[0-3]|[1-4][0-9]|0[1-9])")
                .replace("w", "(5[0-3]|[1-4][0-9]|[1-9])")
                .replace("W", "([1-5])")
                .replaceAll("z{4,}", "([@]+)")
                .replaceAll("z{1,3}", "([@]{1,4})")
                .replaceAll("a{1,}", "([aApP][mM])")
                .replaceAll("G{1,}", "([aA][dD]|[bB][cC])")
                .replace(" ", "\\s")
                .replace("@", "a-zA-Z");
        
        return format;

    }

    /**
     * 
     * The dateTime config vars are ';' seperated.
     * 
     * @param configValue
     * @return
     */
    private List<String> parseConfigValues(String configValue) {
        if (configValue == null || "".equals(configValue)) {
            return Collections.emptyList();
        }
        return Arrays.asList(configValue.split(";"));
    }

    /**
     * @return the allowedFormats
     */
    public List<String> getAllowedFormats() {
        return this.allowedFormats;
    }

    /**
     * @param allowedFormats the allowedFormats to set
     */
    public void setAllowedFormats(List<String> allowedFormats) {
        this.allowedFormats = allowedFormats;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidDataPatternConstraint#getValidationMessageParams()
     */
    @Override
    public List<String> getValidationMessageParams() {
        if(validationMessageParams == null){
            validationMessageParams = new ArrayList<String>();
            if (allowedFormats != null && !allowedFormats.isEmpty()) {
                validationMessageParams.add(StringUtils.join(allowedFormats, ", "));
            } else {
                List<String> dateFormatParams =
                    parseConfigValues(ConfigContext.getCurrentContextConfig().getProperty(
                            CoreConstants.STRING_TO_DATE_FORMATS));
                validationMessageParams.add(StringUtils.join(dateFormatParams, ", "));
            }
        }
        return validationMessageParams;
    }

}
