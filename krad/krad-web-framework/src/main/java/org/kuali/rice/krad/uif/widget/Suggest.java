/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.AttributeQuery;

/**
 * Widget that provides dynamic select options to the user as they
 * are entering the value (also known as auto-complete)
 *
 * <p>
 * Widget is backed by an <code>AttributeQuery</code> that provides
 * the configuration for executing a query server side that will retrieve
 * the valid option values.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Suggest extends WidgetBase {
    private static final long serialVersionUID = 7373706855319347225L;

    private AttributeQuery suggestQuery;
    private String sourcePropertyName;

    public Suggest() {
        super();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Adjusts the query field mappings on the query based on the binding configuration of the field</li>
     * <li>TODO: determine query if render is true and query is not set</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (!isRender()) {
            return;
        }

        InputField field = (InputField) parent;
        BindingInfo bindingInfo = field.getBindingInfo();

        // adjust from side on query field mapping to match parent fields path
        suggestQuery.updateQueryFieldMapping(bindingInfo);
    }

    /**
     * Attribute query instance the will be executed to provide
     * the suggest options
     *
     * @return AttributeQuery
     */
    public AttributeQuery getSuggestQuery() {
        return suggestQuery;
    }

    /**
     * Setter for the suggest attribute query
     *
     * @param suggestQuery
     */
    public void setSuggestQuery(AttributeQuery suggestQuery) {
        this.suggestQuery = suggestQuery;
    }

    /**
     * Name of the property on the query result object that provides
     * the options for the suggest, values from this field will be
     * collected and sent back on the result to provide as suggest options
     *
     * @return String source property name
     */
    public String getSourcePropertyName() {
        return sourcePropertyName;
    }

    /**
     * Setter for the source property name
     *
     * @param sourcePropertyName
     */
    public void setSourcePropertyName(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName;
    }
}
