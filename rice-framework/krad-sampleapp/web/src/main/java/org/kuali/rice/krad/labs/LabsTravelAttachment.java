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
package org.kuali.rice.krad.labs;


import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.bo.PersistableAttachment;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;
import org.kuali.rice.krad.uif.util.SessionTransient;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="trv_att_sample")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY,UifAutoCreateViewType.LOOKUP})
public class LabsTravelAttachment extends DataObjectBase implements PersistableAttachment, Serializable {

    @Id
    @Column(name="ATTACHMENT_ID",length=30)
    @Label("Id")
    @Description("Unique identifier for the attachment")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String id;

    @ManyToOne
    @JoinColumn(name = "ATT_GRP_NUM" ,insertable=false, updatable=false)
    LabsTravelAttachmentGroup labsTravelAttachmentGroup;

    @Id
    @Column(name = "ATT_GRP_NUM",length = 10)
    @Label("Travel Attachment Group Number")
    @NotNull
    private String travelAttachmentGroupNumber;

    @Column(name="DESCRIPTION",length=100)
    @Label("Description")
    @Description("Descriptor for the attachment")
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)})
    private String description;

    @Column(name="ATTACHMENT_FILENAME",length=300)
    @Label("File Name")
    @Description("File name of the attachment")
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY),
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA),
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT)})
    private String fileName;

    @Column(name="ATTACHMENT_FILE_CONTENT_TYPE",length=255)
    @Label("Content Type")
    @Description("Content Type of the attachment")
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT)})
    private String contentType;

    @Column(name="ATTACHMENT_FILE")
    @Label("Attachment Content")
    @Description("Content of the attachment")
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY),
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA),
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT)})
    private byte[] attachmentContent;

    @Label("Attachment File")
    @Description("File of the attachment")
    @SessionTransient
    private transient MultipartFile attachmentFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getAttachmentContent() {
        return attachmentContent;
    }

    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public MultipartFile getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(MultipartFile attachmentFile) {
        //Convert multiPartFile to fields that can be saved in db
        if(attachmentFile != null) {
            setContentType(attachmentFile.getContentType());
            setFileName(attachmentFile.getOriginalFilename());
            try {
                setAttachmentContent(attachmentFile.getBytes());

            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public LabsTravelAttachmentGroup getLabsTravelAttachmentGroup() {
        return labsTravelAttachmentGroup;
    }

    public void setLabsTravelAttachmentGroup(LabsTravelAttachmentGroup labsTravelAttachmentGroup) {
        this.labsTravelAttachmentGroup = labsTravelAttachmentGroup;
    }

    public String getTravelAttachmentGroupNumber() {
        return travelAttachmentGroupNumber;
    }

    public void setTravelAttachmentGroupNumber(String travelAttachmentGroupNumber) {
        this.travelAttachmentGroupNumber = travelAttachmentGroupNumber;
    }
}
