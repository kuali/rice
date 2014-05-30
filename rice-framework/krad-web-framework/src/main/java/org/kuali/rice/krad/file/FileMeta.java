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
package org.kuali.rice.krad.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * The file object interface used by the MultiFileUpload component(s), these component(s) expect objects which
 * implement this interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface FileMeta {
    /**
     * Init method called to initialize the FileMeta object
     *
     * @param multipartFile the file this object contains or represents
     * @throws Exception
     */
    public void init(MultipartFile multipartFile) throws Exception;

    /**
     * Unique id of the FileMeta object
     *
     * @return
     */
    public String getId();

    /**
     * @see #getId()
     */
    public void setId(String id);

    /**
     * The name of the file
     *
     * @return
     */
    public String getName();

    /**
     * @see #getName()
     */
    public void setName(String name);

    /**
     * The content type of the file
     *
     * @return
     */
    public String getContentType();

    /**
     * @see #getContentType()
     */
    public void setContentType(String contentType);

    /**
     * The size of the file (in bytes)
     *
     * @return
     */
    public Long getSize();

    /**
     * @see #getSize()
     */
    public void setSize(Long size);

    /**
     * The size of the file formatted into a more readable format
     *
     * @return
     */
    public String getSizeFormatted();

    /**
     * The date the file was uploaded
     *
     * @return
     */
    public Date getDateUploaded();

    /**
     * @see #getDateUploaded()
     */
    public void setDateUploaded(Date dateUploaded);

    /**
     * The file uploaded date formatted ina more readable String format
     *
     * @return
     */
    public String getDateUploadedFormatted();

    /**
     * The url to use to download the file
     *
     * @return the url of the file download
     */
    public String getUrl();

    /**
     * @see #getUrl()
     */
    public void setUrl(String url);

}
