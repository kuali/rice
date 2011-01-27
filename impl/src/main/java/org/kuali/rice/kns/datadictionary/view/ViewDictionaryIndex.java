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
package org.kuali.rice.kns.datadictionary.view;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kns.datadictionary.DataDictionaryException;
import org.kuali.rice.kns.uif.container.View;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Indexes <code>View</code> bean entries for retrieval
 * 
 * <p>
 * Builds up a Map index where the key is the view id, and the value is the bean
 * name. This is used to retrieve a <code>View</code> instance by its unique id.
 * Furthermore, any configured <code>ViewTypeIndexer</code> implementations will
 * be invoked to do further indexing to support alternative ways of retrieving a
 * <code>View</code>
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewDictionaryIndex implements Runnable {
	private static final Log LOG = LogFactory.getLog(ViewDictionaryIndex.class);

	private DefaultListableBeanFactory ddBeans;

	// view entries keyed by view id with value the spring bean name
	private Map<String, String> viewEntriesById;

	private Map<String, ViewTypeIndexer> viewIndexers;

	public ViewDictionaryIndex(DefaultListableBeanFactory ddBeans) {
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
		String beanName = viewEntriesById.get(viewId);
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
		ViewTypeIndexer typeIndexer = getViewTypeIndexer(viewTypeName);

		if (typeIndexer != null) {
			return typeIndexer.retrieveViewByKey(indexKey);
		}

		return null;
	}

	/**
	 * Initializes the view index <code>Map</code> then iterates through all the
	 * beans in the factory that implement <code>View</code>, adding them to the
	 * index
	 */
	protected void buildViewIndicies() {
		viewEntriesById = new HashMap<String, String>();
		viewIndexers = new HashMap<String, ViewTypeIndexer>();

		Map<String, View> viewBeans = ddBeans.getBeansOfType(View.class);
		for (String beanName : viewBeans.keySet()) {
			View view = viewBeans.get(beanName);
			if (viewEntriesById.containsKey(view.getId())) {
				throw new DataDictionaryException("Two views must not share the same id. Found duplicate id: "
						+ view.getId());
			}

			viewEntriesById.put(view.getId(), beanName);

			// invoke view type indexer
			ViewTypeIndexer typeIndexer = getViewTypeIndexer(view.getViewTypeName());
			if (typeIndexer != null) {
				typeIndexer.indexView(view);
			}
		}
	}

	protected ViewTypeIndexer getViewTypeIndexer(String viewTypeName) {
		if (viewIndexers.containsKey(viewTypeName)) {
			return viewIndexers.get(viewTypeName);
		}

		Map<String, ViewTypeIndexer> indexerBeans = ddBeans.getBeansOfType(ViewTypeIndexer.class);
		for (String beanName : indexerBeans.keySet()) {
			ViewTypeIndexer typeIndexer = indexerBeans.get(beanName);
			if (typeIndexer.getViewTypeName().equals(viewTypeName)) {
				viewIndexers.put(viewTypeName, typeIndexer);

				return typeIndexer;
			}
		}

		return null;
	}

}
