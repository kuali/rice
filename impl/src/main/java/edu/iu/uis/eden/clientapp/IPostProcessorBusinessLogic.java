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

import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.exception.ResourceUnavailableException;


/**
 * Original PostProcessor remote interface.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @deprecated uses PostProcessorRemote
 */
public interface IPostProcessorBusinessLogic {
  /**
   * @param locator Provides runtime/deployment information to the businessclass
   */
  public void setLocator(ResourceLocator locator);

  /**
   * Return the ResourceLocator set by the ControlledPostProcessor bean
   * @return
   */
  public ResourceLocator getLocator();

  /**
   * The document has changed route status. The docEvent contains the information about the change.
   * This method should do what ever is appropriate for various route status changes to a document.
   * The method should return true if the change is correct and all application actions as a result
   * of the change are successful. It should return false if the application considers this an
   * incorrect change.
   *
   * The method can throw a ResourceUnavailableException in order to get EDEN to requeue the
   * document and try again later.
   * @param statusChangeEvent
   * @param msg any error message to be propagated back to users should be set here
   * @return true if the status change is correct and application actions are successful
   * @throws java.lang.Exception A general Exception will cause EDEN to put the document into Exception routing
   * @throws ResourceUnavailableException EDEN will requeue the document and try the change again
   * later if this exception is thrown
   * @see DocumentRouteStatusChange
   */
  public boolean doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent, StringBuffer msg)
    throws java.lang.Exception;

  /**
   * The document has changed route level. The docEvent contains the information about the change.
   * This method should do what ever is appropriate for various route level changes to a document.
   * The method should return true if the change is correct and all application actions as a result
   * of the change are successful. It should return false if the application considers this an
   * incorrect change.
   *
   * The method can throw a ResourceUnavailableException in order to get EDEN to requeue the
   * document and try again later.
   * @param levelChangeEvent
   * @param msg any error message to be propagated back to users should be set here
   * @return true if the status change is correct and application actions are successful
   * @throws java.lang.Exception A general Exception will cause EDEN to put the document into Exception routing
   * @throws ResourceUnavailableException EDEN will requeue the document and try the change again
   * later if this exception is thrown
   * @see DocumentRouteLevelChange
   */
  public boolean doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent, StringBuffer parm2)
    throws java.lang.Exception;

  /**
   * User is requesting SuperUser authority for the given document. The application should make
   * any authorization checks appropriate and return true if the user is authorized.
   * @param routeHeaderId document being accessed
   * @param user user attempting SuperUser action
   * @return true if the user is granted SuperUser authority
   * @throws java.lang.Exception
   *//*
  public boolean verifySUAuthority(Long routeHeaderId, UserIdDO user)
    throws java.lang.Exception;
*/
  
  /**
   * Eden is signaling that the document should be deleted. The application can reject this by
   * returning false. If the EdenException is thrown the docuemnt will go to exception routing. If
   * a ResourceUnavailableException is thrown, the doc will be requeued and will try again later to
   * delete the document.
   * @param event
   * @param message
   * @return
   * @throws java.lang.Exception A general Exception will cause EDEN to put the document into Exception routing
   * @throws ResourceUnavailableException EDEN will requeue the document and try the change again
   */
  public boolean doDeleteRouteHeader(DeleteEvent event, StringBuffer message)
    throws java.lang.Exception;

  public String getVersion() throws Exception;
}