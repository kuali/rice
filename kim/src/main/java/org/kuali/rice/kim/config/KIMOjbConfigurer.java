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
package org.kuali.rice.kim.config;

import org.kuali.rice.ojb.BaseOjbConfigurer;

/**
 * This class defines information specific to the KIM OJB setup.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KIMOjbConfigurer extends BaseOjbConfigurer {

	private static final String DEFAULT_KIM_REPOSITORY_METADATA = "classpath:OJB-repository-kim.xml";
	private static final String KIM_JCD_ALIAS = "kimDataSource";

	/**
	 * This overridden method adds in the KIM jcdAlias data source. 
	 * 
	 * @see org.kuali.rice.ojb.BaseOjbConfigurer#getJcdAliases()
	 */
	@Override
	protected String[] getJcdAliases() {
		return new String[] { KIM_JCD_ALIAS };
	}

	/**
	 * This overridden method returns the KIM OJB file.
	 * 
	 * @see org.kuali.rice.ojb.BaseOjbConfigurer#getMetadataLocation()
	 */
	@Override
	protected String getMetadataLocation() {
		return DEFAULT_KIM_REPOSITORY_METADATA;
	}
}