/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.container;

import org.kuali.rice.kns.uif.UifConstants.ViewType;

/**
 * View type for Maintenance documents
 * 
 * <p>
 * Supports primary display for a new maintenance record, in which case the
 * fields are display for populating the new record, and an edit maintenance
 * record, which is a comparison view with the old record read-only on the left
 * side and the new record (changed record) on the right side
 * </p>
 * 
 * <p>
 * The <code>MaintenanceView</code> provides the interface for the maintenance
 * framework. It works with the <code>Maintainable</code> service and
 * maintenance controller.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceView extends DocumentView {
	private static final long serialVersionUID = -3382802967703882341L;

	private Class<?> objectClassName;

	// TODO: figure out what this is used for
	private boolean allowsRecordDeletion = false;

	public MaintenanceView() {
		super();
		
		setViewTypeName(ViewType.MAINTENANCE);
	}

	public Class<?> getObjectClassName() {
		return this.objectClassName;
	}

	public void setObjectClassName(Class<?> objectClassName) {
		this.objectClassName = objectClassName;
	}

	public boolean isAllowsRecordDeletion() {
		return this.allowsRecordDeletion;
	}

	public void setAllowsRecordDeletion(boolean allowsRecordDeletion) {
		this.allowsRecordDeletion = allowsRecordDeletion;
	}

}
