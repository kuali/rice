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
package org.kuali.rice.kim.test.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleTypeServiceImplTest extends KIMTestCase {

	private KimRoleTypeServiceBase roleTypeService;

	public void setUp() throws Exception {
		super.setUp();
		roleTypeService = (KimRoleTypeServiceBase)GlobalResourceLoader.getService(new QName("KIM", "kimRoleTypeService"));
	}

	@Test
	public void testTranslateQualificationAttributeSet() {
		// code not ready yet
		AttributeSet qualification = new AttributeSet();
		qualification.put("Attribute 1", "PHYS");
		AttributeSet roleQualifier = new AttributeSet();
		roleQualifier.put("Attribute 2", "CHEM");
		assertTrue( "low level qualification rp1 match hie level qualifier rp2", roleTypeService.doesRoleQualifierMatchQualification(qualification, roleQualifier));	
	}


}
