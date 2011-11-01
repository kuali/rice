package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.framework.peopleflow.PeopleFlowTypeService;
import org.kuali.rice.krad.inquiry.InquirableImpl;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.MaintenanceForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view helper for the people flow inquiry view to retrieve the type attribute remotable fields
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowInquirableImpl extends InquirableImpl {
    private static final long serialVersionUID = -8392423307944257532L;

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

        PeopleFlowBo peopleFlow = (PeopleFlowBo) ((InquiryForm) model).getDataObject();

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
}
