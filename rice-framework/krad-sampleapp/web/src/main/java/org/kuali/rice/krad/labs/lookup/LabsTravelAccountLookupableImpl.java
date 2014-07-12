/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.labs.lookup;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.lookup.LookupForm;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.Collection;
import java.util.Map;

/**
 * Silly example for demonstrating Spring instantiation of certain services used in KRAD.
 *
 * <pre>
 * {@code <property name="viewHelperService" value="#{#getService('labsTravelAccountLookupable')}"/>}
 * </pre>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsTravelAccountLookupableImpl extends LookupableImpl {

    private static final long serialVersionUID = -8463531661189633011L;

    private transient IdentityService identityService;

    /**
     * {@inheritDoc}
     *
     * <p>
     * This lookupable modifies the lookup results so that any search for 'fred' actually searches for 'admin'.
     * </p>
     */
    @Override
    public Collection<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean bounded) {
        if (StringUtils.equals(MapUtils.getString(searchCriteria, "fiscalOfficer.principalName"), "fred")) {
            String principalName = getIdentityService().getPrincipal("admin").getPrincipalName();
            searchCriteria.put("fiscalOfficer.principalName", principalName);
        }

        return super.performSearch(form, searchCriteria, bounded);
    }

    /**
     * Returns the {@link IdentityService}.
     *
     * @return the {@link IdentityService}
     */
    public IdentityService getIdentityService() {
        return identityService;
    }

    /**
     * Sets the {@link IdentityService}.
     *
     * @param identityService  the {@link IdentityService} to set
     */
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

}