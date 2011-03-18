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
package org.kuali.rice.kew.routelog.web;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.KualiForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * The Struts ActionForm used with {@link RouteLogAction} to display the routelog.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RouteLogForm extends KualiForm {

    private static final long serialVersionUID = -3997667167734868281L;
    private String methodToCall = "";
    private String routeHeaderId;
    private List rootRequests = new ArrayList();
    private int pendingActionRequestCount;
    private List futureRootRequests = new ArrayList();
    private int futureActionRequestCount;
    private boolean showFuture;
    private String showFutureError;
    private boolean removeHeader;
    private boolean lookFuture;
    private boolean showNotes;
    private String docId;
    private String returnUrlLocation = null;
    private boolean showCloseButton = false;
    private String newRouteLogActionMessage;
    private boolean enableLogAction = false;

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
    public boolean isShowFutureHasError() {
        return !org.apache.commons.lang.StringUtils.isEmpty(getShowFutureError());
    }
    public String getShowFutureError() {
        return showFutureError;
    }
    public void setShowFutureError(String showFutureError) {
        this.showFutureError = showFutureError;
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
	
	public String getNewRouteLogActionMessage() {
		return this.newRouteLogActionMessage;
	}
	
	public void setNewRouteLogActionMessage(String newRouteLogActionMessage) {
		this.newRouteLogActionMessage = newRouteLogActionMessage;
	}
	
	public boolean isEnableLogAction() {
		return this.enableLogAction;
	}
	
	public void setEnableLogAction(boolean enableLogAction) {
		this.enableLogAction = enableLogAction;
	}
	
	public String getHeaderMenuBar() {
		Properties parameters = new Properties();
        parameters.put("showFuture", isShowFuture());
        parameters.put("showNotes", isShowNotes());
		if (getRouteHeaderId() != null) {
			parameters.put("routeHeaderId", getRouteHeaderId());
		}
		if (getDocId() != null) {
			parameters.put("docId", getDocId());
		}
		if (getReturnUrlLocation() != null) {
			parameters.put("backUrl", getReturnUrlLocation());
		}
        String url = UrlFactory.parameterizeUrl("RouteLog.do", parameters);
        String krBaseUrl = ConfigContext.getCurrentContextConfig().getKRBaseURL();
        url = "<div class=\"lookupcreatenew\" title=\"Refresh\"><a href=\"" + url + "\"><img src=\""+krBaseUrl+"/images/tinybutton-refresh.gif\" alt=\"refresh\"></a></div>";
        return url;
	}
}
