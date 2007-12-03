package edu.iu.uis.eden;

import java.io.Serializable;


/**
 * <p><Title> </p>
 * <p><Description> </p>
 * <p><p><p>Copyright: Copyright (c) 2002</p>
 * <p><p>Company: UIS - Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version $Revision: 1.2 $ - $Date: 2007-12-03 03:48:51 $
 */
public interface IDocumentEvent extends Serializable {
  public static final String ROUTE_LEVEL_CHANGE = "rt_lvl_change";
  public static final String ROUTE_STATUS_CHANGE = "rt_status_change";
  public static final String DELETE_CHANGE = "delete_document";
  public static final String ACTION_TAKEN = "action_taken";

  public String getDocumentEventCode();

  public Long getRouteHeaderId();

  /**
   * Returns the application document id registered for this document when it was
   * created.
   * @return
   */
  public String getAppDocId();
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
