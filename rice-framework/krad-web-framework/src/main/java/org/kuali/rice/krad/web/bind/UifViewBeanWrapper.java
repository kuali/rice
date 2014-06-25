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
package org.kuali.rice.krad.web.bind;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.omg.CORBA.Request;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class is a top level BeanWrapper for a UIF View Model.
 *
 * <p>Registers custom property editors configured on the field associated with the property name for which
 * we are getting or setting a value. In addition determines if the field requires encryption and if so applies
 * the {@link UifEncryptionPropertyEditorWrapper}</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifViewBeanWrapper extends BeanWrapperImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifViewBeanWrapper.class);

    // this stores all properties this wrapper has already checked
    // with the view so the service isn't called again
    private Set<String> processedProperties;

    private final UifBeanPropertyBindingResult bindingResult;

    public UifViewBeanWrapper(ViewModel model, UifBeanPropertyBindingResult bindingResult) {
        super(model);

        this.bindingResult = bindingResult;
        this.processedProperties = new HashSet<String>();
    }

    /**
     * Attempts to find a corresponding data field for the given property name in the current view or previous view,
     * then if the field has a property editor configured it is registered with the property editor registry to use
     * for this property.
     *
     * @param propertyName name of the property to find field and editor for
     */
    private void registerEditorFromView(String propertyName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to find property editor for property '" + propertyName + "'");
        }

        // check if we already processed this property for this BeanWrapper instance
        if (processedProperties.contains(propertyName)) {
            return;
        }

        ViewPostMetadata viewPostMetadata = ((ViewModel) getWrappedInstance()).getViewPostMetadata();
        if (viewPostMetadata == null) {
            return;
        }

        PropertyEditor propertyEditor = viewPostMetadata.getFieldEditor(propertyName);
        if (propertyEditor != null) {
            registerCustomEditor(null, propertyName, propertyEditor);
        }

        processedProperties.add(propertyName);
    }

    /**
     * Finds a property editor for the given propert name, checking for a custom registered editor and editors
     * by type.
     *
     * @param propertyName name of the property to get editor for
     * @return property editor instance
     */
    protected PropertyEditor findEditorForPropertyName(String propertyName) {
        Class<?> clazz = getPropertyType(propertyName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting retrieval of property editor using class '"
                    + clazz
                    + "' and property path '"
                    + propertyName
                    + "'");
        }

        PropertyEditor editor = findCustomEditor(clazz, propertyName);
        if (editor == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No custom property editor found using class '"
                        + clazz
                        + "' and property path '"
                        + propertyName
                        + "'. Attempting to find default property editor class.");
            }
            editor = getDefaultEditor(clazz);
        }

        return editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
            if (pd != null) {
                return pd.getPropertyType();
            }

            // Maybe an indexed/mapped property...
            Object value = super.getPropertyValue(propertyName);
            if (value != null) {
                return value.getClass();
            }

            // Check to see if there is a custom editor,
            // which might give an indication on the desired target type.
            Class<?> editorType = guessPropertyTypeFromEditors(propertyName);
            if (editorType != null) {
                return editorType;
            }
        } catch (InvalidPropertyException ex) {
            // Consider as not determinable.
        }

        return null;
    }

    /**
     * Overridden to copy property editor registration to the new bean wrapper.
     *
     * {@inheritDoc}
     */
    @Override
    protected BeanWrapperImpl getBeanWrapperForPropertyPath(String propertyPath) {
        BeanWrapperImpl beanWrapper = super.getBeanWrapperForPropertyPath(propertyPath);

        PropertyTokenHolder tokens = getPropertyNameTokens(propertyPath);
        String canonicalName = tokens.canonicalName;

        int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(canonicalName);
        if (pos != -1) {
            canonicalName = canonicalName.substring(0, pos);
        }

        copyCustomEditorsTo(beanWrapper, canonicalName);

        return beanWrapper;
    }

    /**
     * Overridden to register any property editor for the property before the value is pulled.
     *
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        registerEditorFromView(propertyName);

        Object value = null;
        try {
            value = super.getPropertyValue(propertyName);
        } catch (NullValueInNestedPathException e) {
            // swallow null values in path and return null as the value
        }

        return value;
    }

    /**
     * Overridden to perform processing before and after the value is set.
     *
     * <p>First binding security is checked to determine whether the path allows binding. Next,
     * access security is checked to determine whether the value needs decrypted. Finally, if
     * change tracking is enabled, the original value is compared with the new for indicating a
     * modified path.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        boolean isPropertyAccessible = checkPropertyBindingAccess(pv.getName());
        if (!isPropertyAccessible) {
            return;
        }

        Object value = processValueBeforeSet(pv.getName(), pv.getValue());

        pv = new PropertyValue(pv, value);

        // save off the original value if we are change tracking
        boolean originalValueSaved = true;
        Object originalValue = null;
        if (bindingResult.isChangeTracking()) {
            try {
                originalValue = getPropertyValue(pv.getName());
            } catch (Exception e) {
                // be failsafe here, if an exception happens here then we can't make any assumptions about whether
                // the property value changed or not
                originalValueSaved = false;
            }
        }

        // set the actual property value
        super.setPropertyValue(pv);

        // if we are change tracking and we saved original value, check if it's modified
        if (bindingResult.isChangeTracking() && originalValueSaved) {
            try {
                Object newValue = getPropertyValue(pv.getName());
                if (ObjectUtils.notEqual(originalValue, newValue)) {
                    // if they are not equal, it's been modified!
                    bindingResult.addModifiedPath(pv.getName());
                }
            } catch (Exception e) {
                // failsafe here as well
            }
        }
    }

    /**
     * Overridden to perform processing before and after the value is set.
     *
     * <p>First binding security is checked to determine whether the path allows binding. Next,
     * access security is checked to determine whether the value needs decrypted. Finally, if
     * change tracking is enabled, the original value is compared with the new for indicating a
     * modified path.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        boolean isPropertyAccessible = checkPropertyBindingAccess(propertyName);
        if (!isPropertyAccessible) {
            return;
        }

        value = processValueBeforeSet(propertyName, value);

        // save off the original value
        boolean originalValueSaved = true;
        Object originalValue = null;
        try {
            originalValue = getPropertyValue(propertyName);
        } catch (Exception e) {
            // be failsafe here, if an exception happens here then we can't make any assumptions about whether
            // the property value changed or not
            originalValueSaved = false;
        }

        // set the actual property value
        super.setPropertyValue(propertyName, value);

        // only check if it's modified if we were able to save the original value
        if (originalValueSaved) {
            try {
                Object newValue = getPropertyValue(propertyName);
                if (ObjectUtils.notEqual(originalValue, newValue)) {
                    // if they are not equal, it's been modified!
                    bindingResult.addModifiedPath(propertyName);
                }
            } catch (Exception e) {
                // failsafe here as well
            }
        }
    }

    /**
     * Determines whether request binding is allowed for the given property name/path.
     *
     * <p>Binding access is determined by default based on the view's post metadata. A set of
     * accessible binding paths (populated during the view lifecycle) is maintained within this data.
     * Overrides can be specified using the annotations {@link org.kuali.rice.krad.web.bind.RequestProtected}
     * and {@link org.kuali.rice.krad.web.bind.RequestAccessible}.</p>
     *
     * <p>If the path is not accessible, it is recorded in the binding results suppressed fields. Controller
     * methods can accept the binding result and further handle these properties if necessary.</p>
     *
     * @param propertyName name/path of the property to check binding access for
     * @return boolean true if binding access is allowed, false if not allowed
     */
    protected boolean checkPropertyBindingAccess(String propertyName) {
        boolean isAccessible = false;

        // check for explicit property annotations that indicate access
        Boolean bindingAnnotationAccess = checkBindingAnnotationsInPath(propertyName);
        if (bindingAnnotationAccess != null) {
            isAccessible = bindingAnnotationAccess.booleanValue();
        } else {
            // default access, must be in view's accessible binding paths
            ViewPostMetadata viewPostMetadata = ((ViewModel) getWrappedInstance()).getViewPostMetadata();
            if ((viewPostMetadata != null) && (viewPostMetadata.getAccessibleBindingPaths() != null)) {
                isAccessible = viewPostMetadata.getAccessibleBindingPaths().contains(propertyName);
            }
        }

        if (!isAccessible) {
            LOG.debug("Request parameter sent for inaccessible binding path: " + propertyName);

            bindingResult.recordSuppressedField(propertyName);
        }

        return isAccessible;
    }

    /**
     * Determines whether one of the binding annotations is present within the given property path, and if
     * so returns whether access should be granted based on those annotation(s).
     *
     * <p>Binding annotations may occur anywhere in the property path. For example, if the path is 'object.field1',
     * a binding annotation may be present on the 'object' property or the 'field1' property. If multiple annotations
     * are found in the path, the annotation at the deepest level is taken. If both the protected and accessible
     * annotation are found at the same level, the protected access is used.</p>
     *
     * @param propertyPath path to look for annotations
     * @return Boolean true if an annotation is found and the access is allowed, false if an annotation is found
     * and the access is protected, null if no annotations where found in the path
     */
    protected Boolean checkBindingAnnotationsInPath(String propertyPath) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        while (!StringUtils.isEmpty(propertyPath)) {
            String nestedPath = ObjectPropertyUtils.getPathTail(propertyPath);
            String parentPropertyPath = ObjectPropertyUtils.removePathTail(propertyPath);

            Class<?> parentPropertyClass = getWrappedClass();

            // for nested paths, we need to get the class of the immediate parent
            if (!StringUtils.isEmpty(parentPropertyPath)) {
                parentPropertyClass = ObjectPropertyUtils.getPropertyType(getWrappedInstance(), parentPropertyPath);
            }

            // remove index or map key to get the correct property name
            if (org.apache.commons.lang.StringUtils.endsWith(nestedPath, "]")) {
                nestedPath = org.apache.commons.lang.StringUtils.substringBefore(nestedPath, "[");
            }

            RequestProtected protectedAnnotation = (RequestProtected) CopyUtils.getFieldAnnotation(parentPropertyClass,
                    nestedPath, RequestProtected.class);
            if ((protectedAnnotation != null) && annotationMatchesRequestMethod(protectedAnnotation.method(),
                    request.getMethod())) {
                return Boolean.FALSE;
            }

            RequestAccessible accessibleAnnotation = (RequestAccessible) CopyUtils.getFieldAnnotation(
                    parentPropertyClass, nestedPath, RequestAccessible.class);
            if (accessibleAnnotation != null) {
                boolean isAnnotationRequestMethod = annotationMatchesRequestMethod(accessibleAnnotation.method(),
                        request.getMethod());
                boolean isAnnotationMethodToCalls = annotationMatchesMethodToCalls(accessibleAnnotation.methodToCalls(),
                        request.getParameter(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME));
                if (isAnnotationRequestMethod && isAnnotationMethodToCalls) {
                    //((UifFormBase) this.bindingResult.getTarget()).getMethodToCall())) {
                    return Boolean.TRUE;
                }
            }

            propertyPath = parentPropertyPath;
        }

        // if its a GET request and there was no annotation found, then we still want to disallow
        if(request.getMethod().equalsIgnoreCase(RequestMethod.GET.name())) {
            return Boolean.FALSE;
        }

        return null;
    }

    /**
     * Indicates whether one of the given request accessible methods to call in the given array matches the
     * actual methodToCall of the request.
     *
     * @param annotationMethodToCalls array of request accessible methods to call to check against
     * @param methodToCall method to call of the request
     * @return boolean true if one of the annotation methods to call match, false if none match
     */
    protected boolean annotationMatchesMethodToCalls(String[] annotationMethodToCalls, String methodToCall) {
        // empty array of methods should match all
        if ((annotationMethodToCalls == null) || (annotationMethodToCalls.length == 0)) {
            return true;
        }

        for (String annotationMethodToCall : annotationMethodToCalls) {
            if (org.apache.commons.lang.StringUtils.equals(annotationMethodToCall, methodToCall)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates whether one of the given request methods in the given array matches the actual method of
     * the request.
     *
     * @param annotationMethods array of request methods to check
     * @param requestMethod method of the request to match on
     * @return boolean true if one of the annotation methods match, false if none match
     */
    protected boolean annotationMatchesRequestMethod(RequestMethod[] annotationMethods, String requestMethod) {
        // empty array of methods should match all
        if ((annotationMethods == null) || (annotationMethods.length == 0)) {
            return true;
        }

        for (RequestMethod annotationMethod : annotationMethods) {
            if (org.apache.commons.lang.StringUtils.equals(annotationMethod.name(), requestMethod)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Registers any custom property editor for the property name/path, converts empty string values to null, and
     * calls helper method to decrypt secure values.
     *
     * @param propertyName name of the property
     * @param value value of the property to process
     * @return updated (possibly) property value
     */
    protected Object processValueBeforeSet(String propertyName, Object value) {
        registerEditorFromView(propertyName);

        Object processedValue = value;

        // Convert blank string values to null so empty strings are not set on the form as values (useful for legacy
        // checks) Jira: KULRICE-11424
        if (value instanceof String) {
            String propertyValue = (String) value;

            if (StringUtils.isEmpty(propertyValue)) {
                processedValue = null;
            } else {
                processedValue = decryptValueIfNecessary(propertyName, propertyValue);
            }
        }

        return processedValue;
    }

    /**
     * If the given property name is secure, decrypts the value by calling the encryption service.
     *
     * @param propertyName name of the property
     * @param propertyValue value of the property
     * @return String decrypted property value (or original value if not secure)
     */
    protected String decryptValueIfNecessary(String propertyName, String propertyValue) {
        String decryptedPropertyValue = propertyValue;

        if (propertyValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
            propertyValue = org.apache.commons.lang.StringUtils.removeEnd(propertyValue,
                    EncryptionService.ENCRYPTION_POST_PREFIX);
        }

        if (isSecure(getWrappedClass(), propertyName)) {
            try {
                if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                    decryptedPropertyValue = CoreApiServiceLocator.getEncryptionService().decrypt(propertyValue);
                }
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        return decryptedPropertyValue;
    }

    /**
     * Checks whether the given property is secure.
     *
     * @param wrappedClass class the property is associated with
     * @param propertyPath path to the property
     * @return boolean true if the property is secure, false if not
     */
    protected boolean isSecure(Class<?> wrappedClass, String propertyPath) {
        if (KRADServiceLocatorWeb.getDataObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(
                wrappedClass, propertyPath)) {
            return true;
        }

        BeanWrapperImpl beanWrapper;
        try {
            beanWrapper = getBeanWrapperForPropertyPath(propertyPath);
        } catch (NotReadablePropertyException nrpe) {
            LOG.debug("Bean wrapper was not found for "
                    + propertyPath
                    + ", but since it cannot be accessed it will not be set as secure.", nrpe);
            return false;
        }

        if (org.apache.commons.lang.StringUtils.isNotBlank(beanWrapper.getNestedPath())) {
            PropertyTokenHolder tokens = getPropertyNameTokens(propertyPath);
            String nestedPropertyPath = org.apache.commons.lang.StringUtils.removeStart(tokens.canonicalName,
                    beanWrapper.getNestedPath());

            return isSecure(beanWrapper.getWrappedClass(), nestedPropertyPath);
        }

        return false;
    }

    /**
     * Parse the given property name into the corresponding property name tokens.
     *
     * @param propertyName the property name to parse
     * @return representation of the parsed property tokens
     */
    private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
        PropertyTokenHolder tokens = new PropertyTokenHolder();
        String actualName = null;
        List<String> keys = new ArrayList<String>(2);
        int searchIndex = 0;
        while (searchIndex != -1) {
            int keyStart = propertyName.indexOf(PROPERTY_KEY_PREFIX, searchIndex);
            searchIndex = -1;
            if (keyStart != -1) {
                int keyEnd = propertyName.indexOf(PROPERTY_KEY_SUFFIX, keyStart + PROPERTY_KEY_PREFIX.length());
                if (keyEnd != -1) {
                    if (actualName == null) {
                        actualName = propertyName.substring(0, keyStart);
                    }
                    String key = propertyName.substring(keyStart + PROPERTY_KEY_PREFIX.length(), keyEnd);
                    if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
                        key = key.substring(1, key.length() - 1);
                    }
                    keys.add(key);
                    searchIndex = keyEnd + PROPERTY_KEY_SUFFIX.length();
                }
            }
        }
        tokens.actualName = (actualName != null ? actualName : propertyName);
        tokens.canonicalName = tokens.actualName;
        if (!keys.isEmpty()) {
            tokens.canonicalName += PROPERTY_KEY_PREFIX +
                    StringUtils.collectionToDelimitedString(keys, PROPERTY_KEY_SUFFIX + PROPERTY_KEY_PREFIX) +
                    PROPERTY_KEY_SUFFIX;
            tokens.keys = StringUtils.toStringArray(keys);
        }
        return tokens;
    }

    private static class PropertyTokenHolder {

        public String canonicalName;

        public String actualName;

        public String[] keys;
    }
}
