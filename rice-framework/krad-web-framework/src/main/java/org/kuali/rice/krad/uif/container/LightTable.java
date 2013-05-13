/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Image;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.uif.widget.RichTable;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LightTable is a light-weight collection table implementation that supports a subset of features.
 * Current known supported features are:
 * <ul>
 * <li>DataField</li>
 * <li>InputField with TextControl, CheckboxControl, or single SelectControl</li>
 * <li>MessageField</li>
 * <li>LinkField</li>
 * <li>ActionField</li>
 * <li>ImageField</li>
 * <li>most RichTable options</li>
 * <li>FieldGroup containing only Actions, Image, Messages, or Links</li>
 * <li>SpringEL for String properties on supported components only</li>
 * <li>SpringEL specifically for the render property</li>
 * </ul>
 *
 * Other features are not guaranteed to work, but may work at your own risk.  The intent of this table is to be a
 * light-weight alternative to the fully featured table already available in KRAD and it is more suited to displaying
 * large sets of simple data to the user.
 */
@BeanTags({@BeanTag(name = "lightTableGroup-bean", parent = "Uif-LightTableGroup"),
        @BeanTag(name = "lightTableSection-bean", parent = "Uif-LightTableSection"),
        @BeanTag(name = "lightTableSubSection-bean", parent = "Uif-LightTableSubSection")})
public class LightTable extends Group implements DataBinding {

    private static final String VALUE_TOKEN = "@v@";
    private static final String EXPRESSION_TOKEN = "@e@";
    private static final String RENDER = "render";
    private static final String ID_TOKEN = "@id@";
    private static final String A_TOKEN = "@";
    private static final String SEPARATOR = "@@@";

    private String propertyName;
    private BindingInfo bindingInfo;
    private List<Label> headerLabels;
    private RichTable richTable;
    private Map<String, String> expressionConversionMap;
    private List<String> initialComponentIds;
    private Map<String, String> renderIdExpressionMap;
    private boolean emptyTable;

    /**
     * LightTable constructor
     */
    public LightTable() {
        expressionConversionMap = new HashMap<String, String>();
        initialComponentIds = new ArrayList<String>();
        renderIdExpressionMap = new HashMap<String, String>();
    }

    /**
     * Initialization override that sets up DataField value placeholders for parsing and populates the
     * expressionConversionMap
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        //init binding info
        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }

        //iterate over this collections items to initialize
        for (Component item : this.getItems()) {
            initialComponentIds.add(item.getId());

            //if data field, setup a forced placeholder value
            if (item instanceof DataField) {
                ((DataField) item).setForcedValue(VALUE_TOKEN + item.getId() + VALUE_TOKEN);
            }

            ///populate expression map
            expressionConversionMap = buildExpressionMap(item, expressionConversionMap);
        }
    }

    /**
     * Builds the expression map which contains "propertyName@@@id" and the expression.  Also fills the
     * renderIdExpressionMap which contains all the component ids and expressions for render conditions, and overrides
     * ids with a placeholder id.  This method is recursive for child components which match certain supported types.
     *
     * @param item the item to iterate on
     * @param expressionMap the map holding the expressions for the items of this collection
     * @return the expressionMap with expressions populated
     */
    protected Map<String, String> buildExpressionMap(Component item, Map<String, String> expressionMap) {
        if (item == null) {
            return expressionMap;
        }

        List<String> toRemove = new ArrayList<String>();

        if (item.getExpressionGraph() != null && !item.getExpressionGraph().isEmpty()) {
            for (String name : item.getExpressionGraph().keySet()) {
                processExpression(name, item, expressionMap, toRemove);
            }
        }

        //id placeholder
        item.setId(ID_TOKEN + item.getId() + ID_TOKEN);

        if (item instanceof Group) {
            ((Group) item).getLayoutManager().setId(ID_TOKEN + ((Group) item).getLayoutManager().getId() + ID_TOKEN);
        }

        expressionMap = addChildExpressions(item.getComponentsForLifecycle(), expressionMap);

        for (String name : toRemove) {
            item.getExpressionGraph().remove(name);
        }

        return expressionMap;
    }

