package edu.iu.uis.eden.edl.extract;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;

public class Dump {

	private static final long serialVersionUID = -6136544551121011531L;
	
	private Long docId;
	private String docTypeName;
	private String docRouteStatusCode;
	private Timestamp docModificationDate;
	private Timestamp docCreationDate;
	private String docDescription;
    private String docInitiatorId;
    private String docCurrentNodeName;
    private Integer lockVerNbr;
    
    private List fields = new ArrayList();
    
	public Timestamp getDocCreationDate() {
		return docCreationDate;
	}
	public void setDocCreationDate(Timestamp docCreationDate) {
		this.docCreationDate = docCreationDate;
	}
	public String getDocCurrentNodeName() {
		return docCurrentNodeName;
	}
	public void setDocCurrentNodeName(String docCurrentNodeName) {
		this.docCurrentNodeName = docCurrentNodeName;
	}
	public String getDocDescription() {
		return docDescription;
	}
	public void setDocDescription(String docDescription) {
		this.docDescription = docDescription;
	}
	public Long getDocId() {
		return docId;
	}
	public String getDocInitiatorId() {
		return docInitiatorId;
	}
	public void setDocInitiatorId(String docInitiatorId) {
		this.docInitiatorId = docInitiatorId;
	}
	public Timestamp getDocModificationDate() {
		return docModificationDate;
	}
	public void setDocModificationDate(Timestamp docModificationDate) {
		this.docModificationDate = docModificationDate;
	}
	public String getDocRouteStatusCode() {
		return docRouteStatusCode;
	}
	public void setDocRouteStatusCode(String docRouteStatusCode) {
		this.docRouteStatusCode = docRouteStatusCode;
	}
	public String getDocTypeName() {
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
    public String getFormattedCreateDateTime() {
        long time = getDocCreationDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(EdenConstants.TIMESTAMP_DATE_FORMAT_PATTERN2);
        return dateFormat.format(date);
    }

    public String getFormattedCreateDate() {
        long time = getDocCreationDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = EdenConstants.getDefaultDateFormat();
        return dateFormat.format(date);
    }
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	

	public List getFields() {
		return fields;
	}

	public void setFields(List fields) {
		this.fields = fields;
	}
	
}
