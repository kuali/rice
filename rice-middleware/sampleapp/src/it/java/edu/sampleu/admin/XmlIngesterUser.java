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
package edu.sampleu.admin;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XmlIngesterUser {

    public XmlIngesterUser(String id) {
        principalId = id;
        principalName = id;
        emplId = id;
        givenName = id.substring(0, 1);
        lastName = id.substring(1, id.length());
        emailAddress = id + "@bogus.email.com";
    }

    String principalId;
    String principalName;
    String emplId;
    String givenName;
    String lastName;
    String emailAddress;

    public String getPrincipalId() {
        return principalId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getEmplId() {
        return emplId;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
