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
package org.kuali.rice.krad.data.metadata;

/**
 * Sorting orders used for collection definitions and lookup generation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum SortDirection {
	ASCENDING,
	DESCENDING,
	// These latter are Oracle options on table sorting. However, recent ANSI
	// standards have extended them as requiring to be understood by all databases.
	// (Though not all support the style of sorting.)
	ASCENDING_NULLS_FIRST,
	ASCENDING_NULLS_LAST,
	DESCENDING_NULLS_FIRST,
	DESCENDING_NULLS_LAST
}
