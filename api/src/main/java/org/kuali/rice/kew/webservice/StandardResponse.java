package org.kuali.rice.kew.webservice;

/**
 * "Standard" response object
 */
public class StandardResponse extends ErrorResponse {
    protected String docStatus;
    protected String createDate;
    protected String initiatorPrincipalId;
    protected String routedByPrincipalId;
    protected String routedByUserName;
    protected String appDocId;
    protected String initiatorName;
    public String getDocStatus() {
        return docStatus;
    }
    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getInitiatorPrincipalId() {
        return initiatorPrincipalId;
    }
    public void setInitiatorPrincipalId(String initiatorId) {
        this.initiatorPrincipalId = initiatorId;
    }
    public String getAppDocId() {
        return appDocId;
    }
    public void setAppDocId(String appDocId) {
        this.appDocId = appDocId;
    }
    public String getInitiatorName() {
        return initiatorName;
    }
    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }
    public String getRoutedByPrincipalId() {
        return routedByPrincipalId;
    }
    public void setRoutedByPrincipalId(String routedByUserId) {
        this.routedByPrincipalId = routedByUserId;
    }
    public String getRoutedByUserName() {
        return routedByUserName;
    }
    public void setRoutedByUserName(String routedByUserName) {
        this.routedByUserName = routedByUserName;
    }
}