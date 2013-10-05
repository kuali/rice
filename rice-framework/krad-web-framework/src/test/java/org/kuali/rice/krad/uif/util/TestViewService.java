/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.validator.ValidationController;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.service.ViewTypeService;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Simple view service implementation for supporting framework level UIF unit tests. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestViewService implements ViewService, Lifecycle {

    private static Logger LOG = Logger.getLogger(TestViewService.class);

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
        dataDictionary.parseDataDictionaryConfigurationFiles(false);
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
     * @see org.kuali.rice.krad.uif.service.ViewService#buildView(org.kuali.rice.krad.uif.view.View, java.lang.Object, java.util.Map)
     */
    @Override
    public View buildView(View view, final Object model, final Map<String, String> parameters) {
        return ViewLifecycle.encapsulateLifecycle(view, new Runnable(){
            @Override
            public void run() {
                ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
                View view = viewLifecycle.getView();
                
                // populate view from request parameters
                viewLifecycle.populateViewFromRequestParameters(parameters);

                // backup view request parameters on form for recreating lost views (session timeout)
                ((UifFormBase) model).setViewRequestParameters(view.getViewRequestParameters());

                // run view lifecycle
                // invoke initialize phase on the views helper service
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing initialize phase for view: " + view.getId());
                }
                viewLifecycle.performInitialization(model);

                // do indexing                               
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processing indexing for view: " + view.getId());
                }
                view.index();

                // update status on view
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Updating view status to INITIALIZED for view: " + view.getId());
                }
                view.setViewStatus(ViewStatus.INITIALIZED);

                // Apply Model Phase
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing apply model phase for view: " + view.getId());
                }
                viewLifecycle.performApplyModel(model);

                // do indexing
                if (LOG.isInfoEnabled()) {
                    LOG.info("reindexing after apply model for view: " + view.getId());
                }
                view.index();

                // Finalize Phase
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing finalize phase for view: " + view.getId());
                }
                viewLifecycle.performFinalize(model);

                // do indexing
                if (LOG.isInfoEnabled()) {
                    LOG.info("processing final indexing for view: " + view.getId());
                }
                view.index();

                // update status on view
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Updating view status to FINAL for view: " + view.getId());
                }
                view.setViewStatus(ViewStatus.FINAL);

                // Validation of the page's beans
                if (CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsBoolean(
                        UifConstants.VALIDATE_VIEWS_ONBUILD)) {
                    ValidationController validator = new ValidationController(true, true, true, true, false);
                    Log tempLogger = LogFactory.getLog(TestViewService.class);
                    validator.validate(view, tempLogger, false);
                }
            }}).getView();
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewTypeService(org.kuali.rice.krad.uif.UifConstants.ViewType)
     */
    @Override
    public ViewTypeService getViewTypeService(ViewType viewType) {
        return null;
    }

}
