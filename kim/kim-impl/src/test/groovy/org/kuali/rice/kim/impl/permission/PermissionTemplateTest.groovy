/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.permission

import org.junit.Assert
import org.junit.Test
import org.kuali.rice.kim.api.permission.KimPermissionTemplate
import org.kuali.rice.kim.api.type.KimType

class PermissionTemplateTest {
	
	private static final String ID = "50"
	private static final String NAMESPACE_CODE = "KUALI"
	private static final String NAME = "PermissionTemplateName"
	private static final String KIM_TYPE_ID = "611777493"
		
	@Test
	public void testNotEqualsWithKimPermissionTemplate() {
    KimPermissionTemplate immutable = KimPermissionTemplate.Builder.create(ID, NAMESPACE_CODE, NAME, KIM_TYPE_ID).build()
    KimPermissionTemplateBo bo = KimPermissionTemplateBo.from(immutable)
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
    Assert.assertEquals(immutable, KimPermissionTemplateBo.to(bo))
    }
}
