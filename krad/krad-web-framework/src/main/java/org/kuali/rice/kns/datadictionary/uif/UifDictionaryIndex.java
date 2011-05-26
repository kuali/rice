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
package org.kuali.rice.kns.datadictionary.uif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.datadictionary.DataDictionaryException;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.service.ViewTypeService;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Indexes <code>View</code> and <code>Component</code> bean entries for retrieval
 * 
 * <p>
 * name. This is used to retrieve a <code>View</code> instance by its unique id.
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
	
	// view entries keyed by bean name with value the view prototype
	private Map<String, View> viewEntriesByBean;

	// view entries indexed by type
	private Map<String, ViewTypeDictionaryIndex> viewEntriesByType;

    // component entries indexed by component id and bean name
    private Map<String, String> componentBeanEntriesById;

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
	 * @param viewId
	 *            - the unique id for the view
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
	 * @param viewTypeName
	 *            - type name for the view
	 * @param indexKey
	 *            - Map of index key parameters, these are the parameters the
	 *            indexer used to index the view initially and needs to identify
	 *            an unique view instance
	 * @return View instance that matches the given index or Null if one is not
	 *         found
	 */
	public View getViewByTypeIndex(String viewTypeName, Map<String, String> indexKey) {
		String index = buildTypeIndex(indexKey);

		ViewTypeDictionaryIndex typeIndex = getTypeIndex(viewTypeName);

		String beanName = typeIndex.get(index);
		if (StringUtils.isBlank(beanName)) {
			throw new DataDictionaryException("Unable to find View with index: " + index);
		}

		return ddBeans.getBean(beanName, View.class);
	}

	/**
	 * Gets all <code>View</code> prototypes configured for the given view type
	 * name
	 * 
	 * @param viewTypeName
	 *            - view type name to retrieve
	 * @return List<View> view prototypes with the given type name, or empty
	 *         list
	 */
	public List<View> getViewsForType(String viewTypeName) {
		List<View> typeViews = new ArrayList<View>();

		// get view ids for the type
		if (viewEntriesByType.containsKey(viewTypeName)) {
			ViewTypeDictionaryIndex typeIndex = viewEntriesByType.get(viewTypeName);
			for (Entry<String, String> typeEntry : typeIndex.getViewIndex().entrySet()) {
				// get the view prototype by bean name
				if (viewEntriesByBean.containsKey(typeEntry.getValue())) {
					View typeView = viewEntriesByBean.get(typeEntry.getValue());
					typeViews.add(typeView);
				}
			}
		}
		else {
			throw new DataDictionaryException("Unable to find view index for type: " + viewTypeName);
		}

		return typeViews;
	}

    /**
     * Returns a Component configured in the dictionary with the given
     * id. Standard Spring scoping rules apply in terms of prototypes
     * or singletons
     *
     * @param id - id of the component set in the dictionary of set by the framework
     * @return Component found for id or null if no component was found
     */
    public Component getComponentById(String id) {
        if (componentBeanEntriesById.containsKey(id)) {
            String componentBeanId = componentBeanEntriesById.get(id);

            return (Component) ddBeans.getBean(componentBeanId);
        }

        return null;
    }

	/**
	 * Initializes the view index <code>Map</code> then iterates through all the
	 * beans in the factory that implement <code>View</code>, adding them to the
	 * index
	 */
	protected void buildViewIndicies() {
		viewBeanEntriesById = new HashMap<String, String>();
		viewEntriesByBean = new HashMap<String, View>();
		viewEntriesByType = new HashMap<String, ViewTypeDictionaryIndex>();
        componentBeanEntriesById = new HashMap<String, String>();

		Map<String, View> viewBeans = ddBeans.getBeansOfType(View.class);
		for (String beanName : viewBeans.keySet()) {
			View view = viewBeans.get(beanName);
			if (viewBeanEntriesById.containsKey(view.getId())) {
				throw new DataDictionaryException("Two views must not share the same id. Found duplicate id: "
						+ view.getId());
			}
			
			viewBeanEntriesById.put(view.getId(), beanName);
			viewEntriesByBean.put(beanName, view);

			indexViewForType(view, beanName);
		}

        Map<String, Component> components = ddBeans.getBeansOfType(Component.class);
        for (String beanName : components.keySet()) {
            Component component = components.get(beanName);
            if (componentBeanEntriesById.containsKey(component.getId())) {
                throw new DataDictionaryException(
                        "Two components must not share the same id. Found duplicate id: " + component.getId());
            }

            componentBeanEntriesById.put(component.getId(), beanName);
        }
	}

	/**
	 * Performs additional indexing based on the view type associated with the
	 * view instance. The <code>ViewTypeService</code> associated with the view
	 * type name on the instance is invoked to retrieve the parameter key/value
	 * pairs from the view instance, which are then used to build up an index
	 * which will key the entry
	 * 
	 * @param view
	 *            - view instance to index
	 * @param beanName
	 *            - name of the view's bean in Spring
	 */
	protected void indexViewForType(View view, String beanName) {
		String viewType = view.getViewTypeName();

		ViewTypeService typeService = KNSServiceLocatorWeb.getViewService().getViewTypeService(viewType);
		if (typeService == null) {
			// don't do any further indexing
			return;
		}

		// invoke type service to retrieve it parameter name/value pairs from
		// the view
		Map<String, String> typeParameters = typeService.getParametersFromView(view);

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
	 * @param viewType
	 *            - name of the view type to retrieve index for
	 * @return ViewTypeDictionaryIndex instance
	 */
	protected ViewTypeDictionaryIndex getTypeIndex(String viewType) {
		ViewTypeDictionaryIndex typeIndex = null;

		if (viewEntriesByType.containsKey(viewType)) {
			typeIndex = viewEntriesByType.get(viewType);
		}
		else {
			typeIndex = new ViewTypeDictionaryIndex();
			viewEntriesByType.put(viewType, typeIndex);
		}

		return typeIndex;
	}

	/**
	 * Builds up an index string from the given Map of parameters
	 * 
	 * @param typeParameters
	 *            - Map of parameters to use for index
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
