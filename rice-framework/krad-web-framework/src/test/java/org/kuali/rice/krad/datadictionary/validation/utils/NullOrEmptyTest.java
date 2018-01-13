/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.utils;

import org.junit.Test;
import org.junit.Assert;
import org.kuali.rice.krad.datadictionary.validation.Person;
import org.kuali.rice.krad.datadictionary.validation.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Unit test for ValidationUtils.isNullOrEmpty()
 */
public class NullOrEmptyTest {

    @Test
    public void testNullObject() {
        Person person = null ;
        Assert.assertTrue(ValidationUtils.isNullOrEmpty(person));
    }

    @Test
    public void testNotNullObject() {
        Person person = new Person();
        Assert.assertFalse(ValidationUtils.isNullOrEmpty(person));
    }

    @Test
    public void testStringEmpty() {
        String message = "";
        Assert.assertTrue(ValidationUtils.isNullOrEmpty(message));
    }

    @Test
    public void testStringNotEmpty() {
        String message = "Hi";
        Assert.assertFalse(ValidationUtils.isNullOrEmpty(message));
    }

    @Test
    public void testListEmpty() {
        ArrayList<String> quickList = new ArrayList<String>();
        Assert.assertTrue(ValidationUtils.isNullOrEmpty(quickList));
    }

    @Test
    public void testListNotEmpty() {
        ArrayList<String> quickList = new ArrayList<String>();
        quickList.add("Hi");
        Assert.assertFalse(ValidationUtils.isNullOrEmpty(quickList));
    }

    @Test
    public void testSetEmpty() {
        HashSet<String> quickSet = new HashSet<String>() ;
        Assert.assertTrue(ValidationUtils.isNullOrEmpty(quickSet));
    }

    @Test
    public void testSetNotEmpty() {
        HashSet<String> quickSet = new HashSet<String>() ;
        quickSet.add("Hi");
        Assert.assertFalse(ValidationUtils.isNullOrEmpty(quickSet));
    }

    @Test
    public void testMapEmpty() {
        HashMap<String, String> quickMap = new HashMap<String, String>();
        Assert.assertTrue(ValidationUtils.isNullOrEmpty(quickMap));
    }

    @Test
    public void testMapNotEmpty() {
        HashMap<String, String> quickMap = new HashMap<String, String>();
        quickMap.put("Hi", "There!");
        Assert.assertFalse(ValidationUtils.isNullOrEmpty(quickMap));
    }
}
