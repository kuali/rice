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
package org.kuali.core.service.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.RiceConstants;
import org.kuali.core.UserSession;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.PostProcessorService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is the postProcessor for the Kuali application, and it is responsible for plumbing events up to documents using the
 * built into the document methods for handling route status and other routing changes that take place asyncronously and potentially
 * on a different server.
 */
@Transactional
public class PostProcessorServiceImpl implements PostProcessorService {

    private static Logger LOG = Logger.getLogger(PostProcessorServiceImpl.class);

    private DocumentService documentService;
    private DateTimeService dateTimeService;

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteStatusChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws RemoteException {
        try {
            LOG.info(new StringBuffer("started handling route status change from ").append(statusChangeEvent.getOldRouteStatus()).append(" to ").append(statusChangeEvent.getNewRouteStatus()).append(" for document ").append(statusChangeEvent.getRouteHeaderId()));
            establishGlobalVariables();
            Document document = documentService.getByDocumentHeaderId(statusChangeEvent.getRouteHeaderId().toString());
            if (document == null) {
                if (!EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(statusChangeEvent.getNewRouteStatus())) {
                    throw new RuntimeException("unable to load document " + statusChangeEvent.getRouteHeaderId());
                }
            }
            else {
                // PLEASE READ BEFORE YOU MODIFY:
                // we dont want to update the document on a Save, as this will cause an
                // OptimisticLockException in many cases, because the DB versionNumber will be
                // incremented one higher than the document in the browser, so when the user then
                // hits Submit or Save again, the versionNumbers are out of synch, and the
                // OptimisticLockException is thrown. This is not the optimal solution, and will
                // be a problem anytime where the user can continue to edit the document after a
                // workflow state change, without reloading the form.
                if (!document.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
                    document.handleRouteStatusChange();
                    if (document.getDocumentHeader().getWorkflowDocument().stateIsCanceled() || document.getDocumentHeader().getWorkflowDocument().stateIsDisapproved() || document.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                        document.getDocumentHeader().setDocumentFinalDate(dateTimeService.getCurrentSqlDate());
                    }
                    documentService.updateDocument(document);
                }
                document.doRouteStatusChange(statusChangeEvent);
            }
            LOG.info(new StringBuffer("finished handling route status change from ").append(statusChangeEvent.getOldRouteStatus()).append(" to ").append(statusChangeEvent.getNewRouteStatus()).append(" for document ").append(statusChangeEvent.getRouteHeaderId()));
        }
        catch (Exception e) {
            logAndRethrow("route status", e);
        }
        return true;
    }

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    public boolean doRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) throws RemoteException {
        // on route level change we'll serialize the XML for the document. we
        // are doing this here cause it's a heavy hitter, and we
        // want to avoid the user waiting for this during sync processing
        try {
            LOG.debug(new StringBuffer("started handling route level change from ").append(levelChangeEvent.getOldRouteLevel()).append(" to ").append(levelChangeEvent.getNewRouteLevel()).append(" for document ").append(levelChangeEvent.getRouteHeaderId()));
            establishGlobalVariables();
            Document document = documentService.getByDocumentHeaderId(levelChangeEvent.getRouteHeaderId().toString());
            if (document == null) {
                throw new RuntimeException("unable to load document " + levelChangeEvent.getRouteHeaderId());
            }
            document.populateDocumentForRouting();
            document.handleRouteLevelChange(levelChangeEvent);
            document.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            LOG.debug(new StringBuffer("finished handling route level change from ").append(levelChangeEvent.getOldRouteLevel()).append(" to ").append(levelChangeEvent.getNewRouteLevel()).append(" for document ").append(levelChangeEvent.getRouteHeaderId()));
        }
        catch (Exception e) {
            logAndRethrow("route level", e);
        }
        return true;
    }

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doDeleteRouteHeader(edu.iu.uis.eden.clientapp.vo.DeleteEventVO)
     */
    public boolean doDeleteRouteHeader(DeleteEventVO event) throws RemoteException {
        return true;
    }

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    public boolean doActionTaken(ActionTakenEventVO event) throws RemoteException {
        try {
            LOG.debug(new StringBuffer("started doing action taken for action taken code").append(event.getActionTaken().getActionTaken()).append(" for document ").append(event.getRouteHeaderId()));
            establishGlobalVariables();
            Document document = documentService.getByDocumentHeaderId(event.getRouteHeaderId().toString());
            if (ObjectUtils.isNull(document)) {
                // only throw an exception if we are not cancelling
                if (!EdenConstants.ACTION_TAKEN_CANCELED.equals(event.getActionTaken())) {
                    LOG.warn("doActionTaken() Unable to load document with id " + event.getRouteHeaderId() + 
                            " using action taken code '" + EdenConstants.ACTION_TAKEN_CD.get(event.getActionTaken().getActionTaken()));
//                    throw new RuntimeException("unable to load document " + event.getRouteHeaderId());
                }
            } else {
                document.doActionTaken(event);
                LOG.debug(new StringBuffer("finished doing action taken for action taken code").append(event.getActionTaken().getActionTaken()).append(" for document ").append(event.getRouteHeaderId()));
            }
        }
        catch (Exception e) {
            logAndRethrow("do action taken", e);
        }
        return true;
    }

    private void logAndRethrow(String changeType, Exception e) throws RuntimeException {
        LOG.error("caught exception while handling " + changeType + " change", e);
        logOptimisticDetails(5, e);

        throw new RuntimeException("post processor caught exception while handling " + changeType + " change: " + e.getMessage(), e);
    }

    /**
     * Logs further details of OptimisticLockExceptions, using the given depth value to limit recursion Just In Case
     *
     * @param depth
     * @param t
     */
    private void logOptimisticDetails(int depth, Throwable t) {
        if ((depth > 0) && (t != null)) {
            if (t instanceof OptimisticLockException) {
                OptimisticLockException o = (OptimisticLockException) t;

                LOG.error("source of OptimisticLockException = " + o.getSourceObject().getClass().getName() + " ::= " + o.getSourceObject());
            }
            else {
                Throwable cause = t.getCause();
                if (cause != t) {
                    logOptimisticDetails(--depth, cause);
                }
            }
        }
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public final void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public final void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Establishes the UserSession if one does not already exist.
     */
    protected void establishGlobalVariables() throws WorkflowException, UserNotFoundException {
        if (GlobalVariables.getUserSession() == null) {
            GlobalVariables.setUserSession(new UserSession(RiceConstants.SYSTEM_USER));
        }
        GlobalVariables.clear();
    }

}
