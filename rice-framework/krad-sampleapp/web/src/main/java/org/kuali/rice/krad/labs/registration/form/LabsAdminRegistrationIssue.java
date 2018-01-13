/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.labs.registration.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for KS Admin Registration Lab prototype
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsAdminRegistrationIssue {
    private List<String> messages = new ArrayList<String>();
    private LabsAdminRegistrationCourse course;

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public LabsAdminRegistrationCourse getCourse() {
        return course;
    }

    public void setCourse(LabsAdminRegistrationCourse course) {
        this.course = course;
    }
}
