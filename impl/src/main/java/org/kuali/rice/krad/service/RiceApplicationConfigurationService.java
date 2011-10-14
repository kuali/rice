/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.service;

import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

import java.util.List;

/**
 * This interface defines methods that a KualiConfiguration Service must provide. Provides methods for getting string
 * resources.
 */
public interface RiceApplicationConfigurationService {

    /**
     * This method can be used to supplement the list of ParameterDetailTypes defined in the database from other sources.
     * 
     * @return List<ParameterDetailedType> containing the detailed types configured in non-database sources
     */
    List<Component> getNonDatabaseComponents();

}
