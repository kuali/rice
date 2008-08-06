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
package org.kuali.workflow.config;

import org.kuali.rice.config.RiceConfigurer;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.ksb.messaging.config.KSBThinClientConfigurer;

/**
 * A configurer which configures KEW Thin-Client mode.
 *      
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ThinClientKEWConfigurer extends RiceConfigurer {

	public ThinClientKEWConfigurer() {
		setMessageEntity(KEWConstants.KEW_MESSAGING_ENTITY);
		// thin client allows us to still have access to the DigitalSignatureService but not use the full capabilities of the bus
		getModules().add(new KSBThinClientConfigurer());
		getModules().add(new KEWConfigurer());
	}
		
}
