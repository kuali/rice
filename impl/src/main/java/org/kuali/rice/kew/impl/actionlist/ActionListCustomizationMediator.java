/*
 * Copyright 2006-2012 The Kuali Foundation
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.cxf.common.util.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.action.ActionItemCustomization;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.framework.actionlist.ActionListCustomizationHandlerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal utility class that partitions ActionItems by application id, and calls the appropriate
 * {@link ActionListCustomizationHandlerService} for each parition to retrieve any customizations.
 */
public class ActionListCustomizationMediator implements ActionListCustomizationHandlerService {

    /**
     * {@inheritDoc}
     * @deprecated use {@link #getActionListCustomizations(String, java.util.List)}
     */
    @Deprecated
    @Override
    public List<ActionItemCustomization> customizeActionList(String principalId, List<ActionItem> actionItems)
            throws RiceIllegalArgumentException {
        Map<String, ActionItemCustomization> customizationMap = getActionListCustomizations(principalId, actionItems);
        List<ActionItemCustomization> results = new ArrayList<ActionItemCustomization>(actionItems.size());

        for (ActionItem item : actionItems) {
            ActionItemCustomization customization = customizationMap.get(item.getId());
            // customization may be null, in case we'll put a null item in the list
            results.add(customization);
        }

        return results;
    }

    /**
     * <p>partitions ActionItems by application id, and calls the appropriate
     * {@link ActionListCustomizationHandlerService} for each parition, merging the results.</p>
     *
     * <dl><dt><b>inherited docs:</b></dt><dd>{@inheritDoc}</dd></dl>
     */
    @Override
    public Map<String, ActionItemCustomization> getActionListCustomizations(String principalId,
            List<ActionItem> actionItems) throws RiceIllegalArgumentException {
        if (StringUtils.isEmpty(principalId)) {
            throw new RiceIllegalArgumentException("invalid principalId: " + principalId);
        }
        if (actionItems == null) {
            actionItems = Collections.emptyList();
        }

        // map from action item ID to ActionItemCustomization
        Map<String, ActionItemCustomization> results = new HashMap<String, ActionItemCustomization>();

        // group each action item by application id that needs to be called for action list customizations (note that
        // the application id comes from the extension/rule attribute record, most action lists will have doc types
        // with no custom action list attribute, though the default still needs to be run in this case)

        ListMultimap<String, ActionItem> itemsByApplicationId = ArrayListMultimap.create();

        for (ActionItem actionItem : actionItems) {
            DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(actionItem.getDocName());
            if (docType == null) {
                throw new IllegalStateException(
                        String.format("Document Type with name %s does not exist", actionItem.getDocName())
                );
            }
            // OK to have a null key, this represents the default app id
            itemsByApplicationId.put(docType.getApplicationId(), actionItem);
        }

        // For each application id, pass all action items which might need to be customized (because they have a
        // document type, which declares an action list attribute, which has an application id declared) to the
        // appropriate ActionListCustomizationHandlerService endpoint

        for (String applicationId : itemsByApplicationId.keySet()) {
            ActionListCustomizationHandlerService actionListCustomizationHandler =
                    KewFrameworkServiceLocator.getActionListCustomizationHandlerService(applicationId);

            if (actionListCustomizationHandler == null) {
                // get the local ActionListCustomizationHandlerService as a fallback
                actionListCustomizationHandler =
                        KewFrameworkServiceLocator.getActionListCustomizationHandlerService(null);
            }

            Map<String, ActionItemCustomization> customizations =
                    actionListCustomizationHandler.getActionListCustomizations(principalId, itemsByApplicationId.get(
                            applicationId));


            // Get back the customized results and reassemble with customized results from all different application
            // customizations (as well as default customizations)
            results.putAll(customizations);
        }

        return results;
    }
}
