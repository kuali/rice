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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextarea;
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
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.repository.AgendaAttributeBo;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link Maintainable} for the {@link AgendaEditor}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AgendaEditorMaintainable extends MaintainableImpl {
	
	private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgendaEditorMaintainable.class);

    private final ModelObjectUtils.Transformer<KrmsTypeAttribute, RemotableAttributeField> attributeTransformer =
            new TypeAttributeToFieldTransformer();

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

        if (agendaEditor.getAgenda() != null && !StringUtils.isBlank(agendaEditor.getAgenda().getTypeId())) {

            KrmsTypeDefinition krmsType =
                    KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
                            getTypeById(agendaEditor.getAgenda().getTypeId());

            if (krmsType != null) {
                String serviceName = krmsType.getServiceName();

                if (!StringUtils.isBlank(serviceName)) {
                    AgendaTypeService agendaTypeService = KrmsRepositoryServiceLocator.getService(krmsType.getServiceName());

                    if (agendaTypeService == null) {
                        LOG.warn("could not find " + AgendaTypeService.class.getSimpleName() + " with name '" + krmsType.getServiceName() +"'");
                    } else {
                        List<RemotableAttributeField> remotableAttributeFields = agendaTypeService.getAttributeFields(krmsType.getId());

                        if (!CollectionUtils.isEmpty(remotableAttributeFields)) {
                            results.addAll(remotableAttributeFields);
                        }
                    }
                }

                // need to merge in type attributes that the service doesn't create RemotableAttributeFields for
                // iterate through attribute fields and build list of attribute names
                Set<String> serviceDefinedAttributes = new HashSet<String>();
                for (RemotableAttributeField field : results) {
                    serviceDefinedAttributes.add(field.getName());
                }

                List<KrmsTypeAttribute> typeAttributes = krmsType.getAttributes();
                if (!CollectionUtils.isEmpty(typeAttributes)) {
                    List<RemotableAttributeField> typeAttributeFields = ModelObjectUtils.transform(typeAttributes, attributeTransformer);

                    // add fields for any attributes not yet provided by the AgendaTypeService
                    for (RemotableAttributeField field : typeAttributeFields) {
                        if (!serviceDefinedAttributes.contains(field.getName())) {
                            results.add(field);
                        }
                    }
                }
            }
        }

        return results;
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
    public void saveDataObject() {
        AgendaEditor agendaEditor = (AgendaEditor) getDataObject();
        AgendaBo agendaBo = ((AgendaEditor) getDataObject()).getAgenda();
        if (agendaBo != null) { // apply custom attributes to agendaBo

            Set<AgendaAttributeBo> attributes = new HashSet<AgendaAttributeBo>();

            ContextBo context = agendaEditor.getContext();

            if (context == null) {
                context = getBoService().findBySinglePrimaryKey(ContextBo.class, agendaBo.getContextId());
            }

            Map<String, KrmsAttributeDefinition> attributeDefinitionMap = buildAttributeDefinitionMap(agendaBo.getTypeId());

            // for each entry, build an AgendaAttributeBo and add it to the set
            for (Map.Entry<String,String> entry  : agendaEditor.getCustomAttributesMap().entrySet()){

                KrmsAttributeDefinition attrDef = attributeDefinitionMap.get(entry.getKey()); // get the definition from our map

                if (attrDef != null) {
                    AgendaAttributeBo attributeBo = new AgendaAttributeBo();
                    attributeBo.setAgendaId(agendaBo.getId());
                    attributeBo.setAttributeDefinitionId(attrDef.getId());
                    attributeBo.setValue(entry.getValue());
                    attributes.add( attributeBo );
                } else {
                    throw new RiceIllegalStateException("there is no attribute definition with the name '" +
                    entry.getKey() + "' that is valid for the agenda type with id = '" + agendaBo.getTypeId() +"'");
                }
            }
            agendaBo.setAttributeBos(attributes);

            // And finally, save it
            getBusinessObjectService().linkAndSave((PersistableBusinessObject) agendaBo);
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

    private static class TypeAttributeToFieldTransformer implements  ModelObjectUtils.Transformer<KrmsTypeAttribute, RemotableAttributeField>, Serializable {

        private static final long serialVersionUID = 7469910736454633231L;

        @Override
                public RemotableAttributeField transform(KrmsTypeAttribute input) {

                    KrmsTypeRepositoryService typeRepositoryService = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();

                    KrmsAttributeDefinition attributeDefinition =
                            typeRepositoryService.getAttributeDefinitionById(input.getAttributeDefinitionId());

                    RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(attributeDefinition.getName());

                    RemotableTextarea.Builder controlBuilder = RemotableTextarea.Builder.create();
                    controlBuilder.setCols(80);
                    controlBuilder.setRows(5);


                    controlBuilder.setWatermark(attributeDefinition.getDescription());

                    builder.setLongLabel(attributeDefinition.getName());
                    builder.setName(attributeDefinition.getName());
                    builder.setHelpSummary("helpSummary: " + attributeDefinition.getDescription());
                    builder.setHelpDescription("helpDescription: " + attributeDefinition.getDescription());
                    builder.setControl(controlBuilder);
                    builder.setMaxLength(400);

                    return builder.build();
                }
            };

}
