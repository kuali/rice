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
package org.kuali.rice.krad.web.form;

import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.uif.UifConstants.ViewType;

/**
 * Form class for <code>MaintenanceView</code> screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceForm extends DocumentFormBase {
	private static final long serialVersionUID = -5805825500852498048L;
	
	protected String dataObjectClassName;
	protected String maintenanceAction;

	public MaintenanceForm() {
		super();
		setViewTypeName(ViewType.MAINTENANCE);
	}

	@Override
	public MaintenanceDocument getDocument() {
		return (MaintenanceDocument) super.getDocument();
	}

	public String getDataObjectClassName() {
		return this.dataObjectClassName;
	}

	public void setDataObjectClassName(String dataObjectClassName) {
		this.dataObjectClassName = dataObjectClassName;
	}

	public String getMaintenanceAction() {
		return this.maintenanceAction;
	}

	public void setMaintenanceAction(String maintenanceAction) {
		this.maintenanceAction = maintenanceAction;
	}

}
