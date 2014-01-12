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
package org.kuali.rice.krad.uif.util;

import java.util.Set;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.Widget;

class MockViewAuthorizer implements ViewAuthorizer {

    @Override
    public Set<String> getActionFlags(View view, ViewModel model, Person user, Set<String> actions) {
        return new java.util.HashSet<String>();
    }

    @Override
    public Set<String> getEditModes(View view, ViewModel model, Person user, Set<String> editModes) {
        return new java.util.HashSet<String>();
    }

    @Override
    public boolean canOpenView(View view, ViewModel model, Person user) {
        return true;
    }

    @Override
    public boolean canEditView(View view, ViewModel model, Person user) {
        return true;
    }

    @Override
    public boolean canUnmaskField(View view, ViewModel model, DataField field, String propertyName, Person user) {
        return true;
    }

    @Override
    public boolean canPartialUnmaskField(View view, ViewModel model, DataField field, String propertyName,
            Person user) {
        return true;
    }

    @Override
    public boolean canEditField(View view, ViewModel model, Field field, String propertyName, Person user) {
        return true;
    }

    @Override
    public boolean canViewField(View view, ViewModel model, Field field, String propertyName, Person user) {
        return true;
    }

    @Override
    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId, Person user) {
        return true;
    }

    @Override
    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId, Person user) {
        return true;
    }

    @Override
    public boolean canEditWidget(View view, ViewModel model, Widget widget, String widgetId, Person user) {
        return true;
    }

    @Override
    public boolean canViewWidget(View view, ViewModel model, Widget widget, String widgetId, Person user) {
        return true;
    }

    @Override
    public boolean canPerformAction(View view, ViewModel model, Action action, String actionEvent, String actionId,
            Person user) {
        return true;
    }

    @Override
    public boolean canEditLine(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Person user) {
        return true;
    }

    @Override
    public boolean canViewLine(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Person user) {
        return true;
    }

    @Override
    public boolean canEditLineField(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Field field, String propertyName, Person user) {
        return true;
    }

    @Override
    public boolean canViewLineField(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Field field, String propertyName, Person user) {
        return true;
    }

    @Override
    public boolean canPerformLineAction(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Action action, String actionEvent, String actionId,
            Person user) {
        return true;
    }

}
