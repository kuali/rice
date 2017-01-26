/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.engine.node;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Map;

/**
 * A piece of state on a {@link Branch} stored as a key-value pair of Strings.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RTE_BRCH_ST_T")
@AttributeOverrides({
@AttributeOverride(name="stateId", column=@Column(name="RTE_BRCH_ST_ID")),
@AttributeOverride(name="versionNumber", column=@Column(name="VER_NBR", insertable = false, updatable = false))
})
public class BranchState extends State {
    /**
     * Prefix under which "variables" are stored in the branch state table, to distinguish
     * them from non-variable key/value pairs.
     */
    public static final String VARIABLE_PREFIX = "var::";

    private static final long serialVersionUID = -7642477013444817952L;

    @ManyToOne
	@JoinColumn(name="RTE_BRCH_ID", nullable = false)
	private Branch branch;

    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

    public BranchState() {}
    
    public BranchState(String key, String value) {
    	super(key, value);
    }
    
    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getBranchStateId() {
        return getStateId();
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public BranchState deepCopy(Map<Object, Object> visited) {
        if (visited.containsKey(this)) {
            return (BranchState)visited.get(this);
        }
        BranchState copy = new BranchState(getKey(), getValue());
        visited.put(this, copy);
        copy.stateId = stateId;
        copy.lockVerNbr = lockVerNbr;
        if (branch != null) {
            copy.branch = branch.deepCopy(visited);
        }
        return copy;
    }

}

