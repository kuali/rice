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
package org.kuali.rice.krad.test;

import org.kuali.rice.krad.web.bind.RequestAccessible;
import org.kuali.rice.krad.web.bind.RequestProtected;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Form class for test cases.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestForm extends UifFormBase {

    @RequestAccessible
    private String field1;

    @RequestProtected
    private String field2;

    private String field3;

    @RequestAccessible(method= RequestMethod.GET)
    private String field4;

    private String field5;

    private String field6;

    @RequestAccessible(methodToCalls = { "field7TestMethodToCall" })
    private String field7;

    private TestDataObject dataObject;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
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

    public TestDataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(TestDataObject dataObject) {
        this.dataObject = dataObject;
    }
}
