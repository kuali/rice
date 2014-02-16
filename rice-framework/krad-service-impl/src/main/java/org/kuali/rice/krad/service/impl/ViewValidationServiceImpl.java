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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.state.StateMapping;
import org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ViewValidationService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Implementation of Validation service for views, uses the same validation mechanisms as DictionaryValidationService
 * but uses a different AttributeValueReader to get the correct information from InputFields - which
 * include any AttributeDefinition defined attributes, if defined and not overriden
 *
 * @see ViewValidationService
 */
public class ViewValidationServiceImpl implements ViewValidationService {

    protected DictionaryValidationService dictionaryValidationService;

    /**
     * @see ViewValidationService#validateView(org.kuali.rice.krad.uif.view.ViewModel)
     */
    @Override
    public DictionaryValidationResult validateView(ViewModel model) {
        return validateView(model, null);
    }

    /**
     * @see ViewValidationService#validateViewSimulation(ViewModel)
     */
    @Override
    public void validateViewSimulation(ViewModel model) {
        validateViewSimulation(model, null);
    }

    /**
     * @see ViewValidationService#validateViewSimulation(ViewModel, String)
     */
    @Override
    public void validateViewSimulation(ViewModel model, String untilState) {
        // Get state mapping for view from post data
        Object stateMappingObject = model.getViewPostMetadata().getComponentPostData(
                model.getViewPostMetadata().getId(), UifConstants.PostMetadata.STATE_MAPPING);

        StateMapping stateMapping = null;
        if (stateMappingObject != null) {
             stateMapping = (StateMapping) stateMappingObject;
        }

        // Get state object path from post data
        Object statePathObject = model.getViewPostMetadata().getComponentPostData(
                model.getViewPostMetadata().getId(), UifConstants.PostMetadata.STATE_OBJECT_BINDING_PATH);

        String path = null;
        if (statePathObject != null) {
             path = (String) statePathObject;
        }

        Object object;
        if (StringUtils.isNotBlank(path)) {
            object = ObjectPropertyUtils.getPropertyValue(model, path);
        } else {
            object = model;
        }

        if (stateMapping != null && !stateMapping.getStates().isEmpty()) {
            int startIndex = stateMapping.getStates().indexOf(stateMapping.getNextState(object));
            if (startIndex == -1) {
                //Assume checking against all states that exist
                startIndex = 0;
            }

            for (int i = startIndex; i < stateMapping.getStates().size(); i++) {
                String state = stateMapping.getStates().get(i);

                validateView(model, state);
                GlobalVariables.getMessageMap().merge(GlobalVariables.getMessageMap().getErrorMessages(),
                        GlobalVariables.getMessageMap().getWarningMessages());
                GlobalVariables.getMessageMap().clearErrorMessages();

                if (untilState != null && untilState.equals(state)) {
                    break;
                }
            }
            validateView(model, stateMapping.getCurrentState(object));
        } else {
            validateView(model, null);
        }

    }

    /**
     * @see ViewValidationService#validateView(ViewModel, String)
     */
    @Override
    public DictionaryValidationResult validateView(ViewModel model, String forcedValidationState) {
        // Get state object path from post data
        Object statePathObject = model.getViewPostMetadata().getComponentPostData(model.getViewPostMetadata().getId(),
                UifConstants.PostMetadata.STATE_OBJECT_BINDING_PATH);

        String path = null;
        if (statePathObject != null) {
             path = (String) statePathObject;
        }

        Object object;

        if (StringUtils.isNotBlank(path)) {
            object = ObjectPropertyUtils.getPropertyValue(model, path);
        } else {
            object = model;
        }

        String validationState = null;

        // Get state mapping for view from post data
        Object stateMappingObject = model.getViewPostMetadata().getComponentPostData(
                model.getViewPostMetadata().getId(), UifConstants.PostMetadata.STATE_MAPPING);

        StateMapping stateMapping = null;
        if (stateMappingObject != null) {
             stateMapping = (StateMapping) stateMappingObject;
        }

        if (StringUtils.isNotBlank(forcedValidationState)) {
            //use forced selected state if passed in
            validationState = forcedValidationState;
        } else if (stateMapping != null) {
            //default is current state
            validationState = stateMapping.getCurrentState(object);

        }

        return getDictionaryValidationService().validate(new ViewAttributeValueReader(model), true,
                validationState, stateMapping);
    }

    /**
     * @see ViewValidationService#validateViewAgainstNextState(ViewModel)
     */
    @Override
    public DictionaryValidationResult validateViewAgainstNextState(ViewModel model) {
        // Get state object path from post data
        Object statePathObject = model.getViewPostMetadata().getComponentPostData(model.getViewPostMetadata().getId(),
                UifConstants.PostMetadata.STATE_OBJECT_BINDING_PATH);

        String path = null;
        if (statePathObject != null) {
             path = (String) statePathObject;
        }

        Object object;

        if (StringUtils.isNotBlank(path)) {
            object = ObjectPropertyUtils.getPropertyValue(model, path);
        } else {
            object = model;
        }

        String validationState = null;

        // Get state mapping for view from post data
        Object stateMappingObject = model.getViewPostMetadata().getComponentPostData(
                model.getViewPostMetadata().getId(), UifConstants.PostMetadata.STATE_MAPPING);

        StateMapping stateMapping = null;
        if (stateMappingObject != null) {
             stateMapping = (StateMapping) stateMappingObject;
        }

        if (stateMapping != null) {
            //validation state is the next state for this call
            validationState = stateMapping.getNextState(object);
        }
        return getDictionaryValidationService().validate(new ViewAttributeValueReader(model), true,
                validationState, stateMapping);
    }

    /**
     * Gets the DictionaryValidationService to use for View validation
     *
     * @return DictionaryValidationService
     */
    public DictionaryValidationService getDictionaryValidationService() {
        if (dictionaryValidationService == null) {
            this.dictionaryValidationService = KRADServiceLocatorWeb.getDictionaryValidationService();
        }
        return dictionaryValidationService;
    }

    /**
     * Set the DictionaryValidationService
     *
     * @param dictionaryValidationService
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
}
