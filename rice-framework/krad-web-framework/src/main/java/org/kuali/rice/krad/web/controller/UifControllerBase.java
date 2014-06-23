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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.file.FileMeta;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.ControllerService;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.NavigationControllerService;
import org.kuali.rice.krad.web.service.QueryControllerService;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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
    protected static final String DELETE_FILE_UPLOAD_LINE_DIALOG = "DialogGroup-DeleteFileUploadLine";

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
     * Called by the multiFile upload element to add a file object to the collection it controls.
     */
    @MethodAccessible
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addFileUploadLine")
    public ModelAndView addFileUploadLine(@ModelAttribute("KualiForm") final UifFormBase uifForm, BindingResult result,
            MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        uifForm.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey());
        uifForm.setAjaxRequest(true);

        final String collectionId = request.getParameter(UifParameters.UPDATE_COMPONENT_ID);
        final String bindingPath = request.getParameter(UifConstants.PostMetadata.BINDING_PATH);

        Class<?> collectionObjectClass = (Class<?>) uifForm.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.COLL_OBJECT_CLASS);

        Iterator<String> fileNamesItr = request.getFileNames();

        while (fileNamesItr.hasNext()) {
            String propertyPath = fileNamesItr.next();
            MultipartFile uploadedFile = request.getFile(propertyPath);
            final FileMeta fileObject = (FileMeta) KRADUtils.createNewObjectFromClass(collectionObjectClass);
            fileObject.init(uploadedFile);

            String id = UUID.randomUUID().toString() + "_" + uploadedFile.getName();

            fileObject.setId(id);
            fileObject.setDateUploaded(new Date());
            fileObject.setUrl(
                    "?methodToCall=getFileFromLine&formKey=" + uifForm.getFormKey() + "&fileName=" + fileObject
                            .getName() + "&propertyPath=" + propertyPath);

            ViewLifecycle.encapsulateLifecycle(uifForm.getView(), uifForm, uifForm.getViewPostMetadata(), null, request,
                    new Runnable() {
                        @Override
                        public void run() {
                            ViewLifecycle.getHelper().processAndAddLineObject(uifForm, fileObject, collectionId,
                                    bindingPath);
                        }
                    });
        }

        return refresh(uifForm);
    }

    /**
     * Called by the multiFile upload widget to delete a file; Inform the model of file to delete.
     */
    @MethodAccessible
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deleteFileUploadLine")
    public ModelAndView deleteFileUploadLine(@ModelAttribute("KualiForm") final UifFormBase uifForm,
            BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DialogResponse deleteFileUploadLineDialogResponse = uifForm.getDialogResponse(DELETE_FILE_UPLOAD_LINE_DIALOG);

        if (deleteFileUploadLineDialogResponse == null) {
            // no confirmation dialog found, so create one on the form and return it
            return showDialog(DELETE_FILE_UPLOAD_LINE_DIALOG, true, uifForm);
        }

        // Empty hook method for deleting a line in a collection representing a set of files
        return deleteLine(uifForm);
    }

    /**
     * Called by the multiFile upload widget to get the file contents for a file upload line.
     */
    @MethodAccessible
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=getFileFromLine")
    public void getFileFromLine(@ModelAttribute("KualiForm") final UifFormBase uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String selectedCollectionPath = request.getParameter("propertyPath");

        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection was not set for delete line action, cannot delete line");
        }

        String selectedLine = request.getParameter(UifParameters.SELECTED_LINE_INDEX);
        final int selectedLineIndex;
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        } else {
            selectedLineIndex = -1;
        }

        if (selectedLineIndex == -1) {
            throw new RuntimeException("Selected line index was not set for delete line action, cannot delete line");
        }

        Collection<FileMeta> collection = ObjectPropertyUtils.getPropertyValue(uifForm, selectedCollectionPath);

        if (collection instanceof List) {
            FileMeta fileLine = ((List<FileMeta>) collection).get(selectedLineIndex);
            sendFileFromLineResponse(uifForm, request, response, (List<FileMeta>) collection, fileLine);
        }
    }

    /**
     * Hook controller method to send a response back by using response.flushBuffer() using request/collection/fileLine
     * information provided
     *
     *  <p>
        A sample implementation may look like:
        <code> <pre>
        if (fileLine instanceof FileMetaBlob) {
             InputStream is = ((FileMetaBlob) fileLine).getBlob().getBinaryStream();
             response.setContentType("application/force-download");
             response.setHeader("Content-Disposition", "attachment; filename=" + fileLine.getName());

             // copy it to response's OutputStream
             FileCopyUtils.copy(is, response.getOutputStream());

             response.flushBuffer();
         }
        </pre></code>
     *  </p>
     */
    public void sendFileFromLineResponse(UifFormBase uifForm, HttpServletRequest request, HttpServletResponse response,
            List<FileMeta> collection, FileMeta fileLine) throws Exception {
        // empty method for overrides
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

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
