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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;
import java.util.Calendar;

import edu.iu.uis.eden.actiontaken.ActionTakenValue;

/**
 * A transport object representing an {@link ActionTakenValue}
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class ActionTakenVO implements Serializable {
    static final long serialVersionUID = -8818100923517546091L;
    private Long actionTakenId;
    private Long routeHeaderId;
    private Integer docVersion;
    private UserVO userVO;
    private UserVO delegatorVO;
    private String actionTaken;
    private Calendar actionDate;
    private String annotation = null;

    public ActionTakenVO() {
    }

    public Calendar getActionDate() {
        return actionDate;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public Long getActionTakenId() {
        return actionTakenId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public Integer getDocVersion() {
        return docVersion;
    }

    public Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public void setDocVersion(Integer docVersion) {
        this.docVersion = docVersion;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setActionTakenId(Long actionTakenId) {
        this.actionTakenId = actionTakenId;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public void setActionDate(Calendar actionDate) {
        this.actionDate = actionDate;
    }

    public UserVO getUserVO() {
        return userVO;
    }

    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }

    public UserVO getDelegatorVO() {
        return delegatorVO;
    }

    public void setDelegatorVO(UserVO delegatorVO) {
        this.delegatorVO = delegatorVO;
    }

}