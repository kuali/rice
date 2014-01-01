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
package org.kuali.rice.krad.sampleapp_2_4_M2.labs;

import javax.persistence.Transient;

public class LabsUserControl {

    @Transient
    private String myPrincipalName;
    @Transient
    private String myPersonName;

    public String getMyPrincipalName() {
        return myPrincipalName;
    }

    public void setMyPrincipalName(String myPrincipalName) {
        this.myPrincipalName = myPrincipalName;
    }

    public String getMyPersonName() {
        return myPersonName;
    }

    public void setMyPersonName(String myPersonName) {
        this.myPersonName = myPersonName;
    }
}
