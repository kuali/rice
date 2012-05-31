/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.preferences;

import java.io.Serializable;

import org.kuali.rice.kew.util.KEWConstants;

/**
 * Model bean representing an individual user's Preferences within KEW.
 * 
 * <p>When loaded, Preferences could be in a state where they require being saved to the database.
 * If this is the case then {{@link #requiresSave} will evaluate to true.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Preferences implements Serializable {

	static final long serialVersionUID = -5323719135590442782L;
   
	private boolean requiresSave = false;
	
    private String emailNotification;
    private String notifyPrimaryDelegation;
    private String notifySecondaryDelegation;
    private String openNewWindow;
    private String showActionRequested;
    private String showDateCreated;
    private String showDocumentStatus;
    private String showAppDocStatus;
	private String showDocType;
    private String showInitiator;
    private String showDocTitle;
    private String showWorkgroupRequest;
    private String showDelegator;
    private String showClearFyi;
    private String pageSize;
    private String refreshRate;
    private String colorSaved;
    private String colorInitiated;
    private String colorDissaproved;
    private String colorEnroute;
    private String colorApproved;
    private String colorFinal;
    private String colorDissapproveCancel;
    private String colorProccessed;
    private String colorException;
    private String colorCanceled;
    private String delegatorFilter;
    private String useOutbox;
    private String showDateApproved;
    private String showCurrentNode;
    private String primaryDelegateFilter;
 
    /**
     * @return Returns the colorApproved.
     */
    public String getColorApproved() {
        return colorApproved;
    }
    /**
     * @param colorApproved The colorApproved to set.
     */
    public void setColorApproved(String colorApproved) {
        this.colorApproved = colorApproved;
    }
    /**
     * @return Returns the colorCanceled.
     */
    public String getColorCanceled() {
        return colorCanceled;
    }
    /**
     * @param colorCanceled The colorCanceled to set.
     */
    public void setColorCanceled(String colorCanceled) {
        this.colorCanceled = colorCanceled;
    }
    /**
     * @return Returns the colorDissapproveCancel.
     */
    public String getColorDissapproveCancel() {
        return colorDissapproveCancel;
    }
    /**
     * @param colorDissapproveCancel The colorDissapproveCancel to set.
     */
    public void setColorDissapproveCancel(String colorDissapproveCancel) {
        this.colorDissapproveCancel = colorDissapproveCancel;
    }
    /**
     * @return Returns the colorDissaproved.
     */
    public String getColorDissaproved() {
        return colorDissaproved;
    }
    /**
     * @param colorDissaproved The colorDissaproved to set.
     */
    public void setColorDissaproved(String colorDissaproved) {
        this.colorDissaproved = colorDissaproved;
    }
    /**
     * @return Returns the colorEnroute.
     */
    public String getColorEnroute() {
        return colorEnroute;
    }
    /**
     * @param colorEnroute The colorEnroute to set.
     */
    public void setColorEnroute(String colorEnroute) {
        this.colorEnroute = colorEnroute;
    }
    /**
     * @return Returns the colorException.
     */
    public String getColorException() {
        return colorException;
    }
    /**
     * @param colorException The colorException to set.
     */
    public void setColorException(String colorException) {
        this.colorException = colorException;
    }
    /**
     * @return Returns the colorFinal.
     */
    public String getColorFinal() {
        return colorFinal;
    }
    /**
     * @param colorFinal The colorFinal to set.
     */
    public void setColorFinal(String colorFinal) {
        this.colorFinal = colorFinal;
    }
    /**
     * @return Returns the colorInitiated.
     */
    public String getColorInitiated() {
        return colorInitiated;
    }
    /**
     * @param colorInitiated The colorInitiated to set.
     */
    public void setColorInitiated(String colorInitiated) {
        this.colorInitiated = colorInitiated;
    }
    /**
     * @return Returns the colorProccessed.
     */
    public String getColorProccessed() {
        return colorProccessed;
    }
    /**
     * @param colorProccessed The colorProccessed to set.
     */
    public void setColorProccessed(String colorProccessed) {
        this.colorProccessed = colorProccessed;
    }
    /**
     * @return Returns the colorSaved.
     */
    public String getColorSaved() {
        return colorSaved;
    }
    /**
     * @param colorSaved The colorSaved to set.
     */
    public void setColorSaved(String colorSaved) {
        this.colorSaved = colorSaved;
    }
    /**
     * @return Returns the emailNotification.
     */
    public String getEmailNotification() {
        return emailNotification;
    }
    /**
     * @param emailNotification The emailNotification to set.
     */
    public void setEmailNotification(String emailNotification) {
        this.emailNotification = emailNotification;
    }
	/**
     * @return Whether or not to send email notifications for primary delegation requests.
     */
    public String getNotifyPrimaryDelegation() {
		return notifyPrimaryDelegation;
	}
	public void setNotifyPrimaryDelegation(String notifyPrimaryDelegation) {
		this.notifyPrimaryDelegation = notifyPrimaryDelegation;
	}
	/**
	 * @return Whether or not to send email notifications for secondary delegation requests.
	 */
	public String getNotifySecondaryDelegation() {
		return notifySecondaryDelegation;
	}
	public void setNotifySecondaryDelegation(String notifySecondaryDelegation) {
		this.notifySecondaryDelegation = notifySecondaryDelegation;
	}
	/**
     * @return Returns the openNewWindow.
     */
    public String getOpenNewWindow() {
        return openNewWindow;
    }
    /**
     * @param openNewWindow The openNewWindow to set.
     */
    public void setOpenNewWindow(String openNewWindow) {
        this.openNewWindow = openNewWindow;
    }
    /**
     * @return Returns the pageSize.
     */
    public String getPageSize() {
        return pageSize;
    }
    /**
     * @param pageSize The pageSize to set.
     */
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
    /**
     * @return Returns the refreshRate.
     */
    public String getRefreshRate() {
        return refreshRate;
    }
    /**
     * @param refreshRate The refreshRate to set.
     */
    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }
    /**
     * @return Returns the showActionRequested.
     */
    public String getShowActionRequested() {
        return showActionRequested;
    }
    /**
     * @param showActionRequested The showActionRequested to set.
     */
    public void setShowActionRequested(String showActionRequested) {
        this.showActionRequested = showActionRequested;
    }
    /**
     * @return Returns the showDateCreated.
     */
    public String getShowDateCreated() {
        return showDateCreated;
    }
    /**
     * @param showDateCreated The showDateCreated to set.
     */
    public void setShowDateCreated(String showDateCreated) {
        this.showDateCreated = showDateCreated;
    }
    /**
     * @return Returns the showDocType.
     */
    public String getShowDocType() {
        return showDocType;
    }
    /**
     * @param showDocType The showDocType to set.
     */
    public void setShowDocType(String showDocType) {
        this.showDocType = showDocType;
    }
    /**
     * @return Returns the showDocumentStatus.
     */
    public String getShowDocumentStatus() {
        return showDocumentStatus;
    }
    /**
     * @param showDocumentStatus The showDocumentStatus to set.
     */
    public void setShowDocumentStatus(String showDocumentStatus) {
        this.showDocumentStatus = showDocumentStatus;
    }
    /**
     * @return Returns the showAppDocumentStatus.
     */
    public String getShowAppDocStatus() {
		return this.showAppDocStatus;
	}
    /**
     * @param showAppDocStatus The showAppDocStatus to set.
     */
	public void setShowAppDocStatus(String showAppDocStatus) {
		this.showAppDocStatus = showAppDocStatus;
	}

    /**
     * @return Returns the showInitiator.
     */
    public String getShowInitiator() {
        return showInitiator;
    }
    /**
     * @param showInitiator The showInitiator to set.
     */
    public void setShowInitiator(String showInitiator) {
        this.showInitiator = showInitiator;
    }
    /**
     * @return Returns the showTitle.
     */
    public String getShowDocTitle() {
        return showDocTitle;
    }
    /**
     * @param showTitle The showTitle to set.
     */
    public void setShowDocTitle(String showTitle) {
        this.showDocTitle = showTitle;
    }
    /**
     * @return Returns the showWorkGroupRequest.
     */
    public String getShowWorkgroupRequest() {
        return showWorkgroupRequest;
    }
    /**
     * @param showWorkGroupRequest The showWorkGroupRequest to set.
     */
    public void setShowWorkgroupRequest(String showWorkGroupRequest) {
        this.showWorkgroupRequest = showWorkGroupRequest;
    }
    public String getShowDelegator() {
        return showDelegator;
    }
    public void setShowDelegator(String showDelegator) {
        this.showDelegator = showDelegator;
    }

    public String getShowClearFyi() {
        return showClearFyi;
    }

    public void setShowClearFyi(String showClearFyi) {
        this.showClearFyi = showClearFyi;
    }

    public String getDelegatorFilter() {
        return delegatorFilter;
    }

    public void setDelegatorFilter(String delegatorFilter) {
        this.delegatorFilter = delegatorFilter;
    }
    public String getUseOutbox() {
        return this.useOutbox;
    }
    public void setUseOutbox(String useOutbox) {
        this.useOutbox = useOutbox;
    }
    public boolean isUsingOutbox() {
	if (this.getUseOutbox() != null && this.getUseOutbox().equals(KEWConstants.PREFERENCES_YES_VAL)) {
	    return true;
	}
	return false;
    }

    public boolean isRequiresSave() {
        return this.requiresSave;
    }

    public void setRequiresSave(boolean requiresSave) {
        this.requiresSave = requiresSave;
    }
	/**
	 * @return the showDateApproved
	 */
	public String getShowDateApproved() {
		return this.showDateApproved;
	}
	/**
	 * @param showDateApproved the showDateApproved to set
	 */
	public void setShowDateApproved(String showDateApproved) {
		this.showDateApproved = showDateApproved;
	}
	/**
	 * @return the showCurrentNode
	 */
	public String getShowCurrentNode() {
		return this.showCurrentNode;
	}
	/**
	 * @param currentNodes the currentNodes to set
	 */
	public void setShowCurrentNode(String currentNode) {
		this.showCurrentNode = currentNode;
	}
	/**
	 * @return the primaryDelegateFilter
	 */
	public String getPrimaryDelegateFilter() {
		return this.primaryDelegateFilter;
	}
	/**
	 * @param primaryDelegateFilter the primaryDelegateFilter to set
	 */
	public void setPrimaryDelegateFilter(String primaryDelegateFilter) {
		this.primaryDelegateFilter = primaryDelegateFilter;
	}
}
