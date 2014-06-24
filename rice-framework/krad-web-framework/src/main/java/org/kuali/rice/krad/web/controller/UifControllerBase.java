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
package org.kuali.rice.krad.web.controller;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.ControllerService;
import org.kuali.rice.krad.web.service.FileControllerService;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.NavigationControllerService;
import org.kuali.rice.krad.web.service.QueryControllerService;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Properties;

/**
 * Base controller class for views within the KRAD User Interface Framework.
 *
 * <p>Provides common methods such as navigation, collection handling, queries, and refresh calls.
 *
 * All subclass controller methods after processing should call one of the #getModelAndView methods to
 * setup the {@link org.kuali.rice.krad.uif.view.View} and return the {@link org.springframework.web.servlet.ModelAndView}
 * instance.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifControllerBase {

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private NavigationControllerService navigationControllerService;

    @Autowired
    private CollectionControllerService collectionControllerService;

    @Autowired
    private RefreshControllerService refreshControllerService;

    @Autowired
    private QueryControllerService queryControllerService;

    @Autowired
    private FileControllerService fileControllerService;

    @Autowired
    private ModelAndViewService modelAndViewService;

    /**
     * Creates form instance the will be used for the default model.
     *
     * @return UifFormBase form instance for holding model data
     */
    @ModelAttribute(value = UifConstants.DEFAULT_MODEL_NAME)
    protected UifFormBase initForm() {
        return createInitialForm();
    }

    /**
     * Invoked to create a new form instance for the request before it is passed to the Binder/BeanWrapper.
     *
     * @return UifFormBase instance that will be used for data binding and backing the view.
     */
    protected abstract UifFormBase createInitialForm();

    /**
     * Default method mapping for cases where the method to call is not passed, calls the start method.
     */
    @RequestMapping()
    public ModelAndView defaultMapping(UifFormBase form) {
        return start(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ControllerService#start(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=start")
    public ModelAndView start(UifFormBase form) {
        return getControllerService().start(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ControllerService#sessionTimeout(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=sessionTimeout")
    public ModelAndView sessionTimeout(UifFormBase form) {
        return getControllerService().sessionTimeout(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ControllerService#cancel(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=cancel")
    public ModelAndView cancel(UifFormBase form) {
        return getControllerService().cancel(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.NavigationControllerService#back(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=back")
    public ModelAndView back(UifFormBase form) {
        return getNavigationControllerService().back(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.NavigationControllerService#returnToPrevious(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=returnToPrevious")
    public ModelAndView returnToPrevious(UifFormBase form) {
        return getNavigationControllerService().returnToPrevious(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.NavigationControllerService#returnToHub(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=returnToHub")
    public ModelAndView returnToHub(UifFormBase form) {
        return getNavigationControllerService().returnToHub(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.FileControllerService#addFileUploadLine(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addFileUploadLine")
    public ModelAndView addFileUploadLine(UifFormBase form) {
        return getFileControllerService().addFileUploadLine(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.FileControllerService#deleteFileUploadLine(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deleteFileUploadLine")
    public ModelAndView deleteFileUploadLine(UifFormBase form) {
        return getFileControllerService().deleteFileUploadLine(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.FileControllerService#getFileFromLine(org.kuali.rice.krad.web.form.UifFormBase,
     * javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=getFileFromLine")
    public void getFileFromLine(UifFormBase form, HttpServletResponse response) {
        getFileControllerService().getFileFromLine(form, response);
    }

    /**
     * @see org.kuali.rice.krad.web.service.NavigationControllerService#navigate(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=navigate")
    public ModelAndView navigate(UifFormBase form) {
        return getNavigationControllerService().navigate(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.CollectionControllerService#addLine(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addLine")
    public ModelAndView addLine(UifFormBase form) {
        return getCollectionControllerService().addLine(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.CollectionControllerService#addBlankLine(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addBlankLine")
    public ModelAndView addBlankLine(UifFormBase form) {
        return getCollectionControllerService().addBlankLine(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.CollectionControllerService#saveLine(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveLine")
    public ModelAndView saveLine(UifFormBase form) {
        return getCollectionControllerService().saveLine(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.CollectionControllerService#deleteLine(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deleteLine")
    public ModelAndView deleteLine(final UifFormBase form) {
        return getCollectionControllerService().deleteLine(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.CollectionControllerService#retrieveCollectionPage(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=retrieveCollectionPage")
    public ModelAndView retrieveCollectionPage(UifFormBase form) {
        return getCollectionControllerService().retrieveCollectionPage(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.CollectionControllerService#tableJsonRetrieval(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=tableJsonRetrieval")
    public ModelAndView tableJsonRetrieval(UifFormBase form) {
        return getCollectionControllerService().tableJsonRetrieval(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.RefreshControllerService#refresh(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(params = "methodToCall=refresh")
    public ModelAndView refresh(UifFormBase form) {
        return getRefreshControllerService().refresh(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.QueryControllerService#performLookup(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=performLookup")
    public ModelAndView performLookup(UifFormBase form) {
        return getQueryControllerService().performLookup(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.QueryControllerService#performFieldSuggest(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=performFieldSuggest")
    @ResponseBody
    public AttributeQueryResult performFieldSuggest(UifFormBase form) {
        return getQueryControllerService().performFieldSuggest(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.QueryControllerService#performFieldQuery(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=performFieldQuery")
    @ResponseBody
    public AttributeQueryResult performFieldQuery(UifFormBase form) {
        return getQueryControllerService().performFieldQuery(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#checkForm(org.kuali.rice.krad.web.form.UifFormBase)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=checkForm")
    public ModelAndView checkForm(UifFormBase form) {
        return getModelAndViewService().checkForm(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#showDialog(java.lang.String, boolean,
     * org.kuali.rice.krad.web.form.UifFormBase)
     */
    protected ModelAndView showDialog(String dialogId, boolean confirmation, UifFormBase form) {
        return getModelAndViewService().showDialog(dialogId, confirmation, form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#performRedirect(org.kuali.rice.krad.web.form.UifFormBase,
     * java.lang.String, java.util.Properties)
     */
    protected ModelAndView performRedirect(UifFormBase form, String baseUrl, Properties urlParameters) {
        return getModelAndViewService().performRedirect(form, baseUrl, urlParameters);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#performRedirect(org.kuali.rice.krad.web.form.UifFormBase,
     * java.lang.String)
     */
    protected ModelAndView performRedirect(UifFormBase form, String redirectUrl) {
        return getModelAndViewService().performRedirect(form, redirectUrl);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#getMessageView(org.kuali.rice.krad.web.form.UifFormBase,
     * java.lang.String, java.lang.String)
     */
    protected ModelAndView getMessageView(UifFormBase form, String headerText, String messageText) {
        return getModelAndViewService().getMessageView(form, headerText, messageText);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#getModelAndView(org.kuali.rice.krad.web.form.UifFormBase)
     */
    protected ModelAndView getModelAndView(UifFormBase form) {
        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#getModelAndView(org.kuali.rice.krad.web.form.UifFormBase,
     * java.lang.String)
     */
    protected ModelAndView getModelAndView(UifFormBase form, String pageId) {
        return getModelAndViewService().getModelAndView(form, pageId);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#getModelAndView(org.kuali.rice.krad.web.form.UifFormBase,
     * java.util.Map<java.lang.String,java.lang.Object>)
     */
    protected ModelAndView getModelAndView(UifFormBase form, Map<String, Object> additionalViewAttributes) {
        return getModelAndViewService().getModelAndView(form, additionalViewAttributes);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#getModelAndViewWithInit(org.kuali.rice.krad.web.form.UifFormBase,
     * java.lang.String)
     */
    protected ModelAndView getModelAndViewWithInit(UifFormBase form, String viewId) {
        return getModelAndViewService().getModelAndViewWithInit(form, viewId);
    }

    /**
     * @see org.kuali.rice.krad.web.service.ModelAndViewService#getModelAndViewWithInit(org.kuali.rice.krad.web.form.UifFormBase,
     * java.lang.String, java.lang.String)
     */
    protected ModelAndView getModelAndViewWithInit(UifFormBase form, String viewId, String pageId) {
        return getModelAndViewService().getModelAndViewWithInit(form, viewId, pageId);
    }

    protected ControllerService getControllerService() {
        return controllerService;
    }

    public void setControllerService(ControllerService controllerService) {
        this.controllerService = controllerService;
    }

    protected NavigationControllerService getNavigationControllerService() {
        return navigationControllerService;
    }

    public void setNavigationControllerService(NavigationControllerService navigationControllerService) {
        this.navigationControllerService = navigationControllerService;
    }

    protected CollectionControllerService getCollectionControllerService() {
        return collectionControllerService;
    }

    public void setCollectionControllerService(CollectionControllerService collectionControllerService) {
        this.collectionControllerService = collectionControllerService;
    }

    protected RefreshControllerService getRefreshControllerService() {
        return refreshControllerService;
    }

    public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
        this.refreshControllerService = refreshControllerService;
    }

    protected QueryControllerService getQueryControllerService() {
        return queryControllerService;
    }

    public void setQueryControllerService(QueryControllerService queryControllerService) {
        this.queryControllerService = queryControllerService;
    }

    protected FileControllerService getFileControllerService() {
        return fileControllerService;
    }

    public void setFileControllerService(FileControllerService fileControllerService) {
        this.fileControllerService = fileControllerService;
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
