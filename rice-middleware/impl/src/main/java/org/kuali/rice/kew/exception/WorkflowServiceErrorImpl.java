/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.krad.util.MessageMap;



/**
 * <p>Title: DocElementError </p>
 * <p>Description: A simple object holding any error(s) generated by
 * an IDocElement and it's children IDocElements.  See IDocElement
 * documentation for further explanation.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Indiana University</p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkflowServiceErrorImpl implements Serializable, WorkflowServiceError {

	private static final String CHILDREN_IN_ERROR = "-1";

  static final long serialVersionUID = 6900090941686297017L;
  private Collection children;
  private String type;
  private String message;
  private String arg1;
  private String arg2;
  
  /**
   * Passing the actual message map around so we don't lose doc search messages in standalone.
   */
  private MessageMap messageMap;



  private WorkflowServiceErrorImpl() {
  }

  public WorkflowServiceErrorImpl(String message, String type) {
    children = new ArrayList();
    this.message = message;
    this.type = type;
  }

  public WorkflowServiceErrorImpl(String message, String type, String arg1) {
      children = new ArrayList();
      this.message = message;
      this.type = type;
      this.arg1 = arg1;
  }

  public WorkflowServiceErrorImpl(String message, String type, String arg1, String arg2) {
      children = new ArrayList();
      this.message = message;
      this.type = type;
      this.arg1 = arg1;
      this.arg2 = arg2;
  }
  public WorkflowServiceErrorImpl(String message, String type, String arg1, String arg2, MessageMap messageMap) {
      children = new ArrayList();
      this.message = message;
      this.type = type;
      this.arg1 = arg1;
      this.arg2 = arg2;
      this.messageMap = messageMap;
  }

  public Collection getChildren() {
    return this.children;
  }

  public String getMessage() {
    return this.message;
  }

  public String getKey() {
    return this.type;
  }

  public String getArg1() {
    return arg1;
  }

  public String getArg2() {
    return arg2;
  }

  public void addChild(WorkflowServiceError busError) {
    if (busError != null) {
      children.add(busError);
    }
  }

  public void addChildren(Collection children) {
    this.children.addAll(children);
  }

  public Collection getFlatChildrenList() {
    return buildFlatChildrenList(this, null);
  }

  private static Collection buildFlatChildrenList(WorkflowServiceError error, List flatList) {
    if (flatList == null) {
      flatList = new ArrayList();
    }

    if (error.getKey() != CHILDREN_IN_ERROR) {
      flatList.add(error);
    }

    Iterator iter = error.getChildren().iterator();

    while (iter.hasNext()) {
      WorkflowServiceError childError = (WorkflowServiceError) iter.next();
      buildFlatChildrenList(childError, flatList);
    }

    return flatList;
  }

  public String toString() {
    String s = "[WorkflowServiceErrorImpl: type=" + type + ", message=" + message + ", arg1=" + arg1 + ", arg2=" + arg2 + ", children=";
    if (children == null) {
        s += "null";
    } else {
        s += children;
    }
    s += "]";
    return s;
  }

  /**
   * @return the messageMap
   */
  @Override
  public MessageMap getMessageMap() {
  	return this.messageMap;
  }

  /**
   * @param messageMap the messageMap to set
   */
  public void setMessageMap(MessageMap messageMap) {
  	this.messageMap = messageMap;
  }
}
