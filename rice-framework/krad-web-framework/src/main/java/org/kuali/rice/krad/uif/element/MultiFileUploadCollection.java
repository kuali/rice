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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.UrlInfo;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * A content element that will display the collection configured with a file upload button.
 *
 * <p>This is used to list objects that represent files.
 * The file will be uploaded to the methodToCall specified and the response is expected to be the refreshed collection.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "multiFileUploadCollection", parent = "Uif-MultiFileUploadCollection")
public class MultiFileUploadCollection extends ContentElementBase {
    private static final long serialVersionUID = 6324034860109503990L;

    private CollectionGroup collection;

    private String methodToCall;
    private UrlInfo url;

    private String addFilesButtonText;

    private String acceptFileTypes;
    private Integer maxFileSize;
    private Integer minFileSize;

    private String propertyPath;

    /**
     * This finalize method adds template options to the templateOptions property based on settings in the
     * parameters of this class
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        UifFormBase form = (UifFormBase) model;

        // Set plugin convenience setters into templateOptions to be consumed by the plugin
        if (url == null && methodToCall != null) {
            getTemplateOptions().put(UifConstants.MultiFileUploadOptions.URL,
                    "?methodToCall=" + methodToCall + "&formKey=" + form.getFormKey() + "&viewId=" + form.getViewId()
                            + "&bindingPath=" + collection.getBindingInfo().getBindingPath()
                            + "&updateComponentId=" + collection.getId() );
        } else if (url != null) {
            getTemplateOptions().put(UifConstants.MultiFileUploadOptions.URL, url.getHref());
        }

        if (acceptFileTypes != null) {
            getTemplateOptions().put(UifConstants.MultiFileUploadOptions.ACCEPT_FILE_TYPES, acceptFileTypes);
        }

        if (maxFileSize != null) {
            getTemplateOptions().put(UifConstants.MultiFileUploadOptions.MAX_SIZE, maxFileSize.toString());
        }

        if (minFileSize != null) {
            getTemplateOptions().put(UifConstants.MultiFileUploadOptions.MIN_SIZE, minFileSize.toString());
        }

        this.propertyPath = collection.getBindingInfo().getBindingPath();

        // Make collection inherit readOnly
        this.collection.setReadOnly(this.getReadOnly());
    }

    /**
     * The collection which holds the files uploaded
     *
     * @return the collection
     */
    @BeanTagAttribute
    public CollectionGroup getCollection() {
        return collection;
    }

    /**
     * @see MultiFileUploadCollection#getCollection()
     */
    public void setCollection(CollectionGroup collection) {
        this.collection = collection;
    }

    /**
     * The methodToCall for uploading files, this methodToCall must return the refreshed collection
     *
     * @return the methodToCall for file uploads
     */
    @BeanTagAttribute
    public String getMethodToCall() {
        return methodToCall;
    }

    /**
     * @see MultiFileUploadCollection#getMethodToCall()
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    /**
     * The url override for file uploads, this will be used instead of the methodToCall, if set, as the url to post
     * the file upload to
     *
     * @return the file upload url configuration override
     */
    @BeanTagAttribute
    public UrlInfo getUrl() {
        return url;
    }

    /**
     * @see MultiFileUploadCollection#getUrl()
     */
    public void setUrl(UrlInfo url) {
        this.url = url;
    }

    /**
     * The text to be used on the add files button
     *
     * @return the text of the add files button
     */
    @BeanTagAttribute
    public String getAddFilesButtonText() {
        return addFilesButtonText;
    }

    /**
     * @see MultiFileUploadCollection#getAddFilesButtonText()
     */
    public void setAddFilesButtonText(String addFilesButtonText) {
        this.addFilesButtonText = addFilesButtonText;
    }

    /**
     * A regex used to allow or disallow a certain file types for this file upload component
     *
     * @return the regex for file upload verification
     */
    @BeanTagAttribute
    public String getAcceptFileTypes() {
        return acceptFileTypes;
    }

    /**
     * @see MultiFileUploadCollection#getAcceptFileTypes()
     */
    public void setAcceptFileTypes(String acceptFileTypes) {
        this.acceptFileTypes = acceptFileTypes;
    }

    /**
     * The maximum file size to allow (in bytes) for a file upload
     *
     * @return maximum file size in bytes
     */
    @BeanTagAttribute
    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * @see MultiFileUploadCollection#getMaxFileSize()
     */
    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * The minimum file size needed (in bytes) for a file to be uploaded
     *
     * @return minimum file size in bytes
     */
    @BeanTagAttribute
    public Integer getMinFileSize() {
        return minFileSize;
    }

    /**
     * @see MultiFileUploadCollection#getMinFileSize()
     */
    public void setMinFileSize(Integer minFileSize) {
        this.minFileSize = minFileSize;
    }

    /**
     * The property path used for this component, which is the binding path of collection
     *
     * @return the property path
     */
    public String getPropertyPath() {
        return propertyPath;
    }
}
