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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.DataDictionaryException;
import org.kuali.rice.kns.uif.container.View;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ViewTypeIndexerBase implements ViewTypeIndexer {

	protected Map<String, View> viewIndex;

	public ViewTypeIndexerBase() {
		viewIndex = new HashMap<String, View>();
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.view.ViewTypeIndexer#indexView(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void indexView(View view) {
		Map<String, String> indexKey = buildIndexKey(view);
		String indexKeyString = buildIndexKeyString(indexKey);

		if (viewIndex.containsKey(indexKeyString)) {
			throw new DataDictionaryException("Two Views must not share the same type index: " + indexKeyString);
		}

		viewIndex.put(indexKeyString, view);
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.view.ViewTypeIndexer#retrieveViewByKey(java.util.Map)
	 */
	@Override
	public View retrieveViewByKey(Map<String, String> indexKey) {
		String indexKeyString = buildIndexKeyString(indexKey);

		if (viewIndex.containsKey(indexKeyString)) {
			return viewIndex.get(indexKeyString);
		}

		return null;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.view.ViewTypeIndexer#retrieveMatchingViews(java.util.Map)
	 */
	@Override
	public List<View> retrieveMatchingViews(Map<String, String> partialKey) {
		List<View> matchingViews = new ArrayList<View>();

		for (String index : viewIndex.keySet()) {
			boolean matches = true;
			for (String matchKey : partialKey.keySet()) {
				String matchValue = partialKey.get(matchKey);
				String matchString = matchKey + "^^" + matchValue;

				if (!StringUtils.contains(index, matchString)) {
					matches = false;
					break;
				}
			}

			if (matches) {
				matchingViews.add(viewIndex.get(index));
			}
		}

		return matchingViews;
	}

	protected abstract Map<String, String> buildIndexKey(View view);

	protected String buildIndexKeyString(Map<String, String> indexKey) {
		String indexKeyString = "";

		for (String parameterName : indexKey.keySet()) {
			if (StringUtils.isNotBlank(indexKeyString)) {
				indexKeyString += "|||";
			}
			indexKeyString += parameterName + "^^" + indexKey.get(parameterName);
		}

		return indexKeyString;
	}

}
