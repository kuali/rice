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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.service.ViewTypeService;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.kuali.rice.krad.uif.UifConstants.ViewType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Indexes <code>View</code> bean entries for retrieval
 *
 * <p>
 * This is used to retrieve a <code>View</code> instance by its unique id.
 * Furthermore, view of certain types (that have a <code>ViewTypeService</code>
 * are indexed by their type to support retrieval of views based on parameters.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDictionaryIndex implements Runnable {
    private static final Log LOG = LogFactory.getLog(UifDictionaryIndex.class);

    private DefaultListableBeanFactory ddBeans;

    // view entries keyed by view id with value the spring bean name
    private Map<String, String> viewBeanEntriesById;

    // view entries indexed by type
    private Map<String, ViewTypeDictionaryIndex> viewEntriesByType;

    public UifDictionaryIndex(DefaultListableBeanFactory ddBeans) {
        this.ddBeans = ddBeans;
    }

    public void run() {
        LOG.info("Starting View Index Building");
        buildViewIndicies();
        LOG.info("Completed View Index Building");
    }

    /**
     * Retrieves the View instance with the given id from the bean factory.
     * Since View instances are stateful, we need to get a new instance from
     * Spring each time.
     *
     * @param viewId - the unique id for the view
     * @return <code>View</code> instance
     */
    public View getViewById(String viewId) {
        String beanName = viewBeanEntriesById.get(viewId);
        if (StringUtils.isBlank(beanName)) {
            throw new DataDictionaryException("Unable to find View with id: " + viewId);
        }

        return ddBeans.getBean(beanName, View.class);
    }

    /**
     * Retrieves a <code>View</code> instance that is of the given type based on
     * the index key
     *
     * @param viewTypeName - type name for the view
     * @param indexKey - Map of index key parameters, these are the parameters the
     * indexer used to index the view initially and needs to identify
     * an unique view instance
     * @return View instance that matches the given index or Null if one is not
     *         found
     */
    public View getViewByTypeIndex(ViewType viewTypeName, Map<String, String> indexKey) {
        String index = buildTypeIndex(indexKey);

        ViewTypeDictionaryIndex typeIndex = getTypeIndex(viewTypeName);

        String beanName = typeIndex.get(index);
        if (StringUtils.isNotBlank(beanName)) {
            return ddBeans.getBean(beanName, View.class);
        }

        return null;
    }

    /**
     * Indicates whether a <code>View</code> exists for the given view type and index information
     *
     * @param viewTypeName - type name for the view
     * @param indexKey - Map of index key parameters, these are the parameters the indexer used to index
     * the view initially and needs to identify an unique view instance
     * @return boolean true if view exists, false if not
     */
    public boolean viewByTypeExist(ViewType viewTypeName, Map<String, String> indexKey) {
        boolean viewExist = false;

        String index = buildTypeIndex(indexKey);
        ViewTypeDictionaryIndex typeIndex = getTypeIndex(viewTypeName);

        String beanName = typeIndex.get(index);
        if (StringUtils.isNotBlank(beanName)) {
            viewExist = true;
        }

        return viewExist;
    }

    /**
     * Retrieves the configured property values for the view bean definition associated with the given id
     *
     * <p>
     * Since constructing the View object can be expensive, when metadata only is needed this method can be used
     * to retrieve the configured property values. Note this looks at the merged bean definition
     * </p>
     *
     * @param viewId - id for the view to retrieve
     * @return PropertyValues configured on the view bean definition, or null if view is not found
     */
    public PropertyValues getViewPropertiesById(String viewId) {
        String beanName = viewBeanEntriesById.get(viewId);
        if (StringUtils.isBlank(beanName)) {
            BeanDefinition beanDefinition = ddBeans.getMergedBeanDefinition(beanName);

            return beanDefinition.getPropertyValues();
        }

        return null;
    }

    /**
     * Retrieves the configured property values for the view bean definition associated with the given type and
     * index
     *
     * <p>
     * Since constructing the View object can be expensive, when metadata only is needed this method can be used
     * to retrieve the configured property values. Note this looks at the merged bean definition
     * </p>
     *
     * @param viewTypeName - type name for the view
     * @param indexKey - Map of index key parameters, these are the parameters the indexer used to index
     * the view initially and needs to identify an unique view instance
     * @return PropertyValues configured on the view bean definition, or null if view is not found
     */
    public PropertyValues getViewPropertiesByType(ViewType viewTypeName, Map<String, String> indexKey) {
        String index = buildTypeIndex(indexKey);

        ViewTypeDictionaryIndex typeIndex = getTypeIndex(viewTypeName);

        String beanName = typeIndex.get(index);
        if (StringUtils.isNotBlank(beanName)) {
            BeanDefinition beanDefinition = ddBeans.getMergedBeanDefinition(beanName);

            return beanDefinition.getPropertyValues();
        }

        return null;
    }

    /**
     * Gets all <code>View</code> prototypes configured for the given view type
     * name
     *
     * @param viewTypeName - view type name to retrieve
     * @return List<View> view prototypes with the given type name, or empty
     *         list
     */
    public List<View> getViewsForType(ViewType viewTypeName) {
        List<View> typeViews = new ArrayList<View>();

        // get view ids for the type
        if (viewEntriesByType.containsKey(viewTypeName.name())) {
            ViewTypeDictionaryIndex typeIndex = viewEntriesByType.get(viewTypeName.name());
            for (Entry<String, String> typeEntry : typeIndex.getViewIndex().entrySet()) {
                View typeView = ddBeans.getBean(typeEntry.getValue(), View.class);
                typeViews.add(typeView);
            }
        } else {
            throw new DataDictionaryException("Unable to find view index for type: " + viewTypeName);
        }

        return typeViews;
    }

    /**
     * Initializes the view index <code>Map</code> then iterates through all the
     * beans in the factory that implement <code>View</code>, adding them to the
     * index
     */
    protected void buildViewIndicies() {
        viewBeanEntriesById = new HashMap<String, String>();
        viewEntriesByType = new HashMap<String, ViewTypeDictionaryIndex>();

        String[] beanNames = ddBeans.getBeanNamesForType(View.class);
        for (int i = 0; i < beanNames.length; i++) {
            String beanName = beanNames[i];
            BeanDefinition beanDefinition = ddBeans.getMergedBeanDefinition(beanName);
            PropertyValues propertyValues = beanDefinition.getPropertyValues();

            String id = ViewModelUtils.getStringValFromPVs(propertyValues, "id");
            if (StringUtils.isBlank(id)) {
                id = beanName;
            }

            if (viewBeanEntriesById.containsKey(id)) {
                throw new DataDictionaryException("Two views must not share the same id. Found duplicate id: " + id);
            }
            viewBeanEntriesById.put(id, beanName);

            indexViewForType(propertyValues, beanName);
        }
    }

    /**
     * Performs additional indexing based on the view type associated with the view instance. The
     * <code>ViewTypeService</code> associated with the view type name on the instance is invoked to retrieve
     * the parameter key/value pairs from the configured property values, which are then used to build up an index
     * used to key the entry
     *
     * @param propertyValues - property values configured on the view bean definition
     * @param beanName - name of the view's bean in Spring
     */
    protected void indexViewForType(PropertyValues propertyValues, String beanName) {
        String viewTypeName = ViewModelUtils.getStringValFromPVs(propertyValues,  "viewTypeName");
        if (StringUtils.isBlank(viewTypeName)) {
            return;
        }

        UifConstants.ViewType viewType = ViewType.valueOf(viewTypeName);

        ViewTypeService typeService = KRADServiceLocatorWeb.getViewService().getViewTypeService(viewType);
        if (typeService == null) {
            // don't do any further indexing
            return;
        }

        // invoke type service to retrieve it parameter name/value pairs
        Map<String, String> typeParameters = typeService.getParametersFromViewConfiguration(propertyValues);

        // build the index string from the parameters
        String index = buildTypeIndex(typeParameters);

        // get the index for the type and add the view entry
        ViewTypeDictionaryIndex typeIndex = getTypeIndex(viewType);

        typeIndex.put(index, beanName);
    }

    /**
     * Retrieves the <code>ViewTypeDictionaryIndex</code> instance for the given
     * view type name. If one does not exist yet for the given name, a new
     * instance is created
     *
     * @param viewType - name of the view type to retrieve index for
     * @return ViewTypeDictionaryIndex instance
     */
    protected ViewTypeDictionaryIndex getTypeIndex(UifConstants.ViewType viewType) {
        ViewTypeDictionaryIndex typeIndex = null;

        if (viewEntriesByType.containsKey(viewType.name())) {
            typeIndex = viewEntriesByType.get(viewType.name());
        } else {
            typeIndex = new ViewTypeDictionaryIndex();
            viewEntriesByType.put(viewType.name(), typeIndex);
        }

        return typeIndex;
    }

    /**
     * Builds up an index string from the given Map of parameters
     *
     * @param typeParameters - Map of parameters to use for index
     * @return String index
     */
    protected String buildTypeIndex(Map<String, String> typeParameters) {
        String index = "";

        for (String parameterName : typeParameters.keySet()) {
            if (StringUtils.isNotBlank(index)) {
                index += "|||";
            }
            index += parameterName + "^^" + typeParameters.get(parameterName);
        }

        return index;
    }

}
