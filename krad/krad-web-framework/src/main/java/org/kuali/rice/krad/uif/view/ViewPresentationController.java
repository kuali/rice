/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.view;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.widget.Widget;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.Set;

/**
 * Configured for a <code>View</code> instance to provide conditional logic
 * based on any variable (view configuration, system parameters, ...) that does
 * not depend on the current user
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewPresentationController {

    public Set<String> getActionFlags(View view, UifFormBase model);

    public Set<String> getEditModes(View view, UifFormBase model);

    public boolean canEditField(View view, ViewModel model, Field field, String propertyName);

    public boolean canViewField(View view, ViewModel model, Field field, String propertyName);

    public boolean fieldIsRequired(View view, ViewModel model, Field field, String propertyName);

    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId);

    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId);

    public boolean canEditWidget(View view, ViewModel model, Widget widget, String widgetId);

    public boolean canViewWidget(View view, ViewModel model, Widget widget, String widgetId);

    public boolean canTakeAction(View view, ViewModel model, ActionField actionField, String actionEvent,
            String actionId);

    public boolean canEditLine(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line);

    public boolean canViewLine(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line);

    public boolean canEditLineField(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Field field, String propertyName);

    public boolean canViewLineField(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, Field field, String propertyName);

    public boolean canTakeLineAction(View view, ViewModel model, CollectionGroup collectionGroup,
            String collectionPropertyName, Object line, ActionField actionField, String actionEvent, String actionId);

}
