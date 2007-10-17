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
package edu.iu.uis.eden.batch;

import java.util.Collection;
import java.util.List;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * A service which is responsible for iterating through
 * a list of xml documents or collections of xml documents
 * and ingesting them into the workflow engine.
 * Pipeline:<br/>
 * <ol>
 *   <li>Acquisition: <code>XmlPollerService</code>, <i>Struts upload action</i></li>
 *   <li>Ingestion: XmlIngesterService</li>
 *   <li>Digestion: XmlDigesterService</li>
 * </ol>
 * @see edu.iu.uis.eden.batch.XmlDigesterService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface XmlIngesterService {
    /**
     * Ingest a list of xml doc collections
     * @param xmlDocCollections the list of {@link XmlDocCollection}s
     * @return failed xml doc collections
     */
    Collection ingest(List<XmlDocCollection> xmlDocCollections) throws Exception;
    
    /**
     * Ingest a list of xml doc collections, but it routing is supported by any of the services ultimately being called 
     * route the xml data with user being the initiator of any documents created.
     * @param xmlDocCollections
     * @param user
     * @return failed xml doc collections
     */
    Collection ingest(List<XmlDocCollection> xmlDocCollections, WorkflowUser user) throws Exception;
}