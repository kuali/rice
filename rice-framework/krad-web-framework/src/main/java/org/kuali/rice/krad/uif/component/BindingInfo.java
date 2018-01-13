/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.uif.component;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.view.View;

/**
 * Provides binding configuration for an DataBinding component (attribute or
 * collection)
 *
 * <p>
 * From the binding configuration the binding path is determined (if not
 * manually set) and used to set the path in the UI or to get the value from the
 * model
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "bindingInfo", parent = "Uif-BindingInfo")
public class BindingInfo extends UifDictionaryBeanBase implements Serializable {
    private static final long serialVersionUID = -7389398061672136091L;

    private boolean bindToForm;
    private boolean bindToMap;

    private String bindingName;
    private String bindByNamePrefix;
    private String bindingObjectPath;

    private String collectionPath;

    private String bindingPath;

    public BindingInfo() {
        super();

        bindToForm = false;
        bindToMap = false;
    }

    /**
     * Sets up some default binding properties based on the view configuration
     * and the component's property name
     *
     * <p>
     * Sets the bindingName (if not set) to the given property name, and if the
     * binding object path has not been set uses the default binding object path
     * setup for the view
     * </p>
     *
     * @param view the view instance the component belongs to
     * @param propertyName name of the property (relative to the parent object) the component binds to
     */
    public void setDefaults(View view, String propertyName) {
        if (StringUtils.isBlank(bindingName)) {
            bindingName = propertyName;
        }

        if (StringUtils.isBlank(bindingObjectPath)) {
            bindingObjectPath = view.getDefaultBindingObjectPath();
        }
    }

    /**
     * Path to the property on the model the component binds to. Uses standard
     * dot notation for nested properties. If the binding path was manually set
     * it will be returned as it is, otherwise the path will be formed by using
     * the binding object path and the bind prefix
     *
     * <p>
     * e.g. Property name 'foo' on a model would have binding path "foo", while
     * property name 'name' of the nested model property 'account' would have
     * binding path "account.name"
     * </p>
     *
     * @return binding path
     */
    @BeanTagAttribute
    public String getBindingPath() {
        if (StringUtils.isNotBlank(bindingPath)) {
            return bindingPath;
        }

        String formedBindingPath = getBindingPathPrefix();

        if (StringUtils.isNotBlank(bindingName)) {
            if (bindToMap) {
                formedBindingPath += "[" + bindingName + "]";
            } else {
                if (StringUtils.isNotBlank(formedBindingPath)) {
                    formedBindingPath += ".";
                }
                formedBindingPath += bindingName;
            }
        }

        return formedBindingPath;
    }

    /**
     * Returns the binding prefix string that can be used to setup the binding
     * on <code>DataBinding</code> components that are children of the component
     * that contains the <code>BindingInfo</code>. The binding prefix is formed
     * like the binding path but without including the object path
     *
     * @return binding prefix for nested components
     */
    public String getBindingPrefixForNested() {
        String bindingPrefix = "";

        if (StringUtils.isNotBlank(bindByNamePrefix)) {
            bindingPrefix = bindByNamePrefix;
        }

        if (bindToMap) {
            bindingPrefix += "[" + bindingName + "]";
        } else {
            if (StringUtils.isNotBlank(bindingPrefix)) {
                bindingPrefix += ".";
            }
            bindingPrefix += bindingName;
        }

        return bindingPrefix;
    }

    /**
     * Returns the binding path that is formed by taking the binding configuration
     * of this <code>BindingInfo</code> instance with the given property path as the
     * binding name. This can be used to get the binding path when just a property
     * name is given that is assumed to be on the same parent object of the field with
     * the configured binding info
     *
     * <p>
     * Special check is done for org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX prefix
     * on the property name which indicates the property path is the full path and should
     * not be adjusted. Also, if the property is prefixed with
     * org.kuali.rice.krad.uif.UifConstants#FIELD_PATH_BIND_ADJUST_PREFIX, this indicates we should only append the
     * binding object path
     * </p>
     *
     * @param propertyPath path for property to return full binding path for
     * @return full binding path
     */
    public String getPropertyAdjustedBindingPath(String propertyPath) {
        if (propertyPath.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
            propertyPath = StringUtils.removeStart(propertyPath, UifConstants.NO_BIND_ADJUST_PREFIX);

            return propertyPath;
        }

        if (propertyPath.startsWith(UifConstants.FIELD_PATH_BIND_ADJUST_PREFIX)) {
            propertyPath = StringUtils.removeStart(propertyPath, UifConstants.FIELD_PATH_BIND_ADJUST_PREFIX);
        }

        String formedBindingPath = getBindingPathPrefix();

        //KULRICE-13096 avoid duplicate property paths
        if (propertyPath.startsWith(formedBindingPath)) {
            return propertyPath;
        }

        if (bindToMap) {
            formedBindingPath += "[" + propertyPath + "]";
        } else {
            if (StringUtils.isNotBlank(formedBindingPath)) {
                formedBindingPath += ".";
            }
            formedBindingPath += propertyPath;
        }

        return formedBindingPath;
    }

    /**
     * Returns the binding path prefix to affix before the binding path or property adjusted binding path.
     *
     * @return the binding path prefix to affix before a binding path.
     */
    public String getBindingPathPrefix() {
        String bindingPathPrefix = "";

        if (!bindToForm && StringUtils.isNotBlank(bindingObjectPath)) {
            bindingPathPrefix = bindingObjectPath;
        }

        if (StringUtils.isNotBlank(bindByNamePrefix)) {
            if (!bindByNamePrefix.startsWith("[") && StringUtils.isNotBlank(bindingPathPrefix)) {
                bindingPathPrefix += ".";
            }

            bindingPathPrefix += bindByNamePrefix;
        }

        return bindingPathPrefix;
    }

    /**
     * Helper method for adding a path to the binding prefix
     *
     * @param bindPrefix path to add
     */
    public void addToBindByNamePrefix(String bindPrefix) {
        if (StringUtils.isNotBlank(bindByNamePrefix) && StringUtils.isNotBlank(bindPrefix)) {
            bindByNamePrefix += "." + bindPrefix;
        } else {
            bindByNamePrefix = bindPrefix;
        }
    }

    /**
     * Setter for the binding path. Can be left blank in which the path will be
     * determined from the binding configuration
     *
     * @param bindingPath
     */
    public void setBindingPath(String bindingPath) {
        this.bindingPath = bindingPath;
    }

    /**
     * Indicates whether the component binds directly to the form (that is its
     * bindingName gives a property available through the form), or whether is
     * binds through a nested form object. If bindToForm is false, it is assumed
     * the component binds to the object given by the form property whose path
     * is configured by bindingObjectPath.
     *
     * @return true if component binds directly to form, false if it
     *         binds to a nested object
     */
    @BeanTagAttribute
    public boolean isBindToForm() {
        return this.bindToForm;
    }

    /**
     * Setter for the bind to form indicator
     *
     * @param bindToForm
     */
    public void setBindToForm(boolean bindToForm) {
        this.bindToForm = bindToForm;
    }

    /**
     * Gives the name of the property that the component binds to. The name can
     * be nested but not the full path, just from the parent object or in the
     * case of binding directly to the form from the form object
     *
     * <p>
     * If blank this will be set from the name field of the component
     * </p>
     *
     * @return name of the bind property
     */
    @BeanTagAttribute
    public String getBindingName() {
        return this.bindingName;
    }

    /**
     * Setter for the bind property name
     *
     * @param bindingName
     */
    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    /**
     * Prefix that will be used to form the binding path from the component
     * name. Typically used for nested collection properties
     *
     * @return binding prefix
     */
    @BeanTagAttribute
    public String getBindByNamePrefix() {
        return this.bindByNamePrefix;
    }

    /**
     * Setter for the prefix to use for forming the binding path by name
     *
     * @param bindByNamePrefix
     */
    public void setBindByNamePrefix(String bindByNamePrefix) {
        this.bindByNamePrefix = bindByNamePrefix;
    }

    /**
     * If field is part of a collection field, gives path to collection
     *
     * <p>
     * This is used for metadata purposes when getting finding the attribute
     * definition from the dictionary and is not used in building the final
     * binding path
     * </p>
     *
     * @return path to collection
     */
    public String getCollectionPath() {
        return this.collectionPath;
    }

    /**
     * Setter for the field's collection path (if part of a collection)
     *
     * @param collectionPath
     */
    public void setCollectionPath(String collectionPath) {
        this.collectionPath = collectionPath;
    }

    /**
     * For attribute fields that do not belong to the default form object (given
     * by the view), this field specifies the path to the object (on the form)
     * the attribute does belong to.
     *
     * <p>
     * e.g. Say we have an attribute field with property name 'number', that
     * belongs to the object given by the 'account' property on the form. The
     * form object path would therefore be set to 'account'. If the property
     * belonged to the object given by the 'document.header' property of the
     * form, the binding object path would be set to 'document.header'. Note if
     * the binding object path is not set for an attribute field (or any
     * <code>DataBinding</code> component), the binding object path configured
     * on the <code>View</code> will be used (unless bindToForm is set to true,
     * where is assumed the property is directly available from the form).
     * </p>
     *
     * @return path to object from form
     */
    @BeanTagAttribute
    public String getBindingObjectPath() {
        return this.bindingObjectPath;
    }

    /**
     * Setter for the object path on the form
     *
     * @param bindingObjectPath
     */
    public void setBindingObjectPath(String bindingObjectPath) {
        this.bindingObjectPath = bindingObjectPath;
    }

    /**
     * Indicates whether the parent object for the property that we are binding
     * to is a Map. If true the binding path will be adjusted to use the map key
     * syntax
     *
     * @return true if the property binds to a map, false if it does not
     */
    @BeanTagAttribute
    public boolean isBindToMap() {
        return this.bindToMap;
    }

    /**
     * Setter for the bind to map indicator
     *
     * @param bindToMap
     */
    public void setBindToMap(boolean bindToMap) {
        this.bindToMap = bindToMap;
    }
}
