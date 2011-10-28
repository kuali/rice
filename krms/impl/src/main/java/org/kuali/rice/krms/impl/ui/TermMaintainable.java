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
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.MaintenanceDocument;
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
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.repository.AgendaAttributeBo;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.TermBo;
import org.kuali.rice.krms.impl.repository.TermResolverBo;
import org.kuali.rice.krms.impl.repository.TermResolverParameterSpecificationBo;
import org.kuali.rice.krms.impl.repository.TermSpecificationBo;
import org.kuali.rice.krms.impl.type.AgendaTypeServiceBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link org.kuali.rice.krad.maintenance.Maintainable} for the {@link org.kuali.rice.krms.impl.ui.AgendaEditor}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class TermMaintainable extends MaintainableImpl {
	
	private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TermMaintainable.class);

	/**
	 * @return the boService
	 */
	public BusinessObjectService getBoService() {
		return KRADServiceLocator.getBusinessObjectService();
	}

    public List<RemotableAttributeField> retrieveCustomAttributes(View view, Object model, Container container) {

        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();

        String termSpecId = ((TermBo)((MaintenanceForm)model).getDocument().getNewMaintainableObject().getDataObject()).getSpecificationId();

        Collection<TermResolverBo> termResolvers = getBoService().findMatching(TermResolverBo.class,
                Collections.singletonMap("outputId", termSpecId)
        );

        TermResolverBo termResolver = null;

        if (termResolvers != null && termResolvers.size() == 1) {
            termResolver = termResolvers.iterator().next();
        }

        if (termResolver != null && !CollectionUtils.isEmpty(termResolver.getParameterSpecifications())) {
            List<TermResolverParameterSpecificationBo> params = new ArrayList<TermResolverParameterSpecificationBo>(termResolver.getParameterSpecifications());

            Collections.sort(params, new Comparator<TermResolverParameterSpecificationBo>() {
                @Override
                public int compare(TermResolverParameterSpecificationBo o1, TermResolverParameterSpecificationBo o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (TermResolverParameterSpecificationBo param : params) {
                RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(param.getName());
                RemotableTextInput.Builder inputBuilder = RemotableTextInput.Builder.create();
                inputBuilder.setSize(80);
                builder.setControl(inputBuilder);
                builder.setDataType(DataType.STRING);
                builder.setLongLabel(param.getName());
                builder.setShortLabel(param.getName());

                results.add(builder.build());
            }
        }

        return results;
    }

//    private AgendaTypeService getAgendaTypeService(String krmsTypeId) {
//        //
//        // Get the AgendaTypeService by hook or by crook
//        //
//
//        KrmsTypeDefinition krmsType =
//                    KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
//                            getTypeById(krmsTypeId);
//
//        AgendaTypeService agendaTypeService = null;
//
//        if (!StringUtils.isBlank(krmsTypeId)) {
//            String serviceName = krmsType.getServiceName();
//
//            if (!StringUtils.isBlank(serviceName)) {
//                agendaTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
//            }
//        }
//
//        if (agendaTypeService == null) { agendaTypeService = AgendaTypeServiceBase.defaultAgendaTypeService; }
//
//        return agendaTypeService;
//    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
		Map<String, String[]> requestParameters) {

		super.processAfterNew(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription("New Term Document");

	}



    @Override
    public Class getDataObjectClass() {
        return TermBo.class;
    }

    /*
    @Override
    protected void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        MaintenanceForm form = (MaintenanceForm) model;
        AgendaEditor agendaEditor = (AgendaEditor) form.getDocument().getNewMaintainableObject().getDataObject();
        if (addLine instanceof ActionBo) {
            ((ActionBo) addLine).setNamespace(agendaEditor.getAgendaItemLine().getRule().getNamespace());
        }

        super.processBeforeAddLine(view, collectionGroup, model, addLine);
    }
    */
}