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
package org.kuali.rice.krad.web.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.file.FileMeta;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.FileControllerService;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of the file controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FileControllerServiceImpl implements FileControllerService {

    protected static final String DELETE_FILE_UPLOAD_LINE_DIALOG = "DialogGroup-DeleteFileUploadLine";

    private CollectionControllerService collectionControllerService;
    private ModelAndViewService modelAndViewService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView addFileUploadLine(final UifFormBase form) {
        form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey());
        form.setAjaxRequest(true);

        MultipartHttpServletRequest request = (MultipartHttpServletRequest) form.getRequest();

        final String collectionId = request.getParameter(UifParameters.UPDATE_COMPONENT_ID);
        final String bindingPath = request.getParameter(UifConstants.PostMetadata.BINDING_PATH);

        Class<?> collectionObjectClass = (Class<?>) form.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.COLL_OBJECT_CLASS);

        Iterator<String> fileNamesItr = request.getFileNames();

        while (fileNamesItr.hasNext()) {
            String propertyPath = fileNamesItr.next();

            MultipartFile uploadedFile = request.getFile(propertyPath);

            final FileMeta fileObject = (FileMeta) KRADUtils.createNewObjectFromClass(collectionObjectClass);
            try {
                fileObject.init(uploadedFile);
            } catch (Exception e) {
                throw new RuntimeException("Unable to initialize new file object", e);
            }

            String id = UUID.randomUUID().toString() + "_" + uploadedFile.getName();
            fileObject.setId(id);

            fileObject.setDateUploaded(new Date());

            fileObject.setUrl("?methodToCall=getFileFromLine&formKey="
                    + form.getFormKey()
                    + "&fileName="
                    + fileObject.getName()
                    + "&propertyPath="
                    + propertyPath);

            ViewLifecycle.encapsulateLifecycle(form.getView(), form, form.getViewPostMetadata(), null, request,
                    new Runnable() {
                        @Override
                        public void run() {
                            ViewLifecycle.getHelper().processAndAddLineObject(form, fileObject, collectionId,
                                    bindingPath);
                        }
                    });
        }

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView deleteFileUploadLine(UifFormBase form) {
        DialogResponse deleteFileUploadLineDialogResponse = form.getDialogResponse(DELETE_FILE_UPLOAD_LINE_DIALOG);

        if (deleteFileUploadLineDialogResponse == null) {
            // no confirmation dialog found, so create one on the form and return it
            return getModelAndViewService().showDialog(DELETE_FILE_UPLOAD_LINE_DIALOG, true, form);
        }

        return getCollectionControllerService().deleteLine(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFileFromLine(UifFormBase form, HttpServletResponse response) {
        HttpServletRequest request = form.getRequest();

        String selectedCollectionPath = request.getParameter("propertyPath");

        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection was not set for delete line action, cannot delete line");
        }

        String selectedLine = request.getParameter(UifParameters.SELECTED_LINE_INDEX);
        int selectedLineIndex;
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        } else {
            selectedLineIndex = -1;
        }

        if (selectedLineIndex == -1) {
            throw new RuntimeException("Selected line index was not set for delete line action, cannot delete line");
        }

        Collection<FileMeta> collection = ObjectPropertyUtils.getPropertyValue(form, selectedCollectionPath);

        if (collection instanceof List) {
            FileMeta fileLine = ((List<FileMeta>) collection).get(selectedLineIndex);
            sendFileFromLineResponse(form, response, (List<FileMeta>) collection, fileLine);
        }
    }

    /**
     * Hook controller method to send a response back by using response.flushBuffer() using request/collection/fileLine
     * information provided.
     *
     * <p>A sample implementation may look like:
     * <code> <pre>
     * if (fileLine instanceof FileMetaBlob) {
     * InputStream is = ((FileMetaBlob) fileLine).getBlob().getBinaryStream();
     * response.setContentType("application/force-download");
     * response.setHeader("Content-Disposition", "attachment; filename=" + fileLine.getName());
     *
     * // copy it to response's OutputStream
     * FileCopyUtils.copy(is, response.getOutputStream());
     *
     * response.flushBuffer();
     * }
     * </pre></code></p>
     *
     * @param form form instance containing the file request data
     * @param response Http response object for streaming back the file contents
     * @param collection collection the file object belongs to
     * @param fileLine the particular file line instance the contents should be sent back for
     */
    protected void sendFileFromLineResponse(UifFormBase form, HttpServletResponse response, List<FileMeta> collection,
            FileMeta fileLine) {
        // empty method for overrides
    }

    protected CollectionControllerService getCollectionControllerService() {
        return collectionControllerService;
    }

    public void setCollectionControllerService(CollectionControllerService collectionControllerService) {
        this.collectionControllerService = collectionControllerService;
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
