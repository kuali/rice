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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.util.KRADUtils;

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
@BeanTag(name = "cssGridLabelColumnLayout-bean", parent = "Uif-CssGridLabelColumnLayout")
public class CssGridLabelColumnLayoutManager extends CssGridLayoutManagerBase {
    private static final long serialVersionUID = 3100360397450755904L;

    private int numberOfLabelColumns = 1;
    private String labelColumnCssClass = "";

    /**
     * CssGridLabelColumnLayoutManager's performFinalize method calculates and separates the items into rows
     *
     * @see org.kuali.rice.krad.uif.component.Component#performFinalize(org.kuali.rice.krad.uif.view.View, Object,
     *      org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(Object model, Component component) {
        super.performFinalize(model, component);

        Container container = (Container) component;
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
        int labelColumnSize = 3;
        int fieldColumnSize = 9;
        if (numberOfLabelColumns > 1) {
            labelColumnSize = (NUMBER_OF_COLUMNS / numberOfLabelColumns) * 1 / 3;
            fieldColumnSize = (NUMBER_OF_COLUMNS / numberOfLabelColumns) * 2 / 3;
        }
        int itemNumber = 0;
        int rowIndex = 0;
        boolean isOdd = true;

        List<Component> currentRow = new ArrayList<Component>();
        for (Component item : container.getItems()) {
            if (!(item instanceof Field)) {
                throw new RuntimeException("Must use fields when separateFieldLabelsIntoColumns option is "
                        + "true for CssGridLayouts. Item class: "
                        + item.getClass().getName()
                        +
                        " in Container id: "
                        + container.getId());
            }

            isOdd = rowIndex % 2 == 0;
            Field field = (Field) item;
            Label label;

            // pull out label field
            if (field.getFieldLabel() != null) {
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

/*                if (field instanceof InputField && field.getRequired() != null && field.getRequired()) {
                    label.setRenderRequiredIndicator(true);
                }*/

                // set boolean to indicate label field should not be
                // rendered with the attribute
                field.setLabelRendered(true);
            } else {
                throw new RuntimeException("Label must exist when separateFieldLabelsIntoColumns option is "
                        + "true for CssGridLayouts. Item class: "
                        + item.getClass().getName()
                        +
                        " in Container id: "
                        + container.getId());
            }

            // Determine "cell" label div css and add it to cellCssClassAttributes (retrieved by index in template)
            List<String> cellCssClasses = label.getWrapperCssClasses();
            if (cellCssClasses == null) {
                label.setWrapperCssClasses(new ArrayList<String>());
                cellCssClasses = label.getWrapperCssClasses();
            }
            cellCssClasses.add(0, labelColumnCssClass);
            cellCssClasses.add(0, BOOTSTRAP_SPAN_PREFIX + labelColumnSize);
            cellCssClassAttributes.add(getCellStyleClassesAsString(cellCssClasses));

            // Add label
            currentRow.add(label);

            // Determine "cell" field div css and add it to cellCssClassAttributes (retrieved by index in template)
            cellCssClasses = field.getWrapperCssClasses();
            if (cellCssClasses == null) {
                field.setWrapperCssClasses(new ArrayList<String>());
                cellCssClasses = field.getWrapperCssClasses();
            }
            cellCssClasses.add(0, BOOTSTRAP_SPAN_PREFIX + fieldColumnSize);
            cellCssClassAttributes.add(getCellStyleClassesAsString(cellCssClasses));

            // Add field
            currentRow.add(field);

            itemNumber++;
            if (itemNumber == numberOfLabelColumns) {
                rows.add(new ArrayList<Component>(currentRow));
                currentRow = new ArrayList<Component>();

                // Determine "row" div css
                String rowCss = rowLayoutCssClass + " " + KRADUtils.generateRowCssClassString(conditionalRowCssClasses,
                        rowIndex, isOdd, null, null);
                rowCssClassAttributes.add(rowCss);

                itemNumber = 0;
                rowIndex++;
            }
        }

        // Add any extra fields that do not take up a full row
        if (itemNumber > 0) {
            // Determine "row" div css
            String rowCss = rowLayoutCssClass + " " + KRADUtils.generateRowCssClassString(conditionalRowCssClasses,
                    rowIndex, isOdd, null, null);
            rowCssClassAttributes.add(rowCss);

            rows.add(currentRow);
        }
    }



    /**
     * The css class to use on the label column's div "cells"
     *
     * @return the css class to use on label column div "cells"
     */
    @BeanTagAttribute(name = "labelColumnCssClass")
    public String getLabelColumnCssClass() {
        return labelColumnCssClass;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.CssGridLabelColumnLayoutManager#getLabelColumnCssClass()
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
    @BeanTagAttribute(name = "numberOfLabelColumns")
    public int getNumberOfLabelColumns() {
        return numberOfLabelColumns;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.CssGridLabelColumnLayoutManager#getLabelColumnCssClass()
     */
    public void setNumberOfLabelColumns(int numberOfLabelColumns) {
        this.numberOfLabelColumns = numberOfLabelColumns;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        CssGridLabelColumnLayoutManager cssGridLayoutManagerCopy = (CssGridLabelColumnLayoutManager) component;

        cssGridLayoutManagerCopy.setNumberOfLabelColumns(this.numberOfLabelColumns);
        cssGridLayoutManagerCopy.setLabelColumnCssClass(this.labelColumnCssClass);

    }

}
