/**
 * Copyright 2005-2016 The Kuali Foundation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.kew.api.document.node.RouteNodeInstanceState;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;


/**
 * Represents a materialized instance of a {@link RouteNode} definition on a {@link DocumentRouteHeaderValue}.  Node instances
 * are generated by the engine using the {@link RouteNode} as a prototype and connected as a 
 * Directed Acyclic Graph.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RTE_NODE_INSTN_T")
//@Sequence(name="KREW_RTE_NODE_S",property="routeNodeInstanceId")
@NamedQueries({
	@NamedQuery(name="RouteNodeInstance.FindByRouteNodeInstanceId",query="select r from RouteNodeInstance r where r.routeNodeInstanceId = :routeNodeInstanceId"),
	@NamedQuery(name="RouteNodeInstance.FindActiveNodeInstances",query="select r from RouteNodeInstance r where r.documentId = :documentId and r.active = true"),
	@NamedQuery(name="RouteNodeInstance.FindTerminalNodeInstances",query="select r from RouteNodeInstance r where r.documentId = :documentId and r.active = false and r.complete = true"),
	@NamedQuery(name="RouteNodeInstance.FindInitialNodeInstances",query="select d.initialRouteNodeInstances from DocumentRouteHeaderValue d where d.documentId = :documentId"),
	@NamedQuery(name="RouteNodeInstance.FindProcessNodeInstances", query="select r from RouteNodeInstance r where r.process.routeNodeInstanceId = :processId"),
	@NamedQuery(name="RouteNodeInstance.FindRouteNodeInstances", query="select r from RouteNodeInstance r where r.documentId = :documentId")
})
public class RouteNodeInstance implements Serializable {
    
	private static final long serialVersionUID = 7183670062805580420L;
	
	@Id
	@GeneratedValue(generator="KREW_RTE_NODE_S")
	@GenericGenerator(name="KREW_RTE_NODE_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_RTE_NODE_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="RTE_NODE_INSTN_ID")
	private String routeNodeInstanceId;
    @Column(name="DOC_HDR_ID")
	private String documentId;
    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="BRCH_ID")
	private Branch branch;
    @OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RTE_NODE_ID")
    private RouteNode routeNode;
    @Column(name="ACTV_IND")
    private boolean active = false;
    @Column(name="CMPLT_IND")
    private boolean complete = false;
    @Column(name="INIT_IND")
    private boolean initial = true;
    @OneToOne(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="PROC_RTE_NODE_INSTN_ID")
	private RouteNodeInstance process;
    
    @ManyToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "KREW_RTE_NODE_INSTN_LNK_T", joinColumns = @JoinColumn(name = "FROM_RTE_NODE_INSTN_ID"), inverseJoinColumns = @JoinColumn(name = "TO_RTE_NODE_INSTN_ID"))
    @Fetch(value = FetchMode.SELECT)
    private List<RouteNodeInstance> nextNodeInstances = new ArrayList<RouteNodeInstance>();
    
    @ManyToMany(fetch=FetchType.EAGER, mappedBy="nextNodeInstances")
    @Fetch(value = FetchMode.SELECT)
    //@JoinTable(name = "KREW_RTE_NODE_INSTN_LNK_T", joinColumns = @JoinColumn(name = "TO_RTE_NODE_INSTN_ID"), inverseJoinColumns = @JoinColumn(name = "FROM_RTE_NODE_INSTN_ID"))
    private List<RouteNodeInstance> previousNodeInstances = new ArrayList<RouteNodeInstance>();

    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, mappedBy="nodeInstance", orphanRemoval=true)    
    @Fetch(value = FetchMode.SELECT)
    private List<NodeState> state = new ArrayList<NodeState>();
    	
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    
    private List<DocumentRouteHeaderValue> initialDocumentRouteHeaderValues = new ArrayList<DocumentRouteHeaderValue>();

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    public Branch getBranch() {
        return branch;
    }
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    public RouteNode getRouteNode() {
        return routeNode;
    }
    public void setRouteNode(RouteNode node) {
        this.routeNode = node;
    }
    public String getRouteNodeInstanceId() {
        return routeNodeInstanceId;
    }
    public void setRouteNodeInstanceId(String routeNodeInstanceId) {
        this.routeNodeInstanceId = routeNodeInstanceId;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public List<RouteNodeInstance> getNextNodeInstances() {
        return nextNodeInstances;
    }
    public RouteNodeInstance getNextNodeInstance(int index) {
    	while (getNextNodeInstances().size() <= index) {
    		nextNodeInstances.add(new RouteNodeInstance());
    	}
    	return (RouteNodeInstance) getNextNodeInstances().get(index);
    }
    public void setNextNodeInstances(List<RouteNodeInstance> nextNodeInstances) {
        this.nextNodeInstances = nextNodeInstances;
    }
    public List<RouteNodeInstance> getPreviousNodeInstances() {
        return previousNodeInstances;
    }
    public RouteNodeInstance getPreviousNodeInstance(int index) {
    	while (previousNodeInstances.size() <= index) {
    		previousNodeInstances.add(new RouteNodeInstance());
    	}
    	return (RouteNodeInstance) getPreviousNodeInstances().get(index);
    }
    public void setPreviousNodeInstances(List<RouteNodeInstance> previousNodeInstances) {
        this.previousNodeInstances = previousNodeInstances;
    }
    public boolean isInitial() {
        return initial;
    }
    public void setInitial(boolean initial) {
        this.initial = initial;
    }
    public List<NodeState> getState() {
        return state;
    }
    public void setState(List<NodeState> state) {
        this.state.clear();
    	this.state.addAll(state);
        //this.state = state;
    }
    public RouteNodeInstance getProcess() {
		return process;
	}
	public void setProcess(RouteNodeInstance process) {
		this.process = process;
	}
	public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    
    public NodeState getNodeState(String key) {
        for (Iterator iter = getState().iterator(); iter.hasNext();) {
            NodeState nodeState = (NodeState) iter.next();
            if (nodeState.getKey().equals(key)) {
                return nodeState;
            }
        }
        return null;
    }
    
    public void addNodeState(NodeState state) {
        this.state.add(state);
        state.setNodeInstance(this);
    }
    
    public void removeNodeState(String key) {
        for (Iterator iter = getState().iterator(); iter.hasNext();) {
            NodeState nodeState = (NodeState) iter.next();
            if (nodeState.getKey().equals(key)) {
                iter.remove();
                break;
            }
        }
    }
    
    public void addNextNodeInstance(RouteNodeInstance nextNodeInstance) {
        nextNodeInstances.add(nextNodeInstance);
        nextNodeInstance.getPreviousNodeInstances().add(this);
    }
    
    public void removeNextNodeInstance(RouteNodeInstance nextNodeInstance) {
        nextNodeInstances.remove(nextNodeInstance);
        nextNodeInstance.getPreviousNodeInstances().remove(this);
    }
    
    public void clearNextNodeInstances() {
        for (Iterator iterator = nextNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance nextNodeInstance = (RouteNodeInstance) iterator.next();
            iterator.remove();
            nextNodeInstance.getPreviousNodeInstances().remove(this);
        }
    }
    
    public String getName() {
        return (getRouteNode() == null ? null : getRouteNode().getRouteNodeName());
    }
    
    public boolean isInProcess() {
        return getProcess() != null;
    }
    
    public DocumentType getDocumentType() {
        return KEWServiceLocator.getDocumentTypeService().findByDocumentId(getDocumentId());
    }
    
    /*
     * methods used to display route node instances' data on documentoperation.jsp
     */
    
    public NodeState getNodeStateByIndex(int index){
    	while (state.size() <= index) {
            state.add(new NodeState());
        }
        return (NodeState) getState().get(index);
    }   

    public void populateState(List<NodeState> state) {
        this.state.addAll(state);
     }

    public List<DocumentRouteHeaderValue> getInitialDocumentRouteHeaderValues() {
        return initialDocumentRouteHeaderValues;
    }

    public void setInitialDocumentRouteHeaderValues(List<DocumentRouteHeaderValue> initialDocumentRouteHeaderValues) {
        this.initialDocumentRouteHeaderValues = initialDocumentRouteHeaderValues;
    }
    
    public String toString() {
        return new ToStringBuilder(this)
            .append("routeNodeInstanceId", routeNodeInstanceId)
            .append("documentId", documentId)
            .append("branch", branch == null ? null : branch.getBranchId())
            .append("routeNode", routeNode == null ? null : routeNode.getRouteNodeName())
            .append("active", active)
            .append("complete", complete)
            .append("initial", initial)
            .append("process", process)
            .append("state", state == null ? null : state.size())
            .toString();
    }
    
	//@PrePersist
	public void beforeInsert(){
		OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());
	}


	public static org.kuali.rice.kew.api.document.node.RouteNodeInstance to(RouteNodeInstance routeNodeInstance) {
		if (routeNodeInstance == null) {
			return null;
		}
		org.kuali.rice.kew.api.document.node.RouteNodeInstance.Builder builder = org.kuali.rice.kew.api.document.node
                .RouteNodeInstance.Builder.create();
		builder.setActive(routeNodeInstance.isActive());
		builder.setBranchId(routeNodeInstance.getBranch().getBranchId());
		builder.setComplete(routeNodeInstance.isComplete());
		builder.setDocumentId(routeNodeInstance.getDocumentId());
		builder.setId(routeNodeInstance.getRouteNodeInstanceId());
		builder.setInitial(routeNodeInstance.isInitial());
		builder.setName(routeNodeInstance.getName());
		if (routeNodeInstance.getProcess() != null) {
			builder.setProcessId(routeNodeInstance.getProcess().getRouteNodeInstanceId());
		}
		builder.setRouteNodeId(routeNodeInstance.getRouteNode().getRouteNodeId());
		List<RouteNodeInstanceState.Builder> states = new ArrayList<RouteNodeInstanceState.Builder>();
		for (NodeState stateBo : routeNodeInstance.getState()) {
			RouteNodeInstanceState.Builder stateBuilder = RouteNodeInstanceState.Builder.create();
			stateBuilder.setId(stateBo.getStateId());
			stateBuilder.setKey(stateBo.getKey());
			stateBuilder.setValue(stateBo.getValue());
			states.add(stateBuilder);
		}
		builder.setState(states);

        List<org.kuali.rice.kew.api.document.node.RouteNodeInstance.Builder> nextNodes = new ArrayList<org.kuali.rice.kew.api.document.node.RouteNodeInstance.Builder>();
        if (routeNodeInstance.getNextNodeInstances() != null) {
            for (RouteNodeInstance next : routeNodeInstance.getNextNodeInstances()) {
                // KULRICE-8152 - During load testing, sometimes routeNodeInstance.getNextNodeInstances() returns an
                // arraylist with size = 1 but all elements are null, which causes a "contract was null" error when the
                // create is called.  This check to see if next is not null prevents the error from occurring.
                if (next != null) {
                    // will this make things blow up?
                    nextNodes.add(org.kuali.rice.kew.api.document.node.RouteNodeInstance.Builder.create(RouteNodeInstance.to(next)));
                }
            }
        }
        builder.setNextNodeInstances(nextNodes);

		return builder.build();



	}
    
}
