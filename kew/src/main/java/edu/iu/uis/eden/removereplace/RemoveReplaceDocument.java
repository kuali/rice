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
package edu.iu.uis.eden.removereplace;

import java.io.Serializable;
import java.util.List;

/**
 * A Remove/Replace document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemoveReplaceDocument implements Serializable {

    private static final long serialVersionUID = 6671410167992149054L;

    public static final String REMOVE_OPERATION = "M";
    public static final String REPLACE_OPERATION = "P";

    private Long documentId;
    private String userWorkflowId;
    private String replacementUserWorkflowId;
    private String operation;
    private List<WorkgroupTarget> workgroupTargets;
    private List<RuleTarget> ruleTargets;
    private Integer lockVerNbr;

    public Long getDocumentId() {
        return this.documentId;
    }
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
    public String getOperation() {
        return this.operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public List<RuleTarget> getRuleTargets() {
        return this.ruleTargets;
    }
    public void setRuleTargets(List<RuleTarget> ruleTargets) {
        this.ruleTargets = ruleTargets;
    }
    public String getUserWorkflowId() {
        return this.userWorkflowId;
    }
    public void setUserWorkflowId(String userWorkflowId) {
        this.userWorkflowId = userWorkflowId;
    }
    public String getReplacementUserWorkflowId() {
        return this.replacementUserWorkflowId;
    }
    public void setReplacementUserWorkflowId(String replacementUserWorkflowId) {
        this.replacementUserWorkflowId = replacementUserWorkflowId;
    }
    public List<WorkgroupTarget> getWorkgroupTargets() {
        return this.workgroupTargets;
    }
    public void setWorkgroupTargets(List<WorkgroupTarget> workgroupTargets) {
        this.workgroupTargets = workgroupTargets;
    }
    public Integer getLockVerNbr() {
        return this.lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

}