    /**
     * Process the expression for the item by putting placeholder values in for String properties and adding markers
     * for render expressions to the component; adds the original expression to the expressionMap
     *
     * @param name the property name
     * @param item the component this expressio is on
     * @param expressionMap the map to add expressions to
     * @param toRemove the property name is added this map to be removed later
     */
    public void processExpression(String name, Component item, Map<String, String> expressionMap,
            List<String> toRemove) {
        Class<?> clazz = ObjectPropertyUtils.getPropertyType(item, name);
        if (clazz.isAssignableFrom(String.class)) {
            //add expressions for string properties only
            expressionMap.put(name + SEPARATOR + item.getId(), item.getExpressionGraph().get(name));
            toRemove.add(name);
            ObjectPropertyUtils.setPropertyValue(item, name,
                    EXPRESSION_TOKEN + name + SEPARATOR + item.getId() + EXPRESSION_TOKEN);

        } else if (name.endsWith(RENDER) && clazz.isAssignableFrom(boolean.class)) {
            //setup render tokens to be able to determine where to remove content for render false, if needed
            Component renderComponent = item;

            //check for nested render (child element)
            if (!name.equals(RENDER)) {
                renderComponent = ObjectPropertyUtils.getPropertyValue(item, StringUtils.removeEnd(name, ".render"));
            }

            //add render expression to the map
            renderIdExpressionMap.put(renderComponent.getId(), item.getExpressionGraph().get(name));
            toRemove.add(name);

            String renderMarker = A_TOKEN + RENDER + A_TOKEN + renderComponent.getId() + A_TOKEN;

            //setup pre render content token
            String pre = renderComponent.getPreRenderContent() == null ? "" : renderComponent.getPreRenderContent();
            renderComponent.setPreRenderContent(renderMarker + pre);

            //setup post render content token
            String post = renderComponent.getPostRenderContent() == null ? "" : renderComponent.getPostRenderContent();
            renderComponent.setPostRenderContent(post + renderMarker);

            //force render to true
            ObjectPropertyUtils.setPropertyValue(item, name, true);
        }
    }

    /**
     * Add expressions to the expression map for nested components of specific types
     *
     * @param components the child components
     * @param expressionMap the map to add expressions to
     * @return the map with child component expressions added
     */
    protected Map<String, String> addChildExpressions(List<? extends Component> components,
            Map<String, String> expressionMap) {
        for (Component comp : components) {
            if (comp != null && (comp instanceof Action
                    || comp instanceof Image
                    || comp instanceof Message
                    || comp instanceof Link
                    || comp instanceof Inquiry
                    || comp instanceof Group
                    || comp instanceof Tooltip
                    || comp instanceof InputField
                    || comp instanceof CheckboxControl
                    || comp instanceof TextControl
                    || comp instanceof SelectControl)) {
                expressionMap = buildExpressionMap(comp, expressionMap);
            }
        }

        return expressionMap;
    }

    /**
     * performFinalize override corrects the binding path for the DataFields and turns off rendering on some components
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        headerLabels = new ArrayList<Label>();
        for (Component item : this.getItems()) {
            //get the header labels
            if (item instanceof Field) {
                headerLabels.add(ComponentUtils.copy(((Field) item).getFieldLabel()));
                ((Field) item).getFieldLabel().setRender(false);
            } else {
                headerLabels.add(null);
            }

            if (item instanceof FieldGroup) {
                ((FieldGroup) item).getGroup().setValidationMessages(null);

            }

            if (item instanceof DataField) {
                ((DataField) item).getBindingInfo().setBindByNamePrefix(this.getBindingInfo().getBindingPath() + "[0]");
            }
        }

        Object collectionValue = ObjectPropertyUtils.getPropertyValue(model, bindingInfo.getBindingPath());

        //set emptyTable true if null, empty, or not valid collection
        if (collectionValue == null || !(collectionValue instanceof Collection) ||
                ((Collection) collectionValue).isEmpty()) {
            emptyTable = true;
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(richTable);
        return components;
    }

    /**
     * Build the rows from the rowTemplate passed in.  This method uses regex to locate pieces of the row that need
     * to be replaced with row specific content per row.
     *
     * @param rowTemplate the first row of the collection in html generated from the ftl
     * @param model the model
     * @return the full set of rows for the table in html(String) to be used by the calling ftl
     */
    public String buildRows(String rowTemplate, UifFormBase model) {
        if (StringUtils.isBlank(rowTemplate)) {
            return "";
        }

        StringBuffer rows = new StringBuffer();
        List<Object> collectionObjects = ObjectPropertyUtils.getPropertyValue(model, bindingInfo.getBindingPath());

        //uncheck any checked checkboxes globally for this row
        rowTemplate = rowTemplate.replace("checked=\"checked\"", "");

        //init token patterns
        Pattern idPattern = Pattern.compile(ID_TOKEN + "(.*?)" + ID_TOKEN);
        Pattern expressionPattern = Pattern.compile(EXPRESSION_TOKEN + "(.*?)" + EXPRESSION_TOKEN);

        ExpressionEvaluatorService expressionEvaluatorService = KRADServiceLocatorWeb.getExpressionEvaluatorService();

        int i = 0;
        for (Object obj : collectionObjects) {
            //add line index to all ids
            String row = idPattern.matcher(rowTemplate).replaceAll("$1" + UifConstants.IdSuffixes.LINE + i);

            //create the expanded context
            Map<String, Object> expandedContext = new HashMap<String, Object>();
            expandedContext.put(UifConstants.ContextVariableNames.LINE, obj);
            expandedContext.put(UifConstants.ContextVariableNames.INDEX, i);

            int itemIndex = 0;
            for (Component item : this.getItems()) {
                //determine original id for this component
                String originalId = initialComponentIds.get(itemIndex);

                //special DataField handling
                row = handleDataFieldInRow(item, obj, row, i, originalId);

                //special InputField handling
                row = handleInputFieldInRow(item, obj, row, i, originalId);

                //add item context
                if (item.getContext() != null) {
                    expandedContext.putAll(item.getContext());
                }

                //evaluate expressions found by the pattern
                row = evaluateAndReplaceExpressionValues(row, i, model, expandedContext, expressionPattern,
                        expressionEvaluatorService);

                itemIndex++;
            }

            row = "<tr>" + row + "</tr>";

            //special render property expression handling
            row = evaluateRenderExpressions(row, i, model, expandedContext, expressionEvaluatorService);

            //append row
            rows.append("\n" + row);
            i++;
        }

        return rows.toString();
    }

