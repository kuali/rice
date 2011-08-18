/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kim.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.util.Constants;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.ParameterService;

import static java.util.Arrays.asList;
import static org.kuali.rice.core.util.BufferedLogger.*;

/**
 * 
 */
public class PrincipalMapper extends AbstractContextMapper {
    private Constants constants;
    private ParameterService parameterService;
    
    public Principal mapFromContext(DirContextOperations context) {
        return new Principal((Principal.Builder) doMapFromContext(context));
    }

    public Object doMapFromContext(DirContextOperations context) {
        final Principal.Builder builder = Principal.Builder.create();
        final String uaid = context.getStringAttribute(getConstants().getKimLdapIdProperty());
        
        if (uaid == null) {
            throw new InvalidLdapEntityException("LDAP Search Results yielded an invalid result with attributes " 
                                                 + context.getAttributes());
        }
        
        person.setPrincipalId(uaid);
        person.setEntityId(uaid);
        person.setPrincipalName(context.getStringAttribute(getConstants().getKimLdapNameProperty()));
        person.setActive(isPersonActive(context));

        return person;
    }
    
     /**
     * 
     * Checks the configured active principal affiliations, if one is found, returns true
     * @param context
     * @return true if a matching active affiliation is found
     */
    protected boolean isPersonActive(DirContextOperations context) {
        String[] affils = context.getStringAttributes(getConstants().getAffiliationLdapProperty());
        Object edsVal = getLdapValue("principals.active.Y");
        if (affils != null && affils.length > 0
                && edsVal != null) {
            if (edsVal instanceof List) {
                List<String> edsValLst = (List<String>)edsVal;
                for (String affil : affils) {
                    if (edsValLst.contains(affil)) {
                        return true;
                    }
                }
            } else {
                String edsValStr = (String)edsVal;
                for (String affil : affils) {
                    if (StringUtils.equals(affil, edsValStr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Object getLdapValue(String kimAttribute) {
        Matcher matcher = getKimAttributeMatcher(kimAttribute);
        debug("Does ", kimAttribute, " match? ", matcher.matches());
        if (!matcher.matches()) {
            return null;
        }
        String value = matcher.group(2);

        // If it's actually a list. It can only be a list if there are commas
        if (value.contains(",")) {
            return asList(value.split(","));
        }

        return value;
    }

    protected Matcher getKimAttributeMatcher(String kimAttribute) {
        Parameter mappedParam = getParameterService().retrieveParameter(getConstants().getParameterNamespaceCode(),
                                                                        getConstants().getParameterDetailTypeCode(),
                                                                        getConstants().getMappedParameterName());

        String regexStr = String.format("(%s|.*;%s)=([^=;]*).*", kimAttribute, kimAttribute);
        debug("Matching KIM attribute with regex ", regexStr);
        Matcher retval = Pattern.compile(regexStr).matcher(mappedParam.getParameterValue());
        
        if (!retval.matches()) {
            mappedParam = getParameterService().retrieveParameter(getConstants().getParameterNamespaceCode(),
                                                                  getConstants().getParameterDetailTypeCode(),
                                                                  getConstants().getMappedValuesName());
            retval = Pattern.compile(regexStr).matcher(mappedParam.getParameterValue());
        }

        return retval;
    }

   /**
     * Gets the value of constants
     *
     * @return the value of constants
     */
    public final Constants getConstants() {
        return this.constants;
    }

    /**
     * Sets the value of constants
     *
     * @param argConstants Value to assign to this.constants
     */
    public final void setConstants(final Constants argConstants) {
        this.constants = argConstants;
    }

    public ParameterService getParameterService() {
        return this.parameterService;
    }

    public void setParameterService(ParameterService service) {
        this.parameterService = service;
    }
}