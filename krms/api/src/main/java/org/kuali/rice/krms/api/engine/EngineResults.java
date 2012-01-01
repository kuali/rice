/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krms.api.engine;

import java.util.List;

public interface EngineResults {

	// TODO - need to determine what goes here...
	public ResultEvent getResultEvent(int index);
	public List<ResultEvent> getAllResults();
	public List<ResultEvent> getResultsOfType(String type);
	public Object getAttribute(String key);
	public void setAttribute(String key, Object attr);
	
	public void addResult(ResultEvent result);
}
