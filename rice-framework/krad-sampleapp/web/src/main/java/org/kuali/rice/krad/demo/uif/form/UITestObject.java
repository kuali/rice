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
package org.kuali.rice.krad.demo.uif.form;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For test view purposes only
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UITestObject implements Serializable {
    private static final long serialVersionUID = -7525378097732916411L;

    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;

    private KualiDecimal feeAmount;

    private static int count = 0;
    private Integer int1;

    private boolean bfield;

    private List<String> stringList = Arrays.asList("String1", "String2", "String3");

    private Date date1;
    private String date1Str;

    static int skipCount2 = 0;
    private Date date2;

    private MultipartFile fileUpload;

    // private String fileUploadName;

    private UITestObject innerObject;

    private Map<String, Object> remoteFieldValuesMap;

    private List<UITestObject> subList = new ArrayList<UITestObject>();

    public UITestObject() {
        remoteFieldValuesMap = new HashMap<String, Object>();
        remoteFieldValuesMap.put("remoteField1", "Apple");
        remoteFieldValuesMap.put("remoteField2", "Banana");
        remoteFieldValuesMap.put("remoteField3", true);
        remoteFieldValuesMap.put("remoteField4", "Fruit");

        int1 = ++count;

        feeAmount = new KualiDecimal(int1);

        // populate date1 and date1Str
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(2010, 1, 1);
            long offset = cal.getTime().getTime();
            cal.set(2014, 1, 1);
            long end = cal.getTime().getTime();
            long diff = end - offset + 1;
            date1 = new Date(offset + (long) (Math.random() * diff));
            date1Str = new SimpleDateFormat("MM/dd/yyyy").format(date1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // populate date2
        skipCount2++;
        // every fifth date leave blank
        if (skipCount2 % 5 != 0 && date2 == null) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(2010, 1, 1);
                long offset = cal.getTime().getTime();
                cal.set(2014, 1, 1);
                long end = cal.getTime().getTime();
                long diff = end - offset + 1;
                date2 = new Date(offset + (long) (Math.random() * diff));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UITestObject(String field1, String field2, String field3, String field4) {
        this();

        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
    }

    public UITestObject(String field1, String field2, String field3, String field4, String field5, String field6) {
        this(field1, field2, field3, field4);

        this.field5 = field5;
        this.field6 = field6;
    }

    public UITestObject(String field1, String field2, String field3, String field4, String field5, String field6,
            String field7, String field8) {
        this(field1, field2, field3, field4);

        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
    }

    public UITestObject(String field1, String field2, String field3, String field4, UITestObject innerObject) {
        this(field1, field2, field3, field4);

        this.innerObject = innerObject;
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
     * @return the field3 as a 'dollar' value for testing
     */
    public String getDollar3() {
        if (this.field3 != null && this.field3.length() > 0) {
            return "$" + this.field3 + ".00";
        }
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

    public KualiDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(KualiDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public MultipartFile getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(MultipartFile fileUpload) {
        this.fileUpload = fileUpload;
        // this.fileUploadName = fileUpload.getOriginalFilename();
    }

    //    public String getFileUploadName() {
    //        return fileUploadName;
    //    }
    //
    //    public void setFileUploadName(String fileUploadName) {
    //        this.fileUploadName = fileUploadName;
    //    }

    /**
     * @param subList the subList to set
     */
    public void setSubList(List<UITestObject> subList) {
        this.subList = subList;
    }

    /**
     * @return the subList
     */
    public List<UITestObject> getSubList() {
        return subList;
    }

    public Map<String, Object> getRemoteFieldValuesMap() {
        return remoteFieldValuesMap;
    }

    public void setRemoteFieldValuesMap(Map<String, Object> remoteFieldValuesMap) {
        this.remoteFieldValuesMap = remoteFieldValuesMap;
    }

    /**
     * boolean field
     *
     * @return bField
     */
    public boolean isBfield() {
        return bfield;
    }

    /**
     * @param bfield boolean field
     */
    public void setBfield(boolean bfield) {
        this.bfield = bfield;
    }

    @Override
    public String toString() {
        return "" + field1 + field2 + field3 + field4;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public UITestObject getInnerObject() {
        return innerObject;
    }

    public void setInnerObject(UITestObject innerObject) {
        this.innerObject = innerObject;
    }

    public void setInputField1(String field) {
        field1 = field;
    }

    public String getInputField1() {
        return field1;
    }

    public void setInputField2(String field) {
        field2 = field;
    }

    public String getInputField2() {
        return field2;
    }

    public void setInputField3(String field) {
        field3 = field;
    }

    public String getInputField3() {
        return field3;
    }

    public void setInputField4(String field) {
        field4 = field;
    }

    public String getInputField4() {
        return field4;
    }

    public Integer getInt1() {
        return int1;
    }

    public void setInt1(Integer int1) {
        this.int1 = int1;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public String getDate1Str() {
        return date1Str;
    }

    public void setDate1Str(String date1Str) {
        this.date1Str = date1Str;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }
}

