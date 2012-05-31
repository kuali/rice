/*
 * Copyright 2007-2011 The Kuali Foundation
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
package edu.sampleu.travel.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.struts.upload.FormFile;
import org.kuali.rice.kns.bo.PersistableAttachment;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TRV_ATTACH_SAMPLE_T")
public class AttachmentSample extends PersistableBusinessObjectBase implements PersistableAttachment {
    
	@Id
	@Column(name="attachment_id")
    private String id;
	@Column(name="description")
    private String description;
    @Column(name="attachment_filename")
    private String fileName;
    @Column(name="attachment_file_content_type")
    private String contentType;
    @Column(name="attachment_file")
    private byte[] attachmentContent;
    
    private transient FormFile attachmentFile;

    
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="attachment_id", insertable=false, updatable=false)
    private List<MultiAttachmentSample> multiAttachment;
    
    public AttachmentSample() {
        this.multiAttachment = new ArrayList<MultiAttachmentSample>();
    }
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getAttachmentContent() {
        return this.attachmentContent;
    }

    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public FormFile getAttachmentFile() {
        return getAttachmentFile();
    }
    
    public void setAttachmentFile(FormFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }  
    
    public List<MultiAttachmentSample> getMultiAttachment() {
        return this.multiAttachment;
    }
    public void setMultiAttachment(List<MultiAttachmentSample> multiAttachment) {
        this.multiAttachment = multiAttachment;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> toStringMap = new LinkedHashMap<String, Object>();
        toStringMap.put("id", id);
        return toStringMap;
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(this.multiAttachment);
        return managedLists;
    }

}
