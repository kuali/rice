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
package org.kuali.rice.krad.uif.lifecycle.model;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.view.ViewPresentationController;
import org.kuali.rice.krad.uif.widget.Widget;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Apply authorization and presentation logic for the component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApplyAuthAndPresentationLogicTask extends ViewLifecycleTaskBase<Component> {

    /**
     * Default constructor.
     */
    public ApplyAuthAndPresentationLogicTask() {
        super(Component.class);
    }

    /**
     * Invokes the view's configured {@link ViewAuthorizer} and {@link ViewPresentationController}
     * to set state of the component
     * 
     * <p>
     * The following authorization is done here: Fields: edit, view, required, mask, and partial
     * mask Groups: edit and view Actions: take action
     * </p>
     * 
     * <p>
     * Note additional checks are also done for fields that are part of a collection group. This
     * authorization is found in {@link org.kuali.rice.krad.uif.container.CollectionGroupBuilder}
     * </p>
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase#performLifecycleTask()
     */
    @Override
    protected void performLifecycleTask() {
        ViewModel model = (ViewModel) ViewLifecycle.getModel();
        Component component = (Component) getElementState().getElement();
        View view = ViewLifecycle.getView();
        ViewPresentationController presentationController = view.getPresentationController();
        ViewAuthorizer authorizer = view.getAuthorizer();

        // if user session is not established cannot perform authorization
        if (GlobalVariables.getUserSession() == null) {
            return;
        }

        Person user = GlobalVariables.getUserSession().getPerson();

        // if component not flagged for render no need to check auth and controller logic
        if (!component.isRender()) {
            return;
        }

        // check top level view edit authorization
        if (component instanceof View) {
            if (!Boolean.TRUE.equals(view.getReadOnly())) {
                boolean canEditView = authorizer.canEditView(view, model, user);
                if (canEditView) {
                    canEditView = presentationController.canEditView(view, model);
                }
                view.setReadOnly(!canEditView);
            }
        }

        // perform group authorization and presentation logic
        else if (component instanceof Group) {
            Group group = (Group) component;

            // if group is not hidden, do authorization for viewing the group
            if (!group.isHidden()) {
                boolean canViewGroup = authorizer.canViewGroup(view, model, group, group.getId(), user);
                if (canViewGroup) {
                    canViewGroup = presentationController.canViewGroup(view, model, group, group.getId());
                }
                group.setHidden(!canViewGroup);
                group.setRender(canViewGroup);
            }

            // if group is editable, do authorization for editing the group
            if (!Boolean.TRUE.equals(group.getReadOnly())) {
                boolean canEditGroup = authorizer.canEditGroup(view, model, group, group.getId(), user);
                if (canEditGroup) {
                    canEditGroup = presentationController.canEditGroup(view, model, group, group.getId());
                }
                group.setReadOnly(!canEditGroup);
            }
        }

        // perform field authorization and presentation logic
        else if (component instanceof Field && !(component instanceof ActionField)) {
            Field field = (Field) component;

            String propertyName = null;
            if (field instanceof DataBinding) {
                propertyName = ((DataBinding) field).getPropertyName();
            }

            // if field is not hidden, do authorization for viewing the field
            if (!field.isHidden()) {
                boolean canViewField = authorizer.canViewField(view, model, field, propertyName, user);
                if (canViewField) {
                    canViewField = presentationController.canViewField(view, model, field, propertyName);
                }
                field.setHidden(!canViewField);
                field.setRender(canViewField);
            }

            // if field is not readOnly, check edit authorization
            if (!Boolean.TRUE.equals(field.getReadOnly())) {
                // check field edit authorization
                boolean canEditField = authorizer.canEditField(view, model, field, propertyName, user);
                if (canEditField) {
                    canEditField = presentationController.canEditField(view, model, field, propertyName);
                }
                field.setReadOnly(!canEditField);
            }

            // if field is not already required, invoke presentation logic to determine if it should be
            if ((field.getRequired() == null) || !field.getRequired().booleanValue()) {
                // boolean fieldIsRequired = 
                presentationController.fieldIsRequired(view, model, field, propertyName);
            }

            if (field instanceof DataField) {
                DataField dataField = (DataField) field;

                // check for masking and mask authorization
                boolean canUnmaskValue = authorizer.canUnmaskField(view, model, dataField, dataField.getPropertyName(),
                        user);
                boolean canPartiallyUnmaskValue = authorizer.canPartialUnmaskField(view, model, dataField,
                        dataField.getPropertyName(), user);
                boolean isMasked = isMaskField(dataField);
                boolean isPartialMask = isPartialMaskField(dataField);

                if (isMasked && !canUnmaskValue)  {
                    dataField.setApplyMask(true);
                    dataField.setMaskFormatter(dataField.getDataFieldSecurity().getAttributeSecurity().
                            getMaskFormatter());
                }
                else if(isMasked && canUnmaskValue)  {
                    // do not mask
                }
                else if (isPartialMask && !canPartiallyUnmaskValue ) {
                    dataField.setApplyMask(true);
                    dataField.setMaskFormatter(
                            dataField.getDataFieldSecurity().getAttributeSecurity().getPartialMaskFormatter());
                }
            }
        }

        // perform action authorization and presentation logic
        else if (component instanceof ActionField || component instanceof Action) {
            Action action = null;
            if (component instanceof ActionField) {
                action = ((ActionField) component).getAction();
            } else {
                action = (Action) component;
            }

            boolean canTakeAction = authorizer.canPerformAction(view, model, action, action.getActionEvent(),
                    action.getId(), user);
            if (canTakeAction) {
                canTakeAction = presentationController.canPerformAction(view, model, action, action.getActionEvent(),
                        action.getId());
            }
            action.setRender(canTakeAction);
        }

        // perform widget authorization and presentation logic
        else if (component instanceof Widget) {
            Widget widget = (Widget) component;

            // if widget is not hidden, do authorization for viewing the widget
            if (!widget.isHidden()) {
                boolean canViewWidget = authorizer.canViewWidget(view, model, widget, widget.getId(), user);
                if (canViewWidget) {
                    canViewWidget = presentationController.canViewWidget(view, model, widget, widget.getId());
                }
                widget.setHidden(!canViewWidget);
                widget.setRender(canViewWidget);
            }

            // if widget is not readOnly, check edit authorization
            if (!Boolean.TRUE.equals(widget.getReadOnly())) {
                boolean canEditWidget = authorizer.canEditWidget(view, model, widget, widget.getId(), user);
                if (canEditWidget) {
                    canEditWidget = presentationController.canEditWidget(view, model, widget, widget.getId());
                }
                widget.setReadOnly(!canEditWidget);
            }
        }
    }

    /**
     *
     */
    private boolean isMaskField(DataField field) {
        if (field.getDataFieldSecurity() == null) {
            return false;
        }

        // check mask authz flag is set
        AttributeSecurity attributeSecurity = field.getDataFieldSecurity().getAttributeSecurity();
        if (attributeSecurity == null || !attributeSecurity.isMask()) {
            return false;
        }

        return true;
    }

    /**
     *
     */
    private boolean isPartialMaskField(DataField field) {
        if (field.getDataFieldSecurity() == null) {
            return false;
        }

        // check partial mask authz flag is set
        AttributeSecurity attributeSecurity = field.getDataFieldSecurity().getAttributeSecurity();
        if (attributeSecurity == null || !attributeSecurity.isPartialMask()) {
            return false;
        }

        return true;
    }

}
