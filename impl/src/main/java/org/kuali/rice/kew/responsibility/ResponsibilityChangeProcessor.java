/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.responsibility;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kew.messaging.ParameterTranslator;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityChangeProcessor implements KSBXMLService {

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.ksb.messaging.service.KSBXMLService#invoke(java.lang.String)
	 */
	public void invoke(String message) throws Exception {
		ParameterTranslator translator = new ParameterTranslator(message);
		String[] parameters = translator.getParameters();

		Set<String> respIds = new HashSet<String>();

		for(String parameter: parameters){
			respIds.add(parameter);
		}

		KEWServiceLocator.getActionRequestService().updateActionRequestsForResponsibilityChange(respIds);

	}

	public static String getResponsibilityChangeContents(Set<String> responsibilities){
		ParameterTranslator translator = new ParameterTranslator();
		for(String resp: responsibilities){
			translator.addParameter(resp);
		}
		return translator.getUntranslatedString();
	}


}
