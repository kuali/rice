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
package org.kuali.rice.krad.labs;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.demo.uif.form.UIInactivatableTestObject;
import org.kuali.rice.krad.demo.uif.form.UITestObject;
import org.kuali.rice.krad.file.FileMetaBlob;
import org.kuali.rice.krad.file.FileMeta;
import org.kuali.rice.krad.labs.fileUploads.FileWithDetails;
import org.kuali.rice.krad.uif.util.SessionTransient;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Basic form for the lab views
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradLabsForm extends UifFormBase {
    private static final long serialVersionUID = -7525378097732916418L;
    private String themeName;
    private String exampleShown;
    private String currentExampleIndex;

    //Fields separated by demonstration type below:

    //InputField
    private String inputField1;
    private String inputField2 = "02/13/2014";
    private String inputField3;
    private String inputField4;
    private String inputField5;
    private String inputField6;
    private String inputField7;
    private String inputField8;
    private String inputField9;
    private String inputField10;
    private String inputField11;
    private String inputField12 = "testing";
    private String inputField13;
    private String inputField14;
    private String inputField15;
    private String inputField16;
    private String inputField17;
    private String inputField18;
    private String inputField19;
    private String inputField20;
    private String inputField21;
    private String inputField22;
    private String inputField23;
    private String inputField24;
    private String inputField25;

    private boolean booleanField1 = false;
    private boolean booleanField2 = false;
    private boolean booleanField3 = false;

    private List<String> checkboxesField1;
    private List<String> checkboxesField2;
    private List<String> checkboxesField3;
    private List<String> checkboxesField4;
    private List<String> multiSelectField1;
    private List<String> multiSelectField2;

    private String testPersonId;
    private Person testPerson;

    private String testGroupId;

    private String testPrincipalId1 = "eric";
    private String testPrincipalId2 = "erin";
    private String testPrincipalId3= "test1";
    private String testPrincipalId4= "edna";
    private String testPrincipalName1 = "eric";
    private String testPrincipalName2 = "erin";
    private String testPrincipalName3 = "test1";
    private String testPrincipalName4 = "edna";

    private String testGroupId1 = "2005";
    private String testGroupId2 = "2001";
    private String testGroupId3 = "2000";
    private String testGroupId4 = "2005";
    private String testGroupId5 = "2005";
    private String testGroupNamespaceCode1 = "KUALI";
    private String testGroupNamespaceCode2 = "KUALI";
    private String testGroupNamespaceCode3 = "KR-WKFLW";
    private String testGroupNamespaceCode4 = "KUALI";
    private String testGroupNamespaceCode5 = "KUALI";
    private String testGroupName1 = "Group1";
    private String testGroupName2 = "TestGroup1";
    private String testGroupName3 = "NotificationAdmin";
    private String testGroupName4 = "Group1";
    private String testGroupName5 = "Group1";

    private String testRoleId1 = "KRSAP10005";
    private String testRoleId2 = "KR1000";
    private String testRoleId3 = "67";
    private String testRoleId4 = "KR1000";
    private String testRoleId5 = "KR1000";
    private String testRoleNamespaceCode1 = "KR-SAP";
    private String testRoleNamespaceCode2 = "KUALI";
    private String testRoleNamespaceCode3 = "KR-WKFLW";
    private String testRoleNamespaceCode4 = "KUALI";
    private String testRoleNamespaceCode5 = "KUALI";
    private String testRoleName1 = "Travel Approver";
    private String testRoleName2 = "GuestRole";
    private String testRoleName3 = "Router";
    private String testRoleName4 = "GuestRole";
    private String testRoleName5 = "GuestRole";

    //DataField
    private String dataField1 = "1001";
    private String dataField2 = "";
    private String dataField3 = "My Book Title";
    private String dataField4 = "My Book Title";
    private String dataField5 = "Option 2";
    private String dataField6 = "Some text";
    private String dataField7 = "Book Title";
    private String dataField8 = "My Book Title";
    private String dataField9 = "My Other Book Title";
    private String dataField10 = "My Other Book Title";

    //MessageField
    private String messageField1;
    private String richMessageField = "[color=green][b]Message[/b][/color]";

    // do not initialize this object (for binding testing)
    private UITestObject nestedObject1;

    // Collections
    private List<UITestObject> collection1 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_2 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_3 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_4 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_5 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_6 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_7 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_8 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_9 = new ArrayList<UITestObject>();
    private List<UITestObject> collection1_10 = new ArrayList<UITestObject>();
    private List<UITestObject> collection2 = new ArrayList<UITestObject>();
    private List<UITestObject> collection3 = new ArrayList<UITestObject>();
    private List<UITestObject> collection4 = new ArrayList<UITestObject>();
    private List<UITestObject> collection5 = new ArrayList<UITestObject>();

    private List<UITestObject> emptyCollection = new ArrayList<UITestObject>();

    private List<UITestObject> mediumCollection1 = new ArrayList<UITestObject>();
    private List<UITestObject> mediumCollection2 = new ArrayList<UITestObject>();

    private List<UIInactivatableTestObject> inactivatableCollection = new ArrayList<UIInactivatableTestObject>();
    private List<UIInactivatableTestObject> inactivatableCollection2 = new ArrayList<UIInactivatableTestObject>();

    private List<UITestObject> groupedCollection1 = new ArrayList<UITestObject>();
    private List<UITestObject> groupedCollection2 = new ArrayList<UITestObject>();
    private List<UITestObject> groupedCollection3 = new ArrayList<UITestObject>();
    private List<UITestObject> doubleGroupedCollection = new ArrayList<UITestObject>();

    private String fakeTotal = "123(server value)";

    private List<FileMeta> files = new ArrayList<FileMeta>();
    private List<FileWithDetails> files2 = new ArrayList<FileWithDetails>();

    @SessionTransient
    private MultipartFile uploadOne;

    @SessionTransient
    private MultipartFile uploadTwo;

    private String backdoorId;

    public KradLabsForm() {
        super();

        messageField1 = "fruits";

        getCollection1().add(new UITestObject("13", "14", "15", "16"));
        getCollection1().add(new UITestObject("17", "18", "19", "20"));
        getCollection1().add(new UITestObject("5", "6", "7", "8"));
        getCollection1().add(new UITestObject("1", "2", "3", "4"));
        getCollection1().add(new UITestObject("9", "10", "11", "12"));
        getCollection1().add(new UITestObject("13", "14", "15", "16"));
        getCollection1().add(new UITestObject("213", "143", "151", "126"));
        getCollection1().add(new UITestObject("133", "144", "155", "156"));
        getCollection1().add(new UITestObject("25", "14", "15", "15"));
        getCollection1().add(new UITestObject("1", "5", "5", "4"));
        getCollection1().add(new UITestObject("5", "5", "5", "5"));
        getCollection1().add(new UITestObject("5", "7", "3", "1"));

        for (int i = 0; i < 20; i++) {
            List<UITestObject> mediumSubCollection1 = new ArrayList<UITestObject>();
            List<UITestObject> mediumSubCollection2 = new ArrayList<UITestObject>();

            for (int j = 0; j < 5; j++) {
                mediumSubCollection1.add(new UITestObject("ab extra", "ab extra", "ab extra", "ab extra", "ab extra",
                        "ab extra", "ab extra", "ab extra"));

                mediumSubCollection2.add(new UITestObject("ab extra", "ab extra", "ab extra", "ab extra", "ab extra",
                        "ab extra", "ab extra", "ab extra"));
            }

            UITestObject line = new UITestObject("ab extra", "ab extra", "ab extra", "ab extra", "ab extra", "ab extra",
                    "ab extra", "ab extra");
            line.setSubList(mediumSubCollection1);

            UITestObject line2 = new UITestObject("ab extra", "ab extra", "ab extra", "ab extra", "ab extra",
                    "ab extra", "ab extra", "ab extra");
            line2.setSubList(mediumSubCollection2);

            mediumCollection1.add(line);
            mediumCollection2.add(line2);
        }

        collection1_2.addAll(collection1);
        collection1_3.addAll(collection1);
        collection1_4.addAll(collection1);
        collection1_5.addAll(collection1);
        collection1_6.addAll(collection1);
        collection1_7.addAll(collection1);
        collection1_8.addAll(collection1);
        collection1_9.addAll(collection1);
        collection1_10.addAll(collection1);

        getCollection2().add(new UITestObject("A", "B", "C", "D"));
        getCollection2().add(new UITestObject("1", "2", "3", "4"));
        getCollection2().add(new UITestObject("W", "X", "Y", "Z"));
        collection2.add(new UITestObject("a", "b", "c", "d"));
        collection2.add(new UITestObject("a", "s", "d", "f"));

        collection3.add(new UITestObject("A", "B", "C", "D"));
        collection3.get(0).getSubList().add(new UITestObject("A", "B", "C", "D"));
        collection3.get(0).getSubList().add(new UITestObject("1", "2", "3", "4"));
        collection3.get(0).getSubList().add(new UITestObject("W", "X", "Y", "Z"));
        collection3.add(new UITestObject("1", "2", "3", "4"));
        collection3.get(1).getSubList().add(new UITestObject("A", "B", "C", "D"));
        collection3.get(1).getSubList().add(new UITestObject("1", "2", "3", "4"));
        collection3.add(new UITestObject("W", "X", "Y", "Z"));
        collection3.get(2).getSubList().add(new UITestObject("W", "X", "Y", "Z"));

        collection4.add(new UITestObject("A", "B", "C", "D"));
        collection4.get(0).getSubList().add(new UITestObject("A", "B", "C", "D"));
        collection4.get(0).getSubList().add(new UITestObject("1", "2", "3", "4"));
        collection4.get(0).getSubList().add(new UITestObject("W", "X", "Y", "Z"));
        collection4.add(new UITestObject("1", "2", "3", "4"));
        collection4.get(1).getSubList().add(new UITestObject("a", "b", "C", "D"));
        collection4.get(1).getSubList().add(new UITestObject("a", "s", "D", "F"));

        //triple nesting
        collection5.add(new UITestObject("a", "a", "a", "a"));
        collection5.get(0).getSubList().add(new UITestObject("A", "B", "C", "D"));
        collection5.get(0).getSubList().get(0).getSubList().add(new UITestObject("a3", "3", "3", "3"));
        collection5.get(0).getSubList().get(0).getSubList().add(new UITestObject("a3", "3", "3", "3"));
        collection5.get(0).getSubList().add(new UITestObject("1", "2", "3", "4"));
        collection5.get(0).getSubList().get(1).getSubList().add(new UITestObject("b3", "3", "3", "3"));
        collection5.get(0).getSubList().get(1).getSubList().add(new UITestObject("b3", "3", "3", "3"));
        collection5.get(0).getSubList().get(1).getSubList().add(new UITestObject("b3", "3", "3", "3"));
        collection5.add(new UITestObject("b", "b", "b", "b"));
        collection5.get(1).getSubList().add(new UITestObject("a", "b", "C", "D"));
        collection5.get(1).getSubList().get(0).getSubList().add(new UITestObject("a23", "3", "3", "3"));
        collection5.get(1).getSubList().get(0).getSubList().add(new UITestObject("a23", "3", "3", "3"));
        collection5.get(1).getSubList().add(new UITestObject("a", "s", "D", "F"));
        collection5.get(1).getSubList().get(1).getSubList().add(new UITestObject("b23", "3", "3", "3"));
        collection5.get(1).getSubList().get(1).getSubList().add(new UITestObject("b23", "3", "3", "3"));

        inactivatableCollection.add(new UIInactivatableTestObject("A", "100", "200", "300", true));
        inactivatableCollection.add(new UIInactivatableTestObject("B", "100", "200", "300", true));
        inactivatableCollection.add(new UIInactivatableTestObject("b3", "3", "3", "3", false));
        inactivatableCollection.add(new UIInactivatableTestObject("a", "b", "C", "D", true));
        inactivatableCollection.add(new UIInactivatableTestObject("W", "X", "Y", "Z", false));
        inactivatableCollection.add(new UIInactivatableTestObject("a", "s", "d", "f", true));
        inactivatableCollection.add(new UIInactivatableTestObject("Fall", "2002", "AAA123", "3", false));

        inactivatableCollection2.addAll(inactivatableCollection);

        groupedCollection1.add(new UITestObject("A", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("A", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("A", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("A", "103", "200", "300"));
        groupedCollection1.add(new UITestObject("A", "104", "200", "300"));

        groupedCollection1.add(new UITestObject("B", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("B", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("B", "102", "200", "300"));

        groupedCollection1.add(new UITestObject("C", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("C", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("C", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("C", "103", "200", "300"));

        groupedCollection1.add(new UITestObject("D", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "103", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "103", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "103", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "103", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "103", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "100", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "101", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "102", "200", "300"));
        groupedCollection1.add(new UITestObject("D", "103", "200", "300"));

        groupedCollection2.addAll(groupedCollection1);
        groupedCollection3.addAll(groupedCollection1);

        doubleGroupedCollection.add(new UITestObject("Fall", "2001", "AAA123", "2"));
        doubleGroupedCollection.add(new UITestObject("Fall", "2001", "BBB123", "3"));
        doubleGroupedCollection.add(new UITestObject("Fall", "2001", "CCC123", "4"));
        doubleGroupedCollection.add(new UITestObject("Fall", "2001", "DDD123", "3"));

        doubleGroupedCollection.add(new UITestObject("Fall", "2002", "AAA123", "3"));
        doubleGroupedCollection.add(new UITestObject("Fall", "2002", "BBB123", "2"));
        doubleGroupedCollection.add(new UITestObject("Fall", "2002", "CCC123", "3"));

        doubleGroupedCollection.add(new UITestObject("Fall", "2003", "AAA123", "3"));
        doubleGroupedCollection.add(new UITestObject("Fall", "2003", "CCC123", "3"));

        doubleGroupedCollection.add(new UITestObject("Spring", "2001", "AAA123", "3"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2001", "BBB123", "3"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2001", "CCC123", "3"));

        doubleGroupedCollection.add(new UITestObject("Spring", "2002", "AAA123", "4"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2002", "BBB123", "4"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2002", "CCC123", "2"));

        doubleGroupedCollection.add(new UITestObject("Spring", "2003", "AAA123", "4"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2003", "BBB123", "3"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2003", "CCC123", "3"));
        doubleGroupedCollection.add(new UITestObject("Spring", "2003", "DDD123", "2"));
    }

    /**
     * Theme by name (id) currently used for the component library view
     *
     * @return
     */
    public String getThemeName() {
        return themeName;
    }

    /**
     * @param themeName
     */
    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    /**
     * Backing property for the large example dropdown since on is required.  Stores dropdown selection
     *
     * @return
     */
    public String getExampleShown() {
        return exampleShown;
    }

    /**
     * Large example selection
     *
     * @param exampleShown
     */
    public void setExampleShown(String exampleShown) {
        this.exampleShown = exampleShown;
    }

    /**
     * Index of the current example, used to reselect between submit actions
     *
     * @return
     */
    public String getCurrentExampleIndex() {
        return currentExampleIndex;
    }

    /**
     * Index of the current example
     *
     * @param currentExampleIndex
     */
    public void setCurrentExampleIndex(String currentExampleIndex) {
        this.currentExampleIndex = currentExampleIndex;
    }

    /**
     * Below are basic getters and setters for this data object - no javadoc needed *
     */

    public String getDataField1() {
        return dataField1;
    }

    public void setDataField1(String dataField1) {
        this.dataField1 = dataField1;
    }

    public String getDataField2() {
        return dataField2;
    }

    public void setDataField2(String dataField2) {
        this.dataField2 = dataField2;
    }

    public String getDataField3() {
        return dataField3;
    }

    public void setDataField3(String dataField3) {
        this.dataField3 = dataField3;
    }

    public String getDataField4() {
        return dataField4;
    }

    public void setDataField4(String dataField4) {
        this.dataField4 = dataField4;
    }

    public void setDataField5(String dataField5) {
        this.dataField5 = dataField5;
    }

    public String getDataField5() {
        return dataField5;
    }

    public String getDataField6() {
        return dataField6;
    }

    public void setDataField6(String dataField6) {
        this.dataField6 = dataField6;
    }

    public String getDataField7() {
        return dataField7;
    }

    public void setDataField7(String dataField7) {
        this.dataField7 = dataField7;
    }

    public String getDataField8() {
        return dataField8;
    }

    public void setDataField8(String dataField8) {
        this.dataField8 = dataField8;
    }

    public String getDataField9() {
        return dataField9;
    }

    public void setDataField9(String dataField9) {
        this.dataField9 = dataField9;
    }

    public String getInputField1() {
        return inputField1;
    }

    public void setInputField1(String inputField1) {
        this.inputField1 = inputField1;
    }

    public String getInputField2() {
        return inputField2;
    }

    public void setInputField2(String inputField2) {
        this.inputField2 = inputField2;
    }

    public String getInputField3() {

        return inputField3;
    }

    public void setInputField3(String inputField3) {
        this.inputField3 = inputField3;
    }

    public String getInputField4() {
        return inputField4;
    }

    public void setInputField4(String inputField4) {
        this.inputField4 = inputField4;
    }

    public String getInputField5() {
        return inputField5;
    }

    public void setInputField5(String inputField5) {
        this.inputField5 = inputField5;
    }

    public String getInputField6() {
        return inputField6;
    }

    public void setInputField6(String inputField6) {
        this.inputField6 = inputField6;
    }

    public String getInputField7() {
        return inputField7;
    }

    public void setInputField7(String inputField7) {
        this.inputField7 = inputField7;
    }

    public String getInputField8() {
        return inputField8;
    }

    public void setInputField8(String inputField8) {
        this.inputField8 = inputField8;
    }

    public String getInputField9() {
        return inputField9;
    }

    public void setInputField9(String inputField9) {
        this.inputField9 = inputField9;
    }

    public String getInputField10() {
        return inputField10;
    }

    public void setInputField10(String inputField10) {
        this.inputField10 = inputField10;
    }

    public String getInputField11() {
        return inputField11;
    }

    public void setInputField11(String inputField11) {
        this.inputField11 = inputField11;
    }

    public String getInputField12() {
        return inputField12;
    }

    public void setInputField12(String inputField12) {
        this.inputField12 = inputField12;
    }

    public String getInputField13() {
        return inputField13;
    }

    public void setInputField13(String inputField13) {
        this.inputField13 = inputField13;
    }

    public String getInputField14() {
        return inputField14;
    }

    public void setInputField14(String inputField14) {
        this.inputField14 = inputField14;
    }

    public String getInputField15() {
        return inputField15;
    }

    public void setInputField15(String inputField15) {
        this.inputField15 = inputField15;
    }

    public String getInputField16() {
        return inputField16;
    }

    public void setInputField16(String inputField16) {
        this.inputField16 = inputField16;
    }

    public String getInputField17() {
        return inputField17;
    }

    public void setInputField17(String inputField17) {
        this.inputField17 = inputField17;
    }

    public String getInputField18() {
        return inputField18;
    }

    public void setInputField18(String inputField18) {
        this.inputField18 = inputField18;
    }

    public String getInputField19() {
        return inputField19;
    }

    public void setInputField19(String inputField19) {
        this.inputField19 = inputField19;
    }

    public String getInputField20() {
        return inputField20;
    }

    public void setInputField20(String inputField20) {
        this.inputField20 = inputField20;
    }

    public String getInputField21() {
        return inputField21;
    }

    public void setInputField21(String inputField21) {
        this.inputField21 = inputField21;
    }

    public String getInputField22() {
        return inputField22;
    }

    public void setInputField22(String inputField22) {
        this.inputField22 = inputField22;
    }

    public String getInputField23() {
        return inputField23;
    }

    public void setInputField23(String inputField23) {
        this.inputField23 = inputField23;
    }

    public String getInputField24() {
        return inputField24;
    }

    public void setInputField24(String inputField24) {
        this.inputField24 = inputField24;
    }

    public String getInputField25() {
        return inputField25;
    }

    public void setInputField25(String inputField25) {
        this.inputField25 = inputField25;
    }

    public String getMessageField1() {
        return messageField1;
    }

    public void setMessageField1(String messageField1) {
        this.messageField1 = messageField1;
    }

    public String getTestPersonId() {
        return testPersonId;
    }

    public void setTestPersonId(String testPersonId) {
        this.testPersonId = testPersonId;
    }

    public Person getTestPerson() {
        if ((testPerson == null) || !StringUtils.equals(testPerson.getPrincipalId(), getTestPersonId())) {
            testPerson = KimApiServiceLocator.getPersonService().getPerson(getTestPersonId());

            if (testPerson == null) {
                try {
                    testPerson = KimApiServiceLocator.getPersonService().getPersonImplementationClass().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return testPerson;
    }

    public void setTestPerson(Person testPerson) {
        this.testPerson = testPerson;
    }

    public String getTestGroupId() {
        return testGroupId;
    }

    public void setTestGroupId(String testGroupId) {
        this.testGroupId = testGroupId;
    }

    public String getTestPrincipalId1() {
        return testPrincipalId1;
    }

    public void setTestPrincipalId1(String testPrincipalId1) {
        this.testPrincipalId1 = testPrincipalId1;
    }

    public String getTestPrincipalId2() {
        return testPrincipalId2;
    }

    public void setTestPrincipalId2(String testPrincipalId2) {
        this.testPrincipalId2 = testPrincipalId2;
    }

    public String getTestPrincipalId3() {
        return testPrincipalId3;
    }

    public void setTestPrincipalId3(String testPrincipalId3) {
        this.testPrincipalId3 = testPrincipalId3;
    }

    public String getTestPrincipalId4() {
        return testPrincipalId4;
    }

    public void setTestPrincipalId4(String testPrincipalId4) {
        this.testPrincipalId4 = testPrincipalId4;
    }

    public String getTestPrincipalName1() {
        return testPrincipalName1;
    }

    public void setTestPrincipalName1(String testPrincipalName1) {
        this.testPrincipalName1 = testPrincipalName1;
    }

    public String getTestPrincipalName2() {
        return testPrincipalName2;
    }

    public void setTestPrincipalName2(String testPrincipalName2) {
        this.testPrincipalName2 = testPrincipalName2;
    }

    public String getTestPrincipalName3() {
        return testPrincipalName3;
    }

    public void setTestPrincipalName3(String testPrincipalName3) {
        this.testPrincipalName3 = testPrincipalName3;
    }

    public String getTestPrincipalName4() {
        return testPrincipalName4;
    }

    public void setTestPrincipalName4(String testPrincipalName4) {
        this.testPrincipalName4 = testPrincipalName4;
    }

    public String getTestGroupId1() {
        return testGroupId1;
    }

    public void setTestGroupId1(String testGroupId1) {
        this.testGroupId1 = testGroupId1;
    }

    public String getTestGroupId2() {
        return testGroupId2;
    }

    public void setTestGroupId2(String testGroupId2) {
        this.testGroupId2 = testGroupId2;
    }

    public String getTestGroupId3() {
        return testGroupId3;
    }

    public void setTestGroupId3(String testGroupId3) {
        this.testGroupId3 = testGroupId3;
    }

    public String getTestGroupId4() {
        return testGroupId4;
    }

    public void setTestGroupId4(String testGroupId4) {
        this.testGroupId4 = testGroupId4;
    }

    public String getTestGroupId5() {
        return testGroupId5;
    }

    public void setTestGroupId5(String testGroupId5) {
        this.testGroupId5 = testGroupId5;
    }

    public String getTestGroupNamespaceCode1() {
        return testGroupNamespaceCode1;
    }

    public void setTestGroupNamespaceCode1(String testGroupNamespaceCode1) {
        this.testGroupNamespaceCode1 = testGroupNamespaceCode1;
    }

    public String getTestGroupNamespaceCode2() {
        return testGroupNamespaceCode2;
    }

    public void setTestGroupNamespaceCode2(String testGroupNamespaceCode2) {
        this.testGroupNamespaceCode2 = testGroupNamespaceCode2;
    }

    public String getTestGroupNamespaceCode3() {
        return testGroupNamespaceCode3;
    }

    public void setTestGroupNamespaceCode3(String testGroupNamespaceCode3) {
        this.testGroupNamespaceCode3 = testGroupNamespaceCode3;
    }

    public String getTestGroupNamespaceCode4() {
        return testGroupNamespaceCode4;
    }

    public void setTestGroupNamespaceCode4(String testGroupNamespaceCode4) {
        this.testGroupNamespaceCode4 = testGroupNamespaceCode4;
    }

    public String getTestGroupNamespaceCode5() {
        return testGroupNamespaceCode5;
    }

    public void setTestGroupNamespaceCode5(String testGroupNamespaceCode5) {
        this.testGroupNamespaceCode5 = testGroupNamespaceCode5;
    }

    public String getTestGroupName1() {
        return testGroupName1;
    }

    public void setTestGroupName1(String testGroupName1) {
        this.testGroupName1 = testGroupName1;
    }

    public String getTestGroupName2() {
        return testGroupName2;
    }

    public void setTestGroupName2(String testGroupName2) {
        this.testGroupName2 = testGroupName2;
    }

    public String getTestGroupName3() {
        return testGroupName3;
    }

    public void setTestGroupName3(String testGroupName3) {
        this.testGroupName3 = testGroupName3;
    }

    public String getTestGroupName4() {
        return testGroupName4;
    }

    public void setTestGroupName4(String testGroupName4) {
        this.testGroupName4 = testGroupName4;
    }

    public String getTestGroupName5() {
        return testGroupName5;
    }

    public void setTestGroupName5(String testGroupName5) {
        this.testGroupName5 = testGroupName5;
    }

    public String getTestRoleId1() {
        return testRoleId1;
    }

    public void setTestRoleId1(String testRoleId1) {
        this.testRoleId1 = testRoleId1;
    }

    public String getTestRoleId2() {
        return testRoleId2;
    }

    public void setTestRoleId2(String testRoleId2) {
        this.testRoleId2 = testRoleId2;
    }

    public String getTestRoleId3() {
        return testRoleId3;
    }

    public void setTestRoleId3(String testRoleId3) {
        this.testRoleId3 = testRoleId3;
    }

    public String getTestRoleId4() {
        return testRoleId4;
    }

    public void setTestRoleId4(String testRoleId4) {
        this.testRoleId4 = testRoleId4;
    }

    public String getTestRoleId5() {
        return testRoleId5;
    }

    public void setTestRoleId5(String testRoleId5) {
        this.testRoleId5 = testRoleId5;
    }

    public String getTestRoleNamespaceCode1() {
        return testRoleNamespaceCode1;
    }

    public void setTestRoleNamespaceCode1(String testRoleNamespaceCode1) {
        this.testRoleNamespaceCode1 = testRoleNamespaceCode1;
    }

    public String getTestRoleNamespaceCode2() {
        return testRoleNamespaceCode2;
    }

    public void setTestRoleNamespaceCode2(String testRoleNamespaceCode2) {
        this.testRoleNamespaceCode2 = testRoleNamespaceCode2;
    }

    public String getTestRoleNamespaceCode3() {
        return testRoleNamespaceCode3;
    }

    public void setTestRoleNamespaceCode3(String testRoleNamespaceCode3) {
        this.testRoleNamespaceCode3 = testRoleNamespaceCode3;
    }

    public String getTestRoleNamespaceCode4() {
        return testRoleNamespaceCode4;
    }

    public void setTestRoleNamespaceCode4(String testRoleNamespaceCode4) {
        this.testRoleNamespaceCode4 = testRoleNamespaceCode4;
    }

    public String getTestRoleNamespaceCode5() {
        return testRoleNamespaceCode5;
    }

    public void setTestRoleNamespaceCode5(String testRoleNamespaceCode5) {
        this.testRoleNamespaceCode5 = testRoleNamespaceCode5;
    }

    public String getTestRoleName1() {
        return testRoleName1;
    }

    public void setTestRoleName1(String testRoleName1) {
        this.testRoleName1 = testRoleName1;
    }

    public String getTestRoleName2() {
        return testRoleName2;
    }

    public void setTestRoleName2(String testRoleName2) {
        this.testRoleName2 = testRoleName2;
    }

    public String getTestRoleName3() {
        return testRoleName3;
    }

    public void setTestRoleName3(String testRoleName3) {
        this.testRoleName3 = testRoleName3;
    }

    public String getTestRoleName4() {
        return testRoleName4;
    }

    public void setTestRoleName4(String testRoleName4) {
        this.testRoleName4 = testRoleName4;
    }

    public String getTestRoleName5() {
        return testRoleName5;
    }

    public void setTestRoleName5(String testRoleName5) {
        this.testRoleName5 = testRoleName5;
    }

    public List<UITestObject> getEmptyCollection() {
        return emptyCollection;
    }

    public void setEmptyCollection(List<UITestObject> emptyCollection) {
        this.emptyCollection = emptyCollection;
    }

    public UITestObject getNestedObject1() {
        return nestedObject1;
    }

    public void setNestedObject1(UITestObject nestedObject1) {
        this.nestedObject1 = nestedObject1;
    }

    public List<UITestObject> getCollection1() {
        return collection1;
    }

    public void setCollection1(List<UITestObject> collection1) {
        this.collection1 = collection1;
    }

    public List<UITestObject> getCollection1_2() {
        return collection1_2;
    }

    public void setCollection1_2(List<UITestObject> collection1_2) {
        this.collection1_2 = collection1_2;
    }

    public List<UITestObject> getCollection1_3() {
        return collection1_3;
    }

    public void setCollection1_3(List<UITestObject> collection1_3) {
        this.collection1_3 = collection1_3;
    }

    public List<UITestObject> getCollection1_4() {
        return collection1_4;
    }

    public void setCollection1_4(List<UITestObject> collection1_4) {
        this.collection1_4 = collection1_4;
    }

    public List<UITestObject> getCollection1_5() {
        return collection1_5;
    }

    public void setCollection1_5(List<UITestObject> collection1_5) {
        this.collection1_5 = collection1_5;
    }

    public List<UITestObject> getCollection1_6() {
        return collection1_6;
    }

    public void setCollection1_6(List<UITestObject> collection1_6) {
        this.collection1_6 = collection1_6;
    }

    public List<UITestObject> getCollection1_7() {
        return collection1_7;
    }

    public void setCollection1_7(List<UITestObject> collection1_7) {
        this.collection1_7 = collection1_7;
    }

    public List<UITestObject> getCollection1_8() {
        return collection1_8;
    }

    public void setCollection1_8(List<UITestObject> collection1_8) {
        this.collection1_8 = collection1_8;
    }

    public List<UITestObject> getCollection1_9() {
        return collection1_9;
    }

    public void setCollection1_9(List<UITestObject> collection1_9) {
        this.collection1_9 = collection1_9;
    }

    public List<UITestObject> getCollection1_10() {
        return collection1_10;
    }

    public void setCollection1_10(List<UITestObject> collection1_10) {
        this.collection1_10 = collection1_10;
    }

    public List<UITestObject> getCollection2() {
        return collection2;
    }

    public void setCollection2(List<UITestObject> collection2) {
        this.collection2 = collection2;
    }

    public List<UITestObject> getCollection3() {
        return collection3;
    }

    public void setCollection3(List<UITestObject> collection3) {
        this.collection3 = collection3;
    }

    public List<UITestObject> getCollection4() {
        return collection4;
    }

    public void setCollection4(List<UITestObject> collection4) {
        this.collection4 = collection4;
    }

    public List<UITestObject> getCollection5() {
        return collection5;
    }

    public void setCollection5(List<UITestObject> collection5) {
        this.collection5 = collection5;
    }

    public List<UIInactivatableTestObject> getInactivatableCollection() {
        return inactivatableCollection;
    }

    public void setInactivatableCollection(List<UIInactivatableTestObject> inactivatableCollection) {
        this.inactivatableCollection = inactivatableCollection;
    }

    public List<UIInactivatableTestObject> getInactivatableCollection2() {
        return inactivatableCollection2;
    }

    public void setInactivatableCollection2(List<UIInactivatableTestObject> inactivatableCollection2) {
        this.inactivatableCollection2 = inactivatableCollection2;
    }

    public List<UITestObject> getGroupedCollection1() {
        return groupedCollection1;
    }

    public void setGroupedCollection1(List<UITestObject> groupedCollection1) {
        this.groupedCollection1 = groupedCollection1;
    }

    public List<UITestObject> getGroupedCollection2() {
        return groupedCollection2;
    }

    public void setGroupedCollection2(List<UITestObject> groupedCollection2) {
        this.groupedCollection2 = groupedCollection2;
    }

    public List<UITestObject> getGroupedCollection3() {
        return groupedCollection3;
    }

    public void setGroupedCollection3(List<UITestObject> groupedCollection3) {
        this.groupedCollection3 = groupedCollection3;
    }

    public List<UITestObject> getDoubleGroupedCollection() {
        return doubleGroupedCollection;
    }

    public void setDoubleGroupedCollection(List<UITestObject> doubleGroupedCollection) {
        this.doubleGroupedCollection = doubleGroupedCollection;
    }

    public String getFakeTotal() {
        return fakeTotal;
    }

    public void setFakeTotal(String fakeTotal) {
        this.fakeTotal = fakeTotal;
    }

    public String getRichMessageField() {
        return richMessageField;
    }

    public void setRichMessageField(String richMessageField) {
        this.richMessageField = richMessageField;
    }

    public List<String> getCheckboxesField1() {
        return checkboxesField1;
    }

    public void setCheckboxesField1(List<String> checkboxesField1) {
        this.checkboxesField1 = checkboxesField1;
    }

    public List<String> getCheckboxesField2() {
        return checkboxesField2;
    }

    public void setCheckboxesField2(List<String> checkboxesField2) {
        this.checkboxesField2 = checkboxesField2;
    }

    public List<String> getCheckboxesField3() {
        return checkboxesField3;
    }

    public void setCheckboxesField3(List<String> checkboxesField3) {
        this.checkboxesField3 = checkboxesField3;
    }

    public List<String> getCheckboxesField4() {
        return checkboxesField4;
    }

    public void setCheckboxesField4(List<String> checkboxesField4) {
        this.checkboxesField4 = checkboxesField4;
    }

    public List<String> getMultiSelectField1() {
        return multiSelectField1;
    }

    public void setMultiSelectField1(List<String> multiSelectField1) {
        this.multiSelectField1 = multiSelectField1;
    }

    public List<String> getMultiSelectField2() {
        return multiSelectField2;
    }

    public void setMultiSelectField2(List<String> multiSelectField2) {
        this.multiSelectField2 = multiSelectField2;
    }

    public boolean isBooleanField1() {
        return booleanField1;
    }

    public void setBooleanField1(boolean booleanField1) {
        this.booleanField1 = booleanField1;
    }

    public boolean isBooleanField2() {
        return booleanField2;
    }

    public void setBooleanField2(boolean booleanField2) {
        this.booleanField2 = booleanField2;
    }

    public boolean isBooleanField3() {
        return booleanField3;
    }

    public void setBooleanField3(boolean booleanField3) {
        this.booleanField3 = booleanField3;
    }

    public void setSessionTimeoutInterval(int interval) {
        this.sessionTimeoutInterval = interval;
    }

    public List<UITestObject> getMediumCollection1() {
        return mediumCollection1;
    }

    public void setMediumCollection1(List<UITestObject> mediumCollection1) {
        this.mediumCollection1 = mediumCollection1;
    }

    public List<UITestObject> getMediumCollection2() {
        return mediumCollection2;
    }

    public void setMediumCollection2(List<UITestObject> mediumCollection2) {
        this.mediumCollection2 = mediumCollection2;
    }

    public List<FileMeta> getFiles() {
        return files;
    }


    public void setFiles(List<FileMeta> files) {
        this.files = files;
    }

    public List<FileWithDetails> getFiles2() {
        return files2;
    }

    public void setFiles2(List<FileWithDetails> files2) {
        this.files2 = files2;
    }

    public MultipartFile getUploadOne() {
        return uploadOne;
    }

    public void setUploadOne(MultipartFile uploadOne) {
        this.uploadOne = uploadOne;
    }

    public MultipartFile getUploadTwo() {
        return uploadTwo;
    }

    public void setUploadTwo(MultipartFile uploadTwo) {
        this.uploadTwo = uploadTwo;
    }

    public String getBackdoorId() {
        return backdoorId;
    }

    public void setBackdoorId(String backdoorId) {
        this.backdoorId = backdoorId;
    }

    public List<FileMetaBlob> getFiles(String propertyPath) {
        System.out.println("KradLabsForm, get files => " + propertyPath);
        List<FileMetaBlob> returnObjects = new ArrayList<FileMetaBlob>();

        // fake data for testing
        FileMetaBlob fakeFile = new FileMetaBlob();
        fakeFile.setName(propertyPath + "_fakeName1.png");
        fakeFile.setSize(5000000L);
        fakeFile.setDateUploaded(new Date());

        returnObjects.add(fakeFile);

        fakeFile = new FileMetaBlob();
        fakeFile.setName(propertyPath + "_fakeName2.png");
        fakeFile.setSize(5000000L);
        fakeFile.setDateUploaded(new Date());
        returnObjects.add(fakeFile);

        return returnObjects;
    }
}