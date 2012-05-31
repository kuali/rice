/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.removereplace;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@IdClass(org.kuali.rice.kew.removereplace.RuleTargetId.class)
@Entity
@Table(name="KREW_RMV_RPLC_RULE_T")
public class RuleTarget {

    @Id
	@Column(name="DOC_HDR_ID")
	private Long documentId;
    @Id
	@Column(name="RULE_ID")
	private Long ruleId;
    
    // Added for JPA uni-directional one-to-many (not yet supported by JPA)
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name="DOC_HDR_ID")
    private RemoveReplaceDocument removeReplaceDocument;

    public Long getDocumentId() {
        return this.documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(Long id) {
        this.ruleId = id;
    }

}

