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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.core.BindingInfo;
import org.kuali.rice.kns.uif.field.AttributeField;

/**
 * Provides methods for getting property values, types, and paths within the
 * context of a <code>View</code>
 * 
 * <p>
 * The view provides a special map named 'abstractTypeClasses' that indicates
 * concrete classes that should be used in place of abstract property types that
 * are encountered on the object graph. This classes takes into account that map
 * while dealing with properties. e.g. suppose we have propertyPath
 * 'document.name' on the form, with the type of the document property set to
 * the interface Document. Using class introspection we would get back the
 * interface type for document and this would not be able to get the property
 * type for name. Using the view map, we can replace document with a concrete
 * class and then use it to get the name property
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewModelUtils {

    public static Class<?> getPropertyType(View view, String propertyPath) {
        Class<?> propertyType = null;

        if (StringUtils.isBlank(propertyPath)) {
            return propertyType;
        }

        // in case of partial match, holds the class that matched and the
        // property so we can get by reflection
        Class<?> modelClass = view.getFormClass();
        String modelProperty = propertyPath;

        int bestMatchLength = 0;

        // removed collection indexes from path for matching
        String flattenedPropertyPath = propertyPath.replaceAll("\\[.+\\]", "");

        // check if property path matches one of the modelClass entries
        Map<String, Class<?>> modelClasses = view.getAbstractTypeClasses();
        for (String path : modelClasses.keySet()) {
            // full match
            if (StringUtils.equals(path, flattenedPropertyPath)) {
                propertyType = modelClasses.get(path);
                break;
            }

            // partial match
            if (flattenedPropertyPath.startsWith(path) && (path.length() > bestMatchLength)) {
                bestMatchLength = path.length();

                modelClass = modelClasses.get(path);
                modelProperty = StringUtils.removeStart(flattenedPropertyPath, path);
                modelProperty = StringUtils.removeStart(modelProperty, ".");
            }
        }

        // if full match not found, get type based on reflection
        if (propertyType == null) {
            propertyType = ObjectPropertyUtils.getPropertyType(modelClass, modelProperty);
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
    
    public static Object getValue(View view, Object model, String propertyName, BindingInfo bindingInfo){
        Object value = null;
        if(bindingInfo == null && StringUtils.isNotBlank(propertyName)){
            if(StringUtils.isNotBlank(view.getDefaultBindingObjectPath())){
                value = ObjectPropertyUtils.getPropertyValue(model, view.getDefaultBindingObjectPath() + "." + propertyName);
            }
            else{
                value = ObjectPropertyUtils.getPropertyValue(model, propertyName);
            }
            
        }
        else if(bindingInfo != null){
            if(StringUtils.isNotBlank(bindingInfo.getBindingPath()) && !bindingInfo.getBindingPath().equals("null")){
                value = ObjectPropertyUtils.getPropertyValue(model, bindingInfo.getBindingPath());
            }
        }
        return value;
    }

}
