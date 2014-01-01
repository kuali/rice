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
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.inquiry.InquirableImpl;
import org.kuali.rice.krad.uif.widget.Inquiry;

import java.util.Collections;
import java.util.Map;

/**
 * InquirableImpl for {@link TravelCompanyCategory}.  This is a very limited implementation to make the
 * demonstration page work.  Rather than query, it creates an instance of the data object manually.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelCompanyCategoryInquirable extends InquirableImpl implements Inquirable {

    @Override
    public void setDataObjectClass(Class<?> dataObjectClass) {
        if (!TravelCompanyCategory.class.equals(dataObjectClass)) {
            throw new IllegalArgumentException("This Inquirable is only good for class TravelCompaniesCategory");
        }
    }

    @Override
    public Object retrieveDataObject(Map<String, String> fieldValues) {
        TravelCompanyCategory tcc = new TravelCompanyCategory();

        tcc.setName("Preferred Providers");

        QueryResults<TravelCompany> travelCompanies =
                KradDataServiceLocator.getDataObjectService().findMatching(TravelCompany.class,
                        QueryByCriteria.Builder.create().build());
        tcc.setCompanies(travelCompanies.getResults());

        return tcc;
    }

    @Override
    public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry) {
        inquiry.buildInquiryLink(dataObject, propertyName, TravelCompanyCategory.class,
                Collections.<String,String>emptyMap());
    }
}
