/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krad.app.persistence.jpa;

import java.util.HashSet;
import java.util.Set;

/**
 * The class which exposes the names of all KNS business object entities - which are managed
 * by all JPA persistence units
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KNSPersistableBusinessObjectClassExposer implements
        PersistableBusinessObjectClassExposer {

	/**
	 * Exposes all KNS objects - a hard coded list for now - to persistence units
	 * 
	 * @see PersistableBusinessObjectClassExposer#exposePersistableBusinessObjectClassNames()
	 */
	@Override
	public Set<String> exposePersistableBusinessObjectClassNames() {
		Set<String> knsBOs = new HashSet<String>();
		/*knsBOs.add(org.kuali.rice.kns.document.DocumentBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.MaintenanceDocumentBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.MaintenanceLock.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.TransactionalDocumentBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.AdHocRoutePerson.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.AdHocRouteRecipient.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.AdHocRouteWorkgroup.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.Attachment.class.getName());
		//knsBOs.add(org.kuali.rice.kns.bo.CampusImpl.class.getName());
		//knsBOs.add(org.kuali.rice.kns.bo.CampusTypeImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.DocumentAttachment.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.DocumentHeader.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.GlobalBusinessObjectDetailBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.LookupResults.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.KualiCodeBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.MultipleValueLookupMetadata.class.getName());
		knsBOs.add(NamespaceBo.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.Note.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.NoteType.class.getName());
		knsBOs.add(ParameterBo.class.getName());
		knsBOs.add(ComponentBo.class.getName());
		knsBOs.add(ParameterTypeBo.class.getName());
		knsBOs.add(CampusBo.class.getName());
		knsBOs.add(CampusTypeBo.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.PersistableAttachmentBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.PersistableBusinessObjectBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.PersistableBusinessObjectExtensionBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.authorization.PessimisticLock.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.SelectedObjectIds.class.getName());
		knsBOs.add(org.kuali.rice.shareddata.impl.country.CountryBo.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.CountyImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.StateImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.PostalCodeImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.SessionDocument.class.getName()); */
		return knsBOs;
	}

}
