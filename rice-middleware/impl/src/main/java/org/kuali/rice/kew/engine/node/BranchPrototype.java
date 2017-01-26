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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Represents a Branch in the definition of a DocumentType.  This should not be confused with the
 * {@link Branch} class which represents the actual instance of a branch on a document. 
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RTE_BRCH_PROTO_T")
//@Sequence(name="KREW_RTE_NODE_S", property="branchId")
public class BranchPrototype implements Serializable {

	private static final long serialVersionUID = 8645994738204838275L;
    
    @Id
    @GeneratedValue(generator="KREW_RTE_NODE_S")
	@Column(name="RTE_BRCH_PROTO_ID")
	private String branchId;
	@Column(name="BRCH_NM")
	private String name;
	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
	
	public String getBranchId() {
		return branchId;
	}
	
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	
}

