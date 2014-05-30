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
package org.kuali.rice.krad.uif.field;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Field that contains a nested <code>Group</code>. Can be used to group
 * together fields by providing a group without header and footer, or simply to
 * nest full groups. The items getter/setter provided is for convenience and
 * will set the items <code>List</code> in the nested <code>Group</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "fieldGroup", parent = "Uif-FieldGroupBase"),
        @BeanTag(name = "verticalFieldGroup", parent = "Uif-VerticalFieldGroup"),
        @BeanTag(name = "horizontalFieldGroup", parent = "Uif-HorizontalFieldGroup")})
public class FieldGroup extends FieldBase {
    private static final long serialVersionUID = -505654043702442196L;

    private Group group;

    public FieldGroup() {
        super();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the align on group if empty and the align has been set on the field</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if (StringUtils.isNotBlank(getAlign()) && group != null) {
            group.setAlign(getAlign());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        super.afterEvaluateExpression();
        
        if (group != null) {
            group.setReadOnly(getReadOnly());
        }
    }

    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        this.addDataAttribute(UifConstants.DataAttributes.PARENT, parent.getId());
        if (group != null) {
            this.addDataAttribute(UifConstants.DataAttributes.GROUP, group.getId());
        }

        setNestedComponentIdAndSuffix(getFieldLabel(), UifConstants.IdSuffixes.LABEL);

        if (this.getFieldLabel() != null) {
            this.getFieldLabel().setLabelForComponentId(this.getId() + UifConstants.IdSuffixes.FIELDSET);
        }
    }

    /**
     * <code>Group</code> instance that is contained within in the field
     *
     * @return Group instance
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Group getGroup() {
        return this.group;
    }

    /**
     * Setter for the field's nested group
     *
     * @param group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * List of <code>Component</code> instances contained in the nested group
     *
     * <p>
     * Convenience method for configuration to get the items List from the
     * field's nested group
     * </p>
     *
     * @return List<? extends Component> items
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public List<? extends Component> getItems() {
        if (group != null) {
            return group.getItems();
        }

        return null;
    }

    /**
     * Setter for the field's nested group items
     *
     * <p>
     * Convenience method for configuration to set the items List for the
     * field's nested group
     * </p>
     *
     * @param items
     */
    public void setItems(List<? extends Component> items) {
        if (group != null) {
            group.setItems(items);
        }
    }
}
