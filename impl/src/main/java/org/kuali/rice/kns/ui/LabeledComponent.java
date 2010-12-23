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
package org.kuali.rice.kns.ui;

import org.kuali.rice.kns.ui.field.LabelField;

/**
 * Marker interface for components that have an associated label. This is used
 * in particular within the containers to determine whether an additional label
 * field needs to be generated
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LabeledComponent {

	/**
	 * Indicates whether the <code>LabelField</code> for the component should be
	 * included with the component (in the container). If this method returns
	 * true, the method getLabelField will be called on the component to get the
	 * actual <code>LabelField</code> instance to include.
	 * 
	 * @return boolean, true if label field should be included in the container,
	 *         false if not
	 */
	public boolean isIncludeLabelField();

	/**
	 * Returns the <code>LabelField</code> that should be rendered in the
	 * container for the field
	 * 
	 * @return <code>LabelField</code> instance
	 */
	public LabelField getLabelField();

}
