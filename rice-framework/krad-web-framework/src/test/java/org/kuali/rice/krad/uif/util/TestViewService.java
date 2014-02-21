/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataDictionaryException;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.service.ViewTypeService;
import org.kuali.rice.krad.uif.view.View;

/**
 * Simple view service implementation for supporting framework level UIF unit tests. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestViewService implements ViewService, Lifecycle {
    
    private static final Logger LOG = Logger.getLogger(TestViewService.class);

    private boolean running;
    private DataDictionary dataDictionary;

    /**
     * @return the dataDictionary
     */
    public DataDictionary getDataDictionary() {
        return this.dataDictionary;
    }

    /**
     * @param dataDictionary the dataDictionary to set
     */
    public void setDataDictionary(DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void start() {
        try {
            dataDictionary.parseDataDictionaryConfigurationFiles(false);
        } catch (DataDictionaryException e) {
            LOG.error("Error initializing data dictionary", e);
        }
        running = true;
    }

    /**
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop() {
        running = false;
    }

    /**
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isStarted() {
        return running;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewById(java.lang.String)
     */
    @Override
    public View getViewById(String viewId) {
        return dataDictionary.getViewById(viewId);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewByType(org.kuali.rice.krad.uif.UifConstants.ViewType, java.util.Map)
     */
    @Override
    public View getViewByType(ViewType viewType, Map<String, String> parameters) {
        return dataDictionary.getViewByTypeIndex(viewType, parameters);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewIdForViewType(org.kuali.rice.krad.uif.UifConstants.ViewType, java.util.Map)
     */
    @Override
    public String getViewIdForViewType(ViewType viewType, Map<String, String> parameters) {
        return dataDictionary.getViewIdByTypeIndex(viewType, parameters);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewTypeService(org.kuali.rice.krad.uif.UifConstants.ViewType)
     */
    @Override
    public ViewTypeService getViewTypeService(ViewType viewType) {
        return null;
    }

}
