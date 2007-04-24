/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.KualiModule;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class returns list of approved document indicator value pairs.
 */
public class InstalledModulesValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        KualiModuleService kms = KNSServiceLocator.getKualiModuleService();
        for ( KualiModule module : kms.getInstalledModules() ) {
            keyValues.add(new KeyLabelPair(module.getModuleCode(), module.getModuleCode() + " - " + module.getModuleName()));            
        }

        return keyValues;
    }

}