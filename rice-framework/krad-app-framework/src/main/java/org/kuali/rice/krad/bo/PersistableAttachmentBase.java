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
package org.kuali.rice.krad.bo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;


/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public class PersistableAttachmentBase extends PersistableBusinessObjectBaseAdapter implements PersistableAttachment {

    private static final long serialVersionUID = 1L;


    /**
     * EclipseLink static weaving does not weave MappedSuperclass unless an Entity or Embedded is
     * weaved which uses it, hence this class.
     */
    @Embeddable
    private static final class WeaveMe extends PersistableAttachmentBase {}

    @Lob
	@Column(name = "ATT_CNTNT")
    private byte[] attachmentContent;

	@Column(name = "FILE_NM", length = 150)
    private String fileName;

	@Column(name = "CNTNT_TYP", length = 255)
    private String contentType;

    @Override
    public byte[] getAttachmentContent() {
        return this.attachmentContent;
    }

    @Override
    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }


    @Override
    public String getFileName() {
        return fileName;
    }


    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Override
    public String getContentType() {
        return contentType;
    }


    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
