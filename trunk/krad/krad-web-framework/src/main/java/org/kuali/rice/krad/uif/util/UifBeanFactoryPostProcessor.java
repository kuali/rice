/**
 * Copyright 2005-2012 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Configurable;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Post processes the bean factory to handle UIF property expressions
 *
 * <p>
 * Conditional logic can be implemented with the UIF dictionary by means of property expressions. These are
 * expressions that follow SPEL and can be given as the value for a property using the @{} placeholder. Since such
 * a value would cause an exception when creating the object if the property is a non-string type (value cannot be
 * converted), we need to move those expressions to a Map for processing, and then remove the original property
 * configuration containing the expression. The expressions are then evaluated during the view apply model phase and
 * the result is set as the value for the corresponding property.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private static final Log LOG = LogFactory.getLog(UifBeanFactoryPostProcessor.class);

    /**
     * Iterates through all beans in the factory and invokes processing for expressions
     *
     * @param beanFactory - bean factory instance to process
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Set<String> processedBeanNames = new HashSet<String>();

        LOG.info("Beginning post processing of bean factory for UIF expressions");

        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (int i = 0; i < beanNames.length; i++) {
            String beanName = beanNames[i];
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

            processBeanDefinition(beanName, beanDefinition, beanFactory, processedBeanNames);
        }

        LOG.info("Finished post processing of bean factory for UIF expressions");
    }

    /**
     * If the bean class is type Component, LayoutManager, or BindingInfo, iterate through configured property values
     * and check for expressions
     *
     * <p>
     * If a expression is found for a property, it is added to the 'propertyExpressions' map and then the original
     * property value is removed to prevent binding errors (when converting to a non string type)
     * </p>
     *
     * @param beanName - name of the bean in the factory (only set for top level beans, not nested)
     * @param beanDefinition - bean definition to process for expressions
     * @param beanFactory - bean factory being processed
     */
    protected void processBeanDefinition(String beanName, BeanDefinition beanDefinition,
            ConfigurableListableBeanFactory beanFactory, Set<String> processedBeanNames) {
        Class<?> beanClass = getBeanClass(beanDefinition, beanFactory);
        if ((beanClass == null) || !Configurable.class.isAssignableFrom(beanClass)) {
            return;
        }

        if (processedBeanNames.contains(beanName)) {
            return;
        }

        LOG.debug("Processing bean name '" + beanName + "'");

        MutablePropertyValues pvs = beanDefinition.getPropertyValues();

        if (pvs.getPropertyValue(UifPropertyPaths.PROPERTY_EXPRESSIONS) != null) {
            // already processed so skip (could be reloading dictionary)
            return;
        }

        Map<String, String> propertyExpressions = new ManagedMap<String, String>();
        Map<String, String> parentPropertyExpressions = getPropertyExpressionsFromParent(beanDefinition.getParentName(),
                beanFactory, processedBeanNames);
        boolean parentExpressionsExist = !parentPropertyExpressions.isEmpty();

        PropertyValue[] pvArray = pvs.getPropertyValues();
        for (PropertyValue pv : pvArray) {
            if (hasExpression(pv.getValue())) {
                // process expression
                String strValue = getStringValue(pv.getValue());
                propertyExpressions.put(pv.getName(), strValue);

                // remove property value so expression will not cause binding exception
                pvs.removePropertyValue(pv.getName());
            } else {
                // process nested objects
                Object newValue = processPropertyValue(pv.getName(), pv.getValue(), parentPropertyExpressions,
                        propertyExpressions, beanFactory, processedBeanNames);
                pvs.removePropertyValue(pv.getName());
                pvs.addPropertyValue(pv.getName(), newValue);
            }

            // removed expression (if exists) from parent map since the property was set on child
            if (parentPropertyExpressions.containsKey(pv.getName())) {
                parentPropertyExpressions.remove(pv.getName());
            }

            // if property is nested, need to override any parent expressions set on nested beans
            if (StringUtils.contains(pv.getName(), ".")) {
                //removeParentExpressionsOnNested(pv.getName(), pvs, beanDefinition.getParentName(), beanFactory);
            }
        }

        if (!propertyExpressions.isEmpty() || parentExpressionsExist) {
            // merge two maps
            ManagedMap<String, String> mergedPropertyExpressions = new ManagedMap<String, String>();
            mergedPropertyExpressions.setMergeEnabled(false);
            mergedPropertyExpressions.putAll(parentPropertyExpressions);
            mergedPropertyExpressions.putAll(propertyExpressions);

            pvs.addPropertyValue(UifPropertyPaths.PROPERTY_EXPRESSIONS, mergedPropertyExpressions);
        }

        if (StringUtils.isNotBlank(beanName)) {
            processedBeanNames.add(beanName);
        }
    }

    protected void removeParentExpressionsOnNested(String propertyName, MutablePropertyValues pvs,
            String parentBeanName, ConfigurableListableBeanFactory beanFactory) {
        BeanDefinition parentBeanDefinition = beanFactory.getMergedBeanDefinition(parentBeanName);

        // TODO: this only handles one level of nesting
        MutablePropertyValues parentPvs = parentBeanDefinition.getPropertyValues();
        PropertyValue[] pvArray = parentPvs.getPropertyValues();
        for (PropertyValue pv : pvArray) {
            boolean isNameMatch = false;
            String nestedPropertyName = "";
            if (propertyName.startsWith(pv.getName())) {
                nestedPropertyName = StringUtils.removeStart(propertyName, pv.getName());
                if (nestedPropertyName.startsWith(".")) {
                    nestedPropertyName = StringUtils.removeStart(nestedPropertyName, ".");
                    isNameMatch = true;
                }
            }

            // if property name from parent matches and is a bean definition, check for property expressions map
            if (isNameMatch && ((pv.getValue() instanceof BeanDefinition) || (pv
                    .getValue() instanceof BeanDefinitionHolder))) {
                BeanDefinition propertyBeanDefinition;
                if (pv.getValue() instanceof BeanDefinition) {
                    propertyBeanDefinition = (BeanDefinition) pv.getValue();
                } else {
                    propertyBeanDefinition = ((BeanDefinitionHolder) pv.getValue()).getBeanDefinition();
                }

                MutablePropertyValues nestedPvs = propertyBeanDefinition.getPropertyValues();
                if (nestedPvs.contains(UifPropertyPaths.PROPERTY_EXPRESSIONS)) {
                    PropertyValue propertyExpressionsPV = nestedPvs.getPropertyValue(
                            UifPropertyPaths.PROPERTY_EXPRESSIONS);
                    if (propertyExpressionsPV != null) {
                        Object value = propertyExpressionsPV.getValue();
                        if ((value != null) && (value instanceof ManagedMap)) {
                            Map<String, String> nestedPropertyExpressions = (ManagedMap) value;
                            if (nestedPropertyExpressions.containsKey(nestedPropertyName)) {
                                // need to make copy of property value with expression removed from map
                                ManagedMap<String, String> copiedPropertyExpressions = new ManagedMap<String, String>();
                                copiedPropertyExpressions.setMergeEnabled(false);
                                copiedPropertyExpressions.putAll(nestedPropertyExpressions);
                                copiedPropertyExpressions.remove(nestedPropertyName);

                                BeanDefinition copiedBeanDefinition = new GenericBeanDefinition(propertyBeanDefinition);
                                copiedBeanDefinition.getPropertyValues().add(UifPropertyPaths.PROPERTY_EXPRESSIONS,
                                        copiedPropertyExpressions);

                                pvs.add(pv.getName(), copiedBeanDefinition);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the class for the object that will be created from the bean definition. Since the class might not
     * be configured on the bean definition, but by a parent, each parent bean definition is recursively checked for
     * a class until one is found
     *
     * @param beanDefinition - bean definition to get class for
     * @param beanFactory - bean factory that contains the bean definition
     * @return Class<?> class configured for the bean definition, or null
     */
    protected Class<?> getBeanClass(BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {
        if (StringUtils.isNotBlank(beanDefinition.getBeanClassName())) {
            try {
                return Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                // swallow exception and return null so bean is not processed
                return null;
            }
        } else if (StringUtils.isNotBlank(beanDefinition.getParentName())) {
            BeanDefinition parentBeanDefinition = beanFactory.getBeanDefinition(beanDefinition.getParentName());
            if (parentBeanDefinition != null) {
                return getBeanClass(parentBeanDefinition, beanFactory);
            }
        }

        return null;
    }

    /**
     * Retrieves the property expressions map set on the bean with given name. If the bean has not been processed
     * by the bean factory post processor, that is done before retrieving the map
     *
     * @param parentBeanName - name of the parent bean to retrieve map for (if empty a new map will be returned)
     * @param beanFactory - bean factory to retrieve bean definition from
     * @param processedBeanNames - set of bean names that have been processed so far
     * @return Map<String, String> property expressions map from parent or new instance
     */
    protected Map<String, String> getPropertyExpressionsFromParent(String parentBeanName,
            ConfigurableListableBeanFactory beanFactory, Set<String> processedBeanNames) {
        Map<String, String> propertyExpressions = new HashMap<String, String>();
        if (StringUtils.isBlank(parentBeanName) || !beanFactory.containsBeanDefinition(parentBeanName)) {
            return propertyExpressions;
        }

        if (!processedBeanNames.contains(parentBeanName)) {
            processBeanDefinition(parentBeanName, beanFactory.getBeanDefinition(parentBeanName), beanFactory,
                    processedBeanNames);
        }

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(parentBeanName);
        MutablePropertyValues pvs = beanDefinition.getPropertyValues();

        PropertyValue propertyExpressionsPV = pvs.getPropertyValue(UifPropertyPaths.PROPERTY_EXPRESSIONS);
        if (propertyExpressionsPV != null) {
            Object value = propertyExpressionsPV.getValue();
            if ((value != null) && (value instanceof ManagedMap)) {
                propertyExpressions.putAll((ManagedMap) value);
            }
        }

        return propertyExpressions;
    }

    /**
     * Checks whether the given property value is of String type, and if so whether it contains the expression
     * placholder(s)
     *
     * @param propertyValue - value to check for expressions
     * @return boolean true if the property value contains expression(s), false if it does not
     */
    protected boolean hasExpression(Object propertyValue) {
        if (propertyValue != null) {
            // if value is string, check for el expression
            String strValue = getStringValue(propertyValue);
            if (strValue != null) {
                String elPlaceholder = StringUtils.substringBetween(strValue, UifConstants.EL_PLACEHOLDER_PREFIX,
                        UifConstants.EL_PLACEHOLDER_SUFFIX);
                if (StringUtils.isNotBlank(elPlaceholder)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Processes the given property name/value pair for complex objects, such as bean definitions or collections,
     * which if found will be processed for contained property expression values
     *
     * @param propertyName - name of the property whose value is being processed
     * @param propertyValue - value to check
     * @param parentPropertyExpressions - map that holds property expressions for the parent bean definition, used for
     * merging
     * @param propertyExpressions - map that holds property expressions for the bean definition being processed
     * @param beanFactory - bean factory that contains the bean definition being processed
     * @param processedBeanNames - set of bean names that have been processed so far
     * @return Object new value to set for property
     */
    protected Object processPropertyValue(String propertyName, Object propertyValue,
            Map<String, String> parentPropertyExpressions, Map<String, String> propertyExpressions,
            ConfigurableListableBeanFactory beanFactory, Set<String> processedBeanNames) {
        if (propertyValue == null) {
            return null;
        }

        // process nested bean definitions
        if ((propertyValue instanceof BeanDefinition) || (propertyValue instanceof BeanDefinitionHolder)) {
            BeanDefinition beanDefinition;
            if (propertyValue instanceof BeanDefinition) {
                beanDefinition = (BeanDefinition) propertyValue;
            } else {
                beanDefinition = ((BeanDefinitionHolder) propertyValue).getBeanDefinition();
            }

            // since overriding the entire bean, clear any expressions from parent that start with the bean property
            removeExpressionsByPrefix(propertyName, parentPropertyExpressions);
            processBeanDefinition(null, beanDefinition, beanFactory, processedBeanNames);

            return propertyValue;
        }

        // recurse into collections
        if (propertyValue instanceof Object[]) {
            visitArray(propertyName, parentPropertyExpressions, propertyExpressions, (Object[]) propertyValue,
                    beanFactory, processedBeanNames);
        } else if (propertyValue instanceof List) {
            visitList(propertyName, parentPropertyExpressions, propertyExpressions, (List) propertyValue, beanFactory,
                    processedBeanNames);
        } else if (propertyValue instanceof Set) {
            visitSet(propertyName, parentPropertyExpressions, propertyExpressions, (Set) propertyValue, beanFactory,
                    processedBeanNames);
        } else if (propertyValue instanceof Map) {
            visitMap(propertyName, parentPropertyExpressions, propertyExpressions, (Map) propertyValue, beanFactory,
                    processedBeanNames);
        }

        // others (primitive) just return value as is
        return propertyValue;
    }

    /**
     * Removes entries from the given expressions map whose key starts with the given prefix
     *
     * @param propertyNamePrefix - prefix to search for and remove
     * @param propertyExpressions - map of property expressions to filter
     */
    protected void removeExpressionsByPrefix(String propertyNamePrefix, Map<String, String> propertyExpressions) {
        Map<String, String> adjustedPropertyExpressions = new HashMap<String, String>();
        for (String propertyName : propertyExpressions.keySet()) {
            if (!propertyName.startsWith(propertyNamePrefix)) {
                adjustedPropertyExpressions.put(propertyName, propertyExpressions.get(propertyName));
            }
        }

        propertyExpressions.clear();
        propertyExpressions.putAll(adjustedPropertyExpressions);
    }

    /**
     * Determines whether the given value is of String type and if so returns the string value
     *
     * @param value - object value to check
     * @return String string value for object or null if object is not a string type
     */
    protected String getStringValue(Object value) {
        if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue) value;
            return typedStringValue.getValue();
        } else if (value instanceof String) {
            return (String) value;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected void visitArray(String propertyName, Map<String, String> parentPropertyExpressions,
            Map<String, String> propertyExpressions, Object[] arrayVal, ConfigurableListableBeanFactory beanFactory,
            Set<String> processedBeanNames) {
        for (int i = 0; i < arrayVal.length; i++) {
            Object elem = arrayVal[i];
            String elemPropertyName = propertyName + "[" + i + "]";

            if (hasExpression(elem)) {
                String strValue = getStringValue(elem);
                propertyExpressions.put(elemPropertyName, strValue);
                arrayVal[i] = null;
            } else {
                Object newElem = processPropertyValue(elemPropertyName, elem, parentPropertyExpressions,
                        propertyExpressions, beanFactory, processedBeanNames);
                arrayVal[i] = newElem;
            }

            if (parentPropertyExpressions.containsKey(elemPropertyName)) {
                parentPropertyExpressions.remove(elemPropertyName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void visitList(String propertyName, Map<String, String> parentPropertyExpressions,
            Map<String, String> propertyExpressions, List listVal, ConfigurableListableBeanFactory beanFactory,
            Set<String> processedBeanNames) {
        List newList = new ArrayList();

        for (int i = 0; i < listVal.size(); i++) {
            Object elem = listVal.get(i);
            String elemPropertyName = propertyName + "[" + i + "]";

            if (hasExpression(elem)) {
                String strValue = getStringValue(elem);
                propertyExpressions.put(elemPropertyName, strValue);
                newList.add(i, null);
            } else {
                Object newElem = processPropertyValue(elemPropertyName, elem, parentPropertyExpressions,
                        propertyExpressions, beanFactory, processedBeanNames);
                newList.add(i, newElem);
            }

            if (parentPropertyExpressions.containsKey(elemPropertyName)) {
                parentPropertyExpressions.remove(elemPropertyName);
            }
        }

        // determine if we need to clear any parent expressions for this list
        if (listVal instanceof ManagedList) {
            boolean isMergeEnabled = ((ManagedList) listVal).isMergeEnabled();
            if (!isMergeEnabled) {
                // clear any expressions that match the property name minus index
                Map<String, String> adjustedParentExpressions = new HashMap<String, String>();
                for (Map.Entry<String, String> parentExpression : parentPropertyExpressions.entrySet()) {
                    if (!parentExpression.getKey().startsWith(propertyName + "[")) {
                        adjustedParentExpressions.put(parentExpression.getKey(), parentExpression.getValue());
                    }
                }

                parentPropertyExpressions.clear();
                parentPropertyExpressions.putAll(adjustedParentExpressions);
            }
        }

        listVal.clear();
        listVal.addAll(newList);
    }

    @SuppressWarnings("unchecked")
    protected void visitSet(String propertyName, Map<String, String> parentPropertyExpressions,
            Map<String, String> propertyExpressions, Set setVal, ConfigurableListableBeanFactory beanFactory,
            Set<String> processedBeanNames) {
        Set newContent = new LinkedHashSet();

        // TODO: this is not handled correctly
        for (Object elem : setVal) {
            Object newElem = processPropertyValue(propertyName, elem, parentPropertyExpressions, propertyExpressions,
                    beanFactory, processedBeanNames);
            newContent.add(newElem);
        }

        setVal.clear();
        setVal.addAll(newContent);
    }

    @SuppressWarnings("unchecked")
    protected void visitMap(String propertyName, Map<String, String> parentPropertyExpressions,
            Map<String, String> propertyExpressions, Map<?, ?> mapVal, ConfigurableListableBeanFactory beanFactory,
            Set<String> processedBeanNames) {
        Map newContent = new LinkedHashMap();

        boolean isMergeEnabled = false;
        if (mapVal instanceof ManagedMap) {
            isMergeEnabled = ((ManagedMap) mapVal).isMergeEnabled();
        }

        for (Map.Entry entry : mapVal.entrySet()) {
            Object key = entry.getKey();
            Object val = entry.getValue();

            String keyStr = getStringValue(key);
            String elemPropertyName = propertyName + "['" + keyStr + "']";

            if (hasExpression(val)) {
                String strValue = getStringValue(val);
                propertyExpressions.put(elemPropertyName, strValue);
                newContent.put(key, null);
            } else {
                Object newElem = processPropertyValue(elemPropertyName, val, parentPropertyExpressions,
                        propertyExpressions, beanFactory, processedBeanNames);
                newContent.put(key, newElem);
            }

            if (isMergeEnabled && parentPropertyExpressions.containsKey(elemPropertyName)) {
                parentPropertyExpressions.remove(elemPropertyName);
            }
        }

        if (!isMergeEnabled) {
            // clear any expressions that match the property minus key
            Map<String, String> adjustedParentExpressions = new HashMap<String, String>();
            for (Map.Entry<String, String> parentExpression : parentPropertyExpressions.entrySet()) {
                if (!parentExpression.getKey().startsWith(propertyName + "[")) {
                    adjustedParentExpressions.put(parentExpression.getKey(), parentExpression.getValue());
                }
            }

            parentPropertyExpressions.clear();
            parentPropertyExpressions.putAll(adjustedParentExpressions);
        }

        mapVal.clear();
        mapVal.putAll(newContent);
    }
}
