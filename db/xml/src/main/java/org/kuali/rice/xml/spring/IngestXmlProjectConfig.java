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
package org.kuali.rice.xml.spring;

import org.kuali.common.util.project.model.ProjectIdentifier;
import org.kuali.common.util.project.spring.ProjectIdentifierConfig;
import org.kuali.rice.xml.project.XmlProjectConstants;
import org.springframework.context.annotation.Configuration;

/**
 * Defines the project identifier for the Rice XML project
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
public class IngestXmlProjectConfig implements ProjectIdentifierConfig {

	@Override
	public ProjectIdentifier projectIdentifier() {
		return XmlProjectConstants.ID;
	}

}
