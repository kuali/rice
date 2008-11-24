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
import java.util.Map;
import java.util.Properties;

import org.kuali.rice.kns.bo.Parameter;

/**
 * This interface defines methods that a KualiConfiguration Service must provide. Provides methods for getting string
 * resources.
 */
public interface KualiConfigurationService {
    /**
         * Given a property name (key), returns the value associated with that key, or null if none is available.
         * 
         * @param key
         * @return String associated with the given key
         * @throws IllegalArgumentException
         *                 if the key is null
         */
    public String getPropertyString(String key);

    /**
         * Given a property name (key), returns the "booleanized" value associated with that key.
         * 
         * true, yes, on, or 1 are translated into <b>true</b> - all other values result in <b>false</b>
         * 
         * @param key
         * @return String associated with the given key
         * @throws IllegalArgumentException
         *                 if the key is null
         */
    public boolean getPropertyAsBoolean(String key);

    /**
         * @return Properties instance containing all (key,value) pairs known to the service
         */
    public Properties getAllProperties();

    /**
         * Returns whether this instance is production based on the configuration options.
         */
    public boolean isProductionEnvironment();

    /**
     * This method retrieves a parameter based on the primary key
     */
    public Parameter getParameter(String namespaceCode, String detailTypeCode, String parameterName);

    /**
     * This method retrieves a parameter based on the primary key.  Unlike {@link #getParameter(String, String, String)},
     * this method does not throw an exception if the parameter cannot be found.  It instead returns null.
     */
    public Parameter getParameterWithoutExceptions(String namespaceCode, String detailTypeCode, String parameterName);
    
    /**
     * This method retrieves a set of parameters based on arbitraty criteria
     */
    public List<Parameter> getParameters(Map<String, String> criteria);

    /**
     * This method retrieves a parameter expected to have a Yes / no value and converts to a boolean for convenience
     */
    public boolean getIndicatorParameter(String namespaceCode, String detailTypeCode, String parameterName);

    /**
     * This method returns a list of the parameter values split on implementation specific criteria.
     * For the default KualiConfigurationServiceImpl, the split is on a semi-colon.
     */
    public List<String> getParameterValues(String namespaceCode, String detailTypeCode, String parameterName);

    /**
     * This method returns the value of the specified parameter
     */
    public String getParameterValue(String namespaceCode, String detailTypeCode, String parameterName);

    /**
     * This method determines whether the parameter values list constains the specified constrainedValue
     */
    public boolean evaluateConstrainedValue(String namespaceCode, String detailTypeCode, String parameterName,
	    String constrainedValue);
}