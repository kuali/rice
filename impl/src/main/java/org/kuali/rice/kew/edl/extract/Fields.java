/*
 * Copyright 2005-2008 The Kuali Foundation
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

package org.kuali.rice.kew.edl.extract;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 *
 *
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KREW_EDL_FLD_DMP_T")
//@Sequence(name="KREW_EDL_FLD_DMP_S", property="fieldId")
public class Fields {

	private static final long serialVersionUID = -6136544551121011531L;

    @Id
    @GeneratedValue(generator="KREW_EDL_FLD_DMP_S")
	@GenericGenerator(name="KREW_EDL_FLD_DMP_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_EDL_FLD_DMP_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="EDL_FIELD_DMP_ID")
	private Long fieldId;
    @Column(name="DOC_HDR_ID")
	private Long docId;
    @Column(name="FLD_NM")
	private String fieldName;
    @Column(name="FLD_VAL")
	private String fieldValue;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DOC_HDR_ID", insertable=false, updatable=false)
	private Dump dump;

    //@PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KNSServiceLocator.getEntityManagerFactory().createEntityManager());
    }


	public Long getFieldId() {
		return fieldId;
	}
	public Long getDocId() {
		return docId;
	}
	public void setDocId(final Long docId) {
		this.docId = docId;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(final String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getFiledName() {
		return fieldName;
	}
	public void setFieldName(final String filedName) {
		this.fieldName = filedName;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(final Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public Dump getDump() {
		return dump;
	}
	public void setDump(final Dump dump) {
		this.dump = dump;
	}
}

