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
package edu.iu.uis.eden.messaging.bam;

import org.apache.commons.lang.StringUtils;

/**
 * A parameter of a method invocation recorded by the BAM.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BAMParam {

	private Long bamParamId;
	private BAMTargetEntry bamTargetEntry;
	private String param;
	
	
	public BAMTargetEntry getBamTargetEntry() {
		return this.bamTargetEntry;
	}
	public void setBamTargetEntry(BAMTargetEntry bamTargetEntry) {
		this.bamTargetEntry = bamTargetEntry;
	}
	public Long getBamParamId() {
		return this.bamParamId;
	}
	public void setBamParamId(Long bamParamId) {
		this.bamParamId = bamParamId;
	}
	public String getParam() {
		return this.param;
	}
	public void setParam(String paramToString) {
		if (StringUtils.isEmpty(paramToString)) {
			paramToString = "<null>";//oracle blows null constraint on empty strings
		}
		this.param = paramToString;
	}
}