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
package edu.sampleu.travel.krad.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Form for Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UILayoutTestForm extends UifFormBase {
    private static final long serialVersionUID = -7525378097732916418L;

    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;
    private String field9;
    private String field10;
    private String field11;
    private String field12;
    private String field13;
    private String field14;
    private String field15;
    private String field16;
    private String field17;
    private String field18;
    private String field19;
    private String field20;
    private String field21;
    private String field22;
    private String field23;
    private String field24;
    private String field25;
    private String field26;
    private String field27;
    private String field28;
    private String field29;
    private String field30;
    private String field31;
    private String field32;
    private String field33;
    private String field34;
    private String field35;
    private String field36;
    private String field37;
    private String field38;
    private String field39;
    private String field40;
    private String field41;
    private String field42;
    private String field43;
    private String field44;
    private String field45;
    private String field46;
    private String field47 = "";
    private String field48;
    private String field49;
    private String field50;
    private String field51;
    private String field52;
    private String field53;
    private String field54;
    private String field55;
    private String field56;
    private String field57;
    private String field58;
    private String field59;
    private String field60;
    private String field61;
    private String field62;
    private String field63;
    private String field64;
    private String field65;
    private String field66;
    private String field67;
    private String field68;
    private String field69;
    private String field70;
    private String field71;
    private String field72;
    private String field73;
    private String field74;
    private String field75;
    private String field76;
    private String field77;
    private String field78;
    private String field79;
    private String field80;
    private String field81;
    
    private String gField1;
    private String gField2;
    private String gField3;
    
    private String mField1 = "SecretInfo555";
    private String mField2 = "SecretInfo111";
    private String mField3 = "SecretInfo222";
    
    private List<UITestListObject> list1 = new ArrayList<UITestListObject>();
    private List<UITestListObject> list2 = new ArrayList<UITestListObject>();
    private List<UITestListObject> list3 = new ArrayList<UITestListObject>();
    private List<UITestListObject> list4 = new ArrayList<UITestListObject>();
    private List<UITestListObject> list5 = new ArrayList<UITestListObject>();
    private List<UITestListObject> list6 = new ArrayList<UITestListObject>();
    
    private Tree<String, String> tree1 = new Tree<String,String>();

    private boolean bField1;
    private boolean bField2;
    private boolean bField3;

    public UILayoutTestForm() {
        super();
        list1.add(new UITestListObject("A","B","C","D"));
        list1.add(new UITestListObject("1","2","3","4"));
        list1.add(new UITestListObject("W","X","Y","Z"));
        list1.add(new UITestListObject("a","b","c","d"));
        list1.add(new UITestListObject("a","s","d","f"));
        
        list2.add(new UITestListObject("A","B","C","D"));
        list2.add(new UITestListObject("1","2","3","4"));
        list2.add(new UITestListObject("W","X","Y","Z"));
        list2.add(new UITestListObject("a","b","c","d"));
        list2.add(new UITestListObject("a","s","d","f"));
        
        list3.add(new UITestListObject("A","B","C","D"));
        list3.add(new UITestListObject("1","2","3","4"));
        list3.add(new UITestListObject("W","X","Y","Z"));
        
        list4.add(new UITestListObject("A","B","C","D"));
        list4.get(0).getSubList().add(new UITestListObject("A","B","C","D"));
        list4.get(0).getSubList().add(new UITestListObject("1","2","3","4"));
        list4.get(0).getSubList().add(new UITestListObject("W","X","Y","Z"));
        list4.add(new UITestListObject("1","2","3","4"));
        list4.get(1).getSubList().add(new UITestListObject("a","b","C","D"));
        list4.get(1).getSubList().add(new UITestListObject("a","s","D","F"));
        
        list5.add(new UITestListObject("a","a","a","a"));
        list5.get(0).getSubList().add(new UITestListObject("A","B","C","D"));
        list5.get(0).getSubList().add(new UITestListObject("1","2","3","4"));
        list5.add(new UITestListObject("b","b","b","b"));
        list5.get(1).getSubList().add(new UITestListObject("a","b","C","D"));
        list5.get(1).getSubList().add(new UITestListObject("a","s","D","F"));
        
        list6.add(new UITestListObject("A","B","C","D"));
        list6.add(new UITestListObject("1","2","3","4"));
        list6.add(new UITestListObject("W","X","Y","Z"));
        list6.add(new UITestListObject("a","b","c","d"));
        list6.add(new UITestListObject("a","s","d","f"));
        
        
        
        Node<String,String> item1 = new Node<String,String>("Item 1", "Item 1");
        item1.addChild(new Node<String,String>("SubItem A", "SubItem A"));
        item1.addChild(new Node<String,String>("SubItem B", "SubItem B"));
        
        Node<String,String> item2 = new Node<String,String>("Item 2", "Item 2");
        item2.addChild(new Node<String,String>("SubItem A", "SubItem A"));
        Node<String,String> sub2B = new Node<String,String>("SubItem B", "SubItem B");
        sub2B.addChild(new Node<String,String>("Item B-1", "Item B-1"));
        sub2B.addChild(new Node<String,String>("Item B-2", "Item B-2"));
        sub2B.addChild(new Node<String,String>("Item B-3", "Item B-3"));
        item2.addChild(sub2B);
        item2.addChild(new Node<String,String>("SubItem C", "SubItem C"));
        
        Node<String,String> item3 = new Node<String,String>("Item 3", "Item 3");
        item3.addChild(new Node<String,String>("SubItem A", "SubItem A"));
        item3.addChild(new Node<String,String>("SubItem B", "SubItem B"));
        item3.addChild(new Node<String,String>("SubItem C", "SubItem C"));
        item3.addChild(new Node<String,String>("SubItem D", "SubItem D"));
        
        Node<String,String> root = new Node<String,String>("Root", "Root");
        root.addChild(item1);
        root.addChild(item2);
        root.addChild(item3);
        
        tree1.setRootElement(root);
    }

    @Override
    public void postBind(HttpServletRequest request) {
        super.postBind(request);
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
     * @return the field5
     */
    public String getField5() {
        return this.field5;
    }

    /**
     * @param field5 the field5 to set
     */
    public void setField5(String field5) {
        this.field5 = field5;
    }

    /**
     * @return the field6
     */
    public String getField6() {
        return this.field6;
    }

    /**
     * @param field6 the field6 to set
     */
    public void setField6(String field6) {
        this.field6 = field6;
    }

    /**
     * @return the field7
     */
    public String getField7() {
        return this.field7;
    }

    /**
     * @param field7 the field7 to set
     */
    public void setField7(String field7) {
        this.field7 = field7;
    }

    /**
     * @return the field8
     */
    public String getField8() {
        return this.field8;
    }

    /**
     * @param field8 the field8 to set
     */
    public void setField8(String field8) {
        this.field8 = field8;
    }

    /**
     * @return the field9
     */
    public String getField9() {
        return this.field9;
    }

    /**
     * @param field9 the field9 to set
     */
    public void setField9(String field9) {
        this.field9 = field9;
    }

    /**
     * @return the field10
     */
    public String getField10() {
        return this.field10;
    }

    /**
     * @param field10 the field10 to set
     */
    public void setField10(String field10) {
        this.field10 = field10;
    }

    /**
     * @return the field11
     */
    public String getField11() {
        return this.field11;
    }

    /**
     * @param field11 the field11 to set
     */
    public void setField11(String field11) {
        this.field11 = field11;
    }

    /**
     * @return the field12
     */
    public String getField12() {
        return this.field12;
    }

    /**
     * @param field12 the field12 to set
     */
    public void setField12(String field12) {
        this.field12 = field12;
    }

    /**
     * @return the field13
     */
    public String getField13() {
        return this.field13;
    }

    /**
     * @param field13 the field13 to set
     */
    public void setField13(String field13) {
        this.field13 = field13;
    }

    /**
     * @return the field14
     */
    public String getField14() {
        return this.field14;
    }

    /**
     * @param field14 the field14 to set
     */
    public void setField14(String field14) {
        this.field14 = field14;
    }

    /**
     * @return the field15
     */
    public String getField15() {
        return this.field15;
    }

    /**
     * @param field15 the field15 to set
     */
    public void setField15(String field15) {
        this.field15 = field15;
    }

    /**
     * @return the field16
     */
    public String getField16() {
        return this.field16;
    }

    /**
     * @param field16 the field16 to set
     */
    public void setField16(String field16) {
        this.field16 = field16;
    }

    /**
     * @return the field17
     */
    public String getField17() {
        return this.field17;
    }

    /**
     * @param field17 the field17 to set
     */
    public void setField17(String field17) {
        this.field17 = field17;
    }

    /**
     * @return the field18
     */
    public String getField18() {
        return this.field18;
    }

    /**
     * @param field18 the field18 to set
     */
    public void setField18(String field18) {
        this.field18 = field18;
    }

    /**
     * @return the field19
     */
    public String getField19() {
        return this.field19;
    }

    /**
     * @param field19 the field19 to set
     */
    public void setField19(String field19) {
        this.field19 = field19;
    }

    /**
     * @return the field20
     */
    public String getField20() {
        return this.field20;
    }

    /**
     * @param field20 the field20 to set
     */
    public void setField20(String field20) {
        this.field20 = field20;
    }

    /**
     * @return the field21
     */
    public String getField21() {
        return this.field21;
    }

    /**
     * @param field21 the field21 to set
     */
    public void setField21(String field21) {
        this.field21 = field21;
    }

    /**
     * @return the field22
     */
    public String getField22() {
        return this.field22;
    }

    /**
     * @param field22 the field22 to set
     */
    public void setField22(String field22) {
        this.field22 = field22;
    }

    /**
     * @return the field23
     */
    public String getField23() {
        return this.field23;
    }

    /**
     * @param field23 the field23 to set
     */
    public void setField23(String field23) {
        this.field23 = field23;
    }

    /**
     * @return the field24
     */
    public String getField24() {
        return this.field24;
    }

    /**
     * @param field24 the field24 to set
     */
    public void setField24(String field24) {
        this.field24 = field24;
    }

    /**
     * @return the field25
     */
    public String getField25() {
        return this.field25;
    }

    /**
     * @param field25 the field25 to set
     */
    public void setField25(String field25) {
        this.field25 = field25;
    }

    /**
     * @return the field26
     */
    public String getField26() {
        return this.field26;
    }

    /**
     * @param field26 the field26 to set
     */
    public void setField26(String field26) {
        this.field26 = field26;
    }

    /**
     * @return the field27
     */
    public String getField27() {
        return this.field27;
    }

    /**
     * @param field27 the field27 to set
     */
    public void setField27(String field27) {
        this.field27 = field27;
    }

    /**
     * @return the field28
     */
    public String getField28() {
        return this.field28;
    }

    /**
     * @param field28 the field28 to set
     */
    public void setField28(String field28) {
        this.field28 = field28;
    }

    /**
     * @return the field29
     */
    public String getField29() {
        return this.field29;
    }

    /**
     * @param field29 the field29 to set
     */
    public void setField29(String field29) {
        this.field29 = field29;
    }

    /**
     * @return the field30
     */
    public String getField30() {
        return this.field30;
    }

    /**
     * @param field30 the field30 to set
     */
    public void setField30(String field30) {
        this.field30 = field30;
    }

    /**
     * @return the field31
     */
    public String getField31() {
        return this.field31;
    }

    /**
     * @param field31 the field31 to set
     */
    public void setField31(String field31) {
        this.field31 = field31;
    }

    /**
     * @return the field32
     */
    public String getField32() {
        return this.field32;
    }

    /**
     * @param field32 the field32 to set
     */
    public void setField32(String field32) {
        this.field32 = field32;
    }

    /**
     * @return the field33
     */
    public String getField33() {
        return this.field33;
    }

    /**
     * @param field33 the field33 to set
     */
    public void setField33(String field33) {
        this.field33 = field33;
    }

    /**
     * @return the field34
     */
    public String getField34() {
        return this.field34;
    }
    
    

    /**
     * @return the field35
     */
    public String getField35() {
        return this.field35;
    }

    /**
     * @param field35 the field35 to set
     */
    public void setField35(String field35) {
        this.field35 = field35;
    }

    /**
     * @return the field36
     */
    public String getField36() {
        return this.field36;
    }

    /**
     * @param field36 the field36 to set
     */
    public void setField36(String field36) {
        this.field36 = field36;
    }

    /**
     * @return the field37
     */
    public String getField37() {
        return this.field37;
    }

    /**
     * @param field37 the field37 to set
     */
    public void setField37(String field37) {
        this.field37 = field37;
    }

    /**
     * @return the field38
     */
    public String getField38() {
        return this.field38;
    }

    /**
     * @param field38 the field38 to set
     */
    public void setField38(String field38) {
        this.field38 = field38;
    }

    /**
     * @return the field39
     */
    public String getField39() {
        return this.field39;
    }

    /**
     * @param field39 the field39 to set
     */
    public void setField39(String field39) {
        this.field39 = field39;
    }

    /**
     * @return the field40
     */
    public String getField40() {
        return this.field40;
    }

    /**
     * @param field40 the field40 to set
     */
    public void setField40(String field40) {
        this.field40 = field40;
    }

    /**
     * @return the field41
     */
    public String getField41() {
        return this.field41;
    }

    /**
     * @param field41 the field41 to set
     */
    public void setField41(String field41) {
        this.field41 = field41;
    }

    /**
     * @return the field42
     */
    public String getField42() {
        return this.field42;
    }

    /**
     * @param field42 the field42 to set
     */
    public void setField42(String field42) {
        this.field42 = field42;
    }

    /**
     * @return the field43
     */
    public String getField43() {
        return this.field43;
    }

    /**
     * @param field43 the field43 to set
     */
    public void setField43(String field43) {
        this.field43 = field43;
    }

    /**
     * @return the field44
     */
    public String getField44() {
        return this.field44;
    }

    /**
     * @param field44 the field44 to set
     */
    public void setField44(String field44) {
        this.field44 = field44;
    }

    /**
     * @return the field45
     */
    public String getField45() {
        return this.field45;
    }

    /**
     * @param field45 the field45 to set
     */
    public void setField45(String field45) {
        this.field45 = field45;
    }

    /**
     * @return the field46
     */
    public String getField46() {
        return this.field46;
    }

    /**
     * @param field46 the field46 to set
     */
    public void setField46(String field46) {
        this.field46 = field46;
    }

    /**
     * @return the field47
     */
    public String getField47() {
        return this.field47;
    }

    /**
     * @param field47 the field47 to set
     */
    public void setField47(String field47) {
        this.field47 = field47;
    }

    /**
     * @return the field48
     */
    public String getField48() {
        return this.field48;
    }

    /**
     * @param field48 the field48 to set
     */
    public void setField48(String field48) {
        this.field48 = field48;
    }

    /**
     * @return the field49
     */
    public String getField49() {
        return this.field49;
    }

    /**
     * @param field49 the field49 to set
     */
    public void setField49(String field49) {
        this.field49 = field49;
    }

    /**
     * @return the field50
     */
    public String getField50() {
        return this.field50;
    }

    /**
     * @param field50 the field50 to set
     */
    public void setField50(String field50) {
        this.field50 = field50;
    }
    
    

    /**
     * @return the field51
     */
    public String getField51() {
        return this.field51;
    }

    /**
     * @param field51 the field51 to set
     */
    public void setField51(String field51) {
        this.field51 = field51;
    }

    /**
     * @return the field52
     */
    public String getField52() {
        return this.field52;
    }

    /**
     * @param field52 the field52 to set
     */
    public void setField52(String field52) {
        this.field52 = field52;
    }

    /**
     * @return the field53
     */
    public String getField53() {
        return this.field53;
    }

    /**
     * @param field53 the field53 to set
     */
    public void setField53(String field53) {
        this.field53 = field53;
    }

    /**
     * @return the field54
     */
    public String getField54() {
        return this.field54;
    }

    /**
     * @param field54 the field54 to set
     */
    public void setField54(String field54) {
        this.field54 = field54;
    }

    /**
     * @return the field55
     */
    public String getField55() {
        return this.field55;
    }

    /**
     * @param field55 the field55 to set
     */
    public void setField55(String field55) {
        this.field55 = field55;
    }

    /**
     * @return the field56
     */
    public String getField56() {
        return this.field56;
    }

    /**
     * @param field56 the field56 to set
     */
    public void setField56(String field56) {
        this.field56 = field56;
    }

    /**
     * @return the field57
     */
    public String getField57() {
        return this.field57;
    }

    /**
     * @param field57 the field57 to set
     */
    public void setField57(String field57) {
        this.field57 = field57;
    }

    /**
     * @return the field58
     */
    public String getField58() {
        return this.field58;
    }

    /**
     * @param field58 the field58 to set
     */
    public void setField58(String field58) {
        this.field58 = field58;
    }

    /**
     * @return the field59
     */
    public String getField59() {
        return this.field59;
    }

    /**
     * @param field59 the field59 to set
     */
    public void setField59(String field59) {
        this.field59 = field59;
    }

    /**
     * @return the field60
     */
    public String getField60() {
        return this.field60;
    }

    /**
     * @param field60 the field60 to set
     */
    public void setField60(String field60) {
        this.field60 = field60;
    }

    /**
     * @return the field61
     */
    public String getField61() {
        return this.field61;
    }

    /**
     * @param field61 the field61 to set
     */
    public void setField61(String field61) {
        this.field61 = field61;
    }

    /**
     * @return the field62
     */
    public String getField62() {
        return this.field62;
    }

    /**
     * @param field62 the field62 to set
     */
    public void setField62(String field62) {
        this.field62 = field62;
    }

    /**
     * @return the field63
     */
    public String getField63() {
        return this.field63;
    }

    /**
     * @param field63 the field63 to set
     */
    public void setField63(String field63) {
        this.field63 = field63;
    }

    /**
     * @return the field64
     */
    public String getField64() {
        return this.field64;
    }

    /**
     * @param field64 the field64 to set
     */
    public void setField64(String field64) {
        this.field64 = field64;
    }

    /**
     * @return the field65
     */
    public String getField65() {
        return this.field65;
    }

    /**
     * @param field65 the field65 to set
     */
    public void setField65(String field65) {
        this.field65 = field65;
    }

    /**
     * @param field34 the field34 to set
     */
    public void setField34(String field34) {
        this.field34 = field34;
    }

    /**
     * @return the bField1
     */
    public boolean isbField1() {
        return this.bField1;
    }

    /**
     * @param bField1 the bField1 to set
     */
    public void setbField1(boolean bField1) {
        this.bField1 = bField1;
    }

    /**
     * @return the bField2
     */
    public boolean isbField2() {
        return this.bField2;
    }

    /**
     * @param bField2 the bField2 to set
     */
    public void setbField2(boolean bField2) {
        this.bField2 = bField2;
    }

    /**
     * @return the bField3
     */
    public boolean isbField3() {
        return this.bField3;
    }

    /**
     * @param bField3 the bField3 to set
     */
    public void setbField3(boolean bField3) {
        this.bField3 = bField3;
    }

    /**
     * @return the list1
     */
    public List<UITestListObject> getList1() {
        return this.list1;
    }

    /**
     * @param list1 the list1 to set
     */
    public void setList1(List<UITestListObject> list1) {
        this.list1 = list1;
    }

    /**
     * @return the list2
     */
    public List<UITestListObject> getList2() {
        return this.list2;
    }

    /**
     * @param list2 the list2 to set
     */
    public void setList2(List<UITestListObject> list2) {
        this.list2 = list2;
    }

    /**
     * @return the list3
     */
    public List<UITestListObject> getList3() {
        return this.list3;
    }

    /**
     * @param list3 the list3 to set
     */
    public void setList3(List<UITestListObject> list3) {
        this.list3 = list3;
    }

    /**
     * @return the list4
     */
    public List<UITestListObject> getList4() {
        return this.list4;
    }

    /**
     * @param list4 the list4 to set
     */
    public void setList4(List<UITestListObject> list4) {
        this.list4 = list4;
    }

    /**
     * @return the gField1
     */
    public String getgField1() {
        return this.gField1;
    }

    /**
     * @param gField1 the gField1 to set
     */
    public void setgField1(String gField1) {
        this.gField1 = gField1;
    }

    /**
     * @return the gField2
     */
    public String getgField2() {
        return this.gField2;
    }

    /**
     * @param gField2 the gField2 to set
     */
    public void setgField2(String gField2) {
        this.gField2 = gField2;
    }

    /**
     * @return the gField3
     */
    public String getgField3() {
        return this.gField3;
    }

    /**
     * @param gField3 the gField3 to set
     */
    public void setgField3(String gField3) {
        this.gField3 = gField3;
    }

    /**
     * @return the field66
     */
    public String getField66() {
        return this.field66;
    }

    /**
     * @param field66 the field66 to set
     */
    public void setField66(String field66) {
        this.field66 = field66;
    }

    /**
     * @return the field67
     */
    public String getField67() {
        return this.field67;
    }

    /**
     * @param field67 the field67 to set
     */
    public void setField67(String field67) {
        this.field67 = field67;
    }

    /**
     * @return the field68
     */
    public String getField68() {
        return this.field68;
    }

    /**
     * @param field68 the field68 to set
     */
    public void setField68(String field68) {
        this.field68 = field68;
    }

    /**
     * @return the field69
     */
    public String getField69() {
        return this.field69;
    }

    /**
     * @param field69 the field69 to set
     */
    public void setField69(String field69) {
        this.field69 = field69;
    }

    /**
     * @return the field70
     */
    public String getField70() {
        return this.field70;
    }

    /**
     * @param field70 the field70 to set
     */
    public void setField70(String field70) {
        this.field70 = field70;
    }

    /**
     * @return the field71
     */
    public String getField71() {
        return this.field71;
    }

    /**
     * @param field71 the field71 to set
     */
    public void setField71(String field71) {
        this.field71 = field71;
    }

    /**
     * @return the field72
     */
    public String getField72() {
        return this.field72;
    }

    /**
     * @param field72 the field72 to set
     */
    public void setField72(String field72) {
        this.field72 = field72;
    }

    /**
     * @return the field73
     */
    public String getField73() {
        return this.field73;
    }

    /**
     * @param field73 the field73 to set
     */
    public void setField73(String field73) {
        this.field73 = field73;
    }

    /**
     * @return the field74
     */
    public String getField74() {
        return this.field74;
    }

    /**
     * @param field74 the field74 to set
     */
    public void setField74(String field74) {
        this.field74 = field74;
    }

    /**
     * @return the field75
     */
    public String getField75() {
        return this.field75;
    }

    /**
     * @param field75 the field75 to set
     */
    public void setField75(String field75) {
        this.field75 = field75;
    }

    /**
     * @return the field76
     */
    public String getField76() {
        return this.field76;
    }

    /**
     * @param field76 the field76 to set
     */
    public void setField76(String field76) {
        this.field76 = field76;
    }

    /**
     * @return the field77
     */
    public String getField77() {
        return this.field77;
    }

    /**
     * @param field77 the field77 to set
     */
    public void setField77(String field77) {
        this.field77 = field77;
    }

    /**
     * @return the field78
     */
    public String getField78() {
        return this.field78;
    }

    /**
     * @param field78 the field78 to set
     */
    public void setField78(String field78) {
        this.field78 = field78;
    }

    /**
     * @return the field79
     */
    public String getField79() {
        return this.field79;
    }

    /**
     * @param field79 the field79 to set
     */
    public void setField79(String field79) {
        this.field79 = field79;
    }

    /**
     * @return the field80
     */
    public String getField80() {
        return this.field80;
    }

    /**
     * @param field80 the field80 to set
     */
    public void setField80(String field80) {
        this.field80 = field80;
    }

    /**
     * @return the field81
     */
    public String getField81() {
        return this.field81;
    }

    /**
     * @param field81 the field81 to set
     */
    public void setField81(String field81) {
        this.field81 = field81;
    }

    /**
     * @return the mField1
     */
    public String getmField1() {
        return this.mField1;
    }

    /**
     * @param mField1 the mField1 to set
     */
    public void setmField1(String mField1) {
        this.mField1 = mField1;
    }

    /**
     * @return the mField2
     */
    public String getmField2() {
        return this.mField2;
    }

    /**
     * @param mField2 the mField2 to set
     */
    public void setmField2(String mField2) {
        this.mField2 = mField2;
    }

    /**
     * @return the mField3
     */
    public String getmField3() {
        return this.mField3;
    }

    /**
     * @param mField3 the mField3 to set
     */
    public void setmField3(String mField3) {
        this.mField3 = mField3;
    }

    /**
     * @return the list5
     */
    public List<UITestListObject> getList5() {
        return this.list5;
    }

    /**
     * @param list5 the list5 to set
     */
    public void setList5(List<UITestListObject> list5) {
        this.list5 = list5;
    }

    /**
     * @return the list6
     */
    public List<UITestListObject> getList6() {
        return this.list6;
    }

    /**
     * @param list6 the list6 to set
     */
    public void setList6(List<UITestListObject> list6) {
        this.list6 = list6;
    }

    /**
     * @return the tree1
     */
    public Tree<String, String> getTree1() {
        return this.tree1;
    }

    /**
     * @param tree1 the tree1 to set
     */
    public void setTree1(Tree<String, String> tree1) {
        this.tree1 = tree1;
    }
    
    
}