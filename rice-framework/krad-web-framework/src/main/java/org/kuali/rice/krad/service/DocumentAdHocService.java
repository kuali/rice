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
package org.kuali.rice.krad.service;

import java.util.List;

import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.document.Document;

/**
 * This service populates {@link Document}s with {@link AdHocRoutePerson}s and {@link AdHocRouteWorkgroup}s
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentAdHocService {

	/**
	 * This method populates the given {@link Document} with the {@link AdHocRoutePerson}s and {@link AdHocRouteWorkgroup}s
	 * that are associated with it.
	 *
	 * @param doc
	 */
	void addAdHocs(Document doc);

	/**
	 * This method replaces the current ad-hoc recipients for a given document with the provided list.
	 *
	 * @param documentNumber Document Number to which to attach the route log recipients.  All existing stored ad-hoc recipients will be deleted.
	 * @param adHocRoutingRecipients New list of ad-hoc recipients (Persons and Workgroups) which will be attached to the document.
	 */
	void replaceAdHocsForDocument( String documentNumber, List<AdHocRouteRecipient> adHocRoutingRecipients );
}
