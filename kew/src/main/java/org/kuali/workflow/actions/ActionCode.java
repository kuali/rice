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
package org.kuali.workflow.actions;

import edu.iu.uis.eden.EdenConstants;

/**
 * This is a description of what this class does - arh14 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public enum ActionCode {
    ACKNOWLEDGE(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD),
    ADHOC(EdenConstants.ACTION_TAKEN_ADHOC_CD),
    REVOKEADHOC(EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD),
    APPROVE(EdenConstants.ACTION_TAKEN_APPROVED_CD),
    BLANKETAPPROVE(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD),
    CANCEL(EdenConstants.ACTION_TAKEN_CANCELED_CD),
    COMPLETE(EdenConstants.ACTION_TAKEN_COMPLETED_CD),
    ROUTE(EdenConstants.ACTION_TAKEN_ROUTED_CD),
    DISAPPROVE(EdenConstants.ACTION_TAKEN_DENIED_CD),
    CLEARFYI(EdenConstants.ACTION_TAKEN_FYI_CD),
    LOG(EdenConstants.ACTION_TAKEN_LOG_DOCUMENT_ACTION_CD),
    MOVE(EdenConstants.ACTION_TAKEN_MOVE_CD),
    TAKE(EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY_CD),
    RELEASE(EdenConstants.ACTION_TAKEN_RELEASE_WORKGROUP_AUTHORITY_CD),
    JUMP(EdenConstants.ACTION_TAKEN_RETURNED_TO_PREVIOUS_CD),
    SAVE(EdenConstants.ACTION_TAKEN_SAVED_CD),
    //SUARACKNOWLEDGE(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED_CD),
    SUARAPPROVE(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_APPROVED_CD),
    //SUARCOMPLETE(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_COMPLETED_CD),
    //SUARFYI(EdenConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI_CD),
    SUAPPROVE(EdenConstants.ACTION_TAKEN_SU_APPROVED_CD),
    SUCANCEL(EdenConstants.ACTION_TAKEN_SU_CANCELED_CD),
    SUDISAPPROVE(EdenConstants.ACTION_TAKEN_SU_DISAPPROVED_CD),
    SUJUMP(EdenConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD),
    SUNODEAPPROVE(EdenConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD);
    
    private String code;
    
    private ActionCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
