/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.kuali.rice.krad.demo.travel.dataobject.TravelAccount;

import java.util.List;

/**
 * Provides additional methods for retrieving {@link TravelAccount}s.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ComponentViewHelperService {

    /**
     * Returns a list of {@link TravelAccount}s by {@code term}.
     *
     * @param term the {@link TravelAccount} number
     *
     * @return a list of {@link TravelAccount}s by {@code term}
     */
    List<TravelAccount> retrieveTravelAccounts(String term);

    /**
     * Returns a list of {@link TravelAccount}s by {@code subAccount} and {@code term}.
     *
     * @param subAccount the {@link TravelAccount} identifier
     * @param term the {@link TravelAccount} number
     *
     * @return a list of {@link TravelAccount}s by {@code subAccount} and {@code term}
     */
    List<TravelAccount> retrieveTravelAccountsBySubAcctAndTerm(String subAccount, String term);

    /**
     * Returns a list of {@link TravelAccount}s by {@code name}.
     *
     * @param name the {@link TravelAccount} name
     *
     * @return a list of {@link TravelAccount}s by {@code name}
     */
    List<TravelAccount> retrieveTravelAccountsByName(String name);

}