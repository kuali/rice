/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kew.documentlink;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.service.KEWServiceLocator;

/**
 * Server side bean for DocumentLinkDAO 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */

@Entity
@Table(name="KREW_DOC_LNK_T")
@Sequence(name="KREW_DOC_LNK_S",property="docLinkId")
public class DocumentLink implements WorkflowPersistable {

	private static final long serialVersionUID = 551926904795633010L;
	
	@Id
	@Column(name="DOC_LNK_ID")
	private Long docLinkId;
    @Column(name="ORGN_DOC_ID")
	private Long orgnDocId;
    @Column(name="DEST_DOC_ID")
	private Long destDocId;
    
	/**
	 * @return the docLinkId
	 */
	public Long getDocLinkId() {
		return this.docLinkId;
	}

	/**
	 * @param docLinkId the docLinkId to set
	 */
	public void setDocLinkId(Long docLinkId) {
		this.docLinkId = docLinkId;
	}

	/**
	 * @return the orgnDocId
	 */
	public Long getOrgnDocId() {
		return this.orgnDocId;
	}

	/**
	 * @param orgnDocId the orgnDocId to set
	 */
	public void setOrgnDocId(Long orgnDocId) {
		this.orgnDocId = orgnDocId;
	}

	/**
	 * @return the destDocId
	 */
	public Long getDestDocId() {
		return this.destDocId;
	}

	/**
	 * @param destDocId the destDocId to set
	 */
	public void setDestDocId(Long destDocId) {
		this.destDocId = destDocId;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.bo.WorkflowPersistable#copy(boolean)
	 */
	public Object copy(boolean preserveKeys) {
		return null;
	}
	
	@PrePersist
	public void beforeInsert(){
		OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());		
	}

}
