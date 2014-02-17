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
package org.kuali.rice.krad.datadictionary.uif;

import org.kuali.rice.krad.datadictionary.DictionaryBeanBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Common base class for dictionary objects that can contain dynamic expressions within the
 * property value
 *
 * <p>
 * Should be extended by other classes to provide property expression support
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDictionaryBeanBase extends DictionaryBeanBase implements UifDictionaryBean {

    private Map<String, String> expressionGraph;
    private Map<String, String> refreshExpressionGraph;
    private Map<String, String> propertyExpressions;

    public UifDictionaryBeanBase() {
        expressionGraph = new HashMap<String, String>();
        refreshExpressionGraph = new HashMap<String, String>();
        propertyExpressions = new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getExpressionGraph() {
        return expressionGraph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExpressionGraph(Map<String, String> expressionGraph) {
        this.expressionGraph = expressionGraph;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#getPropertyExpressions
     */
    public Map<String, String> getPropertyExpressions() {
        return propertyExpressions;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#setPropertyExpressions
     */
    public void setPropertyExpressions(Map<String, String> propertyExpressions) {
        this.propertyExpressions = propertyExpressions;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#getPropertyExpression
     */
    public String getPropertyExpression(String propertyName) {
        if (this.propertyExpressions.containsKey(propertyName)) {
            return this.propertyExpressions.get(propertyName);
        }

        return null;
    }
}
