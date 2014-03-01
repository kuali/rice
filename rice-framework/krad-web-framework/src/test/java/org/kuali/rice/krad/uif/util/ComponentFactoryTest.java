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
package org.kuali.rice.krad.uif.util;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.view.InquiryView;

/**
 * Unit tests for proving correct operation of the ViewHelperService.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFactoryTest extends ProcessLoggingUnitTest {
    
    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-ComponentFactoryTest");
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        GlobalResourceLoader.stop();
    }

    @Test
    public void testSanity() throws Throwable {
        try {
            Message message = ComponentFactory.getMessage();
            assertEquals("uif-message", message.getCssClasses().get(0));
        } catch (NullPointerException e) {
            Assume.assumeNoException("Missing required testing resources, skipping", e);
        }
    }

    @Test
    public void testInquiry() throws Throwable {
        try {
            InquiryView inquiryView = ComponentFactory.getInquiryView();
            assertEquals("uif-formView", inquiryView.getCssClasses().get(0));
        } catch (NullPointerException e) {
            Assume.assumeNoException("Missing required testing resources, skipping", e);
        }
    }

}