    /**
     * Evaluate expressions and replace content found by the expressionPattern in the row
     *
     * @param row the row being modified
     * @param index the line index
     * @param model the model
     * @param expandedContext the context to evaluate expressions against
     * @param expressionPattern the expression pattern used to find expression tokens for value replacement
     * @param expressionEvaluatorService the expression service to use for evaluation
     * @return the modified row
     */
    private String evaluateAndReplaceExpressionValues(String row, int index, Object model,
            Map<String, Object> expandedContext, Pattern expressionPattern,
            ExpressionEvaluatorService expressionEvaluatorService) {

        Matcher matcher = expressionPattern.matcher(row);

        while (matcher.find()) {
            String matchingGroup = matcher.group(1);
            String expression = expressionConversionMap.get(matchingGroup);

            //adjust prefix for evaluation
            expression = expression.replace(UifConstants.LINE_PATH_BIND_ADJUST_PREFIX,
                    this.getBindingInfo().getBindingPath() + "[" + index + "].");

            //get expression result
            Object value = expressionEvaluatorService.evaluateExpressionTemplate(model, expandedContext, expression);

            if (value != null){
                row = row.replace(matcher.group(), value.toString());
            }
            else {
                row = row.replace(matcher.group(), "");
            }
        }

        return row;
    }

    /**
     * Evaluates the render expressions for the row and removes the content if render is evaluated false
     *
     * @param row the row being modified
     * @param index the line index
     * @param model the model
     * @param expandedContext the context to evaluate expressions against
     * @param expressionEvaluatorService the expression service to use for evaluation
     * @return the modified row
     */
    private String evaluateRenderExpressions(String row, int index, Object model, Map<String, Object> expandedContext,
            ExpressionEvaluatorService expressionEvaluatorService) {
        for (String id : renderIdExpressionMap.keySet()) {
            String expression = renderIdExpressionMap.get(id);

            //adjust prefix for evaluation
            expression = expression.replace(UifConstants.LINE_PATH_BIND_ADJUST_PREFIX,
                    this.getBindingInfo().getBindingPath() + "[" + index + "].");

            //get expression result
            Object value = expressionEvaluatorService.evaluateExpressionTemplate(model, expandedContext, expression);

            String wrap = A_TOKEN + RENDER + A_TOKEN + id + A_TOKEN;

            if (value != null && value instanceof String && Boolean.parseBoolean((String) value) == false) {
                //do not render this component - remove content between render wrappers
                row = row.replaceAll(wrap + "(.|\\s)*?" + wrap, "");
            } else {
                //remove render wrappers only - keep content
                row = row.replaceAll(wrap, "");
            }
        }

        return row;
    }

    /**
     * Special handling of the DataField in the row, replaces necessary content with row specific content
     *
     * @param item the item being processed
     * @param obj the row's object model
     * @param row the row in html
     * @param index the current row index
     * @param originalId the original id of the component item
     * @return the updated row
     */
    private String handleDataFieldInRow(Component item, Object obj, String row, int index, String originalId) {
        if (!(item instanceof DataField)) {
            return row;
        }

        Object currentValue = ObjectPropertyUtils.getPropertyValue(obj, ((DataField) item).getPropertyName());

        if (currentValue == null) {
            currentValue = "";
        }

        //for readOnly DataFields replace the value marked with the value on the current object
        row = row.replaceAll(VALUE_TOKEN + originalId + VALUE_TOKEN, currentValue.toString());

        if (((DataField) item).getInquiry() != null
                && ((DataField) item).getInquiry().getInquiryParameters() != null
                && ((DataField) item).getInquiry().getInquiryLink() != null) {

            String inquiryLinkId = ((DataField) item).getInquiry().getInquiryLink().getBaseId().replace(ID_TOKEN, "")
                    + UifConstants.IdSuffixes.LINE
                    + index;

            //process each Inquiry link parameter by replacing each in the inquiry url with their
            //current value
            for (String key : ((DataField) item).getInquiry().getInquiryParameters().keySet()) {
                String name = ((DataField) item).getInquiry().getInquiryParameters().get(key);

                //omit the binding prefix fromt he key to get the path relative to the current object
                key = key.replace(((DataField) item).getBindingInfo().getBindByNamePrefix() + ".", "");

                if (ObjectPropertyUtils.isReadableProperty(obj, key)) {
                    String value = ObjectPropertyUtils.getPropertyValue(obj, key);
                    row = row.replaceFirst("(" + inquiryLinkId + "(.|\\s)*?" + name + ")=.*?([&|\"])",
                            "$1=" + value + "$3");
                }

            }
        }

        return row;
    }

