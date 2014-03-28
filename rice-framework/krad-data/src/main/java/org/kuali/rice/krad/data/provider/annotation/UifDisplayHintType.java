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
package org.kuali.rice.krad.data.provider.annotation;

import com.google.common.annotations.Beta;

/**
 * BETA: Defines the type of the {@link UifDisplayHint}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Beta
public enum UifDisplayHintType {
	/**
	 * Does nothing. Just a placeholder so defaults can be defined.
	 */
	NONE,
	/**
	 * Tells the defaulter to hide this field and not generate an input field for it.
	 */
	HIDDEN,
	/**
	 * Tells the defaulter to exclude this field and not generate an attribute definition at all.
	 */
	EXCLUDE,
	/**
	 * If a values finder is present for the field, generate as a Drop-down list.
	 */
	DROPDOWN,
	/**
	 * If a values finder is present for the field, generate as a set of radio buttons.
	 */
	RADIO,
	/**
	 * Indicates that this hint tells that the attribute should be put in a specific section.
	 */
	SECTION,
	/**
	 * Don't include this attribute in the inquiry page.
	 */
	NO_INQUIRY,
	/**
	 * Don't include this attribute in the lookup criteria.
	 */
	NO_LOOKUP_CRITERIA,
	/**
	 * Don't include this attribute in the lookup results.
	 */
	NO_LOOKUP_RESULT;
}