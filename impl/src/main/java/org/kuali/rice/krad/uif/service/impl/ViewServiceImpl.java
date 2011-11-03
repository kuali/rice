/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.service.ViewTypeService;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Implementation of <code>ViewService</code>
 *
 * <p>
 * Provides methods for retrieving View instances and carrying out the View
 * lifecycle methods. Interacts with the configured <code>ViewHelperService</code>
 * during the view lifecycle
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewServiceImpl implements ViewService {
    private static final Logger LOG = Logger.getLogger(ViewServiceImpl.class);

    private DataDictionaryService dataDictionaryService;

    // TODO: remove once we can get beans by type from spring
    private List<ViewTypeService> viewTypeServices;

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewById(java.lang.String)
     */
    public View getViewById(String viewId) {
        LOG.debug("retrieving view instance for id: " + viewId);

        View view = dataDictionaryService.getViewById(viewId);
        if (view == null) {
            LOG.error("View not found for id: " + viewId);
            throw new RuntimeException("View not found for id: " + viewId);
        }

        LOG.debug("Updating view status to CREATED for view: " + view.getId());
        view.setViewStatus(ViewStatus.CREATED);

        return view;
    }

    /**
     * Retrieves the <code>ViewTypeService</code> for the given view type, then builds up the index based
     * on the supported view type parameters and queries the dictionary service to retrieve the view
     * based on its type and index
     *
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewByType(org.kuali.rice.krad.uif.UifConstants.ViewType,
     *      java.util.Map<java.lang.String,java.lang.String>)
     */
    public View getViewByType(ViewType viewType, Map<String, String> parameters) {
        ViewTypeService typeService = getViewTypeService(viewType);
        if (typeService == null) {
            throw new RuntimeException("Unable to find view type service for view type name: " + viewType);
        }

        Map<String, String> typeParameters = typeService.getParametersFromRequest(parameters);

        Map<String, String> indexKey = new HashMap<String, String>();
        for (Map.Entry<String, String> parameter : typeParameters.entrySet()) {
            indexKey.put(parameter.getKey(), parameter.getValue());
        }

        View view = dataDictionaryService.getViewByTypeIndex(viewType, indexKey);
        if (view == null) {
            LOG.error("View not found for type: " + viewType);
            throw new RuntimeException("View not found for type: " + viewType);
        }

        LOG.debug("Updating view status to CREATED for view: " + view.getId());
        view.setViewStatus(ViewStatus.CREATED);

        return view;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#buildView(org.kuali.rice.krad.uif.view.View, java.lang.Object,
     * java.util.Map<java.lang.String,java.lang.String>)
     */
    public void buildView(View view, Object model, Map<String, String> parameters) {
        // get the configured helper service for the view
        ViewHelperService helperService = view.getViewHelperService();

        // populate view from request parameters
        helperService.populateViewFromRequestParameters(view, parameters);

        // backup view request parameters on form for recreating lost views (session timeout)
        ((UifFormBase) model).setViewRequestParameters(view.getViewRequestParameters());

        // run view lifecycle
        performViewLifecycle(view, model, parameters);
    }

    /**
     * Initializes a newly created <code>View</code> instance. Each component of the tree is invoked
     * to perform setup based on its configuration. In addition helper service methods are invoked to
     * perform custom initialization
     *
     * @param view - view instance to initialize
     * @param model - object instance containing the view data
     * @param parameters - Map of key values pairs that provide configuration for the <code>View</code>, this
     * is generally comes from the request and can be the request parameter Map itself. Any parameters
     * not valid for the View will be filtered out
     */
    protected void performViewLifecycle(View view, Object model, Map<String, String> parameters) {
        // get the configured helper service for the view
        ViewHelperService helperService = view.getViewHelperService();

        // invoke initialize phase on the views helper service
        LOG.info("performing initialize phase for view: " + view.getId());
        helperService.performInitialization(view, model);

        // do indexing
        LOG.debug("processing indexing for view: " + view.getId());
        view.index();

        // update status on view
        LOG.debug("Updating view status to INITIALIZED for view: " + view.getId());
        view.setViewStatus(ViewStatus.INITIALIZED);

        // Apply Model Phase
        LOG.info("performing apply model phase for view: " + view.getId());
        helperService.performApplyModel(view, model);

        // do indexing
        LOG.info("reindexing after apply model for view: " + view.getId());
        view.index();

        // Finalize Phase
        LOG.info("performing finalize phase for view: " + view.getId());
        helperService.performFinalize(view, model);

        // do indexing
        LOG.info("processing final indexing for view: " + view.getId());
        view.index();

        // update status on view
        LOG.debug("Updating view status to FINAL for view: " + view.getId());
        view.setViewStatus(ViewStatus.FINAL);
    }

    public ViewTypeService getViewTypeService(UifConstants.ViewType viewType) {
        if (viewTypeServices != null) {
            for (ViewTypeService typeService : viewTypeServices) {
                if (viewType.equals(typeService.getViewTypeName())) {
                    return typeService;
                }
            }
        }

        return null;
    }

    public List<ViewTypeService> getViewTypeServices() {
        return this.viewTypeServices;
    }

    public void setViewTypeServices(List<ViewTypeService> viewTypeServices) {
        this.viewTypeServices = viewTypeServices;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
