/*
 * Copyright 2006-2015 The Kuali Foundation
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

package org.kuali.rice.kew.actionitem.oracle.queue;

/**
 * This is the information that can be found on the actn_item_changed_mq message queue
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionItemChangedPayload {
    private Character actnType;
    private String actnItemId;

    /**
     * @return the Action Item Id
     */
    public String getActnItemId() {
        return actnItemId;
    }

    /**
     * @param actnItemId the Action Item Id to set
     */
    public void setActnItemId(String actnItemId) {
        this.actnItemId = actnItemId;
    }

    /**
     * This is the type of change that has happened on the KREW_ACTN_ITM_T table.
     * The value of actnType will be one of the following: I (Insert), U (Update), or D(Delete)
     *
     * @return the actnType
     */
    public Character getActnType() {
        return actnType;
    }

    /**
     * @param actnType the actnType to set
     */
    public void setActnType(Character actnType) {
        this.actnType = actnType;
    }
}
