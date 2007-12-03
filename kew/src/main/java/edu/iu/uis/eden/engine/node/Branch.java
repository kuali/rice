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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a branch in the routing path of the document.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Branch implements Serializable {

	private static final long serialVersionUID = 7164561979112939112L;
	
	private Long branchId;
	private Branch parentBranch;
	private String name;
	private List<BranchState> branchState = new ArrayList<BranchState>();
//	  apache lazy list commented out due to not being serializable
//    private List branchState = ListUtils.lazyList(new ArrayList(),
//            new Factory() {
//				public Object create() {
//					return new BranchState();
//				}
//			});
    private RouteNodeInstance initialNode;
    private RouteNodeInstance splitNode;
	private RouteNodeInstance joinNode;
	
	private Long initialNodeId;
	
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
	public Long getBranchId() {
		return branchId;
	}
	public void setBranchId(Long branchId) {
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
        this.branchState = branchState;
    }
    
    public BranchState getDocBranchState(int index){
    	while (branchState.size() <= index) {
            branchState.add(new BranchState());
        }
        return (BranchState) branchState.get(index);
   
    }
    
	public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public String toString() {
        return "[Branch: branchId=" + branchId +
                      ", parentBranch=" + (parentBranch == null ? "null" : parentBranch.getBranchId()) +
                      "]";
    }
}
