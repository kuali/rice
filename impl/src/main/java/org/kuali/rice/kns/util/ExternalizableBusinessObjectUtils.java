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
package org.kuali.rice.kns.util;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;

/**
 * A class with utilities related to externalizable business objects
 */
public class ExternalizableBusinessObjectUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExternalizableBusinessObjectUtils.class);

	/**
	 * Given a class, this method determines which of the interfaces that the class implements extends {@link ExternalizableBusinessObject}
	 * 
	 * @param businessObjectClass
	 * @return
	 */
	public static Class determineExternalizableBusinessObjectSubInterface(Class businessObjectClass) {
		if (businessObjectClass == null) {
			return null;
		}
		if (businessObjectClass.isInterface()) {
			if (!ExternalizableBusinessObject.class.equals(businessObjectClass) && ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
				return businessObjectClass;
			}
			else {
				return null;
			}
		}
		if (ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
			Class tempClass = businessObjectClass;
			while (tempClass != null && !Object.class.equals(tempClass)) {
				for (Class tempClassInterface : tempClass.getInterfaces()) {
					if (!ExternalizableBusinessObject.class.equals(tempClassInterface) && ExternalizableBusinessObject.class.isAssignableFrom(tempClassInterface)) {
						return tempClassInterface;
					}
				}
				tempClass = tempClass.getSuperclass();
			}
		}
		return null;
	}
	
	public static boolean isExternalizableBusinessObjectInterface(Class businessObjectClass) {
		return businessObjectClass != null && businessObjectClass.isInterface() && ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass);
	}

	public static boolean isExternalizableBusinessObjectInterface(String businessObjectClassName) {
		try {
			Class businessObjectClass = Class.forName( businessObjectClassName );
			return isExternalizableBusinessObjectInterface(businessObjectClass);
		} catch ( Exception ex ) {
			LOG.debug( "Unable to get class object for class name: " + businessObjectClassName, ex );
			return false;
		}
	}
}
