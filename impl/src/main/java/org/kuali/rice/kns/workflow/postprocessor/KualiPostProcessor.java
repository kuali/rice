/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.rice.kns.workflow.postprocessor;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kew.clientapp.PostProcessorRemote;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.AfterProcessEventDTO;
import org.kuali.rice.kew.dto.BeforeProcessEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;


/**
 * 
 * This class is the public entry point by which workflow communicates status changes, 
 * level changes, and other useful changes.
 * 
 * Note that this class delegates all of these activities to the PostProcessorService, 
 * which does the actual work.  This is done to ensure proper transaction scoping, and 
 * to resolve some issues present otherwise.
 * 
 * Because of this, its important to understand that a transaction will be started at 
 * the PostProcessorService method call, so any work that needs to be done within the 
 * same transaction needs to happen inside that service implementation, rather than 
 * in here.
 * 
 */
public class KualiPostProcessor implements PostProcessorRemote {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);

    /**
     * 
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doRouteStatusChange(statusChangeEvent);
    }

    /**
     * 
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doActionTaken(org.kuali.rice.kew.dto.ActionTakenEventDTO)
     */
    public boolean doActionTaken(ActionTakenEventDTO event) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doActionTaken(event);
    }

    /**
     * 
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doDeleteRouteHeader(org.kuali.rice.kew.dto.DeleteEventDTO)
     */
    public boolean doDeleteRouteHeader(DeleteEventDTO event) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doDeleteRouteHeader(event);
    }

    /**
     * 
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doRouteLevelChange(org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO)
     */
    public boolean doRouteLevelChange(DocumentRouteLevelChangeDTO levelChangeEvent) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doRouteLevelChange(levelChangeEvent);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#beforeProcess(org.kuali.rice.kew.dto.BeforeProcessEventDTO)
     */
    public boolean beforeProcess(BeforeProcessEventDTO beforeProcessEvent) throws Exception {
        return KNSServiceLocator.getPostProcessorService().beforeProcess(beforeProcessEvent);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#afterProcess(org.kuali.rice.kew.dto.AfterProcessEventDTO)
     */
    public boolean afterProcess(AfterProcessEventDTO afterProcessEvent) throws Exception {
        return KNSServiceLocator.getPostProcessorService().afterProcess(afterProcessEvent);
    }
 }