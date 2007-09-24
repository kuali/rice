package edu.iu.uis.eden.edl.extract;

public class Fields {

	private static final long serialVersionUID = -6136544551121011531L;
	
	private Long fieldId;
    private Long docId;
    private String fieldName;
    private String fieldValue;
    private Integer lockVerNbr;
    
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
