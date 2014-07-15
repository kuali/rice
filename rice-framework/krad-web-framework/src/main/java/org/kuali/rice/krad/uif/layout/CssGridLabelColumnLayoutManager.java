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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.util.LifecycleElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A Css Grid Layout which only takes fields as its content and separates out the field's labels into
 * separate columns
 *
 * <p>This layout does not use the container's items' colspan property to influence column size.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "cssGridLabelColumnLayout", parent = "Uif-CssGridLabelColumnLayout")
public class CssGridLabelColumnLayoutManager extends CssGridLayoutManagerBase {
    private static final long serialVersionUID = 3100360397450755904L;

    private int numberOfLabelColumns = 1;
    private String labelColumnCssClass = "";

    private CssGridSizes labelColumnSizes;
    private CssGridSizes fieldColumnSizes;

    // Internal local variables
    protected int xsCurrentFieldSize;
    protected int smCurrentFieldSize;
    protected int mdCurrentFieldSize;
    protected int lgCurrentFieldSize;

    public CssGridLabelColumnLayoutManager() {
        super();
        labelColumnSizes = new CssGridSizes();
        fieldColumnSizes = new CssGridSizes();
    }

    /**
     * CssGridLabelColumnLayoutManager's performFinalize method calculates and separates the items into rows
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement component) {
        super.performFinalize(model, component);

        Container container = (Container) component;
        cellItems = new ArrayList<Component>();
        processSeparateLabelLayout(container);
    }

    /**
     * Separates the labels and field content into the appropriate number of rows and div "cells" based on
     * the numberOfLabelColumns property, by making making labels take up their own column and turning off rendering
     * them for the fields
     *
     * @param container the container using this layout manager
     */
    private void processSeparateLabelLayout(Container container) {
        // Defaults if label and field column sizes are not set directly
        int labelColumnSize = 3;
        int fieldColumnSize = 9;
        if (numberOfLabelColumns > 1) {
            labelColumnSize = (NUMBER_OF_COLUMNS / numberOfLabelColumns) * 1 / 3;
            fieldColumnSize = (NUMBER_OF_COLUMNS / numberOfLabelColumns) * 2 / 3;
        }

        for (Component item : container.getItems()) {
            // Throw exception for non-fields or fields without labels
            if (!(item instanceof Field)) {
                throw new RuntimeException("Must use fields "
                        + " for CssGridLabelColumnLayouts. Item class: "
                        + item.getClass().getName()
                        +
                        " in Container id: "
                        + container.getId());
            } else if (((Field) item).getFieldLabel() == null) {
                throw new RuntimeException(
                        "Label must exist on fields in CssGridLabelColumnLayoutManager. Item class: " + item.getClass()
                                .getName() + " in Container id: " + container.getId());
            }

            xsCurrentFieldSize = 0;
            smCurrentFieldSize = 0;
            mdCurrentFieldSize = 0;
            lgCurrentFieldSize = 0;

            Field field = (Field) item;
            Label label = separateLabel(field);

            // Determine "cell" label div css
            List<String> cellCssClasses = label.getWrapperCssClasses();
            if (cellCssClasses == null) {
                label.setWrapperCssClasses(new ArrayList<String>());
                cellCssClasses = label.getWrapperCssClasses();
            }

            cellCssClasses.add(0, labelColumnCssClass);
            calculateCssClassAndSize(label, cellCssClasses, labelColumnSizes, labelColumnSize);

            // Add dynamic left clear classes for potential wrapping content at each screen size
            addLeftClearCssClass(cellCssClasses);
            cellCssClassAttributes.add(getCellStyleClassesAsString(cellCssClasses));

            // Add label
            cellItems.add(label);

            // Determine "cell" field div css
            cellCssClasses = field.getWrapperCssClasses();
            if (cellCssClasses == null) {
                field.setWrapperCssClasses(new ArrayList<String>());
                cellCssClasses = field.getWrapperCssClasses();
            }

            calculateCssClassAndSize(field, cellCssClasses, fieldColumnSizes, fieldColumnSize);

            // Add dynamic float classes for each size, this is to make the label appear right when content is on
            // the same "row" as the label, and left (default) when they are on separate lines
            // assumption here is that content will take up more columns when becoming smaller, so if the float
            // is right at the smallest level, assume that it will be right for the other levels
            if (xsCurrentFieldSize > 0 && xsCurrentFieldSize <= CssGridLayoutManagerBase.NUMBER_OF_COLUMNS) {
                label.addStyleClass(CssConstants.CssGrid.XS_FLOAT_RIGHT);
            } else if (smCurrentFieldSize > 0 && smCurrentFieldSize <= CssGridLayoutManagerBase.NUMBER_OF_COLUMNS) {
                label.addStyleClass(CssConstants.CssGrid.SM_FLOAT_RIGHT);
            } else if (mdCurrentFieldSize > 0 && mdCurrentFieldSize <= CssGridLayoutManagerBase.NUMBER_OF_COLUMNS) {
                label.addStyleClass(CssConstants.CssGrid.MD_FLOAT_RIGHT);
            } else if (lgCurrentFieldSize > 0 && lgCurrentFieldSize <= CssGridLayoutManagerBase.NUMBER_OF_COLUMNS) {
                label.addStyleClass(CssConstants.CssGrid.LG_FLOAT_RIGHT);
            }

            // Add dynamic left clear classes for potential wrapping content at each screen size
            addLeftClearCssClass(cellCssClasses);
            cellCssClassAttributes.add(getCellStyleClassesAsString(cellCssClasses));

            // Add field
            cellItems.add(field);
        }

    }

