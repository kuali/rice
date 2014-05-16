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
package org.kuali.rice.krad.labs.collections;

import org.kuali.rice.krad.demo.travel.dataobject.TravelAccount;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nigupta on 5/15/2014.
 */
public class TravelAccountCollectionsForm extends UifFormBase {

    private List<TravelAccount> travelAccountList = new ArrayList<TravelAccount>();

    private String accountNumberField;
    private String accountNameField;
    private String accountTypeCode;

    public TravelAccountCollectionsForm() {
        super();
    }

    public List<TravelAccount> getTravelAccountList() {
        return travelAccountList;
    }

    public void setTravelAccountList( List<TravelAccount> list ) {
        travelAccountList = list;
    }


    public String getAccountNameField() {
        return accountNameField;
    }

    public void setAccountNameField(String accountNameField) {
        this.accountNameField = accountNameField;
    }

    public String getAccountNumberField() {
        return accountNumberField;
    }

    public void setAccountNumberField(String accountNumberField) {
        this.accountNumberField = accountNumberField;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    public void setAccountTypeCode( String code ) {
        accountTypeCode = code;
    }
}
