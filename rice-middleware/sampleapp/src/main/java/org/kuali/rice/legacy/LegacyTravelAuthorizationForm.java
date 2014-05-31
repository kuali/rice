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
package org.kuali.rice.legacy;

import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LegacyTravelAuthorizationForm extends KualiTransactionalDocumentFormBase {

    private LegacyTravelAuthorization travelAuthorization = new LegacyTravelAuthorization();

    public LegacyTravelAuthorizationForm() {
        super();

        setDocument(new LegacyTravelAuthorization());

        setFormatterType("document.tripBegin", DateFormatter.class);
        setFormatterType("document.tripEnd", DateFormatter.class);
    }

    public LegacyTravelAuthorization getTravelAccount() {
        return travelAuthorization;
    }

    public void setTravelAccount(LegacyTravelAuthorization travelAuthorization) {
        this.travelAuthorization = travelAuthorization;
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "LegacyTravelAuthorizationKns";
    }

    @Override
    public String getDocTypeName() {
        return getDefaultDocumentTypeName();
    }
}