    /**
     * Special handling of the InputField in the row, replaces necessary content with row specific content
     *
     * @param item the item being processed
     * @param obj the row's object model
     * @param row the row in html
     * @param index the current row index
     * @param originalId the original id of the component item
     * @return the updated row
     */
    private String handleInputFieldInRow(Component item, Object obj, String row, int index, String originalId) {
        if (!(item instanceof InputField) || ((InputField) item).getControl() == null) {
            return row;
        }

        Control control = ((InputField) item).getControl();

        //updates the name path to the current path for any instance this item's propertyName with
        //a collection binding prefix
        row = row.replace("name=\"" + ((InputField) item).getBindingInfo().getBindingPath() + "\"",
                "name=\"" + this.getBindingInfo().getBindingPath() + "[" + index + "]." + ((InputField) item)
                        .getPropertyName() + "\"");

        Object value = ObjectPropertyUtils.getPropertyValue(obj, ((InputField) item).getPropertyName());
        String stringValue = "";

        if (value == null) {
            stringValue = "";
        } else if (value.getClass().isAssignableFrom(boolean.class)) {
            stringValue = "" + value;
        } else if (!(value instanceof Collection)) {
            stringValue = value.toString();
        }

        String controlId = originalId + "_line" + index + UifConstants.IdSuffixes.CONTROL;

        if (control instanceof CheckboxControl && stringValue.equalsIgnoreCase("true")) {
            //CheckboxControl handling - only replace if true with a checked attribute appended
            row = row.replaceAll("(id(\\s)*?=(\\s)*?\"" + controlId + "\")", "$1 checked=\"checked\" ");
        } else if (control instanceof TextControl) {
            //TextControl handling - replace with
            row = row.replaceAll("(id(\\s)*?=(\\s)*?\"" + controlId + "\"(.|\\s)*?value=\")(.|\\s)*?\"",
                    "$1" + stringValue + "\"");
        } else if (control instanceof SelectControl && !((SelectControl) control).isMultiple()) {
            //SelectControl handling (single item only)
            Pattern pattern = Pattern.compile("<select(\\s)*?id(\\s)*?=(\\s)*?\"" + controlId + "\"(.|\\s)*?</select>");
            Matcher matcher = pattern.matcher(row);
            String replacement = "";

            if (matcher.find()) {
                //remove selected from select options
                String selected = "selected=\"selected\"";
                replacement = matcher.group().replace(selected, "");

                //put selected on only the selected option
                String selectedValue = "value=\"" + stringValue + "\"";
                replacement = replacement.replace(selectedValue, selectedValue + " " + selected);
            }

            //replace the old select tag with the old one
            if (StringUtils.isNotBlank(replacement)) {
                row = matcher.replaceAll(replacement);
            }
        }

        return row;
    }

    /**
     * The propertyName of the list backing this collection
     *
     * @return the propertyName of this collection
     */
    @BeanTagAttribute(name = "propertyName")
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Set the propertyName
     *
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * The bindingInfo for this collection table, containg the property path and other options
     *
     * @return the bindingInfo
     */
    @BeanTagAttribute(name = "bindingInfo", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BindingInfo getBindingInfo() {
        return bindingInfo;
    }

    /**
     * Set the bindingInfo
     *
     * @param bindingInfo
     */
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * The labels for the header derived from the items of this collection (the fields)
     *
     * @return the header labels
     */
    public List<Label> getHeaderLabels() {
        return headerLabels;
    }

    /**
     * The richTable widget definition for this table for setting dataTable javascript options
     *
     * @return the RichTable widget
     */
    @BeanTagAttribute(name = "richTable", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public RichTable getRichTable() {
        return richTable;
    }

    /**
     * Set the richTable widget
     *
     * @param richTable
     */
    public void setRichTable(RichTable richTable) {
        this.richTable = richTable;
    }

    /**
     * True if this table is empty, false otherwise
     *
     * @return true if the collection backing this table is empty
     */
    public boolean isEmptyTable() {
        return emptyTable;
    }
}
