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
package org.kuali.rice.krad.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.test.MockController;
import org.kuali.rice.krad.test.TestForm;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.form.UifFormManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.Assert.fail;

/**
 * Test cases for {@link org.kuali.rice.krad.web.controller.UifControllerHandlerInterceptor}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifControllerHandlerInterceptorTest {

    private UifControllerHandlerInterceptor handlerInterceptor;
    private MockController controller;
    private MockHttpServletRequest request;
    private UifFormBase model;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        request.setMethod("POST");

        UifFormManager uifFormManager = new UifFormManager();

        String formKey = "TEST";

        model = new TestForm();
        model.setFormKey(formKey);
        uifFormManager.addSessionForm(model);

        request.getSession().setAttribute(UifParameters.FORM_MANAGER, uifFormManager);
        request.setParameter(UifParameters.FORM_KEY, formKey);

        handlerInterceptor = new UifControllerHandlerInterceptor();
        controller = new MockController();
    }

    /**
     * Tests method access is being granted where annotations are present and the method is within
     * the view configuration.
     */
    @Test
    public void testCheckHandlerMethodAccess() throws Exception {
        ViewPostMetadata viewPostMetadata = new ViewPostMetadata();
        model.setViewPostMetadata(viewPostMetadata);

        assertMethodAccess("Accessible annotation not picked up", "method1", true);
        assertMethodAccess("Custom method should be allowed due to not being in the available methods", "method2", true);
        viewPostMetadata.addAvailableMethodToCall( "method2" );
        assertMethodAccess("Accessible annotation picked up where not present", "method2", false);

        viewPostMetadata.addAccessibleMethodToCall("method4");
        viewPostMetadata.addAccessibleMethodToCall("method6");

        assertMethodAccess("Accessible method by view not picked up", "method4", true);
        assertMethodAccess("Accessible method by view not picked up", "method6", true);

        assertMethodAccess("Method not accessible for empty method to call", null, true);
    }

    /**
     * Helper method for testing {@link UifControllerHandlerInterceptor#checkHandlerMethodAccess}.
     *
     * @param failureMessage message to show if assert fails
     * @param methodToCall controller method to check access for
     * @param access expected access result
     * @throws Exception
     */
    protected void assertMethodAccess(String failureMessage, String methodToCall, boolean access) throws Exception {
        request.setParameter(UifParameters.METHOD_TO_CALL, methodToCall);

        // if method to call is blank, pick a method as the default handler
        if (StringUtils.isBlank(methodToCall)) {
            methodToCall = "method5";
        }

        try {
            handlerInterceptor.checkHandlerMethodAccess(request, getHandlerMethod(methodToCall));
        } catch (MethodAccessException e) {
            if (access) {
                fail(failureMessage);
            }

            return;
        }

        if (!access) {
            fail(failureMessage);
        }
    }

    /**
     * Builds instance of a handler method (using the controller) for the given method to call.
     *
     * @param methodToCall method on controller to build handler for
     * @return handler method instance
     */
    protected HandlerMethod getHandlerMethod(String methodToCall) {
        Method method = null;

        for (Method controllerMethod : controller.getClass().getMethods()) {
            if (StringUtils.equals(controllerMethod.getName(), methodToCall)) {
                method = controllerMethod;
            }
        }

        if (method != null) {
            return new HandlerMethod(controller, method);
        }

        return null;
    }
}
