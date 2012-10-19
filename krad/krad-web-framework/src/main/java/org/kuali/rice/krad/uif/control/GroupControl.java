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
package org.kuali.rice.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.InputField;

/**
 * Represents a group control, which is a special control to handle
 * the input of a KIM Group by group name
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name="groupControl")
public class GroupControl extends TextControl {
    private static final long serialVersionUID = 5598459655735440981L;

    private String namespaceCodePropertyName;
    private String groupIdPropertyName;

    public GroupControl() {
        super();
    }

    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (!(parent instanceof InputField)) {
            return;
        }

        InputField field = (InputField) parent;

        if (StringUtils.isNotBlank(groupIdPropertyName)) {
            field.getAdditionalHiddenPropertyNames().add(groupIdPropertyName);
        }

        if (StringUtils.isBlank(field.getQuickfinder().getDataObjectClassName())) {
            field.getQuickfinder().setDataObjectClassName("org.kuali.rice.kim.impl.group.GroupBo");
        }

        if (field.getQuickfinder().getFieldConversions().isEmpty()) {
            if (StringUtils.isNotBlank(groupIdPropertyName)) {
                field.getQuickfinder().getFieldConversions().put("id", groupIdPropertyName);
            }

            field.getQuickfinder().getFieldConversions().put("name", field.getPropertyName());

            if (StringUtils.isNotBlank(namespaceCodePropertyName)) {
                field.getQuickfinder().getFieldConversions().put("namespaceCode", namespaceCodePropertyName);
            }
        }

        if (field.getQuickfinder().getLookupParameters().isEmpty()) {
            if (StringUtils.isNotBlank(namespaceCodePropertyName)) {
                field.getQuickfinder().getLookupParameters().put(namespaceCodePropertyName, "namespaceCode");
            }
        }
    }

    /**
     * The name of the property on the parent object that holds the group namespace
     *
     * @return String namespaceCodePropertyName
     */
    @BeanTagAttribute(name="namespaceCodePropertyName")
    public String getNamespaceCodePropertyName() {
        return namespaceCodePropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the group namespace
     *
     * @param namespaceCodePropertyName
     */
    public void setNamespaceCodePropertyName(String namespaceCodePropertyName) {
        this.namespaceCodePropertyName = namespaceCodePropertyName;
    }

    /**
     * The name of the property on the parent object that holds the group id
     *
     * @return String groupIdPropertyName
     */
    @BeanTagAttribute(name="groupIdPropertyName")
    public String getGroupIdPropertyName() {
        return groupIdPropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the group id
     *
     * @param groupIdPropertyName
     */
    public void setGroupIdPropertyName(String groupIdPropertyName) {
        this.groupIdPropertyName = groupIdPropertyName;
    }
}
