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
package org.kuali.rice.kew.impl.actionlist;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.action.ActionItemCustomization;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.framework.actionlist.ActionListCustomizationHandlerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of the ActionListCustomizationHandlerService.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListCustomizationHandlerServiceImpl implements ActionListCustomizationHandlerService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListCustomizationHandlerServiceImpl.class);

    private DocumentTypeService documentTypeService;

    @Override
    public List<ActionItemCustomization> customizeActionList(String principalId, List<ActionItem> actionItems)
            throws RiceIllegalArgumentException {
        Map<String, ActionItemCustomization> customizationMap = getActionListCustomizations(principalId, actionItems);
        List<ActionItemCustomization> results = new ArrayList<ActionItemCustomization>(actionItems.size());

        for (ActionItem item : actionItems) {
            ActionItemCustomization customization = customizationMap.get(item.getId());
            // customization may be null, in that case we'll put a null item in the list
            results.add(customization);
        }

        return results;
    }

    @Override
    public Map<String, ActionItemCustomization> getActionListCustomizations(String principalId,
            List<ActionItem> actionItems) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId was null or blank");
        }
        if (actionItems == null) { actionItems = Collections.emptyList(); }

        Map<String, ActionItemCustomization> actionItemCustomizations = new HashMap<String, ActionItemCustomization>();

        for (ActionItem actionItem : actionItems) {
            DocumentType documentType = getDocumentTypeService().findByName(actionItem.getDocName());

            if (documentType == null) {
                throw new IllegalStateException("Could not retrieve a document type by name: " +
                        actionItem.getDocName());
            }
            try { // try to get the custom action list attribute and convert it to an ActionItemCustomization
                CustomActionListAttribute customActionListAttribute = documentType.getCustomActionListAttribute();
                if (customActionListAttribute != null) {
                    ActionItemCustomization actionItemCustomization = ActionItemCustomization.Builder.create(
                            customActionListAttribute.getLegalActions(principalId, actionItem),
                            customActionListAttribute.getDocHandlerDisplayParameters(principalId, actionItem)).build();
                    // add to our map of results
                    actionItemCustomizations.put(actionItem.getId(), actionItemCustomization);
                }
            } catch (Exception e) {
                LOG.error("Problem loading custom action list attribute " + actionItem.getId(), e);
            }
        }

        return actionItemCustomizations;
    }

    public DocumentTypeService getDocumentTypeService() {
        return documentTypeService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
}
