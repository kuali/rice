/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.core.datadictionary.control;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KualiUserControlDefinition extends ControlDefinitionBase {

    // logger
    private static Log LOG = LogFactory.getLog(KualiUserControlDefinition.class);

    private String universalIdAttributeName;
    private String userIdAttributeName;
    private String personNameAttributeName;

    public KualiUserControlDefinition() {
        LOG.debug("creating new KualiUserControlDefinition");
    }

    /**
     * 
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return true;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "KualiUserControlDefinition";
    }

    /**
     * Gets the personNameAttributeName attribute.
     * 
     * @return Returns the personNameAttributeName.
     */
    public String getPersonNameAttributeName() {
        return personNameAttributeName;
    }

    /**
     * Sets the personNameAttributeName attribute value.
     * 
     * @param personNameAttributeName The personNameAttributeName to set.
     */
    public void setPersonNameAttributeName(String personNameAttributeName) {
        LOG.debug("calling setPersonNameAttributeName '" + personNameAttributeName + "'");
        if (StringUtils.isBlank(personNameAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) personNameAttributeName");
        }
        this.personNameAttributeName = personNameAttributeName;
    }

    /**
     * Gets the universalIdAttributeName attribute.
     * 
     * @return Returns the universalIdAttributeName.
     */
    public String getUniversalIdAttributeName() {
        return universalIdAttributeName;
    }

    /**
     * Sets the universalIdAttributeName attribute value.
     * 
     * @param universalIdAttributeName The universalIdAttributeName to set.
     */
    public void setUniversalIdAttributeName(String universalIdAttributeName) {
        LOG.debug("calling setUniversalIdAttributeName '" + universalIdAttributeName + "'");
        if (StringUtils.isBlank(universalIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) universalIdAttributeName");
        }
        this.universalIdAttributeName = universalIdAttributeName;
    }

    /**
     * Gets the userIdAttributeName attribute.
     * 
     * @return Returns the userIdAttributeName.
     */
    public String getUserIdAttributeName() {
        return userIdAttributeName;
    }

    /**
     * Sets the userIdAttributeName attribute value.
     * 
     * @param userIdAttributeName The userIdAttributeName to set.
     */
    public void setUserIdAttributeName(String userIdAttributeName) {
        LOG.debug("calling setUserIdAttributeName '" + userIdAttributeName + "'");
        if (StringUtils.isBlank(userIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) userIdAttributeName");
        }
        this.userIdAttributeName = userIdAttributeName;
    }

}
