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

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
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
public class AgendaEditorMaintainable extends MaintainableImpl {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @return the boService
	 */
	public BusinessObjectService getBoService() {
		return KRADServiceLocator.getBusinessObjectService();
	}

    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {
        Object dataObject = null;

        try {
            // Since the dataObject is a wrapper class we need to build it and populate with the agenda bo.
            AgendaEditor agendaEditor = new AgendaEditor();
            AgendaBo agenda = getLookupService().findObjectBySearch(((AgendaEditor) getDataObject()).getAgenda().getClass(), dataObjectKeys);
            if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction())) {
              // If we don't clear the primary key and set the fieldsClearedOnCopy flag then the
              // MaintenanceDocumentServiceImpl.processMaintenanceObjectForCopy() will try to locate the primary keys in
              // an attempt to clear them which again would cause an exception due to the wrapper class.
              agenda.setId(null);
              document.setFieldsClearedOnCopy(true);
            }
            agendaEditor.setAgenda(agenda);
            dataObject = agendaEditor;
        } catch (ClassNotPersistenceCapableException ex) {
            if (!document.getOldMaintainableObject().isExternalBusinessObject()) {
                throw new RuntimeException("Data Object Class: " + getDataObjectClass() +
                        " is not persistable and is not externalizable - configuration error");
            }
            // otherwise, let fall through
        }

        return dataObject;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
			Map<String, String[]> requestParameters) {

		super.processAfterNew(document, requestParameters);
		
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

    @Override
    public void saveDataObject() {
        AgendaBo agendaBo = ((AgendaEditor) getDataObject()).getAgenda();
        if (agendaBo instanceof PersistableBusinessObject) {
            getBusinessObjectService().linkAndSave((PersistableBusinessObject) agendaBo);
        } else {
            throw new RuntimeException(
                    "Cannot save object of type: " + agendaBo + " with business object service");
        }
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

    @Override
    public boolean isOldDataObjectInDocument() {
        boolean isOldDataObjectInExistence = true;

        if (getDataObject() == null) {
            isOldDataObjectInExistence = false;
        } else {
            // dataObject contains a non persistable wrapper - use agenda from the wrapper object instead
            Map<String, ?> keyFieldValues = getDataObjectMetaDataService().getPrimaryKeyFieldValues(((AgendaEditor) getDataObject()).getAgenda());
            for (Object keyValue : keyFieldValues.values()) {
                if (keyValue == null) {
                    isOldDataObjectInExistence = false;
                } else if ((keyValue instanceof String) && StringUtils.isBlank((String) keyValue)) {
                    isOldDataObjectInExistence = false;
                }

                if (!isOldDataObjectInExistence) {
                    break;
                }
            }
        }

        return isOldDataObjectInExistence;
    }

}
