/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.removereplace;

import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.postprocessor.DefaultPostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;

/**
 * PostProcessor implementation for the Remove/Replace Document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RemoveReplacePostProcessor extends DefaultPostProcessor {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RemoveReplacePostProcessor.class);

    @Override
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
	if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
	    LOG.info("Finalizing RemoveReplaceDocument with ID " + statusChangeEvent.getRouteHeaderId());
	    KEWServiceLocator.getRemoveReplaceDocumentService().finalize(statusChangeEvent.getRouteHeaderId());
	}
	return super.doRouteStatusChange(statusChangeEvent);
    }

}
