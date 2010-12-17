/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kim.service;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.kim.bo.KimType;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * Tests the {@link KIMServiceLocator} class. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KIMServiceLocatorTest extends KIMTestCase {

	@Test
	public void testGetKimTypeService_KimType() {
		
		// test by passing null
		
		KimTypeService typeService1 = KIMServiceLocator.getKimTypeService((KimType)null);
		assertNull("type service should be null", typeService1);
		
		// test by passing a KimType with a null service name
		
		KimTypeInfo nullKimType = new KimTypeInfo();
		nullKimType.setKimTypeServiceName(null);
		KimTypeService typeService2 = KIMServiceLocator.getKimTypeService(nullKimType);
		assertNotNull("type service shoudl have been found", typeService2);
		assertEquals("should be the default kim type", KimTypeServiceBase.class, typeService2.getClass());
		
		// test by passing a KimType with an empty service name
		
		KimTypeInfo emptyKimType = new KimTypeInfo();
		nullKimType.setKimTypeServiceName("");
		KimTypeService typeService3 = KIMServiceLocator.getKimTypeService(emptyKimType);
		assertNotNull("type service should have been found", typeService3);
		assertEquals("should be the default kim type", KimTypeServiceBase.class, typeService3.getClass());
		
		// test by passing a KimType that refers to the Permission TypeService
		
		KimType permissionKimType = KIMServiceLocator.getTypeInfoService().getKimTypeByName(KimConstants.NAMESPACE_CODE, "Permission");
		assertNotNull("The KR-IDM:Permission KimType should exist.", permissionKimType);
		
		KimTypeService typeService4 = KIMServiceLocator.getKimTypeService(permissionKimType);
		assertNotNull("type service should have been found", typeService4);
		
	}
	
	@Test
	public void testGetKimTypeService_QName() {
		
		// test by passing null
		
		try {
			KIMServiceLocator.getKimTypeService((QName)null);
			fail("getKimTypeService with a null QName should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		// test by passing an invalid QName
		
		KimTypeService typeService1 = KIMServiceLocator.getKimTypeService(new QName("badNamespace", "badServiceName"));
		assertNull("A null KimTypeService should have been returned.", typeService1);
		
		// test by passing a QName for a valid service, but not one which is a KimTypeService, null should be returned
		
		// fetch the group service instead
		KimTypeService typeService2 = KIMServiceLocator.getKimTypeService(new QName(KIMServiceLocator.KIM_GROUP_SERVICE));
		assertNull("A null KimTypeService should have been returned.", typeService2);

		// test by passing the QName for the Permission TypeService
		
		QName permissionServiceName = new QName("permissionPermissionTypeService");
		KimTypeService typeService3 = KIMServiceLocator.getKimTypeService(permissionServiceName);
		assertNotNull("permission type service should have been found", typeService3);
		
	}
	
}
