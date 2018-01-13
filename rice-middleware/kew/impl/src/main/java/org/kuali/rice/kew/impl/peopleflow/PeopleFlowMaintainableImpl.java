/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;

import javax.xml.namespace.QName;
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
     * sort {@link org.kuali.rice.kew.impl.peopleflow.PeopleFlowMemberBo}s by stop number (priority), and clean
     * out the actionRequestPolicyCode for non-ROLE members.
     *
     * @param collection - the Collection to add the given addLine to
     * @param addLine - the line to add to the given collection
     * @param insertFirst - indicates if the item should be inserted as the first item
     */
    @Override
    protected int addLine(Collection<Object> collection, Object addLine, boolean insertFirst) {
        if (collection instanceof List) {
            ((List) collection).add(0, addLine);
            if (addLine instanceof PeopleFlowMemberBo) {

                // action request policy is only valid for MemberType.ROLE
                PeopleFlowMemberBo member = (PeopleFlowMemberBo) addLine;
                if (member.getMemberType() != MemberType.ROLE) {
                    member.setActionRequestPolicyCode(null);
                }

                Collections.sort((List) collection, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        if ((o1 instanceof PeopleFlowMemberBo) && (o1 instanceof PeopleFlowMemberBo)) {
                            return ((PeopleFlowMemberBo) o1).getPriority() - ((PeopleFlowMemberBo) o2)
                                    .getPriority();
                        }
                        return 0; // if not both PeopleFlowMemberBo something strange is going on.  Use equals as doing nothing.
                    }
                });
            }
        } else {
            collection.add(addLine);
        }
        // find the index where we added it after the sort so we can return that index
        int index = 0;
        for (Object element : collection) {
            if (element == addLine) {
                return index;
            }
            index++;
        }
        return -1;
    }

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
                (PeopleFlowBo) ((MaintenanceDocumentForm) model).getDocument().getNewMaintainableObject().getDataObject();

        // retrieve the type service and invoke to get the remotable field definitions
        String typeId = peopleFlow.getTypeId();
        if (StringUtils.isNotBlank(typeId)) {
            KewTypeDefinition typeDefinition = KewApiServiceLocator.getKewTypeRepositoryService().getTypeById(typeId);
            PeopleFlowTypeService peopleFlowTypeService = GlobalResourceLoader.<PeopleFlowTypeService>getService(
                QName.valueOf(typeDefinition.getServiceName()));
            remoteFields = peopleFlowTypeService.getAttributeFields(typeId);
        }

        return remoteFields;
    }

    /**
     * Set the attribute bo list from the map of attribute key/value pairs.
     */
    @Override
    public void prepareForSave() {
        ((PeopleFlowBo) getDataObject()).updateAttributeBoValues();
    }

    /**
     * Set the map of attribute key/value pairs list from the attribute bo list and update the members.
     */
    @Override
    public void processAfterRetrieve() {
        ((PeopleFlowBo) getDataObject()).postLoad();
    }

    /**
     * Calls {@link org.kuali.rice.kew.api.peopleflow.PeopleFlowService} to save the people flow instance
     */
    @Override
    public void saveDataObject() {
        PeopleFlowDefinition peopleFlowDefinition;
        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction())) {
            peopleFlowDefinition = PeopleFlowBo.maintenanceCopy(((PeopleFlowBo) getDataObject()));
        } else {
        // this to method ends up copying a versionNumber to null versionNumber 
            peopleFlowDefinition = PeopleFlowBo.to(((PeopleFlowBo) getDataObject()));
        }
        if (StringUtils.isNotBlank(peopleFlowDefinition.getId())) {
            KewApiServiceLocator.getPeopleFlowService().updatePeopleFlow(peopleFlowDefinition);
        } else {
            KewApiServiceLocator.getPeopleFlowService().createPeopleFlow(peopleFlowDefinition);
        }
    }

    /**
     * In the case of edit maintenance adds a new blank line to the old side
     * This method is intended to override the method in MaintainableImpl
     * but has a different set of parameters, so it is not actually an override.
     * This version was needed to fetch the old collection from a different path
     * than MaintainableImpl uses.
     *
     * @see org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl#processAfterAddLine(org.kuali.rice.krad.uif.view.View,
     * org.kuali.rice.krad.uif.container.CollectionGroup, Object, Object, boolean)
     */
    @Override
    public void processAfterAddLine(ViewModel model, Object addLine, String collectionId, String collectionPath,
                boolean isValidLine) {
        // Check for maintenance documents in edit but exclude notes
        if (model instanceof MaintenanceDocumentForm
                && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(((MaintenanceDocumentForm)model).getMaintenanceAction()) && !(addLine instanceof Note)) {

            Class<?> collectionObjectClass = (Class<?>) model.getViewPostMetadata().getComponentPostData(collectionId,
                    UifConstants.PostMetadata.COLL_OBJECT_CLASS);

            // get the old object's collection
            String oldCollectionPath = collectionPath.replace("newMaintainableObject","oldMaintainableObject");
            Collection<Object> oldCollection = ObjectPropertyUtils.getPropertyValue(model, oldCollectionPath );
            try {
                Object blankLine = collectionObjectClass.newInstance();
                oldCollection.add(blankLine);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create new line instance for old maintenance object", e);
            }
        }
    }

    /**
     * This method is an override of ViewHelperService.processCollectionDeleteLine().
     * It is virtually identical except that a local processAfterDeleteLine() method is called
     * with a different parameter set than is called from within this method to delete the line
     * from the old maintainable object.
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionDeleteLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, java.lang.String, int)
     */
    @Override
    public void processCollectionDeleteLine(ViewModel model, String collectionId, String collectionPath, int lineIndex) {

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object deleteLine = ((List<Object>) collection).get(lineIndex);

            // validate the delete action is allowed for this line
            boolean isValid = performDeleteLineValidation(model, collectionId, collectionPath, deleteLine);
            if (isValid) {
                ((List<Object>) collection).remove(lineIndex);
                processAfterDeleteLine(model, collectionId, collectionPath, lineIndex);
            }
        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }
    }

    /**
     * In the case of edit maintenance deleted the item on the old side.
     * This method is intended to override the method in MaintainableImpl
     * but has a different set of parameters, so it is not actually an override.
     * This was needed to fetch the old collection from a different path
     * than MaintainableImpl uses. This version has the path (to the newMaintainableObject
     * provided as a parameter, this is used to generate a path to the oldMaintainableObject
     *
     *
     * @see org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl#processAfterDeleteLine(View,
     *      org.kuali.rice.krad.uif.container.CollectionGroup, java.lang.Object,  int)
     */
    @Override
    public void processAfterDeleteLine(ViewModel model, String collectionId, String collectionPath, int lineIndex) {

        // Check for maintenance documents in edit
        if (model instanceof MaintenanceDocumentForm
                && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(((MaintenanceDocumentForm)model).getMaintenanceAction())) {

            // get the old object's collection
            String oldCollectionPath = collectionPath.replace("newMaintainableObject","oldMaintainableObject");
            Collection<Object> oldCollection = ObjectPropertyUtils.getPropertyValue(model, oldCollectionPath);
            try {
                // Remove the object at lineIndex from the collection
                oldCollection.remove(oldCollection.toArray()[lineIndex]);
            } catch (Exception e) {
                throw new RuntimeException("Unable to delete line instance for old maintenance object", e);
            }
        }
    }


}
