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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.bo.user.KualiModuleUser;
import org.kuali.rice.kns.bo.user.KualiModuleUserBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class KualiModuleUserServiceDefaultImpl extends KualiModuleUserServiceBaseImpl {
	
	private String moduleId;
	
	public KualiModuleUserServiceDefaultImpl(String moduleId) {
		this.moduleId = moduleId;
		List<String> properties = new ArrayList<String>();
        properties.add(KNSPropertyConstants.ACTIVE);
        setPropertyList(properties);
	}
	
	public KualiModuleUser getModuleUser(UniversalUser universalUser) throws UserNotFoundException {
		KualiModuleUserBase user = new KualiModuleUserBase(this.moduleId, universalUser);
		user.setActive(true);
		return user;
	}

}