package org.kuali.rice.kew.edl.extract;

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name="KREW_EDL_FLD_DMP_T")
public class Fields {

	private static final long serialVersionUID = -6136544551121011531L;
	
	@Id
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
    
	public Long getFieldId() {
		return fieldId;
	}
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getFiledName() {
		return fieldName;
	}
	public void setFieldName(String filedName) {
		this.fieldName = filedName;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public Dump getDump() {
		return dump;
	}
	public void setDump(Dump dump) {
		this.dump = dump;
	}
	
   
}

