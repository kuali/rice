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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.uif.container.View;

/**
 * Allows <code>View</code> beans to be instance is special ways based on the
 * view type
 * 
 * <p>
 * As the view dictionary entries are indexed the associated view type will be
 * retrieved and if there is an associated <code>ViewTypeIndexer</code> it will
 * be invoked to do further indexing. This is useful to index a view based on
 * other properties, like a class name.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.kns.datadictionary.view.ViewDictionaryIndex
 */
public interface ViewTypeIndexer {

	/**
	 * Gives the view type name that is supported by the indexer
	 * 
	 * <p>
	 * If the given name matches the viewTypeName property of the
	 * <code>View</code> instance being indexed, the indexer will be invoked
	 * </p>
	 * 
	 * @return String view type name
	 */
	public String getViewTypeName();

	/**
	 * Called during the index process to do further indexing on the
	 * <code>View</code> instance
	 * 
	 * @param view
	 *            - View instance to index
	 */
	public void indexView(View view);

	/**
	 * Called to retrieve a <code>View</code> instance that is of the type given
	 * by {@link #getViewTypeName()} based on the index key
	 * 
	 * @param indexKey
	 *            - Map of index key parameters, these are the parameters the
	 *            indexer used to index the view initially and needs to identify
	 *            an unique view instance. See
	 *            {@link #getIndexKeyParameterNames()} for supported index key
	 *            parameter names
	 * @return View instance that matches the given index
	 */
	public View retrieveViewByKey(Map<String, String> indexKey);

	/**
	 * Retrieves any <code>View</code> instances whose index matches the given
	 * key parameters. The given query <code>Map</code> can specify only part of
	 * the parameters that make up the full key, which is why more than one
	 * instance can be returned
	 * 
	 * @param partialKey
	 *            - - Map of index key parameters, these are one or more
	 *            parameters the indexer used to initially index the View. See
	 *            {@link #getIndexKeyParameterNames()} for supported index key
	 *            parameter names
	 * @return List<View> matching view instances or empty list if none were
	 *         found
	 */
	public List<View> retrieveMatchingViews(Map<String, String> partialKey);

	/**
	 * Names of the key parameters that make up the index String. These
	 * parameter names can be used with a value for each to retrieve
	 * <code>View</code> instances from the index
	 * 
	 * @return Set<String> index key parameter names
	 */
	public Set<String> getIndexKeyParameterNames();

}
