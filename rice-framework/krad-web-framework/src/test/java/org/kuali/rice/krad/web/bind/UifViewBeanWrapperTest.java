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
package org.kuali.rice.krad.web.bind;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.test.TestForm;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link UifViewBeanWrapper}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifViewBeanWrapperTest {

    private UifViewBeanWrapper beanWrapper;

    @Before
    public void setUp() throws Exception {
        TestForm model = new TestForm();
        model.setMethodToCall("field7TestMethodToCall");

        UifBeanPropertyBindingResult bindingResult = new UifBeanPropertyBindingResult(model, "model", true, 100);

        beanWrapper = new UifViewBeanWrapper(model, bindingResult);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addParameter(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME, "field7TestMethodToCall");

        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));
    }

    /**
     * Tests binding annotations are being correctly picked up for various property paths.
     */
    @Test
    public void testCheckForAnnotationInPropertyPath() {
        assertBindingAnnotationsInPath("Annotation on simple form property not picked up", "field1", Boolean.TRUE);

        assertBindingAnnotationsInPath("Annotation on simple form property not picked up", "field2", Boolean.FALSE);

        assertBindingAnnotationsInPath("Annotation picked up where not present", "field3", null);

        assertBindingAnnotationsInPath("Annotation on nested property not picked up", "dataObject.field5",
                Boolean.FALSE);

        assertBindingAnnotationsInPath("Annotation picked up on nested property where not present", "dataObject.field1",
                null);

        assertBindingAnnotationsInPath("Annotation on nested property not picked up", "dataObject.nestedObject.field1",
                Boolean.TRUE);

        assertBindingAnnotationsInPath("Annotation not correctly picked up for multiple annotations",
                "dataObject.nestedObject.field5", Boolean.FALSE);

        assertBindingAnnotationsInPath("Annotation on nested list property not picked up", "dataObject.list[0].field5",
                Boolean.FALSE);

        assertBindingAnnotationsInPath("Annotation on nested list property picked up where not present",
                "dataObject.list[0].field1", null);

        assertBindingAnnotationsInPath("Annotation on map property not picked up", "dataObject.map['key']",
                Boolean.TRUE);

        assertBindingAnnotationsInPath("Annotation on accessible list property not picked up",
                "dataObject.list2[3].field7", Boolean.TRUE);

        assertBindingAnnotationsInPath("Annotation picked up for wrong request method", "field4", null);

        assertBindingAnnotationsInPath("Annotation picked up for wrong request method", "field7", Boolean.TRUE);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));

        assertBindingAnnotationsInPath("Annotation not picked up for correct request method", "field4", Boolean.TRUE);

        assertBindingAnnotationsInPath("Annotation not picked up for multiple request methods", "dataObject.field8",
                Boolean.TRUE);
    }

    /**
     * Helper method for testing {@link UifViewBeanWrapper#checkBindingAnnotationsInPath(java.lang.String)}.
     *
     * @param failureMessage message to show if assert fails
     * @param path property path to invoke method with
     * @param result expected annotation result
     */
    protected void assertBindingAnnotationsInPath(String failureMessage, String path, Boolean result) {
        Boolean bindingAnnotationAccess = beanWrapper.checkBindingAnnotationsInPath(path);

        assertEquals(failureMessage, result, bindingAnnotationAccess);
    }

    /**
     * Tests binding access is correctly being granted and prevented for various cases.
     */
    @Test
    public void testCheckPropertyAccess() {
        ViewPostMetadata viewPostMetadata = new ViewPostMetadata();
        ((ViewModel) beanWrapper.getWrappedInstance()).setViewPostMetadata(viewPostMetadata);

        viewPostMetadata.addAccessibleBindingPath("field5");
        viewPostMetadata.addAccessibleBindingPath("dataObject.field1");
        viewPostMetadata.addAccessibleBindingPath("dataObject.list[3].field3");
        viewPostMetadata.addAccessibleBindingPath("dataObject.list[3].list[2].field3");

        assertPropertyBindingAccess("Access not granted for view binding path", "field5", true);

        assertPropertyBindingAccess("Access granted for path without annotation and not in view", "field6", false);

        assertPropertyBindingAccess("Access not granted for path with accessible annotation", "field1", true);

        assertPropertyBindingAccess("Access granted for path with protected annotation", "field2", false);

        assertPropertyBindingAccess("Access not granted for nested view binding path", "dataObject.field1", true);

        assertPropertyBindingAccess("Access not granted for nested view binding path", "dataObject.list[3].field3",
                true);

        assertPropertyBindingAccess("Access not granted for nested view binding path",
                "dataObject.list[3].list[2].field3", true);

        assertPropertyBindingAccess("Access granted for path without annotation and not in view",
                "dataObject.list[3].list[1].field3", false);
    }

    /**
     * Helper method for testing {@link UifViewBeanWrapper#checkPropertyBindingAccess(java.lang.String)}.
     *
     * @param failureMessage message to show if assert fails
     * @param path property path to invoke method with
     * @param access expected access result
     */
    protected void assertPropertyBindingAccess(String failureMessage, String path, boolean access) {
        boolean isPropertyAccessible = beanWrapper.checkPropertyBindingAccess(path);

        assertEquals(failureMessage, Boolean.valueOf(access), Boolean.valueOf(isPropertyAccessible));
    }
}
