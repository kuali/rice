/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.components;

import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.demo.travel.dataobject.TravelAccount;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link ComponentViewHelperService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentViewHelperServiceImpl extends ViewHelperServiceImpl implements ComponentViewHelperService {

    private static final long serialVersionUID = -3952713360851782891L;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TravelAccount> retrieveTravelAccounts(String term) {
        List<TravelAccount> matchingAccounts = new ArrayList<TravelAccount>();

        Map<String, String> lookupCriteria = new HashMap<String, String>();
        lookupCriteria.put("number", term + SearchOperator.LIKE_MANY.op());

        matchingAccounts = (List<TravelAccount>) KRADServiceLocatorWeb.getLookupService().findCollectionBySearch(
                TravelAccount.class, lookupCriteria);

        return matchingAccounts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TravelAccount> retrieveTravelAccountsBySubAcctAndTerm(String subAccount, String term) {
        List<TravelAccount> matchingAccounts = new ArrayList<TravelAccount>();

        Map<String, String> lookupCriteria = new HashMap<String, String>();
        lookupCriteria.put("subAccounts.subAccount", subAccount);
        lookupCriteria.put("number", term + SearchOperator.LIKE_MANY.op());

        matchingAccounts = (List<TravelAccount>) KRADServiceLocatorWeb.getLookupService().findCollectionBySearch(
                TravelAccount.class, lookupCriteria);

        return matchingAccounts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TravelAccount> retrieveTravelAccountsByName(String name) {
        List<TravelAccount> matchingAccounts = new ArrayList<TravelAccount>();

        Map<String, String> lookupCriteria = new HashMap<String, String>();
        lookupCriteria.put("name", name + SearchOperator.LIKE_MANY.op());

        matchingAccounts = (List<TravelAccount>) KRADServiceLocatorWeb.getLookupService().findCollectionBySearch(
                TravelAccount.class, lookupCriteria);

        return matchingAccounts;
    }

}