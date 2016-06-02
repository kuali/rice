/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;
import static org.mockito.Mockito.mock;

import java.io.FileNotFoundException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.ProcessLoggingUnitTest;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.mock.web.MockHttpServletRequest;

public class ComponentFreemarkerTest extends ProcessLoggingUnitTest {
    
    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-ComponentFreemarkerTest");
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        UifUnitTestUtils.tearDownMockConfig();
    }

    @Test
    public void testHtmlOutput() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        View view = mock(View.class);
        ViewLifecycle.encapsulateLifecycle(view, new Object(), request, new Runnable() {
            @Override
            public void run() {
                Message msg = CopyUtils.copy(ComponentFactory.getMessage());
                msg.setMessageText("foobar");
                msg.setId("_naps");
                msg.setWrapperTag("pans");

                msg.setViewStatus(UifConstants.ViewStatus.FINAL);

                RenderComponentPhase renderPhase = (RenderComponentPhase) KRADServiceLocatorWeb
                        .getViewLifecyclePhaseBuilder().buildPhase(UifConstants.ViewPhases.RENDER, msg, null, "", null);

                try {
                    ViewLifecycle.getProcessor().performPhase(renderPhase);
                } catch (IllegalStateException e) {
                    if (e.getCause() instanceof FileNotFoundException) {
                        assumeNoException(e.getCause());
                    } else {
                        throw e;
                    }
                }

                assertTrue(msg.isSelfRendered());
                assertEquals("<pans id=\"_naps\" class=\"uif-message\"     >\r\n" +
                        "foobar  </pans>", msg.getRenderedHtmlOutput().trim());
            }
        });
    }

}
