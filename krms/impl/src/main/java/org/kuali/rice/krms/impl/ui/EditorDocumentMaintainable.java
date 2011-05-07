/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBo;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class EditorDocumentMaintainable extends KualiMaintainableImpl {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @return the boService
	 */
	public BusinessObjectService getBoService() {
		return KNSServiceLocator.getBusinessObjectService();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setupMaintenanceObject(MaintenanceDocument document,
			String maintenanceAction, Map<String, String[]> requestParameters) {

		super.setupMaintenanceObject(document, maintenanceAction, requestParameters);
		
		if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
			String[] agendaIds = requestParameters.get("agendaId");
			if (agendaIds == null || agendaIds.length != 1) { 
				//throw new RiceRuntimeException("one and only one agendaId request parameter may be passed");
			} else {
				// TODO: throw out this hacky junk
				String agendaId = agendaIds[0];

				AgendaBo agenda = getBoService().findBySinglePrimaryKey(AgendaBo.class, agendaId);
				String contextId = agenda.getContextId();

				ContextBo context = getBoService().findBySinglePrimaryKey(ContextBo.class, contextId);

				EditorDocument editor = (EditorDocument) document.getDocumentBusinessObject();

				editor.setContext(context);
				editor.setAgenda(agenda);
			}
		}
		
		
	}
	
//	/**
//	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
//	 */
//	@Override
//	public void processAfterNew(MaintenanceDocument document,
//			Map<String, String[]> parameters) {
//		// TODO gilesp - THIS METHOD NEEDS JAVADOCS
//		super.processAfterNew(document, parameters);
//		
//		EditorDocument editor = (EditorDocument) document.getDocumentBusinessObject();
//		
//		editor.getContext().setName("new");
//	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getBusinessObject()
	 */
	@Override
	public PersistableBusinessObject getBusinessObject() {
		// TODO gilesp - THIS METHOD NEEDS JAVADOCS
		return super.getBusinessObject();
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareBusinessObject(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		// TODO gilesp - THIS METHOD NEEDS JAVADOCS
		super.prepareBusinessObject(businessObject);
	}
	
}
