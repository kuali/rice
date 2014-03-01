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
package org.kuali.rice.krms.impl.ui;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krms.impl.repository.TermBo;
import org.kuali.rice.krms.impl.repository.TermResolverBo;
import org.kuali.rice.krms.impl.repository.TermResolverParameterSpecificationBo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * {@link org.kuali.rice.krad.maintenance.Maintainable} for the {@link org.kuali.rice.krms.impl.ui.AgendaEditor}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class TermMaintainable extends MaintainableImpl {

    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TermMaintainable.class);


    public List<RemotableAttributeField> retrieveCustomAttributes(View view, Object model, Container container) {

        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();
        String termSpecId = ((TermBo)((MaintenanceDocumentForm)model).getDocument().getNewMaintainableObject().getDataObject()).getSpecificationId();

        QueryResults<TermResolverBo> termResolvers = getDataObjectService().findMatching(TermResolverBo.class,
                QueryByCriteria.Builder.forAttribute("outputId", termSpecId).build()
        );

        TermResolverBo termResolver = null;

        if (termResolvers.getResults() != null && termResolvers.getResults().size() == 1) {
            termResolver = termResolvers.getResults().get(0);
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

    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {

        TermBo termBo = (TermBo) super.retrieveObjectForEditOrCopy(document, dataObjectKeys);
        termBo.exportToParametersMap();

        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction())) {
            document.getDocumentHeader().setDocumentDescription("New Term Document");
        }

        return termBo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterNew(MaintenanceDocument document,
            Map<String, String[]> requestParameters) {

        super.processAfterNew(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription("New Term Document");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {


        super.processAfterEdit(document,
                requestParameters);

        document.getDocumentHeader().setDocumentDescription("Edited Term Document");
    }

    @Override
    public void saveDataObject() {
        TermBo term = (TermBo) getDataObject();
        term.importFromParametersMap();

        super.saveDataObject();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Class getDataObjectClass() {
        return TermBo.class;
    }

    @Override
    public void processBeforeAddLine(ViewModel model, Object addLine, String collectionId, String collectionPath) {
        super.processBeforeAddLine(model, addLine, collectionId, collectionPath);
    }


}