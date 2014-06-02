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

import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Class used for interactions between the controller and form when using the multifile upload widget.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FileMetaBlob implements Serializable, FileMeta {

    private static final long serialVersionUID = 56328058337130228L;

    private String id;
    private String name;
    private String contentType;
    private Long size;
    private Date dateUploaded;
    private String url;

    private MultipartFile multipartFile;
    private Blob blob;

    public FileMetaBlob() {
    }

    public void init(MultipartFile multipartFile) throws Exception {
        this.name = multipartFile.getOriginalFilename();
        this.contentType = multipartFile.getContentType();
        this.size = multipartFile.getSize();
        this.multipartFile = multipartFile;
        blob = new SerialBlob(multipartFile.getBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getSize() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSizeFormatted() {
        DecimalFormat format = new DecimalFormat("0.#");

        if (size >= 1000000000) {
            return format.format((((double)size) / 1000000000)) + " GB";
        } else if (size >= 1000000) {
            return format.format((((double)size) / 1000000)) + " MB";
        } else {
            return format.format((((double)size) / 1000)) + " KB";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDateUploaded() {
        return dateUploaded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDateUploadedFormatted() {
        if (dateUploaded != null) {
            return CoreApiServiceLocator.getDateTimeService().toDateTimeString(dateUploaded);
        } else {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the MultipartFile that is populated by the controller during the upload process.
     *
     * @return the MultipartFile object
     */
    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    /**
     * @see #getMultipartFile()
     */
    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    /**
     * Get the serialized blob data representing the file
     *
     * @return the blob data
     */
    public Blob getBlob() {
        return blob;
    }

    /**
     * @see #getBlob()
     */
    public void setBlob(Blob blob) {
        this.blob = blob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FileBase{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", dateUploaded=" + dateUploaded +
                ", url='" + url + '\'' +
                '}';
    }
}
