/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.routelog.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

/**
 * The Struts ActionForm used with {@link RouteLogAction} to display the routelog.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RouteLogForm extends ActionForm {
    
    private static final long serialVersionUID = -3997667167734868281L;
    private String methodToCall = "";
    private String routeHeaderId;    
    private List rootRequests = new ArrayList();
    private int pendingActionRequestCount;
    private List futureRootRequests = new ArrayList();
    private int futureActionRequestCount;
    private boolean showFuture;
    private boolean removeHeader;
    private boolean lookFuture;
    private boolean showNotes;
    private String docId;
    private String returnUrlLocation = null;
    private boolean showCloseButton = false;
    
    public boolean isShowCloseButton() {
        return showCloseButton;
    }
    public void setShowCloseButton(boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
    }
    public String getReturnUrlLocation() {
        return returnUrlLocation;
    }
    public void setReturnUrlLocation(String returnUrlLocation) {
        this.returnUrlLocation = returnUrlLocation;
    }
    public String getDocId() {
        return docId;
    }
    public void setDocId(String docId) {
        this.docId = docId;
    }
    public boolean isShowFuture() {
        return showFuture;
    }
    public void setShowFuture(boolean showReportURL) {
        this.showFuture = showReportURL;
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    public String getRouteHeaderId() {
        return routeHeaderId;
    }
    public void setRouteHeaderId(String routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public int getPendingActionRequestCount() {
        return pendingActionRequestCount;
    }

    public void setPendingActionRequestCount(int pendingActionRequestCount) {
        this.pendingActionRequestCount = pendingActionRequestCount;
    }
    
    public List getRootRequests() {
        return rootRequests;
    }
    public void setRootRequests(List rootRequests) {
        this.rootRequests = rootRequests;
    }
    public int getFutureActionRequestCount() {
        return futureActionRequestCount;
    }
    public void setFutureActionRequestCount(int futureActionRequestCount) {
        this.futureActionRequestCount = futureActionRequestCount;
    }
    public List getFutureRootRequests() {
        return futureRootRequests;
    }
    public void setFutureRootRequests(List futureRootRequests) {
        this.futureRootRequests = futureRootRequests;
    }
    public boolean isRemoveHeader() {
        return removeHeader;
    }
    public void setRemoveHeader(boolean removeBar) {
        this.removeHeader = removeBar;
    }
    public boolean isLookFuture() {
        return lookFuture;
    }
    public void setLookFuture(boolean showFutureLink) {
        this.lookFuture = showFutureLink;
    }
	public boolean isShowNotes() {
		return showNotes;
	}
	public void setShowNotes(boolean showNotes) {
		this.showNotes = showNotes;
	}
}