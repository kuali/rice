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
package org.kuali.rice.xml.project;

import org.kuali.common.util.project.model.KualiGroup;
import org.kuali.common.util.project.model.ProjectIdentifier;

/**
 * Defines the constants for the workflow XML ingestion process.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XmlProjectConstants {

	// The groupId and artifactId used here must exactly match what is in the pom
	public static final ProjectIdentifier ID = new ProjectIdentifier(KualiGroup.RICE.getId(), "rice-xml");

}
