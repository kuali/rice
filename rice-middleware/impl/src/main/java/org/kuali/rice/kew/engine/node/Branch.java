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
package org.kuali.rice.kew.engine.node;

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a branch in the routing path of the document.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KREW_RTE_BRCH_T")
public class Branch implements Serializable {

	private static final long serialVersionUID = 7164561979112939112L;
	
	@Id
	@GeneratedValue(generator = "KREW_RTE_NODE_S")
    @PortableSequenceGenerator(name = "KREW_RTE_NODE_S")
	@Column(name = "RTE_BRCH_ID")
	private String branchId;

	@ManyToOne
	@JoinColumn(name = "PARNT_ID")
	private Branch parentBranch;

	@Column(name = "NM")
	private String name;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "branch", orphanRemoval = true)
	private List<BranchState> branchState = new ArrayList<BranchState>();

    @ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "INIT_RTE_NODE_INSTN_ID")
	private RouteNodeInstance initialNode;

    @ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "SPLT_RTE_NODE_INSTN_ID")
	private RouteNodeInstance splitNode;

    @ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "JOIN_RTE_NODE_INSTN_ID")
	private RouteNodeInstance joinNode;
		
	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;	
	}
	
    public RouteNodeInstance getSplitNode() {
        return splitNode;
    }
    public void setSplitNode(RouteNodeInstance splitNode) {
        this.splitNode = splitNode;
    }
    public RouteNodeInstance getInitialNode() {
		return initialNode;
	}
	public void setInitialNode(RouteNodeInstance activeNode) {
		this.initialNode = activeNode;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public RouteNodeInstance getJoinNode() {
		return joinNode;
	}
	public void setJoinNode(RouteNodeInstance joinNode) {
		this.joinNode = joinNode;
	}
	public Branch getParentBranch() {
		return parentBranch;
	}
	public void setParentBranch(Branch parentBranch) {
		this.parentBranch = parentBranch;
	}
    public BranchState getBranchState(String key) {
        for (Iterator iter = branchState.iterator(); iter.hasNext();) {
            BranchState branchState = (BranchState) iter.next();
            if (branchState.getKey().equals(key)) {
                return branchState;
            }
        }
        return null;
    }
    public void addBranchState(BranchState state) {
        branchState.add(state);
        state.setBranch(this);
    }
    public List<BranchState> getBranchState() {
        return branchState;
    }
    public void setBranchState(List<BranchState> branchState) {
        this.branchState.clear();
        this.branchState.addAll(branchState);
    }
    
    public BranchState getDocBranchState(int index){
    	while (branchState.size() <= index) {
            branchState.add(new BranchState());
        }
        return branchState.get(index);
   
    }
    
	public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public Branch deepCopy(Map<Object, Object> visited) {
        if (visited.containsKey(this)) {
            return (Branch)visited.get(this);
        }
        Branch copy = new Branch();
        visited.put(this, copy);
        copy.branchId = branchId;
        copy.name = name;
        copy.lockVerNbr = lockVerNbr;
        if (parentBranch != null) {
            copy.parentBranch = parentBranch.deepCopy(visited);
        }
        if (branchState != null) {
            List<BranchState> copies = new ArrayList<BranchState>();
            for (BranchState state : branchState) {
                copies.add(state.deepCopy(visited));
            }
            copy.branchState = copies;
        }
        if (initialNode != null) {
            copy.initialNode = initialNode.deepCopy(visited);
        }
        if (splitNode != null) {
            copy.splitNode = splitNode.deepCopy(visited);
        }
        if (joinNode != null) {
            copy.joinNode = joinNode.deepCopy(visited);
        }
        return copy;
    }


    public String toString() {
        return "[Branch: branchId=" + branchId +
                      ", parentBranch=" + (parentBranch == null ? "null" : parentBranch.getBranchId()) +
                      "]";
    }

}