    /**
     * Override is used to calculates total field and label size in addition to calculateCssClassAndSize functionality
     *
     * @see org.kuali.rice.krad.uif.layout.CssGridLayoutManagerBase#calculateCssClassAndSize(org.kuali.rice.krad.uif.component.Component,
     * java.util.List, CssGridSizes, int)
     */
    @Override
    protected void calculateCssClassAndSize(Component item, List<String> cellCssClasses, CssGridSizes defaultSizes,
            int basicSize) {
        int xsPrevTotalSize = xsTotalSize;
        int smPrevTotalSize = smTotalSize;
        int mdPrevTotalSize = mdTotalSize;
        int lgPrevTotalSize = lgTotalSize;

        super.calculateCssClassAndSize(item, cellCssClasses, defaultSizes, basicSize);

        xsCurrentFieldSize += xsTotalSize - xsPrevTotalSize;
        smCurrentFieldSize += smTotalSize - smPrevTotalSize;
        mdCurrentFieldSize += mdTotalSize - mdPrevTotalSize;
        lgCurrentFieldSize += lgTotalSize - lgPrevTotalSize;
    }

    /**
     * Returns the label on the field and sets the appropriate display settings and css classes to make it render
     * correctly
     *
     * @param field the field to get the label from
     * @return the label
     */
    private Label separateLabel(Field field) {
        Label label;
        field.setLabelLeft(false);

        // pull out label field
        field.getFieldLabel().addStyleClass("displayWith-" + field.getId());
        if (!field.isRender() && StringUtils.isBlank(field.getProgressiveRender())) {
            field.getFieldLabel().setRender(false);
        } else if (!field.isRender() && StringUtils.isNotBlank(field.getProgressiveRender())) {
            field.getFieldLabel().setRender(true);
            String prefixStyle = "";
            if (StringUtils.isNotBlank(field.getFieldLabel().getStyle())) {
                prefixStyle = field.getFieldLabel().getStyle();
            }
            field.getFieldLabel().setStyle(prefixStyle + ";" + "display: none;");
        }

        label = field.getFieldLabel();

        if (field instanceof InputField && field.getRequired() != null && field.getRequired()) {
            label.setRenderRequiredIndicator(true);
        }

        // set boolean to indicate label field should not be
        // rendered with the attribute
        field.setLabelRendered(true);

        return label;
    }

    /**
     * The css class to use on the label column's div "cells"
     *
     * @return the css class to use on label column div "cells"
     */
    @BeanTagAttribute
    public String getLabelColumnCssClass() {
        return labelColumnCssClass;
    }

    /**
     * Setter for {@link #getLabelColumnCssClass()}.
     *
     * @param labelColumnCssClass property value
     */
    public void setLabelColumnCssClass(String labelColumnCssClass) {
        this.labelColumnCssClass = labelColumnCssClass;
    }

    /**
     * The number of label columns used in this layout
     *
     * <p>
     * The only supported values for this property are 1-3 which translates to 2-6 columns per a
     * row.  This property defines how many of the total columns are label columns.
     * </p>
     *
     * @return the total number of label columns
     */
    @BeanTagAttribute
    public int getNumberOfLabelColumns() {
        return numberOfLabelColumns;
    }

    /**
     * Setter for {@link #getNumberOfLabelColumns()}.
     *
     * @param numberOfLabelColumns property value
     */
    public void setNumberOfLabelColumns(int numberOfLabelColumns) {
        this.numberOfLabelColumns = numberOfLabelColumns;
    }

    /**
     * CssGridSizes that will be used by every label in this layout, unless the label itself has cssGridSizes
     * explicitly set; note that this OVERRIDES any framework automation set by numberOfLabelColumns for label sizes.
     *
     * <p>
     * Be careful with the usage of this setting, it's intent is to be set with fieldColumnSizes, or some
     * combination of custom field and label cssGridSizes, or unintended behavior/layout may result.  This is an
     * advanced layout configuration setting and requires knowledge of bootstrap css grid layout/behavior.
     * </p>
     *
     * @return the custom labelColumnSizes
     */
    @BeanTagAttribute(name = "labelColumnSizes", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CssGridSizes getLabelColumnSizes() {
        return labelColumnSizes;
    }

    /**
     * @see CssGridLabelColumnLayoutManager#getLabelColumnSizes()
     */
    public void setLabelColumnSizes(CssGridSizes labelColumnSizes) {
        this.labelColumnSizes = labelColumnSizes;
    }

    /**
     * CssGridSizes that will be used by every field in this layout, unless the field itself has cssGridSizes
     * explicitly set; note that this OVERRIDES any framework automation set by numberOfLabelColumns for field sizes.
     *
     * <p>
     * Be careful with the usage of this setting, it's intent is to be set with labelColumnSizes, or some
     * combination of custom field and label cssGridSizes, or unintended behavior/layout may result.  This is an
     * advanced layout configuration setting and requires knowledge of bootstrap css grid layout/behavior.
     * </p>
     *
     * @return
     */
    @BeanTagAttribute(name = "fieldColumnSizes", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CssGridSizes getFieldColumnSizes() {
        return fieldColumnSizes;
    }

    /**
     * @see CssGridLabelColumnLayoutManager#getFieldColumnSizes()
     */
    public void setFieldColumnSizes(CssGridSizes fieldColumnSizes) {
        this.fieldColumnSizes = fieldColumnSizes;
    }
}
