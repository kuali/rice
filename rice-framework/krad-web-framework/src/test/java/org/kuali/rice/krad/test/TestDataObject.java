/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.test;

import org.kuali.rice.krad.web.bind.RequestAccessible;
import org.kuali.rice.krad.web.bind.RequestProtected;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Data object for test cases.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestDataObject implements Serializable {
    private static final long serialVersionUID = -7525378097732916411L;

    private String field1;
    private String field2;
    private String field3;
    private String field4;

    @RequestProtected
    private String field5;
    private String field6;
    private String field7;

    @RequestAccessible(method = {RequestMethod.GET, RequestMethod.POST})
    private String field8;
    private boolean bfield;

    private List<String> stringList = Arrays.asList("String1", "String2", "String3");

    @RequestAccessible
    private TestDataObject nestedObject;

    @RequestAccessible
    private Map<String, Object> map;
    private List<TestDataObject> list = new ArrayList<TestDataObject>();

    @RequestAccessible
    private List<TestDataObject> list2 = new ArrayList<TestDataObject>();

    public TestDataObject() {
    }

    public TestDataObject(String field1, String field2, String field3, String field4) {
        this();

        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    public String getField1() {
        return this.field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return this.field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return this.field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return this.field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public String getField6() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6 = field6;
    }

    public String getField7() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7 = field7;
    }

    public String getField8() {
        return field8;
    }

    public void setField8(String field8) {
        this.field8 = field8;
    }

    public boolean isBfield() {
        return bfield;
    }

    public void setBfield(boolean bfield) {
        this.bfield = bfield;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public TestDataObject getNestedObject() {
        return nestedObject;
    }

    public void setNestedObject(TestDataObject nestedObject) {
        this.nestedObject = nestedObject;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public List<TestDataObject> getList() {
        return list;
    }

    public void setList(List<TestDataObject> list) {
        this.list = list;
    }

    public List<TestDataObject> getList2() {
        return list2;
    }

    public void setList2(List<TestDataObject> list2) {
        this.list2 = list2;
    }
}

