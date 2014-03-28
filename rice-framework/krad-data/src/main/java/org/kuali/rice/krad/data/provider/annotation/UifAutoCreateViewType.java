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
 * BETA: Enum representing the types which can be auto-generated.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Beta
public enum UifAutoCreateViewType {
	/**
	 * Convenience value which tells the system to generate all of the other items.
	 */
	ALL,
	/**
	 * Generate a bean which provides an InquiryView
	 */
	INQUIRY,
	/**
	 * Generate a bean which provides a LookupView
	 */
	LOOKUP,
	/**
	 * Generate beans which provides a maintenance document and related MaintenanceView
	 */
	MAINT_DOC;
}
