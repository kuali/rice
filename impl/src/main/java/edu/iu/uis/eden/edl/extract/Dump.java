package edu.iu.uis.eden.edl.extract;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.util.RiceConstants;


@Entity
@Table(name="EN_EDL_DMP_T")
public class Dump {

	private static final long serialVersionUID = -6136544551121011531L;
	
	@Id
	@Column(name="DOC_HDR_ID")
	private Long docId;
	@Column(name="DOC_TYP_NM")
	private String docTypeName;
	@Column(name="DOC_RTE_STAT_CD")
	private String docRouteStatusCode;
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DOC_MDFN_DT")
	private Timestamp docModificationDate;
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DOC_CRTE_DT")
	private Timestamp docCreationDate;
	@Column(name="DOC_TTL")
	private String docDescription;
    @Column(name="DOC_INITR_ID")
	private String docInitiatorId;
    @Column(name="DOC_CRNT_NODE_NM")
	private String docCurrentNodeName;
    @Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
    
    @Transient
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
        DateFormat dateFormat = new SimpleDateFormat(KEWConstants.TIMESTAMP_DATE_FORMAT_PATTERN2);
        return dateFormat.format(date);
    }

    public String getFormattedCreateDate() {
        long time = getDocCreationDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = RiceConstants.getDefaultDateFormat();
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

