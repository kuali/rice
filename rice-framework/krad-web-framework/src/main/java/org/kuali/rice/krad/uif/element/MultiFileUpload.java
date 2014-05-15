/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.UrlInfo;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.List;

/**
 *
 */
public class MultiFileUpload extends ContentElementBase {
    private static final long serialVersionUID = -5701956559393771176L;

    private String methodToCall;
    private UrlInfo url;

    private String propertyPath;

    private String addFilesButtonText;
    private String uploadAllButtonText;
    private String cancelAllButtonText;

    private String acceptFileTypes;
    private Integer maxFileSize;
    private Integer minFileSize;
    private Integer maxNumberOfFiles;

    private String fileUploadRowTemplate;
    private String fileDownloadRowTemplate;
    private List<String> fileTableColumnNames;

    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        UifFormBase form = (UifFormBase) model;

        // Set plugin convenience setters into templateOptions to be consumed by the plugin
        if (url == null && methodToCall != null) {
            templateOptions.put("url", "?methodToCall=" + methodToCall + "&formKey=" + form.getFormKey()
                    + "&viewId=" + form.getViewId());
        } else if (url != null) {
            templateOptions.put("url", url.getHref());
        }

        if (acceptFileTypes != null) {
            templateOptions.put("acceptFileTypes", acceptFileTypes);
        }

        if (maxFileSize != null) {
            templateOptions.put("maxFileSize", maxFileSize.toString());
        }

        if (minFileSize != null) {
            templateOptions.put("minFileSize", minFileSize.toString());
        }

        if (maxNumberOfFiles != null) {
            templateOptions.put("maxNumberOfFiles", maxNumberOfFiles.toString());
        }

        templateOptions.put("uploadTemplateId", this.getId() + "_uploadTemplate");
        templateOptions.put("downloadTemplateId", this.getId() + "_downloadTemplate");


    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public UrlInfo getUrl() {
        return url;
    }

    public void setUrl(UrlInfo url) {
        this.url = url;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getAddFilesButtonText() {
        return addFilesButtonText;
    }

    public void setAddFilesButtonText(String addFilesButtonText) {
        this.addFilesButtonText = addFilesButtonText;
    }

    public String getUploadAllButtonText() {
        return uploadAllButtonText;
    }

    public void setUploadAllButtonText(String uploadAllButtonText) {
        this.uploadAllButtonText = uploadAllButtonText;
    }

    public String getCancelAllButtonText() {
        return cancelAllButtonText;
    }

    public void setCancelAllButtonText(String cancelAllButtonText) {
        this.cancelAllButtonText = cancelAllButtonText;
    }

    public String getAcceptFileTypes() {
        return acceptFileTypes;
    }

    public void setAcceptFileTypes(String acceptFileTypes) {
        this.acceptFileTypes = acceptFileTypes;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Integer getMinFileSize() {
        return minFileSize;
    }

    public void setMinFileSize(Integer minFileSize) {
        this.minFileSize = minFileSize;
    }

    public Integer getMaxNumberOfFiles() {
        return maxNumberOfFiles;
    }

    public void setMaxNumberOfFiles(Integer maxNumberOfFiles) {
        this.maxNumberOfFiles = maxNumberOfFiles;
    }

    public String getFileUploadRowTemplate() {
        return fileUploadRowTemplate;
    }

    public void setFileUploadRowTemplate(String fileUploadRowTemplate) {
        this.fileUploadRowTemplate = fileUploadRowTemplate;
    }

    public String getFileDownloadRowTemplate() {
        return fileDownloadRowTemplate;
    }

    public void setFileDownloadRowTemplate(String fileDownloadRowTemplate) {
        this.fileDownloadRowTemplate = fileDownloadRowTemplate;
    }

    public List<String> getFileTableColumnNames() {
        return fileTableColumnNames;
    }

    public void setFileTableColumnNames(List<String> fileTableColumnNames) {
        this.fileTableColumnNames = fileTableColumnNames;
    }
}
