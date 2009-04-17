/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kns.bo.ParameterDetailType;

/**
 * This interface defines methods that a KualiConfiguration Service must provide. Provides methods for getting string
 * resources.
 */
public interface RiceApplicationConfigurationService {
    /**
     * Given a property name (parameterName), returns the value associated with it.
     * 
     * @param parameterName
     * @return String associated with the given parameterName
     */
    public String getConfigurationParameter( String parameterName ); 
    
    /**
     * This method can be used to supplement the list of ParameterDetailTypes defined in the database from other sources.
     * 
     * @return List<ParameterDetailedType> containing the detailed types configured in non-database sources
     */
    public List<ParameterDetailType> getNonDatabaseComponents();
}