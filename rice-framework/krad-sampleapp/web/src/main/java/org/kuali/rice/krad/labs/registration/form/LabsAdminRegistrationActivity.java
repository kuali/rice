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
package org.kuali.rice.krad.labs.registration.form;

import java.io.Serializable;

/**
 * Class for KS Admin Registration Lab prototype
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsAdminRegistrationActivity implements Serializable {
    private static final long serialVersionUID = -58207877197440410L;

    private String type;
    private String dateTime;
    private String instructor;
    private String room;

    public LabsAdminRegistrationActivity(String type, String dateTime, String instructor, String room) {
        this.type = type;
        this.dateTime = dateTime;
        this.instructor = instructor;
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
