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
package org.kuali.rice.kcb.util;

import java.lang.reflect.Proxy;

import junit.framework.Assert;

import org.junit.Test;
import org.kuali.rice.kcb.util.BeanFactoryInvocationHandler;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.test.AssertThrows;


/**
 * Tests BeanFactoryIinvocationHandler class 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BeanFactoryInvocationHandlerTest {
     private static interface BadInterface {
         public int add(int a, int b);
         public void notAGetter();
         public void get();
         public Object getBean();
     }

     @Test
     public void test() {
         StaticListableBeanFactory bf = new StaticListableBeanFactory();
         bf.addBean("bean", "This is a bean");
         final BadInterface bad = (BadInterface)
             Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                    new Class[] { BadInterface.class },
                                    new BeanFactoryInvocationHandler(bf));
         new AssertThrows(RuntimeException.class) {
             public void test() {
                 int result = bad.add(2, 2);
             }
         }.runTest();
         new AssertThrows(RuntimeException.class) {
             public void test() {
                 bad.notAGetter();
             }
         }.runTest();
         new AssertThrows(RuntimeException.class) {
             public void test() {
                 bad.get();
             }
         }.runTest();
         Assert.assertEquals("This is a bean", bad.getBean());
     }
}