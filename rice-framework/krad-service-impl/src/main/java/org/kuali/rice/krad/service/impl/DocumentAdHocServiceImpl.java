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
package org.kuali.rice.krad.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentAdHocService;

/**
 * Implementation for {@link DocumentAdHocService}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentAdHocServiceImpl implements DocumentAdHocService {

	protected DataObjectService dataObjectService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceAdHocsForDocument( String documentNumber, List<AdHocRouteRecipient> adHocRoutingRecipients ) {
		if ( StringUtils.isBlank(documentNumber) ) {
			return;
		}
		dataObjectService.deleteMatching( AdHocRoutePerson.class,
				QueryByCriteria.Builder.forAttribute("documentNumber", documentNumber).build() );
		dataObjectService.deleteMatching( AdHocRouteWorkgroup.class,
				QueryByCriteria.Builder.forAttribute("documentNumber", documentNumber).build() );

		if ( adHocRoutingRecipients != null ) {
			for ( AdHocRouteRecipient recipient : adHocRoutingRecipients ) {
				recipient.setdocumentNumber(documentNumber);
				dataObjectService.save(recipient);
			}
		}
	}

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
        QueryResults<AdHocRoutePerson> adHocRoutePersons = dataObjectService.findMatching(AdHocRoutePerson.class,
			    QueryByCriteria.Builder.fromPredicates(
			    		PredicateFactory.equal("documentNumber", document.getDocumentNumber()),
			    		PredicateFactory.equal("type", AdHocRoutePerson.PERSON_TYPE) ) );

        QueryResults<AdHocRouteWorkgroup> adHocRouteWorkgroups = dataObjectService.findMatching(AdHocRouteWorkgroup.class,
			    QueryByCriteria.Builder.fromPredicates(
			    		PredicateFactory.equal("documentNumber", document.getDocumentNumber()),
			    		PredicateFactory.equal("type", AdHocRoutePerson.WORKGROUP_TYPE) ) );

        //populate group namespace and names on adHocRouteWorkgroups
        for (AdHocRouteWorkgroup adHocRouteWorkgroup : adHocRouteWorkgroups.getResults()) {
            Group group = KimApiServiceLocator.getGroupService().getGroup(adHocRouteWorkgroup.getId());
            adHocRouteWorkgroup.setRecipientName(group.getName());
            adHocRouteWorkgroup.setRecipientNamespaceCode(group.getNamespaceCode());
        }

        // We *must* copy these into new arrays.  The returned lists are unmodifiable.
        document.setAdHocRoutePersons( new ArrayList<AdHocRoutePerson>( adHocRoutePersons.getResults() ) );
        document.setAdHocRouteWorkgroups( new ArrayList<AdHocRouteWorkgroup>( adHocRouteWorkgroups.getResults() ) );
	}

	public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}

}
