/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.krms.framework.type.RuleTypeService;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.type.ActionTypeServiceBase;
import org.kuali.rice.krms.impl.type.AgendaTypeServiceBase;
import org.kuali.rice.krms.impl.type.RuleTypeServiceBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Maintainable} for the {@link AgendaEditor}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AgendaEditorMaintainable extends MaintainableImpl {
	
	private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgendaEditorMaintainable.class);

	/**
	 * @return the boService
	 */
	public BusinessObjectService getBoService() {
		return KRADServiceLocator.getBusinessObjectService();
	}

    /**
     * return the contextBoService
     */
    private ContextBoService getContextBoService() {
        return KrmsRepositoryServiceLocator.getContextBoService();
    }

    public List<RemotableAttributeField> retrieveCustomAttributes(View view, Object model, Container container) {
        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();

        MaintenanceForm maintenanceForm = (MaintenanceForm)model;
        AgendaEditor agendaEditor = (AgendaEditor)maintenanceForm.getDocument().getNewMaintainableObject().getDataObject();

        // if we have an agenda w/ a typeId set on it
        if (agendaEditor.getAgenda() != null && !StringUtils.isBlank(agendaEditor.getAgenda().getTypeId())) {

            String krmsTypeId = agendaEditor.getAgenda().getTypeId();

            AgendaTypeService agendaTypeService = getAgendaTypeService(krmsTypeId);
            results.addAll(agendaTypeService.getAttributeFields(krmsTypeId));
        }

        return results;
    }

    private AgendaTypeService getAgendaTypeService(String krmsTypeId) {
        //
        // Get the AgendaTypeService by hook or by crook
        //

        KrmsTypeDefinition krmsType =
                    KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
                            getTypeById(krmsTypeId);

        AgendaTypeService agendaTypeService = null;

        if (!StringUtils.isBlank(krmsTypeId)) {
            String serviceName = krmsType.getServiceName();

            if (!StringUtils.isBlank(serviceName)) {
                agendaTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
            }
        }

        if (agendaTypeService == null) { agendaTypeService = AgendaTypeServiceBase.defaultAgendaTypeService; }

        return agendaTypeService;
    }

    /**
     *  This only supports a single action within a rule.
     */
    public List<RemotableAttributeField> retrieveRuleActionCustomAttributes(View view, Object model, Container container) {
        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();

        MaintenanceForm maintenanceForm = (MaintenanceForm)model;
        AgendaEditor agendaEditor = (AgendaEditor)maintenanceForm.getDocument().getNewMaintainableObject().getDataObject();

        // if we have an rule action w/ a typeId set on it
        if (!StringUtils.isBlank(agendaEditor.getAgendaItemLineRuleAction().getTypeId())) {
            ActionTypeService actionTypeService = getActionTypeService(agendaEditor.getAgendaItemLineRuleAction().getTypeId());
            results.addAll(actionTypeService.getAttributeFields(agendaEditor.getAgendaItemLineRuleAction().getTypeId()));
        }

        return results;
    }

    public List<RemotableAttributeField> retrieveRuleCustomAttributes(View view, Object model, Container container) {
        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();

        MaintenanceForm maintenanceForm = (MaintenanceForm)model;
        AgendaEditor agendaEditor = (AgendaEditor)maintenanceForm.getDocument().getNewMaintainableObject().getDataObject();

        // if we have an rule w/ a typeId set on it
        if (agendaEditor.getAgendaItemLine() != null && agendaEditor.getAgendaItemLine().getRule() != null
                && !StringUtils.isBlank(agendaEditor.getAgendaItemLine().getRule().getTypeId())) {

            String krmsTypeId = agendaEditor.getAgendaItemLine().getRule().getTypeId();

            RuleTypeService ruleTypeService = getRuleTypeService(krmsTypeId);
            results.addAll(ruleTypeService.getAttributeFields(krmsTypeId));
        }

        return results;
    }

    private ActionTypeService getActionTypeService(String krmsTypeId) {
        //
        // Get the ActionTypeService by hook or by crook
        //

        KrmsTypeDefinition krmsType =
                    KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
                            getTypeById(krmsTypeId);

        ActionTypeService actionTypeService = null;

        if (!StringUtils.isBlank(krmsTypeId)) {
            String serviceName = krmsType.getServiceName();

            if (!StringUtils.isBlank(serviceName)) {
                actionTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
            }
        }
        if (actionTypeService == null) {
            actionTypeService = ActionTypeServiceBase.defaultActionTypeService;
        }

//        if (actionTypeService == null) { actionTypeService = AgendaTypeServiceBase.defaultAgendaTypeService; }

        return actionTypeService;
    }

    private RuleTypeService getRuleTypeService(String krmsTypeId) {
        RuleTypeService ruleTypeService = null;
        String serviceName = getRuleTypeServiceName(krmsTypeId);

        if (!StringUtils.isBlank(serviceName)) {
            ruleTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
        }
        if (ruleTypeService == null) {
            ruleTypeService = RuleTypeServiceBase.defaultRuleTypeService;
        }
        return ruleTypeService;
    }

    private String getRuleTypeServiceName(String krmsTypeId) {
        String serviceName = null;
        KrmsTypeDefinition krmsType =
                KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
                        getTypeById(krmsTypeId);

        if (!StringUtils.isBlank(krmsTypeId)) {
            serviceName = krmsType.getServiceName();
        }
        return serviceName;
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

            // set custom attributes map in AgendaEditor
            agendaEditor.setCustomAttributesMap(agenda.getAttributes());

            // set extra fields on AgendaEditor
            agendaEditor.setNamespace(agenda.getContext().getNamespace());
            agendaEditor.setContextName(agenda.getContext().getName());

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
        document.getDocumentHeader().setDocumentDescription("New Agenda Editor Document");

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
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterCopy(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription("New Agenda Editor Document");
    }

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterEdit(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription("Modify Agenda Editor Document");
    }

    @Override
    public void prepareForSave() {
        // set agenda attributes
        AgendaEditor agendaEditor = (AgendaEditor) getDataObject();
        agendaEditor.getAgenda().setAttributes(agendaEditor.getCustomAttributesMap());
    }

    @Override
    public void saveDataObject() {
        AgendaBo agendaBo = ((AgendaEditor) getDataObject()).getAgenda();
        if (agendaBo instanceof PersistableBusinessObject) {
            Map<String,String> primaryKeys = new HashMap<String, String>();
            primaryKeys.put("id", agendaBo.getId());
            AgendaBo blah = getBusinessObjectService().findByPrimaryKey(AgendaBo.class, primaryKeys);
            getBusinessObjectService().delete(blah);

            getBusinessObjectService().linkAndSave(agendaBo);
        } else {
            throw new RuntimeException(
                    "Cannot save object of type: " + agendaBo + " with business object service");
        }
    }

    /**
     * Build a map from attribute name to attribute definition from all the defined attribute definitions for the
     * specified agenda type
     * @param agendaTypeId
     * @return
     */
    private Map<String, KrmsAttributeDefinition> buildAttributeDefinitionMap(String agendaTypeId) {
        KrmsAttributeDefinitionService attributeDefinitionService =
            KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions =
                attributeDefinitionService.findAttributeDefinitionsByType(agendaTypeId);

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }
        return attributeDefinitionMap;
    }

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

     // Since the dataObject is a wrapper class we need to return the agendaBo instead.
    @Override
    public Class getDataObjectClass() {
        return AgendaBo.class;
    }

    @Override
    protected void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        MaintenanceForm form = (MaintenanceForm) model;
        AgendaEditor agendaEditor = (AgendaEditor) form.getDocument().getNewMaintainableObject().getDataObject();
        if (addLine instanceof ActionBo) {
            ((ActionBo) addLine).setNamespace(agendaEditor.getAgendaItemLine().getRule().getNamespace());
        }

        super.processBeforeAddLine(view, collectionGroup, model, addLine);
    }
}
