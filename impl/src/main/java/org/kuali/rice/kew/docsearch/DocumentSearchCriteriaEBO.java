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
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;


/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface DocumentSearchCriteriaEBO extends ExternalizableBusinessObject {

	public String getAppDocId();

	public String getDocRouteStatus();

	public String getDocTitle();

	public String getDocTypeFullName();

	public String getInitiator();

	public String getRouteHeaderId();

	/**
	 * @return the dateCreated
	 */
	public java.sql.Timestamp getDateCreated();

}