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
package edu.iu.uis.eden.clientapp;

import java.rmi.RemoteException;

import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.ResponsiblePartyVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;

/**
 * A RouteModule is responsible for generating Action Requests for a given Route Header document.
 * Implementations of this Interface are potentially remotable, so this Interface uses value objects.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RouteModuleRemote {

  /**
   * Generate action requests for the given RouteHeaderVO.
   *
   * @return ActionRequestVO[] the generated action requests
   * @throws EdenException
   */
  public ActionRequestVO[] findActionRequests(RouteHeaderVO routeHeader, DocumentContentVO documentContent) throws RemoteException;

  /**
   * The route module will resolve the given responsibilityId and return an object that contains the key to
   * either a user or a Eden workgroup.
   * @param rId ResponsibiliyId that we need resolved.
   * @return The ResponsibleParty containing a key to a user or workgroup.
   * @throws EdenException if any problems are found this exception can be thrown.
   */
  public ResponsiblePartyVO resolveResponsibilityId(Long responsibilityId) throws RemoteException;

}