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
package org.kuali.core.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.Module;
import org.kuali.core.service.KualiModuleService;

public class ModuleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ModuleLookupableHelperServiceImpl.class);
    
    private KualiModuleService moduleService;
    
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocation((String) fieldValues.get(Constants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(Constants.DOC_FORM_KEY));

        return getSearchResults();
    }

    public List<Module> getSearchResults() {
        List<KualiModule> allModules = moduleService.getInstalledModules();
        List<Module> modules = new ArrayList<Module>();
        
        Module cfModule = new Module();
        cfModule.setModuleCode(Constants.CROSS_MODULE_CODE);
        cfModule.setModuleName(Constants.CROSS_MODULE_NAME);
        modules.add(cfModule);
        
        for ( KualiModule kualiModule : allModules ) {
            Module module = new Module();
            module.setModuleCode(kualiModule.getModuleCode());
            module.setModuleName(kualiModule.getModuleName());
            modules.add(module);
        }
        
        return modules;
    }
    public void setModuleService(KualiModuleService kualiModuleService) {
        this.moduleService = kualiModuleService;
    }

}
