/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.ojb;

import org.kuali.rice.ojb.BaseOjbConfigurer;

/**
 * KEW implementation of the OjbConfigurer.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OjbConfigurer extends BaseOjbConfigurer {

	private static final String DEFAULT_KEW_REPOSITORY_METADATA = "classpath:org/kuali/workflow/ojb/OJB-repository-kew.xml";
	private static final String KEW_JCD_ALIAS = "enWorkflowDataSource";

	@Override
	protected String[] getJcdAliases() {
		return new String[] { KEW_JCD_ALIAS };
	}

	@Override
	protected String getMetadataLocation() {
		return DEFAULT_KEW_REPOSITORY_METADATA;
	}

}
