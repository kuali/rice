/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import java.util.HashMap;
import java.util.List;

import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentAdHocService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;

/**
 * Implementation for {@link DocumentAdHocService}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentAdHocServiceImpl implements DocumentAdHocService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAdHocs(Document document) {
		if ( document == null ) {
			return;
		}
        /* Instead of reading the doc header to see if doc is in saved status
         * its probably easier and faster to just do this all the time and
         * store a null when appropriate.
         */
        List<AdHocRoutePerson> adHocRoutePersons;
        List<AdHocRouteWorkgroup> adHocRouteWorkgroups;
        HashMap<String, Object> criteriaPerson = new HashMap<String, Object>(2);
        HashMap<String, Object> criteriaWorkgroup = new HashMap<String, Object>(2);

        criteriaPerson.put("documentNumber", document.getDocumentNumber());
        criteriaPerson.put("type", AdHocRoutePerson.PERSON_TYPE);
        adHocRoutePersons = (List<AdHocRoutePerson>) getLegacyDataAdapter().findMatching(AdHocRoutePerson.class, criteriaPerson);
        criteriaWorkgroup.put("documentNumber", document.getDocumentNumber());
        criteriaWorkgroup.put("type", AdHocRouteWorkgroup.WORKGROUP_TYPE);
        adHocRouteWorkgroups = (List<AdHocRouteWorkgroup>) getLegacyDataAdapter().findMatching(AdHocRouteWorkgroup.class, criteriaWorkgroup);

        //populate group namespace and names on adHocRouteWorkgroups
        for (AdHocRouteWorkgroup adHocRouteWorkgroup : adHocRouteWorkgroups) {
            Group group = KimApiServiceLocator.getGroupService().getGroup(adHocRouteWorkgroup.getId());
            adHocRouteWorkgroup.setRecipientName(group.getName());
            adHocRouteWorkgroup.setRecipientNamespaceCode(group.getNamespaceCode());
        }
        document.setAdHocRoutePersons(adHocRoutePersons);
        document.setAdHocRouteWorkgroups(adHocRouteWorkgroups);
	}

    /**
     * gets the {@link LegacyDataAdapter} from {@code KRADServiceLocator}
     * @return the {@link LegacyDataAdapter} from {@code KRADServiceLocator}
     */
    protected LegacyDataAdapter getLegacyDataAdapter() {
    	return KRADServiceLocatorWeb.getLegacyDataAdapter();
    }

}
