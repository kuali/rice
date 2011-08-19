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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.ValueConfiguredControl;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Utilities for collection layout managers
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionLayoutUtils {

    public static void prepareSelectFieldForLine(Field selectField, CollectionGroup collectionGroup, String lineBindingPath,
            Object line) {
        // if select property name set use as property name for select field
        String selectPropertyName = collectionGroup.getSelectPropertyName();
        if (StringUtils.isNotBlank(selectPropertyName)) {
            // if select property contains form prefix, will bind to form and not each line
            if (selectPropertyName.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
                selectPropertyName = StringUtils.removeStart(selectPropertyName, UifConstants.NO_BIND_ADJUST_PREFIX);
                ((DataBinding) selectField).getBindingInfo().setBindingName(selectPropertyName);
                ((DataBinding) selectField).getBindingInfo().setBindToForm(true);

                setControlValueToLineIdentifier(selectField, line);
            } else {
                ((DataBinding) selectField).getBindingInfo().setBindingName(selectPropertyName);
                ((DataBinding) selectField).getBindingInfo().setBindByNamePrefix(lineBindingPath);
            }
        } else {
            // select property name not given, use UifFormBase#selectedCollectionLines
            String collectionLineKey = KRADUtils.translateToMapSafeKey(
                    collectionGroup.getBindingInfo().getBindingPath());
            String selectBindingPath = UifPropertyPaths.SELECTED_COLLECTION_LINES + "['" + collectionLineKey + "']";

            ((DataBinding) selectField).getBindingInfo().setBindingName(selectBindingPath);
            ((DataBinding) selectField).getBindingInfo().setBindToForm(true);

            setControlValueToLineIdentifier(selectField, line);
        }
    }

    protected static void setControlValueToLineIdentifier(Field selectField, Object line) {
        if (selectField instanceof AttributeField) {
            Control selectControl = ((AttributeField) selectField).getControl();

            selectControl.addStyleClass("kr-select-line");

            if ((selectControl != null) && (selectControl instanceof ValueConfiguredControl)) {
                String lineIdentifier =
                        KRADServiceLocatorWeb.getDataObjectMetaDataService().getDataObjectIdentifierString(line);
                ((ValueConfiguredControl) selectControl).setValue(lineIdentifier);
            }
        }
    }
}
