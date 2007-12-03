package edu.iu.uis.eden;

import java.util.Collection;

public interface WorkflowServiceError {
  public Collection getChildren();
  public Collection getFlatChildrenList();
  public String getMessage();
  public String getKey();
  public String getArg1();
  public String getArg2();
  public void addChild(WorkflowServiceError busError);
  public void addChildren(Collection children);
  
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
