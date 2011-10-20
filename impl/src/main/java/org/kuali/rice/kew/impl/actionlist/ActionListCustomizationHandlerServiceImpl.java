/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.impl.actionlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.action.ActionItemCustomization;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.api.extension.ExtensionUtils;
import org.kuali.rice.kew.framework.actionlist.ActionListCustomizationHandlerService;

/**
 * Reference implementation of the ActionListCustomizationHandlerService.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListCustomizationHandlerServiceImpl implements ActionListCustomizationHandlerService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionListCustomizationHandlerServiceImpl.class);
            
    private ExtensionRepositoryService extensionRepositoryService;
    
    @Override
    public List<ActionItemCustomization> customizeActionList(String principalId, List<ActionItem> actionItems)
            throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId was null or blank");
        }
        if (actionItems == null) {
            actionItems = Collections.emptyList();
        }
        List<ActionItemCustomization> actionItemCustomizations = new ArrayList<ActionItemCustomization>();
        // TODO Iterate through the list of Action Items. Return Action Item Customizations only for those Action Items which...    
        for (ActionItem actionItem : actionItems) {
            // TODO Figure out how to properly load a CAL Attribute 
            // Load the CustomActionListAttribute
            CustomActionListAttribute customActionListAttribute = loadAttribute("attributeName?");
            // Build the list of ActionItemCustomizations
            ActionItemCustomization actionItemCustomization;
            try {
                actionItemCustomization = ActionItemCustomization.Builder.create(customActionListAttribute.getLegalActions(principalId, actionItem), customActionListAttribute.getDocHandlerDisplayParameters(principalId, actionItem)).build();
                actionItemCustomizations.add(actionItemCustomization);
            } catch (Exception e) {
                // TODO give a better message like attribute name or something
                LOG.error("Problem loading custom action list attribute", e);
            }           
        }
        return actionItemCustomizations;
    }

    /**
     * Loads CustomActionListAttribute implementation class via {@link ExtensionRepositoryService}
     * @param attributeName the CustomActionListAttribute name
     * @return instance of the CustomActionListAttribute implementation class
     * @throws RiceIllegalArgumentException if specified attribute name cannot be found or loaded
     */
    protected CustomActionListAttribute loadAttribute(String attributeName) {
        ExtensionDefinition extensionDefinition = extensionRepositoryService.getExtensionByName(attributeName);
        if (extensionDefinition == null) {
            throw new RiceIllegalArgumentException("Failed to locate a CustomActionListAttribute with the given name: " + attributeName);
        }
        
        CustomActionListAttribute customActionListAttribute = ExtensionUtils.loadExtension(extensionDefinition);
        if (customActionListAttribute == null) {
            throw new RiceIllegalArgumentException("Failed to load CustomActionListAttribute for: " + extensionDefinition);
        }
        return (CustomActionListAttribute)customActionListAttribute;
    }
    
    /**
     * Loads RuleValidationAttribute implementation class via {@link ExtensionRepositoryService}
     * @param attributeName the RuleValidationAttribute name
     * @return instance of the RuleValidationAttribute implementation class
     * @throws RiceIllegalArgumentException if specified attribute name cannot be found or loaded
     */

    public ExtensionRepositoryService getExtensionRepositoryService() {
        return extensionRepositoryService;
    }

    public void setExtensionRepositoryService(ExtensionRepositoryService extensionRepositoryService) {
        this.extensionRepositoryService = extensionRepositoryService;
    }
}
