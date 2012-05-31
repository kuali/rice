/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import org.kuali.rice.kns.bo.State;

import java.util.List;

public interface StateService {

    /**
     * get a state object based on the given state code and default country code. The default country code is set up in the system.
     * 
     * @param postalStateCode the given state code
     * @return a state object based on the given state code and default country code
     */
    public State getByPrimaryId(String postalStateCode);

    /**
     * get a state object based on the given state code and country code
     * 
     * @param postalCountryCode the given country code
     * @param postalStateCode the given state code
     * @return a state object based on the given state code and country code
     */
    public State getByPrimaryId(String postalCountryCode, String postalStateCode);

    /**
     * get a state object based on the given state code and default country code. The default country code is set up in the system. The default country code is set up in
     * the system. If the given postal state code is same as that of the given existing postal code, return the existing postal code;
     * otherwise, retrieve a state object.
     * 
     * @param postalStateCode the given state code
     * @return a state object based on the given state code and default country code
     */
    public State getByPrimaryIdIfNecessary(String postalStateCode, State existingState);

    /**
     * get a state object based on the given state code and country code. If the given postal state code and country code
     * are same as those of the given existing postal code, return the existing State; otherwise, retrieve a State
     * object.
     * 
     * @param postalCountryCode the given country code
     * @param postalStateCode the given state code
     * @return a state object based on the given state code and country code
     */
    public State getByPrimaryIdIfNecessary(String postalCountryCode, String postalStateCode, State existingState);
    
    /**
     * get all states in the system-default country 
     * @return all states in the system-default country 
     */
    public List<State> findAllStates();
    
    /**
     * get all states in the given country 
     * @param postalCountryCode the given country code
     * @return all states in the given country 
     */
    public List<State> findAllStates(String postalCountryCode);

    /*
    * get all states by the alternative postal country code
    * @param alternatePostalCountryCode the alternative postal country code
    * @return all states within the given alternative postal country code
    */
    public List<State> findAllStatesByAltCountryCode(String alternatePostalCountryCode);
}
