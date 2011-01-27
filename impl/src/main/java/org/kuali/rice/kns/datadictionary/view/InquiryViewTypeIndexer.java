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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.UifConstants.ViewTypeIndexParameterNames;
import org.kuali.rice.kns.uif.container.InquiryView;
import org.kuali.rice.kns.uif.container.View;

/**
 * Indexes views of type INQUIRY, <code>InquiryView</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryViewTypeIndexer extends ViewTypeIndexerBase {

	/**
	 * @see org.kuali.rice.kns.datadictionary.view.ViewTypeIndexer#getViewTypeName()
	 */
	@Override
	public String getViewTypeName() {
		return ViewType.INQUIRY;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.view.ViewTypeIndexer#getIndexKeyParameterNames()
	 */
	@Override
	public Set<String> getIndexKeyParameterNames() {
		Set<String> parameters = new HashSet<String>();

		parameters.add(ViewTypeIndexParameterNames.NAME);
		parameters.add(ViewTypeIndexParameterNames.MODEL_CLASS);

		return parameters;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.view.ViewTypeIndexerBase#buildIndexKey(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	protected Map<String, String> buildIndexKey(View view) {
		Map<String, String> indexKey = new HashMap<String, String>();

		InquiryView inquiryView = (InquiryView) view;

		indexKey.put(ViewTypeIndexParameterNames.NAME, inquiryView.getName());
		indexKey.put(ViewTypeIndexParameterNames.MODEL_CLASS, inquiryView.getModelClass().getName());

		return indexKey;
	}

}
