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
package org.kuali.rice.krad.uif;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * This class MethodInvokerConfig
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MethodInvokerConfigTest {

    @Test
    public void testGetArgumentsTypes() {
        // if arguments are not set, will select first match
        checkGetArgumentTypes(TestMethodClass.class, "retrieveTestObject", 0, 1);

        checkGetArgumentTypes(TestMethodClass.class, "retrieveTestObject", 1, 1);
        checkGetArgumentTypes(TestMethodClass.class, "retrieveTestObject", 2, 2);
    }

    protected void checkGetArgumentTypes(Class targetClass, String methodName, int argumentTypeSize, int expectedSize) {
        MethodInvokerConfig methodInvokerConfig = new MethodInvokerConfig();
        methodInvokerConfig.setTargetClass(targetClass);
        methodInvokerConfig.setTargetMethod(methodName);
        methodInvokerConfig.setArguments(new Object[argumentTypeSize]);

        Class[] classes = methodInvokerConfig.getArgumentTypes();
        Assert.assertEquals("Should return " + argumentTypeSize + " classes", expectedSize, classes.length);
    }

    private class TestMethodClass {

        public List<Object> retrieveTestObject(String term1) {
            return new ArrayList<Object>();
        }

        public List<Object> retrieveTestObject(String term1, String term2) {
            return new ArrayList<Object>();
        }

    }

}
