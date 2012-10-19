/**
 * Copyright 2005-2012 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.AttributeQuery;

import java.util.List;

/**
 * Widget that provides dynamic select options to the user as they
 * are entering the value (also known as auto-complete)
 *
 * <p>
 * Widget is backed by an <code>AttributeQuery</code> that provides
 * the configuration for executing a query server side that will retrieve
 * the valid option values
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name="suggest")
public class Suggest extends WidgetBase {
    private static final long serialVersionUID = 7373706855319347225L;

    private AttributeQuery suggestQuery;

    private String sourcePropertyName;
    private boolean sourceQueryMethodResults;

    private boolean retrieveAllSuggestions;
    private List<Object> suggestOptions;

    private String suggestOptionsJsString;

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

        // if source property name or query method or options not set then we can't render the Suggest widget
        if (StringUtils.isBlank(sourcePropertyName) &&
                !suggestQuery.hasConfiguredMethod() &&
                (suggestOptions == null || suggestOptions.isEmpty())) {
            setRender(false);
        }

        if (!isRender()) {
            return;
        }

        if (retrieveAllSuggestions) {
            if (suggestOptions == null || suggestOptions.isEmpty()) {
                // execute query method to retrieve up front suggestions
                if (suggestQuery.hasConfiguredMethod()) {
                    retrieveSuggestOptions(view);
                }
            } else {
                suggestOptionsJsString = ScriptUtils.translateValue(suggestOptions);
            }
        } else {
            // adjust from side on query field mapping to match parent fields path
            InputField field = (InputField) parent;

            BindingInfo bindingInfo = field.getBindingInfo();
            suggestQuery.updateQueryFieldMapping(bindingInfo);
        }
    }

    /**
     * Invokes the configured query method and sets the returned method value as the suggest options or
     * suggest options JS string
     *
     * @param view view instance the suggest belongs to, used to get the view helper service if needed
     */
    protected void retrieveSuggestOptions(View view) {
        String queryMethodToCall = suggestQuery.getQueryMethodToCall();
        MethodInvokerConfig queryMethodInvoker = suggestQuery.getQueryMethodInvokerConfig();

        if (queryMethodInvoker == null) {
            queryMethodInvoker = new MethodInvokerConfig();
        }

        // if method not set on invoker, use queryMethodToCall, note staticMethod could be set(don't know since
        // there is not a getter), if so it will override the target method in prepare
        if (StringUtils.isBlank(queryMethodInvoker.getTargetMethod())) {
            queryMethodInvoker.setTargetMethod(queryMethodToCall);
        }

        // if target class or object not set, use view helper service
        if ((queryMethodInvoker.getTargetClass() == null) && (queryMethodInvoker.getTargetObject() == null)) {
            queryMethodInvoker.setTargetObject(view.getViewHelperService());
        }

        try {
            queryMethodInvoker.prepare();

            Object methodResult = queryMethodInvoker.invoke();
            if (methodResult instanceof String) {
                suggestOptionsJsString = (String) methodResult;
            } else if (methodResult instanceof List) {
                suggestOptions = (List<Object>) methodResult;
                suggestOptionsJsString = ScriptUtils.translateValue(suggestOptions);
            } else {
                throw new RuntimeException("Suggest query method did not return List<String> for suggestions");
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke query method: " + queryMethodInvoker.getTargetMethod(), e);
        }
    }

    /**
     * Attribute query instance the will be executed to provide
     * the suggest options
     *
     * @return AttributeQuery
     */
    @BeanTagAttribute(name="suggestQuery",type = BeanTagAttribute.AttributeType.SINGLEBEAN)
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
    @BeanTagAttribute(name="sourcePropertyName")
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

    /**
     * When set to true the results of a query method will be used directly as the suggestions (
     * it will not be assumed the method returns objects from which the source property name is then used
     * to pull out the suggestions)
     *
     * <p>
     * Note this is not supported for auto queries (only custom method queries). The query method can return
     * a list of Strings which will be used for the suggestions, a list of object with 'label' and 'value' properties,
     * or a custom object (if the plugin has been customized to handle the object)
     * </p>
     *
     * @return boolean true if the query method results should be used as the suggestions, false to assume
     * objects are returned and suggestions are formed using the source property name
     */
    @BeanTagAttribute(name="sourceQueryMethodResults")
    public boolean isSourceQueryMethodResults() {
        return sourceQueryMethodResults;
    }

    /**
     * Setter for the source query method results indicator
     *
     * @param sourceQueryMethodResults
     */
    public void setSourceQueryMethodResults(boolean sourceQueryMethodResults) {
        this.sourceQueryMethodResults = sourceQueryMethodResults;
    }

    /**
     * Indicates whether all suggest options should be retrieved up front and provide to the suggest
     * widget as options locally
     *
     * <p>
     * Use this for a small list of options to improve performance. The query will be performed on the client
     * to filter the provider options based on the users input instead of doing a query each time
     * </p>
     *
     * <p>
     * When a query method is configured and this option set to true the method will be invoked to set the
     * options. The query method should not take any arguments and should return the suggestion options
     * List or the JS String as a result. If a query method is not configured the suggest options can be
     * set through configuration or a view helper method (for example a component finalize method)
     * </p>
     *
     * @return boolean true to provide the suggest options initially, false to use ajax retrieval based on the
     * user's input
     */
    @BeanTagAttribute(name="retrieveAllSuggestions")
    public boolean isRetrieveAllSuggestions() {
        return retrieveAllSuggestions;
    }

    /**
     * Setter for the retrieve all suggestions indicator
     *
     * @param retrieveAllSuggestions
     */
    public void setRetrieveAllSuggestions(boolean retrieveAllSuggestions) {
        this.retrieveAllSuggestions = retrieveAllSuggestions;
    }

    /**
     * When {@link #isRetrieveAllSuggestions()} is true, this list provides the full list of suggestions
     *
     * <p>
     * If a query method is configured that method will be invoked to populate this list, otherwise the
     * list should be populated through configuration or the view helper
     * </p>
     *
     * <p>
     * The suggest options can either be a list of Strings, in which case the strings will be the suggested
     * values. Or a list of objects. If the object does not have 'label' and 'value' properties, a custom render
     * and select method must be provided
     * </p>
     *
     * @return List<Object> list of suggest options
     */
    @BeanTagAttribute(name="SuggestOptions",type= BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Object> getSuggestOptions() {
        return suggestOptions;
    }

    /**
     * Setter for the list of suggest options
     *
     * @param suggestOptions
     */
    public void setSuggestOptions(List<Object> suggestOptions) {
        this.suggestOptions = suggestOptions;
    }

    /**
     * Returns the suggest options as a JS String (set by the framework from method invocation)
     *
     * @return String suggest options JS string
     */
    public String getSuggestOptionsJsString() {
        if (StringUtils.isNotBlank(suggestOptionsJsString)) {
            return this.suggestOptionsJsString;
        }

        return "null";
    }
}
