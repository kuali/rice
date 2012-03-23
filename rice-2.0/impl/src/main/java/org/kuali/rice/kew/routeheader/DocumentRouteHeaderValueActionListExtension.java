/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;


/**
 * An extension of {@link DocumentRouteHeaderValue} which is mapped to OJB to help
 * with optimization of the loading of a user's Action List.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
//@Entity
//@Table(name="KREW_DOC_HDR_T")
@MappedSuperclass
public class DocumentRouteHeaderValueActionListExtension extends DocumentRouteHeaderValue {

	private static final long serialVersionUID = 8458532812557846684L;

    @Transient
	private PrincipalContract actionListInitiatorPrincipal = null;

    public PrincipalContract getActionListInitiatorPrincipal() {
        return actionListInitiatorPrincipal;
    }

    public void setActionListInitiatorPrincipal(PrincipalContract actionListInitiatorPrincipal) {
        this.actionListInitiatorPrincipal = actionListInitiatorPrincipal;
    }

    /**
     * Gets the initiator name, masked appropriately if restricted.
     */
    public String getInitiatorName() {
        String initiatorName = null;
        Person initiator = KimApiServiceLocator.getPersonService().getPerson(getActionListInitiatorPrincipal().getPrincipalId());
    	if (initiator != null) {
    	    initiatorName = initiator.getName();
    	}
    	return initiatorName;
    }

}

