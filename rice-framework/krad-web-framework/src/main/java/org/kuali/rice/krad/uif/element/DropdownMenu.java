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
package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.UifKeyValueLocation;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DropdownMenu extends ContentElementBase {
    private static final long serialVersionUID = -1759659012620124641L;

    private String dropdownToggleText;
    private Message dropdownToggle;
    private boolean includeToggleCaret;

    private List<UifKeyValueLocation> options;

    private int menuNumberOfColumns;
    private List<DropdownMenu> siblingDropdownMenus;

    private List<List<DropdownMenu>> menuColumns;

    private boolean nestedMenu;

    public DropdownMenu() {
        super();

        menuNumberOfColumns = 1;

        includeToggleCaret = true;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#performApplyModel(org.kuali.rice.krad.uif.view.View, Object,
     *      org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (StringUtils.isNotBlank(dropdownToggleText) && StringUtils.isBlank(dropdownToggle.getMessageText())) {
            dropdownToggle.setMessageText(dropdownToggleText);
        }

    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // if (StringUtils.isNotBlank(dropdownToggle.getMessageText()) && dropdownToggle.isRender()) {
        getCssClasses().add(0, "dropdown");
        //  }

        menuColumns = new ArrayList<List<DropdownMenu>>(this.menuNumberOfColumns);

        for (int i = 0; i < this.menuNumberOfColumns; i++) {
            List<DropdownMenu> column = new ArrayList<DropdownMenu>();
            menuColumns.add(i, column);
        }

        if (this.siblingDropdownMenus != null) {
            int currentColumn = 0;
            for (DropdownMenu siblingMenu : this.siblingDropdownMenus) {
                menuColumns.get(currentColumn).add(siblingMenu);

                currentColumn++;
            }
        }

        if ((this.options == null || this.options.isEmpty()) && (this.siblingDropdownMenus == null || this
                .siblingDropdownMenus.isEmpty())) {
            this.includeToggleCaret = false;
        }
    }

    public String getDropdownToggleText() {
        return dropdownToggleText;
    }

    public void setDropdownToggleText(String dropdownToggleText) {
        this.dropdownToggleText = dropdownToggleText;
    }

    public Message getDropdownToggle() {
        return dropdownToggle;
    }

    public void setDropdownToggle(Message dropdownToggle) {
        this.dropdownToggle = dropdownToggle;
    }

    public boolean isIncludeToggleCaret() {
        return includeToggleCaret;
    }

    public void setIncludeToggleCaret(boolean includeToggleCaret) {
        this.includeToggleCaret = includeToggleCaret;
    }

    public List<UifKeyValueLocation> getOptions() {
        return options;
    }

    public void setOptions(List<UifKeyValueLocation> options) {
        this.options = options;
    }

    public int getMenuNumberOfColumns() {
        return menuNumberOfColumns;
    }

    public void setMenuNumberOfColumns(int menuNumberOfColumns) {
        this.menuNumberOfColumns = menuNumberOfColumns;
    }

    public List<DropdownMenu> getSiblingDropdownMenus() {
        return siblingDropdownMenus;
    }

    public void setSiblingDropdownMenus(List<DropdownMenu> siblingDropdownMenus) {
        this.siblingDropdownMenus = siblingDropdownMenus;
    }

    public boolean isNestedMenu() {
        return nestedMenu;
    }

    public void setNestedMenu(boolean nestedMenu) {
        this.nestedMenu = nestedMenu;
    }

    public List<List<DropdownMenu>> getMenuColumns() {
        return menuColumns;
    }

    public void setMenuColumns(List<List<DropdownMenu>> menuColumns) {
        this.menuColumns = menuColumns;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#copy()
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        DropdownMenu dropdownCopy = (DropdownMenu) component;

        if (this.dropdownToggle != null) {
            dropdownCopy.setDropdownToggle((Message) this.dropdownToggle.copy());
        }
        dropdownCopy.setDropdownToggleText(this.dropdownToggleText);

        if (this.options != null) {
            List<UifKeyValueLocation> optionsCopy = new ArrayList<UifKeyValueLocation>();

            for (UifKeyValueLocation location : this.options) {
                optionsCopy.add(CloneUtils.deepClone(location));
            }
            dropdownCopy.setOptions(optionsCopy);
        }

        dropdownCopy.setIncludeToggleCaret(this.includeToggleCaret);

        dropdownCopy.setMenuNumberOfColumns(this.menuNumberOfColumns);

        if (this.siblingDropdownMenus != null) {
            List<DropdownMenu> siblingDropdownMenusCopy = new ArrayList<DropdownMenu>();

            for (DropdownMenu dropdownMenu : this.siblingDropdownMenus) {
                siblingDropdownMenusCopy.add(CloneUtils.deepClone(dropdownMenu));
            }
            dropdownCopy.setSiblingDropdownMenus(siblingDropdownMenusCopy);
        }

        dropdownCopy.setNestedMenu(this.nestedMenu);
    }
}
