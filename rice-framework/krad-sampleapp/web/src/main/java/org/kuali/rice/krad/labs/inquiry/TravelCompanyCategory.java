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
package org.kuali.rice.krad.labs.inquiry;

import edu.sampleu.travel.dataobject.TravelCompany;

import java.util.List;

/**
 * Represents a named group of {@link TravelCompany}s.  This is not a mapped entity, it does not have a database table
 * associated with it.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelCompanyCategory {

    private String name;
    private List<TravelCompany> companies;

    /**
     * The name of this travel company category
     *
     * @return the travel company category name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the travel company category name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The {@link TravelCompany}s in this category.
     *
     * @return the {@link TravelCompany}s in this category.
     */
    public List<TravelCompany> getCompanies() {
        return companies;
    }

    /**
     * Set the {@link TravelCompany}s in this category.
     *
     * @param companies the companies to set
     */
    public void setCompanies(List<TravelCompany> companies) {
        this.companies = companies;
    }
}
