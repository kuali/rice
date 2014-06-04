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
package org.kuali.rice.krad.uif.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Header;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.SpaceField;
import org.kuali.rice.krad.uif.layout.GridLayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;

/**
 * Generates <code>Field</code> instances to produce a comparison view among
 * objects of the same type
 *
 * <p>
 * Modifier is initialized with a List of <code>ComparableInfo</code> instances.
 * For each comparable info, a copy of the configured group field is made and
 * adjusted to the binding object path for the comparable. The comparison fields
 * are ordered based on the configured order property of the comparable. In
 * addition, a <code>HeaderField<code> can be generated to label each group
 * of comparison fields.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "compareFieldCreateModifier", parent = "Uif-CompareFieldCreate-Modifier"),
        @BeanTag(name = "maintenanceCompareModifier", parent = "Uif-MaintenanceCompare-Modifier")})
public class CompareFieldCreateModifier extends ComponentModifierBase {
    private static final long serialVersionUID = -6285531580512330188L;

    private int defaultOrderSequence;
    private boolean generateCompareHeaders;

    private Header headerFieldPrototype;
    private List<ComparableInfo> comparables;

    public CompareFieldCreateModifier() {
        defaultOrderSequence = 1;
        generateCompareHeaders = true;

        comparables = new ArrayList<ComparableInfo>();
    }

    /**
     * Calls <code>ViewHelperService</code> to initialize the header field prototype
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model, Component component) {
        super.performInitialization(model, component);

        if ((component != null) && !(component instanceof Group)) {
            throw new IllegalArgumentException(
                    "Compare field initializer only support Group components, found type: " + component.getClass());
        }

        if (component == null) {
            return;
        }

        Group group = (Group) component;

        // add the renderOnComparableModifier to allow for optional field rendering based on the comparable
        for (Component item : group.getItems()) {
            item.pushObjectToContext("renderOnComparableModifier", Boolean.TRUE);
        }
    }

    /**
     * Generates the comparison fields
     *
     * <p>
     * First the configured List of ComparableInfo instances are
     * sorted based on their order property. Then if generateCompareHeaders is
     * set to true, a HeaderField is created for each comparable
     * using the headerFieldPrototype and the headerText given by the
     * comparable. Finally for each field configured on the Group,
     * a corresponding comparison field is generated for each comparable and
     * adjusted to the binding object path given by the comparable in addition
     * to suffixing the id and setting the readOnly property
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void performModification(Object model, Component component) {
        if ((component != null) && !(component instanceof Group)) {
            throw new IllegalArgumentException(
                    "Compare field initializer only support Group components, found type: " + component.getClass());
        }

        if (component == null) {
            return;
        }
        
        Group group = (Group) component;

        // list to hold the generated compare items
        List<Component> comparisonItems = new ArrayList<Component>();

        // sort comparables by their order property
        List<ComparableInfo> groupComparables = ComponentUtils.sort(comparables, defaultOrderSequence);

        // evaluate expressions on comparables
        Map<String, Object> context = new HashMap<String, Object>();
        
        View view = ViewLifecycle.getView();
        
        Map<String, Object> viewContext = view.getContext();
        if (viewContext != null) {
            context.putAll(view.getContext());
        }

        context.put(UifConstants.ContextVariableNames.COMPONENT, component);

        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        for (ComparableInfo comparable : groupComparables) {
            expressionEvaluator.evaluateExpressionsOnConfigurable(view, comparable, context);
        }

        // generate compare header
        if (isGenerateCompareHeaders()) {
            // add space field for label column
            SpaceField spaceField = ComponentFactory.getSpaceField();
            comparisonItems.add(spaceField);

            for (ComparableInfo comparable : groupComparables) {
                Header compareHeaderField = ComponentUtils.copy(headerFieldPrototype, comparable.getComparableId());
                compareHeaderField.setHeaderText(comparable.getHeaderText());
                comparisonItems.add(compareHeaderField);
            }
            
            // if group is using grid layout, make first row a header
            if (group.getLayoutManager() instanceof GridLayoutManager) {
                ((GridLayoutManager) group.getLayoutManager()).setRenderFirstRowHeader(true);
            }
        }

        // find the comparable to use for comparing value changes (if configured)
        boolean performValueChangeComparison = false;
        String compareValueObjectBindingPath = null;
        for (ComparableInfo comparable : groupComparables) {
            if (comparable.isCompareToForValueChange()) {
                performValueChangeComparison = true;
                compareValueObjectBindingPath = comparable.getBindingObjectPath();
            }
        }

        // generate the compare items from the configured group
        boolean changeIconShowedOnHeader = false;
        for (Component item : group.getItems()) {
            int defaultSuffix = 0;
            boolean suppressLabel = false;

            for (ComparableInfo comparable : groupComparables) {
                String comparableId = comparable.getComparableId();
                if (StringUtils.isBlank(comparableId)) {
                    comparableId = UifConstants.IdSuffixes.COMPARE + defaultSuffix;
                }

                Component compareItem = ComponentUtils.copy(item, comparableId);

                ComponentUtils.setComponentPropertyDeep(compareItem, UifPropertyPaths.BIND_OBJECT_PATH,
                        comparable.getBindingObjectPath());
                if (comparable.isReadOnly()) {
                    compareItem.setReadOnly(true);
                    if (compareItem.getPropertyExpressions().containsKey("readOnly")) {
                        compareItem.getPropertyExpressions().remove("readOnly");
                    }
                }

                // label will be enabled for first comparable only
                if (suppressLabel && (compareItem instanceof Field)) {
                    ((Field) compareItem).getFieldLabel().setRender(false);
                }

                // add the renderOnComparableModifier to allow for optional field rendering based on the comparable
                compareItem.pushObjectToContext("renderOnComparableModifier", comparable.isCompareToForFieldRender());

                // do value comparison
                if (performValueChangeComparison && comparable.isHighlightValueChange() && !comparable
                        .isCompareToForValueChange()) {
                    boolean valueChanged = performValueComparison(group, compareItem, model,
                            compareValueObjectBindingPath);

                    // add icon to group header if not done so yet
                    if (valueChanged && !changeIconShowedOnHeader && isGenerateCompareHeaders()) {
                        Group groupToSetHeader = null;
                        if (group.getDisclosure() != null && group.getDisclosure().isRender()) {
                            groupToSetHeader = group;
                        } else if (group.getContext().get(UifConstants.ContextVariableNames.PARENT) != null) {
                            // use the parent group to set the notification if available
                            groupToSetHeader = (Group) group.getContext().get(UifConstants.ContextVariableNames.PARENT);
                        }

                        if (groupToSetHeader.getDisclosure().isRender()) {
                            groupToSetHeader.getDisclosure().setOnDocumentReadyScript(
                                    "showChangeIconOnDisclosure('" + groupToSetHeader.getId() + "');");
                        } else if (groupToSetHeader.getHeader() != null) {
                            groupToSetHeader.getHeader().setOnDocumentReadyScript(
                                    "showChangeIconOnHeader('" + groupToSetHeader.getHeader().getId() + "');");
                        }

                        changeIconShowedOnHeader = true;
                    }
                }

                comparisonItems.add(compareItem);

                defaultSuffix++;

                suppressLabel = true;
            }
        }
        
        // update the group's list of components
        group.setItems(comparisonItems);
    }

    /**
     * For each attribute field in the compare item, retrieves the field value and compares against the value for the
     * main comparable. If the value is different, adds script to the field on ready event to add the change icon to
     * the field and the containing group header
     *
     * @param group group that contains the item and whose header will be highlighted for changes
     * @param compareItem the compare item being generated and to pull attribute fields from
     * @param model object containing the data
     * @param compareValueObjectBindingPath object path for the comparison item
     * @return true if the value in the field represented by compareItem is equal to the comparison items value, false
     *         otherwise
     */
    protected boolean performValueComparison(Group group, Component compareItem, Object model,
            String compareValueObjectBindingPath) {
        // get any attribute fields for the item so we can compare the values
        List<DataField> itemFields = ViewLifecycleUtils.getElementsOfTypeDeep(compareItem, DataField.class);
        boolean valueChanged = false;
        for (DataField field : itemFields) {
            String fieldBindingPath = field.getBindingInfo().getBindingPath();
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, fieldBindingPath);

            String compareBindingPath = StringUtils.replaceOnce(fieldBindingPath,
                    field.getBindingInfo().getBindingObjectPath(), compareValueObjectBindingPath);
            Object compareValue = ObjectPropertyUtils.getPropertyValue(model, compareBindingPath);

            if (!((fieldValue == null) && (compareValue == null))) {
                // if one is null then value changed
                if ((fieldValue == null) || (compareValue == null)) {
                    valueChanged = true;
                } else {
                    // both not null, compare values
                    valueChanged = !fieldValue.equals(compareValue);
                }
            }
            if (valueChanged) {
                // add script to show change icon
                String onReadyScript = "showChangeIcon('" + field.getId() + "');";
                field.setRenderMarkerIconSpan(true);
                field.setOnDocumentReadyScript(onReadyScript);
            }
            // TODO: add script for value changed?
        }
        return valueChanged;
    }

    /**
     * Generates an comparableId suffix for the comparable item
     *
     * <p>
     * If the comparableId to use if configured on the ComparableInfo
     * it will be used, else the given integer index will be used with an
     * underscore
     * </p>
     *
     * @param comparable comparable info to check for id suffix
     * @param index sequence integer
     * @return id suffix
     * @see org.kuali.rice.krad.uif.modifier.ComparableInfo#getComparableId()
     */
    protected String getComparableId(ComparableInfo comparable, int index) {
        String comparableId = comparable.getComparableId();
        if (StringUtils.isBlank(comparableId)) {
            comparableId = "_" + index;
        }

        return comparableId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> components = new HashSet<Class<? extends Component>>();
        components.add(Group.class);

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.modifier.ComponentModifierBase#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        components.add(headerFieldPrototype);

        return components;
    }

    /**
     * Indicates the starting integer sequence value to use for
     * <code>ComparableInfo</code> instances that do not have the order property
     * set
     *
     * @return default sequence starting value
     */
    @BeanTagAttribute
    public int getDefaultOrderSequence() {
        return this.defaultOrderSequence;
    }

    /**
     * Setter for the default sequence starting value
     *
     * @param defaultOrderSequence
     */
    public void setDefaultOrderSequence(int defaultOrderSequence) {
        this.defaultOrderSequence = defaultOrderSequence;
    }

    /**
     * Indicates whether a <code>HeaderField</code> should be created for each
     * group of comparison fields
     *
     * <p>
     * If set to true, for each group of comparison fields a header field will
     * be created using the headerFieldPrototype configured on the modifier with
     * the headerText property of the comparable
     * </p>
     *
     * @return true if the headers should be created, false if no
     *         headers should be created
     */
    @BeanTagAttribute
    public boolean isGenerateCompareHeaders() {
        return this.generateCompareHeaders;
    }

    /**
     * Setter for the generate comparison headers indicator
     *
     * @param generateCompareHeaders
     */
    public void setGenerateCompareHeaders(boolean generateCompareHeaders) {
        this.generateCompareHeaders = generateCompareHeaders;
    }

    /**
     * Prototype instance to use for creating the <code>HeaderField</code> for
     * each group of comparison fields (if generateCompareHeaders is true)
     *
     * @return header field prototype
     */
    @BeanTagAttribute
    public Header getHeaderFieldPrototype() {
        return this.headerFieldPrototype;
    }

    /**
     * Setter for the header field prototype
     *
     * @param headerFieldPrototype
     */
    public void setHeaderFieldPrototype(Header headerFieldPrototype) {
        this.headerFieldPrototype = headerFieldPrototype;
    }

    /**
     * List of <code>ComparableInfo</code> instances the compare fields should
     * be generated for
     *
     * <p>
     * For each comparable, a copy of the fields configured for the
     * <code>Group</code> will be created for the comparison view
     * </p>
     *
     * @return comparables to generate fields for
     */
    @BeanTagAttribute
    public List<ComparableInfo> getComparables() {
        return this.comparables;
    }

    /**
     * Setter for the list of comparable info instances
     *
     * @param comparables
     */
    public void setComparables(List<ComparableInfo> comparables) {
        this.comparables = comparables;
    }

}