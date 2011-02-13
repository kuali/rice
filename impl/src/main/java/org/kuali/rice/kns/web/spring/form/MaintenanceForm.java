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
package org.kuali.rice.kns.web.spring.form;

import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class MaintenanceForm extends DocumentFormBase {
    
    protected String businessObjectClassName;
    protected String maintenanceAction;
    

    @Override
    public MaintenanceDocument getDocument() {
        return this.getDocument();
    }
    
    public String getBusinessObjectClassName() {
        return this.businessObjectClassName;
    }

    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }

    public String getMaintenanceAction() {
        return this.maintenanceAction;
    }

    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }
    
}
