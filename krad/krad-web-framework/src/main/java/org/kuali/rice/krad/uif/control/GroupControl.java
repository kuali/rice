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
package org.kuali.rice.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.kim.api.group.Group;

/**
 * Represents a group control, which is a special control to handle
 * the input of a KIM Group by group name
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
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

        if (!(parent instanceof AttributeField)) {
            return;
        }

        AttributeField field = (AttributeField) parent;

        if (StringUtils.isNotBlank(groupIdPropertyName)) {
            field.getHiddenPropertyNames().add(groupIdPropertyName);
        }

        if (StringUtils.isBlank(field.getFieldLookup().getDataObjectClassName())) {
            field.getFieldLookup().setDataObjectClassName("org.kuali.rice.kim.impl.group.GroupBo");
        }

        if (field.getFieldLookup().getFieldConversions().isEmpty()) {
            if (StringUtils.isNotBlank(groupIdPropertyName)) {
                field.getFieldLookup().getFieldConversions().put("id", groupIdPropertyName);
            }

            field.getFieldLookup().getFieldConversions().put("name", field.getPropertyName());

            if (StringUtils.isNotBlank(namespaceCodePropertyName)) {
                field.getFieldLookup().getFieldConversions().put("namespaceCode", namespaceCodePropertyName);
            }
        }

        if (field.getFieldLookup().getLookupParameters().isEmpty()) {
            if (StringUtils.isNotBlank(namespaceCodePropertyName)) {
                field.getFieldLookup().getLookupParameters().put(namespaceCodePropertyName, "namespaceCode");
            }
        }
    }

    /**
     * The name of the property on the parent object that holds the group namespace
     *
     * @return String namespaceCodePropertyName
     */
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
