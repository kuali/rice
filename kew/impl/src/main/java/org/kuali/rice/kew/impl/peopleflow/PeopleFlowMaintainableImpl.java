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
package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.MaintenanceForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Custom view helper for the people flow maintenance document to retrieve the type attribute remotable fields
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowMaintainableImpl extends MaintainableImpl {

    /**
     * Invokes the {@link org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService} to retrieve the remotable
     * field definitions for the attributes associated with the selected type
     *
     * @param view - view instance
     * @param model - object containing the form data, from which the selected type will be pulled
     * @param container - container that holds the remotable fields
     * @return List<RemotableAttributeField> instances for the type attributes, or empty list if not attributes exist
     */
    public List<RemotableAttributeField> retrieveTypeAttributes(View view, Object model, Container container) {
        List<RemotableAttributeField> remoteFields = new ArrayList<RemotableAttributeField>();

        PeopleFlowBo peopleFlow =
                (PeopleFlowBo) ((MaintenanceForm) model).getDocument().getNewMaintainableObject().getDataObject();

        // retrieve the type service and invoke to get the remotable field definitions
        String typeId = peopleFlow.getTypeId();
        if (StringUtils.isNotBlank(typeId)) {
            KewTypeDefinition typeDefinition = KewApiServiceLocator.getKewTypeRepositoryService().getTypeById(typeId);
            PeopleFlowTypeService peopleFlowTypeService = GlobalResourceLoader.<PeopleFlowTypeService>getService(
                    typeDefinition.getServiceName());
            remoteFields = peopleFlowTypeService.getAttributeFields(typeId);
        }

        return remoteFields;
    }

    /**
     * Set the attribute bo list from the map of attribute key/value pairs and then calls
     * {@link org.kuali.rice.kew.api.peopleflow.PeopleFlowService} to save the people flow instance
     */
    @Override
    public void saveDataObject() {
        ((PeopleFlowBo) getDataObject()).updateAttributeBoValues();

        PeopleFlowDefinition peopleFlowDefinition = PeopleFlowBo.to(((PeopleFlowBo) getDataObject()));
        if (StringUtils.isNotBlank(peopleFlowDefinition.getId())) {
            KewApiServiceLocator.getPeopleFlowService().updatePeopleFlow(peopleFlowDefinition);
        } else {
            KewApiServiceLocator.getPeopleFlowService().createPeopleFlow(peopleFlowDefinition);
        }
    }

    @Override
    public void processCollectionAddLine(View view, Object model, String collectionPath) {
        // =======================================================================================
        // COPIED FROM ViewHelperServiceImpl processCollectionAddLine to add sorting of collection
        // =======================================================================================
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // now get the new line we need to add
        String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLinePath);
        if (addLine == null) {
            logAndThrowRuntime("Add line instance not found for path: " + addLinePath);
        }

        processBeforeAddLine(view, collectionGroup, model, addLine);

        // validate the line to make sure it is ok to add
        boolean isValidLine = performAddLineValidation(view, collectionGroup, model, addLine);
        if (isValidLine) {
            // TODO: should check to see if there is an add line method on the
            // collection parent and if so call that instead of just adding to
            // the collection (so that sequence can be set)
            if (collection instanceof List) {
                ((List) collection).add(0, addLine);
                // ADDED sorting for PeopleFlowMemberBo
                if (addLine instanceof PeopleFlowMemberBo) {
                    Collections.sort((List) collection, new Comparator<Object>() {
                        public int compare(Object o1, Object o2) {
                            if ((o1 instanceof PeopleFlowMemberBo) && (o1 instanceof PeopleFlowMemberBo)) {
                                return ((PeopleFlowMemberBo)o1).getPriority() - ((PeopleFlowMemberBo)o2).getPriority();
                            }
                            return 0; // if not both PeopleFlowMemberBo something strange is going on.  Use equals as doing nothing.
                        }
                    });
                }
            } else {
                collection.add(addLine);
            }

            // make a new instance for the add line
            collectionGroup.initializeNewCollectionLine(view, model, collectionGroup, true);
        }

        processAfterAddLine(view, collectionGroup, model, addLine);
    }
}