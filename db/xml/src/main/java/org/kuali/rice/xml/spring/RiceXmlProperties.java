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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.kuali.common.util.LocationUtils;
import org.kuali.common.util.project.ProjectUtils;
import org.kuali.common.util.project.model.ProjectResource;
import org.kuali.rice.xml.project.XmlProjectConstants;

/**
 * Holds the Rice properties for the workflow XML ingestion process.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum RiceXmlProperties {

	DB(ProjectResource.classpath(XmlProjectConstants.ID, "db.properties")),
	APP(ProjectResource.classpath(XmlProjectConstants.ID, "application-rice-properties.xml", true));

    private final ProjectResource resource;

	private RiceXmlProperties(ProjectResource resource) {
		checkNotNull(resource, "'resource' cannot be null");
		this.resource = resource;
		String path = ProjectUtils.getPath(resource);
		checkArgument(LocationUtils.exists(path), "[%s] does not exist", path);
	}

	public ProjectResource getResource() {
		return resource;
	}

}
