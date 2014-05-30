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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Layout manager that organizes its components in a table based grid
 *
 * <p>
 * Items are laid out from left to right (with each item taking up one column)
 * until the configured number of columns is reached. If the item count is
 * greater than the number of columns, a new row will be created to render the
 * remaining items (and so on until all items are placed). Labels for the fields
 * can be pulled out (default) and rendered as a separate column. The manager
 * also supports the column span and row span options for the field items. If
 * not specified the default is 1.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "gridLayout", parent = "Uif-GridLayoutBase"),
        @BeanTag(name = "twoColumnGridLayout", parent = "Uif-TwoColumnGridLayout"),
        @BeanTag(name = "fourColumnGridLayout", parent = "Uif-FourColumnGridLayout"),
        @BeanTag(name = "sixColumnGridLayout", parent = "Uif-SixColumnGridLayout")})
public class GridLayoutManagerBase extends LayoutManagerBase implements GridLayoutManager {
    private static final long serialVersionUID = 1890011900375071128L;

    private int numberOfColumns;

    private boolean suppressLineWrapping;
    private boolean applyAlternatingRowStyles;
    private boolean applyDefaultCellWidths;
    private boolean renderFirstRowHeader;
    private boolean renderAlternatingHeaderColumns;
    private boolean renderRowFirstCellHeader;

    private List<String> rowCssClasses;

    private List<String> rowDataAttributes;

    public GridLayoutManagerBase() {
        super();

        rowCssClasses = new ArrayList<String>();
        rowDataAttributes = new ArrayList<String>();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>If suppressLineWrapping is true, sets the number of columns to the
     * container's items list size</li>
     * <li>Adjust the cell attributes for the container items</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);
        
        Container container = (Container) parent;

        // Default equal cell widths class
        if (this.isApplyDefaultCellWidths()){
            this.addStyleClass("uif-table-fixed");
        }

        if (suppressLineWrapping) {
            numberOfColumns = container.getItems().size();
        }

        for (Component item : container.getItems()) {
            if (!(this instanceof TableLayoutManager)) {
                item.addWrapperCssClass("uif-gridLayoutCell");
            }
            setCellAttributes(item);
        }
    }

    /**
     * Moves the width, align, and valign settings of the component to the corresponding cell properties (if not
     * already configured)
     *
     * @param component instance to adjust settings for
     */
    protected void setCellAttributes(Component component) {
        if (StringUtils.isNotBlank(component.getWidth()) && StringUtils.isBlank(component.getCellWidth())) {
            component.setCellWidth(component.getWidth());
            component.setWidth("");
        }

        if (StringUtils.isNotBlank(component.getAlign()) && !StringUtils.contains(component.getWrapperStyle(),
                CssConstants.TEXT_ALIGN)) {
            if (component.getWrapperStyle() == null) {
                component.setWrapperStyle("");
            }

            component.setWrapperStyle(
                    component.getWrapperStyle() + CssConstants.TEXT_ALIGN + component.getAlign() + ";");
            component.setAlign("");
        }

        if (StringUtils.isNotBlank(component.getValign()) && !StringUtils.contains(component.getWrapperStyle(),
                CssConstants.VERTICAL_ALIGN)) {
            if (component.getWrapperStyle() == null) {
                component.setWrapperStyle("");
            }

            component.setWrapperStyle(
                    component.getWrapperStyle() + CssConstants.VERTICAL_ALIGN + component.getValign() + ";");
            component.setValign("");
        }
    }

    /**
     * @see LayoutManagerBase#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return Group.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isSuppressLineWrapping() {
        return this.suppressLineWrapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuppressLineWrapping(boolean suppressLineWrapping) {
        this.suppressLineWrapping = suppressLineWrapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isApplyAlternatingRowStyles() {
        return this.applyAlternatingRowStyles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplyAlternatingRowStyles(boolean applyAlternatingRowStyles) {
        this.applyAlternatingRowStyles = applyAlternatingRowStyles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isApplyDefaultCellWidths() {
        return this.applyDefaultCellWidths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplyDefaultCellWidths(boolean applyDefaultCellWidths) {
        this.applyDefaultCellWidths = applyDefaultCellWidths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderRowFirstCellHeader() {
        return renderRowFirstCellHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderRowFirstCellHeader(boolean renderRowFirstCellHeader) {
        this.renderRowFirstCellHeader = renderRowFirstCellHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderFirstRowHeader() {
        return renderFirstRowHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderFirstRowHeader(boolean renderFirstRowHeader) {
        this.renderFirstRowHeader = renderFirstRowHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderAlternatingHeaderColumns() {
        return this.renderAlternatingHeaderColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderAlternatingHeaderColumns(boolean renderAlternatingHeaderColumns) {
        this.renderAlternatingHeaderColumns = renderAlternatingHeaderColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getRowCssClasses() {
        return rowCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowCssClasses(List<String> rowCssClasses) {
        this.rowCssClasses = rowCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "rowDataAttributes", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getRowDataAttributes() {
        return rowDataAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowDataAttributes(List<String> rowDataAttributes) {
        this.rowDataAttributes = rowDataAttributes;
    }
}
