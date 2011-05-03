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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Server side bean for DocumentLinkDAO 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */

@Entity
@Table(name="KREW_DOC_LNK_T")
//@Sequence(name="KREW_DOC_LNK_S",property="docLinkId")
public class DocumentLink implements Serializable {

	private static final long serialVersionUID = 551926904795633010L;
	
	@Id
	@GeneratedValue(generator="KREW_DOC_LNK_S")
	@GenericGenerator(name="KREW_DOC_LNK_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_DOC_LNK_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="DOC_LNK_ID")
	private Long docLinkId;
    @Column(name="ORGN_DOC_ID")
	private String orgnDocId;
    @Column(name="DEST_DOC_ID")
	private String destDocId;
    
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
	public String getOrgnDocId() {
		return this.orgnDocId;
	}

	/**
	 * @param orgnDocId the orgnDocId to set
	 */
	public void setOrgnDocId(String orgnDocId) {
		this.orgnDocId = orgnDocId;
	}

	/**
	 * @return the destDocId
	 */
	public String getDestDocId() {
		return this.destDocId;
	}

	/**
	 * @param destDocId the destDocId to set
	 */
	public void setDestDocId(String destDocId) {
		this.destDocId = destDocId;
	}
	
	//@PrePersist
	public void beforeInsert(){
		OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());		
	}

}
