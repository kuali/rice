package edu.iu.uis.eden;


/**
 * <p><Title> </p>
 * <p><Description> </p>
 * <p><p><p>Copyright: Copyright (c) 2002</p>
 * <p><p>Company: UIS - Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version $Revision: 1.2 $ - $Date: 2007-12-03 03:48:52 $
 */
public class DocumentRouteLevelChange implements IDocumentEvent {
	
	// TODO for now we will include the new node-based routing fields onto this object to avoid an interface
	// change to the PostProcessor interface.
	
  private static final long serialVersionUID = 785552701611174468L;

  private Long routeHeaderId;
  private String appDocId;
  private Integer oldRouteLevel;
  private Integer newRouteLevel;
  private String oldNodeName;
  private String newNodeName;
  private Long oldNodeInstanceId;
  private Long newNodeInstanceId;
  
  //  this constructor is for backwards compatibility
  public DocumentRouteLevelChange(Long routeHeaderId, String appDocId, Integer oldRouteLevel, Integer newRouteLevel) {
	  this(routeHeaderId, appDocId, oldRouteLevel, newRouteLevel, null, null, null, null);
  }
  
  public DocumentRouteLevelChange(Long routeHeaderId, String appDocId, Integer oldRouteLevel,
    Integer newRouteLevel, String oldNodeName, String newNodeName, Long oldNodeInstanceId, Long newNodeInstanceId) {
    this.routeHeaderId = routeHeaderId;
    this.oldRouteLevel = oldRouteLevel;
    this.newRouteLevel = newRouteLevel;
    this.oldNodeName = oldNodeName;
    this.newNodeName = newNodeName;
    this.oldNodeInstanceId = oldNodeInstanceId;
    this.newNodeInstanceId = newNodeInstanceId;
    this.appDocId = appDocId;
  }

  public String getDocumentEventCode() {
    return ROUTE_LEVEL_CHANGE;
  }

  public Long getRouteHeaderId() {
    return routeHeaderId;
  }

  public Integer getOldRouteLevel() {
    return oldRouteLevel;
  }

  public Integer getNewRouteLevel() {
    return newRouteLevel;
  }

  public Long getNewNodeInstanceId() {
	return newNodeInstanceId;
  }

  public String getNewNodeName() {
	return newNodeName;
  }

  public Long getOldNodeInstanceId() {
	return oldNodeInstanceId;
  }

  public String getOldNodeName() {
	return oldNodeName;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("RouteHeaderID ").append(routeHeaderId);
    buffer.append(" changing from routeLevel ").append(oldRouteLevel);
    buffer.append(" to routeLevel ").append(newRouteLevel);

    return buffer.toString();
  }

  /**
   * @return
   */
  public String getAppDocId() {
    return appDocId;
  }
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
