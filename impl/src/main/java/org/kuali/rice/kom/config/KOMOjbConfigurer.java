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
package org.kuali.rice.kom.config;

import org.kuali.rice.core.ojb.BaseOjbConfigurer;

/**
 * This class defines information specific to the KOM OJB setup.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KOMOjbConfigurer extends BaseOjbConfigurer {

	private static final String DEFAULT_KOM_REPOSITORY_METADATA = "classpath:org/kuali/rice/kom/config/OJB-repository-kom.xml";
	private static final String KOM_JCD_ALIAS = "komDataSource";

	/**
	 * This overridden method adds in the KOM jcdAlias data source. 
	 * 
	 * @see org.kuali.rice.core.ojb.BaseOjbConfigurer#getJcdAliases()
	 */
	@Override
	protected String[] getJcdAliases() {
		return new String[] { KOM_JCD_ALIAS };
	}

	/**
	 * This overridden method returns the KOM OJB file.
	 * 
	 * @see org.kuali.rice.core.ojb.BaseOjbConfigurer#getMetadataLocation()
	 */
	@Override
	protected String getMetadataLocation() {
		return DEFAULT_KOM_REPOSITORY_METADATA;
	}
}