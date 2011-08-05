/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package edu.sampleu.travel.krad.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * For test view purposes only
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UITestListObject implements Serializable {
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private List<UITestListObject> subList = new ArrayList<UITestListObject>();
    private static final long serialVersionUID = -7525378097732916411L;
    
    
    /**
     * This constructs a ...
     * 
     */
    public UITestListObject() {
    }

    /**
     * This constructs a ...
     * 
     */
    public UITestListObject(String field1, String field2, String field3, String field4) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    /**
     * @return the field1
     */
    public String getField1() {
        return this.field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1) {
        this.field1 = field1;
    }

    /**
     * @return the field2
     */
    public String getField2() {
        return this.field2;
    }

    /**
     * @param field2 the field2 to set
     */
    public void setField2(String field2) {
        this.field2 = field2;
    }

    /**
     * @return the field3
     */
    public String getField3() {
        return this.field3;
    }

    /**
     * @param field3 the field3 to set
     */
    public void setField3(String field3) {
        this.field3 = field3;
    }

    /**
     * @return the field4
     */
    public String getField4() {
        return this.field4;
    }

    /**
     * @param field4 the field4 to set
     */
    public void setField4(String field4) {
        this.field4 = field4;
    }

    /**
     * @param subList the subList to set
     */
    public void setSubList(List<UITestListObject> subList) {
        this.subList = subList;
    }

    /**
     * @return the subList
     */
    public List<UITestListObject> getSubList() {
        return subList;
    }

}
