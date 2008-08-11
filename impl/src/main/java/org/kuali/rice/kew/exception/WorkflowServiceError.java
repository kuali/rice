package org.kuali.rice.kew.exception;

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
