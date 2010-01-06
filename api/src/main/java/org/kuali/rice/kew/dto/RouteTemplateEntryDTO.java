/*
 * Copyright 2005-2008 The Kuali Foundation
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

import org.kuali.rice.kew.util.KEWConstants;

/**
 * Represents a route level
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @deprecated use RouteNodeVO instead
 */
public class RouteTemplateEntryDTO implements java.io.Serializable, Cloneable {
    
  static final long serialVersionUID = -6088763707485186852L;
  private Long docTypeId;
  private String routeMethodName;
  private Integer routeLevel;
  private Long exceptionGroupId; // the Group that gets the document if this route method throws an exception
  private String routeLevelName;
  private boolean finalApprover;
  private boolean mandatoryRoute;
  private Integer jrf_ver_nbr;

  public RouteTemplateEntryDTO() {}

  public RouteTemplateEntryDTO(int docTypeId, String routeMethodName, int groupId, int routeLevel,
    String routeLevelName, boolean finalApprover, boolean manadatoryRoute) {
    this.docTypeId = new Long(docTypeId);
    this.exceptionGroupId = new Long(groupId);
    this.finalApprover = finalApprover;
    this.routeLevel = new Integer(routeLevel);
    this.routeLevelName = routeLevelName;
    this.routeMethodName = routeMethodName;
    this.mandatoryRoute = manadatoryRoute;
  }

  public Long getDocTypeId() {
    return docTypeId;
  }

  public Long getExceptionGroupId() {
    return exceptionGroupId;
  }

  public Integer getJrf_ver_nbr() {
    return jrf_ver_nbr;
  }

  public Integer getRouteLevel() {
    return routeLevel;
  }

  public String getRouteLevelName() {
    return routeLevelName;
  }

  public String getRouteMethodName() {
    return routeMethodName;
  }

  public void setRouteMethodName(String routeMethodName) {
    this.routeMethodName = routeMethodName;
  }

  public void setRouteLevelName(String routeLevelName) {
    this.routeLevelName = routeLevelName;
  }

  public void setRouteLevel(Integer routeLevel) {
    this.routeLevel = routeLevel;
  }

  public void setJrf_ver_nbr(Integer jrf_ver_nbr) {
    this.jrf_ver_nbr = jrf_ver_nbr;
  }

  public void setExceptionGroupId(Long exceptionGroupId) {
    this.exceptionGroupId = exceptionGroupId;
  }

  public void setDocTypeId(Long docTypeId) {
    this.docTypeId = docTypeId;
  }

  public void setFinalApprover(boolean finalApprover) {
    this.finalApprover = finalApprover;
  }

  public boolean isFinalApprover() {
    return finalApprover;
  }

  public boolean isMandatoryRoute() {
    return mandatoryRoute;
  }

  public void setMandatoryRoute(boolean mandatoryRoute) {
    this.mandatoryRoute = mandatoryRoute;
  }

  public void setMandatoryRoute(String mandatoryRoute) {
    this.mandatoryRoute = KEWConstants.TRUE_CD.equals(mandatoryRoute);

    if (mandatoryRoute == null) {
      this.mandatoryRoute = false;
    }
  }

}
