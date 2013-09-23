/**
 * Copyright 2005-2013 The Kuali Foundation
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

/**
 * Class representing the hints which can be passed through when auto-generating the input fields for an attribute.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public @interface UifDisplayHint {
	UifDisplayHintType value();

	// public static final UifDisplayHint HIDDEN = new UifDisplayHint(UifDisplayHintType.HIDDEN);
	// public static final UifDisplayHint EXCLUDE = new UifDisplayHint(UifDisplayHintType.EXCLUDE);
	// public static final UifDisplayHint DROPDOWN = new UifDisplayHint(UifDisplayHintType.DROPDOWN);
	// public static final UifDisplayHint RADIO = new UifDisplayHint(UifDisplayHintType.RADIO);

	// private final UifDisplayHintType hintType;
	String id() default "";

	String label() default "";

	// public UifDisplayHint(UifDisplayHintType hintType) {
	// this.hintType = hintType;
	// }
	//
	// public UifDisplayHint(UifDisplayHintType hintType, String id) {
	// this(hintType);
	// this.id = id;
	// }
	//
	// public UifDisplayHint(UifDisplayHintType hintType, String id, String label) {
	// this(hintType, id);
	// this.label = label;
	// }
	//
	// public UifDisplayHintType getHintType() {
	// return hintType;
	// }
	//
	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }
	//
	// public String getLabel() {
	// return label;
	// }
	//
	// public void setLabel(String label) {
	// this.label = label;
	// }

}
