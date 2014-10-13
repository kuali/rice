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
package org.kuali.rice.krad.web.form;

import java.util.Date;
import java.util.List;

/**
 * Form to test a paged view.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestPagedViewForm extends UifFormBase {

    private String textField1;
    private String textField2;
    private String textField3;

    private Date dateField1;
    private Date dataField2;
    private Date dataField3;

    private List<String> checkboxField;
    private String radioField;
    private List<String> multiSelectField;

    public String getTextField1() {
        return textField1;
    }

    public void setTextField1(String textField1) {
        this.textField1 = textField1;
    }

    public String getTextField2() {
        return textField2;
    }

    public void setTextField2(String textField2) {
        this.textField2 = textField2;
    }

    public String getTextField3() {
        return textField3;
    }

    public void setTextField3(String textField3) {
        this.textField3 = textField3;
    }

    public Date getDateField1() {
        return dateField1;
    }

    public void setDateField1(Date dateField1) {
        this.dateField1 = dateField1;
    }

    public Date getDataField2() {
        return dataField2;
    }

    public void setDataField2(Date dataField2) {
        this.dataField2 = dataField2;
    }

    public Date getDataField3() {
        return dataField3;
    }

    public void setDataField3(Date dataField3) {
        this.dataField3 = dataField3;
    }

    public List<String> getCheckboxField() {
        return checkboxField;
    }

    public void setCheckboxField(List<String> checkboxField) {
        this.checkboxField = checkboxField;
    }

    public String getRadioField() {
        return radioField;
    }

    public void setRadioField(String radioField) {
        this.radioField = radioField;
    }

    public List<String> getMultiSelectField() {
        return multiSelectField;
    }

    public void setMultiSelectField(List<String> multiSelectField) {
        this.multiSelectField = multiSelectField;
    }

}