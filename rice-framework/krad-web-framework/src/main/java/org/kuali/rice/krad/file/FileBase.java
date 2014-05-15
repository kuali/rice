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

package org.kuali.rice.krad.file;

import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public class FileBase implements Serializable{

    private static final long serialVersionUID = 56328058337130228L;

    private String id;
    private String name;
    private String contentType;
    private Long size;
    private Date dateUploaded;
    private String url;
    private String deleteUrl;
    private String error;

    public FileBase() {
    }

    public FileBase(MultipartFile file) {
        this.name = file.getOriginalFilename();
        this.contentType = file.getContentType();
        this.size = file.getSize();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getDateUploadedFormatted() {
        if (dateUploaded != null) {
            return CoreApiServiceLocator.getDateTimeService().toDateTimeString(dateUploaded);
        }
        else{
            return "";
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeleteUrl() {
        return deleteUrl;
    }

    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
