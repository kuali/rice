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

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBo;

/**
 * {@link Maintainable} for the {@link AgendaEditor}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AgendaEditorMaintainable extends KualiMaintainableImpl {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @return the boService
	 */
	public BusinessObjectService getBoService() {
		return KRADServiceLocator.getBusinessObjectService();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setupMaintenanceObject(MaintenanceDocument document,
			String maintenanceAction, Map<String, String[]> requestParameters) {

		super.setupMaintenanceObject(document, maintenanceAction, requestParameters);
		
//		if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
//			String[] agendaIds = requestParameters.get("agendaId");
//			if (agendaIds == null || agendaIds.length != 1) { 
//				//throw new RiceRuntimeException("one and only one agendaId request parameter may be passed");
//			} else {
//				// TODO: change this, it makes more sense for MAINTENANCE_EDIT_ACTION
//				String agendaId = agendaIds[0];
//
//				AgendaBo agenda = getBoService().findBySinglePrimaryKey(AgendaBo.class, agendaId);
//				String contextId = agenda.getContextId();
//
//				ContextBo context = getBoService().findBySinglePrimaryKey(ContextBo.class, contextId);
//
//				AgendaEditor editor = (AgendaEditor) document.getDocumentBusinessObject();
//
//				editor.setContext(context);
//				editor.setAgenda(agenda);
//			}
//		}
		
		
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
	    // TODO here's where we can handle persisting our agenda and context
	    super.saveBusinessObject();
	}
	
//	/**
//	 * @see org.kuali.rice.krad.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.krad.document.MaintenanceDocument, java.util.Map)
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
	 * @see org.kuali.rice.krad.maintenance.KualiMaintainableImpl#prepareBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
	 */
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		super.prepareBusinessObject(businessObject);
	}
	
}
