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

    private String travelAccountAttributeName;

    private String dollar1;
    private String dollar2;
    private String dollar3;
    private String dollar4;
    private String dollar5;
    private String dollar6;
    private String dollar7;
    private String dollar8;

    private KualiDecimal feeAmount;

    private static int count = 0;
    private Integer int1 = 1;
    private Integer int2 = 2;
    private Integer int3 = 3;

    private boolean bfield;
    private boolean bfield2;

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
        int3 = int1 * int2;

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

        this.dollar1 = setCurrency(field1);
        this.dollar2 = setCurrency(field2);
        this.dollar3 = setCurrency(field3);
        this.dollar4 = setCurrency(field4);
    }

    public UITestObject(String field1, String field2, String field3, String field4, String field5, String field6) {
        this(field1, field2, field3, field4);

        this.field5 = field5;
        this.field6 = field6;
        this.dollar5 = setCurrency(field5);
        this.dollar6 = setCurrency(field6);
    }

    public UITestObject(String field1, String field2, String field3, String field4, String field5, String field6,
            String field7, String field8) {
        this(field1, field2, field3, field4, field5, field6);

        this.field7 = field7;
        this.field8 = field8;
        this.dollar7 = setCurrency(field7);
        this.dollar8 = setCurrency(field8);
    }

    public UITestObject(String field1, String field2, String field3, String field4, UITestObject innerObject) {
        this(field1, field2, field3, field4);

        this.innerObject = innerObject;
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private String setCurrency(String field) {
        if (field != null && field.length() > 0 && isInteger(field)) {
            if (!field.contains("$")) {
                field = "$" + field ;
            }
            if (!field.contains(".00")) {
                field = field + ".00";
            }
        }
        return field;
    }

    private String unsetCurrency(String field){
        field = field.replace("$","");
        field = field.replace(".00","");
        return field;
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

    public String getTravelAccountAttributeName() {
        if (travelAccountAttributeName == null) {
            return "number";
        }
        return travelAccountAttributeName;
    }

    public void setTravelAccountAttributeName(String travelAccountAttributeName) {
        this.travelAccountAttributeName = travelAccountAttributeName;
    }

    public String getDollar1() {
        return dollar1;
    }

    public void setDollar1(String dollar1) {
        this.field1 = unsetCurrency(dollar1);
        this.dollar1 = setCurrency(dollar1);
    }

    public String getDollar2() {
        return dollar2;
    }

    public void setDollar2(String dollar2) {
        this.field2 = unsetCurrency(dollar2);
        this.dollar2 = setCurrency(dollar2);
    }

    public String getDollar3() {
        return dollar3;
    }

    public void setDollar3(String dollar3) {
        this.field3 = unsetCurrency(dollar3);
        this.dollar3 = setCurrency(dollar3);
    }

    public String getDollar4() {
        return dollar4;
    }

    public void setDollar4(String dollar4) {
        this.field4 = unsetCurrency(dollar4);
        this.dollar4 = setCurrency(dollar4);
    }

    public String getDollar5() {
        return dollar5;
    }

    public void setDollar5(String dollar5) {
        this.field5 = unsetCurrency(dollar5);
        this.dollar5 = setCurrency(dollar5);
    }

    public String getDollar6() {
        return dollar6;
    }

    public void setDollar6(String dollar6) {
        this.field6 = unsetCurrency(dollar6);
        this.dollar6 = setCurrency(dollar6);
    }

    public String getDollar7() {
        return dollar7;
    }

    public void setDollar7(String dollar7) {
        this.field7 = unsetCurrency(dollar7);
        this.dollar7 = setCurrency(dollar7);
    }

    public String getDollar8() {
        return dollar8;
    }

    public void setDollar8(String dollar8) {
        this.field8 = unsetCurrency(dollar8);
        this.dollar8 = setCurrency(dollar8);
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

    public List<UITestObject> getSubList() {
        return subList;
    }

    public void setSubList(List<UITestObject> subList) {
        this.subList = subList;
    }

    public Map<String, Object> getRemoteFieldValuesMap() {
        return remoteFieldValuesMap;
    }

    public void setRemoteFieldValuesMap(Map<String, Object> remoteFieldValuesMap) {
        this.remoteFieldValuesMap = remoteFieldValuesMap;
    }

    public boolean isBfield() {
        return bfield;
    }

    public void setBfield(boolean bfield) {
        this.bfield = bfield;
    }

    public boolean isBfield2() {
        return bfield2;
    }

    public void setBfield2(boolean bfield2) {
        this.bfield2 = bfield2;
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

    public Integer getInt2() {
        return int2;
    }

    public void setInt2(Integer int2) {
        this.int2 = int2;
    }

    public Integer getInt3() {
        return int3;
    }

    public void setInt3(Integer int3) {
        this.int3 = int3;
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

    // add static method to be used in library
    static public String getStaticData() {
        return "test";
    }
}

