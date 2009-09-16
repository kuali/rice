/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.rice.kew.dto;


/**
 * Event passed to remote post processor when document changes route levels.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentRouteLevelChangeDTO extends DocumentEventDTO {

	private static final long serialVersionUID = 6822976938764899168L;
	private Integer oldRouteLevel;
    private Integer newRouteLevel;
    private String oldNodeName;
    private String newNodeName;
    private Long oldNodeInstanceId;
    private Long newNodeInstanceId;

    public DocumentRouteLevelChangeDTO() {
        super(ROUTE_LEVEL_CHANGE);
    }

    public Integer getNewRouteLevel() {
        return newRouteLevel;
    }

    public void setNewRouteLevel(Integer newRouteLevel) {
        this.newRouteLevel = newRouteLevel;
    }

    public Integer getOldRouteLevel() {
        return oldRouteLevel;
    }

    public void setOldRouteLevel(Integer oldRouteLevel) {
        this.oldRouteLevel = oldRouteLevel;
    }

    public Long getNewNodeInstanceId() {
        return newNodeInstanceId;
    }

    public void setNewNodeInstanceId(Long newNodeInstanceId) {
        this.newNodeInstanceId = newNodeInstanceId;
    }

    public String getNewNodeName() {
        return newNodeName;
    }

    public void setNewNodeName(String newNodeName) {
        this.newNodeName = newNodeName;
    }

    public Long getOldNodeInstanceId() {
        return oldNodeInstanceId;
    }

    public void setOldNodeInstanceId(Long oldNodeInstanceId) {
        this.oldNodeInstanceId = oldNodeInstanceId;
    }

    public String getOldNodeName() {
        return oldNodeName;
    }

    public void setOldNodeName(String oldNodeName) {
        this.oldNodeName = oldNodeName;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("RouteHeaderID ").append(getRouteHeaderId());
        buffer.append(" changing from routeLevel ").append(oldRouteLevel);
        buffer.append(" to routeLevel ").append(newRouteLevel);
        buffer.append(", from node ").append(oldNodeName);
        buffer.append(" to node ").append(newNodeName);
        return buffer.toString();
    }

}
