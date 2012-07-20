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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Image;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.RichTable;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Layout manager that works with {@code CollectionGroup} components and
 * renders the collection as a Table
 *
 * <p>
 * Based on the fields defined, the {@code TableLayoutManager} will
 * dynamically create instances of the fields for each collection row. In
 * addition, the manager can create standard fields like the action and sequence
 * fields for each row. The manager supports options inherited from the
 * {@code GridLayoutManager} such as rowSpan, colSpan, and cell width
 * settings.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TableLayoutManager extends GridLayoutManager implements CollectionLayoutManager {
    private static final long serialVersionUID = 3622267585541524208L;

    private boolean useShortLabels;
    private boolean repeatHeader;
    private Label headerLabelPrototype;

    private boolean renderSequenceField;
    private boolean generateAutoSequence;
    private Field sequenceFieldPrototype;

    private FieldGroup actionFieldPrototype;
    private FieldGroup subCollectionFieldGroupPrototype;
    private Field selectFieldPrototype;

    private boolean separateAddLine;
    private Group addLineGroup;

    // internal counter for the data columns (not including sequence, action)
    private int numberOfDataColumns;

    private List<Label> headerLabels;
    private List<Component> dataFields;

    private RichTable richTable;
    private boolean headerAdded;

    private int actionColumnIndex = -1;
    private String actionColumnPlacement;

    private Group rowDetailsGroup;
    private String rowDetailsLinkName = "Details";
    private boolean rowDetailsUseImage;

    public TableLayoutManager() {
        useShortLabels = false;
        repeatHeader = false;
        renderSequenceField = true;
        generateAutoSequence = false;
        separateAddLine = false;

        headerLabels = new ArrayList<Label>();
        dataFields = new ArrayList<Component>();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Sets sequence field prototype if auto sequence is true</li>
     * <li>Initializes the prototypes</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.layout.BoxLayoutManager#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performInitialization(View view, Object model, Container container) {
        if (container instanceof CollectionGroup) {
            this.setupDetails((CollectionGroup) container, view);

            if (((CollectionGroup) container).isAddViaLightBox()) {
                setSeparateAddLine(true);
            }
        }

        super.performInitialization(view, model, container);

        getRowCssClasses().clear();

        if (generateAutoSequence && !(getSequenceFieldPrototype() instanceof Message)) {
            sequenceFieldPrototype = ComponentFactory.getMessageField();
            view.assignComponentIds(getSequenceFieldPrototype());
        }

        view.getViewHelperService().performComponentInitialization(view, model, getHeaderLabelPrototype());
        view.getViewHelperService().performComponentInitialization(view, model, getSequenceFieldPrototype());
        view.getViewHelperService().performComponentInitialization(view, model, getActionFieldPrototype());
        view.getViewHelperService().performComponentInitialization(view, model, getSubCollectionFieldGroupPrototype());
        view.getViewHelperService().performComponentInitialization(view, model, getSelectFieldPrototype());
    }

    /**
     * Sets up the final column count for rendering based on whether the
     * sequence and action fields have been generated
     *
     * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performFinalize(View view, Object model, Container container) {
        super.performFinalize(view, model, container);

        UifFormBase formBase = (UifFormBase) model;

        CollectionGroup collectionGroup = (CollectionGroup) container;

        int totalColumns = getNumberOfDataColumns();
        if (renderSequenceField) {
            totalColumns++;
        }

        if (collectionGroup.isIncludeLineSelectionField()) {
            totalColumns++;
        }

        if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly()) {
            totalColumns++;
        }

        setNumberOfColumns(totalColumns);

        // if add line event, add highlighting for added row
        if (UifConstants.ActionEvents.ADD_LINE.equals(formBase.getActionEvent())) {
            String highlightScript = "jQuery(\"#" + container.getId() + " tr:first\").effect(\"highlight\",{}, 6000);";
            String onReadyScript = collectionGroup.getOnDocumentReadyScript();
            if (StringUtils.isNotBlank(onReadyScript)) {
                highlightScript = onReadyScript + highlightScript;
            }
            collectionGroup.setOnDocumentReadyScript(highlightScript);
        }
    }

    /**
     * Assembles the field instances for the collection line. The given sequence
     * field prototype is copied for the line sequence field. Likewise a copy of
     * the actionFieldPrototype is made and the given actions are set as the
     * items for the action field. Finally the generated items are assembled
     * together into the dataFields list with the given lineFields.
     *
     * @see org.kuali.rice.krad.uif.layout.CollectionLayoutManager#buildLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.CollectionGroup,
     *      java.util.List, java.util.List, java.lang.String, java.util.List,
     *      java.lang.String, java.lang.Object, int)
     */
    public void buildLine(View view, Object model, CollectionGroup collectionGroup, List<Field> lineFields,
            List<FieldGroup> subCollectionFields, String bindingPath, List<Action> actions, String idSuffix,
            Object currentLine, int lineIndex) {

        boolean isAddLine = lineIndex == -1;

        if (isAddLine && collectionGroup.isRenderAddBlankLineButton()) {
            return;
        }

        boolean renderActions = collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly();
        int extraColumns = 0;

        if (collectionGroup.isHighlightNewItems() && ((UifFormBase) model).isAddedCollectionItem(currentLine)) {
            getRowCssClasses().add(collectionGroup.getNewItemsCssClass());
        } else if (isAddLine
                && collectionGroup.isRenderAddLine()
                && !collectionGroup.isReadOnly()
                && !isSeparateAddLine()) {
            getRowCssClasses().add(collectionGroup.getAddItemCssClass());
        } else if (lineIndex != -1) {
            getRowCssClasses().add("");
        }

        // if separate add line prepare the add line group
        if (isAddLine && separateAddLine) {
            if (StringUtils.isBlank(addLineGroup.getTitle()) && StringUtils.isBlank(
                    addLineGroup.getHeader().getHeaderText())) {
                addLineGroup.getHeader().setHeaderText(collectionGroup.getAddLabel());
            }
            addLineGroup.setItems(lineFields);

            List<Component> footerItems = new ArrayList<Component>(actions);
            footerItems.addAll(addLineGroup.getFooter().getItems());
            addLineGroup.getFooter().setItems(footerItems);

            if (collectionGroup.isAddViaLightBox()) {
                String actionScript = "showLightboxComponent('" + addLineGroup.getId() + "');";
                if (StringUtils.isNotBlank(collectionGroup.getAddViaLightBoxAction().getActionScript())) {
                    actionScript = collectionGroup.getAddViaLightBoxAction().getActionScript() + actionScript;
                }
                collectionGroup.getAddViaLightBoxAction().setActionScript(actionScript);
                addLineGroup.setStyle("display: none");
            }

            return;
        }

        // if first line for table set number of data columns
        if (dataFields.isEmpty()) {
            if (isSuppressLineWrapping()) {
                setNumberOfDataColumns(lineFields.size());
            } else {
                setNumberOfDataColumns(getNumberOfColumns());
            }
        }

        // TODO: implement repeat header
        if (!headerAdded) {
            headerLabels = new ArrayList<Label>();
            dataFields = new ArrayList<Component>();

            buildTableHeaderRows(collectionGroup, lineFields);
            ComponentUtils.pushObjectToContext(headerLabels, UifConstants.ContextVariableNames.LINE, currentLine);
            ComponentUtils.pushObjectToContext(headerLabels, UifConstants.ContextVariableNames.INDEX, new Integer(
                    lineIndex));
            headerAdded = true;
        }

        // set label field rendered to true on line fields
        for (Field field : lineFields) {
            field.setLabelRendered(true);
            field.setFieldLabel(null);
        }

        int rowCount = calculateNumberOfRows(lineFields);
        int rowSpan = rowCount + subCollectionFields.size();

        if (actionColumnIndex == 1 && renderActions) {
            addActionColumn(idSuffix, currentLine, lineIndex, rowSpan, actions);
        }

        // sequence field is always first and should span all rows for the line
        if (renderSequenceField) {
            Component sequenceField = null;
            if (!isAddLine) {
                sequenceField = ComponentUtils.copy(getSequenceFieldPrototype(), idSuffix);
                //Ignore in validation processing
                sequenceField.addDataAttribute(UifConstants.DataAttributes.VIGNORE, "yes");
                if (generateAutoSequence && (sequenceField instanceof MessageField)) {
                    ((MessageField) sequenceField).setMessageText(Integer.toString(lineIndex + 1));
                }
            } else {
                sequenceField = ComponentUtils.copy(collectionGroup.getAddLineLabel(), idSuffix);
            }
            sequenceField.setRowSpan(rowSpan);

            if (sequenceField instanceof DataBinding) {
                ((DataBinding) sequenceField).getBindingInfo().setBindByNamePrefix(bindingPath);
            }

            ComponentUtils.updateContextForLine(sequenceField, currentLine, lineIndex);
            dataFields.add(sequenceField);
            extraColumns++;

            if (actionColumnIndex == 2 && renderActions) {
                addActionColumn(idSuffix, currentLine, lineIndex, rowSpan, actions);
            }
        }

        // select field will come after sequence field (if enabled) or be first column
        if (collectionGroup.isIncludeLineSelectionField()) {
            Field selectField = ComponentUtils.copy(getSelectFieldPrototype(), idSuffix);
            CollectionLayoutUtils.prepareSelectFieldForLine(selectField, collectionGroup, bindingPath, currentLine);

            ComponentUtils.updateContextForLine(selectField, currentLine, lineIndex);
            dataFields.add(selectField);
            extraColumns++;

            if (renderActions) {
                if ((actionColumnIndex == 3 && renderSequenceField) || (actionColumnIndex == 2
                        && !renderSequenceField)) {
                    addActionColumn(idSuffix, currentLine, lineIndex, rowSpan, actions);
                }
            }
        }

        // now add the fields in the correct position
        int cellPosition = 0;

        boolean renderActionsLast = actionColumnIndex == -1 || actionColumnIndex > lineFields.size() + extraColumns;

        for (Field lineField : lineFields) {
            dataFields.add(lineField);

            cellPosition += lineField.getColSpan();

            // action field
            if (!renderActionsLast && cellPosition == (actionColumnIndex - extraColumns - 1)) {
                addActionColumn(idSuffix, currentLine, lineIndex, rowSpan, actions);
            }
        }

        if (renderActions && renderActionsLast) {
            addActionColumn(idSuffix, currentLine, lineIndex, rowSpan, actions);
        }

        // update colspan on sub-collection fields
        for (FieldGroup subCollectionField : subCollectionFields) {
            subCollectionField.setColSpan(numberOfDataColumns);
        }

        // add sub-collection fields to end of data fields
        dataFields.addAll(subCollectionFields);
    }

    /**
     * Adds the action field in a row
     *
     * @param idSuffix
     * @param currentLine
     * @param lineIndex
     * @param rowSpan
     * @param actions
     */
    private void addActionColumn(String idSuffix, Object currentLine, int lineIndex, int rowSpan,
            List<Action> actions) {
        FieldGroup lineActionsField = ComponentUtils.copy(getActionFieldPrototype(), idSuffix);

        ComponentUtils.updateContextForLine(lineActionsField, currentLine, lineIndex);
        lineActionsField.setRowSpan(rowSpan);
        lineActionsField.setItems(actions);

        dataFields.add(lineActionsField);
    }

    /**
     * Create the {@code Label} instances that will be used to render
     * the table header
     *
     * <p>
     * For each column, a copy of headerLabelPrototype is made that determines
     * the label configuration. The actual label text comes from the field for
     * which the header applies to. The first column is always the sequence (if
     * enabled) and the last column contains the actions. Both the sequence and
     * action header fields will span all rows for the header.
     * </p>
     *
     * <p>
     * The headerLabels list will contain the final list of header fields built
     * </p>
     *
     * @param collectionGroup - CollectionGroup container the table applies to
     * @param lineFields - fields for the data columns from which the headers are pulled
     */
    protected void buildTableHeaderRows(CollectionGroup collectionGroup, List<Field> lineFields) {
        // row count needed to determine the row span for the sequence and
        // action fields, since they should span all rows for the line
        int rowCount = calculateNumberOfRows(lineFields);
        boolean renderActions = collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly();
        int extraColumns = 0;

        if (actionColumnIndex == 1 && renderActions) {
            addActionHeader(rowCount, 1);
        }

        // first column is sequence label (if action column not 1)
        if (renderSequenceField) {
            getSequenceFieldPrototype().setLabelRendered(true);
            getSequenceFieldPrototype().setRowSpan(rowCount);
            addHeaderField(getSequenceFieldPrototype(), 1);
            extraColumns++;

            if (actionColumnIndex == 2 && renderActions) {
                addActionHeader(rowCount, 2);
            }
        }

        // next is select field
        if (collectionGroup.isIncludeLineSelectionField()) {
            getSelectFieldPrototype().setLabelRendered(true);
            getSelectFieldPrototype().setRowSpan(rowCount);
            addHeaderField(getSelectFieldPrototype(), 1);
            extraColumns++;

            if (actionColumnIndex == 3 && renderActions && renderSequenceField) {
                addActionHeader(rowCount, 3);
            } else if (actionColumnIndex == 2 && renderActions) {
                addActionHeader(rowCount, 2);
            }
        }

        // pull out label fields from the container's items
        int cellPosition = 0;
        boolean renderActionsLast = actionColumnIndex == -1 || actionColumnIndex > lineFields.size() + extraColumns;

        for (Field field : lineFields) {
            if (!field.isRender() && StringUtils.isEmpty(field.getProgressiveRender())) {
                continue;
            }

            cellPosition += field.getColSpan();
            addHeaderField(field, cellPosition);

            // add action header
            if (renderActions && !renderActionsLast && cellPosition == actionColumnIndex - extraColumns - 1) {
                cellPosition += 1;
                addActionHeader(rowCount, cellPosition);
            }
        }

        if (renderActions && renderActionsLast) {
            cellPosition += 1;
            addActionHeader(rowCount, cellPosition);
        }
    }

    /**
     * Adds the action header
     *
     * @param rowCount
     * @param cellPosition
     */
    private void addActionHeader(int rowCount, int cellPosition) {
        getActionFieldPrototype().setLabelRendered(true);
        getActionFieldPrototype().setRowSpan(rowCount);
        addHeaderField(getActionFieldPrototype(), cellPosition);
    }

    /**
     * Creates a new instance of the header field prototype and then sets the
     * label to the short (if useShortLabels is set to true) or long label of
     * the given component. After created the header field is added to the list
     * making up the table header
     *
     * @param field - field instance the header field is being created for
     * @param column - column number for the header, used for setting the id
     */
    protected void addHeaderField(Field field, int column) {
        Label headerLabel = ComponentUtils.copy(getHeaderLabelPrototype(), "_c" + column);
        if (useShortLabels) {
            headerLabel.setLabelText(field.getShortLabel());
        } else {
            headerLabel.setLabelText(field.getLabel());
        }

        headerLabel.setRowSpan(field.getRowSpan());
        headerLabel.setColSpan(field.getColSpan());

        if ((field.getRequired() != null) && field.getRequired().booleanValue()) {
            headerLabel.getRequiredMessage().setRender(true);
        } else {
            headerLabel.getRequiredMessage().setRender(false);
        }

        headerLabels.add(headerLabel);
    }

    /**
     * Calculates how many rows will be needed per collection line to display
     * the list of fields. Assumption is made that the total number of cells the
     * fields take up is evenly divisible by the configured number of columns
     *
     * @param items - list of items that make up one collection line
     * @return int number of rows
     */
    protected int calculateNumberOfRows(List<? extends Field> items) {
        int rowCount = 0;

        // check flag that indicates only one row should be created
        if (isSuppressLineWrapping()) {
            return 1;
        }

        int cellCount = 0;
        for (Field field : items) {
            cellCount += field.getColSpan() + field.getRowSpan() - 1;
        }

        if (cellCount != 0) {
            rowCount = cellCount / getNumberOfDataColumns();
        }

        return rowCount;
    }

    /**
     * @see CollectionLayoutManager#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return CollectionGroup.class;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(richTable);
        components.add(addLineGroup);
        components.addAll(headerLabels);
        components.addAll(dataFields);

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = super.getComponentPrototypes();

        components.add(getHeaderLabelPrototype());
        components.add(getSequenceFieldPrototype());
        components.add(getActionFieldPrototype());
        components.add(getSubCollectionFieldGroupPrototype());
        components.add(getSelectFieldPrototype());

        return components;
    }

    /**
     * Indicates whether the short label for the collection field should be used
     * as the table header or the regular label
     *
     * @return boolean true if short label should be used, false if long label
     *         should be used
     */
    public boolean isUseShortLabels() {
        return this.useShortLabels;
    }

    /**
     * Setter for the use short label indicator
     *
     * @param useShortLabels
     */
    public void setUseShortLabels(boolean useShortLabels) {
        this.useShortLabels = useShortLabels;
    }

    /**
     * Indicates whether the header should be repeated before each collection
     * row. If false the header is only rendered at the beginning of the table
     *
     * @return boolean true if header should be repeated, false if it should
     *         only be rendered once
     */
    public boolean isRepeatHeader() {
        return this.repeatHeader;
    }

    /**
     * Setter for the repeat header indicator
     *
     * @param repeatHeader
     */
    public void setRepeatHeader(boolean repeatHeader) {
        this.repeatHeader = repeatHeader;
    }

    /**
     * {@code Label} instance to use as a prototype for creating the
     * tables header fields. For each header field the prototype will be copied
     * and adjusted as necessary
     *
     * @return Label instance to serve as prototype
     */
    public Label getHeaderLabelPrototype() {
        return this.headerLabelPrototype;
    }

    /**
     * Setter for the header field prototype
     *
     * @param headerLabelPrototype
     */
    public void setHeaderLabelPrototype(Label headerLabelPrototype) {
        this.headerLabelPrototype = headerLabelPrototype;
    }

    /**
     * List of {@code Label} instances that should be rendered to make
     * up the tables header
     *
     * @return List of label field instances
     */
    public List<Label> getHeaderLabels() {
        return this.headerLabels;
    }

    /**
     * Indicates whether the sequence field should be rendered for the
     * collection
     *
     * @return boolean true if sequence field should be rendered, false if not
     */
    public boolean isRenderSequenceField() {
        return this.renderSequenceField;
    }

    /**
     * Setter for the render sequence field indicator
     *
     * @param renderSequenceField
     */
    public void setRenderSequenceField(boolean renderSequenceField) {
        this.renderSequenceField = renderSequenceField;
    }

    /**
     * Attribute name to use as sequence value. For each collection line the
     * value of this field on the line will be retrieved and used as the
     * sequence value
     *
     * @return String sequence property name
     */
    public String getSequencePropertyName() {
        if ((getSequenceFieldPrototype() != null) && (getSequenceFieldPrototype() instanceof DataField)) {
            return ((DataField) getSequenceFieldPrototype()).getPropertyName();
        }

        return null;
    }

    /**
     * Setter for the sequence property name
     *
     * @param sequencePropertyName
     */
    public void setSequencePropertyName(String sequencePropertyName) {
        if ((getSequenceFieldPrototype() != null) && (getSequenceFieldPrototype() instanceof DataField)) {
            ((DataField) getSequenceFieldPrototype()).setPropertyName(sequencePropertyName);
        }
    }

    /**
     * Indicates whether the sequence field should be generated with the current
     * line number
     *
     * <p>
     * If set to true the sequence field prototype will be changed to a message
     * field (if not already a message field) and the text will be set to the
     * current line number
     * </p>
     *
     * @return boolean true if the sequence field should be generated from the
     *         line number, false if not
     */
    public boolean isGenerateAutoSequence() {
        return this.generateAutoSequence;
    }

    /**
     * Setter for the generate auto sequence field
     *
     * @param generateAutoSequence
     */
    public void setGenerateAutoSequence(boolean generateAutoSequence) {
        this.generateAutoSequence = generateAutoSequence;
    }

    /**
     * {@code Field} instance to serve as a prototype for the
     * sequence field. For each collection line this instance is copied and
     * adjusted as necessary
     *
     * @return Attribute field instance
     */
    public Field getSequenceFieldPrototype() {
        return this.sequenceFieldPrototype;
    }

    /**
     * Setter for the sequence field prototype
     *
     * @param sequenceFieldPrototype
     */
    public void setSequenceFieldPrototype(Field sequenceFieldPrototype) {
        this.sequenceFieldPrototype = sequenceFieldPrototype;
    }

    /**
     * {@code FieldGroup} instance to serve as a prototype for the actions
     * column. For each collection line this instance is copied and adjusted as
     * necessary. Note the actual actions for the group come from the collection
     * groups actions List
     * (org.kuali.rice.krad.uif.container.CollectionGroup.getActions()). The
     * FieldGroup prototype is useful for setting styling of the actions column
     * and for the layout of the action fields. Note also the label associated
     * with the prototype is used for the action column header
     *
     * @return GroupField instance
     */
    public FieldGroup getActionFieldPrototype() {
        return this.actionFieldPrototype;
    }

    /**
     * Setter for the action field prototype
     *
     * @param actionFieldPrototype
     */
    public void setActionFieldPrototype(FieldGroup actionFieldPrototype) {
        this.actionFieldPrototype = actionFieldPrototype;
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
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#isIncludeLineSelectionField()} is true
     *
     * <p>
     * This prototype can be used to set the control used for the select field (generally will be a checkbox control)
     * in addition to styling and other setting. The binding path will be formed with using the
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getLineSelectPropertyName()} or if not set the
     * framework
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
     * Indicates whether the add line should be rendered in a separate group, or as part of the table (first line)
     *
     * <p>
     * When separate add line is enabled, the fields for the add line will be placed in the {@link #getAddLineGroup()}.
     * This group can be used to configure the add line presentation. In addition to the fields, the header on the
     * group (unless already set) will be set to
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getAddLabel()} and the add line actions will
     * be placed into the group's footer.
     * </p>
     *
     * @return boolean true if add line should be separated, false if it should be placed into the table
     */
    public boolean isSeparateAddLine() {
        return separateAddLine;
    }

    /**
     * Setter for the separate add line indicator
     *
     * @param separateAddLine
     */
    public void setSeparateAddLine(boolean separateAddLine) {
        this.separateAddLine = separateAddLine;
    }

    /**
     * When {@link #isSeparateAddLine()} is true, this group will be used to render the add line
     *
     * <p>
     * This group can be used to configure how the add line will be rendered. For example the layout manager configured
     * on the group will be used to rendered the add line fields. If the header (title) is not set on the group, it
     * will be set from
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getAddLabel()}. In addition,
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getAddLineActions()} will be added to the group
     * footer items.
     * </p>
     *
     * @return Group instance for the collection add line
     */
    public Group getAddLineGroup() {
        return addLineGroup;
    }

    /**
     * Setter for the add line Group
     *
     * @param addLineGroup
     */
    public void setAddLineGroup(Group addLineGroup) {
        this.addLineGroup = addLineGroup;
    }

    /**
     * List of {@code Component} instances that make up the tables body. Pulled
     * by the layout manager template to send through the Grid layout
     *
     * @return List<Component> table body fields
     */
    public List<Component> getDataFields() {
        return this.dataFields;
    }

    /**
     * Widget associated with the table to add functionality such as sorting,
     * paging, and export
     *
     * @return RichTable instance
     */
    public RichTable getRichTable() {
        return this.richTable;
    }

    /**
     * Setter for the rich table widget
     *
     * @param richTable
     */
    public void setRichTable(RichTable richTable) {
        this.richTable = richTable;
    }

    /**
     * @return the numberOfDataColumns
     */
    public int getNumberOfDataColumns() {
        return this.numberOfDataColumns;
    }

    /**
     * @param numberOfDataColumns the numberOfDataColumns to set
     */
    public void setNumberOfDataColumns(int numberOfDataColumns) {
        this.numberOfDataColumns = numberOfDataColumns;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#getHiddenColumns()
     */
    public Set<String> getHiddenColumns() {
        if (richTable != null) {
            return richTable.getHiddenColumns();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#setHiddenColumns(java.util.Set<java.lang.String>)
     */
    public void setHiddenColumns(Set<String> hiddenColumns) {
        if (richTable != null) {
            richTable.setHiddenColumns(hiddenColumns);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#getSortableColumns()
     */
    public Set<String> getSortableColumns() {
        if (richTable != null) {
            return richTable.getSortableColumns();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#setSortableColumns(java.util.Set<java.lang.String>)
     */
    public void setSortableColumns(Set<String> sortableColumns) {
        if (richTable != null) {
            richTable.setSortableColumns(sortableColumns);
        }
    }

    /**
     * Indicates the index of the action column
     *
     * @return int = the action column index
     */
    public int getActionColumnIndex() {
        return actionColumnIndex;
    }

    /**
     * Indicates the actions column placement
     *
     * <p>
     * Valid values are 'LEFT', 'RIGHT' or any valid number. The default is 'RIGHT' or '-1'. The column placement index
     * takes all displayed columns, including sequence and selection columns, into account.
     * </p>
     *
     * @return String - the action column placement
     */
    public String getActionColumnPlacement() {
        return actionColumnPlacement;
    }

    /**
     * Setter for the action column placement
     *
     * @param actionColumnPlacement - action column placement string
     */
    public void setActionColumnPlacement(String actionColumnPlacement) {
        this.actionColumnPlacement = actionColumnPlacement;

        if ("LEFT".equals(actionColumnPlacement)) {
            actionColumnIndex = 1;
        } else if ("RIGHT".equals(actionColumnPlacement)) {
            actionColumnIndex = -1;
        } else if (StringUtils.isNumeric(actionColumnPlacement)) {
            actionColumnIndex = Integer.parseInt(actionColumnPlacement);
        }
    }

    /**
     * The row details info group to use when using a TableLayoutManager with the a richTable.
     *
     * <p>This group will be displayed when the user clicks the "Details" link/image on a row.
     * This allows extra/long data to be hidden in table rows and then revealed during interaction
     * with the table without the need to leave the page.  Allows for any group content.</p>
     *
     * <p>Does not currently work with javascript required content.</p>
     *
     * @return rowDetailsGroup component
     */
    public Group getRowDetailsGroup() {
        return rowDetailsGroup;
    }

    /**
     * Set the row details info group
     *
     * @param rowDetailsGroup row details group
     */
    public void setRowDetailsGroup(Group rowDetailsGroup) {
        this.rowDetailsGroup = rowDetailsGroup;
    }

    /**
     * Name of the link for displaying row details in a TableLayoutManager CollectionGroup
     *
     * @return name of the link
     */
    public String getRowDetailsLinkName() {
        return rowDetailsLinkName;
    }

    /**
     * Row details link name
     *
     * @param rowDetailsLinkName name of the details link
     */
    public void setRowDetailsLinkName(String rowDetailsLinkName) {
        this.rowDetailsLinkName = rowDetailsLinkName;
    }

    /**
     * If true, the row details link will use an image instead of a link to display row details in
     * a TableLayoutManager CollectionGroup
     *
     * @return true if displaying an image instead of a link for row details
     */
    public boolean isRowDetailsUseImage() {
        return rowDetailsUseImage;
    }

    /**
     * Sets row details link use image flag
     *
     * @param rowDetailsUseImage true to use image for details, false otherwise
     */
    public void setRowDetailsUseImage(boolean rowDetailsUseImage) {
        this.rowDetailsUseImage = rowDetailsUseImage;
    }

    /**
     * Creates the details group for the line using the information setup through the setter methods of this
     * interface.  Line details are currently only supported in TableLayoutManagers which use richTable.
     *
     * @param collectionGroup the CollectionGroup for this TableLayoutManager
     * @param view the current view
     */
    public void setupDetails(CollectionGroup collectionGroup, View view) {
        if (getRowDetailsGroup() != null && this.getRichTable() != null && this.getRichTable().isRender()) {
            this.getRowDetailsGroup().setHidden(true);
            FieldGroup detailsFieldGroup = ComponentFactory.getFieldGroup();
            detailsFieldGroup.setDataRoleAttribute("detailsFieldGroup");
            Action rowDetailsAction = ComponentFactory.getActionLink();
            rowDetailsAction.addStyleClass("uif-detailsAction");
            view.assignComponentIds(rowDetailsAction);

            if (rowDetailsUseImage) {
                Image rowDetailsImage = ComponentFactory.getImage();
                view.assignComponentIds(rowDetailsImage);
                rowDetailsImage.setAltText(rowDetailsLinkName);
                rowDetailsImage.getPropertyExpressions().put("source",
                        KRADConstants.IMAGE_URL_EXPRESSION + KRADConstants.DETAILS_IMAGE);
                rowDetailsAction.setActionImage(rowDetailsImage);
            } else if (StringUtils.isNotBlank(rowDetailsLinkName)) {
                rowDetailsAction.setActionLabel(rowDetailsLinkName);
            }

            //build js for link
            rowDetailsAction.setActionScript(
                    "expandDataTableDetail(this,'" + this.getId() + "'," + rowDetailsUseImage + ")");

            List<Component> detailsItems = new ArrayList<Component>();

            detailsItems.add(rowDetailsAction);
            this.getRowDetailsGroup().setDataRoleAttribute("details");
            detailsItems.add(getRowDetailsGroup());
            detailsFieldGroup.setItems(detailsItems);
            view.assignComponentIds(detailsFieldGroup);

            List<Component> theItems = new ArrayList<Component>();
            theItems.add(detailsFieldGroup);
            theItems.addAll(collectionGroup.getItems());
            collectionGroup.setItems(theItems);
        }
    }
}