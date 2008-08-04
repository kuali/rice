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

/**
 * Service responsible for polling a location for
 * xml documents to ingest.
 * Pipeline:<br/>
 * <ol>
 *   <li>Acquisition: <code>XmlPollerService</code>, <i>Struts upload action</i></li>
 *   <li>Ingestion: XmlIngesterService</li>
 *   <li>Digestion: XmlDigesterService</li>
 * </ol>
 * @see edu.iu.uis.eden.batch.XmlIngesterService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface XmlPollerService extends Runnable {

	public static final String XML_ROOT_LOC_PROP = "data.xml.root.location";
	public static final String XML_PENDING_LOC_PROP = "data.xml.pending.location";
	public static final String XML_COMPLETED_LOC_PROP = "data.xml.completed.location";
	public static final String XML_PROBLEM_LOC_PROP = "data.xml.problem.location";
	public static final String XML_POLL_INTERVAL_PROP = "data.xml.pollIntervalSecs";
	public static final String XML_INIT_DELAY_SECS_PROP = "initialDelaySecs";

    int getPollIntervalSecs();
    int getInitialDelaySecs();

}