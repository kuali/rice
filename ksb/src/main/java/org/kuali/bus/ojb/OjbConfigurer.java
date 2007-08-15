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
package org.kuali.bus.ojb;

import org.kuali.rice.ojb.BaseOjbConfigurer;

public class OjbConfigurer extends BaseOjbConfigurer {

	private static final String DEFAULT_KSB_REPOSITORY_METADATA = "classpath:OJB-repository-ksb.xml";
	private static final String KSB_MESSAGE_JCD_ALIAS = "ksbMessageDataSource";
	private static final String KSB_REGISTRY_JCD_ALIAS = "ksbRegistryDataSource";

	@Override
	protected String[] getJcdAliases() {
		return new String[] { KSB_MESSAGE_JCD_ALIAS, KSB_REGISTRY_JCD_ALIAS };
	}

	@Override
	protected String getMetadataLocation() {
		return DEFAULT_KSB_REPOSITORY_METADATA;
	}
}