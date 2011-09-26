/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants.Orientation;
import org.kuali.rice.krad.uif.component.KeepExpression;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout manager that works with <code>CollectionGroup</code> containers and
 * renders the collection lines in a vertical row
 *
 * <p>
 * For each line of the collection, a <code>Group</code> instance is created.
 * The group header contains a label for the line (summary information), the
 * group fields are the collection line fields, and the group footer contains
 * the line actions. All the groups are rendered using the
 * <code>BoxLayoutManager</code> with vertical orientation.
 * </p>
 *
 * <p>
 * Modify the lineGroupPrototype to change header/footer styles or any other
 * customization for the line groups
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StackedLayoutManager extends LayoutManagerBase implements CollectionLayoutManager {
    private static final long serialVersionUID = 4602368505430238846L;

    @KeepExpression
    private String summaryTitle;
    private List<String> summaryFields;

    private Group addLineGroup;
    private Group lineGroupPrototype;
    private FieldGroup subCollectionFieldGroupPrototype;
    private Field selectFieldPrototype;

    private List<Group> stackedGroups;

    public StackedLayoutManager() {
        super();

        summaryFields = new ArrayList<String>();
        stackedGroups = new ArrayList<Group>();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Initializes the prototypes</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.layout.BoxLayoutManager#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performInitialization(View view, Container container) {
        super.performInitialization(view, container);

        if (addLineGroup != null) {
            view.getViewHelperService().performComponentInitialization(view, addLineGroup);
        }
        view.getViewHelperService().performComponentInitialization(view, lineGroupPrototype);
        view.getViewHelperService().performComponentInitialization(view, subCollectionFieldGroupPrototype);
        view.getViewHelperService().performComponentInitialization(view, selectFieldPrototype);
    }

    /**
     * Builds a <code>Group</code> instance for a collection line. The group is
     * built by first creating a copy of the configured prototype. Then the
     * header for the group is created using the configured summary fields on
     * the <code>CollectionGroup</code>. The line fields passed in are set as
     * the items for the group, and finally the actions are placed into the
     * group footer
     *
     * @see org.kuali.rice.krad.uif.layout.CollectionLayoutManager#buildLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.CollectionGroup,
     *      java.util.List, java.util.List, java.lang.String, java.util.List,
     *      java.lang.String, java.lang.Object, int)
     */
    public void buildLine(View view, Object model, CollectionGroup collectionGroup, List<Field> lineFields,
            List<FieldGroup> subCollectionFields, String bindingPath, List<ActionField> actions, String idSuffix,
            Object currentLine, int lineIndex) {
        boolean isAddLine = lineIndex == -1;

        // construct new group
        Group lineGroup = null;
        if (isAddLine) {
            stackedGroups = new ArrayList<Group>();

            if (addLineGroup == null) {
                lineGroup = ComponentUtils.copy(lineGroupPrototype, idSuffix);
            } else {
                lineGroup = ComponentUtils.copy(getAddLineGroup(), idSuffix);
            }
        } else {
            lineGroup = ComponentUtils.copy(lineGroupPrototype, idSuffix);
        }

        ComponentUtils.updateContextForLine(lineGroup, currentLine, lineIndex);

        // build header text for group
        String headerText = "";
        if (isAddLine) {
            headerText = collectionGroup.getAddLineLabel();
        } else {
            // get the collection for this group from the model
            List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(model,
                    ((DataBinding) collectionGroup).getBindingInfo().getBindingPath());

            headerText = buildLineHeaderText(modelCollection.get(lineIndex), lineGroup);
        }

        // don't set header if text is blank (could already be set by other means)
        if (StringUtils.isNotBlank(headerText)) {
            lineGroup.getHeader().setHeaderText(headerText);
        }

        // stack all fields (including sub-collections) for the group
        List<Field> groupFields = new ArrayList<Field>();
        groupFields.addAll(lineFields);
        groupFields.addAll(subCollectionFields);

        lineGroup.setItems(groupFields);

        // set line actions on group footer
        if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly() && (lineGroup.getFooter() != null)) {
            lineGroup.getFooter().setItems(actions);
        }

        stackedGroups.add(lineGroup);
    }

    /**
     * Builds the header text for the collection line
     *
     * <p>
     * Header text is built up by first the collection label, either specified
     * on the collection definition or retrieved from the dictionary. Then for
     * each summary field defined, the value from the model is retrieved and
     * added to the header.
     * </p>
     *
     * <p>
     * Note the {@link #getSummaryTitle()} field may have expressions defined, in which cause it will be copied to the
     * property expressions map to set the title for the line group (which will have the item context variable set)
     * </p>
     *
     * @param line - Collection line containing data
     * @param lineGroup - Group instance for rendering the line and whose title should be built
     * @return String header text for line
     */
    protected String buildLineHeaderText(Object line, Group lineGroup) {
        // check for expression on summary title
        if (KRADServiceLocatorWeb.getExpressionEvaluatorService().containsElPlaceholder(summaryTitle)) {
            lineGroup.getPropertyExpressions().put("title", summaryTitle);
            return null;
        }

        // build up line summary from declared field values and fixed title
        String summaryFieldString = "";
        for (String summaryField : summaryFields) {
            Object summaryFieldValue = ObjectPropertyUtils.getPropertyValue(line, summaryField);
            if (StringUtils.isNotBlank(summaryFieldString)) {
                summaryFieldString += " - ";
            }

            if (summaryFieldValue != null) {
                summaryFieldString += summaryFieldValue;
            } else {
                summaryFieldString += "Null";
            }
        }

        String headerText = summaryTitle;
        if (StringUtils.isNotBlank(summaryFieldString)) {
            headerText += " ( " + summaryFieldString + " )";
        }

        return headerText;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.ContainerAware#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return CollectionGroup.class;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.addAll(stackedGroups);

        return components;
    }

    /**
     * Text to appears in the header for each collection lines Group. Used in
     * conjunction with {@link #getSummaryFields()} to build up the final header
     * text
     *
     * @return String summary title text
     */
    public String getSummaryTitle() {
        return this.summaryTitle;
    }

    /**
     * Setter for the summary title text
     *
     * @param summaryTitle
     */
    public void setSummaryTitle(String summaryTitle) {
        this.summaryTitle = summaryTitle;
    }

    /**
     * List of attribute names from the collection line class that should be
     * used to build the line summary. To build the summary the value for each
     * attribute is retrieved from the line instance. All the values are then
     * placed together with a separator.
     *
     * @return List<String> summary field names
     * @see #buildLineHeaderText(java.lang.Object)
     */
    public List<String> getSummaryFields() {
        return this.summaryFields;
    }

    /**
     * Setter for the summary field name list
     *
     * @param summaryFields
     */
    public void setSummaryFields(List<String> summaryFields) {
        this.summaryFields = summaryFields;
    }

    /**
     * Group instance that will be used for the add line
     *
     * <p>
     * Add line fields and actions configured on the
     * <code>CollectionGroup</code> will be set onto the add line group (if add
     * line is enabled). If the add line group is not configured, a new instance
     * of the line group prototype will be used for the add line.
     * </p>
     *
     * @return Group add line group instance
     * @see #getAddLineGroup()
     */
    public Group getAddLineGroup() {
        return this.addLineGroup;
    }

    /**
     * Setter for the add line group
     *
     * @param addLineGroup
     */
    public void setAddLineGroup(Group addLineGroup) {
        this.addLineGroup = addLineGroup;
    }

    /**
     * Group instance that is used as a prototype for creating the collection
     * line groups. For each line a copy of the prototype is made and then
     * adjusted as necessary
     *
     * @return Group instance to use as prototype
     */
    public Group getLineGroupPrototype() {
        return this.lineGroupPrototype;
    }

    /**
     * Setter for the line group prototype
     *
     * @param lineGroupPrototype
     */
    public void setLineGroupPrototype(Group lineGroupPrototype) {
        this.lineGroupPrototype = lineGroupPrototype;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.CollectionLayoutManager#getSubCollectionFieldGroupPrototype()
     */
    public FieldGroup getSubCollectionFieldGroupPrototype() {
        return this.subCollectionFieldGroupPrototype;
    }

    /**
     * Setter for the sub-collection field group prototype
     *
     * @param subCollectionFieldGroupPrototype
     */
    public void setSubCollectionFieldGroupPrototype(FieldGroup subCollectionFieldGroupPrototype) {
        this.subCollectionFieldGroupPrototype = subCollectionFieldGroupPrototype;
    }

    /**
     * Field instance that serves as a prototype for creating the select field on each line when
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#isRenderSelectField()} is true
     *
     * <p>
     * This prototype can be used to set the control used for the select field (generally will be a checkbox control)
     * in addition to styling and other setting. The binding path will be formed with using the
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getSelectPropertyName()} or if not set the framework
     * will use {@link org.kuali.rice.krad.web.form.UifFormBase#getSelectedCollectionLines()}
     * </p>
     *
     * @return Field select field prototype instance
     */
    public Field getSelectFieldPrototype() {
        return selectFieldPrototype;
    }

    /**
     * Setter for the prototype instance for select fields
     *
     * @param selectFieldPrototype
     */
    public void setSelectFieldPrototype(Field selectFieldPrototype) {
        this.selectFieldPrototype = selectFieldPrototype;
    }

    /**
     * Final <code>List</code> of Groups to render for the collection
     *
     * @return List<Group> collection groups
     */
    public List<Group> getStackedGroups() {
        return this.stackedGroups;
    }

    /**
     * Setter for the collection groups
     *
     * @param stackedGroups
     */
    public void setStackedGroups(List<Group> stackedGroups) {
        this.stackedGroups = stackedGroups;
    }

}
