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
package org.kuali.rice.kim.lookup;

import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableImpl;

/**
 * This is a description of what this class does - ag266 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PrincipalLookupable extends KualiLookupableImpl {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.lookup.KualiLookupableImpl#getDocFormKey()
	 */
	@Override
	public String getDocFormKey() {
		// TODO ag266 - THIS METHOD NEEDS JAVADOCS
		return super.getDocFormKey();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.lookup.KualiLookupableImpl#getMaintenanceUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
	 */
	@Override
	protected String getMaintenanceUrl(BusinessObject bo, String methodToCall) {
		// TODO ag266 - THIS METHOD NEEDS JAVADOCS
		return super.getMaintenanceUrl(bo, methodToCall);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.lookup.KualiLookupableImpl#getParameters()
	 */
	@Override
	public Map getParameters() {
		// TODO ag266 - THIS METHOD NEEDS JAVADOCS
		return super.getParameters();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.lookup.KualiLookupableImpl#getReturnLocation()
	 */
	@Override
	public String getReturnLocation() {
		// TODO ag266 - THIS METHOD NEEDS JAVADOCS
		return super.getReturnLocation();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.lookup.KualiLookupableImpl#getReturnUrl(org.kuali.core.bo.BusinessObject, java.util.Map, java.lang.String)
	 */
	@Override
	public String getReturnUrl(BusinessObject bo, Map fieldConversions,
			String lookupImpl) {
		// TODO ag266 - THIS METHOD NEEDS JAVADOCS
		return super.getReturnUrl(bo, fieldConversions, lookupImpl);
	}
}
