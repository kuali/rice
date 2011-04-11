/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.uif.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.mapping.Collection;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.AttributeField;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewModelUtils {

    public static Class<?> getPropertyType(View view, String propertyPath) {
        Class<?> propertyType = null;

        if (StringUtils.isBlank(propertyPath)) {
            return propertyType;
        }

        // TODO: make this do partial matching & collection matching

        // check if property path matches one of the modelClass entries
        Map<String, Class<?>> modelClasses = view.getAbstractTypeClasses();
        for (String path : modelClasses.keySet()) {
            if (StringUtils.equals(path, propertyPath)) {
                propertyType = modelClasses.get(path);
            }
        }

        // if not found in the view's map, get the type based on the form
        if (propertyType == null) {
            propertyType = ObjectPropertyUtils.getPropertyType(view.getFormClass(), propertyPath);
        }

        return propertyType;
    }

    public static String getParentObjectPath(AttributeField field) {
        String parentObjectPath = "";

        String objectPath = field.getBindingInfo().getBindingObjectPath();
        String propertyPrefix = field.getBindingInfo().getBindByNamePrefix();

        if (!field.getBindingInfo().isBindToForm() && StringUtils.isNotBlank(objectPath)) {
            parentObjectPath = objectPath;
        }

        if (StringUtils.isNotBlank(propertyPrefix)) {
            if (StringUtils.isNotBlank(parentObjectPath)) {
                parentObjectPath += ".";
            }

            parentObjectPath += propertyPrefix;
        }

        return parentObjectPath;
    }

    public static Class<?> getParentObjectClassForMetadata(View view, AttributeField field) {
        String parentObjectPath = getParentObjectPath(field);

        return getPropertyType(view, parentObjectPath);
    }

    public static Object getParentObjectForMetadata(View view, Object model, AttributeField field) {
        // default to model as parent
        Object parentObject = model;

        String parentObjectPath = getParentObjectPath(field);
        if (StringUtils.isNotBlank(parentObjectPath)) {
            parentObject = ObjectPropertyUtils.getPropertyValue(model, parentObjectPath);

            // attempt to create new instance if parent is null or is a
            // collection or map
            if ((parentObject == null) || Collection.class.isAssignableFrom(parentObject.getClass())
                    || Map.class.isAssignableFrom(parentObject.getClass())) {
                try {
                    Class<?> parentObjectClass = getPropertyType(view, parentObjectPath);
                    parentObject = parentObjectClass.newInstance();
                }
                catch (InstantiationException e) {
                    // swallow exception and let null be returned
                }
                catch (IllegalAccessException e) {
                    // swallow exception and let null be returned
                }
            }
        }

        return parentObject;
    }

}
