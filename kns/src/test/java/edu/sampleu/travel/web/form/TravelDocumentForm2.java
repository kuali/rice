/*
 * Copyright 2007 The Kuali Foundation.
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
package edu.sampleu.travel.web.form;

import org.kuali.core.web.struts.form.KualiDocumentFormBase;

import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.document.TravelDocument2;

public class TravelDocumentForm2 extends KualiDocumentFormBase {

    private TravelAccount travelAccount = new TravelAccount();

    public TravelDocumentForm2() {
        super();
        this.setDocument(new TravelDocument2());
    }

    public TravelAccount getTravelAccount() {
        return travelAccount;
    }

    public void setTravelAccount(TravelAccount travelAccount) {
        this.travelAccount = travelAccount;
    }
    
}
