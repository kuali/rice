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
package org.kuali.notification.services.impl;

import java.util.Collection;

import org.junit.Test;
import org.kuali.notification.bo.NotificationContentType;
import org.kuali.notification.service.NotificationContentTypeService;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;


/**
 * Tests NotificationContentTypeService implementation 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@BaselineMode(Mode.ROLLBACK)
public class NotificationContentTypeServiceImplTest extends NotificationTestCaseBase {
    @Test public void testUpdate() {
        NotificationContentType type = new NotificationContentType();
        type.setDescription("blah");
        type.setName("test");
        type.setNamespace("test");
        type.setXsd("test");
        type.setXsl("test");
        
        NotificationContentTypeService impl = services.getNotificationContentTypeService();
        impl.saveNotificationContentType(type);
        
        type = impl.getNotificationContentType("test");
        assertEquals("test", type.getName());
        assertEquals("blah", type.getDescription());
        assertEquals(true, type.isCurrent());
        assertEquals(Integer.valueOf(0), type.getVersion());
        
        type = new NotificationContentType();
        type.setDescription("blah 2");
        type.setName("test");
        type.setNamespace("test 2");
        type.setXsd("test 2");
        type.setXsl("test 2");
        
        impl.saveNotificationContentType(type);
        
        type = impl.getNotificationContentType("test");
        assertEquals("test", type.getName());
        assertEquals("blah 2", type.getDescription());
        assertEquals(true, type.isCurrent());
        assertEquals(Integer.valueOf(1), type.getVersion());
        
        Collection<NotificationContentType> alltypes = impl.getAllCurrentContentTypes();
        assertEquals(3, alltypes.size());
    }
}
