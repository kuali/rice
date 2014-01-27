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
package org.kuali.rice.config;

import org.kuali.common.util.project.KualiProjectConstants;
import org.kuali.common.util.project.model.ProjectIdentifier;
import org.kuali.common.util.project.spring.AutowiredProjectConfig;
import org.kuali.common.util.project.spring.ProjectIdentifierConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Defines the project identifier for the Sample App.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ AutowiredProjectConfig.class })
public class SampleAppProjectConfig implements ProjectIdentifierConfig {

    // These 2 must always exactly match the information in the pom
    public static final ProjectIdentifier KR_SAP = new ProjectIdentifier(KualiProjectConstants.RICE_GROUP_ID, "rice-sampleapp");

    @Override
    public ProjectIdentifier projectIdentifier() {
        return KR_SAP;
    }
}
