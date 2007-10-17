/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.engine.node;

/**
 * A piece of state on a {@link Branch} stored as a key-value pair of Strings.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BranchState extends State {
    /**
     * Prefix under which "variables" are stored in the branch state table, to distinguish
     * them from non-variable key/value pairs.
     */
    public static final String VARIABLE_PREFIX = "var::";

    private static final long serialVersionUID = -7642477013444817952L;

    private Branch branch;
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

    public Long getBranchStateId() {
        return getStateId();
    }

    public void setBranchStateId(Long branchStateId) {
        setStateId(branchStateId);
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public String toString() {
        return "[BranchState: stateId=" + getStateId() + ", branch=" + branch + ", key=" + key + ", value=" + value + "]"; 
    }
}
