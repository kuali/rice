/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Validation service for KRAD views.  The ViewValidationService uses the DictionaryValidationService to validate the
 * fields of the View by using the constraints that were set at either the InputField or AttributeDefinition level for
 * that field.  If errors/warnings are found they are added to the messageMap and when the view is returned these
 * messages are displayed.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewValidationService {

    /**
     * This is the main validation method that should be used when validating Views
     * Validates the view based on the model passed in, this will correctly use previousView by default
     * as it automatically contains the generated data the validation requires. Validates against the current state if
     * state based validation
     * is setup.
     *
     * @param model the model which contains the values (and View) to validated
     * @return DictionaryValidationResult that contains any errors/messages if any, messages will have already
     *         been added to the MessageMap
     */
    public DictionaryValidationResult validateView(ViewModel model);


    /**
     * Validate the view against the specific validationState instead of the default (current state).  If
     * forcedValidationState
     * is null, validates against the current state if state validation is setup.
     *
     * @param model the model which contains the values to validated
     * @param forcedValidationState the state being validated against
     * @return that contains any errors/messages if any,, messages will have already
     *         been added to the MessageMap
     */
    public DictionaryValidationResult validateView(ViewModel model, String forcedValidationState);

    /**
     * Validate the view against the next state based on the order of the states in the views StateMapping.  This
     * will validate against current state + 1.  If there is no next state, this will validate against the current
     * state.
     *
     * @param model the model which contains the values to validated
     * @return that contains any errors/messages if any,, messages will have already
     *         been added to the MessageMap
     */
    public DictionaryValidationResult validateViewAgainstNextState(ViewModel model);

    /**
     * Simulate view validation - this will run all validations against all states from the current state to
     * the last state in the list of states in the view's stateMapping.  Validation errors received for the current
     * state will be added as errors to the MessageMap. Validation errors for future states will be warnings.
     *
     * @param model the model which contains the values to validated
     */
    public void validateViewSimulation(ViewModel model);

    /**
     * Simulate view validation - this will run all validations against all states from the current state to
     * the untilState specified in the list of states in the view's stateMapping.  Validation errors received for the
     * current state will be added as errors to the MessageMap. Validation errors for future states will be warnings.
     *
     * @param model the model which contains the values to validated
     * @param untilState state to perform simulation to, if not set performs simulation up to the last state
     */
    public void validateViewSimulation(ViewModel model, String untilState);

}